package ee.design.gen.go

import ee.common.ext.then
import ee.design.*
import ee.lang.*
import ee.lang.gen.go.g
import ee.lang.gen.go.retError
import ee.lang.gen.go.retType
import ee.lang.gen.go.retTypeAndError

fun StructureUnitI<*>.addEventHorizonArtifacts() {

    val reposFactory = lambda {
        p("name")
        p("factory", lambda { ret(g.eh.Entity) })
        ret(g.eh.ReadWriteRepo)
    }

    findDownByType(EntityI::class.java).filter { !it.isVirtual() && it.derivedAsType().isEmpty() }.groupBy {
        it.findParentMust(ModuleI::class.java)
    }.forEach { (module, items) ->
        module.extend {

            val aggregateInitializer = mutableListOf<ControllerI<*>>()
            val httpRouters = mutableListOf<ControllerI<*>>()
            val httpClients = mutableListOf<ControllerI<*>>()

            items.forEach {
                it.extend {
                    addEventHorizonArtifacts(aggregateInitializer, httpRouters, httpClients, reposFactory)
                }
            }

            controller {
                name("${module.name().capitalize()}${DesignDerivedType.EventhorizonInitializer}")
                    .derivedAsType(DesignDerivedType.Aggregate)
                val eventStore = prop { type(g.eh.EventStore).replaceable(false).name("eventStore") }
                val eventBus = prop { type(g.eh.EventBus).replaceable(false).name("eventBus") }
                val commandBus = prop { type(g.eh.CommandBus).replaceable(false).name("commandBus") }
                val readRepos = p { type(reposFactory).name("readRepos") }

                val aggregateInitializerProps = aggregateInitializer.map { item ->
                    prop {
                        type(item).name("${item.parent().name()}${item.name().capitalize()}")
                    }
                }
                constr {
                    params(eventStore, eventBus, commandBus, readRepos,
                        *aggregateInitializerProps.map { p(it) { default(true) } }.toTypedArray())
                }
                op {
                    name("Setup")
                    retError()
                    macrosBody(OperationI<*>::toGoEventhorizonInitializerSetupBody.name)
                }
            }

            controller {
                name("${module.name().capitalize()}${DesignDerivedType.HttpRouter}").derivedAsType(
                    DesignDerivedType.Http)
                val pathPrefix = propS { name("pathPrefix") }
                val httpRouterParams = httpRouters.map {
                    prop {
                        type(it).name("${it.parent().name()}${it.name().capitalize()}")
                    }
                }
                op {
                    name("Setup")
                    params(prop { type(g.mux.Router).name("router") })
                    retError()
                    macrosBody(OperationI<*>::toGoSetupModuleHttpRouter.name)
                }
                constr {
                    params(pathPrefix, p { type(g.context.Context).name("context") },
                        p { type(g.eh.CommandBus).name("commandBus") }, p {
                            type(reposFactory).name("readRepos")
                        }, *httpRouterParams.map { p(it) { default(true) } }.toTypedArray())
                    macrosBeforeBody(ConstructorI<*>::toGoHttpModuleRouterBeforeBody.name)
                }
            }

            controller {
                name("${module.name().capitalize()}${DesignDerivedType.HttpClient}").derivedAsType(
                    DesignDerivedType.Client)
                val url = propS { name("url") }
                val client = prop { name("client").type(g.net.http.Client) }

                val httpClientParams = httpClients.map {
                    prop {
                        type(it).name("${it.parent().name()}${it.name().capitalize()}")
                    }
                }

                constr {
                    params(url, client, *httpClientParams.map { p(it) { default(true) } }.toTypedArray())
                    macrosBeforeBody(ConstructorI<*>::toGoHttpModuleClientBeforeBody.name)
                }
            }


            val clis = mutableListOf<ControllerI<*>>()
            items.forEach {
                it.extend {
                    clis.add(it.addCli())
                }
            }

            controller {
                name("${module.name().capitalize()}${DesignDerivedType.Cli}").derivedAsType(
                    DesignDerivedType.Cli)


                val cliParams = clis.map {
                    prop {
                        type(it).name("${it.parent().name()}${it.name().capitalize()}")
                    }
                }

                constr {
                    params(*cliParams.map { p(it) { default(true) } }.toTypedArray())
                    macrosBeforeBody(ConstructorI<*>::toGoHttpModuleCliBeforeBody.name)
                }
            }
        }
    }
}

private fun EntityI<*>.addEventHorizonArtifacts(fillAggregateInitializer: MutableList<ControllerI<*>>,
    fillHttpRouters: MutableList<ControllerI<*>>, fillHttpClients: MutableList<ControllerI<*>>,
    reposFactory: LambdaI<*>) {

    val entity = this

    val finders = findDownByType(FindByI::class.java)
    val counters = findDownByType(CountByI::class.java)
    val exists = findDownByType(ExistByI::class.java)

    val creaters = findDownByType(CreateByI::class.java)
    val updaters = findDownByType(UpdateByI::class.java)
    val deleters = findDownByType(DeleteByI::class.java)

    val businessCommands = findDownByType(BusinessCommandI::class.java)

    val commandHandler = addCommandHandler()
    val eventHandler = addEventHandler()

    val queryRepository = addQueryRepository(finders, counters, exists)

    fillAggregateInitializer.add(
        //fillAggregateInitializer
        controller {
            name(DesignDerivedType.AggregateInitializer).derivedAsType(DesignDerivedType.Aggregate)
            prop {
                type(g.gee.eh.AggregateInitializer)
                    .anonymous(true).name("fillAggregateInitializer")
            }
            prop { type(commandHandler).anonymous(true).name("commandHandler") }
            prop { type(eventHandler).anonymous(true).name("eventHandler") }
            prop { type(eventHandler).name("projectorHandler") }

            macrosBefore(CompilationUnitI<*>::toGoAggregateInitializerConst.name)
            macrosAfter(CompilationUnitI<*>::toGoAggregateInitializerRegisterForEvents.name)

            constr {
                params(p { type(g.eh.EventStore).name("eventStore") },
                    p { type(g.eh.EventBus).name("eventBus") },
                    p { type(g.eh.CommandBus).name("commandBus") },
                    p { type(reposFactory).name("readRepos") })
                macrosBody(ConstructorI<*>::toGoAggregateInitializerBody.name)
            }
        })

    val httpQueryHandler = addHttpQueryHandler(finders, counters, exists, queryRepository)

    val httpCommandHandler = addHttpCommandHandler(creaters, updaters, deleters, businessCommands)

    fillHttpRouters.add(
        controller {
            name(DesignDerivedType.HttpRouter).derivedAsType(DesignDerivedType.Http)
            val pathPrefix = propS { name("pathPrefix") }
            val queryHandler = prop { type(httpQueryHandler).name("queryHandler") }
            val commandHandler = prop { type(httpCommandHandler).name("commandHandler") }

            op {
                name("Setup")
                params(prop { type(g.mux.Router).name("router") })
                retError()
                macrosBody(OperationI<*>::toGoSetupHttpRouterBody.name)
            }
            constr {
                params(pathPrefix, p { type(g.context.Context).name("context") },
                    p { type(g.eh.CommandHandler).name("commandBus") },
                    p { type(reposFactory).name("readRepos") },
                    p { type(queryRepository).default(true).name("queryRepository") },
                    p(queryHandler) { default(true) }, p(commandHandler) { default(true) })
                macrosBeforeBody(ConstructorI<*>::toGoHttpRouterBeforeBody.name)
            }
        }
    )

    fillHttpClients.add(
        controller {
            name(DesignDerivedType.HttpClient).derivedAsType(DesignDerivedType.Client)
            val url = propS { name("url") }
            val client = prop { type(g.net.http.Client).name("client") }

            constr {
                params(url, client)
                macrosBeforeBody(ConstructorI<*>::toGoHttpClientBeforeBody.name)
            }

            op {
                name("importJSON")
                params(p { name("fileJSON").type(n.String) })
                retError()
                macrosBody(OperationI<*>::toGoHttpClientImportJsonBody.name)
            }

            op {
                name("Create")
                params(p { name("items").type(n.List.GT(entity)) })
                retError()
                macrosBody(OperationI<*>::toGoHttpClientCreateBody.name)
            }

            op {
                name("ReadFileJSON")
                params(p { name("fileJSON").type(n.String) })
                retTypeAndError(n.List.GT(entity))
                macrosBody(OperationI<*>::toGoHttpClientReadFileJsonBody.name)
            }
        }
    )

    addStateMachineArtifacts()
}

private fun EntityI<*>.addStateMachineArtifacts() {
    val entity = this
    val stateMachines = findDownByType(StateMachineI::class.java)
    stateMachines.forEach { stateMachine ->

        val prefix = (stateMachine is AggregateHandler).not().then { stateMachine.name() }

        val handlers = mutableListOf<ControllerI<*>>()
        val executors = mutableListOf<ControllerI<*>>()

        stateMachine.states().forEach { state ->
            val statePrefix = "$prefix${state.name()}"
            //add event handler
            handlers.add(controller {
                name("$statePrefix${DesignDerivedType.Handler}")
                    .derivedAsType(DesignDerivedType.StateMachine).derivedFrom(state)
                val events = state.uniqueEvents()
                events.forEach { event ->
                    prop {
                        type(lambda {
                            p(event.name(), event)
                            p("entity", entity)
                            retError()
                        }).name("${event.name()}${DesignDerivedType.Handler}")
                    }
                }

                op {
                    name("Apply")
                    p("event", g.eh.Event)
                    p("entity", g.eh.Entity)
                    retError()
                    macrosBody(OperationI<*>::toGoStateEventHandlerApplyEvent.name)
                }

                op {
                    name("SetupEventHandler")
                    retError()
                    macrosBody(OperationI<*>::toGoStateEventHandlerSetupBody.name)
                }
            })

            //add executor
            executors.add(controller {
                name("$statePrefix${DesignDerivedType.Executor}")
                    .derivedAsType(DesignDerivedType.StateMachine).derivedFrom(state)
            })
        }

        //add state machine handlers
        controller {
            name("$prefix${DesignDerivedType.Handlers}")
                .derivedAsType(DesignDerivedType.StateMachine)
            handlers.forEach {
                prop { type(it).name(it.derivedFrom().name().decapitalize()).default() }
            }
            constructorFull { }
        }

        //add state machine executors
        controller {
            name("$prefix${DesignDerivedType.Executors}")
                .derivedAsType(DesignDerivedType.StateMachine)
            executors.forEach {
                prop { type(it).name(it.derivedFrom().name().decapitalize()).default() }
            }
            constructorFull { }
        }
    }
}

private fun EntityI<*>.addCli(): BusinessControllerI<*> =
    controller {
        name(DesignDerivedType.Cli).derivedAsType(DesignDerivedType.Cli)
    }

private fun EntityI<*>.addHttpQueryHandler(finders: List<FindByI<*>>, counters: List<CountByI<*>>,
    exists: List<ExistByI<*>>, queryRepository: BusinessControllerI<*>): BusinessControllerI<*> {
    return controller {
        name(DesignDerivedType.HttpQueryHandler).derivedAsType(DesignDerivedType.Http)
        prop { type(g.gee.eh.HttpQueryHandler).anonymous(true).name("HttpQueryHandler") }
        prop { type(queryRepository).name("queryRepository") }
        //queries
        finders.forEach {
            op {
                name(it.name().capitalize())
                p("w", g.net.http.ResponseWriter)
                p("r", g.net.http.Request)
                derivedFrom(it)
                macrosBody(OperationI<*>::toGoHttpHandlerBody.name)
            }
        }

        counters.forEach {
            op {
                name(it.name().capitalize())
                p("w", g.net.http.ResponseWriter)
                p("r", g.net.http.Request)
                derivedFrom(it)
                macrosBody(OperationI<*>::toGoHttpHandlerBody.name)
            }
        }

        exists.forEach {
            op {
                name(it.name().capitalize())
                p("w", g.net.http.ResponseWriter)
                p("r", g.net.http.Request)
                derivedFrom(it)
                macrosBody(OperationI<*>::toGoHttpHandlerBody.name)
            }
        }

        constructorFull { }
    }
}

private fun EntityI<*>.addQueryRepository(finders: List<FindByI<*>>,
    counters: List<CountByI<*>>,
    exists: List<ExistByI<*>>): BusinessControllerI<*> {
    return controller {
        name(DesignDerivedType.QueryRepository).derivedAsType(DesignDerivedType.Query)
        prop(g.eh.ReadRepo).replaceable(false).name("repo")
        prop(g.context.Context).replaceable(false).name("context")

        //queries
        finders.forEach {
            op(it).macrosBody(OperationI<*>::toGoFindByBody.name)
        }

        counters.forEach {
            op(it).macrosBody(OperationI<*>::toGoCountByBody.name)
        }

        exists.forEach {
            op(it).macrosBody(OperationI<*>::toGoExistByBody.name)
        }

        constructorFull {}
    }
}

private fun EntityI<*>.addHttpCommandHandler(
    creaters: List<CreateByI<*>>,
    updaters: List<UpdateByI<*>>,
    deleters: List<DeleteByI<*>>,
    businessCommands: List<BusinessCommandI<*>>): BusinessControllerI<*> {
    return controller {
        name(DesignDerivedType.HttpCommandHandler).derivedAsType(DesignDerivedType.Http)
        prop {
            type(g.gee.eh.HttpCommandHandler).anonymous(true).name("HttpCommandHandler")
        }

        //commands
        creaters.forEach {
            op {
                name(it.name().capitalize())
                p("w", g.net.http.ResponseWriter)
                p("r", g.net.http.Request)
                derivedFrom(it)
                macrosBody(OperationI<*>::toGoHttpHandlerCommandBody.name)
            }
        }

        updaters.forEach {
            op {
                name(it.name().capitalize())
                p("w", g.net.http.ResponseWriter)
                p("r", g.net.http.Request)
                derivedFrom(it)
                macrosBody(OperationI<*>::toGoHttpHandlerCommandBody.name)
            }
        }

        deleters.forEach {
            op {
                name(it.name().capitalize())
                p("w", g.net.http.ResponseWriter)
                p("r", g.net.http.Request)
                derivedFrom(it)
                macrosBody(OperationI<*>::toGoHttpHandlerCommandBody.name)
            }
        }

        businessCommands.forEach {
            op {
                name(it.name().capitalize())
                p("w", g.net.http.ResponseWriter)
                p("r", g.net.http.Request)
                derivedFrom(it)
                macrosBody(OperationI<*>::toGoHttpHandlerCommandBody.name)
            }
        }

        constructorFull {}
    }
}

private fun EntityI<*>.addEventHandler(): BusinessControllerI<*> {
    //event handler
    val item = this
    val events = findDownByType(EventI::class.java).sortedBy { it.name() }
    return controller {
        name(DesignDerivedType.EventHandler).derivedAsType(DesignDerivedType.Aggregate).derivedFrom(item)
        events.forEach { event ->
            prop {
                type(lambda {
                    p(event.name(), event)
                    p("entity", item)
                    retError()
                }).name("${event.name()}${DesignDerivedType.Handler}")
            }
        }

        op {
            name("Apply")
            p("event", g.eh.Event)
            p("entity", g.eh.Entity)
            retError()
            macrosBody(OperationI<*>::toGoEventHandlerApplyEvent.name)
        }

        op {
            name("SetupEventHandler")
            retError()
            macrosBody(OperationI<*>::toGoEventHandlerSetupBody.name)
        }

        //add event type enum
        enumType {
            name("${item.name()}EventType")
            events.forEach { lit { name(it.parentNameAndName().capitalize()) } }
        }

        constructorFull { derivedAsType(LangDerivedKind.MANUAL) }
    }
}

private fun EntityI<*>.addCommandHandler(): BusinessControllerI<*> {
    val item = this
    val commands = findDownByType(CommandI::class.java)
    return controller {
        name(DesignDerivedType.CommandHandler).derivedAsType(DesignDerivedType.Aggregate).derivedFrom(item)
        commands.forEach { command ->
            prop {
                type(lambda {
                    p(command.name(), command)
                    p("entity", item)
                    p("store", g.gee.eh.AggregateStoreEvent)
                    retError()
                }).name("${command.name()}${DesignDerivedType.Handler}")
            }

            op {
                name("Add${command.name().capitalize()}Preparer")
                p("preparer", lambda {
                    p("cmd", command)
                    p("entity", item)
                    retError()
                })
                derivedFrom(command)
                macrosBody(OperationI<*>::toGoCommandHandlerAddPreparerBody.name)
            }
        }

        op {
            name("Execute")
            p("cmd", g.eh.Command)
            p("entity", g.eh.Entity)
            p("store", g.gee.eh.AggregateStoreEvent)
            retError()
            macrosBody(OperationI<*>::toGoCommandHandlerExecuteCommandBody.name)
        }

        op {
            name("SetupCommandHandler")
            retError()
            macrosBody(OperationI<*>::toGoCommandHandlerSetupBody.name)
        }

        //add command type enum
        enumType {
            name("${item.name()}CommandType")
            commands.forEach { lit { name(it.nameAndParentName().capitalize()) } }
        }

        constructorFull { derivedAsType(LangDerivedKind.MANUAL) }
    }
}
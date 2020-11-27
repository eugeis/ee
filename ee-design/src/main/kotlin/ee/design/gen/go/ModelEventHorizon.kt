package ee.design.gen.go

import ee.common.ext.then
import ee.common.ext.toPlural
import ee.design.*
import ee.lang.*
import ee.lang.gen.go.g

fun StructureUnitI<*>.addEsArtifacts() {

    val reposFactory = lambda {
        notErr()
        p("name")
        p("factory", lambda {
            notErr()
            ret(g.eh.Entity)
        })
        ret(g.eh.ReadWriteRepo)
    }

    findDownByType(EntityI::class.java).filter { !it.isVirtual() && it.derivedAsType().isEmpty() }.groupBy {
        it.findParentMust(ModuleI::class.java)
    }.forEach { (module, items) ->
        module.extend {

            val aggregateInitializer = mutableMapOf<String, ControllerI<*>>()
            val httpRouters = mutableMapOf<String, ControllerI<*>>()
            val httpClients = mutableMapOf<String, ControllerI<*>>()

            items.forEach {
                it.extend {
                    addEsArtifacts(aggregateInitializer, httpRouters, httpClients, reposFactory)
                }
            }

            controller {
                name(DesignDerivedType.EsInitializer).derivedAsType(DesignDerivedType.Aggregate)
                val eventStore = prop { type(g.eh.EventStore).replaceable(false).name("eventStore") }
                val eventBus = prop { type(g.eh.EventBus).replaceable(false).name("eventBus") }
                val commandBus = prop { type(g.eh.CommandBus).replaceable(false).name("commandBus") }
                val readRepos = p { type(reposFactory).name("readRepos") }

                val aggregateInitializerProps = aggregateInitializer.map { (_, item) ->
                    prop {
                        type(item).name("${item.parent().name()}${item.name().capitalize()}")
                    }
                }
                constr {
                    params(
                        eventStore, eventBus, commandBus, readRepos,
                        *aggregateInitializerProps.map { p(it) { default(true) } }.toTypedArray()
                    )
                }
                op {
                    name("Setup")
                    macrosBody(OperationI<*>::toGoEhInitializerSetupBody.name)
                }
            }

            controller {
                name(DesignDerivedType.HttpRouter).derivedAsType(DesignDerivedType.Http)
                val pathPrefix = propS { name("pathPrefix") }
                val httpRouterParams = httpRouters.map { (_, item) ->
                    prop {
                        type(item).name("${item.parent().name()}${item.name().capitalize()}")
                    }
                }
                op {
                    name("Setup")
                    p("router", g.mux.Router)
                    macrosBody(OperationI<*>::toGoSetupModuleHttpRouter.name)
                }
                constr {
                    params(pathPrefix, p { type(g.context.Context).name("context") },
                        p { type(g.eh.CommandBus).name("commandBus") }, p {
                            type(reposFactory).name("readRepos")
                        }, *httpRouterParams.map { p(it) { default(true) } }.toTypedArray()
                    )
                    macrosBeforeBody(ConstructorI<*>::toGoHttpModuleRouterBeforeBody.name)
                }
            }

            val client = controller {
                name(DesignDerivedType.HttpClient).derivedAsType(DesignDerivedType.Client)
                val url = propS { name("url") }
                val client = prop { name("client").type(g.net.http.Client) }

                val httpClientParams = httpClients.mapValues { (_, item) ->
                    prop {
                        type(item).name("${item.parent().name()}${item.name().capitalize()}")
                    }
                }

                constr {
                    params(url, client, *httpClientParams.map { (_, it) ->
                        p(it) { default() }
                    }.toTypedArray())
                    macrosBeforeBody(ConstructorI<*>::toGoHttpModuleClientBeforeBody.name)
                }
            }


            val entityClis = mutableMapOf<String, ControllerI<*>>()
            items.forEach { entity ->
                entity.extend {
                    entityClis[entity.name()] = entity.addCli(httpClients[entity.name()]!!)
                }
            }

            controller {
                name(DesignDerivedType.Cli).derivedAsType(DesignDerivedType.Cli)
                prop { name("client").type(client) }

                val cliParams = entityClis.mapValues { (_, item) ->
                    prop {
                        type(item).name("${item.parent().name()}${item.name().capitalize()}")
                    }
                }

                constr {
                    params(p { name("url").type(n.String) }, p { name("httpClient").type(g.net.http.Client) },
                        p { name("client").type(client).default().notInitByDefaultTypeValue() },

                        *cliParams.map { (entityName, it) ->
                            val entityClient = httpClients[entityName]!!
                            p(it) { default().value("New${it.name()}(client.${entityClient.parentNameAndName()})") }
                        }.toTypedArray()
                    )
                    macrosBeforeBody(ConstructorI<*>::toGoCliBeforeBody.name)
                }
            }
        }
    }
}

private fun EntityI<*>.addEsArtifacts(
    fillAggregateInitializer: MutableMap<String, ControllerI<*>>,
    fillHttpRouters: MutableMap<String, ControllerI<*>>, fillHttpClients: MutableMap<String, ControllerI<*>>,
    reposFactory: LambdaI<*>
) {

    val entity = this

    val propId = entity.propId()

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

    fillAggregateInitializer[entity.name()] =
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
        }

    val httpQueryHandler = addHttpQueryHandler(finders, counters, exists, queryRepository)

    val httpCommandHandler = addHttpCommandHandler(creaters, updaters, deleters, businessCommands)

    fillHttpRouters[entity.name()] =
        controller {
            name(DesignDerivedType.HttpRouter).derivedAsType(DesignDerivedType.Http)
            val pathPrefix = propS { name("pathPrefix") }
            val pathPrefixIdBased = propS { name("pathPrefixIdBased") }
            val queryHandler = prop { type(httpQueryHandler).name("queryHandler") }
            val commandH = prop { type(httpCommandHandler).name("commandHandler") }

            op {
                name("Setup")
                p("router", g.mux.Router)
                macrosBody(OperationI<*>::toGoSetupHttpRouterBody.name)
            }
            constr {
                params(
                    pathPrefix,
                    p(pathPrefixIdBased) { default().notInitByDefaultTypeValue() },
                    p { type(g.context.Context).name("context") },
                    p { type(g.eh.CommandHandler).name("commandBus") },
                    p { type(reposFactory).name("readRepos") },
                    p { type(queryRepository).default().name("queryRepository") },
                    p(queryHandler) { default().initByDefaultTypeValue() },
                    p(commandH) { default().initByDefaultTypeValue() })
                macrosBeforeBody(ConstructorI<*>::toGoHttpRouterBeforeBody.name)
            }
        }

    fillHttpClients[entity.name()] =
        controller {
            name(DesignDerivedType.HttpClient).derivedAsType(DesignDerivedType.Client)

            val urlIdBased = propS { name("urlIdBased") }
            val url = propS { name("url") }
            val client = prop { type(g.net.http.Client).name("client") }

            constr {
                params(p(urlIdBased).default().notInitByDefaultTypeValue(), url, client)
                macrosBeforeBody(ConstructorI<*>::toGoHttpClientBeforeBody.name)
            }

            op {
                name("importJSON")
                params(p { name("fileJSON").type(n.String) })

                macrosBody(OperationI<*>::toGoHttpClientImportJsonBody.name)
            }

            op {
                name("exportJSON")
                params(p { name("targetFileJSON").type(n.String) })

                macrosBody(OperationI<*>::toGoHttpClientExportJsonBody.name)
            }

            op {
                name("Create")
                params(p { name("item").type(entity) })

                macrosBody(OperationI<*>::toGoHttpClientCreateBody.name)
            }

            op {
                name("CreateItems")
                params(p { name("items").type(n.List.GT(entity)) })

                macrosBody(OperationI<*>::toGoHttpClientCreateItemsBody.name)
            }

            op {
                name("DeleteBy${propId.name().toPlural().capitalize()}")
                params(p { name("itemIds").type(n.List.GT(entity.propId().type())) })

                macrosBody(OperationI<*>::toGoHttpClientDeleteByIdsBody.name)
            }

            op {
                name("DeleteBy${propId.name().capitalize()}")
                params(p { name("itemId").type(g.google.uuid.UUID) })

                macrosBody(OperationI<*>::toGoHttpClientDeleteByIdBody.name)
            }

            op {
                name("FindAll")
                ret(n.List.GT(entity))
                macrosBody(OperationI<*>::toGoHttpClientFindAllBody.name)
            }

            op {
                name("ReadFileJSON")
                params(p { name("fileJSON").type(n.String) })
                ret(n.List.GT(entity))
                macrosBody(OperationI<*>::toGoHttpClientReadFileJsonBody.name)
            }
        }

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

                        }).name("${event.name()}${DesignDerivedType.Handler}")
                    }
                }

                op {
                    name("Apply")
                    p("event", g.eh.Event)
                    p("entity", g.eh.Entity)

                    macrosBody(OperationI<*>::toGoStateEventHandlerApplyEvent.name)
                }

                op {
                    name("SetupEventHandler")

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

private fun EntityI<*>.addCli(client: ControllerI<*>): BusinessControllerI<*> {

    val propId = propId()

    return controller {
        name(DesignDerivedType.Cli).derivedAsType(DesignDerivedType.Cli)
        val entityClient = prop { type(client).name("client") }

        constr {
            params(entityClient)
        }

        op {
            name("BuildCommands")
            ret(n.List.GT(g.cli.Command)).notErr()

            macrosBody(OperationI<*>::toGoCliBuildCommands.name)
        }

        op {
            name("BuildCommandImportJSON")
            ret(g.cli.Command).notErr()
            macrosBody(OperationI<*>::toGoCliImportJsonBody.name)
        }

        op {
            name("BuildCommandExportJSON")
            ret(g.cli.Command).notErr()
            macrosBody(OperationI<*>::toGoCliExportJsonBody.name)
        }

        op {
            name("BuildCommandDeleteBy${propId.name().toPlural().capitalize()}")
            ret(g.cli.Command).notErr()
            macrosBody(OperationI<*>::toGoCliDeleteByIdsBody.name)
        }

        op {
            name("BuildCommandDeleteBy${propId.name().capitalize()}")
            ret(g.cli.Command).notErr()
            macrosBody(OperationI<*>::toGoCliDeleteByIdBody.name)
        }
    }
}

private fun EntityI<*>.addHttpQueryHandler(
    finders: List<FindByI<*>>, counters: List<CountByI<*>>,
    exists: List<ExistByI<*>>, queryRepository: BusinessControllerI<*>
): BusinessControllerI<*> {
    return controller {
        name(DesignDerivedType.HttpQueryHandler).derivedAsType(DesignDerivedType.Http)
        prop {
            type(g.gee.eh.HttpQueryHandler)
                .anonymous(true).name(DesignDerivedType.HttpQueryHandler.decapitalize())
        }
        prop { type(queryRepository).name("queryRepository") }
        //queries
        finders.forEach {
            op {
                name(it.name().capitalize()).notErr()
                p("w", g.net.http.ResponseWriter)
                p("r", g.net.http.Request)
                derivedFrom(it)
                macrosBody(OperationI<*>::toGoHttpHandlerBody.name)
            }
        }

        counters.forEach {
            op {
                name(it.name().capitalize()).notErr()
                p("w", g.net.http.ResponseWriter)
                p("r", g.net.http.Request)
                derivedFrom(it)
                macrosBody(OperationI<*>::toGoHttpHandlerBody.name)
            }
        }

        exists.forEach {
            op {
                name(it.name().capitalize()).notErr()
                p("w", g.net.http.ResponseWriter)
                p("r", g.net.http.Request)
                derivedFrom(it)
                macrosBody(OperationI<*>::toGoHttpHandlerBody.name)
            }
        }

        constructorFull { }
    }
}

private fun EntityI<*>.addQueryRepository(
    finders: List<FindByI<*>>,
    counters: List<CountByI<*>>,
    exists: List<ExistByI<*>>
): BusinessControllerI<*> {
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
    businessCommands: List<BusinessCommandI<*>>
): BusinessControllerI<*> {
    return controller {
        name(DesignDerivedType.HttpCommandHandler).derivedAsType(DesignDerivedType.Http)
        prop {
            type(g.gee.eh.HttpCommandHandler)
                .anonymous(true).name(DesignDerivedType.HttpCommandHandler.decapitalize())
        }

        //commands
        creaters.forEach {
            op {
                name(it.name().capitalize()).notErr()
                p("w", g.net.http.ResponseWriter)
                p("r", g.net.http.Request)
                derivedFrom(it)
                macrosBody(OperationI<*>::toGoHttpHandlerCommandBody.name)
            }
        }

        updaters.forEach {
            op {
                name(it.name().capitalize()).notErr()
                p("w", g.net.http.ResponseWriter)
                p("r", g.net.http.Request)
                derivedFrom(it)
                macrosBody(OperationI<*>::toGoHttpHandlerCommandBody.name)
            }
        }

        deleters.forEach {
            op {
                name(it.name().capitalize()).notErr()
                p("w", g.net.http.ResponseWriter)
                p("r", g.net.http.Request)
                derivedFrom(it)
                macrosBody(OperationI<*>::toGoHttpHandlerCommandBody.name)
            }
        }

        businessCommands.forEach {
            op {
                name(it.name().capitalize()).notErr()
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

                }).name("${event.name()}${DesignDerivedType.Handler}")
            }
        }

        op {
            name("Apply")
            p("event", g.eh.Event)
            p("entity", g.eh.Entity)

            macrosBody(OperationI<*>::toGoEventHandlerApplyEvent.name)
        }

        op {
            name("SetupEventHandler")

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

                }).name("${command.name()}${DesignDerivedType.Handler}")
            }

            op {
                name("Add${command.name().capitalize()}Preparer").notErr()
                p("preparer", lambda {
                    p("cmd", command)
                    p("entity", item)

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

            macrosBody(OperationI<*>::toGoCommandHandlerExecuteCommandBody.name)
        }

        op {
            name("SetupCommandHandler")

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
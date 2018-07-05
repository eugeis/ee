package ee.design.gen.go

import ee.design.*
import ee.lang.*
import ee.lang.gen.go.g
import ee.lang.gen.go.retError
import org.slf4j.LoggerFactory


private val log = LoggerFactory.getLogger("DesignGoUtils")

fun StructureUnitI<*>.addEventHorizonArtifactsForAggregate() {

    val reposFactory = lambda {
        p("name")
        p("factory", lambda { ret(g.eh.Entity) })
        ret(g.eh.ReadWriteRepo)
    }

    findDownByType(EntityI::class.java).filter { !it.isVirtual() && it.derivedAsType().isEmpty() }.groupBy {
        it.findParentMust(ModuleI::class.java)
    }.forEach { module, items ->
        module.extend {

            val aggregateInitializer = mutableListOf<ControllerI<*>>()
            val httpRouters = mutableListOf<ControllerI<*>>()

            items.forEach { item ->
                item.extend {
                    entityHandlers(aggregateInitializer, reposFactory, httpRouters)
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
        }
    }
}

private fun EntityI<*>.entityHandlers(aggregateInitializer: MutableList<ControllerI<*>>,
    reposFactory: LambdaI<*>, httpRouters: MutableList<ControllerI<*>>) {

    val finders = findDownByType(FindByI::class.java)
    val counters = findDownByType(CountByI::class.java)
    val exists = findDownByType(ExistByI::class.java)

    val creaters = findDownByType(CreateByI::class.java)
    val updaters = findDownByType(UpdateByI::class.java)
    val deleters = findDownByType(DeleteByI::class.java)

    val businessCommands = findDownByType(BusinessCommandI::class.java)

    val commandHandler = commandHandler()
    val eventHandler = eventHandler()

    val queryRepository = queryRepository(finders, counters, exists)

    aggregateInitializer.add(
        //aggregateInitializer
        controller {
            name(DesignDerivedType.AggregateInitializer).derivedAsType(DesignDerivedType.Aggregate)
            prop {
                type(g.gee.eh.AggregateInitializer)
                    .anonymous(true).name("aggregateInitializer")
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

    val httpQueryHandler = httpQueryHandler(queryRepository, finders, counters, exists)

    val httpCommandHandler = httpCommandHandler(creaters, updaters, deleters, businessCommands)

    httpRouters.add(
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
}

private fun EntityI<*>.httpQueryHandler(queryRepository: BusinessControllerI<*>,
    finders: List<FindByI<*>>, counters: List<CountByI<*>>,
    exists: List<ExistByI<*>>): BusinessControllerI<*> {
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

private fun EntityI<*>.queryRepository(finders: List<FindByI<*>>,
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

private fun EntityI<*>.httpCommandHandler(
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

private fun EntityI<*>.eventHandler(): BusinessControllerI<*> {
    //event handler
    val item = this
    val events = findDownByType(EventI::class.java)
    return controller {
        name(DesignDerivedType.EventHandler).derivedAsType(DesignDerivedType.Aggregate)
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

private fun EntityI<*>.commandHandler(): BusinessControllerI<*> {
    val item = this
    val commands = findDownByType(CommandI::class.java)
    return controller {
        name(DesignDerivedType.CommandHandler).derivedAsType(DesignDerivedType.Aggregate)
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
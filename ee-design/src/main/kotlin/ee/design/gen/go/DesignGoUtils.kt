package ee.design.gen.go

import ee.design.*
import ee.lang.*
import ee.lang.gen.go.g
import org.slf4j.LoggerFactory


private val log = LoggerFactory.getLogger("DesignGoUtils")

fun StructureUnitI.addEventhorizonArtifactsForAggregate() {
    findDownByType(EntityI::class.java).filter { !it.virtual() && it.derivedAsType().isEmpty() }.groupBy {
        it.findParentMust(ModuleI::class.java)
    }.forEach { module, items ->
        module.extend {
            val aggregateInitializer = arrayListOf<ControllerI>()
            val httpRouters = arrayListOf<ControllerI>()
            items.forEach { item ->
                item.extend {

                    val finders = findDownByType(FindBy::class.java)
                    val counters = findDownByType(CountByI::class.java)
                    val exists = findDownByType(ExistByI::class.java)

                    val creaters = findDownByType(CreateByI::class.java)
                    val updaters = findDownByType(UpdateByI::class.java)
                    val deleters = findDownByType(DeleteByI::class.java)

                    val bussinesCommands = findDownByType(BussinesCommandI::class.java)

                    //command handler
                    val commands = item.findDownByType(CommandI::class.java)
                    val commandHandler = controller {
                        name(DesignDerivedType.CommandHandler).derivedAsType(DesignDerivedType.Aggregate)
                        commands.forEach { command ->
                            prop({
                                type(lambda {
                                    p(command.name(), command)
                                    p("entity", item)
                                    p("store", g.gee.eh.AggregateStoreEvent)
                                    ret(g.error)
                                }).name("${command.name()}${DesignDerivedType.Handler}")
                            })
                        }

                        op {
                            name("Execute")
                            p("cmd", g.eh.Command)
                            p("entity", n.Any)
                            p("store", g.gee.eh.AggregateStoreEvent)
                            ret(g.error)
                            macrosBody(OperationI::toGoCommandHandlerExecuteCommandBody.name)
                        }

                        op {
                            name("SetupCommandHandler")
                            ret(g.error)
                            macrosBody(OperationI::toGoCommandHandlerSetupBody.name)
                        }

                        constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
                    }

                    //event handler
                    val events = item.findDownByType(EventI::class.java)
                    val eventHandler = controller {
                        name(DesignDerivedType.EventHandler).derivedAsType(DesignDerivedType.Aggregate)
                        events.forEach { event ->
                            prop({
                                type(lambda {
                                    p(event.name(), event)
                                    p("entity", item)
                                    ret(g.error)
                                }).name("${event.name()}${DesignDerivedType.Handler}")
                            })
                        }

                        op {
                            name("Apply")
                            p("event", g.eh.Event)
                            p("entity", n.Any)
                            ret(g.error)
                            macrosBody(OperationI::toGoEventHandlerApplyEvent.name)
                        }

                        op {
                            name("SetupEventHandler")
                            ret(g.error)
                            macrosBody(OperationI::toGoEventHandlerSetupBody.name)
                        }

                        constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
                    }

                    aggregateInitializer.add(
                            //aggregateInitializer
                            controller {
                                name(DesignDerivedType.AggregateInitializer).derivedAsType(DesignDerivedType.Aggregate)
                                prop { type(g.gee.eh.AggregateInitializer).anonymous(true).name("AggregateInitializer") }
                                val commandHandler = prop { type(commandHandler).anonymous(true).name("CommandHandler") }
                                val eventHandler = prop { type(eventHandler).anonymous(true).name("EventHandler") }

                                macrosBefore(CompilationUnitI::toGoAggregateInitializerConst.name)
                                macrosAfter(CompilationUnitI::toGoAggregateInitializerRegisterForEvents.name)

                                constr {
                                    params(p { type(g.eh.EventStore).replaceable(false).name("eventStore") },
                                            p { type(g.eh.EventBus).replaceable(false).name("eventBus") },
                                            p { type(g.eh.EventPublisher).replaceable(false).name("eventPublisher") },
                                            p { type(g.eh.CommandBus).replaceable(false).name("commandBus") })
                                    macrosBody(ConstructorI::toGoAggregateInitializerBody.name)
                                }
                            })

                    val httpQueryHandler = controller {
                        name(DesignDerivedType.HttpQueryHandler).derivedAsType(DesignDerivedType.Http)

                        //queries
                        finders.forEach {
                            op {
                                name(it.name().capitalize())
                                p("w", g.net.http.ResponseWriter)
                                p("r", g.net.http.Request)
                                macrosBody(OperationI::toGoHttpHandlerBody.name)
                            }
                        }

                        counters.forEach {
                            op {
                                name(it.name().capitalize())
                                p("w", g.net.http.ResponseWriter)
                                p("r", g.net.http.Request)
                                macrosBody(OperationI::toGoHttpHandlerBody.name)
                            }
                        }

                        exists.forEach {
                            op {
                                name(it.name().capitalize())
                                p("w", g.net.http.ResponseWriter)
                                p("r", g.net.http.Request)
                                macrosBody(OperationI::toGoHttpHandlerBody.name)
                            }
                        }

                        constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
                    }

                    val httpCommandHandler = controller {
                        name(DesignDerivedType.HttpCommandHandler).derivedAsType(DesignDerivedType.Http)

                        //commands
                        creaters.forEach {
                            op {
                                name(it.name().capitalize())
                                p("w", g.net.http.ResponseWriter)
                                p("r", g.net.http.Request)
                                macrosBody(OperationI::toGoHttpHandlerBody.name)
                            }
                        }

                        updaters.forEach {
                            op {
                                name(it.name().capitalize())
                                p("w", g.net.http.ResponseWriter)
                                p("r", g.net.http.Request)
                                macrosBody(OperationI::toGoHttpHandlerBody.name)
                            }
                        }

                        deleters.forEach {
                            op {
                                name(it.name().capitalize())
                                p("w", g.net.http.ResponseWriter)
                                p("r", g.net.http.Request)
                                macrosBody(OperationI::toGoHttpHandlerBody.name)
                            }
                        }

                        bussinesCommands.forEach {
                            op {
                                name(it.name().capitalize())
                                p("w", g.net.http.ResponseWriter)
                                p("r", g.net.http.Request)
                                macrosBody(OperationI::toGoHttpHandlerBody.name)
                            }
                        }

                        constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
                    }

                    httpRouters.add(
                            controller {
                                name(DesignDerivedType.HttpRouter).derivedAsType(DesignDerivedType.Http)
                                val pathPrefix = propS { name("PathPrefix") }
                                val router = prop { type(g.mux.Router).name("Router") }
                                val queryHandler = prop { type(httpQueryHandler).name("QueryHandler") }
                                val commandHandler = prop { type(httpCommandHandler).name("CommandHandler") }

                                op {
                                    name("Setup")
                                    ret(g.error)
                                    macrosBody(OperationI::toGoSetupHttpRouterBody.name)
                                }
                                constr {
                                    params(pathPrefix, router,
                                            p(queryHandler, { default(true) }), p(commandHandler, { default(true) }))
                                    derivedAsType(LangDerivedKind.MANUAL)
                                }
                            }
                    )
                }

            }

            controller {
                name("${module.name().capitalize()}${DesignDerivedType.EventhorizonInitializer}").derivedAsType(DesignDerivedType.Aggregate)
                val eventStore = prop { type(g.eh.EventStore).replaceable(false).name("eventStore") }
                val eventBus = prop { type(g.eh.EventBus).replaceable(false).name("eventBus") }
                val eventPublisher = prop { type(g.eh.EventPublisher).replaceable(false).name("eventPublisher") }
                val commandBus = prop { type(g.eh.CommandBus).replaceable(false).name("commandBus") }

                val aggregateInitializerProps = aggregateInitializer.map { item ->
                    prop {
                        type(item).name("${item.parent().name()}${item.name().capitalize()}")
                    }
                }
                constr {
                    params(eventStore, eventBus, eventPublisher, commandBus,
                            *aggregateInitializerProps.map { p(it, { default(true) }) }.toTypedArray())
                }
                op {
                    name("Setup")
                    ret(g.error)
                    macrosBody(OperationI::toGoEventhorizonInitializerSetupBody.name)
                }
            }

            controller {
                name("${module.name().capitalize()}${DesignDerivedType.HttpRouter}").derivedAsType(DesignDerivedType.Http)
                prop { type(g.mux.Router).name("Router") }
                propS { name("PathPrefix") }
                httpRouters.forEach { item ->
                    prop {
                        type(item).name("${item.parent().name()}${item.name().capitalize()}")
                    }
                }
                op {
                    name("Setup")
                    ret(g.error)
                    macrosBody(OperationI::toGoSetupModuleHttpRouter.name)
                }
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
            }
        }
    }
}
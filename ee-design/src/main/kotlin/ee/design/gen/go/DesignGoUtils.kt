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
                            prop {
                                type(lambda {
                                    p(command.name(), command)
                                    p("entity", item)
                                    p("store", g.gee.eh.AggregateStoreEvent)
                                    ret(g.error)
                                }).name("${command.name()}${DesignDerivedType.Handler}")
                            }
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

                        //add command type enum
                        enumType {
                            name("${item.name()}CommandType")
                            commands.forEach { lit({ name(it.nameAndParentName().capitalize()) }) }
                        }

                        constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
                    }

                    //event handler
                    val events = item.findDownByType(EventI::class.java)
                    val eventHandler = controller {
                        name(DesignDerivedType.EventHandler).derivedAsType(DesignDerivedType.Aggregate)
                        events.forEach { event ->
                            prop {
                                type(lambda {
                                    p(event.name(), event)
                                    p("entity", item)
                                    ret(g.error)
                                }).name("${event.name()}${DesignDerivedType.Handler}")
                            }
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

                        //add event type enum
                        enumType {
                            name("${item.name()}EventType")
                            events.forEach { lit({ name(it.parentNameAndName().capitalize()) }) }
                        }

                        constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
                    }


                    val queryRepository = controller {
                        name(DesignDerivedType.QueryRepository).derivedAsType(DesignDerivedType.Query)
                        prop(g.eh.ReadRepo).replaceable(false).name("repo")
                        prop(g.context.Context).replaceable(false).name("context")

                        //queries
                        finders.forEach {
                            op(it).macrosBody(OperationI::toGoFindByBody.name)
                        }

                        counters.forEach {
                            op(it).macrosBody(OperationI::toGoCountByBody.name)
                        }

                        exists.forEach {
                            op(it).macrosBody(OperationI::toGoExistByBody.name)
                        }

                        constructorAllProps {}
                    }


                    aggregateInitializer.add(
                            //aggregateInitializer
                            controller {
                                name(DesignDerivedType.AggregateInitializer).derivedAsType(DesignDerivedType.Aggregate)
                                prop { type(g.gee.eh.AggregateInitializer).anonymous(true).name("aggregateInitializer") }
                                val commandHandler = prop { type(commandHandler).anonymous(true).name("commandHandler") }
                                prop { type(eventHandler).anonymous(true).name("eventHandler") }
                                prop { type(eventHandler).name("projectorHandler") }

                                macrosBefore(CompilationUnitI::toGoAggregateInitializerConst.name)
                                macrosAfter(CompilationUnitI::toGoAggregateInitializerRegisterForEvents.name)

                                constr {
                                    params(p { type(g.eh.EventStore).name("eventStore") },
                                            p { type(g.eh.EventBus).name("eventBus") },
                                            p { type(g.eh.EventPublisher).name("eventPublisher") },
                                            p { type(g.eh.CommandBus).name("commandBus") },
                                            p {
                                                type(lambda {
                                                    p("name")
                                                    ret(g.eh.ReadWriteRepo)
                                                }).name("readRepos")
                                            })
                                    macrosBody(ConstructorI::toGoAggregateInitializerBody.name)
                                }
                            })

                    val httpQueryHandler = controller {
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
                                macrosBody(OperationI::toGoHttpHandlerBody.name)
                            }
                        }

                        counters.forEach {
                            op {
                                name(it.name().capitalize())
                                p("w", g.net.http.ResponseWriter)
                                p("r", g.net.http.Request)
                                derivedFrom(it)
                                macrosBody(OperationI::toGoHttpHandlerBody.name)
                            }
                        }

                        exists.forEach {
                            op {
                                name(it.name().capitalize())
                                p("w", g.net.http.ResponseWriter)
                                p("r", g.net.http.Request)
                                derivedFrom(it)
                                macrosBody(OperationI::toGoHttpHandlerBody.name)
                            }
                        }

                        constructorAllProps { }
                    }

                    val httpCommandHandler = controller {
                        name(DesignDerivedType.HttpCommandHandler).derivedAsType(DesignDerivedType.Http)
                        prop { type(g.gee.eh.HttpCommandHandler).anonymous(true).name("HttpCommandHandler") }

                        //commands
                        creaters.forEach {
                            op {
                                name(it.name().capitalize())
                                p("w", g.net.http.ResponseWriter)
                                p("r", g.net.http.Request)
                                derivedFrom(it)
                                macrosBody(OperationI::toGoHttpHandlerCommandBody.name)
                            }
                        }

                        updaters.forEach {
                            op {
                                name(it.name().capitalize())
                                p("w", g.net.http.ResponseWriter)
                                p("r", g.net.http.Request)
                                derivedFrom(it)
                                macrosBody(OperationI::toGoHttpHandlerCommandBody.name)
                            }
                        }

                        deleters.forEach {
                            op {
                                name(it.name().capitalize())
                                p("w", g.net.http.ResponseWriter)
                                p("r", g.net.http.Request)
                                derivedFrom(it)
                                macrosBody(OperationI::toGoHttpHandlerCommandBody.name)
                            }
                        }

                        bussinesCommands.forEach {
                            op {
                                name(it.name().capitalize())
                                p("w", g.net.http.ResponseWriter)
                                p("r", g.net.http.Request)
                                macrosBody(OperationI::toGoHttpHandlerCommandBody.name)
                            }
                        }

                        constructorAllProps {}
                    }

                    httpRouters.add(
                            controller {
                                name(DesignDerivedType.HttpRouter).derivedAsType(DesignDerivedType.Http)
                                val pathPrefix = propS { name("pathPrefix") }
                                val queryHandler = prop { type(httpQueryHandler).name("queryHandler") }
                                val commandHandler = prop { type(httpCommandHandler).name("commandHandler") }

                                op {
                                    name("Setup")
                                    params(prop { type(g.mux.Router).name("router") })
                                    ret(g.error)
                                    macrosBody(OperationI::toGoSetupHttpRouterBody.name)
                                }
                                constr {
                                    params(pathPrefix,
                                            p { type(g.context.Context).name("context") },
                                            p { type(g.eh.CommandBus).name("commandBus") },
                                            p {
                                                type(lambda {
                                                    p("name")
                                                    ret(g.eh.ReadWriteRepo)
                                                }).name("readRepos")
                                            },
                                            p { type(queryRepository).default(true).name("queryRepository") },
                                            p(queryHandler, { default(true) }),
                                            p(commandHandler, { default(true) })
                                    )
                                    macrosBeforeBody(ConstructorI::toGoHttpRouterBeforeBody.name)
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
                val readRepos = p {
                    type(lambda {
                        p("name")
                        ret(g.eh.ReadWriteRepo)
                    }).name("readRepos")
                }

                val aggregateInitializerProps = aggregateInitializer.map { item ->
                    prop {
                        type(item).name("${item.parent().name()}${item.name().capitalize()}")
                    }
                }
                constr {
                    params(eventStore, eventBus, eventPublisher, commandBus, readRepos,
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
                val pathPrefix = propS { name("pathPrefix") }
                val httpRouterParams = httpRouters.map {
                    prop {
                        type(it).name("${it.parent().name()}${it.name().capitalize()}")
                    }
                }
                op {
                    name("Setup")
                    params(prop { type(g.mux.Router).name("router") })
                    ret(g.error)
                    macrosBody(OperationI::toGoSetupModuleHttpRouter.name)
                }
                constr {
                    params(pathPrefix,
                            p { type(g.context.Context).name("context") },
                            p { type(g.eh.CommandBus).name("commandBus") },
                            p {
                                type(lambda {
                                    p("name")
                                    ret(g.eh.ReadWriteRepo)
                                }).name("readRepos")
                            },
                            *httpRouterParams.map { p(it, { default(true) }) }.toTypedArray())
                    macrosBeforeBody(ConstructorI::toGoHttpModuleRouterBeforeBody.name)
                }
            }
        }
    }
}
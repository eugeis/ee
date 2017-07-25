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
            val initializer = arrayListOf<ControllerI>()
            items.forEach { item ->
                item.extend {
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
                                }).replaceable(true).name("${command.name()}${DesignDerivedType.Handler}")
                            })
                        }

                        op {
                            name("Execute")
                            p("cmd", g.eh.Command)
                            p("entity", n.Any)
                            p("store", g.gee.eh.AggregateStoreEvent)
                            ret(g.error)
                            macros(OperationI::toGoCommandHandlerExecuteCommand.name)
                        }

                        op {
                            name("SetupCommandHandler")
                            ret(g.error)
                            macros(OperationI::toGoCommandHandlerSetup.name)
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
                                }).replaceable(true).name("${event.name()}${DesignDerivedType.Handler}")
                            })
                        }

                        op {
                            name("Apply")
                            p("event", g.eh.Event)
                            p("entity", n.Any)
                            ret(g.error)
                            macros(OperationI::toGoEventHandlerApplyEvent.name)
                        }

                        op {
                            name("SetupEventHandler")
                            ret(g.error)
                            macros(OperationI::toGoEventHandlerSetup.name)
                        }

                        constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
                    }

                    initializer.add(
                            //initializer
                            controller {
                                name(DesignDerivedType.AggregateInitializer).derivedAsType(DesignDerivedType.Aggregate)
                                prop({ type(g.gee.eh.AggregateInitializer).anonymous(true).name("AggregateInitializer") })
                                prop({ type(commandHandler).anonymous(true).name("CommandHandler") })
                                prop({ type(eventHandler).anonymous(true).name("EventHandler") })
                                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
                                macros(CompilationUnitI::toGoAggregateInitializer.name)
                            })
                }

            }

            controller {
                name("${module.name().capitalize()}${DesignDerivedType.EventhorizonInitializer}").derivedAsType(DesignDerivedType.Aggregate)
                prop { type(g.eh.EventStore).replaceable(false).name("eventStore") }
                prop { type(g.eh.EventBus).replaceable(false).name("eventBus") }
                prop { type(g.eh.EventPublisher).replaceable(false).name("eventPublisher") }
                prop { type(g.eh.CommandBus).replaceable(false).name("commandBus") }
                initializer.forEach { item ->
                    prop {
                        type(item).replaceable(true).default(true).
                                name("${item.parent().name()}${item.name().capitalize()}")
                    }
                }
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
                macros(CompilationUnitI::toGoEventhorizonInitializer.name)
            }
        }
    }
}
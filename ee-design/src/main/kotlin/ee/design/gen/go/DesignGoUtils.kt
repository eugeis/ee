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
                    //aggregate
                    val aggregate = controller {
                        name(DesignDerivedType.Aggregate).derivedAsType(DesignDerivedType.Aggregate)
                        prop({ type(g.eh.AggregateBase).anonymous(true).name("AggregateBase") })
                        prop({ type(item).anonymous(true).name("Entity") })
                        prop({ type(g.eh.CommandHandler).anonymous(true).name("CommandHandler") })
                        constructorOwnPropsOnly { derivedAsType(LangDerivedKind.MANUAL) }
                        macros(CompilationUnitI::toGoAggregate.name)
                    }

                    //command handler
                    val commands = item.findDownByType(CommandI::class.java)
                    val handler = controller {
                        name(DesignDerivedType.CommandHandler).derivedAsType(DesignDerivedType.Aggregate)
                        commands.forEach { command ->
                            prop({
                                type(lambda {
                                    p(command.name(), command)
                                    p("aggregate", aggregate)
                                    ret(g.error)
                                }).replaceable(true).name("${command.name()}${DesignDerivedType.Handler}")
                            })
                        }
                        op {
                            name("HandleCommand")
                            p("ctx", g.context.Context)
                            p("cmd", g.eh.Command)
                            p("aggregate", aggregate)
                            ret(g.error)
                            macros(OperationI::toGoHandleCommand.name)
                        }
                        constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
                        //macros(CompilationUnitI::toGoAggregateInitializer.name)
                    }

                    initializer.add(
                            //initializer
                            controller {
                                name(DesignDerivedType.AggregateInitializer).derivedAsType(DesignDerivedType.Aggregate)
                                prop({ type(g.gee.eh.AggregateInitializer).anonymous(true).name("AggregateInitializer") })
                                prop({ type(handler).anonymous(true).name("SingleCommandHandlers") })
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
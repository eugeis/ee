package ee.design.gen.go

import ee.design.ControllerI
import ee.design.DesignDerivedType
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.go.g


object eh : StructureUnit({ namespace("github.com.looplab.eventhorizon").name("eh") }) {

    object AggregateBase : ExternalType() {
    }

    object AggregateType : ExternalType() {
    }

    object CommandType : ExternalType() {
    }

    object AggregateCommandHandler : ExternalType() {
        object SetAggregate : Operation() {
            val aggregateType = p()
            val cmdType = p()
            val ret = ret()
        }
    }

    object EventStore : ExternalType() {
        object Save : Operation() {
            val ctx = p()
        }
    }

    object EventBus : ExternalType() {
    }

    object EventPublisher : ExternalType() {
    }

    object EventHandler : ExternalType() {
    }

    object CommandBus : ExternalType() {
    }

    object Event : ExternalType() {
    }

    object UUID : ExternalType() {
    }
}

fun StructureUnitI.addEventhorizonArtifactsForAggregate() {
    findDownByType(EntityI::class.java).filter { !it.virtual() && it.derivedAsType().isEmpty() }.groupBy {
        it.findParentMust(ModuleI::class.java)
    }.forEach { module, items ->
        module.extend {
            val initializer = arrayListOf<ControllerI>()
            val aggregates = arrayListOf<ControllerI>()
            items.forEach { item ->
                item.extend {
                    aggregates.add(
                            controller {
                                name(DesignDerivedType.Aggregate).derivedAsType(DesignDerivedType.Aggregate)
                                prop({ type(eh.AggregateBase).anonymous(true).name("AggregateBase") })
                                prop({ type(item).anonymous(true).name("Entity") })
                                macros(CompilationUnitI::toGoAggregate.name)
                            })
                    initializer.add(
                            controller {
                                name(DesignDerivedType.AggregateInitializer).derivedAsType(DesignDerivedType.Aggregate)
                                prop({ type(g.gee.eh.AggregateInitializer).anonymous(true).name("AggregateInitializer") })
                                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
                                macros(CompilationUnitI::toGoAggregateInitializer.name)
                            })
                }

            }

            controller {
                name("${module.name().capitalize()}${DesignDerivedType.EventhorizonInitializer}").derivedAsType(DesignDerivedType.Aggregate)
                prop { type(eh.EventStore).replaceable(false).name("eventStore") }
                prop { type(eh.EventBus).replaceable(false).name("eventBus") }
                prop { type(eh.EventPublisher).replaceable(false).name("eventPublisher") }
                prop { type(eh.CommandBus).replaceable(false).name("commandBus") }
                initializer.forEach { item ->
                    prop { type(item).default(true).name("${item.parent().name().capitalize()}${item.name().capitalize()}") }
                }
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
                macros(CompilationUnitI::toGoEventhorizonInitializer.name)
            }
        }
    }
}
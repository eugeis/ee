package ee.design.gen.go

import ee.design.DesignDerivedType
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*


object eh : StructureUnit({ namespace("github.com.looplab.eventhorizon").name("eh") }) {

    object AggregateType : ExternalType() {
    }

    object AggregateBase : ExternalType() {
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
            items.forEach { item ->
                entity {
                    name("${item.name()}${DesignDerivedType.AGGREGATE}").derivedAsType(DesignDerivedType.AGGREGATE)
                    prop({ type(eh.AggregateBase).anonymous(true).name("AggregateBase") })
                    prop({ type(item).anonymous(true).name("Entity") })
                    derivedFrom(item)
                    parent(module)
                }
            }

            enumType {
                name("${module.name().capitalize()}AggregateType").derivedAsType(DesignDerivedType.AGGREGATE)
                items.forEach {
                    lit({ name(it.nameAndParentName()) })
                }
                parent(module)
            }

            controller {
                name("${module.name().capitalize()}EventhorizonInitializer").derivedAsType(DesignDerivedType.AGGREGATE)
                prop { type(eh.EventStore).name("store") }
                prop { type(eh.EventBus).name("eventBus") }
                prop { type(eh.EventPublisher).name("publisher") }
                prop { type(eh.CommandBus).name("commandBus") }

                parent(module)
            }
        }
    }
}
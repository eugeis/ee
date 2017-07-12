package ee.design.gen.go

import ee.design.CommandI
import ee.design.DesignDerivedType
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*


object eh : StructureUnit({ namespace("github.com.looplab.eventhorizon").name("eh") }) {

    object AggregateBase : ExternalType() {
    }

    object AggregateType : ExternalType() {
    }

    object AggregateCommandHandler : ExternalType() {
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
                    val AggregateBase = prop({ type(eh.AggregateBase).anonymous(true).name("AggregateBase") })
                    val Entity = prop({ type(item).anonymous(true).name("Entity") })
                    derivedFrom(item)
                    parent(module)
                }

                controller {
                    name("${item.name().capitalize()}AggregateInitializer").derivedAsType(DesignDerivedType.AGGREGATE)
                    val store = prop { type(eh.EventStore).name("store") }
                    val notifier = prop { type(eh.EventBus).name("notifier") }
                    val publisher = prop { type(eh.EventPublisher).name("publisher") }
                    val executor = prop { type(eh.CommandBus).name("executor") }

                    parent(module)

                    op { name("setup") }

                    val registerCommands = op {
                        name("registerCommands")
                        val commandController = p { type(eh.AggregateCommandHandler).name("commandController") }
                        item.commands().first().findDownByType(CommandI::class.java).forEach {
                        }
                    }
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

                op { name("setup") }

                op { name("registerCommands") }

            }
        }
    }
}
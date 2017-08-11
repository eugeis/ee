package ee.design

import ee.design.gen.go.toGoPropOptionalAfterBody
import ee.lang.*
import org.slf4j.LoggerFactory

open class DesignDerivedKindNames : LangDerivedKindNames() {
    val HttpGet = "Get"
    val HttpPost = "Post"
    val HttpPut = "Put"
    val HttpDelete = "Delete"
}

object DesignDerivedKind : DesignDerivedKindNames()

open class DesignDerivedTypeNames {
    val Aggregate = "Aggregate"
    val AggregateInitializer = "AggregateInitializer"
    val AggregateType = "AggregateType"
    val Command = "Command"
    val CommandHandler = "CommandHandler"
    val Http = "Http"
    val HttpRouter = "Router"
    val HttpQueryHandler = "HttpQueryHandler"
    val HttpCommandHandler = "HttpCommandHandler"
    val Event = "Event"
    val EventHandler = "EventHandler"
    val EventhorizonInitializer = "EventhorizonInitializer"
    val Handler = "Handler"
}

object DesignDerivedType : DesignDerivedTypeNames()


private val log = LoggerFactory.getLogger("DesignUtils")

fun Queries.findBy(vararg params: AttributeI) = findBy { params(*params) }
fun Queries.existBy(vararg params: AttributeI) = existBy { params(*params) }

fun CompilationUnitI.op(vararg params: AttributeI, body: OperationI.() -> Unit = {}) = op {
    params(*params)
    body()
}

fun Commands.command(vararg params: AttributeI) = command { props(*params) }
fun Commands.createBy(vararg params: AttributeI) = createBy { props(*params) }
fun Commands.updateBy(vararg params: AttributeI) = updateBy { props(*params) }
fun Commands.deleteBy(vararg params: AttributeI) = deleteBy { props(*params) }
fun Commands.composite(vararg commands: CommandI) = composite { commands(*commands) }


fun StructureUnitI.defineNamesForTypeControllers() {
    findDownByType(ControllerI::class.java).forEach {
        val parent = it.findParent(CompilationUnitI::class.java)
        if (parent != null) {
            it.name("${parent.name().capitalize()}${it.name().capitalize()}")
        }
    }
}

fun StructureUnitI.addQueriesForAggregates() {
    findDownByType(EntityI::class.java).filter { !it.virtual() && it.queries().isEmpty() }.extend {
        log.debug("Add default queries to ${name()}")
        queries(Queries {
            name("queries")

            findBy { name("findAll") }
            findBy {
                name("findById")
                id()
            }
            countBy { name("countAll") }
            findBy {
                name("countById")
                id()
            }
            existBy { name("existAll") }
            existBy {
                name("existById")
                id()
            }
        })
    }
}

fun StructureUnitI.addCommandsAndEventsForAggregates() {
    findDownByType(EntityI::class.java).filter { !it.virtual() && it.commands().isEmpty() }.extend {
        log.debug("Add default commands to ${name()}")
        val dataTypeProps = propsAll().filter { !it.meta() }.map { p(it).setOptionalTag() }

        var created: CreatedI = Created.EMPTY
        var updated: UpdatedI = Updated.EMPTY
        var deleted: DeletedI = Deleted.EMPTY

        if (events().isEmpty()) {
            val events = Events { name("events") }
            events(events)

            created = events.created {
                name("created")
                props(*dataTypeProps.toTypedArray())
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
            }

            updated = events.updated {
                name("updated")
                props(*dataTypeProps.toTypedArray())
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
            }

            deleted = events.deleted {
                name("deleted")
                props(id())
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
            }
        } else {
            val events = events().first()
            created = events.findDownByType(CreatedI::class.java).firstOrNull() ?: created
            updated = events.findDownByType(UpdatedI::class.java).firstOrNull() ?: updated
            deleted = events.findDownByType(DeletedI::class.java).firstOrNull() ?: deleted
        }

        if (commands().isEmpty()) {
            commands(Commands {
                name("commands")

                createBy {
                    name("create")
                    props(*dataTypeProps.toTypedArray())
                    event(created)
                    constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
                }

                updateBy {
                    name("update")
                    props(*dataTypeProps.toTypedArray())
                    event(updated)
                    constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
                }

                deleteBy {
                    name("delete")
                    props(id())
                    event(deleted)
                    constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
                }
            })
        }
    }
}

fun AttributeI.setOptionalTag(): AttributeI {
    macrosAfterBody(AttributeI::toGoPropOptionalAfterBody.name)
    return this
}

fun StructureUnitI.declareAsBaseWithNonImplementedOperation() {
    findDownByType(CompilationUnitI::class.java).filter { it.operations().isNotEMPTY() && !it.base() }.forEach { it.base(true) }

    //derive controllers from super units
    findDownByType(ControllerI::class.java).filter { it.parent() is CompilationUnitI }.forEach {
        val dataItem = it.parent() as CompilationUnitI
        dataItem.propagateItemToSubtypes(it)

        val T = it.G { type(dataItem).name("T") }
        if (it !is Queries) {
            it.prop { type(T).name("addItem") }
        }
    }
}

fun <T : CompilationUnitI> T.propagateItemToSubtypes(item: CompilationUnitI) {
    superUnitFor().filter { superUnitChild ->
        superUnitChild.items().filterIsInstance<CompilationUnitI>().find {
            (it.name() == item.name() || it.superUnit() == superUnitChild)
        } == null
    }.forEach { superUnitChild ->
        val derivedItem = item.deriveSubType<ControllerI> {
            namespace(superUnitChild.namespace())
            G { type(superUnitChild).name("T") }
        }
        superUnitChild.addItem(derivedItem)
        superUnitChild.propagateItemToSubtypes(derivedItem)
    }
}

fun EntityI.buildId(): AttributeI = Attribute { key(true).type(n.UUID).name("id") }

fun EntityI.id(): AttributeI = storage.getOrPut(this, "id", {
    var ret = props().find { it.key() }
    if (ret == null && superUnit() is EntityI) {
        ret = (superUnit() as EntityI).id()
    } else if (ret == null) {
        log.warn("Id can't be found for '$this', return EMPTY")
        ret = Attribute.EMPTY
    }
    ret
})


fun StructureUnitI.addCommandEnumsForAggregate() {
    findDownByType(CommandI::class.java).groupBy { it.findParentMust(EntityI::class.java) }.forEach { entity, items ->
        if (!entity.virtual()) {
            val parent = entity.commands().first()
            parent.extend {
                log.debug("Add CommandType to ${entity.name()}")
                enumType {
                    name("${entity.name()}CommandType")
                    items.forEach {
                        lit({ name(it.nameAndParentName().capitalize()) })
                    }
                }
            }
        }
    }
}

fun StructureUnitI.addEventEnumsForAggregate() {
    findDownByType(EventI::class.java).groupBy { it.findParentMust(EntityI::class.java) }.forEach { entity, items ->
        if (!entity.virtual()) {
            val parent = entity.events().first()
            parent.extend {
                log.debug("Add EventType to ${entity.name()}")
                enumType {
                    name("${entity.name()}EventType")
                    items.forEach {
                        lit({ name(it.parentNameAndName().capitalize()) })
                    }
                }
            }
        }
    }
}

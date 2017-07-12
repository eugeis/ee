package ee.design

import ee.lang.*
import org.slf4j.LoggerFactory

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


fun StructureUnitI.defineNamesForControllers() {
    findDownByType(ControllerI::class.java).forEach {
        val parent = it.findParentMust(CompilationUnitI::class.java)
        it.name("${parent.name().capitalize()}${it.name().capitalize()}")
    }
}

fun StructureUnitI.addDefaultCommandsAndEventsForEntities() {
    findDownByType(EntityI::class.java).filter { !it.virtual() && it.commands().isEmpty() }.extend {
        log.debug("Add default commands to ${name()}")
        val dataTypeProps = props().filter { !it.meta() && !it.key() }

        val entity = this

        commands(Commands {
            name("commands")

            createBy {
                name("create")
                props(*dataTypeProps.toTypedArray())
                parent(entity)
            }

            updateBy {
                name("update")
                props(*dataTypeProps.toTypedArray())
                parent(entity)
            }

            deleteBy {
                name("delete")
                props(id())
                parent(entity)
            }
        })

        if (events().isEmpty()) {
            events(Events {
                name("events")

                created {
                    name("created")
                    props(*dataTypeProps.toTypedArray())
                    parent(entity)
                }

                updated {
                    name("updated")
                    props(*dataTypeProps.toTypedArray())
                    parent(entity)
                }

                deleted {
                    name("deleted")
                    props(id())
                    parent(entity)
                }
            })
        }
    }
}

fun StructureUnitI.addCommandEnumsForAggregate() {
    findDownByType(CommandI::class.java).groupBy { it.findParentMust(EntityI::class.java) }.forEach { entity, items ->
        if (!entity.virtual()) {
            val parent = entity.commands().first()
            parent.extend {
                enums(EnumType {
                    name("${entity.name()}CommandType")
                    items.forEach {
                        lit({ name(it.nameAndParentName()) })
                    }
                })
            }
        }
    }
}

fun StructureUnitI.addEventEnumsForAggregate() {
    findDownByType(EventI::class.java).groupBy { it.findParentMust(EntityI::class.java) }.forEach { entity, items ->
        if (!entity.virtual()) {
            val parent = entity.events().first()
            parent.extend {
                enums(EnumType {
                    name("${entity.name()}EventType")
                    items.forEach {
                        lit({ name(it.parentNameAndName()) })
                    }
                })
            }
        }
    }
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

fun EntityI.buildId(): AttributeI = Attribute { key(true).name("id") }

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

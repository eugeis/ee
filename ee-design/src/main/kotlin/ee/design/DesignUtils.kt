package ee.design

import ee.lang.*
import org.slf4j.LoggerFactory

open class DesignDerivedKindNames : LangDerivedKindNames() {
}

object DesignDerivedKind : DesignDerivedKindNames()

open class DesignDerivedTypeNames {
    val AGGREGATE = "Aggregate"
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

fun StructureUnitI.addCommandsAndEventsForAggregates() {
    findDownByType(EntityI::class.java).filter { !it.virtual() && it.commands().isEmpty() }.extend {
        log.debug("Add default commands to ${name()}")
        val dataTypeProps = props().filter { !it.meta() && !it.key() }

        val entity = this

        commands(Commands {
            name("commands")

            createBy {
                name("create")
                props(*dataTypeProps.toTypedArray())
            }

            updateBy {
                name("update")
                props(*dataTypeProps.toTypedArray())
            }

            deleteBy {
                name("delete")
                props(id())
            }
        })

        if (events().isEmpty()) {
            events(Events {
                name("events")

                created {
                    name("created")
                    props(*dataTypeProps.toTypedArray())
                }

                updated {
                    name("updated")
                    props(*dataTypeProps.toTypedArray())
                }

                deleted {
                    name("deleted")
                    props(id())
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
                log.debug("Add CommandType to ${entity.name()}")
                enumType {
                    name("${entity.name()}CommandType")
                    items.forEach {
                        lit({ name(it.nameAndParentName()) })
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
                        lit({ name(it.parentNameAndName()) })
                    }
                }
            }
        }
    }
}


fun StructureUnitI.addAggregateType() {
    findDownByType(EntityI::class.java).filter { !it.virtual() && it.derivedAsType().isEmpty() }.groupBy {
        it.findParentMust(ModuleI::class.java)
    }.forEach { module, items ->
        module.extend {

            enumType {
                name("${module.name().capitalize()}AggregateType").derivedAsType(DesignDerivedType.AGGREGATE)
                prop({ type(n.String).name("commands") })
                prop({ type(n.String).name("events") })
                items.forEach {
                    lit({ name(it.nameAndParentName()) })
                }
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

fun EntityI.buildId(): AttributeI = Attribute { key(true).type(n.String).name("id") }

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

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

fun EntityI.findBy(vararg params: AttributeI) = findBy { params(*params) }
fun EntityI.existBy(vararg params: AttributeI) = existBy { params(*params) }

fun CompilationUnitI.op(vararg params: AttributeI, body: OperationI.() -> Unit = {}) = op {
    params(*params)
    body()
}

fun EntityI.command(vararg params: AttributeI) = command { props(*params) }
fun EntityI.createBy(vararg params: AttributeI) = createBy { props(*params) }
fun EntityI.updateBy(vararg params: AttributeI) = updateBy { props(*params) }
fun EntityI.deleteBy(vararg params: AttributeI) = deleteBy { props(*params) }
fun EntityI.composite(vararg commands: CommandI) = composite { commands(*commands) }

fun EntityI.event(vararg params: AttributeI) = event { props(*params) }
fun EntityI.created(vararg params: AttributeI) = created { props(*params) }
fun EntityI.updated(vararg params: AttributeI) = updated { props(*params) }
fun EntityI.deleted(vararg params: AttributeI) = deleted { props(*params) }

fun EntityI.hasNoQueries() = findBys().isEmpty() && countBys().isEmpty() && existBys().isEmpty()
fun EntityI.hasNoEvents() = events().isEmpty() && created().isEmpty() && updated().isEmpty() && deleted().isEmpty()
fun EntityI.hasNoCommands() = commands().isEmpty() && createBys().isEmpty() && updateBys().isEmpty() && deleteBys().isEmpty()

fun StructureUnitI.defineNamesForTypeControllers() {
    findDownByType(ControllerI::class.java).forEach {
        val parent = it.findParent(CompilationUnitI::class.java)
        if (parent != null) {
            it.name("${parent.name().capitalize()}${it.name().capitalize()}")
        }
    }
}

fun StructureUnitI.addQueriesForAggregates() {
    findDownByType(EntityI::class.java).filter { !it.virtual() && it.defaultQueries() }.extend {
        log.debug("Add default queries to ${name()}")

        findBy { name("findAll") }
        findBy {
            name("findById")
            params(id())
        }
        countBy { name("countAll") }
        countBy {
            name("countById")
            params(id())
        }
        existBy { name("existAll") }
        existBy {
            name("existById")
            params(id())
        }
    }
}

fun StructureUnitI.addCommandsAndEventsForAggregates() {
    findDownByType(EntityI::class.java).filter {
        !it.virtual() &&
                (it.defaultCommands() || it.defaultEvents())
    }.extend {
        val dataTypeProps = propsAll().filter { !it.meta() }.map { p(it).setOptionalTag() }

        var created: CreatedI = Created.EMPTY
        var updated: UpdatedI = Updated.EMPTY
        var deleted: DeletedI = Deleted.EMPTY

        if (defaultEvents()) {
            log.debug("Add default events to ${name()}")

            created = created {
                name("created")
                props(*dataTypeProps.toTypedArray())
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
            }

            updated = updated {
                name("updated")
                props(*dataTypeProps.toTypedArray())
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
            }

            deleted = deleted {
                name("deleted")
                props(id())
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
            }
        } else {
            created = created().firstOrNull() ?: created
            updated = updated().firstOrNull() ?: updated
            deleted = deleted().firstOrNull() ?: deleted
        }

        if (defaultCommands()) {
            log.debug("Add default commands to ${name()}")
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
        } else {
            createBys().filter { it.event().isEMPTY() }.forEach { it.event(created) }
            updateBys().filter { it.event().isEMPTY() }.forEach { it.event(updated) }
            deleteBys().filter { it.event().isEMPTY() }.forEach { it.event(deleted) }
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
        it.prop { type(T).name("addItem") }
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
    initIfNotInitialized()
    var ret = props().find { it.key() }
    if (ret == null && superUnit() is EntityI) {
        ret = (superUnit() as EntityI).id()
    } else if (ret == null) {
        log.warn("Id can't be found for '$this', return EMPTY")
        ret = Attribute.EMPTY
    }
    ret
})

package ee.design

import ee.common.ext.ifElse
import ee.design.gen.go.toGoPropOptionalAfterBody
import ee.lang.*
import ee.lang.gen.go.retTypeAndError
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
    val QueryRepository = "QueryRepository"
    val HttpQueryHandler = "HttpQueryHandler"
    val HttpCommandHandler = "HttpCommandHandler"
    val Event = "Event"
    val EventHandler = "EventHandler"
    val EventhorizonInitializer = "EventhorizonInitializer"
    val Handler = "Handler"
    val Projector = "Projector"
    val Query = "Query"

}

object DesignDerivedType : DesignDerivedTypeNames()


private val log = LoggerFactory.getLogger("DesignUtils")

fun EntityI.findBy(vararg params: AttributeI) = findBy {
    params(*params)
    retTypeAndError(this@findBy)
}

fun EntityI.existBy(vararg params: AttributeI) = existBy {
    params(*params)
    retTypeAndError(n.Boolean)
}

fun EntityI.countBy(vararg params: AttributeI) = countBy {
    params(*params)
    retTypeAndError(n.Int)
}

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

//TODO provide customizable solution for event name derivation from command
fun CommandI.deriveEventName() = name().endsWith("gin").ifElse(
        { name().replace("gin", "gged") }, { "${name()}d" })

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
        val item = this
        log.debug("Add default queries to ${name()}")

        findBy {
            name("FindAll")
            retTypeAndError(n.List.GT(item))
        }
        findBy {
            name("FindById")
            params(id())
            retTypeAndError(item)
        }
        countBy {
            name("CountAll")
            retTypeAndError(n.Long)
        }
        countBy {
            name("CountById")
            params(id())
            retTypeAndError(n.Long)
        }
        existBy {
            name("ExistAll")
            retTypeAndError(n.Boolean)
        }
        existBy {
            name("ExistById")
            params(id())
            retTypeAndError(n.Boolean)
        }
    }
}

fun StructureUnitI.addDefaultReturnValuesForQueries() {
    findDownByType(FindByI::class.java).filter { it.returns().isEmpty() }.extend {
        if (multiResult()) {
            retTypeAndError(n.List.GT(findParentMust(TypeI::class.java)))
        } else {
            retTypeAndError(findParentMust(TypeI::class.java))
        }
    }

    findDownByType(CountByI::class.java).filter { it.returns().isEmpty() }.extend {
        retTypeAndError(n.Long)
    }

    findDownByType(ExistByI::class.java).filter { it.returns().isEmpty() }.extend {
        retTypeAndError(n.Boolean)
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
        }

        //create corresponding events for all commands without events
        createBys().filter { it.event().isEMPTY() }.forEach {
            it.event(created {
                name(it.deriveEventName())
                props(*it.props().toTypedArray())
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
            })
        }
        updateBys().filter { it.event().isEMPTY() }.forEach {
            it.event(updated {
                name(it.deriveEventName())
                props(*it.props().toTypedArray())
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
            })
        }
        deleteBys().filter { it.event().isEMPTY() }.forEach {
            it.event(deleted {
                name(it.deriveEventName())
                props(*it.props().toTypedArray())
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
            })
        }
        commands().filter { it.event().isEMPTY() }.forEach {
            it.event(event {
                name(it.deriveEventName())
                props(*it.props().toTypedArray())
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
            })
        }
    }
}

fun StructureUnitI.addIdPropToEntities() {
    findDownByType(EntityI::class.java).filter { !it.virtual() && it.props().filter { it.key() }.isEmpty() }.extend {
        val id = buildId()
    }
}


fun StructureUnitI.addIdPropToEventsAndCommands() {
    findDownByType(EntityI::class.java).filter { !it.virtual() }.extend {
        created().filter { it.props().find { it.key() } == null }.forEach { it.prop(id()) }
        updated().filter { it.props().find { it.key() } == null }.forEach { it.prop(id()) }
        deleted().filter { it.props().find { it.key() } == null }.forEach { it.prop(id()) }

        events().filter { it.props().find { it.key() } == null }.forEach { it.prop(id()) }

        createBys().filter { it.props().find { it.key() } == null }.forEach { it.prop(id()) }
        updateBys().filter { it.props().find { it.key() } == null }.forEach { it.prop(id()) }
        deleteBys().filter { it.props().find { it.key() } == null }.forEach { it.prop(id()) }

        commands().filter { it.props().find { it.key() } == null }.forEach { it.prop(id()) }
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

fun EntityI.buildId(): AttributeI = prop { key(true).type(n.UUID).name("id") }

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

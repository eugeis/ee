package ee.design

import ee.common.ext.ifElse
import ee.common.ext.then
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

fun EntityIB<*>.findBy(vararg params: AttributeIB<*>) = findBy {
    params(*params)
    retTypeAndError(this@findBy)
}

fun EntityIB<*>.existBy(vararg params: AttributeIB<*>) = existBy {
    params(*params)
    retTypeAndError(n.Boolean)
}

fun EntityIB<*>.countBy(vararg params: AttributeIB<*>) = countBy {
    params(*params)
    retTypeAndError(n.Int)
}

fun CreateByIB<*>.primary() = findParentMust(EntityIB::class.java).createBys().size == 1 ||
        name().startsWith("create", true)

fun UpdateByIB<*>.primary() = findParentMust(EntityIB::class.java).updateBys().size == 1 ||
        name().startsWith("update", true)

fun DeleteByIB<*>.primary() = findParentMust(EntityIB::class.java).countBys().size == 1 ||
        name().startsWith("delete", true)

fun CompilationUnitIB<*>.op(vararg params: AttributeIB<*>, body: OperationIB<*>.() -> Unit = {}) = op {
    params(*params)
    body()
}

fun EntityIB<*>.command(vararg params: AttributeIB<*>) = command { props(*params) }
fun EntityIB<*>.createBy(vararg params: AttributeIB<*>) = createBy { props(*params) }
fun EntityIB<*>.updateBy(vararg params: AttributeIB<*>) = updateBy { props(*params) }
fun EntityIB<*>.deleteBy(vararg params: AttributeIB<*>) = deleteBy { props(*params) }
fun EntityIB<*>.composite(vararg commands: CommandIB<*>) = composite { commands(*commands) }

fun EntityIB<*>.event(vararg params: AttributeIB<*>) = event { props(*params) }
fun EntityIB<*>.created(vararg params: AttributeIB<*>) = created { props(*params) }
fun EntityIB<*>.updated(vararg params: AttributeIB<*>) = updated { props(*params) }
fun EntityIB<*>.deleted(vararg params: AttributeIB<*>) = deleted { props(*params) }

//TODO provide customizable solution for event name derivation from command
val consonants = ".*[wrtzpsdfghklxcvbnm]".toRegex()

fun CommandIB<*>.deriveEventName() = name().endsWith("gin").ifElse(
        { name().capitalize().replace("gin", "gged") },
        { "${name().capitalize()}${consonants.matches(name()).then("e")}d" })

fun EntityIB<*>.hasNoQueries() = findBys().isEmpty() && countBys().isEmpty() && existBys().isEmpty()
fun EntityIB<*>.hasNoEvents() = events().isEmpty() && created().isEmpty() && updated().isEmpty() && deleted().isEmpty()
fun EntityIB<*>.hasNoCommands() = commands().isEmpty() && createBys().isEmpty() && updateBys().isEmpty() && deleteBys().isEmpty()

fun StructureUnitIB<*>.defineNamesForTypeControllers() {
    findDownByType(ControllerIB::class.java).forEach {
        val parent = it.findParent(CompilationUnitIB::class.java)
        if (parent != null) {
            it.name("${parent.name().capitalize()}${it.name().capitalize()}")
        }
    }
}

fun StructureUnitIB<*>.addQueriesForAggregates() {
    findDownByType(EntityIB::class.java).filter { !it.virtual() && it.defaultQueries() }.extend {
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

fun StructureUnitIB<*>.addDefaultReturnValuesForQueries() {
    findDownByType(FindByIB::class.java).filter { it.returns().isEmpty() }.extend {
        if (multiResult()) {
            retTypeAndError(n.List.GT(findParentMust(TypeIB::class.java)))
        } else {
            retTypeAndError(findParentMust(TypeIB::class.java))
        }
    }

    findDownByType(CountByIB::class.java).filter { it.returns().isEmpty() }.extend {
        retTypeAndError(n.Long)
    }

    findDownByType(ExistByIB::class.java).filter { it.returns().isEmpty() }.extend {
        retTypeAndError(n.Boolean)
    }
}

fun StructureUnitIB<*>.addCommandsAndEventsForAggregates() {
    findDownByType(EntityIB::class.java).filter { !it.virtual() }.extend {
        val dataTypeProps = propsAll().filter { !it.meta() }.map { p(it) }

        var created: CreatedIB<*> = Created.EMPTY
        var updated: UpdatedIB<*> = Updated.EMPTY
        var deleted: DeletedIB<*> = Deleted.EMPTY

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
                props(*it.props().map { p(it) }.toTypedArray())
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
            })
        }
        updateBys().filter { it.event().isEMPTY() }.forEach {
            it.event(updated {
                name(it.deriveEventName())
                props(*it.props().map { p(it) }.toTypedArray())
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
            })
        }
        deleteBys().filter { it.event().isEMPTY() }.forEach {
            it.event(deleted {
                name(it.deriveEventName())
                props(*it.props().map { p(it) }.toTypedArray())
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
            })
        }
        commands().filter { it.event().isEMPTY() }.forEach {
            it.event(event {
                name(it.deriveEventName())
                props(*it.props().map { p(it) }.toTypedArray())
                constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
            })
        }
    }
}

fun StructureUnitIB<*>.addIdPropToEntities() {
    findDownByType(EntityIB::class.java).filter { !it.virtual() && it.props().find { it.key() } == null }.extend {
        val id = buildId()
    }
}

fun StructureUnitIB<*>.addIdPropToEventsAndCommands() {
    findDownByType(EntityIB::class.java).filter { !it.virtual() }.extend {
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


fun StructureUnitIB<*>.setOptionalTagToEventsAndCommandsProps() {
    val allProps = hashSetOf<AttributeIB<*>>()

    findDownByType(EventIB::class.java).forEach {
        allProps.addAll(it.props().filter { !it.key() })
    }

    findDownByType(CommandIB::class.java).forEach {
        allProps.addAll(it.props().filter { !it.key() })
    }

    allProps.forEach {
        it.setOptionalTag()
    }
}

fun AttributeIB<*>.setOptionalTag(): AttributeIB<*> {
    macrosAfterBody(AttributeIB<*>::toGoPropOptionalAfterBody.name)
    return this
}

fun AttributeIB<*>.applyValue(value: Any): ActionIB<*> {
    return ApplyAction { target(this@applyValue) }
}

/*
fun StructureUnitIB<*>.declareAsBaseWithNonImplementedOperation() {
    findDownByType(CompilationUnitIB::class.java).filter { it.operations().isNotEMPTY() && !it.base() }.forEach { it.base(true) }

    //derive controllers from super units
    findDownByType(ControllerIB::class.java).filter { it.parent() is CompilationUnitI }.forEach {
        val dataItem = it.parent() as CompilationUnitI
        dataItem.propagateItemToSubtypes(it)

        val T = it.G { type(dataItem).name("T") }
        it.prop { type(T).name("addItem") }
    }
}
*/

fun <T : CompilationUnitIB<*>> T.propagateItemToSubtypes(item: CompilationUnitIB<*>) {
    superUnitFor().filter { superUnitChild ->
        superUnitChild.items().filterIsInstance<CompilationUnitIB<*>>().find {
            (it.name() == item.name() || it.superUnit() == superUnitChild)
        } == null
    }.forEach { superUnitChild ->
        val derivedItem = item.deriveSubType {
            namespace(superUnitChild.namespace())
            G { type(superUnitChild).name("T") }
        }
        superUnitChild.addItem(derivedItem)
        superUnitChild.propagateItemToSubtypes(derivedItem)
    }
}

fun EntityIB<*>.buildId(): AttributeIB<*> = prop { key(true).type(n.UUID).name("id") }

fun EntityIB<*>.id(): AttributeIB<*> = storage.getOrPut(this, "id", {
    initIfNotInitialized()
    var ret = props().find { it.key() }
    if (ret == null && superUnit() is EntityIB<*>) {
        ret = (superUnit() as EntityIB<*>).id()
    } else if (ret == null) {
        log.warn("Id can't be found for '$this', return EMPTY")
        ret = Attribute.EMPTY
    }
    ret
})

fun EntityIB<*>.dataTypeProps(): List<AttributeIB<*>> = storage.getOrPut(this, "dataTypeProps", {
    propsAll().filter { !it.meta() }.map { p(it) }
})

fun EntityIB<*>.createdEvent(): EventIB<*> = storage.getOrPut(this, "createdEvent", {
    created {
        name("created")
        props(*dataTypeProps().toTypedArray())
        constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
    }
})


fun EntityIB<*>.updatedEvent(): EventIB<*> = storage.getOrPut(this, "updatedEvent", {
    updated {
        name("updated")
        props(*dataTypeProps().toTypedArray())
        constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
    }
})

fun EntityIB<*>.deletedEvent(): EventIB<*> = storage.getOrPut(this, "deletedEvent", {
    deleted {
        name("deleted")
        props(id())
        constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
    }
})

fun StateIB<*>.execute(command: CommandIB<*>, value: ExecutorIB<*>.() -> Unit = {}) = execute {
    on(command)
    value()
}

fun StateIB<*>.handle(event: EventIB<*>, value: HandlerIB<*>.() -> Unit = {}) = handle {
    on(event)
    value()
}



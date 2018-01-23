package ee.design

import ee.common.ext.ifElse
import ee.common.ext.then
import ee.design.gen.go.toGoPropOptionalAfterBody
import ee.lang.*
import ee.lang.gen.go.retTypeAndError
import org.slf4j.LoggerFactory
import java.util.logging.Handler

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

fun EntityI<*>.findBy(vararg params: AttributeI<*>) = findBy {
    params(*params)
    retTypeAndError(this@findBy)
}

fun EntityI<*>.existBy(vararg params: AttributeI<*>) = existBy {
    params(*params)
    retTypeAndError(n.Boolean)
}

fun EntityI<*>.countBy(vararg params: AttributeI<*>) = countBy {
    params(*params)
    retTypeAndError(n.Int)
}

fun CreateByI<*>.primary() =
    findParentMust(EntityI::class.java).createBys().size == 1 || name().startsWith("create", true)

fun UpdateByI<*>.primary() =
    findParentMust(EntityI::class.java).updateBys().size == 1 || name().startsWith("update", true)

fun DeleteByI<*>.primary() =
    findParentMust(EntityI::class.java).countBys().size == 1 || name().startsWith("delete", true)

fun CompilationUnitI<*>.op(vararg params: AttributeI<*>, body: OperationI<*>.() -> Unit = {}) = op {
    params(*params)
    body()
}

fun EntityI<*>.command(vararg params: AttributeI<*>) = command { props(*params) }
fun EntityI<*>.createBy(vararg params: AttributeI<*>) = createBy { props(*params) }
fun EntityI<*>.updateBy(vararg params: AttributeI<*>) = updateBy { props(*params) }
fun EntityI<*>.deleteBy(vararg params: AttributeI<*>) = deleteBy { props(*params) }
fun EntityI<*>.composite(vararg commands: CommandI<*>) = composite { commands(*commands) }

fun EntityI<*>.event(vararg params: AttributeI<*>) = event { props(*params) }
fun EntityI<*>.created(vararg params: AttributeI<*>) = created { props(*params) }
fun EntityI<*>.updated(vararg params: AttributeI<*>) = updated { props(*params) }
fun EntityI<*>.deleted(vararg params: AttributeI<*>) = deleted { props(*params) }

//TODO provide customizable solution for event name derivation from command
val consonants = ".*[wrtzpsdfghklxcvbnm]".toRegex()

fun CommandI<*>.deriveEventName() = name().endsWith("gin").ifElse({ name().capitalize().replace("gin", "gged") },
    { "${name().capitalize()}${consonants.matches(name()).then("e")}d" })

fun CommandI<*>.deriveEvent(): EventI<*> {
    val entity = findParentMust(EntityI::class.java)
    return when (this) {
        is CreateByI -> entity.created {
            name(deriveEventName())
            props(*props().map { p(it) }.toTypedArray())
            constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
        }
        is UpdateByI -> entity.updated {
            name(deriveEventName())
            props(*props().map { p(it) }.toTypedArray())
            constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
        }
        is DeleteByI -> entity.deleted {
            name(deriveEventName())
            props(*props().map { p(it) }.toTypedArray())
            constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
        }
        else         -> entity.event {
            name(deriveEventName())
            props(*props().map { p(it) }.toTypedArray())
            constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
        }
    }
}

fun EntityI<*>.hasNoQueries() = findBys().isEmpty() && countBys().isEmpty() && existBys().isEmpty()
fun EntityI<*>.hasNoEvents() = events().isEmpty() && created().isEmpty() && updated().isEmpty() && deleted().isEmpty()
fun EntityI<*>.hasNoCommands() =
    commands().isEmpty() && createBys().isEmpty() && updateBys().isEmpty() && deleteBys().isEmpty()

fun StructureUnitI<*>.renameControllersAccordingParentType() {
    findDownByType(ControllerI::class.java).forEach { item ->
        item.extendAdapt {
            val parent = findParent(CompilationUnitI::class.java)
            if (parent != null) {
                var parentPrefix = parent.name().capitalize()
                if (!name().startsWith(parentPrefix)) {
                    name("$parentPrefix${name().capitalize()}")
                }
            }
        }
    }
}

fun StructureUnitI<*>.addQueriesForAggregates() {
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

fun StructureUnitI<*>.addDefaultReturnValuesForQueries() {
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

fun StructureUnitI<*>.addCommandsAndEventsForAggregates() {
    findDownByType(EntityI::class.java).filter { !it.virtual() }.extend {

        if (defaultCommands()) {
            create()
            update()
            delete()
        }

        if (defaultEvents()) {
            findDownByType(CommandI::class.java).filter { it.event().isEMPTY() }.forEach { it.event(it.deriveEvent()) }
        }
    }
}

fun StructureUnitI<*>.addAggregateHandler() {
    findDownByType(EntityI::class.java).filter { !it.virtual() && it.handlers().isEmpty() }.extend {
        handler {
            val initial = state { }
        }
    }
}

fun StructureUnitI<*>.addIdPropToEntities() {
    findDownByType(EntityI::class.java).filter { !it.virtual() && it.props().find { it.key() } == null }.extend {
        val id = buildId()
    }
}

fun StructureUnitI<*>.addIdPropToEventsAndCommands() {
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


fun StructureUnitI<*>.setOptionalTagToEventsAndCommandsProps() {
    val allProps = hashSetOf<AttributeI<*>>()

    findDownByType(EventI::class.java).forEach {
        allProps.addAll(it.props().filter { !it.key() })
    }

    findDownByType(CommandI::class.java).forEach {
        allProps.addAll(it.props().filter { !it.key() })
    }

    allProps.forEach {
        it.setOptionalTag()
    }
}

fun AttributeI<*>.setOptionalTag(): AttributeI<*> {
    macrosAfterBody(AttributeI<*>::toGoPropOptionalAfterBody.name)
    return this
}

/*
fun StructureUnitI<*>.declareAsBaseWithNonImplementedOperation() {
    findDownByType(CompilationUnitI::class.java).filter { it.operations().isNotEMPTY() && !it.base() }.forEach { it.base(true) }

    //derive controllers from super units
    findDownByType(ControllerI::class.java).filter { it.parent() is CompilationUnitI }.forEach {
        val dataItem = it.parent() as CompilationUnitI
        dataItem.propagateItemToSubtypes(it)

        val T = it.G { type(dataItem).name("T") }
        it.prop { type(T).name("addItem") }
    }
}
*/

fun <T : CompilationUnitI<*>> T.propagateItemToSubtypes(item: CompilationUnitI<*>) {
    superUnitFor().filter { superUnitChild ->
        superUnitChild.items().filterIsInstance<CompilationUnitI<*>>().find {
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

fun EntityI<*>.buildId(): AttributeI<*> = prop { key(true).type(n.UUID).name("id") }

fun EntityI<*>.id(): AttributeI<*> = storage.getOrPut(this, "id", {
    initIfNotInitialized()
    var ret = props().find { it.key() }
    if (ret == null && superUnit() is EntityI<*>) {
        ret = (superUnit() as EntityI<*>).id()
    } else if (ret == null) {
        log.warn("Id can't be found for '$this', return EMPTY")
        ret = Attribute.EMPTY
    }
    ret
})

fun EntityI<*>.dataTypeProps(): List<AttributeI<*>> = storage.getOrPut(this, "dataTypeProps", {
    propsAll().filter { !it.meta() }.map { p(it) }
})

fun EntityI<*>.create(): CommandI<*> = storage.getOrPut(this, "create", {
    createBy {
        name("create")
        props(*dataTypeProps().toTypedArray())
        constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
    }
})


fun EntityI<*>.update(): CommandI<*> = storage.getOrPut(this, "update", {
    updateBy {
        name("update")
        props(*dataTypeProps().toTypedArray())
        constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
    }
})

fun EntityI<*>.delete(): CommandI<*> = storage.getOrPut(this, "delete", {
    deleteBy {
        name("delete")
        props(id())
        constructorAllProps { derivedAsType(LangDerivedKind.MANUAL) }
    }
})

fun StateI<*>.execute(command: CommandI<*>, value: ExecutorI<*>.() -> Unit = {}) = execute {
    on(command)
    value()
}

fun StateI<*>.handle(event: EventI<*>, value: HandlerI<*>.() -> Unit = {}) = handle {
    on(event)
    value()
}

fun eventOf(command: CommandI<*>): EventI<*> {
    if (command.event().isEMPTY()) {
        command.event(command.deriveEvent())
    }
    return command.event()
}

fun StateI<*>.executeAndProduce(command: CommandI<*>): ExecutorI<*> {
    val ret = execute(command)
    ret.produce(eventOf(command))
    return ret
}


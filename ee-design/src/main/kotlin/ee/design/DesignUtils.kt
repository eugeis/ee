package ee.design

import ee.common.ext.then
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
    val Aggregate = "Aggr"
    val AggregateCommands = "AggrCommands"
    val AggregateEvents = "AggrEvents"
    val AggregateInitializer = "AggrInitializer"
    val AggregateType = "AggregateType"
    val Command = "Command"
    val CommandHandler = "CommandHandler"
    val Http = "Http"
    val HttpRouter = "Router"
    val Client = "Client"
    val HttpClient = "Client"
    val Cli = "Cli"
    val QueryRepository = "QueryRepository"
    val HttpQueryHandler = "HttpQueryHandler"
    val HttpCommandHandler = "HttpCommandHandler"
    val Event = "Event"
    val EventHandler = "EventHandler"
    val EsInitializer = "EsInitializer"
    val Handler = "Handler"
    val Handlers = "Handlers"
    val Executor = "Executor"
    val Executors = "Executors"

    val Projector = "Projector"
    val Query = "Query"
    val StateMachine = "StateMachine"

}

object DesignDerivedType : DesignDerivedTypeNames()


private val log = LoggerFactory.getLogger("DesignUtils")

fun EntityI<*>.findBy(vararg params: AttributeI<*>) = findBy {
    params(*params)
    ret(this@findBy)
}

fun EntityI<*>.existBy(vararg params: AttributeI<*>) = existBy {
    params(*params)
    ret(n.Boolean)
}

fun EntityI<*>.countBy(vararg params: AttributeI<*>) = countBy {
    params(*params)
    ret(n.Int)
}

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

fun OperationI<*>.findParamKey() = params().find { it.isKey() }
fun OperationI<*>.findParamsNoKeys() = params().filter { !it.isKey() }
fun CompilationUnitI<*>.findPropKey() = props().find { it.isKey() }
fun CompilationUnitI<*>.findPropsNoKeys() = props().filter { !it.isKey() }

fun <T : OperationI<*>> List<T>.filterOpsWithKey(): List<T> =
    filter { item -> item.params().find { it.isKey() } != null }

fun <T : OperationI<*>> List<T>.filterOpsWithoutKey(): List<T> =
    filter { item -> item.params().find { it.isKey() } == null }

fun <T : CommandI<*>> List<T>.filterCommandsWithKey(): List<T> =
    filter { item -> item.props().find { it.isKey() } != null }

fun <T : CommandI<*>> List<T>.filterCommandsWithoutKey(): List<T> =
    filter { item -> item.props().find { it.isKey() } == null }


fun CommandI<*>.deriveEventName(): String {
    var ret = name().capitalize()
    when {
        ret.contains("Send") -> {
            ret = ret.replace("Send", "Sent")
        }
        name().contains("send") -> {
            ret = ret.replace("send", "sent")
        }
        name().endsWith("gin") -> {
            ret = ret.replace("gin", "gged")
        }
        else -> {
            ret = "$ret${consonants.matches(ret).then("e")}d"
        }
    }
    return ret
}

fun CommandI<*>.deriveEvent(): EventI<*> {
    val entity = findParentMust(EntityI::class.java)
    val command = this
    return when (this) {
        is CreateByI -> entity.created {
            name(deriveEventName())
            //paramsNotDerived(*command.paramsNotDerived().map { p(it) }.toTypedArray())
            props(*command.props().toTypedArray())
            constructorFull { derivedAsType(LangDerivedKind.MANUAL) }
        }
        is UpdateByI -> entity.updated {
            name(deriveEventName())
            //paramsNotDerived(*command.paramsNotDerived().map { p(it) }.toTypedArray())
            props(*command.props().toTypedArray())
            constructorFull { derivedAsType(LangDerivedKind.MANUAL) }
        }
        is DeleteByI -> entity.deleted {
            name(deriveEventName())
            //paramsNotDerived(*command.paramsNotDerived().map { p(it) }.toTypedArray())
            props(*command.props().toTypedArray())
            constructorFull { derivedAsType(LangDerivedKind.MANUAL) }
        }
        else -> entity.event {
            name(deriveEventName())
            //paramsNotDerived(*command.paramsNotDerived().map { p(it) }.toTypedArray())
            props(*command.props().toTypedArray())
            constructorFull { derivedAsType(LangDerivedKind.MANUAL) }
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
    findDownByType(EntityI::class.java).filter { !it.isVirtual() && it.isDefaultQueries() }.extend {
        val item = this
        log.debug("Add isDefault queries to ${name()}")

        findBy {
            name("FindAll")
            ret(n.List.GT(item))
        }
        findBy {
            name("FindById")
            params(propId())
            ret(item)
        }
        countBy {
            name("CountAll")
            ret(n.Long)
        }
        countBy {
            name("CountById")
            params(propId())
            ret(n.Long)
        }
        existBy {
            name("ExistAll")
            ret(n.Boolean)
        }
        existBy {
            name("ExistById")
            params(propId())
            ret(n.Boolean)
        }
    }
}

fun StructureUnitI<*>.addDefaultReturnValuesForQueries() {
    findDownByType(FindByI::class.java).filter { it.returns().isEmpty() }.extend {
        if (isMultiResult()) {
            ret(n.List.GT(findParentMust(TypeI::class.java)))
        } else {
            ret(findParentMust(TypeI::class.java))
        }
    }

    findDownByType(CountByI::class.java).filter { it.returns().isEmpty() }.extend {
        ret(n.Long)
    }

    findDownByType(ExistByI::class.java).filter { it.returns().isEmpty() }.extend {
        ret(n.Boolean)
    }
}

fun StructureUnitI<*>.addCommandsAndEventsForAggregates() {
    findDownByType(EntityI::class.java).filter { !it.isVirtual() }.extend {

        if (isDefaultCommands()) {
            create()
            update()
            delete()
        }

        if (isDefaultEvents()) {
            findDownByType(CommandI::class.java).filter { it.event().isEMPTY() }.forEach { it.event(it.deriveEvent()) }
        }
    }
}

fun StructureUnitI<*>.addAggregateHandler() {
    findDownByType(EntityI::class.java).filter { !it.isVirtual() && it.handlers().isEmpty() }.extend {
        handler {
            name("Handler")
            val initial = state { name("initial") }
        }
    }
}

fun StructureUnitI<*>.addIdPropToEntities() {
    findDownByType(EntityI::class.java).filter { item -> !item.isVirtual() && item.props().find { it.isKey() } == null }
        .extend {
            val id = propId()
        }
}

fun StructureUnitI<*>.addIdPropToCommands() {
    findDownByType(EntityI::class.java).filter { !it.isVirtual() }.extend {
        createBys().filter { item -> item.props().find { it.isKey() } == null }.forEach { it.prop(propId()) }
        updateBys().filter { item -> item.props().find { it.isKey() } == null }.forEach { it.prop(propId()) }
        deleteBys().filter { item -> item.props().find { it.isKey() } == null }.forEach { it.prop(propId()) }

        commands().filter { it.props().find { it.isKey() } == null }.forEach { it.prop(propId()) }
    }
}

fun StructureUnitI<*>.addIdPropToEventsAndCommands() {
    findDownByType(EntityI::class.java).filter { !it.isVirtual() }.extend {
        created().filter { it.props().find { it.isKey() } == null }.forEach { it.prop(propId()) }
        updated().filter { it.props().find { it.isKey() } == null }.forEach { it.prop(propId()) }
        deleted().filter { it.props().find { it.isKey() } == null }.forEach { it.prop(propId()) }

        events().filter { it.props().find { it.isKey() } == null }.forEach { it.prop(propId()) }

        createBys().filter { item -> item.props().find { it.isKey() } == null }.forEach { it.prop(propId()) }
        updateBys().filter { item -> item.props().find { it.isKey() } == null }.forEach { it.prop(propId()) }
        deleteBys().filter { item -> item.props().find { it.isKey() } == null }.forEach { it.prop(propId()) }

        commands().filter { it.props().find { it.isKey() } == null }.forEach { it.prop(propId()) }
    }
}

fun StructureUnitI<*>.markReplaceableConfigProps() {
    findDownByType(ConfigI::class.java).forEach { item ->
        item.props().forEach {
            it.replaceable()
        }
    }
}

fun <T : StructureUnitI<*>> T.markTypesPropsReplaceable(): T {
    findDownByType(TypeI::class.java).forEach { item ->
        if (item !is EnumTypeI && item !is ControllerI) {
            item.props().forEach { prop ->
                if (prop.isReplaceable() == null) {
                    prop.replaceable()
                }
            }
        }
    }
    return this
}

fun StructureUnitI<*>.setOptionalTagToEventsAndCommandsProps() {
    val allProps = hashSetOf<AttributeI<*>>()

    findDownByType(EventI::class.java).forEach {
        allProps.addAll(it.props().filter { !it.isKey() })
    }

    findDownByType(CommandI::class.java).forEach {
        allProps.addAll(it.props().filter { !it.isKey() })
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
    findDownByType(CompilationUnitI::class.java).filterSkipped { it.operations().isNotEMPTY() && !it.isBase() }.forEach { it.isBase(true) }

    //derive controllers from super units
    findDownByType(ControllerI::class.java).filterSkipped { it.parent() is CompilationUnitI }.forEach {
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

fun EntityI<*>.addPropId(): AttributeI<*> {
    return prop {
        key(true).type(n.UUID).name("id")
    }
}

fun EntityI<*>.propId(): AttributeI<*> = storage.getOrPut(this, "propId") {
    initIfNotInitialized()
    var ret = props().find { it.isKey() }
    if (ret == null && superUnit() is EntityI<*>) {
        ret = (superUnit() as EntityI<*>).propId()
    } else if (ret == null) {
        log.debug("prop 'id' can't be found for '$this', build default one")
        ret = addPropId()
    }
    ret
}

const val PROP_DELETED_AT = "deletedAt"

fun EntityI<*>.addPropDeletedAt(): AttributeI<*> {
    return prop {
        key(true).type(n.Date).name(PROP_DELETED_AT)
    }
}

fun EntityI<*>.propDeletedAt(): AttributeI<*> = storage.getOrPut(this, "propDeletedAt") {
    initIfNotInitialized()
    var ret = props().find { it.name() == PROP_DELETED_AT }
    if (ret == null && superUnit() is EntityI<*>) {
        ret = (superUnit() as EntityI<*>).propDeletedAt()
    } else if (ret == null) {
        log.debug("prop 'deleted' can't be found for '$this', build default one")
        ret = addPropDeletedAt()
    }
    ret
}

fun EntityI<*>.dataTypeProps(): List<AttributeI<*>> = storage.getOrPut(this, "dataTypeProps") {
    propsAll().filter { !it.isMeta() }.map { p(it) }
}

fun EntityI<*>.create(): CommandI<*> = storage.getOrPut(this, "create") {
    createBy {
        name("create")
        props(*dataTypeProps().toTypedArray())
        constructorFull { derivedAsType(LangDerivedKind.MANUAL) }
    }
}


fun EntityI<*>.update(): CommandI<*> = storage.getOrPut(this, "update") {
    updateBy {
        name("update")
        props(*dataTypeProps().toTypedArray())
        constructorFull { derivedAsType(LangDerivedKind.MANUAL) }
    }
}

fun EntityI<*>.delete(): CommandI<*> = storage.getOrPut(this, "delete") {
    deleteBy {
        name("delete")
        constructorFull { derivedAsType(LangDerivedKind.MANUAL) }
    }
}

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


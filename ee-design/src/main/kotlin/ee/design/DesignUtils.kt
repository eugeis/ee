package ee.design

import ee.common.ext.then
import ee.common.ext.toSingular
import ee.design.gen.go.toGoPropOptionalAfterBody
import ee.lang.*
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("DesignUtils")

open class DesignDerivedKindNames : LangDerivedKindNames() {
    val HttpGet = "Get"
    val HttpPost = "Post"
    val HttpPut = "Put"
    val HttpDelete = "Delete"
}

object DesignDerivedKind : DesignDerivedKindNames()

open class DesignDerivedTypeNames {
    val Aggregate = "Aggregate"
    val AggregateCommands = "AggregateCommands"
    val AggregateEvents = "AggregateEvents"
    val AggregateEngine = "AggregateEngine"
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
    val EsEngine = "EsEngine"
    val Handler = "Handler"
    val Handlers = "Handlers"
    val Executor = "Executor"
    val Executors = "Executors"
    val Logic = "Logic"

    val Projector = "Projector"
    val Query = "Query"
    val StateMachine = "State"
    val StateMachineEvents = "StateEvents"
    val StateMachineCommands = "StateCommands"

}

object DesignDerivedType : DesignDerivedTypeNames()


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
    ret = when {
        ret.contains("Send") -> {
            ret.replace("Send", "Sent")
        }
        name().contains("send") -> {
            ret.replace("send", "sent")
        }
        name().endsWith("gin") -> {
            ret.replace("gin", "gged")
        }
        else -> {
            "$ret${consonants.matches(ret).then("e")}d"
        }
    }
    return ret
}

fun CommandI<*>.deriveEvent(): EventI<*> {
    val entity = findParentMust(EntityI::class.java)
    return when (val command = this) {
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
        is AddChildByI -> entity.childAdded {
            name(deriveEventName())
            child(command.child())
            type(command.type())
            props(*command.props().toTypedArray())
            constructorFull { derivedAsType(LangDerivedKind.MANUAL) }
        }
        is UpdateChildByI -> entity.childUpdated {
            name(deriveEventName())
            child(command.child())
            type(command.type())
            props(*command.props().toTypedArray())
            constructorFull { derivedAsType(LangDerivedKind.MANUAL) }
        }
        is RemoveChildByI -> entity.childRemoved {
            name(deriveEventName())
            child(command.child())
            type(command.type())
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
                val parentPrefix = parent.name().capitalize()
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
            params(getOrAddPropId())
            ret(item)
        }
        countBy {
            name("CountAll")
            ret(n.Long)
        }
        countBy {
            name("CountById")
            params(getOrAddPropId())
            ret(n.Long)
        }
        existBy {
            name("ExistAll")
            ret(n.Boolean)
        }
        existBy {
            name("ExistById")
            params(getOrAddPropId())
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
            commandCreate()
            commandUpdate()
            commandDelete()

            propsCollectionValues().forEach {
                it.commandAddToList()
                it.commandUpdateToList()
                it.commandRemoveToList()
            }

            propsMapValues().forEach {
                it.commandPutToMap()
                it.commandUpdateToMap()
                it.commandRemoveFromMap()
            }
        }

        if (isDefaultEvents()) {
            findDownByType(CommandI::class.java).filter { it.event().isEMPTY() }.forEach {
                it.event(it.deriveEvent())
            }
        }
    }
}

fun AttributeI<*>.commandAddToList(): CommandI<*> = storage.getOrPut(this, "commandAddToList") {
    commandAdd(type().generics().first().type())
}

fun AttributeI<*>.commandUpdateToList(): CommandI<*> = storage.getOrPut(this, "commandUpdateToList") {
    commandUpdate(type().generics().first().type())
}

fun AttributeI<*>.commandRemoveToList(): CommandI<*> = storage.getOrPut(this, "commandRemoveToList") {
    commandRemove(type().generics().first().type())
}


fun AttributeI<*>.commandPutToMap(): CommandI<*> = storage.getOrPut(this, "commandPutToMap") {
    commandAdd(type().generics()[1].type())
}

fun AttributeI<*>.commandUpdateToMap(): CommandI<*> = storage.getOrPut(this, "commandUpdateToMap") {
    commandUpdate(type().generics()[1].type())
}

fun AttributeI<*>.commandRemoveFromMap(): CommandI<*> = storage.getOrPut(this, "commandRemoveFromMap") {
    commandRemove(type().generics()[1].type())
}

private fun AttributeI<*>.commandAdd(type: TypeI<*>): AddChildByI<*> {
    val attr = this
    val entity = parent() as EntityI
    return entity.addChildBy {
        name("${attr.name().toSingular()}Add")
        child(attr)
        type(type)
        prop { name("value").type(type).anonymous() }
        constructorFull { derivedAsType(LangDerivedKind.MANUAL) }
    }
}

private fun AttributeI<*>.commandUpdate(type: TypeI<*>): UpdateChildByI<*> {
    val attr = this
    val entity = parent() as EntityI
    return entity.updateChildBy {
        name("${attr.name().toSingular()}Update")
        child(attr)
        type(type)
        prop { name("value").type(type).anonymous() }
        constructorFull { derivedAsType(LangDerivedKind.MANUAL) }
    }
}

private fun AttributeI<*>.commandRemove(type: TypeI<*>): RemoveChildByI<*> {
    val attr = this
    val entity = parent() as EntityI
    return entity.removeChildBy {
        name("${attr.name().toSingular()}Remove")
        child(attr)
        type(type)
        prop { name("value").type(type).anonymous() }
        constructorFull { derivedAsType(LangDerivedKind.MANUAL) }
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
    findDownByType(EntityI::class.java).filter { item -> !item.isVirtual() && item.propId() == null }
        .forEach {
            it.getOrAddPropId()
        }
}

fun StructureUnitI<*>.addIdPropToCommands() {
    findDownByType(EntityI::class.java).filter { !it.isVirtual() }.forEach { entity ->
        entity.findDownByType(CommandI::class.java).filter { item -> item.propId() == null }
            .forEach {
                it.prop(entity.getOrAddPropId())
            }
    }
}

fun StructureUnitI<*>.addIdPropToValues() {
    findDownByType(EntityI::class.java).filter { !it.isVirtual() }.forEach { entity ->
        entity.propsCollectionValueTypes().filter { item -> item.propId() == null }
            .forEach {
                it.prop(entity.getOrAddPropId())
            }
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

fun EntityI<*>.commandCreate(): CommandI<*> = storage.getOrPut(this, "commandCreate") {
    createBy {
        name("create")
        props(*dataTypeProps().toTypedArray())
        constructorFull { derivedAsType(LangDerivedKind.MANUAL) }
    }
}


fun EntityI<*>.commandUpdate(): CommandI<*> = storage.getOrPut(this, "commandUpdate") {
    updateBy {
        name("update")
        props(*dataTypeProps().toTypedArray())
        constructorFull { derivedAsType(LangDerivedKind.MANUAL) }
    }
}

fun EntityI<*>.commandDelete(): CommandI<*> = storage.getOrPut(this, "commandDelete") {
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

fun StateI<*>.executeAndProduceAndHandle(command: CommandI<*>): HandlerI<*> {
    val ret = execute(command)
    val event = eventOf(command)
    ret.produce(event)
    return handle(event)
}

fun EventI<*>.hasData(): Boolean = propsNoMetaNoValueNoId().isNotEmpty()
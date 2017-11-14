package ee.design

import ee.lang.*


open class Action(value: Action.() -> Unit = {}) : ActionB<Action>(value) {

    companion object {
        val EMPTY = Action { name(ItemEmpty.name()) }.apply<Action> { init() }
    }
}

open class ActionB<B : ActionB<B>>(value: B.() -> Unit = {}) : LogicUnitB<B>(value), ActionIB<B> {
}


open class AggregateHandler(value: AggregateHandler.() -> Unit = {}) : AggregateHandlerB<AggregateHandler>(value) {

    companion object {
        val EMPTY = AggregateHandler { name(ItemEmpty.name()) }.apply<AggregateHandler> { init() }
    }
}

open class AggregateHandlerB<B : AggregateHandlerB<B>>(value: B.() -> Unit = {}) : StateMachineB<B>(value), AggregateHandlerIB<B> {
}


open class ApplyAction(value: ApplyAction.() -> Unit = {}) : ApplyActionB<ApplyAction>(value) {

    companion object {
        val EMPTY = ApplyAction { name(ItemEmpty.name()) }.apply<ApplyAction> { init() }
    }
}

open class ApplyActionB<B : ApplyActionB<B>>(value: B.() -> Unit = {}) : ActionB<B>(value), ApplyActionIB<B> {

    override fun target(): AttributeIB<*> = attr(TARGET, { Attribute.EMPTY })
    override fun target(value: AttributeIB<*>): B = apply { attr(TARGET, value) }

    override fun operator(): AttributeIB<*> = attr(OPERATOR, { Attribute.EMPTY })
    override fun operator(value: AttributeIB<*>): B = apply { attr(OPERATOR, value) }

    override fun value(): Any = attr(VALUE, { "" })
    override fun value(aValue: Any): B = apply { attr(VALUE, aValue) }

    companion object {
        val TARGET = "_target"
        val OPERATOR = "_operator"
        val VALUE = "_value"
    }
}


open class Basic(value: Basic.() -> Unit = {}) : BasicB<Basic>(value) {

    companion object {
        val EMPTY = Basic { name(ItemEmpty.name()) }.apply<Basic> { init() }
    }
}

open class BasicB<B : BasicB<B>>(value: B.() -> Unit = {}) : DataTypeB<B>(value), BasicIB<B> {
}


open class Bundle(value: Bundle.() -> Unit = {}) : BundleB<Bundle>(value) {

    companion object {
        val EMPTY = Bundle { name(ItemEmpty.name()) }.apply<Bundle> { init() }
    }
}

open class BundleB<B : BundleB<B>>(value: B.() -> Unit = {}) : StructureUnitB<B>(value), BundleIB<B> {

    override fun units(): ListMultiHolder<StructureUnitIB<*>> = itemAsList(UNITS, StructureUnitIB::class.java, true)
    override fun units(vararg value: StructureUnitIB<*>): B = apply { units().addItems(value.asList()) }

    override fun fillSupportsItems() {
        units()
        super.fillSupportsItems()
    }

    companion object {
        val UNITS = "_units"
    }
}


open class BusinessCommand(value: BusinessCommand.() -> Unit = {}) : BusinessCommandB<BusinessCommand>(value) {

    companion object {
        val EMPTY = BusinessCommand { name(ItemEmpty.name()) }.apply<BusinessCommand> { init() }
    }
}

open class BusinessCommandB<B : BusinessCommandB<B>>(value: B.() -> Unit = {}) : CommandB<B>(value), BusinessCommandIB<B> {
}


open class BussinesEvent(value: BussinesEvent.() -> Unit = {}) : BussinesEventB<BussinesEvent>(value) {

    companion object {
        val EMPTY = BussinesEvent { name(ItemEmpty.name()) }.apply<BussinesEvent> { init() }
    }
}

open class BussinesEventB<B : BussinesEventB<B>>(value: B.() -> Unit = {}) : EventB<B>(value), BussinesEventIB<B> {
}


open class Check(value: Check.() -> Unit = {}) : CheckB<Check>(value) {

    companion object {
        val EMPTY = Check { name(ItemEmpty.name()) }.apply<Check> { init() }
    }
}

open class CheckB<B : CheckB<B>>(value: B.() -> Unit = {}) : LogicUnitB<B>(value), CheckIB<B> {

    override fun cachedInContext(): Boolean = attr(CACHED_IN_CONTEXT, { false })
    override fun cachedInContext(value: Boolean): B = apply { attr(CACHED_IN_CONTEXT, value) }

    companion object {
        val CACHED_IN_CONTEXT = "_cachedInContext"
    }
}


open class Command(value: Command.() -> Unit = {}) : CommandB<Command>(value) {

    companion object {
        val EMPTY = Command { name(ItemEmpty.name()) }.apply<Command> { init() }
    }
}

open class CommandB<B : CommandB<B>>(value: B.() -> Unit = {}) : EventB<B>(value), CommandIB<B> {

    override fun affectMulti(): Boolean = attr(AFFECT_MULTI, { false })
    override fun affectMulti(value: Boolean): B = apply { attr(AFFECT_MULTI, value) }

    override fun event(): EventIB<*> = attr(EVENT, { Event.EMPTY })
    override fun event(value: EventIB<*>): B = apply { attr(EVENT, value) }

    companion object {
        val AFFECT_MULTI = "_affectMulti"
        val EVENT = "_event"
    }
}


open class Comp(value: Comp.() -> Unit = {}) : CompB<Comp>(value) {

    companion object {
        val EMPTY = Comp { name(ItemEmpty.name()) }.apply<Comp> { init() }
    }
}

open class CompB<B : CompB<B>>(value: B.() -> Unit = {}) : ModuleGroupB<B>(value), CompIB<B> {

    override fun moduleGroups(): ListMultiHolder<ModuleGroupIB<*>> = itemAsList(MODULE_GROUPS, ModuleGroupIB::class.java, true)
    override fun moduleGroups(vararg value: ModuleGroupIB<*>): B = apply { moduleGroups().addItems(value.asList()) }

    override fun fillSupportsItems() {
        moduleGroups()
        super.fillSupportsItems()
    }

    companion object {
        val MODULE_GROUPS = "_moduleGroups"
    }
}


open class CompositeCommand(value: CompositeCommand.() -> Unit = {}) : CompositeCommandB<CompositeCommand>(value) {

    companion object {
        val EMPTY = CompositeCommand { name(ItemEmpty.name()) }.apply<CompositeCommand> { init() }
    }
}

open class CompositeCommandB<B : CompositeCommandB<B>>(value: B.() -> Unit = {}) : CompilationUnitB<B>(value), CompositeCommandIB<B> {

    override fun commands(): ListMultiHolder<CommandIB<*>> = itemAsList(COMMANDS, CommandIB::class.java, true)
    override fun commands(vararg value: CommandIB<*>): B = apply { commands().addItems(value.asList()) }

    override fun fillSupportsItems() {
        commands()
        super.fillSupportsItems()
    }

    companion object {
        val COMMANDS = "_commands"
    }
}


open class Controller(value: Controller.() -> Unit = {}) : ControllerB<Controller>(value) {

    companion object {
        val EMPTY = Controller { name(ItemEmpty.name()) }.apply<Controller> { init() }
    }
}

open class ControllerB<B : ControllerB<B>>(value: B.() -> Unit = {}) : CompilationUnitB<B>(value), ControllerIB<B> {

    override fun enums(): ListMultiHolder<EnumTypeIB<*>> = itemAsList(ENUMS, EnumTypeIB::class.java, true)
    override fun enums(vararg value: EnumTypeIB<*>): B = apply { enums().addItems(value.asList()) }
    override fun enumType(value: EnumTypeIB<*>): EnumTypeIB<*> = applyAndReturn { enums().addItem(value); value }
    override fun enumType(value: EnumTypeIB<*>.() -> Unit): EnumTypeIB<*> = enumType(EnumType(value))

    override fun values(): ListMultiHolder<ValuesIB<*>> = itemAsList(VALUES, ValuesIB::class.java, true)
    override fun values(vararg value: ValuesIB<*>): B = apply { values().addItems(value.asList()) }
    override fun valueType(value: ValuesIB<*>): ValuesIB<*> = applyAndReturn { values().addItem(value); value }
    override fun valueType(value: ValuesIB<*>.() -> Unit): ValuesIB<*> = valueType(Values(value))

    override fun basics(): ListMultiHolder<BasicIB<*>> = itemAsList(BASICS, BasicIB::class.java, true)
    override fun basics(vararg value: BasicIB<*>): B = apply { basics().addItems(value.asList()) }
    override fun basic(value: BasicIB<*>): BasicIB<*> = applyAndReturn { basics().addItem(value); value }
    override fun basic(value: BasicIB<*>.() -> Unit): BasicIB<*> = basic(Basic(value))

    override fun fillSupportsItems() {
        enums()
        values()
        basics()
        super.fillSupportsItems()
    }

    companion object {
        val ENUMS = "_enums"
        val VALUES = "_values"
        val BASICS = "_basics"
    }
}


open class CountBy(value: CountBy.() -> Unit = {}) : CountByB<CountBy>(value) {

    companion object {
        val EMPTY = CountBy { name(ItemEmpty.name()) }.apply<CountBy> { init() }
    }
}

open class CountByB<B : CountByB<B>>(value: B.() -> Unit = {}) : DataTypeOperationB<B>(value), CountByIB<B> {
}


open class CreateBy(value: CreateBy.() -> Unit = {}) : CreateByB<CreateBy>(value) {

    companion object {
        val EMPTY = CreateBy { name(ItemEmpty.name()) }.apply<CreateBy> { init() }
    }
}

open class CreateByB<B : CreateByB<B>>(value: B.() -> Unit = {}) : CommandB<B>(value), CreateByIB<B> {
}


open class Created(value: Created.() -> Unit = {}) : CreatedB<Created>(value) {

    companion object {
        val EMPTY = Created { name(ItemEmpty.name()) }.apply<Created> { init() }
    }
}

open class CreatedB<B : CreatedB<B>>(value: B.() -> Unit = {}) : EventB<B>(value), CreatedIB<B> {
}


open class DeleteBy(value: DeleteBy.() -> Unit = {}) : DeleteByB<DeleteBy>(value) {

    companion object {
        val EMPTY = DeleteBy { name(ItemEmpty.name()) }.apply<DeleteBy> { init() }
    }
}

open class DeleteByB<B : DeleteByB<B>>(value: B.() -> Unit = {}) : CommandB<B>(value), DeleteByIB<B> {
}


open class Deleted(value: Deleted.() -> Unit = {}) : DeletedB<Deleted>(value) {

    companion object {
        val EMPTY = Deleted { name(ItemEmpty.name()) }.apply<Deleted> { init() }
    }
}

open class DeletedB<B : DeletedB<B>>(value: B.() -> Unit = {}) : EventB<B>(value), DeletedIB<B> {
}


open class DynamicState(value: DynamicState.() -> Unit = {}) : DynamicStateB<DynamicState>(value) {

    companion object {
        val EMPTY = DynamicState { name(ItemEmpty.name()) }.apply<DynamicState> { init() }
    }
}

open class DynamicStateB<B : DynamicStateB<B>>(value: B.() -> Unit = {}) : StateB<B>(value), DynamicStateIB<B> {

    override fun checks(): ListMultiHolder<CheckIB<*>> = itemAsList(CHECKS, CheckIB::class.java, true)
    override fun checks(vararg value: CheckIB<*>): B = apply { checks().addItems(value.asList()) }
    override fun yes(value: CheckIB<*>): CheckIB<*> = applyAndReturn { checks().addItem(value); value }
    override fun yes(value: CheckIB<*>.() -> Unit): CheckIB<*> = yes(Check(value))

    override fun notChecks(): ListMultiHolder<CheckIB<*>> = itemAsList(NOT_CHECKS, CheckIB::class.java, true)
    override fun notChecks(vararg value: CheckIB<*>): B = apply { notChecks().addItems(value.asList()) }
    override fun no(value: CheckIB<*>): CheckIB<*> = applyAndReturn { notChecks().addItem(value); value }
    override fun no(value: CheckIB<*>.() -> Unit): CheckIB<*> = no(Check(value))

    override fun fillSupportsItems() {
        checks()
        notChecks()
        super.fillSupportsItems()
    }

    companion object {
        val CHECKS = "_checks"
        val NOT_CHECKS = "_notChecks"
    }
}


open class Entity(value: Entity.() -> Unit = {}) : EntityB<Entity>(value) {

    companion object {
        val EMPTY = Entity { name(ItemEmpty.name()) }.apply<Entity> { init() }
    }
}

open class EntityB<B : EntityB<B>>(value: B.() -> Unit = {}) : DataTypeB<B>(value), EntityIB<B> {

    override fun defaultEvents(): Boolean = attr(DEFAULT_EVENTS, { true })
    override fun defaultEvents(value: Boolean): B = apply { attr(DEFAULT_EVENTS, value) }

    override fun defaultQueries(): Boolean = attr(DEFAULT_QUERIES, { true })
    override fun defaultQueries(value: Boolean): B = apply { attr(DEFAULT_QUERIES, value) }

    override fun defaultCommands(): Boolean = attr(DEFAULT_COMMANDS, { true })
    override fun defaultCommands(value: Boolean): B = apply { attr(DEFAULT_COMMANDS, value) }

    override fun belongsToAggregate(): EntityIB<*> = attr(BELONGS_TO_AGGREGATE, { Entity.EMPTY })
    override fun belongsToAggregate(value: EntityIB<*>): B = apply { attr(BELONGS_TO_AGGREGATE, value) }

    override fun aggregateFor(): ListMultiHolder<EntityIB<*>> = itemAsList(AGGREGATE_FOR, EntityIB::class.java, true)
    override fun aggregateFor(vararg value: EntityIB<*>): B = apply { aggregateFor().addItems(value.asList()) }

    override fun controllers(): ListMultiHolder<ControllerIB<*>> = itemAsList(CONTROLLERS, ControllerIB::class.java, true)
    override fun controllers(vararg value: ControllerIB<*>): B = apply { controllers().addItems(value.asList()) }
    override fun controller(value: ControllerIB<*>): ControllerIB<*> = applyAndReturn { controllers().addItem(value); value }
    override fun controller(value: ControllerIB<*>.() -> Unit): ControllerIB<*> = controller(Controller(value))

    override fun findBys(): ListMultiHolder<FindByIB<*>> = itemAsList(FIND_BYS, FindByIB::class.java, true)
    override fun findBys(vararg value: FindByIB<*>): B = apply { findBys().addItems(value.asList()) }
    override fun findBy(value: FindByIB<*>): FindByIB<*> = applyAndReturn { findBys().addItem(value); value }
    override fun findBy(value: FindByIB<*>.() -> Unit): FindByIB<*> = findBy(FindBy(value))

    override fun countBys(): ListMultiHolder<CountByIB<*>> = itemAsList(COUNT_BYS, CountByIB::class.java, true)
    override fun countBys(vararg value: CountByIB<*>): B = apply { countBys().addItems(value.asList()) }
    override fun countBy(value: CountByIB<*>): CountByIB<*> = applyAndReturn { countBys().addItem(value); value }
    override fun countBy(value: CountByIB<*>.() -> Unit): CountByIB<*> = countBy(CountBy(value))

    override fun existBys(): ListMultiHolder<ExistByIB<*>> = itemAsList(EXIST_BYS, ExistByIB::class.java, true)
    override fun existBys(vararg value: ExistByIB<*>): B = apply { existBys().addItems(value.asList()) }
    override fun existBy(value: ExistByIB<*>): ExistByIB<*> = applyAndReturn { existBys().addItem(value); value }
    override fun existBy(value: ExistByIB<*>.() -> Unit): ExistByIB<*> = existBy(ExistBy(value))

    override fun commands(): ListMultiHolder<BusinessCommandIB<*>> = itemAsList(COMMANDS, BusinessCommandIB::class.java, true)
    override fun commands(vararg value: BusinessCommandIB<*>): B = apply { commands().addItems(value.asList()) }
    override fun command(value: BusinessCommandIB<*>): BusinessCommandIB<*> = applyAndReturn { commands().addItem(value); value }
    override fun command(value: BusinessCommandIB<*>.() -> Unit): BusinessCommandIB<*> = command(BusinessCommand(value))

    override fun composites(): ListMultiHolder<CompositeCommandIB<*>> = itemAsList(COMPOSITES, CompositeCommandIB::class.java, true)
    override fun composites(vararg value: CompositeCommandIB<*>): B = apply { composites().addItems(value.asList()) }
    override fun composite(value: CompositeCommandIB<*>): CompositeCommandIB<*> = applyAndReturn { composites().addItem(value); value }
    override fun composite(value: CompositeCommandIB<*>.() -> Unit): CompositeCommandIB<*> = composite(CompositeCommand(value))

    override fun createBys(): ListMultiHolder<CreateByIB<*>> = itemAsList(CREATE_BYS, CreateByIB::class.java, true)
    override fun createBys(vararg value: CreateByIB<*>): B = apply { createBys().addItems(value.asList()) }
    override fun createBy(value: CreateByIB<*>): CreateByIB<*> = applyAndReturn { createBys().addItem(value); value }
    override fun createBy(value: CreateByIB<*>.() -> Unit): CreateByIB<*> = createBy(CreateBy(value))

    override fun updateBys(): ListMultiHolder<UpdateByIB<*>> = itemAsList(UPDATE_BYS, UpdateByIB::class.java, true)
    override fun updateBys(vararg value: UpdateByIB<*>): B = apply { updateBys().addItems(value.asList()) }
    override fun updateBy(value: UpdateByIB<*>): UpdateByIB<*> = applyAndReturn { updateBys().addItem(value); value }
    override fun updateBy(value: UpdateByIB<*>.() -> Unit): UpdateByIB<*> = updateBy(UpdateBy(value))

    override fun deleteBys(): ListMultiHolder<DeleteByIB<*>> = itemAsList(DELETE_BYS, DeleteByIB::class.java, true)
    override fun deleteBys(vararg value: DeleteByIB<*>): B = apply { deleteBys().addItems(value.asList()) }
    override fun deleteBy(value: DeleteByIB<*>): DeleteByIB<*> = applyAndReturn { deleteBys().addItem(value); value }
    override fun deleteBy(value: DeleteByIB<*>.() -> Unit): DeleteByIB<*> = deleteBy(DeleteBy(value))

    override fun events(): ListMultiHolder<BussinesEventIB<*>> = itemAsList(EVENTS, BussinesEventIB::class.java, true)
    override fun events(vararg value: BussinesEventIB<*>): B = apply { events().addItems(value.asList()) }
    override fun event(value: BussinesEventIB<*>): BussinesEventIB<*> = applyAndReturn { events().addItem(value); value }
    override fun event(value: BussinesEventIB<*>.() -> Unit): BussinesEventIB<*> = event(BussinesEvent(value))

    override fun created(): ListMultiHolder<CreatedIB<*>> = itemAsList(CREATED, CreatedIB::class.java, true)
    override fun created(vararg value: CreatedIB<*>): B = apply { created().addItems(value.asList()) }
    override fun created(value: CreatedIB<*>): CreatedIB<*> = applyAndReturn { created().addItem(value); value }
    override fun created(value: CreatedIB<*>.() -> Unit): CreatedIB<*> = created(Created(value))

    override fun updated(): ListMultiHolder<UpdatedIB<*>> = itemAsList(UPDATED, UpdatedIB::class.java, true)
    override fun updated(vararg value: UpdatedIB<*>): B = apply { updated().addItems(value.asList()) }
    override fun updated(value: UpdatedIB<*>): UpdatedIB<*> = applyAndReturn { updated().addItem(value); value }
    override fun updated(value: UpdatedIB<*>.() -> Unit): UpdatedIB<*> = updated(Updated(value))

    override fun deleted(): ListMultiHolder<DeletedIB<*>> = itemAsList(DELETED, DeletedIB::class.java, true)
    override fun deleted(vararg value: DeletedIB<*>): B = apply { deleted().addItems(value.asList()) }
    override fun deleted(value: DeletedIB<*>): DeletedIB<*> = applyAndReturn { deleted().addItem(value); value }
    override fun deleted(value: DeletedIB<*>.() -> Unit): DeletedIB<*> = deleted(Deleted(value))

    override fun handlers(): ListMultiHolder<AggregateHandlerIB<*>> = itemAsList(HANDLERS, AggregateHandlerIB::class.java, true)
    override fun handlers(vararg value: AggregateHandlerIB<*>): B = apply { handlers().addItems(value.asList()) }
    override fun handler(value: AggregateHandlerIB<*>): AggregateHandlerIB<*> = applyAndReturn { handlers().addItem(value); value }
    override fun handler(value: AggregateHandlerIB<*>.() -> Unit): AggregateHandlerIB<*> = handler(AggregateHandler(value))

    override fun projectors(): ListMultiHolder<ProjectorIB<*>> = itemAsList(PROJECTORS, ProjectorIB::class.java, true)
    override fun projectors(vararg value: ProjectorIB<*>): B = apply { projectors().addItems(value.asList()) }
    override fun projector(value: ProjectorIB<*>): ProjectorIB<*> = applyAndReturn { projectors().addItem(value); value }
    override fun projector(value: ProjectorIB<*>.() -> Unit): ProjectorIB<*> = projector(Projector(value))

    override fun processManager(): ListMultiHolder<ProcessManagerIB<*>> = itemAsList(PROCESS_MANAGER, ProcessManagerIB::class.java, true)
    override fun processManager(vararg value: ProcessManagerIB<*>): B = apply { processManager().addItems(value.asList()) }
    override fun processManager(value: ProcessManagerIB<*>): ProcessManagerIB<*> = applyAndReturn { processManager().addItem(value); value }
    override fun processManager(value: ProcessManagerIB<*>.() -> Unit): ProcessManagerIB<*> = processManager(ProcessManager(value))

    override fun fillSupportsItems() {
        aggregateFor()
        controllers()
        findBys()
        countBys()
        existBys()
        commands()
        composites()
        createBys()
        updateBys()
        deleteBys()
        events()
        created()
        updated()
        deleted()
        handlers()
        projectors()
        processManager()
        super.fillSupportsItems()
    }

    companion object {
        val DEFAULT_EVENTS = "_defaultEvents"
        val DEFAULT_QUERIES = "_defaultQueries"
        val DEFAULT_COMMANDS = "_defaultCommands"
        val BELONGS_TO_AGGREGATE = "_belongsToAggregate"
        val AGGREGATE_FOR = "_aggregateFor"
        val CONTROLLERS = "_controllers"
        val FIND_BYS = "_findBys"
        val COUNT_BYS = "_countBys"
        val EXIST_BYS = "_existBys"
        val COMMANDS = "_commands"
        val COMPOSITES = "_composites"
        val CREATE_BYS = "_createBys"
        val UPDATE_BYS = "_updateBys"
        val DELETE_BYS = "_deleteBys"
        val EVENTS = "_events"
        val CREATED = "_created"
        val UPDATED = "_updated"
        val DELETED = "_deleted"
        val HANDLERS = "_handlers"
        val PROJECTORS = "_projectors"
        val PROCESS_MANAGER = "_processManager"
    }
}


open class Event(value: Event.() -> Unit = {}) : EventB<Event>(value) {

    companion object {
        val EMPTY = Event { name(ItemEmpty.name()) }.apply<Event> { init() }
    }
}

open class EventB<B : EventB<B>>(value: B.() -> Unit = {}) : CompilationUnitB<B>(value), EventIB<B> {
}


open class Executor(value: Executor.() -> Unit = {}) : ExecutorB<Executor>(value) {

    companion object {
        val EMPTY = Executor { name(ItemEmpty.name()) }.apply<Executor> { init() }
    }
}

open class ExecutorB<B : ExecutorB<B>>(value: B.() -> Unit = {}) : LogicUnitB<B>(value), ExecutorIB<B> {

    override fun on(): CommandIB<*> = attr(ON, { Command.EMPTY })
    override fun on(value: CommandIB<*>): B = apply { attr(ON, value) }

    override fun checks(): ListMultiHolder<CheckIB<*>> = itemAsList(CHECKS, CheckIB::class.java, true)
    override fun checks(vararg value: CheckIB<*>): B = apply { checks().addItems(value.asList()) }
    override fun yes(value: CheckIB<*>): CheckIB<*> = applyAndReturn { checks().addItem(value); value }
    override fun yes(value: CheckIB<*>.() -> Unit): CheckIB<*> = yes(Check(value))

    override fun notChecks(): ListMultiHolder<CheckIB<*>> = itemAsList(NOT_CHECKS, CheckIB::class.java, true)
    override fun notChecks(vararg value: CheckIB<*>): B = apply { notChecks().addItems(value.asList()) }
    override fun no(value: CheckIB<*>): CheckIB<*> = applyAndReturn { notChecks().addItem(value); value }
    override fun no(value: CheckIB<*>.() -> Unit): CheckIB<*> = no(Check(value))

    override fun actions(): ListMultiHolder<ActionIB<*>> = itemAsList(ACTIONS, ActionIB::class.java, true)
    override fun actions(vararg value: ActionIB<*>): B = apply { actions().addItems(value.asList()) }
    override fun action(value: ActionIB<*>): ActionIB<*> = applyAndReturn { actions().addItem(value); value }
    override fun action(value: ActionIB<*>.() -> Unit): ActionIB<*> = action(Action(value))

    override fun output(): ListMultiHolder<EventIB<*>> = itemAsList(OUTPUT, EventIB::class.java, true)
    override fun output(vararg value: EventIB<*>): B = apply { output().addItems(value.asList()) }
    override fun produce(value: EventIB<*>): EventIB<*> = applyAndReturn { output().addItem(value); value }
    override fun produce(value: EventIB<*>.() -> Unit): EventIB<*> = produce(Event(value))

    override fun fillSupportsItems() {
        checks()
        notChecks()
        actions()
        output()
        super.fillSupportsItems()
    }

    companion object {
        val ON = "_on"
        val CHECKS = "_checks"
        val NOT_CHECKS = "_notChecks"
        val ACTIONS = "_actions"
        val OUTPUT = "_output"
    }
}


open class ExistBy(value: ExistBy.() -> Unit = {}) : ExistByB<ExistBy>(value) {

    companion object {
        val EMPTY = ExistBy { name(ItemEmpty.name()) }.apply<ExistBy> { init() }
    }
}

open class ExistByB<B : ExistByB<B>>(value: B.() -> Unit = {}) : DataTypeOperationB<B>(value), ExistByIB<B> {
}


open class ExternalModule(value: ExternalModule.() -> Unit = {}) : ExternalModuleB<ExternalModule>(value) {

    companion object {
        val EMPTY = ExternalModule { name(ItemEmpty.name()) }.apply<ExternalModule> { init() }
    }
}

open class ExternalModuleB<B : ExternalModuleB<B>>(value: B.() -> Unit = {}) : ModuleB<B>(value), ExternalModuleIB<B> {

    override fun externalTypes(): ListMultiHolder<ExternalTypeIB<*>> = itemAsList(EXTERNAL_TYPES, ExternalTypeIB::class.java, true)
    override fun externalTypes(vararg value: ExternalTypeIB<*>): B = apply { externalTypes().addItems(value.asList()) }

    override fun fillSupportsItems() {
        externalTypes()
        super.fillSupportsItems()
    }

    companion object {
        val EXTERNAL_TYPES = "_externalTypes"
    }
}


open class Facet(value: Facet.() -> Unit = {}) : FacetB<Facet>(value) {

    companion object {
        val EMPTY = Facet { name(ItemEmpty.name()) }.apply<Facet> { init() }
    }
}

open class FacetB<B : FacetB<B>>(value: B.() -> Unit = {}) : ModuleGroupB<B>(value), FacetIB<B> {
}


open class FindBy(value: FindBy.() -> Unit = {}) : FindByB<FindBy>(value) {

    companion object {
        val EMPTY = FindBy { name(ItemEmpty.name()) }.apply<FindBy> { init() }
    }
}

open class FindByB<B : FindByB<B>>(value: B.() -> Unit = {}) : DataTypeOperationB<B>(value), FindByIB<B> {

    override fun multiResult(): Boolean = attr(MULTI_RESULT, { true })
    override fun multiResult(value: Boolean): B = apply { attr(MULTI_RESULT, value) }

    companion object {
        val MULTI_RESULT = "_multiResult"
    }
}


open class Handler(value: Handler.() -> Unit = {}) : HandlerB<Handler>(value) {

    companion object {
        val EMPTY = Handler { name(ItemEmpty.name()) }.apply<Handler> { init() }
    }
}

open class HandlerB<B : HandlerB<B>>(value: B.() -> Unit = {}) : LogicUnitB<B>(value), HandlerIB<B> {

    override fun on(): EventIB<*> = attr(ON, { Event.EMPTY })
    override fun on(value: EventIB<*>): B = apply { attr(ON, value) }

    override fun checks(): ListMultiHolder<CheckIB<*>> = itemAsList(CHECKS, CheckIB::class.java, true)
    override fun checks(vararg value: CheckIB<*>): B = apply { checks().addItems(value.asList()) }
    override fun yes(value: CheckIB<*>): CheckIB<*> = applyAndReturn { checks().addItem(value); value }
    override fun yes(value: CheckIB<*>.() -> Unit): CheckIB<*> = yes(Check(value))

    override fun notChecks(): ListMultiHolder<CheckIB<*>> = itemAsList(NOT_CHECKS, CheckIB::class.java, true)
    override fun notChecks(vararg value: CheckIB<*>): B = apply { notChecks().addItems(value.asList()) }
    override fun no(value: CheckIB<*>): CheckIB<*> = applyAndReturn { notChecks().addItem(value); value }
    override fun no(value: CheckIB<*>.() -> Unit): CheckIB<*> = no(Check(value))

    override fun to(): StateIB<*> = attr(TO, { State.EMPTY })
    override fun to(value: StateIB<*>): B = apply { attr(TO, value) }

    override fun actions(): ListMultiHolder<ActionIB<*>> = itemAsList(ACTIONS, ActionIB::class.java, true)
    override fun actions(vararg value: ActionIB<*>): B = apply { actions().addItems(value.asList()) }
    override fun action(value: ActionIB<*>): ActionIB<*> = applyAndReturn { actions().addItem(value); value }
    override fun action(value: ActionIB<*>.() -> Unit): ActionIB<*> = action(Action(value))

    override fun output(): ListMultiHolder<CommandIB<*>> = itemAsList(OUTPUT, CommandIB::class.java, true)
    override fun output(vararg value: CommandIB<*>): B = apply { output().addItems(value.asList()) }
    override fun produce(value: CommandIB<*>): CommandIB<*> = applyAndReturn { output().addItem(value); value }
    override fun produce(value: CommandIB<*>.() -> Unit): CommandIB<*> = produce(Command(value))

    override fun fillSupportsItems() {
        checks()
        notChecks()
        actions()
        output()
        super.fillSupportsItems()
    }

    companion object {
        val ON = "_on"
        val CHECKS = "_checks"
        val NOT_CHECKS = "_notChecks"
        val TO = "_to"
        val ACTIONS = "_actions"
        val OUTPUT = "_output"
    }
}


open class Model(value: Model.() -> Unit = {}) : ModelB<Model>(value) {

    companion object {
        val EMPTY = Model { name(ItemEmpty.name()) }.apply<Model> { init() }
    }
}

open class ModelB<B : ModelB<B>>(value: B.() -> Unit = {}) : StructureUnitB<B>(value), ModelIB<B> {

    override fun models(): ListMultiHolder<ModelIB<*>> = itemAsList(MODELS, ModelIB::class.java, true)
    override fun models(vararg value: ModelIB<*>): B = apply { models().addItems(value.asList()) }

    override fun comps(): ListMultiHolder<CompIB<*>> = itemAsList(COMPS, CompIB::class.java, true)
    override fun comps(vararg value: CompIB<*>): B = apply { comps().addItems(value.asList()) }

    override fun fillSupportsItems() {
        models()
        comps()
        super.fillSupportsItems()
    }

    companion object {
        val MODELS = "_models"
        val COMPS = "_comps"
    }
}


open class Module(value: Module.() -> Unit = {}) : ModuleB<Module>(value) {

    companion object {
        val EMPTY = Module { name(ItemEmpty.name()) }.apply<Module> { init() }
    }
}

open class ModuleB<B : ModuleB<B>>(value: B.() -> Unit = {}) : StructureUnitB<B>(value), ModuleIB<B> {

    override fun parentNamespace(): Boolean = attr(PARENT_NAMESPACE, { false })
    override fun parentNamespace(value: Boolean): B = apply { attr(PARENT_NAMESPACE, value) }

    override fun dependencies(): ListMultiHolder<ModuleIB<*>> = itemAsList(DEPENDENCIES, ModuleIB::class.java, true)
    override fun dependencies(vararg value: ModuleIB<*>): B = apply { dependencies().addItems(value.asList()) }

    override fun entities(): ListMultiHolder<EntityIB<*>> = itemAsList(ENTITIES, EntityIB::class.java, true)
    override fun entities(vararg value: EntityIB<*>): B = apply { entities().addItems(value.asList()) }
    override fun entity(value: EntityIB<*>): EntityIB<*> = applyAndReturn { entities().addItem(value); value }
    override fun entity(value: EntityIB<*>.() -> Unit): EntityIB<*> = entity(Entity(value))

    override fun enums(): ListMultiHolder<EnumTypeIB<*>> = itemAsList(ENUMS, EnumTypeIB::class.java, true)
    override fun enums(vararg value: EnumTypeIB<*>): B = apply { enums().addItems(value.asList()) }
    override fun enumType(value: EnumTypeIB<*>): EnumTypeIB<*> = applyAndReturn { enums().addItem(value); value }
    override fun enumType(value: EnumTypeIB<*>.() -> Unit): EnumTypeIB<*> = enumType(EnumType(value))

    override fun values(): ListMultiHolder<ValuesIB<*>> = itemAsList(VALUES, ValuesIB::class.java, true)
    override fun values(vararg value: ValuesIB<*>): B = apply { values().addItems(value.asList()) }
    override fun valueType(value: ValuesIB<*>): ValuesIB<*> = applyAndReturn { values().addItem(value); value }
    override fun valueType(value: ValuesIB<*>.() -> Unit): ValuesIB<*> = valueType(Values(value))

    override fun basics(): ListMultiHolder<BasicIB<*>> = itemAsList(BASICS, BasicIB::class.java, true)
    override fun basics(vararg value: BasicIB<*>): B = apply { basics().addItems(value.asList()) }
    override fun basic(value: BasicIB<*>): BasicIB<*> = applyAndReturn { basics().addItem(value); value }
    override fun basic(value: BasicIB<*>.() -> Unit): BasicIB<*> = basic(Basic(value))

    override fun controllers(): ListMultiHolder<ControllerIB<*>> = itemAsList(CONTROLLERS, ControllerIB::class.java, true)
    override fun controllers(vararg value: ControllerIB<*>): B = apply { controllers().addItems(value.asList()) }
    override fun controller(value: ControllerIB<*>): ControllerIB<*> = applyAndReturn { controllers().addItem(value); value }
    override fun controller(value: ControllerIB<*>.() -> Unit): ControllerIB<*> = controller(Controller(value))

    override fun processManagers(): ListMultiHolder<ProcessManagerIB<*>> = itemAsList(PROCESS_MANAGERS, ProcessManagerIB::class.java, true)
    override fun processManagers(vararg value: ProcessManagerIB<*>): B = apply { processManagers().addItems(value.asList()) }
    override fun processManager(value: ProcessManagerIB<*>): ProcessManagerIB<*> = applyAndReturn { processManagers().addItem(value); value }
    override fun processManager(value: ProcessManagerIB<*>.() -> Unit): ProcessManagerIB<*> = processManager(ProcessManager(value))

    override fun projectors(): ListMultiHolder<ProjectorIB<*>> = itemAsList(PROJECTORS, ProjectorIB::class.java, true)
    override fun projectors(vararg value: ProjectorIB<*>): B = apply { projectors().addItems(value.asList()) }
    override fun projector(value: ProjectorIB<*>): ProjectorIB<*> = applyAndReturn { projectors().addItem(value); value }
    override fun projector(value: ProjectorIB<*>.() -> Unit): ProjectorIB<*> = projector(Projector(value))

    override fun fillSupportsItems() {
        dependencies()
        entities()
        enums()
        values()
        basics()
        controllers()
        processManagers()
        projectors()
        super.fillSupportsItems()
    }

    companion object {
        val PARENT_NAMESPACE = "_parentNamespace"
        val DEPENDENCIES = "_dependencies"
        val ENTITIES = "_entities"
        val ENUMS = "_enums"
        val VALUES = "_values"
        val BASICS = "_basics"
        val CONTROLLERS = "_controllers"
        val PROCESS_MANAGERS = "_processManagers"
        val PROJECTORS = "_projectors"
    }
}


open class ModuleGroup(value: ModuleGroup.() -> Unit = {}) : ModuleGroupB<ModuleGroup>(value) {

    companion object {
        val EMPTY = ModuleGroup { name(ItemEmpty.name()) }.apply<ModuleGroup> { init() }
    }
}

open class ModuleGroupB<B : ModuleGroupB<B>>(value: B.() -> Unit = {}) : StructureUnitB<B>(value), ModuleGroupIB<B> {

    override fun modules(): ListMultiHolder<ModuleIB<*>> = itemAsList(MODULES, ModuleIB::class.java, true)
    override fun modules(vararg value: ModuleIB<*>): B = apply { modules().addItems(value.asList()) }

    override fun fillSupportsItems() {
        modules()
        super.fillSupportsItems()
    }

    companion object {
        val MODULES = "_modules"
    }
}


open class ProcessManager(value: ProcessManager.() -> Unit = {}) : ProcessManagerB<ProcessManager>(value) {

    companion object {
        val EMPTY = ProcessManager { name(ItemEmpty.name()) }.apply<ProcessManager> { init() }
    }
}

open class ProcessManagerB<B : ProcessManagerB<B>>(value: B.() -> Unit = {}) : StateMachineB<B>(value), ProcessManagerIB<B> {
}


open class Projector(value: Projector.() -> Unit = {}) : ProjectorB<Projector>(value) {

    companion object {
        val EMPTY = Projector { name(ItemEmpty.name()) }.apply<Projector> { init() }
    }
}

open class ProjectorB<B : ProjectorB<B>>(value: B.() -> Unit = {}) : StateMachineB<B>(value), ProjectorIB<B> {
}


open class State(value: State.() -> Unit = {}) : StateB<State>(value) {

    companion object {
        val EMPTY = State { name(ItemEmpty.name()) }.apply<State> { init() }
    }
}

open class StateB<B : StateB<B>>(value: B.() -> Unit = {}) : ControllerB<B>(value), StateIB<B> {

    override fun timeout(): Long = attr(TIMEOUT, { 0L })
    override fun timeout(value: Long): B = apply { attr(TIMEOUT, value) }

    override fun entryActions(): ListMultiHolder<ActionIB<*>> = itemAsList(ENTRY_ACTIONS, ActionIB::class.java, true)
    override fun entryActions(vararg value: ActionIB<*>): B = apply { entryActions().addItems(value.asList()) }
    override fun entry(value: ActionIB<*>): ActionIB<*> = applyAndReturn { entryActions().addItem(value); value }
    override fun entry(value: ActionIB<*>.() -> Unit): ActionIB<*> = entry(Action(value))

    override fun exitActions(): ListMultiHolder<ActionIB<*>> = itemAsList(EXIT_ACTIONS, ActionIB::class.java, true)
    override fun exitActions(vararg value: ActionIB<*>): B = apply { exitActions().addItems(value.asList()) }
    override fun exit(value: ActionIB<*>): ActionIB<*> = applyAndReturn { exitActions().addItem(value); value }
    override fun exit(value: ActionIB<*>.() -> Unit): ActionIB<*> = exit(Action(value))

    override fun executors(): ListMultiHolder<ExecutorIB<*>> = itemAsList(EXECUTORS, ExecutorIB::class.java, true)
    override fun executors(vararg value: ExecutorIB<*>): B = apply { executors().addItems(value.asList()) }
    override fun execute(value: ExecutorIB<*>): ExecutorIB<*> = applyAndReturn { executors().addItem(value); value }
    override fun execute(value: ExecutorIB<*>.() -> Unit): ExecutorIB<*> = execute(Executor(value))

    override fun handlers(): ListMultiHolder<HandlerIB<*>> = itemAsList(HANDLERS, HandlerIB::class.java, true)
    override fun handlers(vararg value: HandlerIB<*>): B = apply { handlers().addItems(value.asList()) }
    override fun handle(value: HandlerIB<*>): HandlerIB<*> = applyAndReturn { handlers().addItem(value); value }
    override fun handle(value: HandlerIB<*>.() -> Unit): HandlerIB<*> = handle(Handler(value))

    override fun fillSupportsItems() {
        entryActions()
        exitActions()
        executors()
        handlers()
        super.fillSupportsItems()
    }

    companion object {
        val TIMEOUT = "_timeout"
        val ENTRY_ACTIONS = "_entryActions"
        val EXIT_ACTIONS = "_exitActions"
        val EXECUTORS = "_executors"
        val HANDLERS = "_handlers"
    }
}


open class StateMachine(value: StateMachine.() -> Unit = {}) : StateMachineB<StateMachine>(value) {

    companion object {
        val EMPTY = StateMachine { name(ItemEmpty.name()) }.apply<StateMachine> { init() }
    }
}

open class StateMachineB<B : StateMachineB<B>>(value: B.() -> Unit = {}) : ControllerB<B>(value), StateMachineIB<B> {

    override fun stateProp(): AttributeIB<*> = attr(STATE_PROP, { Attribute.EMPTY })
    override fun stateProp(value: AttributeIB<*>): B = apply { attr(STATE_PROP, value) }

    override fun timeoutProp(): AttributeIB<*> = attr(TIMEOUT_PROP, { Attribute.EMPTY })
    override fun timeoutProp(value: AttributeIB<*>): B = apply { attr(TIMEOUT_PROP, value) }

    override fun timeout(): Long = attr(TIMEOUT, { 0L })
    override fun timeout(value: Long): B = apply { attr(TIMEOUT, value) }

    override fun states(): ListMultiHolder<StateIB<*>> = itemAsList(STATES, StateIB::class.java, true)
    override fun states(vararg value: StateIB<*>): B = apply { states().addItems(value.asList()) }
    override fun state(value: StateIB<*>): StateIB<*> = applyAndReturn { states().addItem(value); value }
    override fun state(value: StateIB<*>.() -> Unit): StateIB<*> = state(State(value))

    override fun checks(): ListMultiHolder<CheckIB<*>> = itemAsList(CHECKS, CheckIB::class.java, true)
    override fun checks(vararg value: CheckIB<*>): B = apply { checks().addItems(value.asList()) }
    override fun check(value: CheckIB<*>): CheckIB<*> = applyAndReturn { checks().addItem(value); value }
    override fun check(value: CheckIB<*>.() -> Unit): CheckIB<*> = check(Check(value))

    override fun fillSupportsItems() {
        states()
        checks()
        super.fillSupportsItems()
    }

    companion object {
        val STATE_PROP = "_stateProp"
        val TIMEOUT_PROP = "_timeoutProp"
        val TIMEOUT = "_timeout"
        val STATES = "_states"
        val CHECKS = "_checks"
    }
}


open class UpdateBy(value: UpdateBy.() -> Unit = {}) : UpdateByB<UpdateBy>(value) {

    companion object {
        val EMPTY = UpdateBy { name(ItemEmpty.name()) }.apply<UpdateBy> { init() }
    }
}

open class UpdateByB<B : UpdateByB<B>>(value: B.() -> Unit = {}) : CommandB<B>(value), UpdateByIB<B> {
}


open class Updated(value: Updated.() -> Unit = {}) : UpdatedB<Updated>(value) {

    companion object {
        val EMPTY = Updated { name(ItemEmpty.name()) }.apply<Updated> { init() }
    }
}

open class UpdatedB<B : UpdatedB<B>>(value: B.() -> Unit = {}) : EventB<B>(value), UpdatedIB<B> {
}


open class Values(value: Values.() -> Unit = {}) : ValuesB<Values>(value) {

    companion object {
        val EMPTY = Values { name(ItemEmpty.name()) }.apply<Values> { init() }
    }
}

open class ValuesB<B : ValuesB<B>>(value: B.() -> Unit = {}) : DataTypeB<B>(value), ValuesIB<B> {
}


open class Widget(value: Widget.() -> Unit = {}) : WidgetB<Widget>(value) {

    companion object {
        val EMPTY = Widget { name(ItemEmpty.name()) }.apply<Widget> { init() }
    }
}

open class WidgetB<B : WidgetB<B>>(value: B.() -> Unit = {}) : CompilationUnitB<B>(value), WidgetIB<B> {
}


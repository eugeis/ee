package ee.design

import ee.lang.*


open class AggregateHandler(value: AggregateHandler.() -> Unit = {}) : AggregateHandlerB<AggregateHandler>(value) {

    companion object {
        val EMPTY = AggregateHandler { name(ItemEmpty.name()) }.apply<AggregateHandler> { init() }
    }
}

open class AggregateHandlerB<B : AggregateHandlerB<B>>(value: B.() -> Unit = {}) : StateMachineB<B>(value),
                                                                                   AggregateHandlerI<B> {}


open class Basic(value: Basic.() -> Unit = {}) : BasicB<Basic>(value) {

    companion object {
        val EMPTY = Basic { name(ItemEmpty.name()) }.apply<Basic> { init() }
    }
}

open class BasicB<B : BasicB<B>>(value: B.() -> Unit = {}) : DataTypeB<B>(value), BasicI<B> {}


open class Bundle(value: Bundle.() -> Unit = {}) : BundleB<Bundle>(value) {

    companion object {
        val EMPTY = Bundle { name(ItemEmpty.name()) }.apply<Bundle> { init() }
    }
}

open class BundleB<B : BundleB<B>>(value: B.() -> Unit = {}) : StructureUnitB<B>(value), BundleI<B> {

    override fun units(): ListMultiHolder<StructureUnitI<*>> = itemAsList(UNITS, StructureUnitI::class.java, true)
    override fun units(vararg value: StructureUnitI<*>): B = apply { units().addItems(value.asList()) }

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

open class BusinessCommandB<B : BusinessCommandB<B>>(value: B.() -> Unit = {}) : CommandB<B>(value),
                                                                                 BusinessCommandI<B> {}


open class BusinessController(value: BusinessController.() -> Unit = {}) :
        BusinessControllerB<BusinessController>(value) {

    companion object {
        val EMPTY = BusinessController { name(ItemEmpty.name()) }.apply<BusinessController> { init() }
    }
}

open class BusinessControllerB<B : BusinessControllerB<B>>(value: B.() -> Unit = {}) : ControllerB<B>(value),
                                                                                       BusinessControllerI<B> {}


open class BusinessEvent(value: BusinessEvent.() -> Unit = {}) : BusinessEventB<BusinessEvent>(value) {

    companion object {
        val EMPTY = BusinessEvent { name(ItemEmpty.name()) }.apply<BusinessEvent> { init() }
    }
}

open class BusinessEventB<B : BusinessEventB<B>>(value: B.() -> Unit = {}) : EventB<B>(value), BusinessEventI<B> {}


open class Check(value: Check.() -> Unit = {}) : CheckB<Check>(value) {

    companion object {
        val EMPTY = Check { name(ItemEmpty.name()) }.apply<Check> { init() }
    }
}

open class CheckB<B : CheckB<B>>(value: B.() -> Unit = {}) : LogicUnitB<B>(value), CheckI<B> {

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

open class CommandB<B : CommandB<B>>(value: B.() -> Unit = {}) : CompilationUnitB<B>(value), CommandI<B> {

    override fun affectMulti(): Boolean = attr(AFFECT_MULTI, { false })
    override fun affectMulti(value: Boolean): B = apply { attr(AFFECT_MULTI, value) }

    override fun event(): EventI<*> = attr(EVENT, { Event.EMPTY })
    override fun event(value: EventI<*>): B = apply { attr(EVENT, value) }

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

open class CompB<B : CompB<B>>(value: B.() -> Unit = {}) : ModuleGroupB<B>(value), CompI<B> {

    override fun moduleGroups(): ListMultiHolder<ModuleGroupI<*>> =
        itemAsList(MODULE_GROUPS, ModuleGroupI::class.java, true)

    override fun moduleGroups(vararg value: ModuleGroupI<*>): B = apply { moduleGroups().addItems(value.asList()) }

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

open class CompositeCommandB<B : CompositeCommandB<B>>(value: B.() -> Unit = {}) : CompilationUnitB<B>(value),
                                                                                   CompositeCommandI<B> {

    override fun commands(): ListMultiHolder<CommandI<*>> = itemAsList(COMMANDS, CommandI::class.java, true)
    override fun commands(vararg value: CommandI<*>): B = apply { commands().addItems(value.asList()) }

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

open class ControllerB<B : ControllerB<B>>(value: B.() -> Unit = {}) : CompilationUnitB<B>(value), ControllerI<B> {

    override fun enums(): ListMultiHolder<EnumTypeI<*>> = itemAsList(ENUMS, EnumTypeI::class.java, true)
    override fun enums(vararg value: EnumTypeI<*>): B = apply { enums().addItems(value.asList()) }
    override fun enumType(value: EnumTypeI<*>): EnumTypeI<*> = applyAndReturn { enums().addItem(value); value }
    override fun enumType(value: EnumTypeI<*>.() -> Unit): EnumTypeI<*> = enumType(EnumType(value))

    override fun values(): ListMultiHolder<ValuesI<*>> = itemAsList(VALUES, ValuesI::class.java, true)
    override fun values(vararg value: ValuesI<*>): B = apply { values().addItems(value.asList()) }
    override fun valueType(value: ValuesI<*>): ValuesI<*> = applyAndReturn { values().addItem(value); value }
    override fun valueType(value: ValuesI<*>.() -> Unit): ValuesI<*> = valueType(Values(value))

    override fun basics(): ListMultiHolder<BasicI<*>> = itemAsList(BASICS, BasicI::class.java, true)
    override fun basics(vararg value: BasicI<*>): B = apply { basics().addItems(value.asList()) }
    override fun basic(value: BasicI<*>): BasicI<*> = applyAndReturn { basics().addItem(value); value }
    override fun basic(value: BasicI<*>.() -> Unit): BasicI<*> = basic(Basic(value))

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

open class CountByB<B : CountByB<B>>(value: B.() -> Unit = {}) : DataTypeOperationB<B>(value), CountByI<B> {}


open class CreateBy(value: CreateBy.() -> Unit = {}) : CreateByB<CreateBy>(value) {

    companion object {
        val EMPTY = CreateBy { name(ItemEmpty.name()) }.apply<CreateBy> { init() }
    }
}

open class CreateByB<B : CreateByB<B>>(value: B.() -> Unit = {}) : CommandB<B>(value), CreateByI<B> {}


open class Created(value: Created.() -> Unit = {}) : CreatedB<Created>(value) {

    companion object {
        val EMPTY = Created { name(ItemEmpty.name()) }.apply<Created> { init() }
    }
}

open class CreatedB<B : CreatedB<B>>(value: B.() -> Unit = {}) : EventB<B>(value), CreatedI<B> {}


open class DeleteBy(value: DeleteBy.() -> Unit = {}) : DeleteByB<DeleteBy>(value) {

    companion object {
        val EMPTY = DeleteBy { name(ItemEmpty.name()) }.apply<DeleteBy> { init() }
    }
}

open class DeleteByB<B : DeleteByB<B>>(value: B.() -> Unit = {}) : CommandB<B>(value), DeleteByI<B> {}


open class Deleted(value: Deleted.() -> Unit = {}) : DeletedB<Deleted>(value) {

    companion object {
        val EMPTY = Deleted { name(ItemEmpty.name()) }.apply<Deleted> { init() }
    }
}

open class DeletedB<B : DeletedB<B>>(value: B.() -> Unit = {}) : EventB<B>(value), DeletedI<B> {}


open class DynamicState(value: DynamicState.() -> Unit = {}) : DynamicStateB<DynamicState>(value) {

    companion object {
        val EMPTY = DynamicState { name(ItemEmpty.name()) }.apply<DynamicState> { init() }
    }
}

open class DynamicStateB<B : DynamicStateB<B>>(value: B.() -> Unit = {}) : StateB<B>(value), DynamicStateI<B> {

    override fun checks(): ListMultiHolder<CheckI<*>> = itemAsList(CHECKS, CheckI::class.java, true)
    override fun checks(vararg value: CheckI<*>): B = apply { checks().addItems(value.asList()) }
    override fun yes(value: CheckI<*>): CheckI<*> = applyAndReturn { checks().addItem(value); value }
    override fun yes(value: CheckI<*>.() -> Unit): CheckI<*> = yes(Check(value))

    override fun notChecks(): ListMultiHolder<CheckI<*>> = itemAsList(NOT_CHECKS, CheckI::class.java, true)
    override fun notChecks(vararg value: CheckI<*>): B = apply { notChecks().addItems(value.asList()) }
    override fun no(value: CheckI<*>): CheckI<*> = applyAndReturn { notChecks().addItem(value); value }
    override fun no(value: CheckI<*>.() -> Unit): CheckI<*> = no(Check(value))

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

open class EntityB<B : EntityB<B>>(value: B.() -> Unit = {}) : DataTypeB<B>(value), EntityI<B> {

    override fun defaultEvents(): Boolean = attr(DEFAULT_EVENTS, { true })
    override fun defaultEvents(value: Boolean): B = apply { attr(DEFAULT_EVENTS, value) }

    override fun defaultQueries(): Boolean = attr(DEFAULT_QUERIES, { true })
    override fun defaultQueries(value: Boolean): B = apply { attr(DEFAULT_QUERIES, value) }

    override fun defaultCommands(): Boolean = attr(DEFAULT_COMMANDS, { true })
    override fun defaultCommands(value: Boolean): B = apply { attr(DEFAULT_COMMANDS, value) }

    override fun belongsToAggregate(): EntityI<*> = attr(BELONGS_TO_AGGREGATE, { Entity.EMPTY })
    override fun belongsToAggregate(value: EntityI<*>): B = apply { attr(BELONGS_TO_AGGREGATE, value) }

    override fun aggregateFor(): ListMultiHolder<EntityI<*>> = itemAsList(AGGREGATE_FOR, EntityI::class.java, true)
    override fun aggregateFor(vararg value: EntityI<*>): B = apply { aggregateFor().addItems(value.asList()) }

    override fun controllers(): ListMultiHolder<BusinessControllerI<*>> =
        itemAsList(CONTROLLERS, BusinessControllerI::class.java, true)

    override fun controllers(vararg value: BusinessControllerI<*>): B = apply { controllers().addItems(value.asList()) }
    override fun controller(value: BusinessControllerI<*>): BusinessControllerI<*> =
        applyAndReturn { controllers().addItem(value); value }

    override fun controller(value: BusinessControllerI<*>.() -> Unit): BusinessControllerI<*> =
        controller(BusinessController(value))

    override fun findBys(): ListMultiHolder<FindByI<*>> = itemAsList(FIND_BYS, FindByI::class.java, true)
    override fun findBys(vararg value: FindByI<*>): B = apply { findBys().addItems(value.asList()) }
    override fun findBy(value: FindByI<*>): FindByI<*> = applyAndReturn { findBys().addItem(value); value }
    override fun findBy(value: FindByI<*>.() -> Unit): FindByI<*> = findBy(FindBy(value))

    override fun countBys(): ListMultiHolder<CountByI<*>> = itemAsList(COUNT_BYS, CountByI::class.java, true)
    override fun countBys(vararg value: CountByI<*>): B = apply { countBys().addItems(value.asList()) }
    override fun countBy(value: CountByI<*>): CountByI<*> = applyAndReturn { countBys().addItem(value); value }
    override fun countBy(value: CountByI<*>.() -> Unit): CountByI<*> = countBy(CountBy(value))

    override fun existBys(): ListMultiHolder<ExistByI<*>> = itemAsList(EXIST_BYS, ExistByI::class.java, true)
    override fun existBys(vararg value: ExistByI<*>): B = apply { existBys().addItems(value.asList()) }
    override fun existBy(value: ExistByI<*>): ExistByI<*> = applyAndReturn { existBys().addItem(value); value }
    override fun existBy(value: ExistByI<*>.() -> Unit): ExistByI<*> = existBy(ExistBy(value))

    override fun commands(): ListMultiHolder<BusinessCommandI<*>> =
        itemAsList(COMMANDS, BusinessCommandI::class.java, true)

    override fun commands(vararg value: BusinessCommandI<*>): B = apply { commands().addItems(value.asList()) }
    override fun command(value: BusinessCommandI<*>): BusinessCommandI<*> =
        applyAndReturn { commands().addItem(value); value }

    override fun command(value: BusinessCommandI<*>.() -> Unit): BusinessCommandI<*> = command(BusinessCommand(value))

    override fun composites(): ListMultiHolder<CompositeCommandI<*>> =
        itemAsList(COMPOSITES, CompositeCommandI::class.java, true)

    override fun composites(vararg value: CompositeCommandI<*>): B = apply { composites().addItems(value.asList()) }
    override fun composite(value: CompositeCommandI<*>): CompositeCommandI<*> =
        applyAndReturn { composites().addItem(value); value }

    override fun composite(value: CompositeCommandI<*>.() -> Unit): CompositeCommandI<*> =
        composite(CompositeCommand(value))

    override fun createBys(): ListMultiHolder<CreateByI<*>> = itemAsList(CREATE_BYS, CreateByI::class.java, true)
    override fun createBys(vararg value: CreateByI<*>): B = apply { createBys().addItems(value.asList()) }
    override fun createBy(value: CreateByI<*>): CreateByI<*> = applyAndReturn { createBys().addItem(value); value }
    override fun createBy(value: CreateByI<*>.() -> Unit): CreateByI<*> = createBy(CreateBy(value))

    override fun updateBys(): ListMultiHolder<UpdateByI<*>> = itemAsList(UPDATE_BYS, UpdateByI::class.java, true)
    override fun updateBys(vararg value: UpdateByI<*>): B = apply { updateBys().addItems(value.asList()) }
    override fun updateBy(value: UpdateByI<*>): UpdateByI<*> = applyAndReturn { updateBys().addItem(value); value }
    override fun updateBy(value: UpdateByI<*>.() -> Unit): UpdateByI<*> = updateBy(UpdateBy(value))

    override fun deleteBys(): ListMultiHolder<DeleteByI<*>> = itemAsList(DELETE_BYS, DeleteByI::class.java, true)
    override fun deleteBys(vararg value: DeleteByI<*>): B = apply { deleteBys().addItems(value.asList()) }
    override fun deleteBy(value: DeleteByI<*>): DeleteByI<*> = applyAndReturn { deleteBys().addItem(value); value }
    override fun deleteBy(value: DeleteByI<*>.() -> Unit): DeleteByI<*> = deleteBy(DeleteBy(value))

    override fun events(): ListMultiHolder<BusinessEventI<*>> = itemAsList(EVENTS, BusinessEventI::class.java, true)
    override fun events(vararg value: BusinessEventI<*>): B = apply { events().addItems(value.asList()) }
    override fun event(value: BusinessEventI<*>): BusinessEventI<*> = applyAndReturn { events().addItem(value); value }
    override fun event(value: BusinessEventI<*>.() -> Unit): BusinessEventI<*> = event(BusinessEvent(value))

    override fun created(): ListMultiHolder<CreatedI<*>> = itemAsList(CREATED, CreatedI::class.java, true)
    override fun created(vararg value: CreatedI<*>): B = apply { created().addItems(value.asList()) }
    override fun created(value: CreatedI<*>): CreatedI<*> = applyAndReturn { created().addItem(value); value }
    override fun created(value: CreatedI<*>.() -> Unit): CreatedI<*> = created(Created(value))

    override fun updated(): ListMultiHolder<UpdatedI<*>> = itemAsList(UPDATED, UpdatedI::class.java, true)
    override fun updated(vararg value: UpdatedI<*>): B = apply { updated().addItems(value.asList()) }
    override fun updated(value: UpdatedI<*>): UpdatedI<*> = applyAndReturn { updated().addItem(value); value }
    override fun updated(value: UpdatedI<*>.() -> Unit): UpdatedI<*> = updated(Updated(value))

    override fun deleted(): ListMultiHolder<DeletedI<*>> = itemAsList(DELETED, DeletedI::class.java, true)
    override fun deleted(vararg value: DeletedI<*>): B = apply { deleted().addItems(value.asList()) }
    override fun deleted(value: DeletedI<*>): DeletedI<*> = applyAndReturn { deleted().addItem(value); value }
    override fun deleted(value: DeletedI<*>.() -> Unit): DeletedI<*> = deleted(Deleted(value))

    override fun handlers(): ListMultiHolder<AggregateHandlerI<*>> =
        itemAsList(HANDLERS, AggregateHandlerI::class.java, true)

    override fun handlers(vararg value: AggregateHandlerI<*>): B = apply { handlers().addItems(value.asList()) }
    override fun handler(value: AggregateHandlerI<*>): AggregateHandlerI<*> =
        applyAndReturn { handlers().addItem(value); value }

    override fun handler(value: AggregateHandlerI<*>.() -> Unit): AggregateHandlerI<*> =
        handler(AggregateHandler(value))

    override fun projectors(): ListMultiHolder<ProjectorI<*>> = itemAsList(PROJECTORS, ProjectorI::class.java, true)
    override fun projectors(vararg value: ProjectorI<*>): B = apply { projectors().addItems(value.asList()) }
    override fun projector(value: ProjectorI<*>): ProjectorI<*> = applyAndReturn { projectors().addItem(value); value }
    override fun projector(value: ProjectorI<*>.() -> Unit): ProjectorI<*> = projector(Projector(value))

    override fun processManager(): ListMultiHolder<ProcessManagerI<*>> =
        itemAsList(PROCESS_MANAGER, ProcessManagerI::class.java, true)

    override fun processManager(vararg value: ProcessManagerI<*>): B =
        apply { processManager().addItems(value.asList()) }

    override fun processManager(value: ProcessManagerI<*>): ProcessManagerI<*> =
        applyAndReturn { processManager().addItem(value); value }

    override fun processManager(value: ProcessManagerI<*>.() -> Unit): ProcessManagerI<*> =
        processManager(ProcessManager(value))

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

open class EventB<B : EventB<B>>(value: B.() -> Unit = {}) : CompilationUnitB<B>(value), EventI<B> {}


open class Executor(value: Executor.() -> Unit = {}) : ExecutorB<Executor>(value) {

    companion object {
        val EMPTY = Executor { name(ItemEmpty.name()) }.apply<Executor> { init() }
    }
}

open class ExecutorB<B : ExecutorB<B>>(value: B.() -> Unit = {}) : LogicUnitB<B>(value), ExecutorI<B> {

    override fun on(): CommandI<*> = attr(ON, { Command.EMPTY })
    override fun on(value: CommandI<*>): B = apply { attr(ON, value) }

    override fun checks(): ListMultiHolder<CheckI<*>> = itemAsList(CHECKS, CheckI::class.java, true)
    override fun checks(vararg value: CheckI<*>): B = apply { checks().addItems(value.asList()) }
    override fun yes(value: CheckI<*>): CheckI<*> = applyAndReturn { checks().addItem(value); value }
    override fun yes(value: CheckI<*>.() -> Unit): CheckI<*> = yes(Check(value))

    override fun notChecks(): ListMultiHolder<CheckI<*>> = itemAsList(NOT_CHECKS, CheckI::class.java, true)
    override fun notChecks(vararg value: CheckI<*>): B = apply { notChecks().addItems(value.asList()) }
    override fun no(value: CheckI<*>): CheckI<*> = applyAndReturn { notChecks().addItem(value); value }
    override fun no(value: CheckI<*>.() -> Unit): CheckI<*> = no(Check(value))

    override fun actions(): ListMultiHolder<ActionI<*>> = itemAsList(ACTIONS, ActionI::class.java, true)
    override fun actions(vararg value: ActionI<*>): B = apply { actions().addItems(value.asList()) }
    override fun action(value: ActionI<*>): ActionI<*> = applyAndReturn { actions().addItem(value); value }
    override fun action(value: ActionI<*>.() -> Unit): ActionI<*> = action(Action(value))

    override fun output(): ListMultiHolder<EventI<*>> = itemAsList(OUTPUT, EventI::class.java, true)
    override fun output(vararg value: EventI<*>): B = apply { output().addItems(value.asList()) }
    override fun produce(value: EventI<*>): EventI<*> = applyAndReturn { output().addItem(value); value }
    override fun produce(value: EventI<*>.() -> Unit): EventI<*> = produce(Event(value))

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

open class ExistByB<B : ExistByB<B>>(value: B.() -> Unit = {}) : DataTypeOperationB<B>(value), ExistByI<B> {}


open class ExternalModule(value: ExternalModule.() -> Unit = {}) : ExternalModuleB<ExternalModule>(value) {

    companion object {
        val EMPTY = ExternalModule { name(ItemEmpty.name()) }.apply<ExternalModule> { init() }
    }
}

open class ExternalModuleB<B : ExternalModuleB<B>>(value: B.() -> Unit = {}) : ModuleB<B>(value), ExternalModuleI<B> {

    override fun externalTypes(): ListMultiHolder<ExternalTypeI<*>> =
        itemAsList(EXTERNAL_TYPES, ExternalTypeI::class.java, true)

    override fun externalTypes(vararg value: ExternalTypeI<*>): B = apply { externalTypes().addItems(value.asList()) }

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

open class FacetB<B : FacetB<B>>(value: B.() -> Unit = {}) : ModuleGroupB<B>(value), FacetI<B> {}


open class FindBy(value: FindBy.() -> Unit = {}) : FindByB<FindBy>(value) {

    companion object {
        val EMPTY = FindBy { name(ItemEmpty.name()) }.apply<FindBy> { init() }
    }
}

open class FindByB<B : FindByB<B>>(value: B.() -> Unit = {}) : DataTypeOperationB<B>(value), FindByI<B> {

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

open class HandlerB<B : HandlerB<B>>(value: B.() -> Unit = {}) : LogicUnitB<B>(value), HandlerI<B> {

    override fun on(): EventI<*> = attr(ON, { Event.EMPTY })
    override fun on(value: EventI<*>): B = apply { attr(ON, value) }

    override fun checks(): ListMultiHolder<CheckI<*>> = itemAsList(CHECKS, CheckI::class.java, true)
    override fun checks(vararg value: CheckI<*>): B = apply { checks().addItems(value.asList()) }
    override fun yes(value: CheckI<*>): CheckI<*> = applyAndReturn { checks().addItem(value); value }
    override fun yes(value: CheckI<*>.() -> Unit): CheckI<*> = yes(Check(value))

    override fun notChecks(): ListMultiHolder<CheckI<*>> = itemAsList(NOT_CHECKS, CheckI::class.java, true)
    override fun notChecks(vararg value: CheckI<*>): B = apply { notChecks().addItems(value.asList()) }
    override fun no(value: CheckI<*>): CheckI<*> = applyAndReturn { notChecks().addItem(value); value }
    override fun no(value: CheckI<*>.() -> Unit): CheckI<*> = no(Check(value))

    override fun to(): StateI<*> = attr(TO, { State.EMPTY })
    override fun to(value: StateI<*>): B = apply { attr(TO, value) }

    override fun actions(): ListMultiHolder<ActionI<*>> = itemAsList(ACTIONS, ActionI::class.java, true)
    override fun actions(vararg value: ActionI<*>): B = apply { actions().addItems(value.asList()) }
    override fun action(value: ActionI<*>): ActionI<*> = applyAndReturn { actions().addItem(value); value }
    override fun action(value: ActionI<*>.() -> Unit): ActionI<*> = action(Action(value))

    override fun output(): ListMultiHolder<CommandI<*>> = itemAsList(OUTPUT, CommandI::class.java, true)
    override fun output(vararg value: CommandI<*>): B = apply { output().addItems(value.asList()) }
    override fun produce(value: CommandI<*>): CommandI<*> = applyAndReturn { output().addItem(value); value }
    override fun produce(value: CommandI<*>.() -> Unit): CommandI<*> = produce(Command(value))

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

open class ModelB<B : ModelB<B>>(value: B.() -> Unit = {}) : StructureUnitB<B>(value), ModelI<B> {

    override fun models(): ListMultiHolder<ModelI<*>> = itemAsList(MODELS, ModelI::class.java, true)
    override fun models(vararg value: ModelI<*>): B = apply { models().addItems(value.asList()) }

    override fun comps(): ListMultiHolder<CompI<*>> = itemAsList(COMPS, CompI::class.java, true)
    override fun comps(vararg value: CompI<*>): B = apply { comps().addItems(value.asList()) }

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

open class ModuleB<B : ModuleB<B>>(value: B.() -> Unit = {}) : StructureUnitB<B>(value), ModuleI<B> {

    override fun parentNamespace(): Boolean = attr(PARENT_NAMESPACE, { false })
    override fun parentNamespace(value: Boolean): B = apply { attr(PARENT_NAMESPACE, value) }

    override fun dependencies(): ListMultiHolder<ModuleI<*>> = itemAsList(DEPENDENCIES, ModuleI::class.java, true)
    override fun dependencies(vararg value: ModuleI<*>): B = apply { dependencies().addItems(value.asList()) }

    override fun entities(): ListMultiHolder<EntityI<*>> = itemAsList(ENTITIES, EntityI::class.java, true)
    override fun entities(vararg value: EntityI<*>): B = apply { entities().addItems(value.asList()) }
    override fun entity(value: EntityI<*>): EntityI<*> = applyAndReturn { entities().addItem(value); value }
    override fun entity(value: EntityI<*>.() -> Unit): EntityI<*> = entity(Entity(value))

    override fun enums(): ListMultiHolder<EnumTypeI<*>> = itemAsList(ENUMS, EnumTypeI::class.java, true)
    override fun enums(vararg value: EnumTypeI<*>): B = apply { enums().addItems(value.asList()) }
    override fun enumType(value: EnumTypeI<*>): EnumTypeI<*> = applyAndReturn { enums().addItem(value); value }
    override fun enumType(value: EnumTypeI<*>.() -> Unit): EnumTypeI<*> = enumType(EnumType(value))

    override fun values(): ListMultiHolder<ValuesI<*>> = itemAsList(VALUES, ValuesI::class.java, true)
    override fun values(vararg value: ValuesI<*>): B = apply { values().addItems(value.asList()) }
    override fun valueType(value: ValuesI<*>): ValuesI<*> = applyAndReturn { values().addItem(value); value }
    override fun valueType(value: ValuesI<*>.() -> Unit): ValuesI<*> = valueType(Values(value))

    override fun basics(): ListMultiHolder<BasicI<*>> = itemAsList(BASICS, BasicI::class.java, true)
    override fun basics(vararg value: BasicI<*>): B = apply { basics().addItems(value.asList()) }
    override fun basic(value: BasicI<*>): BasicI<*> = applyAndReturn { basics().addItem(value); value }
    override fun basic(value: BasicI<*>.() -> Unit): BasicI<*> = basic(Basic(value))

    override fun controllers(): ListMultiHolder<BusinessControllerI<*>> =
        itemAsList(CONTROLLERS, BusinessControllerI::class.java, true)

    override fun controllers(vararg value: BusinessControllerI<*>): B = apply { controllers().addItems(value.asList()) }
    override fun controller(value: BusinessControllerI<*>): BusinessControllerI<*> =
        applyAndReturn { controllers().addItem(value); value }

    override fun controller(value: BusinessControllerI<*>.() -> Unit): BusinessControllerI<*> =
        controller(BusinessController(value))

    override fun processManagers(): ListMultiHolder<ProcessManagerI<*>> =
        itemAsList(PROCESS_MANAGERS, ProcessManagerI::class.java, true)

    override fun processManagers(vararg value: ProcessManagerI<*>): B =
        apply { processManagers().addItems(value.asList()) }

    override fun processManager(value: ProcessManagerI<*>): ProcessManagerI<*> =
        applyAndReturn { processManagers().addItem(value); value }

    override fun processManager(value: ProcessManagerI<*>.() -> Unit): ProcessManagerI<*> =
        processManager(ProcessManager(value))

    override fun projectors(): ListMultiHolder<ProjectorI<*>> = itemAsList(PROJECTORS, ProjectorI::class.java, true)
    override fun projectors(vararg value: ProjectorI<*>): B = apply { projectors().addItems(value.asList()) }
    override fun projector(value: ProjectorI<*>): ProjectorI<*> = applyAndReturn { projectors().addItem(value); value }
    override fun projector(value: ProjectorI<*>.() -> Unit): ProjectorI<*> = projector(Projector(value))

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

open class ModuleGroupB<B : ModuleGroupB<B>>(value: B.() -> Unit = {}) : StructureUnitB<B>(value), ModuleGroupI<B> {

    override fun modules(): ListMultiHolder<ModuleI<*>> = itemAsList(MODULES, ModuleI::class.java, true)
    override fun modules(vararg value: ModuleI<*>): B = apply { modules().addItems(value.asList()) }

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

open class ProcessManagerB<B : ProcessManagerB<B>>(value: B.() -> Unit = {}) : StateMachineB<B>(value),
                                                                               ProcessManagerI<B> {}


open class Projector(value: Projector.() -> Unit = {}) : ProjectorB<Projector>(value) {

    companion object {
        val EMPTY = Projector { name(ItemEmpty.name()) }.apply<Projector> { init() }
    }
}

open class ProjectorB<B : ProjectorB<B>>(value: B.() -> Unit = {}) : StateMachineB<B>(value), ProjectorI<B> {}


open class State(value: State.() -> Unit = {}) : StateB<State>(value) {

    companion object {
        val EMPTY = State { name(ItemEmpty.name()) }.apply<State> { init() }
    }
}

open class StateB<B : StateB<B>>(value: B.() -> Unit = {}) : ControllerB<B>(value), StateI<B> {

    override fun timeout(): Long = attr(TIMEOUT, { 0L })
    override fun timeout(value: Long): B = apply { attr(TIMEOUT, value) }

    override fun entryActions(): ListMultiHolder<ActionI<*>> = itemAsList(ENTRY_ACTIONS, ActionI::class.java, true)
    override fun entryActions(vararg value: ActionI<*>): B = apply { entryActions().addItems(value.asList()) }
    override fun entry(value: ActionI<*>): ActionI<*> = applyAndReturn { entryActions().addItem(value); value }
    override fun entry(value: ActionI<*>.() -> Unit): ActionI<*> = entry(Action(value))

    override fun exitActions(): ListMultiHolder<ActionI<*>> = itemAsList(EXIT_ACTIONS, ActionI::class.java, true)
    override fun exitActions(vararg value: ActionI<*>): B = apply { exitActions().addItems(value.asList()) }
    override fun exit(value: ActionI<*>): ActionI<*> = applyAndReturn { exitActions().addItem(value); value }
    override fun exit(value: ActionI<*>.() -> Unit): ActionI<*> = exit(Action(value))

    override fun executors(): ListMultiHolder<ExecutorI<*>> = itemAsList(EXECUTORS, ExecutorI::class.java, true)
    override fun executors(vararg value: ExecutorI<*>): B = apply { executors().addItems(value.asList()) }
    override fun execute(value: ExecutorI<*>): ExecutorI<*> = applyAndReturn { executors().addItem(value); value }
    override fun execute(value: ExecutorI<*>.() -> Unit): ExecutorI<*> = execute(Executor(value))

    override fun handlers(): ListMultiHolder<HandlerI<*>> = itemAsList(HANDLERS, HandlerI::class.java, true)
    override fun handlers(vararg value: HandlerI<*>): B = apply { handlers().addItems(value.asList()) }
    override fun handle(value: HandlerI<*>): HandlerI<*> = applyAndReturn { handlers().addItem(value); value }
    override fun handle(value: HandlerI<*>.() -> Unit): HandlerI<*> = handle(Handler(value))

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

open class StateMachineB<B : StateMachineB<B>>(value: B.() -> Unit = {}) : ControllerB<B>(value), StateMachineI<B> {

    override fun stateProp(): AttributeI<*> = attr(STATE_PROP, { Attribute.EMPTY })
    override fun stateProp(value: AttributeI<*>): B = apply { attr(STATE_PROP, value) }

    override fun timeoutProp(): AttributeI<*> = attr(TIMEOUT_PROP, { Attribute.EMPTY })
    override fun timeoutProp(value: AttributeI<*>): B = apply { attr(TIMEOUT_PROP, value) }

    override fun timeout(): Long = attr(TIMEOUT, { 0L })
    override fun timeout(value: Long): B = apply { attr(TIMEOUT, value) }

    override fun states(): ListMultiHolder<StateI<*>> = itemAsList(STATES, StateI::class.java, true)
    override fun states(vararg value: StateI<*>): B = apply { states().addItems(value.asList()) }
    override fun state(value: StateI<*>): StateI<*> = applyAndReturn { states().addItem(value); value }
    override fun state(value: StateI<*>.() -> Unit): StateI<*> = state(State(value))

    override fun checks(): ListMultiHolder<CheckI<*>> = itemAsList(CHECKS, CheckI::class.java, true)
    override fun checks(vararg value: CheckI<*>): B = apply { checks().addItems(value.asList()) }
    override fun check(value: CheckI<*>): CheckI<*> = applyAndReturn { checks().addItem(value); value }
    override fun check(value: CheckI<*>.() -> Unit): CheckI<*> = check(Check(value))

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

open class UpdateByB<B : UpdateByB<B>>(value: B.() -> Unit = {}) : CommandB<B>(value), UpdateByI<B> {}


open class Updated(value: Updated.() -> Unit = {}) : UpdatedB<Updated>(value) {

    companion object {
        val EMPTY = Updated { name(ItemEmpty.name()) }.apply<Updated> { init() }
    }
}

open class UpdatedB<B : UpdatedB<B>>(value: B.() -> Unit = {}) : EventB<B>(value), UpdatedI<B> {}


open class Values(value: Values.() -> Unit = {}) : ValuesB<Values>(value) {

    companion object {
        val EMPTY = Values { name(ItemEmpty.name()) }.apply<Values> { init() }
    }
}

open class ValuesB<B : ValuesB<B>>(value: B.() -> Unit = {}) : DataTypeB<B>(value), ValuesI<B> {}


open class Widget(value: Widget.() -> Unit = {}) : WidgetB<Widget>(value) {

    companion object {
        val EMPTY = Widget { name(ItemEmpty.name()) }.apply<Widget> { init() }
    }
}

open class WidgetB<B : WidgetB<B>>(value: B.() -> Unit = {}) : CompilationUnitB<B>(value), WidgetI<B> {}


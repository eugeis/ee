package ee.design

import ee.lang.Action
import ee.lang.ActionI
import ee.lang.Attribute
import ee.lang.AttributeI
import ee.lang.Basic
import ee.lang.BasicI
import ee.lang.CompilationUnitB
import ee.lang.DataTypeB
import ee.lang.DataTypeOperationB
import ee.lang.EnumType
import ee.lang.EnumTypeI
import ee.lang.ExternalTypeI
import ee.lang.ItemEmpty
import ee.lang.ListMultiHolder
import ee.lang.LogicUnitB
import ee.lang.Predicate
import ee.lang.PredicateI
import ee.lang.StructureUnitB
import ee.lang.StructureUnitI
import ee.lang.Values
import ee.lang.ValuesB
import ee.lang.ValuesI


open class AggregateHandler(adapt: AggregateHandler.() -> Unit = {}) : AggregateHandlerB<AggregateHandler>(adapt) {

    companion object {
        val EMPTY = AggregateHandler { name(ItemEmpty.name()) }.apply<AggregateHandler> { init() }
    }
}

open class AggregateHandlerB<B : AggregateHandlerB<B>>(adapt: B.() -> Unit = {}) : StateMachineB<B>(adapt), AggregateHandlerI<B> {
}


open class Bundle(adapt: Bundle.() -> Unit = {}) : BundleB<Bundle>(adapt) {

    companion object {
        val EMPTY = Bundle { name(ItemEmpty.name()) }.apply<Bundle> { init() }
    }
}

open class BundleB<B : BundleB<B>>(adapt: B.() -> Unit = {}) : StructureUnitB<B>(adapt), BundleI<B> {

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


open class BusinessCommand(adapt: BusinessCommand.() -> Unit = {}) : BusinessCommandB<BusinessCommand>(adapt) {

    companion object {
        val EMPTY = BusinessCommand { name(ItemEmpty.name()) }.apply<BusinessCommand> { init() }
    }
}

open class BusinessCommandB<B : BusinessCommandB<B>>(adapt: B.() -> Unit = {}) : CommandB<B>(adapt), BusinessCommandI<B> {
}


open class BusinessController(adapt: BusinessController.() -> Unit = {}) : BusinessControllerB<BusinessController>(adapt) {

    companion object {
        val EMPTY = BusinessController { name(ItemEmpty.name()) }.apply<BusinessController> { init() }
    }
}

open class BusinessControllerB<B : BusinessControllerB<B>>(adapt: B.() -> Unit = {}) : ControllerB<B>(adapt), BusinessControllerI<B> {
}


open class BusinessEvent(adapt: BusinessEvent.() -> Unit = {}) : BusinessEventB<BusinessEvent>(adapt) {

    companion object {
        val EMPTY = BusinessEvent { name(ItemEmpty.name()) }.apply<BusinessEvent> { init() }
    }
}

open class BusinessEventB<B : BusinessEventB<B>>(adapt: B.() -> Unit = {}) : EventB<B>(adapt), BusinessEventI<B> {
}


open class Command(adapt: Command.() -> Unit = {}) : CommandB<Command>(adapt) {

    companion object {
        val EMPTY = Command { name(ItemEmpty.name()) }.apply<Command> { init() }
    }
}

open class CommandB<B : CommandB<B>>(adapt: B.() -> Unit = {}) : DataTypeB<B>(adapt), CommandI<B> {

    override fun httpMethod(): String = attr(HTTP_METHOD, { "" })
    override fun httpMethod(value: String): B = apply { attr(HTTP_METHOD, value) }

    override fun isAffectMulti(): Boolean = attr(AFFECT_MULTI, { false })
    override fun affectMulti(value: Boolean): B = apply { attr(AFFECT_MULTI, value) }

    override fun event(): EventI<*> = attr(EVENT, { Event.EMPTY })
    override fun event(value: EventI<*>): B = apply { attr(EVENT, value) }

    companion object {
        val HTTP_METHOD = "_httpMethod"
        val AFFECT_MULTI = "_affectMulti"
        val EVENT = "_event"
    }
}


open class Comp(adapt: Comp.() -> Unit = {}) : CompB<Comp>(adapt) {

    companion object {
        val EMPTY = Comp { name(ItemEmpty.name()) }.apply<Comp> { init() }
    }
}

open class CompB<B : CompB<B>>(adapt: B.() -> Unit = {}) : ModuleGroupB<B>(adapt), CompI<B> {

    override fun moduleGroups(): ListMultiHolder<ModuleGroupI<*>> = itemAsList(MODULE_GROUPS, ModuleGroupI::class.java, true)
    override fun moduleGroups(vararg value: ModuleGroupI<*>): B = apply { moduleGroups().addItems(value.asList()) }

    override fun fillSupportsItems() {
        moduleGroups()
        super.fillSupportsItems()
    }

    companion object {
        val MODULE_GROUPS = "_moduleGroups"
    }
}


open class CompositeCommand(adapt: CompositeCommand.() -> Unit = {}) : CompositeCommandB<CompositeCommand>(adapt) {

    companion object {
        val EMPTY = CompositeCommand { name(ItemEmpty.name()) }.apply<CompositeCommand> { init() }
    }
}

open class CompositeCommandB<B : CompositeCommandB<B>>(adapt: B.() -> Unit = {}) : CompilationUnitB<B>(adapt), CompositeCommandI<B> {

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


open class Config(adapt: Config.() -> Unit = {}) : ConfigB<Config>(adapt) {

    companion object {
        val EMPTY = Config { name(ItemEmpty.name()) }.apply<Config> { init() }
    }
}

open class ConfigB<B : ConfigB<B>>(adapt: B.() -> Unit = {}) : ValuesB<B>(adapt), ConfigI<B> {

    override fun prefix(): String = attr(PREFIX, { "" })
    override fun prefix(value: String): B = apply { attr(PREFIX, value) }

    companion object {
        val PREFIX = "_prefix"
    }
}


open class Controller(adapt: Controller.() -> Unit = {}) : ControllerB<Controller>(adapt) {

    companion object {
        val EMPTY = Controller { name(ItemEmpty.name()) }.apply<Controller> { init() }
    }
}

open class ControllerB<B : ControllerB<B>>(adapt: B.() -> Unit = {}) : CompilationUnitB<B>(adapt), ControllerI<B> {

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


open class CountBy(adapt: CountBy.() -> Unit = {}) : CountByB<CountBy>(adapt) {

    companion object {
        val EMPTY = CountBy { name(ItemEmpty.name()) }.apply<CountBy> { init() }
    }
}

open class CountByB<B : CountByB<B>>(adapt: B.() -> Unit = {}) : DataTypeOperationB<B>(adapt), CountByI<B> {
}


open class CreateBy(adapt: CreateBy.() -> Unit = {}) : CreateByB<CreateBy>(adapt) {

    companion object {
        val EMPTY = CreateBy { name(ItemEmpty.name()) }.apply<CreateBy> { init() }
    }
}

open class CreateByB<B : CreateByB<B>>(adapt: B.() -> Unit = {}) : CommandB<B>(adapt), CreateByI<B> {
}


open class Created(adapt: Created.() -> Unit = {}) : CreatedB<Created>(adapt) {

    companion object {
        val EMPTY = Created { name(ItemEmpty.name()) }.apply<Created> { init() }
    }
}

open class CreatedB<B : CreatedB<B>>(adapt: B.() -> Unit = {}) : EventB<B>(adapt), CreatedI<B> {
}


open class DeleteBy(adapt: DeleteBy.() -> Unit = {}) : DeleteByB<DeleteBy>(adapt) {

    companion object {
        val EMPTY = DeleteBy { name(ItemEmpty.name()) }.apply<DeleteBy> { init() }
    }
}

open class DeleteByB<B : DeleteByB<B>>(adapt: B.() -> Unit = {}) : CommandB<B>(adapt), DeleteByI<B> {
}


open class Deleted(adapt: Deleted.() -> Unit = {}) : DeletedB<Deleted>(adapt) {

    companion object {
        val EMPTY = Deleted { name(ItemEmpty.name()) }.apply<Deleted> { init() }
    }
}

open class DeletedB<B : DeletedB<B>>(adapt: B.() -> Unit = {}) : EventB<B>(adapt), DeletedI<B> {
}


open class DynamicState(adapt: DynamicState.() -> Unit = {}) : DynamicStateB<DynamicState>(adapt) {

    companion object {
        val EMPTY = DynamicState { name(ItemEmpty.name()) }.apply<DynamicState> { init() }
    }
}

open class DynamicStateB<B : DynamicStateB<B>>(adapt: B.() -> Unit = {}) : StateB<B>(adapt), DynamicStateI<B> {

    override fun ifTrue(): ListMultiHolder<PredicateI<*>> = itemAsList(IF_TRUE, PredicateI::class.java, true)
    override fun ifTrue(vararg value: PredicateI<*>): B = apply { ifTrue().addItems(value.asList()) }
    override fun ifT(value: PredicateI<*>): PredicateI<*> = applyAndReturn { ifTrue().addItem(value); value }
    override fun ifT(value: PredicateI<*>.() -> Unit): PredicateI<*> = ifT(Predicate(value))

    override fun ifFalse(): ListMultiHolder<PredicateI<*>> = itemAsList(IF_FALSE, PredicateI::class.java, true)
    override fun ifFalse(vararg value: PredicateI<*>): B = apply { ifFalse().addItems(value.asList()) }
    override fun ifF(value: PredicateI<*>): PredicateI<*> = applyAndReturn { ifFalse().addItem(value); value }
    override fun ifF(value: PredicateI<*>.() -> Unit): PredicateI<*> = ifF(Predicate(value))

    override fun fillSupportsItems() {
        ifTrue()
        ifFalse()
        super.fillSupportsItems()
    }

    companion object {
        val IF_TRUE = "_ifTrue"
        val IF_FALSE = "_ifFalse"
    }
}


open class Entity(adapt: Entity.() -> Unit = {}) : EntityB<Entity>(adapt) {

    companion object {
        val EMPTY = Entity { name(ItemEmpty.name()) }.apply<Entity> { init() }
    }
}

open class EntityB<B : EntityB<B>>(adapt: B.() -> Unit = {}) : DataTypeB<B>(adapt), EntityI<B> {

    override fun isDefaultEvents(): Boolean = attr(DEFAULT_EVENTS, { true })
    override fun defaultEvents(value: Boolean): B = apply { attr(DEFAULT_EVENTS, value) }

    override fun isDefaultQueries(): Boolean = attr(DEFAULT_QUERIES, { true })
    override fun defaultQueries(value: Boolean): B = apply { attr(DEFAULT_QUERIES, value) }

    override fun isDefaultCommands(): Boolean = attr(DEFAULT_COMMANDS, { true })
    override fun defaultCommands(value: Boolean): B = apply { attr(DEFAULT_COMMANDS, value) }

    override fun belongsToAggregate(): EntityI<*> = attr(BELONGS_TO_AGGREGATE, { Entity.EMPTY })
    override fun belongsToAggregate(value: EntityI<*>): B = apply { attr(BELONGS_TO_AGGREGATE, value) }

    override fun aggregateFor(): ListMultiHolder<EntityI<*>> = itemAsList(AGGREGATE_FOR, EntityI::class.java, true)
    override fun aggregateFor(vararg value: EntityI<*>): B = apply { aggregateFor().addItems(value.asList()) }

    override fun controllers(): ListMultiHolder<BusinessControllerI<*>> = itemAsList(CONTROLLERS, BusinessControllerI::class.java, true)
    override fun controllers(vararg value: BusinessControllerI<*>): B = apply { controllers().addItems(value.asList()) }
    override fun controller(value: BusinessControllerI<*>): BusinessControllerI<*> = applyAndReturn { controllers().addItem(value); value }
    override fun controller(value: BusinessControllerI<*>.() -> Unit): BusinessControllerI<*> = controller(BusinessController(value))

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

    override fun commands(): ListMultiHolder<BusinessCommandI<*>> = itemAsList(COMMANDS, BusinessCommandI::class.java, true)
    override fun commands(vararg value: BusinessCommandI<*>): B = apply { commands().addItems(value.asList()) }
    override fun command(value: BusinessCommandI<*>): BusinessCommandI<*> = applyAndReturn { commands().addItem(value); value }
    override fun command(value: BusinessCommandI<*>.() -> Unit): BusinessCommandI<*> = command(BusinessCommand(value))

    override fun composites(): ListMultiHolder<CompositeCommandI<*>> = itemAsList(COMPOSITES, CompositeCommandI::class.java, true)
    override fun composites(vararg value: CompositeCommandI<*>): B = apply { composites().addItems(value.asList()) }
    override fun composite(value: CompositeCommandI<*>): CompositeCommandI<*> = applyAndReturn { composites().addItem(value); value }
    override fun composite(value: CompositeCommandI<*>.() -> Unit): CompositeCommandI<*> = composite(CompositeCommand(value))

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

    override fun checks(): ListMultiHolder<PredicateI<*>> = itemAsList(CHECKS, PredicateI::class.java, true)
    override fun checks(vararg value: PredicateI<*>): B = apply { checks().addItems(value.asList()) }
    override fun check(value: PredicateI<*>): PredicateI<*> = applyAndReturn { checks().addItem(value); value }
    override fun check(value: PredicateI<*>.() -> Unit): PredicateI<*> = check(Predicate(value))

    override fun handlers(): ListMultiHolder<AggregateHandlerI<*>> = itemAsList(HANDLERS, AggregateHandlerI::class.java, true)
    override fun handlers(vararg value: AggregateHandlerI<*>): B = apply { handlers().addItems(value.asList()) }
    override fun handler(value: AggregateHandlerI<*>): AggregateHandlerI<*> = applyAndReturn { handlers().addItem(value); value }
    override fun handler(value: AggregateHandlerI<*>.() -> Unit): AggregateHandlerI<*> = handler(AggregateHandler(value))

    override fun projectors(): ListMultiHolder<ProjectorI<*>> = itemAsList(PROJECTORS, ProjectorI::class.java, true)
    override fun projectors(vararg value: ProjectorI<*>): B = apply { projectors().addItems(value.asList()) }
    override fun projector(value: ProjectorI<*>): ProjectorI<*> = applyAndReturn { projectors().addItem(value); value }
    override fun projector(value: ProjectorI<*>.() -> Unit): ProjectorI<*> = projector(Projector(value))

    override fun saga(): ListMultiHolder<SagaI<*>> = itemAsList(SAGA, SagaI::class.java, true)
    override fun saga(vararg value: SagaI<*>): B = apply { saga().addItems(value.asList()) }
    override fun saga(value: SagaI<*>): SagaI<*> = applyAndReturn { saga().addItem(value); value }
    override fun saga(value: SagaI<*>.() -> Unit): SagaI<*> = saga(Saga(value))

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
        checks()
        handlers()
        projectors()
        saga()
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
        val CHECKS = "_checks"
        val HANDLERS = "_handlers"
        val PROJECTORS = "_projectors"
        val SAGA = "_saga"
    }
}


open class Event(adapt: Event.() -> Unit = {}) : EventB<Event>(adapt) {

    companion object {
        val EMPTY = Event { name(ItemEmpty.name()) }.apply<Event> { init() }
    }
}

open class EventB<B : EventB<B>>(adapt: B.() -> Unit = {}) : DataTypeB<B>(adapt), EventI<B> {
}


open class Executor(adapt: Executor.() -> Unit = {}) : ExecutorB<Executor>(adapt) {

    companion object {
        val EMPTY = Executor { name(ItemEmpty.name()) }.apply<Executor> { init() }
    }
}

open class ExecutorB<B : ExecutorB<B>>(adapt: B.() -> Unit = {}) : LogicUnitB<B>(adapt), ExecutorI<B> {

    override fun on(): CommandI<*> = attr(ON, { Command.EMPTY })
    override fun on(value: CommandI<*>): B = apply { attr(ON, value) }

    override fun ifTrue(): ListMultiHolder<PredicateI<*>> = itemAsList(IF_TRUE, PredicateI::class.java, true)
    override fun ifTrue(vararg value: PredicateI<*>): B = apply { ifTrue().addItems(value.asList()) }
    override fun ifT(value: PredicateI<*>): PredicateI<*> = applyAndReturn { ifTrue().addItem(value); value }
    override fun ifT(value: PredicateI<*>.() -> Unit): PredicateI<*> = ifT(Predicate(value))

    override fun ifFalse(): ListMultiHolder<PredicateI<*>> = itemAsList(IF_FALSE, PredicateI::class.java, true)
    override fun ifFalse(vararg value: PredicateI<*>): B = apply { ifFalse().addItems(value.asList()) }
    override fun ifF(value: PredicateI<*>): PredicateI<*> = applyAndReturn { ifFalse().addItem(value); value }
    override fun ifF(value: PredicateI<*>.() -> Unit): PredicateI<*> = ifF(Predicate(value))

    override fun actions(): ListMultiHolder<ActionI<*>> = itemAsList(ACTIONS, ActionI::class.java, true)
    override fun actions(vararg value: ActionI<*>): B = apply { actions().addItems(value.asList()) }
    override fun action(value: ActionI<*>): ActionI<*> = applyAndReturn { actions().addItem(value); value }
    override fun action(value: ActionI<*>.() -> Unit): ActionI<*> = action(Action(value))

    override fun output(): ListMultiHolder<EventI<*>> = itemAsList(OUTPUT, EventI::class.java, true)
    override fun output(vararg value: EventI<*>): B = apply { output().addItems(value.asList()) }
    override fun produce(value: EventI<*>): EventI<*> = applyAndReturn { output().addItem(value); value }
    override fun produce(value: EventI<*>.() -> Unit): EventI<*> = produce(Event(value))

    override fun fillSupportsItems() {
        ifTrue()
        ifFalse()
        actions()
        output()
        super.fillSupportsItems()
    }

    companion object {
        val ON = "_on"
        val IF_TRUE = "_ifTrue"
        val IF_FALSE = "_ifFalse"
        val ACTIONS = "_actions"
        val OUTPUT = "_output"
    }
}


open class ExistBy(adapt: ExistBy.() -> Unit = {}) : ExistByB<ExistBy>(adapt) {

    companion object {
        val EMPTY = ExistBy { name(ItemEmpty.name()) }.apply<ExistBy> { init() }
    }
}

open class ExistByB<B : ExistByB<B>>(adapt: B.() -> Unit = {}) : DataTypeOperationB<B>(adapt), ExistByI<B> {
}


open class ExternalModule(adapt: ExternalModule.() -> Unit = {}) : ExternalModuleB<ExternalModule>(adapt) {

    companion object {
        val EMPTY = ExternalModule { name(ItemEmpty.name()) }.apply<ExternalModule> { init() }
    }
}

open class ExternalModuleB<B : ExternalModuleB<B>>(adapt: B.() -> Unit = {}) : ModuleB<B>(adapt), ExternalModuleI<B> {

    override fun externalTypes(): ListMultiHolder<ExternalTypeI<*>> = itemAsList(EXTERNAL_TYPES, ExternalTypeI::class.java, true)
    override fun externalTypes(vararg value: ExternalTypeI<*>): B = apply { externalTypes().addItems(value.asList()) }

    override fun fillSupportsItems() {
        externalTypes()
        super.fillSupportsItems()
    }

    companion object {
        val EXTERNAL_TYPES = "_externalTypes"
    }
}


open class Facet(adapt: Facet.() -> Unit = {}) : FacetB<Facet>(adapt) {

    companion object {
        val EMPTY = Facet { name(ItemEmpty.name()) }.apply<Facet> { init() }
    }
}

open class FacetB<B : FacetB<B>>(adapt: B.() -> Unit = {}) : ModuleGroupB<B>(adapt), FacetI<B> {
}


open class FindBy(adapt: FindBy.() -> Unit = {}) : FindByB<FindBy>(adapt) {

    companion object {
        val EMPTY = FindBy { name(ItemEmpty.name()) }.apply<FindBy> { init() }
    }
}

open class FindByB<B : FindByB<B>>(adapt: B.() -> Unit = {}) : DataTypeOperationB<B>(adapt), FindByI<B> {

    override fun isMultiResult(): Boolean = attr(MULTI_RESULT, { true })
    override fun multiResult(value: Boolean): B = apply { attr(MULTI_RESULT, value) }

    companion object {
        val MULTI_RESULT = "_multiResult"
    }
}


open class Handler(adapt: Handler.() -> Unit = {}) : HandlerB<Handler>(adapt) {

    companion object {
        val EMPTY = Handler { name(ItemEmpty.name()) }.apply<Handler> { init() }
    }
}

open class HandlerB<B : HandlerB<B>>(adapt: B.() -> Unit = {}) : LogicUnitB<B>(adapt), HandlerI<B> {

    override fun on(): EventI<*> = attr(ON, { Event.EMPTY })
    override fun on(value: EventI<*>): B = apply { attr(ON, value) }

    override fun ifTrue(): ListMultiHolder<PredicateI<*>> = itemAsList(IF_TRUE, PredicateI::class.java, true)
    override fun ifTrue(vararg value: PredicateI<*>): B = apply { ifTrue().addItems(value.asList()) }
    override fun ifT(value: PredicateI<*>): PredicateI<*> = applyAndReturn { ifTrue().addItem(value); value }
    override fun ifT(value: PredicateI<*>.() -> Unit): PredicateI<*> = ifT(Predicate(value))

    override fun ifFalse(): ListMultiHolder<PredicateI<*>> = itemAsList(IF_FALSE, PredicateI::class.java, true)
    override fun ifFalse(vararg value: PredicateI<*>): B = apply { ifFalse().addItems(value.asList()) }
    override fun ifF(value: PredicateI<*>): PredicateI<*> = applyAndReturn { ifFalse().addItem(value); value }
    override fun ifF(value: PredicateI<*>.() -> Unit): PredicateI<*> = ifF(Predicate(value))

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
        ifTrue()
        ifFalse()
        actions()
        output()
        super.fillSupportsItems()
    }

    companion object {
        val ON = "_on"
        val IF_TRUE = "_ifTrue"
        val IF_FALSE = "_ifFalse"
        val TO = "_to"
        val ACTIONS = "_actions"
        val OUTPUT = "_output"
    }
}


open class Model(adapt: Model.() -> Unit = {}) : ModelB<Model>(adapt) {

    companion object {
        val EMPTY = Model { name(ItemEmpty.name()) }.apply<Model> { init() }
    }
}

open class ModelB<B : ModelB<B>>(adapt: B.() -> Unit = {}) : StructureUnitB<B>(adapt), ModelI<B> {

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


open class Module(adapt: Module.() -> Unit = {}) : ModuleB<Module>(adapt) {

    companion object {
        val EMPTY = Module { name(ItemEmpty.name()) }.apply<Module> { init() }
    }
}

open class ModuleB<B : ModuleB<B>>(adapt: B.() -> Unit = {}) : StructureUnitB<B>(adapt), ModuleI<B> {

    override fun isParentNamespace(): Boolean = attr(PARENT_NAMESPACE, { false })
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

    override fun controllers(): ListMultiHolder<BusinessControllerI<*>> = itemAsList(CONTROLLERS, BusinessControllerI::class.java, true)
    override fun controllers(vararg value: BusinessControllerI<*>): B = apply { controllers().addItems(value.asList()) }
    override fun controller(value: BusinessControllerI<*>): BusinessControllerI<*> = applyAndReturn { controllers().addItem(value); value }
    override fun controller(value: BusinessControllerI<*>.() -> Unit): BusinessControllerI<*> = controller(BusinessController(value))

    override fun sagas(): ListMultiHolder<SagaI<*>> = itemAsList(SAGAS, SagaI::class.java, true)
    override fun sagas(vararg value: SagaI<*>): B = apply { sagas().addItems(value.asList()) }
    override fun saga(value: SagaI<*>): SagaI<*> = applyAndReturn { sagas().addItem(value); value }
    override fun saga(value: SagaI<*>.() -> Unit): SagaI<*> = saga(Saga(value))

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
        sagas()
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
        val SAGAS = "_sagas"
        val PROJECTORS = "_projectors"
    }
}


open class ModuleGroup(adapt: ModuleGroup.() -> Unit = {}) : ModuleGroupB<ModuleGroup>(adapt) {

    companion object {
        val EMPTY = ModuleGroup { name(ItemEmpty.name()) }.apply<ModuleGroup> { init() }
    }
}

open class ModuleGroupB<B : ModuleGroupB<B>>(adapt: B.() -> Unit = {}) : StructureUnitB<B>(adapt), ModuleGroupI<B> {

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


open class Projector(adapt: Projector.() -> Unit = {}) : ProjectorB<Projector>(adapt) {

    companion object {
        val EMPTY = Projector { name(ItemEmpty.name()) }.apply<Projector> { init() }
    }
}

open class ProjectorB<B : ProjectorB<B>>(adapt: B.() -> Unit = {}) : StateMachineB<B>(adapt), ProjectorI<B> {
}


open class Saga(adapt: Saga.() -> Unit = {}) : SagaB<Saga>(adapt) {

    companion object {
        val EMPTY = Saga { name(ItemEmpty.name()) }.apply<Saga> { init() }
    }
}

open class SagaB<B : SagaB<B>>(adapt: B.() -> Unit = {}) : StateMachineB<B>(adapt), SagaI<B> {
}


open class State(adapt: State.() -> Unit = {}) : StateB<State>(adapt) {

    companion object {
        val EMPTY = State { name(ItemEmpty.name()) }.apply<State> { init() }
    }
}

open class StateB<B : StateB<B>>(adapt: B.() -> Unit = {}) : ControllerB<B>(adapt), StateI<B> {

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

    override fun controllers(): ListMultiHolder<BusinessControllerI<*>> = itemAsList(CONTROLLERS, BusinessControllerI::class.java, true)
    override fun controllers(vararg value: BusinessControllerI<*>): B = apply { controllers().addItems(value.asList()) }
    override fun controller(value: BusinessControllerI<*>): BusinessControllerI<*> = applyAndReturn { controllers().addItem(value); value }
    override fun controller(value: BusinessControllerI<*>.() -> Unit): BusinessControllerI<*> = controller(BusinessController(value))

    override fun fillSupportsItems() {
        entryActions()
        exitActions()
        executors()
        handlers()
        controllers()
        super.fillSupportsItems()
    }

    companion object {
        val TIMEOUT = "_timeout"
        val ENTRY_ACTIONS = "_entryActions"
        val EXIT_ACTIONS = "_exitActions"
        val EXECUTORS = "_executors"
        val HANDLERS = "_handlers"
        val CONTROLLERS = "_controllers"
    }
}


open class StateMachine(adapt: StateMachine.() -> Unit = {}) : StateMachineB<StateMachine>(adapt) {

    companion object {
        val EMPTY = StateMachine { name(ItemEmpty.name()) }.apply<StateMachine> { init() }
    }
}

open class StateMachineB<B : StateMachineB<B>>(adapt: B.() -> Unit = {}) : ControllerB<B>(adapt), StateMachineI<B> {

    override fun defaultState(): StateI<*> = attr(DEFAULT_STATE, { State.EMPTY })
    override fun defaultState(value: StateI<*>): B = apply { attr(DEFAULT_STATE, value) }

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

    override fun checks(): ListMultiHolder<PredicateI<*>> = itemAsList(CHECKS, PredicateI::class.java, true)
    override fun checks(vararg value: PredicateI<*>): B = apply { checks().addItems(value.asList()) }
    override fun check(value: PredicateI<*>): PredicateI<*> = applyAndReturn { checks().addItem(value); value }
    override fun check(value: PredicateI<*>.() -> Unit): PredicateI<*> = check(Predicate(value))

    override fun controllers(): ListMultiHolder<BusinessControllerI<*>> = itemAsList(CONTROLLERS, BusinessControllerI::class.java, true)
    override fun controllers(vararg value: BusinessControllerI<*>): B = apply { controllers().addItems(value.asList()) }
    override fun controller(value: BusinessControllerI<*>): BusinessControllerI<*> = applyAndReturn { controllers().addItem(value); value }
    override fun controller(value: BusinessControllerI<*>.() -> Unit): BusinessControllerI<*> = controller(BusinessController(value))

    override fun fillSupportsItems() {
        states()
        checks()
        controllers()
        super.fillSupportsItems()
    }

    companion object {
        val DEFAULT_STATE = "_defaultState"
        val STATE_PROP = "_stateProp"
        val TIMEOUT_PROP = "_timeoutProp"
        val TIMEOUT = "_timeout"
        val STATES = "_states"
        val CHECKS = "_checks"
        val CONTROLLERS = "_controllers"
    }
}


open class UpdateBy(adapt: UpdateBy.() -> Unit = {}) : UpdateByB<UpdateBy>(adapt) {

    companion object {
        val EMPTY = UpdateBy { name(ItemEmpty.name()) }.apply<UpdateBy> { init() }
    }
}

open class UpdateByB<B : UpdateByB<B>>(adapt: B.() -> Unit = {}) : CommandB<B>(adapt), UpdateByI<B> {
}


open class Updated(adapt: Updated.() -> Unit = {}) : UpdatedB<Updated>(adapt) {

    companion object {
        val EMPTY = Updated { name(ItemEmpty.name()) }.apply<Updated> { init() }
    }
}

open class UpdatedB<B : UpdatedB<B>>(adapt: B.() -> Unit = {}) : EventB<B>(adapt), UpdatedI<B> {
}


open class Widget(adapt: Widget.() -> Unit = {}) : WidgetB<Widget>(adapt) {

    companion object {
        val EMPTY = Widget { name(ItemEmpty.name()) }.apply<Widget> { init() }
    }
}

open class WidgetB<B : WidgetB<B>>(adapt: B.() -> Unit = {}) : CompilationUnitB<B>(adapt), WidgetI<B> {
}


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

    override fun units(): ListMultiHolder<StructureUnitI<*>> = itemAsList(units, StructureUnitI::class.java, true)
    override fun units(vararg value: StructureUnitI<*>): B = apply { units().addItems(value.asList()) }

    override fun fillSupportsItems() {
        units()
        super.fillSupportsItems()
    }

    companion object {
        val units = "_units"
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

open class CommandB<B : CommandB<B>>(adapt: B.() -> Unit = {}) : CompilationUnitB<B>(adapt), CommandI<B> {

    override fun httpMethod(): String = attr(httpMethod, { "" })
    override fun httpMethod(value: String): B = apply { attr(httpMethod, value) }

    override fun isAffectMulti(): Boolean = attr(affectMulti, { false })
    override fun affectMulti(value: Boolean): B = apply { attr(affectMulti, value) }

    override fun event(): EventI<*> = attr(event, { Event.EMPTY })
    override fun event(value: EventI<*>): B = apply { attr(event, value) }

    companion object {
        val httpMethod = "_httpMethod"
        val affectMulti = "_affectMulti"
        val event = "_event"
    }
}


open class Comp(adapt: Comp.() -> Unit = {}) : CompB<Comp>(adapt) {

    companion object {
        val EMPTY = Comp { name(ItemEmpty.name()) }.apply<Comp> { init() }
    }
}

open class CompB<B : CompB<B>>(adapt: B.() -> Unit = {}) : ModuleGroupB<B>(adapt), CompI<B> {

    override fun moduleGroups(): ListMultiHolder<ModuleGroupI<*>> = itemAsList(moduleGroups, ModuleGroupI::class.java, true)
    override fun moduleGroups(vararg value: ModuleGroupI<*>): B = apply { moduleGroups().addItems(value.asList()) }

    override fun fillSupportsItems() {
        moduleGroups()
        super.fillSupportsItems()
    }

    companion object {
        val moduleGroups = "_moduleGroups"
    }
}


open class CompositeCommand(adapt: CompositeCommand.() -> Unit = {}) : CompositeCommandB<CompositeCommand>(adapt) {

    companion object {
        val EMPTY = CompositeCommand { name(ItemEmpty.name()) }.apply<CompositeCommand> { init() }
    }
}

open class CompositeCommandB<B : CompositeCommandB<B>>(adapt: B.() -> Unit = {}) : CompilationUnitB<B>(adapt), CompositeCommandI<B> {

    override fun commands(): ListMultiHolder<CommandI<*>> = itemAsList(commands, CommandI::class.java, true)
    override fun commands(vararg value: CommandI<*>): B = apply { commands().addItems(value.asList()) }

    override fun fillSupportsItems() {
        commands()
        super.fillSupportsItems()
    }

    companion object {
        val commands = "_commands"
    }
}


open class Config(adapt: Config.() -> Unit = {}) : ConfigB<Config>(adapt) {

    companion object {
        val EMPTY = Config { name(ItemEmpty.name()) }.apply<Config> { init() }
    }
}

open class ConfigB<B : ConfigB<B>>(adapt: B.() -> Unit = {}) : ValuesB<B>(adapt), ConfigI<B> {

    override fun prefix(): String = attr(prefix, { "" })
    override fun prefix(value: String): B = apply { attr(prefix, value) }

    companion object {
        val prefix = "_prefix"
    }
}


open class Controller(adapt: Controller.() -> Unit = {}) : ControllerB<Controller>(adapt) {

    companion object {
        val EMPTY = Controller { name(ItemEmpty.name()) }.apply<Controller> { init() }
    }
}

open class ControllerB<B : ControllerB<B>>(adapt: B.() -> Unit = {}) : CompilationUnitB<B>(adapt), ControllerI<B> {

    override fun enums(): ListMultiHolder<EnumTypeI<*>> = itemAsList(enums, EnumTypeI::class.java, true)
    override fun enums(vararg value: EnumTypeI<*>): B = apply { enums().addItems(value.asList()) }
    override fun enumType(value: EnumTypeI<*>): EnumTypeI<*> = applyAndReturn { enums().addItem(value); value }
    override fun enumType(value: EnumTypeI<*>.() -> Unit): EnumTypeI<*> = enumType(EnumType(value))

    override fun values(): ListMultiHolder<ValuesI<*>> = itemAsList(values, ValuesI::class.java, true)
    override fun values(vararg value: ValuesI<*>): B = apply { values().addItems(value.asList()) }
    override fun valueType(value: ValuesI<*>): ValuesI<*> = applyAndReturn { values().addItem(value); value }
    override fun valueType(value: ValuesI<*>.() -> Unit): ValuesI<*> = valueType(Values(value))

    override fun basics(): ListMultiHolder<BasicI<*>> = itemAsList(basics, BasicI::class.java, true)
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
        val enums = "_enums"
        val values = "_values"
        val basics = "_basics"
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

    override fun ifTrue(): ListMultiHolder<PredicateI<*>> = itemAsList(ifTrue, PredicateI::class.java, true)
    override fun ifTrue(vararg value: PredicateI<*>): B = apply { ifTrue().addItems(value.asList()) }
    override fun ifT(value: PredicateI<*>): PredicateI<*> = applyAndReturn { ifTrue().addItem(value); value }
    override fun ifT(value: PredicateI<*>.() -> Unit): PredicateI<*> = ifT(Predicate(value))

    override fun ifFalse(): ListMultiHolder<PredicateI<*>> = itemAsList(ifFalse, PredicateI::class.java, true)
    override fun ifFalse(vararg value: PredicateI<*>): B = apply { ifFalse().addItems(value.asList()) }
    override fun ifF(value: PredicateI<*>): PredicateI<*> = applyAndReturn { ifFalse().addItem(value); value }
    override fun ifF(value: PredicateI<*>.() -> Unit): PredicateI<*> = ifF(Predicate(value))

    override fun fillSupportsItems() {
        ifTrue()
        ifFalse()
        super.fillSupportsItems()
    }

    companion object {
        val ifTrue = "_ifTrue"
        val ifFalse = "_ifFalse"
    }
}


open class Entity(adapt: Entity.() -> Unit = {}) : EntityB<Entity>(adapt) {

    companion object {
        val EMPTY = Entity { name(ItemEmpty.name()) }.apply<Entity> { init() }
    }
}

open class EntityB<B : EntityB<B>>(adapt: B.() -> Unit = {}) : DataTypeB<B>(adapt), EntityI<B> {

    override fun isDefaultEvents(): Boolean = attr(defaultEvents, { true })
    override fun defaultEvents(value: Boolean): B = apply { attr(defaultEvents, value) }

    override fun isDefaultQueries(): Boolean = attr(defaultQueries, { true })
    override fun defaultQueries(value: Boolean): B = apply { attr(defaultQueries, value) }

    override fun isDefaultCommands(): Boolean = attr(defaultCommands, { true })
    override fun defaultCommands(value: Boolean): B = apply { attr(defaultCommands, value) }

    override fun belongsToAggregate(): EntityI<*> = attr(belongsToAggregate, { Entity.EMPTY })
    override fun belongsToAggregate(value: EntityI<*>): B = apply { attr(belongsToAggregate, value) }

    override fun aggregateFor(): ListMultiHolder<EntityI<*>> = itemAsList(aggregateFor, EntityI::class.java, true)
    override fun aggregateFor(vararg value: EntityI<*>): B = apply { aggregateFor().addItems(value.asList()) }

    override fun controllers(): ListMultiHolder<BusinessControllerI<*>> = itemAsList(controllers, BusinessControllerI::class.java, true)
    override fun controllers(vararg value: BusinessControllerI<*>): B = apply { controllers().addItems(value.asList()) }
    override fun controller(value: BusinessControllerI<*>): BusinessControllerI<*> = applyAndReturn { controllers().addItem(value); value }
    override fun controller(value: BusinessControllerI<*>.() -> Unit): BusinessControllerI<*> = controller(BusinessController(value))

    override fun findBys(): ListMultiHolder<FindByI<*>> = itemAsList(findBys, FindByI::class.java, true)
    override fun findBys(vararg value: FindByI<*>): B = apply { findBys().addItems(value.asList()) }
    override fun findBy(value: FindByI<*>): FindByI<*> = applyAndReturn { findBys().addItem(value); value }
    override fun findBy(value: FindByI<*>.() -> Unit): FindByI<*> = findBy(FindBy(value))

    override fun countBys(): ListMultiHolder<CountByI<*>> = itemAsList(countBys, CountByI::class.java, true)
    override fun countBys(vararg value: CountByI<*>): B = apply { countBys().addItems(value.asList()) }
    override fun countBy(value: CountByI<*>): CountByI<*> = applyAndReturn { countBys().addItem(value); value }
    override fun countBy(value: CountByI<*>.() -> Unit): CountByI<*> = countBy(CountBy(value))

    override fun existBys(): ListMultiHolder<ExistByI<*>> = itemAsList(existBys, ExistByI::class.java, true)
    override fun existBys(vararg value: ExistByI<*>): B = apply { existBys().addItems(value.asList()) }
    override fun existBy(value: ExistByI<*>): ExistByI<*> = applyAndReturn { existBys().addItem(value); value }
    override fun existBy(value: ExistByI<*>.() -> Unit): ExistByI<*> = existBy(ExistBy(value))

    override fun commands(): ListMultiHolder<BusinessCommandI<*>> = itemAsList(commands, BusinessCommandI::class.java, true)
    override fun commands(vararg value: BusinessCommandI<*>): B = apply { commands().addItems(value.asList()) }
    override fun command(value: BusinessCommandI<*>): BusinessCommandI<*> = applyAndReturn { commands().addItem(value); value }
    override fun command(value: BusinessCommandI<*>.() -> Unit): BusinessCommandI<*> = command(BusinessCommand(value))

    override fun composites(): ListMultiHolder<CompositeCommandI<*>> = itemAsList(composites, CompositeCommandI::class.java, true)
    override fun composites(vararg value: CompositeCommandI<*>): B = apply { composites().addItems(value.asList()) }
    override fun composite(value: CompositeCommandI<*>): CompositeCommandI<*> = applyAndReturn { composites().addItem(value); value }
    override fun composite(value: CompositeCommandI<*>.() -> Unit): CompositeCommandI<*> = composite(CompositeCommand(value))

    override fun createBys(): ListMultiHolder<CreateByI<*>> = itemAsList(createBys, CreateByI::class.java, true)
    override fun createBys(vararg value: CreateByI<*>): B = apply { createBys().addItems(value.asList()) }
    override fun createBy(value: CreateByI<*>): CreateByI<*> = applyAndReturn { createBys().addItem(value); value }
    override fun createBy(value: CreateByI<*>.() -> Unit): CreateByI<*> = createBy(CreateBy(value))

    override fun updateBys(): ListMultiHolder<UpdateByI<*>> = itemAsList(updateBys, UpdateByI::class.java, true)
    override fun updateBys(vararg value: UpdateByI<*>): B = apply { updateBys().addItems(value.asList()) }
    override fun updateBy(value: UpdateByI<*>): UpdateByI<*> = applyAndReturn { updateBys().addItem(value); value }
    override fun updateBy(value: UpdateByI<*>.() -> Unit): UpdateByI<*> = updateBy(UpdateBy(value))

    override fun deleteBys(): ListMultiHolder<DeleteByI<*>> = itemAsList(deleteBys, DeleteByI::class.java, true)
    override fun deleteBys(vararg value: DeleteByI<*>): B = apply { deleteBys().addItems(value.asList()) }
    override fun deleteBy(value: DeleteByI<*>): DeleteByI<*> = applyAndReturn { deleteBys().addItem(value); value }
    override fun deleteBy(value: DeleteByI<*>.() -> Unit): DeleteByI<*> = deleteBy(DeleteBy(value))

    override fun events(): ListMultiHolder<BusinessEventI<*>> = itemAsList(events, BusinessEventI::class.java, true)
    override fun events(vararg value: BusinessEventI<*>): B = apply { events().addItems(value.asList()) }
    override fun event(value: BusinessEventI<*>): BusinessEventI<*> = applyAndReturn { events().addItem(value); value }
    override fun event(value: BusinessEventI<*>.() -> Unit): BusinessEventI<*> = event(BusinessEvent(value))

    override fun created(): ListMultiHolder<CreatedI<*>> = itemAsList(created, CreatedI::class.java, true)
    override fun created(vararg value: CreatedI<*>): B = apply { created().addItems(value.asList()) }
    override fun created(value: CreatedI<*>): CreatedI<*> = applyAndReturn { created().addItem(value); value }
    override fun created(value: CreatedI<*>.() -> Unit): CreatedI<*> = created(Created(value))

    override fun updated(): ListMultiHolder<UpdatedI<*>> = itemAsList(updated, UpdatedI::class.java, true)
    override fun updated(vararg value: UpdatedI<*>): B = apply { updated().addItems(value.asList()) }
    override fun updated(value: UpdatedI<*>): UpdatedI<*> = applyAndReturn { updated().addItem(value); value }
    override fun updated(value: UpdatedI<*>.() -> Unit): UpdatedI<*> = updated(Updated(value))

    override fun deleted(): ListMultiHolder<DeletedI<*>> = itemAsList(deleted, DeletedI::class.java, true)
    override fun deleted(vararg value: DeletedI<*>): B = apply { deleted().addItems(value.asList()) }
    override fun deleted(value: DeletedI<*>): DeletedI<*> = applyAndReturn { deleted().addItem(value); value }
    override fun deleted(value: DeletedI<*>.() -> Unit): DeletedI<*> = deleted(Deleted(value))

    override fun checks(): ListMultiHolder<PredicateI<*>> = itemAsList(checks, PredicateI::class.java, true)
    override fun checks(vararg value: PredicateI<*>): B = apply { checks().addItems(value.asList()) }
    override fun check(value: PredicateI<*>): PredicateI<*> = applyAndReturn { checks().addItem(value); value }
    override fun check(value: PredicateI<*>.() -> Unit): PredicateI<*> = check(Predicate(value))

    override fun handlers(): ListMultiHolder<AggregateHandlerI<*>> = itemAsList(handlers, AggregateHandlerI::class.java, true)
    override fun handlers(vararg value: AggregateHandlerI<*>): B = apply { handlers().addItems(value.asList()) }
    override fun handler(value: AggregateHandlerI<*>): AggregateHandlerI<*> = applyAndReturn { handlers().addItem(value); value }
    override fun handler(value: AggregateHandlerI<*>.() -> Unit): AggregateHandlerI<*> = handler(AggregateHandler(value))

    override fun projectors(): ListMultiHolder<ProjectorI<*>> = itemAsList(projectors, ProjectorI::class.java, true)
    override fun projectors(vararg value: ProjectorI<*>): B = apply { projectors().addItems(value.asList()) }
    override fun projector(value: ProjectorI<*>): ProjectorI<*> = applyAndReturn { projectors().addItem(value); value }
    override fun projector(value: ProjectorI<*>.() -> Unit): ProjectorI<*> = projector(Projector(value))

    override fun processManager(): ListMultiHolder<ProcessManagerI<*>> = itemAsList(processManager, ProcessManagerI::class.java, true)
    override fun processManager(vararg value: ProcessManagerI<*>): B = apply { processManager().addItems(value.asList()) }
    override fun processManager(value: ProcessManagerI<*>): ProcessManagerI<*> = applyAndReturn { processManager().addItem(value); value }
    override fun processManager(value: ProcessManagerI<*>.() -> Unit): ProcessManagerI<*> = processManager(ProcessManager(value))

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
        processManager()
        super.fillSupportsItems()
    }

    companion object {
        val defaultEvents = "_defaultEvents"
        val defaultQueries = "_defaultQueries"
        val defaultCommands = "_defaultCommands"
        val belongsToAggregate = "_belongsToAggregate"
        val aggregateFor = "_aggregateFor"
        val controllers = "_controllers"
        val findBys = "_findBys"
        val countBys = "_countBys"
        val existBys = "_existBys"
        val commands = "_commands"
        val composites = "_composites"
        val createBys = "_createBys"
        val updateBys = "_updateBys"
        val deleteBys = "_deleteBys"
        val events = "_events"
        val created = "_created"
        val updated = "_updated"
        val deleted = "_deleted"
        val checks = "_checks"
        val handlers = "_handlers"
        val projectors = "_projectors"
        val processManager = "_processManager"
    }
}


open class Event(adapt: Event.() -> Unit = {}) : EventB<Event>(adapt) {

    companion object {
        val EMPTY = Event { name(ItemEmpty.name()) }.apply<Event> { init() }
    }
}

open class EventB<B : EventB<B>>(adapt: B.() -> Unit = {}) : CompilationUnitB<B>(adapt), EventI<B> {
}


open class Executor(adapt: Executor.() -> Unit = {}) : ExecutorB<Executor>(adapt) {

    companion object {
        val EMPTY = Executor { name(ItemEmpty.name()) }.apply<Executor> { init() }
    }
}

open class ExecutorB<B : ExecutorB<B>>(adapt: B.() -> Unit = {}) : LogicUnitB<B>(adapt), ExecutorI<B> {

    override fun on(): CommandI<*> = attr(on, { Command.EMPTY })
    override fun on(value: CommandI<*>): B = apply { attr(on, value) }

    override fun ifTrue(): ListMultiHolder<PredicateI<*>> = itemAsList(ifTrue, PredicateI::class.java, true)
    override fun ifTrue(vararg value: PredicateI<*>): B = apply { ifTrue().addItems(value.asList()) }
    override fun ifT(value: PredicateI<*>): PredicateI<*> = applyAndReturn { ifTrue().addItem(value); value }
    override fun ifT(value: PredicateI<*>.() -> Unit): PredicateI<*> = ifT(Predicate(value))

    override fun ifFalse(): ListMultiHolder<PredicateI<*>> = itemAsList(ifFalse, PredicateI::class.java, true)
    override fun ifFalse(vararg value: PredicateI<*>): B = apply { ifFalse().addItems(value.asList()) }
    override fun ifF(value: PredicateI<*>): PredicateI<*> = applyAndReturn { ifFalse().addItem(value); value }
    override fun ifF(value: PredicateI<*>.() -> Unit): PredicateI<*> = ifF(Predicate(value))

    override fun actions(): ListMultiHolder<ActionI<*>> = itemAsList(actions, ActionI::class.java, true)
    override fun actions(vararg value: ActionI<*>): B = apply { actions().addItems(value.asList()) }
    override fun action(value: ActionI<*>): ActionI<*> = applyAndReturn { actions().addItem(value); value }
    override fun action(value: ActionI<*>.() -> Unit): ActionI<*> = action(Action(value))

    override fun output(): ListMultiHolder<EventI<*>> = itemAsList(output, EventI::class.java, true)
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
        val on = "_on"
        val ifTrue = "_ifTrue"
        val ifFalse = "_ifFalse"
        val actions = "_actions"
        val output = "_output"
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

    override fun externalTypes(): ListMultiHolder<ExternalTypeI<*>> = itemAsList(externalTypes, ExternalTypeI::class.java, true)
    override fun externalTypes(vararg value: ExternalTypeI<*>): B = apply { externalTypes().addItems(value.asList()) }

    override fun fillSupportsItems() {
        externalTypes()
        super.fillSupportsItems()
    }

    companion object {
        val externalTypes = "_externalTypes"
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

    override fun isMultiResult(): Boolean = attr(multiResult, { true })
    override fun multiResult(value: Boolean): B = apply { attr(multiResult, value) }

    companion object {
        val multiResult = "_multiResult"
    }
}


open class Handler(adapt: Handler.() -> Unit = {}) : HandlerB<Handler>(adapt) {

    companion object {
        val EMPTY = Handler { name(ItemEmpty.name()) }.apply<Handler> { init() }
    }
}

open class HandlerB<B : HandlerB<B>>(adapt: B.() -> Unit = {}) : LogicUnitB<B>(adapt), HandlerI<B> {

    override fun on(): EventI<*> = attr(on, { Event.EMPTY })
    override fun on(value: EventI<*>): B = apply { attr(on, value) }

    override fun ifTrue(): ListMultiHolder<PredicateI<*>> = itemAsList(ifTrue, PredicateI::class.java, true)
    override fun ifTrue(vararg value: PredicateI<*>): B = apply { ifTrue().addItems(value.asList()) }
    override fun ifT(value: PredicateI<*>): PredicateI<*> = applyAndReturn { ifTrue().addItem(value); value }
    override fun ifT(value: PredicateI<*>.() -> Unit): PredicateI<*> = ifT(Predicate(value))

    override fun ifFalse(): ListMultiHolder<PredicateI<*>> = itemAsList(ifFalse, PredicateI::class.java, true)
    override fun ifFalse(vararg value: PredicateI<*>): B = apply { ifFalse().addItems(value.asList()) }
    override fun ifF(value: PredicateI<*>): PredicateI<*> = applyAndReturn { ifFalse().addItem(value); value }
    override fun ifF(value: PredicateI<*>.() -> Unit): PredicateI<*> = ifF(Predicate(value))

    override fun to(): StateI<*> = attr(to, { State.EMPTY })
    override fun to(value: StateI<*>): B = apply { attr(to, value) }

    override fun actions(): ListMultiHolder<ActionI<*>> = itemAsList(actions, ActionI::class.java, true)
    override fun actions(vararg value: ActionI<*>): B = apply { actions().addItems(value.asList()) }
    override fun action(value: ActionI<*>): ActionI<*> = applyAndReturn { actions().addItem(value); value }
    override fun action(value: ActionI<*>.() -> Unit): ActionI<*> = action(Action(value))

    override fun output(): ListMultiHolder<CommandI<*>> = itemAsList(output, CommandI::class.java, true)
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
        val on = "_on"
        val ifTrue = "_ifTrue"
        val ifFalse = "_ifFalse"
        val to = "_to"
        val actions = "_actions"
        val output = "_output"
    }
}


open class Model(adapt: Model.() -> Unit = {}) : ModelB<Model>(adapt) {

    companion object {
        val EMPTY = Model { name(ItemEmpty.name()) }.apply<Model> { init() }
    }
}

open class ModelB<B : ModelB<B>>(adapt: B.() -> Unit = {}) : StructureUnitB<B>(adapt), ModelI<B> {

    override fun models(): ListMultiHolder<ModelI<*>> = itemAsList(models, ModelI::class.java, true)
    override fun models(vararg value: ModelI<*>): B = apply { models().addItems(value.asList()) }

    override fun comps(): ListMultiHolder<CompI<*>> = itemAsList(comps, CompI::class.java, true)
    override fun comps(vararg value: CompI<*>): B = apply { comps().addItems(value.asList()) }

    override fun fillSupportsItems() {
        models()
        comps()
        super.fillSupportsItems()
    }

    companion object {
        val models = "_models"
        val comps = "_comps"
    }
}


open class Module(adapt: Module.() -> Unit = {}) : ModuleB<Module>(adapt) {

    companion object {
        val EMPTY = Module { name(ItemEmpty.name()) }.apply<Module> { init() }
    }
}

open class ModuleB<B : ModuleB<B>>(adapt: B.() -> Unit = {}) : StructureUnitB<B>(adapt), ModuleI<B> {

    override fun isParentNamespace(): Boolean = attr(parentNamespace, { false })
    override fun parentNamespace(value: Boolean): B = apply { attr(parentNamespace, value) }

    override fun dependencies(): ListMultiHolder<ModuleI<*>> = itemAsList(dependencies, ModuleI::class.java, true)
    override fun dependencies(vararg value: ModuleI<*>): B = apply { dependencies().addItems(value.asList()) }

    override fun entities(): ListMultiHolder<EntityI<*>> = itemAsList(entities, EntityI::class.java, true)
    override fun entities(vararg value: EntityI<*>): B = apply { entities().addItems(value.asList()) }
    override fun entity(value: EntityI<*>): EntityI<*> = applyAndReturn { entities().addItem(value); value }
    override fun entity(value: EntityI<*>.() -> Unit): EntityI<*> = entity(Entity(value))

    override fun enums(): ListMultiHolder<EnumTypeI<*>> = itemAsList(enums, EnumTypeI::class.java, true)
    override fun enums(vararg value: EnumTypeI<*>): B = apply { enums().addItems(value.asList()) }
    override fun enumType(value: EnumTypeI<*>): EnumTypeI<*> = applyAndReturn { enums().addItem(value); value }
    override fun enumType(value: EnumTypeI<*>.() -> Unit): EnumTypeI<*> = enumType(EnumType(value))

    override fun values(): ListMultiHolder<ValuesI<*>> = itemAsList(values, ValuesI::class.java, true)
    override fun values(vararg value: ValuesI<*>): B = apply { values().addItems(value.asList()) }
    override fun valueType(value: ValuesI<*>): ValuesI<*> = applyAndReturn { values().addItem(value); value }
    override fun valueType(value: ValuesI<*>.() -> Unit): ValuesI<*> = valueType(Values(value))

    override fun basics(): ListMultiHolder<BasicI<*>> = itemAsList(basics, BasicI::class.java, true)
    override fun basics(vararg value: BasicI<*>): B = apply { basics().addItems(value.asList()) }
    override fun basic(value: BasicI<*>): BasicI<*> = applyAndReturn { basics().addItem(value); value }
    override fun basic(value: BasicI<*>.() -> Unit): BasicI<*> = basic(Basic(value))

    override fun controllers(): ListMultiHolder<BusinessControllerI<*>> = itemAsList(controllers, BusinessControllerI::class.java, true)
    override fun controllers(vararg value: BusinessControllerI<*>): B = apply { controllers().addItems(value.asList()) }
    override fun controller(value: BusinessControllerI<*>): BusinessControllerI<*> = applyAndReturn { controllers().addItem(value); value }
    override fun controller(value: BusinessControllerI<*>.() -> Unit): BusinessControllerI<*> = controller(BusinessController(value))

    override fun processManagers(): ListMultiHolder<ProcessManagerI<*>> = itemAsList(processManagers, ProcessManagerI::class.java, true)
    override fun processManagers(vararg value: ProcessManagerI<*>): B = apply { processManagers().addItems(value.asList()) }
    override fun processManager(value: ProcessManagerI<*>): ProcessManagerI<*> = applyAndReturn { processManagers().addItem(value); value }
    override fun processManager(value: ProcessManagerI<*>.() -> Unit): ProcessManagerI<*> = processManager(ProcessManager(value))

    override fun projectors(): ListMultiHolder<ProjectorI<*>> = itemAsList(projectors, ProjectorI::class.java, true)
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
        val parentNamespace = "_parentNamespace"
        val dependencies = "_dependencies"
        val entities = "_entities"
        val enums = "_enums"
        val values = "_values"
        val basics = "_basics"
        val controllers = "_controllers"
        val processManagers = "_processManagers"
        val projectors = "_projectors"
    }
}


open class ModuleGroup(adapt: ModuleGroup.() -> Unit = {}) : ModuleGroupB<ModuleGroup>(adapt) {

    companion object {
        val EMPTY = ModuleGroup { name(ItemEmpty.name()) }.apply<ModuleGroup> { init() }
    }
}

open class ModuleGroupB<B : ModuleGroupB<B>>(adapt: B.() -> Unit = {}) : StructureUnitB<B>(adapt), ModuleGroupI<B> {

    override fun modules(): ListMultiHolder<ModuleI<*>> = itemAsList(modules, ModuleI::class.java, true)
    override fun modules(vararg value: ModuleI<*>): B = apply { modules().addItems(value.asList()) }

    override fun fillSupportsItems() {
        modules()
        super.fillSupportsItems()
    }

    companion object {
        val modules = "_modules"
    }
}


open class ProcessManager(adapt: ProcessManager.() -> Unit = {}) : ProcessManagerB<ProcessManager>(adapt) {

    companion object {
        val EMPTY = ProcessManager { name(ItemEmpty.name()) }.apply<ProcessManager> { init() }
    }
}

open class ProcessManagerB<B : ProcessManagerB<B>>(adapt: B.() -> Unit = {}) : StateMachineB<B>(adapt), ProcessManagerI<B> {
}


open class Projector(adapt: Projector.() -> Unit = {}) : ProjectorB<Projector>(adapt) {

    companion object {
        val EMPTY = Projector { name(ItemEmpty.name()) }.apply<Projector> { init() }
    }
}

open class ProjectorB<B : ProjectorB<B>>(adapt: B.() -> Unit = {}) : StateMachineB<B>(adapt), ProjectorI<B> {
}


open class State(adapt: State.() -> Unit = {}) : StateB<State>(adapt) {

    companion object {
        val EMPTY = State { name(ItemEmpty.name()) }.apply<State> { init() }
    }
}

open class StateB<B : StateB<B>>(adapt: B.() -> Unit = {}) : ControllerB<B>(adapt), StateI<B> {

    override fun timeout(): Long = attr(timeout, { 0L })
    override fun timeout(value: Long): B = apply { attr(timeout, value) }

    override fun entryActions(): ListMultiHolder<ActionI<*>> = itemAsList(entryActions, ActionI::class.java, true)
    override fun entryActions(vararg value: ActionI<*>): B = apply { entryActions().addItems(value.asList()) }
    override fun entry(value: ActionI<*>): ActionI<*> = applyAndReturn { entryActions().addItem(value); value }
    override fun entry(value: ActionI<*>.() -> Unit): ActionI<*> = entry(Action(value))

    override fun exitActions(): ListMultiHolder<ActionI<*>> = itemAsList(exitActions, ActionI::class.java, true)
    override fun exitActions(vararg value: ActionI<*>): B = apply { exitActions().addItems(value.asList()) }
    override fun exit(value: ActionI<*>): ActionI<*> = applyAndReturn { exitActions().addItem(value); value }
    override fun exit(value: ActionI<*>.() -> Unit): ActionI<*> = exit(Action(value))

    override fun executors(): ListMultiHolder<ExecutorI<*>> = itemAsList(executors, ExecutorI::class.java, true)
    override fun executors(vararg value: ExecutorI<*>): B = apply { executors().addItems(value.asList()) }
    override fun execute(value: ExecutorI<*>): ExecutorI<*> = applyAndReturn { executors().addItem(value); value }
    override fun execute(value: ExecutorI<*>.() -> Unit): ExecutorI<*> = execute(Executor(value))

    override fun handlers(): ListMultiHolder<HandlerI<*>> = itemAsList(handlers, HandlerI::class.java, true)
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
        val timeout = "_timeout"
        val entryActions = "_entryActions"
        val exitActions = "_exitActions"
        val executors = "_executors"
        val handlers = "_handlers"
    }
}


open class StateMachine(adapt: StateMachine.() -> Unit = {}) : StateMachineB<StateMachine>(adapt) {

    companion object {
        val EMPTY = StateMachine { name(ItemEmpty.name()) }.apply<StateMachine> { init() }
    }
}

open class StateMachineB<B : StateMachineB<B>>(adapt: B.() -> Unit = {}) : ControllerB<B>(adapt), StateMachineI<B> {

    override fun defaultState(): StateI<*> = attr(defaultState, { State.EMPTY })
    override fun defaultState(value: StateI<*>): B = apply { attr(defaultState, value) }

    override fun stateProp(): AttributeI<*> = attr(stateProp, { Attribute.EMPTY })
    override fun stateProp(value: AttributeI<*>): B = apply { attr(stateProp, value) }

    override fun timeoutProp(): AttributeI<*> = attr(timeoutProp, { Attribute.EMPTY })
    override fun timeoutProp(value: AttributeI<*>): B = apply { attr(timeoutProp, value) }

    override fun timeout(): Long = attr(timeout, { 0L })
    override fun timeout(value: Long): B = apply { attr(timeout, value) }

    override fun states(): ListMultiHolder<StateI<*>> = itemAsList(states, StateI::class.java, true)
    override fun states(vararg value: StateI<*>): B = apply { states().addItems(value.asList()) }
    override fun state(value: StateI<*>): StateI<*> = applyAndReturn { states().addItem(value); value }
    override fun state(value: StateI<*>.() -> Unit): StateI<*> = state(State(value))

    override fun checks(): ListMultiHolder<PredicateI<*>> = itemAsList(checks, PredicateI::class.java, true)
    override fun checks(vararg value: PredicateI<*>): B = apply { checks().addItems(value.asList()) }
    override fun check(value: PredicateI<*>): PredicateI<*> = applyAndReturn { checks().addItem(value); value }
    override fun check(value: PredicateI<*>.() -> Unit): PredicateI<*> = check(Predicate(value))

    override fun fillSupportsItems() {
        states()
        checks()
        super.fillSupportsItems()
    }

    companion object {
        val defaultState = "_defaultState"
        val stateProp = "_stateProp"
        val timeoutProp = "_timeoutProp"
        val timeout = "_timeout"
        val states = "_states"
        val checks = "_checks"
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


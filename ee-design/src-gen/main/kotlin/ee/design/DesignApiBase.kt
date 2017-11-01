package ee.design

import ee.lang.Attribute
import ee.lang.AttributeI
import ee.lang.CompilationUnit
import ee.lang.DataType
import ee.lang.DataTypeOperation
import ee.lang.EnumType
import ee.lang.EnumTypeI
import ee.lang.ExternalTypeI
import ee.lang.ItemEmpty
import ee.lang.ListMultiHolderI
import ee.lang.LogicUnit
import ee.lang.MacroComposite
import ee.lang.StructureUnit
import ee.lang.StructureUnitI


open class Basic : DataType, BasicI {
    constructor(value: Basic.() -> Unit = {}) : super(value as DataType.() -> Unit)

    companion object {
        val EMPTY = Basic { name(ItemEmpty.name()) }.apply<Basic> { init() }
    }
}


open class Bundle : StructureUnit, BundleI {
    constructor(value: Bundle.() -> Unit = {}) : super(value as StructureUnit.() -> Unit)

    override fun units(): ListMultiHolderI<StructureUnitI> = itemAsList(UNITS, StructureUnitI::class.java, true)
    override fun units(vararg value: StructureUnitI): BundleI = apply { units().addItems(value.asList()) }

    override fun fillSupportsItems() {
        units()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Bundle { name(ItemEmpty.name()) }.apply<Bundle> { init() }
        val UNITS = "_units"
    }
}


open class BussinesCommand : Command, BussinesCommandI {
    constructor(value: BussinesCommand.() -> Unit = {}) : super(value as Command.() -> Unit)

    companion object {
        val EMPTY = BussinesCommand { name(ItemEmpty.name()) }.apply<BussinesCommand> { init() }
    }
}


open class BussinesEvent : Event, BussinesEventI {
    constructor(value: BussinesEvent.() -> Unit = {}) : super(value as Event.() -> Unit)

    companion object {
        val EMPTY = BussinesEvent { name(ItemEmpty.name()) }.apply<BussinesEvent> { init() }
    }
}


open class Check : LogicUnit, CheckI {
    constructor(value: Check.() -> Unit = {}) : super(value as LogicUnit.() -> Unit)

    override fun cachedInContext(): Boolean = attr(CACHED_IN_CONTEXT, { false })
    override fun cachedInContext(value: Boolean): CheckI = apply { attr(CACHED_IN_CONTEXT, value) }

    companion object {
        val EMPTY = Check { name(ItemEmpty.name()) }.apply<Check> { init() }
        val CACHED_IN_CONTEXT = "_cachedInContext"
    }
}


open class Command : CompilationUnit, CommandI {
    constructor(value: Command.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    override fun affectMulti(): Boolean = attr(AFFECT_MULTI, { false })
    override fun affectMulti(value: Boolean): CommandI = apply { attr(AFFECT_MULTI, value) }

    override fun event(): EventI = attr(EVENT, { Event() })
    override fun event(value: EventI): CommandI = apply { attr(EVENT, value) }

    companion object {
        val EMPTY = Command { name(ItemEmpty.name()) }.apply<Command> { init() }
        val AFFECT_MULTI = "_affectMulti"
        val EVENT = "_event"
    }
}


open class Comp : ModuleGroup, CompI {
    constructor(value: Comp.() -> Unit = {}) : super(value as ModuleGroup.() -> Unit)

    override fun moduleGroups(): ListMultiHolderI<ModuleGroupI> = itemAsList(MODULE_GROUPS, ModuleGroupI::class.java, true)
    override fun moduleGroups(vararg value: ModuleGroupI): CompI = apply { moduleGroups().addItems(value.asList()) }

    override fun fillSupportsItems() {
        moduleGroups()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Comp { name(ItemEmpty.name()) }.apply<Comp> { init() }
        val MODULE_GROUPS = "_moduleGroups"
    }
}


open class CompositeCommand : CompilationUnit, CompositeCommandI {
    constructor(value: CompositeCommand.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    override fun commands(): ListMultiHolderI<CommandI> = itemAsList(COMMANDS, CommandI::class.java, true)
    override fun commands(vararg value: CommandI): CompositeCommandI = apply { commands().addItems(value.asList()) }

    override fun fillSupportsItems() {
        commands()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = CompositeCommand { name(ItemEmpty.name()) }.apply<CompositeCommand> { init() }
        val COMMANDS = "_commands"
    }
}


open class Controller : CompilationUnit, ControllerI {
    constructor(value: Controller.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    override fun enums(): ListMultiHolderI<EnumTypeI> = itemAsList(ENUMS, EnumTypeI::class.java, true)
    override fun enums(vararg value: EnumTypeI): ControllerI = apply { enums().addItems(value.asList()) }
    override fun enumType(value: EnumTypeI): EnumTypeI = applyAndReturn { enums().addItem(value); value }
    override fun enumType(value: EnumTypeI.() -> Unit): EnumTypeI = enumType(EnumType(value))

    override fun values(): ListMultiHolderI<ValuesI> = itemAsList(VALUES, ValuesI::class.java, true)
    override fun values(vararg value: ValuesI): ControllerI = apply { values().addItems(value.asList()) }
    override fun valueType(value: ValuesI): ValuesI = applyAndReturn { values().addItem(value); value }
    override fun valueType(value: ValuesI.() -> Unit): ValuesI = valueType(Values(value))

    override fun basics(): ListMultiHolderI<BasicI> = itemAsList(BASICS, BasicI::class.java, true)
    override fun basics(vararg value: BasicI): ControllerI = apply { basics().addItems(value.asList()) }
    override fun basic(value: BasicI): BasicI = applyAndReturn { basics().addItem(value); value }
    override fun basic(value: BasicI.() -> Unit): BasicI = basic(Basic(value))

    override fun fillSupportsItems() {
        enums()
        values()
        basics()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Controller { name(ItemEmpty.name()) }.apply<Controller> { init() }
        val ENUMS = "_enums"
        val VALUES = "_values"
        val BASICS = "_basics"
    }
}


open class CountBy : DataTypeOperation, CountByI {
    constructor(value: CountBy.() -> Unit = {}) : super(value as DataTypeOperation.() -> Unit)

    companion object {
        val EMPTY = CountBy { name(ItemEmpty.name()) }.apply<CountBy> { init() }
    }
}


open class CreateBy : Command, CreateByI {
    constructor(value: CreateBy.() -> Unit = {}) : super(value as Command.() -> Unit)

    companion object {
        val EMPTY = CreateBy { name(ItemEmpty.name()) }.apply<CreateBy> { init() }
    }
}


open class Created : Event, CreatedI {
    constructor(value: Created.() -> Unit = {}) : super(value as Event.() -> Unit)

    companion object {
        val EMPTY = Created { name(ItemEmpty.name()) }.apply<Created> { init() }
    }
}


open class DeleteBy : Command, DeleteByI {
    constructor(value: DeleteBy.() -> Unit = {}) : super(value as Command.() -> Unit)

    companion object {
        val EMPTY = DeleteBy { name(ItemEmpty.name()) }.apply<DeleteBy> { init() }
    }
}


open class Deleted : Event, DeletedI {
    constructor(value: Deleted.() -> Unit = {}) : super(value as Event.() -> Unit)

    companion object {
        val EMPTY = Deleted { name(ItemEmpty.name()) }.apply<Deleted> { init() }
    }
}


open class Entity : DataType, EntityI {
    constructor(value: Entity.() -> Unit = {}) : super(value as DataType.() -> Unit)

    override fun defaultEvents(): Boolean = attr(DEFAULT_EVENTS, { true })
    override fun defaultEvents(value: Boolean): EntityI = apply { attr(DEFAULT_EVENTS, value) }

    override fun defaultQueries(): Boolean = attr(DEFAULT_QUERIES, { true })
    override fun defaultQueries(value: Boolean): EntityI = apply { attr(DEFAULT_QUERIES, value) }

    override fun defaultCommands(): Boolean = attr(DEFAULT_COMMANDS, { true })
    override fun defaultCommands(value: Boolean): EntityI = apply { attr(DEFAULT_COMMANDS, value) }

    override fun belongsToAggregate(): EntityI = attr(BELONGS_TO_AGGREGATE, { Entity() })
    override fun belongsToAggregate(value: EntityI): EntityI = apply { attr(BELONGS_TO_AGGREGATE, value) }

    override fun aggregateFor(): ListMultiHolderI<EntityI> = itemAsList(AGGREGATE_FOR, EntityI::class.java, true)
    override fun aggregateFor(vararg value: EntityI): EntityI = apply { aggregateFor().addItems(value.asList()) }

    override fun controllers(): ListMultiHolderI<ControllerI> = itemAsList(CONTROLLERS, ControllerI::class.java, true)
    override fun controllers(vararg value: ControllerI): EntityI = apply { controllers().addItems(value.asList()) }
    override fun controller(value: ControllerI): ControllerI = applyAndReturn { controllers().addItem(value); value }
    override fun controller(value: ControllerI.() -> Unit): ControllerI = controller(Controller(value))

    override fun findBys(): ListMultiHolderI<FindByI> = itemAsList(FIND_BYS, FindByI::class.java, true)
    override fun findBys(vararg value: FindByI): EntityI = apply { findBys().addItems(value.asList()) }
    override fun findBy(value: FindByI): FindByI = applyAndReturn { findBys().addItem(value); value }
    override fun findBy(value: FindByI.() -> Unit): FindByI = findBy(FindBy(value))

    override fun countBys(): ListMultiHolderI<CountByI> = itemAsList(COUNT_BYS, CountByI::class.java, true)
    override fun countBys(vararg value: CountByI): EntityI = apply { countBys().addItems(value.asList()) }
    override fun countBy(value: CountByI): CountByI = applyAndReturn { countBys().addItem(value); value }
    override fun countBy(value: CountByI.() -> Unit): CountByI = countBy(CountBy(value))

    override fun existBys(): ListMultiHolderI<ExistByI> = itemAsList(EXIST_BYS, ExistByI::class.java, true)
    override fun existBys(vararg value: ExistByI): EntityI = apply { existBys().addItems(value.asList()) }
    override fun existBy(value: ExistByI): ExistByI = applyAndReturn { existBys().addItem(value); value }
    override fun existBy(value: ExistByI.() -> Unit): ExistByI = existBy(ExistBy(value))

    override fun commands(): ListMultiHolderI<BussinesCommandI> = itemAsList(COMMANDS, BussinesCommandI::class.java, true)
    override fun commands(vararg value: BussinesCommandI): EntityI = apply { commands().addItems(value.asList()) }
    override fun command(value: BussinesCommandI): BussinesCommandI = applyAndReturn { commands().addItem(value); value }
    override fun command(value: BussinesCommandI.() -> Unit): BussinesCommandI = command(BussinesCommand(value))

    override fun composites(): ListMultiHolderI<CompositeCommandI> = itemAsList(COMPOSITES, CompositeCommandI::class.java, true)
    override fun composites(vararg value: CompositeCommandI): EntityI = apply { composites().addItems(value.asList()) }
    override fun composite(value: CompositeCommandI): CompositeCommandI = applyAndReturn { composites().addItem(value); value }
    override fun composite(value: CompositeCommandI.() -> Unit): CompositeCommandI = composite(CompositeCommand(value))

    override fun createBys(): ListMultiHolderI<CreateByI> = itemAsList(CREATE_BYS, CreateByI::class.java, true)
    override fun createBys(vararg value: CreateByI): EntityI = apply { createBys().addItems(value.asList()) }
    override fun createBy(value: CreateByI): CreateByI = applyAndReturn { createBys().addItem(value); value }
    override fun createBy(value: CreateByI.() -> Unit): CreateByI = createBy(CreateBy(value))

    override fun updateBys(): ListMultiHolderI<UpdateByI> = itemAsList(UPDATE_BYS, UpdateByI::class.java, true)
    override fun updateBys(vararg value: UpdateByI): EntityI = apply { updateBys().addItems(value.asList()) }
    override fun updateBy(value: UpdateByI): UpdateByI = applyAndReturn { updateBys().addItem(value); value }
    override fun updateBy(value: UpdateByI.() -> Unit): UpdateByI = updateBy(UpdateBy(value))

    override fun deleteBys(): ListMultiHolderI<DeleteByI> = itemAsList(DELETE_BYS, DeleteByI::class.java, true)
    override fun deleteBys(vararg value: DeleteByI): EntityI = apply { deleteBys().addItems(value.asList()) }
    override fun deleteBy(value: DeleteByI): DeleteByI = applyAndReturn { deleteBys().addItem(value); value }
    override fun deleteBy(value: DeleteByI.() -> Unit): DeleteByI = deleteBy(DeleteBy(value))

    override fun events(): ListMultiHolderI<BussinesEventI> = itemAsList(EVENTS, BussinesEventI::class.java, true)
    override fun events(vararg value: BussinesEventI): EntityI = apply { events().addItems(value.asList()) }
    override fun event(value: BussinesEventI): BussinesEventI = applyAndReturn { events().addItem(value); value }
    override fun event(value: BussinesEventI.() -> Unit): BussinesEventI = event(BussinesEvent(value))

    override fun created(): ListMultiHolderI<CreatedI> = itemAsList(CREATED, CreatedI::class.java, true)
    override fun created(vararg value: CreatedI): EntityI = apply { created().addItems(value.asList()) }
    override fun created(value: CreatedI): CreatedI = applyAndReturn { created().addItem(value); value }
    override fun created(value: CreatedI.() -> Unit): CreatedI = created(Created(value))

    override fun updated(): ListMultiHolderI<UpdatedI> = itemAsList(UPDATED, UpdatedI::class.java, true)
    override fun updated(vararg value: UpdatedI): EntityI = apply { updated().addItems(value.asList()) }
    override fun updated(value: UpdatedI): UpdatedI = applyAndReturn { updated().addItem(value); value }
    override fun updated(value: UpdatedI.() -> Unit): UpdatedI = updated(Updated(value))

    override fun deleted(): ListMultiHolderI<DeletedI> = itemAsList(DELETED, DeletedI::class.java, true)
    override fun deleted(vararg value: DeletedI): EntityI = apply { deleted().addItems(value.asList()) }
    override fun deleted(value: DeletedI): DeletedI = applyAndReturn { deleted().addItem(value); value }
    override fun deleted(value: DeletedI.() -> Unit): DeletedI = deleted(Deleted(value))

    override fun stateMachines(): ListMultiHolderI<StateMachineI> = itemAsList(STATE_MACHINES, StateMachineI::class.java, true)
    override fun stateMachines(vararg value: StateMachineI): EntityI = apply { stateMachines().addItems(value.asList()) }
    override fun stateMachine(value: StateMachineI): StateMachineI = applyAndReturn { stateMachines().addItem(value); value }
    override fun stateMachine(value: StateMachineI.() -> Unit): StateMachineI = stateMachine(StateMachine(value))

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
        stateMachines()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Entity { name(ItemEmpty.name()) }.apply<Entity> { init() }
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
        val STATE_MACHINES = "_stateMachines"
    }
}


open class Event : CompilationUnit, EventI {
    constructor(value: Event.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    companion object {
        val EMPTY = Event { name(ItemEmpty.name()) }.apply<Event> { init() }
    }
}


open class ExistBy : DataTypeOperation, ExistByI {
    constructor(value: ExistBy.() -> Unit = {}) : super(value as DataTypeOperation.() -> Unit)

    companion object {
        val EMPTY = ExistBy { name(ItemEmpty.name()) }.apply<ExistBy> { init() }
    }
}


open class ExternalModule : Module, ExternalModuleI {
    constructor(value: ExternalModule.() -> Unit = {}) : super(value as Module.() -> Unit)

    override fun externalTypes(): ListMultiHolderI<ExternalTypeI> = itemAsList(EXTERNAL_TYPES, ExternalTypeI::class.java, true)
    override fun externalTypes(vararg value: ExternalTypeI): ExternalModuleI = apply { externalTypes().addItems(value.asList()) }

    override fun fillSupportsItems() {
        externalTypes()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = ExternalModule { name(ItemEmpty.name()) }.apply<ExternalModule> { init() }
        val EXTERNAL_TYPES = "_externalTypes"
    }
}


open class Facet : ModuleGroup, FacetI {
    constructor(value: Facet.() -> Unit = {}) : super(value as ModuleGroup.() -> Unit)

    companion object {
        val EMPTY = Facet { name(ItemEmpty.name()) }.apply<Facet> { init() }
    }
}


open class FindBy : DataTypeOperation, FindByI {
    constructor(value: FindBy.() -> Unit = {}) : super(value as DataTypeOperation.() -> Unit)

    override fun multiResult(): Boolean = attr(MULTI_RESULT, { true })
    override fun multiResult(value: Boolean): FindByI = apply { attr(MULTI_RESULT, value) }

    companion object {
        val EMPTY = FindBy { name(ItemEmpty.name()) }.apply<FindBy> { init() }
        val MULTI_RESULT = "_multiResult"
    }
}


open class Model : StructureUnit, ModelI {
    constructor(value: Model.() -> Unit = {}) : super(value as StructureUnit.() -> Unit)

    override fun models(): ListMultiHolderI<ModelI> = itemAsList(MODELS, ModelI::class.java, true)
    override fun models(vararg value: ModelI): ModelI = apply { models().addItems(value.asList()) }

    override fun comps(): ListMultiHolderI<CompI> = itemAsList(COMPS, CompI::class.java, true)
    override fun comps(vararg value: CompI): ModelI = apply { comps().addItems(value.asList()) }

    override fun fillSupportsItems() {
        models()
        comps()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Model { name(ItemEmpty.name()) }.apply<Model> { init() }
        val MODELS = "_models"
        val COMPS = "_comps"
    }
}


open class Module : StructureUnit, ModuleI {
    constructor(value: Module.() -> Unit = {}) : super(value as StructureUnit.() -> Unit)

    override fun parentNamespace(): Boolean = attr(PARENT_NAMESPACE, { false })
    override fun parentNamespace(value: Boolean): ModuleI = apply { attr(PARENT_NAMESPACE, value) }

    override fun dependencies(): ListMultiHolderI<ModuleI> = itemAsList(DEPENDENCIES, ModuleI::class.java, true)
    override fun dependencies(vararg value: ModuleI): ModuleI = apply { dependencies().addItems(value.asList()) }

    override fun entities(): ListMultiHolderI<EntityI> = itemAsList(ENTITIES, EntityI::class.java, true)
    override fun entities(vararg value: EntityI): ModuleI = apply { entities().addItems(value.asList()) }
    override fun entity(value: EntityI): EntityI = applyAndReturn { entities().addItem(value); value }
    override fun entity(value: EntityI.() -> Unit): EntityI = entity(Entity(value))

    override fun enums(): ListMultiHolderI<EnumTypeI> = itemAsList(ENUMS, EnumTypeI::class.java, true)
    override fun enums(vararg value: EnumTypeI): ModuleI = apply { enums().addItems(value.asList()) }
    override fun enumType(value: EnumTypeI): EnumTypeI = applyAndReturn { enums().addItem(value); value }
    override fun enumType(value: EnumTypeI.() -> Unit): EnumTypeI = enumType(EnumType(value))

    override fun values(): ListMultiHolderI<ValuesI> = itemAsList(VALUES, ValuesI::class.java, true)
    override fun values(vararg value: ValuesI): ModuleI = apply { values().addItems(value.asList()) }
    override fun valueType(value: ValuesI): ValuesI = applyAndReturn { values().addItem(value); value }
    override fun valueType(value: ValuesI.() -> Unit): ValuesI = valueType(Values(value))

    override fun basics(): ListMultiHolderI<BasicI> = itemAsList(BASICS, BasicI::class.java, true)
    override fun basics(vararg value: BasicI): ModuleI = apply { basics().addItems(value.asList()) }
    override fun basic(value: BasicI): BasicI = applyAndReturn { basics().addItem(value); value }
    override fun basic(value: BasicI.() -> Unit): BasicI = basic(Basic(value))

    override fun controllers(): ListMultiHolderI<ControllerI> = itemAsList(CONTROLLERS, ControllerI::class.java, true)
    override fun controllers(vararg value: ControllerI): ModuleI = apply { controllers().addItems(value.asList()) }
    override fun controller(value: ControllerI): ControllerI = applyAndReturn { controllers().addItem(value); value }
    override fun controller(value: ControllerI.() -> Unit): ControllerI = controller(Controller(value))

    override fun fillSupportsItems() {
        dependencies()
        entities()
        enums()
        values()
        basics()
        controllers()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Module { name(ItemEmpty.name()) }.apply<Module> { init() }
        val PARENT_NAMESPACE = "_parentNamespace"
        val DEPENDENCIES = "_dependencies"
        val ENTITIES = "_entities"
        val ENUMS = "_enums"
        val VALUES = "_values"
        val BASICS = "_basics"
        val CONTROLLERS = "_controllers"
    }
}


open class ModuleGroup : StructureUnit, ModuleGroupI {
    constructor(value: ModuleGroup.() -> Unit = {}) : super(value as StructureUnit.() -> Unit)

    override fun modules(): ListMultiHolderI<ModuleI> = itemAsList(MODULES, ModuleI::class.java, true)
    override fun modules(vararg value: ModuleI): ModuleGroupI = apply { modules().addItems(value.asList()) }

    override fun fillSupportsItems() {
        modules()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = ModuleGroup { name(ItemEmpty.name()) }.apply<ModuleGroup> { init() }
        val MODULES = "_modules"
    }
}


open class State : Controller, StateI {
    constructor(value: State.() -> Unit = {}) : super(value as Controller.() -> Unit)

    override fun timeout(): Long = attr(TIMEOUT, { 0L })
    override fun timeout(value: Long): StateI = apply { attr(TIMEOUT, value) }

    override fun entryCommands(): ListMultiHolderI<CommandI> = itemAsList(ENTRY_COMMANDS, CommandI::class.java, true)
    override fun entryCommands(vararg value: CommandI): StateI = apply { entryCommands().addItems(value.asList()) }
    override fun entry(value: CommandI): CommandI = applyAndReturn { entryCommands().addItem(value); value }
    override fun entry(value: CommandI.() -> Unit): CommandI = entry(Command(value))

    override fun exitCommands(): ListMultiHolderI<CommandI> = itemAsList(EXIT_COMMANDS, CommandI::class.java, true)
    override fun exitCommands(vararg value: CommandI): StateI = apply { exitCommands().addItems(value.asList()) }
    override fun exit(value: CommandI): CommandI = applyAndReturn { exitCommands().addItem(value); value }
    override fun exit(value: CommandI.() -> Unit): CommandI = exit(Command(value))

    override fun transitions(): ListMultiHolderI<TransitionI> = itemAsList(TRANSITIONS, TransitionI::class.java, true)
    override fun transitions(vararg value: TransitionI): StateI = apply { transitions().addItems(value.asList()) }
    override fun on(value: TransitionI): TransitionI = applyAndReturn { transitions().addItem(value); value }
    override fun on(value: TransitionI.() -> Unit): TransitionI = on(Transition(value))

    override fun fillSupportsItems() {
        entryCommands()
        exitCommands()
        transitions()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = State { name(ItemEmpty.name()) }.apply<State> { init() }
        val TIMEOUT = "_timeout"
        val ENTRY_COMMANDS = "_entryCommands"
        val EXIT_COMMANDS = "_exitCommands"
        val TRANSITIONS = "_transitions"
    }
}


open class StateMachine : Controller, StateMachineI {
    constructor(value: StateMachine.() -> Unit = {}) : super(value as Controller.() -> Unit)

    override fun timeout(): Long = attr(TIMEOUT, { 0L })
    override fun timeout(value: Long): StateMachineI = apply { attr(TIMEOUT, value) }

    override fun stateProp(): AttributeI = attr(STATE_PROP, { Attribute() })
    override fun stateProp(value: AttributeI): StateMachineI = apply { attr(STATE_PROP, value) }

    override fun timeoutProp(): AttributeI = attr(TIMEOUT_PROP, { Attribute() })
    override fun timeoutProp(value: AttributeI): StateMachineI = apply { attr(TIMEOUT_PROP, value) }

    override fun states(): ListMultiHolderI<StateI> = itemAsList(STATES, StateI::class.java, true)
    override fun states(vararg value: StateI): StateMachineI = apply { states().addItems(value.asList()) }
    override fun to(value: StateI): StateI = applyAndReturn { states().addItem(value); value }
    override fun to(value: StateI.() -> Unit): StateI = to(State(value))

    override fun conditions(): ListMultiHolderI<CheckI> = itemAsList(CONDITIONS, CheckI::class.java, true)
    override fun conditions(vararg value: CheckI): StateMachineI = apply { conditions().addItems(value.asList()) }
    override fun cond(value: CheckI): CheckI = applyAndReturn { conditions().addItem(value); value }
    override fun cond(value: CheckI.() -> Unit): CheckI = cond(Check(value))

    override fun fillSupportsItems() {
        states()
        conditions()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = StateMachine { name(ItemEmpty.name()) }.apply<StateMachine> { init() }
        val TIMEOUT = "_timeout"
        val STATE_PROP = "_stateProp"
        val TIMEOUT_PROP = "_timeoutProp"
        val STATES = "_states"
        val CONDITIONS = "_conditions"
    }
}


open class Transition : MacroComposite, TransitionI {
    constructor(value: Transition.() -> Unit = {}) : super(value as MacroComposite.() -> Unit)

    override fun event(): EventI = attr(EVENT, { Event() })
    override fun event(value: EventI): TransitionI = apply { attr(EVENT, value) }

    override fun redirect(): EventI = attr(REDIRECT, { Event() })
    override fun redirect(value: EventI): TransitionI = apply { attr(REDIRECT, value) }

    override fun to(): StateI = attr(TO, { State() })
    override fun to(value: StateI): TransitionI = apply { attr(TO, value) }

    override fun checks(): ListMultiHolderI<CheckI> = itemAsList(CHECKS, CheckI::class.java, true)
    override fun checks(vararg value: CheckI): TransitionI = apply { checks().addItems(value.asList()) }
    override fun check(value: CheckI): CheckI = applyAndReturn { checks().addItem(value); value }
    override fun check(value: CheckI.() -> Unit): CheckI = check(Check(value))

    override fun notChecks(): ListMultiHolderI<CheckI> = itemAsList(NOT_CHECKS, CheckI::class.java, true)
    override fun notChecks(vararg value: CheckI): TransitionI = apply { notChecks().addItems(value.asList()) }
    override fun checkNot(value: CheckI): CheckI = applyAndReturn { notChecks().addItem(value); value }
    override fun checkNot(value: CheckI.() -> Unit): CheckI = checkNot(Check(value))

    override fun fillSupportsItems() {
        checks()
        notChecks()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Transition { name(ItemEmpty.name()) }.apply<Transition> { init() }
        val EVENT = "_event"
        val REDIRECT = "_redirect"
        val TO = "_to"
        val CHECKS = "_checks"
        val NOT_CHECKS = "_notChecks"
    }
}


open class UpdateBy : Command, UpdateByI {
    constructor(value: UpdateBy.() -> Unit = {}) : super(value as Command.() -> Unit)

    companion object {
        val EMPTY = UpdateBy { name(ItemEmpty.name()) }.apply<UpdateBy> { init() }
    }
}


open class Updated : Event, UpdatedI {
    constructor(value: Updated.() -> Unit = {}) : super(value as Event.() -> Unit)

    companion object {
        val EMPTY = Updated { name(ItemEmpty.name()) }.apply<Updated> { init() }
    }
}


open class Values : DataType, ValuesI {
    constructor(value: Values.() -> Unit = {}) : super(value as DataType.() -> Unit)

    companion object {
        val EMPTY = Values { name(ItemEmpty.name()) }.apply<Values> { init() }
    }
}


open class Widget : CompilationUnit, WidgetI {
    constructor(value: Widget.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    companion object {
        val EMPTY = Widget { name(ItemEmpty.name()) }.apply<Widget> { init() }
    }
}


package ee.design

import ee.lang.CompilationUnit
import ee.lang.DataType
import ee.lang.DataTypeOperation
import ee.lang.EnumType
import ee.lang.EnumTypeI
import ee.lang.ExternalTypeI
import ee.lang.ListMultiHolderI
import ee.lang.StructureUnit
import ee.lang.StructureUnitI


open class Basic : DataType, BasicI {
    constructor(value: Basic.() -> Unit = {}) : super(value as DataType.() -> Unit)

    companion object {
        val EMPTY = Basic()
    }
}


open class Bundle : StructureUnit, BundleI {
    constructor(value: Bundle.() -> Unit = {}) : super(value as StructureUnit.() -> Unit)

    override fun units(): ListMultiHolderI<StructureUnitI> = itemAsList(UNITS, StructureUnitI::class.java, true, true)
    override fun units(vararg value: StructureUnitI): BundleI = apply { units().addItems(value.asList()) }

    override fun fillSupportsItems() {
        units()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Bundle()
        val UNITS = "_units"
    }
}


open class BussinesCommand : Command, BussinesCommandI {
    constructor(value: BussinesCommand.() -> Unit = {}) : super(value as Command.() -> Unit)

    companion object {
        val EMPTY = BussinesCommand()
    }
}


open class BussinesEvent : Event, BussinesEventI {
    constructor(value: BussinesEvent.() -> Unit = {}) : super(value as Event.() -> Unit)

    companion object {
        val EMPTY = BussinesEvent()
    }
}


open class Command : CompilationUnit, CommandI {
    constructor(value: Command.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    companion object {
        val EMPTY = Command()
    }
}


open class Commands : Controller, CommandsI {
    constructor(value: Commands.() -> Unit = {}) : super(value as Controller.() -> Unit)

    override fun commands(): ListMultiHolderI<BussinesCommandI> = itemAsList(COMMANDS, BussinesCommandI::class.java, true, true)
    override fun commands(vararg value: BussinesCommandI): CommandsI = apply { commands().addItems(value.asList()) }
    override fun command(value: BussinesCommandI): BussinesCommandI = applyAndReturn { commands().addItem(value); value }
    override fun command(value: BussinesCommandI.() -> Unit) : BussinesCommandI = command(BussinesCommand(value))

    override fun composites(): ListMultiHolderI<CompositeCommandI> = itemAsList(COMPOSITES, CompositeCommandI::class.java, true, true)
    override fun composites(vararg value: CompositeCommandI): CommandsI = apply { composites().addItems(value.asList()) }
    override fun composite(value: CompositeCommandI): CompositeCommandI = applyAndReturn { composites().addItem(value); value }
    override fun composite(value: CompositeCommandI.() -> Unit) : CompositeCommandI = composite(CompositeCommand(value))

    override fun createBys(): ListMultiHolderI<CreateByI> = itemAsList(CREATE_BYS, CreateByI::class.java, true, true)
    override fun createBys(vararg value: CreateByI): CommandsI = apply { createBys().addItems(value.asList()) }
    override fun createBy(value: CreateByI): CreateByI = applyAndReturn { createBys().addItem(value); value }
    override fun createBy(value: CreateByI.() -> Unit) : CreateByI = createBy(CreateBy(value))

    override fun updateBys(): ListMultiHolderI<UpdateByI> = itemAsList(UPDATE_BYS, UpdateByI::class.java, true, true)
    override fun updateBys(vararg value: UpdateByI): CommandsI = apply { updateBys().addItems(value.asList()) }
    override fun updateBy(value: UpdateByI): UpdateByI = applyAndReturn { updateBys().addItem(value); value }
    override fun updateBy(value: UpdateByI.() -> Unit) : UpdateByI = updateBy(UpdateBy(value))

    override fun deleteBys(): ListMultiHolderI<DeleteByI> = itemAsList(DELETE_BYS, DeleteByI::class.java, true, true)
    override fun deleteBys(vararg value: DeleteByI): CommandsI = apply { deleteBys().addItems(value.asList()) }
    override fun deleteBy(value: DeleteByI): DeleteByI = applyAndReturn { deleteBys().addItem(value); value }
    override fun deleteBy(value: DeleteByI.() -> Unit) : DeleteByI = deleteBy(DeleteBy(value))

    override fun fillSupportsItems() {
        commands()
        composites()
        createBys()
        updateBys()
        deleteBys()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Commands()
        val COMMANDS = "_commands"
        val COMPOSITES = "_composites"
        val CREATE_BYS = "_createBys"
        val UPDATE_BYS = "_updateBys"
        val DELETE_BYS = "_deleteBys"
    }
}


open class Comp : ModuleGroup, CompI {
    constructor(value: Comp.() -> Unit = {}) : super(value as ModuleGroup.() -> Unit)

    override fun moduleGroups(): ListMultiHolderI<ModuleGroupI> = itemAsList(MODULE_GROUPS, ModuleGroupI::class.java, true, true)
    override fun moduleGroups(vararg value: ModuleGroupI): CompI = apply { moduleGroups().addItems(value.asList()) }

    override fun fillSupportsItems() {
        moduleGroups()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Comp()
        val MODULE_GROUPS = "_moduleGroups"
    }
}


open class CompositeCommand : CompilationUnit, CompositeCommandI {
    constructor(value: CompositeCommand.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    override fun commands(): ListMultiHolderI<CommandI> = itemAsList(COMMANDS, CommandI::class.java, true, true)
    override fun commands(vararg value: CommandI): CompositeCommandI = apply { commands().addItems(value.asList()) }

    override fun fillSupportsItems() {
        commands()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = CompositeCommand()
        val COMMANDS = "_commands"
    }
}


open class Controller : CompilationUnit, ControllerI {
    constructor(value: Controller.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    override fun enums(): ListMultiHolderI<EnumTypeI> = itemAsList(ENUMS, EnumTypeI::class.java, true, true)
    override fun enums(vararg value: EnumTypeI): ControllerI = apply { enums().addItems(value.asList()) }
    override fun enumType(value: EnumTypeI): EnumTypeI = applyAndReturn { enums().addItem(value); value }
    override fun enumType(value: EnumTypeI.() -> Unit) : EnumTypeI = enumType(EnumType(value))

    override fun values(): ListMultiHolderI<ValuesI> = itemAsList(VALUES, ValuesI::class.java, true, true)
    override fun values(vararg value: ValuesI): ControllerI = apply { values().addItems(value.asList()) }
    override fun valueType(value: ValuesI): ValuesI = applyAndReturn { values().addItem(value); value }
    override fun valueType(value: ValuesI.() -> Unit) : ValuesI = valueType(Values(value))

    override fun basics(): ListMultiHolderI<BasicI> = itemAsList(BASICS, BasicI::class.java, true, true)
    override fun basics(vararg value: BasicI): ControllerI = apply { basics().addItems(value.asList()) }
    override fun basic(value: BasicI): BasicI = applyAndReturn { basics().addItem(value); value }
    override fun basic(value: BasicI.() -> Unit) : BasicI = basic(Basic(value))

    override fun fillSupportsItems() {
        enums()
        values()
        basics()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Controller()
        val ENUMS = "_enums"
        val VALUES = "_values"
        val BASICS = "_basics"
    }
}


open class CountBy : DataTypeOperation, CountByI {
    constructor(value: CountBy.() -> Unit = {}) : super(value as DataTypeOperation.() -> Unit)

    companion object {
        val EMPTY = CountBy()
    }
}


open class CreateBy : Command, CreateByI {
    constructor(value: CreateBy.() -> Unit = {}) : super(value as Command.() -> Unit)

    companion object {
        val EMPTY = CreateBy()
    }
}


open class Created : Event, CreatedI {
    constructor(value: Created.() -> Unit = {}) : super(value as Event.() -> Unit)

    companion object {
        val EMPTY = Created()
    }
}


open class DeleteBy : Command, DeleteByI {
    constructor(value: DeleteBy.() -> Unit = {}) : super(value as Command.() -> Unit)

    companion object {
        val EMPTY = DeleteBy()
    }
}


open class Deleted : Event, DeletedI {
    constructor(value: Deleted.() -> Unit = {}) : super(value as Event.() -> Unit)

    companion object {
        val EMPTY = Deleted()
    }
}


open class Entity : DataType, EntityI {
    constructor(value: Entity.() -> Unit = {}) : super(value as DataType.() -> Unit)

    override fun belongsToAggregate(): EntityI = attr(BELONGS_TO_AGGREGATE, { Entity() })
    override fun belongsToAggregate(value: EntityI): EntityI = apply { attr(BELONGS_TO_AGGREGATE, value) }

    override fun aggregateFor(): ListMultiHolderI<EntityI> = itemAsList(AGGREGATE_FOR, EntityI::class.java, true, true)
    override fun aggregateFor(vararg value: EntityI): EntityI = apply { aggregateFor().addItems(value.asList()) }

    override fun controllers(): ListMultiHolderI<ControllerI> = itemAsList(CONTROLLERS, ControllerI::class.java, true, true)
    override fun controllers(vararg value: ControllerI): EntityI = apply { controllers().addItems(value.asList()) }
    override fun controller(value: ControllerI): ControllerI = applyAndReturn { controllers().addItem(value); value }
    override fun controller(value: ControllerI.() -> Unit) : ControllerI = controller(Controller(value))

    override fun commands(): ListMultiHolderI<CommandsI> = itemAsList(COMMANDS, CommandsI::class.java, true, true)
    override fun commands(vararg value: CommandsI): EntityI = apply { commands().addItems(value.asList()) }
    override fun command(value: CommandsI): CommandsI = applyAndReturn { commands().addItem(value); value }
    override fun command(value: CommandsI.() -> Unit) : CommandsI = command(Commands(value))

    override fun queries(): ListMultiHolderI<QueriesI> = itemAsList(QUERIES, QueriesI::class.java, true, true)
    override fun queries(vararg value: QueriesI): EntityI = apply { queries().addItems(value.asList()) }
    override fun query(value: QueriesI): QueriesI = applyAndReturn { queries().addItem(value); value }
    override fun query(value: QueriesI.() -> Unit) : QueriesI = query(Queries(value))

    override fun events(): ListMultiHolderI<EventsI> = itemAsList(EVENTS, EventsI::class.java, true, true)
    override fun events(vararg value: EventsI): EntityI = apply { events().addItems(value.asList()) }
    override fun event(value: EventsI): EventsI = applyAndReturn { events().addItem(value); value }
    override fun event(value: EventsI.() -> Unit) : EventsI = event(Events(value))

    override fun fillSupportsItems() {
        aggregateFor()
        controllers()
        commands()
        queries()
        events()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Entity()
        val BELONGS_TO_AGGREGATE = "_belongsToAggregate"
        val AGGREGATE_FOR = "_aggregateFor"
        val CONTROLLERS = "_controllers"
        val COMMANDS = "_commands"
        val QUERIES = "_queries"
        val EVENTS = "_events"
    }
}


open class Event : CompilationUnit, EventI {
    constructor(value: Event.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    companion object {
        val EMPTY = Event()
    }
}


open class Events : Controller, EventsI {
    constructor(value: Events.() -> Unit = {}) : super(value as Controller.() -> Unit)

    override fun events(): ListMultiHolderI<BussinesEventI> = itemAsList(EVENTS, BussinesEventI::class.java, true, true)
    override fun events(vararg value: BussinesEventI): EventsI = apply { events().addItems(value.asList()) }
    override fun event(value: BussinesEventI): BussinesEventI = applyAndReturn { events().addItem(value); value }
    override fun event(value: BussinesEventI.() -> Unit) : BussinesEventI = event(BussinesEvent(value))

    override fun created(): ListMultiHolderI<CreatedI> = itemAsList(CREATED, CreatedI::class.java, true, true)
    override fun created(vararg value: CreatedI): EventsI = apply { created().addItems(value.asList()) }
    override fun created(value: CreatedI): CreatedI = applyAndReturn { created().addItem(value); value }
    override fun created(value: CreatedI.() -> Unit) : CreatedI = created(Created(value))

    override fun updated(): ListMultiHolderI<UpdatedI> = itemAsList(UPDATED, UpdatedI::class.java, true, true)
    override fun updated(vararg value: UpdatedI): EventsI = apply { updated().addItems(value.asList()) }
    override fun updated(value: UpdatedI): UpdatedI = applyAndReturn { updated().addItem(value); value }
    override fun updated(value: UpdatedI.() -> Unit) : UpdatedI = updated(Updated(value))

    override fun deleted(): ListMultiHolderI<DeletedI> = itemAsList(DELETED, DeletedI::class.java, true, true)
    override fun deleted(vararg value: DeletedI): EventsI = apply { deleted().addItems(value.asList()) }
    override fun deleted(value: DeletedI): DeletedI = applyAndReturn { deleted().addItem(value); value }
    override fun deleted(value: DeletedI.() -> Unit) : DeletedI = deleted(Deleted(value))

    override fun fillSupportsItems() {
        events()
        created()
        updated()
        deleted()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Events()
        val EVENTS = "_events"
        val CREATED = "_created"
        val UPDATED = "_updated"
        val DELETED = "_deleted"
    }
}


open class ExistBy : DataTypeOperation, ExistByI {
    constructor(value: ExistBy.() -> Unit = {}) : super(value as DataTypeOperation.() -> Unit)

    companion object {
        val EMPTY = ExistBy()
    }
}


open class ExternalModule : Module, ExternalModuleI {
    constructor(value: ExternalModule.() -> Unit = {}) : super(value as Module.() -> Unit)

    override fun externalTypes(): ListMultiHolderI<ExternalTypeI> = itemAsList(EXTERNAL_TYPES, ExternalTypeI::class.java, true, true)
    override fun externalTypes(vararg value: ExternalTypeI): ExternalModuleI = apply { externalTypes().addItems(value.asList()) }

    override fun fillSupportsItems() {
        externalTypes()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = ExternalModule()
        val EXTERNAL_TYPES = "_externalTypes"
    }
}


open class Facet : ModuleGroup, FacetI {
    constructor(value: Facet.() -> Unit = {}) : super(value as ModuleGroup.() -> Unit)

    companion object {
        val EMPTY = Facet()
    }
}


open class FindBy : DataTypeOperation, FindByI {
    constructor(value: FindBy.() -> Unit = {}) : super(value as DataTypeOperation.() -> Unit)

    companion object {
        val EMPTY = FindBy()
    }
}


open class Model : StructureUnit, ModelI {
    constructor(value: Model.() -> Unit = {}) : super(value as StructureUnit.() -> Unit)

    override fun models(): ListMultiHolderI<ModelI> = itemAsList(MODELS, ModelI::class.java, true, true)
    override fun models(vararg value: ModelI): ModelI = apply { models().addItems(value.asList()) }

    override fun comps(): ListMultiHolderI<CompI> = itemAsList(COMPS, CompI::class.java, true, true)
    override fun comps(vararg value: CompI): ModelI = apply { comps().addItems(value.asList()) }

    override fun fillSupportsItems() {
        models()
        comps()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Model()
        val MODELS = "_models"
        val COMPS = "_comps"
    }
}


open class Module : StructureUnit, ModuleI {
    constructor(value: Module.() -> Unit = {}) : super(value as StructureUnit.() -> Unit)

    override fun parentNamespace(): Boolean = attr(PARENT_NAMESPACE, { false })
    override fun parentNamespace(value: Boolean): ModuleI = apply { attr(PARENT_NAMESPACE, value) }

    override fun dependencies(): ListMultiHolderI<ModuleI> = itemAsList(DEPENDENCIES, ModuleI::class.java, true, true)
    override fun dependencies(vararg value: ModuleI): ModuleI = apply { dependencies().addItems(value.asList()) }

    override fun entities(): ListMultiHolderI<EntityI> = itemAsList(ENTITIES, EntityI::class.java, true, true)
    override fun entities(vararg value: EntityI): ModuleI = apply { entities().addItems(value.asList()) }
    override fun entity(value: EntityI): EntityI = applyAndReturn { entities().addItem(value); value }
    override fun entity(value: EntityI.() -> Unit) : EntityI = entity(Entity(value))

    override fun enums(): ListMultiHolderI<EnumTypeI> = itemAsList(ENUMS, EnumTypeI::class.java, true, true)
    override fun enums(vararg value: EnumTypeI): ModuleI = apply { enums().addItems(value.asList()) }
    override fun enumType(value: EnumTypeI): EnumTypeI = applyAndReturn { enums().addItem(value); value }
    override fun enumType(value: EnumTypeI.() -> Unit) : EnumTypeI = enumType(EnumType(value))

    override fun values(): ListMultiHolderI<ValuesI> = itemAsList(VALUES, ValuesI::class.java, true, true)
    override fun values(vararg value: ValuesI): ModuleI = apply { values().addItems(value.asList()) }
    override fun valueType(value: ValuesI): ValuesI = applyAndReturn { values().addItem(value); value }
    override fun valueType(value: ValuesI.() -> Unit) : ValuesI = valueType(Values(value))

    override fun basics(): ListMultiHolderI<BasicI> = itemAsList(BASICS, BasicI::class.java, true, true)
    override fun basics(vararg value: BasicI): ModuleI = apply { basics().addItems(value.asList()) }
    override fun basic(value: BasicI): BasicI = applyAndReturn { basics().addItem(value); value }
    override fun basic(value: BasicI.() -> Unit) : BasicI = basic(Basic(value))

    override fun controllers(): ListMultiHolderI<ControllerI> = itemAsList(CONTROLLERS, ControllerI::class.java, true, true)
    override fun controllers(vararg value: ControllerI): ModuleI = apply { controllers().addItems(value.asList()) }
    override fun controller(value: ControllerI): ControllerI = applyAndReturn { controllers().addItem(value); value }
    override fun controller(value: ControllerI.() -> Unit) : ControllerI = controller(Controller(value))

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
        val EMPTY = Module()
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

    override fun modules(): ListMultiHolderI<ModuleI> = itemAsList(MODULES, ModuleI::class.java, true, true)
    override fun modules(vararg value: ModuleI): ModuleGroupI = apply { modules().addItems(value.asList()) }

    override fun fillSupportsItems() {
        modules()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = ModuleGroup()
        val MODULES = "_modules"
    }
}


open class Queries : Controller, QueriesI {
    constructor(value: Queries.() -> Unit = {}) : super(value as Controller.() -> Unit)

    override fun findBys(): ListMultiHolderI<FindByI> = itemAsList(FIND_BYS, FindByI::class.java, true, true)
    override fun findBys(vararg value: FindByI): QueriesI = apply { findBys().addItems(value.asList()) }
    override fun findBy(value: FindByI): FindByI = applyAndReturn { findBys().addItem(value); value }
    override fun findBy(value: FindByI.() -> Unit) : FindByI = findBy(FindBy(value))

    override fun countBys(): ListMultiHolderI<CountByI> = itemAsList(COUNT_BYS, CountByI::class.java, true, true)
    override fun countBys(vararg value: CountByI): QueriesI = apply { countBys().addItems(value.asList()) }
    override fun countBy(value: CountByI): CountByI = applyAndReturn { countBys().addItem(value); value }
    override fun countBy(value: CountByI.() -> Unit) : CountByI = countBy(CountBy(value))

    override fun existBys(): ListMultiHolderI<ExistByI> = itemAsList(EXIST_BYS, ExistByI::class.java, true, true)
    override fun existBys(vararg value: ExistByI): QueriesI = apply { existBys().addItems(value.asList()) }
    override fun existBy(value: ExistByI): ExistByI = applyAndReturn { existBys().addItem(value); value }
    override fun existBy(value: ExistByI.() -> Unit) : ExistByI = existBy(ExistBy(value))

    override fun fillSupportsItems() {
        findBys()
        countBys()
        existBys()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Queries()
        val FIND_BYS = "_findBys"
        val COUNT_BYS = "_countBys"
        val EXIST_BYS = "_existBys"
    }
}


open class UpdateBy : Command, UpdateByI {
    constructor(value: UpdateBy.() -> Unit = {}) : super(value as Command.() -> Unit)

    companion object {
        val EMPTY = UpdateBy()
    }
}


open class Updated : Event, UpdatedI {
    constructor(value: Updated.() -> Unit = {}) : super(value as Event.() -> Unit)

    companion object {
        val EMPTY = Updated()
    }
}


open class Values : DataType, ValuesI {
    constructor(value: Values.() -> Unit = {}) : super(value as DataType.() -> Unit)

    companion object {
        val EMPTY = Values()
    }
}


open class Widget : CompilationUnit, WidgetI {
    constructor(value: Widget.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    companion object {
        val EMPTY = Widget()
    }
}


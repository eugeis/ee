package ee.design

import ee.lang.Attribute
import ee.lang.AttributeI
import ee.lang.CompilationUnit
import ee.lang.DataType
import ee.lang.DataTypeOperation
import ee.lang.EnumTypeI
import ee.lang.ExternalTypeI
import ee.lang.ListMultiHolderI
import ee.lang.OperationI
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

    override fun units(): ListMultiHolderI<StructureUnitI> = itemAsList(UNITS, StructureUnitI::class.java)
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


open class Command : DataTypeOperation, CommandI {
    constructor(value: Command.() -> Unit = {}) : super(value as DataTypeOperation.() -> Unit)

    companion object {
        val EMPTY = Command()
    }
}


open class CommandController : Controller, CommandControllerI {
    constructor(value: CommandController.() -> Unit = {}) : super(value as Controller.() -> Unit)

    override fun commands(): ListMultiHolderI<BussinesCommandI> = itemAsList(COMMANDS, BussinesCommandI::class.java)
    override fun commands(vararg value: BussinesCommandI): CommandControllerI = apply { commands().addItems(value.asList()) }
    override fun command(value: BussinesCommandI): BussinesCommandI = applyAndReturn { commands().add(value); value }
    override fun command(value: BussinesCommandI.() -> Unit) : BussinesCommandI = command(BussinesCommand(value))

    override fun composites(): ListMultiHolderI<CompositeCommandI> = itemAsList(COMPOSITES, CompositeCommandI::class.java)
    override fun composites(vararg value: CompositeCommandI): CommandControllerI = apply { composites().addItems(value.asList()) }
    override fun composite(value: CompositeCommandI): CompositeCommandI = applyAndReturn { composites().add(value); value }
    override fun composite(value: CompositeCommandI.() -> Unit) : CompositeCommandI = composite(CompositeCommand(value))

    override fun createBys(): ListMultiHolderI<CreateByI> = itemAsList(CREATE_BYS, CreateByI::class.java)
    override fun createBys(vararg value: CreateByI): CommandControllerI = apply { createBys().addItems(value.asList()) }
    override fun createBy(value: CreateByI): CreateByI = applyAndReturn { createBys().add(value); value }
    override fun createBy(value: CreateByI.() -> Unit) : CreateByI = createBy(CreateBy(value))

    override fun updateBys(): ListMultiHolderI<UpdateByI> = itemAsList(UPDATE_BYS, UpdateByI::class.java)
    override fun updateBys(vararg value: UpdateByI): CommandControllerI = apply { updateBys().addItems(value.asList()) }
    override fun updateBy(value: UpdateByI): UpdateByI = applyAndReturn { updateBys().add(value); value }
    override fun updateBy(value: UpdateByI.() -> Unit) : UpdateByI = updateBy(UpdateBy(value))

    override fun deleteBys(): ListMultiHolderI<DeleteByI> = itemAsList(DELETE_BYS, DeleteByI::class.java)
    override fun deleteBys(vararg value: DeleteByI): CommandControllerI = apply { deleteBys().addItems(value.asList()) }
    override fun deleteBy(value: DeleteByI): DeleteByI = applyAndReturn { deleteBys().add(value); value }
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
        val EMPTY = CommandController()
        val COMMANDS = "_commands"
        val COMPOSITES = "_composites"
        val CREATE_BYS = "_createBys"
        val UPDATE_BYS = "_updateBys"
        val DELETE_BYS = "_deleteBys"
    }
}


open class Comp : ModuleGroup, CompI {
    constructor(value: Comp.() -> Unit = {}) : super(value as ModuleGroup.() -> Unit)

    override fun moduleGroups(): ListMultiHolderI<ModuleGroupI> = itemAsList(MODULE_GROUPS, ModuleGroupI::class.java)
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


open class CompositeCommand : DataTypeOperation, CompositeCommandI {
    constructor(value: CompositeCommand.() -> Unit = {}) : super(value as DataTypeOperation.() -> Unit)

    override fun operations(): ListMultiHolderI<OperationI> = itemAsList(OPERATIONS, OperationI::class.java)
    override fun operations(vararg value: OperationI): CompositeCommandI = apply { operations().addItems(value.asList()) }

    override fun fillSupportsItems() {
        operations()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = CompositeCommand()
        val OPERATIONS = "_operations"
    }
}


open class Controller : CompilationUnit, ControllerI {
    constructor(value: Controller.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    companion object {
        val EMPTY = Controller()
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


open class DeleteBy : Command, DeleteByI {
    constructor(value: DeleteBy.() -> Unit = {}) : super(value as Command.() -> Unit)

    companion object {
        val EMPTY = DeleteBy()
    }
}


open class Entity : DataType, EntityI {
    constructor(value: Entity.() -> Unit = {}) : super(value as DataType.() -> Unit)

    override fun id(): AttributeI = attr(ID, { Attribute.EMPTY })
    override fun id(value: AttributeI): EntityI = apply { attr(ID, value) }

    override fun controllers(): ListMultiHolderI<ControllerI> = itemAsList(CONTROLLERS, ControllerI::class.java)
    override fun controllers(vararg value: ControllerI): EntityI = apply { controllers().addItems(value.asList()) }

    override fun commands(): ListMultiHolderI<CommandControllerI> = itemAsList(COMMANDS, CommandControllerI::class.java)
    override fun commands(vararg value: CommandControllerI): EntityI = apply { commands().addItems(value.asList()) }

    override fun queries(): ListMultiHolderI<QueryControllerI> = itemAsList(QUERIES, QueryControllerI::class.java)
    override fun queries(vararg value: QueryControllerI): EntityI = apply { queries().addItems(value.asList()) }

    override fun fillSupportsItems() {
        controllers()
        commands()
        queries()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Entity()
        val ID = "_id"
        val CONTROLLERS = "_controllers"
        val COMMANDS = "_commands"
        val QUERIES = "_queries"
    }
}


open class Event : CompilationUnit, EventI {
    constructor(value: Event.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    companion object {
        val EMPTY = Event()
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

    override fun externalTypes(): ListMultiHolderI<ExternalTypeI> = itemAsList(EXTERNAL_TYPES, ExternalTypeI::class.java)
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

    override fun models(): ListMultiHolderI<ModelI> = itemAsList(MODELS, ModelI::class.java)
    override fun models(vararg value: ModelI): ModelI = apply { models().addItems(value.asList()) }

    override fun comps(): ListMultiHolderI<CompI> = itemAsList(COMPS, CompI::class.java)
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

    override fun dependencies(): ListMultiHolderI<ModuleI> = itemAsList(DEPENDENCIES, ModuleI::class.java)
    override fun dependencies(vararg value: ModuleI): ModuleI = apply { dependencies().addItems(value.asList()) }

    override fun events(): ListMultiHolderI<EventI> = itemAsList(EVENTS, EventI::class.java)
    override fun events(vararg value: EventI): ModuleI = apply { events().addItems(value.asList()) }

    override fun commands(): ListMultiHolderI<CommandI> = itemAsList(COMMANDS, CommandI::class.java)
    override fun commands(vararg value: CommandI): ModuleI = apply { commands().addItems(value.asList()) }

    override fun entities(): ListMultiHolderI<EntityI> = itemAsList(ENTITIES, EntityI::class.java)
    override fun entities(vararg value: EntityI): ModuleI = apply { entities().addItems(value.asList()) }

    override fun enums(): ListMultiHolderI<EnumTypeI> = itemAsList(ENUMS, EnumTypeI::class.java)
    override fun enums(vararg value: EnumTypeI): ModuleI = apply { enums().addItems(value.asList()) }

    override fun values(): ListMultiHolderI<ValuesI> = itemAsList(VALUES, ValuesI::class.java)
    override fun values(vararg value: ValuesI): ModuleI = apply { values().addItems(value.asList()) }

    override fun basics(): ListMultiHolderI<BasicI> = itemAsList(BASICS, BasicI::class.java)
    override fun basics(vararg value: BasicI): ModuleI = apply { basics().addItems(value.asList()) }

    override fun controllers(): ListMultiHolderI<ControllerI> = itemAsList(CONTROLLERS, ControllerI::class.java)
    override fun controllers(vararg value: ControllerI): ModuleI = apply { controllers().addItems(value.asList()) }

    override fun fillSupportsItems() {
        dependencies()
        events()
        commands()
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
        val EVENTS = "_events"
        val COMMANDS = "_commands"
        val ENTITIES = "_entities"
        val ENUMS = "_enums"
        val VALUES = "_values"
        val BASICS = "_basics"
        val CONTROLLERS = "_controllers"
    }
}


open class ModuleGroup : StructureUnit, ModuleGroupI {
    constructor(value: ModuleGroup.() -> Unit = {}) : super(value as StructureUnit.() -> Unit)

    override fun modules(): ListMultiHolderI<ModuleI> = itemAsList(MODULES, ModuleI::class.java)
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


open class QueryController : Controller, QueryControllerI {
    constructor(value: QueryController.() -> Unit = {}) : super(value as Controller.() -> Unit)

    override fun findBys(): ListMultiHolderI<FindByI> = itemAsList(FIND_BYS, FindByI::class.java)
    override fun findBys(vararg value: FindByI): QueryControllerI = apply { findBys().addItems(value.asList()) }
    override fun findBy(value: FindByI): FindByI = applyAndReturn { findBys().add(value); value }
    override fun findBy(value: FindByI.() -> Unit) : FindByI = findBy(FindBy(value))

    override fun countBys(): ListMultiHolderI<CountByI> = itemAsList(COUNT_BYS, CountByI::class.java)
    override fun countBys(vararg value: CountByI): QueryControllerI = apply { countBys().addItems(value.asList()) }
    override fun countBy(value: CountByI): CountByI = applyAndReturn { countBys().add(value); value }
    override fun countBy(value: CountByI.() -> Unit) : CountByI = countBy(CountBy(value))

    override fun existBys(): ListMultiHolderI<ExistByI> = itemAsList(EXIST_BYS, ExistByI::class.java)
    override fun existBys(vararg value: ExistByI): QueryControllerI = apply { existBys().addItems(value.asList()) }
    override fun existBy(value: ExistByI): ExistByI = applyAndReturn { existBys().add(value); value }
    override fun existBy(value: ExistByI.() -> Unit) : ExistByI = existBy(ExistBy(value))

    override fun fillSupportsItems() {
        findBys()
        countBys()
        existBys()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = QueryController()
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


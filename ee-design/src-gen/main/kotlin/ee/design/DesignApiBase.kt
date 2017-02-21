package ee.design

import ee.lang.Attribute
import ee.lang.AttributeI
import ee.lang.CompilationUnit
import ee.lang.EnumTypeI
import ee.lang.ExternalTypeI
import ee.lang.ListMultiHolderI
import ee.lang.Operation
import ee.lang.OperationI
import ee.lang.StructureUnit
import ee.lang.StructureUnitI


open class Basic : CompilationUnit, BasicI {
    constructor(value: Basic.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    companion object {
        val EMPTY = Basic()
    }
}


fun BasicI?.isEmpty(): Boolean = (this == null || this == Basic.EMPTY)
fun BasicI?.isNotEmpty(): Boolean = !isEmpty()


open class Bundle : StructureUnit, BundleI {
    constructor(value: Bundle.() -> Unit = {}) : super(value as StructureUnit.() -> Unit)

    override fun units(): ListMultiHolderI<StructureUnitI> = itemAsList(UNITS, StructureUnitI::class.java)
    override fun units(vararg value: StructureUnitI): BundleI = apply { units().addItems(value.asList()) }

    companion object {
        val EMPTY = Bundle()
        val UNITS = "_units"
    }
}


fun BundleI?.isEmpty(): Boolean = (this == null || this == Bundle.EMPTY)
fun BundleI?.isNotEmpty(): Boolean = !isEmpty()


open class Command : DataTypeOperation, CommandI {
    constructor(value: Command.() -> Unit = {}) : super(value as DataTypeOperation.() -> Unit)

    companion object {
        val EMPTY = Command()
    }
}


fun CommandI?.isEmpty(): Boolean = (this == null || this == Command.EMPTY)
fun CommandI?.isNotEmpty(): Boolean = !isEmpty()


open class CommandController : Controller, CommandControllerI {
    constructor(value: CommandController.() -> Unit = {}) : super(value as Controller.() -> Unit)

    override fun commands(): ListMultiHolderI<CommandI> = itemAsList(COMMANDS, CommandI::class.java)
    override fun commands(vararg value: CommandI): CommandControllerI = apply { commands().addItems(value.asList()) }
    override fun command(value: CommandI): CommandI = applyAndReturn { commands().add(value); value }
    override fun command(value: CommandI.() -> Unit) : CommandI = command(Command(value))

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

    companion object {
        val EMPTY = CommandController()
        val COMMANDS = "_commands"
        val COMPOSITES = "_composites"
        val CREATE_BYS = "_createBys"
        val UPDATE_BYS = "_updateBys"
        val DELETE_BYS = "_deleteBys"
    }
}


fun CommandControllerI?.isEmpty(): Boolean = (this == null || this == CommandController.EMPTY)
fun CommandControllerI?.isNotEmpty(): Boolean = !isEmpty()


open class Comp : ModuleGroup, CompI {
    constructor(value: Comp.() -> Unit = {}) : super(value as ModuleGroup.() -> Unit)

    override fun moduleGroups(): ListMultiHolderI<ModuleGroupI> = itemAsList(MODULE_GROUPS, ModuleGroupI::class.java)
    override fun moduleGroups(vararg value: ModuleGroupI): CompI = apply { moduleGroups().addItems(value.asList()) }

    companion object {
        val EMPTY = Comp()
        val MODULE_GROUPS = "_moduleGroups"
    }
}


fun CompI?.isEmpty(): Boolean = (this == null || this == Comp.EMPTY)
fun CompI?.isNotEmpty(): Boolean = !isEmpty()


open class CompositeCommand : DataTypeOperation, CompositeCommandI {
    constructor(value: CompositeCommand.() -> Unit = {}) : super(value as DataTypeOperation.() -> Unit)

    override fun operations(): ListMultiHolderI<OperationI> = itemAsList(OPERATIONS, OperationI::class.java)
    override fun operations(vararg value: OperationI): CompositeCommandI = apply { operations().addItems(value.asList()) }

    companion object {
        val EMPTY = CompositeCommand()
        val OPERATIONS = "_operations"
    }
}


fun CompositeCommandI?.isEmpty(): Boolean = (this == null || this == CompositeCommand.EMPTY)
fun CompositeCommandI?.isNotEmpty(): Boolean = !isEmpty()


open class Controller : CompilationUnit, ControllerI {
    constructor(value: Controller.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    companion object {
        val EMPTY = Controller()
    }
}


fun ControllerI?.isEmpty(): Boolean = (this == null || this == Controller.EMPTY)
fun ControllerI?.isNotEmpty(): Boolean = !isEmpty()


open class CountBy : DataTypeOperation, CountByI {
    constructor(value: CountBy.() -> Unit = {}) : super(value as DataTypeOperation.() -> Unit)

    companion object {
        val EMPTY = CountBy()
    }
}


fun CountByI?.isEmpty(): Boolean = (this == null || this == CountBy.EMPTY)
fun CountByI?.isNotEmpty(): Boolean = !isEmpty()


open class CreateBy : Command, CreateByI {
    constructor(value: CreateBy.() -> Unit = {}) : super(value as Command.() -> Unit)

    companion object {
        val EMPTY = CreateBy()
    }
}


fun CreateByI?.isEmpty(): Boolean = (this == null || this == CreateBy.EMPTY)
fun CreateByI?.isNotEmpty(): Boolean = !isEmpty()


open class DataTypeOperation : Operation, DataTypeOperationI {
    constructor(value: DataTypeOperation.() -> Unit = {}) : super(value as Operation.() -> Unit)

    companion object {
        val EMPTY = DataTypeOperation()
    }
}


fun DataTypeOperationI?.isEmpty(): Boolean = (this == null || this == DataTypeOperation.EMPTY)
fun DataTypeOperationI?.isNotEmpty(): Boolean = !isEmpty()


open class DeleteBy : Command, DeleteByI {
    constructor(value: DeleteBy.() -> Unit = {}) : super(value as Command.() -> Unit)

    companion object {
        val EMPTY = DeleteBy()
    }
}


fun DeleteByI?.isEmpty(): Boolean = (this == null || this == DeleteBy.EMPTY)
fun DeleteByI?.isNotEmpty(): Boolean = !isEmpty()


open class Entity : CompilationUnit, EntityI {
    constructor(value: Entity.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    override fun id(): AttributeI = attr(ID, { Attribute.EMPTY })
    override fun id(value: AttributeI): EntityI = apply { attr(ID, value) }

    override fun controllers(): ListMultiHolderI<ControllerI> = itemAsList(CONTROLLERS, ControllerI::class.java)
    override fun controllers(vararg value: ControllerI): EntityI = apply { controllers().addItems(value.asList()) }

    override fun commands(): ListMultiHolderI<CommandControllerI> = itemAsList(COMMANDS, CommandControllerI::class.java)
    override fun commands(vararg value: CommandControllerI): EntityI = apply { commands().addItems(value.asList()) }

    override fun queries(): ListMultiHolderI<QueryControllerI> = itemAsList(QUERIES, QueryControllerI::class.java)
    override fun queries(vararg value: QueryControllerI): EntityI = apply { queries().addItems(value.asList()) }

    companion object {
        val EMPTY = Entity()
        val ID = "_id"
        val CONTROLLERS = "_controllers"
        val COMMANDS = "_commands"
        val QUERIES = "_queries"
    }
}


fun EntityI?.isEmpty(): Boolean = (this == null || this == Entity.EMPTY)
fun EntityI?.isNotEmpty(): Boolean = !isEmpty()


open class Event : CompilationUnit, EventI {
    constructor(value: Event.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    companion object {
        val EMPTY = Event()
    }
}


fun EventI?.isEmpty(): Boolean = (this == null || this == Event.EMPTY)
fun EventI?.isNotEmpty(): Boolean = !isEmpty()


open class ExistBy : DataTypeOperation, ExistByI {
    constructor(value: ExistBy.() -> Unit = {}) : super(value as DataTypeOperation.() -> Unit)

    companion object {
        val EMPTY = ExistBy()
    }
}


fun ExistByI?.isEmpty(): Boolean = (this == null || this == ExistBy.EMPTY)
fun ExistByI?.isNotEmpty(): Boolean = !isEmpty()


open class ExternalModule : Module, ExternalModuleI {
    constructor(value: ExternalModule.() -> Unit = {}) : super(value as Module.() -> Unit)

    override fun externalTypes(): ListMultiHolderI<ExternalTypeI> = itemAsList(EXTERNAL_TYPES, ExternalTypeI::class.java)
    override fun externalTypes(vararg value: ExternalTypeI): ExternalModuleI = apply { externalTypes().addItems(value.asList()) }

    companion object {
        val EMPTY = ExternalModule()
        val EXTERNAL_TYPES = "_externalTypes"
    }
}


fun ExternalModuleI?.isEmpty(): Boolean = (this == null || this == ExternalModule.EMPTY)
fun ExternalModuleI?.isNotEmpty(): Boolean = !isEmpty()


open class Facet : ModuleGroup, FacetI {
    constructor(value: Facet.() -> Unit = {}) : super(value as ModuleGroup.() -> Unit)

    companion object {
        val EMPTY = Facet()
    }
}


fun FacetI?.isEmpty(): Boolean = (this == null || this == Facet.EMPTY)
fun FacetI?.isNotEmpty(): Boolean = !isEmpty()


open class FindBy : DataTypeOperation, FindByI {
    constructor(value: FindBy.() -> Unit = {}) : super(value as DataTypeOperation.() -> Unit)

    companion object {
        val EMPTY = FindBy()
    }
}


fun FindByI?.isEmpty(): Boolean = (this == null || this == FindBy.EMPTY)
fun FindByI?.isNotEmpty(): Boolean = !isEmpty()


open class Model : StructureUnit, ModelI {
    constructor(value: Model.() -> Unit = {}) : super(value as StructureUnit.() -> Unit)

    override fun models(): ListMultiHolderI<ModelI> = itemAsList(MODELS, ModelI::class.java)
    override fun models(vararg value: ModelI): ModelI = apply { models().addItems(value.asList()) }

    override fun comps(): ListMultiHolderI<CompI> = itemAsList(COMPS, CompI::class.java)
    override fun comps(vararg value: CompI): ModelI = apply { comps().addItems(value.asList()) }

    companion object {
        val EMPTY = Model()
        val MODELS = "_models"
        val COMPS = "_comps"
    }
}


fun ModelI?.isEmpty(): Boolean = (this == null || this == Model.EMPTY)
fun ModelI?.isNotEmpty(): Boolean = !isEmpty()


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


fun ModuleI?.isEmpty(): Boolean = (this == null || this == Module.EMPTY)
fun ModuleI?.isNotEmpty(): Boolean = !isEmpty()


open class ModuleGroup : StructureUnit, ModuleGroupI {
    constructor(value: ModuleGroup.() -> Unit = {}) : super(value as StructureUnit.() -> Unit)

    override fun modules(): ListMultiHolderI<ModuleI> = itemAsList(MODULES, ModuleI::class.java)
    override fun modules(vararg value: ModuleI): ModuleGroupI = apply { modules().addItems(value.asList()) }

    companion object {
        val EMPTY = ModuleGroup()
        val MODULES = "_modules"
    }
}


fun ModuleGroupI?.isEmpty(): Boolean = (this == null || this == ModuleGroup.EMPTY)
fun ModuleGroupI?.isNotEmpty(): Boolean = !isEmpty()


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

    companion object {
        val EMPTY = QueryController()
        val FIND_BYS = "_findBys"
        val COUNT_BYS = "_countBys"
        val EXIST_BYS = "_existBys"
    }
}


fun QueryControllerI?.isEmpty(): Boolean = (this == null || this == QueryController.EMPTY)
fun QueryControllerI?.isNotEmpty(): Boolean = !isEmpty()


open class UpdateBy : Command, UpdateByI {
    constructor(value: UpdateBy.() -> Unit = {}) : super(value as Command.() -> Unit)

    companion object {
        val EMPTY = UpdateBy()
    }
}


fun UpdateByI?.isEmpty(): Boolean = (this == null || this == UpdateBy.EMPTY)
fun UpdateByI?.isNotEmpty(): Boolean = !isEmpty()


open class Values : CompilationUnit, ValuesI {
    constructor(value: Values.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    companion object {
        val EMPTY = Values()
    }
}


fun ValuesI?.isEmpty(): Boolean = (this == null || this == Values.EMPTY)
fun ValuesI?.isNotEmpty(): Boolean = !isEmpty()


open class Widget : CompilationUnit, WidgetI {
    constructor(value: Widget.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    companion object {
        val EMPTY = Widget()
    }
}


fun WidgetI?.isEmpty(): Boolean = (this == null || this == Widget.EMPTY)
fun WidgetI?.isNotEmpty(): Boolean = !isEmpty()


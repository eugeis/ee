package ee.design

import ee.lang.Attribute
import ee.lang.AttributeI
import ee.lang.CompilationUnit
import ee.lang.EnumTypeI
import ee.lang.EnumTypes
import ee.lang.ExternalTypeI
import ee.lang.ExternalTypes
import ee.lang.Operation
import ee.lang.OperationI
import ee.lang.Operations
import ee.lang.StructureUnit
import ee.lang.StructureUnitI
import ee.lang.StructureUnits
import ee.lang.ValueHolder
import ee.lang.ValueHolderI


open class Basic : CompilationUnit, BasicI {
    constructor(value: Basic.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    companion object {
        val EMPTY = Basic()
    }
}


fun BasicI?.isEmpty(): Boolean = (this == null || this == Basic.EMPTY)
fun BasicI?.isNotEmpty(): Boolean = !isEmpty()


open class Bundle : StructureUnit, BundleI {
    private var _units: StructureUnits = add(StructureUnits({ name("units") }))

    constructor(value: Bundle.() -> Unit = {}) : super(value as StructureUnit.() -> Unit)

    override fun units(): List<StructureUnitI> = _units.items()
    override fun units(vararg value: StructureUnitI): BundleI = apply { _units.addAll(value.toList()) }

    companion object {
        val EMPTY = Bundle()
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
    private var _commands: Commands = add(Commands({ name("commands") }))
    private var _composites: CompositeCommands = add(CompositeCommands({ name("composites") }))
    private var _createBys: CreateBys = add(CreateBys({ name("createBys") }))
    private var _updateBys: UpdateBys = add(UpdateBys({ name("updateBys") }))
    private var _deleteBys: DeleteBys = add(DeleteBys({ name("deleteBys") }))

    constructor(value: CommandController.() -> Unit = {}) : super(value as Controller.() -> Unit)

    override fun commands(): List<CommandI> = _commands.items()
    override fun commands(vararg value: CommandI): CommandControllerI = apply { _commands.addAll(value.toList()) }
    override fun command(value: CommandI): CommandI = applyAndReturn { _commands.add(value); value }
    override fun command(value: CommandI.() -> Unit) : CommandI = command(Command(value))

    override fun composites(): List<CompositeCommandI> = _composites.items()
    override fun composites(vararg value: CompositeCommandI): CommandControllerI = apply { _composites.addAll(value.toList()) }
    override fun composite(value: CompositeCommandI): CompositeCommandI = applyAndReturn { _composites.add(value); value }
    override fun composite(value: CompositeCommandI.() -> Unit) : CompositeCommandI = composite(CompositeCommand(value))

    override fun createBys(): List<CreateByI> = _createBys.items()
    override fun createBys(vararg value: CreateByI): CommandControllerI = apply { _createBys.addAll(value.toList()) }
    override fun createBy(value: CreateByI): CreateByI = applyAndReturn { _createBys.add(value); value }
    override fun createBy(value: CreateByI.() -> Unit) : CreateByI = createBy(CreateBy(value))

    override fun updateBys(): List<UpdateByI> = _updateBys.items()
    override fun updateBys(vararg value: UpdateByI): CommandControllerI = apply { _updateBys.addAll(value.toList()) }
    override fun updateBy(value: UpdateByI): UpdateByI = applyAndReturn { _updateBys.add(value); value }
    override fun updateBy(value: UpdateByI.() -> Unit) : UpdateByI = updateBy(UpdateBy(value))

    override fun deleteBys(): List<DeleteByI> = _deleteBys.items()
    override fun deleteBys(vararg value: DeleteByI): CommandControllerI = apply { _deleteBys.addAll(value.toList()) }
    override fun deleteBy(value: DeleteByI): DeleteByI = applyAndReturn { _deleteBys.add(value); value }
    override fun deleteBy(value: DeleteByI.() -> Unit) : DeleteByI = deleteBy(DeleteBy(value))

    companion object {
        val EMPTY = CommandController()
    }
}


fun CommandControllerI?.isEmpty(): Boolean = (this == null || this == CommandController.EMPTY)
fun CommandControllerI?.isNotEmpty(): Boolean = !isEmpty()


open class Comp : ModuleGroup, CompI {
    private var _moduleGroups: ModuleGroups = add(ModuleGroups({ name("moduleGroups") }))

    constructor(value: Comp.() -> Unit = {}) : super(value as ModuleGroup.() -> Unit)

    override fun moduleGroups(): List<ModuleGroupI> = _moduleGroups.items()
    override fun moduleGroups(vararg value: ModuleGroupI): CompI = apply { _moduleGroups.addAll(value.toList()) }

    companion object {
        val EMPTY = Comp()
    }
}


fun CompI?.isEmpty(): Boolean = (this == null || this == Comp.EMPTY)
fun CompI?.isNotEmpty(): Boolean = !isEmpty()


open class CompositeCommand : DataTypeOperation, CompositeCommandI {
    private var _operations: Operations = add(Operations({ name("operations") }))

    constructor(value: CompositeCommand.() -> Unit = {}) : super(value as DataTypeOperation.() -> Unit)

    override fun operations(): List<OperationI> = _operations.items()
    override fun operations(vararg value: OperationI): CompositeCommandI = apply { _operations.addAll(value.toList()) }

    companion object {
        val EMPTY = CompositeCommand()
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
    private var _id: ValueHolderI<AttributeI> = add(ValueHolder(Attribute.EMPTY as AttributeI, { name("id") }))
    private var _controllers: Controllers = add(Controllers({ name("controllers") }))
    private var _commands: CommandControllers = add(CommandControllers({ name("commands") }))
    private var _queries: QueryControllers = add(QueryControllers({ name("queries") }))

    constructor(value: Entity.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    override fun id(): AttributeI = _id.value()
    override fun id(value: AttributeI): EntityI = apply { _id.value(value) }

    override fun controllers(): List<ControllerI> = _controllers.items()
    override fun controllers(vararg value: ControllerI): EntityI = apply { _controllers.addAll(value.toList()) }

    override fun commands(): List<CommandControllerI> = _commands.items()
    override fun commands(vararg value: CommandControllerI): EntityI = apply { _commands.addAll(value.toList()) }

    override fun queries(): List<QueryControllerI> = _queries.items()
    override fun queries(vararg value: QueryControllerI): EntityI = apply { _queries.addAll(value.toList()) }

    companion object {
        val EMPTY = Entity()
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
    private var _externalTypes: ExternalTypes = add(ExternalTypes({ name("externalTypes") }))

    constructor(value: ExternalModule.() -> Unit = {}) : super(value as Module.() -> Unit)

    override fun externalTypes(): List<ExternalTypeI> = _externalTypes.items()
    override fun externalTypes(vararg value: ExternalTypeI): ExternalModuleI = apply { _externalTypes.addAll(value.toList()) }

    companion object {
        val EMPTY = ExternalModule()
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
    private var _models: Models = add(Models({ name("models") }))
    private var _comps: Comps = add(Comps({ name("comps") }))

    constructor(value: Model.() -> Unit = {}) : super(value as StructureUnit.() -> Unit)

    override fun models(): List<ModelI> = _models.items()
    override fun models(vararg value: ModelI): ModelI = apply { _models.addAll(value.toList()) }

    override fun comps(): List<CompI> = _comps.items()
    override fun comps(vararg value: CompI): ModelI = apply { _comps.addAll(value.toList()) }

    companion object {
        val EMPTY = Model()
    }
}


fun ModelI?.isEmpty(): Boolean = (this == null || this == Model.EMPTY)
fun ModelI?.isNotEmpty(): Boolean = !isEmpty()


open class Module : StructureUnit, ModuleI {
    private var _parentNamespace: ValueHolderI<Boolean> = add(ValueHolder(false, { name("parentNamespace") }))
    private var _dependencies: Modules = add(Modules({ name("dependencies") }))
    private var _events: Events = add(Events({ name("events") }))
    private var _commands: Commands = add(Commands({ name("commands") }))
    private var _entities: Entitys = add(Entitys({ name("entities") }))
    private var _enums: EnumTypes = add(EnumTypes({ name("enums") }))
    private var _values: Valuess = add(Valuess({ name("values") }))
    private var _basics: Basics = add(Basics({ name("basics") }))
    private var _controllers: Controllers = add(Controllers({ name("controllers") }))

    constructor(value: Module.() -> Unit = {}) : super(value as StructureUnit.() -> Unit)

    override fun parentNamespace(): Boolean = _parentNamespace.value()
    override fun parentNamespace(value: Boolean): ModuleI = apply { _parentNamespace.value(value) }

    override fun dependencies(): List<ModuleI> = _dependencies.items()
    override fun dependencies(vararg value: ModuleI): ModuleI = apply { _dependencies.addAll(value.toList()) }

    override fun events(): List<EventI> = _events.items()
    override fun events(vararg value: EventI): ModuleI = apply { _events.addAll(value.toList()) }

    override fun commands(): List<CommandI> = _commands.items()
    override fun commands(vararg value: CommandI): ModuleI = apply { _commands.addAll(value.toList()) }

    override fun entities(): List<EntityI> = _entities.items()
    override fun entities(vararg value: EntityI): ModuleI = apply { _entities.addAll(value.toList()) }

    override fun enums(): List<EnumTypeI> = _enums.items()
    override fun enums(vararg value: EnumTypeI): ModuleI = apply { _enums.addAll(value.toList()) }

    override fun values(): List<ValuesI> = _values.items()
    override fun values(vararg value: ValuesI): ModuleI = apply { _values.addAll(value.toList()) }

    override fun basics(): List<BasicI> = _basics.items()
    override fun basics(vararg value: BasicI): ModuleI = apply { _basics.addAll(value.toList()) }

    override fun controllers(): List<ControllerI> = _controllers.items()
    override fun controllers(vararg value: ControllerI): ModuleI = apply { _controllers.addAll(value.toList()) }

    companion object {
        val EMPTY = Module()
    }
}


fun ModuleI?.isEmpty(): Boolean = (this == null || this == Module.EMPTY)
fun ModuleI?.isNotEmpty(): Boolean = !isEmpty()


open class ModuleGroup : StructureUnit, ModuleGroupI {
    private var _modules: Modules = add(Modules({ name("modules") }))

    constructor(value: ModuleGroup.() -> Unit = {}) : super(value as StructureUnit.() -> Unit)

    override fun modules(): List<ModuleI> = _modules.items()
    override fun modules(vararg value: ModuleI): ModuleGroupI = apply { _modules.addAll(value.toList()) }

    companion object {
        val EMPTY = ModuleGroup()
    }
}


fun ModuleGroupI?.isEmpty(): Boolean = (this == null || this == ModuleGroup.EMPTY)
fun ModuleGroupI?.isNotEmpty(): Boolean = !isEmpty()


open class QueryController : Controller, QueryControllerI {
    private var _findBys: FindBys = add(FindBys({ name("findBys") }))
    private var _countBys: CountBys = add(CountBys({ name("countBys") }))
    private var _existBys: ExistBys = add(ExistBys({ name("existBys") }))

    constructor(value: QueryController.() -> Unit = {}) : super(value as Controller.() -> Unit)

    override fun findBys(): List<FindByI> = _findBys.items()
    override fun findBys(vararg value: FindByI): QueryControllerI = apply { _findBys.addAll(value.toList()) }
    override fun findBy(value: FindByI): FindByI = applyAndReturn { _findBys.add(value); value }
    override fun findBy(value: FindByI.() -> Unit) : FindByI = findBy(FindBy(value))

    override fun countBys(): List<CountByI> = _countBys.items()
    override fun countBys(vararg value: CountByI): QueryControllerI = apply { _countBys.addAll(value.toList()) }
    override fun countBy(value: CountByI): CountByI = applyAndReturn { _countBys.add(value); value }
    override fun countBy(value: CountByI.() -> Unit) : CountByI = countBy(CountBy(value))

    override fun existBys(): List<ExistByI> = _existBys.items()
    override fun existBys(vararg value: ExistByI): QueryControllerI = apply { _existBys.addAll(value.toList()) }
    override fun existBy(value: ExistByI): ExistByI = applyAndReturn { _existBys.add(value); value }
    override fun existBy(value: ExistByI.() -> Unit) : ExistByI = existBy(ExistBy(value))

    companion object {
        val EMPTY = QueryController()
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


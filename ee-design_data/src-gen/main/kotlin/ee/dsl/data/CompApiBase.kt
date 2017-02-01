package ee.design

import ee.design.Attribute
import ee.design.AttributeI
import ee.design.CompilationUnit
import ee.design.EnumTypeI
import ee.design.EnumTypes
import ee.design.Module
import ee.design.Operation
import ee.design.OperationI
import ee.design.Operations
import ee.design.ValueHolder
import ee.design.ValueHolderI


open class Basic : CompilationUnit, BasicI {
    constructor(value: Basic.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    companion object {
        val EMPTY = Basic()
    }
}


fun BasicI?.isEmpty(): Boolean = (this == null || this == Basic.EMPTY)
fun BasicI?.isNotEmpty(): Boolean = !isEmpty()


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


open class DataType : CompilationUnit, DataTypeI {
    private var _id: ValueHolderI<AttributeI> = add(ValueHolder(Attribute.EMPTY as AttributeI, { name("id") }))
    private var _controllers: Controllers = add(Controllers({ name("controllers") }))
    private var _commands: CommandControllers = add(CommandControllers({ name("commands") }))
    private var _queries: QueryControllers = add(QueryControllers({ name("queries") }))

    constructor(value: DataType.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    override fun id(): AttributeI = _id.value()
    override fun id(value: AttributeI): DataTypeI = apply { _id.value(value) }

    override fun controllers(): List<ControllerI> = _controllers.items()
    override fun controllers(vararg value: ControllerI): DataTypeI = apply { _controllers.addAll(value.toList()) }

    override fun commands(): List<CommandControllerI> = _commands.items()
    override fun commands(vararg value: CommandControllerI): DataTypeI = apply { _commands.addAll(value.toList()) }

    override fun queries(): List<QueryControllerI> = _queries.items()
    override fun queries(vararg value: QueryControllerI): DataTypeI = apply { _queries.addAll(value.toList()) }

    companion object {
        val EMPTY = DataType()
    }
}


fun DataTypeI?.isEmpty(): Boolean = (this == null || this == DataType.EMPTY)
fun DataTypeI?.isNotEmpty(): Boolean = !isEmpty()


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


open class DesignModule : Module, DesignModuleI {
    private var _events: Events = add(Events({ name("events") }))
    private var _commands: Commands = add(Commands({ name("commands") }))
    private var _entities: Entitys = add(Entitys({ name("entities") }))
    private var _enums: EnumTypes = add(EnumTypes({ name("enums") }))
    private var _values: Valuess = add(Valuess({ name("values") }))
    private var _basics: Basics = add(Basics({ name("basics") }))
    private var _controllers: Controllers = add(Controllers({ name("controllers") }))

    constructor(value: DesignModule.() -> Unit = {}) : super(value as Module.() -> Unit)

    override fun events(): List<EventI> = _events.items()
    override fun events(vararg value: EventI): DesignModuleI = apply { _events.addAll(value.toList()) }

    override fun commands(): List<CommandI> = _commands.items()
    override fun commands(vararg value: CommandI): DesignModuleI = apply { _commands.addAll(value.toList()) }

    override fun entities(): List<EntityI> = _entities.items()
    override fun entities(vararg value: EntityI): DesignModuleI = apply { _entities.addAll(value.toList()) }

    override fun enums(): List<EnumTypeI> = _enums.items()
    override fun enums(vararg value: EnumTypeI): DesignModuleI = apply { _enums.addAll(value.toList()) }

    override fun values(): List<ValuesI> = _values.items()
    override fun values(vararg value: ValuesI): DesignModuleI = apply { _values.addAll(value.toList()) }

    override fun basics(): List<BasicI> = _basics.items()
    override fun basics(vararg value: BasicI): DesignModuleI = apply { _basics.addAll(value.toList()) }

    override fun controllers(): List<ControllerI> = _controllers.items()
    override fun controllers(vararg value: ControllerI): DesignModuleI = apply { _controllers.addAll(value.toList()) }

    companion object {
        val EMPTY = DesignModule()
    }
}


fun DesignModuleI?.isEmpty(): Boolean = (this == null || this == DesignModule.EMPTY)
fun DesignModuleI?.isNotEmpty(): Boolean = !isEmpty()


open class Entity : DataType, EntityI {
    constructor(value: Entity.() -> Unit = {}) : super(value as DataType.() -> Unit)

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


open class FindBy : DataTypeOperation, FindByI {
    constructor(value: FindBy.() -> Unit = {}) : super(value as DataTypeOperation.() -> Unit)

    companion object {
        val EMPTY = FindBy()
    }
}


fun FindByI?.isEmpty(): Boolean = (this == null || this == FindBy.EMPTY)
fun FindByI?.isNotEmpty(): Boolean = !isEmpty()


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


open class Values : DataType, ValuesI {
    constructor(value: Values.() -> Unit = {}) : super(value as DataType.() -> Unit)

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


package ee.design.comp

import ee.common.ext.addReturn
import ee.design.*

open class Model : CompGroup {
    val models: MutableList<Model> by lazy {
        children.filterIsInstance(Model::class.java).toMutableList()
    }

    constructor(init: Model.() -> Unit = {}) : super() {
        init()
    }

    constructor(namespace: String, init: Model.() -> Unit = {}) : super(namespace) {
        init()
    }
}

abstract open class StructureGroup<T : StructureUnit> : StructureUnit {
    abstract val items: MutableList<T>

    constructor(init: StructureGroup<T>.() -> Unit = {}) : super() {
        init()
    }

    constructor(namespace: String, init: StructureGroup<T>.() -> Unit = {}) : super(namespace) {
        init()
    }
}

open class Bundle : StructureGroup<StructureUnit> {
    override val items: MutableList<StructureUnit> by lazy {
        children.filterIsInstance(StructureUnit::class.java).toMutableList()
    }

    constructor() : super()
    constructor(name: String, init: Bundle.() -> Unit = {}) : super(name) {
        init()
    }

    constructor(vararg items: StructureUnit, init: Bundle.() -> Unit = {}) : super() {
        children.addAll(items)
        init()
    }
}

open class ModuleGroup : StructureGroup<CompModule> {
    override val items: MutableList<CompModule> by lazy {
        children.filterIsInstance(CompModule::class.java).toMutableList()
    }

    constructor() : super()
    constructor(name: String, init: ModuleGroup.() -> Unit = {}) : super(name) {
        init()
    }

    constructor(vararg items: CompModule, init: ModuleGroup.() -> Unit = {}) : super() {
        this.items.addAll(items)
        init()
    }
}

open class CompGroup : StructureGroup<Comp> {
    override val items: MutableList<Comp> by lazy {
        children.filterIsInstance(Comp::class.java).toMutableList()
    }

    constructor() : super()
    constructor(name: String, init: CompGroup.() -> Unit = {}) : super(name) {
        init()
    }

    constructor(vararg items: Comp, init: CompGroup.() -> Unit = {}) : super() {
        this.items.addAll(items)
        init()
    }
}

open class Comp : ModuleGroup {
    val moduleGroups: MutableList<ModuleGroup> by lazy {
        children.filterIsInstance(ModuleGroup::class.java).toMutableList()
    }

    constructor(init: Comp.() -> Unit = {}) : super() {
        init()
    }

    constructor(namespace: String, init: Comp.() -> Unit = {}) : super(namespace) {
        init()
    }
}

open class CompModule : StructureUnit {
    val parentNamespace: Boolean = false
    val dependencies: ModuleGroup = ModuleGroup("dependencies")
    val events: MutableList<Event> by lazy {
        children.filterIsInstance(Event::class.java).toMutableList()
    }
    val commands: MutableList<Command> by lazy {
        children.filterIsInstance(Command::class.java).toMutableList()
    }
    val entities: MutableList<Entity> by lazy {
        children.filterIsInstance(Entity::class.java).toMutableList()
    }
    val enums: MutableList<EnumType> by lazy {
        children.filterIsInstance(EnumType::class.java).toMutableList()
    }
    val values: MutableList<Values> by lazy {
        children.filterIsInstance(Values::class.java).toMutableList()
    }
    val basics: MutableList<Basic> by lazy {
        children.filterIsInstance(Basic::class.java).toMutableList()
    }
    val controllers: MutableList<Controller> by lazy {
        children.filterIsInstance(Controller::class.java).toMutableList()
    }

    constructor() : super()
    constructor(vararg dependencies: CompModule) : super() {
        this.dependencies.items.addAll(dependencies)
    }

    constructor(init: CompModule.() -> Unit = {}) : super() {
        init()
    }

    constructor(init: CompModule.() -> Unit = {}, vararg dependencies: CompModule) : super() {
        this.dependencies.items.addAll(dependencies)
        init()
    }

    fun comp(): Comp {
        return parent as Comp
    }
}

open class Facet : ModuleGroup {
    constructor() : super()
    constructor(name: String, init: Facet.() -> Unit = {}) : super(name) {
        init()
    }

    fun module(namespace: String, init: ExternalModule.() -> Unit): ExternalModule =
            add(ExternalModule(namespace, init))
}

open class ExternalModule : CompModule {
    val externalTypes: MutableList<ExternalType> by lazy {
        children.filterIsInstance(ExternalType::class.java).toMutableList()
    }

    constructor() : super()
    constructor(namespace: String, init: ExternalModule.() -> Unit = {}) : super() {
        this.namespace = namespace
        init()
    }

    fun type(init: ExternalType.() -> Unit = {}): ExternalType {
        return add(ExternalType(init), {
            if (namespace.isEmpty()) {
                namespace = this@ExternalModule.namespace
            }
        })
    }

}


open class Controller : CompilationUnit {
    constructor() : super() {
        base = true
    }

    constructor(init: Controller.() -> Unit = {}) : super() {
        base = true
        init()
    }

    override fun <T : Element> createType(): T {
        return Controller() as T
    }
}

open class Queries : Controller {
    companion object {
        val EMPTY = Queries()
    }

    val findBys: MutableList<FindBy> = arrayListOf()
    val countBys: MutableList<CountBy> = arrayListOf()
    val existBys: MutableList<ExistBy> = arrayListOf()

    constructor() : super()
    constructor(init: Queries.() -> Unit = {}) : super() {
        init()
    }

    fun findBy(vararg params: Attribute, ret: Attribute = t.void, init: FindBy.() -> Unit = {}): FindBy
            = findBys.addReturn(add(FindBy(params.toList(), ret, init)))

    fun countBy(vararg params: Attribute, ret: Attribute = t.void, init: CountBy.() -> Unit = {}): CountBy
            = countBys.addReturn(add(CountBy(params.toList(), ret, init)))

    fun existBy(vararg params: Attribute, ret: Attribute = t.void, init: ExistBy.() -> Unit = {}): ExistBy
            = existBys.addReturn(add(ExistBy(params.toList(), ret, init)))

    override fun <T : Element> createType(): T {
        return Queries() as T
    }
}

class Event : CompilationUnit {
    constructor() : super()
    constructor(init: Event.() -> Unit = {}) : super() {
        init()
    }
}

open class Command : DataTypeOperation {
    constructor() : super()
    constructor(params: List<Attribute> = emptyList(), ret: Attribute = t.void,
                init: Command.() -> Unit = {}) : super(params, ret) {
        init()
    }
}

open class CompositeCommand : DataTypeOperation {
    var operations: List<Operation> = emptyList()

    constructor() : super()
    constructor(operations: List<Operation>, init: CompositeCommand.() -> Unit = {}) : super() {
        this.operations = operations
        init()
    }
}

open class Commands : Controller {
    companion object {
        val EMPTY = Commands()
    }

    constructor() : super()
    constructor(init: Commands.() -> Unit = {}) : super() {
        init()
    }

    val commands: MutableList<Command> = arrayListOf()
    val compositeCommands: MutableList<CompositeCommand> = arrayListOf()
    val createBys: MutableList<CreateBy> = arrayListOf()
    val updateBys: MutableList<UpdateBy> = arrayListOf()
    val deleteBys: MutableList<DeleteBy> = arrayListOf()

    fun createBy(vararg params: Attribute, ret: Attribute = t.void, init: CreateBy.() -> Unit = {}): CreateBy
            = createBys.addReturn(add(CreateBy(params.toList(), ret, init)))

    fun updateBy(vararg params: Attribute, ret: Attribute = t.void, init: UpdateBy.() -> Unit = {}): UpdateBy
            = updateBys.addReturn(add(UpdateBy(params.toList(), ret, init)))

    fun deleteBy(vararg params: Attribute, ret: Attribute = t.void, init: DeleteBy.() -> Unit = {}): DeleteBy
            = deleteBys.addReturn(add(DeleteBy(params.toList(), ret, init)))

    fun command(vararg params: Attribute, ret: Attribute = t.void, init: Command.() -> Unit = {}): Command
            = commands.addReturn(add(Command(params.toList(), ret, init)))

    fun compositeCommand(vararg operations: Operation, init: CompositeCommand.() -> Unit = {}): CompositeCommand
            = compositeCommands.addReturn(add(CompositeCommand(operations.toList(), init)))

    override fun <T : Element> createType(): T {
        return Commands() as T
    }
}


open class DataTypeOperation : Operation {
    constructor() : super()
    constructor(params: List<Attribute> = emptyList(), ret: Attribute = t.void,
                init: DataTypeOperation.() -> Unit = {}) : super(params, ret) {
        init()
    }
}

open class FindBy : DataTypeOperation {
    constructor() : super()
    constructor(params: List<Attribute> = emptyList(), ret: Attribute = t.void,
                init: FindBy.() -> Unit = {}) : super(params, ret) {
        deriveName("findBy")
        init()
    }
}

open class CountBy : DataTypeOperation {
    constructor() : super()
    constructor(params: List<Attribute> = emptyList(), ret: Attribute = t.void,
                init: CountBy.() -> Unit = {}) : super(params, ret) {
        deriveName("countBy")
        init()
    }
}

open class ExistBy : DataTypeOperation {
    constructor() : super()
    constructor(params: List<Attribute> = emptyList(), ret: Attribute = t.void,
                init: ExistBy.() -> Unit = {}) : super(params, ret) {
        deriveName("existsBy")
        init()
    }
}

open class CreateBy : Command {
    constructor() : super()
    constructor(params: List<Attribute> = emptyList(), ret: Attribute = t.void,
                init: CreateBy.() -> Unit = {}) : super(params, ret) {
        deriveName("createBy")
        init()

    }
}

open class DeleteBy : Command {
    constructor() : super()
    constructor(params: List<Attribute> = emptyList(), ret: Attribute = t.void,
                init: DeleteBy.() -> Unit = {}) : super(params, ret) {
        deriveName("deleteBy")
        init()
    }
}

open class UpdateBy : Command {
    constructor() : super()
    constructor(params: List<Attribute> = emptyList(), ret: Attribute = t.void,
                init: UpdateBy.() -> Unit = {}) : super(params, ret) {
        deriveName("updateBy")
        init()
    }
}

open class DataType : CompilationUnit {
    val _id: Attribute? by lazy { propsAll.filterIsInstance<Attribute>().find { it.key } }
    val controllers: List<Controller> by lazy { children.filterIsInstance(Controller::class.java).filter { it !is Commands && it !is Queries } }
    val commandsControllers: List<Commands> by lazy { children.filterIsInstance(Commands::class.java) }
    val queriesControllers: List<Queries> by lazy { children.filterIsInstance(Queries::class.java) }

    constructor() : super()
    constructor(init: DataType.() -> Unit = {}) : super() {
        init()
    }

    fun id(type: Type = t.String,
           init: Attribute.() -> Unit = {}): Attribute = prop(type) {
        this.key = true
        init()
    }

    override fun <T : Element> createType(): T {
        return DataType() as T
    }
}

open class Entity : DataType {
    constructor() : super()
    constructor(init: Entity.() -> Unit = {}) : super() {
        init()
    }

    override fun <T : Element> createType(): T {
        return Entity() as T
    }
}


open class Basic : CompilationUnit {
    constructor() : super()
    constructor(init: Basic.() -> Unit = {}) : super() {
        init()
    }

    override fun <T : Element> createType(): T {
        return Basic() as T
    }
}

open class Values : DataType {
    constructor() : super()
    constructor(init: Values.() -> Unit = {}) : super() {
        init()
    }

    override fun <T : Element> createType(): T {
        return Values() as T
    }
}

open class Widget : CompilationUnit {
    constructor() : super()
    constructor(init: Widget.() -> Unit = {}) : super() {
        init()
    }
}

/*
class Reference<T : Base>(val ref: String, var target: T? = null)

open class Base(val name: String) {
    //val ref = Reference<*: Base>(name, this) ??
    val ref = Reference(name, this)
}

class ChildX(name: String) : Base(name)
class ChildY(name: String) : Base(name)

fun test() {
    val childX = ChildX("test")
    val childY = ChildY("test")


    //cast needed because, Base.ref is Base,
    // it is possible to have ref in Base class with "generic from inherited type" ref
    val refChildX: Reference<ChildX> = childX.ref as Reference<ChildX>
    val refChildY: Reference<ChildY> = childY.ref as Reference<ChildY>
}*/
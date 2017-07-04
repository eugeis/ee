package ee.lang


open class Attribute : Composite, AttributeI {

    constructor(value: Attribute.() -> Unit = {}) : super(value as Composite.() -> Unit)

    override fun accessible(): Boolean = attr(ACCESSIBLE, { true })
    override fun accessible(value: Boolean): AttributeI = apply { attr(ACCESSIBLE, value) }

    override fun hidden(): Boolean = attr(HIDDEN, { false })
    override fun hidden(value: Boolean): AttributeI = apply { attr(HIDDEN, value) }

    override fun inherited(): Boolean = attr(INHERITED, { false })
    override fun inherited(value: Boolean): AttributeI = apply { attr(INHERITED, value) }

    override fun initByDefaultTypeValue(): Boolean = attr(INIT_BY_DEFAULT_TYPE_VALUE, { true })
    override fun initByDefaultTypeValue(value: Boolean): AttributeI = apply { attr(INIT_BY_DEFAULT_TYPE_VALUE, value) }

    override fun key(): Boolean = attr(KEY, { false })
    override fun key(value: Boolean): AttributeI = apply { attr(KEY, value) }

    override fun length(): Int? = attr(LENGTH)
    override fun length(value: Int?): AttributeI = apply { attr(LENGTH, value) }

    override fun meta(): Boolean = attr(META, { false })
    override fun meta(value: Boolean): AttributeI = apply { attr(META, value) }

    override fun multi(): Boolean = attr(MULTI, { false })
    override fun multi(value: Boolean): AttributeI = apply { attr(MULTI, value) }

    override fun mutable(): Boolean = attr(MUTABLE, { true })
    override fun mutable(value: Boolean): AttributeI = apply { attr(MUTABLE, value) }

    override fun nonFluent(): String = attr(NON_FLUENT, { "" })
    override fun nonFluent(value: String): AttributeI = apply { attr(NON_FLUENT, value) }

    override fun nullable(): Boolean = attr(NULLABLE, { false })
    override fun nullable(value: Boolean): AttributeI = apply { attr(NULLABLE, value) }

    override fun open(): Boolean = attr(OPEN, { false })
    override fun open(value: Boolean): AttributeI = apply { attr(OPEN, value) }

    override fun replaceable(): Boolean = attr(REPLACEABLE, { false })
    override fun replaceable(value: Boolean): AttributeI = apply { attr(REPLACEABLE, value) }

    override fun type(): TypeI = attr(TYPE, { n.String as TypeI })
    override fun type(value: TypeI): AttributeI = apply { attr(TYPE, value) }

    override fun unique(): Boolean = attr(UNIQUE, { false })
    override fun unique(value: Boolean): AttributeI = apply { attr(UNIQUE, value) }

    override fun value(): Any? = attr(VALUE)
    override fun value(aValue: Any?): AttributeI = apply { attr(VALUE, aValue) }

    companion object {
        val EMPTY = Attribute({ name(ItemEmpty.name()) })
        val ACCESSIBLE = "_accessible"
        val HIDDEN = "_hidden"
        val INHERITED = "_inherited"
        val INIT_BY_DEFAULT_TYPE_VALUE = "_initByDefaultTypeValue"
        val KEY = "_key"
        val LENGTH = "_length"
        val META = "_meta"
        val MULTI = "_multi"
        val MUTABLE = "_mutable"
        val NON_FLUENT = "_nonFluent"
        val NULLABLE = "_nullable"
        val OPEN = "_open"
        val REPLACEABLE = "_replaceable"
        val TYPE = "_type"
        val UNIQUE = "_unique"
        val VALUE = "_value"
    }
}


open class CompilationUnit : Type, CompilationUnitI {

    constructor(value: CompilationUnit.() -> Unit = {}) : super(value as Type.() -> Unit)

    override fun base(): Boolean = attr(BASE, { false })
    override fun base(value: Boolean): CompilationUnitI = apply { attr(BASE, value) }

    override fun constructors(): ListMultiHolder<ConstructorI> = itemAsList(CONSTRUCTORS, ConstructorI::class.java)
    override fun constructors(vararg value: ConstructorI): CompilationUnitI = apply { constructors().addItems(value.asList()) }
    override fun constr(value: ConstructorI): ConstructorI = applyAndReturn { constructors().addItem(value); value }
    override fun constr(value: ConstructorI.() -> Unit): ConstructorI = constr(Constructor(value))

    override fun open(): Boolean = attr(OPEN, { true })
    override fun open(value: Boolean): CompilationUnitI = apply { attr(OPEN, value) }

    override fun operations(): ListMultiHolder<OperationI> = itemAsList(OPERATIONS, OperationI::class.java)
    override fun operations(vararg value: OperationI): CompilationUnitI = apply { operations().addItems(value.asList()) }
    override fun op(value: OperationI): OperationI = applyAndReturn { operations().addItem(value); value }
    override fun op(value: OperationI.() -> Unit): OperationI = op(Operation(value))

    override fun props(): ListMultiHolder<AttributeI> = itemAsList(PROPS, AttributeI::class.java)
    override fun props(vararg value: AttributeI): CompilationUnitI = apply { props().addItems(value.asList()) }
    override fun prop(value: AttributeI): AttributeI = applyAndReturn { props().addItem(value); value }
    override fun prop(value: AttributeI.() -> Unit): AttributeI = prop(Attribute(value))

    override fun superUnit(): CompilationUnitI = attr(SUPER_UNIT, { CompilationUnit.EMPTY })
    override fun superUnit(value: CompilationUnitI): CompilationUnitI = apply { attr(SUPER_UNIT, value) }

    override fun superUnitFor(): ListMultiHolder<CompilationUnitI> = itemAsList(SUPER_UNIT_FOR, CompilationUnitI::class.java)
    override fun superUnitFor(vararg value: CompilationUnitI): CompilationUnitI = apply { superUnitFor().addItems(value.asList()) }

    override fun virtual(): Boolean = attr(VIRTUAL, { false })
    override fun virtual(value: Boolean): CompilationUnitI = apply { attr(VIRTUAL, value) }

    companion object {
        val EMPTY = CompilationUnitEmpty
        val BASE = "_base"
        val CONSTRUCTORS = "_constructors"
        val OPEN = "_open"
        val OPERATIONS = "_operations"
        val PROPS = "_props"
        val SUPER_UNIT = "__superUnit"
        val SUPER_UNIT_FOR = "__superUnitFor"
        val VIRTUAL = "_virtual"
    }
}


open class Constructor : LogicUnit, ConstructorI {

    constructor(value: Constructor.() -> Unit = {}) : super(value as LogicUnit.() -> Unit)

    override fun primary(): Boolean = attr(PRIMARY, { false })
    override fun primary(value: Boolean): ConstructorI = apply { attr(PRIMARY, value) }

    companion object {
        val EMPTY = Constructor({ name(ItemEmpty.name()) })
        val PRIMARY = "_primary"
    }
}


open class EnumType : CompilationUnit, EnumTypeI {

    constructor(value: EnumType.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    override fun literals(): ListMultiHolder<LiteralI> = itemAsList(LITERALS, LiteralI::class.java)
    override fun literals(vararg value: LiteralI): EnumTypeI = apply { literals().addItems(value.asList()) }
    override fun lit(value: LiteralI): LiteralI = applyAndReturn { literals().addItem(value); value }
    override fun lit(value: LiteralI.() -> Unit): LiteralI = lit(Literal(value))

    companion object {
        val EMPTY = EnumType({ name(ItemEmpty.name()) })
        val LITERALS = "_literals"
    }
}


open class ExternalType : Type, ExternalTypeI {

    constructor(value: ExternalType.() -> Unit = {}) : super(value as Type.() -> Unit)

    companion object {
        val EMPTY = ExternalType({ name(ItemEmpty.name()) })
    }
}


open class Generic : Type, GenericI {

    constructor(value: Generic.() -> Unit = {}) : super(value as Type.() -> Unit)

    override fun type(): TypeI = attr(TYPE, { Type.EMPTY })
    override fun type(value: TypeI): GenericI = apply { attr(TYPE, value) }

    companion object {
        val EMPTY = Generic({ name(ItemEmpty.name()) })
        val TYPE = "_type"
    }
}


open class Lambda : Type, LambdaI {

    constructor(value: Lambda.() -> Unit = {}) : super(value as Type.() -> Unit)

    override fun operation(): OperationI = attr(OPERATION, { Operation.EMPTY })
    override fun operation(value: OperationI): LambdaI = apply { attr(OPERATION, value) }

    companion object {
        val EMPTY = Lambda({ name(ItemEmpty.name()) })
        val OPERATION = "_operation"
    }
}


open class Literal : LogicUnit, LiteralI {

    constructor(value: Literal.() -> Unit = {}) : super(value as LogicUnit.() -> Unit)

    companion object {
        val EMPTY = Literal({ name(ItemEmpty.name()) })
    }
}


open class LogicUnit : TextComposite, LogicUnitI {

    constructor(value: LogicUnit.() -> Unit = {}) : super(value as TextComposite.() -> Unit)

    override fun params(): ListMultiHolder<AttributeI> = itemAsList(PARAMS, AttributeI::class.java)
    override fun params(vararg value: AttributeI): LogicUnitI = apply { params().addItems(value.asList()) }

    override fun superUnit(): LogicUnitI = attr(SUPER_UNIT, { LogicUnit.EMPTY })
    override fun superUnit(value: LogicUnitI): LogicUnitI = apply { attr(SUPER_UNIT, value) }

    override fun virtual(): Boolean = attr(VIRTUAL, { false })
    override fun virtual(value: Boolean): LogicUnitI = apply { attr(VIRTUAL, value) }

    companion object {
        val EMPTY = LogicUnitEmpty
        val PARAMS = "_params"
        val SUPER_UNIT = "__superUnit"
        val VIRTUAL = "_virtual"
    }
}


open class NativeType : Type, NativeTypeI {

    constructor(value: NativeType.() -> Unit = {}) : super(value as Type.() -> Unit)

    companion object {
        val EMPTY = NativeType({ name(ItemEmpty.name()) })
    }
}


open class Operation : LogicUnit, OperationI {

    constructor(value: Operation.() -> Unit = {}) : super(value as LogicUnit.() -> Unit)

    override fun generics(): ListMultiHolder<GenericI> = itemAsList(GENERICS, GenericI::class.java)
    override fun generics(vararg value: GenericI): OperationI = apply { generics().addItems(value.asList()) }
    override fun G(value: GenericI): GenericI = applyAndReturn { generics().addItem(value); value }
    override fun G(value: GenericI.() -> Unit): GenericI = G(Generic(value))

    override fun open(): Boolean = attr(OPEN, { true })
    override fun open(value: Boolean): OperationI = apply { attr(OPEN, value) }

    override fun ret(): AttributeI = attr(RET, { Attribute.EMPTY })
    override fun ret(value: AttributeI): OperationI = apply { attr(RET, value) }
    override fun r(value: AttributeI): AttributeI = applyAndReturn { ret().addItem(value) }
    override fun r(value: AttributeI.() -> Unit): AttributeI = r(Attribute(value))

    companion object {
        val EMPTY = Operation({ name(ItemEmpty.name()) })
        val GENERICS = "_generics"
        val OPEN = "_open"
        val RET = "_ret"
    }
}


open class StructureUnit : Composite, StructureUnitI {

    constructor(value: StructureUnit.() -> Unit = {}) : super(value as Composite.() -> Unit)

    override fun artifact(): String = attr(ARTIFACT, { "" })
    override fun artifact(value: String): StructureUnitI = apply { attr(ARTIFACT, value) }

    override fun fullName(): String = attr(FULL_NAME, { "" })
    override fun fullName(value: String): StructureUnitI = apply { attr(FULL_NAME, value) }

    override fun key(): String = attr(KEY, { "" })
    override fun key(value: String): StructureUnitI = apply { attr(KEY, value) }

    companion object {
        val EMPTY = StructureUnit({ name(ItemEmpty.name()) })
        val ARTIFACT = "_artifact"
        val FULL_NAME = "_fullName"
        val KEY = "_key"
    }
}


open class TextComposite : Composite, TextCompositeI {

    constructor(value: TextComposite.() -> Unit = {}) : super(value as Composite.() -> Unit)

    companion object {
        val EMPTY = TextComposite({ name(ItemEmpty.name()) })
    }
}


open class Type : Composite, TypeI {

    constructor(value: Type.() -> Unit = {}) : super(value as Composite.() -> Unit)

    override fun defaultValue(): Any? = attr(DEFAULT_VALUE)
    override fun defaultValue(value: Any?): TypeI = apply { attr(DEFAULT_VALUE, value) }

    override fun generics(): ListMultiHolder<GenericI> = itemAsList(GENERICS, GenericI::class.java)
    override fun generics(vararg value: GenericI): TypeI = apply { generics().addItems(value.asList()) }
    override fun G(value: GenericI): GenericI = applyAndReturn { generics().addItem(value); value }
    override fun G(value: GenericI.() -> Unit): GenericI = G(Generic(value))

    override fun multi(): Boolean = attr(MULTI, { false })
    override fun multi(value: Boolean): TypeI = apply { attr(MULTI, value) }

    companion object {
        val EMPTY = Type({ name(ItemEmpty.name()) })
        val DEFAULT_VALUE = "_defaultValue"
        val GENERICS = "_generics"
        val MULTI = "_multi"
    }
}


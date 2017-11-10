package ee.lang


open class Attribute : Literal, AttributeI {

    constructor(value: Attribute.() -> Unit = {}) : super(value as Literal.() -> Unit)

    override fun accessible(): Boolean? = attr(ACCESSIBLE)
    override fun accessible(value: Boolean?): AttributeI = apply { attr(ACCESSIBLE, value) }

    override fun anonymous(): Boolean = attr(ANONYMOUS, { false })
    override fun anonymous(value: Boolean): AttributeI = apply { attr(ANONYMOUS, value) }

    override fun default(): Boolean = attr(DEFAULT, { false })
    override fun default(value: Boolean): AttributeI = apply { attr(DEFAULT, value) }

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

    override fun mutable(): Boolean? = attr(MUTABLE)
    override fun mutable(value: Boolean?): AttributeI = apply { attr(MUTABLE, value) }

    override fun nonFluent(): String = attr(NON_FLUENT, { "" })
    override fun nonFluent(value: String): AttributeI = apply { attr(NON_FLUENT, value) }

    override fun nullable(): Boolean = attr(NULLABLE, { false })
    override fun nullable(value: Boolean): AttributeI = apply { attr(NULLABLE, value) }

    override fun open(): Boolean = attr(OPEN, { false })
    override fun open(value: Boolean): AttributeI = apply { attr(OPEN, value) }

    override fun replaceable(): Boolean? = attr(REPLACEABLE)
    override fun replaceable(value: Boolean?): AttributeI = apply { attr(REPLACEABLE, value) }

    override fun type(): TypeI = attr(TYPE, { n.Void })
    override fun type(value: TypeI): AttributeI = apply { attr(TYPE, value) }

    override fun unique(): Boolean = attr(UNIQUE, { false })
    override fun unique(value: Boolean): AttributeI = apply { attr(UNIQUE, value) }

    override fun value(): Any? = attr(VALUE)
    override fun value(aValue: Any?): AttributeI = apply { attr(VALUE, aValue) }

    companion object {
        val EMPTY = Attribute { name(ItemEmpty.name()) }.apply<Attribute> { init() }
        val ACCESSIBLE = "_accessible"
        val ANONYMOUS = "_anonymous"
        val DEFAULT = "_default"
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

    companion object {
        val EMPTY = CompilationUnitEmpty
        val BASE = "_base"
    }
}


open class Constructor : LogicUnit, ConstructorI {

    constructor(value: Constructor.() -> Unit = {}) : super(value as LogicUnit.() -> Unit)

    override fun primary(): Boolean = attr(PRIMARY, { false })
    override fun primary(value: Boolean): ConstructorI = apply { attr(PRIMARY, value) }

    companion object {
        val EMPTY = Constructor { name(ItemEmpty.name()) }.apply<Constructor> { init() }
        val PRIMARY = "_primary"
    }
}


open class DataType : CompilationUnit, DataTypeI {

    constructor(value: DataType.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    companion object {
        val EMPTY = DataType { name(ItemEmpty.name()) }.apply<DataType> { init() }
    }
}


open class DataTypeOperation : Operation, DataTypeOperationI {

    constructor(value: DataTypeOperation.() -> Unit = {}) : super(value as Operation.() -> Unit)

    companion object {
        val EMPTY = DataTypeOperation { name(ItemEmpty.name()) }.apply<DataTypeOperation> { init() }
    }
}


open class EnumType : DataType, EnumTypeI {

    constructor(value: EnumType.() -> Unit = {}) : super(value as DataType.() -> Unit)

    override fun literals(): ListMultiHolder<LiteralI> = itemAsList(LITERALS, LiteralI::class.java, true)
    override fun literals(vararg value: LiteralI): EnumTypeI = apply { literals().addItems(value.asList()) }
    override fun lit(value: LiteralI): LiteralI = applyAndReturn { literals().addItem(value); value }
    override fun lit(value: LiteralI.() -> Unit): LiteralI = lit(Literal(value))

    override fun fillSupportsItems() {
        literals()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = EnumType { name(ItemEmpty.name()) }.apply<EnumType> { init() }
        val LITERALS = "_literals"
    }
}


open class Expression : MacroComposite, ExpressionI {

    constructor(value: Expression.() -> Unit = {}) : super(value as MacroComposite.() -> Unit)

    companion object {
        val EMPTY = Expression { name(ItemEmpty.name()) }.apply<Expression> { init() }
    }
}


open class ExternalType : Type, ExternalTypeI {

    constructor(value: ExternalType.() -> Unit = {}) : super(value as Type.() -> Unit)

    companion object {
        val EMPTY = ExternalType { name(ItemEmpty.name()) }.apply<ExternalType> { init() }
    }
}


open class Generic : Type, GenericI {

    constructor(value: Generic.() -> Unit = {}) : super(value as Type.() -> Unit)

    override fun type(): TypeI = attr(TYPE, { Type.EMPTY })
    override fun type(value: TypeI): GenericI = apply { attr(TYPE, value) }

    companion object {
        val EMPTY = Generic { name(ItemEmpty.name()) }.apply<Generic> { init() }
        val TYPE = "_type"
    }
}


open class Lambda : Type, LambdaI {

    constructor(value: Lambda.() -> Unit = {}) : super(value as Type.() -> Unit)

    override fun operation(): OperationI = attr(OPERATION, { Operation.EMPTY })
    override fun operation(value: OperationI): LambdaI = apply { attr(OPERATION, value) }

    companion object {
        val EMPTY = Lambda { name(ItemEmpty.name()) }.apply<Lambda> { init() }
        val OPERATION = "_operation"
    }
}


open class Literal : LogicUnit, LiteralI {

    constructor(value: Literal.() -> Unit = {}) : super(value as LogicUnit.() -> Unit)

    companion object {
        val EMPTY = Literal { name(ItemEmpty.name()) }.apply<Literal> { init() }
    }
}


open class LogicUnit : Expression, LogicUnitI {

    constructor(value: LogicUnit.() -> Unit = {}) : super(value as Expression.() -> Unit)

    override fun params(): ListMultiHolder<AttributeI> = itemAsList(PARAMS, AttributeI::class.java, true)
    override fun params(vararg value: AttributeI): LogicUnitI = apply { params().addItems(value.asList()) }

    override fun superUnit(): LogicUnitI = attr(SUPER_UNIT, { LogicUnit.EMPTY })
    override fun superUnit(value: LogicUnitI): LogicUnitI = apply { attr(SUPER_UNIT, value) }

    override fun virtual(): Boolean = attr(VIRTUAL, { false })
    override fun virtual(value: Boolean): LogicUnitI = apply { attr(VIRTUAL, value) }

    override fun visible(): Boolean = attr(VISIBLE, { true })
    override fun visible(value: Boolean): LogicUnitI = apply { attr(VISIBLE, value) }

    override fun fillSupportsItems() {
        params()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = LogicUnitEmpty
        val PARAMS = "_params"
        val SUPER_UNIT = "__superUnit"
        val VIRTUAL = "_virtual"
        val VISIBLE = "_visible"
    }
}


open class MacroComposite : Composite, MacroCompositeI {

    constructor(value: MacroComposite.() -> Unit = {}) : super(value as Composite.() -> Unit)

    override fun macrosAfter(): ListMultiHolder<String> = itemAsList(MACROS_AFTER, String::class.java, true)
    override fun macrosAfter(vararg value: String): MacroCompositeI = apply { macrosAfter().addItems(value.asList()) }

    override fun macrosAfterBody(): ListMultiHolder<String> = itemAsList(MACROS_AFTER_BODY, String::class.java, true)
    override fun macrosAfterBody(vararg value: String): MacroCompositeI = apply { macrosAfterBody().addItems(value.asList()) }

    override fun macrosBefore(): ListMultiHolder<String> = itemAsList(MACROS_BEFORE, String::class.java, true)
    override fun macrosBefore(vararg value: String): MacroCompositeI = apply { macrosBefore().addItems(value.asList()) }

    override fun macrosBeforeBody(): ListMultiHolder<String> = itemAsList(MACROS_BEFORE_BODY, String::class.java, true)
    override fun macrosBeforeBody(vararg value: String): MacroCompositeI = apply { macrosBeforeBody().addItems(value.asList()) }

    override fun macrosBody(): ListMultiHolder<String> = itemAsList(MACROS_BODY, String::class.java, true)
    override fun macrosBody(vararg value: String): MacroCompositeI = apply { macrosBody().addItems(value.asList()) }

    override fun tags(): ListMultiHolder<String> = itemAsList(TAGS, String::class.java, true)
    override fun tags(vararg value: String): MacroCompositeI = apply { tags().addItems(value.asList()) }

    override fun fillSupportsItems() {
        macrosAfter()
        macrosAfterBody()
        macrosBefore()
        macrosBeforeBody()
        macrosBody()
        tags()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = MacroComposite { name(ItemEmpty.name()) }.apply<MacroComposite> { init() }
        val MACROS_AFTER = "_macrosAfter"
        val MACROS_AFTER_BODY = "_macrosAfterBody"
        val MACROS_BEFORE = "_macrosBefore"
        val MACROS_BEFORE_BODY = "_macrosBeforeBody"
        val MACROS_BODY = "_macrosBody"
        val TAGS = "_tags"
    }
}


open class NativeType : Type, NativeTypeI {

    constructor(value: NativeType.() -> Unit = {}) : super(value as Type.() -> Unit)

    companion object {
        val EMPTY = NativeType { name(ItemEmpty.name()) }.apply<NativeType> { init() }
    }
}


open class Operation : LogicUnit, OperationI {

    constructor(value: Operation.() -> Unit = {}) : super(value as LogicUnit.() -> Unit)

    override fun generics(): ListMultiHolder<GenericI> = itemAsList(GENERICS, GenericI::class.java, true)
    override fun generics(vararg value: GenericI): OperationI = apply { generics().addItems(value.asList()) }
    override fun G(value: GenericI): GenericI = applyAndReturn { generics().addItem(value); value }
    override fun G(value: GenericI.() -> Unit): GenericI = G(Generic(value))

    override fun open(): Boolean = attr(OPEN, { true })
    override fun open(value: Boolean): OperationI = apply { attr(OPEN, value) }

    override fun returns(): ListMultiHolder<AttributeI> = itemAsList(RETURNS, AttributeI::class.java, true)
    override fun returns(vararg value: AttributeI): OperationI = apply { returns().addItems(value.asList()) }
    override fun ret(value: AttributeI): AttributeI = applyAndReturn { returns().addItem(value); value }
    override fun ret(value: AttributeI.() -> Unit): AttributeI = ret(Attribute(value))

    override fun fillSupportsItems() {
        generics()
        returns()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Operation { name(ItemEmpty.name()) }.apply<Operation> { init() }
        val GENERICS = "_generics"
        val OPEN = "_open"
        val RETURNS = "_returns"
    }
}


open class StructureUnit : MacroComposite, StructureUnitI {

    constructor(value: StructureUnit.() -> Unit = {}) : super(value as MacroComposite.() -> Unit)

    override fun artifact(): String = attr(ARTIFACT, { "" })
    override fun artifact(value: String): StructureUnitI = apply { attr(ARTIFACT, value) }

    override fun fullName(): String = attr(FULL_NAME, { "" })
    override fun fullName(value: String): StructureUnitI = apply { attr(FULL_NAME, value) }

    override fun key(): String = attr(KEY, { "" })
    override fun key(value: String): StructureUnitI = apply { attr(KEY, value) }

    companion object {
        val EMPTY = StructureUnit { name(ItemEmpty.name()) }.apply<StructureUnit> { init() }
        val ARTIFACT = "_artifact"
        val FULL_NAME = "_fullName"
        val KEY = "_key"
    }
}


open class Type : MacroComposite, TypeI {

    constructor(value: Type.() -> Unit = {}) : super(value as MacroComposite.() -> Unit)

    override fun constructors(): ListMultiHolder<ConstructorI> = itemAsList(CONSTRUCTORS, ConstructorI::class.java, true)
    override fun constructors(vararg value: ConstructorI): TypeI = apply { constructors().addItems(value.asList()) }
    override fun constr(value: ConstructorI): ConstructorI = applyAndReturn { constructors().addItem(value); value }
    override fun constr(value: ConstructorI.() -> Unit): ConstructorI = constr(Constructor(value))

    override fun defaultValue(): Any? = attr(DEFAULT_VALUE)
    override fun defaultValue(value: Any?): TypeI = apply { attr(DEFAULT_VALUE, value) }

    override fun generics(): ListMultiHolder<GenericI> = itemAsList(GENERICS, GenericI::class.java, true)
    override fun generics(vararg value: GenericI): TypeI = apply { generics().addItems(value.asList()) }
    override fun G(value: GenericI): GenericI = applyAndReturn { generics().addItem(value); value }
    override fun G(value: GenericI.() -> Unit): GenericI = G(Generic(value))

    override fun ifc(): Boolean = attr(IFC, { false })
    override fun ifc(value: Boolean): TypeI = apply { attr(IFC, value) }

    override fun multi(): Boolean = attr(MULTI, { false })
    override fun multi(value: Boolean): TypeI = apply { attr(MULTI, value) }

    override fun open(): Boolean = attr(OPEN, { true })
    override fun open(value: Boolean): TypeI = apply { attr(OPEN, value) }

    override fun operations(): ListMultiHolder<OperationI> = itemAsList(OPERATIONS, OperationI::class.java, true)
    override fun operations(vararg value: OperationI): TypeI = apply { operations().addItems(value.asList()) }
    override fun op(value: OperationI): OperationI = applyAndReturn { operations().addItem(value); value }
    override fun op(value: OperationI.() -> Unit): OperationI = op(Operation(value))

    override fun props(): ListMultiHolder<AttributeI> = itemAsList(PROPS, AttributeI::class.java, true)
    override fun props(vararg value: AttributeI): TypeI = apply { props().addItems(value.asList()) }
    override fun prop(value: AttributeI): AttributeI = applyAndReturn { props().addItem(value); value }
    override fun prop(value: AttributeI.() -> Unit): AttributeI = prop(Attribute(value))

    override fun superUnit(): CompilationUnitI = attr(SUPER_UNIT, { CompilationUnit.EMPTY })
    override fun superUnit(value: CompilationUnitI): TypeI = apply { attr(SUPER_UNIT, value) }

    override fun superUnitFor(): ListMultiHolder<CompilationUnitI> = itemAsList(SUPER_UNIT_FOR, CompilationUnitI::class.java, true)
    override fun superUnitFor(vararg value: CompilationUnitI): TypeI = apply { superUnitFor().addItems(value.asList()) }

    override fun virtual(): Boolean = attr(VIRTUAL, { false })
    override fun virtual(value: Boolean): TypeI = apply { attr(VIRTUAL, value) }

    override fun fillSupportsItems() {
        constructors()
        generics()
        operations()
        props()
        superUnitFor()
        super.fillSupportsItems()
    }

    companion object {
        val EMPTY = Type { name(ItemEmpty.name()) }.apply<Type> { init() }
        val CONSTRUCTORS = "_constructors"
        val DEFAULT_VALUE = "_defaultValue"
        val GENERICS = "_generics"
        val IFC = "_ifc"
        val MULTI = "_multi"
        val OPEN = "_open"
        val OPERATIONS = "_operations"
        val PROPS = "_props"
        val SUPER_UNIT = "__superUnit"
        val SUPER_UNIT_FOR = "__superUnitFor"
        val VIRTUAL = "_virtual"
    }
}


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

    override fun nonFluent(): String = attr(NON_FLUENT, {  })
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
        val EMPTY = Attribute()
        val ACCESSIBLE = "accessible"
        val HIDDEN = "hidden"
        val INHERITED = "inherited"
        val INIT_BY_DEFAULT_TYPE_VALUE = "initByDefaultTypeValue"
        val KEY = "key"
        val LENGTH = "length"
        val META = "meta"
        val MULTI = "multi"
        val MUTABLE = "mutable"
        val NON_FLUENT = "nonFluent"
        val NULLABLE = "nullable"
        val OPEN = "open"
        val REPLACEABLE = "replaceable"
        val TYPE = "type"
        val UNIQUE = "unique"
        val VALUE = "value"
    }
}


fun AttributeI?.isEmpty(): Boolean = (this == null || this == Attribute.EMPTY)
fun AttributeI?.isNotEmpty(): Boolean = !isEmpty()


open class CompilationUnit : Type, CompilationUnitI {

    constructor(value: CompilationUnit.() -> Unit = {}) : super(value as Type.() -> Unit)

    override fun base(): Boolean = attr(BASE, { false })
    override fun base(value: Boolean): CompilationUnitI = apply { attr(BASE, value) }

    override fun constructors(): List<ConstructorI> = itemAsList(CONSTRUCTORS, ConstructorI)
    override fun constructors(vararg value: ConstructorI): CompilationUnitI = apply { constructors.addAll(value) }
    override fun constr(value: ConstructorI): ConstructorI = applyAndReturn { _constructors.add(value); value }
    override fun constr(value: ConstructorI.() -> Unit) : ConstructorI = constr(Constructor(value))

    override fun open(): Boolean = attr(OPEN, { true })
    override fun open(value: Boolean): CompilationUnitI = apply { attr(OPEN, value) }

    override fun operations(): List<OperationI> = itemAsList(OPERATIONS, OperationI)
    override fun operations(vararg value: OperationI): CompilationUnitI = apply { operations.addAll(value) }
    override fun op(value: OperationI): OperationI = applyAndReturn { _operations.add(value); value }
    override fun op(value: OperationI.() -> Unit) : OperationI = op(Operation(value))

    override fun props(): List<AttributeI> = itemAsList(PROPS, AttributeI)
    override fun props(vararg value: AttributeI): CompilationUnitI = apply { props.addAll(value) }
    override fun prop(value: AttributeI): AttributeI = applyAndReturn { _props.add(value); value }
    override fun prop(value: AttributeI.() -> Unit) : AttributeI = prop(Attribute(value))

    override fun superUnit(): CompilationUnitI = attr(SUPER_UNIT, {  })
    override fun superUnit(value: CompilationUnitI): CompilationUnitI = apply { attr(SUPER_UNIT, value) }

    override fun superUnitFor(): List<CompilationUnitI> = itemAsList(SUPER_UNIT_FOR, CompilationUnitI)
    override fun superUnitFor(vararg value: CompilationUnitI): CompilationUnitI = apply { superUnitFor.addAll(value) }

    override fun virtual(): Boolean = attr(VIRTUAL, { false })
    override fun virtual(value: Boolean): CompilationUnitI = apply { attr(VIRTUAL, value) }

    companion object {
        val EMPTY = CompilationUnitEmpty
        val BASE = "base"
        val CONSTRUCTORS = "constructors"
        val OPEN = "open"
        val OPERATIONS = "operations"
        val PROPS = "props"
        val SUPER_UNIT = "superUnit"
        val SUPER_UNIT_FOR = "superUnitFor"
        val VIRTUAL = "virtual"
    }
}


fun CompilationUnitI?.isEmpty(): Boolean = (this == null || this == CompilationUnit.EMPTY)
fun CompilationUnitI?.isNotEmpty(): Boolean = !isEmpty()


open class Constructor : LogicUnit, ConstructorI {

    constructor(value: Constructor.() -> Unit = {}) : super(value as LogicUnit.() -> Unit)

    override fun primary(): Boolean = attr(PRIMARY, { false })
    override fun primary(value: Boolean): ConstructorI = apply { attr(PRIMARY, value) }

    companion object {
        val EMPTY = Constructor()
        val PRIMARY = "primary"
    }
}


fun ConstructorI?.isEmpty(): Boolean = (this == null || this == Constructor.EMPTY)
fun ConstructorI?.isNotEmpty(): Boolean = !isEmpty()


open class EnumType : CompilationUnit, EnumTypeI {

    constructor(value: EnumType.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    override fun literals(): List<LiteralI> = itemAsList(LITERALS, LiteralI)
    override fun literals(vararg value: LiteralI): EnumTypeI = apply { literals.addAll(value) }
    override fun lit(value: LiteralI): LiteralI = applyAndReturn { _literals.add(value); value }
    override fun lit(value: LiteralI.() -> Unit) : LiteralI = lit(Literal(value))

    companion object {
        val EMPTY = EnumType()
        val LITERALS = "literals"
    }
}


fun EnumTypeI?.isEmpty(): Boolean = (this == null || this == EnumType.EMPTY)
fun EnumTypeI?.isNotEmpty(): Boolean = !isEmpty()


open class ExternalType : Type, ExternalTypeI {

    constructor(value: ExternalType.() -> Unit = {}) : super(value as Type.() -> Unit)

    companion object {
        val EMPTY = ExternalType()
    }
}


fun ExternalTypeI?.isEmpty(): Boolean = (this == null || this == ExternalType.EMPTY)
fun ExternalTypeI?.isNotEmpty(): Boolean = !isEmpty()


open class Generic : Type, GenericI {

    constructor(value: Generic.() -> Unit = {}) : super(value as Type.() -> Unit)

    override fun type(): TypeI = attr(TYPE, {  })
    override fun type(value: TypeI): GenericI = apply { attr(TYPE, value) }

    companion object {
        val EMPTY = Generic()
        val TYPE = "type"
    }
}


fun GenericI?.isEmpty(): Boolean = (this == null || this == Generic.EMPTY)
fun GenericI?.isNotEmpty(): Boolean = !isEmpty()


open class Lambda : Type, LambdaI {

    constructor(value: Lambda.() -> Unit = {}) : super(value as Type.() -> Unit)

    override fun operation(): OperationI = attr(OPERATION, {  })
    override fun operation(value: OperationI): LambdaI = apply { attr(OPERATION, value) }

    companion object {
        val EMPTY = Lambda()
        val OPERATION = "operation"
    }
}


fun LambdaI?.isEmpty(): Boolean = (this == null || this == Lambda.EMPTY)
fun LambdaI?.isNotEmpty(): Boolean = !isEmpty()


open class Literal : LogicUnit, LiteralI {

    constructor(value: Literal.() -> Unit = {}) : super(value as LogicUnit.() -> Unit)

    companion object {
        val EMPTY = Literal()
    }
}


fun LiteralI?.isEmpty(): Boolean = (this == null || this == Literal.EMPTY)
fun LiteralI?.isNotEmpty(): Boolean = !isEmpty()


open class LogicUnit : TextComposite, LogicUnitI {

    constructor(value: LogicUnit.() -> Unit = {}) : super(value as TextComposite.() -> Unit)

    override fun params(): List<AttributeI> = itemAsList(PARAMS, AttributeI)
    override fun params(vararg value: AttributeI): LogicUnitI = apply { params.addAll(value) }

    override fun superUnit(): LogicUnitI = attr(SUPER_UNIT, {  })
    override fun superUnit(value: LogicUnitI): LogicUnitI = apply { attr(SUPER_UNIT, value) }

    override fun virtual(): Boolean = attr(VIRTUAL, { false })
    override fun virtual(value: Boolean): LogicUnitI = apply { attr(VIRTUAL, value) }

    companion object {
        val EMPTY = LogicUnitEmpty
        val PARAMS = "params"
        val SUPER_UNIT = "superUnit"
        val VIRTUAL = "virtual"
    }
}


fun LogicUnitI?.isEmpty(): Boolean = (this == null || this == LogicUnit.EMPTY)
fun LogicUnitI?.isNotEmpty(): Boolean = !isEmpty()


open class NativeType : Type, NativeTypeI {

    constructor(value: NativeType.() -> Unit = {}) : super(value as Type.() -> Unit)

    companion object {
        val EMPTY = NativeType()
    }
}


fun NativeTypeI?.isEmpty(): Boolean = (this == null || this == NativeType.EMPTY)
fun NativeTypeI?.isNotEmpty(): Boolean = !isEmpty()


open class Operation : LogicUnit, OperationI {

    constructor(value: Operation.() -> Unit = {}) : super(value as LogicUnit.() -> Unit)

    override fun generics(): List<GenericI> = itemAsList(GENERICS, GenericI)
    override fun generics(vararg value: GenericI): OperationI = apply { generics.addAll(value) }
    override fun G(value: GenericI): GenericI = applyAndReturn { _generics.add(value); value }
    override fun G(value: GenericI.() -> Unit) : GenericI = G(Generic(value))

    override fun open(): Boolean = attr(OPEN, { true })
    override fun open(value: Boolean): OperationI = apply { attr(OPEN, value) }

    override fun ret(): AttributeI = attr(RET, {  })
    override fun ret(value: AttributeI): OperationI = apply { attr(RET, value) }
    override fun r(value: AttributeI): AttributeI = applyAndReturn { _ret.value(value) }
    override fun r(value: AttributeI.() -> Unit) : AttributeI = r(Attribute(value))

    companion object {
        val EMPTY = Operation()
        val GENERICS = "generics"
        val OPEN = "open"
        val RET = "ret"
    }
}


fun OperationI?.isEmpty(): Boolean = (this == null || this == Operation.EMPTY)
fun OperationI?.isNotEmpty(): Boolean = !isEmpty()


open class StructureUnit : Composite, StructureUnitI {

    constructor(value: StructureUnit.() -> Unit = {}) : super(value as Composite.() -> Unit)

    override fun artifact(): String = attr(ARTIFACT, {  })
    override fun artifact(value: String): StructureUnitI = apply { attr(ARTIFACT, value) }

    override fun fullName(): String = attr(FULL_NAME, {  })
    override fun fullName(value: String): StructureUnitI = apply { attr(FULL_NAME, value) }

    override fun key(): String = attr(KEY, {  })
    override fun key(value: String): StructureUnitI = apply { attr(KEY, value) }

    companion object {
        val EMPTY = StructureUnit()
        val ARTIFACT = "artifact"
        val FULL_NAME = "fullName"
        val KEY = "key"
    }
}


fun StructureUnitI?.isEmpty(): Boolean = (this == null || this == StructureUnit.EMPTY)
fun StructureUnitI?.isNotEmpty(): Boolean = !isEmpty()


open class TextComposite : Composite, TextCompositeI {

    constructor(value: TextComposite.() -> Unit = {}) : super(value as Composite.() -> Unit)

    companion object {
        val EMPTY = TextComposite()
    }
}


fun TextCompositeI?.isEmpty(): Boolean = (this == null || this == TextComposite.EMPTY)
fun TextCompositeI?.isNotEmpty(): Boolean = !isEmpty()


open class Type : Composite, TypeI {

    constructor(value: Type.() -> Unit = {}) : super(value as Composite.() -> Unit)

    override fun defaultValue(): Any? = attr(DEFAULT_VALUE)
    override fun defaultValue(value: Any?): TypeI = apply { attr(DEFAULT_VALUE, value) }

    override fun generics(): List<GenericI> = itemAsList(GENERICS, GenericI)
    override fun generics(vararg value: GenericI): TypeI = apply { generics.addAll(value) }
    override fun G(value: GenericI): GenericI = applyAndReturn { _generics.add(value); value }
    override fun G(value: GenericI.() -> Unit) : GenericI = G(Generic(value))

    override fun multi(): Boolean = attr(MULTI, { false })
    override fun multi(value: Boolean): TypeI = apply { attr(MULTI, value) }

    companion object {
        val EMPTY = Type()
        val DEFAULT_VALUE = "defaultValue"
        val GENERICS = "generics"
        val MULTI = "multi"
    }
}


fun TypeI?.isEmpty(): Boolean = (this == null || this == Type.EMPTY)
fun TypeI?.isNotEmpty(): Boolean = !isEmpty()


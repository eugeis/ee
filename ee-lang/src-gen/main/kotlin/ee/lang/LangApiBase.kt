package ee.lang


open class Attribute : Composite, AttributeI {
    private var _type: ValueHolderI<TypeI> = add(ValueHolder(n.String as TypeI, { name("type") }))
    private var _key: ValueHolderI<Boolean> = add(ValueHolder(false, { name("key") }))
    private var _unique: ValueHolderI<Boolean> = add(ValueHolder(false, { name("unique") }))
    private var _value: NullValueHolderI<Any> = add(NullValueHolder({ name("value") }))
    private var _initByDefaultTypeValue: ValueHolderI<Boolean> = add(ValueHolder(true, { name("initByDefaultTypeValue") }))
    private var _nullable: ValueHolderI<Boolean> = add(ValueHolder(false, { name("nullable") }))
    private var _accessible: ValueHolderI<Boolean> = add(ValueHolder(true, { name("accessible") }))
    private var _replaceable: ValueHolderI<Boolean> = add(ValueHolder(false, { name("replaceable") }))
    private var _meta: ValueHolderI<Boolean> = add(ValueHolder(false, { name("meta") }))
    private var _multi: ValueHolderI<Boolean> = add(ValueHolder(false, { name("multi") }))
    private var _hidden: ValueHolderI<Boolean> = add(ValueHolder(false, { name("hidden") }))
    private var _mutable: ValueHolderI<Boolean> = add(ValueHolder(true, { name("mutable") }))
    private var _length: NullValueHolderI<Int> = add(NullValueHolder({ name("length") }))
    private var _inherited: ValueHolderI<Boolean> = add(ValueHolder(false, { name("inherited") }))
    private var _open: ValueHolderI<Boolean> = add(ValueHolder(false, { name("open") }))
    private var _nonFluent: ValueHolderI<String> = add(ValueHolder("" as String, { name("nonFluent") }))

    constructor(value: Attribute.() -> Unit = {}) : super(value as Composite.() -> Unit, {

    })

    override fun type(): TypeI = _type.value()
    override fun type(value: TypeI): AttributeI = apply { _type.value(value) }

    override fun key(): Boolean = _key.value()
    override fun key(value: Boolean): AttributeI = apply { _key.value(value) }

    override fun unique(): Boolean = _unique.value()
    override fun unique(value: Boolean): AttributeI = apply { _unique.value(value) }

    override fun value(): Any? = _value.value()
    override fun value(aValue: Any?): AttributeI = apply { _value.value(aValue) }

    override fun initByDefaultTypeValue(): Boolean = _initByDefaultTypeValue.value()
    override fun initByDefaultTypeValue(value: Boolean): AttributeI = apply { _initByDefaultTypeValue.value(value) }

    override fun nullable(): Boolean = _nullable.value()
    override fun nullable(value: Boolean): AttributeI = apply { _nullable.value(value) }

    override fun accessible(): Boolean = _accessible.value()
    override fun accessible(value: Boolean): AttributeI = apply { _accessible.value(value) }

    override fun replaceable(): Boolean = _replaceable.value()
    override fun replaceable(value: Boolean): AttributeI = apply { _replaceable.value(value) }

    override fun meta(): Boolean = _meta.value()
    override fun meta(value: Boolean): AttributeI = apply { _meta.value(value) }

    override fun multi(): Boolean = _multi.value()
    override fun multi(value: Boolean): AttributeI = apply { _multi.value(value) }

    override fun hidden(): Boolean = _hidden.value()
    override fun hidden(value: Boolean): AttributeI = apply { _hidden.value(value) }

    override fun mutable(): Boolean = _mutable.value()
    override fun mutable(value: Boolean): AttributeI = apply { _mutable.value(value) }

    override fun length(): Int? = _length.value()
    override fun length(value: Int?): AttributeI = apply { _length.value(value) }

    override fun inherited(): Boolean = _inherited.value()
    override fun inherited(value: Boolean): AttributeI = apply { _inherited.value(value) }

    override fun open(): Boolean = _open.value()
    override fun open(value: Boolean): AttributeI = apply { _open.value(value) }

    override fun nonFluent(): String = _nonFluent.value()
    override fun nonFluent(value: String): AttributeI = apply { _nonFluent.value(value) }

    companion object {
        val EMPTY = Attribute()
    }
}


fun AttributeI?.isEmpty(): Boolean = (this == null || this == Attribute.EMPTY)
fun AttributeI?.isNotEmpty(): Boolean = !isEmpty()


open class CompilationUnit : Type, CompilationUnitI {
    private var _base: ValueHolderI<Boolean> = add(ValueHolder(false, { name("base") }))
    private var _open: ValueHolderI<Boolean> = add(ValueHolder(true, { name("open") }))
    private var _virtual: ValueHolderI<Boolean> = add(ValueHolder(false, { name("virtual") }))
    private var _superUnitFor: CompilationUnits = add(CompilationUnits({ name("superUnitFor") }))
    private var _superUnit: ValueHolderI<CompilationUnitI> = add(ValueHolder(CompilationUnit.EMPTY as CompilationUnitI, { name("superUnit") }))
    private var _props: Attributes = add(Attributes({ name("props") }))
    private var _operations: Operations = add(Operations({ name("operations") }))
    private var _constructors: Constructors = add(Constructors({ name("constructors") }))

    constructor(value: CompilationUnit.() -> Unit = {}) : super(value as Type.() -> Unit)

    override fun base(): Boolean = _base.value()
    override fun base(value: Boolean): CompilationUnitI = apply { _base.value(value) }

    override fun open(): Boolean = _open.value()
    override fun open(value: Boolean): CompilationUnitI = apply { _open.value(value) }

    override fun virtual(): Boolean = _virtual.value()
    override fun virtual(value: Boolean): CompilationUnitI = apply { _virtual.value(value) }

    override fun superUnitFor(): List<CompilationUnitI> = _superUnitFor.items()
    override fun superUnitFor(vararg value: CompilationUnitI): CompilationUnitI = apply { _superUnitFor.addAll(value.toList()) }
    override fun superUnitFor(value: CompilationUnits): CompilationUnitI = apply { _superUnitFor = value }

    override fun superUnit(): CompilationUnitI = _superUnit.value()
    override fun superUnit(value: CompilationUnitI): CompilationUnitI = apply { _superUnit.value(value) }

    override fun props(): List<AttributeI> = _props.items()
    override fun props(vararg value: AttributeI): CompilationUnitI = apply { _props.addAll(value.toList()) }
    override fun props(value: Attributes): CompilationUnitI = apply { _props = value }
    override fun prop(value: AttributeI): AttributeI = applyAndReturn { _props.add(value); value }
    override fun prop(value: AttributeI.() -> Unit): AttributeI = prop(Attribute(value))

    override fun operations(): List<OperationI> = _operations.items()
    override fun operations(vararg value: OperationI): CompilationUnitI = apply { _operations.addAll(value.toList()) }
    override fun operations(value: Operations): CompilationUnitI = apply { _operations = value }
    override fun op(value: OperationI): OperationI = applyAndReturn { _operations.add(value); value }
    override fun op(value: OperationI.() -> Unit): OperationI = op(Operation(value))

    override fun constructors(): List<ConstructorI> = _constructors.items()
    override fun constructors(vararg value: ConstructorI): CompilationUnitI = apply { _constructors.addAll(value.toList()) }
    override fun constructors(value: Constructors): CompilationUnitI = apply { _constructors = value }
    override fun constr(value: ConstructorI): ConstructorI = applyAndReturn { _constructors.add(value); value }
    override fun constr(value: ConstructorI.() -> Unit): ConstructorI = constr(Constructor(value))

    companion object {
        val EMPTY = CompilationUnitEmpty
    }
}


fun CompilationUnitI?.isEmpty(): Boolean = (this == null || this == CompilationUnit.EMPTY)
fun CompilationUnitI?.isNotEmpty(): Boolean = !isEmpty()


open class Constructor : LogicUnit, ConstructorI {
    private var _primary: ValueHolderI<Boolean> = add(ValueHolder(false, { name("primary") }))

    constructor(value: Constructor.() -> Unit = {}) : super(value as LogicUnit.() -> Unit)

    override fun primary(): Boolean = _primary.value()
    override fun primary(value: Boolean): ConstructorI = apply { _primary.value(value) }

    companion object {
        val EMPTY = Constructor()
    }
}


fun ConstructorI?.isEmpty(): Boolean = (this == null || this == Constructor.EMPTY)
fun ConstructorI?.isNotEmpty(): Boolean = !isEmpty()


open class EnumType : CompilationUnit, EnumTypeI {
    private var _literals: Literals = add(Literals({ name("literals") }))

    constructor(value: EnumType.() -> Unit = {}) : super(value as CompilationUnit.() -> Unit)

    override fun literals(): List<LiteralI> = _literals.items()
    override fun literals(vararg value: LiteralI): EnumTypeI = apply { _literals.addAll(value.toList()) }
    override fun literals(value: Literals): EnumTypeI = apply { _literals = value }
    override fun lit(value: LiteralI): LiteralI = applyAndReturn { _literals.add(value); value }
    override fun lit(value: LiteralI.() -> Unit): LiteralI = lit(Literal(value))

    companion object {
        val EMPTY = EnumType()
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
    private var _type: ValueHolderI<TypeI> = add(ValueHolder(Type.EMPTY as TypeI, { name("type") }))

    constructor(value: Generic.() -> Unit = {}) : super(value as Type.() -> Unit)

    override fun type(): TypeI = _type.value()
    override fun type(value: TypeI): GenericI = apply { _type.value(value) }

    companion object {
        val EMPTY = Generic()
    }
}


fun GenericI?.isEmpty(): Boolean = (this == null || this == Generic.EMPTY)
fun GenericI?.isNotEmpty(): Boolean = !isEmpty()


open class Lambda : Type, LambdaI {
    private var _operation: ValueHolderI<OperationI> = add(ValueHolder(Operation.EMPTY as OperationI, { name("operation") }))

    constructor(value: Lambda.() -> Unit = {}) : super(value as Type.() -> Unit)

    override fun operation(): OperationI = _operation.value()
    override fun operation(value: OperationI): LambdaI = apply { _operation.value(value) }

    companion object {
        val EMPTY = Lambda()
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
    private var _virtual: ValueHolderI<Boolean> = add(ValueHolder(false, { name("virtual") }))
    private var _superUnit: ValueHolderI<LogicUnitI> = add(ValueHolder(LogicUnit.EMPTY as LogicUnitI, { name("superUnit") }))
    private var _params: Attributes = add(Attributes({ name("params") }))

    constructor(value: LogicUnit.() -> Unit = {}) : super(value as TextComposite.() -> Unit)

    override fun virtual(): Boolean = _virtual.value()
    override fun virtual(value: Boolean): LogicUnitI = apply { _virtual.value(value) }

    override fun superUnit(): LogicUnitI = _superUnit.value()
    override fun superUnit(value: LogicUnitI): LogicUnitI = apply { _superUnit.value(value) }

    override fun params(): List<AttributeI> = _params.items()
    override fun params(vararg value: AttributeI): LogicUnitI = apply { _params.addAll(value.toList()) }
    override fun params(value: Attributes): LogicUnitI = apply { _params = value }

    companion object {
        val EMPTY = LogicUnitEmpty
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
    private var _generics: Generics = add(Generics({ name("generics") }))
    private var _ret: ValueHolderI<AttributeI> = add(ValueHolder(Attribute.EMPTY as AttributeI, { name("ret") }))
    private var _open: ValueHolderI<Boolean> = add(ValueHolder(true, { name("open") }))

    constructor(value: Operation.() -> Unit = {}) : super(value as LogicUnit.() -> Unit)

    override fun generics(): List<GenericI> = _generics.items()
    override fun generics(vararg value: GenericI): OperationI = apply { _generics.addAll(value.toList()) }
    override fun generics(value: Generics): OperationI = apply { _generics = value }
    override fun G(value: GenericI): GenericI = applyAndReturn { _generics.add(value); value }
    override fun G(value: GenericI.() -> Unit): GenericI = G(Generic(value))

    override fun ret(): AttributeI = _ret.value()
    override fun ret(value: AttributeI): OperationI = apply { _ret.value(value) }
    override fun r(value: AttributeI): AttributeI = applyAndReturn { _ret.value(value) }
    override fun r(value: AttributeI.() -> Unit): AttributeI = r(Attribute(value))

    override fun open(): Boolean = _open.value()
    override fun open(value: Boolean): OperationI = apply { _open.value(value) }

    companion object {
        val EMPTY = Operation()
    }
}


fun OperationI?.isEmpty(): Boolean = (this == null || this == Operation.EMPTY)
fun OperationI?.isNotEmpty(): Boolean = !isEmpty()


open class StructureUnit : Composite, StructureUnitI {
    private var _key: ValueHolderI<String> = add(ValueHolder("" as String, { name("key") }))
    private var _fullName: ValueHolderI<String> = add(ValueHolder("" as String, { name("fullName") }))
    private var _artifact: ValueHolderI<String> = add(ValueHolder("" as String, { name("artifact") }))

    constructor(value: StructureUnit.() -> Unit = {}) : super(value as Composite.() -> Unit)

    override fun key(): String = _key.value()
    override fun key(value: String): StructureUnitI = apply { _key.value(value) }

    override fun fullName(): String = _fullName.value()
    override fun fullName(value: String): StructureUnitI = apply { _fullName.value(value) }

    override fun artifact(): String = _artifact.value()
    override fun artifact(value: String): StructureUnitI = apply { _artifact.value(value) }

    companion object {
        val EMPTY = StructureUnit()
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
    private var _generics: Generics = add(Generics({ name("generics") }))
    private var _multi: ValueHolderI<Boolean> = add(ValueHolder(false, { name("multi") }))
    private var _defaultValue: NullValueHolderI<Any> = add(NullValueHolder({ name("defaultValue") }))

    constructor(value: Type.() -> Unit = {}) : super(value as Composite.() -> Unit)

    override fun generics(): List<GenericI> = _generics.items()
    override fun generics(vararg value: GenericI): TypeI = apply { _generics.addAll(value.toList()) }
    override fun generics(value: Generics): TypeI = apply { _generics = value }
    override fun G(value: GenericI): GenericI = applyAndReturn { _generics.add(value); value }
    override fun G(value: GenericI.() -> Unit): GenericI = G(Generic(value))

    override fun multi(): Boolean = _multi.value()
    override fun multi(value: Boolean): TypeI = apply { _multi.value(value) }

    override fun defaultValue(): Any? = _defaultValue.value()
    override fun defaultValue(value: Any?): TypeI = apply { _defaultValue.value(value) }

    companion object {
        val EMPTY = Type()
    }
}


fun TypeI?.isEmpty(): Boolean = (this == null || this == Type.EMPTY)
fun TypeI?.isNotEmpty(): Boolean = !isEmpty()


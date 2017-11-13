package ee.lang


open class Attribute(value: Attribute.() -> Unit = {}) : AttributeB<Attribute>(value) {

    companion object {
        val EMPTY = Attribute { name(ItemEmpty.name()) }.apply<Attribute> { init() }
    }
}

open class AttributeB<B : AttributeIB<B>>(value: B.() -> Unit = {}) : LiteralB<B>(value), AttributeIB<B> {

    override fun accessible(): Boolean? = attr(ACCESSIBLE)
    override fun accessible(value: Boolean?): B = apply { attr(ACCESSIBLE, value) }

    override fun anonymous(): Boolean = attr(ANONYMOUS, { false })
    override fun anonymous(value: Boolean): B = apply { attr(ANONYMOUS, value) }

    override fun default(): Boolean = attr(DEFAULT, { false })
    override fun default(value: Boolean): B = apply { attr(DEFAULT, value) }

    override fun hidden(): Boolean = attr(HIDDEN, { false })
    override fun hidden(value: Boolean): B = apply { attr(HIDDEN, value) }

    override fun inherited(): Boolean = attr(INHERITED, { false })
    override fun inherited(value: Boolean): B = apply { attr(INHERITED, value) }

    override fun initByDefaultTypeValue(): Boolean = attr(INIT_BY_DEFAULT_TYPE_VALUE, { true })
    override fun initByDefaultTypeValue(value: Boolean): B = apply { attr(INIT_BY_DEFAULT_TYPE_VALUE, value) }

    override fun key(): Boolean = attr(KEY, { false })
    override fun key(value: Boolean): B = apply { attr(KEY, value) }

    override fun length(): Int? = attr(LENGTH)
    override fun length(value: Int?): B = apply { attr(LENGTH, value) }

    override fun meta(): Boolean = attr(META, { false })
    override fun meta(value: Boolean): B = apply { attr(META, value) }

    override fun multi(): Boolean = attr(MULTI, { false })
    override fun multi(value: Boolean): B = apply { attr(MULTI, value) }

    override fun mutable(): Boolean? = attr(MUTABLE)
    override fun mutable(value: Boolean?): B = apply { attr(MUTABLE, value) }

    override fun nonFluent(): String = attr(NON_FLUENT, { "" })
    override fun nonFluent(value: String): B = apply { attr(NON_FLUENT, value) }

    override fun nullable(): Boolean = attr(NULLABLE, { false })
    override fun nullable(value: Boolean): B = apply { attr(NULLABLE, value) }

    override fun open(): Boolean = attr(OPEN, { false })
    override fun open(value: Boolean): B = apply { attr(OPEN, value) }

    override fun replaceable(): Boolean? = attr(REPLACEABLE)
    override fun replaceable(value: Boolean?): B = apply { attr(REPLACEABLE, value) }

    override fun type(): TypeIB<*> = attr(TYPE, { n.Void })
    override fun type(value: TypeIB<*>): B = apply { attr(TYPE, value) }

    override fun unique(): Boolean = attr(UNIQUE, { false })
    override fun unique(value: Boolean): B = apply { attr(UNIQUE, value) }

    override fun value(): Any? = attr(VALUE)
    override fun value(aValue: Any?): B = apply { attr(VALUE, aValue) }

    companion object {
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



open class CompilationUnit(value: CompilationUnit.() -> Unit = {}) : CompilationUnitB<CompilationUnit>(value) {

    companion object {
        val EMPTY = CompilationUnitEmpty
    }
}

open class CompilationUnitB<B : CompilationUnitIB<B>>(value: B.() -> Unit = {}) : TypeB<B>(value), CompilationUnitIB<B> {

    override fun base(): Boolean = attr(BASE, { false })
    override fun base(value: Boolean): B = apply { attr(BASE, value) }

    companion object {
        val BASE = "_base"
    }
}



open class Constructor(value: Constructor.() -> Unit = {}) : ConstructorB<Constructor>(value) {

    companion object {
        val EMPTY = Constructor { name(ItemEmpty.name()) }.apply<Constructor> { init() }
    }
}

open class ConstructorB<B : ConstructorIB<B>>(value: B.() -> Unit = {}) : LogicUnitB<B>(value), ConstructorIB<B> {

    override fun primary(): Boolean = attr(PRIMARY, { false })
    override fun primary(value: Boolean): B = apply { attr(PRIMARY, value) }

    companion object {
        val PRIMARY = "_primary"
    }
}



open class DataType(value: DataType.() -> Unit = {}) : DataTypeB<DataType>(value) {

    companion object {
        val EMPTY = DataType { name(ItemEmpty.name()) }.apply<DataType> { init() }
    }
}

open class DataTypeB<B : DataTypeIB<B>>(value: B.() -> Unit = {}) : CompilationUnitB<B>(value), DataTypeIB<B> {
}



open class DataTypeOperation(value: DataTypeOperation.() -> Unit = {}) : DataTypeOperationB<DataTypeOperation>(value) {

    companion object {
        val EMPTY = DataTypeOperation { name(ItemEmpty.name()) }.apply<DataTypeOperation> { init() }
    }
}

open class DataTypeOperationB<B : DataTypeOperationIB<B>>(value: B.() -> Unit = {}) : OperationB<B>(value), DataTypeOperationIB<B> {
}



open class EnumType(value: EnumType.() -> Unit = {}) : EnumTypeB<EnumType>(value) {

    companion object {
        val EMPTY = EnumType { name(ItemEmpty.name()) }.apply<EnumType> { init() }
    }
}

open class EnumTypeB<B : EnumTypeIB<B>>(value: B.() -> Unit = {}) : DataTypeB<B>(value), EnumTypeIB<B> {

    override fun literals(): ListMultiHolder<LiteralIB<*>> = itemAsList(LITERALS, LiteralIB::class.java, true)
    override fun literals(vararg value: LiteralIB<*>): B = apply { literals().addItems(value.asList()) }
    override fun lit(value: LiteralIB<*>): LiteralIB<*> = applyAndReturn { literals().addItem(value); value }
    override fun lit(value: LiteralIB<*>.() -> Unit): LiteralIB<*> = lit(Literal(value))

    override fun fillSupportsItems() {
        literals()
        super.fillSupportsItems()
    }

    companion object {
        val LITERALS = "_literals"
    }
}



open class Expression(value: Expression.() -> Unit = {}) : ExpressionB<Expression>(value) {

    companion object {
        val EMPTY = Expression { name(ItemEmpty.name()) }.apply<Expression> { init() }
    }
}

open class ExpressionB<B : ExpressionIB<B>>(value: B.() -> Unit = {}) : MacroCompositeB<B>(value), ExpressionIB<B> {
}



open class ExternalType(value: ExternalType.() -> Unit = {}) : ExternalTypeB<ExternalType>(value) {

    companion object {
        val EMPTY = ExternalType { name(ItemEmpty.name()) }.apply<ExternalType> { init() }
    }
}

open class ExternalTypeB<B : ExternalTypeIB<B>>(value: B.() -> Unit = {}) : TypeB<B>(value), ExternalTypeIB<B> {
}



open class Generic(value: Generic.() -> Unit = {}) : GenericB<Generic>(value) {

    companion object {
        val EMPTY = Generic { name(ItemEmpty.name()) }.apply<Generic> { init() }
    }
}

open class GenericB<B : GenericIB<B>>(value: B.() -> Unit = {}) : TypeB<B>(value), GenericIB<B> {

    override fun type(): TypeIB<*> = attr(TYPE, { Type.EMPTY })
    override fun type(value: TypeIB<*>): B = apply { attr(TYPE, value) }

    companion object {
        val TYPE = "_type"
    }
}



open class Lambda(value: Lambda.() -> Unit = {}) : LambdaB<Lambda>(value) {

    companion object {
        val EMPTY = Lambda { name(ItemEmpty.name()) }.apply<Lambda> { init() }
    }
}

open class LambdaB<B : LambdaIB<B>>(value: B.() -> Unit = {}) : TypeB<B>(value), LambdaIB<B> {

    override fun operation(): OperationIB<*> = attr(OPERATION, { Operation.EMPTY })
    override fun operation(value: OperationIB<*>): B = apply { attr(OPERATION, value) }

    companion object {
        val OPERATION = "_operation"
    }
}



open class Literal(value: Literal.() -> Unit = {}) : LiteralB<Literal>(value) {

    companion object {
        val EMPTY = Literal { name(ItemEmpty.name()) }.apply<Literal> { init() }
    }
}

open class LiteralB<B : LiteralIB<B>>(value: B.() -> Unit = {}) : LogicUnitB<B>(value), LiteralIB<B> {
}



open class LogicUnit(value: LogicUnit.() -> Unit = {}) : LogicUnitB<LogicUnit>(value) {

    companion object {
        val EMPTY = LogicUnitEmpty
    }
}

open class LogicUnitB<B : LogicUnitIB<B>>(value: B.() -> Unit = {}) : ExpressionB<B>(value), LogicUnitIB<B> {

    override fun params(): ListMultiHolder<AttributeIB<*>> = itemAsList(PARAMS, AttributeIB::class.java, true)
    override fun params(vararg value: AttributeIB<*>): B = apply { params().addItems(value.asList()) }

    override fun superUnit(): LogicUnitIB<*> = attr(SUPER_UNIT, { LogicUnit.EMPTY })
    override fun superUnit(value: LogicUnitIB<*>): B = apply { attr(SUPER_UNIT, value) }

    override fun virtual(): Boolean = attr(VIRTUAL, { false })
    override fun virtual(value: Boolean): B = apply { attr(VIRTUAL, value) }

    override fun visible(): Boolean = attr(VISIBLE, { true })
    override fun visible(value: Boolean): B = apply { attr(VISIBLE, value) }

    override fun fillSupportsItems() {
        params()
        super.fillSupportsItems()
    }

    companion object {
        val PARAMS = "_params"
        val SUPER_UNIT = "__superUnit"
        val VIRTUAL = "_virtual"
        val VISIBLE = "_visible"
    }
}



open class MacroComposite(value: MacroComposite.() -> Unit = {}) : MacroCompositeB<MacroComposite>(value) {

    companion object {
        val EMPTY = MacroComposite { name(ItemEmpty.name()) }.apply<MacroComposite> { init() }
    }
}

open class MacroCompositeB<B : MacroCompositeIB<B>>(value: B.() -> Unit = {}) : CompositeB<B>(value), MacroCompositeIB<B> {

    override fun macrosAfter(): ListMultiHolder<String> = itemAsList(MACROS_AFTER, String::class.java, true)
    override fun macrosAfter(vararg value: String): B = apply { macrosAfter().addItems(value.asList()) }

    override fun macrosAfterBody(): ListMultiHolder<String> = itemAsList(MACROS_AFTER_BODY, String::class.java, true)
    override fun macrosAfterBody(vararg value: String): B = apply { macrosAfterBody().addItems(value.asList()) }

    override fun macrosBefore(): ListMultiHolder<String> = itemAsList(MACROS_BEFORE, String::class.java, true)
    override fun macrosBefore(vararg value: String): B = apply { macrosBefore().addItems(value.asList()) }

    override fun macrosBeforeBody(): ListMultiHolder<String> = itemAsList(MACROS_BEFORE_BODY, String::class.java, true)
    override fun macrosBeforeBody(vararg value: String): B = apply { macrosBeforeBody().addItems(value.asList()) }

    override fun macrosBody(): ListMultiHolder<String> = itemAsList(MACROS_BODY, String::class.java, true)
    override fun macrosBody(vararg value: String): B = apply { macrosBody().addItems(value.asList()) }

    override fun tags(): ListMultiHolder<String> = itemAsList(TAGS, String::class.java, true)
    override fun tags(vararg value: String): B = apply { tags().addItems(value.asList()) }

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
        val MACROS_AFTER = "_macrosAfter"
        val MACROS_AFTER_BODY = "_macrosAfterBody"
        val MACROS_BEFORE = "_macrosBefore"
        val MACROS_BEFORE_BODY = "_macrosBeforeBody"
        val MACROS_BODY = "_macrosBody"
        val TAGS = "_tags"
    }
}



open class NativeType(value: NativeType.() -> Unit = {}) : NativeTypeB<NativeType>(value) {

    companion object {
        val EMPTY = NativeType { name(ItemEmpty.name()) }.apply<NativeType> { init() }
    }
}

open class NativeTypeB<B : NativeTypeIB<B>>(value: B.() -> Unit = {}) : TypeB<B>(value), NativeTypeIB<B> {
}



open class Operation(value: Operation.() -> Unit = {}) : OperationB<Operation>(value) {

    companion object {
        val EMPTY = Operation { name(ItemEmpty.name()) }.apply<Operation> { init() }
    }
}

open class OperationB<B : OperationIB<B>>(value: B.() -> Unit = {}) : LogicUnitB<B>(value), OperationIB<B> {

    override fun generics(): ListMultiHolder<GenericIB<*>> = itemAsList(GENERICS, GenericIB::class.java, true)
    override fun generics(vararg value: GenericIB<*>): B = apply { generics().addItems(value.asList()) }
    override fun G(value: GenericIB<*>): GenericIB<*> = applyAndReturn { generics().addItem(value); value }
    override fun G(value: GenericIB<*>.() -> Unit): GenericIB<*> = G(Generic(value))

    override fun open(): Boolean = attr(OPEN, { true })
    override fun open(value: Boolean): B = apply { attr(OPEN, value) }

    override fun returns(): ListMultiHolder<AttributeIB<*>> = itemAsList(RETURNS, AttributeIB::class.java, true)
    override fun returns(vararg value: AttributeIB<*>): B = apply { returns().addItems(value.asList()) }
    override fun ret(value: AttributeIB<*>): AttributeIB<*> = applyAndReturn { returns().addItem(value); value }
    override fun ret(value: AttributeIB<*>.() -> Unit): AttributeIB<*> = ret(Attribute(value))

    override fun fillSupportsItems() {
        generics()
        returns()
        super.fillSupportsItems()
    }

    companion object {
        val GENERICS = "_generics"
        val OPEN = "_open"
        val RETURNS = "_returns"
    }
}



open class StructureUnit(value: StructureUnit.() -> Unit = {}) : StructureUnitB<StructureUnit>(value) {

    companion object {
        val EMPTY = StructureUnit { name(ItemEmpty.name()) }.apply<StructureUnit> { init() }
    }
}

open class StructureUnitB<B : StructureUnitIB<B>>(value: B.() -> Unit = {}) : MacroCompositeB<B>(value), StructureUnitIB<B> {

    override fun artifact(): String = attr(ARTIFACT, { "" })
    override fun artifact(value: String): B = apply { attr(ARTIFACT, value) }

    override fun fullName(): String = attr(FULL_NAME, { "" })
    override fun fullName(value: String): B = apply { attr(FULL_NAME, value) }

    override fun key(): String = attr(KEY, { "" })
    override fun key(value: String): B = apply { attr(KEY, value) }

    companion object {
        val ARTIFACT = "_artifact"
        val FULL_NAME = "_fullName"
        val KEY = "_key"
    }
}



open class Type(value: Type.() -> Unit = {}) : TypeB<Type>(value) {

    companion object {
        val EMPTY = Type { name(ItemEmpty.name()) }.apply<Type> { init() }
    }
}

open class TypeB<B : TypeIB<B>>(value: B.() -> Unit = {}) : MacroCompositeB<B>(value), TypeIB<B> {

    override fun constructors(): ListMultiHolder<ConstructorIB<*>> = itemAsList(CONSTRUCTORS, ConstructorIB::class.java, true)
    override fun constructors(vararg value: ConstructorIB<*>): B = apply { constructors().addItems(value.asList()) }
    override fun constr(value: ConstructorIB<*>): ConstructorIB<*> = applyAndReturn { constructors().addItem(value); value }
    override fun constr(value: ConstructorIB<*>.() -> Unit): ConstructorIB<*> = constr(Constructor(value))

    override fun defaultValue(): Any? = attr(DEFAULT_VALUE)
    override fun defaultValue(value: Any?): B = apply { attr(DEFAULT_VALUE, value) }

    override fun generics(): ListMultiHolder<GenericIB<*>> = itemAsList(GENERICS, GenericIB::class.java, true)
    override fun generics(vararg value: GenericIB<*>): B = apply { generics().addItems(value.asList()) }
    override fun G(value: GenericIB<*>): GenericIB<*> = applyAndReturn { generics().addItem(value); value }
    override fun G(value: GenericIB<*>.() -> Unit): GenericIB<*> = G(Generic(value))

    override fun ifc(): Boolean = attr(IFC, { false })
    override fun ifc(value: Boolean): B = apply { attr(IFC, value) }

    override fun multi(): Boolean = attr(MULTI, { false })
    override fun multi(value: Boolean): B = apply { attr(MULTI, value) }

    override fun open(): Boolean = attr(OPEN, { true })
    override fun open(value: Boolean): B = apply { attr(OPEN, value) }

    override fun operations(): ListMultiHolder<OperationIB<*>> = itemAsList(OPERATIONS, OperationIB::class.java, true)
    override fun operations(vararg value: OperationIB<*>): B = apply { operations().addItems(value.asList()) }
    override fun op(value: OperationIB<*>): OperationIB<*> = applyAndReturn { operations().addItem(value); value }
    override fun op(value: OperationIB<*>.() -> Unit): OperationIB<*> = op(Operation(value))

    override fun props(): ListMultiHolder<AttributeIB<*>> = itemAsList(PROPS, AttributeIB::class.java, true)
    override fun props(vararg value: AttributeIB<*>): B = apply { props().addItems(value.asList()) }
    override fun prop(value: AttributeIB<*>): AttributeIB<*> = applyAndReturn { props().addItem(value); value }
    override fun prop(value: AttributeIB<*>.() -> Unit): AttributeIB<*> = prop(Attribute(value))

    override fun superUnit(): CompilationUnitIB<*> = attr(SUPER_UNIT, { CompilationUnit.EMPTY })
    override fun superUnit(value: CompilationUnitIB<*>): B = apply { attr(SUPER_UNIT, value) }

    override fun superUnitFor(): ListMultiHolder<CompilationUnitIB<*>> = itemAsList(SUPER_UNIT_FOR, CompilationUnitIB::class.java, true)
    override fun superUnitFor(vararg value: CompilationUnitIB<*>): B = apply { superUnitFor().addItems(value.asList()) }

    override fun virtual(): Boolean = attr(VIRTUAL, { false })
    override fun virtual(value: Boolean): B = apply { attr(VIRTUAL, value) }

    override fun fillSupportsItems() {
        constructors()
        generics()
        operations()
        props()
        superUnitFor()
        super.fillSupportsItems()
    }

    companion object {
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



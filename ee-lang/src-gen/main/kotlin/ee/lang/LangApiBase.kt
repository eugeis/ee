package ee.lang


open class AndExpression(value: AndExpression.() -> Unit = {}) : AndExpressionB<AndExpression>(value) {

    companion object {
        val EMPTY = AndExpression { name(ItemEmpty.name()) }.apply<AndExpression> { init() }
    }
}

open class AndExpressionB<B : AndExpressionI<B>>(value: B.() -> Unit = {}) : LeftRightPredicateExpressionB<B>(value), AndExpressionI<B> {
}



open class Attribute(value: Attribute.() -> Unit = {}) : AttributeB<Attribute>(value) {

    companion object {
        val EMPTY = Attribute { name(ItemEmpty.name()) }.apply<Attribute> { init() }
    }
}

open class AttributeB<B : AttributeI<B>>(value: B.() -> Unit = {}) : LiteralB<B>(value), AttributeI<B> {

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

    override fun unique(): Boolean = attr(UNIQUE, { false })
    override fun unique(value: Boolean): B = apply { attr(UNIQUE, value) }

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
        val UNIQUE = "_unique"
    }
}



open class CompilationUnit(value: CompilationUnit.() -> Unit = {}) : CompilationUnitB<CompilationUnit>(value) {

    companion object {
        val EMPTY = CompilationUnitEmpty
    }
}

open class CompilationUnitB<B : CompilationUnitI<B>>(value: B.() -> Unit = {}) : TypeB<B>(value), CompilationUnitI<B> {

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

open class ConstructorB<B : ConstructorI<B>>(value: B.() -> Unit = {}) : LogicUnitB<B>(value), ConstructorI<B> {

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

open class DataTypeB<B : DataTypeI<B>>(value: B.() -> Unit = {}) : CompilationUnitB<B>(value), DataTypeI<B> {
}



open class DataTypeOperation(value: DataTypeOperation.() -> Unit = {}) : DataTypeOperationB<DataTypeOperation>(value) {

    companion object {
        val EMPTY = DataTypeOperation { name(ItemEmpty.name()) }.apply<DataTypeOperation> { init() }
    }
}

open class DataTypeOperationB<B : DataTypeOperationI<B>>(value: B.() -> Unit = {}) : OperationB<B>(value), DataTypeOperationI<B> {
}



open class EnumType(value: EnumType.() -> Unit = {}) : EnumTypeB<EnumType>(value) {

    companion object {
        val EMPTY = EnumType { name(ItemEmpty.name()) }.apply<EnumType> { init() }
    }
}

open class EnumTypeB<B : EnumTypeI<B>>(value: B.() -> Unit = {}) : DataTypeB<B>(value), EnumTypeI<B> {

    override fun literals(): ListMultiHolder<LiteralI<*>> = itemAsList(LITERALS, LiteralI::class.java, true)
    override fun literals(vararg value: LiteralI<*>): B = apply { literals().addItems(value.asList()) }
    override fun lit(value: LiteralI<*>): LiteralI<*> = applyAndReturn { literals().addItem(value); value }
    override fun lit(value: LiteralI<*>.() -> Unit): LiteralI<*> = lit(Literal(value))

    override fun fillSupportsItems() {
        literals()
        super.fillSupportsItems()
    }

    companion object {
        val LITERALS = "_literals"
    }
}



open class EqExpression(value: EqExpression.() -> Unit = {}) : EqExpressionB<EqExpression>(value) {

    companion object {
        val EMPTY = EqExpression { name(ItemEmpty.name()) }.apply<EqExpression> { init() }
    }
}

open class EqExpressionB<B : EqExpressionI<B>>(value: B.() -> Unit = {}) : LeftRightExpressionB<B>(value), EqExpressionI<B> {
}



open class Expression(value: Expression.() -> Unit = {}) : ExpressionB<Expression>(value) {

    companion object {
        val EMPTY = Expression { name(ItemEmpty.name()) }.apply<Expression> { init() }
    }
}

open class ExpressionB<B : ExpressionI<B>>(value: B.() -> Unit = {}) : MacroCompositeB<B>(value), ExpressionI<B> {
}



open class ExternalType(value: ExternalType.() -> Unit = {}) : ExternalTypeB<ExternalType>(value) {

    companion object {
        val EMPTY = ExternalType { name(ItemEmpty.name()) }.apply<ExternalType> { init() }
    }
}

open class ExternalTypeB<B : ExternalTypeI<B>>(value: B.() -> Unit = {}) : TypeB<B>(value), ExternalTypeI<B> {
}



open class Generic(value: Generic.() -> Unit = {}) : GenericB<Generic>(value) {

    companion object {
        val EMPTY = Generic { name(ItemEmpty.name()) }.apply<Generic> { init() }
    }
}

open class GenericB<B : GenericI<B>>(value: B.() -> Unit = {}) : TypeB<B>(value), GenericI<B> {

    override fun type(): TypeI<*> = attr(TYPE, { Type.EMPTY })
    override fun type(value: TypeI<*>): B = apply { attr(TYPE, value) }

    companion object {
        val TYPE = "_type"
    }
}



open class GtExpression(value: GtExpression.() -> Unit = {}) : GtExpressionB<GtExpression>(value) {

    companion object {
        val EMPTY = GtExpression { name(ItemEmpty.name()) }.apply<GtExpression> { init() }
    }
}

open class GtExpressionB<B : GtExpressionI<B>>(value: B.() -> Unit = {}) : LeftRightExpressionB<B>(value), GtExpressionI<B> {
}



open class GteExpression(value: GteExpression.() -> Unit = {}) : GteExpressionB<GteExpression>(value) {

    companion object {
        val EMPTY = GteExpression { name(ItemEmpty.name()) }.apply<GteExpression> { init() }
    }
}

open class GteExpressionB<B : GteExpressionI<B>>(value: B.() -> Unit = {}) : LeftRightExpressionB<B>(value), GteExpressionI<B> {
}



open class Lambda(value: Lambda.() -> Unit = {}) : LambdaB<Lambda>(value) {

    companion object {
        val EMPTY = Lambda { name(ItemEmpty.name()) }.apply<Lambda> { init() }
    }
}

open class LambdaB<B : LambdaI<B>>(value: B.() -> Unit = {}) : TypeB<B>(value), LambdaI<B> {

    override fun operation(): OperationI<*> = attr(OPERATION, { Operation.EMPTY })
    override fun operation(value: OperationI<*>): B = apply { attr(OPERATION, value) }

    companion object {
        val OPERATION = "_operation"
    }
}



open class LeftRightExpression(value: LeftRightExpression.() -> Unit = {}) : LeftRightExpressionB<LeftRightExpression>(value) {

    companion object {
        val EMPTY = LeftRightExpression { name(ItemEmpty.name()) }.apply<LeftRightExpression> { init() }
    }
}

open class LeftRightExpressionB<B : LeftRightExpressionI<B>>(value: B.() -> Unit = {}) : PredicateB<B>(value), LeftRightExpressionI<B> {

    override fun left(): LiteralI<*> = attr(LEFT, { Literal.EMPTY })
    override fun left(value: LiteralI<*>): B = apply { attr(LEFT, value) }

    override fun right(): LiteralI<*> = attr(RIGHT, { Literal.EMPTY })
    override fun right(value: LiteralI<*>): B = apply { attr(RIGHT, value) }

    companion object {
        val LEFT = "_left"
        val RIGHT = "_right"
    }
}



open class LeftRightPredicateExpression(value: LeftRightPredicateExpression.() -> Unit = {}) : LeftRightPredicateExpressionB<LeftRightPredicateExpression>(value) {

    companion object {
        val EMPTY = LeftRightPredicateExpression { name(ItemEmpty.name()) }.apply<LeftRightPredicateExpression> { init() }
    }
}

open class LeftRightPredicateExpressionB<B : LeftRightPredicateExpressionI<B>>(value: B.() -> Unit = {}) : PredicateB<B>(value), LeftRightPredicateExpressionI<B> {

    override fun left(): PredicateI<*> = attr(LEFT, { Predicate.EMPTY })
    override fun left(value: PredicateI<*>): B = apply { attr(LEFT, value) }

    override fun right(): PredicateI<*> = attr(RIGHT, { Predicate.EMPTY })
    override fun right(value: PredicateI<*>): B = apply { attr(RIGHT, value) }

    companion object {
        val LEFT = "_left"
        val RIGHT = "_right"
    }
}



open class Literal(value: Literal.() -> Unit = {}) : LiteralB<Literal>(value) {

    companion object {
        val EMPTY = Literal { name(ItemEmpty.name()) }.apply<Literal> { init() }
    }
}

open class LiteralB<B : LiteralI<B>>(value: B.() -> Unit = {}) : ExpressionB<B>(value), LiteralI<B> {

    override fun type(): TypeI<*> = attr(TYPE, { n.Void })
    override fun type(value: TypeI<*>): B = apply { attr(TYPE, value) }

    override fun value(): Any? = attr(VALUE)
    override fun value(aValue: Any?): B = apply { attr(VALUE, aValue) }

    companion object {
        val TYPE = "_type"
        val VALUE = "_value"
    }
}



open class LogicUnit(value: LogicUnit.() -> Unit = {}) : LogicUnitB<LogicUnit>(value) {

    companion object {
        val EMPTY = LogicUnitEmpty
    }
}

open class LogicUnitB<B : LogicUnitI<B>>(value: B.() -> Unit = {}) : ExpressionB<B>(value), LogicUnitI<B> {

    override fun params(): ListMultiHolder<AttributeI<*>> = itemAsList(PARAMS, AttributeI::class.java, true)
    override fun params(vararg value: AttributeI<*>): B = apply { params().addItems(value.asList()) }

    override fun superUnit(): LogicUnitI<*> = attr(SUPER_UNIT, { LogicUnit.EMPTY })
    override fun superUnit(value: LogicUnitI<*>): B = apply { attr(SUPER_UNIT, value) }

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



open class LtExpression(value: LtExpression.() -> Unit = {}) : LtExpressionB<LtExpression>(value) {

    companion object {
        val EMPTY = LtExpression { name(ItemEmpty.name()) }.apply<LtExpression> { init() }
    }
}

open class LtExpressionB<B : LtExpressionI<B>>(value: B.() -> Unit = {}) : LeftRightExpressionB<B>(value), LtExpressionI<B> {
}



open class LteExpression(value: LteExpression.() -> Unit = {}) : LteExpressionB<LteExpression>(value) {

    companion object {
        val EMPTY = LteExpression { name(ItemEmpty.name()) }.apply<LteExpression> { init() }
    }
}

open class LteExpressionB<B : LteExpressionI<B>>(value: B.() -> Unit = {}) : LeftRightExpressionB<B>(value), LteExpressionI<B> {
}



open class MacroComposite(value: MacroComposite.() -> Unit = {}) : MacroCompositeB<MacroComposite>(value) {

    companion object {
        val EMPTY = MacroComposite { name(ItemEmpty.name()) }.apply<MacroComposite> { init() }
    }
}

open class MacroCompositeB<B : MacroCompositeI<B>>(value: B.() -> Unit = {}) : CompositeB<B>(value), MacroCompositeI<B> {

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

open class NativeTypeB<B : NativeTypeI<B>>(value: B.() -> Unit = {}) : TypeB<B>(value), NativeTypeI<B> {
}



open class NeExpression(value: NeExpression.() -> Unit = {}) : NeExpressionB<NeExpression>(value) {

    companion object {
        val EMPTY = NeExpression { name(ItemEmpty.name()) }.apply<NeExpression> { init() }
    }
}

open class NeExpressionB<B : NeExpressionI<B>>(value: B.() -> Unit = {}) : LeftRightExpressionB<B>(value), NeExpressionI<B> {
}



open class NotExpression(value: NotExpression.() -> Unit = {}) : NotExpressionB<NotExpression>(value) {

    companion object {
        val EMPTY = NotExpression { name(ItemEmpty.name()) }.apply<NotExpression> { init() }
    }
}

open class NotExpressionB<B : NotExpressionI<B>>(value: B.() -> Unit = {}) : PredicateB<B>(value), NotExpressionI<B> {

    override fun value(): PredicateI<*> = attr(VALUE, { Predicate.EMPTY })
    override fun value(aValue: PredicateI<*>): B = apply { attr(VALUE, aValue) }

    companion object {
        val VALUE = "_value"
    }
}



open class Operation(value: Operation.() -> Unit = {}) : OperationB<Operation>(value) {

    companion object {
        val EMPTY = Operation { name(ItemEmpty.name()) }.apply<Operation> { init() }
    }
}

open class OperationB<B : OperationI<B>>(value: B.() -> Unit = {}) : LogicUnitB<B>(value), OperationI<B> {

    override fun generics(): ListMultiHolder<GenericI<*>> = itemAsList(GENERICS, GenericI::class.java, true)
    override fun generics(vararg value: GenericI<*>): B = apply { generics().addItems(value.asList()) }
    override fun G(value: GenericI<*>): GenericI<*> = applyAndReturn { generics().addItem(value); value }
    override fun G(value: GenericI<*>.() -> Unit): GenericI<*> = G(Generic(value))

    override fun open(): Boolean = attr(OPEN, { true })
    override fun open(value: Boolean): B = apply { attr(OPEN, value) }

    override fun returns(): ListMultiHolder<AttributeI<*>> = itemAsList(RETURNS, AttributeI::class.java, true)
    override fun returns(vararg value: AttributeI<*>): B = apply { returns().addItems(value.asList()) }
    override fun ret(value: AttributeI<*>): AttributeI<*> = applyAndReturn { returns().addItem(value); value }
    override fun ret(value: AttributeI<*>.() -> Unit): AttributeI<*> = ret(Attribute(value))

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



open class OrExpression(value: OrExpression.() -> Unit = {}) : OrExpressionB<OrExpression>(value) {

    companion object {
        val EMPTY = OrExpression { name(ItemEmpty.name()) }.apply<OrExpression> { init() }
    }
}

open class OrExpressionB<B : OrExpressionI<B>>(value: B.() -> Unit = {}) : LeftRightPredicateExpressionB<B>(value), OrExpressionI<B> {
}



open class Predicate(value: Predicate.() -> Unit = {}) : PredicateB<Predicate>(value) {

    companion object {
        val EMPTY = Predicate { name(ItemEmpty.name()) }.apply<Predicate> { init() }
    }
}

open class PredicateB<B : PredicateI<B>>(value: B.() -> Unit = {}) : ExpressionB<B>(value), PredicateI<B> {
}



open class StructureUnit(value: StructureUnit.() -> Unit = {}) : StructureUnitB<StructureUnit>(value) {

    companion object {
        val EMPTY = StructureUnit { name(ItemEmpty.name()) }.apply<StructureUnit> { init() }
    }
}

open class StructureUnitB<B : StructureUnitI<B>>(value: B.() -> Unit = {}) : MacroCompositeB<B>(value), StructureUnitI<B> {

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

open class TypeB<B : TypeI<B>>(value: B.() -> Unit = {}) : MacroCompositeB<B>(value), TypeI<B> {

    override fun constructors(): ListMultiHolder<ConstructorI<*>> = itemAsList(CONSTRUCTORS, ConstructorI::class.java, true)
    override fun constructors(vararg value: ConstructorI<*>): B = apply { constructors().addItems(value.asList()) }
    override fun constr(value: ConstructorI<*>): ConstructorI<*> = applyAndReturn { constructors().addItem(value); value }
    override fun constr(value: ConstructorI<*>.() -> Unit): ConstructorI<*> = constr(Constructor(value))

    override fun defaultValue(): Any? = attr(DEFAULT_VALUE)
    override fun defaultValue(value: Any?): B = apply { attr(DEFAULT_VALUE, value) }

    override fun generics(): ListMultiHolder<GenericI<*>> = itemAsList(GENERICS, GenericI::class.java, true)
    override fun generics(vararg value: GenericI<*>): B = apply { generics().addItems(value.asList()) }
    override fun G(value: GenericI<*>): GenericI<*> = applyAndReturn { generics().addItem(value); value }
    override fun G(value: GenericI<*>.() -> Unit): GenericI<*> = G(Generic(value))

    override fun ifc(): Boolean = attr(IFC, { false })
    override fun ifc(value: Boolean): B = apply { attr(IFC, value) }

    override fun multi(): Boolean = attr(MULTI, { false })
    override fun multi(value: Boolean): B = apply { attr(MULTI, value) }

    override fun open(): Boolean = attr(OPEN, { true })
    override fun open(value: Boolean): B = apply { attr(OPEN, value) }

    override fun operations(): ListMultiHolder<OperationI<*>> = itemAsList(OPERATIONS, OperationI::class.java, true)
    override fun operations(vararg value: OperationI<*>): B = apply { operations().addItems(value.asList()) }
    override fun op(value: OperationI<*>): OperationI<*> = applyAndReturn { operations().addItem(value); value }
    override fun op(value: OperationI<*>.() -> Unit): OperationI<*> = op(Operation(value))

    override fun props(): ListMultiHolder<AttributeI<*>> = itemAsList(PROPS, AttributeI::class.java, true)
    override fun props(vararg value: AttributeI<*>): B = apply { props().addItems(value.asList()) }
    override fun prop(value: AttributeI<*>): AttributeI<*> = applyAndReturn { props().addItem(value); value }
    override fun prop(value: AttributeI<*>.() -> Unit): AttributeI<*> = prop(Attribute(value))

    override fun superUnitFor(): ListMultiHolder<TypeI<*>> = itemAsList(SUPER_UNIT_FOR, TypeI::class.java, true)
    override fun superUnitFor(vararg value: TypeI<*>): B = apply { superUnitFor().addItems(value.asList()) }

    override fun superUnits(): ListMultiHolder<TypeI<*>> = itemAsList(SUPER_UNITS, TypeI::class.java, true)
    override fun superUnits(vararg value: TypeI<*>): B = apply { superUnits().addItems(value.asList()) }

    override fun virtual(): Boolean = attr(VIRTUAL, { false })
    override fun virtual(value: Boolean): B = apply { attr(VIRTUAL, value) }

    override fun fillSupportsItems() {
        constructors()
        generics()
        operations()
        props()
        superUnitFor()
        superUnits()
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
        val SUPER_UNIT_FOR = "_superUnitFor"
        val SUPER_UNITS = "_superUnits"
        val VIRTUAL = "_virtual"
    }
}



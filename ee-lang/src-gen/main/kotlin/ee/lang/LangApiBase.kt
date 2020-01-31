package ee.lang


open class Action(adapt: Action.() -> Unit = {}) : ActionB<Action>(adapt) {

    companion object {
        val EMPTY = Action { name(ItemEmpty.name()) }.apply<Action> { init() }
    }
}

open class ActionB<B : ActionI<B>>(adapt: B.() -> Unit = {}) : LogicUnitB<B>(adapt), ActionI<B> {
}



open class AndPredicate(adapt: AndPredicate.() -> Unit = {}) : AndPredicateB<AndPredicate>(adapt) {

    companion object {
        val EMPTY = AndPredicate { name(ItemEmpty.name()) }.apply<AndPredicate> { init() }
    }
}

open class AndPredicateB<B : AndPredicateI<B>>(adapt: B.() -> Unit = {}) : LeftRightPredicatesPredicateB<B>(adapt), AndPredicateI<B> {
}



open class ApplyAction(adapt: ApplyAction.() -> Unit = {}) : ApplyActionB<ApplyAction>(adapt) {

    companion object {
        val EMPTY = ApplyAction { name(ItemEmpty.name()) }.apply<ApplyAction> { init() }
    }
}

open class ApplyActionB<B : ApplyActionI<B>>(adapt: B.() -> Unit = {}) : ActionB<B>(adapt), ApplyActionI<B> {

    override fun target(): AttributeI<*> = attr(TARGET, { Attribute.EMPTY })
    override fun target(value: AttributeI<*>): B = apply { attr(TARGET, value) }

    override fun value(): LiteralI<*> = attr(VALUE, { Literal.EMPTY })
    override fun value(aValue: LiteralI<*>): B = apply { attr(VALUE, aValue) }

    companion object {
        val TARGET = "_target"
        val VALUE = "_value"
    }
}



open class AssignAction(adapt: AssignAction.() -> Unit = {}) : AssignActionB<AssignAction>(adapt) {

    companion object {
        val EMPTY = AssignAction { name(ItemEmpty.name()) }.apply<AssignAction> { init() }
    }
}

open class AssignActionB<B : AssignActionI<B>>(adapt: B.() -> Unit = {}) : ApplyActionB<B>(adapt), AssignActionI<B> {
}



open class Attribute(adapt: Attribute.() -> Unit = {}) : AttributeB<Attribute>(adapt) {

    companion object {
        val EMPTY = Attribute { name(ItemEmpty.name()) }.apply<Attribute> { init() }
    }
}

open class AttributeB<B : AttributeI<B>>(adapt: B.() -> Unit = {}) : LiteralB<B>(adapt), AttributeI<B> {

    override fun isAccessible(): Boolean? = attr(ACCESSIBLE)
    override fun accessible(value: Boolean?): B = apply { attr(ACCESSIBLE, value) }

    override fun isAnonymous(): Boolean = attr(ANONYMOUS, { false })
    override fun anonymous(value: Boolean): B = apply { attr(ANONYMOUS, value) }

    override fun isConcurrent(): Boolean = attr(CONCURRENT, { false })
    override fun concurrent(value: Boolean): B = apply { attr(CONCURRENT, value) }

    override fun isDefault(): Boolean = attr(DEFAULT, { false })
    override fun default(value: Boolean): B = apply { attr(DEFAULT, value) }

    override fun externalName(): String? = attr(EXTERNALNAME)
    override fun externalName(value: String?): B = apply { attr(EXTERNALNAME, value) }

    override fun isFixValue(): Boolean = attr(FIXVALUE, { false })
    override fun fixValue(value: Boolean): B = apply { attr(FIXVALUE, value) }

    override fun isHidden(): Boolean = attr(HIDDEN, { false })
    override fun hidden(value: Boolean): B = apply { attr(HIDDEN, value) }

    override fun isImploded(): Boolean = attr(IMPLODED, { false })
    override fun imploded(value: Boolean): B = apply { attr(IMPLODED, value) }

    override fun isInherited(): Boolean = attr(INHERITED, { false })
    override fun inherited(value: Boolean): B = apply { attr(INHERITED, value) }

    override fun isInitByDefaultTypeValue(): Boolean = attr(INITBYDEFAULTTYPEVALUE, { true })
    override fun initByDefaultTypeValue(value: Boolean): B = apply { attr(INITBYDEFAULTTYPEVALUE, value) }

    override fun isKey(): Boolean = attr(KEY, { false })
    override fun key(value: Boolean): B = apply { attr(KEY, value) }

    override fun length(): Int? = attr(LENGTH)
    override fun length(value: Int?): B = apply { attr(LENGTH, value) }

    override fun isMeta(): Boolean = attr(META, { false })
    override fun meta(value: Boolean): B = apply { attr(META, value) }

    override fun isMulti(): Boolean = attr(MULTI, { false })
    override fun multi(value: Boolean): B = apply { attr(MULTI, value) }

    override fun isMutable(): Boolean? = attr(MUTABLE)
    override fun mutable(value: Boolean?): B = apply { attr(MUTABLE, value) }

    override fun nonFluent(): String = attr(NONFLUENT, { "" })
    override fun nonFluent(value: String): B = apply { attr(NONFLUENT, value) }

    override fun isNullable(): Boolean = attr(NULLABLE, { false })
    override fun nullable(value: Boolean): B = apply { attr(NULLABLE, value) }

    override fun isOpen(): Boolean = attr(OPEN, { false })
    override fun open(value: Boolean): B = apply { attr(OPEN, value) }

    override fun isReplaceable(): Boolean? = attr(REPLACEABLE)
    override fun replaceable(value: Boolean?): B = apply { attr(REPLACEABLE, value) }

    override fun isToEquals(): Boolean? = attr(TOEQUALS)
    override fun toEquals(value: Boolean?): B = apply { attr(TOEQUALS, value) }

    override fun isToStr(): Boolean? = attr(TOSTR)
    override fun toStr(value: Boolean?): B = apply { attr(TOSTR, value) }

    override fun isUnique(): Boolean = attr(UNIQUE, { false })
    override fun unique(value: Boolean): B = apply { attr(UNIQUE, value) }

    companion object {
        val ACCESSIBLE = "_accessible"
        val ANONYMOUS = "_anonymous"
        val CONCURRENT = "_concurrent"
        val DEFAULT = "_default"
        val EXTERNALNAME = "_externalName"
        val FIXVALUE = "_fixValue"
        val HIDDEN = "_hidden"
        val IMPLODED = "_imploded"
        val INHERITED = "_inherited"
        val INITBYDEFAULTTYPEVALUE = "_initByDefaultTypeValue"
        val KEY = "_key"
        val LENGTH = "_length"
        val META = "_meta"
        val MULTI = "_multi"
        val MUTABLE = "_mutable"
        val NONFLUENT = "_nonFluent"
        val NULLABLE = "_nullable"
        val OPEN = "_open"
        val REPLACEABLE = "_replaceable"
        val TOEQUALS = "_toEquals"
        val TOSTR = "_toStr"
        val UNIQUE = "_unique"
    }
}



open class Basic(adapt: Basic.() -> Unit = {}) : BasicB<Basic>(adapt) {

    companion object {
        val EMPTY = Basic { name(ItemEmpty.name()) }.apply<Basic> { init() }
    }
}

open class BasicB<B : BasicI<B>>(adapt: B.() -> Unit = {}) : DataTypeB<B>(adapt), BasicI<B> {
}



open class CompilationUnit(adapt: CompilationUnit.() -> Unit = {}) : CompilationUnitB<CompilationUnit>(adapt) {

    companion object {
        val EMPTY = CompilationUnitEmpty
    }
}

open class CompilationUnitB<B : CompilationUnitI<B>>(adapt: B.() -> Unit = {}) : TypeB<B>(adapt), CompilationUnitI<B> {

    override fun isBase(): Boolean = attr(BASE, { false })
    override fun base(value: Boolean): B = apply { attr(BASE, value) }

    companion object {
        val BASE = "_base"
    }
}



open class Constructor(adapt: Constructor.() -> Unit = {}) : ConstructorB<Constructor>(adapt) {

    companion object {
        val EMPTY = Constructor { name(ItemEmpty.name()) }.apply<Constructor> { init() }
    }
}

open class ConstructorB<B : ConstructorI<B>>(adapt: B.() -> Unit = {}) : LogicUnitB<B>(adapt), ConstructorI<B> {

    override fun isPrimary(): Boolean = attr(PRIMARY, { false })
    override fun primary(value: Boolean): B = apply { attr(PRIMARY, value) }

    companion object {
        val PRIMARY = "_primary"
    }
}



open class DataType(adapt: DataType.() -> Unit = {}) : DataTypeB<DataType>(adapt) {

    companion object {
        val EMPTY = DataType { name(ItemEmpty.name()) }.apply<DataType> { init() }
    }
}

open class DataTypeB<B : DataTypeI<B>>(adapt: B.() -> Unit = {}) : CompilationUnitB<B>(adapt), DataTypeI<B> {
}



open class DataTypeOperation(adapt: DataTypeOperation.() -> Unit = {}) : DataTypeOperationB<DataTypeOperation>(adapt) {

    companion object {
        val EMPTY = DataTypeOperation { name(ItemEmpty.name()) }.apply<DataTypeOperation> { init() }
    }
}

open class DataTypeOperationB<B : DataTypeOperationI<B>>(adapt: B.() -> Unit = {}) : OperationB<B>(adapt), DataTypeOperationI<B> {
}



open class DecrementExpression(adapt: DecrementExpression.() -> Unit = {}) : DecrementExpressionB<DecrementExpression>(adapt) {

    companion object {
        val EMPTY = DecrementExpression { name(ItemEmpty.name()) }.apply<DecrementExpression> { init() }
    }
}

open class DecrementExpressionB<B : DecrementExpressionI<B>>(adapt: B.() -> Unit = {}) : LiteralB<B>(adapt), DecrementExpressionI<B> {
}



open class DivideAssignAction(adapt: DivideAssignAction.() -> Unit = {}) : DivideAssignActionB<DivideAssignAction>(adapt) {

    companion object {
        val EMPTY = DivideAssignAction { name(ItemEmpty.name()) }.apply<DivideAssignAction> { init() }
    }
}

open class DivideAssignActionB<B : DivideAssignActionI<B>>(adapt: B.() -> Unit = {}) : ApplyActionB<B>(adapt), DivideAssignActionI<B> {
}



open class DivideExpression(adapt: DivideExpression.() -> Unit = {}) : DivideExpressionB<DivideExpression>(adapt) {

    companion object {
        val EMPTY = DivideExpression { name(ItemEmpty.name()) }.apply<DivideExpression> { init() }
    }
}

open class DivideExpressionB<B : DivideExpressionI<B>>(adapt: B.() -> Unit = {}) : LeftRightLiteralB<B>(adapt), DivideExpressionI<B> {
}



open class EnumLiteral(adapt: EnumLiteral.() -> Unit = {}) : EnumLiteralB<EnumLiteral>(adapt) {

    companion object {
        val EMPTY = EnumLiteral { name(ItemEmpty.name()) }.apply<EnumLiteral> { init() }
    }
}

open class EnumLiteralB<B : EnumLiteralI<B>>(adapt: B.() -> Unit = {}) : LiteralB<B>(adapt), EnumLiteralI<B> {

    override fun externalName(): String? = attr(EXTERNALNAME)
    override fun externalName(value: String?): B = apply { attr(EXTERNALNAME, value) }

    companion object {
        val EXTERNALNAME = "_externalName"
    }
}



open class EnumType(adapt: EnumType.() -> Unit = {}) : EnumTypeB<EnumType>(adapt) {

    companion object {
        val EMPTY = EnumType { name(ItemEmpty.name()) }.apply<EnumType> { init() }
    }
}

open class EnumTypeB<B : EnumTypeI<B>>(adapt: B.() -> Unit = {}) : DataTypeB<B>(adapt), EnumTypeI<B> {

    override fun literals(): ListMultiHolder<EnumLiteralI<*>> = itemAsList(LITERALS, EnumLiteralI::class.java, true)
    override fun literals(vararg value: EnumLiteralI<*>): B = apply { literals().addItems(value.asList()) }
    override fun lit(value: EnumLiteralI<*>): EnumLiteralI<*> = applyAndReturn { literals().addItem(value); value }
    override fun lit(value: EnumLiteralI<*>.() -> Unit): EnumLiteralI<*> = lit(EnumLiteral(value))

    override fun fillSupportsItems() {
        literals()
        super.fillSupportsItems()
    }

    companion object {
        val LITERALS = "_literals"
    }
}



open class EqPredicate(adapt: EqPredicate.() -> Unit = {}) : EqPredicateB<EqPredicate>(adapt) {

    companion object {
        val EMPTY = EqPredicate { name(ItemEmpty.name()) }.apply<EqPredicate> { init() }
    }
}

open class EqPredicateB<B : EqPredicateI<B>>(adapt: B.() -> Unit = {}) : LeftRightPredicateB<B>(adapt), EqPredicateI<B> {
}



open class Expression(adapt: Expression.() -> Unit = {}) : ExpressionB<Expression>(adapt) {

    companion object {
        val EMPTY = Expression { name(ItemEmpty.name()) }.apply<Expression> { init() }
    }
}

open class ExpressionB<B : ExpressionI<B>>(adapt: B.() -> Unit = {}) : MacroCompositeB<B>(adapt), ExpressionI<B> {
}



open class ExternalType(adapt: ExternalType.() -> Unit = {}) : ExternalTypeB<ExternalType>(adapt) {

    companion object {
        val EMPTY = ExternalType { name(ItemEmpty.name()) }.apply<ExternalType> { init() }
    }
}

open class ExternalTypeB<B : ExternalTypeI<B>>(adapt: B.() -> Unit = {}) : TypeB<B>(adapt), ExternalTypeI<B> {
}



open class Generic(adapt: Generic.() -> Unit = {}) : GenericB<Generic>(adapt) {

    companion object {
        val EMPTY = Generic { name(ItemEmpty.name()) }.apply<Generic> { init() }
    }
}

open class GenericB<B : GenericI<B>>(adapt: B.() -> Unit = {}) : TypeB<B>(adapt), GenericI<B> {

    override fun type(): TypeI<*> = attr(TYPE, { Type.EMPTY })
    override fun type(value: TypeI<*>): B = apply { attr(TYPE, value) }

    companion object {
        val TYPE = "__type"
    }
}



open class GtPredicate(adapt: GtPredicate.() -> Unit = {}) : GtPredicateB<GtPredicate>(adapt) {

    companion object {
        val EMPTY = GtPredicate { name(ItemEmpty.name()) }.apply<GtPredicate> { init() }
    }
}

open class GtPredicateB<B : GtPredicateI<B>>(adapt: B.() -> Unit = {}) : LeftRightPredicateB<B>(adapt), GtPredicateI<B> {
}



open class GtePredicate(adapt: GtePredicate.() -> Unit = {}) : GtePredicateB<GtePredicate>(adapt) {

    companion object {
        val EMPTY = GtePredicate { name(ItemEmpty.name()) }.apply<GtePredicate> { init() }
    }
}

open class GtePredicateB<B : GtePredicateI<B>>(adapt: B.() -> Unit = {}) : LeftRightPredicateB<B>(adapt), GtePredicateI<B> {
}



open class IncrementExpression(adapt: IncrementExpression.() -> Unit = {}) : IncrementExpressionB<IncrementExpression>(adapt) {

    companion object {
        val EMPTY = IncrementExpression { name(ItemEmpty.name()) }.apply<IncrementExpression> { init() }
    }
}

open class IncrementExpressionB<B : IncrementExpressionI<B>>(adapt: B.() -> Unit = {}) : LiteralB<B>(adapt), IncrementExpressionI<B> {
}



open class Lambda(adapt: Lambda.() -> Unit = {}) : LambdaB<Lambda>(adapt) {

    companion object {
        val EMPTY = Lambda { name(ItemEmpty.name()) }.apply<Lambda> { init() }
    }
}

open class LambdaB<B : LambdaI<B>>(adapt: B.() -> Unit = {}) : TypeB<B>(adapt), LambdaI<B> {

    override fun operation(): OperationI<*> = attr(OPERATION, { Operation.EMPTY })
    override fun operation(value: OperationI<*>): B = apply { attr(OPERATION, value) }

    companion object {
        val OPERATION = "_operation"
    }
}



open class LeftRightLiteral(adapt: LeftRightLiteral.() -> Unit = {}) : LeftRightLiteralB<LeftRightLiteral>(adapt) {

    companion object {
        val EMPTY = LeftRightLiteral { name(ItemEmpty.name()) }.apply<LeftRightLiteral> { init() }
    }
}

open class LeftRightLiteralB<B : LeftRightLiteralI<B>>(adapt: B.() -> Unit = {}) : LiteralB<B>(adapt), LeftRightLiteralI<B> {

    override fun left(): LiteralI<*> = attr(LEFT, { Literal.EMPTY })
    override fun left(value: LiteralI<*>): B = apply { attr(LEFT, value) }

    override fun right(): LiteralI<*> = attr(RIGHT, { Literal.EMPTY })
    override fun right(value: LiteralI<*>): B = apply { attr(RIGHT, value) }

    companion object {
        val LEFT = "_left"
        val RIGHT = "_right"
    }
}



open class LeftRightPredicate(adapt: LeftRightPredicate.() -> Unit = {}) : LeftRightPredicateB<LeftRightPredicate>(adapt) {

    companion object {
        val EMPTY = LeftRightPredicate { name(ItemEmpty.name()) }.apply<LeftRightPredicate> { init() }
    }
}

open class LeftRightPredicateB<B : LeftRightPredicateI<B>>(adapt: B.() -> Unit = {}) : PredicateB<B>(adapt), LeftRightPredicateI<B> {

    override fun left(): LiteralI<*> = attr(LEFT, { Literal.EMPTY })
    override fun left(value: LiteralI<*>): B = apply { attr(LEFT, value) }

    override fun right(): LiteralI<*> = attr(RIGHT, { Literal.EMPTY })
    override fun right(value: LiteralI<*>): B = apply { attr(RIGHT, value) }

    companion object {
        val LEFT = "_left"
        val RIGHT = "_right"
    }
}



open class LeftRightPredicatesPredicate(adapt: LeftRightPredicatesPredicate.() -> Unit = {}) : LeftRightPredicatesPredicateB<LeftRightPredicatesPredicate>(adapt) {

    companion object {
        val EMPTY = LeftRightPredicatesPredicate { name(ItemEmpty.name()) }.apply<LeftRightPredicatesPredicate> { init() }
    }
}

open class LeftRightPredicatesPredicateB<B : LeftRightPredicatesPredicateI<B>>(adapt: B.() -> Unit = {}) : PredicateB<B>(adapt), LeftRightPredicatesPredicateI<B> {

    override fun left(): PredicateI<*> = attr(LEFT, { Predicate.EMPTY })
    override fun left(value: PredicateI<*>): B = apply { attr(LEFT, value) }

    override fun right(): PredicateI<*> = attr(RIGHT, { Predicate.EMPTY })
    override fun right(value: PredicateI<*>): B = apply { attr(RIGHT, value) }

    companion object {
        val LEFT = "_left"
        val RIGHT = "_right"
    }
}



open class Literal(adapt: Literal.() -> Unit = {}) : LiteralB<Literal>(adapt) {

    companion object {
        val EMPTY = Literal { name(ItemEmpty.name()) }.apply<Literal> { init() }
    }
}

open class LiteralB<B : LiteralI<B>>(adapt: B.() -> Unit = {}) : ExpressionB<B>(adapt), LiteralI<B> {

    override fun params(): ListMultiHolder<AttributeI<*>> = itemAsList(PARAMS, AttributeI::class.java, true)
    override fun params(vararg value: AttributeI<*>): B = apply { params().addItems(value.asList()) }

    override fun type(): TypeI<*> = attr(TYPE, { n.Void })
    override fun type(value: TypeI<*>): B = apply { attr(TYPE, value) }

    override fun value(): Any? = attr(VALUE)
    override fun value(aValue: Any?): B = apply { attr(VALUE, aValue) }

    override fun fillSupportsItems() {
        params()
        super.fillSupportsItems()
    }

    companion object {
        val PARAMS = "_params"
        val TYPE = "__type"
        val VALUE = "_value"
    }
}



open class LogicUnit(adapt: LogicUnit.() -> Unit = {}) : LogicUnitB<LogicUnit>(adapt) {

    companion object {
        val EMPTY = LogicUnitEmpty
    }
}

open class LogicUnitB<B : LogicUnitI<B>>(adapt: B.() -> Unit = {}) : ExpressionB<B>(adapt), LogicUnitI<B> {

    override fun isErrorHandling(): Boolean = attr(ERRORHANDLING, { true })
    override fun errorHandling(value: Boolean): B = apply { attr(ERRORHANDLING, value) }

    override fun params(): ListMultiHolder<AttributeI<*>> = itemAsList(PARAMS, AttributeI::class.java, true)
    override fun params(vararg value: AttributeI<*>): B = apply { params().addItems(value.asList()) }

    override fun superUnit(): LogicUnitI<*> = attr(SUPERUNIT, { LogicUnit.EMPTY })
    override fun superUnit(value: LogicUnitI<*>): B = apply { attr(SUPERUNIT, value) }

    override fun isVirtual(): Boolean = attr(VIRTUAL, { false })
    override fun virtual(value: Boolean): B = apply { attr(VIRTUAL, value) }

    override fun isVisible(): Boolean = attr(VISIBLE, { true })
    override fun visible(value: Boolean): B = apply { attr(VISIBLE, value) }

    override fun fillSupportsItems() {
        params()
        super.fillSupportsItems()
    }

    companion object {
        val ERRORHANDLING = "_errorHandling"
        val PARAMS = "_params"
        val SUPERUNIT = "__superUnit"
        val VIRTUAL = "_virtual"
        val VISIBLE = "_visible"
    }
}



open class LtPredicate(adapt: LtPredicate.() -> Unit = {}) : LtPredicateB<LtPredicate>(adapt) {

    companion object {
        val EMPTY = LtPredicate { name(ItemEmpty.name()) }.apply<LtPredicate> { init() }
    }
}

open class LtPredicateB<B : LtPredicateI<B>>(adapt: B.() -> Unit = {}) : LeftRightPredicateB<B>(adapt), LtPredicateI<B> {
}



open class LtePredicate(adapt: LtePredicate.() -> Unit = {}) : LtePredicateB<LtePredicate>(adapt) {

    companion object {
        val EMPTY = LtePredicate { name(ItemEmpty.name()) }.apply<LtePredicate> { init() }
    }
}

open class LtePredicateB<B : LtePredicateI<B>>(adapt: B.() -> Unit = {}) : LeftRightPredicateB<B>(adapt), LtePredicateI<B> {
}



open class MacroComposite(adapt: MacroComposite.() -> Unit = {}) : MacroCompositeB<MacroComposite>(adapt) {

    companion object {
        val EMPTY = MacroComposite { name(ItemEmpty.name()) }.apply<MacroComposite> { init() }
    }
}

open class MacroCompositeB<B : MacroCompositeI<B>>(adapt: B.() -> Unit = {}) : CompositeB<B>(adapt), MacroCompositeI<B> {

    override fun macrosAfter(): ListMultiHolder<String> = itemAsList(MACROSAFTER, String::class.java, true)
    override fun macrosAfter(vararg value: String): B = apply { macrosAfter().addItems(value.asList()) }

    override fun macrosAfterBody(): ListMultiHolder<String> = itemAsList(MACROSAFTERBODY, String::class.java, true)
    override fun macrosAfterBody(vararg value: String): B = apply { macrosAfterBody().addItems(value.asList()) }

    override fun macrosBefore(): ListMultiHolder<String> = itemAsList(MACROSBEFORE, String::class.java, true)
    override fun macrosBefore(vararg value: String): B = apply { macrosBefore().addItems(value.asList()) }

    override fun macrosBeforeBody(): ListMultiHolder<String> = itemAsList(MACROSBEFOREBODY, String::class.java, true)
    override fun macrosBeforeBody(vararg value: String): B = apply { macrosBeforeBody().addItems(value.asList()) }

    override fun macrosBody(): ListMultiHolder<String> = itemAsList(MACROSBODY, String::class.java, true)
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
        val MACROSAFTER = "_macrosAfter"
        val MACROSAFTERBODY = "_macrosAfterBody"
        val MACROSBEFORE = "_macrosBefore"
        val MACROSBEFOREBODY = "_macrosBeforeBody"
        val MACROSBODY = "_macrosBody"
        val TAGS = "_tags"
    }
}



open class MinusAssignAction(adapt: MinusAssignAction.() -> Unit = {}) : MinusAssignActionB<MinusAssignAction>(adapt) {

    companion object {
        val EMPTY = MinusAssignAction { name(ItemEmpty.name()) }.apply<MinusAssignAction> { init() }
    }
}

open class MinusAssignActionB<B : MinusAssignActionI<B>>(adapt: B.() -> Unit = {}) : ApplyActionB<B>(adapt), MinusAssignActionI<B> {
}



open class MinusExpression(adapt: MinusExpression.() -> Unit = {}) : MinusExpressionB<MinusExpression>(adapt) {

    companion object {
        val EMPTY = MinusExpression { name(ItemEmpty.name()) }.apply<MinusExpression> { init() }
    }
}

open class MinusExpressionB<B : MinusExpressionI<B>>(adapt: B.() -> Unit = {}) : LeftRightLiteralB<B>(adapt), MinusExpressionI<B> {
}



open class NativeType(adapt: NativeType.() -> Unit = {}) : NativeTypeB<NativeType>(adapt) {

    companion object {
        val EMPTY = NativeType { name(ItemEmpty.name()) }.apply<NativeType> { init() }
    }
}

open class NativeTypeB<B : NativeTypeI<B>>(adapt: B.() -> Unit = {}) : TypeB<B>(adapt), NativeTypeI<B> {
}



open class NePredicate(adapt: NePredicate.() -> Unit = {}) : NePredicateB<NePredicate>(adapt) {

    companion object {
        val EMPTY = NePredicate { name(ItemEmpty.name()) }.apply<NePredicate> { init() }
    }
}

open class NePredicateB<B : NePredicateI<B>>(adapt: B.() -> Unit = {}) : LeftRightPredicateB<B>(adapt), NePredicateI<B> {
}



open class NotPredicate(adapt: NotPredicate.() -> Unit = {}) : NotPredicateB<NotPredicate>(adapt) {

    companion object {
        val EMPTY = NotPredicate { name(ItemEmpty.name()) }.apply<NotPredicate> { init() }
    }
}

open class NotPredicateB<B : NotPredicateI<B>>(adapt: B.() -> Unit = {}) : PredicateB<B>(adapt), NotPredicateI<B> {

    override fun value(): PredicateI<*> = attr(VALUE, { Predicate.EMPTY })
    override fun value(aValue: PredicateI<*>): B = apply { attr(VALUE, aValue) }

    companion object {
        val VALUE = "_value"
    }
}



open class Operation(adapt: Operation.() -> Unit = {}) : OperationB<Operation>(adapt) {

    companion object {
        val EMPTY = Operation { name(ItemEmpty.name()) }.apply<Operation> { init() }
    }
}

open class OperationB<B : OperationI<B>>(adapt: B.() -> Unit = {}) : LogicUnitB<B>(adapt), OperationI<B> {

    override fun isErr(): Boolean = attr(ERR, { true })
    override fun err(value: Boolean): B = apply { attr(ERR, value) }

    override fun generics(): ListMultiHolder<GenericI<*>> = itemAsList(GENERICS, GenericI::class.java, true)
    override fun generics(vararg value: GenericI<*>): B = apply { generics().addItems(value.asList()) }
    override fun G(value: GenericI<*>): GenericI<*> = applyAndReturn { generics().addItem(value); value }
    override fun G(value: GenericI<*>.() -> Unit): GenericI<*> = G(Generic(value))

    override fun isNonBlock(): Boolean? = attr(NONBLOCK)
    override fun nonBlock(value: Boolean?): B = apply { attr(NONBLOCK, value) }

    override fun isOpen(): Boolean = attr(OPEN, { true })
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
        val ERR = "_err"
        val GENERICS = "_generics"
        val NONBLOCK = "_nonBlock"
        val OPEN = "_open"
        val RETURNS = "_returns"
    }
}



open class OrPredicate(adapt: OrPredicate.() -> Unit = {}) : OrPredicateB<OrPredicate>(adapt) {

    companion object {
        val EMPTY = OrPredicate { name(ItemEmpty.name()) }.apply<OrPredicate> { init() }
    }
}

open class OrPredicateB<B : OrPredicateI<B>>(adapt: B.() -> Unit = {}) : LeftRightPredicatesPredicateB<B>(adapt), OrPredicateI<B> {
}



open class PlusAssignAction(adapt: PlusAssignAction.() -> Unit = {}) : PlusAssignActionB<PlusAssignAction>(adapt) {

    companion object {
        val EMPTY = PlusAssignAction { name(ItemEmpty.name()) }.apply<PlusAssignAction> { init() }
    }
}

open class PlusAssignActionB<B : PlusAssignActionI<B>>(adapt: B.() -> Unit = {}) : ApplyActionB<B>(adapt), PlusAssignActionI<B> {
}



open class PlusExpression(adapt: PlusExpression.() -> Unit = {}) : PlusExpressionB<PlusExpression>(adapt) {

    companion object {
        val EMPTY = PlusExpression { name(ItemEmpty.name()) }.apply<PlusExpression> { init() }
    }
}

open class PlusExpressionB<B : PlusExpressionI<B>>(adapt: B.() -> Unit = {}) : LeftRightLiteralB<B>(adapt), PlusExpressionI<B> {
}



open class Predicate(adapt: Predicate.() -> Unit = {}) : PredicateB<Predicate>(adapt) {

    companion object {
        val EMPTY = Predicate { name(ItemEmpty.name()) }.apply<Predicate> { init() }
    }
}

open class PredicateB<B : PredicateI<B>>(adapt: B.() -> Unit = {}) : ExpressionB<B>(adapt), PredicateI<B> {
}



open class PropsRef(adapt: PropsRef.() -> Unit = {}) : PropsRefB<PropsRef>(adapt) {

    companion object {
        val EMPTY = PropsRef { name(ItemEmpty.name()) }.apply<PropsRef> { init() }
    }
}

open class PropsRefB<B : PropsRefI<B>>(adapt: B.() -> Unit = {}) : MacroCompositeB<B>(adapt), PropsRefI<B> {

    override fun isAll(): Boolean = attr(ALL, { false })
    override fun all(value: Boolean): B = apply { attr(ALL, value) }

    override fun props(): ListMultiHolder<AttributeI<*>> = itemAsList(PROPS, AttributeI::class.java, true)
    override fun props(vararg value: AttributeI<*>): B = apply { props().addItems(value.asList()) }
    override fun prop(value: AttributeI<*>): AttributeI<*> = applyAndReturn { props().addItem(value); value }
    override fun prop(value: AttributeI<*>.() -> Unit): AttributeI<*> = prop(Attribute(value))

    override fun fillSupportsItems() {
        props()
        super.fillSupportsItems()
    }

    companion object {
        val ALL = "_all"
        val PROPS = "_props"
    }
}



open class RemainderAssignAction(adapt: RemainderAssignAction.() -> Unit = {}) : RemainderAssignActionB<RemainderAssignAction>(adapt) {

    companion object {
        val EMPTY = RemainderAssignAction { name(ItemEmpty.name()) }.apply<RemainderAssignAction> { init() }
    }
}

open class RemainderAssignActionB<B : RemainderAssignActionI<B>>(adapt: B.() -> Unit = {}) : ApplyActionB<B>(adapt), RemainderAssignActionI<B> {
}



open class RemainderExpression(adapt: RemainderExpression.() -> Unit = {}) : RemainderExpressionB<RemainderExpression>(adapt) {

    companion object {
        val EMPTY = RemainderExpression { name(ItemEmpty.name()) }.apply<RemainderExpression> { init() }
    }
}

open class RemainderExpressionB<B : RemainderExpressionI<B>>(adapt: B.() -> Unit = {}) : LeftRightLiteralB<B>(adapt), RemainderExpressionI<B> {
}



open class StructureUnit(adapt: StructureUnit.() -> Unit = {}) : StructureUnitB<StructureUnit>(adapt) {

    companion object {
        val EMPTY = StructureUnit { name(ItemEmpty.name()) }.apply<StructureUnit> { init() }
    }
}

open class StructureUnitB<B : StructureUnitI<B>>(adapt: B.() -> Unit = {}) : MacroCompositeB<B>(adapt), StructureUnitI<B> {

    override fun artifact(): String = attr(ARTIFACT, { "" })
    override fun artifact(value: String): B = apply { attr(ARTIFACT, value) }

    override fun fullName(): String = attr(FULLNAME, { "" })
    override fun fullName(value: String): B = apply { attr(FULLNAME, value) }

    override fun key(): String = attr(KEY, { "" })
    override fun key(value: String): B = apply { attr(KEY, value) }

    companion object {
        val ARTIFACT = "_artifact"
        val FULLNAME = "_fullName"
        val KEY = "_key"
    }
}



open class TimesAssignAction(adapt: TimesAssignAction.() -> Unit = {}) : TimesAssignActionB<TimesAssignAction>(adapt) {

    companion object {
        val EMPTY = TimesAssignAction { name(ItemEmpty.name()) }.apply<TimesAssignAction> { init() }
    }
}

open class TimesAssignActionB<B : TimesAssignActionI<B>>(adapt: B.() -> Unit = {}) : ApplyActionB<B>(adapt), TimesAssignActionI<B> {
}



open class TimesExpression(adapt: TimesExpression.() -> Unit = {}) : TimesExpressionB<TimesExpression>(adapt) {

    companion object {
        val EMPTY = TimesExpression { name(ItemEmpty.name()) }.apply<TimesExpression> { init() }
    }
}

open class TimesExpressionB<B : TimesExpressionI<B>>(adapt: B.() -> Unit = {}) : LeftRightLiteralB<B>(adapt), TimesExpressionI<B> {
}



open class Type(adapt: Type.() -> Unit = {}) : TypeB<Type>(adapt) {

    companion object {
        val EMPTY = Type { name(ItemEmpty.name()) }.apply<Type> { init() }
    }
}

open class TypeB<B : TypeI<B>>(adapt: B.() -> Unit = {}) : MacroCompositeB<B>(adapt), TypeI<B> {

    override fun constructors(): ListMultiHolder<ConstructorI<*>> = itemAsList(CONSTRUCTORS, ConstructorI::class.java, true)
    override fun constructors(vararg value: ConstructorI<*>): B = apply { constructors().addItems(value.asList()) }
    override fun constr(value: ConstructorI<*>): ConstructorI<*> = applyAndReturn { constructors().addItem(value); value }
    override fun constr(value: ConstructorI<*>.() -> Unit): ConstructorI<*> = constr(Constructor(value))

    override fun defaultValue(): Any? = attr(DEFAULTVALUE)
    override fun defaultValue(value: Any?): B = apply { attr(DEFAULTVALUE, value) }

    override fun generics(): ListMultiHolder<GenericI<*>> = itemAsList(GENERICS, GenericI::class.java, true)
    override fun generics(vararg value: GenericI<*>): B = apply { generics().addItems(value.asList()) }
    override fun G(value: GenericI<*>): GenericI<*> = applyAndReturn { generics().addItem(value); value }
    override fun G(value: GenericI<*>.() -> Unit): GenericI<*> = G(Generic(value))

    override fun isIfc(): Boolean = attr(IFC, { false })
    override fun ifc(value: Boolean): B = apply { attr(IFC, value) }

    override fun macroEmptyInstance(): String? = attr(MACROEMPTYINSTANCE)
    override fun macroEmptyInstance(value: String?): B = apply { attr(MACROEMPTYINSTANCE, value) }

    override fun isMulti(): Boolean = attr(MULTI, { false })
    override fun multi(value: Boolean): B = apply { attr(MULTI, value) }

    override fun isNonBlock(): Boolean = attr(NONBLOCK, { false })
    override fun nonBlock(value: Boolean): B = apply { attr(NONBLOCK, value) }

    override fun isOpen(): Boolean = attr(OPEN, { true })
    override fun open(value: Boolean): B = apply { attr(OPEN, value) }

    override fun operations(): ListMultiHolder<OperationI<*>> = itemAsList(OPERATIONS, OperationI::class.java, true)
    override fun operations(vararg value: OperationI<*>): B = apply { operations().addItems(value.asList()) }
    override fun op(value: OperationI<*>): OperationI<*> = applyAndReturn { operations().addItem(value); value }
    override fun op(value: OperationI<*>.() -> Unit): OperationI<*> = op(Operation(value))

    override fun props(): ListMultiHolder<AttributeI<*>> = itemAsList(PROPS, AttributeI::class.java, true)
    override fun props(vararg value: AttributeI<*>): B = apply { props().addItems(value.asList()) }
    override fun prop(value: AttributeI<*>): AttributeI<*> = applyAndReturn { props().addItem(value); value }
    override fun prop(value: AttributeI<*>.() -> Unit): AttributeI<*> = prop(Attribute(value))

    override fun superUnitFor(): ListMultiHolder<TypeI<*>> = itemAsList(SUPERUNITFOR, TypeI::class.java, true)
    override fun superUnitFor(vararg value: TypeI<*>): B = apply { superUnitFor().addItems(value.asList()) }

    override fun superUnits(): ListMultiHolder<TypeI<*>> = itemAsList(SUPERUNITS, TypeI::class.java, true)
    override fun superUnits(vararg value: TypeI<*>): B = apply { superUnits().addItems(value.asList()) }

    override fun isToEqualsAll(): Boolean = attr(TOEQUALSALL, { false })
    override fun toEqualsAll(value: Boolean): B = apply { attr(TOEQUALSALL, value) }

    override fun isToStrAll(): Boolean = attr(TOSTRALL, { false })
    override fun toStrAll(value: Boolean): B = apply { attr(TOSTRALL, value) }

    override fun isVirtual(): Boolean = attr(VIRTUAL, { false })
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
        val DEFAULTVALUE = "_defaultValue"
        val GENERICS = "_generics"
        val IFC = "_ifc"
        val MACROEMPTYINSTANCE = "_macroEmptyInstance"
        val MULTI = "_multi"
        val NONBLOCK = "_nonBlock"
        val OPEN = "_open"
        val OPERATIONS = "_operations"
        val PROPS = "_props"
        val SUPERUNITFOR = "__superUnitFor"
        val SUPERUNITS = "__superUnits"
        val TOEQUALSALL = "_toEqualsAll"
        val TOSTRALL = "_toStrAll"
        val VIRTUAL = "_virtual"
    }
}



open class Values(adapt: Values.() -> Unit = {}) : ValuesB<Values>(adapt) {

    companion object {
        val EMPTY = Values { name(ItemEmpty.name()) }.apply<Values> { init() }
    }
}

open class ValuesB<B : ValuesI<B>>(adapt: B.() -> Unit = {}) : DataTypeB<B>(adapt), ValuesI<B> {
}



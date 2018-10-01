package ee.lang


open class Action(value: Action.() -> Unit = {}) : ActionB<Action>(value) {

    companion object {
        val EMPTY = Action { name(ItemEmpty.name()) }.apply<Action> { init() }
    }
}

open class ActionB<B : ActionI<B>>(value: B.() -> Unit = {}) : LogicUnitB<B>(value), ActionI<B> {
}



open class AndPredicate(value: AndPredicate.() -> Unit = {}) : AndPredicateB<AndPredicate>(value) {

    companion object {
        val EMPTY = AndPredicate { name(ItemEmpty.name()) }.apply<AndPredicate> { init() }
    }
}

open class AndPredicateB<B : AndPredicateI<B>>(value: B.() -> Unit = {}) : LeftRightPredicatesPredicateB<B>(value), AndPredicateI<B> {
}



open class ApplyAction(value: ApplyAction.() -> Unit = {}) : ApplyActionB<ApplyAction>(value) {

    companion object {
        val EMPTY = ApplyAction { name(ItemEmpty.name()) }.apply<ApplyAction> { init() }
    }
}

open class ApplyActionB<B : ApplyActionI<B>>(value: B.() -> Unit = {}) : ActionB<B>(value), ApplyActionI<B> {

    override fun target(): AttributeI<*> = attr(target, { Attribute.EMPTY })
    override fun target(value: AttributeI<*>): B = apply { attr(target, value) }

    override fun value(): LiteralI<*> = attr(value, { Literal.EMPTY })
    override fun value(aValue: LiteralI<*>): B = apply { attr(value, aValue) }

    companion object {
        val target = "_target"
        val value = "_value"
    }
}



open class AssignAction(value: AssignAction.() -> Unit = {}) : AssignActionB<AssignAction>(value) {

    companion object {
        val EMPTY = AssignAction { name(ItemEmpty.name()) }.apply<AssignAction> { init() }
    }
}

open class AssignActionB<B : AssignActionI<B>>(value: B.() -> Unit = {}) : ApplyActionB<B>(value), AssignActionI<B> {
}



open class Attribute(value: Attribute.() -> Unit = {}) : AttributeB<Attribute>(value) {

    companion object {
        val EMPTY = Attribute { name(ItemEmpty.name()) }.apply<Attribute> { init() }
    }
}

open class AttributeB<B : AttributeI<B>>(value: B.() -> Unit = {}) : LiteralB<B>(value), AttributeI<B> {

    override fun isAccessible(): Boolean? = attr(accessible)
    override fun accessible(value: Boolean?): B = apply { attr(accessible, value) }

    override fun isAnonymous(): Boolean = attr(anonymous, { false })
    override fun anonymous(value: Boolean): B = apply { attr(anonymous, value) }

    override fun isDefault(): Boolean = attr(default, { false })
    override fun default(value: Boolean): B = apply { attr(default, value) }

    override fun externalName(): String? = attr(externalName)
    override fun externalName(value: String?): B = apply { attr(externalName, value) }

    override fun isFixValue(): Boolean = attr(fixValue, { false })
    override fun fixValue(value: Boolean): B = apply { attr(fixValue, value) }

    override fun isHidden(): Boolean = attr(hidden, { false })
    override fun hidden(value: Boolean): B = apply { attr(hidden, value) }

    override fun isInherited(): Boolean = attr(inherited, { false })
    override fun inherited(value: Boolean): B = apply { attr(inherited, value) }

    override fun isInitByDefaultTypeValue(): Boolean = attr(initByDefaultTypeValue, { true })
    override fun initByDefaultTypeValue(value: Boolean): B = apply { attr(initByDefaultTypeValue, value) }

    override fun isKey(): Boolean = attr(key, { false })
    override fun key(value: Boolean): B = apply { attr(key, value) }

    override fun length(): Int? = attr(length)
    override fun length(value: Int?): B = apply { attr(length, value) }

    override fun isMeta(): Boolean = attr(meta, { false })
    override fun meta(value: Boolean): B = apply { attr(meta, value) }

    override fun isMulti(): Boolean = attr(multi, { false })
    override fun multi(value: Boolean): B = apply { attr(multi, value) }

    override fun isMutable(): Boolean? = attr(mutable)
    override fun mutable(value: Boolean?): B = apply { attr(mutable, value) }

    override fun nonFluent(): String = attr(nonFluent, { "" })
    override fun nonFluent(value: String): B = apply { attr(nonFluent, value) }

    override fun isNullable(): Boolean = attr(nullable, { false })
    override fun nullable(value: Boolean): B = apply { attr(nullable, value) }

    override fun isOpen(): Boolean = attr(open, { false })
    override fun open(value: Boolean): B = apply { attr(open, value) }

    override fun isReplaceable(): Boolean? = attr(replaceable)
    override fun replaceable(value: Boolean?): B = apply { attr(replaceable, value) }

    override fun isUnique(): Boolean = attr(unique, { false })
    override fun unique(value: Boolean): B = apply { attr(unique, value) }

    companion object {
        val accessible = "_accessible"
        val anonymous = "_anonymous"
        val default = "_default"
        val externalName = "_externalName"
        val fixValue = "_fixValue"
        val hidden = "_hidden"
        val inherited = "_inherited"
        val initByDefaultTypeValue = "_initByDefaultTypeValue"
        val key = "_key"
        val length = "_length"
        val meta = "_meta"
        val multi = "_multi"
        val mutable = "_mutable"
        val nonFluent = "_nonFluent"
        val nullable = "_nullable"
        val open = "_open"
        val replaceable = "_replaceable"
        val unique = "_unique"
    }
}



open class Basic(value: Basic.() -> Unit = {}) : BasicB<Basic>(value) {

    companion object {
        val EMPTY = Basic { name(ItemEmpty.name()) }.apply<Basic> { init() }
    }
}

open class BasicB<B : BasicI<B>>(value: B.() -> Unit = {}) : DataTypeB<B>(value), BasicI<B> {
}



open class CompilationUnit(value: CompilationUnit.() -> Unit = {}) : CompilationUnitB<CompilationUnit>(value) {

    companion object {
        val EMPTY = CompilationUnitEmpty
    }
}

open class CompilationUnitB<B : CompilationUnitI<B>>(value: B.() -> Unit = {}) : TypeB<B>(value), CompilationUnitI<B> {

    override fun isBase(): Boolean = attr(base, { false })
    override fun base(value: Boolean): B = apply { attr(base, value) }

    companion object {
        val base = "_base"
    }
}



open class Constructor(value: Constructor.() -> Unit = {}) : ConstructorB<Constructor>(value) {

    companion object {
        val EMPTY = Constructor { name(ItemEmpty.name()) }.apply<Constructor> { init() }
    }
}

open class ConstructorB<B : ConstructorI<B>>(value: B.() -> Unit = {}) : LogicUnitB<B>(value), ConstructorI<B> {

    override fun isPrimary(): Boolean = attr(primary, { false })
    override fun primary(value: Boolean): B = apply { attr(primary, value) }

    companion object {
        val primary = "_primary"
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



open class DecrementExpression(value: DecrementExpression.() -> Unit = {}) : DecrementExpressionB<DecrementExpression>(value) {

    companion object {
        val EMPTY = DecrementExpression { name(ItemEmpty.name()) }.apply<DecrementExpression> { init() }
    }
}

open class DecrementExpressionB<B : DecrementExpressionI<B>>(value: B.() -> Unit = {}) : LiteralB<B>(value), DecrementExpressionI<B> {
}



open class DivideAssignAction(value: DivideAssignAction.() -> Unit = {}) : DivideAssignActionB<DivideAssignAction>(value) {

    companion object {
        val EMPTY = DivideAssignAction { name(ItemEmpty.name()) }.apply<DivideAssignAction> { init() }
    }
}

open class DivideAssignActionB<B : DivideAssignActionI<B>>(value: B.() -> Unit = {}) : ApplyActionB<B>(value), DivideAssignActionI<B> {
}



open class DivideExpression(value: DivideExpression.() -> Unit = {}) : DivideExpressionB<DivideExpression>(value) {

    companion object {
        val EMPTY = DivideExpression { name(ItemEmpty.name()) }.apply<DivideExpression> { init() }
    }
}

open class DivideExpressionB<B : DivideExpressionI<B>>(value: B.() -> Unit = {}) : LeftRightLiteralB<B>(value), DivideExpressionI<B> {
}



open class EnumLiteral(value: EnumLiteral.() -> Unit = {}) : EnumLiteralB<EnumLiteral>(value) {

    companion object {
        val EMPTY = EnumLiteral { name(ItemEmpty.name()) }.apply<EnumLiteral> { init() }
    }
}

open class EnumLiteralB<B : EnumLiteralI<B>>(value: B.() -> Unit = {}) : LiteralB<B>(value), EnumLiteralI<B> {
}



open class EnumType(value: EnumType.() -> Unit = {}) : EnumTypeB<EnumType>(value) {

    companion object {
        val EMPTY = EnumType { name(ItemEmpty.name()) }.apply<EnumType> { init() }
    }
}

open class EnumTypeB<B : EnumTypeI<B>>(value: B.() -> Unit = {}) : DataTypeB<B>(value), EnumTypeI<B> {

    override fun literals(): ListMultiHolder<EnumLiteralI<*>> = itemAsList(literals, EnumLiteralI::class.java, true)
    override fun literals(vararg value: EnumLiteralI<*>): B = apply { literals().addItems(value.asList()) }
    override fun lit(value: EnumLiteralI<*>): EnumLiteralI<*> = applyAndReturn { literals().addItem(value); value }
    override fun lit(value: EnumLiteralI<*>.() -> Unit): EnumLiteralI<*> = lit(EnumLiteral(value))

    override fun fillSupportsItems() {
        literals()
        super.fillSupportsItems()
    }

    companion object {
        val literals = "_literals"
    }
}



open class EqPredicate(value: EqPredicate.() -> Unit = {}) : EqPredicateB<EqPredicate>(value) {

    companion object {
        val EMPTY = EqPredicate { name(ItemEmpty.name()) }.apply<EqPredicate> { init() }
    }
}

open class EqPredicateB<B : EqPredicateI<B>>(value: B.() -> Unit = {}) : LeftRightPredicateB<B>(value), EqPredicateI<B> {
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

    override fun type(): TypeI<*> = attr(type, { Type.EMPTY })
    override fun type(value: TypeI<*>): B = apply { attr(type, value) }

    companion object {
        val type = "__type"
    }
}



open class GtPredicate(value: GtPredicate.() -> Unit = {}) : GtPredicateB<GtPredicate>(value) {

    companion object {
        val EMPTY = GtPredicate { name(ItemEmpty.name()) }.apply<GtPredicate> { init() }
    }
}

open class GtPredicateB<B : GtPredicateI<B>>(value: B.() -> Unit = {}) : LeftRightPredicateB<B>(value), GtPredicateI<B> {
}



open class GtePredicate(value: GtePredicate.() -> Unit = {}) : GtePredicateB<GtePredicate>(value) {

    companion object {
        val EMPTY = GtePredicate { name(ItemEmpty.name()) }.apply<GtePredicate> { init() }
    }
}

open class GtePredicateB<B : GtePredicateI<B>>(value: B.() -> Unit = {}) : LeftRightPredicateB<B>(value), GtePredicateI<B> {
}



open class IncrementExpression(value: IncrementExpression.() -> Unit = {}) : IncrementExpressionB<IncrementExpression>(value) {

    companion object {
        val EMPTY = IncrementExpression { name(ItemEmpty.name()) }.apply<IncrementExpression> { init() }
    }
}

open class IncrementExpressionB<B : IncrementExpressionI<B>>(value: B.() -> Unit = {}) : LiteralB<B>(value), IncrementExpressionI<B> {
}



open class Lambda(value: Lambda.() -> Unit = {}) : LambdaB<Lambda>(value) {

    companion object {
        val EMPTY = Lambda { name(ItemEmpty.name()) }.apply<Lambda> { init() }
    }
}

open class LambdaB<B : LambdaI<B>>(value: B.() -> Unit = {}) : TypeB<B>(value), LambdaI<B> {

    override fun operation(): OperationI<*> = attr(operation, { Operation.EMPTY })
    override fun operation(value: OperationI<*>): B = apply { attr(operation, value) }

    companion object {
        val operation = "_operation"
    }
}



open class LeftRightLiteral(value: LeftRightLiteral.() -> Unit = {}) : LeftRightLiteralB<LeftRightLiteral>(value) {

    companion object {
        val EMPTY = LeftRightLiteral { name(ItemEmpty.name()) }.apply<LeftRightLiteral> { init() }
    }
}

open class LeftRightLiteralB<B : LeftRightLiteralI<B>>(value: B.() -> Unit = {}) : LiteralB<B>(value), LeftRightLiteralI<B> {

    override fun left(): LiteralI<*> = attr(left, { Literal.EMPTY })
    override fun left(value: LiteralI<*>): B = apply { attr(left, value) }

    override fun right(): LiteralI<*> = attr(right, { Literal.EMPTY })
    override fun right(value: LiteralI<*>): B = apply { attr(right, value) }

    companion object {
        val left = "_left"
        val right = "_right"
    }
}



open class LeftRightPredicate(value: LeftRightPredicate.() -> Unit = {}) : LeftRightPredicateB<LeftRightPredicate>(value) {

    companion object {
        val EMPTY = LeftRightPredicate { name(ItemEmpty.name()) }.apply<LeftRightPredicate> { init() }
    }
}

open class LeftRightPredicateB<B : LeftRightPredicateI<B>>(value: B.() -> Unit = {}) : PredicateB<B>(value), LeftRightPredicateI<B> {

    override fun left(): LiteralI<*> = attr(left, { Literal.EMPTY })
    override fun left(value: LiteralI<*>): B = apply { attr(left, value) }

    override fun right(): LiteralI<*> = attr(right, { Literal.EMPTY })
    override fun right(value: LiteralI<*>): B = apply { attr(right, value) }

    companion object {
        val left = "_left"
        val right = "_right"
    }
}



open class LeftRightPredicatesPredicate(value: LeftRightPredicatesPredicate.() -> Unit = {}) : LeftRightPredicatesPredicateB<LeftRightPredicatesPredicate>(value) {

    companion object {
        val EMPTY = LeftRightPredicatesPredicate { name(ItemEmpty.name()) }.apply<LeftRightPredicatesPredicate> { init() }
    }
}

open class LeftRightPredicatesPredicateB<B : LeftRightPredicatesPredicateI<B>>(value: B.() -> Unit = {}) : PredicateB<B>(value), LeftRightPredicatesPredicateI<B> {

    override fun left(): PredicateI<*> = attr(left, { Predicate.EMPTY })
    override fun left(value: PredicateI<*>): B = apply { attr(left, value) }

    override fun right(): PredicateI<*> = attr(right, { Predicate.EMPTY })
    override fun right(value: PredicateI<*>): B = apply { attr(right, value) }

    companion object {
        val left = "_left"
        val right = "_right"
    }
}



open class Literal(value: Literal.() -> Unit = {}) : LiteralB<Literal>(value) {

    companion object {
        val EMPTY = Literal { name(ItemEmpty.name()) }.apply<Literal> { init() }
    }
}

open class LiteralB<B : LiteralI<B>>(value: B.() -> Unit = {}) : ExpressionB<B>(value), LiteralI<B> {

    override fun params(): ListMultiHolder<AttributeI<*>> = itemAsList(params, AttributeI::class.java, true)
    override fun params(vararg value: AttributeI<*>): B = apply { params().addItems(value.asList()) }

    override fun type(): TypeI<*> = attr(type, { n.Void })
    override fun type(value: TypeI<*>): B = apply { attr(type, value) }

    override fun value(): Any? = attr(value)
    override fun value(aValue: Any?): B = apply { attr(value, aValue) }

    override fun fillSupportsItems() {
        params()
        super.fillSupportsItems()
    }

    companion object {
        val params = "_params"
        val type = "__type"
        val value = "_value"
    }
}



open class LogicUnit(value: LogicUnit.() -> Unit = {}) : LogicUnitB<LogicUnit>(value) {

    companion object {
        val EMPTY = LogicUnitEmpty
    }
}

open class LogicUnitB<B : LogicUnitI<B>>(value: B.() -> Unit = {}) : ExpressionB<B>(value), LogicUnitI<B> {

    override fun isErrorHandling(): Boolean = attr(errorHandling, { true })
    override fun errorHandling(value: Boolean): B = apply { attr(errorHandling, value) }

    override fun params(): ListMultiHolder<AttributeI<*>> = itemAsList(params, AttributeI::class.java, true)
    override fun params(vararg value: AttributeI<*>): B = apply { params().addItems(value.asList()) }

    override fun superUnit(): LogicUnitI<*> = attr(superUnit, { LogicUnit.EMPTY })
    override fun superUnit(value: LogicUnitI<*>): B = apply { attr(superUnit, value) }

    override fun isVirtual(): Boolean = attr(virtual, { false })
    override fun virtual(value: Boolean): B = apply { attr(virtual, value) }

    override fun isVisible(): Boolean = attr(visible, { true })
    override fun visible(value: Boolean): B = apply { attr(visible, value) }

    override fun fillSupportsItems() {
        params()
        super.fillSupportsItems()
    }

    companion object {
        val errorHandling = "_errorHandling"
        val params = "_params"
        val superUnit = "__superUnit"
        val virtual = "_virtual"
        val visible = "_visible"
    }
}



open class LtPredicate(value: LtPredicate.() -> Unit = {}) : LtPredicateB<LtPredicate>(value) {

    companion object {
        val EMPTY = LtPredicate { name(ItemEmpty.name()) }.apply<LtPredicate> { init() }
    }
}

open class LtPredicateB<B : LtPredicateI<B>>(value: B.() -> Unit = {}) : LeftRightPredicateB<B>(value), LtPredicateI<B> {
}



open class LtePredicate(value: LtePredicate.() -> Unit = {}) : LtePredicateB<LtePredicate>(value) {

    companion object {
        val EMPTY = LtePredicate { name(ItemEmpty.name()) }.apply<LtePredicate> { init() }
    }
}

open class LtePredicateB<B : LtePredicateI<B>>(value: B.() -> Unit = {}) : LeftRightPredicateB<B>(value), LtePredicateI<B> {
}



open class MacroComposite(value: MacroComposite.() -> Unit = {}) : MacroCompositeB<MacroComposite>(value) {

    companion object {
        val EMPTY = MacroComposite { name(ItemEmpty.name()) }.apply<MacroComposite> { init() }
    }
}

open class MacroCompositeB<B : MacroCompositeI<B>>(value: B.() -> Unit = {}) : CompositeB<B>(value), MacroCompositeI<B> {

    override fun macrosAfter(): ListMultiHolder<String> = itemAsList(macrosAfter, String::class.java, true)
    override fun macrosAfter(vararg value: String): B = apply { macrosAfter().addItems(value.asList()) }

    override fun macrosAfterBody(): ListMultiHolder<String> = itemAsList(macrosAfterBody, String::class.java, true)
    override fun macrosAfterBody(vararg value: String): B = apply { macrosAfterBody().addItems(value.asList()) }

    override fun macrosBefore(): ListMultiHolder<String> = itemAsList(macrosBefore, String::class.java, true)
    override fun macrosBefore(vararg value: String): B = apply { macrosBefore().addItems(value.asList()) }

    override fun macrosBeforeBody(): ListMultiHolder<String> = itemAsList(macrosBeforeBody, String::class.java, true)
    override fun macrosBeforeBody(vararg value: String): B = apply { macrosBeforeBody().addItems(value.asList()) }

    override fun macrosBody(): ListMultiHolder<String> = itemAsList(macrosBody, String::class.java, true)
    override fun macrosBody(vararg value: String): B = apply { macrosBody().addItems(value.asList()) }

    override fun tags(): ListMultiHolder<String> = itemAsList(tags, String::class.java, true)
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
        val macrosAfter = "_macrosAfter"
        val macrosAfterBody = "_macrosAfterBody"
        val macrosBefore = "_macrosBefore"
        val macrosBeforeBody = "_macrosBeforeBody"
        val macrosBody = "_macrosBody"
        val tags = "_tags"
    }
}



open class MinusAssignAction(value: MinusAssignAction.() -> Unit = {}) : MinusAssignActionB<MinusAssignAction>(value) {

    companion object {
        val EMPTY = MinusAssignAction { name(ItemEmpty.name()) }.apply<MinusAssignAction> { init() }
    }
}

open class MinusAssignActionB<B : MinusAssignActionI<B>>(value: B.() -> Unit = {}) : ApplyActionB<B>(value), MinusAssignActionI<B> {
}



open class MinusExpression(value: MinusExpression.() -> Unit = {}) : MinusExpressionB<MinusExpression>(value) {

    companion object {
        val EMPTY = MinusExpression { name(ItemEmpty.name()) }.apply<MinusExpression> { init() }
    }
}

open class MinusExpressionB<B : MinusExpressionI<B>>(value: B.() -> Unit = {}) : LeftRightLiteralB<B>(value), MinusExpressionI<B> {
}



open class NativeType(value: NativeType.() -> Unit = {}) : NativeTypeB<NativeType>(value) {

    companion object {
        val EMPTY = NativeType { name(ItemEmpty.name()) }.apply<NativeType> { init() }
    }
}

open class NativeTypeB<B : NativeTypeI<B>>(value: B.() -> Unit = {}) : TypeB<B>(value), NativeTypeI<B> {
}



open class NePredicate(value: NePredicate.() -> Unit = {}) : NePredicateB<NePredicate>(value) {

    companion object {
        val EMPTY = NePredicate { name(ItemEmpty.name()) }.apply<NePredicate> { init() }
    }
}

open class NePredicateB<B : NePredicateI<B>>(value: B.() -> Unit = {}) : LeftRightPredicateB<B>(value), NePredicateI<B> {
}



open class NotPredicate(value: NotPredicate.() -> Unit = {}) : NotPredicateB<NotPredicate>(value) {

    companion object {
        val EMPTY = NotPredicate { name(ItemEmpty.name()) }.apply<NotPredicate> { init() }
    }
}

open class NotPredicateB<B : NotPredicateI<B>>(value: B.() -> Unit = {}) : PredicateB<B>(value), NotPredicateI<B> {

    override fun value(): PredicateI<*> = attr(value, { Predicate.EMPTY })
    override fun value(aValue: PredicateI<*>): B = apply { attr(value, aValue) }

    companion object {
        val value = "_value"
    }
}



open class Operation(value: Operation.() -> Unit = {}) : OperationB<Operation>(value) {

    companion object {
        val EMPTY = Operation { name(ItemEmpty.name()) }.apply<Operation> { init() }
    }
}

open class OperationB<B : OperationI<B>>(value: B.() -> Unit = {}) : LogicUnitB<B>(value), OperationI<B> {

    override fun generics(): ListMultiHolder<GenericI<*>> = itemAsList(generics, GenericI::class.java, true)
    override fun generics(vararg value: GenericI<*>): B = apply { generics().addItems(value.asList()) }
    override fun G(value: GenericI<*>): GenericI<*> = applyAndReturn { generics().addItem(value); value }
    override fun G(value: GenericI<*>.() -> Unit): GenericI<*> = G(Generic(value))

    override fun isOpen(): Boolean = attr(open, { true })
    override fun open(value: Boolean): B = apply { attr(open, value) }

    override fun returns(): ListMultiHolder<AttributeI<*>> = itemAsList(returns, AttributeI::class.java, true)
    override fun returns(vararg value: AttributeI<*>): B = apply { returns().addItems(value.asList()) }
    override fun ret(value: AttributeI<*>): AttributeI<*> = applyAndReturn { returns().addItem(value); value }
    override fun ret(value: AttributeI<*>.() -> Unit): AttributeI<*> = ret(Attribute(value))

    override fun isSuspend(): Boolean = attr(suspend, { false })
    override fun suspend(value: Boolean): B = apply { attr(suspend, value) }

    override fun fillSupportsItems() {
        generics()
        returns()
        super.fillSupportsItems()
    }

    companion object {
        val generics = "_generics"
        val open = "_open"
        val returns = "_returns"
        val suspend = "_suspend"
    }
}



open class OrPredicate(value: OrPredicate.() -> Unit = {}) : OrPredicateB<OrPredicate>(value) {

    companion object {
        val EMPTY = OrPredicate { name(ItemEmpty.name()) }.apply<OrPredicate> { init() }
    }
}

open class OrPredicateB<B : OrPredicateI<B>>(value: B.() -> Unit = {}) : LeftRightPredicatesPredicateB<B>(value), OrPredicateI<B> {
}



open class PlusAssignAction(value: PlusAssignAction.() -> Unit = {}) : PlusAssignActionB<PlusAssignAction>(value) {

    companion object {
        val EMPTY = PlusAssignAction { name(ItemEmpty.name()) }.apply<PlusAssignAction> { init() }
    }
}

open class PlusAssignActionB<B : PlusAssignActionI<B>>(value: B.() -> Unit = {}) : ApplyActionB<B>(value), PlusAssignActionI<B> {
}



open class PlusExpression(value: PlusExpression.() -> Unit = {}) : PlusExpressionB<PlusExpression>(value) {

    companion object {
        val EMPTY = PlusExpression { name(ItemEmpty.name()) }.apply<PlusExpression> { init() }
    }
}

open class PlusExpressionB<B : PlusExpressionI<B>>(value: B.() -> Unit = {}) : LeftRightLiteralB<B>(value), PlusExpressionI<B> {
}



open class Predicate(value: Predicate.() -> Unit = {}) : PredicateB<Predicate>(value) {

    companion object {
        val EMPTY = Predicate { name(ItemEmpty.name()) }.apply<Predicate> { init() }
    }
}

open class PredicateB<B : PredicateI<B>>(value: B.() -> Unit = {}) : ExpressionB<B>(value), PredicateI<B> {
}



open class RemainderAssignAction(value: RemainderAssignAction.() -> Unit = {}) : RemainderAssignActionB<RemainderAssignAction>(value) {

    companion object {
        val EMPTY = RemainderAssignAction { name(ItemEmpty.name()) }.apply<RemainderAssignAction> { init() }
    }
}

open class RemainderAssignActionB<B : RemainderAssignActionI<B>>(value: B.() -> Unit = {}) : ApplyActionB<B>(value), RemainderAssignActionI<B> {
}



open class RemainderExpression(value: RemainderExpression.() -> Unit = {}) : RemainderExpressionB<RemainderExpression>(value) {

    companion object {
        val EMPTY = RemainderExpression { name(ItemEmpty.name()) }.apply<RemainderExpression> { init() }
    }
}

open class RemainderExpressionB<B : RemainderExpressionI<B>>(value: B.() -> Unit = {}) : LeftRightLiteralB<B>(value), RemainderExpressionI<B> {
}



open class StructureUnit(value: StructureUnit.() -> Unit = {}) : StructureUnitB<StructureUnit>(value) {

    companion object {
        val EMPTY = StructureUnit { name(ItemEmpty.name()) }.apply<StructureUnit> { init() }
    }
}

open class StructureUnitB<B : StructureUnitI<B>>(value: B.() -> Unit = {}) : MacroCompositeB<B>(value), StructureUnitI<B> {

    override fun artifact(): String = attr(artifact, { "" })
    override fun artifact(value: String): B = apply { attr(artifact, value) }

    override fun fullName(): String = attr(fullName, { "" })
    override fun fullName(value: String): B = apply { attr(fullName, value) }

    override fun key(): String = attr(key, { "" })
    override fun key(value: String): B = apply { attr(key, value) }

    companion object {
        val artifact = "_artifact"
        val fullName = "_fullName"
        val key = "_key"
    }
}



open class TimesAssignAction(value: TimesAssignAction.() -> Unit = {}) : TimesAssignActionB<TimesAssignAction>(value) {

    companion object {
        val EMPTY = TimesAssignAction { name(ItemEmpty.name()) }.apply<TimesAssignAction> { init() }
    }
}

open class TimesAssignActionB<B : TimesAssignActionI<B>>(value: B.() -> Unit = {}) : ApplyActionB<B>(value), TimesAssignActionI<B> {
}



open class TimesExpression(value: TimesExpression.() -> Unit = {}) : TimesExpressionB<TimesExpression>(value) {

    companion object {
        val EMPTY = TimesExpression { name(ItemEmpty.name()) }.apply<TimesExpression> { init() }
    }
}

open class TimesExpressionB<B : TimesExpressionI<B>>(value: B.() -> Unit = {}) : LeftRightLiteralB<B>(value), TimesExpressionI<B> {
}



open class Type(value: Type.() -> Unit = {}) : TypeB<Type>(value) {

    companion object {
        val EMPTY = Type { name(ItemEmpty.name()) }.apply<Type> { init() }
    }
}

open class TypeB<B : TypeI<B>>(value: B.() -> Unit = {}) : MacroCompositeB<B>(value), TypeI<B> {

    override fun constructors(): ListMultiHolder<ConstructorI<*>> = itemAsList(constructors, ConstructorI::class.java, true)
    override fun constructors(vararg value: ConstructorI<*>): B = apply { constructors().addItems(value.asList()) }
    override fun constr(value: ConstructorI<*>): ConstructorI<*> = applyAndReturn { constructors().addItem(value); value }
    override fun constr(value: ConstructorI<*>.() -> Unit): ConstructorI<*> = constr(Constructor(value))

    override fun defaultValue(): Any? = attr(defaultValue)
    override fun defaultValue(value: Any?): B = apply { attr(defaultValue, value) }

    override fun generics(): ListMultiHolder<GenericI<*>> = itemAsList(generics, GenericI::class.java, true)
    override fun generics(vararg value: GenericI<*>): B = apply { generics().addItems(value.asList()) }
    override fun G(value: GenericI<*>): GenericI<*> = applyAndReturn { generics().addItem(value); value }
    override fun G(value: GenericI<*>.() -> Unit): GenericI<*> = G(Generic(value))

    override fun isIfc(): Boolean = attr(ifc, { false })
    override fun ifc(value: Boolean): B = apply { attr(ifc, value) }

    override fun isMulti(): Boolean = attr(multi, { false })
    override fun multi(value: Boolean): B = apply { attr(multi, value) }

    override fun isOpen(): Boolean = attr(open, { true })
    override fun open(value: Boolean): B = apply { attr(open, value) }

    override fun operations(): ListMultiHolder<OperationI<*>> = itemAsList(operations, OperationI::class.java, true)
    override fun operations(vararg value: OperationI<*>): B = apply { operations().addItems(value.asList()) }
    override fun op(value: OperationI<*>): OperationI<*> = applyAndReturn { operations().addItem(value); value }
    override fun op(value: OperationI<*>.() -> Unit): OperationI<*> = op(Operation(value))

    override fun props(): ListMultiHolder<AttributeI<*>> = itemAsList(props, AttributeI::class.java, true)
    override fun props(vararg value: AttributeI<*>): B = apply { props().addItems(value.asList()) }
    override fun prop(value: AttributeI<*>): AttributeI<*> = applyAndReturn { props().addItem(value); value }
    override fun prop(value: AttributeI<*>.() -> Unit): AttributeI<*> = prop(Attribute(value))

    override fun superUnitFor(): ListMultiHolder<TypeI<*>> = itemAsList(superUnitFor, TypeI::class.java, true)
    override fun superUnitFor(vararg value: TypeI<*>): B = apply { superUnitFor().addItems(value.asList()) }

    override fun superUnits(): ListMultiHolder<TypeI<*>> = itemAsList(superUnits, TypeI::class.java, true)
    override fun superUnits(vararg value: TypeI<*>): B = apply { superUnits().addItems(value.asList()) }

    override fun isVirtual(): Boolean = attr(virtual, { false })
    override fun virtual(value: Boolean): B = apply { attr(virtual, value) }

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
        val constructors = "_constructors"
        val defaultValue = "_defaultValue"
        val generics = "_generics"
        val ifc = "_ifc"
        val multi = "_multi"
        val open = "_open"
        val operations = "_operations"
        val props = "_props"
        val superUnitFor = "__superUnitFor"
        val superUnits = "__superUnits"
        val virtual = "_virtual"
    }
}



open class Values(value: Values.() -> Unit = {}) : ValuesB<Values>(value) {

    companion object {
        val EMPTY = Values { name(ItemEmpty.name()) }.apply<Values> { init() }
    }
}

open class ValuesB<B : ValuesI<B>>(value: B.() -> Unit = {}) : DataTypeB<B>(value), ValuesI<B> {
}



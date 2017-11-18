package ee.lang


interface ActionI<B : ActionI<B>> : LogicUnitI<B> {
}


interface AndExpressionI<B : AndExpressionI<B>> : LeftRightPredicatesPredicateI<B> {
}


interface ApplyActionI<B : ApplyActionI<B>> : ActionI<B> {
    fun target(value: AttributeI<*>): B

    fun value(aValue: LiteralI<*>): B
    fun target(): AttributeI<*>

    fun value(): LiteralI<*>
}


interface AssignActionI<B : AssignActionI<B>> : ApplyActionI<B> {
}


interface AttributeI<B : AttributeI<B>> : LiteralI<B> {
    fun accessible(value: Boolean?): B

    fun anonymous(value: Boolean): B

    fun default(value: Boolean): B

    fun hidden(value: Boolean): B

    fun inherited(value: Boolean): B

    fun initByDefaultTypeValue(value: Boolean): B

    fun key(value: Boolean): B

    fun length(value: Int?): B

    fun meta(value: Boolean): B

    fun multi(value: Boolean): B

    fun mutable(value: Boolean?): B

    fun nonFluent(value: String): B

    fun nullable(value: Boolean): B

    fun open(value: Boolean): B

    fun replaceable(value: Boolean?): B

    fun unique(value: Boolean): B
    fun accessible(): Boolean?

    fun anonymous(): Boolean

    fun default(): Boolean

    fun hidden(): Boolean

    fun inherited(): Boolean

    fun initByDefaultTypeValue(): Boolean

    fun key(): Boolean

    fun length(): Int?

    fun meta(): Boolean

    fun multi(): Boolean

    fun mutable(): Boolean?

    fun nonFluent(): String

    fun nullable(): Boolean

    fun open(): Boolean

    fun replaceable(): Boolean?

    fun unique(): Boolean
}


interface CompilationUnitI<B : CompilationUnitI<B>> : TypeI<B> {
    fun base(value: Boolean): B
    fun base(): Boolean
}


interface ConstructorI<B : ConstructorI<B>> : LogicUnitI<B> {
    fun primary(value: Boolean): B
    fun primary(): Boolean
}


interface DataTypeI<B : DataTypeI<B>> : CompilationUnitI<B> {
}


interface DataTypeOperationI<B : DataTypeOperationI<B>> : OperationI<B> {
}


interface DecrementExpressionI<B : DecrementExpressionI<B>> : LiteralI<B> {
}


interface DivideAssignActionI<B : DivideAssignActionI<B>> : ApplyActionI<B> {
}


interface DivideExpressionI<B : DivideExpressionI<B>> : LeftRightLiteralI<B> {
}


interface EnumTypeI<B : EnumTypeI<B>> : DataTypeI<B> {
    fun literals(vararg value: LiteralI<*>): B
    fun literals(): ListMultiHolder<LiteralI<*>>
    fun lit(value: LiteralI<*>): LiteralI<*>
    fun lit(value: LiteralI<*>.() -> Unit = {}): LiteralI<*>
}


interface EqExpressionI<B : EqExpressionI<B>> : LeftRightPredicateI<B> {
}


interface ExpressionI<B : ExpressionI<B>> : MacroCompositeI<B> {
}


interface ExternalTypeI<B : ExternalTypeI<B>> : TypeI<B> {
}


interface GenericI<B : GenericI<B>> : TypeI<B> {
    fun type(value: TypeI<*>): B
    fun type(): TypeI<*>
}


interface GtExpressionI<B : GtExpressionI<B>> : LeftRightPredicateI<B> {
}


interface GteExpressionI<B : GteExpressionI<B>> : LeftRightPredicateI<B> {
}


interface IncrementExpressionI<B : IncrementExpressionI<B>> : LiteralI<B> {
}


interface LambdaI<B : LambdaI<B>> : TypeI<B> {
    fun operation(value: OperationI<*>): B
    fun operation(): OperationI<*>
}


interface LeftRightLiteralI<B : LeftRightLiteralI<B>> : LiteralI<B> {
    fun left(value: LiteralI<*>): B

    fun right(value: LiteralI<*>): B
    fun left(): LiteralI<*>

    fun right(): LiteralI<*>
}


interface LeftRightPredicateI<B : LeftRightPredicateI<B>> : PredicateI<B> {
    fun left(value: LiteralI<*>): B

    fun right(value: LiteralI<*>): B
    fun left(): LiteralI<*>

    fun right(): LiteralI<*>
}


interface LeftRightPredicatesPredicateI<B : LeftRightPredicatesPredicateI<B>> : PredicateI<B> {
    fun left(value: PredicateI<*>): B

    fun right(value: PredicateI<*>): B
    fun left(): PredicateI<*>

    fun right(): PredicateI<*>
}


interface LiteralI<B : LiteralI<B>> : ExpressionI<B> {
    fun params(vararg value: AttributeI<*>): B

    fun type(value: TypeI<*>): B

    fun value(aValue: Any?): B
    fun params(): ListMultiHolder<AttributeI<*>>

    fun type(): TypeI<*>

    fun value(): Any?
}


interface LogicUnitI<B : LogicUnitI<B>> : ExpressionI<B> {
    fun params(vararg value: AttributeI<*>): B

    fun superUnit(value: LogicUnitI<*>): B

    fun virtual(value: Boolean): B

    fun visible(value: Boolean): B
    fun params(): ListMultiHolder<AttributeI<*>>

    fun superUnit(): LogicUnitI<*>

    fun virtual(): Boolean

    fun visible(): Boolean
}


interface LtExpressionI<B : LtExpressionI<B>> : LeftRightPredicateI<B> {
}


interface LteExpressionI<B : LteExpressionI<B>> : LeftRightPredicateI<B> {
}


interface MacroCompositeI<B : MacroCompositeI<B>> : CompositeI<B> {
    fun macrosAfter(vararg value: String): B

    fun macrosAfterBody(vararg value: String): B

    fun macrosBefore(vararg value: String): B

    fun macrosBeforeBody(vararg value: String): B

    fun macrosBody(vararg value: String): B

    fun tags(vararg value: String): B
    fun macrosAfter(): ListMultiHolder<String>

    fun macrosAfterBody(): ListMultiHolder<String>

    fun macrosBefore(): ListMultiHolder<String>

    fun macrosBeforeBody(): ListMultiHolder<String>

    fun macrosBody(): ListMultiHolder<String>

    fun tags(): ListMultiHolder<String>
}


interface MinusAssignActionI<B : MinusAssignActionI<B>> : ApplyActionI<B> {
}


interface MinusExpressionI<B : MinusExpressionI<B>> : LeftRightLiteralI<B> {
}


interface NativeTypeI<B : NativeTypeI<B>> : TypeI<B> {
}


interface NeExpressionI<B : NeExpressionI<B>> : LeftRightPredicateI<B> {
}


interface NotExpressionI<B : NotExpressionI<B>> : PredicateI<B> {
    fun value(aValue: PredicateI<*>): B
    fun value(): PredicateI<*>
}


interface OperationI<B : OperationI<B>> : LogicUnitI<B> {
    fun generics(vararg value: GenericI<*>): B

    fun open(value: Boolean): B

    fun returns(vararg value: AttributeI<*>): B
    fun generics(): ListMultiHolder<GenericI<*>>
    fun G(value: GenericI<*>): GenericI<*>
    fun G(value: GenericI<*>.() -> Unit = {}): GenericI<*>

    fun open(): Boolean

    fun returns(): ListMultiHolder<AttributeI<*>>
    fun ret(value: AttributeI<*>): AttributeI<*>
    fun ret(value: AttributeI<*>.() -> Unit = {}): AttributeI<*>
}


interface OrExpressionI<B : OrExpressionI<B>> : LeftRightPredicatesPredicateI<B> {
}


interface PlusAssignActionI<B : PlusAssignActionI<B>> : ApplyActionI<B> {
}


interface PlusExpressionI<B : PlusExpressionI<B>> : LeftRightLiteralI<B> {
}


interface PredicateI<B : PredicateI<B>> : ExpressionI<B> {
}


interface RemainderAssignActionI<B : RemainderAssignActionI<B>> : ApplyActionI<B> {
}


interface RemainderExpressionI<B : RemainderExpressionI<B>> : LeftRightLiteralI<B> {
}


interface StructureUnitI<B : StructureUnitI<B>> : MacroCompositeI<B> {
    fun artifact(value: String): B

    fun fullName(value: String): B

    fun key(value: String): B
    fun artifact(): String

    fun fullName(): String

    fun key(): String
}


interface TimesAssignActionI<B : TimesAssignActionI<B>> : ApplyActionI<B> {
}


interface TimesExpressionI<B : TimesExpressionI<B>> : LeftRightLiteralI<B> {
}


interface TypeI<B : TypeI<B>> : MacroCompositeI<B> {
    fun constructors(vararg value: ConstructorI<*>): B

    fun defaultValue(value: Any?): B

    fun generics(vararg value: GenericI<*>): B

    fun ifc(value: Boolean): B

    fun multi(value: Boolean): B

    fun open(value: Boolean): B

    fun operations(vararg value: OperationI<*>): B

    fun props(vararg value: AttributeI<*>): B

    fun superUnitFor(vararg value: TypeI<*>): B

    fun superUnits(vararg value: TypeI<*>): B

    fun virtual(value: Boolean): B
    fun constructors(): ListMultiHolder<ConstructorI<*>>
    fun constr(value: ConstructorI<*>): ConstructorI<*>
    fun constr(value: ConstructorI<*>.() -> Unit = {}): ConstructorI<*>

    fun defaultValue(): Any?

    fun generics(): ListMultiHolder<GenericI<*>>
    fun G(value: GenericI<*>): GenericI<*>
    fun G(value: GenericI<*>.() -> Unit = {}): GenericI<*>

    fun ifc(): Boolean

    fun multi(): Boolean

    fun open(): Boolean

    fun operations(): ListMultiHolder<OperationI<*>>
    fun op(value: OperationI<*>): OperationI<*>
    fun op(value: OperationI<*>.() -> Unit = {}): OperationI<*>

    fun props(): ListMultiHolder<AttributeI<*>>
    fun prop(value: AttributeI<*>): AttributeI<*>
    fun prop(value: AttributeI<*>.() -> Unit = {}): AttributeI<*>

    fun superUnitFor(): ListMultiHolder<TypeI<*>>

    fun superUnits(): ListMultiHolder<TypeI<*>>

    fun virtual(): Boolean
}


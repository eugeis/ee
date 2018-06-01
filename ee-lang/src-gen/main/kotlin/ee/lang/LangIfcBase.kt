package ee.lang


interface ActionI<B : ActionI<B>> : LogicUnitI<B> {
}


interface AndPredicateI<B : AndPredicateI<B>> : LeftRightPredicatesPredicateI<B> {
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
    fun accessible(): B = accessible(true)
    fun notAccessible(): B = accessible(false)

    fun anonymous(value: Boolean): B
    fun anonymous(): B = anonymous(true)
    fun notAnonymous(): B = anonymous(false)

    fun default(value: Boolean): B
    fun default(): B = default(true)
    fun notDefault(): B = default(false)

    fun externalName(value: String?): B

    fun fixValue(value: Boolean): B
    fun fixValue(): B = fixValue(true)
    fun notFixValue(): B = fixValue(false)

    fun hidden(value: Boolean): B
    fun hidden(): B = hidden(true)
    fun notHidden(): B = hidden(false)

    fun inherited(value: Boolean): B
    fun inherited(): B = inherited(true)
    fun notInherited(): B = inherited(false)

    fun initByDefaultTypeValue(value: Boolean): B
    fun initByDefaultTypeValue(): B = initByDefaultTypeValue(true)
    fun notInitByDefaultTypeValue(): B = initByDefaultTypeValue(false)

    fun key(value: Boolean): B
    fun key(): B = key(true)
    fun notKey(): B = key(false)

    fun length(value: Int?): B

    fun meta(value: Boolean): B
    fun meta(): B = meta(true)
    fun notMeta(): B = meta(false)

    fun multi(value: Boolean): B
    fun multi(): B = multi(true)
    fun notMulti(): B = multi(false)

    fun mutable(value: Boolean?): B
    fun mutable(): B = mutable(true)
    fun notMutable(): B = mutable(false)

    fun nonFluent(value: String): B

    fun nullable(value: Boolean): B
    fun nullable(): B = nullable(true)
    fun notNullable(): B = nullable(false)

    fun open(value: Boolean): B
    fun open(): B = open(true)
    fun notOpen(): B = open(false)

    fun replaceable(value: Boolean?): B
    fun replaceable(): B = replaceable(true)
    fun notReplaceable(): B = replaceable(false)

    fun unique(value: Boolean): B
    fun unique(): B = unique(true)
    fun notUnique(): B = unique(false)
    fun isAccessible(): Boolean?

    fun isAnonymous(): Boolean

    fun isDefault(): Boolean

    fun externalName(): String?

    fun isFixValue(): Boolean

    fun isHidden(): Boolean

    fun isInherited(): Boolean

    fun isInitByDefaultTypeValue(): Boolean

    fun isKey(): Boolean

    fun length(): Int?

    fun isMeta(): Boolean

    fun isMulti(): Boolean

    fun isMutable(): Boolean?

    fun nonFluent(): String

    fun isNullable(): Boolean

    fun isOpen(): Boolean

    fun isReplaceable(): Boolean?

    fun isUnique(): Boolean
}


interface BasicI<B : BasicI<B>> : DataTypeI<B> {
}


interface CompilationUnitI<B : CompilationUnitI<B>> : TypeI<B> {
    fun base(value: Boolean): B
    fun base(): B = base(true)
    fun notBase(): B = base(false)
    fun isBase(): Boolean
}


interface ConstructorI<B : ConstructorI<B>> : LogicUnitI<B> {
    fun primary(value: Boolean): B
    fun primary(): B = primary(true)
    fun notPrimary(): B = primary(false)
    fun isPrimary(): Boolean
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


interface EnumLiteralI<B : EnumLiteralI<B>> : LiteralI<B> {
}


interface EnumTypeI<B : EnumTypeI<B>> : DataTypeI<B> {
    fun literals(vararg value: EnumLiteralI<*>): B
    fun literals(): ListMultiHolder<EnumLiteralI<*>>
    fun lit(value: EnumLiteralI<*>): EnumLiteralI<*>
    fun lit(value: EnumLiteralI<*>.() -> Unit = {}): EnumLiteralI<*>
}


interface EqPredicateI<B : EqPredicateI<B>> : LeftRightPredicateI<B> {
}


interface ExpressionI<B : ExpressionI<B>> : MacroCompositeI<B> {
}


interface ExternalTypeI<B : ExternalTypeI<B>> : TypeI<B> {
}


interface GenericI<B : GenericI<B>> : TypeI<B> {
    fun type(value: TypeI<*>): B
    fun type(): TypeI<*>
}


interface GtPredicateI<B : GtPredicateI<B>> : LeftRightPredicateI<B> {
}


interface GtePredicateI<B : GtePredicateI<B>> : LeftRightPredicateI<B> {
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
    fun errorHandling(value: Boolean): B
    fun errorHandling(): B = errorHandling(true)
    fun notErrorHandling(): B = errorHandling(false)

    fun params(vararg value: AttributeI<*>): B

    fun superUnit(value: LogicUnitI<*>): B

    fun virtual(value: Boolean): B
    fun virtual(): B = virtual(true)
    fun notVirtual(): B = virtual(false)

    fun visible(value: Boolean): B
    fun visible(): B = visible(true)
    fun notVisible(): B = visible(false)
    fun isErrorHandling(): Boolean

    fun params(): ListMultiHolder<AttributeI<*>>

    fun superUnit(): LogicUnitI<*>

    fun isVirtual(): Boolean

    fun isVisible(): Boolean
}


interface LtPredicateI<B : LtPredicateI<B>> : LeftRightPredicateI<B> {
}


interface LtePredicateI<B : LtePredicateI<B>> : LeftRightPredicateI<B> {
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


interface NePredicateI<B : NePredicateI<B>> : LeftRightPredicateI<B> {
}


interface NotPredicateI<B : NotPredicateI<B>> : PredicateI<B> {
    fun value(aValue: PredicateI<*>): B
    fun value(): PredicateI<*>
}


interface OperationI<B : OperationI<B>> : LogicUnitI<B> {
    fun generics(vararg value: GenericI<*>): B

    fun open(value: Boolean): B
    fun open(): B = open(true)
    fun notOpen(): B = open(false)

    fun returns(vararg value: AttributeI<*>): B
    fun generics(): ListMultiHolder<GenericI<*>>
    fun G(value: GenericI<*>): GenericI<*>
    fun G(value: GenericI<*>.() -> Unit = {}): GenericI<*>

    fun isOpen(): Boolean

    fun returns(): ListMultiHolder<AttributeI<*>>
    fun ret(value: AttributeI<*>): AttributeI<*>
    fun ret(value: AttributeI<*>.() -> Unit = {}): AttributeI<*>
}


interface OrPredicateI<B : OrPredicateI<B>> : LeftRightPredicatesPredicateI<B> {
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
    fun ifc(): B = ifc(true)
    fun notIfc(): B = ifc(false)

    fun multi(value: Boolean): B
    fun multi(): B = multi(true)
    fun notMulti(): B = multi(false)

    fun open(value: Boolean): B
    fun open(): B = open(true)
    fun notOpen(): B = open(false)

    fun operations(vararg value: OperationI<*>): B

    fun props(vararg value: AttributeI<*>): B

    fun superUnitFor(vararg value: TypeI<*>): B

    fun superUnits(vararg value: TypeI<*>): B

    fun virtual(value: Boolean): B
    fun virtual(): B = virtual(true)
    fun notVirtual(): B = virtual(false)
    fun constructors(): ListMultiHolder<ConstructorI<*>>
    fun constr(value: ConstructorI<*>): ConstructorI<*>
    fun constr(value: ConstructorI<*>.() -> Unit = {}): ConstructorI<*>

    fun defaultValue(): Any?

    fun generics(): ListMultiHolder<GenericI<*>>
    fun G(value: GenericI<*>): GenericI<*>
    fun G(value: GenericI<*>.() -> Unit = {}): GenericI<*>

    fun isIfc(): Boolean

    fun isMulti(): Boolean

    fun isOpen(): Boolean

    fun operations(): ListMultiHolder<OperationI<*>>
    fun op(value: OperationI<*>): OperationI<*>
    fun op(value: OperationI<*>.() -> Unit = {}): OperationI<*>

    fun props(): ListMultiHolder<AttributeI<*>>
    fun prop(value: AttributeI<*>): AttributeI<*>
    fun prop(value: AttributeI<*>.() -> Unit = {}): AttributeI<*>

    fun superUnitFor(): ListMultiHolder<TypeI<*>>

    fun superUnits(): ListMultiHolder<TypeI<*>>

    fun isVirtual(): Boolean
}


interface ValuesI<B : ValuesI<B>> : DataTypeI<B> {
}


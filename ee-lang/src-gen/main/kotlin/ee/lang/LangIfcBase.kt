package ee.lang


interface AttributeIB<B : AttributeIB<B>> : LiteralIB<B> {
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

    fun type(value: TypeIB<*>): B

    fun unique(value: Boolean): B

    fun value(aValue: Any?): B
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

    fun type(): TypeIB<*>

    fun unique(): Boolean

    fun value(): Any?
}



interface CompilationUnitIB<B : CompilationUnitIB<B>> : TypeIB<B> {
    fun base(value: Boolean): B
    fun base(): Boolean
}



interface ConstructorIB<B : ConstructorIB<B>> : LogicUnitIB<B> {
    fun primary(value: Boolean): B
    fun primary(): Boolean
}



interface DataTypeIB<B : DataTypeIB<B>> : CompilationUnitIB<B> {
}



interface DataTypeOperationIB<B : DataTypeOperationIB<B>> : OperationIB<B> {
}



interface EnumTypeIB<B : EnumTypeIB<B>> : DataTypeIB<B> {
    fun literals(vararg value: LiteralIB<*>): B
    fun literals(): ListMultiHolder<LiteralIB<*>>
    fun lit(value: LiteralIB<*>): LiteralIB<*>
    fun lit(value: LiteralIB<*>.() -> Unit = {}): LiteralIB<*>
}



interface ExpressionIB<B : ExpressionIB<B>> : MacroCompositeIB<B> {
}



interface ExternalTypeIB<B : ExternalTypeIB<B>> : TypeIB<B> {
}



interface GenericIB<B : GenericIB<B>> : TypeIB<B> {
    fun type(value: TypeIB<*>): B
    fun type(): TypeIB<*>
}



interface LambdaIB<B : LambdaIB<B>> : TypeIB<B> {
    fun operation(value: OperationIB<*>): B
    fun operation(): OperationIB<*>
}



interface LiteralIB<B : LiteralIB<B>> : LogicUnitIB<B> {
}



interface LogicUnitIB<B : LogicUnitIB<B>> : ExpressionIB<B> {
    fun params(vararg value: AttributeIB<*>): B

    fun superUnit(value: LogicUnitIB<*>): B

    fun virtual(value: Boolean): B

    fun visible(value: Boolean): B
    fun params(): ListMultiHolder<AttributeIB<*>>

    fun superUnit(): LogicUnitIB<*>

    fun virtual(): Boolean

    fun visible(): Boolean
}



interface MacroCompositeIB<B : MacroCompositeIB<B>> : CompositeIB<B> {
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



interface NativeTypeIB<B : NativeTypeIB<B>> : TypeIB<B> {
}



interface OperationIB<B : OperationIB<B>> : LogicUnitIB<B> {
    fun generics(vararg value: GenericIB<*>): B

    fun open(value: Boolean): B

    fun returns(vararg value: AttributeIB<*>): B
    fun generics(): ListMultiHolder<GenericIB<*>>
    fun G(value: GenericIB<*>): GenericIB<*>
    fun G(value: GenericIB<*>.() -> Unit = {}): GenericIB<*>

    fun open(): Boolean

    fun returns(): ListMultiHolder<AttributeIB<*>>
    fun ret(value: AttributeIB<*>): AttributeIB<*>
    fun ret(value: AttributeIB<*>.() -> Unit = {}): AttributeIB<*>
}



interface StructureUnitIB<B : StructureUnitIB<B>> : MacroCompositeIB<B> {
    fun artifact(value: String): B

    fun fullName(value: String): B

    fun key(value: String): B
    fun artifact(): String

    fun fullName(): String

    fun key(): String
}



interface TypeIB<B : TypeIB<B>> : MacroCompositeIB<B> {
    fun constructors(vararg value: ConstructorIB<*>): B

    fun defaultValue(value: Any?): B

    fun generics(vararg value: GenericIB<*>): B

    fun ifc(value: Boolean): B

    fun multi(value: Boolean): B

    fun open(value: Boolean): B

    fun operations(vararg value: OperationIB<*>): B

    fun props(vararg value: AttributeIB<*>): B

    fun superUnit(value: CompilationUnitIB<*>): B

    fun superUnitFor(vararg value: CompilationUnitIB<*>): B

    fun virtual(value: Boolean): B
    fun constructors(): ListMultiHolder<ConstructorIB<*>>
    fun constr(value: ConstructorIB<*>): ConstructorIB<*>
    fun constr(value: ConstructorIB<*>.() -> Unit = {}): ConstructorIB<*>

    fun defaultValue(): Any?

    fun generics(): ListMultiHolder<GenericIB<*>>
    fun G(value: GenericIB<*>): GenericIB<*>
    fun G(value: GenericIB<*>.() -> Unit = {}): GenericIB<*>

    fun ifc(): Boolean

    fun multi(): Boolean

    fun open(): Boolean

    fun operations(): ListMultiHolder<OperationIB<*>>
    fun op(value: OperationIB<*>): OperationIB<*>
    fun op(value: OperationIB<*>.() -> Unit = {}): OperationIB<*>

    fun props(): ListMultiHolder<AttributeIB<*>>
    fun prop(value: AttributeIB<*>): AttributeIB<*>
    fun prop(value: AttributeIB<*>.() -> Unit = {}): AttributeIB<*>

    fun superUnit(): CompilationUnitIB<*>

    fun superUnitFor(): ListMultiHolder<CompilationUnitIB<*>>

    fun virtual(): Boolean
}



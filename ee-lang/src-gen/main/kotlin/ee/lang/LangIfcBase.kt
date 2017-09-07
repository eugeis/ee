package ee.lang


interface AttributeI : MacroCompositeI {
    fun accessible(): Boolean?
    fun accessible(value: Boolean?): AttributeI

    fun anonymous(): Boolean
    fun anonymous(value: Boolean): AttributeI

    fun default(): Boolean
    fun default(value: Boolean): AttributeI

    fun hidden(): Boolean
    fun hidden(value: Boolean): AttributeI

    fun inherited(): Boolean
    fun inherited(value: Boolean): AttributeI

    fun initByDefaultTypeValue(): Boolean
    fun initByDefaultTypeValue(value: Boolean): AttributeI

    fun key(): Boolean
    fun key(value: Boolean): AttributeI

    fun length(): Int?
    fun length(value: Int?): AttributeI

    fun meta(): Boolean
    fun meta(value: Boolean): AttributeI

    fun multi(): Boolean
    fun multi(value: Boolean): AttributeI

    fun mutable(): Boolean?
    fun mutable(value: Boolean?): AttributeI

    fun nonFluent(): String
    fun nonFluent(value: String): AttributeI

    fun nullable(): Boolean
    fun nullable(value: Boolean): AttributeI

    fun open(): Boolean
    fun open(value: Boolean): AttributeI

    fun replaceable(): Boolean?
    fun replaceable(value: Boolean?): AttributeI

    fun type(): TypeI
    fun type(value: TypeI): AttributeI

    fun unique(): Boolean
    fun unique(value: Boolean): AttributeI

    fun value(): Any?
    fun value(aValue: Any?): AttributeI
}


interface CompilationUnitI : TypeI {
    fun base(): Boolean
    fun base(value: Boolean): CompilationUnitI
}


interface ConstructorI : LogicUnitI {
    fun primary(): Boolean
    fun primary(value: Boolean): ConstructorI
}


interface DataTypeI : CompilationUnitI {
}


interface DataTypeOperationI : OperationI {
}


interface EnumTypeI : DataTypeI {
    fun literals(): ListMultiHolder<LiteralI>
    fun literals(vararg value: LiteralI): EnumTypeI
    fun lit(value: LiteralI): LiteralI
    fun lit(value: LiteralI.() -> Unit = {}): LiteralI
}


interface ExternalTypeI : TypeI {
}


interface GenericI : TypeI {
    fun type(): TypeI
    fun type(value: TypeI): GenericI
}


interface LambdaI : TypeI {
    fun operation(): OperationI
    fun operation(value: OperationI): LambdaI
}


interface LiteralI : LogicUnitI {
}


interface LogicUnitI : MacroCompositeI {
    fun params(): ListMultiHolder<AttributeI>
    fun params(vararg value: AttributeI): LogicUnitI

    fun superUnit(): LogicUnitI
    fun superUnit(value: LogicUnitI): LogicUnitI

    fun virtual(): Boolean
    fun virtual(value: Boolean): LogicUnitI

    fun visible(): Boolean
    fun visible(value: Boolean): LogicUnitI
}


interface MacroCompositeI : CompositeI {
    fun macrosAfter(): ListMultiHolder<String>
    fun macrosAfter(vararg value: String): MacroCompositeI

    fun macrosAfterBody(): ListMultiHolder<String>
    fun macrosAfterBody(vararg value: String): MacroCompositeI

    fun macrosBefore(): ListMultiHolder<String>
    fun macrosBefore(vararg value: String): MacroCompositeI

    fun macrosBeforeBody(): ListMultiHolder<String>
    fun macrosBeforeBody(vararg value: String): MacroCompositeI

    fun macrosBody(): ListMultiHolder<String>
    fun macrosBody(vararg value: String): MacroCompositeI

    fun tags(): ListMultiHolder<String>
    fun tags(vararg value: String): MacroCompositeI
}


interface NativeTypeI : TypeI {
}


interface OperationI : LogicUnitI {
    fun generics(): ListMultiHolder<GenericI>
    fun generics(vararg value: GenericI): OperationI
    fun G(value: GenericI): GenericI
    fun G(value: GenericI.() -> Unit = {}): GenericI

    fun open(): Boolean
    fun open(value: Boolean): OperationI

    fun returns(): ListMultiHolder<AttributeI>
    fun returns(vararg value: AttributeI): OperationI
    fun ret(value: AttributeI): AttributeI
    fun ret(value: AttributeI.() -> Unit = {}): AttributeI
}


interface StructureUnitI : MacroCompositeI {
    fun artifact(): String
    fun artifact(value: String): StructureUnitI

    fun fullName(): String
    fun fullName(value: String): StructureUnitI

    fun key(): String
    fun key(value: String): StructureUnitI
}


interface TypeI : MacroCompositeI {
    fun constructors(): ListMultiHolder<ConstructorI>
    fun constructors(vararg value: ConstructorI): TypeI
    fun constr(value: ConstructorI): ConstructorI
    fun constr(value: ConstructorI.() -> Unit = {}): ConstructorI

    fun defaultValue(): Any?
    fun defaultValue(value: Any?): TypeI

    fun generics(): ListMultiHolder<GenericI>
    fun generics(vararg value: GenericI): TypeI
    fun G(value: GenericI): GenericI
    fun G(value: GenericI.() -> Unit = {}): GenericI

    fun ifc(): Boolean
    fun ifc(value: Boolean): TypeI

    fun multi(): Boolean
    fun multi(value: Boolean): TypeI

    fun open(): Boolean
    fun open(value: Boolean): TypeI

    fun operations(): ListMultiHolder<OperationI>
    fun operations(vararg value: OperationI): TypeI
    fun op(value: OperationI): OperationI
    fun op(value: OperationI.() -> Unit = {}): OperationI

    fun props(): ListMultiHolder<AttributeI>
    fun props(vararg value: AttributeI): TypeI
    fun prop(value: AttributeI): AttributeI
    fun prop(value: AttributeI.() -> Unit = {}): AttributeI

    fun superUnit(): CompilationUnitI
    fun superUnit(value: CompilationUnitI): TypeI

    fun superUnitFor(): ListMultiHolder<CompilationUnitI>
    fun superUnitFor(vararg value: CompilationUnitI): TypeI

    fun virtual(): Boolean
    fun virtual(value: Boolean): TypeI
}


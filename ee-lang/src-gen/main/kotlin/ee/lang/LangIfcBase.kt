package ee.lang


interface AttributeI : CompositeI {
    fun accessible(): Boolean
    fun accessible(value: Boolean): AttributeI

    fun anonymous(): Boolean
    fun anonymous(value: Boolean): AttributeI

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

    fun mutable(): Boolean
    fun mutable(value: Boolean): AttributeI

    fun nonFluent(): String
    fun nonFluent(value: String): AttributeI

    fun nullable(): Boolean
    fun nullable(value: Boolean): AttributeI

    fun open(): Boolean
    fun open(value: Boolean): AttributeI

    fun replaceable(): Boolean
    fun replaceable(value: Boolean): AttributeI

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

    fun constructors(): ListMultiHolder<ConstructorI>
    fun constructors(vararg value: ConstructorI): CompilationUnitI
    fun constr(value: ConstructorI): ConstructorI
    fun constr(value: ConstructorI.() -> Unit = {}): ConstructorI

    fun open(): Boolean
    fun open(value: Boolean): CompilationUnitI

    fun operations(): ListMultiHolder<OperationI>
    fun operations(vararg value: OperationI): CompilationUnitI
    fun op(value: OperationI): OperationI
    fun op(value: OperationI.() -> Unit = {}): OperationI

    fun props(): ListMultiHolder<AttributeI>
    fun props(vararg value: AttributeI): CompilationUnitI
    fun prop(value: AttributeI): AttributeI
    fun prop(value: AttributeI.() -> Unit = {}): AttributeI

    fun superUnit(): CompilationUnitI
    fun superUnit(value: CompilationUnitI): CompilationUnitI

    fun superUnitFor(): ListMultiHolder<CompilationUnitI>
    fun superUnitFor(vararg value: CompilationUnitI): CompilationUnitI

    fun virtual(): Boolean
    fun virtual(value: Boolean): CompilationUnitI
}


interface ConstructorI : LogicUnitI {
    fun primary(): Boolean
    fun primary(value: Boolean): ConstructorI
}


interface DataTypeI : CompilationUnitI {
}


interface DataTypeOperationI : OperationI {
}


interface EnumTypeI : CompilationUnitI {
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


interface LogicUnitI : TextCompositeI {
    fun params(): ListMultiHolder<AttributeI>
    fun params(vararg value: AttributeI): LogicUnitI

    fun superUnit(): LogicUnitI
    fun superUnit(value: LogicUnitI): LogicUnitI

    fun virtual(): Boolean
    fun virtual(value: Boolean): LogicUnitI

    fun visible(): Boolean
    fun visible(value: Boolean): LogicUnitI
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

    fun ret(): AttributeI
    fun ret(value: AttributeI): OperationI
    fun r(value: AttributeI): AttributeI
    fun r(value: AttributeI.() -> Unit = {}): AttributeI
}


interface StructureUnitI : CompositeI {
    fun artifact(): String
    fun artifact(value: String): StructureUnitI

    fun fullName(): String
    fun fullName(value: String): StructureUnitI

    fun key(): String
    fun key(value: String): StructureUnitI
}


interface TextCompositeI : CompositeI {
    fun macro(): String
    fun macro(value: String): TextCompositeI
}


interface TypeI : CompositeI {
    fun defaultValue(): Any?
    fun defaultValue(value: Any?): TypeI

    fun generics(): ListMultiHolder<GenericI>
    fun generics(vararg value: GenericI): TypeI
    fun G(value: GenericI): GenericI
    fun G(value: GenericI.() -> Unit = {}): GenericI

    fun multi(): Boolean
    fun multi(value: Boolean): TypeI
}


package ee.design


interface AttributeI : CompositeI {
    fun type(): TypeI
    fun type(value: TypeI): AttributeI

    fun key(): Boolean
    fun key(value: Boolean): AttributeI

    fun unique(): Boolean
    fun unique(value: Boolean): AttributeI

    fun value(): Any?
    fun value(aValue: Any?): AttributeI

    fun initByDefaultTypeValue(): Boolean
    fun initByDefaultTypeValue(value: Boolean): AttributeI

    fun nullable(): Boolean
    fun nullable(value: Boolean): AttributeI

    fun accessible(): Boolean
    fun accessible(value: Boolean): AttributeI

    fun replaceable(): Boolean
    fun replaceable(value: Boolean): AttributeI

    fun meta(): Boolean
    fun meta(value: Boolean): AttributeI

    fun multi(): Boolean
    fun multi(value: Boolean): AttributeI

    fun hidden(): Boolean
    fun hidden(value: Boolean): AttributeI

    fun mutable(): Boolean
    fun mutable(value: Boolean): AttributeI

    fun length(): Int?
    fun length(value: Int?): AttributeI

    fun inherited(): Boolean
    fun inherited(value: Boolean): AttributeI

    fun open(): Boolean
    fun open(value: Boolean): AttributeI

    fun nonFluent(): String
    fun nonFluent(value: String): AttributeI
}


interface BundleI : StructureUnitI {
    fun units(): List<StructureUnitI>
    fun units(vararg value: StructureUnitI): BundleI
}


interface CompI : ModuleGroupI {
    fun moduleGroups(): List<ModuleGroupI>
    fun moduleGroups(vararg value: ModuleGroupI): CompI
}


interface CompilationUnitI : TypeI {
    fun base(): Boolean
    fun base(value: Boolean): CompilationUnitI

    fun open(): Boolean
    fun open(value: Boolean): CompilationUnitI

    fun virtual(): Boolean
    fun virtual(value: Boolean): CompilationUnitI

    fun superUnitFor(): List<CompilationUnitI>
    fun superUnitFor(vararg value: CompilationUnitI): CompilationUnitI

    fun superUnit(): CompilationUnitI
    fun superUnit(value: CompilationUnitI): CompilationUnitI

    fun props(): List<AttributeI>
    fun props(vararg value: AttributeI): CompilationUnitI
    fun prop(value: AttributeI): AttributeI
    fun prop(value: AttributeI.() -> Unit = {}) : AttributeI

    fun operations(): List<OperationI>
    fun operations(vararg value: OperationI): CompilationUnitI
    fun op(value: OperationI): OperationI
    fun op(value: OperationI.() -> Unit = {}) : OperationI

    fun constructors(): List<ConstructorI>
    fun constructors(vararg value: ConstructorI): CompilationUnitI
    fun constr(value: ConstructorI): ConstructorI
    fun constr(value: ConstructorI.() -> Unit = {}) : ConstructorI
}


interface ConstructorI : LogicUnitI {
    fun primary(): Boolean
    fun primary(value: Boolean): ConstructorI
}


interface EnumTypeI : CompilationUnitI {
    fun literals(): List<LiteralI>
    fun literals(vararg value: LiteralI): EnumTypeI
    fun lit(value: LiteralI): LiteralI
    fun lit(value: LiteralI.() -> Unit = {}) : LiteralI
}


interface ExternalModuleI : ModuleI {
    fun externalTypes(): List<ExternalTypeI>
    fun externalTypes(vararg value: ExternalTypeI): ExternalModuleI
    fun type(value: ExternalTypeI): ExternalTypeI
    fun type(value: ExternalTypeI.() -> Unit = {}) : ExternalTypeI
}


interface ExternalTypeI : TypeI {
}


interface FacetI : ModuleGroupI {
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
    fun virtual(): Boolean
    fun virtual(value: Boolean): LogicUnitI

    fun superUnit(): LogicUnitI
    fun superUnit(value: LogicUnitI): LogicUnitI

    fun params(): List<AttributeI>
    fun params(vararg value: AttributeI): LogicUnitI
}


interface ModelI : StructureUnitI {
    fun models(): List<ModelI>
    fun models(vararg value: ModelI): ModelI

    fun comps(): List<CompI>
    fun comps(vararg value: CompI): ModelI
}


interface ModuleI : StructureUnitI {
    fun parentNamespace(): Boolean
    fun parentNamespace(value: Boolean): ModuleI

    fun dependencies(): List<ModuleI>
    fun dependencies(vararg value: ModuleI): ModuleI
}


interface ModuleGroupI : StructureUnitI {
    fun modules(): List<ModuleI>
    fun modules(vararg value: ModuleI): ModuleGroupI
}


interface NativeTypeI : TypeI {
}


interface OperationI : LogicUnitI {
    fun generics(): List<GenericI>
    fun generics(vararg value: GenericI): OperationI
    fun G(value: GenericI): GenericI
    fun G(value: GenericI.() -> Unit = {}) : GenericI

    fun ret(): AttributeI
    fun ret(value: AttributeI): OperationI
    fun r(value: AttributeI): AttributeI
    fun r(value: AttributeI.() -> Unit = {}) : AttributeI
}


interface StructureUnitI : CompositeI {
    fun key(): String
    fun key(value: String): StructureUnitI

    fun fullName(): String
    fun fullName(value: String): StructureUnitI

    fun artifact(): String
    fun artifact(value: String): StructureUnitI
}


interface TextCompositeI : CompositeI {
}


interface TypeI : CompositeI {
    fun generics(): List<GenericI>
    fun generics(vararg value: GenericI): TypeI
    fun G(value: GenericI): GenericI
    fun G(value: GenericI.() -> Unit = {}) : GenericI

    fun multi(): Boolean
    fun multi(value: Boolean): TypeI

    fun defaultValue(): Any?
    fun defaultValue(value: Any?): TypeI
}


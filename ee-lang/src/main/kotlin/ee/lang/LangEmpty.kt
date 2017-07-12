package ee.lang


object CompilationUnitEmpty : Type(), CompilationUnitI {
    override fun base(): Boolean = false
    override fun base(value: Boolean): CompilationUnitI = this
    override fun open(): Boolean = false
    override fun open(value: Boolean): CompilationUnitI = this
    override fun virtual(): Boolean = false
    override fun virtual(value: Boolean): CompilationUnitI = this
    override fun superUnitFor(vararg value: CompilationUnitI): CompilationUnitI = this
    override fun superUnit(): CompilationUnitI = this
    override fun superUnit(value: CompilationUnitI): CompilationUnitI = this
    override fun props(vararg value: AttributeI): CompilationUnitI = this
    override fun prop(value: AttributeI): AttributeI = Attribute.EMPTY
    override fun prop(value: AttributeI.() -> Unit): AttributeI = Attribute.EMPTY
    override fun operations(vararg value: OperationI): CompilationUnitI = this
    override fun op(value: OperationI): OperationI = Operation.EMPTY
    override fun op(value: OperationI.() -> Unit): OperationI = Operation.EMPTY
    override fun constructors(vararg value: ConstructorI): CompilationUnitI = this
    override fun constr(value: ConstructorI): ConstructorI = Constructor.EMPTY
    override fun constr(value: ConstructorI.() -> Unit): ConstructorI = Constructor.EMPTY
    override fun constructors(): ListMultiHolder<ConstructorI> = ListMultiHolder.empty()
    override fun operations(): ListMultiHolder<OperationI> = ListMultiHolder.empty()
    override fun props(): ListMultiHolder<AttributeI> = ListMultiHolder.empty()
    override fun superUnitFor(): ListMultiHolder<CompilationUnitI> = ListMultiHolder.empty()
}

object LogicUnitEmpty : TextComposite(), LogicUnitI {
    override fun virtual(): Boolean = false
    override fun virtual(value: Boolean): LogicUnitI = this
    override fun superUnit(): LogicUnitI = this
    override fun superUnit(value: LogicUnitI): LogicUnitI = this
    override fun params(vararg value: AttributeI): LogicUnitI = this
    override fun params(): ListMultiHolder<AttributeI> = ListMultiHolder.empty()
    override fun p(value: AttributeI): AttributeI = Attribute.EMPTY
    override fun p(value: AttributeI.() -> Unit): AttributeI = Attribute.EMPTY
    override fun visible(): Boolean = false
    override fun visible(value: Boolean): LogicUnitI = this
}
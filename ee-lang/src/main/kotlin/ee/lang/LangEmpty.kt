package ee.lang


object CompilationUnitEmpty : TypeB<CompilationUnitEmpty>(), CompilationUnitIB<CompilationUnitEmpty> {
    override fun base(): Boolean = false
    override fun base(value: Boolean): CompilationUnitEmpty = this
    override fun open(): Boolean = false
    override fun open(value: Boolean): CompilationUnitEmpty = this
    override fun virtual(): Boolean = false
    override fun virtual(value: Boolean): CompilationUnitEmpty = this
    override fun superUnitFor(vararg value: CompilationUnitIB<*>): CompilationUnitEmpty = this
    override fun operations(vararg value: OperationIB<*>): CompilationUnitEmpty = this
    override fun op(value: OperationIB<*>): OperationIB<*> = Operation.EMPTY
    override fun op(value: OperationIB<*>.() -> Unit): OperationIB<*> = Operation.EMPTY
    override fun constructors(vararg value: ConstructorIB<*>): CompilationUnitEmpty = this
    override fun constr(value: ConstructorIB<*>): ConstructorIB<*> = Constructor.EMPTY
    override fun constr(value: ConstructorIB<*>.() -> Unit): ConstructorIB<*> = Constructor.EMPTY
    override fun constructors(): ListMultiHolder<ConstructorIB<*>> = ListMultiHolder.empty()
    override fun operations(): ListMultiHolder<OperationIB<*>> = ListMultiHolder.empty()
    override fun superUnitFor(): ListMultiHolder<CompilationUnitIB<*>> = ListMultiHolder.empty()
}

object LogicUnitEmpty : MacroCompositeB<LogicUnitEmpty>(), LogicUnitIB<LogicUnitEmpty> {
    override fun virtual(): Boolean = false
    override fun virtual(value: Boolean): LogicUnitEmpty = this
    override fun superUnit(): LogicUnitEmpty = this
    override fun superUnit(value: LogicUnitIB<*>): LogicUnitEmpty = this
    override fun params(vararg value: AttributeIB<*>): LogicUnitEmpty = this
    override fun params(): ListMultiHolder<AttributeIB<*>> = ListMultiHolder.empty()
    override fun visible(): Boolean = false
    override fun visible(value: Boolean): LogicUnitEmpty = this
}
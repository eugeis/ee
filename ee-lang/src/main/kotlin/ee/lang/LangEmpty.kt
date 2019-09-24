package ee.lang


object CompilationUnitEmpty : TypeB<CompilationUnitEmpty>(), CompilationUnitI<CompilationUnitEmpty> {
    override fun isBase(): Boolean = false
    override fun base(value: Boolean): CompilationUnitEmpty = this
    override fun isNonBlock(): Boolean = false
    override fun nonBlock(value: Boolean): CompilationUnitEmpty = this
    override fun isOpen(): Boolean = false
    override fun open(value: Boolean): CompilationUnitEmpty = this
    override fun isVirtual(): Boolean = false
    override fun virtual(value: Boolean): CompilationUnitEmpty = this
    override fun superUnitFor(vararg value: TypeI<*>): CompilationUnitEmpty = this
    override fun operations(vararg value: OperationI<*>): CompilationUnitEmpty = this
    override fun op(value: OperationI<*>): OperationI<*> = Operation.EMPTY
    override fun op(value: OperationI<*>.() -> Unit): OperationI<*> = Operation.EMPTY
    override fun constructors(vararg value: ConstructorI<*>): CompilationUnitEmpty = this
    override fun constr(value: ConstructorI<*>): ConstructorI<*> = Constructor.EMPTY
    override fun constr(value: ConstructorI<*>.() -> Unit): ConstructorI<*> = Constructor.EMPTY
    override fun constructors(): ListMultiHolder<ConstructorI<*>> = ListMultiHolder.empty()
    override fun operations(): ListMultiHolder<OperationI<*>> = ListMultiHolder.empty()
    override fun superUnitFor(): ListMultiHolder<TypeI<*>> = ListMultiHolder.empty()
}

object LogicUnitEmpty : MacroCompositeB<LogicUnitEmpty>(), LogicUnitI<LogicUnitEmpty> {
    override fun isVirtual(): Boolean = false
    override fun virtual(value: Boolean): LogicUnitEmpty = this
    override fun superUnit(): LogicUnitEmpty = this
    override fun superUnit(value: LogicUnitI<*>): LogicUnitEmpty = this
    override fun params(vararg value: AttributeI<*>): LogicUnitEmpty = this
    override fun params(): ListMultiHolder<AttributeI<*>> = ListMultiHolder.empty()
    override fun isVisible(): Boolean = false
    override fun visible(value: Boolean): LogicUnitEmpty = this
    override fun errorHandling(value: Boolean): LogicUnitEmpty = this
    override fun isErrorHandling(): Boolean = false
}
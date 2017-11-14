package ee.design

import ee.lang.*


interface ActionIB<B : ActionIB<B>> : LogicUnitIB<B> {
}


interface AggregateHandlerIB<B : AggregateHandlerIB<B>> : StateMachineIB<B> {
}


interface ApplyActionIB<B : ApplyActionIB<B>> : ActionIB<B> {
    fun target(): AttributeIB<*>
    fun target(value: AttributeIB<*>): B

    fun operator(): AttributeIB<*>
    fun operator(value: AttributeIB<*>): B

    fun value(): Any
    fun value(aValue: Any): B
}


interface BasicIB<B : BasicIB<B>> : DataTypeIB<B> {
}


interface BundleIB<B : BundleIB<B>> : StructureUnitIB<B> {
    fun units(): ListMultiHolder<StructureUnitIB<*>>
    fun units(vararg value: StructureUnitIB<*>): B
}


interface BusinessCommandIB<B : BusinessCommandIB<B>> : CommandIB<B> {
}


interface BussinesEventIB<B : BussinesEventIB<B>> : EventIB<B> {
}


interface CheckIB<B : CheckIB<B>> : LogicUnitIB<B> {
    fun cachedInContext(): Boolean
    fun cachedInContext(value: Boolean): B
}


interface CommandIB<B : CommandIB<B>> : EventIB<B> {
    fun affectMulti(): Boolean
    fun affectMulti(value: Boolean): B

    fun event(): EventIB<*>
    fun event(value: EventIB<*>): B
}


interface CompIB<B : CompIB<B>> : ModuleGroupIB<B> {
    fun moduleGroups(): ListMultiHolder<ModuleGroupIB<*>>
    fun moduleGroups(vararg value: ModuleGroupIB<*>): B
}


interface CompositeCommandIB<B : CompositeCommandIB<B>> : CompilationUnitIB<B> {
    fun commands(): ListMultiHolder<CommandIB<*>>
    fun commands(vararg value: CommandIB<*>): B
}


interface ControllerIB<B : ControllerIB<B>> : CompilationUnitIB<B> {
    fun enums(): ListMultiHolder<EnumTypeIB<*>>
    fun enums(vararg value: EnumTypeIB<*>): B
    fun enumType(value: EnumTypeIB<*>): EnumTypeIB<*>
    fun enumType(value: EnumTypeIB<*>.() -> Unit = {}): EnumTypeIB<*>

    fun values(): ListMultiHolder<ValuesIB<*>>
    fun values(vararg value: ValuesIB<*>): B
    fun valueType(value: ValuesIB<*>): ValuesIB<*>
    fun valueType(value: ValuesIB<*>.() -> Unit = {}): ValuesIB<*>

    fun basics(): ListMultiHolder<BasicIB<*>>
    fun basics(vararg value: BasicIB<*>): B
    fun basic(value: BasicIB<*>): BasicIB<*>
    fun basic(value: BasicIB<*>.() -> Unit = {}): BasicIB<*>
}


interface CountByIB<B : CountByIB<B>> : DataTypeOperationIB<B> {
}


interface CreateByIB<B : CreateByIB<B>> : CommandIB<B> {
}


interface CreatedIB<B : CreatedIB<B>> : EventIB<B> {
}


interface DeleteByIB<B : DeleteByIB<B>> : CommandIB<B> {
}


interface DeletedIB<B : DeletedIB<B>> : EventIB<B> {
}


interface DynamicStateIB<B : DynamicStateIB<B>> : StateIB<B> {
    fun checks(): ListMultiHolder<CheckIB<*>>
    fun checks(vararg value: CheckIB<*>): B
    fun yes(value: CheckIB<*>): CheckIB<*>
    fun yes(value: CheckIB<*>.() -> Unit = {}): CheckIB<*>

    fun notChecks(): ListMultiHolder<CheckIB<*>>
    fun notChecks(vararg value: CheckIB<*>): B
    fun no(value: CheckIB<*>): CheckIB<*>
    fun no(value: CheckIB<*>.() -> Unit = {}): CheckIB<*>
}


interface EntityIB<B : EntityIB<B>> : DataTypeIB<B> {
    fun defaultEvents(): Boolean
    fun defaultEvents(value: Boolean): B

    fun defaultQueries(): Boolean
    fun defaultQueries(value: Boolean): B

    fun defaultCommands(): Boolean
    fun defaultCommands(value: Boolean): B

    fun belongsToAggregate(): EntityIB<*>
    fun belongsToAggregate(value: EntityIB<*>): B

    fun aggregateFor(): ListMultiHolder<EntityIB<*>>
    fun aggregateFor(vararg value: EntityIB<*>): B

    fun controllers(): ListMultiHolder<ControllerIB<*>>
    fun controllers(vararg value: ControllerIB<*>): B
    fun controller(value: ControllerIB<*>): ControllerIB<*>
    fun controller(value: ControllerIB<*>.() -> Unit = {}): ControllerIB<*>

    fun findBys(): ListMultiHolder<FindByIB<*>>
    fun findBys(vararg value: FindByIB<*>): B
    fun findBy(value: FindByIB<*>): FindByIB<*>
    fun findBy(value: FindByIB<*>.() -> Unit = {}): FindByIB<*>

    fun countBys(): ListMultiHolder<CountByIB<*>>
    fun countBys(vararg value: CountByIB<*>): B
    fun countBy(value: CountByIB<*>): CountByIB<*>
    fun countBy(value: CountByIB<*>.() -> Unit = {}): CountByIB<*>

    fun existBys(): ListMultiHolder<ExistByIB<*>>
    fun existBys(vararg value: ExistByIB<*>): B
    fun existBy(value: ExistByIB<*>): ExistByIB<*>
    fun existBy(value: ExistByIB<*>.() -> Unit = {}): ExistByIB<*>

    fun commands(): ListMultiHolder<BusinessCommandIB<*>>
    fun commands(vararg value: BusinessCommandIB<*>): B
    fun command(value: BusinessCommandIB<*>): BusinessCommandIB<*>
    fun command(value: BusinessCommandIB<*>.() -> Unit = {}): BusinessCommandIB<*>

    fun composites(): ListMultiHolder<CompositeCommandIB<*>>
    fun composites(vararg value: CompositeCommandIB<*>): B
    fun composite(value: CompositeCommandIB<*>): CompositeCommandIB<*>
    fun composite(value: CompositeCommandIB<*>.() -> Unit = {}): CompositeCommandIB<*>

    fun createBys(): ListMultiHolder<CreateByIB<*>>
    fun createBys(vararg value: CreateByIB<*>): B
    fun createBy(value: CreateByIB<*>): CreateByIB<*>
    fun createBy(value: CreateByIB<*>.() -> Unit = {}): CreateByIB<*>

    fun updateBys(): ListMultiHolder<UpdateByIB<*>>
    fun updateBys(vararg value: UpdateByIB<*>): B
    fun updateBy(value: UpdateByIB<*>): UpdateByIB<*>
    fun updateBy(value: UpdateByIB<*>.() -> Unit = {}): UpdateByIB<*>

    fun deleteBys(): ListMultiHolder<DeleteByIB<*>>
    fun deleteBys(vararg value: DeleteByIB<*>): B
    fun deleteBy(value: DeleteByIB<*>): DeleteByIB<*>
    fun deleteBy(value: DeleteByIB<*>.() -> Unit = {}): DeleteByIB<*>

    fun events(): ListMultiHolder<BussinesEventIB<*>>
    fun events(vararg value: BussinesEventIB<*>): B
    fun event(value: BussinesEventIB<*>): BussinesEventIB<*>
    fun event(value: BussinesEventIB<*>.() -> Unit = {}): BussinesEventIB<*>

    fun created(): ListMultiHolder<CreatedIB<*>>
    fun created(vararg value: CreatedIB<*>): B
    fun created(value: CreatedIB<*>): CreatedIB<*>
    fun created(value: CreatedIB<*>.() -> Unit = {}): CreatedIB<*>

    fun updated(): ListMultiHolder<UpdatedIB<*>>
    fun updated(vararg value: UpdatedIB<*>): B
    fun updated(value: UpdatedIB<*>): UpdatedIB<*>
    fun updated(value: UpdatedIB<*>.() -> Unit = {}): UpdatedIB<*>

    fun deleted(): ListMultiHolder<DeletedIB<*>>
    fun deleted(vararg value: DeletedIB<*>): B
    fun deleted(value: DeletedIB<*>): DeletedIB<*>
    fun deleted(value: DeletedIB<*>.() -> Unit = {}): DeletedIB<*>

    fun handlers(): ListMultiHolder<AggregateHandlerIB<*>>
    fun handlers(vararg value: AggregateHandlerIB<*>): B
    fun handler(value: AggregateHandlerIB<*>): AggregateHandlerIB<*>
    fun handler(value: AggregateHandlerIB<*>.() -> Unit = {}): AggregateHandlerIB<*>

    fun projectors(): ListMultiHolder<ProjectorIB<*>>
    fun projectors(vararg value: ProjectorIB<*>): B
    fun projector(value: ProjectorIB<*>): ProjectorIB<*>
    fun projector(value: ProjectorIB<*>.() -> Unit = {}): ProjectorIB<*>

    fun processManager(): ListMultiHolder<ProcessManagerIB<*>>
    fun processManager(vararg value: ProcessManagerIB<*>): B
    fun processManager(value: ProcessManagerIB<*>): ProcessManagerIB<*>
    fun processManager(value: ProcessManagerIB<*>.() -> Unit = {}): ProcessManagerIB<*>
}


interface EventIB<B : EventIB<B>> : CompilationUnitIB<B> {
}


interface ExecutorIB<B : ExecutorIB<B>> : LogicUnitIB<B> {
    fun on(): CommandIB<*>
    fun on(value: CommandIB<*>): B

    fun checks(): ListMultiHolder<CheckIB<*>>
    fun checks(vararg value: CheckIB<*>): B
    fun yes(value: CheckIB<*>): CheckIB<*>
    fun yes(value: CheckIB<*>.() -> Unit = {}): CheckIB<*>

    fun notChecks(): ListMultiHolder<CheckIB<*>>
    fun notChecks(vararg value: CheckIB<*>): B
    fun no(value: CheckIB<*>): CheckIB<*>
    fun no(value: CheckIB<*>.() -> Unit = {}): CheckIB<*>

    fun actions(): ListMultiHolder<ActionIB<*>>
    fun actions(vararg value: ActionIB<*>): B
    fun action(value: ActionIB<*>): ActionIB<*>
    fun action(value: ActionIB<*>.() -> Unit = {}): ActionIB<*>

    fun output(): ListMultiHolder<EventIB<*>>
    fun output(vararg value: EventIB<*>): B
    fun produce(value: EventIB<*>): EventIB<*>
    fun produce(value: EventIB<*>.() -> Unit = {}): EventIB<*>
}


interface ExistByIB<B : ExistByIB<B>> : DataTypeOperationIB<B> {
}


interface ExternalModuleIB<B : ExternalModuleIB<B>> : ModuleIB<B> {
    fun externalTypes(): ListMultiHolder<ExternalTypeIB<*>>
    fun externalTypes(vararg value: ExternalTypeIB<*>): B
}


interface FacetIB<B : FacetIB<B>> : ModuleGroupIB<B> {
}


interface FindByIB<B : FindByIB<B>> : DataTypeOperationIB<B> {
    fun multiResult(): Boolean
    fun multiResult(value: Boolean): B
}


interface HandlerIB<B : HandlerIB<B>> : LogicUnitIB<B> {
    fun on(): EventIB<*>
    fun on(value: EventIB<*>): B

    fun checks(): ListMultiHolder<CheckIB<*>>
    fun checks(vararg value: CheckIB<*>): B
    fun yes(value: CheckIB<*>): CheckIB<*>
    fun yes(value: CheckIB<*>.() -> Unit = {}): CheckIB<*>

    fun notChecks(): ListMultiHolder<CheckIB<*>>
    fun notChecks(vararg value: CheckIB<*>): B
    fun no(value: CheckIB<*>): CheckIB<*>
    fun no(value: CheckIB<*>.() -> Unit = {}): CheckIB<*>

    fun to(): StateIB<*>
    fun to(value: StateIB<*>): B

    fun actions(): ListMultiHolder<ActionIB<*>>
    fun actions(vararg value: ActionIB<*>): B
    fun action(value: ActionIB<*>): ActionIB<*>
    fun action(value: ActionIB<*>.() -> Unit = {}): ActionIB<*>

    fun output(): ListMultiHolder<CommandIB<*>>
    fun output(vararg value: CommandIB<*>): B
    fun produce(value: CommandIB<*>): CommandIB<*>
    fun produce(value: CommandIB<*>.() -> Unit = {}): CommandIB<*>
}


interface ModelIB<B : ModelIB<B>> : StructureUnitIB<B> {
    fun models(): ListMultiHolder<ModelIB<*>>
    fun models(vararg value: ModelIB<*>): B

    fun comps(): ListMultiHolder<CompIB<*>>
    fun comps(vararg value: CompIB<*>): B
}


interface ModuleIB<B : ModuleIB<B>> : StructureUnitIB<B> {
    fun parentNamespace(): Boolean
    fun parentNamespace(value: Boolean): B

    fun dependencies(): ListMultiHolder<ModuleIB<*>>
    fun dependencies(vararg value: ModuleIB<*>): B

    fun entities(): ListMultiHolder<EntityIB<*>>
    fun entities(vararg value: EntityIB<*>): B
    fun entity(value: EntityIB<*>): EntityIB<*>
    fun entity(value: EntityIB<*>.() -> Unit = {}): EntityIB<*>

    fun enums(): ListMultiHolder<EnumTypeIB<*>>
    fun enums(vararg value: EnumTypeIB<*>): B
    fun enumType(value: EnumTypeIB<*>): EnumTypeIB<*>
    fun enumType(value: EnumTypeIB<*>.() -> Unit = {}): EnumTypeIB<*>

    fun values(): ListMultiHolder<ValuesIB<*>>
    fun values(vararg value: ValuesIB<*>): B
    fun valueType(value: ValuesIB<*>): ValuesIB<*>
    fun valueType(value: ValuesIB<*>.() -> Unit = {}): ValuesIB<*>

    fun basics(): ListMultiHolder<BasicIB<*>>
    fun basics(vararg value: BasicIB<*>): B
    fun basic(value: BasicIB<*>): BasicIB<*>
    fun basic(value: BasicIB<*>.() -> Unit = {}): BasicIB<*>

    fun controllers(): ListMultiHolder<ControllerIB<*>>
    fun controllers(vararg value: ControllerIB<*>): B
    fun controller(value: ControllerIB<*>): ControllerIB<*>
    fun controller(value: ControllerIB<*>.() -> Unit = {}): ControllerIB<*>

    fun processManagers(): ListMultiHolder<ProcessManagerIB<*>>
    fun processManagers(vararg value: ProcessManagerIB<*>): B
    fun processManager(value: ProcessManagerIB<*>): ProcessManagerIB<*>
    fun processManager(value: ProcessManagerIB<*>.() -> Unit = {}): ProcessManagerIB<*>

    fun projectors(): ListMultiHolder<ProjectorIB<*>>
    fun projectors(vararg value: ProjectorIB<*>): B
    fun projector(value: ProjectorIB<*>): ProjectorIB<*>
    fun projector(value: ProjectorIB<*>.() -> Unit = {}): ProjectorIB<*>
}


interface ModuleGroupIB<B : ModuleGroupIB<B>> : StructureUnitIB<B> {
    fun modules(): ListMultiHolder<ModuleIB<*>>
    fun modules(vararg value: ModuleIB<*>): B
}


interface ProcessManagerIB<B : ProcessManagerIB<B>> : StateMachineIB<B> {
}


interface ProjectorIB<B : ProjectorIB<B>> : StateMachineIB<B> {
}


interface StateIB<B : StateIB<B>> : ControllerIB<B> {
    fun timeout(): Long
    fun timeout(value: Long): B

    fun entryActions(): ListMultiHolder<ActionIB<*>>
    fun entryActions(vararg value: ActionIB<*>): B
    fun entry(value: ActionIB<*>): ActionIB<*>
    fun entry(value: ActionIB<*>.() -> Unit = {}): ActionIB<*>

    fun exitActions(): ListMultiHolder<ActionIB<*>>
    fun exitActions(vararg value: ActionIB<*>): B
    fun exit(value: ActionIB<*>): ActionIB<*>
    fun exit(value: ActionIB<*>.() -> Unit = {}): ActionIB<*>

    fun executors(): ListMultiHolder<ExecutorIB<*>>
    fun executors(vararg value: ExecutorIB<*>): B
    fun execute(value: ExecutorIB<*>): ExecutorIB<*>
    fun execute(value: ExecutorIB<*>.() -> Unit = {}): ExecutorIB<*>

    fun handlers(): ListMultiHolder<HandlerIB<*>>
    fun handlers(vararg value: HandlerIB<*>): B
    fun handle(value: HandlerIB<*>): HandlerIB<*>
    fun handle(value: HandlerIB<*>.() -> Unit = {}): HandlerIB<*>
}


interface StateMachineIB<B : StateMachineIB<B>> : ControllerIB<B> {
    fun stateProp(): AttributeIB<*>
    fun stateProp(value: AttributeIB<*>): B

    fun timeoutProp(): AttributeIB<*>
    fun timeoutProp(value: AttributeIB<*>): B

    fun timeout(): Long
    fun timeout(value: Long): B

    fun states(): ListMultiHolder<StateIB<*>>
    fun states(vararg value: StateIB<*>): B
    fun state(value: StateIB<*>): StateIB<*>
    fun state(value: StateIB<*>.() -> Unit = {}): StateIB<*>

    fun checks(): ListMultiHolder<CheckIB<*>>
    fun checks(vararg value: CheckIB<*>): B
    fun check(value: CheckIB<*>): CheckIB<*>
    fun check(value: CheckIB<*>.() -> Unit = {}): CheckIB<*>
}


interface UpdateByIB<B : UpdateByIB<B>> : CommandIB<B> {
}


interface UpdatedIB<B : UpdatedIB<B>> : EventIB<B> {
}


interface ValuesIB<B : ValuesIB<B>> : DataTypeIB<B> {
}


interface WidgetIB<B : WidgetIB<B>> : CompilationUnitIB<B> {
}


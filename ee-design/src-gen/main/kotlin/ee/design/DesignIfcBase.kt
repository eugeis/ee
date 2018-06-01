package ee.design

import ee.lang.ActionI
import ee.lang.AttributeI
import ee.lang.BasicI
import ee.lang.CompilationUnitI
import ee.lang.DataTypeI
import ee.lang.DataTypeOperationI
import ee.lang.EnumTypeI
import ee.lang.ExternalTypeI
import ee.lang.ListMultiHolder
import ee.lang.LogicUnitI
import ee.lang.PredicateI
import ee.lang.StructureUnitI
import ee.lang.ValuesI


interface AggregateHandlerI<B : AggregateHandlerI<B>> : StateMachineI<B> {
}


interface BundleI<B : BundleI<B>> : StructureUnitI<B> {
    fun units(): ListMultiHolder<StructureUnitI<*>>
    fun units(vararg value: StructureUnitI<*>): B
}


interface BusinessCommandI<B : BusinessCommandI<B>> : CommandI<B> {
}


interface BusinessControllerI<B : BusinessControllerI<B>> : ControllerI<B> {
}


interface BusinessEventI<B : BusinessEventI<B>> : EventI<B> {
}


interface CommandI<B : CommandI<B>> : CompilationUnitI<B> {
    fun httpMethod(): String
    fun httpMethod(value: String): B

    fun isAffectMulti(): Boolean
    fun affectMulti(value: Boolean): B
    fun affectMulti(): B = affectMulti(true)
    fun notAffectMulti(): B = affectMulti(false)

    fun event(): EventI<*>
    fun event(value: EventI<*>): B
}


interface CompI<B : CompI<B>> : ModuleGroupI<B> {
    fun moduleGroups(): ListMultiHolder<ModuleGroupI<*>>
    fun moduleGroups(vararg value: ModuleGroupI<*>): B
}


interface CompositeCommandI<B : CompositeCommandI<B>> : CompilationUnitI<B> {
    fun commands(): ListMultiHolder<CommandI<*>>
    fun commands(vararg value: CommandI<*>): B
}


interface ConfigI<B : ConfigI<B>> : ValuesI<B> {
    fun prefix(): String
    fun prefix(value: String): B
}


interface ControllerI<B : ControllerI<B>> : CompilationUnitI<B> {
    fun enums(): ListMultiHolder<EnumTypeI<*>>
    fun enums(vararg value: EnumTypeI<*>): B
    fun enumType(value: EnumTypeI<*>): EnumTypeI<*>
    fun enumType(value: EnumTypeI<*>.() -> Unit = {}): EnumTypeI<*>

    fun values(): ListMultiHolder<ValuesI<*>>
    fun values(vararg value: ValuesI<*>): B
    fun valueType(value: ValuesI<*>): ValuesI<*>
    fun valueType(value: ValuesI<*>.() -> Unit = {}): ValuesI<*>

    fun basics(): ListMultiHolder<BasicI<*>>
    fun basics(vararg value: BasicI<*>): B
    fun basic(value: BasicI<*>): BasicI<*>
    fun basic(value: BasicI<*>.() -> Unit = {}): BasicI<*>
}


interface CountByI<B : CountByI<B>> : DataTypeOperationI<B> {
}


interface CreateByI<B : CreateByI<B>> : CommandI<B> {
}


interface CreatedI<B : CreatedI<B>> : EventI<B> {
}


interface DeleteByI<B : DeleteByI<B>> : CommandI<B> {
}


interface DeletedI<B : DeletedI<B>> : EventI<B> {
}


interface DynamicStateI<B : DynamicStateI<B>> : StateI<B> {
    fun ifTrue(): ListMultiHolder<PredicateI<*>>
    fun ifTrue(vararg value: PredicateI<*>): B
    fun ifT(value: PredicateI<*>): PredicateI<*>
    fun ifT(value: PredicateI<*>.() -> Unit = {}): PredicateI<*>

    fun ifFalse(): ListMultiHolder<PredicateI<*>>
    fun ifFalse(vararg value: PredicateI<*>): B
    fun ifF(value: PredicateI<*>): PredicateI<*>
    fun ifF(value: PredicateI<*>.() -> Unit = {}): PredicateI<*>
}


interface EntityI<B : EntityI<B>> : DataTypeI<B> {
    fun isDefaultEvents(): Boolean
    fun defaultEvents(value: Boolean): B
    fun defaultEvents(): B = defaultEvents(true)
    fun notDefaultEvents(): B = defaultEvents(false)

    fun isDefaultQueries(): Boolean
    fun defaultQueries(value: Boolean): B
    fun defaultQueries(): B = defaultQueries(true)
    fun notDefaultQueries(): B = defaultQueries(false)

    fun isDefaultCommands(): Boolean
    fun defaultCommands(value: Boolean): B
    fun defaultCommands(): B = defaultCommands(true)
    fun notDefaultCommands(): B = defaultCommands(false)

    fun belongsToAggregate(): EntityI<*>
    fun belongsToAggregate(value: EntityI<*>): B

    fun aggregateFor(): ListMultiHolder<EntityI<*>>
    fun aggregateFor(vararg value: EntityI<*>): B

    fun controllers(): ListMultiHolder<BusinessControllerI<*>>
    fun controllers(vararg value: BusinessControllerI<*>): B
    fun controller(value: BusinessControllerI<*>): BusinessControllerI<*>
    fun controller(value: BusinessControllerI<*>.() -> Unit = {}): BusinessControllerI<*>

    fun findBys(): ListMultiHolder<FindByI<*>>
    fun findBys(vararg value: FindByI<*>): B
    fun findBy(value: FindByI<*>): FindByI<*>
    fun findBy(value: FindByI<*>.() -> Unit = {}): FindByI<*>

    fun countBys(): ListMultiHolder<CountByI<*>>
    fun countBys(vararg value: CountByI<*>): B
    fun countBy(value: CountByI<*>): CountByI<*>
    fun countBy(value: CountByI<*>.() -> Unit = {}): CountByI<*>

    fun existBys(): ListMultiHolder<ExistByI<*>>
    fun existBys(vararg value: ExistByI<*>): B
    fun existBy(value: ExistByI<*>): ExistByI<*>
    fun existBy(value: ExistByI<*>.() -> Unit = {}): ExistByI<*>

    fun commands(): ListMultiHolder<BusinessCommandI<*>>
    fun commands(vararg value: BusinessCommandI<*>): B
    fun command(value: BusinessCommandI<*>): BusinessCommandI<*>
    fun command(value: BusinessCommandI<*>.() -> Unit = {}): BusinessCommandI<*>

    fun composites(): ListMultiHolder<CompositeCommandI<*>>
    fun composites(vararg value: CompositeCommandI<*>): B
    fun composite(value: CompositeCommandI<*>): CompositeCommandI<*>
    fun composite(value: CompositeCommandI<*>.() -> Unit = {}): CompositeCommandI<*>

    fun createBys(): ListMultiHolder<CreateByI<*>>
    fun createBys(vararg value: CreateByI<*>): B
    fun createBy(value: CreateByI<*>): CreateByI<*>
    fun createBy(value: CreateByI<*>.() -> Unit = {}): CreateByI<*>

    fun updateBys(): ListMultiHolder<UpdateByI<*>>
    fun updateBys(vararg value: UpdateByI<*>): B
    fun updateBy(value: UpdateByI<*>): UpdateByI<*>
    fun updateBy(value: UpdateByI<*>.() -> Unit = {}): UpdateByI<*>

    fun deleteBys(): ListMultiHolder<DeleteByI<*>>
    fun deleteBys(vararg value: DeleteByI<*>): B
    fun deleteBy(value: DeleteByI<*>): DeleteByI<*>
    fun deleteBy(value: DeleteByI<*>.() -> Unit = {}): DeleteByI<*>

    fun events(): ListMultiHolder<BusinessEventI<*>>
    fun events(vararg value: BusinessEventI<*>): B
    fun event(value: BusinessEventI<*>): BusinessEventI<*>
    fun event(value: BusinessEventI<*>.() -> Unit = {}): BusinessEventI<*>

    fun created(): ListMultiHolder<CreatedI<*>>
    fun created(vararg value: CreatedI<*>): B
    fun created(value: CreatedI<*>): CreatedI<*>
    fun created(value: CreatedI<*>.() -> Unit = {}): CreatedI<*>

    fun updated(): ListMultiHolder<UpdatedI<*>>
    fun updated(vararg value: UpdatedI<*>): B
    fun updated(value: UpdatedI<*>): UpdatedI<*>
    fun updated(value: UpdatedI<*>.() -> Unit = {}): UpdatedI<*>

    fun deleted(): ListMultiHolder<DeletedI<*>>
    fun deleted(vararg value: DeletedI<*>): B
    fun deleted(value: DeletedI<*>): DeletedI<*>
    fun deleted(value: DeletedI<*>.() -> Unit = {}): DeletedI<*>

    fun checks(): ListMultiHolder<PredicateI<*>>
    fun checks(vararg value: PredicateI<*>): B
    fun check(value: PredicateI<*>): PredicateI<*>
    fun check(value: PredicateI<*>.() -> Unit = {}): PredicateI<*>

    fun handlers(): ListMultiHolder<AggregateHandlerI<*>>
    fun handlers(vararg value: AggregateHandlerI<*>): B
    fun handler(value: AggregateHandlerI<*>): AggregateHandlerI<*>
    fun handler(value: AggregateHandlerI<*>.() -> Unit = {}): AggregateHandlerI<*>

    fun projectors(): ListMultiHolder<ProjectorI<*>>
    fun projectors(vararg value: ProjectorI<*>): B
    fun projector(value: ProjectorI<*>): ProjectorI<*>
    fun projector(value: ProjectorI<*>.() -> Unit = {}): ProjectorI<*>

    fun processManager(): ListMultiHolder<ProcessManagerI<*>>
    fun processManager(vararg value: ProcessManagerI<*>): B
    fun processManager(value: ProcessManagerI<*>): ProcessManagerI<*>
    fun processManager(value: ProcessManagerI<*>.() -> Unit = {}): ProcessManagerI<*>
}


interface EventI<B : EventI<B>> : CompilationUnitI<B> {
}


interface ExecutorI<B : ExecutorI<B>> : LogicUnitI<B> {
    fun on(): CommandI<*>
    fun on(value: CommandI<*>): B

    fun ifTrue(): ListMultiHolder<PredicateI<*>>
    fun ifTrue(vararg value: PredicateI<*>): B
    fun ifT(value: PredicateI<*>): PredicateI<*>
    fun ifT(value: PredicateI<*>.() -> Unit = {}): PredicateI<*>

    fun ifFalse(): ListMultiHolder<PredicateI<*>>
    fun ifFalse(vararg value: PredicateI<*>): B
    fun ifF(value: PredicateI<*>): PredicateI<*>
    fun ifF(value: PredicateI<*>.() -> Unit = {}): PredicateI<*>

    fun actions(): ListMultiHolder<ActionI<*>>
    fun actions(vararg value: ActionI<*>): B
    fun action(value: ActionI<*>): ActionI<*>
    fun action(value: ActionI<*>.() -> Unit = {}): ActionI<*>

    fun output(): ListMultiHolder<EventI<*>>
    fun output(vararg value: EventI<*>): B
    fun produce(value: EventI<*>): EventI<*>
    fun produce(value: EventI<*>.() -> Unit = {}): EventI<*>
}


interface ExistByI<B : ExistByI<B>> : DataTypeOperationI<B> {
}


interface ExternalModuleI<B : ExternalModuleI<B>> : ModuleI<B> {
    fun externalTypes(): ListMultiHolder<ExternalTypeI<*>>
    fun externalTypes(vararg value: ExternalTypeI<*>): B
}


interface FacetI<B : FacetI<B>> : ModuleGroupI<B> {
}


interface FindByI<B : FindByI<B>> : DataTypeOperationI<B> {
    fun isMultiResult(): Boolean
    fun multiResult(value: Boolean): B
    fun multiResult(): B = multiResult(true)
    fun notMultiResult(): B = multiResult(false)
}


interface HandlerI<B : HandlerI<B>> : LogicUnitI<B> {
    fun on(): EventI<*>
    fun on(value: EventI<*>): B

    fun ifTrue(): ListMultiHolder<PredicateI<*>>
    fun ifTrue(vararg value: PredicateI<*>): B
    fun ifT(value: PredicateI<*>): PredicateI<*>
    fun ifT(value: PredicateI<*>.() -> Unit = {}): PredicateI<*>

    fun ifFalse(): ListMultiHolder<PredicateI<*>>
    fun ifFalse(vararg value: PredicateI<*>): B
    fun ifF(value: PredicateI<*>): PredicateI<*>
    fun ifF(value: PredicateI<*>.() -> Unit = {}): PredicateI<*>

    fun to(): StateI<*>
    fun to(value: StateI<*>): B

    fun actions(): ListMultiHolder<ActionI<*>>
    fun actions(vararg value: ActionI<*>): B
    fun action(value: ActionI<*>): ActionI<*>
    fun action(value: ActionI<*>.() -> Unit = {}): ActionI<*>

    fun output(): ListMultiHolder<CommandI<*>>
    fun output(vararg value: CommandI<*>): B
    fun produce(value: CommandI<*>): CommandI<*>
    fun produce(value: CommandI<*>.() -> Unit = {}): CommandI<*>
}


interface ModelI<B : ModelI<B>> : StructureUnitI<B> {
    fun models(): ListMultiHolder<ModelI<*>>
    fun models(vararg value: ModelI<*>): B

    fun comps(): ListMultiHolder<CompI<*>>
    fun comps(vararg value: CompI<*>): B
}


interface ModuleI<B : ModuleI<B>> : StructureUnitI<B> {
    fun isParentNamespace(): Boolean
    fun parentNamespace(value: Boolean): B
    fun parentNamespace(): B = parentNamespace(true)
    fun notParentNamespace(): B = parentNamespace(false)

    fun dependencies(): ListMultiHolder<ModuleI<*>>
    fun dependencies(vararg value: ModuleI<*>): B

    fun entities(): ListMultiHolder<EntityI<*>>
    fun entities(vararg value: EntityI<*>): B
    fun entity(value: EntityI<*>): EntityI<*>
    fun entity(value: EntityI<*>.() -> Unit = {}): EntityI<*>

    fun enums(): ListMultiHolder<EnumTypeI<*>>
    fun enums(vararg value: EnumTypeI<*>): B
    fun enumType(value: EnumTypeI<*>): EnumTypeI<*>
    fun enumType(value: EnumTypeI<*>.() -> Unit = {}): EnumTypeI<*>

    fun values(): ListMultiHolder<ValuesI<*>>
    fun values(vararg value: ValuesI<*>): B
    fun valueType(value: ValuesI<*>): ValuesI<*>
    fun valueType(value: ValuesI<*>.() -> Unit = {}): ValuesI<*>

    fun basics(): ListMultiHolder<BasicI<*>>
    fun basics(vararg value: BasicI<*>): B
    fun basic(value: BasicI<*>): BasicI<*>
    fun basic(value: BasicI<*>.() -> Unit = {}): BasicI<*>

    fun controllers(): ListMultiHolder<BusinessControllerI<*>>
    fun controllers(vararg value: BusinessControllerI<*>): B
    fun controller(value: BusinessControllerI<*>): BusinessControllerI<*>
    fun controller(value: BusinessControllerI<*>.() -> Unit = {}): BusinessControllerI<*>

    fun processManagers(): ListMultiHolder<ProcessManagerI<*>>
    fun processManagers(vararg value: ProcessManagerI<*>): B
    fun processManager(value: ProcessManagerI<*>): ProcessManagerI<*>
    fun processManager(value: ProcessManagerI<*>.() -> Unit = {}): ProcessManagerI<*>

    fun projectors(): ListMultiHolder<ProjectorI<*>>
    fun projectors(vararg value: ProjectorI<*>): B
    fun projector(value: ProjectorI<*>): ProjectorI<*>
    fun projector(value: ProjectorI<*>.() -> Unit = {}): ProjectorI<*>
}


interface ModuleGroupI<B : ModuleGroupI<B>> : StructureUnitI<B> {
    fun modules(): ListMultiHolder<ModuleI<*>>
    fun modules(vararg value: ModuleI<*>): B
}


interface ProcessManagerI<B : ProcessManagerI<B>> : StateMachineI<B> {
}


interface ProjectorI<B : ProjectorI<B>> : StateMachineI<B> {
}


interface StateI<B : StateI<B>> : ControllerI<B> {
    fun timeout(): Long
    fun timeout(value: Long): B

    fun entryActions(): ListMultiHolder<ActionI<*>>
    fun entryActions(vararg value: ActionI<*>): B
    fun entry(value: ActionI<*>): ActionI<*>
    fun entry(value: ActionI<*>.() -> Unit = {}): ActionI<*>

    fun exitActions(): ListMultiHolder<ActionI<*>>
    fun exitActions(vararg value: ActionI<*>): B
    fun exit(value: ActionI<*>): ActionI<*>
    fun exit(value: ActionI<*>.() -> Unit = {}): ActionI<*>

    fun executors(): ListMultiHolder<ExecutorI<*>>
    fun executors(vararg value: ExecutorI<*>): B
    fun execute(value: ExecutorI<*>): ExecutorI<*>
    fun execute(value: ExecutorI<*>.() -> Unit = {}): ExecutorI<*>

    fun handlers(): ListMultiHolder<HandlerI<*>>
    fun handlers(vararg value: HandlerI<*>): B
    fun handle(value: HandlerI<*>): HandlerI<*>
    fun handle(value: HandlerI<*>.() -> Unit = {}): HandlerI<*>
}


interface StateMachineI<B : StateMachineI<B>> : ControllerI<B> {
    fun stateProp(): AttributeI<*>
    fun stateProp(value: AttributeI<*>): B

    fun timeoutProp(): AttributeI<*>
    fun timeoutProp(value: AttributeI<*>): B

    fun timeout(): Long
    fun timeout(value: Long): B

    fun states(): ListMultiHolder<StateI<*>>
    fun states(vararg value: StateI<*>): B
    fun state(value: StateI<*>): StateI<*>
    fun state(value: StateI<*>.() -> Unit = {}): StateI<*>

    fun checks(): ListMultiHolder<PredicateI<*>>
    fun checks(vararg value: PredicateI<*>): B
    fun check(value: PredicateI<*>): PredicateI<*>
    fun check(value: PredicateI<*>.() -> Unit = {}): PredicateI<*>
}


interface UpdateByI<B : UpdateByI<B>> : CommandI<B> {
}


interface UpdatedI<B : UpdatedI<B>> : EventI<B> {
}


interface WidgetI<B : WidgetI<B>> : CompilationUnitI<B> {
}


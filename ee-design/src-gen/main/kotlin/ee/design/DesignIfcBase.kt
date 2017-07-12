package ee.design

import ee.lang.CompilationUnitI
import ee.lang.DataTypeI
import ee.lang.DataTypeOperationI
import ee.lang.EnumTypeI
import ee.lang.ExternalTypeI
import ee.lang.ListMultiHolderI
import ee.lang.StructureUnitI


interface BasicI : DataTypeI {
}


interface BundleI : StructureUnitI {
    fun units(): ListMultiHolderI<StructureUnitI>
    fun units(vararg value: StructureUnitI): BundleI
}


interface BussinesCommandI : CommandI {
}


interface BussinesEventI : EventI {
}


interface CommandI : CompilationUnitI {
}


interface CommandsI : ControllerI {
    fun commands(): ListMultiHolderI<BussinesCommandI>
    fun commands(vararg value: BussinesCommandI): CommandsI
    fun command(value: BussinesCommandI): BussinesCommandI
    fun command(value: BussinesCommandI.() -> Unit = {}) : BussinesCommandI

    fun composites(): ListMultiHolderI<CompositeCommandI>
    fun composites(vararg value: CompositeCommandI): CommandsI
    fun composite(value: CompositeCommandI): CompositeCommandI
    fun composite(value: CompositeCommandI.() -> Unit = {}) : CompositeCommandI

    fun createBys(): ListMultiHolderI<CreateByI>
    fun createBys(vararg value: CreateByI): CommandsI
    fun createBy(value: CreateByI): CreateByI
    fun createBy(value: CreateByI.() -> Unit = {}) : CreateByI

    fun updateBys(): ListMultiHolderI<UpdateByI>
    fun updateBys(vararg value: UpdateByI): CommandsI
    fun updateBy(value: UpdateByI): UpdateByI
    fun updateBy(value: UpdateByI.() -> Unit = {}) : UpdateByI

    fun deleteBys(): ListMultiHolderI<DeleteByI>
    fun deleteBys(vararg value: DeleteByI): CommandsI
    fun deleteBy(value: DeleteByI): DeleteByI
    fun deleteBy(value: DeleteByI.() -> Unit = {}) : DeleteByI
}


interface CompI : ModuleGroupI {
    fun moduleGroups(): ListMultiHolderI<ModuleGroupI>
    fun moduleGroups(vararg value: ModuleGroupI): CompI
}


interface CompositeCommandI : CompilationUnitI {
    fun commands(): ListMultiHolderI<CommandI>
    fun commands(vararg value: CommandI): CompositeCommandI
}


interface ControllerI : CompilationUnitI {
    fun enums(): ListMultiHolderI<EnumTypeI>
    fun enums(vararg value: EnumTypeI): ControllerI
}


interface CountByI : DataTypeOperationI {
}


interface CreateByI : CommandI {
}


interface CreatedI : EventI {
}


interface DeleteByI : CommandI {
}


interface DeletedI : EventI {
}


interface EntityI : DataTypeI {
    fun belongsToAggregate(): EntityI
    fun belongsToAggregate(value: EntityI): EntityI

    fun aggregateFor(): ListMultiHolderI<EntityI>
    fun aggregateFor(vararg value: EntityI): EntityI

    fun controllers(): ListMultiHolderI<ControllerI>
    fun controllers(vararg value: ControllerI): EntityI

    fun commands(): ListMultiHolderI<CommandsI>
    fun commands(vararg value: CommandsI): EntityI

    fun queries(): ListMultiHolderI<QueriesI>
    fun queries(vararg value: QueriesI): EntityI

    fun events(): ListMultiHolderI<EventsI>
    fun events(vararg value: EventsI): EntityI
}


interface EventI : CompilationUnitI {
}


interface EventsI : ControllerI {
    fun events(): ListMultiHolderI<BussinesEventI>
    fun events(vararg value: BussinesEventI): EventsI
    fun event(value: BussinesEventI): BussinesEventI
    fun event(value: BussinesEventI.() -> Unit = {}) : BussinesEventI

    fun created(): ListMultiHolderI<CreatedI>
    fun created(vararg value: CreatedI): EventsI
    fun created(value: CreatedI): CreatedI
    fun created(value: CreatedI.() -> Unit = {}) : CreatedI

    fun updated(): ListMultiHolderI<UpdatedI>
    fun updated(vararg value: UpdatedI): EventsI
    fun updated(value: UpdatedI): UpdatedI
    fun updated(value: UpdatedI.() -> Unit = {}) : UpdatedI

    fun deleted(): ListMultiHolderI<DeletedI>
    fun deleted(vararg value: DeletedI): EventsI
    fun deleted(value: DeletedI): DeletedI
    fun deleted(value: DeletedI.() -> Unit = {}) : DeletedI
}


interface ExistByI : DataTypeOperationI {
}


interface ExternalModuleI : ModuleI {
    fun externalTypes(): ListMultiHolderI<ExternalTypeI>
    fun externalTypes(vararg value: ExternalTypeI): ExternalModuleI
}


interface FacetI : ModuleGroupI {
}


interface FindByI : DataTypeOperationI {
}


interface ModelI : StructureUnitI {
    fun models(): ListMultiHolderI<ModelI>
    fun models(vararg value: ModelI): ModelI

    fun comps(): ListMultiHolderI<CompI>
    fun comps(vararg value: CompI): ModelI
}


interface ModuleI : StructureUnitI {
    fun parentNamespace(): Boolean
    fun parentNamespace(value: Boolean): ModuleI

    fun dependencies(): ListMultiHolderI<ModuleI>
    fun dependencies(vararg value: ModuleI): ModuleI

    fun entities(): ListMultiHolderI<EntityI>
    fun entities(vararg value: EntityI): ModuleI

    fun enums(): ListMultiHolderI<EnumTypeI>
    fun enums(vararg value: EnumTypeI): ModuleI

    fun values(): ListMultiHolderI<ValuesI>
    fun values(vararg value: ValuesI): ModuleI

    fun basics(): ListMultiHolderI<BasicI>
    fun basics(vararg value: BasicI): ModuleI

    fun controllers(): ListMultiHolderI<ControllerI>
    fun controllers(vararg value: ControllerI): ModuleI
}


interface ModuleGroupI : StructureUnitI {
    fun modules(): ListMultiHolderI<ModuleI>
    fun modules(vararg value: ModuleI): ModuleGroupI
}


interface QueriesI : ControllerI {
    fun findBys(): ListMultiHolderI<FindByI>
    fun findBys(vararg value: FindByI): QueriesI
    fun findBy(value: FindByI): FindByI
    fun findBy(value: FindByI.() -> Unit = {}) : FindByI

    fun countBys(): ListMultiHolderI<CountByI>
    fun countBys(vararg value: CountByI): QueriesI
    fun countBy(value: CountByI): CountByI
    fun countBy(value: CountByI.() -> Unit = {}) : CountByI

    fun existBys(): ListMultiHolderI<ExistByI>
    fun existBys(vararg value: ExistByI): QueriesI
    fun existBy(value: ExistByI): ExistByI
    fun existBy(value: ExistByI.() -> Unit = {}) : ExistByI
}


interface UpdateByI : CommandI {
}


interface UpdatedI : EventI {
}


interface ValuesI : DataTypeI {
}


interface WidgetI : CompilationUnitI {
}


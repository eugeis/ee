package ee.design

import ee.lang.CompilationUnitI
import ee.lang.DataTypeI
import ee.lang.DataTypeOperationI
import ee.lang.EnumTypeI
import ee.lang.ExternalTypeI
import ee.lang.ListMultiHolderI
import ee.lang.OperationI
import ee.lang.StructureUnitI


interface BasicI : DataTypeI {
}


interface BundleI : StructureUnitI {
    fun units(): ListMultiHolderI<StructureUnitI>
    fun units(vararg value: StructureUnitI): BundleI
}


interface BussinesCommandI : CommandI {
}


interface CommandI : DataTypeOperationI {
}


interface CommandControllerI : ControllerI {
    fun commands(): ListMultiHolderI<BussinesCommandI>
    fun commands(vararg value: BussinesCommandI): CommandControllerI
    fun command(value: BussinesCommandI): BussinesCommandI
    fun command(value: BussinesCommandI.() -> Unit = {}) : BussinesCommandI

    fun composites(): ListMultiHolderI<CompositeCommandI>
    fun composites(vararg value: CompositeCommandI): CommandControllerI
    fun composite(value: CompositeCommandI): CompositeCommandI
    fun composite(value: CompositeCommandI.() -> Unit = {}) : CompositeCommandI

    fun createBys(): ListMultiHolderI<CreateByI>
    fun createBys(vararg value: CreateByI): CommandControllerI
    fun createBy(value: CreateByI): CreateByI
    fun createBy(value: CreateByI.() -> Unit = {}) : CreateByI

    fun updateBys(): ListMultiHolderI<UpdateByI>
    fun updateBys(vararg value: UpdateByI): CommandControllerI
    fun updateBy(value: UpdateByI): UpdateByI
    fun updateBy(value: UpdateByI.() -> Unit = {}) : UpdateByI

    fun deleteBys(): ListMultiHolderI<DeleteByI>
    fun deleteBys(vararg value: DeleteByI): CommandControllerI
    fun deleteBy(value: DeleteByI): DeleteByI
    fun deleteBy(value: DeleteByI.() -> Unit = {}) : DeleteByI
}


interface CompI : ModuleGroupI {
    fun moduleGroups(): ListMultiHolderI<ModuleGroupI>
    fun moduleGroups(vararg value: ModuleGroupI): CompI
}


interface CompositeCommandI : DataTypeOperationI {
    fun operations(): ListMultiHolderI<OperationI>
    fun operations(vararg value: OperationI): CompositeCommandI
}


interface ControllerI : CompilationUnitI {
}


interface CountByI : DataTypeOperationI {
}


interface CreateByI : CommandI {
}


interface DeleteByI : CommandI {
}


interface EntityI : DataTypeI {
    fun controllers(): ListMultiHolderI<ControllerI>
    fun controllers(vararg value: ControllerI): EntityI

    fun commands(): ListMultiHolderI<CommandControllerI>
    fun commands(vararg value: CommandControllerI): EntityI

    fun queries(): ListMultiHolderI<QueryControllerI>
    fun queries(vararg value: QueryControllerI): EntityI
}


interface EventI : CompilationUnitI {
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

    fun events(): ListMultiHolderI<EventI>
    fun events(vararg value: EventI): ModuleI

    fun commands(): ListMultiHolderI<CommandI>
    fun commands(vararg value: CommandI): ModuleI

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


interface QueryControllerI : ControllerI {
    fun findBys(): ListMultiHolderI<FindByI>
    fun findBys(vararg value: FindByI): QueryControllerI
    fun findBy(value: FindByI): FindByI
    fun findBy(value: FindByI.() -> Unit = {}) : FindByI

    fun countBys(): ListMultiHolderI<CountByI>
    fun countBys(vararg value: CountByI): QueryControllerI
    fun countBy(value: CountByI): CountByI
    fun countBy(value: CountByI.() -> Unit = {}) : CountByI

    fun existBys(): ListMultiHolderI<ExistByI>
    fun existBys(vararg value: ExistByI): QueryControllerI
    fun existBy(value: ExistByI): ExistByI
    fun existBy(value: ExistByI.() -> Unit = {}) : ExistByI
}


interface UpdateByI : CommandI {
}


interface ValuesI : DataTypeI {
}


interface WidgetI : CompilationUnitI {
}


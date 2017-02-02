package ee.design

import ee.lang.AttributeI
import ee.lang.CompilationUnitI
import ee.lang.EnumTypeI
import ee.lang.ExternalTypeI
import ee.lang.OperationI
import ee.lang.StructureUnitI


interface BasicI : CompilationUnitI {
}


interface BundleI : StructureUnitI {
    fun units(): List<StructureUnitI>
    fun units(vararg value: StructureUnitI): BundleI
}


interface CommandI : DataTypeOperationI {
}


interface CommandControllerI : ControllerI {
    fun commands(): List<CommandI>
    fun commands(vararg value: CommandI): CommandControllerI
    fun command(value: CommandI): CommandI
    fun command(value: CommandI.() -> Unit = {}) : CommandI

    fun composites(): List<CompositeCommandI>
    fun composites(vararg value: CompositeCommandI): CommandControllerI
    fun composite(value: CompositeCommandI): CompositeCommandI
    fun composite(value: CompositeCommandI.() -> Unit = {}) : CompositeCommandI

    fun createBys(): List<CreateByI>
    fun createBys(vararg value: CreateByI): CommandControllerI
    fun createBy(value: CreateByI): CreateByI
    fun createBy(value: CreateByI.() -> Unit = {}) : CreateByI

    fun updateBys(): List<UpdateByI>
    fun updateBys(vararg value: UpdateByI): CommandControllerI
    fun updateBy(value: UpdateByI): UpdateByI
    fun updateBy(value: UpdateByI.() -> Unit = {}) : UpdateByI

    fun deleteBys(): List<DeleteByI>
    fun deleteBys(vararg value: DeleteByI): CommandControllerI
    fun deleteBy(value: DeleteByI): DeleteByI
    fun deleteBy(value: DeleteByI.() -> Unit = {}) : DeleteByI
}


interface CompI : ModuleGroupI {
    fun moduleGroups(): List<ModuleGroupI>
    fun moduleGroups(vararg value: ModuleGroupI): CompI
}


interface CompositeCommandI : DataTypeOperationI {
    fun operations(): List<OperationI>
    fun operations(vararg value: OperationI): CompositeCommandI
}


interface ControllerI : CompilationUnitI {
}


interface CountByI : DataTypeOperationI {
}


interface CreateByI : CommandI {
}


interface DataTypeOperationI : OperationI {
}


interface DeleteByI : CommandI {
}


interface EntityI : CompilationUnitI {
    fun id(): AttributeI
    fun id(value: AttributeI): EntityI

    fun controllers(): List<ControllerI>
    fun controllers(vararg value: ControllerI): EntityI

    fun commands(): List<CommandControllerI>
    fun commands(vararg value: CommandControllerI): EntityI

    fun queries(): List<QueryControllerI>
    fun queries(vararg value: QueryControllerI): EntityI
}


interface EventI : CompilationUnitI {
}


interface ExistByI : DataTypeOperationI {
}


interface ExternalModuleI : ModuleI {
    fun externalTypes(): List<ExternalTypeI>
    fun externalTypes(vararg value: ExternalTypeI): ExternalModuleI
}


interface FacetI : ModuleGroupI {
}


interface FindByI : DataTypeOperationI {
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

    fun events(): List<EventI>
    fun events(vararg value: EventI): ModuleI

    fun commands(): List<CommandI>
    fun commands(vararg value: CommandI): ModuleI

    fun entities(): List<EntityI>
    fun entities(vararg value: EntityI): ModuleI

    fun enums(): List<EnumTypeI>
    fun enums(vararg value: EnumTypeI): ModuleI

    fun values(): List<ValuesI>
    fun values(vararg value: ValuesI): ModuleI

    fun basics(): List<BasicI>
    fun basics(vararg value: BasicI): ModuleI

    fun controllers(): List<ControllerI>
    fun controllers(vararg value: ControllerI): ModuleI
}


interface ModuleGroupI : StructureUnitI {
    fun modules(): List<ModuleI>
    fun modules(vararg value: ModuleI): ModuleGroupI
}


interface QueryControllerI : ControllerI {
    fun findBys(): List<FindByI>
    fun findBys(vararg value: FindByI): QueryControllerI
    fun findBy(value: FindByI): FindByI
    fun findBy(value: FindByI.() -> Unit = {}) : FindByI

    fun countBys(): List<CountByI>
    fun countBys(vararg value: CountByI): QueryControllerI
    fun countBy(value: CountByI): CountByI
    fun countBy(value: CountByI.() -> Unit = {}) : CountByI

    fun existBys(): List<ExistByI>
    fun existBys(vararg value: ExistByI): QueryControllerI
    fun existBy(value: ExistByI): ExistByI
    fun existBy(value: ExistByI.() -> Unit = {}) : ExistByI
}


interface UpdateByI : CommandI {
}


interface ValuesI : CompilationUnitI {
}


interface WidgetI : CompilationUnitI {
}


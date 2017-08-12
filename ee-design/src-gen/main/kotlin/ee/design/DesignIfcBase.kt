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
    fun event(): EventI
    fun event(value: EventI): CommandI
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
    fun enumType(value: EnumTypeI): EnumTypeI
    fun enumType(value: EnumTypeI.() -> Unit = {}): EnumTypeI

    fun values(): ListMultiHolderI<ValuesI>
    fun values(vararg value: ValuesI): ControllerI
    fun valueType(value: ValuesI): ValuesI
    fun valueType(value: ValuesI.() -> Unit = {}): ValuesI

    fun basics(): ListMultiHolderI<BasicI>
    fun basics(vararg value: BasicI): ControllerI
    fun basic(value: BasicI): BasicI
    fun basic(value: BasicI.() -> Unit = {}): BasicI
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
    fun defaultEvents(): Boolean
    fun defaultEvents(value: Boolean): EntityI

    fun defaultQueries(): Boolean
    fun defaultQueries(value: Boolean): EntityI

    fun defaultCommands(): Boolean
    fun defaultCommands(value: Boolean): EntityI

    fun belongsToAggregate(): EntityI
    fun belongsToAggregate(value: EntityI): EntityI

    fun aggregateFor(): ListMultiHolderI<EntityI>
    fun aggregateFor(vararg value: EntityI): EntityI

    fun controllers(): ListMultiHolderI<ControllerI>
    fun controllers(vararg value: ControllerI): EntityI
    fun controller(value: ControllerI): ControllerI
    fun controller(value: ControllerI.() -> Unit = {}): ControllerI

    fun findBys(): ListMultiHolderI<FindByI>
    fun findBys(vararg value: FindByI): EntityI
    fun findBy(value: FindByI): FindByI
    fun findBy(value: FindByI.() -> Unit = {}): FindByI

    fun countBys(): ListMultiHolderI<CountByI>
    fun countBys(vararg value: CountByI): EntityI
    fun countBy(value: CountByI): CountByI
    fun countBy(value: CountByI.() -> Unit = {}): CountByI

    fun existBys(): ListMultiHolderI<ExistByI>
    fun existBys(vararg value: ExistByI): EntityI
    fun existBy(value: ExistByI): ExistByI
    fun existBy(value: ExistByI.() -> Unit = {}): ExistByI

    fun commands(): ListMultiHolderI<BussinesCommandI>
    fun commands(vararg value: BussinesCommandI): EntityI
    fun command(value: BussinesCommandI): BussinesCommandI
    fun command(value: BussinesCommandI.() -> Unit = {}): BussinesCommandI

    fun composites(): ListMultiHolderI<CompositeCommandI>
    fun composites(vararg value: CompositeCommandI): EntityI
    fun composite(value: CompositeCommandI): CompositeCommandI
    fun composite(value: CompositeCommandI.() -> Unit = {}): CompositeCommandI

    fun createBys(): ListMultiHolderI<CreateByI>
    fun createBys(vararg value: CreateByI): EntityI
    fun createBy(value: CreateByI): CreateByI
    fun createBy(value: CreateByI.() -> Unit = {}): CreateByI

    fun updateBys(): ListMultiHolderI<UpdateByI>
    fun updateBys(vararg value: UpdateByI): EntityI
    fun updateBy(value: UpdateByI): UpdateByI
    fun updateBy(value: UpdateByI.() -> Unit = {}): UpdateByI

    fun deleteBys(): ListMultiHolderI<DeleteByI>
    fun deleteBys(vararg value: DeleteByI): EntityI
    fun deleteBy(value: DeleteByI): DeleteByI
    fun deleteBy(value: DeleteByI.() -> Unit = {}): DeleteByI

    fun events(): ListMultiHolderI<BussinesEventI>
    fun events(vararg value: BussinesEventI): EntityI
    fun event(value: BussinesEventI): BussinesEventI
    fun event(value: BussinesEventI.() -> Unit = {}): BussinesEventI

    fun created(): ListMultiHolderI<CreatedI>
    fun created(vararg value: CreatedI): EntityI
    fun created(value: CreatedI): CreatedI
    fun created(value: CreatedI.() -> Unit = {}): CreatedI

    fun updated(): ListMultiHolderI<UpdatedI>
    fun updated(vararg value: UpdatedI): EntityI
    fun updated(value: UpdatedI): UpdatedI
    fun updated(value: UpdatedI.() -> Unit = {}): UpdatedI

    fun deleted(): ListMultiHolderI<DeletedI>
    fun deleted(vararg value: DeletedI): EntityI
    fun deleted(value: DeletedI): DeletedI
    fun deleted(value: DeletedI.() -> Unit = {}): DeletedI
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

    fun entities(): ListMultiHolderI<EntityI>
    fun entities(vararg value: EntityI): ModuleI
    fun entity(value: EntityI): EntityI
    fun entity(value: EntityI.() -> Unit = {}): EntityI

    fun enums(): ListMultiHolderI<EnumTypeI>
    fun enums(vararg value: EnumTypeI): ModuleI
    fun enumType(value: EnumTypeI): EnumTypeI
    fun enumType(value: EnumTypeI.() -> Unit = {}): EnumTypeI

    fun values(): ListMultiHolderI<ValuesI>
    fun values(vararg value: ValuesI): ModuleI
    fun valueType(value: ValuesI): ValuesI
    fun valueType(value: ValuesI.() -> Unit = {}): ValuesI

    fun basics(): ListMultiHolderI<BasicI>
    fun basics(vararg value: BasicI): ModuleI
    fun basic(value: BasicI): BasicI
    fun basic(value: BasicI.() -> Unit = {}): BasicI

    fun controllers(): ListMultiHolderI<ControllerI>
    fun controllers(vararg value: ControllerI): ModuleI
    fun controller(value: ControllerI): ControllerI
    fun controller(value: ControllerI.() -> Unit = {}): ControllerI
}


interface ModuleGroupI : StructureUnitI {
    fun modules(): ListMultiHolderI<ModuleI>
    fun modules(vararg value: ModuleI): ModuleGroupI
}


interface UpdateByI : CommandI {
}


interface UpdatedI : EventI {
}


interface ValuesI : DataTypeI {
}


interface WidgetI : CompilationUnitI {
}


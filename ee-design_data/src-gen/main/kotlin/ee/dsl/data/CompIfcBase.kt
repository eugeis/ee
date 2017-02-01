package ee.design

import ee.design.AttributeI
import ee.design.CompilationUnitI
import ee.design.EnumTypeI
import ee.design.ModuleI
import ee.design.OperationI


interface BasicI : CompilationUnitI {
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


interface DataTypeI : CompilationUnitI {
    fun id(): AttributeI
    fun id(value: AttributeI): DataTypeI

    fun controllers(): List<ControllerI>
    fun controllers(vararg value: ControllerI): DataTypeI

    fun commands(): List<CommandControllerI>
    fun commands(vararg value: CommandControllerI): DataTypeI

    fun queries(): List<QueryControllerI>
    fun queries(vararg value: QueryControllerI): DataTypeI
}


interface DataTypeOperationI : OperationI {
}


interface DeleteByI : CommandI {
}


interface DesignModuleI : ModuleI {
    fun events(): List<EventI>
    fun events(vararg value: EventI): DesignModuleI

    fun commands(): List<CommandI>
    fun commands(vararg value: CommandI): DesignModuleI

    fun entities(): List<EntityI>
    fun entities(vararg value: EntityI): DesignModuleI

    fun enums(): List<EnumTypeI>
    fun enums(vararg value: EnumTypeI): DesignModuleI

    fun values(): List<ValuesI>
    fun values(vararg value: ValuesI): DesignModuleI

    fun basics(): List<BasicI>
    fun basics(vararg value: BasicI): DesignModuleI

    fun controllers(): List<ControllerI>
    fun controllers(vararg value: ControllerI): DesignModuleI
}


interface EntityI : DataTypeI {
}


interface EventI : CompilationUnitI {
}


interface ExistByI : DataTypeOperationI {
}


interface FindByI : DataTypeOperationI {
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


interface ValuesI : DataTypeI {
}


interface WidgetI : CompilationUnitI {
}


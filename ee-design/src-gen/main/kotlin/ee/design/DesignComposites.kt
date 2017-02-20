package ee.design

import ee.lang.MultiHolder


open class Basics : MultiHolder<BasicI> {
    constructor(value: Basics.() -> Unit = {}) : super(BasicI::class.java,
            value as MultiHolder<BasicI>.() -> Unit)
}


open class Bundles : MultiHolder<BundleI> {
    constructor(value: Bundles.() -> Unit = {}) : super(BundleI::class.java,
            value as MultiHolder<BundleI>.() -> Unit)
}


open class Commands : MultiHolder<CommandI> {
    constructor(value: Commands.() -> Unit = {}) : super(CommandI::class.java,
            value as MultiHolder<CommandI>.() -> Unit)
}


open class CommandControllers : MultiHolder<CommandControllerI> {
    constructor(value: CommandControllers.() -> Unit = {}) : super(CommandControllerI::class.java,
            value as MultiHolder<CommandControllerI>.() -> Unit)
}


open class Comps : MultiHolder<CompI> {
    constructor(value: Comps.() -> Unit = {}) : super(CompI::class.java,
            value as MultiHolder<CompI>.() -> Unit)
}


open class CompositeCommands : MultiHolder<CompositeCommandI> {
    constructor(value: CompositeCommands.() -> Unit = {}) : super(CompositeCommandI::class.java,
            value as MultiHolder<CompositeCommandI>.() -> Unit)
}


open class Controllers : MultiHolder<ControllerI> {
    constructor(value: Controllers.() -> Unit = {}) : super(ControllerI::class.java,
            value as MultiHolder<ControllerI>.() -> Unit)
}


open class CountBys : MultiHolder<CountByI> {
    constructor(value: CountBys.() -> Unit = {}) : super(CountByI::class.java,
            value as MultiHolder<CountByI>.() -> Unit)
}


open class CreateBys : MultiHolder<CreateByI> {
    constructor(value: CreateBys.() -> Unit = {}) : super(CreateByI::class.java,
            value as MultiHolder<CreateByI>.() -> Unit)
}


open class DataTypeOperations : MultiHolder<DataTypeOperationI> {
    constructor(value: DataTypeOperations.() -> Unit = {}) : super(DataTypeOperationI::class.java,
            value as MultiHolder<DataTypeOperationI>.() -> Unit)
}


open class DeleteBys : MultiHolder<DeleteByI> {
    constructor(value: DeleteBys.() -> Unit = {}) : super(DeleteByI::class.java,
            value as MultiHolder<DeleteByI>.() -> Unit)
}


open class Entitys : MultiHolder<EntityI> {
    constructor(value: Entitys.() -> Unit = {}) : super(EntityI::class.java,
            value as MultiHolder<EntityI>.() -> Unit)
}


open class Events : MultiHolder<EventI> {
    constructor(value: Events.() -> Unit = {}) : super(EventI::class.java,
            value as MultiHolder<EventI>.() -> Unit)
}


open class ExistBys : MultiHolder<ExistByI> {
    constructor(value: ExistBys.() -> Unit = {}) : super(ExistByI::class.java,
            value as MultiHolder<ExistByI>.() -> Unit)
}


open class ExternalModules : MultiHolder<ExternalModuleI> {
    constructor(value: ExternalModules.() -> Unit = {}) : super(ExternalModuleI::class.java,
            value as MultiHolder<ExternalModuleI>.() -> Unit)
}


open class Facets : MultiHolder<FacetI> {
    constructor(value: Facets.() -> Unit = {}) : super(FacetI::class.java,
            value as MultiHolder<FacetI>.() -> Unit)
}


open class FindBys : MultiHolder<FindByI> {
    constructor(value: FindBys.() -> Unit = {}) : super(FindByI::class.java,
            value as MultiHolder<FindByI>.() -> Unit)
}


open class Models : MultiHolder<ModelI> {
    constructor(value: Models.() -> Unit = {}) : super(ModelI::class.java,
            value as MultiHolder<ModelI>.() -> Unit)
}


open class Modules : MultiHolder<ModuleI> {
    constructor(value: Modules.() -> Unit = {}) : super(ModuleI::class.java,
            value as MultiHolder<ModuleI>.() -> Unit)
}


open class ModuleGroups : MultiHolder<ModuleGroupI> {
    constructor(value: ModuleGroups.() -> Unit = {}) : super(ModuleGroupI::class.java,
            value as MultiHolder<ModuleGroupI>.() -> Unit)
}


open class QueryControllers : MultiHolder<QueryControllerI> {
    constructor(value: QueryControllers.() -> Unit = {}) : super(QueryControllerI::class.java,
            value as MultiHolder<QueryControllerI>.() -> Unit)
}


open class UpdateBys : MultiHolder<UpdateByI> {
    constructor(value: UpdateBys.() -> Unit = {}) : super(UpdateByI::class.java,
            value as MultiHolder<UpdateByI>.() -> Unit)
}


open class Valuess : MultiHolder<ValuesI> {
    constructor(value: Valuess.() -> Unit = {}) : super(ValuesI::class.java,
            value as MultiHolder<ValuesI>.() -> Unit)
}


open class Widgets : MultiHolder<WidgetI> {
    constructor(value: Widgets.() -> Unit = {}) : super(WidgetI::class.java,
            value as MultiHolder<WidgetI>.() -> Unit)
}


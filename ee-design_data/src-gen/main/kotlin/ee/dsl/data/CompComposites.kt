package ee.design

import ee.design.TypedComposite


open class Basics : TypedComposite<BasicI> {
    constructor(value: Basics.() -> Unit = {}) : super(BasicI::class.java,
            value as TypedComposite<BasicI>.() -> Unit)
}


open class Commands : TypedComposite<CommandI> {
    constructor(value: Commands.() -> Unit = {}) : super(CommandI::class.java,
            value as TypedComposite<CommandI>.() -> Unit)
}


open class CommandControllers : TypedComposite<CommandControllerI> {
    constructor(value: CommandControllers.() -> Unit = {}) : super(CommandControllerI::class.java,
            value as TypedComposite<CommandControllerI>.() -> Unit)
}


open class CompositeCommands : TypedComposite<CompositeCommandI> {
    constructor(value: CompositeCommands.() -> Unit = {}) : super(CompositeCommandI::class.java,
            value as TypedComposite<CompositeCommandI>.() -> Unit)
}


open class Controllers : TypedComposite<ControllerI> {
    constructor(value: Controllers.() -> Unit = {}) : super(ControllerI::class.java,
            value as TypedComposite<ControllerI>.() -> Unit)
}


open class CountBys : TypedComposite<CountByI> {
    constructor(value: CountBys.() -> Unit = {}) : super(CountByI::class.java,
            value as TypedComposite<CountByI>.() -> Unit)
}


open class CreateBys : TypedComposite<CreateByI> {
    constructor(value: CreateBys.() -> Unit = {}) : super(CreateByI::class.java,
            value as TypedComposite<CreateByI>.() -> Unit)
}


open class DataTypes : TypedComposite<DataTypeI> {
    constructor(value: DataTypes.() -> Unit = {}) : super(DataTypeI::class.java,
            value as TypedComposite<DataTypeI>.() -> Unit)
}


open class DataTypeOperations : TypedComposite<DataTypeOperationI> {
    constructor(value: DataTypeOperations.() -> Unit = {}) : super(DataTypeOperationI::class.java,
            value as TypedComposite<DataTypeOperationI>.() -> Unit)
}


open class DeleteBys : TypedComposite<DeleteByI> {
    constructor(value: DeleteBys.() -> Unit = {}) : super(DeleteByI::class.java,
            value as TypedComposite<DeleteByI>.() -> Unit)
}


open class DesignModules : TypedComposite<DesignModuleI> {
    constructor(value: DesignModules.() -> Unit = {}) : super(DesignModuleI::class.java,
            value as TypedComposite<DesignModuleI>.() -> Unit)
}


open class Entitys : TypedComposite<EntityI> {
    constructor(value: Entitys.() -> Unit = {}) : super(EntityI::class.java,
            value as TypedComposite<EntityI>.() -> Unit)
}


open class Events : TypedComposite<EventI> {
    constructor(value: Events.() -> Unit = {}) : super(EventI::class.java,
            value as TypedComposite<EventI>.() -> Unit)
}


open class ExistBys : TypedComposite<ExistByI> {
    constructor(value: ExistBys.() -> Unit = {}) : super(ExistByI::class.java,
            value as TypedComposite<ExistByI>.() -> Unit)
}


open class FindBys : TypedComposite<FindByI> {
    constructor(value: FindBys.() -> Unit = {}) : super(FindByI::class.java,
            value as TypedComposite<FindByI>.() -> Unit)
}


open class QueryControllers : TypedComposite<QueryControllerI> {
    constructor(value: QueryControllers.() -> Unit = {}) : super(QueryControllerI::class.java,
            value as TypedComposite<QueryControllerI>.() -> Unit)
}


open class UpdateBys : TypedComposite<UpdateByI> {
    constructor(value: UpdateBys.() -> Unit = {}) : super(UpdateByI::class.java,
            value as TypedComposite<UpdateByI>.() -> Unit)
}


open class Valuess : TypedComposite<ValuesI> {
    constructor(value: Valuess.() -> Unit = {}) : super(ValuesI::class.java,
            value as TypedComposite<ValuesI>.() -> Unit)
}


open class Widgets : TypedComposite<WidgetI> {
    constructor(value: Widgets.() -> Unit = {}) : super(WidgetI::class.java,
            value as TypedComposite<WidgetI>.() -> Unit)
}


package ee.design.gen.angular

import AngularDerivedType
import ee.design.EntityI
import ee.design.gen.ts.DesignTsContextFactory
import ee.lang.*
import ee.lang.gen.ts.TsContextBuilder

open class DesignAngularContextFactory : DesignTsContextFactory() {
    override fun contextBuilder(derived: DerivedController): TsContextBuilder<StructureUnitI<*>> {
        return super.contextBuilder(derived)
    }

    override fun registerForImplOnly(derived: DerivedController) {
        super.registerForImplOnly(derived)
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ViewComponent,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}-entity-view.component" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ListComponent,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}-entity-list.component" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.FormComponent,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}-entity-form.component" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.EnumComponent,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}-enum.component" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.BasicComponent,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}-basic.component" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.Module,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}-model.module" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.RoutingModules,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}-routing.module" }, isNotPartOfNativeTypes))
        derived.registerKinds(
            listOf(AngularDerivedType.Enum, AngularDerivedType.Component, AngularDerivedType.DataService, AngularDerivedType.ViewService),
            { "${this.name().capitalize()}${it}" },
            isNotPartOfNativeTypes)

    }

    override fun buildName(item: ItemI<*>, kind: String): String {
        return if (item is EntityI<*>) {
            buildNameForEntity(item, kind)
        } else if (item is BasicI<*>) {
            buildNameForBasic(item, kind)
        } else {
            super.buildName(item, kind)
        }
    }

    protected open fun buildNameForEntity(item: EntityI<*>, kind: String) = item.nameAndParentName().capitalize()
    protected open fun buildNameForBasic(item: BasicI<*>, kind: String) = item.name().capitalize()
}

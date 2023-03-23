package ee.design.gen.angular

import ee.design.EntityI
import ee.design.gen.ts.DesignTsContextFactory
import ee.lang.*
import ee.lang.gen.ts.AngularDerivedType
import ee.lang.gen.ts.AngularFileFormat
import ee.lang.gen.ts.TsContextBuilder

open class DesignAngularContextFactory : DesignTsContextFactory() {
    override fun contextBuilder(derived: DerivedController): TsContextBuilder<StructureUnitI<*>> {
        return super.contextBuilder(derived)
    }

    override fun registerForImplOnly(derived: DerivedController) {
        super.registerForImplOnly(derived)
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ViewComponent,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}${AngularFileFormat.EntityViewComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ListComponent,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}${AngularFileFormat.EntityListComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.FormComponent,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}${AngularFileFormat.EntityFormComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.EnumComponent,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}${AngularFileFormat.EnumComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.BasicComponent,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}${AngularFileFormat.BasicComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.Module,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}${AngularFileFormat.Module}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.RoutingModules,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}${AngularFileFormat.RoutingModule}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.OwnModule,
            { "${this.name().capitalize()}${AngularDerivedType.Module}" }, { "${this.namespace()}${it}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.OwnRoutingModules,
            { "${this.name().capitalize()}${AngularDerivedType.RoutingModules}" }, { "${this.namespace()}${it}" }, isNotPartOfNativeTypes))

        derived.registerKinds(
            listOf(AngularDerivedType.Enum, AngularDerivedType.Component, AngularDerivedType.DataService, AngularDerivedType.ViewService, AngularDerivedType.OwnDataService),
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

package ee.design.gen.ts

import ee.design.CommandI
import ee.design.CompI
import ee.design.EventI
import ee.lang.*
import ee.lang.gen.ts.*

open class DesignTsContextFactory(alwaysImportTypes: Boolean = false) : LangTsContextFactory(alwaysImportTypes) {
    override fun contextBuilder(
        derived: DerivedController, buildNamespace: StructureUnitI<*>.()->String): TsContextBuilder<StructureUnitI<*>> {

        return TsContextBuilder(CONTEXT_TYPE_SCRIPT, macroController){
            val structureUnit = this
            val compOrStructureUnit = this.findThisOrParentUnsafe(CompI::class.java) ?: structureUnit
            TsContext(
                alwaysImportTypes = alwaysImportTypes,
                moduleFolder = "${compOrStructureUnit.artifact()}/${compOrStructureUnit.artifact()}_ng",
                namespace = structureUnit.buildNamespace(), derivedController = derived,
                macroController = macroController)
        }
    }

    override fun registerForImplOnly(derived: DerivedController) {
        super.registerForImplOnly(derived)

        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ApiBase,
            { "${this.name().capitalize()}" }, { "${this.namespace()}${AngularDerivedType.ApiBase}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ViewComponent,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}${AngularFileFormat.EntityViewComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularOwnComponent.OwnViewComponent,
            { "${this.name().capitalize()}${AngularDerivedType.ViewComponent}" }, { "${this.namespace()}${it}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ModuleViewComponent,
            { "${this.name().capitalize()}${AngularDerivedType.ViewComponent}" }, { "${this.namespace()}${AngularFileFormat.ModuleViewComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularOwnComponent.OwnModuleViewComponent,
            { "${this.name().capitalize()}${AngularDerivedType.ViewComponent}" }, { "${this.namespace()}${it}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ListComponent,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}${AngularFileFormat.EntityListComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularOwnComponent.OwnListComponent,
            { "${this.name().capitalize()}${AngularDerivedType.ListComponent}" }, { "${this.namespace()}${it}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.FormComponent,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}${AngularFileFormat.EntityFormComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularOwnComponent.OwnFormComponent,
            { "${this.name().capitalize()}${AngularDerivedType.FormComponent}" }, { "${this.namespace()}${it}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.EnumComponent,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}${AngularFileFormat.EnumComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.BasicComponent,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}${AngularFileFormat.BasicComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.Module,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}${AngularFileFormat.Module}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.RoutingModules,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}${AngularFileFormat.RoutingModule}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.DataService,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}${AngularFileFormat.DataService}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ViewService,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}${AngularFileFormat.ModuleViewService}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularOwnComponent.OwnViewService,
            { "${this.name().capitalize()}${AngularDerivedType.ViewService}" }, { "${this.namespace()}${it}" }, isNotPartOfNativeTypes))

        derived.registerKinds(
            listOf(AngularDerivedType.Enum, AngularDerivedType.Component),
            { "${this.name().capitalize()}${it}" },
            isNotPartOfNativeTypes)
    }

    override fun buildName(item: ItemI<*>, kind: String): String {
        return if (item is CommandI<*>) {
            buildNameForCommand(item, kind)
        } else if (item is EventI<*>) {
            buildNameForEvent(item, kind)
        } else {
            super.buildName(item, kind)
        }
    }

    protected open fun buildNameForCommand(item: CommandI<*>, kind: String) = item.dataTypeNameAndParentName().capitalize()
    protected open fun buildNameForEvent(item: EventI<*>, kind: String) = item.dataTypeParentNameAndName().capitalize()
}

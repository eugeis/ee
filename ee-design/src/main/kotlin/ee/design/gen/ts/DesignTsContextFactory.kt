package ee.design.gen.ts

import ee.design.CommandI
import ee.design.CompI
import ee.design.EventI
import ee.lang.*
import ee.lang.gen.ts.*
import java.util.*

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

        derived.registerKind(LangDerivedKind.WithParentAsName, { if (this.parent().name().equals(this.name(), true)) { this.name() } else {"${this.parent().name()}${this.name()}"} }, isNotPartOfNativeTypes)

        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ApiBase,
            {
                this.name()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }, { "${this.namespace()}${AngularDerivedType.ApiBase}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ViewComponent,
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${it}" }, { "${this.namespace()}${AngularFileFormat.EntityViewComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ModuleViewComponent,
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${AngularDerivedType.ModuleViewComponent}" }, { "${this.namespace()}${AngularFileFormat.ModuleViewComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ListComponent,
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${it}" }, { "${this.namespace()}${AngularFileFormat.EntityListComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.FormComponent,
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${it}" }, { "${this.namespace()}${AngularFileFormat.EntityFormComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.EntityViewComponent,
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${it}" }, { "${this.namespace()}${AngularFileFormat.EntityViewComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.EntityListComponent,
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${it}" }, { "${this.namespace()}${AngularFileFormat.EntityListComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.EntityFormComponent,
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${it}" }, { "${this.namespace()}${AngularFileFormat.EntityFormComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ValueViewComponent,
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${it}" }, { "${this.namespace()}${AngularFileFormat.EntityViewComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ValueListComponent,
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${it}" }, { "${this.namespace()}${AngularFileFormat.EntityListComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ValueFormComponent,
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${it}" }, { "${this.namespace()}${AngularFileFormat.EntityFormComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.EnumComponent,
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${it}" }, { "${this.namespace()}${AngularFileFormat.EnumComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.BasicComponent,
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${it}" }, { "${this.namespace()}${AngularFileFormat.BasicComponent}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.Module,
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${it}" }, { "${this.namespace()}${AngularFileFormat.Module}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.RoutingModules,
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${it}" }, { "${this.namespace()}${AngularFileFormat.RoutingModule}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.DataService,
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${it}" }, { "${this.namespace()}${AngularFileFormat.DataService}" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ViewService,
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${it}" }, { "${this.namespace()}${AngularFileFormat.ModuleViewService}" }, isNotPartOfNativeTypes))

        derived.registerKinds(
            listOf(AngularDerivedType.Enum, AngularDerivedType.Component),
            { "${this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${it}" },
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

    protected open fun buildNameForCommand(item: CommandI<*>, kind: String) = item.dataTypeNameAndParentName()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    protected open fun buildNameForEvent(item: EventI<*>, kind: String) = item.dataTypeParentNameAndName()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

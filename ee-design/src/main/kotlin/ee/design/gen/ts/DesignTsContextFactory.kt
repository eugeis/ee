package ee.design.gen.ts

import ee.design.CommandI
import ee.design.CompI
import ee.design.EventI
import ee.lang.*
import ee.lang.gen.ts.LangTsContextFactory
import ee.lang.gen.ts.TsContext
import ee.lang.gen.ts.TsContextBuilder
import kotlin.reflect.typeOf

open class DesignTsContextFactory : LangTsContextFactory() {
    override fun contextBuilder(derived: DerivedController): TsContextBuilder<StructureUnitI<*>> {
        return TsContextBuilder(CONTEXT_TYPE_SCRIPT, macroController){
            val structureUnit = this
            val compOrStructureUnit = this.findThisOrParentUnsafe(CompI::class.java) ?: structureUnit
            TsContext(moduleFolder = "${compOrStructureUnit.artifact()}/${compOrStructureUnit.artifact()}_ng",
                namespace = structureUnit.namespace().toLowerCase(), derivedController = derived,
                macroController = macroController)
        }
    }

    override fun registerForImplOnly(derived: DerivedController) {
        super.registerForImplOnly(derived)

        derived.register(NameAndNamespaceTransformers(AngularDerivedType.DataService,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}-data.service" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.ViewService,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}-module-view.service" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.Module,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}-model.module" }, isNotPartOfNativeTypes))
        derived.register(NameAndNamespaceTransformers(AngularDerivedType.RoutingModules,
            { "${this.name().capitalize()}${it}" }, { "${this.namespace()}-routing.module" }, isNotPartOfNativeTypes))

        derived.registerKinds(
            listOf(AngularDerivedType.ViewComponent, AngularDerivedType.ListComponent,
                AngularDerivedType.FormComponent, AngularDerivedType.EnumComponent, AngularDerivedType.BasicComponent,
                AngularDerivedType.Enum, AngularDerivedType.Component),
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

    protected open fun buildNameForCommand(item: CommandI<*>, kind: String) = item.nameAndParentName().capitalize()
    protected open fun buildNameForEvent(item: EventI<*>, kind: String) = item.parentNameAndName().capitalize()
}

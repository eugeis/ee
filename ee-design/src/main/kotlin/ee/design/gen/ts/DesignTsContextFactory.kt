package ee.design.gen.ts

import ee.design.CommandI
import ee.design.CompI
import ee.design.EventI
import ee.lang.*
import ee.lang.gen.ts.LangTsContextFactory
import ee.lang.gen.ts.TsContext
import ee.lang.gen.ts.TsContextBuilder

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

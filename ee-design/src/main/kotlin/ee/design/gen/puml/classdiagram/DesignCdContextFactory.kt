package ee.design.gen.puml.classdiagram

import ee.design.CommandI
import ee.design.CompI
import ee.design.EventI
import ee.lang.*
import ee.lang.gen.puml.classdiagram.CdContext
import ee.lang.gen.puml.classdiagram.CdContextBuilder
import ee.lang.gen.puml.classdiagram.LangCdContextFactory

open class DesignCdContextFactory(alwaysImportTypes: Boolean = false) : LangCdContextFactory(alwaysImportTypes) {
    override fun contextBuilder(
        derived: DerivedController, buildNamespace: StructureUnitI<*>.()->String): CdContextBuilder<StructureUnitI<*>> {

        return CdContextBuilder(CONTEXT_CLASS_DIAGRAM, macroController){
            val structureUnit = this
            val compOrStructureUnit = this.findThisOrParentUnsafe(CompI::class.java) ?: structureUnit
            CdContext(
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

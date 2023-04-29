package ee.design.gen.doc

import ee.design.*
import ee.lang.*
import ee.lang.gen.doc.LangMarkdownContextFactory
import ee.lang.gen.doc.MkContext
import ee.lang.gen.doc.MkContextBuilder
import ee.lang.gen.puml.classdiagram.LangCdContextFactory

open class DesignDocContextFactory(targetAsSingleModule: Boolean = false): LangMarkdownContextFactory(targetAsSingleModule) {
    override fun contextBuilder(
        derived: DerivedController, buildNamespace: StructureUnitI<*>.()->String): MkContextBuilder<StructureUnitI<*>> {

        return MkContextBuilder(LangCdContextFactory.CONTEXT_CLASS_DIAGRAM, macroController){
            val structureUnit = this
            val compOrStructureUnit = this.findThisOrParentUnsafe(CompI::class.java) ?: structureUnit
            MkContext(
                alwaysImportTypes = targetAsSingleModule,
                moduleFolder = compOrStructureUnit.artifact(),
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

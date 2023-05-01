package ee.design.gen.doc

import ee.design.*
import ee.lang.*
import ee.lang.gen.doc.LangMarkdownContextFactory
import ee.lang.gen.doc.DocContext
import ee.lang.gen.doc.DocContextBuilder
import ee.lang.gen.puml.classdiagram.LangCdContextFactory
import java.util.*

open class DesignDocContextFactory(targetAsSingleModule: Boolean = false): LangMarkdownContextFactory(targetAsSingleModule) {
    override fun contextBuilder(
        derived: DerivedController, buildNamespace: StructureUnitI<*>.()->String): DocContextBuilder<StructureUnitI<*>> {

        return DocContextBuilder(LangCdContextFactory.CONTEXT_CLASS_DIAGRAM, macroController){
            val structureUnit = this
            val compOrStructureUnit = this.findThisOrParentUnsafe(CompI::class.java) ?: structureUnit
            DocContext(
                alwaysImportTypes = targetAsSingleModule,
                moduleFolder = compOrStructureUnit.artifact(),
                namespace = structureUnit.buildNamespace(), derivedController = derived,
                macroController = macroController)
        }
    }

    override fun buildName(item: ItemI<*>, kind: String): String {
        return when(item) {
            is CommandI<*> -> buildNameForCommand(item, kind)
            is EventI<*> -> buildNameForEvent(item, kind)
            else -> super.buildName(item, kind)
        }
    }

    protected open fun buildNameForCommand(item: CommandI<*>, kind: String) = item.dataTypeNameAndParentName()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    protected open fun buildNameForEvent(item: EventI<*>, kind: String) = item.dataTypeParentNameAndName()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

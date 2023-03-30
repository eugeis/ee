package ee.design.gen.go

import ee.design.CommandI
import ee.design.CompI
import ee.design.EventI
import ee.lang.*
import ee.lang.gen.go.GoContext
import ee.lang.gen.go.GoContextBuilder
import ee.lang.gen.go.LangGoContextFactory

open class DesignGoContextFactory(targetAsSingleModule: Boolean = true) : LangGoContextFactory(targetAsSingleModule) {
    override fun contextBuilder(
        derived: DerivedController, buildNamespace: StructureUnitI<*>.()->String): GoContextBuilder<StructureUnitI<*>> {
        return GoContextBuilder(CONTEXT_GO, macroController) {
            GoContext(namespace = namespace().toLowerCase(), moduleFolder = computeModuleFolder(),
                    derivedController = derived, macroController = macroController)
        }
    }

    override fun StructureUnitI<*>.computeModuleFolder(): String {
        val compOrStructureUnit = findThisOrParentUnsafe(CompI::class.java) ?: this
        return if (targetAsSingleModule) {
            "${compOrStructureUnit.artifact()}/go"
        } else {
            "${compOrStructureUnit.artifact()}/${artifact()}-go"
        }
    }

    override fun buildName(item: ItemI<*>, kind: String): String {
        return when (item) {
            is CommandI<*> -> buildNameForCommand(item, kind)
            is EventI<*> -> buildNameForEvent(item, kind)
            else -> super.buildName(item, kind)
        }
    }

    protected open fun buildNameForCommand(item: CommandI<*>, kind: String) = item.dataTypeNameAndParentName().capitalize()
    protected open fun buildNameForEvent(item: EventI<*>, kind: String) = item.dataTypeParentNameAndName().capitalize()
}

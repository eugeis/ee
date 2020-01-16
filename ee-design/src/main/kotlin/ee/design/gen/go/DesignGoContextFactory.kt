package ee.design.gen.go

import ee.design.CommandI
import ee.design.CompI
import ee.design.EventI
import ee.lang.*
import ee.lang.gen.KotlinContext
import ee.lang.gen.KotlinContextBuilder
import ee.lang.gen.common.LangCommonContextFactory
import ee.lang.gen.go.GoContext
import ee.lang.gen.go.GoContextBuilder
import ee.lang.gen.go.LangGoContextFactory

open class DesignGoContextFactory(singleModule: Boolean = true) : LangGoContextFactory(singleModule) {
    override fun contextBuilder(derived: DerivedController): GoContextBuilder<StructureUnitI<*>> {
        return GoContextBuilder(CONTEXT_GO, macroController) {
            GoContext(namespace = namespace().toLowerCase(),
                    moduleFolder = computeModuleFolder(),
                    derivedController = derived,
                    macroController = macroController)
        }
    }

    override fun buildName(item: ItemI<*>, kind: String): String {
        return when (item) {
            is CommandI<*> -> buildNameForCommand(item, kind)
            is EventI<*> -> buildNameForEvent(item, kind)
            else -> super.buildName(item, kind)
        }
    }

    private fun StructureUnitI<*>.computeModuleFolder(): String {
        val compOrStructureUnit = this.findThisOrParentUnsafe(CompI::class.java) ?: this
        return if (singleModule) {
            "${compOrStructureUnit.artifact()}/go"
        } else {
            "${compOrStructureUnit.artifact()}/${artifact()}-go"
        }
    }

    protected open fun buildNameForCommand(item: CommandI<*>, kind: String) = item.nameAndParentName().capitalize()
    protected open fun buildNameForEvent(item: EventI<*>, kind: String) = item.parentNameAndName().capitalize()
}

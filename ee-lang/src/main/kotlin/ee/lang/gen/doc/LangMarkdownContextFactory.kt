package ee.lang.gen.doc

import ee.lang.ContextBuilder
import ee.lang.DerivedController
import ee.lang.MacroController
import ee.lang.StructureUnitI
import ee.lang.gen.common.LangCommonContextFactory

open class MkContextBuilder<M>(name: String, macroController: MacroController, builder: M.() -> MkContext)
    : ContextBuilder<M>(name, macroController, builder)

open class LangMarkdownContextFactory(protected val alwaysImportTypes: Boolean = false) : LangCommonContextFactory() {

    open fun buildForImplOnly(): MkContextBuilder<StructureUnitI<*>> {
        return buildForImplOnly("")
    }
    open fun buildForImplOnly(suffixNamespace: String): MkContextBuilder<StructureUnitI<*>> {
        val derivedController = DerivedController()
        registerForImplOnly(derivedController)
        return contextBuilder(derivedController) { "${namespace().toLowerCase()}$suffixNamespace" }
    }

    override fun contextBuilder(
        derived: DerivedController, buildNamespace: StructureUnitI<*>.()->String): MkContextBuilder<StructureUnitI<*>> {

        return MkContextBuilder(CONTEXT_COMMON, macroController) {
            val structureUnit = this
            MkContext(alwaysImportTypes = alwaysImportTypes,
                namespace = structureUnit.buildNamespace(), moduleFolder = "src-gen/$buildNamespace/doc",
                derivedController = derived, macroController = macroController)
        }
    }
}

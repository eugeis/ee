package ee.lang.gen.ts

import ee.lang.ContextBuilder
import ee.lang.DerivedController
import ee.lang.MacroController
import ee.lang.StructureUnitI
import ee.lang.gen.common.LangCommonContextFactory
import ee.lang.gen.go.GoContext

open class TsContextBuilder<M>(name: String, macroController: MacroController, builder: M.() -> TsContext)
    : ContextBuilder<M>(name, macroController, builder)

open class LangTsContextFactory(protected val alwaysImportTypes: Boolean = false) : LangCommonContextFactory() {

    open fun buildForImplOnly(): TsContextBuilder<StructureUnitI<*>> {
        return buildForImplOnly("")
    }
    open fun buildForImplOnly(suffixNamespace: String): TsContextBuilder<StructureUnitI<*>> {
        val derivedController = DerivedController()
        registerForImplOnly(derivedController)
        return contextBuilder(derivedController) { "${namespace().toLowerCase()}$suffixNamespace" }
    }

    override fun contextBuilder(
        derived: DerivedController, buildNamespace: StructureUnitI<*>.()->String): TsContextBuilder<StructureUnitI<*>> {

        return TsContextBuilder(CONTEXT_TYPE_SCRIPT, macroController) {
            val structureUnit = this
            TsContext(alwaysImportTypes = alwaysImportTypes,
                namespace = structureUnit.buildNamespace(), moduleFolder = structureUnit.artifact(),
                    derivedController = derived, macroController = macroController)
        }
    }

    companion object {
        const val CONTEXT_TYPE_SCRIPT = "TypeScript"
    }
}
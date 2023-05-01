package ee.lang.gen.puml.classdiagram

import ee.lang.ContextBuilder
import ee.lang.DerivedController
import ee.lang.MacroController
import ee.lang.StructureUnitI
import ee.lang.gen.common.LangCommonContextFactory
import java.util.*

open class CdContextBuilder<M>(name: String, macroController: MacroController, builder: M.() -> CdContext)
    : ContextBuilder<M>(name, macroController, builder)

open class LangCdContextFactory(protected val alwaysImportTypes: Boolean = false) : LangCommonContextFactory() {

    open fun buildForImplOnly(): CdContextBuilder<StructureUnitI<*>> {
        return buildForImplOnly("")
    }
    open fun buildForImplOnly(suffixNamespace: String): CdContextBuilder<StructureUnitI<*>> {
        val derivedController = DerivedController()
        registerForImplOnly(derivedController)
        return contextBuilder(derivedController) { "${namespace().lowercase(Locale.getDefault())}$suffixNamespace" }
    }

    override fun contextBuilder(
        derived: DerivedController, buildNamespace: StructureUnitI<*>.()->String): CdContextBuilder<StructureUnitI<*>> {

        return CdContextBuilder(CONTEXT_CLASS_DIAGRAM, macroController) {
            val structureUnit = this
            CdContext(alwaysImportTypes = alwaysImportTypes,
                namespace = structureUnit.buildNamespace(), moduleFolder = structureUnit.artifact(),
                derivedController = derived, macroController = macroController)
        }
    }

    companion object {
        const val CONTEXT_CLASS_DIAGRAM = "ClassDiagram"
    }
}

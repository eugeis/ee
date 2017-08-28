package ee.lang.gen.ts

import ee.lang.DerivedController
import ee.lang.StructureUnitI
import ee.lang.gen.common.LangCommonContextFactory

open class LangTsContextFactory : LangCommonContextFactory() {

    open fun buildForImplOnly(): StructureUnitI.() -> TsContext {
        val derivedController = DerivedController()
        registerForImplOnly(derivedController)
        return contextBuilder(derivedController)
    }

    override fun contextBuilder(derived: DerivedController): StructureUnitI.() -> TsContext {
        return {
            val structureUnit = this
            TsContext(moduleFolder = structureUnit.artifact(), namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = derived, macroController = macroController
            )
        }
    }
}
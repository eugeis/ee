package ee.lang.gen.ts

import ee.lang.DerivedController
import ee.lang.StructureUnitIB
import ee.lang.gen.common.LangCommonContextFactory

open class LangTsContextFactory : LangCommonContextFactory() {

    open fun buildForImplOnly(): StructureUnitIB<*>.() -> TsContext {
        val derivedController = DerivedController()
        registerForImplOnly(derivedController)
        return contextBuilder(derivedController)
    }

    override fun contextBuilder(derived: DerivedController): StructureUnitIB<*>.() -> TsContext {
        return {
            val structureUnit = this
            TsContext(moduleFolder = structureUnit.artifact(), namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = derived, macroController = macroController
            )
        }
    }
}
package ee.lang.gen.go

import ee.lang.DerivedController
import ee.lang.StructureUnitI

open class LangGoContextFactory : LangCommonContextFactory() {

    open fun buildForImplOnly(): StructureUnitI.() -> GoContext {
        val derivedController = DerivedController()
        registerForImplOnly(derivedController)
        return contextBuilder(derivedController)
    }

    override fun contextBuilder(derived: DerivedController): StructureUnitI.() -> GoContext {
        return {
            val structureUnit = this
            GoContext(moduleFolder = structureUnit.artifact(),
                    namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = derived,
                    macroController = macroController
            )
        }
    }
}
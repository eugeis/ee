package ee.lang.gen.go

import ee.lang.*
import ee.lang.gen.java.j
import ee.lang.gen.kt.k

open class LangGoContextFactory {
    val isNotPartOfNativeTypes: ItemI.() -> Boolean = { n != parent() && j != parent() && k != parent() }
    val macroController = MacroController()

    open fun buildForImplOnly(): StructureUnitI.() -> GoContext {
        val derivedController = DerivedController()
        registerForImplOnly(derivedController)
        return contextBuilder(derivedController)
    }

    protected open fun registerForImplOnly(derived: DerivedController) {
        derived.registerKinds(listOf(LangDerivedKind.API, LangDerivedKind.IMPL), isNotPartOfNativeTypes, { buildName() })
    }


    protected open fun contextBuilder(derived: DerivedController): StructureUnitI.() -> GoContext {
        return {
            val structureUnit = this
            GoContext(moduleFolder = structureUnit.artifact(),
                    namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = derived,
                    macroController = macroController
            )
        }
    }

    protected open fun ItemI.buildName(): String = name()
}
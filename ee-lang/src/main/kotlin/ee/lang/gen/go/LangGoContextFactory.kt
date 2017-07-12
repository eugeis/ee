package ee.lang.gen.go

import ee.lang.*
import ee.lang.gen.java.j
import ee.lang.gen.kt.k

open class LangGoContextFactory {
    val isNotPartOfNativeTypes: ItemI.() -> Boolean = { n != parent() && j != parent() && k != parent() }


    open fun buildForImplOnly(): StructureUnitI.() -> GoContext {
        return contextBuilder(registerForImplOnly(DerivedController()))
    }

    protected open fun registerForImplOnly(derived: DerivedController): DerivedController {
        derived.registerKinds(listOf(LangDerivedKind.API, LangDerivedKind.IMPL), isNotPartOfNativeTypes, { buildName() })
        return derived
    }


    protected open fun contextBuilder(controller: DerivedController): StructureUnitI.() -> GoContext {
        return {
            val structureUnit = this
            GoContext(moduleFolder = structureUnit.artifact(),
                    namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = controller
            )
        }
    }

    protected open fun ItemI.buildName(): String = name()
}
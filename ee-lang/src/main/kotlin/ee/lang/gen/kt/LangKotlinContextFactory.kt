package ee.lang.gen.kt

import ee.lang.*
import ee.lang.gen.KotlinContext
import ee.lang.gen.java.j

open class LangKotlinContextFactory {
    private val isNotPartOfNativeTypes: ItemI.() -> Boolean = { n != parent() && j != parent() && k != parent() }

    open fun buildForImplOnly(): StructureUnitI.() -> KotlinContext {
        val controller = DerivedController()

        controller.registerKinds(listOf(LangDerivedKind.API, LangDerivedKind.IMPL), isNotPartOfNativeTypes, { "${name()}" })

        return contextBuilder(controller)
    }

    open fun buildForDslBuilder(): StructureUnitI.() -> KotlinContext {
        val controller = DerivedController()

        controller.registerKind(LangDerivedKind.API, isNotPartOfNativeTypes, { "${name()}I" })
        controller.registerKind(LangDerivedKind.IMPL, isNotPartOfNativeTypes, { name() })

        return contextBuilder(controller)
    }

    protected open fun contextBuilder(controller: DerivedController): StructureUnitI.() -> KotlinContext {
        return {
            val structureUnit = this
            KotlinContext(moduleFolder = structureUnit.artifact(),
                    namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = controller
            )
        }
    }
}
package ee.lang.gen.kt

import ee.lang.*
import ee.lang.gen.KotlinContext
import ee.lang.gen.java.j

open class LangKotlinContextFactory {
    private val isNotPartOfNativeTypes: ItemIB<*>.() -> Boolean = { n != parent() && j != parent() && k != parent() }

    open fun buildForImplOnly(): StructureUnitIB<*>.() -> KotlinContext {
        val controller = DerivedController()

        controller.registerKinds(listOf(LangDerivedKind.API, LangDerivedKind.IMPL), { name() }, isNotPartOfNativeTypes)

        return contextBuilder(controller)
    }

    open fun buildForDslBuilder(): StructureUnitIB<*>.() -> KotlinContext {
        val controller = DerivedController()

        controller.registerKind(LangDerivedKind.API, { "${name()}IB" }, isNotPartOfNativeTypes)
        controller.registerKind(LangDerivedKind.IMPL, { name() }, isNotPartOfNativeTypes)
        controller.registerKind(LangDerivedKind.MANUAL, { "${name()}B" }, isNotPartOfNativeTypes)

        return contextBuilder(controller)
    }

    protected open fun contextBuilder(controller: DerivedController): StructureUnitIB<*>.() -> KotlinContext {
        return {
            val structureUnit = this
            KotlinContext(moduleFolder = structureUnit.artifact(),
                    namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = controller
            )
        }
    }
}
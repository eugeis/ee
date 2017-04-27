package ee.lang.gen.kt

import ee.lang.*
import ee.lang.gen.KotlinContext
import ee.lang.gen.java.j
import ee.lang.gen.kt.k

open class LangKotlinContextFactory {
    private val isNotPartOfNativeTypes: ItemI.() -> Boolean = { n != parent() && j != parent() && k != parent() }

    open fun buildForImplOnly(): StructureUnitI.() -> KotlinContext {
        val controller = DerivedController()

        controller.registerKinds(listOf(DerivedNames.API.name, DerivedNames.IMPL.name),
                isNotPartOfNativeTypes, { "${name()}" })
        controller.registerKinds(listOf(DerivedNames.API_BASE.name, DerivedNames.IMPL_BASE.name),
                isNotPartOfNativeTypes, { "${name()}Base" })
        controller.registerKind(DerivedNames.COMPOSITE.name, isNotPartOfNativeTypes, { "${name()}s" })

        return contextBuilder(controller)
    }

    open fun buildForApiAndImpl(): StructureUnitI.() -> KotlinContext {
        val controller = DerivedController()

        controller.registerKind(DerivedNames.API.name, isNotPartOfNativeTypes, { "${name()}" })
        controller.registerKind(DerivedNames.API_BASE.name, isNotPartOfNativeTypes, { "${name()}Base" })
        controller.registerKind(DerivedNames.IMPL.name, isNotPartOfNativeTypes, { "${name()}Impl" })
        controller.registerKind(DerivedNames.IMPL_BASE.name, isNotPartOfNativeTypes, { "${name()}ImplBase" })
        controller.registerKind(DerivedNames.COMPOSITE.name, isNotPartOfNativeTypes, { "${name()}s" })

        return contextBuilder(controller)
    }

    open fun buildForDslBuilder(): StructureUnitI.() -> KotlinContext {
        val controller = DerivedController()

        controller.registerKind(DerivedNames.API.name, isNotPartOfNativeTypes, { "${name()}I" })
        controller.registerKind(DerivedNames.API_BASE.name, isNotPartOfNativeTypes, { "${name()}IfcBase" })
        controller.registerKind(DerivedNames.IMPL.name, isNotPartOfNativeTypes, { name() })
        controller.registerKind(DerivedNames.IMPL_BASE.name, isNotPartOfNativeTypes, { "${name()}Base" })
        controller.registerKind(DerivedNames.COMPOSITE.name, isNotPartOfNativeTypes, { "${name()}s" })

        return contextBuilder(controller)
    }

    protected open fun contextBuilder(controller: DerivedController): StructureUnitI.() -> KotlinContext {
        return {
            val structureUnit = this
            KotlinContext(moduleFolder = structureUnit.artifact(),
                    namespace = structureUnit.namespace(),
                    derivedController = controller
            )
        }
    }
}
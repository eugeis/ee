package ee.lang.gen.go

import ee.lang.*
import ee.lang.gen.java.j
import ee.lang.gen.kt.k

open class LangGoContextFactory {
    val isNotPartOfNativeTypes: ItemI.() -> Boolean = { n != parent() && j != parent() && k != parent() }


    open fun buildForImplOnly(): StructureUnitI.() -> GoContext {
        val controller = DerivedController()

        controller.registerKinds(listOf(DerivedNames.API.name, DerivedNames.IMPL.name),
                isNotPartOfNativeTypes, { buildName() })
        controller.registerKinds(listOf(DerivedNames.API_BASE.name, DerivedNames.IMPL_BASE.name),
                isNotPartOfNativeTypes, { "${buildName()}Base" })
        controller.registerKind(DerivedNames.COMPOSITE.name, isNotPartOfNativeTypes, { "${buildName()}s" })

        return contextBuilder(controller)
    }

    open fun buildForApiAndImpl(): StructureUnitI.() -> GoContext {
        val controller = DerivedController()

        controller.registerKind(DerivedNames.API.name, isNotPartOfNativeTypes, { buildName() })
        controller.registerKind(DerivedNames.API_BASE.name, isNotPartOfNativeTypes, { "${buildName()}Base" })
        controller.registerKind(DerivedNames.IMPL.name, isNotPartOfNativeTypes, { "${buildName()}Impl" })
        controller.registerKind(DerivedNames.IMPL_BASE.name, isNotPartOfNativeTypes, { "${buildName()}ImplBase" })

        return contextBuilder(controller)
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
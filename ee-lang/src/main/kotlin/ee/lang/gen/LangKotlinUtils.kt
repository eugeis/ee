package ee.lang.gen

import ee.lang.*
import ee.lang.gen.java.j

object k : StructureUnit({ name("Kotlin") }) {
    object Core : StructureUnit() {
        object List : ExternalType() {
            val T = G()
        }

        object MutableList : ExternalType() {
            val T = G()
        }

        object MutableMap : ExternalType() {
            val K = G()
            val V = G()
        }
    }
}

object KotlinContextFactory {
    private val isNotPartOfNativeTypes: ItemI.() -> Boolean = { n != parent() && j != parent() && k != parent() }

    fun buildForImplOnly(): StructureUnitI.() -> KotlinContext {
        val controller = DerivedController()

        controller.registerKinds(listOf(DerivedNames.API.name, DerivedNames.IMPL.name),
                isNotPartOfNativeTypes, { "${name()}" })
        controller.registerKinds(listOf(DerivedNames.API_BASE.name, DerivedNames.IMPL_BASE.name),
                isNotPartOfNativeTypes, { "${name()}Base" })
        controller.registerKind(DerivedNames.COMPOSITE.name, isNotPartOfNativeTypes, { "${name()}s" })

        return contextBuilder(controller)
    }

    fun buildForApiAndImpl(): StructureUnitI.() -> KotlinContext {
        val controller = DerivedController()

        controller.registerKind(DerivedNames.API.name, isNotPartOfNativeTypes, { "${name()}" })
        controller.registerKind(DerivedNames.API_BASE.name, isNotPartOfNativeTypes, { "${name()}Base" })
        controller.registerKind(DerivedNames.IMPL.name, isNotPartOfNativeTypes, { "${name()}Impl" })
        controller.registerKind(DerivedNames.IMPL_BASE.name, isNotPartOfNativeTypes, { "${name()}ImplBase" })
        controller.registerKind(DerivedNames.COMPOSITE.name, isNotPartOfNativeTypes, { "${name()}s" })

        return contextBuilder(controller)
    }

    fun buildForDslBuilder(): StructureUnitI.() -> KotlinContext {
        val controller = DerivedController()

        controller.registerKind(DerivedNames.API.name, isNotPartOfNativeTypes, { "${name()}I" })
        controller.registerKind(DerivedNames.API_BASE.name, isNotPartOfNativeTypes, { "${name()}IfcBase" })
        controller.registerKind(DerivedNames.IMPL.name, isNotPartOfNativeTypes, { name() })
        controller.registerKind(DerivedNames.IMPL_BASE.name, isNotPartOfNativeTypes, { "${name()}Base" })
        controller.registerKind(DerivedNames.COMPOSITE.name, isNotPartOfNativeTypes, { "${name()}s" })

        return contextBuilder(controller)
    }

    private fun contextBuilder(controller: DerivedController): StructureUnitI.() -> KotlinContext {
        return {
            val structureUnit = this
            KotlinContext(moduleFolder = structureUnit.artifact(),
                    namespace = structureUnit.namespace(),
                    derivedController = controller
            )
        }
    }
}


fun <T : CompositeI> T.prepareForKotlinGeneration(): T {
    initObjectTreesForKotlin()

    //declare as 'base' all compilation units with non implemented operations.
    declareAsBaseWithNonImplementedOperation()

    prepareAttributesOfEnums()

    //define constructor with all parameters.
    defineConstructorAllForNonConstructors()
    return this
}

fun <T : CompositeI> T.initObjectTreesForKotlin(): T {
    val ret = initObjectTrees()
    initObjectTree()
    return ret
}
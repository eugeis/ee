package ee.lang.gen.kt

import ee.lang.*
import ee.lang.gen.java.j
import ee.lang.gen.KotlinContext

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

    fun buildForImplOnly(namespace: String): KotlinContext {
        var ret = KotlinContext(namespace = namespace)
        val controller = ret.derivedController

        controller.registerKinds(listOf(DerivedNames.API.name, DerivedNames.IMPL.name),
                isNotPartOfNativeTypes, { "${name()}" })
        controller.registerKinds(listOf(DerivedNames.API_BASE.name, DerivedNames.IMPL_BASE.name),
                isNotPartOfNativeTypes, { "${name()}Base" })
        controller.registerKind(DerivedNames.COMPOSITE.name, isNotPartOfNativeTypes, { "${name()}s" })

        return ret
    }

    fun buildForApiAndImpl(namespace: String): KotlinContext {
        var ret = KotlinContext(namespace = namespace)
        val controller = ret.derivedController

        controller.registerKind(DerivedNames.API.name, isNotPartOfNativeTypes, { "${name()}" })
        controller.registerKind(DerivedNames.API_BASE.name, isNotPartOfNativeTypes, { "${name()}Base" })
        controller.registerKind(DerivedNames.IMPL.name, isNotPartOfNativeTypes, { "${name()}Impl" })
        controller.registerKind(DerivedNames.IMPL_BASE.name, isNotPartOfNativeTypes, { "${name()}ImplBase" })
        controller.registerKind(DerivedNames.COMPOSITE.name, isNotPartOfNativeTypes, { "${name()}s" })

        return ret
    }

    fun buildForDslBuilder(namespace: String): KotlinContext {
        var ret = KotlinContext(namespace = namespace)
        val controller = ret.derivedController

        controller.registerKind(DerivedNames.API.name, isNotPartOfNativeTypes, { "${name()}I" })
        controller.registerKind(DerivedNames.API_BASE.name, isNotPartOfNativeTypes, { "${name()}IfcBase" })
        controller.registerKind(DerivedNames.IMPL.name, isNotPartOfNativeTypes, { name() })
        controller.registerKind(DerivedNames.IMPL_BASE.name, isNotPartOfNativeTypes, { "${name()}Base" })
        controller.registerKind(DerivedNames.COMPOSITE.name, isNotPartOfNativeTypes, { "${name()}s" })

        return ret
    }
}


fun <T : CompositeI> T.prepareForKotlinGeneration() {
    initObjectTreesForKotlin()

    //declare as 'base' all compilation units with non implemented operations.
    declareAsBaseWithNonImplementedOperation()

    prepareAttributesOfEnums()

    //define constructor with all parameters.
    defineConstructorAllForNonConstructors()
}

fun <T : CompositeI> T.initObjectTreesForKotlin(): T {
    val ret = initObjectTrees()
    j.initObjectTree()
    k.initObjectTree()
    return ret
}
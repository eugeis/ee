package ee.lang.gen.kt

import ee.lang.*
import ee.lang.gen.java.j
import ee.lang.gen.java.jackson
import ee.lang.gen.java.junit

object k : StructureUnit({ name("Kotlin") }) {
    object core : StructureUnit() {
        object Collection : ExternalType() {
            val T = G()
        }

        object MutableCollection : ExternalType() {
            val T = G()
        }

        object List : ExternalType() {
            val T = G()
        }

        object MutableList : ExternalType() {
            val T = G()
        }

        object Map : ExternalType() {
            val K = G()
            val V = G()
        }

        object MutableMap : ExternalType() {
            val K = G()
            val V = G()
        }

        object Pair : ExternalType() {
            val K = G()
            val V = G()
        }
    }

    object test : StructureUnit({namespace("kotlin.test")}) {
        val assertSame = ExternalType()
        val assertTrue = ExternalType()
        val assertFalse = ExternalType()
        val assertEquals = ExternalType()
    }
}

fun <T : StructureUnitI<*>> T.prepareForKotlinGeneration(): T {
    j.initObjectTree()
    jackson.initObjectTree()
    junit.initObjectTree()
    k.initObjectTree()

    initObjectTrees()

    //declare as 'isBase' all compilation units with non implemented operations.
    declareAsBaseWithNonImplementedOperation()

    prepareAttributesOfEnums()

    //define constructor with all parameters.
    defineConstructorAllPropsForNonConstructors()
    return this
}
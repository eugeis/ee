package ee.lang.gen.kt

import ee.lang.*
import ee.lang.gen.java.j

object k : StructureUnit({ name("Kotlin") }) {
    object core : StructureUnit() {
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

    object json : StructureUnit({ namespace("com.fasterxml.jackson.annotation") }) {
        object JsonProperty : ExternalType()
        object JsonValue : ExternalType()
    }
}

fun <T : StructureUnitI<*>> T.prepareForKotlinGeneration(): T {
    j.initObjectTree()
    k.initObjectTree()

    initObjectTrees()

    //declare as 'base' all compilation units with non implemented operations.
    declareAsBaseWithNonImplementedOperation()

    prepareAttributesOfEnums()

    //define constructor with all parameters.
    defineConstructorAllPropsForNonConstructors()
    return this
}
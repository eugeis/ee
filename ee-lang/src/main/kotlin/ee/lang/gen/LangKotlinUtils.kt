package ee.lang.gen

import ee.lang.*
import ee.lang.gen.j

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
    }
}

fun <T : StructureUnitI> T.prepareForKotlinGeneration(searchForTargetComposite: Boolean = true): T {
    initObjectTrees(searchForTargetComposite)
    j.initObjectTree()
    k.initObjectTree()

    //declare as 'base' all compilation units with non implemented operations.
    declareAsBaseWithNonImplementedOperation()

    prepareAttributesOfEnums()

    //define constructor with all parameters.
    defineConstructorAllForNonConstructors()
    return this
}
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

fun <T : StructureUnitI> T.prepareForKotlinGeneration(searchForTargetComposite: Boolean = true): T {
    initObjectTrees(searchForTargetComposite)

    //declare as 'base' all compilation units with non implemented operations.
    declareAsBaseWithNonImplementedOperation()

    prepareAttributesOfEnums()

    //define constructor with all parameters.
    defineConstructorAllForNonConstructors()
    return this
}
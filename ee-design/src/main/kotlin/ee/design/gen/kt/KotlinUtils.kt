package ee.design.gen.kt

import ee.design.*
import ee.design.gen.KotlinContext
import ee.design.gen.java.java

object kt : StructureUnit() {
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
    fun build(namespace: String): KotlinContext {
        var ret = KotlinContext(namespace = namespace)
        prepareDerivedController(ret)
        return ret
    }
}

private fun prepareDerivedController(context: KotlinContext) {
    val derivedController = context.derivedController

    val isNotPartOfDslTypes: ItemI.() -> Boolean = { n != this.parent() }
    val isNotPartOfDslModelAndTypes: ItemI.() -> Boolean = {
        l != this.parent() && n != this.parent()
    }

    derivedController.registerKind(DerivedNames.API.name, isNotPartOfDslTypes, { "${name()}I" })
    derivedController.registerKind(DerivedNames.API_BASE.name, isNotPartOfDslTypes, { "${name()}IfcBase" })
    derivedController.registerKind(DerivedNames.IMPL.name, isNotPartOfDslTypes, { name() })
    derivedController.registerKind(DerivedNames.IMPL_BASE.name, isNotPartOfDslTypes, { "${name()}Base" })
    derivedController.registerKind(DerivedNames.COMPOSITE.name, isNotPartOfDslTypes, { "${name()}s" })
    derivedController.registerKind(DerivedNames.DSL_TYPE.name, isNotPartOfDslModelAndTypes, { "ItemTypes.${name()}" })
}

fun <T : CompositeI> T.prepareForKotlinGeneration() {
    initObjectTrees()

    //declare as 'base' all compilation units with non implemented operations.
    declareAsBaseWithNonImplementedOperation()

    prepareAttributesOfEnums()

    //define constructor with all parameters.
    defineConstructorAllForNonConstructors()
}

fun <T : CompositeI> T.initObjectTrees() {
    n.initObjectTree()
    java.initObjectTree()
    kt.initObjectTree()
    initObjectTree()
    initBlackNames()
}
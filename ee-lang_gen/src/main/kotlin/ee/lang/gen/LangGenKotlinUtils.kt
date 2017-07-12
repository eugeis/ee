package ee.lang.gen

import ee.lang.*

object DerivedNames {
    val API = "API"
    val API_BASE = "API_BASE"
    val IMPL = "IMPL"
    val IMPL_BASE = "IMPL_BASE"
    val ENUM = "ENUM"
    val COMPOSITE = "COMPOSITE"
    val DSL_TYPE = "DSL_TYPE"
    val EMPTY_CLASS = "EMPTY_CLASS"
    val EMPTY = "EMPTY"
}

open class KotlinContextFactory {
    private val isNotPartOfNativeTypes: ItemI.() -> Boolean = { n != this.parent() }
    private val isNotPartOfNativeAndModelTypes: ItemI.() -> Boolean = { n != this.parent() && l != this.parent() }

    fun buildForDslBuilder(namespace: String, moduleFolder: String): CompositeI.() -> KotlinContext {
        val controller = DerivedController(DerivedStorage<ItemI>())

        controller.registerKind(DerivedNames.API, isNotPartOfNativeTypes, { "${name()}I" })
        controller.registerKind(DerivedNames.API_BASE, isNotPartOfNativeTypes, { "${name()}IfcBase" })
        controller.registerKind(DerivedNames.IMPL, isNotPartOfNativeTypes, { name() })
        controller.registerKind(DerivedNames.IMPL_BASE, isNotPartOfNativeTypes, { "${name()}Base" })
        controller.registerKind(DerivedNames.COMPOSITE, isNotPartOfNativeTypes, { "${name()}s" })
        controller.registerKind(DerivedNames.EMPTY, isNotPartOfNativeTypes, { "${name()}Empty" })
        controller.registerKind(DerivedNames.EMPTY_CLASS, isNotPartOfNativeTypes, { "${name()}EmptyClass" })
        controller.registerKind(DerivedNames.DSL_TYPE, isNotPartOfNativeAndModelTypes, { "ItemTypes.${name()}" })

        val ret = KotlinContext(namespace = namespace, moduleFolder = moduleFolder, genFolder = "src-gen/main/kotlin",
                genFolderDeletable = true, derivedController = controller)
        return { ret }
    }
}

fun <T : CompositeI> T.prepareForKotlinGeneration(): CompositeI {
    n.initObjectTree()
    val ret = initObjectTree()
    return ret
}
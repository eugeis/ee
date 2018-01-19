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
    private val isNotPartOfNativeTypes: ItemI<*>.() -> Boolean = { n != this.parent() }
    private val isNotPartOfNativeAndModelTypes: ItemI<*>.() -> Boolean = { n != this.parent() && l != this.parent() }

    fun buildForDslBuilder(namespace: String, moduleFolder: String): CompositeI<*>.() -> KotlinContext {
        val controller = DerivedController(DerivedStorage<ItemI<*>>())

        controller.registerKind(DerivedNames.API, { "${name()}I" }, isNotPartOfNativeTypes)
        controller.registerKind(DerivedNames.API_BASE, { "${name()}IfcBase" }, isNotPartOfNativeTypes)
        controller.registerKind(DerivedNames.IMPL, { name() }, isNotPartOfNativeTypes)
        controller.registerKind(DerivedNames.IMPL_BASE, { "${name()}Base" }, isNotPartOfNativeTypes)
        controller.registerKind(DerivedNames.COMPOSITE, { "${name()}s" }, isNotPartOfNativeTypes)
        controller.registerKind(DerivedNames.EMPTY, { "${name()}Empty" }, isNotPartOfNativeTypes)
        controller.registerKind(DerivedNames.EMPTY_CLASS, { "${name()}EmptyClass" }, isNotPartOfNativeTypes)
        controller.registerKind(DerivedNames.DSL_TYPE, { "ItemTypes.${name()}" }, isNotPartOfNativeAndModelTypes)

        val ret = KotlinContext(namespace = namespace, moduleFolder = moduleFolder, genFolder = "src-gen/main/kotlin",
            genFolderDeletable = true, derivedController = controller)
        return { ret }
    }
}

fun <T : Composite> T.prepareForKotlinGeneration(): Composite {
    n.initObjectTree()
    val ret = initObjectTree()
    return ret
}

fun ItemI<*>.isNative(): Boolean = this.parent() == n
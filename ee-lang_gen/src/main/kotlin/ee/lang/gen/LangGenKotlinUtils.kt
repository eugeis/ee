package ee.lang.gen

import ee.lang.*

object KotlinContextFactory {
    private val isNotPartOfNativeTypes: ItemI.() -> Boolean = { n != this.parent() }
    private val isNotPartOfNativeAndModelTypes: ItemI.() -> Boolean = { n != this.parent() && l != this.parent() }

    fun buildForDslBuilder(namespace: String, moduleFolder: String): CompositeI.() -> KotlinContext {
        val controller = DerivedController(DerivedStorage<ItemI>())

        controller.registerKind(DerivedNames.API.name, isNotPartOfNativeTypes, { "${name()}I" })
        controller.registerKind(DerivedNames.API_BASE.name, isNotPartOfNativeTypes, { "${name()}IfcBase" })
        controller.registerKind(DerivedNames.IMPL.name, isNotPartOfNativeTypes, { name() })
        controller.registerKind(DerivedNames.IMPL_BASE.name, isNotPartOfNativeTypes, { "${name()}Base" })
        controller.registerKind(DerivedNames.COMPOSITE.name, isNotPartOfNativeTypes, { "${name()}s" })
        controller.registerKind(DerivedNames.EMPTY.name, isNotPartOfNativeTypes, { "${name()}Empty" })
        controller.registerKind(DerivedNames.EMPTY_CLASS.name, isNotPartOfNativeTypes, { "${name()}EmptyClass" })
        controller.registerKind(DerivedNames.DSL_TYPE.name, isNotPartOfNativeAndModelTypes, { "ItemTypes.${name()}" })

        val ret = KotlinContext(namespace = namespace, moduleFolder = moduleFolder, genFolder = "src-gen/main/kotlin",
                genFolderDeletable = true, derivedController = controller)
        return { ret }
    }
}

fun <T : CompositeI> T.prepareForKotlinGeneration(): CompositeI {
    n.initObjectTree()
    val ret = initObjectTree()
    ret.sortByName()
    return ret
}
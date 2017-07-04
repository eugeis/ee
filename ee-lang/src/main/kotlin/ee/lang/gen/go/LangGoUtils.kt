package ee.lang.gen.go

import ee.common.ext.*
import ee.lang.*

object g : StructureUnit({ namespace("").name("Go") }) {
    object time : StructureUnit({ namespace("time") }) {
        val Time = Type()
        val Now = Operation()
    }
}

open class GoContext : GenerationContext {
    val namespaceLastPart: String

    constructor(namespace: String = "", moduleFolder: String = "",
                genFolder: String = "src-gen/main/go", genFolderDeletable: Boolean = true,
                derivedController: DerivedController = DerivedController(DerivedStorage<ItemI>()))
            : super(namespace, moduleFolder, genFolder, genFolderDeletable, derivedController) {
        namespaceLastPart = namespace.substringAfterLast(".")
    }

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${toPackage(indent)}${toImports(indent)}$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return namespaceLastPart.isNotEmpty().then { "${indent}package $namespaceLastPart$nL$nL" }
    }

    private fun toImports(indent: String): String {
        return types.isNotEmpty().then {
            val outsideTypes = types.filter { it.namespace().isNotEmpty() && !it.namespace().equals(namespace, true) }
            outsideTypes.isNotEmpty().then {
                "${outsideTypes.map { "$indent${it.namespace()}" }.toSortedSet().
                        joinSurroundIfNotEmptyToString(nL, "${indent}import ($nL", "$nL)") {
                            """    "${it.toLowerCase().toDotsAsPath()}""""
                        }}"
            }
        }
    }

    override fun n(item: ItemI, derivedKind: String): String {
        val derived = types.addReturn(derivedController.derive(item, derivedKind))
        if (derived.namespace().isEmpty() || derived.namespace().equals(namespace, true)) {
            return derived.name()
        } else {
            return """${derived.namespace().substringAfterLast(".").toLowerCase()}.${derived.name()}"""
        }
    }
}

fun <T : StructureUnitI> T.prepareForGoGeneration(): T {
    g.initObjectTree()
    initObjectTrees()

    //declare as 'base' all compilation units with non implemented operations.
    declareAsBaseWithNonImplementedOperation()

    prepareAttributesOfEnums()

    //define constructor with all parameters.
    defineConstructorAllForNonConstructors()
    return this
}

fun AttributeI.nameForMember(): String = storage.getOrPut(this, "nameForMember", {
    "${accessibleAndMutable().ifElse({ "${name().capitalize()} " }, { "${name().decapitalize()}" })}"
})

val itemAndTemplateNameAsGoFileName: TemplateI<*>.(CompositeI) -> Names = {
    Names("${it.name().capitalize()}${name.capitalize()}.go")
}

val templateNameAsGoFileName: TemplateI<*>.(CompositeI) -> Names = {
    Names("$name.go")
}

val itemNameAsGoFileName: TemplateI<*>.(CompositeI) -> Names = {
    Names("${it.name()}.go")
}
package ee.lang.gen.ts

import ee.common.ext.*
import ee.lang.*


object ts : StructureUnit({ namespace("").name("TypeScript") }) {
}

open class TsContext : GenerationContext {
    val namespaceLastPart: String

    constructor(namespace: String = "", moduleFolder: String = "",
                genFolder: String = "src/main/ts",
                genFolderDeletable: Boolean = false, genFolderPatternDeletable: Regex? = ".*Base.ts".toRegex(),
                derivedController: DerivedController = DerivedController(DerivedStorage<ItemI>()),
                macroController: MacroController = MacroController())
            : super(namespace, moduleFolder, genFolder, genFolderDeletable, genFolderPatternDeletable,
            derivedController, macroController) {
        namespaceLastPart = namespace.substringAfterLast(".")
    }

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${toPackage(indent)}${toImports(indent)}$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return namespaceLastPart.isNotEmpty().then { "${indent}package $namespaceLastPart${nL}${nL}" }
    }

    private fun toImports(indent: String): String {
        return types.isNotEmpty().then {
            val outsideTypes = types.filter { it.namespace().isNotEmpty() && !it.namespace().equals(namespace, true) }
            outsideTypes.isNotEmpty().then {
                outsideTypes.map { "$indent${it.namespace()}" }.toSortedSet().
                        joinSurroundIfNotEmptyToString(nL, "${indent}import (${nL}", "${nL})") {
                            """    "${it.toLowerCase().toDotsAsPath().replace("github/com", "github.com")}""""
                        }
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

fun <T : StructureUnitI> T.prepareForTsGeneration(): T {
    initsForTsGeneration()
    extendForTsGenerationLang()
    return this
}

fun <T : StructureUnitI> T.initsForTsGeneration(): T {
    ts.initObjectTree()
    initObjectTrees()
    return this
}

fun <T : StructureUnitI> T.extendForTsGenerationLang(): T {
    //declare as 'base' all compilation units with non implemented operations.
    declareAsBaseWithNonImplementedOperation()

    prepareAttributesOfEnums()

    defineSuperUnitsAsAnonymousProps()

    defineConstructorEmpty()
    return this
}

val itemAndTemplateNameAsGoFileName: TemplateI<*>.(CompositeI) -> Names = {
    Names("${it.name().capitalize()}${name.capitalize()}.ts")
}
val templateNameAsTsFileName: TemplateI<*>.(CompositeI) -> Names = {
    Names("$name.ts")
}
val itemNameAsTsFileName: TemplateI<*>.(CompositeI) -> Names = {
    Names("${it.name()}.ts")
}
package ee.lang.gen.ts

import ee.common.ext.*
import ee.lang.*


object ts : StructureUnit({ namespace("").name("TypeScript") }) {
}

open class TsContext : GenerationContext {

    constructor(namespace: String = "", moduleFolder: String = "",
                genFolder: String = "src/app/shared",
                genFolderDeletable: Boolean = false, genFolderPatternDeletable: Regex? = ".*Base.ts".toRegex(),
                derivedController: DerivedController = DerivedController(DerivedStorage<ItemIB<*>>()),
                macroController: MacroController = MacroController())
            : super(namespace, moduleFolder, genFolder, genFolderDeletable, genFolderPatternDeletable,
            derivedController, macroController) {
    }

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${toPackage(indent)}${toImports(indent)}$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return ""//namespaceLastPart.isNotEmpty().then { "${indent}package $namespaceLastPart${nL}${nL}" }
    }

    private fun toImports(indent: String): String {
        return types.isNotEmpty().then {
            val outsideTypes = types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace }.
                    groupBy { it.findParentMust(StructureUnitIB::class.java) }
            outsideTypes.isNotEmpty().then {
                "${outsideTypes.map { (su, items) ->
                    """${indent}import {${items.sortedBy { it.name() }.joinToString(", ") {
                        it.name()
                    }}} from "../${su.name().equals("shared", true).not().then {
                        "${su.name().decapitalize()}/"
                    }}${su.name().capitalize()}ApiBase""""
                }.toHashSet().sorted().joinToString(nL)}$nL$nL"
            }
        }
    }
}

fun <T : StructureUnitIB<*>> T.prepareForTsGeneration(): T {
    initsForTsGeneration()
    extendForTsGenerationLang()
    return this
}

fun <T : StructureUnitIB<*>> T.initsForTsGeneration(): T {
    ts.initObjectTree()
    initObjectTrees()
    return this
}

fun <T : StructureUnitIB<*>> T.extendForTsGenerationLang(): T {
    //declare as 'base' all compilation units with non implemented operations.
    declareAsBaseWithNonImplementedOperation()

    prepareAttributesOfEnums()

    defineSuperUnitsAsAnonymousProps()

    defineConstructorEmpty()
    return this
}

val itemAndTemplateNameAsTsFileName: TemplateI<*>.(CompositeIB<*>) -> Names = {
    Names("${it.name().capitalize()}${name.capitalize()}.ts")
}
val templateNameAsTsFileName: TemplateI<*>.(CompositeIB<*>) -> Names = {
    Names("$name.ts")
}
val itemNameAsTsFileName: TemplateI<*>.(CompositeIB<*>) -> Names = {
    Names("${it.name()}.ts")
}
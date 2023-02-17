package ee.lang.gen.ts

import ee.common.ext.then
import ee.lang.*


object ts : StructureUnit({ namespace("").name("TypeScript") }) {}

object angular : StructureUnit({namespace("@angular").name("angular")}) {
    object core : StructureUnit() {
        object Component : ExternalType() {
        }
        object Input : ExternalType() {
        }
        object OnInit : ExternalType() {
        }
        object AfterViewInit : ExternalType() {
        }
        object ViewChild : ExternalType() {
        }
        object Injectable : ExternalType() {
        }
    }
    object forms : StructureUnit() {
        object FormControl : ExternalType() {
        }
    }
}

object rxjs : StructureUnit({namespace("rxjs").name("rxjs")}) {
    object empty : StructureUnit({namespace("").name("")}) {
        object Observable : ExternalType() {
        }
    }

    object operators : StructureUnit({namespace("").name("")}) {
        object map : ExternalType() {
        }
        object startWith : ExternalType() {
        }
    }
}

open class TsContext(
    var alwaysImportTypes: Boolean = false,
    namespace: String = "",
    moduleFolder: String = "",
    genFolder: String = "src/app/shared",
    genFolderDeletable: Boolean = false,
    genFolderPatternDeletable: Regex? = ".*Base.ts".toRegex(),
    derivedController: DerivedController,
    macroController: MacroController) : GenerationContext(
    namespace, moduleFolder, genFolder,
    genFolderDeletable, genFolderPatternDeletable, derivedController, macroController
) {

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${toPackage(indent)}${toImports(indent)}$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return "" //namespaceLastPart.isNotEmpty().then { "${indent}package $namespaceLastPart${nL}${nL}" }
    }

    private fun toImports(indent: String): String {
        return types.isNotEmpty().then {
            val outsideTypes = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
            else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace }
                .groupBy { it.findParentMust(StructureUnitI::class.java) }

            outsideTypes.isNotEmpty().then {
                "${outsideTypes.map { (su, items) ->
                    """${indent}import {${items.sortedBy { it.name() }.joinToString(", ") {
                        it.name()
                    }}} from ${if (su.name().equals("empty", true)) {
                        """'${su.parent().name()}'"""
                    } 
                    else {
                        """'${(su.parent().name().decapitalize() !in arrayOf("rxjs")).then { "@" }}${su.parent().name().decapitalize()}/${su.name().equals("shared", true).not().then {
                            su.name().decapitalize()
                        }}${(su.parent().name().decapitalize() !in arrayOf("angular", "rxjs")).then { "/${su.name().capitalize()}ApiBase" }}'"""
                    }}"""
                }.toHashSet().sorted().joinToString(nL)}$nL$nL"
            }
        }
    }
}

fun <T : StructureUnitI<*>> T.prepareForTsGeneration(): T {
    initsForTsGeneration()
    extendForTsGenerationLang()
    return this
}

fun <T : StructureUnitI<*>> T.initsForTsGeneration(): T {
    ts.initObjectTree()
    angular.initObjectTrees()
    rxjs.initObjectTrees()
    initObjectTrees()
    return this
}

fun <T : StructureUnitI<*>> T.extendForTsGenerationLang(): T {
    //declare as 'isBase' all compilation units with non implemented operations.
    declareAsBaseWithNonImplementedOperation()

    prepareAttributesOfEnums()

    defineSuperUnitsAsAnonymousProps()

    defineConstructorNoProps()
    return this
}

val itemAndTemplateNameAsTsFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name().capitalize()}${name.capitalize()}.ts")
}
val templateNameAsTsFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("$name.ts")
}
val itemNameAsTsFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name()}.ts")
}

val itemAndTemplateNameAsHTMLFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name().capitalize()}${name.capitalize()}.html")
}
val templateNameAsHTMLFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("$name.html")
}
val itemNameAsHTMLFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name()}.html")
}

val itemAndTemplateNameAsCSSFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name().capitalize()}${name.capitalize()}.scss")
}
val templateNameAsCSSFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("$name.scss")
}
val itemNameAsCSSFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name()}.scss")
}

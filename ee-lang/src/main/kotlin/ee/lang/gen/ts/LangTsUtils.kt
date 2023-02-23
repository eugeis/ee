package ee.lang.gen.ts

import ee.common.ext.fileName
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
        object NgModule : ExternalType() {
        }
    }
    object forms : StructureUnit() {
        object FormControl : ExternalType() {
        }
        object FormsModule : ExternalType() {
        }
        object ReactiveFormsModule : ExternalType() {
        }
    }
    object material : StructureUnit() {
        object table : StructureUnit() {
            object MatTableDataSource : ExternalType() {
            }
        }
        object sort : StructureUnit() {
            object MatSort : ExternalType() {
            }
        }
    }
    object common : StructureUnit() {
        object CommonModule: ExternalType() {
        }
    }
    object commonhttp : StructureUnit({(namespace("common/http").name("common/http"))}) {
        object HttpClient : ExternalType() {
        }
    }
    object router : StructureUnit() {
        object Routes : ExternalType() {
        }
        object RouterModule : ExternalType() {
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

object service: StructureUnit({namespace("service").name("service")}) {

    object own: StructureUnit({namespace("own").name("own")}) {
        // ../../service/...-data.service
        object DataService: ExternalType() {
        }
    }
    object template: StructureUnit({namespace("template").name("template")}) {
        // @template/service/data.service
        object DataService: ExternalType() {
        }
    }
    object module: StructureUnit({namespace("module").name("module")}) {
        // ../../service/...-module-view.service
        object ViewService: ExternalType() {
        }
    }
    object other: StructureUnit({namespace("other").name("other")}) {
        // ../../../abc/service/abc-module-view.service
        object DataService: ExternalType() {
        }
    }
}

object module: StructureUnit({namespace("module").name("module")}) {
    object template : StructureUnit() {
        object TemplateModule : ExternalType() {
        }
    }
    object material : StructureUnit() {
        object MaterialModule : ExternalType() {
        }
    }
    object services : StructureUnit() {
        object TemplateTranslateService : ExternalType() {
        }
    }
}

object ngxtranslate: StructureUnit({namespace("ngx-translate").name("ngx-translate")}) {
    object core : StructureUnit() {
        object TranslateLoader : ExternalType() {
        }
        object TranslateModule : ExternalType() {
        }
        object TranslateService : ExternalType() {
        }
    }
    object httploader : StructureUnit({namespace("http-loader").name("http-loader")}) {
        object TranslateHttpLoader : ExternalType() {
        }
    }
}

object ownComponent : StructureUnit({namespace("ownComponent").name("ownComponent")}) {
    object routing : StructureUnit() {
        object RoutingModules : ExternalType()
    }
    object view : StructureUnit() {
        object ViewComponent : ExternalType()
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
                        if (it.name().contains('-')) {"""${it.name().substringAfterLast('-')}${it.name().substringBeforeLast('-')}"""} else {it.name()}
                    }}} from ${if (su.name().equals("empty", true)) {
                        """'${su.parent().name()}'"""
                    } else if (su.parent().name().equals("service", true)) {
                        if (su.name().equals("template", true)) {
                            """'@template/services/data.service'"""
                        } else if (su.name().equals("own", true)) {
                            """'../../service/${items.sortedBy { it.name() }.joinToString(", ") {
                                it.name().substringAfterLast('-').toLowerCase()
                            }}-data.service'"""
                        } else if (su.name().equals("module", true)) {
                            """'../../service/${items.sortedBy { it.name() }.joinToString(", ") {
                                it.name().substringAfterLast('-').toLowerCase()
                            }}-module-view.service'"""
                        } else {
                            """''"""
                        }
                    } else if (su.parent().name().equals("material", true)) {
                        """'@${su.parent().parent().name()}/${su.parent().name()}/${su.name()}'"""
                    } else if (su.parent().name().equals("module", true)) {
                        """'@template/${if(su.name().equals("services", true)) {"""${su.name()}/translate.service"""} else {"""${su.name()}.module"""}}'"""
                    } else if (su.parent().name().equals("ownComponent", true)) {
                        """'./${if(su.name().equals("routing", true)) {"""${items.sortedBy { it.name() }.joinToString(", ") {
                            it.name().substringAfterLast('-').toLowerCase()
                        }}-routing.module"""} else {"""components/view/${items.sortedBy { it.name() }.joinToString(", ") {
                            it.name().substringAfterLast('-').toLowerCase()
                        }}-module-view.component"""}}'"""
                    }
                    else {
                        """'${(su.parent().name().decapitalize() !in arrayOf("rxjs")).then { "@" }}${su.parent().name().decapitalize()}/${su.name().equals("shared", true).not().then {
                            su.name().decapitalize()
                        }}${(su.parent().name().decapitalize() !in arrayOf("angular", "rxjs", "ngx-translate")).then { "/${su.name().capitalize()}ApiBase" }}'"""
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
    service.initObjectTrees()
    module.initObjectTrees()
    ngxtranslate.initObjectTrees()
    ownComponent.initObjectTrees()
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

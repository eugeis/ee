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
    object template: StructureUnit({namespace("template").name("template")}) {
        // @template/service/data.service
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
        fun toImportsOutsideTypes(): String {
            return types.isNotEmpty().then {
                val outsideTypes = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace &&
                        ((!it.name().contains("Component", true) && !it.name().contains("RoutingModules", true)
                                && !it.name().contains("Enum", true) && !it.name().contains("ViewService", true)
                                && !it.name().contains("DataService", true) && !it.namespace().contains(".module")) ||
                                it.name().equals("Component", true)) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                outsideTypes.isNotEmpty().then {
                    "${outsideTypes.map { (su, items) ->
                        """${indent}import {${items.sortedBy { it.name() }.
                        joinToString(", ") {
                            it.name()
                        }}} from ${when(su.parent().name()) {
                            "material" -> """'@${su.parent().parent().name()}/${su.parent().name()}/${su.name()}'"""
                            "module" -> """'@template/${if(su.name().equals("services", true)) {"""${su.name()}/translate.service"""} else {"""${su.name()}.module"""}}'"""
                            else -> when(su.name()) {
                                "empty" -> """'${su.parent().name()}'"""
                                else -> """'${(su.parent().name().decapitalize() !in arrayOf("rxjs")).then { "@" }}${su.parent().name().decapitalize()}/${su.name().equals("shared", true).not().then {
                                    su.name().decapitalize()
                                }}${(su.parent().name().decapitalize() !in arrayOf("angular", "rxjs", "ngx-translate")).then { "/${su.name().capitalize()}ApiBase" }}'"""
                            }
                        }}"""
                    }.toHashSet().sorted().joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsViewComponent(): String {
            return types.isNotEmpty().then {
                val viewComponent = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains("ViewComponent", true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                viewComponent.isNotEmpty().then {
                    "${viewComponent.map { (su, items) ->
                        items.filter { it.namespace().contains("-entity-view.component") }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()}
                                else {su.parent().name().decapitalize()}}${
                                    if (namespace.contains(su.name(), true)) {""}
                                    else {"/${su.name().toLowerCase()}"}}/${it.name().replace("ViewComponent", "").toLowerCase()}/components/view/${
                                        if (namespace.contains(su.name(), true)) {"${it.name().replace("ViewComponent", "").toLowerCase()}-module-view.component"}
                                        else {"${it.name().replace("ViewComponent", "").toLowerCase()}-${
                                            it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}"}}'"""
                            
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsListComponent(): String {
            return types.isNotEmpty().then {
                val listComponent = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains("ListComponent", true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                listComponent.isNotEmpty().then {
                    "${listComponent.map { (su, items) ->
                        items.filter { it.namespace().contains("-entity-list.component") }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()}
                                else {su.parent().name().decapitalize()}}/${su.name().toLowerCase()}/${it.name().replace("ListComponent", "").toLowerCase()}/components/list/${
                                    it.name().replace("ListComponent", "").toLowerCase()}-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsFormComponent(): String {
            return types.isNotEmpty().then {
                val formComponent = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains("FormComponent", true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                formComponent.isNotEmpty().then {
                    "${formComponent.map { (su, items) ->
                        items.filter { it.namespace().contains("-entity-form.component") }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()}
                                else {su.parent().name().decapitalize()}}/${su.name().toLowerCase()}/${it.name().replace("FormComponent", "").toLowerCase()}/components/form/${
                                    it.name().replace("FormComponent", "").toLowerCase()}-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsEnumComponent(): String {
            return types.isNotEmpty().then {
                val enumComponent = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains("EnumComponent", true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                enumComponent.isNotEmpty().then {
                    "${enumComponent.map { (su, items) ->
                        items.filter { it.namespace().contains("-enum.component") }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()}
                                else {su.parent().name().decapitalize()}}/${su.name().toLowerCase()}/enums/${it.name().replace("EnumComponent", "").toLowerCase()}/${
                                    it.name().replace("EnumComponent", "").toLowerCase()}-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsBasicComponent(): String {
            return types.isNotEmpty().then {
                val basicComponent = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains("BasicComponent", true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                basicComponent.isNotEmpty().then {
                    "${basicComponent.map { (su, items) ->
                        items.filter { it.namespace().contains("-basic.component") }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()}
                                else {su.parent().name().decapitalize()}}/${su.name().toLowerCase()}/basics/${it.name().replace("BasicComponent", "").toLowerCase()}/${
                                    it.name().replace("BasicComponent", "").toLowerCase()}-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsViewService(): String {
            return types.isNotEmpty().then {
                val viewService = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains("ViewService", true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                viewService.isNotEmpty().then {
                    "${viewService.map { (su, items) ->
                        items.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()} 
                                else {su.parent().name().decapitalize()}}/${
                                    it.name().replace("ViewService", "").decapitalize()}/service/${
                                        it.name().replace("ViewService", "").toLowerCase()}-${
                                            it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        //TODO: filter service so that it will not be generate on its own service component
        // for now c.n usage is removed on its component name
        fun toImportsDataService(): String {
            return types.isNotEmpty().then {
                val dataService = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains("DataService", true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                dataService.isNotEmpty().then {
                    "${dataService.map { (su, items) ->
                        items.sortedBy { it.name() }.
                        joinToString("\n") {
                            if (it.name().equals("DataService", true)) {"""import {${it.name()}} from '@template/services/data.service'"""} 
                            else {"""import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()}
                                else {su.parent().name().decapitalize()}}/${su.name().toLowerCase()}/${it.name().replace("DataService", "").toLowerCase()}/service/${
                                    it.name().replace("DataService", "").toLowerCase()}-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""}
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsRoutingModules(): String {
            return types.isNotEmpty().then {
                val routingModules = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains("RoutingModules", true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                routingModules.isNotEmpty().then {
                    "${routingModules.map { (su, items) ->
                        items.filter { it.namespace().contains("-routing.module") }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()}
                                else {su.parent().name().decapitalize()}}/${it.name().replace("RoutingModules", "").decapitalize()}/${
                                    it.name().replace("RoutingModules", "").decapitalize()}-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        //TODO: filter module so that it will not be generate on its own module component
        // for now c.n usage is removed on its component name
        fun toImportsElementModules(): String {
            return types.isNotEmpty().then {
                val elementModules = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains("Module", true)
                        && it.namespace().contains("-model.module", true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                elementModules.isNotEmpty().then {
                    "${elementModules.map { (su, items) ->
                        items.filter { it.namespace().contains("-model.module") }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()}
                                else {su.parent().name().decapitalize()}}/${it.name().replace("Module", "").decapitalize()}/${
                                    it.name().replace("Module", "").decapitalize()}-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        return toImportsOutsideTypes().trimIndent() +
                toImportsViewComponent().trimIndent() +
                toImportsListComponent().trimIndent() +
                toImportsFormComponent().trimIndent() +
                toImportsEnumComponent().trimIndent() +
                toImportsBasicComponent().trimIndent() +
                toImportsViewService().trimIndent() +
                toImportsDataService().trimIndent() +
                toImportsRoutingModules().trimIndent() +
                toImportsElementModules().trimIndent()
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

package ee.lang.gen.ts

import ee.common.ext.then
import ee.lang.*

open class AngularDerivedKindNames {
    val DataService = "DataService"
    val ViewService = "ViewService"
    val Service = "Service"
    val ViewComponent = "ViewComponent"
    val ListComponent = "ListComponent"
    val FormComponent = "FormComponent"
    val Component = "Component"
    val Enum = "Enum"
    val EnumComponent = "EnumComponent"
    val BasicComponent = "BasicComponent"
    val Module = "Module"
    val RoutingModules = "RoutingModules"
    val OwnDataService = "OwnDataService"
    val OwnModule = "OwnModule"
    val OwnRoutingModules = "OwnRoutingModules"
}

open class AngularFileFormatNames {
    val DataService = "-data.service"
    val ModuleViewService = "-module-view.service"
    val ModuleViewComponent = "-module-view.component"
    val EntityViewComponent = "-entity-view.component"
    val EntityListComponent = "-entity-list.component"
    val EntityFormComponent = "-entity-form.component"
    val EnumComponent = "-enum.component"
    val BasicComponent = "-basic.component"
    val Module = "-model.module"
    val RoutingModule = "-routing.module"
}

object AngularDerivedType : AngularDerivedKindNames()

object AngularFileFormat : AngularFileFormatNames()

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
                        ((!it.name().contains(AngularDerivedType.Component, true) && !it.name().contains(AngularDerivedType.RoutingModules, true)
                                && !it.name().contains(AngularDerivedType.Enum, true) && !it.name().contains(AngularDerivedType.ViewService, true)
                                && !it.name().contains(AngularDerivedType.DataService, true) && !it.namespace().contains(AngularDerivedType.OwnDataService, true)
                                && !it.namespace().contains(AngularDerivedType.OwnModule, true) && !it.namespace().contains(AngularDerivedType.OwnRoutingModules, true)
                                && !it.namespace().contains(".module")) ||
                                it.name().equals(AngularDerivedType.Component, true)) }
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
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains(AngularDerivedType.ViewComponent, true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                viewComponent.isNotEmpty().then {
                    "${viewComponent.map { (su, items) ->
                        items.filter { it.namespace().contains(AngularFileFormat.EntityViewComponent) }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()}
                                else {su.parent().name().decapitalize()}}${
                                    if (namespace.contains(su.name(), true)) {""}
                                    else {"/${su.name().toLowerCase()}"}}/${it.name().replace(AngularDerivedType.ViewComponent, "").toLowerCase()}/components/view/${
                                        if (namespace.contains(su.name(), true)) {"${it.name().replace(AngularDerivedType.ViewComponent, "").toLowerCase()}${AngularFileFormat.ModuleViewComponent}"}
                                        else {"${it.name().replace(AngularDerivedType.ViewComponent, "").toLowerCase()}-${
                                            it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}"}}'"""
                            
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsListComponent(): String {
            return types.isNotEmpty().then {
                val listComponent = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains(AngularDerivedType.ListComponent, true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                listComponent.isNotEmpty().then {
                    "${listComponent.map { (su, items) ->
                        items.filter { it.namespace().contains(AngularFileFormat.EntityListComponent) }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()}
                                else {su.parent().name().decapitalize()}}/${su.name().toLowerCase()}/${it.name().replace(AngularDerivedType.ListComponent, "").toLowerCase()}/components/list/${
                                    it.name().replace(AngularDerivedType.ListComponent, "").toLowerCase()}-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsFormComponent(): String {
            return types.isNotEmpty().then {
                val formComponent = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains(AngularDerivedType.FormComponent, true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                formComponent.isNotEmpty().then {
                    "${formComponent.map { (su, items) ->
                        items.filter { it.namespace().contains(AngularFileFormat.EntityFormComponent) }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()}
                                else {su.parent().name().decapitalize()}}/${su.name().toLowerCase()}/${it.name().replace(AngularDerivedType.FormComponent, "").toLowerCase()}/components/form/${
                                    it.name().replace(AngularDerivedType.FormComponent, "").toLowerCase()}-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsEnumComponent(): String {
            return types.isNotEmpty().then {
                val enumComponent = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains(AngularDerivedType.EnumComponent, true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                enumComponent.isNotEmpty().then {
                    "${enumComponent.map { (su, items) ->
                        items.filter { it.namespace().contains(AngularFileFormat.EnumComponent) }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()}
                                else {su.parent().name().decapitalize()}}/${su.name().toLowerCase()}/enums/${it.name().replace(AngularDerivedType.EnumComponent, "").toLowerCase()}/${
                                    it.name().replace(AngularDerivedType.EnumComponent, "").toLowerCase()}-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsBasicComponent(): String {
            return types.isNotEmpty().then {
                val basicComponent = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains(AngularDerivedType.BasicComponent, true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                basicComponent.isNotEmpty().then {
                    "${basicComponent.map { (su, items) ->
                        items.filter { it.namespace().contains(AngularFileFormat.BasicComponent) }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()}
                                else {su.parent().name().decapitalize()}}/${su.name().toLowerCase()}/basics/${it.name().replace(AngularDerivedType.BasicComponent, "").toLowerCase()}/${
                                    it.name().replace(AngularDerivedType.BasicComponent, "").toLowerCase()}-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsViewService(): String {
            return types.isNotEmpty().then {
                val viewService = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains(AngularDerivedType.ViewService, true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                viewService.isNotEmpty().then {
                    "${viewService.map { (su, items) ->
                        items.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()} 
                                else {su.parent().name().decapitalize()}}/${
                                    it.name().replace(AngularDerivedType.ViewService, "").decapitalize()}/service/${
                                        it.name().replace(AngularDerivedType.ViewService, "").toLowerCase()}-${
                                            it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsDataService(): String {
            return types.isNotEmpty().then {
                val dataService = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains(AngularDerivedType.DataService, true)
                        && !it.namespace().contains(AngularDerivedType.OwnDataService, true)
                }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                dataService.isNotEmpty().then {
                    "${dataService.map { (su, items) ->
                        items.sortedBy { it.name() }.
                        joinToString("\n") {
                            if (it.name().equals(AngularDerivedType.DataService, true)) {"""import {${it.name()}} from '@template/services/data.service'"""} 
                            else {"""import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()}
                                else {su.parent().name().decapitalize()}}/${su.name().toLowerCase()}/${it.name().replace(AngularDerivedType.DataService, "").toLowerCase()}/service/${
                                    it.name().replace(AngularDerivedType.DataService, "").toLowerCase()}-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""}
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsRoutingModules(): String {
            return types.isNotEmpty().then {
                val routingModules = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains(AngularDerivedType.RoutingModules, true)
                        && !it.namespace().contains(AngularDerivedType.OwnRoutingModules, true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                routingModules.isNotEmpty().then {
                    "${routingModules.map { (su, items) ->
                        items.filter { it.namespace().contains(AngularFileFormat.RoutingModule) }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()}
                                else {su.parent().name().decapitalize()}}/${it.name().replace(AngularDerivedType.RoutingModules, "").decapitalize()}/${
                                    it.name().replace(AngularDerivedType.RoutingModules, "").decapitalize()}-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsElementModules(): String {
            return types.isNotEmpty().then {
                val elementModules = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains(AngularDerivedType.Module, true)
                        && it.namespace().contains(AngularFileFormat.Module, true) && !it.namespace().contains(AngularDerivedType.OwnModule, true)
                }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                elementModules.isNotEmpty().then {
                    "${elementModules.map { (su, items) ->
                        items.filter { it.namespace().contains(AngularFileFormat.Module) }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {su.name().decapitalize()}
                                else {su.parent().name().decapitalize()}}/${it.name().replace(AngularDerivedType.Module, "").decapitalize()}/${
                                    it.name().replace(AngularDerivedType.Module, "").decapitalize()}-${
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

package ee.lang.gen.ts

import ee.common.ext.then
import ee.lang.*
import java.util.*

open class AngularDerivedKindNames {
    val DataService = "DataService"
    val ViewService = "ViewService"
    val Service = "Service"
    val Entity = "Entity"
    val ViewComponent = "ViewComponent"
    val EntityViewComponent = Entity + ViewComponent
    val ListComponent = "ListComponent"
    val EntityListComponent = Entity + ListComponent
    val FormComponent = "FormComponent"
    val EntityFormComponent = Entity + FormComponent
    val AggregateViewComponent = "AggregateViewComponent"
    val EntityAggregateViewComponent = Entity + AggregateViewComponent
    val Component = "Component"
    val Enum = "Enum"
    val EnumComponent = "EnumComponent"
    val Basic = "Basic"
    val BasicComponent = "BasicComponent"
    val Value = "Value"
    val ValueViewComponent = Value + ViewComponent
    val ValueListComponent = Value + ListComponent
    val ValueFormComponent = Value + FormComponent
    val Module = "Module"
    val ModuleViewComponent = Module + ViewComponent
    val RoutingModules = "RoutingModules"
    val ApiBase = "ApiBase"
}

open class AngularFileFormatNames {
    val DataService = "-data.service"
    val ModuleViewService = "-module-view.service"
    val ModuleViewComponent = "-module-view.component"
    val EntityViewComponent = "-entity-view.component"
    val EntityList = "entity-list"
    val EntityAggregateView = "aggregate-view"
    val EntityAggregateViewComponent = "-entity-" + EntityAggregateView + ".component"
    val EntityListComponent = "-${EntityList}.component"
    val EntityForm = "entity-form"
    val EntityFormComponent = "-${EntityForm}.component"
    val EnumComponent = "-enum.component"
    val BasicComponent = "-basic.component"
    val BasicForm = "-basic-form"
    val Module = "-model.module"
    val RoutingModule = "-routing.module"
}

object AngularDerivedType : AngularDerivedKindNames()

object AngularFileFormat : AngularFileFormatNames()

object ts : StructureUnit({ namespace("").name("TypeScript") })

object angular : StructureUnit({namespace("@angular").name("angular")}) {
    object core : StructureUnit() {
        object Component : ExternalType()
        object Input : ExternalType()
        object Output : ExternalType()
        object EventEmitter : ExternalType()
        object OnInit : ExternalType()
        object AfterViewInit : ExternalType()
        object ViewChild : ExternalType()
        object Injectable : ExternalType()
        object NgModule : ExternalType()
    }
    object cdk : StructureUnit() {
        object keycodes: StructureUnit() {
            object COMMA: ExternalType()
            object ENTER: ExternalType()
        }
    }
    object forms : StructureUnit() {
        object FormControl : ExternalType()
        object FormGroup : ExternalType()
        object FormsModule : ExternalType()
        object ReactiveFormsModule : ExternalType()
    }
    object material : StructureUnit() {
        object table : StructureUnit() {
            object MatTableDataSource : ExternalType()
        }
        object sort : StructureUnit() {
            object MatSort : ExternalType()
        }
        object select : StructureUnit() {
            object MatSelectChange : ExternalType()
        }
        object chips: StructureUnit() {
            object MatChipInputEvent : ExternalType()
        }
        object autocomplete: StructureUnit() {
            object MatAutocompleteSelectedEvent : ExternalType()
        }
    }
    object common : StructureUnit() {
        object CommonModule: ExternalType()
        object Location: ExternalType()
    }
    object commonhttp : StructureUnit({(namespace("common/http").name("common/http"))}) {
        object HttpClient : ExternalType()
    }
    object router : StructureUnit() {
        object Routes : ExternalType()
        object RouterModule : ExternalType()
        object ActivatedRoute: ExternalType()
    }

    object platformbrowser: StructureUnit({namespace("platform-browser").name("platform-browser")}) {
        object animations: StructureUnit() {
            object BrowserAnimationsModule: ExternalType()
        }
    }
}

object rxjs : StructureUnit({namespace("rxjs").name("rxjs")}) {
    object empty : StructureUnit({namespace("").name("")}) {
        object Observable : ExternalType()
        object of : ExternalType()
        object forkJoin : ExternalType()
    }

    object operators : StructureUnit({namespace("").name("")}) {
        object map : ExternalType()
        object startWith : ExternalType()
    }
}

object service: StructureUnit({namespace("service").name("service")}) {
    object template: StructureUnit({namespace("template").name("template")}) {
        // @template/service/data.service
        object DataService: ExternalType()
    }
}

object module: StructureUnit({namespace("module").name("module")}) {
    object template : StructureUnit() {
        object TemplateModule : ExternalType()
    }
    object material : StructureUnit() {
        object MaterialModule : ExternalType()
    }
    object simpl: StructureUnit() {
        object SimplModule : ExternalType()
    }
    object services : StructureUnit() {
        object TemplateTranslateService : ExternalType()
    }
}

object ngxtranslate: StructureUnit({namespace("ngx-translate").name("ngx-translate")}) {
    object core : StructureUnit() {
        object TranslateLoader : ExternalType()
        object TranslateModule : ExternalType()
        object TranslateService : ExternalType()
    }
    object httploader : StructureUnit({namespace("http-loader").name("http-loader")}) {
        object TranslateHttpLoader : ExternalType()
    }
}

object siemens : StructureUnit({namespace("@siemens").name("siemens")}) {
    object ixangular: StructureUnit({namespace("ix-angular").name("ix-angular")}) {
        object IxModule: ExternalType()
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
                                && !it.name().contains(AngularDerivedType.DataService, true)
                                && !it.namespace().contains(".module")) ||
                                it.name().equals(AngularDerivedType.Component, true)) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                outsideTypes.isNotEmpty().then {
                    "${outsideTypes.map { (su, items) ->
                        """${indent}import {${items.sortedBy { it.name() }.distinctBy { it.name() }.
                        joinToString(", ") {
                            it.name()
                        }}} from ${when(su.parent().name()) {
                            "siemens" -> """'@${su.parent().name()}/${su.name()}'"""
                            "material", "cdk" -> """'@${su.parent().parent().name()}/${su.parent().name()}/${su.name()}'"""
                            "module" -> """'@template/${if(su.name().equals("services", true)) {"""${su.name()}/translate.service"""} else {"""${su.name()}.module"""}}'"""
                            else -> when(su.name()) {
                                "empty" -> """'${su.parent().name()}'"""
                                else -> """'${(su.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) } !in arrayOf("rxjs")).then { "@" }}${su.parent().name()
                                    .replaceFirstChar { it.lowercase(Locale.getDefault()) }}/${su.name().equals("shared", true).not().then {
                                    su.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }}${(su.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) } !in arrayOf("angular", "rxjs", "ngx-translate")).then { "/${su.name()
                                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}ApiBase" }}'"""
                            }
                        }}"""
                    }.toHashSet().sorted().joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsViewComponent(): String {
            return types.isNotEmpty().then {
                val viewComponent = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && (it.name().contains(AngularDerivedType.ViewComponent, true) || it.name().contains(AngularDerivedType.ModuleViewComponent, true) || it.name().contains(AngularDerivedType.ValueViewComponent, true)) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                viewComponent.isNotEmpty().then {
                    "${viewComponent.map { (su, items) ->
                        items.filter { it.namespace().contains(AngularFileFormat.EntityViewComponent) || it.namespace().contains(AngularFileFormat.ModuleViewComponent) }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {
                                    su.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }
                                else {
                                    su.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }}${
                                    if (su.parent().isEMPTY()) {""}
                                    else {"/${su.name().lowercase(Locale.getDefault())}"}}/${it.name().replace(AngularDerivedType.EntityViewComponent, "").replace(AngularDerivedType.ModuleViewComponent, "").replace(AngularDerivedType.ValueViewComponent, "")
                                .lowercase(Locale.getDefault())}/components/view/${
                                it.name().replace(AngularDerivedType.EntityViewComponent, "").replace(AngularDerivedType.ModuleViewComponent, "").replace(AngularDerivedType.ValueViewComponent, "").lowercase(Locale.getDefault())
                            }-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""
                            
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsAggregateViewComponent(): String {

            return types.isNotEmpty().then {
                val aggregateViewComponent = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && (it.name().contains(AngularDerivedType.AggregateViewComponent, true) ) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                aggregateViewComponent.isNotEmpty().then {
                    "${aggregateViewComponent.map { (su, items) ->
                        items.filter { it.namespace().contains(AngularFileFormat.EntityAggregateView) }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {
                                    su.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }
                                else {
                                    su.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }}${
                                if (su.parent().isEMPTY()) {""}
                                else {"/${su.name().lowercase(Locale.getDefault())}"}}/${it.name().replace(AngularDerivedType.EntityAggregateViewComponent, "").lowercase(Locale.getDefault())}/components/aggregateView/${
                                it.name().replace(AngularDerivedType.EntityAggregateViewComponent, "").lowercase(Locale.getDefault())
                            }-${
                                it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""

                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsListComponent(): String {
            return types.isNotEmpty().then {
                val listComponent = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && (it.name().contains(AngularDerivedType.ListComponent, true) || it.name().contains(AngularDerivedType.ValueListComponent, true))}
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                listComponent.isNotEmpty().then {
                    "${listComponent.map { (su, items) ->
                        items.filter { it.namespace().contains(AngularFileFormat.EntityListComponent) }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {
                                    su.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }
                                else {
                                    su.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }}/${su.name().lowercase(Locale.getDefault())}/${it.name().replace(AngularDerivedType.EntityListComponent, "").replace(AngularDerivedType.ValueListComponent, "")
                                .lowercase(Locale.getDefault())}/components/list/${
                                it.name().replace(AngularDerivedType.EntityListComponent, "").replace(AngularDerivedType.ValueListComponent, "").lowercase(Locale.getDefault())
                            }-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsFormComponent(): String {
            return types.isNotEmpty().then {
                val formComponent = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && (it.name().contains(AngularDerivedType.FormComponent, true) || it.name().contains(AngularDerivedType.ValueFormComponent, true)) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                formComponent.isNotEmpty().then {
                    "${formComponent.map { (su, items) ->
                        items.filter { it.namespace().contains(AngularFileFormat.EntityFormComponent) }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {
                                    su.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }
                                else {
                                    su.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }}/${su.name().lowercase(Locale.getDefault())}/${it.name().replace(AngularDerivedType.EntityFormComponent, "").replace(AngularDerivedType.ValueFormComponent, "")
                                .lowercase(Locale.getDefault())}/components/form/${
                                it.name().replace(AngularDerivedType.EntityFormComponent, "").replace(AngularDerivedType.ValueFormComponent, "").lowercase(Locale.getDefault())
                            }-${
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
                                if (su.parent().isEMPTY()) {
                                    su.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }
                                else {
                                    su.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }}/${su.name().lowercase(Locale.getDefault())}/enums/${it.name().replace(AngularDerivedType.EnumComponent, "")
                                .lowercase(Locale.getDefault())}/${
                                it.name().replace(AngularDerivedType.EnumComponent, "").lowercase(Locale.getDefault())
                            }-${
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
                                if (su.parent().isEMPTY()) {
                                    su.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }
                                else {
                                    su.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }}/${su.name().lowercase(Locale.getDefault())}/basics/${it.name().replace(AngularDerivedType.BasicComponent, "")
                                .lowercase(Locale.getDefault())}/${
                                it.name().replace(AngularDerivedType.BasicComponent, "").lowercase(Locale.getDefault())
                            }-${
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
                                if (su.parent().isEMPTY()) {
                                    su.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                } 
                                else {
                                    su.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }}/${
                                it.name().replace(AngularDerivedType.ViewService, "")
                                    .replaceFirstChar { it.lowercase(Locale.getDefault()) }
                            }/service/${
                                it.name().replace(AngularDerivedType.ViewService, "").lowercase(Locale.getDefault())
                            }-${
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
                }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                dataService.isNotEmpty().then {
                    "${dataService.map { (su, items) ->
                        items.sortedBy { it.name() }.
                        joinToString("\n") {
                            if (it.name().equals(AngularDerivedType.DataService, true)) {"""import {${it.name()}} from '@template/services/data.service'"""} 
                            else {"""import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {
                                    su.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }
                                else {
                                    su.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }}/${su.name().lowercase(Locale.getDefault())}/${if(it.name().replace(AngularDerivedType.DataService, "").equals(su.name(), ignoreCase = true)) {it.name().replace(AngularDerivedType.DataService, "")
                                    .lowercase(Locale.getDefault())} else {it.name().replace(AngularDerivedType.DataService, "")
                                    .lowercase(Locale.getDefault()).replace(su.name().lowercase(Locale.getDefault()), "")}}/service/${if(it.name().replace(AngularDerivedType.DataService, "").equals(su.name(), ignoreCase = true)) {it.name().replace(AngularDerivedType.DataService, "")
                                    .lowercase(Locale.getDefault())} else {it.name().replace(AngularDerivedType.DataService, "")
                                    .lowercase(Locale.getDefault()).replace(su.name().lowercase(Locale.getDefault()), "")}}-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""}
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        fun toImportsRoutingModules(): String {
            return types.isNotEmpty().then {
                val routingModules = if (alwaysImportTypes) types.groupBy { it.findParentMust(StructureUnitI::class.java)  }
                else types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace && it.name().contains(AngularDerivedType.RoutingModules, true) }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                routingModules.isNotEmpty().then {
                    "${routingModules.map { (su, items) ->
                        items.filter { it.namespace().contains(AngularFileFormat.RoutingModule) }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {
                                    su.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }
                                else {
                                    su.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }}/${it.name().replace(AngularDerivedType.RoutingModules, "")
                                .replaceFirstChar { it.lowercase(Locale.getDefault()) }}/${
                                it.name().replace(AngularDerivedType.RoutingModules, "")
                                    .replaceFirstChar { it.lowercase(Locale.getDefault()) }
                            }-${
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
                        && it.namespace().contains(AngularFileFormat.Module, true)
                }
                    .groupBy { it.findParentMust(StructureUnitI::class.java) }

                elementModules.isNotEmpty().then {
                    "${elementModules.map { (su, items) ->
                        items.filter { it.namespace().contains(AngularFileFormat.Module) }.sortedBy { it.name() }.
                        joinToString("\n") {
                            """import {${it.name()}} from '@${
                                if (su.parent().isEMPTY()) {
                                    su.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }
                                else {
                                    su.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
                                }}/${it.name().replace(AngularDerivedType.Module, "")
                                .replaceFirstChar { it.lowercase(Locale.getDefault()) }}/${
                                it.name().replace(AngularDerivedType.Module, "")
                                    .replaceFirstChar { it.lowercase(Locale.getDefault()) }
                            }-${
                                    it.namespace().substringAfterLast(su.namespace()).substringAfter("-")}'"""
                        }
                    }.joinToString(nL)}$nL$nL"
                }
            }
        }

        return toImportsOutsideTypes().trimIndent() +
                toImportsViewComponent().trimIndent() +
                toImportsAggregateViewComponent().trimIndent() +
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
    siemens.initObjectTrees()
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

val angularBasicComponent = AngularNames("basic", true, "component")
val angularModuleViewComponent = AngularNames("module-view", false, "component")
val angularModule = AngularNames("model", false, "module")
val angularModuleService = AngularNames("module-view", false, "service")
val angularEntityViewComponent = AngularEntitiesComponentNames("entity-view", "components/view", "component")
val angularEntityFormComponent = AngularEntitiesComponentNames("entity-form", "components/form", "component")
val angularEntityListComponent = AngularEntitiesComponentNames("entity-list", "components/list", "component")
val angularAggregateEntityComponent = AngularEntitiesComponentNames("entity-aggregate-view", "components/aggregateView", "component")
val angularEntityService = AngularEntitiesComponentNames("data", "service", "service")
val angularRoutingModule = AngularNames("routing", false, "module")
val angularEnumComponent = AngularNames("enum", true, "component")

val english = TranslateNames("en")
val germany = TranslateNames("de")
val france = TranslateNames("fr")
val spain = TranslateNames("es")

val itemAndTemplateNameAsTsFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}.ts")
}
val templateNameAsTsFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("$name.ts")
}
val itemNameAsTsFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name()}.ts")
}

val itemNameAsJsonFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name()}.json")
}

val itemAndTemplateNameAsHTMLFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}.html")
}
val templateNameAsHTMLFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("$name.html")
}
val itemNameAsHTMLFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name()}.html")
}

val itemAndTemplateNameAsCSSFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}.scss")
}
val templateNameAsCSSFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("$name.scss")
}
val itemNameAsCSSFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name()}.scss")
}

class AngularNames(private val elementType: String, private val isChild: Boolean, private val componentName: String) {

    val ts: TemplateI<*>.(CompositeI<*>) -> Names = baseName("ts")

    val html: TemplateI<*>.(CompositeI<*>) -> Names = baseName("html")

    val scss: TemplateI<*>.(CompositeI<*>) -> Names = baseName("scss")

    private fun baseName(extension: String): TemplateI<*>.(CompositeI<*>) -> Names = {
        Names("${it.toAngularComponentFileNameBase(elementType, isChild, componentName)}.$extension")
    }
}

class TranslateNames(private val element: String) {

    val json: TemplateI<*>.(CompositeI<*>) -> Names = baseName("json")

    private fun baseName(extension: String): TemplateI<*>.(CompositeI<*>) -> Names = {
        Names("${element}.$extension")
    }
}

class AngularEntitiesComponentNames(private val elementType: String, private val componentName: String, private val format: String) {

    val ts: TemplateI<*>.(CompositeI<*>) -> Names = baseName("ts")

    val html: TemplateI<*>.(CompositeI<*>) -> Names = baseName("html")

    val scss: TemplateI<*>.(CompositeI<*>) -> Names = baseName("scss")

    private fun baseName(extension: String): TemplateI<*>.(CompositeI<*>) -> Names = {
        Names("${it.toAngularEntityFileNameBase(elementType, componentName, format)}.$extension")
    }
}

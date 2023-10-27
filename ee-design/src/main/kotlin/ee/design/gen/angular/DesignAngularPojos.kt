import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toCamelCase
import ee.design.CompI
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.*
import java.util.*
import kotlin.collections.ArrayList

fun <T : ModuleI<*>> T.toAngularModule(c: GenerationContext, Module: String = AngularDerivedType.Module, components: List<CompI<*>> = listOf()): String {
    return """ 
export function HttpLoaderFactory(http: ${c.n(angular.commonhttp.HttpClient)}) {
    return new ${c.n(ngxtranslate.httploader.TranslateHttpLoader)}(http, '/app/shared/${components.first().name().lowercase(Locale.getDefault())}/assets/i18n/', '.json');
}

@${c.n(angular.core.NgModule)}({
    declarations: [
        ${c.n(this, AngularDerivedType.ModuleViewComponent)},
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationEntities(c, tab + tab)
    }}
${this.entities().filter { !it.isEMPTY() && !it.props().isEmpty() && it.belongsToAggregate().isNotEMPTY()}.joinSurroundIfNotEmptyToString {
        it.belongsToAggregate().toAngularModuleDeclarationAggregateEntities(c, tab + tab)
    }}
${this.values().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationValuesImport(c, tab + tab)
    }}
${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationBasics(c, tab + tab)
    }}
${this.enums().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationEnums(c, tab + tab) 
    }}
    ],
    imports: [
        ${c.n(this, AngularDerivedType.RoutingModules)},
        ${c.n(module.template.TemplateModule)},
        ${c.n(angular.common.CommonModule)},
        ${c.n(angular.forms.FormsModule)},
        ${c.n(angular.forms.ReactiveFormsModule)},
        ${c.n(module.material.MaterialModule)},
        ${c.n(siemens.ixangular.IxModule)},
        ${c.n(ngxtranslate.core.TranslateModule)}.forChild({
            loader: {provide: ${c.n(ngxtranslate.core.TranslateLoader)}, useFactory: HttpLoaderFactory, deps: [${c.n(angular.commonhttp.HttpClient)}]},
        }),
        ${this.entities().any { entity ->
        entity.props().any {
            it.type().parent().name() != this.name() && it.type().parent().name().first().isUpperCase()
        }
    }.then {this.toAngularImportOtherModulesOnImportPart(c)}}
        ${this.values().any { value ->
        value.props().any {
            it.type().parent().name() != this.name() && it.type().parent().name().first().isUpperCase()
        }
    }.then {this.toAngularImportOtherModulesOnImportPart(c)}}
        
    ],
    providers: [
        { provide: ${c.n(ngxtranslate.core.TranslateService)}, useExisting: ${c.n(module.services.TemplateTranslateService)} }
    ],
    exports: [
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleExportViews(c, tab + tab)
    }}
${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationBasics(c, tab + tab)
    }}
${this.enums().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationEnums(c, tab + tab)
    }}
${this.values().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationValues(c, tab + tab)
    }}
    ]
})
export class ${this.name()}${Module} {}"""
}

fun <T : ModuleI<*>> T.toAngularImportOtherModulesOnImportPart(c: GenerationContext): String {
    val sb = StringBuilder()
    val importedOtherModules: MutableList<String> = ArrayList()
    this.entities().forEach { entity ->
        entity.props().filter { it.type().parent().name() != this.name() && it.type().parent().name().first().isUpperCase() && it.type().parent().name() != "List" }.forEach {
            importedOtherModules.add("${c.n(it.type().parent(), AngularDerivedType.Module)},")
        }
    }
    this.values().forEach { value ->
        value.props().filter { it.type().parent().name() != this.name() && it.type().parent().name().first().isUpperCase() && it.type().parent().name() != "List" }.forEach {
            importedOtherModules.add("${c.n(it.type().parent(), AngularDerivedType.Module)},")
        }
    }
    importedOtherModules.distinct().forEach {
        sb.append(it)
        sb.append(("\n${tab + tab}"))
    }
    return sb.toString()
}

fun <T : ModuleI<*>> T.toAngularRoutingModule(c: GenerationContext, RoutingModules: String = AngularDerivedType.RoutingModules): String {
    return """
const routes: ${c.n(angular.router.Routes)} = [
    { path: '', component: ${c.n(this, AngularDerivedType.ModuleViewComponent)} },
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularEntityModulePath(c, tab)
    }}
${this.entities().filter { !it.isEMPTY() && !it.props().isEmpty() && it.belongsToAggregate().isNotEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.belongsToAggregate().toAngularAggregateEntityModulePath(c, tab)
    }}
${this.entities().filter { !it.isEMPTY() && !it.props().isEmpty() && it.belongsToAggregate().isNotEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.belongsToAggregate().toAngularAggregateEntityPropsModulePath(c, tab)
    }}
${this.values().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularValueModulePath(c, tab)
    }}
];

@${c.n(angular.core.NgModule)}({
    imports: [${c.n(angular.router.RouterModule)}.forChild(routes)],
    exports: [${c.n(angular.router.RouterModule)}],
})
export class ${this.name()}${RoutingModules} {}

"""
}

fun <T : ModuleI<*>> T.toAngularModuleHTMLComponent(c: GenerationContext, ViewService: String = AngularDerivedType.ViewService): String {
    return """
<app-page [pageName]="${this.name().lowercase(Locale.getDefault())}${ViewService}.pageName" [pageElement]="${this.name()
        .lowercase(Locale.getDefault())}${ViewService}.pageElement" [tabElement]="${this.name()
        .lowercase(Locale.getDefault())}${ViewService}.tabElement"
        [componentName]="${this.name().lowercase(Locale.getDefault())}${ViewService}.componentName"></app-page>
"""
}

fun <T : ModuleI<*>> T.toAngularDefaultSCSS(c: GenerationContext): String {
    return this.toAngularDefaultSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewHTMLComponent(c: GenerationContext, entities: List<EntityI<*>> = listOf(), DataService: String = AngularDerivedType.DataService): String {
    val serviceName = if(this.parent().name().equals(this.name(), ignoreCase = true)) {this.parent().name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} else {this.parent().name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) } + this.name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}
    return """
<module-${this.parent().name().lowercase(Locale.getDefault())} [componentName]="${serviceName}${DataService}.componentName" [tabElement]="tabElement"></module-${this.parent().name().lowercase(Locale.getDefault())}>

<ng-container *ngIf="isSpecificView; else normalForm">

    <ng-container *ngIf="${serviceName}${DataService}.isEdit; else notSpecificEdit">
        <entity-${this.parent().name().lowercase(Locale.getDefault())}-${this.name().lowercase(Locale.getDefault())}-form class="form-style" [${this.name().lowercase(Locale.getDefault())}]="${this.name().lowercase(Locale.getDefault())}" [isDisabled]="false"></entity-${this.parent().name().lowercase(Locale.getDefault())}-${this.name()
            .lowercase(Locale.getDefault())}-form>
        <button type="button" class="first-button-edit btn btn-outline-danger" (click)="${serviceName}${DataService}.goBackAndClearStorage()">{{'cancel edit' | translate}}</button>
        <button type="button" class="second-button-edit btn btn-outline-success" (click)="${serviceName}${DataService}.editSpecificElement(${this.name()
            .lowercase(Locale.getDefault())}, '${this.name().lowercase(Locale.getDefault())}'); ${entities.any { it.belongsToAggregate().derivedAsType().isEmpty() && it.belongsToAggregate().isNotEMPTY() && it.belongsToAggregate().name().equals(this.name(), true) }.then { """${serviceName}${DataService}.generateYAML();""" }} ${serviceName}${DataService}.goBack()">{{'save changes' | translate}}</button>
    </ng-container>

    <ng-template #notSpecificEdit>
        <span class="material-icons edit-button-specific" (click)="${serviceName}${DataService}.editItems(0, ${this.name()
            .lowercase(Locale.getDefault())})">more_horiz</span>
        <entity-${this.parent().name().lowercase(Locale.getDefault())}-${this.name().lowercase(Locale.getDefault())}-form class="form-style" [${this.name().lowercase(Locale.getDefault())}]="${this.name().lowercase(Locale.getDefault())}" [isDisabled]="true"></entity-${this.parent().name().lowercase(Locale.getDefault())}-${this.name()
            .lowercase(Locale.getDefault())}-form>
    </ng-template>
</ng-container>

<ng-template #normalForm>
    <entity-${this.parent().name().lowercase(Locale.getDefault())}-${this.name().lowercase(Locale.getDefault())}-form class="form-style" [${this.name().lowercase(Locale.getDefault())}]="${this.name().lowercase(Locale.getDefault())}"></entity-${this.parent().name().lowercase(Locale.getDefault())}-${this.name()
        .lowercase(Locale.getDefault())}-form>
    <ng-container *ngIf="${serviceName}${DataService}.isEdit; else notEdit">
        <button type="button" class="first-button-edit btn btn-outline-danger" (click)="${serviceName}${DataService}.goBackAndClearStorage()">{{'cancel edit' | translate}}</button>
        <button type="button" class="second-button-edit btn btn-outline-success" (click)="${serviceName}${DataService}.editElement(${this.name()
            .lowercase(Locale.getDefault())}); ${entities.any { it.belongsToAggregate().derivedAsType().isEmpty() && it.belongsToAggregate().isNotEMPTY() && it.belongsToAggregate().name().equals(this.name(), true) }.then { """${serviceName}${DataService}.generateYAML();""" }} ${serviceName}${DataService}.goBack()">{{'save changes' | translate}}</button>
    </ng-container>
    
    <ng-template #notEdit>
        <ng-container *ngIf="${serviceName}${DataService}.isSpecificNew; else notSpecific">
            <button type="button" class="first-button btn btn-outline-danger" (click)="${serviceName}${DataService}.goBackAndClearStorage()">{{'cancel' | translate}}</button>
            <button type="button" class="second-button btn btn-outline-success" (click)="${serviceName}${DataService}.inputElementSpecific(${this.name()
                .lowercase(Locale.getDefault())}, '${this.name().lowercase(Locale.getDefault())}'); ${entities.any { it.belongsToAggregate().derivedAsType().isEmpty() && it.belongsToAggregate().isNotEMPTY() && it.belongsToAggregate().name().equals(this.name(), true) }.then { """${serviceName}${DataService}.generateYAML();""" }} ${serviceName}${DataService}.goBack() ${entities.filter { entity -> entity.props().any {property ->
        ((property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( entity.props().any {
            childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && !childProperty.type().name().equals("list", true) && childProperty.type().namespace().equals(this.namespace(), true) } ||
                property.type().props().any {
                    childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && !childProperty.type().name().equals("list", true) && childProperty.type().namespace().equals(this.namespace(), true) }
                ) || (entity.isNotEMPTY() && entity.name().equals(this.name(), true) && entity.namespace().equals(this.namespace(), true)))
    }
    }.joinSurroundIfNotEmptyToString("") {"""; ${serviceName}${DataService}.saveElementFor${serviceName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(${this.name()
            .lowercase(Locale.getDefault())})""" }
    }">{{'save' | translate}}</button>
        </ng-container>
        
        <ng-template #notSpecific>
            <button type="button" class="first-button btn btn-outline-danger" (click)="${serviceName}${DataService}.goBackAndClearStorage()">{{'cancel' | translate}}</button>
            <button type="button" class="second-button btn btn-outline-success" (click)="${serviceName}${DataService}.inputElement(${this.name()
            .lowercase(Locale.getDefault())}); ${entities.any { it.belongsToAggregate().derivedAsType().isEmpty() && it.belongsToAggregate().isNotEMPTY() && it.belongsToAggregate().name().equals(this.name(), true) }.then { """${serviceName}${DataService}.generateYAML();""" }} ${serviceName}${DataService}.goBack() ${entities.filter { entity -> entity.props().any {property ->
        ((property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( entity.props().any {
            childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && !childProperty.type().name().equals("list", true) && childProperty.type().namespace().equals(this.namespace(), true) } ||
                property.type().props().any {
                    childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && !childProperty.type().name().equals("list", true) && childProperty.type().namespace().equals(this.namespace(), true) }
                ) || (entity.isNotEMPTY() && entity.name().equals(this.name(), true) && entity.namespace().equals(this.namespace(), true)))
    }
    }.joinSurroundIfNotEmptyToString("") {"""; ${serviceName}${DataService}.saveElementFor${serviceName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(${this.name()
            .lowercase(Locale.getDefault())})""" }
    }">{{'save' | translate}}</button>
        </ng-template>
    </ng-template>
</ng-template>"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewSCSSComponent(c: GenerationContext): String {
    return this.toAngularDefaultSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularFormHTMLComponent(c: GenerationContext, DataService: String = AngularDerivedType.DataService): String {
    return """
<div class="${this.name().lowercase(Locale.getDefault())}-form">
    <form [formGroup]="form">
        ${this.props().any {  it.type() !is BasicI<*> && it.type() !is EntityI<*> && it.type() !is ValuesI<*> }.then { 
            """
        <fieldset>
            <legend>{{"${if(this.name().equals(this.parent().name(), true)) {
                this.name().lowercase(Locale.getDefault())
            } else {"""${this.parent().name().lowercase(Locale.getDefault())}${this.name().lowercase(Locale.getDefault())}"""}}.navTitle" | translate}}</legend>
            ${this.props().filter { it.type() !is BasicI<*> && it.type() !is EntityI<*> && it.type() !is ValuesI<*> }.joinSurroundIfNotEmptyToString(nL) {
                when(it.type().name().lowercase(Locale.getDefault())) {
                    "boolean" -> it.toHTMLBooleanForm(tab, false)
                    "date" -> it.toHTMLDateForm(tab, false)
                    "string", "text" -> it.toHTMLStringForm(tab, "", false)
                    "blob" -> it.toHTMLUploadForm(tab, false)
                    "float", "int" -> it.toHTMLNumberForm(tab, false)
                    else -> when(it.type()) {
                        is EnumTypeI<*> -> it.toHTMLEnumForm(tab, it.type().name(), it.type().parent().name())
                        else -> ""
                    }
                }
            }}
    
            ${this.props().filter { it.type().name().lowercase(Locale.getDefault()) == "blob" }.joinSurroundIfNotEmptyToString(nL) {
                """<div>
                <img *ngFor='let preview of ${it.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.previews' [src]="preview" class="preview">
            </div>"""
            }}
        </fieldset>
            """
    }}
       
    ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString(nL) {
        when(it.type()) {
            is BasicI<*> -> it.toHTMLObjectForm(it.type().name(), it.type().parent().name(), false)
            is EntityI<*> -> it.toHTMLObjectFormEntity(it.type().parent().name(), it.type().name(), it.type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })
            is ValuesI<*> -> it.toHTMLObjectFormValues(it.type().parent().name(), it.type().name(), it.type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })
            else ->  when(it.type().name().lowercase(Locale.getDefault())) {
                "list" -> when(it.type().generics().first().type()) {
                    is EntityI<*>, is ValuesI<*> -> it.toHTMLObjectFormEntityMultiple(it.type().generics().first().type().parent().name(), it.type().generics().first().type().name(), it.type().generics().first().type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })
                    is EnumTypeI<*> -> it.toHTMLObjectFormEnumMultiple(it.type().generics().first().type().parent().name(), it.type().generics().first().type().name(),
                            this.parent().name().lowercase(Locale.getDefault()), false, it.type().generics().first().type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() }
                    )
                    else -> it.toHTMLObjectFormEntityMultiple(it.type().generics().first().type().parent().name(), it.type().generics().first().type().name(), it.type().generics().first().type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })
                }
                else -> ""
            }
        }
    }}    
    </form>
</div>
"""
}

fun <T : CompilationUnitI<*>> T.toAngularFormSCSSComponent(c: GenerationContext, derived: String = AngularFileFormat.EntityForm): String {
    return """
@import "src/styles";

.${this.name().lowercase(Locale.getDefault())}-form {
    @extend .${derived}
}

${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString(nL) {
        when (it.type()) {
            is BasicI<*>, is EntityI<*>, is ValuesI<*> -> it.toSCSSOtherFormStyle(it.type().name())
            else -> ""
        }
    }
    }

a {
    @extend .entity-link
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListHTMLComponent(c: GenerationContext, DataService: String = AngularDerivedType.DataService, isAggregateView: Boolean = false, containAggregateProp: Boolean = false, entities: List<EntityI<*>>): String {
    val serviceName = if(this.parent().name().equals(this.name(), true)) {this.parent().name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} else {this.parent().name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) } + this.name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}
    return """
<ng-container *ngIf="!isSpecificView; else isSpecific">        
    <module-${this.parent().name().lowercase(Locale.getDefault())}></module-${this.parent().name().lowercase(Locale.getDefault())}>
</ng-container>

<ng-template #isSpecific>
    <module-${this.parent().name().lowercase(Locale.getDefault())} [componentName]="${serviceName}${DataService}.componentName" [tabElement]="tabElement"></module-${this.parent().name().lowercase(Locale.getDefault())}>
</ng-template>

<div class="${this.name().lowercase(Locale.getDefault())}-list-button">
    <ng-container *ngIf="!isSpecificView; else isSpecificButton">
        <a class="newButton normal-font-size" [routerLink]="'./new'" routerLinkActive="active-link">
            <ix-icon name="add-circle" size="20"></ix-icon> {{"add" | translate}} {{"new" | translate}} {{"item" | translate}}
        </a>
    </ng-container>

    <ng-template #isSpecificButton>
        <a class="newButton normal-font-size" [routerLink]="'/${this.parent().name().lowercase(Locale.getDefault())}/${this.name().lowercase(Locale.getDefault())}/new'" routerLinkActive="active-link">
            <ix-icon name="add-circle" size="20"></ix-icon> {{"add" | translate}} {{"new" | translate}} {{"item" | translate}}
        </a>
    </ng-template>
    
    <ng-container *ngIf="${serviceName}${DataService}.isHidden; else showed">
        <a class="showButton normal-font-size" (click)="${serviceName}${DataService}.toggleHidden()">
            <ix-icon name="trashcan" size="20"></ix-icon> {{"delete" | translate}}...
        </a>
    </ng-container>
    
    <ng-template #showed>
        <a class="deleteButton normal-font-size" (click)="${serviceName}${DataService}.clearMultipleItems(${serviceName}${DataService}.selection.selected); ${serviceName}${DataService}.toggleHidden(); ${if(containAggregateProp) {"""${serviceName}${DataService}.removeAggregateItem(${serviceName}${DataService}.selection.selected)"""} else {""""""}}">
            <ix-icon name="trashcan" size="20"></ix-icon> {{"delete" | translate}} {{"item" | translate}}
        </a>
    </ng-template>
    
    <a class="loadButton normal-font-size" (click)="this.${serviceName}${DataService}.addMockupServiceDataToLocalStorage(this.${serviceName}${DataService}.getDataFromMockupService())">
        <ix-icon name="paste" size="20"></ix-icon> {{"load" | translate}} {{"configuration" | translate}}
    </a>
</div>

<ng-container *ngIf="data.length > 0; else emptyState">
    <div class="mat-elevation-z8 ${this.name().lowercase(Locale.getDefault())}-list">
        <table class="table table-striped theme-classic-dark">
            <thead>
                <tr>
                    <th width="10%">
                        <div class="form-group">
                            <section [style.visibility]="${serviceName}${DataService}.isHidden? 'hidden': 'visible'">
                                <div style="margin-bottom: 1rem">
                                    <input type="checkbox"  id="checkbox"
                                           (change)="${"$"}event ? ${serviceName}${DataService}.masterToggle() : null"
                                           [checked]="${serviceName}${DataService}.selection.hasValue() && ${serviceName}${DataService}.allRowsSelected()"
                                           [indeterminate]="${serviceName}${DataService}.selection.hasValue() && !${serviceName}${DataService}.allRowsSelected()"/>
                                    <label for="checkbox"></label>
                                </div>
                            </section>
                        </div>
                    </th>
                    <th width="10%">{{"table.action" | translate}}</th>
                    ${this.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL + tab + tab + tab + tab) {
            """<th>{{"${serviceName.lowercase(Locale.getDefault())}.table.${it.name().lowercase(Locale.getDefault())}" | translate}}</th>"""
        }}
                </tr>
            </thead>
            <tbody>
                <tr *ngFor="let row of data; let i = index">
                    <td>
                        <section [style.visibility]="${serviceName}${DataService}.isHidden? 'hidden': 'visible'">
                            <div style="margin-bottom: 1rem">
                                <input type="checkbox" id="checkbox_{{i}}"
                                       (click)="${"$"}event.stopPropagation()"
                                       (change)="${"$"}event ? ${serviceName}${DataService}.selection.toggle(row) : null"
                                       [checked]="${serviceName}${DataService}.selection.isSelected(row) || ${serviceName}${DataService}.allRowsSelected()"/>
                                <label for="checkbox_{{i}}"></label>
                            </div>
                        </section>
                    </td>
                    <td>
                        <ix-icon name="context-menu" size="16" #trigger></ix-icon>
                        <ix-dropdown [ixDropdownTrigger]="trigger">
                            <ix-dropdown-item>
                                <a (click)="${serviceName}${DataService}.editItems(i, row)">
                                    <ix-icon name="pen" size="16"></ix-icon> {{'edit' | translate}}
                                </a>
                            </ix-dropdown-item>
                            <ix-dropdown-item>
                                <a (click)="${serviceName}${DataService}.removeItem(row); ${if(containAggregateProp) {"""${serviceName}${DataService}.removeAggregateItem(row['${entities.filter { !it.isEMPTY() && !it.props().isEMPTY() && !it.belongsToAggregate().isEMPTY() }.joinSurroundIfNotEmptyToString { it.belongsToAggregate().props().filter { prop -> prop.name().contains(it.name(), true) }.joinSurroundIfNotEmptyToString { prop -> prop.name() } }}'])"""} else {""""""}}">
                                    <ix-icon name="trashcan" size="16"></ix-icon> {{'delete' | translate}}
                                </a>
                            </ix-dropdown-item>
                        </ix-dropdown>
                    </td>
                    ${toAngularTableListBasic(this.name(), "", "",false, this.props().size, containAggregateProp)}
                </tr>
            </tbody>
        </table>
    </div>
</ng-container>

<ng-template class ="emptyState" #emptyState>
    <ix-empty-state
        header="No elements available"
        subHeader="Create an element first"
        icon="add"
        action="Create New Item"
        [routerLink]="'/${this.parent().name().lowercase(Locale.getDefault())}/${this.name().lowercase(Locale.getDefault())}/new'"
        routerLinkActive="active-link"
    ></ix-empty-state>
</ng-template>"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityAggregateViewHTMLComponent(c: GenerationContext, DataService: String = AngularDerivedType.DataService, isAggregateView: Boolean = false, containAggregateProp: Boolean = false): String {
    val serviceName = if(this.parent().name().equals(this.name(), ignoreCase = true)) {this.parent().name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} else {this.parent().name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) } + this.name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}
    return """
<module-${this.parent().name().lowercase(Locale.getDefault())} [componentName]="${serviceName}${DataService}.componentName" [tabElement]="tabElement"></module-${this.parent().name().lowercase(Locale.getDefault())}>

<span class="material-icons edit-button-specific" (click)="${serviceName}${DataService}.editItems(0, ${this.name()
            .lowercase(Locale.getDefault())})">more_horiz</span>
            
<entity-${this.parent().name().lowercase(Locale.getDefault())}-${this.name().lowercase(Locale.getDefault())}-form class="form-style" [${this.name().lowercase(Locale.getDefault())}]="${this.name().lowercase(Locale.getDefault())}" [isDisabled]="true"></entity-${this.parent().name().lowercase(Locale.getDefault())}-${this.name()
    .lowercase(Locale.getDefault())}-form>   

"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListSCSSComponent(c: GenerationContext, derived: String = AngularFileFormat.EntityList): String {
    return """
@import "src/styles";

.${this.name().lowercase(Locale.getDefault())}-list {
    @extend .${derived};
}

${if(this.props().size > 3) { 
    """
si-table {
    width: ${(this.props().size * 300) + 300}px
}"""
} else {""}}

.${this.name().lowercase(Locale.getDefault())}-list-button {
    @extend .${derived}-button
}

.emptyState {
    position: absolute;
    top: 30%;
    left: 45%;
}

a {
    @extend .entity-link
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularBasicHTMLComponent(c: GenerationContext, derived: String = AngularFileFormat.BasicForm): String {
    return """
<div class="${this.name().lowercase(Locale.getDefault())}${derived}">
    <fieldset>
        <legend>{{parentName + ".navTitle" | translate}} {{"${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}" | translate}}</legend>
            ${this.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        when(it.type()) {
            is EnumTypeI<*> -> it.toHTMLEnumForm("", it.type().name(), it.type().parent().name())
            is BasicI<*> -> it.toHTMLObjectForm(it.type().name(), it.type().parent().name(), true)
            is EntityI<*>, is ValuesI<*> -> it.toHTMLObjectFormEntityForBasic(it.type().name(), it.type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })
            else -> when(it.type().name().lowercase(Locale.getDefault())) {
                "boolean" -> it.toHTMLBooleanForm("", true)
                "date" -> it.toHTMLDateForm("", true)
                "float", "int" -> it.toHTMLNumberForm("", true)
                "blob" -> it.toHTMLUploadForm("", true)
                "list" -> when(it.type().generics().first().type()) {
                    is EntityI<*>, is ValuesI<*> -> it.toHTMLObjectFormBasicFromEntityMultiple(it.type().generics().first().type().name(), it.type().generics().first().type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })
                    is EnumTypeI<*> -> it.toHTMLObjectFormEnumMultiple(it.type().generics().first().type().parent().name(), it.type().generics().first().type().name(),
                            this.parent().name().lowercase(Locale.getDefault()), true, it.type().generics().first().type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() }
                    )
                    else -> {it.toHTMLStringForm("", this.parent().name().lowercase(Locale.getDefault()), true)}
                }
                else -> {it.toHTMLStringForm("", this.parent().name().lowercase(Locale.getDefault()), true)}
            }
        }
    }}   
    </fieldset>
</div>
"""
}

fun <T : CompilationUnitI<*>> T.toAngularBasicSCSSComponent(c: GenerationContext, derived: String = AngularFileFormat.BasicForm): String {
    return """
@import "src/styles";

.${this.name().lowercase(Locale.getDefault())}${derived} {
    @extend .fieldset-form
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEnumHTMLComponent(c: GenerationContext): String {
    return """
<fieldset>
    <legend>{{ componentName | translate}}</legend>
    <ix-select [readonly]="isDisabled" [allowClear]="!isDisabled" mode="{{mode}}" (itemSelectionChange)="(mode === 'multiple') ? changeValueMultiple(${'$'}event) : changeValue(${'$'}event)" [selectedIndices]="multipleSelectedIndices" i18nSelectListHeader="{{'select' | translate}} {{ componentName | translate}}" i18nPlaceholder="{{'select' | translate}} {{ componentName | translate}}">
        <ix-select-item *ngFor="let item of enumElements; let i = index" label="{{item}}" value="{{i}}"></ix-select-item>
    </ix-select>
</fieldset>"""
}

fun <T : CompilationUnitI<*>> T.toAngularEnumSCSSComponent(c: GenerationContext): String {
    return this.toAngularDefaultSCSS()
}

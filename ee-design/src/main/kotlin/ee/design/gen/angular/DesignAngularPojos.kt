import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.*
import java.util.*
import kotlin.collections.ArrayList

fun <T : ModuleI<*>> T.toAngularModule(c: GenerationContext, Module: String = AngularDerivedType.Module): String {
    return """ 
export function HttpLoaderFactory(http: ${c.n(angular.commonhttp.HttpClient)}) {
    return new ${c.n(ngxtranslate.httploader.TranslateHttpLoader)}(http);
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
        ${c.n(module.simpl.SimplModule)},
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

fun <T : CompilationUnitI<*>> T.toAngularEntityViewHTMLComponent(c: GenerationContext, DataService: String = AngularDerivedType.DataService): String {
    return """
<module-${this.parent().name().lowercase(Locale.getDefault())} [componentName]="${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.componentName" [tabElement]="tabElement"></module-${this.parent().name().lowercase(Locale.getDefault())}>

<ng-container *ngIf="isSpecificView; else normalForm">
    <entity-${this.parent().name().lowercase(Locale.getDefault())}-${this.name().lowercase(Locale.getDefault())}-form class="form-style" [${this.name().lowercase(Locale.getDefault())}]="${this.name().lowercase(Locale.getDefault())}" [isDisabled]="true"></entity-${this.parent().name().lowercase(Locale.getDefault())}-${this.name()
        .lowercase(Locale.getDefault())}-form>
</ng-container>

<ng-template #normalForm>
    <entity-${this.parent().name().lowercase(Locale.getDefault())}-${this.name().lowercase(Locale.getDefault())}-form class="form-style" [${this.name().lowercase(Locale.getDefault())}]="${this.name().lowercase(Locale.getDefault())}"></entity-${this.parent().name().lowercase(Locale.getDefault())}-${this.name()
        .lowercase(Locale.getDefault())}-form>
</ng-template>

<ng-container *ngIf="!isSpecificView">
    <ng-container *ngIf="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.isEdit; else notEdit">
        <button type="button" class="first-button btn btn-outline-danger" (click)="goBack()"
                routerLinkActive="active-link">{{'cancel edit' | translate}}</button>
        <button type="button" class="second-button btn btn-outline-success" (click)="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.editElement(${this.name()
            .lowercase(Locale.getDefault())}); goBack()"
                routerLinkActive="active-link">{{'save changes' | translate}}</button>
    </ng-container>
    
    <ng-template #notEdit>
        <button type="button" class="first-button btn btn-outline-danger" (click)="goBack()"
                routerLinkActive="active-link">{{'cancel' | translate}}</button>
        <button type="button" class="second-button btn btn-outline-success" (click)="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.inputElement(${this.name()
            .lowercase(Locale.getDefault())}); goBack()"
                routerLinkActive="active-link">{{'save' | translate}}</button>
    </ng-template>
</ng-container>
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewSCSSComponent(c: GenerationContext): String {
    return """
.first-button {
    position: absolute;
    left: 16%;
    bottom: 14%;
}

.second-button {
    position: absolute;
    left: 22%;
    bottom: 14%;
}"""
}

fun <T : CompilationUnitI<*>> T.toAngularFormHTMLComponent(c: GenerationContext, DataService: String = AngularDerivedType.DataService): String {
    return """
<div class="${this.name().lowercase(Locale.getDefault())}-form">
    <form [formGroup]="form">
        ${this.props().any {  it.type() !is BasicI<*> && it.type() !is EntityI<*> && it.type() !is ValuesI<*> }.then { 
            """
        <fieldset>
            <legend>{{"${this.name().lowercase(Locale.getDefault())}.navTitle" | translate}}</legend>
            ${this.props().filter { it.type() !is BasicI<*> && it.type() !is EntityI<*> && it.type() !is ValuesI<*> }.joinSurroundIfNotEmptyToString(nL) {
                when(it.type().name().lowercase(Locale.getDefault())) {
                    "boolean" -> it.toHTMLBooleanForm(tab, false)
                    "date" -> it.toHTMLDateForm(tab, false)
                    "string", "text" -> it.toHTMLStringForm(tab, "", false)
                    "blob" -> it.toHTMLUploadForm(tab, false)
                    "float", "int" -> it.toHTMLNumberForm(tab, false)
                    else -> when(it.type()) {
                        is EnumTypeI<*> -> it.toHTMLEnumForm(tab, it.type().name(), it.type().parent().name(), true)
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
            is EntityI<*>, is ValuesI<*> -> it.toHTMLObjectFormEntity(it.type().name(), it.type().props(), it.type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })
            else ->  when(it.type().name().lowercase(Locale.getDefault())) {
                "list" -> when(it.type().generics().first().type()) {
                    is EntityI<*>, is ValuesI<*> -> it.toHTMLObjectFormEntityMultiple(it.type().generics().first().type().name(), it.type().generics().first().type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })
                    is EnumTypeI<*> -> it.toHTMLObjectFormEnumMultiple(it.type().generics().first().type().name(),
                        this.parent().name().lowercase(Locale.getDefault()), false, it.type().generics().first().type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() }
                    )
                    else -> ""
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
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListHTMLComponent(c: GenerationContext, DataService: String = AngularDerivedType.DataService, isAggregateView: Boolean = false, containAggregateProp: Boolean = false): String {
    return """
<ng-container *ngIf="!isSpecificView">        
    <module-${this.parent().name().lowercase(Locale.getDefault())}></module-${this.parent().name().lowercase(Locale.getDefault())}>
    <div class="${this.name().lowercase(Locale.getDefault())}-list-button">
        <a class="newButton bg-dark normal-font-size" [routerLink]="'./new'"
                routerLinkActive="active-link">
            <span aria-hidden='true' class='iconUxt addCircle filled'></span> {{"add" | translate}} {{"new" | translate}} {{"item" | translate}}
        </a>
        
        <ng-container *ngIf="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.isHidden; else showed">
            <a class="showButton bg-dark normal-font-size" (click)="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.toggleHidden()">
                <span aria-hidden='true' class='iconUxt delete filled'></span> {{"delete" | translate}}...
            </a>
        </ng-container>
        
        <ng-template #showed>
            <a class="deleteButton bg-dark normal-font-size" (click)="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.clearMultipleItems(${this.name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.selection.selected); ${this.name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.toggleHidden()">
                <span aria-hidden='true' class='iconUxt delete filled'></span> {{"delete" | translate}} {{"item" | translate}}
            </a>
        </ng-template>
    </div>
    
    <div class="mat-elevation-z8 ${this.name().lowercase(Locale.getDefault())}-list">
        <si-table [rows]="${this.name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.dataSources | async" [loading]="(${this.name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.dataSources | async) === null" [bordered]="false" [condensed]="true" [rowsPerPage]="10">
            <siTableColumn [disableSort]="true" [disableFilter]="true" [widthFactor]="0.5" key="box" name="Action">
                <div class="form-group" *siTableHeaderCell>
                    <section [style.visibility]="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.isHidden? 'hidden': 'visible'">
                        <input class="form-check-input" type="checkbox"
                                      (change)="${"$"}event ? ${this.name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.masterToggle() : null"
                                      [checked]="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.selection.hasValue() && ${this.name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.allRowsSelected()"
                                      [indeterminate]="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.selection.hasValue() && !${this.name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.allRowsSelected()">
                    </section>
                </div>
    
                <div *siTableCell="let row = row; let i = index">
                    <section [style.visibility]="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.isHidden? 'hidden': 'visible'">
                        <input class="form-check-input" type="checkbox"
                                      (click)="${"$"}event.stopPropagation()"
                                      (change)="${"$"}event ? ${this.name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.selection.toggle(row) : null"
                                      [checked]="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.selection.isSelected(row)">
                    </section>
                </div>
            </siTableColumn>
            <siTableColumn [disableSort]="true" [disableFilter]="true" [widthFactor]="0.8" class="header-style" key="action" name="{{'table.action' | translate}}">
                <div *siTableCell="let row = row; let i = index">
                    <mat-menu #appMenu="matMenu">
                        <ng-template matMenuContent>
                            <button mat-menu-item (click)="${this.name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.editItems(i, row)"><mat-icon>edit</mat-icon>
                                <span>{{"edit" | translate}}</span></button>
                            <button mat-menu-item (click)="${this.name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.removeItem(row)"><mat-icon>delete</mat-icon>
                                <span>{{"delete" | translate}}</span></button>
                        </ng-template>
                    </mat-menu>
    
                    <button mat-icon-button [matMenuTriggerFor]="appMenu">
                        <mat-icon>more_vert</mat-icon>
                    </button>
                </div>
            </siTableColumn>
            ${toAngularTableListBasic(this.name(), "", "",false, this.props().size, containAggregateProp)}
    
            <div no-data>
                Loading...
            </div>
        </si-table>
    </div>
</ng-container>
    
<ng-container *ngIf="isSpecificView">
    <module-${this.parent().name().lowercase(Locale.getDefault())} [componentName]="${this.name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.componentName" [tabElement]="tabElement"></module-${this.parent().name().lowercase(Locale.getDefault())}>
    
    <div class="mat-elevation-z8 ${this.name().lowercase(Locale.getDefault())}-list">
        <si-table [rows]="${this.name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.dataSources | async" [loading]="(${this.name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.dataSources | async) === null" [bordered]="false" [condensed]="true" [rowsPerPage]="10">
            <siTableColumn [disableSort]="true" [disableFilter]="true" [widthFactor]="0.5" key="box" name="Action">
                <div class="form-group" *siTableHeaderCell>
                    <section [style.visibility]="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.isHidden? 'hidden': 'visible'">
                        <input class="form-check-input" type="checkbox"
                                      (change)="${"$"}event ? ${this.name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.masterToggle() : null"
                                      [checked]="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.selection.hasValue() && ${this.name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.allRowsSelected()"
                                      [indeterminate]="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.selection.hasValue() && !${this.name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.allRowsSelected()">
                    </section>
                </div>
    
                <div *siTableCell="let row = row; let i = index">
                    <section [style.visibility]="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.isHidden? 'hidden': 'visible'">
                        <input class="form-check-input" type="checkbox"
                                      (click)="${"$"}event.stopPropagation()"
                                      (change)="${"$"}event ? ${this.name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.selection.toggle(row) : null"
                                      [checked]="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.selection.isSelected(row)">
                    </section>
                </div>
            </siTableColumn>
            <siTableColumn [disableSort]="true" [disableFilter]="true" [widthFactor]="0.8" class="header-style" key="action" name="{{'table.action' | translate}}">
                <div *siTableCell="let row = row; let i = index">
                    <mat-menu #appMenu="matMenu">
                        <ng-template matMenuContent>
                            <button mat-menu-item (click)="${this.name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.editItems(i, row)"><mat-icon>edit</mat-icon>
                                <span>{{"edit" | translate}}</span></button>
                            <button mat-menu-item (click)="${this.name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.removeItem(row)"><mat-icon>delete</mat-icon>
                                <span>{{"delete" | translate}}</span></button>
                        </ng-template>
                    </mat-menu>
    
                    <button mat-icon-button [matMenuTriggerFor]="appMenu">
                        <mat-icon>more_vert</mat-icon>
                    </button>
                </div>
            </siTableColumn>
            ${toAngularTableListBasic(this.name(), "", "",false, this.props().size, containAggregateProp)}
    
            <div no-data>
                Loading...
            </div>
        </si-table>
    </div>
</ng-container>    

"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityAggregateViewHTMLComponent(c: GenerationContext, DataService: String = AngularDerivedType.DataService, isAggregateView: Boolean = false, containAggregateProp: Boolean = false): String {
    return """
<module-${this.parent().name().lowercase(Locale.getDefault())} [componentName]="${this.name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.componentName" [tabElement]="tabElement"></module-${this.parent().name().lowercase(Locale.getDefault())}>
    
<entity-${this.parent().name().lowercase(Locale.getDefault())}-${this.name().lowercase(Locale.getDefault())}-form class="form-style" [${this.name().lowercase(Locale.getDefault())}]="${this.name().lowercase(Locale.getDefault())}" [isDisabled]="true"></entity-${this.parent().name().lowercase(Locale.getDefault())}-${this.name()
    .lowercase(Locale.getDefault())}-form>   

"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListSCSSComponent(c: GenerationContext, derived: String = AngularFileFormat.EntityList): String {
    return """
@import "src/styles";

.${this.name().lowercase(Locale.getDefault())}-list {
    @extend .${derived};
    position: absolute;
    width: 80% !important;
    z-index: 1;
    top: 40%;
    left: 10%;
    overflow-x: scroll;
    overflow-y: scroll;
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
            is EnumTypeI<*> -> it.toHTMLEnumForm("", it.type().name(), it.type().parent().name(), true)
            is BasicI<*> -> it.toHTMLObjectForm(it.type().name(), it.type().parent().name(), true)
            is EntityI<*>, is ValuesI<*> -> it.toHTMLObjectFormEntityForBasic(it.type().name(), it.type().props(), it.type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })
            else -> when(it.type().name().lowercase(Locale.getDefault())) {
                "boolean" -> it.toHTMLBooleanForm("", true)
                "date" -> it.toHTMLDateForm("", true)
                "float", "int" -> it.toHTMLNumberForm("", true)
                "blob" -> it.toHTMLUploadForm("", true)
                "list" -> when(it.type().generics().first().type()) {
                    is EntityI<*>, is ValuesI<*> -> it.toHTMLObjectFormBasicFromEntityMultiple(it.type().generics().first().type().name(), it.type().generics().first().type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })
                    is EnumTypeI<*> -> it.toHTMLObjectFormEnumMultiple(it.type().generics().first().type().name(),
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
<si-form-group label="{{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
    <si-dropdown [dropdownOptions]="enumElements" (optionSelected)="changeValue(${'$'}event)" inputId="${this.parent().name().toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${this.name().toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                 [(ngModel)]="${this.name().lowercase(Locale.getDefault())}"
                 [disabled]="isDisabled">
        <ng-container *siDropdownOption="let value = value">
            <span matTooltip="{{ dataService.tooltipText }}" (mouseenter)="dataService.onMouseEnter(value)" (mouseleave)="dataService.onMouseLeave()">{{ value }}</span>
        </ng-container>
    </si-dropdown>
</si-form-group>
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEnumSCSSComponent(c: GenerationContext): String {
    return this.toAngularDefaultSCSS()
}

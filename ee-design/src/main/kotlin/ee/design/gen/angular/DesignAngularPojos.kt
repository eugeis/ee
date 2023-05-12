import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
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
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModulePath(c, tab)
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
        .lowercase(Locale.getDefault())}${ViewService}.tabElement"></app-page>
"""
}

fun <T : ModuleI<*>> T.toAngularDefaultSCSS(c: GenerationContext): String {
    return this.toAngularDefaultSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewHTMLComponent(c: GenerationContext, DataService: String = AngularDerivedType.DataService): String {
    return """
<module-${this.parent().name().lowercase(Locale.getDefault())}></module-${this.parent().name().lowercase(Locale.getDefault())}>

<entity-${this.parent().name().lowercase(Locale.getDefault())}-${this.name().lowercase(Locale.getDefault())}-form [${this.name().lowercase(Locale.getDefault())}]="${this.name().lowercase(Locale.getDefault())}"></entity-${this.parent().name().lowercase(Locale.getDefault())}-${this.name()
        .lowercase(Locale.getDefault())}-form>

<ng-container *ngIf="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.isEdit; else notEdit">
    <button mat-raised-button [routerLink]="'../../'"
            routerLinkActive="active-link">{{'cancel edit' | translate}}</button>
    <button mat-raised-button (click)="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.editElement(${this.name()
        .lowercase(Locale.getDefault())})" [routerLink]="'../../'"
            routerLinkActive="active-link">{{'save changes' | translate}}</button>
</ng-container>

<ng-template #notEdit>
    <button mat-raised-button [routerLink]="'../'"
            routerLinkActive="active-link">{{'cancel' | translate}}</button>
    <button mat-raised-button (click)="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.inputElement(${this.name()
        .lowercase(Locale.getDefault())})" [routerLink]="'../'"
            routerLinkActive="active-link">{{'save' | translate}}</button>
</ng-template>
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewSCSSComponent(c: GenerationContext): String {
    return """
button {
    position: relative;
    left: 10%;
}

"""
}

fun <T : CompilationUnitI<*>> T.toAngularFormHTMLComponent(c: GenerationContext, DataService: String = AngularDerivedType.DataService): String {
    return """
<div class="${this.name().lowercase(Locale.getDefault())}-form">
    <form>
        <fieldset>
            <legend>{{"table.${this.name().lowercase(Locale.getDefault())}" | translate}}</legend>
            ${this.props().filter { it.type() !is BasicI<*> && it.type() !is EntityI<*> && it.type() !is ValuesI<*> }.joinSurroundIfNotEmptyToString(nL) {
        when(it.type().name().lowercase(Locale.getDefault())) {
            "boolean" -> it.toHTMLBooleanForm(tab)
            "date", "list" -> it.toHTMLDateForm(tab)
            "string", "text" -> it.toHTMLStringForm(tab)
            "blob" -> it.toHTMLUploadForm(tab)
            "float", "int" -> it.toHTMLNumberForm(tab)
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
        ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString(nL) {
        when(it.type()) {
            is BasicI<*> -> it.toHTMLObjectForm(it.type().name(), it.type().parent().name())
            is EntityI<*>, is ValuesI<*> -> it.toHTMLObjectFormEntity(it.type().name(), it.type().props())
            else -> ""
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

fun <T : CompilationUnitI<*>> T.toAngularEntityListHTMLComponent(c: GenerationContext, DataService: String = AngularDerivedType.DataService): String {
    return """
<module-${this.parent().name().lowercase(Locale.getDefault())}></module-${this.parent().name().lowercase(Locale.getDefault())}>
<div class="${this.name().lowercase(Locale.getDefault())}-list-button">
    <a class="newButton" [routerLink]="'./new'"
            routerLinkActive="active-link">
        <mat-icon>add_circle_outline</mat-icon> {{"add" | translate}} {{"new" | translate}} {{"item" | translate}}
    </a>
    
    <ng-container *ngIf="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.isHidden; else showed">
        <a class="showButton" (click)="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.toggleHidden()">
            <mat-icon>delete_outline</mat-icon> {{"delete" | translate}}...
        </a>
    </ng-container>
    
    <ng-template #showed>
        <a class="deleteButton" (click)="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.clearMultipleItems(${this.name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.selection.selected); ${this.name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.toggleHidden()">
            <mat-icon>delete_outline</mat-icon> {{"delete" | translate}} {{"item" | translate}}
        </a>
    </ng-template>
    
    <mat-form-field class="filter">
        <mat-label>{{"filter" | translate}}</mat-label>
        <input matInput (keyup)="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.applyFilter(${"$"}event)" placeholder="Input Filter..." [ngModel]="${this.name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.filterValue">
    </mat-form-field>
</div>

<div class="mat-elevation-z8 ${this.name().lowercase(Locale.getDefault())}-list" style="overflow-x: scroll">
    <table mat-table matSort [dataSource]="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.dataSources">
        <ng-container matColumnDef="Box">
            <th mat-header-cell *matHeaderCellDef>
                <section [style.visibility]="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.isHidden? 'hidden': 'visible'">
                    <mat-checkbox color="warn"
                                  (change)="${"$"}event ? ${this.name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.masterToggle() : null"
                                  [checked]="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.selection.hasValue() && ${this.name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.allRowsSelected()"
                                  [indeterminate]="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.selection.hasValue() && !${this.name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.allRowsSelected()"></mat-checkbox>
                </section>
            </th>
            <td mat-cell *matCellDef="let element; let i = index" [attr.data-label]="'box'">
                <section [style.visibility]="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.isHidden? 'hidden': 'visible'">
                    <mat-checkbox color="warn"
                                  (click)="${"$"}event.stopPropagation()"
                                  (change)="${"$"}event ? ${this.name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.selection.toggle(element) : null"
                                  [checked]="${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.selection.isSelected(element)"></mat-checkbox>
                </section>
            </td>
        </ng-container>

        <ng-container matColumnDef="Actions">
            <th mat-header-cell *matHeaderCellDef> {{"table.action" | translate}} </th>
            <td mat-cell *matCellDef="let element; let i = index" [attr.data-label]="'actions'">
                <mat-menu #appMenu="matMenu">
                    <ng-template matMenuContent>
                        <button mat-menu-item (click)="${this.name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.editItems(i, element)"><mat-icon>edit</mat-icon>
                            <span>{{"edit" | translate}}</span></button>
                        <button mat-menu-item (click)="${this.name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${DataService}.removeItem(element)"><mat-icon>delete</mat-icon>
                            <span>{{"delete" | translate}}</span></button>
                    </ng-template>
                </mat-menu>

                <button mat-icon-button [matMenuTriggerFor]="appMenu">
                    <mat-icon>more_vert</mat-icon>
                </button>
            </td>
        </ng-container>
        
        ${toAngularTableListBasic(this.name(), "", "",false)}

        <tr mat-header-row *matHeaderRowDef="tableHeader"></tr>
        <tr mat-row *matRowDef="let row; columns: tableHeader;"></tr>
    </table>
</div>
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
}

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
        <legend>{{"table."+ parentName | translate}} {{"table.${this.name().lowercase(Locale.getDefault())}" | translate}}</legend>
            ${this.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        when(it.type()) {
            is EnumTypeI<*> -> it.toHTMLEnumForm("", it.type().name(), it.type().parent().name())
            is BasicI<*> -> it.toHTMLObjectForm(it.type().name(), it.type().parent().name())
            is EntityI<*>, is ValuesI<*> -> it.toHTMLObjectFormEntityForBasic(it.type().name(), it.type().props())
            else -> when(it.type().name().lowercase(Locale.getDefault())) {
                "boolean" -> it.toHTMLBooleanForm("")
                "date", "list" -> it.toHTMLDateForm("")
                "float", "int" -> it.toHTMLNumberForm("")
                "blob" -> it.toHTMLUploadForm("")
                else -> {it.toHTMLStringForm("")}
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
<mat-form-field appearance="outline">
    <mat-label>{{"table.${this.name().lowercase(Locale.getDefault())}" | translate}}</mat-label>
    <mat-select [(ngModel)]="${this.name().lowercase(Locale.getDefault())}" (selectionChange)="changeValue(${"$"}event)">
        <mat-option *ngFor="let item of enumElements" [value]="item">{{item | translate}}</mat-option>
    </mat-select>
</mat-form-field>
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEnumSCSSComponent(c: GenerationContext): String {
    return this.toAngularDefaultSCSS()
}

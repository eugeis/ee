import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.lang.*

fun <T : ItemI<*>> T.toAngularModuleHTML(): String =
    """<app-page [pageName]="${this.name().toLowerCase()}ViewService.pageName" [pageElement]="${this.name().toLowerCase()}ViewService.pageElement" [tabElement]="${this.name().toLowerCase()}ViewService.tabElement"></app-page>
"""

fun <T : ItemI<*>> T.toAngularEntityViewHTML(): String =
    """<app-${this.parent().name().toLowerCase()}></app-${this.parent().name().toLowerCase()}>

<app-${this.name().toLowerCase()}-form [${this.name().toLowerCase()}]="${this.name().toLowerCase()}"></app-${this.name().toLowerCase()}-form>

<app-button [element]="${this.name().toLowerCase()}" [entityElements]="${this.name().toLowerCase()}DataService.entityElements" [isEdit]="${this.name().toLowerCase()}DataService.isEdit"></app-button>
"""

fun <T : CompilationUnitI<*>> T.toAngularEntityFormHTML(): String =
    """
<div class="${this.name().toLowerCase()}-form">
    <form>
        <fieldset>
            <legend>{{"table.${this.name().toLowerCase()}" | translate}}</legend>
            ${this.props().filter { it.type() !is BasicI<*> && it.type() !is EntityI<*> && it.type() !is ValuesI<*> }.joinSurroundIfNotEmptyToString(nL) {
        when(it.type().name().toLowerCase()) {
            "boolean" -> it.toHTMLBooleanForm(tab)
            "date", "list" -> it.toHTMLDateForm(tab)
            "string", "text" -> it.toHTMLStringForm(tab)
            "blob" -> it.toHTMLUploadForm(tab)
            "float", "int" -> it.toHTMLNumberForm(tab)
            else -> when(it.type()) {
                is EnumTypeI<*> -> it.toHTMLEnumForm(tab, it.type().name())
                else -> ""
            }
        }
    }}
    
            ${this.props().filter { it.type().name().toLowerCase() == "blob" }.joinSurroundIfNotEmptyToString(nL) {
                """<div>
                <img *ngFor='let preview of ${it.parent().name().toLowerCase()}DataService.previews' [src]="preview" class="preview">
            </div>"""
    }}
        </fieldset>
        ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString(nL) {
        when(it.type()) {
            is BasicI<*> -> it.toHTMLObjectForm(it.type().name())
            is EntityI<*>, is ValuesI<*> -> it.toHTMLObjectFormEntity(it.type().name(), it.type().props().first { element -> element.type().name() == "String" })
            else -> ""
        }
    }}
    </form>
</div>
"""

fun <T : CompilationUnitI<*>> T.toAngularBasicHTML(): String =
    """
<div class="${this.name().toLowerCase()}-basic-form">
    <fieldset>
        <legend>{{"table."+ parentName | translate}} {{"table.${this.name().toLowerCase()}" | translate}}</legend>
            ${this.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
            when(it.type()) {
                is EnumTypeI<*> -> it.toHTMLEnumForm("", it.type().name())
                is BasicI<*> -> it.toHTMLObjectForm(it.type().name())
                is EntityI<*>, is ValuesI<*> -> it.toHTMLObjectFormEntityForBasic(it.type().name(), it.type().props().first { element -> element.type().name() == "String" })
                else -> when(it.type().name().toLowerCase()) {
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

fun <T : AttributeI<*>> T.toHTMLObjectFormEntityForBasic(elementType: String, key: AttributeI<*>): String {
    return """
        <fieldset>
            <legend>${elementType.toCamelCase().capitalize()}</legend>
            <mat-form-field appearance="fill">
                <mat-label>{{"select" | translate}} {{"table.${elementType.toLowerCase()}" | translate}}</mat-label>
                <input type="text" matInput [formControl]="control${elementType.toCamelCase().capitalize()}" [matAutocomplete]="auto${elementType.toCamelCase().capitalize()}" [(ngModel)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
                <mat-autocomplete #auto${elementType.toCamelCase().capitalize()}="matAutocomplete" [displayWith]="display${elementType.toCamelCase().capitalize()}">
                    <mat-option *ngFor="let option of filteredOptions${elementType.toCamelCase().capitalize()} | async" [value]="option">
                        {{option.${key.name()}}}
                    </mat-option>
                </mat-autocomplete>
            </mat-form-field>
        </fieldset>"""
}

fun <T : AttributeI<*>> T.toHTMLStringForm(indent: String): String {
    return """
        ${indent}<mat-form-field appearance="outline">
            ${indent}<mat-label>{{"table.${this.name().toLowerCase()}" | translate}}</mat-label>
            ${indent}<input matInput name="${this.name().toLowerCase()}" [(ngModel)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
        ${indent}</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLNumberForm(indent: String): String {
    return """
        ${indent}<mat-form-field appearance="outline">
            ${indent}<mat-label>{{"table.${this.name().toLowerCase()}" | translate}}</mat-label>
            ${indent}<input matInput name="${this.name().toLowerCase()}" type="number" [(ngModel)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
        ${indent}</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLUploadForm(indent: String): String {
    return """
        ${indent}<mat-form-field appearance="outline">
            ${indent}<mat-label>{{"table.${this.name().toLowerCase()}" | translate}}</mat-label>
            ${indent}<input matInput name="${this.name().toLowerCase()}" type="file" (change)="${this.parent().name().toLowerCase()}DataService.selectFiles(${"$"}event)" [(ngModel)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
        ${indent}</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLBooleanForm(indent: String): String {
    return """
        ${indent}<mat-form-field appearance="outline">
            ${indent}<mat-label>{{"table.${this.name().toLowerCase()}" | translate}}</mat-label>
            ${indent}<mat-select [(value)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
                ${indent}<mat-option *ngFor="let item of ['true', 'false']" [value]="item">{{item}}</mat-option>
            ${indent}</mat-select>
        ${indent}</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLDateForm(indent: String): String {
    return """
        ${indent}<mat-form-field appearance="outline">
            ${indent}<mat-label>{{"table.${this.name().toLowerCase()}" | translate}}</mat-label>
            ${indent}<input matInput [matDatepicker]="picker" [(ngModel)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}" [ngModel]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()} | date: 'yyyy-MM-dd'">
            ${indent}<mat-hint>MM/DD/YYYY</mat-hint>
            ${indent}<mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
            ${indent}<mat-datepicker #picker></mat-datepicker>
        ${indent}</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLEnumForm(indent: String, elementName: String): String {
    return """
        ${indent}<app-${elementName.toLowerCase()} [${this.parent().name().toLowerCase()}]="${this.parent().name().toLowerCase()}"></app-${elementName.toLowerCase()}>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectForm(elementType: String): String {
    return """
        <app-${elementType.toLowerCase()} [parentName]="'${this.parent().name().toLowerCase()}'" [${elementType.toLowerCase()}]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}"></app-${elementType.toLowerCase()}>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormBasic(elementType: String): String {
    return """
        <app-${elementType.toLowerCase()}-form [${elementType.toLowerCase()}]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}"></app-${elementType.toLowerCase()}-form>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormEntity(elementType: String, key: AttributeI<*>): String {
    return """
        <fieldset>
            <legend>{{"table.${elementType.toCamelCase().toLowerCase()}" | translate}}</legend>
            <mat-form-field appearance="fill">
                <mat-label>{{"select" | translate}} {{"table.${elementType.toCamelCase().toLowerCase()}" | translate}}</mat-label>
                <input type="text" matInput [formControl]="${this.parent().name().toLowerCase()}DataService.control${elementType.toCamelCase().capitalize()}" [matAutocomplete]="auto${elementType.toCamelCase().capitalize()}" [(ngModel)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
                <mat-autocomplete #auto${elementType.toCamelCase().capitalize()}="matAutocomplete" [displayWith]="${this.parent().name().toLowerCase()}DataService.display${elementType.toCamelCase().capitalize()}">
                    <mat-option *ngFor="let option of ${this.parent().name().toLowerCase()}DataService.filteredOptions${elementType.toCamelCase().capitalize()} | async" [value]="option">
                        {{option.${key.name()}}}
                    </mat-option>
                </mat-autocomplete>
            </mat-form-field>
        </fieldset>"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListHTML(): String =
    """<app-${this.parent().name().toLowerCase()}></app-${this.parent().name().toLowerCase()}>
<div class="${this.name().toLowerCase()}-list-button">
    <a class="newButton" [routerLink]="'./new'"
            routerLinkActive="active-link">
        <mat-icon>add_circle_outline</mat-icon> {{"add" | translate}} {{"new" | translate}} {{"item" | translate}}
    </a>
    
    <ng-container *ngIf="${this.name().toLowerCase()}DataService.isHidden; else showed">
        <a class="showButton" (click)="${this.name().toLowerCase()}DataService.toggleHidden()">
            <mat-icon>delete_outline</mat-icon> {{"delete" | translate}}...
        </a>
    </ng-container>
    
    <ng-template #showed>
        <a class="deleteButton" (click)="${this.name().toLowerCase()}DataService.clearMultipleItems(${this.name().toLowerCase()}DataService.selection.selected); ${this.name().toLowerCase()}DataService.toggleHidden()">
            <mat-icon>delete_outline</mat-icon> {{"delete" | translate}} {{"item" | translate}}
        </a>
    </ng-template>
    
    <mat-form-field class="filter">
        <mat-label>{{"filter" | translate}}</mat-label>
        <input matInput (keyup)="${this.name().toLowerCase()}DataService.applyFilter(${"$"}event)" placeholder="Input Filter..." [ngModel]="${this.name().toLowerCase()}DataService.filterValue">
    </mat-form-field>
</div>

<div class="mat-elevation-z8 ${this.name().toLowerCase()}-list" style="overflow-x: scroll">
    <table mat-table matSort [dataSource]="${this.name().toLowerCase()}DataService.dataSources">
        <ng-container matColumnDef="Box">
            <th mat-header-cell *matHeaderCellDef>
                <section [style.visibility]="${this.name().toLowerCase()}DataService.isHidden? 'hidden': 'visible'">
                    <mat-checkbox color="warn"
                                  (change)="${"$"}event ? ${this.name().toLowerCase()}DataService.masterToggle() : null"
                                  [checked]="${this.name().toLowerCase()}DataService.selection.hasValue() && ${this.name().toLowerCase()}DataService.allRowsSelected()"
                                  [indeterminate]="${this.name().toLowerCase()}DataService.selection.hasValue() && !${this.name().toLowerCase()}DataService.allRowsSelected()"></mat-checkbox>
                </section>
            </th>
            <td mat-cell *matCellDef="let element; let i = index" [attr.data-label]="'box'">
                <section [style.visibility]="${this.name().toLowerCase()}DataService.isHidden? 'hidden': 'visible'">
                    <mat-checkbox color="warn"
                                  (click)="${"$"}event.stopPropagation()"
                                  (change)="${"$"}event ? ${this.name().toLowerCase()}DataService.selection.toggle(element) : null"
                                  [checked]="${this.name().toLowerCase()}DataService.selection.isSelected(element)"></mat-checkbox>
                </section>
            </td>
        </ng-container>

        <ng-container matColumnDef="Actions">
            <th mat-header-cell *matHeaderCellDef> {{"table.action" | translate}} </th>
            <td mat-cell *matCellDef="let element; let i = index" [attr.data-label]="'actions'">
                <mat-menu #appMenu="matMenu">
                    <ng-template matMenuContent>
                        <button mat-menu-item (click)="${this.name().toLowerCase()}DataService.editItems(i, element)"><mat-icon>edit</mat-icon>
                            <span>{{"edit" | translate}}</span></button>
                        <button mat-menu-item (click)="${this.name().toLowerCase()}DataService.removeItem(element)"><mat-icon>delete</mat-icon>
                            <span>{{"delete" | translate}}</span></button>
                    </ng-template>
                </mat-menu>

                <button mat-icon-button [matMenuTriggerFor]="appMenu">
                    <mat-icon>more_vert</mat-icon>
                </button>
            </td>
        </ng-container>
        
        ${toAngularTableListBasic(this.name(), "")}

        <tr mat-header-row *matHeaderRowDef="tableHeader"></tr>
        <tr mat-row *matRowDef="let row; columns: tableHeader;"></tr>
    </table>
</div>
"""

fun <T : ItemI<*>> T.toAngularTableListEntity(elementName: String, findParentNonInternal: ItemI<*>?): String =
    """
        <ng-container matColumnDef="${this.name().toLowerCase()}-entity">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> ${this.name().toUpperCase()} </th>
            <td mat-cell *matCellDef="let element; let i = index"> <a (click)="${this.parent().name().toLowerCase()}DataService.searchItems(i, element['${this.name().toLowerCase()}'], '${findParentNonInternal?.name()?.toLowerCase()}/${elementName.toLowerCase()}', '${this.parent().name().toLowerCase()}')">{{element['${this.name().toLowerCase()}']}}</a> </td>
        </ng-container>
"""

fun <T : ItemI<*>> T.toAngularTableListEnum(parentName: String = ""): String =
    """
        <ng-container matColumnDef="${if(parentName.isEmpty()) "" else "$parentName-"}${this.name()}">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.${this.name().toLowerCase()}" | translate}} </th>
            <td mat-cell *matCellDef="let element"> {{element['${if(parentName.isEmpty()) "" else "$parentName-"}${this.name()}'] | translate}} </td>
        </ng-container>
"""

fun <T : TypeI<*>> T.toAngularTableListBasic(parentName: String = "", basicName: String = ""): String =
    this.props().filter { !isEMPTY() }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.toAngularTableListEntityFromBasic(it.type().name(), it.type().findParentNonInternal(), parentName)
            is BasicI<*> -> it.type().toAngularTableListBasic(parentName, it.name())
            is EnumTypeI<*> -> it.toAngularTableListEnum(basicName)
            else -> it.toAngularTableList(basicName)
        }
    }


fun <T : ItemI<*>> T.toAngularTableListEntityFromBasic(elementName: String, findParentNonInternal: ItemI<*>?, parentName: String): String =
    """
        <ng-container matColumnDef="${this.name().toLowerCase()}-entity">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.${this.name().toLowerCase()}" | translate}}</th>
            <td mat-cell *matCellDef="let element; let i = index"> <a (click)="${parentName.toLowerCase()}DataService.searchItems(i, element['${this.name().toLowerCase()}'], '${findParentNonInternal?.name()?.toLowerCase()}/${elementName.toLowerCase()}', '${parentName.toLowerCase()}')">{{element['${this.name().toLowerCase()}']}}</a> </td>
        </ng-container>
"""

fun <T : ItemI<*>> T.toAngularTableList(parentName: String = ""): String =
    """
        <ng-container matColumnDef="${if(parentName.isEmpty()) "" else "$parentName-"}${this.name()}">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.${this.name().toLowerCase()}" | translate}} </th>
            <td mat-cell *matCellDef="let element"> {{element['${if(parentName.isEmpty()) "" else "$parentName-"}${this.name()}']}} </td>
        </ng-container>
"""

fun <T : ItemI<*>> T.toAngularDefaultSCSS(): String =
    """host{}"""

fun <T : ItemI<*>> T.toAngularEntityViewSCSS(): String =
    """app-button {
    position: relative;
    left: 10%;
}

"""

fun <T : CompilationUnitI<*>> T.toAngularEntityFormSCSS(): String =
    """@import "src/styles";

.${this.name().toLowerCase()}-form {
    @extend .entity-form
}

${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString(nL) {
        when (it.type()) {
            is BasicI<*>, is EntityI<*>, is ValuesI<*> -> it.toSCSSOtherFormStyle(it.type().name())
            else -> ""
        }
    }
}
"""

fun <T : ItemI<*>> T.toSCSSOtherFormStyle(elementType: String): String =
    """app-${elementType.toLowerCase()}-form {
    position: relative;
    left: -10%;
}
"""

fun <T : ItemI<*>> T.toAngularBasicSCSS(): String =
    """@import "src/styles";

.${this.name().toLowerCase()}-basic-form {
    @extend .fieldset-form
}
"""

fun <T : ItemI<*>> T.toAngularEntityListSCSS(): String =
    """@import "src/styles";

.${this.name().toLowerCase()}-list {
    @extend .entity-list;
    position: absolute;
    width: 80% !important;
    z-index: 1;
    top: 40%;
    left: 10%;
}

.${this.name().toLowerCase()}-list-button {
    @extend .entity-list-button
}

a {
    @extend .entity-link
}
"""

fun <T : CompilationUnitI<*>> T.toAngularEnumHTML(parent: ItemI<*>, elementName: String): String =
    """
<mat-form-field appearance="outline">
    <mat-label>{{"table.${this.name().toLowerCase()}" | translate}}</mat-label>
    <mat-select [(value)]="${parent.name().toLowerCase()}.${elementName.toCamelCase()}">
        <mat-option *ngFor="let item of enumElements" [value]="item">{{item | translate}}</mat-option>
    </mat-select>
</mat-form-field>
"""


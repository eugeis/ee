import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.lang.*

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
            ${indent}<input matInput name="${this.name().toLowerCase()}" type="file" (change)="${this.parent().name().decapitalize()}DataService.selectFiles(${"$"}event)" [(ngModel)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
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

fun <T : AttributeI<*>> T.toHTMLEnumForm(indent: String, elementType: String): String {
    return """
        ${indent}<app-${elementType.toLowerCase()} [${elementType.toLowerCase()}]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}" (${elementType.toLowerCase()}Change) = '${this.parent().name().toLowerCase()}.${this.name().toCamelCase()} = ${"$"}event'></app-${elementType.toLowerCase()}>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectForm(elementType: String): String {
    return """
        <app-${elementType.toLowerCase()} [parentName]="'${this.parent().name().toLowerCase()}'" [${elementType.toLowerCase()}]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}"></app-${elementType.toLowerCase()}>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormEntity(elementType: String, key: AttributeI<*>): String {
    return """
        <fieldset>
            <legend>{{"table.${elementType.toCamelCase().toLowerCase()}" | translate}}</legend>
            <mat-form-field appearance="fill">
                <mat-label>{{"select" | translate}} {{"table.${elementType.toCamelCase().toLowerCase()}" | translate}}</mat-label>
                <input type="text" matInput [formControl]="${this.parent().name().decapitalize()}DataService.control${elementType.toCamelCase().capitalize()}" [matAutocomplete]="auto${elementType.toCamelCase().capitalize()}" [(ngModel)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
                <mat-autocomplete #auto${elementType.toCamelCase().capitalize()}="matAutocomplete" [displayWith]="${this.parent().name().decapitalize()}DataService.display${elementType.toCamelCase().capitalize()}">
                    <mat-option *ngFor="let option of ${this.parent().name().decapitalize()}DataService.filteredOptions${elementType.toCamelCase().capitalize()} | async" [value]="option">
                        {{option.${key.name()}}}
                    </mat-option>
                </mat-autocomplete>
            </mat-form-field>
        </fieldset>"""
}

fun <T : ItemI<*>> T.toAngularTableListEntity(elementName: String, findParentNonInternal: ItemI<*>?): String =
    """
        <ng-container matColumnDef="${this.name().toLowerCase()}-entity">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> ${this.name().toUpperCase()} </th>
            <td mat-cell *matCellDef="let element; let i = index"> <a (click)="${this.parent().name().decapitalize()}DataService.searchItems(i, element['${this.name().toLowerCase()}'], '${findParentNonInternal?.name()?.toLowerCase()}/${elementName.toLowerCase()}', '${this.parent().name().toLowerCase()}')">{{element['${this.name().toLowerCase()}']}}</a> </td>
        </ng-container>
"""

fun <T : ItemI<*>> T.toAngularTableListEnum(parentName: String = "", isChild: Boolean): String =
    """
        <ng-container matColumnDef="${if(parentName.isEmpty()) "" else "$parentName-"}${this.name()}">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.${this.name().toLowerCase()}" | translate}} </th>
            <td mat-cell *matCellDef="let element"> {{element${if(parentName.isEmpty()) "" else if(isChild) "['$parentName']" else ""}['${this.name()}'] | translate}} </td>
        </ng-container>
"""

fun <T : TypeI<*>> T.toAngularTableListBasic(parentName: String = "", basicName: String = "", basicParentName: String = "", isChild: Boolean): String =
    this.props().filter { !isEMPTY() }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.toAngularTableListEntityFromBasic(it.type().name(), it.type().findParentNonInternal(), parentName, it.type().props().first { element -> element.type().name() == "String" }, isChild)
            is BasicI<*> -> it.type().toAngularTableListBasic(parentName, it.name(), it.parent().name(),true)
            is EnumTypeI<*> -> it.toAngularTableListEnum(basicName, isChild)
            else -> {
                when(it.type().name()) {
                    "Date" -> it.toAngularTableListDate(basicName)
                    else -> it.toAngularTableList(parentName, basicName, basicParentName)
                }
            }
        }
    }


fun <T : ItemI<*>> T.toAngularTableListEntityFromBasic(elementName: String, findParentNonInternal: ItemI<*>?, parentName: String, key: AttributeI<*>, isChild: Boolean): String =
    """
        <ng-container matColumnDef="${this.name().toLowerCase()}-entity">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.${this.name().toLowerCase()}" | translate}}</th>
            <td mat-cell *matCellDef="let element; let i = index"> <a (click)="${parentName.decapitalize()}DataService.searchItems(i, element${if(isChild) "['${this.parent().name().toLowerCase()}']['${this.name().toLowerCase()}']" else "['${this.name().toLowerCase()}']"}, '${findParentNonInternal?.name()?.toLowerCase()}/${elementName.toLowerCase()}', '${parentName.toLowerCase()}')">{{element${if(isChild) "['${this.parent().name().toLowerCase()}']['${this.name().toLowerCase()}']['${key.name()}']" else "['${this.name().toLowerCase()}']['${key.name()}']"}}}</a> </td>
        </ng-container>
"""

fun <T : ItemI<*>> T.toAngularTableListDate(parentName: String = ""): String =
    """
        <ng-container matColumnDef="${if(parentName.isEmpty()) "" else "$parentName-"}${this.name()}">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.${this.name().toLowerCase()}" | translate}} </th>
            <td mat-cell *matCellDef="let element"> {{element['${if(parentName.isEmpty()) "" else "$parentName-"}${this.name()}'] | DateTimeTranslation}} </td>
        </ng-container>
"""

fun <T : ItemI<*>> T.toAngularTableList(parentName: String = "", elementName: String = "", basicParentName: String = ""): String =
    """
        <ng-container matColumnDef="${if(elementName.isEmpty()) "" else "$elementName-"}${this.name()}">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.${this.name().toLowerCase()}" | translate}} </th>
            <td mat-cell *matCellDef="let element"> {{element${if(elementName.isEmpty()) "" else if(parentName.equals(basicParentName, ignoreCase = true)) "['$elementName']" else "['${basicParentName.toLowerCase()}']['$elementName']"}['${this.name()}']}} </td>
        </ng-container>
"""

fun <T : ItemI<*>> T.toAngularDefaultSCSS(): String =
    """host{}"""

fun <T : ItemI<*>> T.toSCSSOtherFormStyle(elementType: String): String =
    """app-${elementType.toLowerCase()}-form {
    position: relative;
    left: -10%;
}
"""


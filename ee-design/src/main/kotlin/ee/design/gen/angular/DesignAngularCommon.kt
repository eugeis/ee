import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.lang.*
import java.util.*

fun <T : AttributeI<*>> T.toHTMLObjectFormEntityForBasic(elementType: String, key: ListMultiHolder<AttributeI<*>>): String {
    return """
        <fieldset>
            <legend>${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}</legend>
            <mat-form-field appearance="fill">
                <mat-label>{{"select" | translate}} {{"table.${elementType.lowercase(Locale.getDefault())}" | translate}}</mat-label>
                <input type="text" matInput [formControl]="control${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}" [matAutocomplete]="auto${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}" [(ngModel)]="${this.parent().name()
        .lowercase(Locale.getDefault())}.${this.name().toCamelCase()}">
                <mat-autocomplete #auto${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}="matAutocomplete" [displayWith]="display${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}">
                    <mat-option *ngFor="let option of filteredOptions${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} | async" [value]="option">
                        {{ ${if(key.any { it.type().name() == "String" }) {"option." + key.first { it.type().name() == "String" }.name()} else {"option"}} }}
                    </mat-option>
                </mat-autocomplete>
            </mat-form-field>
        </fieldset>"""
}

fun <T : AttributeI<*>> T.toHTMLStringForm(indent: String): String {
    return """
        ${indent}<mat-form-field appearance="outline">
            ${indent}<mat-label>{{"table.${this.name().lowercase(Locale.getDefault())}" | translate}}</mat-label>
            ${indent}<input matInput name="${this.name().lowercase(Locale.getDefault())}" [(ngModel)]="${this.parent().name()
        .lowercase(Locale.getDefault())}.${this.name().toCamelCase()}">
        ${indent}</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLNumberForm(indent: String): String {
    return """
        ${indent}<mat-form-field appearance="outline">
            ${indent}<mat-label>{{"table.${this.name().lowercase(Locale.getDefault())}" | translate}}</mat-label>
            ${indent}<input matInput name="${this.name().lowercase(Locale.getDefault())}" type="number" [(ngModel)]="${this.parent().name()
        .lowercase(Locale.getDefault())}.${this.name().toCamelCase()}">
        ${indent}</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLUploadForm(indent: String): String {
    return """
        ${indent}<mat-form-field appearance="outline">
            ${indent}<mat-label>{{"table.${this.name().lowercase(Locale.getDefault())}" | translate}}</mat-label>
            ${indent}<input matInput name="${this.name().lowercase(Locale.getDefault())}" type="file" (change)="${this.parent().name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.selectFiles(${"$"}event)" [(ngModel)]="${this.parent().name()
        .lowercase(Locale.getDefault())}.${this.name().toCamelCase()}">
        ${indent}</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLBooleanForm(indent: String): String {
    return """
        ${indent}<mat-form-field appearance="outline">
            ${indent}<mat-label>{{"table.${this.name().lowercase(Locale.getDefault())}" | translate}}</mat-label>
            ${indent}<mat-select [(value)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}">
                ${indent}<mat-option *ngFor="let item of ['true', 'false']" [value]="item">{{item}}</mat-option>
            ${indent}</mat-select>
        ${indent}</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLDateForm(indent: String): String {
    return """
        ${indent}<mat-form-field appearance="outline">
            ${indent}<mat-label>{{"table.${this.name().lowercase(Locale.getDefault())}" | translate}}</mat-label>
            ${indent}<input matInput [matDatepicker]="picker" [(ngModel)]="${this.parent().name()
        .lowercase(Locale.getDefault())}.${this.name().toCamelCase()}" [ngModel]="${this.parent().name()
        .lowercase(Locale.getDefault())}.${this.name().toCamelCase()} | date: 'yyyy-MM-dd'">
            ${indent}<mat-hint>MM/DD/YYYY</mat-hint>
            ${indent}<mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
            ${indent}<mat-datepicker #picker></mat-datepicker>
        ${indent}</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLEnumForm(indent: String, elementType: String, parentName: String): String {
    return """
        ${indent}<enum-${parentName.lowercase()}-${elementType.lowercase(Locale.getDefault())} [${elementType.lowercase(Locale.getDefault())}]="${this.parent().name()
        .lowercase(Locale.getDefault())}.${this.name().toCamelCase()}" (${elementType.lowercase(
        Locale.getDefault()
    )}Change) = '${this.parent().name()
        .lowercase(Locale.getDefault())}.${this.name().toCamelCase()} = ${"$"}event'></enum-${parentName.lowercase()}-${elementType.lowercase(
        Locale.getDefault()
    )}>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectForm(elementType: String, parentName: String): String {
    return """
        <basic-${parentName.lowercase()}-${elementType.lowercase(Locale.getDefault())} [parentName]="'${this.parent().name().lowercase(Locale.getDefault())}'" [${elementType.lowercase(
        Locale.getDefault()
    )}]="${this.parent().name()
        .lowercase(Locale.getDefault())}.${this.name().toCamelCase()}"></basic-${parentName.lowercase()}-${elementType.lowercase(
        Locale.getDefault()
    )}>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormEntity(elementType: String, key: ListMultiHolder<AttributeI<*>>): String {
    return """
        <fieldset>
            <legend>{{"table.${elementType.toCamelCase().lowercase(Locale.getDefault())}" | translate}}</legend>
            <mat-form-field appearance="fill">
                <mat-label>{{"select" | translate}} {{"table.${elementType.toCamelCase().lowercase(Locale.getDefault())}" | translate}}</mat-label>
                <input type="text" matInput [formControl]="${this.parent().name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.control${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}" [matAutocomplete]="auto${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}" [(ngModel)]="${this.parent().name()
        .lowercase(Locale.getDefault())}.${this.name().toCamelCase()}">
                <mat-autocomplete #auto${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}="matAutocomplete" [displayWith]="${this.parent().name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.display${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}">
                    <mat-option *ngFor="let option of ${this.parent().name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.filteredOptions${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} | async" [value]="option">
                        {{ ${if(key.any { it.type().name() == "String" }) {"option." + key.first { it.type().name() == "String" }.name()} else {"option"}} }}
                    </mat-option>
                </mat-autocomplete>
            </mat-form-field>
        </fieldset>"""
}

fun <T : ItemI<*>> T.toAngularTableListEntity(elementName: String, findParentNonInternal: ItemI<*>?): String =
    """
        <ng-container matColumnDef="${this.name().lowercase(Locale.getDefault())}-entity">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> ${this.name().uppercase(Locale.getDefault())} </th>
            <td mat-cell *matCellDef="let element; let i = index"> <a (click)="${this.parent().name()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.searchItems(i, element['${this.name()
        .lowercase(Locale.getDefault())}'], '${findParentNonInternal?.name()
        ?.lowercase(Locale.getDefault())}/${elementName.lowercase(
        Locale.getDefault()
    )}', '${this.parent().name()
        .lowercase(Locale.getDefault())}')">{{element['${this.name()
        .lowercase(Locale.getDefault())}']}}</a> </td>
        </ng-container>
"""

fun <T : ItemI<*>> T.toAngularTableListEnum(parentName: String = "", isChild: Boolean): String =
    """
        <ng-container matColumnDef="${if(parentName.isEmpty()) "" else "$parentName-"}${this.name()}">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.${this.name().lowercase(Locale.getDefault())}" | translate}} </th>
            <td mat-cell *matCellDef="let element"> {{element${if(parentName.isEmpty()) "" else if(isChild) "['$parentName']" else ""}['${this.name()}'] | translate}} </td>
        </ng-container>
"""

fun <T : TypeI<*>> T.toAngularTableListBasic(parentName: String = "", basicName: String = "", basicParentName: String = "", isChild: Boolean): String =
    this.props().filter { !isEMPTY() }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.toAngularTableListEntityFromBasic(it.type().name(), it.type().findParentNonInternal(), parentName, it.type().props(), isChild)
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


fun <T : ItemI<*>> T.toAngularTableListEntityFromBasic(elementName: String, findParentNonInternal: ItemI<*>?, parentName: String, key: ListMultiHolder<AttributeI<*>>, isChild: Boolean): String =
    """
        <ng-container matColumnDef="${this.name().lowercase(Locale.getDefault())}-entity">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.${this.name().lowercase(Locale.getDefault())}" | translate}}</th>
            <td mat-cell *matCellDef="let element; let i = index"> <a (click)="${parentName.replaceFirstChar {
        it.lowercase(
            Locale.getDefault()
        )
    }}DataService.searchItems(i, element${if(isChild) "['${this.parent().name()
        .lowercase(Locale.getDefault())}']['${this.name()
        .lowercase(Locale.getDefault())}']" else "['${this.name()
        .lowercase(Locale.getDefault())}']"}, '${findParentNonInternal?.name()
        ?.lowercase(Locale.getDefault())}/${elementName.lowercase(
        Locale.getDefault()
    )}', '${parentName.lowercase(
        Locale.getDefault()
    )}')">{{element${if(isChild) "['${this.parent().name()
        .lowercase(Locale.getDefault())}']['${this.name()
        .lowercase(Locale.getDefault())}']${if (key.any { it.type().name() == "String" }) { "['" + key.first { it.type().name() == "String" }.name() + "']" } else {""}}" else "['${this.name()
        .lowercase(Locale.getDefault())}']${if (key.any { it.type().name() == "String" }) { "['" + key.first { it.type().name() == "String" }.name() + "']" } else {""}}"}}}</a> </td>
        </ng-container>
"""

fun <T : ItemI<*>> T.toAngularTableListDate(parentName: String = ""): String =
    """
        <ng-container matColumnDef="${if(parentName.isEmpty()) "" else "$parentName-"}${this.name()}">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.${this.name().lowercase(Locale.getDefault())}" | translate}} </th>
            <td mat-cell *matCellDef="let element"> {{element['${if(parentName.isEmpty()) "" else "$parentName-"}${this.name()}'] | DateTimeTranslation}} </td>
        </ng-container>
"""

fun <T : ItemI<*>> T.toAngularTableList(parentName: String = "", elementName: String = "", basicParentName: String = ""): String =
    """
        <ng-container matColumnDef="${if(elementName.isEmpty()) "" else "$elementName-"}${this.name()}">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.${this.name().lowercase(Locale.getDefault())}" | translate}} </th>
            <td mat-cell *matCellDef="let element"> {{element${if(elementName.isEmpty()) "" else if(parentName.equals(basicParentName, ignoreCase = true)) "['$elementName']" else "['${basicParentName.lowercase(
        Locale.getDefault()
    )}']['$elementName']"}['${this.name()}']}} </td>
        </ng-container>
"""

fun <T : ItemI<*>> T.toAngularDefaultSCSS(): String =
    """host{}"""

fun <T : ItemI<*>> T.toSCSSOtherFormStyle(elementType: String): String =
    """app-${elementType.lowercase(Locale.getDefault())}-form {
    position: relative;
    left: -10%;
}
"""


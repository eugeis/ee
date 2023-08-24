import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.lang.*
import java.util.*
import kotlin.math.ceil

fun <T : AttributeI<*>> T.toHTMLObjectFormEntityForBasic(elementType: String, toStr: List<AttributeI<*>>): String {
    return """
        <fieldset>
            <legend>${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}</legend>
            <si-form-group label="{{'select' | translate}} {{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
                <si-dropdown inputId="control${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [dropdownOptions]="option${this.name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}"
                            [isDisabled]="isDisabled">
                    <ng-container *siDropdownOption="let value = value">
                        <span matTooltip="{{ ${this.parent().name().lowercase(Locale.getDefault())}.tooltip(value)}}" matTooltipClass="custom-tooltip">{{value${
        toStr.isNotEmpty().then{ """['${toStr.first().name()}']""" }}}}</span>
                    </ng-container>
                </si-dropdown>
            </si-form-group>
        </fieldset>"""
}

fun <T : AttributeI<*>> T.toHTMLStringForm(indent: String, parentName: String = "", isBasic: Boolean): String {
    return """
        ${indent}<si-form-group label="{{'${if (parentName.isBlank()) {this.parent().name().lowercase(Locale.getDefault())} else {parentName.lowercase(Locale.getDefault())}}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
            ${indent}<input siFormControl [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}" ${isBasic.then { """[disabled]="isDisabled"""" }} ${isBasic.not().then { """formControlName="${if (parentName.isBlank()) {
        this.parent().name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }
    } else {
        parentName.toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }
    }}${this.name().toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""" }}>
        ${indent}</si-form-group>"""
}

fun <T : AttributeI<*>> T.toHTMLNumberForm(indent: String, isBasic: Boolean): String {
    return """
        ${indent}<si-form-group label="{{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
            ${indent}<input type="number" siFormControl [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}" ${isBasic.then { """[disabled]="isDisabled"""" }} ${isBasic.not().then { """formControlName="${this.parent().name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${this.name().toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""" }}>
        ${indent}</si-form-group>"""
}

fun <T : AttributeI<*>> T.toHTMLUploadForm(indent: String, isBasic: Boolean): String {
    return """
        ${indent}<si-form-group label="{{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
            ${indent}<input type="file" siFormControl (change)="${this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${this.parent().name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}DataService.selectFiles(${"$"}event)" [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}" ${isBasic.then { """[disabled]="isDisabled"""" }} ${isBasic.not().then { """formControlName="${this.parent().name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${this.name().toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""" }}>
        ${indent}</si-form-group>"""
}

fun <T : AttributeI<*>> T.toHTMLBooleanForm(indent: String, isBasic: Boolean): String {
    return """
        ${indent}<si-form-group label="{{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
            ${indent}<si-dropdown 
                         ${indent}inputId="control${this.name().toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                         ${indent}[dropdownOptions]="['true', 'false']"
                         ${indent}[(ngModel)]="${this.parent().name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}"
                         ${indent}${isBasic.then { """[disabled]="isDisabled"""" }}
                         ${isBasic.not().then { """${indent}formControlName="${this.parent().name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${this.name().toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""" }}>
            ${indent}</si-dropdown>
        ${indent}</si-form-group>"""
}

fun <T : AttributeI<*>> T.toHTMLDateForm(indent: String, isBasic: Boolean): String {
    return """
        ${indent}<si-form-group label="{{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}} MM/DD/YYYY">
            ${indent}<input siFormControl [matDatepicker]="picker" [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}" [ngModel]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()} | date: 'yyyy-MM-dd'" ${isBasic.then { """[disabled]="isDisabled"""" }} ${isBasic.not().then { """formControlName="${this.parent().name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${this.name().toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""" }}>
            ${indent}<mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
            ${indent}<mat-datepicker #picker></mat-datepicker>
        ${indent}</si-form-group>"""
}

fun <T : AttributeI<*>> T.toHTMLEnumForm(indent: String, elementType: String, parentName: String): String {
    return """
        ${indent}<enum-${parentName.lowercase()}-${elementType.lowercase(Locale.getDefault())} [${elementType.lowercase(Locale.getDefault())}]="${this.parent().name()
        .lowercase(Locale.getDefault())}.${this.name().toCamelCase()}" [isDisabled]="isDisabled" (${elementType.lowercase(
        Locale.getDefault()
    )}Change) = '${this.parent().name()
        .lowercase(Locale.getDefault())}.${this.name().toCamelCase()} = ${"$"}event'></enum-${parentName.lowercase()}-${elementType.lowercase(
        Locale.getDefault()
    )}>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectForm(elementType: String, parentName: String, isBasic: Boolean): String {
    return """
        <basic-${parentName.lowercase()}-${elementType.lowercase(Locale.getDefault())} [isDisabled]="isDisabled" [parentName]="'${this.parent().name().lowercase(Locale.getDefault())}'" [${elementType.lowercase(
        Locale.getDefault()
    )}]="${this.parent().name()
        .lowercase(Locale.getDefault())}.${this.name().toCamelCase()}"></basic-${parentName.lowercase()}-${elementType.lowercase(
        Locale.getDefault()
    )}>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormEntity(elementTypeParent: String, elementType: String, toStr: List<AttributeI<*>>): String {
    return """
        <fieldset>
            <legend>{{"${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}" | translate}}
                <a class="newButton bg-dark normal-font-size" [routerLink]="'/${this.type().findParentNonInternal()?.name()?.lowercase(Locale.getDefault())}/${elementType.lowercase(Locale.getDefault())}/new'"
                                                            routerLinkActive="active-link">
                    <span aria-hidden='true' class='iconUxt addCircle filled'></span>
                </a>
            </legend>
            <si-form-group label="{{'select' | translate}} {{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
                <si-dropdown inputId="control${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [dropdownOptions]="${if(this.parent().parent().name().equals(this.parent().name(), true)) {this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} else {this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) } + this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}}DataService.option${elementTypeParent.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}"
                             formControlName="${this.parent().name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}">
                    <ng-container *siDropdownOption="let value = value">
                        <span matTooltip="{{ ${this.parent().name().lowercase(Locale.getDefault())}.tooltip(value) }}" matTooltipClass="custom-tooltip">{{value${
        toStr.isNotEmpty().then{ """['${toStr.first().name()}']""" }}}}</span>
                    </ng-container>
                </si-dropdown>
            </si-form-group>
        </fieldset>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormEntityMultiple(elementTypeParent: String, elementType: String, toStr: List<AttributeI<*>>): String {
    return """
        <fieldset>
            <legend>{{"${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}" | translate}}
                <a class="newButton bg-dark normal-font-size" [routerLink]="'/${this.type().findParentNonInternal()?.name()?.lowercase(Locale.getDefault())}/${elementType.lowercase(Locale.getDefault())}/new'" [queryParams]="{isList: true}"
                                                            routerLinkActive="active-link">
                    <span aria-hidden='true' class='iconUxt addCircle filled'></span>
                </a>
            </legend>
            
            <mat-form-field>
                <mat-label>{{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}</mat-label>
                <mat-chip-grid #chipGrid>
                    <mat-chip-row *ngFor="let ${elementType.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }} of ${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}" (removed)="remove(${elementType.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }})">
                        {{${elementType.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${
        toStr.isNotEmpty().then{ """['${toStr.first().name()}']""" }}}}
                        <button matChipRemove [attr.aria-label]="'remove ' + ${elementType.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}">
                            <mat-icon>cancel</mat-icon>
                        </button>
                    </mat-chip-row>
                </mat-chip-grid>
                <input placeholder="{{'new' | translate}} {{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}"
                       [matChipInputFor]="chipGrid" [matAutocomplete]="auto"
                       [matChipInputSeparatorKeyCodes]="separatorKeysCodes"
                       (matChipInputTokenEnd)="add(${'$'}event)"/>
                <mat-autocomplete #auto="matAutocomplete" (optionSelected)="selected(${'$'}event)">
                    <mat-option *ngFor="let ${elementType.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }} of ${if(this.parent().parent().name().equals(this.parent().name(), true)) {this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} else {this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) } + this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) 
    else it.toString() }}}DataService.filteredOptions${if(elementTypeParent.equals(elementType, true)) {elementTypeParent.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} 
    else {elementTypeParent.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + elementType.toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}} | async" [value]="${elementType.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}">
                        {{${elementType.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${
        toStr.isNotEmpty().then{ """['${toStr.first().name()}']""" }}}}
                    </mat-option>
                </mat-autocomplete>
            </mat-form-field>
        </fieldset>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormBasicFromEntityMultiple(elementType: String, toStr: List<AttributeI<*>>): String {
    return """
        <fieldset>
            <legend>{{"${this.parent().name().lowercase(Locale.getDefault())}.table.${elementType.toCamelCase().lowercase(Locale.getDefault())}" | translate}}</legend>
            <si-form-group label="{{'select' | translate}} {{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
                <si-dropdown inputId="control${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [dropdownOptions]="filteredOptions${this.name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}"
                             [isDisabled]="isDisabled">
                    <ng-container *siDropdownOption="let value = value">
                        <span matTooltip="{{ ${elementType.lowercase(Locale.getDefault())}.tooltip(value) }}" matTooltipClass="custom-tooltip">{{value${
        toStr.isNotEmpty().then{ """['${toStr.first().name()}']""" }}}}</span>
                    </ng-container>
                </si-dropdown>
            </si-form-group>
        </fieldset>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormEnumMultiple(elementTypeParent: String, elementType: String, elementName: String, isBasic: Boolean, toStr: List<AttributeI<*>>): String {
    return """
        <fieldset>
            <legend>{{"${if(elementName.isEmpty()) {this.parent().name().lowercase(Locale.getDefault()) + "."} else "$elementName."}table.${elementType.toCamelCase().lowercase(Locale.getDefault())}" | translate}}</legend>
            <si-form-group label="{{'${if(elementName.isEmpty()) {this.parent().name().lowercase(Locale.getDefault()) + "."} else "$elementName."}table.${elementType.toCamelCase().lowercase(Locale.getDefault())}' | translate}}">
                <si-dropdown inputId="control${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [dropdownOptions]=${if (!isBasic) { """"${if(this.parent().parent().name().equals(this.parent().name(), true)) {this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} else {this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) } + this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}}DataService.option${elementTypeParent.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${elementType.toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""" } else { """option${elementType.toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""" }}
                             [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}"
                             ${isBasic.then { """[disabled]="isDisabled"""" }}
                             ${isBasic.not().then { """formControlName="${this.parent().name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""" }}
                             [multipleSelect]="true">
                </si-dropdown>
            </si-form-group>
        </fieldset>"""
}

fun <T : TypeI<*>> T.toAngularTableListBasic(parentName: String = "", basicName: String = "", basicParentName: String = "", isChild: Boolean, totalChild: Int, containAggregateProp: Boolean = false): String =
    this.props().filter { !isEMPTY() }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.toAngularTableListEntityFromBasic(it.type().name(), it.type().findParentNonInternal(), parentName, isChild, totalChild,  it.type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() }, it.type().props())
            /*is BasicI<*> -> it.toAngularTableListBasics(it.type().name(), it.type().findParentNonInternal(), parentName, it.type().props(), isChild, "Object", totalChild,
                it.type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })*/
            is BasicI<*> -> it.type().toAngularTableListBasic(parentName, it.name(), it.parent().name(),true, totalChild, containAggregateProp)
            is EnumTypeI<*> -> it.toAngularTableListEnum(basicName, totalChild, parentName)
            else -> {
                when(it.type().name()) {
                    "Date" -> it.toAngularTableListDate(basicName, totalChild)
                    "List" -> when(it.type().generics().first().type()) {
                        is EntityI<*>, is ValuesI<*> ->  it.toAngularTableListEntityFromBasicMultiple(it.type().generics().first().type().name(), parentName,  isChild, totalChild,  it.type().generics().first().type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })
                        is EnumTypeI<*> -> it.toAngularTableListEntityFromBasicMultipleEnums(it.type().generics().first().type().findParentNonInternal(), parentName, totalChild,  it.type().generics().first().type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })
                        else -> it.toAngularTableList(parentName, basicName, totalChild, containAggregateProp)
                    }
                    else -> it.toAngularTableList(parentName, basicName, totalChild, containAggregateProp)
                }
            }
        }
    }

fun <T : ItemI<*>> T.toAngularTableListEnum(basicParentName: String = "", totalChild: Int, parentName: String): String =
    """
        <siTableColumn [widthFactor]="${roundToNearestHalf(((this.parent().name().length + this.name().length).toDouble() / totalChild.toDouble()))}" key="${if(basicParentName.isEmpty()) {this.parent().name().lowercase(Locale.getDefault()) }  else basicParentName}${this.name().lowercase(Locale.getDefault())}" name="{{'${if(basicParentName.isEmpty()) {this.parent().name().lowercase(Locale.getDefault()) + "."}  else "$basicParentName."}table.${this.name().lowercase(Locale.getDefault())}' | translate}}" [enableClearFilter]="true" [filterFunction]="${parentName.lowercase(Locale.getDefault())}.findBy${basicParentName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${this.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}">
            <div *siTableCell="let row = row; let i = index">
                <span>{{ row${if(basicParentName.isEmpty()) {"['" + this.parent().name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) } + "']"}  else "['${basicParentName.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']"}['${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}'] }}</span>
            </div>
        </siTableColumn>
"""

fun <T : ItemI<*>> T.toAngularTableListEntityFromBasic(elementName: String, findParentNonInternal: ItemI<*>?, parentName: String, isChild: Boolean, totalChild: Int, toStr: List<AttributeI<*>>, props: ListMultiHolder<AttributeI<*>>): String {
    val serviceName = if(this.parent().parent().name().equals(parentName, true)) {this.parent().parent().name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} else {this.parent().parent().name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault())} + parentName.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}
    return """
        <siTableColumn [widthFactor]="${roundToNearestHalf(((this.parent().name().length + this.name().length).toDouble() / totalChild.toDouble()))}" key="${this.parent().name().lowercase(Locale.getDefault())}${this.name().lowercase(Locale.getDefault())}" name="{{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}" [enableClearFilter]="true" [filterFunction]="${parentName.lowercase(Locale.getDefault())}.findBy${this.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}">
            <div *siTableCell="let row = row; let i = index">
                <a matTooltip="{{ ${parentName.lowercase(Locale.getDefault())}.tooltip(row${if(isChild) "['${this.parent().name()
            .lowercase(Locale.getDefault())}']['${this.name()
            .lowercase(Locale.getDefault())}']" else "['${this.name()
            .lowercase(Locale.getDefault())}']"}) }}" matTooltipClass="custom-tooltip" (click)="${serviceName}DataService.searchItems(i, row${if(isChild) "['${this.parent().name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']['${this.name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']" else "['${this.name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']"}, '${findParentNonInternal?.name()
            ?.toCamelCase()?.replaceFirstChar { it.lowercase(Locale.getDefault()) }}/${elementName.lowercase(
            Locale.getDefault()
    )}', '${parentName.lowercase(
            Locale.getDefault()
    )}')"> ... ; </a>
            ${props.filter { !it.isEMPTY() && (it.type() is EntityI<*> || it.type() is ValuesI<*>) }.joinSurroundIfNotEmptyToString("") {
            """
                <a matTooltip="{{ ${parentName.lowercase(Locale.getDefault())}.tooltip(row['${this.name()
                .lowercase(Locale.getDefault())}']['${it.name()
                .lowercase(Locale.getDefault())}']) }}" matTooltipClass="custom-tooltip" (click)="${serviceName}DataService.searchItems(i, row['${this.name()
                .lowercase(Locale.getDefault())}']['${it.name()
                .lowercase(Locale.getDefault())}'], '${it.name()}/${parentName.lowercase(
                Locale.getDefault())}', '${parentName.lowercase(
                Locale.getDefault()
            )}')"> ${it.name()} ; </a>"""}}
            </div>
        </siTableColumn>
"""
}


fun <T : ItemI<*>> T.toAngularTableListEntityFromBasicMultiple(elementName: String, parentName: String, isChild: Boolean, totalChild: Int, toStr: List<AttributeI<*>>): String {
    return """
        <siTableColumn [widthFactor]="${roundToNearestHalf(((this.parent().name().length + this.name().length).toDouble() / totalChild.toDouble()))}" key="${this.parent().name().lowercase(Locale.getDefault())}${this.name().lowercase(Locale.getDefault())}" name="{{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}" [enableClearFilter]="true" [filterFunction]="${parentName.lowercase(Locale.getDefault())}.findBy${this.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}">
            <div *siTableCell="let row = row; let i = index">
                <ng-container *ngFor="let ${elementName.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }} of row['${this.name().lowercase(Locale.getDefault())}']">
                    <span matTooltip="{{ ${parentName.lowercase(Locale.getDefault())}.tooltip(row${if(isChild) "['${this.parent().name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']['${this.name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']" else "['${this.name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']"}) }}" matTooltipClass="custom-tooltip">
                        {{  ${elementName.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${toStr.isNotEmpty().then{ """['${toStr.first().name()}']""" }}  }};
                    </span>
                </ng-container>
            </div>
        </siTableColumn>
"""
}

fun <T : ItemI<*>> T.toAngularTableListEntityFromBasicMultipleEnums(findParentNonInternal: ItemI<*>?, parentName: String, totalChild: Int, toStr: List<AttributeI<*>>): String =
    """
        <siTableColumn [widthFactor]="${roundToNearestHalf(if(findParentNonInternal.isEMPTY()) {(this.parent().name().length + this.name().length).toDouble() / totalChild.toDouble()} else {(this.findParentNonInternal()!!
        .name().length + this.name().length).toDouble() / totalChild.toDouble()})}" key="${if(findParentNonInternal.isEMPTY()) "" else findParentNonInternal?.name()?.lowercase(Locale.getDefault()) }${this.name().lowercase(Locale.getDefault())}" name="{{'${if(findParentNonInternal.isEMPTY()) "" else findParentNonInternal?.name()?.lowercase(Locale.getDefault()) + "."}table.${this.name().lowercase(Locale.getDefault())}' | translate}}" [enableClearFilter]="true" [filterFunction]="${parentName.lowercase(Locale.getDefault())}.findBy${this.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}">
            <div *siTableCell="let row = row; let i = index">
                <div matTooltip="{{ ${parentName.lowercase(Locale.getDefault())}.tooltip(row${if(findParentNonInternal.isEMPTY()) "" else "['${findParentNonInternal?.name()?.toCamelCase()
            ?.replaceFirstChar { it.lowercase(Locale.getDefault()) }}']"}['${this.name().lowercase(Locale.getDefault())}']) }}" matTooltipClass="custom-tooltip">
                    {{row${if(findParentNonInternal.isEMPTY()) "" else "['" + findParentNonInternal?.name()?.lowercase(Locale.getDefault()) + "']"}['${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']${toStr.isNotEmpty().then{ """['${toStr.first().name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']""" }}}};
                </div>
            </div>
        </siTableColumn>
"""

fun <T : ItemI<*>> T.toAngularTableListDate(parentName: String = "", totalChild: Int): String =
    """
        <siTableColumn [widthFactor]="${roundToNearestHalf(if(parentName.isEmpty()) {(this.parent().name().length + this.name().length).toDouble() / totalChild.toDouble()} else {(parentName.length + this.name().length).toDouble() / totalChild.toDouble()})}" name="{{'${if(parentName.isEmpty()) {this.parent().name().lowercase(Locale.getDefault()) + "."} else "$parentName."}table.${this.name()}.${this.name().lowercase(Locale.getDefault())}' | translate}}" [enableClearFilter]="true">
            <div *siTableCell="let row = row; let i = index">
                {{element['${if(parentName.isEmpty()) "" else "${parentName.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}-"}${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}'] | DateTimeTranslation}}
            </div>
        </siTableColumn>
"""

fun <T : ItemI<*>> T.toAngularTableList(parentName: String = "", elementName: String = "", totalChild: Int, isAggregateView: Boolean): String =
    """
        ${isAggregateView.then { """
        <siTableColumn [widthFactor]="${roundToNearestHalf(if(elementName.isEmpty()) {(this.parent().name().length + this.name().length).toDouble() / totalChild.toDouble()} else {(elementName.length + this.name().length).toDouble() / totalChild.toDouble()})}" key="${if(elementName.isEmpty()) {this.parent().name().lowercase(Locale.getDefault()) } else elementName}${this.name().lowercase(Locale.getDefault())}" name="{{'${if(elementName.isEmpty()) {this.parent().name().lowercase(Locale.getDefault()) + "."} else "$elementName."}table.${this.name().lowercase(Locale.getDefault())}' | translate}}" [enableClearFilter]="true" [filterFunction]="${parentName.lowercase(Locale.getDefault())}.findBy${elementName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${this.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}">
            <div *siTableCell="let row = row; let i = index">
                <a [routerLink]="'./view'" (click)="${if(elementName.isEmpty()) {if(this.parent().parent().name().equals(this.parent().name(), true)) {this.parent().parent().name().lowercase(Locale.getDefault())
    } else {this.parent().parent().name().lowercase(Locale.getDefault()) + this.parent().name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }} else elementName}DataService.saveSpecificData(row, row['${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}'])">{{row['${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']}}</a>
            </div>
        </siTableColumn>
        """}}       
        ${isAggregateView.not().then { """
        <siTableColumn [widthFactor]="${roundToNearestHalf(if(elementName.isEmpty()) {(this.parent().name().length + this.name().length).toDouble() / totalChild.toDouble()} else {(elementName.length + this.name().length).toDouble() / totalChild.toDouble()})}" key="${if(elementName.isEmpty()) {this.parent().name().lowercase(Locale.getDefault()) } else elementName}${this.name().lowercase(Locale.getDefault())}" name="{{'${if(elementName.isEmpty()) {this.parent().name().lowercase(Locale.getDefault()) + "."} else "$elementName."}table.${this.name().lowercase(Locale.getDefault())}' | translate}}" [enableClearFilter]="true" [filterFunction]="${parentName.lowercase(Locale.getDefault())}.findBy${elementName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${this.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}">
            <div *siTableCell="let row = row; let i = index">
                <span>{{row${elementName.isNotEmpty().then { "['${elementName.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']" }}['${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']}}</span>
            </div>
        </siTableColumn>
        """}}
        
"""

fun <T : ItemI<*>> T.toAngularDefaultSCSS(): String =
    """host{}"""

fun <T : ItemI<*>> T.toSCSSOtherFormStyle(elementType: String): String =
    """app-${elementType.lowercase(Locale.getDefault())}-form {
    position: relative;
    left: -10%;
}
"""

fun roundToNearestHalf(number: Double): Double {
    val floorValue = number.toInt()

    return when (number - floorValue) {
        in 0.75 .. 0.99 -> ceil(number)
        in 0.25..0.74 -> floorValue + 0.5
        else -> floorValue.toDouble()
    }
}


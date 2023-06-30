import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.lang.*
import java.util.*
import kotlin.math.ceil

fun <T : AttributeI<*>> T.toHTMLObjectFormEntityForBasic(elementType: String, key: ListMultiHolder<AttributeI<*>>, toStr: List<AttributeI<*>>): String {
    return """
        <fieldset>
            <legend>${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}</legend>
            <si-form-group label="{{'select' | translate}} {{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
                <si-dropdown inputId="control${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [dropdownOptions]="option${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}">
                    <ng-container *siDropdownOption="let value = value">
                        <span matTooltip="{{ ${this.parent().name().lowercase(Locale.getDefault())}DataService.tooltipText }}" (mouseenter)="${this.parent().name().lowercase(Locale.getDefault())}DataService.onMouseEnter(value)" (mouseleave)="${this.parent().name().lowercase(Locale.getDefault())}DataService.onMouseLeave()">{{value${
        toStr.isNotEmpty().then{ """['${toStr.first().name()}']""" }}}}</span>
                    </ng-container>
                </si-dropdown>
            </si-form-group>
        </fieldset>"""
}

fun <T : AttributeI<*>> T.toHTMLStringForm(indent: String, parentName: String = "", isBasic: Boolean): String {
    return """
        ${indent}<si-form-group label="{{'${if (parentName.isBlank()) {this.parent().name().lowercase(Locale.getDefault())} else {parentName.lowercase(Locale.getDefault())}}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
            ${indent}<input siFormControl [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}" ${isBasic.not().then { """formControlName="${if (parentName.isBlank()) {
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
            ${indent}<input type="number" siFormControl [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}" ${isBasic.not().then { """formControlName="${this.parent().name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${this.name().toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""" }}>
        ${indent}</si-form-group>"""
}

fun <T : AttributeI<*>> T.toHTMLUploadForm(indent: String, isBasic: Boolean): String {
    return """
        ${indent}<si-form-group label="{{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
            ${indent}<input type="file" siFormControl (change)="${this.parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.selectFiles(${"$"}event)" [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}" ${isBasic.not().then { """formControlName="${this.parent().name().toCamelCase()
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
                         ${indent}[(ngModel)]="project.${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}"
                         ${isBasic.not().then { """${indent}formControlName="${this.parent().name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${this.name().toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""" }}>
            ${indent}</si-dropdown>
        ${indent}</si-form-group>"""
}

fun <T : AttributeI<*>> T.toHTMLDateForm(indent: String, isBasic: Boolean): String {
    return """
        ${indent}<si-form-group label="{{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}} MM/DD/YYYY">
            ${indent}<input siFormControl [matDatepicker]="picker" [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}" [ngModel]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()} | date: 'yyyy-MM-dd'" ${isBasic.not().then { """formControlName="${this.parent().name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${this.name().toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""" }}>
            ${indent}<mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
            ${indent}<mat-datepicker #picker></mat-datepicker>
        ${indent}</si-form-group>"""
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

fun <T : AttributeI<*>> T.toHTMLObjectFormEntity(elementType: String, key: ListMultiHolder<AttributeI<*>>, toStr: List<AttributeI<*>>): String {
    return """
        <fieldset>
            <legend>{{"${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}" | translate}}
                <a class="newButton bg-dark normal-font-size" [routerLink]="'../../${elementType.lowercase(Locale.getDefault())}/new'"
                                                            routerLinkActive="active-link">
                    <span aria-hidden='true' class='iconUxt addCircle filled'></span>
                </a>
            </legend>
            <si-form-group label="{{'select' | translate}} {{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
                <si-dropdown inputId="control${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [dropdownOptions]="${this.parent().name().lowercase(Locale.getDefault())}DataService.option${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}"
                             formControlName="${this.parent().name().lowercase(Locale.getDefault())}${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}">
                    <ng-container *siDropdownOption="let value = value">
                        <span matTooltip="{{ ${this.parent().name().lowercase(Locale.getDefault())}DataService.tooltipText }}" (mouseenter)="${this.parent().name().lowercase(Locale.getDefault())}DataService.onMouseEnter(value)" (mouseleave)="${this.parent().name().lowercase(Locale.getDefault())}DataService.onMouseLeave()">{{value${
        toStr.isNotEmpty().then{ """['${toStr.first().name()}']""" }}}}</span>
                    </ng-container>
                </si-dropdown>
            </si-form-group>
        </fieldset>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormEntityMultiple(elementType: String, toStr: List<AttributeI<*>>): String {
    return """
        <fieldset>
            <legend>{{"${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}" | translate}}
                <a class="newButton bg-dark normal-font-size" [routerLink]="'../../${elementType.lowercase(Locale.getDefault())}/new'"
                                                            routerLinkActive="active-link">
                    <span aria-hidden='true' class='iconUxt addCircle filled'></span>
                </a>
            </legend>
            <si-form-group label="{{'select' | translate}} {{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
                <si-dropdown inputId="control${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [dropdownOptions]="${this.parent().name().lowercase(Locale.getDefault())}DataService.option${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}"
                             formControlName="${this.parent().name().lowercase(Locale.getDefault())}${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}">
                    <ng-container *siDropdownOption="let value = value">
                        <span matTooltip="{{ ${this.parent().name().lowercase(Locale.getDefault())}DataService.tooltipText }}" (mouseenter)="${this.parent().name().lowercase(Locale.getDefault())}DataService.onMouseEnter(value)" (mouseleave)="${this.parent().name().lowercase(Locale.getDefault())}DataService.onMouseLeave()">{{value${
        toStr.isNotEmpty().then{ """['${toStr.first().name()}']""" }}}}</span>
                    </ng-container>
                </si-dropdown>
            </si-form-group>
        </fieldset>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormBasicFromEntityMultiple(elementType: String, toStr: List<AttributeI<*>>): String {
    return """
        <fieldset>
            <legend>{{"${this.parent().name().lowercase(Locale.getDefault())}.table.${elementType.toCamelCase().lowercase(Locale.getDefault())}" | translate}}</legend>
            <si-form-group label="{{'select' | translate}} {{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
                <si-dropdown inputId="control${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [dropdownOptions]="filteredOptions${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}"
                             >
                    <ng-container *siDropdownOption="let value = value">
                        <span matTooltip="{{ ${elementType.toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.tooltipText }}" (mouseenter)="${elementType.toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.onMouseEnter(value)" (mouseleave)="${elementType.toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.onMouseLeave()">{{value${
        toStr.isNotEmpty().then{ """['${toStr.first().name()}']""" }}}}</span>
                    </ng-container>
                </si-dropdown>
            </si-form-group>
        </fieldset>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormEnumMultiple(elementType: String, elementName: String, isBasic: Boolean, toStr: List<AttributeI<*>>): String {
    return """
        <fieldset>
            <legend>{{"${if(elementName.isEmpty()) {this.parent().name().lowercase(Locale.getDefault()) + "."} else "$elementName."}table.${elementType.toCamelCase().lowercase(Locale.getDefault())}" | translate}}</legend>
            <si-form-group label="{{'${if(elementName.isEmpty()) {this.parent().name().lowercase(Locale.getDefault()) + "."} else "$elementName."}table.${elementType.toCamelCase().lowercase(Locale.getDefault())}' | translate}}">
                <si-dropdown inputId="control${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [dropdownOptions]="option${elementType.toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                             [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}"
                             ${isBasic.not().then { """formControlName="${this.parent().name().lowercase(Locale.getDefault())}${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""" }}>
                    <ng-container *siDropdownOption="let value = value">
                        <span matTooltip="{{ dataService.tooltipText }}" (mouseenter)="dataService.onMouseEnter(value)" (mouseleave)="dataService.onMouseLeave()">{{value${
        toStr.isNotEmpty().then{ """['${toStr.first().name()}']""" }}}}</span>
                    </ng-container>
                </si-dropdown>
            </si-form-group>
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

fun <T : TypeI<*>> T.toAngularTableListBasic(parentName: String = "", basicName: String = "", basicParentName: String = "", isChild: Boolean, totalChild: Int, containAggregateProp: Boolean = false): String =
    this.props().filter { !isEMPTY() }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.toAngularTableListEntityFromBasic(it.type().name(), it.type().findParentNonInternal(), parentName, it.type().props(), isChild, "Object", totalChild,  it.type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })
            /*is BasicI<*> -> it.toAngularTableListBasics(it.type().name(), it.type().findParentNonInternal(), parentName, it.type().props(), isChild, "Object", totalChild,
                it.type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })*/
            is BasicI<*> -> it.type().toAngularTableListBasic(parentName, it.name(), it.parent().name(),true, totalChild, containAggregateProp)
            is EnumTypeI<*> -> it.toAngularTableListEnum(basicName, totalChild)
            else -> {
                when(it.type().name()) {
                    "Date" -> it.toAngularTableListDate(basicName, totalChild)
                    "List" -> when(it.type().generics().first().type()) {
                        is EntityI<*>, is ValuesI<*> ->  it.toAngularTableListEntityFromBasicMultiple(it.type().generics().first().type().name(), it.type().generics().first().type().findParentNonInternal(), parentName, it.type().generics().first().type().props(), isChild, "List", totalChild,  it.type().generics().first().type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })
                        is EnumTypeI<*> -> it.toAngularTableListEntityFromBasicMultipleEnums(it.type().generics().first().type().name(), it.type().generics().first().type().findParentNonInternal(), parentName, it.type().generics().first().type().props(), isChild, "List", totalChild,  it.type().generics().first().type().props().filter { prop -> prop.isToStr() == true && !prop.isEMPTY() })
                        else -> it.toAngularTableList(parentName, basicName, basicParentName, totalChild, containAggregateProp)
                    }
                    else -> it.toAngularTableList(parentName, basicName, basicParentName, totalChild, containAggregateProp)
                }
            }
        }
    }

fun <T : ItemI<*>> T.toAngularTableListEnum(parentName: String = "", totalChild: Int): String =
    """
        <siTableColumn [widthFactor]="${roundToNearestHalf(((this.parent().name().length + this.name().length).toDouble() / totalChild.toDouble()))}" key="${this.name().lowercase(Locale.getDefault())}" name="{{'${if(parentName.isEmpty()) {this.parent().name().lowercase(Locale.getDefault()) + "."}  else "$parentName."}table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
            <div *siTableCell="let row = row; let i = index">
                <span>{{row${if(parentName.isEmpty()) {"['" + this.parent().name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) } + "']"}  else "['${parentName.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']"}['${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']}}</span>
            </div>
        </siTableColumn>
"""

fun <T : ItemI<*>> T.toAngularTableListEntityFromBasic(elementName: String, findParentNonInternal: ItemI<*>?, parentName: String, key: ListMultiHolder<AttributeI<*>>, isChild: Boolean, type: String, totalChild: Int, toStr: List<AttributeI<*>>): String =
    """
        <siTableColumn [widthFactor]="${roundToNearestHalf(((this.parent().name().length + this.name().length).toDouble() / totalChild.toDouble()))}" key="${this.name().lowercase(Locale.getDefault())}" name="{{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}" [enableClearFilter]="true">
            <div *siTableCell="let row = row; let i = index">
                <a matTooltip="{{ ${parentName.toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.tooltipText }}" (mouseenter)="${parentName.toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.onMouseEnter(row${if(isChild) "['${this.parent().name()
        .lowercase(Locale.getDefault())}']['${this.name()
        .lowercase(Locale.getDefault())}']" else "['${this.name()
        .lowercase(Locale.getDefault())}']"})" (mouseleave)="${parentName.toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.onMouseLeave()" (click)="${parentName.replaceFirstChar {
        it.lowercase(
            Locale.getDefault()
        )
    }}DataService.searchItems(i, row${if(isChild) "['${this.parent().name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']['${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']" else "['${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']"}, '${findParentNonInternal?.name()
        ?.toCamelCase()?.replaceFirstChar { it.lowercase(Locale.getDefault()) }}/${elementName.lowercase(
        Locale.getDefault()
    )}', '${parentName.lowercase(
        Locale.getDefault()
    )}')">{{row['${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']${toStr.isNotEmpty().then{ """['${toStr.first().name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']""" }}}}</a>
            </div>
        </siTableColumn>
"""

fun <T : ItemI<*>> T.toAngularTableListEntityFromBasicMultiple(elementName: String, findParentNonInternal: ItemI<*>?, parentName: String, key: ListMultiHolder<AttributeI<*>>, isChild: Boolean, type: String, totalChild: Int, toStr: List<AttributeI<*>>): String =
    """
        <siTableColumn [widthFactor]="${roundToNearestHalf(((this.parent().name().length + this.name().length).toDouble() / totalChild.toDouble()))}" key="${this.name().lowercase(Locale.getDefault())}" name="{{'${this.parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}" [enableClearFilter]="true">
            <div *siTableCell="let row = row; let i = index">
                <a matTooltip="{{ ${parentName.toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.tooltipText }}" (mouseenter)="${parentName.toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.onMouseEnter(row${if(isChild) "['${this.parent().name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']['${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']" else "['${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']"})" (mouseleave)="${parentName.toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.onMouseLeave()" (click)="${parentName.replaceFirstChar {
        it.lowercase(
            Locale.getDefault()
        )
    }}DataService.searchItems(i, row${if(isChild) "['${this.parent().name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']['${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']" else "['${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']"}, '${findParentNonInternal?.name()
        ?.toCamelCase()?.replaceFirstChar { it.lowercase(Locale.getDefault()) }}/${elementName.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}', '${parentName.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}')">
                    {{row['${this.name().lowercase(Locale.getDefault())}']${toStr.isNotEmpty().then{ """['${toStr.first().name()}']""" }}}}
                </a>
            </div>
        </siTableColumn>
"""

/*${type}Of(${if(isChild) "${this.parent().name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}-${this.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${if (key.any { it.type().name() == "String" }) { key.first { it.type().name() == "String" }.name() } else {""}}" else "${
    this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}${if (key.any { it.type().name() == "String" }) {
    key.first { it.type().name() == "String" }.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
} else {""}}"})*/
fun <T : ItemI<*>> T.toAngularTableListBasics(elementName: String, findParentNonInternal: ItemI<*>?, parentName: String, key: ListMultiHolder<AttributeI<*>>, isChild: Boolean, type: String, totalChild: Int, toStr: List<AttributeI<*>>): String =
    """
        <siTableColumn [widthFactor]="${roundToNearestHalf(if(findParentNonInternal.isEMPTY()) {(this.parent().name().length + this.name().length).toDouble() / totalChild.toDouble()} else {(this.findParentNonInternal()!!
        .name().length + this.name().length).toDouble() / totalChild.toDouble()})}" key="${this.name().lowercase(Locale.getDefault())}" name="{{'${if(findParentNonInternal.isEMPTY()) "" else findParentNonInternal?.name()?.lowercase(Locale.getDefault()) + "."}table.${this.name().lowercase(Locale.getDefault())}' | translate}}" [enableClearFilter]="true">
            <div *siTableCell="let row = row; let i = index">
                <div matTooltip="{{ ${parentName.toCamelCase().replaceFirstChar {
        it.lowercase(
            Locale.getDefault()
        )
    }}DataService.tooltipText }}" (mouseenter)="${parentName.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.onMouseEnter(row['${this.name().lowercase(Locale.getDefault())}'])" (mouseleave)="${parentName.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.onMouseLeave()">{{row['${this.name().lowercase(Locale.getDefault())}']${toStr.isNotEmpty().then{ """['${toStr.first().name()}']""" }}}}</div>
            </div>
        </siTableColumn>
"""

fun <T : ItemI<*>> T.toAngularTableListEntityFromBasicMultipleEnums(elementName: String, findParentNonInternal: ItemI<*>?, parentName: String, key: ListMultiHolder<AttributeI<*>>, isChild: Boolean, type: String, totalChild: Int, toStr: List<AttributeI<*>>): String =
    """
        <siTableColumn [widthFactor]="${roundToNearestHalf(if(findParentNonInternal.isEMPTY()) {(this.parent().name().length + this.name().length).toDouble() / totalChild.toDouble()} else {(this.findParentNonInternal()!!
        .name().length + this.name().length).toDouble() / totalChild.toDouble()})}" key="${this.name().lowercase(Locale.getDefault())}" name="{{'${if(findParentNonInternal.isEMPTY()) "" else findParentNonInternal?.name()?.lowercase(Locale.getDefault()) + "."}table.${this.name().lowercase(Locale.getDefault())}' | translate}}" [enableClearFilter]="true">
            <div *siTableCell="let row = row; let i = index">
                <div matTooltip="{{ ${parentName.toCamelCase().replaceFirstChar {
        it.lowercase(
            Locale.getDefault()
        )
    }}DataService.tooltipText }}" (mouseenter)="${parentName.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.onMouseEnter(row${if(findParentNonInternal.isEMPTY()) "" else "['${findParentNonInternal?.name()?.toCamelCase()
        ?.replaceFirstChar { it.lowercase(Locale.getDefault()) }}']"}['${this.name().lowercase(Locale.getDefault())}'])" (mouseleave)="${parentName.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.onMouseLeave()">
                    {{row${if(findParentNonInternal.isEMPTY()) "" else "['" + findParentNonInternal?.name()?.lowercase(Locale.getDefault()) + "']"}['${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']${toStr.isNotEmpty().then{ """['${toStr.first().name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']""" }}}}
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

fun <T : ItemI<*>> T.toAngularTableList(parentName: String = "", elementName: String = "", basicParentName: String = "", totalChild: Int, isAggregateView: Boolean): String =
    """
        ${isAggregateView.then { """
        <siTableColumn [widthFactor]="${roundToNearestHalf(if(elementName.isEmpty()) {(this.parent().name().length + this.name().length).toDouble() / totalChild.toDouble()} else {(elementName.length + this.name().length).toDouble() / totalChild.toDouble()})}" key="${this.name().lowercase(Locale.getDefault())}" name="{{'${if(elementName.isEmpty()) {this.parent().name().lowercase(Locale.getDefault()) + "."} else "$elementName."}table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
            <div *siTableCell="let row = row; let i = index">
                <a [routerLink]="'./view/' + row${elementName.isNotEmpty().then { "['${elementName.toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']" }}['${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']" [queryParams]="{name: row['${this.name().lowercase(Locale.getDefault())}']}" (click)="${if(elementName.isEmpty()) {this.parent().name().lowercase(Locale.getDefault())} else elementName}DataService.saveSpecificData(row)">{{row['${this.name().toCamelCase()
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']}}</a>
            </div>
        </siTableColumn>
        """}}       
        ${isAggregateView.not().then { """
        <siTableColumn [widthFactor]="${roundToNearestHalf(if(elementName.isEmpty()) {(this.parent().name().length + this.name().length).toDouble() / totalChild.toDouble()} else {(elementName.length + this.name().length).toDouble() / totalChild.toDouble()})}" key="${this.name().lowercase(Locale.getDefault())}" name="{{'${if(elementName.isEmpty()) {this.parent().name().lowercase(Locale.getDefault()) + "."} else "$elementName."}table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
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


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
            
            <ix-select (itemSelectionChange)="bindTo${this.parent().parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${elementType.toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(${"$"}event)" [selectedIndices]="selectedIndices" i18nSelectListHeader="{{'select' | translate}} {{'${this.parent().parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}" i18nPlaceholder="{{'select' | translate}} {{'${this.parent().parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
                <ix-select-item *ngFor="let item of option${this.parent().parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${elementType.toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}; let i = index" label="{{item${
        toStr.isNotEmpty().then{ """['${toStr.first().name()}']""" }}}}" value="{{i}}"></ix-select-item>
            </ix-select>          
        </fieldset>"""
}

fun <T : AttributeI<*>> T.toHTMLStringForm(indent: String, parentName: String = "", isBasic: Boolean): String {
    return """
        ${indent}<ix-input-group>
            ${indent}<span slot="input-start">{{'${if (parentName.isBlank()) {if(this.parent().name().equals(this.parent().parent().name(), true)) {
        this.parent().name().lowercase(Locale.getDefault())
    } else {"""${this.parent().parent().name().lowercase(Locale.getDefault())}${this.parent().name().lowercase(Locale.getDefault())}"""}} else {parentName.lowercase(Locale.getDefault())}}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}</span>
            ${indent}<input type="text" class="form-control" [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}" ${isBasic.then { """[disabled]="isDisabled"""" }} ${isBasic.not().then { """formControlName="${if (parentName.isBlank()) {
        this.parent().name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }
    } else {
        parentName.toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }
    }}${this.name().toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""" }}/>
        ${indent}</ix-input-group>"""
}

fun <T : AttributeI<*>> T.toHTMLNumberForm(indent: String, isBasic: Boolean): String {
    return """
        ${indent}<ix-input-group>
            ${indent}<span slot="input-start">{{'${if(this.parent().name().equals(this.parent().parent().name(), true)) {
        this.parent().name().lowercase(Locale.getDefault())
    } else {"""${this.parent().parent().name().lowercase(Locale.getDefault())}${this.parent().name().lowercase(Locale.getDefault())}"""}}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}</span>
            ${indent}<input type="number" class="form-control" [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}" ${isBasic.then { """[disabled]="isDisabled"""" }} ${isBasic.not().then { """formControlName="${this.parent().name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${this.name().toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""" }}/>
        ${indent}</ix-input-group>"""
}

fun <T : AttributeI<*>> T.toHTMLUploadForm(indent: String, isBasic: Boolean): String {
    return """
        ${indent}<ix-upload selectFileText="{{'${if(this.parent().name().equals(this.parent().parent().name(), true)) {
        this.parent().name().lowercase(Locale.getDefault())
    } else {"""${this.parent().parent().name().lowercase(Locale.getDefault())}${this.parent().name().lowercase(Locale.getDefault())}"""}}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}" (change)="${this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${this.parent().name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}DataService.selectFiles(${"$"}event)" [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}" ${isBasic.then { """[disabled]="isDisabled"""" }} ${isBasic.not().then { """formControlName="${this.parent().name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${this.name().toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""" }}></ix-upload>"""
}

fun <T : AttributeI<*>> T.toHTMLBooleanForm(indent: String, isBasic: Boolean): String {
    return """
        ${indent}<ix-select [selectedIndices]="selectedIndices" i18nSelectListHeader="{{'${if(this.parent().name().equals(this.parent().parent().name(), true)) {
        this.parent().name().lowercase(Locale.getDefault())
    } else {"""${this.parent().parent().name().lowercase(Locale.getDefault())}${this.parent().name().lowercase(Locale.getDefault())}"""}}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}" i18nPlaceholder="{{'${if(this.parent().name().equals(this.parent().parent().name(), true)) {
        this.parent().name().lowercase(Locale.getDefault())
    } else {"""${this.parent().parent().name().lowercase(Locale.getDefault())}${this.parent().name().lowercase(Locale.getDefault())}"""}}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
            ${indent}<ix-select-item *ngFor="let item of [true, false]; let i = index" label="{{item}}" value="{{i}}"></ix-select-item>
        ${indent}</ix-select>"""
}

fun <T : AttributeI<*>> T.toHTMLDateForm(indent: String, isBasic: Boolean): String {
    return """
        ${indent}<ix-select [selectedIndices]="selectedIndices" i18nSelectListHeader="{{'select' | translate}} {{'date' | translate}}" i18nPlaceholder="{{'${if(this.parent().name().equals(this.parent().parent().name(), true)) {
        this.parent().name().lowercase(Locale.getDefault())
    } else {"""${this.parent().parent().name().lowercase(Locale.getDefault())}${this.parent().name().lowercase(Locale.getDefault())}"""}}.table.${this.name().lowercase(Locale.getDefault())}' | translate}} MM/DD/YYYY">
            ${indent}<ix-select-item><ix-date-picker [(ngModel)]="${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()}" ${isBasic.then { """[disabled]="isDisabled"""" }} ${isBasic.not().then { """formControlName="${this.parent().name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}${this.name().toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""" }}></ix-date-picker></ix-select-item>
        ${indent}</ix-select>"""
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
        <basic-${parentName.lowercase()}-${elementType.lowercase(Locale.getDefault())} [isDisabled]="isDisabled" [parentName]="'${if(this.parent().name().equals(this.parent().parent().name(), true)) {
        this.parent().name().lowercase(Locale.getDefault())
    } else {"""${this.parent().parent().name().lowercase(Locale.getDefault())}${this.parent().name().lowercase(Locale.getDefault())}"""}}'" [${elementType.lowercase(
        Locale.getDefault()
    )}]="${this.parent().name()
        .lowercase(Locale.getDefault())}.${this.name().toCamelCase()}"></basic-${parentName.lowercase()}-${elementType.lowercase(
        Locale.getDefault()
    )}>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormEntity(elementTypeParent: String, elementType: String, toStr: List<AttributeI<*>>): String {
    return """
        <fieldset>
            <legend>{{"${if(this.parent().name().equals(this.parent().parent().name(), true)) {
        this.parent().name().lowercase(Locale.getDefault())
    } else {"""${this.parent().parent().name().lowercase(Locale.getDefault())}${this.parent().name().lowercase(Locale.getDefault())}"""}}.table.${this.name().lowercase(Locale.getDefault())}" | translate}}
                <a class="newButton normal-font-size" (click)="${if(this.parent().parent().name().equals(this.parent().name(), true)) {this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} else {this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) } + this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}}DataService.locationNavigateTo('${this.type().findParentNonInternal()?.name()?.lowercase(Locale.getDefault())}/${elementType.lowercase(Locale.getDefault())}/new', false)">
                    <ix-icon name="add-circle" size="20"></ix-icon>
                </a>
            </legend>
            
            <ix-select (itemSelectionChange)="bindTo${elementTypeParent.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${elementType.toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(${"$"}event)" [selectedIndices]="selectedIndices" i18nSelectListHeader="{{'select' | translate}} {{'${if(this.parent().name().equals(this.parent().parent().name(), true)) {
        this.parent().name().lowercase(Locale.getDefault())
    } else {"""${this.parent().parent().name().lowercase(Locale.getDefault())}${this.parent().name().lowercase(Locale.getDefault())}"""}}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}" i18nPlaceholder="{{'select' | translate}} {{'${if(this.parent().name().equals(this.parent().parent().name(), true)) {
        this.parent().name().lowercase(Locale.getDefault())
    } else {"""${this.parent().parent().name().lowercase(Locale.getDefault())}${this.parent().name().lowercase(Locale.getDefault())}"""}}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
                <ix-select-item *ngFor="let item of ${if(this.parent().parent().name().equals(this.parent().name(), true)) {this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} else {this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) } + this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}}DataService.option${elementTypeParent.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${elementType.toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}; let i = index" label="{{item${
        toStr.isNotEmpty().then{ """['${toStr.first().name()}']""" }}}}" value="{{i}}"></ix-select-item>
            </ix-select>          
        </fieldset>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormEntityMultiple(elementTypeParent: String, elementType: String, toStr: List<AttributeI<*>>): String {
    return """
        <fieldset>
            <legend>{{"${if(this.parent().name().equals(this.parent().parent().name(), true)) {
        this.parent().name().lowercase(Locale.getDefault())
    } else {"""${this.parent().parent().name().lowercase(Locale.getDefault())}${this.parent().name().lowercase(Locale.getDefault())}"""}}.table.${this.name().lowercase(Locale.getDefault())}" | translate}}
                <a class="newButton normal-font-size" (click)="${if(this.parent().parent().name().equals(this.parent().name(), true)) {this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} else {this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) } + this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault())
    else it.toString() }}}DataService.locationNavigateTo('${this.parent().parent().name().lowercase(Locale.getDefault())}/${elementType.lowercase(Locale.getDefault())}/new', false, 'isList: true')">
                    <ix-icon name="add-circle" size="20"></ix-icon>
                </a>
            </legend>
            
            <ix-select allowClear="true" mode="multiple" (itemSelectionChange)="bindTo${if(elementTypeParent.equals(elementType, true)) {elementTypeParent.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}
    else {elementTypeParent.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + elementType.toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}}(${"$"}event)" [selectedIndices]="multipleSelectedIndices" i18nSelectListHeader="{{'select' | translate}} {{'${if(this.parent().name().equals(this.parent().parent().name(), true)) {
        this.parent().name().lowercase(Locale.getDefault())
    } else {"""${this.parent().parent().name().lowercase(Locale.getDefault())}${this.parent().name().lowercase(Locale.getDefault())}"""}}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}" i18nPlaceholder="{{'select' | translate}} {{'${if(this.parent().name().equals(this.parent().parent().name(), true)) {
        this.parent().name().lowercase(Locale.getDefault())
    } else {"""${this.parent().parent().name().lowercase(Locale.getDefault())}${this.parent().name().lowercase(Locale.getDefault())}"""}}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
                <ix-select-item *ngFor="let item of ${if(this.parent().parent().name().equals(this.parent().name(), true)) {this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} else {this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) } + this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault())
    else it.toString() }}}DataService.option${if(elementTypeParent.equals(elementType, true)) {elementTypeParent.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}
    else {elementTypeParent.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + elementType.toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}}; let i = index" label="{{item${
        toStr.isNotEmpty().then{ """['${toStr.first().name()}']""" }}}}" value="{{i}}"></ix-select-item>
            </ix-select>
        </fieldset>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormBasicFromEntityMultiple(elementType: String, toStr: List<AttributeI<*>>): String {
    return """
        <fieldset>
            <legend>{{"${if(this.parent().name().equals(this.parent().parent().name(), true)) {
        this.parent().name().lowercase(Locale.getDefault())
    } else {"""${this.parent().parent().name().lowercase(Locale.getDefault())}${this.parent().name().lowercase(Locale.getDefault())}"""}}.table.${elementType.toCamelCase().lowercase(Locale.getDefault())}" | translate}}</legend>
            
            <ix-select allowClear="true" mode="multiple" (itemSelectionChange)="bindTo${this.parent().parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${elementType.toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(${"$"}event)" [selectedIndices]="multipleSelectedIndices" i18nSelectListHeader="{{'select' | translate}} {{'${this.parent().parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}" i18nPlaceholder="{{'select' | translate}} {{'${this.parent().parent().name().lowercase(Locale.getDefault())}.table.${this.name().lowercase(Locale.getDefault())}' | translate}}">
                <ix-select-item *ngFor="let item of option${this.parent().parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${elementType.toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}; let i = index" label="{{item['name']}}" value="{{i}}"></ix-select-item>
            </ix-select>
        </fieldset>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormEnumMultiple(elementTypeParent: String, elementType: String, elementName: String, isBasic: Boolean, toStr: List<AttributeI<*>>): String {
    return """
        <fieldset>
            <legend>{{"${if(elementName.isEmpty()) {this.parent().parent().name().lowercase(Locale.getDefault()) + this.parent().name().lowercase(Locale.getDefault()) + "."} else "$elementName."}table.${elementType.toCamelCase().lowercase(Locale.getDefault())}" | translate}}</legend>
            <ix-select allowClear="true" mode="multiple" (itemSelectionChange)="bindTo${if(elementTypeParent.equals(elementType, true)) {elementTypeParent.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}
    else {elementTypeParent.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + elementType.toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}}(${"$"}event)" [selectedIndices]="multipleSelectedIndices" i18nSelectListHeader="{{'select' | translate}} {{'${if(elementName.isEmpty()) {this.parent().parent().name().lowercase(Locale.getDefault()) + this.parent().name().lowercase(Locale.getDefault()) + "."} else "$elementName."}table.${elementType.toCamelCase().lowercase(Locale.getDefault())}' | translate}}" i18nPlaceholder="{{'select' | translate}} {{'${if(elementName.isEmpty()) {this.parent().parent().name().lowercase(Locale.getDefault()) + this.parent().name().lowercase(Locale.getDefault()) + "."} else "$elementName."}table.${elementType.toCamelCase().lowercase(Locale.getDefault())}' | translate}}">
                <ix-select-item *ngFor="let item of ${if (!isBasic) { """${if(this.parent().parent().name().equals(this.parent().name(), true)) {this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} else {this.parent().parent().name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) } + this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}}DataService.option${elementTypeParent.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${elementType.toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}""" } else { """option${elementType.toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}""" }}; let i = index" label="{{item}}" value="{{i}}"></ix-select-item>
            </ix-select>
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
                <td>
                    <span>{{ row['${this.name().toCamelCase()
                    .replaceFirstChar { it.lowercase(Locale.getDefault()) }}'] }}</span>
                </td>
"""

fun <T : ItemI<*>> T.toAngularTableListEntityFromBasic(elementName: String, findParentNonInternal: ItemI<*>?, parentName: String, isChild: Boolean, totalChild: Int, toStr: List<AttributeI<*>>, props: ListMultiHolder<AttributeI<*>>): String {
    val serviceName = if(this.parent().parent().name().equals(parentName, true)) {this.parent().parent().name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} else {this.parent().parent().name().toCamelCase()
            .replaceFirstChar { it.lowercase(Locale.getDefault())} + parentName.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}
    return """
                <td>
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
                </td>
"""
}


fun <T : ItemI<*>> T.toAngularTableListEntityFromBasicMultiple(elementName: String, parentName: String, isChild: Boolean, totalChild: Int, toStr: List<AttributeI<*>>): String {
    return """
                <td>
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
                </td>
"""
}

fun <T : ItemI<*>> T.toAngularTableListEntityFromBasicMultipleEnums(findParentNonInternal: ItemI<*>?, parentName: String, totalChild: Int, toStr: List<AttributeI<*>>): String =
    """
                <td>
                    <div matTooltip="{{ ${parentName.lowercase(Locale.getDefault())}.tooltip(row${if(findParentNonInternal.isEMPTY()) "" else "['${findParentNonInternal?.name()?.toCamelCase()
                    ?.replaceFirstChar { it.lowercase(Locale.getDefault()) }}']"}['${this.name().lowercase(Locale.getDefault())}']) }}" matTooltipClass="custom-tooltip">
                            {{row${if(findParentNonInternal.isEMPTY()) "" else "['" + findParentNonInternal?.name()?.lowercase(Locale.getDefault()) + "']"}['${this.name().toCamelCase()
                    .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']${toStr.isNotEmpty().then{ """['${toStr.first().name().toCamelCase()
                    .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']""" }}}};
                    </div>               
                </td>
"""

fun <T : ItemI<*>> T.toAngularTableListDate(parentName: String = "", totalChild: Int): String =
    """
                <td>
                    <div>
                        {{element['${if(parentName.isEmpty()) "" else "${parentName.toCamelCase()
                    .replaceFirstChar { it.lowercase(Locale.getDefault()) }}-"}${this.name().toCamelCase()
                    .replaceFirstChar { it.lowercase(Locale.getDefault()) }}'] | DateTimeTranslation}}
                    </div>
                </td>
"""

fun <T : ItemI<*>> T.toAngularTableList(parentName: String = "", elementName: String = "", totalChild: Int, isAggregateView: Boolean): String =
    """
        ${isAggregateView.then { """
                <td>
                    <a (click)="${if(elementName.isEmpty()) {if(this.parent().parent().name().equals(this.parent().name(), true)) {this.parent().parent().name().lowercase(Locale.getDefault())
            } else {this.parent().parent().name().lowercase(Locale.getDefault()) + this.parent().name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }} else elementName}DataService.saveSpecificData(row, row['${this.name().toCamelCase()
                    .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']); ${if(elementName.isEmpty()) {if(this.parent().parent().name().equals(this.parent().name(), true)) {this.parent().parent().name().lowercase(Locale.getDefault())
    } else {this.parent().parent().name().lowercase(Locale.getDefault()) + this.parent().name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }} else elementName}DataService.locationNavigateTo('view', true)">{{row['${this.name().toCamelCase()
                    .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']}}</a>
                </td>
        """}}       
        ${isAggregateView.not().then { """
                <td>
                    <span>{{row${elementName.isNotEmpty().then { "['${elementName.toCamelCase()
                    .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']" }}['${this.name().toCamelCase()
                    .replaceFirstChar { it.lowercase(Locale.getDefault()) }}']}}</span>
                </td>
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


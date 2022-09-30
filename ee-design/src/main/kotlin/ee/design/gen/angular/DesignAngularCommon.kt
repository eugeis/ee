import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.toTypeScriptIfNative

fun <T : ItemI<*>> T.toAngularModuleHTML(element: ModuleI<*>): String =
    """<mat-sidenav-container>
    <mat-sidenav #drawer
                 [mode]="'side'" [fixedInViewport]="true">
        <mat-nav-list>
            <a *ngFor="let pageName of ${element.name().toLowerCase()}ViewService.pageElement"
               mat-list-item
               routerLinkActive="active-link"
               [routerLink]="'/' + pageName.toLowerCase()"
            >{{pageName.toUpperCase()}}</a>
        </mat-nav-list>
    </mat-sidenav>

    <mat-sidenav-content>
        <mat-toolbar>
            <button mat-icon-button (click)="drawer.toggle()">
                <mat-icon>menu</mat-icon>
            </button>
            <span>{{pageName}}</span>
        </mat-toolbar>

        <nav mat-tab-nav-bar>
            <div *ngFor="let pageTabsName of ${element.name().toLowerCase()}ViewService.tabElement">
                <a mat-tab-link
                   [routerLink]="'/${element.name().toLowerCase()}' + '/' + pageTabsName.toLowerCase()"
                   routerLinkActive="active-link"
                >{{pageTabsName.toUpperCase()}}
                </a>
            </div>
        </nav>
    </mat-sidenav-content>
</mat-sidenav-container>"""

fun <T : ItemI<*>> T.toAngularEntityViewHTML(c: GenerationContext, element: EntityI<*>, enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>): String =
    """<app-${element.parent().name().toLowerCase()} [pageName]="${element.name().toLowerCase()}DataService.pageName"></app-${element.parent().name().toLowerCase()}>

<app-${element.name().toLowerCase()}-form [${element.name().toLowerCase()}]="${element.name().toLowerCase()}"></app-${element.name().toLowerCase()}-form>

<app-button [element]="${element.name().toLowerCase()}" [isEdit]="${element.name().toLowerCase()}DataService.isEdit" [itemIndex]="${element.name().toLowerCase()}DataService.itemIndex"></app-button>
"""

fun <T : ItemI<*>> T.toAngularFormHTML(c: GenerationContext, element: EntityI<*>, enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>): String =
    """
<div>
    <form class="${element.name().toLowerCase()}-form">
        ${element.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toTypeScriptHTMLForms(tab, element, it.name(), it.type().name(), enums, basics)
    }}
    </form>
</div>
"""

fun <T : AttributeI<*>> T.toTypeScriptHTMLForms(indent: String, parent: EntityI<*>, childElement: String, elementType: String, enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>): String {
    var isEnum = false
    var isBasic = false
    enums.forEach {
        if(it.name() == elementType) {
            isEnum = true;
        }
    }
    basics.forEach {
        if(it.name() == elementType) {
            isBasic = true
        }
    }

    return when (elementType.toLowerCase()) {
        "string", "uuid", "text", "float", "int", "boolean", "blob" -> this.toHTMLStringForm(parent, childElement)
        "date", "list" -> this.toHTMLDateForm(parent, childElement)
        else -> {
            when (isEnum) {
                true -> this.toHTMLEnumForm(parent, childElement, elementType)
                else -> {
                    when (isBasic) {
                        true -> this.toHTMLObjectForm(parent, childElement, elementType)
                        false -> this.toHTMLObjectFormEntity(parent, childElement, elementType)
                    }
                }
            }
        }
    }
}

fun <T : AttributeI<*>> T.toHTMLStringForm(parent: EntityI<*>, childElement: String): String {
    return """
        <mat-form-field appearance="outline">
            <mat-label>${childElement}</mat-label>
            <input matInput name="${childElement.toLowerCase()}" [(ngModel)]="${parent.name().toLowerCase()}.${childElement.toCamelCase()}">
        </mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLDateForm(parent: EntityI<*>, childElement: String): String {
    return """
        <mat-form-field appearance="outline">
            <mat-label>${childElement}</mat-label>
            <input matInput [matDatepicker]="picker" [(ngModel)]="${parent.name().toLowerCase()}.${childElement.toCamelCase()}">
            <mat-hint>MM/DD/YYYY</mat-hint>
            <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
            <mat-datepicker #picker></mat-datepicker>
        </mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLEnumForm(parent: EntityI<*>, childElement: String, elementName: String): String {
    return """
        <mat-form-field appearance="outline">
            <mat-label>${childElement}</mat-label>
            <mat-select [(value)]="${parent.name().toLowerCase()}.${childElement.toCamelCase()}">
                <mat-option *ngFor="let item of ${elementName.toLowerCase()}Enum" [value]="item">{{item}}</mat-option>
            </mat-select>
        </mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectForm(parent: EntityI<*>, childElement: String, elementType: String): String {
    return """
        <app-${elementType.toLowerCase()} [${elementType.toLowerCase()}]="${parent.name().toLowerCase()}.${childElement.toCamelCase()}"></app-${elementType.toLowerCase()}>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormEntity(parent: EntityI<*>, childElement: String, elementType: String): String {
    return """
        <app-${elementType.toLowerCase()}-form [${elementType.toLowerCase()}]="${parent.name().toLowerCase()}.${childElement.toCamelCase()}"></app-${elementType.toLowerCase()}-form>"""
}

fun <T : ItemI<*>> T.toAngularEntityListHTML(element: EntityI<*>): String =
    """<app-${element.parent().name().toLowerCase()} [pageName]="${element.name().toLowerCase()}DataService.pageName"></app-${element.parent().name().toLowerCase()}>
<a class="newButton" [routerLink]="'./new'"
        routerLinkActive="active-link">
    <mat-icon>add_circle_outline</mat-icon> Add New Item
</a>

<a class="deleteButton" (click)="${element.name().toLowerCase()}DataService.clearItems()">
    <mat-icon>delete_outline</mat-icon> Delete All Item
</a>
<app-table [displayedColumns]="tableHeader"></app-table>
"""

fun <T : ItemI<*>> T.toAngularBasicHTML(c: GenerationContext, element: AttributeI<*>, basics: List<BasicI<*>>): String {
    var isBasic = false
    basics.forEach {
        if(it.name() == element.type().name()) {
            isBasic = true
        }
    }

    return when (element.type().toTypeScriptIfNative(c, "", element)) {
        "boolean" -> """
        <mat-form-field appearance="outline">
            <mat-label>${element.name()}</mat-label>
            <mat-select [(value)]="${element.parent().name().toLowerCase()}.${element.name().toCamelCase()}">
                <mat-option *ngFor="let item of ['true', 'false']" [value]="item">{{item}}</mat-option>
            </mat-select>
        </mat-form-field>"""
        "string", "number" -> """
        <mat-form-field appearance="outline">
            <mat-label>${element.name()}</mat-label>
            <input matInput name="${element.name().toLowerCase()}" [(ngModel)]="${element.parent().name().toLowerCase()}.${element.name().toCamelCase()}">
        </mat-form-field>"""
        else -> {
            when (element.type().props().size) {
                0 -> """
        <mat-form-field appearance="outline">
            <mat-label>${element.name()}</mat-label>
            <mat-select [(value)]="${element.parent().name().toLowerCase()}.${element.name().toCamelCase()}">
                <mat-option *ngFor="let item of ${element.name().toLowerCase()}Enum" [value]="item">{{item}}</mat-option>
            </mat-select>
        </mat-form-field>"""
                else -> {
                    when (isBasic) {
                        true ->  """
        <app-${element.type().name().toLowerCase()} [${element.type().name().toLowerCase()}]="${element.parent().name().toLowerCase()}.${element.name().toCamelCase()}"></app-${element.type().name().toLowerCase()}>"""
                        false ->  """
        <app-${element.type().name().toLowerCase()}-form [${element.type().name().toLowerCase()}]="${element.parent().name().toLowerCase()}.${element.name().toCamelCase()}"></app-${element.type().name().toLowerCase()}-form>"""
                    }
                }
            }
        }
    }
}

fun <T : ItemI<*>> T.toAngularDefaultSCSS(): String =
    """:host {}"""

fun <T : ItemI<*>> T.toAngularEntityViewSCSS(): String =
    """app-button {
    position: relative;
    left: 10%;
}

"""

fun <T : ItemI<*>> T.toAngularFormSCSS(): String =
    """form {
    position: relative;
    z-index: 1;
    left: 10%;
}
"""

fun <T : ItemI<*>> T.toAngularEntityListSCSS(): String =
    """app-table {
    position: absolute;
    width: 80% !important;
    z-index: 1;
    top: 30%;
    left: 10%;
}

a {
    text-decoration: none;
    border: 0;
    background: white;
    color: black;
    cursor: pointer;
}

.newButton {
    position: absolute;
    top: 20%;
    left: 30%;
}

.deleteButton {
    position: absolute;
    top: 20%;
    left: 50%;
}
"""


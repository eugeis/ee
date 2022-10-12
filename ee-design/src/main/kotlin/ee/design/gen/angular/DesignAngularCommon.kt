import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.lang.*
import ee.lang.gen.ts.toTypeScriptIfNative

fun <T : ItemI<*>> T.toAngularModuleHTML(): String =
    """<mat-sidenav-container>
    <mat-sidenav #drawer
                 [mode]="'side'" [fixedInViewport]="true">
        <mat-nav-list>
            <a *ngFor="let pageName of ${this.name().toLowerCase()}ViewService.pageElement"
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
            <div *ngFor="let pageTabsName of ${this.name().toLowerCase()}ViewService.tabElement">
                <a mat-tab-link
                   [routerLink]="'/${this.name().toLowerCase()}' + '/' + pageTabsName.toLowerCase()"
                   routerLinkActive="active-link"
                >{{pageTabsName.toUpperCase()}}
                </a>
            </div>
        </nav>
    </mat-sidenav-content>
</mat-sidenav-container>"""

fun <T : ItemI<*>> T.toAngularEntityViewHTML(): String =
    """<app-${this.parent().name().toLowerCase()} [pageName]="${this.name().toLowerCase()}DataService.pageName"></app-${this.parent().name().toLowerCase()}>

<app-${this.name().toLowerCase()}-form [${this.name().toLowerCase()}]="${this.name().toLowerCase()}"></app-${this.name().toLowerCase()}-form>

<app-button [element]="${this.name().toLowerCase()}" [isEdit]="${this.name().toLowerCase()}DataService.isEdit" [itemIndex]="${this.name().toLowerCase()}DataService.itemIndex"></app-button>
"""

fun <T : CompilationUnitI<*>> T.toAngularEntityFormHTML(): String =
    """
<div>
    <form class="${this.name().toLowerCase()}-form">
        ${this.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        when(it.type()) {
            is EnumTypeI<*> -> it.toHTMLEnumForm(it.type().name())
            is BasicI<*> -> it.toHTMLObjectForm(it.type().name())
            is EntityI<*> -> it.toHTMLObjectFormEntity(it.type().name())
            else -> when(it.type().name().toLowerCase()) {
                "boolean" -> it.toHTMLBooleanForm()
                "date", "list" -> it.toHTMLDateForm()
                else -> {it.toHTMLStringForm()}
            }
        }
    }}
    </form>
</div>
"""

fun <T : AttributeI<*>> T.toHTMLStringForm(): String {
    return """
        <mat-form-field appearance="outline">
            <mat-label>${this.name()}</mat-label>
            <input matInput name="${this.name().toLowerCase()}" [(ngModel)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
        </mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLBooleanForm(): String {
    return """
        <mat-form-field appearance="outline">
            <mat-label>${this.name()}</mat-label>
            <mat-select [(value)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
                <mat-option *ngFor="let item of ['true', 'false']" [value]="item">{{item}}</mat-option>
            </mat-select>
        </mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLDateForm(): String {
    return """
        <mat-form-field appearance="outline">
            <mat-label>${this.name()}</mat-label>
            <input matInput [matDatepicker]="picker" [(ngModel)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
            <mat-hint>MM/DD/YYYY</mat-hint>
            <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
            <mat-datepicker #picker></mat-datepicker>
        </mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLEnumForm(elementName: String): String {
    return """
        <mat-form-field appearance="outline">
            <mat-label>${this.name()}</mat-label>
            <mat-select [(value)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
                <mat-option *ngFor="let item of ${elementName.toLowerCase()}Enum" [value]="item">{{item}}</mat-option>
            </mat-select>
        </mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectForm(elementType: String): String {
    return """
        <app-${elementType.toLowerCase()} [${elementType.toLowerCase()}]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}"></app-${elementType.toLowerCase()}>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectFormEntity(elementType: String): String {
    return """
        <app-${elementType.toLowerCase()}-form [${elementType.toLowerCase()}]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}"></app-${elementType.toLowerCase()}-form>"""
}

fun <T : ItemI<*>> T.toAngularEntityListHTML(): String =
    """<app-${this.parent().name().toLowerCase()} [pageName]="${this.name().toLowerCase()}DataService.pageName"></app-${this.parent().name().toLowerCase()}>
<a class="newButton" [routerLink]="'./new'"
        routerLinkActive="active-link">
    <mat-icon>add_circle_outline</mat-icon> Add New Item
</a>

<a class="deleteButton" (click)="${this.name().toLowerCase()}DataService.clearItems()">
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

fun <T : ItemI<*>> T.toAngularEntityFormSCSS(): String =
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


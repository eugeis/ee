import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.lang.*
import ee.lang.gen.ts.toTypeScriptIfNative

fun <T : ItemI<*>> T.toAngularModuleHTML(): String =
    """<mat-sidenav-container>
    <mat-sidenav opened="true" disableClose="true" position="end" #drawer
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
            <span>{{pageName}}</span>

            <span class="toolbar-space"></span>

            <button mat-icon-button (click)="drawer.toggle()">
                <mat-icon>menu</mat-icon>
            </button>
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
        <fieldset>
            <legend>${this.name().capitalize()}</legend>
            ${this.props().filter { it.type() !is BasicI<*> && it.type() !is EntityI<*> }.joinSurroundIfNotEmptyToString(nL) {
        when(it.type().name().toLowerCase()) {
            "boolean" -> it.toHTMLBooleanForm(tab)
            "date", "list" -> it.toHTMLDateForm(tab)
            "string", "text" -> it.toHTMLStringForm(tab)
            "blob" -> it.toHTMLUploadForm(tab)
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
            is EntityI<*> -> it.toHTMLObjectFormEntity(it.type().name())
            else -> ""
        }
    }}
    </form>
</div>
"""

fun <T : CompilationUnitI<*>> T.toAngularBasicHTML(): String =
    """
<fieldset>
    <legend>{{parentName}} ${this.name().capitalize()}</legend>
        ${this.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        when(it.type()) {
            is EnumTypeI<*> -> it.toHTMLEnumForm("", it.type().name())
            is BasicI<*> -> it.toHTMLObjectForm(it.type().name())
            is EntityI<*> -> it.toHTMLObjectFormEntity(it.type().name())
            else -> when(it.type().name().toLowerCase()) {
                "boolean" -> it.toHTMLBooleanForm("")
                "date", "list" -> it.toHTMLDateForm("")
                else -> {it.toHTMLStringForm("")}
            }
        }
    }}
</fieldset>
"""

fun <T : AttributeI<*>> T.toHTMLStringForm(indent: String): String {
    return """
        ${indent}<mat-form-field appearance="outline">
            ${indent}<mat-label>${this.name()}</mat-label>
            ${indent}<input matInput name="${this.name().toLowerCase()}" [(ngModel)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
        ${indent}</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLUploadForm(indent: String): String {
    return """
        ${indent}<mat-form-field appearance="outline">
            ${indent}<mat-label>${this.name()}</mat-label>
            ${indent}<input matInput name="${this.name().toLowerCase()}" type="file" (change)="${this.parent().name().toLowerCase()}DataService.selectFiles(${"$"}event)" [(ngModel)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
        ${indent}</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLBooleanForm(indent: String): String {
    return """
        ${indent}<mat-form-field appearance="outline">
            ${indent}<mat-label>${this.name()}</mat-label>
            ${indent}<mat-select [(value)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
                ${indent}<mat-option *ngFor="let item of ['true', 'false']" [value]="item">{{item}}</mat-option>
            ${indent}</mat-select>
        ${indent}</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLDateForm(indent: String): String {
    return """
        ${indent}<mat-form-field appearance="outline">
            ${indent}<mat-label>${this.name()}</mat-label>
            ${indent}<input matInput [matDatepicker]="picker" [(ngModel)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
            ${indent}<mat-hint>MM/DD/YYYY</mat-hint>
            ${indent}<mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
            ${indent}<mat-datepicker #picker></mat-datepicker>
        ${indent}</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLEnumForm(indent: String, elementName: String): String {
    return """
        ${indent}<mat-form-field appearance="outline">
            ${indent}<mat-label>${this.name()}</mat-label>
            ${indent}<mat-select [(value)]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}">
                ${indent}<mat-option *ngFor="let item of ${elementName.toLowerCase()}Enum" [value]="item">{{item}}</mat-option>
            ${indent}</mat-select>
        ${indent}</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectForm(elementType: String): String {
    return """
        <app-${elementType.toLowerCase()} [parentName]="'${this.parent().name().capitalize()}'" [${elementType.toLowerCase()}]="${this.parent().name().toLowerCase()}.${this.name().toCamelCase()}"></app-${elementType.toLowerCase()}>"""
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

<ng-container *ngIf="${this.name().toLowerCase()}DataService.isHidden; else showed">
    <a class="showButton" (click)="${this.name().toLowerCase()}DataService.toggleHidden()">
        <mat-icon>delete_outline</mat-icon> Delete Multiple Items
    </a>
</ng-container>

<ng-template #showed>
    <a class="deleteButton" (click)="${this.name().toLowerCase()}DataService.clearMultipleItems(${this.name().toLowerCase()}DataService.selection.selected); ${this.name().toLowerCase()}DataService.toggleHidden()">
        <mat-icon>delete_outline</mat-icon> Delete Items
    </a>
</ng-template>

<app-table [selection]="${this.name().toLowerCase()}DataService.selection" [isHidden]="${this.name().toLowerCase()}DataService.isHidden" [displayedColumns]="tableHeader"></app-table>
"""

fun <T : ItemI<*>> T.toAngularModuleSCSS(): String =
    """.toolbar-space {
    flex: 1 1 auto;
}

mat-sidenav-container {
    position: relative;
    width: 100%;
    z-index: 2;
}
"""

fun <T : ItemI<*>> T.toAngularEntityViewSCSS(): String =
    """app-button {
    position: relative;
    left: 10%;
}

"""

fun <T : ItemI<*>> T.toAngularEntityFormSCSS(): String =
    """form {
    position: relative;
    max-width: 80%;
    z-index: 1;
    left: 10%;
}

fieldset {
    width: 80%;
    padding: 20px;
    border: round(30) 1px;

    .mat-form-field {
        padding: 10px 0;
    }
}

@media screen and (max-width: 650px) {
    form {
        left: 5%;
    }
}

@media screen and (max-width: 480px) {
    form {
        left: 5%;
        max-width: 50%;
    }
}
"""

fun <T : ItemI<*>> T.toAngularBasicSCSS(): String =
    """fieldset {
    width: 80%;
    padding: 20px;
    border: round(30) 1px;

    .mat-form-field {
        padding: 10px 0;
    }
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

.deleteButton, .showButton {
    position: absolute;
    top: 20%;
    left: 50%;
}

@media screen and (max-width: 1000px) {
    app-table {
        max-width: 70%;
    }
}

@media screen and (max-width: 585px) {
    app-table {
        max-width: 60%;
    }

    .newButton {
        position: absolute;
        top: 20%;
        left: 10%;
    }

    .deleteButton, .showButton {
        position: absolute;
        top: 20%;
        left: 40%;
    }
}
"""


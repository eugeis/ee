import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*

fun <T : ItemI<*>> T.toAngularConstructorDataService(indent: String, element: EntityI<*>): String {
    return """${indent}constructor(public ${element.name().toLowerCase()}DataService: ${element.name()}DataService) {}$nL"""
}

fun <T : ItemI<*>> T.toAngularViewOnInit(c: GenerationContext, indent: String, element: EntityI<*>, basics: List<BasicI<*>>): String {
    val basicElements: MutableList<String> = ArrayList()
    basics.forEach { basicElements.add(it.name().capitalize()) }

    return """${indent}ngOnInit(): void {
        this.${element.name().toLowerCase()} = this.${element.name().toLowerCase()}DataService.getFirst();
        this.${element.name().toLowerCase()}DataService.checkRoute(this.${element.name().toLowerCase()});
    }"""
}

fun <T : ItemI<*>> T.toAngularFormOnInit(c: GenerationContext, indent: String, element: EntityI<*>, basics: List<BasicI<*>>, entities: List<EntityI<*>>): String {
    val includedElements: MutableList<String> = ArrayList()
    basics.forEach { includedElements.add(it.name().capitalize()) }
    entities.forEach { includedElements.add(it.name().capitalize()) }

    return """${indent}ngOnInit(): void {
        ${element.props().filter { it.type().name() in includedElements }.joinSurroundIfNotEmptyToString(nL + tab) {
        it.toAngularEmptyProps(c, indent, it, element, it.type())
    }.trim()}
    }"""
}

fun <T : ItemI<*>> T.toAngularEmptyProps(c: GenerationContext, indent: String, element: AttributeI<*>, entity: EntityI<*>, elementType: TypeI<*>): String {
    return """${indent}if (this.${entity.name().toLowerCase()}.${element.name().toCamelCase()} === undefined) {
            this.${entity.name().toLowerCase()}.${element.name().toCamelCase()} = new ${c.n(elementType).capitalize()}();
        }"""
}

fun <T : TypeI<*>> T.toAngularModuleArrayElement(attr: EntityI<*>): String =
    "'${attr.name()}'"

fun <T : ItemI<*>> T.toTypeScriptModuleGenerateComponentPart(element: ModuleI<*>): String =
    """@Component({
  selector: 'app-${element.name().toLowerCase()}',
  templateUrl: './${element.name().toLowerCase()}-module-view.component.html',
  styleUrls: ['./${element.name().toLowerCase()}-module-view.component.scss'],
  providers: [${element.name()}ViewService]
})
"""

fun <T : ItemI<*>> T.toTypeScriptEntityGenerateViewComponentPart(c: GenerationContext, element: EntityI<*>, type: String): String =
    """@Component({
  selector: 'app-${c.n(element).toLowerCase()}-${type}',
  templateUrl: './${c.n(element).toLowerCase()}-entity-${type}.component.html',
  styleUrls: ['./${c.n(element).toLowerCase()}-entity-${type}.component.scss'],
  providers: [{provide: TableDataService, useClass: ${c.n(element).capitalize()}DataService}]
})
"""

fun <T : ItemI<*>> T.toTypeScriptEntityGenerateFormComponentPart(c: GenerationContext, element: EntityI<*>, type: String): String =
    """@Component({
  selector: 'app-${c.n(element).toLowerCase()}-form',
  templateUrl: './${c.n(element).toLowerCase()}-form.component.html',
  styleUrls: ['./${c.n(element).toLowerCase()}-form.component.scss']
})
"""

fun <T : ItemI<*>> T.toAngularGenerateEnumElement(c: GenerationContext, indent: String, element: EntityI<*>, elementName: String, elementType: String, enums: List<EnumTypeI<*>>): String {
    var text = ""
    enums.forEach {
        if(it.name() == elementType) {
            text = """${indent}${elementType.toLowerCase()}Enum = this.${element.name().toLowerCase()}DataService.loadEnumElement(${c.n(it)});"""
        }
    }
    return text
}

fun <T : ItemI<*>> T.toTypeScriptEntityProp(c: GenerationContext, indent: String, element: EntityI<*>): String {
    return """${indent}${element.name().toLowerCase()}: ${c.n(element)};$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptFormProp(c: GenerationContext, indent: String, element: EntityI<*>): String {
    return """${indent}@Input() ${element.name().toLowerCase()}: ${c.n(element)};$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptViewEntityPropInit(c: GenerationContext, indent: String, element: EntityI<*>): String {
    return """${indent}${element.name().toLowerCase()}: ${c.n(element)} = new ${c.n(element)}();$nL"""
}


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


fun <T : ItemI<*>> T.toAngularModuleImportEntities(element: EntityI<*>): String {
    return """import {${element.name().capitalize()}ViewComponent} from '@schkola/${element.parent().name().toLowerCase()}/${element.name().toLowerCase()}/components/view/${element.name().toLowerCase()}-entity-view.component';
import {${element.name().capitalize()}ListComponent} from '@schkola/${element.parent().name().toLowerCase()}/${element.name().toLowerCase()}/components/list/${element.name().toLowerCase()}-entity-list.component';
import {${element.name().capitalize()}FormComponent} from '@schkola/${element.parent().name().toLowerCase()}/${element.name().toLowerCase()}/components/form/${element.name().toLowerCase()}-form.component';"""
}

fun <T : ItemI<*>> T.toAngularModuleImportBasics(element: BasicI<*>): String {
    return """import {${element.name().capitalize()}Component} from '@schkola/${element.parent().name().toLowerCase()}/basics/${element.name().toLowerCase()}/${element.name().toLowerCase()}-basic.component';"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationEntities(indent: String, element: EntityI<*>): String {
    return """$indent${element.name().capitalize()}ViewComponent,
$indent${element.name().capitalize()}ListComponent,
$indent${element.name().capitalize()}FormComponent"""
}

fun <T : ItemI<*>> T.toAngularModuleExportViews(indent: String, element: EntityI<*>): String {
    return """$indent${element.name().capitalize()}FormComponent"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationBasics(indent: String, element: BasicI<*>): String {
    return """$indent${element.name().capitalize()}Component"""
}

fun <T : ItemI<*>> T.toAngularModulePath(indent: String, element: EntityI<*>): String {
    return """$indent{ path: '${element.name().toLowerCase()}', component: ${element.name().capitalize()}ListComponent },
$indent{ path: '${element.name().toLowerCase()}/new', component: ${element.name().capitalize()}ViewComponent },
$indent{ path: '${element.name().toLowerCase()}/edit/:id', component: ${element.name().capitalize()}ViewComponent }"""
}


fun <T : ItemI<*>> T.toTypeScriptModuleImportServices(element: ModuleI<*>): String {
    return """import {${element.name()}ViewService} from '../../service/${element.name().toLowerCase()}-module-view.service';$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptModuleInputElement(name: String, indent: String, element: ModuleI<*>): String {
    return """${indent}@Input() $name = '${element.name()}Component';$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptModuleConstructor(indent: String, element: ModuleI<*>): String {
    return """${indent}constructor(public ${element.name().toLowerCase()}ViewService: ${element.name()}ViewService) {}$nL"""
}

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
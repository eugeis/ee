package ee.lang.gen.ts

import ee.common.ext.*
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.java.j
import org.w3c.dom.Attr

var tempIndex = 0
fun <T : TypeI<*>> T.toTypeScriptDefault(c: GenerationContext, derived: String, attr: AttributeI<*>): String {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String, n.Text -> "''"
        n.Boolean        -> "false"
        n.Int            -> "0"
        n.Long           -> "0L"
        n.Float          -> "0f"
        n.Date           -> "${c.n(j.util.Date)}()"
        n.Path           -> "${c.n(j.nio.file.Paths)}.get('')"
        n.Blob           -> "new ByteArray(0)"
        n.Void           -> ""
        n.Error          -> "new Throwable()"
        n.Exception      -> "new Exception()"
        n.Url            -> "${c.n(j.net.URL)}('')"
        n.Map            -> (attr.isNotEMPTY() && attr.isMutable().setAndTrue()).ifElse("new Map()", "new Map()")
        n.List           -> (attr.isNotEMPTY() && attr.isMutable().setAndTrue()).ifElse("new Array()", "new Array()")
        else             -> {
            if (baseType is LiteralI<*>) {
                "${(baseType.findParent(EnumTypeI::class.java) as EnumTypeI<*>).toTypeScript(c, derived,
                    attr)}.${baseType.toTypeScript()}"
            } else if (baseType is EnumTypeI<*>) {
                "${c.n(this, derived)}.${baseType.literals().first().toTypeScript()}"
            } else if (baseType is CompilationUnitI<*>) {
                "new ${c.n(this, derived)}()"
            } else {
                (this.parent() == n).ifElse("''", { "${c.n(this, derived)}.EMPTY" })
            }
        }
    }
}

fun <T : AttributeI<*>> T.toTypeScriptDefault(c: GenerationContext, derived: String): String =
    type().toTypeScriptDefault(c, derived, this)


fun <T : ItemI<*>> T.toTypeScriptEMPTY(c: GenerationContext, derived: String): String =
    (this.parent() == n).ifElse("''", { "${c.n(this, derived)}.EMPTY" })


fun <T : AttributeI<*>> T.toTypeScriptEMPTY(c: GenerationContext, derived: String): String =
    type().toTypeScriptEMPTY(c, derived)

fun <T : AttributeI<*>> T.toTypeScriptTypeSingle(c: GenerationContext, api: String): String =
    type().toTypeScript(c, api, this)

fun <T : AttributeI<*>> T.toTypeScriptTypeDef(c: GenerationContext, api: String): String =
    """${type().toTypeScript(c, api, this)}${isNullable().then("?")}"""


fun <T : AttributeI<*>> T.toTypeScriptCompanionObjectName(c: GenerationContext): String =
    """        val ${name().toUnderscoredUpperCase()} = "_${name()}""""

fun <T : CompilationUnitI<*>> T.toTypeScriptExtends(c: GenerationContext, derived: String, api: String): String {
    if (superUnit().isNotEMPTY() && derived != api) {
        return " extends ${c.n(superUnit(), derived)}, ${c.n(this, api)}"
    } else if (superUnit().isNotEMPTY()) {
        return " extends ${c.n(superUnit(), derived)}"
    } else if (derived != api) {
        return " extends ${c.n(this, api)}"
    } else {
        return ""
    }
}

fun <T : TypeI<*>> T.toTypeScriptIfNative(c: GenerationContext, derived: String, attr: AttributeI<*>): String? {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.Any       -> "any"
        n.String    -> "string"
        n.Boolean   -> "boolean"
        n.Int       -> "number"
        n.Long      -> "number"
        n.Float     -> "number"
        n.Date      -> "Date"
        n.TimeUnit  -> "string"
        n.Path      -> "string"
        n.Text      -> "string"
        n.Blob      -> "Blob"
        n.Exception -> "Error"
        n.Error     -> "Error"
        n.Void      -> "void"
        n.Url       -> "string"
        n.UUID      -> "string"
        n.List      -> "Array${toTypeScriptGenericTypes(c, derived, attr)}"
        n.Map       -> "Map${toTypeScriptGenericTypes(c, derived, attr)}"
        else        -> {
            if (this is LambdaI<*>) operation().toTypeScriptLambda(c, derived) else null
        }
    }
}

fun TypeI<*>.toTypeScriptGenericTypes(c: GenerationContext, derived: String, attr: AttributeI<*>): String =
    generics().joinWrappedToString(", ", "", "<", ">") { it.type().toTypeScript(c, derived, attr) }

fun GenericI<*>.toTypeScript(c: GenerationContext, derived: String): String = c.n(type(), derived)

fun TypeI<*>.toTypeScriptGenerics(c: GenerationContext, derived: String, attr: AttributeI<*>): String =
    generics().joinWrappedToString(", ", "", "<", ">") { it.toTypeScript(c, derived, attr) }

fun TypeI<*>.toTypeScriptGenericsClassDef(c: GenerationContext, derived: String, attr: AttributeI<*>): String =
    generics().joinWrappedToString(", ", "", "<", ">") {
        "${it.name()} : ${it.type().toTypeScript(c, derived, attr)}"
    }

fun TypeI<*>.toTypeScriptGenericsMethodDef(c: GenerationContext, derived: String, attr: AttributeI<*>): String =
    generics().joinWrappedToString(", ", "", "<", "> ") {
        "${it.name()} : ${it.type().toTypeScript(c, derived, attr)}"
    }

fun TypeI<*>.toTypeScriptGenericsStar(context: GenerationContext, derived: String): String =
    generics().joinWrappedToString(", ", "", "<", "> ") { "*" }

fun OperationI<*>.toTypeScriptGenerics(c: GenerationContext, derived: String): String =
    generics().joinWrappedToString(", ", "", "<", "> ") {
        "${it.name()} : ${it.type().toTypeScript(c, derived)}"
    }

fun <T : TypeI<*>> T.toTypeScript(c: GenerationContext, derived: String,
                                  attr: AttributeI<*> = Attribute.EMPTY): String =
    toTypeScriptIfNative(c, derived, attr) ?: "${c.n(this, derived)}${this.toTypeScriptGenericTypes(c, derived, attr)}"

fun <T : AttributeI<*>> T.toTypeScriptValue(c: GenerationContext, derived: String): String {
    if (value() != null) {
        return when (type()) {
            n.String, n.Text                                                  -> "\"${value()}\""
            n.Boolean, n.Int, n.Long, n.Float, n.Date, n.Path, n.Blob, n.Void -> "${value()}"
            else                                                              -> {
                if (value() is LiteralI<*>) {
                    val lit = value() as LiteralI<*>
                    "${(lit.parent() as EnumTypeI<*>).toTypeScript(c, derived, this)}.${lit.toTypeScript()}"
                } else {
                    "${value()}"
                }
            }
        }
    } else {
        return toTypeScriptDefault(c, derived)
    }
}

fun <T : AttributeI<*>> T.toTypeScriptInit(c: GenerationContext, derived: String): String {
    if (value() != null) {
        return " = ${toTypeScriptValue(c, derived)}"
    } else if (isNullable()) {
        return " = null"
    } else if (isInitByDefaultTypeValue()) {
        return " = ${toTypeScriptValue(c, derived)}"
    } else {
        return ""
    }
}

fun <T : AttributeI<*>> T.toTypeScriptInitMember(c: GenerationContext, derived: String): String =
    "this.${name()}${toTypeScriptInit(c, derived)}"

fun <T : AttributeI<*>> T.toTypeScriptSignature(c: GenerationContext, derived: String, api: String,
                                                init: Boolean = true): String =
    "${name()}: ${toTypeScriptTypeDef(c, api)}${init.then { toTypeScriptInit(c, derived) }}"

fun <T : AttributeI<*>> T.toTypeScriptConstructorMember(c: GenerationContext, derived: String, api: String,
                                                        init: Boolean = true): String =
    //"${isReplaceable().setAndTrue().ifElse("", "readonly ")}${toTypeScriptSignature(c, derived, api, init)}"
    "${toTypeScriptSignature(c, derived, api, init)}"

fun <T : AttributeI<*>> T.toTypeScriptMember(c: GenerationContext, derived: String, api: String,
                                             init: Boolean = true, indent: String): String =
    //"    ${isReplaceable().setAndTrue().ifElse("", "readonly ")}${toTypeScriptSignature(c, derived, api, init)}"
    "${indent}${toTypeScriptSignature(c, derived, api, init)}"

fun List<AttributeI<*>>.toTypeScriptSignature(c: GenerationContext, derived: String, api: String): String =
    joinWrappedToString(", ") { it.toTypeScriptSignature(c, derived, api) }

fun List<AttributeI<*>>.toTypeScriptMember(c: GenerationContext, derived: String, api: String): String =
    joinWrappedToString(", ") { it.toTypeScriptSignature(c, derived, api) }

fun <T : ConstructorI<*>> T.toTypeScriptPrimary(c: GenerationContext, derived: String, api: String): String {
    return if (isNotEMPTY()) """(${params().joinWrappedToString(", ", "      ") {
        it.toTypeScriptConstructorMember(c, derived, api)
    }})${superUnit().toTypeScriptCall(c)}""" else ""
}

fun <T : ConstructorI<*>> T.toTypeScript(c: GenerationContext, derived: String, api: String): String {
    return if (isNotEMPTY()) """
    constructor(${params().joinWrappedToString(", ", "                ") {
        it.toTypeScriptSignature(c, derived, api)
    }})${superUnit().isNotEMPTY().then {
        (superUnit() as ConstructorI<*>).toTypeScriptCall(c, (parent() != superUnit().parent()).ifElse("super", "this"))
    }} ${paramsWithOut(superUnit()).joinSurroundIfNotEmptyToString("${nL}        ", prefix = "{${nL}        ") {
        it.toTypeScriptAssign(c)
    }}${(parent() as CompilationUnitI<*>).props().filter { it.isMeta() }.joinSurroundIfNotEmptyToString("${nL}        ",
        prefix = "${nL}        ") {
        it.toTypeScriptInitMember(c, derived)
    }}
    }""" else ""
}

fun <T : ConstructorI<*>> T.toTypeScriptCall(c: GenerationContext, name: String = "this"): String =
    isNotEMPTY().then { " : $name(${params().joinWrappedToString(", ") { it.name() }})" }

fun <T : AttributeI<*>> T.toTypeScriptAssign(c: GenerationContext): String = "this.${name()} = ${name()}"

fun <T : LogicUnitI<*>> T.toTypeScriptCall(c: GenerationContext): String =
    isNotEMPTY().then { "(${params().joinWrappedToString(", ") { it.name() }})" }

fun <T : LogicUnitI<*>> T.toTypeScriptCallValue(c: GenerationContext, derived: String): String =
    isNotEMPTY().then { "(${params().joinWrappedToString(", ") { it.toTypeScriptValue(c, derived) }})" }

fun <T : LiteralI<*>> T.toTypeScriptCallValue(c: GenerationContext, derived: String): String =
    params().isNotEmpty().then { "(${params().joinWrappedToString(", ") { it.toTypeScriptValue(c, derived) }})" }

fun <T : AttributeI<*>> T.toTypeScriptType(c: GenerationContext, derived: String): String =
    type().toTypeScript(c, derived, this)

fun List<AttributeI<*>>.toTypeScriptTypes(c: GenerationContext, derived: String): String =
    joinWrappedToString(", ") { it.toTypeScriptType(c, derived) }

fun <T : OperationI<*>> T.toTypeScriptLambda(c: GenerationContext, derived: String): String =
    """(${params().toTypeScriptTypes(c, derived)}) -> ${retFirst().toTypeScriptType(c, derived)}"""

fun <T : OperationI<*>> T.toTypeScriptImpl(c: GenerationContext, derived: String, api: String): String {
    return """
    ${toTypeScriptGenerics(c, derived)}${name()}(${params().toTypeScriptSignature(c, derived,
        api)}): ${retFirst().toTypeScriptTypeDef(c, api)} {
        throw new ReferenceError('Not implemented yet.');
    }"""
}

fun <T : AttributeI<*>> T.toTypeScriptImportElements(element: AttributeI<*>): String {
    val elementTypeName = element.type().name()
    val elementParentName = element.parent().namespace()
    val elementParentNameRegex = elementParentName.substring(elementParentName.lastIndexOf(".") + 1)
    return """import {${elementTypeName.capitalize()}} from '../../schkola/${elementParentNameRegex.toLowerCase()}/${elementParentNameRegex.capitalize() + "ApiBase"}';"""
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

fun <T : ItemI<*>> T.toAngularConstructorDataService(indent: String, element: EntityI<*>): String {
    return """${indent}constructor(@Inject(${element.name()}DataService) public ${element.name().toLowerCase()}DataService: ${element.name()}DataService) {}$nL"""
}

fun <T : ItemI<*>> T.toAngularViewOnInit(c: GenerationContext,indent: String, element: EntityI<*>, basics: List<BasicI<*>>): String {
    val basicElements: MutableList<String> = ArrayList()
    basics.forEach { basicElements.add(it.name().capitalize()) }
    print("ELEMENT: " + element.name())
    element.props().forEach { print("TYPE: " + it.type().name()) }

    return """${indent}ngOnInit(): void {
        this.${element.name().toLowerCase()} = this.${element.name().toLowerCase()}DataService.getFirst();
        ${element.props().filter { it.type().name() in basicElements }.joinSurroundIfNotEmptyToString(nL + tab) { 
            it.toAngularEmptyBasic(c, indent, it, element, it.type())
    }.trim()}
        this.${element.name().toLowerCase()}DataService.checkRoute(this.${element.name().toLowerCase()});
    }"""
}

fun <T : ItemI<*>> T.toAngularEmptyBasic(c: GenerationContext, indent: String, element: AttributeI<*>, entity: EntityI<*>, elementType: TypeI<*>): String {
    return """${indent}this.${entity.name().toLowerCase()}.${element.name().toCamelCase()} = new ${c.n(elementType).capitalize()}();"""
}

fun <T : ItemI<*>> T.toAngularListOnInit(indent: String): String {
    return """${indent}ngOnInit(): void {
        this.tableHeader = this.generateTableHeader();
    }"""
}

fun <T : TypeI<*>> T.toAngularModuleArrayElement(attr: EntityI<*>): String =
    "'${attr.name()}'"

fun <T : ItemI<*>> T.toTypeScriptBasicGenerateComponentPart(element: BasicI<*>): String =
    """@Component({
  selector: 'app-${element.name().toLowerCase()}',
  templateUrl: './${element.name().toLowerCase()}-basic.component.html',
  styleUrls: ['./${element.name().toLowerCase()}-basic.component.scss'],
})
"""

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

fun <T : ItemI<*>> T.toAngularGenerateEnumElement(c: GenerationContext, indent: String, element: EntityI<*>, elementName: String, elementType: String,  enums: List<EnumTypeI<*>>): String {
    var text = ""
    enums.forEach {
        if(it.name() == elementType) {
            text = """${indent}${elementType.toLowerCase()}Enum = this.${element.name().toLowerCase()}DataService.loadEnumElement(${c.n(it)});"""
        }
    }
    return text
}

fun <T : ItemI<*>> T.toTypeScriptViewEntityProp(c: GenerationContext,indent: String, element: EntityI<*>): String {
    return """${indent}${element.name().toLowerCase()}: ${c.n(element)};$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptViewEntityPropInit(c: GenerationContext,indent: String, element: EntityI<*>): String {
    return """${indent}${element.name().toLowerCase()}: ${c.n(element)} = new ${c.n(element)}();$nL"""
}

fun <T : ItemI<*>> T.toAngularGenerateTableHeader(element: AttributeI<*>): String {
    return "'${element.name().toLowerCase()}'"
}

fun <T : ItemI<*>> T.toAngularInitEmptyElements(c: GenerationContext, element: AttributeI<*>): String {
    return when (element.type().toTypeScriptIfNative(c, "", element)) {
        "boolean" -> """${element.name().toLowerCase()}: false"""
        "string" -> """${element.name().toLowerCase()}: ''"""
        "number" -> """${element.name().toLowerCase()}: 0"""
        else -> {
            when (element.type().props().size) {
                0 -> """${element.name().toLowerCase()}: 0"""
                else -> {
                    """${element.name()}: {${element.type().props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(", ") {
                        it.toAngularInitEmptyElements(c, it)
                    }}}"""
                }
            }
        }
    }
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

fun <T : ItemI<*>> T.toAngularEntityViewHTML(c: GenerationContext, element: EntityI<*>, enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>): String =
    """<app-${element.parent().name().toLowerCase()} [pageName]="${element.name().toLowerCase()}DataService.pageName"></app-${element.parent().name().toLowerCase()}>
<div>
    <form class="${element.name().toLowerCase()}-form">
        ${element.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
            it.toTypeScriptHTMLForms(tab, element, it.name(), it.type().name(), enums, basics)
        }}
    </form>
</div>

<app-button [element]="${element.name().toLowerCase()}" [isEdit]="${element.name().toLowerCase()}DataService.isEdit" [itemIndex]="${element.name().toLowerCase()}DataService.itemIndex"></app-button>
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
        <app-${elementType.toLowerCase()}-view></app-${elementType.toLowerCase()}-view>"""
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

fun <T : ItemI<*>> T.toAngularDefaultSCSS(): String =
    """:host {}"""

fun <T : ItemI<*>> T.toAngularEntityViewSCSS(): String =
    """app-button {
    position: relative;
    left: 10%;
}

form {
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
        <app-${element.type().name().toLowerCase()}-view></app-${element.type().name().toLowerCase()}-view>"""
                    }
                }
            }
        }
    }
}

fun <T : ItemI<*>> T.toAngularModuleImportEntities(element: EntityI<*>): String {
    return """import {${element.name().capitalize()}ViewComponent} from './${element.name().toLowerCase()}/components/view/${element.name().toLowerCase()}-entity-view.component';
import {${element.name().capitalize()}ListComponent} from './${element.name().toLowerCase()}/components/list/${element.name().toLowerCase()}-entity-list.component';"""
}

fun <T : ItemI<*>> T.toAngularModuleImportBasics(element: BasicI<*>): String {
    return """import {${element.name().capitalize()}Component} from './basics/${element.name().toLowerCase()}/${element.name().toLowerCase()}-basic.component';"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationEntities(indent: String, element: EntityI<*>): String {
    return """$indent${element.name().capitalize()}ViewComponent,
$indent${element.name().capitalize()}ListComponent"""
}

fun <T : ItemI<*>> T.toAngularModuleExportViews(indent: String, element: EntityI<*>): String {
    return """$indent${element.name().capitalize()}ViewComponent"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationBasics(indent: String, element: BasicI<*>): String {
    return """$indent${element.name().capitalize()}Component"""
}

fun <T : ItemI<*>> T.toAngularModulePath(indent: String, element: EntityI<*>): String {
    return """$indent{ path: '${element.name().toLowerCase()}', component: ${element.name().capitalize()}ListComponent },
$indent{ path: '${element.name().toLowerCase()}/new', component: ${element.name().capitalize()}ViewComponent },
$indent{ path: '${element.name().toLowerCase()}/edit/:id', component: ${element.name().capitalize()}ViewComponent }"""
}

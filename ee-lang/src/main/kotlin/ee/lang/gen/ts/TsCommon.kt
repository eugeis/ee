package ee.lang.gen.ts

import ee.common.ext.*
import ee.design.EntityI
import ee.design.ModuleI
import ee.design.gen.go.toGoPropOptionalAfterBody
import ee.lang.*
import ee.lang.gen.java.j

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
    return """import {${element.name()}ViewService} from '../../services/${element.name().toLowerCase()}-view.service}';${"\n"}"""
}

fun <T : ItemI<*>> T.toTypeScriptModuleInputElement(name: String, indent: String, element: ModuleI<*>): String {
    return """${indent}@Input() $name = '${element.name()}Component';${"\n"}"""
}

fun <T : ItemI<*>> T.toTypeScriptModuleInitEntity(c: GenerationContext,indent: String, element: ModuleI<*>): String {
    return """${indent}${element.name().toLowerCase()}: ${c.n(this)} = new ${c.n(this)}();${"\n"}"""
}

fun <T : ItemI<*>> T.toTypeScriptModuleConstructor(indent: String, element: ModuleI<*>): String {
    return """${indent}constructor(public ${element.name().toLowerCase()}ViewService: ${element.name()}ViewService) {}${"\n"}"""
}

fun <T : ItemI<*>> T.toTypeScriptOnInit(indent: String, element: ModuleI<*>): String {
    return """${indent}ngOnInit(): void {
        this.${element.name().toLowerCase()} = this.${element.name().toLowerCase()}DataService.getFirst();
        this.${element.name().toLowerCase()}DataService.checkRoute(this.${element.name().toLowerCase()});
    }"""
}

fun <T : TypeI<*>> T.toTypeScriptModuleArrayElement(attr: EntityI<*>): String =
    "'${attr.name()}'"

fun <T : ItemI<*>> T.toTypeScriptGenerateComponentPartWithProviders(element: ModuleI<*>): String =
    """@Component({
  selector: 'app-${element.name().toLowerCase()}',
  templateUrl: './${element.name().toLowerCase()}-view.component.html',
  styleUrls: ['./${element.name().toLowerCase()}-view.component.scss'],
  providers: [${element.name()}ViewService]
})
"""

fun <T : ItemI<*>> T.toTypeScriptModuleHTML(element: ModuleI<*>): String =
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
                   [routerLink]="'/' + ${element.name().toLowerCase()}ViewService.pageElement[0].toLowerCase() + '/' + pageTabsName.toLowerCase()"
                   routerLinkActive="active-link"
                >{{pageTabsName.toUpperCase()}}
                </a>
            </div>
        </nav>
    </mat-sidenav-content>
</mat-sidenav-container>"""

fun <T : ItemI<*>> T.toTypeScriptEntityViewHTML(c: GenerationContext, element: EntityI<*>, enums: List<EnumTypeI<*>>): String =
    """<app-${element.parent().name().toLowerCase()} [pageName]="${element.name().toLowerCase()}DataService.pageName"></app-${element.parent().name().toLowerCase()}>
<div>
    <form class="${element.name().toLowerCase()}-form">
        ${element.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
            it.toTypeScriptHTMLForms(tab, element, it.name(), it.type().name(), enums)
        }}
    </form>
</div>

<app-button [element]="${element.name().toLowerCase()}" [isEdit]="${element.name().toLowerCase()}DataService.isEdit" [itemIndex]="${element.name().toLowerCase()}DataService.itemIndex"></app-button>
"""

fun <T : AttributeI<*>> T.toTypeScriptHTMLForms(indent: String, parent: EntityI<*>, childElement: String, elementType: String, enums: List<EnumTypeI<*>>): String {
    var isEnum = false
    enums.forEach {
        if(it.name() == elementType) {
            isEnum = true;
        }
    }
    return when (elementType.toLowerCase()) {
        "string", "uuid", "text", "float", "int" -> this.toHTMLStringForm(parent, childElement)
        "date" -> this.toHTMLDateForm(parent, childElement)
        else -> {
            when (isEnum) {
                true -> this.toHTMLEnumForm(parent, childElement)
                else -> this.toHTMLObjectForm(parent, childElement, elementType)
            }
        }
    }
}

fun <T : AttributeI<*>> T.toHTMLStringForm(parent: EntityI<*>, childElement: String): String {
    return """
        <mat-form-field appearance="outline">
            <mat-label>${childElement}</mat-label>
            <input matInput name="${childElement.toLowerCase()}" [(ngModel)]="${parent.name().toLowerCase()}.${childElement.toLowerCase()}">
        </mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLDateForm(parent: EntityI<*>, childElement: String): String {
    return """
        <mat-form-field appearance="outline">
            <mat-label>${childElement}</mat-label>
            <input matInput [matDatepicker]="picker" [(ngModel)]="${parent.name().toLowerCase()}.${childElement.toLowerCase()}">
            <mat-hint>MM/DD/YYYY</mat-hint>
            <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
            <mat-datepicker #picker></mat-datepicker>
        </mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLEnumForm(parent: EntityI<*>, childElement: String): String {
    return """
        <mat-form-field appearance="outline">
            <mat-label>${childElement}</mat-label>
            <mat-select [(value)]="${parent.name().toLowerCase()}.${childElement.toLowerCase()}">
                <mat-option *ngFor="let item of ${childElement.toLowerCase()}Enum" [value]="item">{{item}}</mat-option>
            </mat-select>
        </mat-form-field>"""
}

fun <T : AttributeI<*>> T.toHTMLObjectForm(parent: EntityI<*>, childElement: String, elementType: String): String {
    return """
        <app-${elementType.toLowerCase()} [${elementType.toLowerCase()}]="${parent.name().toLowerCase()}.${childElement.toLowerCase()}"></app-${elementType.toLowerCase()}>"""
}

fun <T : ItemI<*>> T.toTypeScriptEntityListHTML(element: EntityI<*>): String =
    """<app-person [pageName]="profileDataService.pageName"></app-person>
<a class="newButton" [routerLink]="'./new'"
        routerLinkActive="active-link">
    <mat-icon>add_circle_outline</mat-icon> Add New Item
</a>

<a class="deleteButton" (click)="profileDataService.clearItems()">
    <mat-icon>delete_outline</mat-icon> Delete All Item
</a>
<app-table [displayedColumns]="tableHeader"></app-table>
"""

fun <T : ItemI<*>> T.toTypeScriptModuleSCSS(): String =
    """:host {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
}"""

fun <T : ItemI<*>> T.toTypeScriptEntityViewSCSS(): String =
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

fun <T : ItemI<*>> T.toTypeScriptEntityListSCSS(): String =
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

fun <T : AttributeI<*>> T.toTypeScriptInputFunction(c: GenerationContext, indent: String, element: AttributeI<*>): String {
    return when (element.type().toTypeScriptIfNative(c, "", element)) {
        "boolean" -> """${indent}this.${element.name()} = Boolean((<HTMLInputElement>document.getElementById('${element.name()}')).value);"""
        "string" -> """${indent}this.${element.name()} = this.simplifiedHtmlInputElement('${element.name()}');"""
        "number" -> """${indent}this.${element.name()} = Number((<HTMLInputElement>document.getElementById('${element.name()}')).value);"""
        else -> {
            when (element.type().props().size) {
                0 -> """${indent}this.${element.name()} = Number((<HTMLInputElement>document.getElementById('${element.name()}')).value);"""
                else -> {
                    element.type().props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
                        it.toTypeScriptInputFunctionWithInterface(c, element.name(), tab, it)
                    }
                }
            }
        }
    }
}

fun <T : AttributeI<*>> T.toTypeScriptInputFunctionWithInterface(c: GenerationContext, elementName: String, indent: String, element: AttributeI<*>): String {
    return if(element.type().toTypeScriptIfNative(c, "", element).equals("string") || element.type().toTypeScriptIfNative(c, "", element).equals("boolean")) {
        """${indent}this.${elementName}.${element.name()} = this.simplifiedHtmlInputElement('${elementName + element.name().capitalize()}');"""
    } else {
        """${indent}this.${elementName}.${element.name()} = Number((<HTMLInputElement>document.getElementById('${elementName + element.name().capitalize()}')).value);"""
    }
}

fun <T : BasicI<*>> T.toTypeScriptInputPushElementToArrayPart(indent: String, element: BasicI<*>): String =
    """${indent}this.dataElement.push([${element.props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(", ") {
        it.toTypeScriptInputPushingElements(it)
    }}]);
"""

fun <T : AttributeI<*>> T.toTypeScriptInputPushingElements(element: AttributeI<*>): String {
    return when (element.type().props().size) {
        0 -> """this.${element.name()}"""
        else -> {
            element.type().props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(", ") {
                it.toTypeScriptInputPushingElementsWithInterface(element.name(), tab, it)
            }
        }
    }
}

fun <T : AttributeI<*>> T.toTypeScriptInputPushingElementsWithInterface(elementName: String, indent: String, element: AttributeI<*>): String {
    return """this.${elementName}.${element.name()}"""
}

fun <T : AttributeI<*>> T.toTypeScriptDeleteFunction(c: GenerationContext, indent: String, element: AttributeI<*>, index: Int): String {
    tempIndex = index
    return when (element.type().toTypeScriptIfNative(c, "", element)) {
        "boolean", "string", "number" -> """${indent}this.dataElement[index][${tempIndex}] = '';"""
        else -> {
            when (element.type().props().size) {
                0 -> """${indent}this.dataElement[index][${tempIndex}] = '';"""
                else -> {
                    element.type().props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
                        it.toTypeScriptDeleteFunctionWithInterface(c, element.name(), tab, it, tempIndex)
                    }
                }
            }
        }
    }
}

fun <T : AttributeI<*>> T.toTypeScriptDeleteFunctionWithInterface(c: GenerationContext, elementName: String, indent: String, element: AttributeI<*>, index: Int): String {
    tempIndex = index + 1
    return when (element.type().toTypeScriptIfNative(c, "", element)) {
        "boolean", "string" -> """${indent}this.dataElement[index][${index}] = '';"""
        else -> {
            """${indent}this.dataElement[index][${index}]} = '';"""
        }
    }
}

fun <T : BasicI<*>> T.toTypeScriptDeleteElementFromArrayPart(indent: String): String =
    """${indent}this.tempArray = this.dataElement.filter(function(element) {return element[0] !== '' })
${indent}this.dataElement = this.tempArray;"""


fun <T : AttributeI<*>> T.toTypeScriptPrintFunction(indent: String, element: AttributeI<*>, index: Int): String {
    tempIndex = index
    return if (element.type().props().size > 0) {
        element.type().props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
            it.toTypeScriptPrintFunctionWithInterface(element.name(), tab, it, tempIndex)
        }
    } else {
        """${indent}console.log('${element.name().capitalize()} Value: ' + this.dataElement[index][${tempIndex}])"""
    }
}

fun <T : AttributeI<*>> T.toTypeScriptPrintFunctionWithInterface(elementName: String, indent: String, element: AttributeI<*>, index: Int): String {
    tempIndex = index + 1
    return """${indent}console.log('${elementName.capitalize()}${element.name().capitalize()} Value: ' + this.dataElement[index][${index}])"""
}

fun <T : AttributeI<*>> T.toTypeScriptLoadFunction(c: GenerationContext, indent: String, element: AttributeI<*>, index: Int): String {
    tempIndex = index
    return when (element.type().toTypeScriptIfNative(c, "", element)) {
        "boolean", "string", "number" -> """${indent}(<HTMLInputElement>document.getElementById('${element.name()}')).value = this.dataElement[index][${tempIndex}];"""
        else -> {
            when (element.type().props().size) {
                0 -> """${indent}(<HTMLInputElement>document.getElementById('${element.name()}')).value = this.dataElement[index][${tempIndex}];"""
                else -> {
                    element.type().props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
                        it.toTypeScriptLoadFunctionWithInterface(element.name(), tab, it, tempIndex)
                    }
                }
            }
        }
    }
}

fun <T : AttributeI<*>> T.toTypeScriptLoadFunctionWithInterface(elementName: String, indent: String, element: AttributeI<*>, index: Int): String {
    tempIndex = index + 1
    return """${indent}(<HTMLInputElement>document.getElementById('${elementName + element.name().capitalize()}')).value = this.dataElement[index][${index}];"""
}

fun <T : AttributeI<*>> T.toTypeScriptEditFunction(c: GenerationContext, indent: String, element: AttributeI<*>, index: Int): String {
    tempIndex = index
    return when (element.type().toTypeScriptIfNative(c, "", element)) {
        "boolean", "string", "number" -> """${indent}this.dataElement[index][${tempIndex}] = this.simplifiedHtmlInputElement('${element.name()}');"""
        else -> {
            when (element.type().props().size) {
                0 -> """${indent}this.dataElement[index][${tempIndex}] = this.simplifiedHtmlInputElement('${element.name()}');"""
                else -> {
                    element.type().props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
                        it.toTypeScriptEditFunctionWithInterface(element.name(), tab, it, tempIndex)
                    }
                }
            }
        }
    }
}

fun <T : AttributeI<*>> T.toTypeScriptEditFunctionWithInterface(elementName: String, indent: String, element: AttributeI<*>, index: Int): String {
    tempIndex = index + 1
    return """${indent}this.dataElement[index][${index}] = this.simplifiedHtmlInputElement('${elementName + element.name().capitalize()}');"""
}

fun <T : AttributeI<*>> T.toHtmlForm(element: AttributeI<*>): String {
    return if (element.type().props().size > 0) {
        element.type().props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL, postfix = nL) {
            it.toHtmlWithInterface(element.name(), it)
        }
    } else {
        """<mat-form-field appearance="fill">
    <mat-label>${element.name().capitalize()}</mat-label>
    <input matInput id="${element.name()}">
</mat-form-field>
"""
    }
}

fun <T : AttributeI<*>> T.toHtmlWithInterface(elementName: String, element: AttributeI<*>): String {
    return """
<mat-form-field appearance="fill">
    <mat-label>${elementName.capitalize()}${element.name().capitalize()}</mat-label>
    <input matInput id="${elementName}${element.name().capitalize()}">
</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toTypeScriptGenerateProperties(c: GenerationContext, indent: String, element: AttributeI<*>): String {
    return when (element.type().toTypeScriptIfNative(c, "", element)) {
        "string", "boolean" -> """${indent}${element.name()}: ${element.type().name()}"""
        "number" -> """${indent}${element.name()}: Number"""
        else -> {
            when (element.type().props().size) {
                0 -> """${indent}${element.name()}: ${element.type().name()}"""
                else -> {
                    """${indent}${element.name()}: ${element.type().name()} = new ${element.type().name().capitalize()}()"""
                }
            }
        }
    }
}

fun <T : BasicI<*>> T.toTypeScriptGenerateArrayPart(indent: String): String =
    """${indent}index = 0;
${indent}dataElement: any[][] = [];
${indent}tempArray: any[][] = [];
"""

fun <T : BasicI<*>> T.toTypeScriptGenerateComponentPart(element: BasicI<*>): String =
    """@Component({
  selector: 'app-${element.name().toLowerCase()}',
  templateUrl: './${element.name().toLowerCase()}.component.html',
  styleUrls: ['./${element.name().toLowerCase()}.component.scss']
})
"""

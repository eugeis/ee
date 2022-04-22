package ee.lang.gen.ts

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.java.j

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
    return """import { ${elementTypeName.capitalize()} } from '../../schkola/${elementParentNameRegex.toLowerCase()}/${elementParentNameRegex.capitalize() + "ApiBase"}';"""
}

fun <T : AttributeI<*>> T.toTypeScriptHtmlInputFunction(c: GenerationContext, indent: String, element: AttributeI<*>): String {
    return when (element.type().toTypeScriptIfNative(c, "", element)) {
        "boolean" -> """${indent}this.${element.name()} = Boolean((<HTMLInputElement>document.getElementById('${element.name()}')).value);"""
        "string" -> """${indent}this.${element.name()} = (<HTMLInputElement>document.getElementById('${element.name()}')).value;"""
        "number" -> """${indent}this.${element.name()} = Number((<HTMLInputElement>document.getElementById('${element.name()}')).value);"""
        else -> {
            when (element.type().props().size) {
                0 -> """${indent}this.${element.name()} = Number((<HTMLInputElement>document.getElementById('${element.name()}')).value);"""
                else -> {
                    element.type().props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
                        it.toTypeScriptHtmlInputFunctionInterface(c, element.name(), tab, it)
                    }
                }
            }
        }
    }
}

fun <T : AttributeI<*>> T.toTypeScriptHtmlInputFunctionInterface(c: GenerationContext,elementName: String, indent: String, element: AttributeI<*>): String {
    return if(element.type().toTypeScriptIfNative(c, "", element).equals("string") || element.type().toTypeScriptIfNative(c, "", element).equals("boolean")) {
        """${indent}this.${elementName}.${element.name()} = (<HTMLInputElement>document.getElementById('${elementName + element.name().capitalize()}')).value;"""
    } else {
        """${indent}this.${elementName}.${element.name()} = Number((<HTMLInputElement>document.getElementById('${elementName + element.name().capitalize()}')).value);"""
    }
}

fun <T : AttributeI<*>> T.toTypeScriptHtmlDeleteFunction(c: GenerationContext, indent: String, element: AttributeI<*>): String {
    return when (element.type().toTypeScriptIfNative(c, "", element)) {
        "boolean" -> """${indent}this.${element.name()} = undefined;"""
        "string" -> """${indent}this.${element.name()} = '';"""
        "number" -> """${indent}this.${element.name()} = 0;"""
        else -> {
            when (element.type().props().size) {
                0 -> """${indent}this.${element.name()} = 0;"""
                else -> {
                    element.type().props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
                        it.toTypeScriptHtmlDeleteFunctionInterface(c, element.name(), tab, it)
                    }
                }
            }
        }
    }
}

fun <T : AttributeI<*>> T.toTypeScriptHtmlDeleteFunctionInterface(c: GenerationContext, elementName: String, indent: String, element: AttributeI<*>): String {
    return when (element.type().toTypeScriptIfNative(c, "", element)) {
        "boolean" -> """${indent}this.${elementName}.${element.name()} = undefined;"""
        "string" -> """${indent}this.${elementName}.${element.name()} = '';"""
        else -> {
            """${indent}this.${elementName}.${element.name()} = 0;"""
        }
    }
}

fun <T : AttributeI<*>> T.toHtmlForm(element: AttributeI<*>): String {
    return if (element.type().props().size > 0) {
        element.type().props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL, postfix = nL) {
            it.toTypeScriptHtmlInterface(element.name(), it)
        }
    } else {
        """<mat-form-field appearance="fill">
    <mat-label>${element.name().capitalize()}</mat-label>
    <input matInput id="${element.name()}">
</mat-form-field>
"""
    }
}

fun <T : AttributeI<*>> T.toTypeScriptHtmlInterface(elementName: String, element: AttributeI<*>): String {
    return """
<mat-form-field appearance="fill">
    <mat-label>${elementName.capitalize()}${element.name().capitalize()}</mat-label>
    <input matInput id="${elementName}${element.name().capitalize()}">
</mat-form-field>"""
}

fun <T : AttributeI<*>> T.toTypeScriptHtmlPrintFunction(indent: String, element: AttributeI<*>): String {
    return if (element.type().props().size > 0) {
        element.type().props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
            it.toTypeScriptHtmlPrintFunctionInterface(element.name(), tab, it)
        }
    } else {
        """${indent}console.log('${element.name().capitalize()} Value: ' + this.${element.name()})"""
    }
}

fun <T : AttributeI<*>> T.toTypeScriptHtmlPrintFunctionInterface(elementName: String, indent: String, element: AttributeI<*>): String {
    return """${indent}console.log('${elementName.capitalize()}${element.name().capitalize()} Value: ' + this.${elementName}.${element.name()})"""
}

fun <T : AttributeI<*>> T.toTypeScriptProperties(c: GenerationContext, indent: String, element: AttributeI<*>): String {
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

fun <T : BasicI<*>> T.toTypeScriptGenerateComponentPart(element: BasicI<*>): String =
    """@Component({
  selector: 'app-${element.name().toLowerCase()}',
  templateUrl: './${element.name().toLowerCase()}.component.html',
  styleUrls: ['./${element.name().toLowerCase()}.component.scss']
})
"""

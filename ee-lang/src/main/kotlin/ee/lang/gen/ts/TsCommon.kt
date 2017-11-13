package ee.lang.gen.ts

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.java.j

fun <T : TypeIB<*>> T.toTypeScriptDefault(c: GenerationContext, derived: String, attr: AttributeIB<*>): String {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String, n.Text -> "\"\""
        n.Boolean -> "false"
        n.Int -> "0"
        n.Long -> "0L"
        n.Float -> "0f"
        n.Date -> "${c.n(j.util.Date)}()"
        n.Path -> "${c.n(j.nio.file.Paths)}.get(\"\")"
        n.Blob -> "new ByteArray(0)"
        n.Void -> ""
        n.Error -> "new Throwable()"
        n.Exception -> "new Exception()"
        n.Url -> "${c.n(j.net.URL)}(\"\")"
        n.Map -> (attr.isNotEMPTY() && attr.mutable().setAndTrue()).ifElse("new Map()", "new Map()")
        n.List -> (attr.isNotEMPTY() && attr.mutable().setAndTrue()).ifElse("new Array()", "new Array()")
        else -> {
            if (baseType is Literal) {
                "${(baseType.findParent(EnumTypeIB::class.java) as EnumTypeIB<*>).toTypeScript(c, derived, attr)}.${baseType.toTypeScript()}"
            } else if (baseType is EnumTypeIB<*>) {
                "${c.n(this, derived)}.${baseType.literals().first().toTypeScript()}"
            } else if (baseType is CompilationUnitIB<*>) {
                "new ${c.n(this, derived)}()"
            } else {
                (this.parent() == n).ifElse("\"\"", { "${c.n(this, derived)}.EMPTY" })
            }
        }
    }
}

fun <T : AttributeIB<*>> T.toTypeScriptDefault(c: GenerationContext, derived: String): String =
        type().toTypeScriptDefault(c, derived, this)


fun <T : ItemIB<*>> T.toTypeScriptEMPTY(c: GenerationContext, derived: String): String =
        (this.parent() == n).ifElse("\"\"", { "${c.n(this, derived)}.EMPTY" })


fun <T : AttributeIB<*>> T.toTypeScriptEMPTY(c: GenerationContext, derived: String): String =
        type().toTypeScriptEMPTY(c, derived)

fun <T : AttributeIB<*>> T.toTypeScriptTypeSingle(c: GenerationContext, api: String): String =
        type().toTypeScript(c, api, this)

fun <T : AttributeIB<*>> T.toTypeScriptTypeDef(c: GenerationContext, api: String): String =
        """${type().toTypeScript(c, api, this)}${nullable().then("?")}"""


fun <T : AttributeIB<*>> T.toTypeScriptCompanionObjectName(c: GenerationContext): String =
        """        val ${name().toUnderscoredUpperCase()} = "_${name()}""""

fun <T : CompilationUnitIB<*>> T.toTypeScriptExtends(c: GenerationContext, derived: String, api: String): String {
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

fun <T : TypeIB<*>> T.toTypeScriptIfNative(c: GenerationContext, derived: String, attr: AttributeIB<*>): String? {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.Any -> "any"
        n.String -> "string"
        n.Boolean -> "boolean"
        n.Int -> "number"
        n.Long -> "number"
        n.Float -> "number"
        n.Date -> "Date"
        n.TimeUnit -> "string"
        n.Path -> "string"
        n.Text -> "string"
        n.Blob -> "Blob"
        n.Exception -> "Error"
        n.Error -> "Error"
        n.Void -> "void"
        n.Url -> "string"
        n.UUID -> "string"
        n.List -> "Array${toTypeScriptGenericTypes(c, derived, attr)}"
        n.Map -> "Map${toTypeScriptGenericTypes(c, derived, attr)}"
        else -> {
            if (this is Lambda) operation().toTypeScriptLambda(c, derived) else null
        }
    }
}

fun TypeIB<*>.toTypeScriptGenericTypes(c: GenerationContext, derived: String, attr: AttributeIB<*>): String =
        generics().joinWrappedToString(", ", "", "<", ">") { it.type().toTypeScript(c, derived, attr) }

fun GenericIB<*>.toTypeScript(c: GenerationContext, derived: String): String =
        c.n(type(), derived)

fun TypeIB<*>.toTypeScriptGenerics(c: GenerationContext, derived: String, attr: AttributeIB<*>): String =
        generics().joinWrappedToString(", ", "", "<", ">") { it.toTypeScript(c, derived, attr) }

fun TypeIB<*>.toTypeScriptGenericsClassDef(c: GenerationContext, derived: String, attr: AttributeIB<*>): String =
        generics().joinWrappedToString(", ", "", "<", ">") {
            "${it.name()} : ${it.type().toTypeScript(c, derived, attr)}"
        }

fun TypeIB<*>.toTypeScriptGenericsMethodDef(c: GenerationContext, derived: String, attr: AttributeIB<*>): String =
        generics().joinWrappedToString(", ", "", "<", "> ") {
            "${it.name()} : ${it.type().toTypeScript(c, derived, attr)}"
        }

fun TypeIB<*>.toTypeScriptGenericsStar(context: GenerationContext, derived: String): String =
        generics().joinWrappedToString(", ", "", "<", "> ") { "*" }

fun OperationIB<*>.toTypeScriptGenerics(c: GenerationContext, derived: String): String =
        generics().joinWrappedToString(", ", "", "<", "> ") {
            "${it.name()} : ${it.type().toTypeScript(c, derived)}"
        }

fun <T : TypeIB<*>> T.toTypeScript(c: GenerationContext, derived: String, attr: AttributeIB<*> = Attribute.EMPTY): String =
        toTypeScriptIfNative(c, derived, attr) ?: "${c.n(this, derived)}${this.toTypeScriptGenericTypes(c, derived, attr)}"

fun <T : AttributeIB<*>> T.toTypeScriptValue(c: GenerationContext, derived: String): String {
    if (value() != null) {
        return when (type()) {
            n.String, n.Text -> "\"${value()}\""
            n.Boolean, n.Int, n.Long, n.Float, n.Date, n.Path, n.Blob, n.Void -> "${value()}"
            else -> {
                if (value() is Literal) {
                    val lit = value() as Literal
                    "${(lit.parent() as EnumTypeIB<*>).toTypeScript(c, derived, this)}.${lit.toTypeScript()}"
                } else {
                    "${value()}"
                }
            }
        }
    } else {
        return toTypeScriptDefault(c, derived)
    }
}

fun <T : AttributeIB<*>> T.toTypeScriptInit(c: GenerationContext, derived: String): String {
    if (value() != null) {
        return " = ${toTypeScriptValue(c, derived)}"
    } else if (nullable()) {
        return " = null"
    } else if (initByDefaultTypeValue()) {
        return " = ${toTypeScriptValue(c, derived)}"
    } else {
        return ""
    }
}

fun <T : AttributeIB<*>> T.toTypeScriptInitMember(c: GenerationContext, derived: String): String = "this.${name()}${toTypeScriptInit(c, derived)}"
fun <T : AttributeIB<*>> T.toTypeScriptSignature(c: GenerationContext, derived: String, api: String, init: Boolean = true): String =
        "${name()}: ${toTypeScriptTypeDef(c, api)}${init.then { toTypeScriptInit(c, derived) }}"

fun <T : AttributeIB<*>> T.toTypeScriptConstructorMember(c: GenerationContext, derived: String, api: String, init: Boolean = true): String =
        //"${replaceable().setAndTrue().ifElse("", "readonly ")}${toTypeScriptSignature(c, derived, api, init)}"
        "${toTypeScriptSignature(c, derived, api, init)}"

fun <T : AttributeIB<*>> T.toTypeScriptMember(c: GenerationContext, derived: String, api: String, init: Boolean = true): String =
        //"    ${replaceable().setAndTrue().ifElse("", "readonly ")}${toTypeScriptSignature(c, derived, api, init)}"
        "    ${toTypeScriptSignature(c, derived, api, init)}"

fun List<AttributeIB<*>>.toTypeScriptSignature(c: GenerationContext, derived: String, api: String): String =
        joinWrappedToString(", ") { it.toTypeScriptSignature(c, derived, api) }

fun List<AttributeIB<*>>.toTypeScriptMember(c: GenerationContext, derived: String, api: String): String =
        joinWrappedToString(", ") { it.toTypeScriptSignature(c, derived, api) }

fun <T : ConstructorIB<*>> T.toTypeScriptPrimary(c: GenerationContext, derived: String, api: String): String {
    return if (isNotEMPTY()) """(${params().
            joinWrappedToString(", ", "      ") { it.toTypeScriptConstructorMember(c, derived, api) }})${
    superUnit().toTypeScriptCall(c)}""" else ""
}

fun <T : ConstructorIB<*>> T.toTypeScript(c: GenerationContext, derived: String, api: String): String {
    return if (isNotEMPTY()) """
    constructor(${params().joinWrappedToString(", ", "                ") { it.toTypeScriptSignature(c, derived, api) }
    })${superUnit().isNotEMPTY().then { (superUnit() as ConstructorIB<*>).toTypeScriptCall(c, (parent() != superUnit().parent()).ifElse("super", "this")) }} ${
    paramsWithOut(superUnit()).joinSurroundIfNotEmptyToString("${nL}        ", prefix = "{${nL}        ") {
        it.toTypeScriptAssign(c)
    }}${(parent() as CompilationUnitIB<*>).props().filter { it.meta() }.joinSurroundIfNotEmptyToString("${nL}        ", prefix = "${nL}        ") {
        it.toTypeScriptInitMember(c, derived)
    }}
    }""" else ""
}

fun <T : ConstructorIB<*>> T.toTypeScriptCall(c: GenerationContext, name: String = "this"): String =
        isNotEMPTY().then { " : $name(${params().joinWrappedToString(", ") { it.name() }})" }

fun <T : AttributeIB<*>> T.toTypeScriptAssign(c: GenerationContext): String =
        "this.${name()} = ${name()}"

fun <T : LogicUnitIB<*>> T.toTypeScriptCall(c: GenerationContext): String =
        isNotEMPTY().then { "(${params().joinWrappedToString(", ") { it.name() }})" }

fun <T : LogicUnitIB<*>> T.toTypeScriptCallValue(c: GenerationContext, derived: String): String =
        isNotEMPTY().then { "(${params().joinWrappedToString(", ") { it.toTypeScriptValue(c, derived) }})" }

fun <T : LiteralIB<*>> T.toTypeScriptCallValue(c: GenerationContext, derived: String): String =
        params().isNotEmpty().then { "(${params().joinWrappedToString(", ") { it.toTypeScriptValue(c, derived) }})" }

fun <T : AttributeIB<*>> T.toTypeScriptType(c: GenerationContext, derived: String): String = type().toTypeScript(c, derived, this)
fun List<AttributeIB<*>>.toTypeScriptTypes(c: GenerationContext, derived: String): String =
        joinWrappedToString(", ") { it.toTypeScriptType(c, derived) }

fun <T : OperationIB<*>> T.toTypeScriptLambda(c: GenerationContext, derived: String): String =
        """(${params().toTypeScriptTypes(c, derived)}) -> ${retFirst().toTypeScriptType(c, derived)}"""

fun <T : OperationIB<*>> T.toTypeScriptImpl(c: GenerationContext, derived: String, api: String): String {
    return """
    ${toTypeScriptGenerics(c, derived)}${name()}(${
    params().toTypeScriptSignature(c, derived, api)}) : ${retFirst().toTypeScriptTypeDef(c, api)} {
        throw new ReferenceError("Not implemented yet.");
    }"""
}
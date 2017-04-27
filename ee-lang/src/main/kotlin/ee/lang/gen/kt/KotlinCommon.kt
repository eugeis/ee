package ee.lang.gen.kt

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.java.j
import ee.lang.gen.kt.k

fun <T : TypeI> T.toKotlinEmpty(c: GenerationContext, derived: String, attr: AttributeI): String {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String, n.Text -> "\"\""
        n.Boolean -> "false"
        n.Int -> "0"
        n.Long -> "0L"
        n.Float -> "0f"
        n.Date -> "${c.n(j.util.Date)}()"
        n.Path -> "${c.n(j.nio.file.Paths)}.get(\"\")"
        n.Blob -> "ByteArray(0)"
        n.Void -> ""
        n.Error -> "Throwable()"
        n.Exception -> "Exception()"
        n.Url -> "${c.n(j.net.URL)}(\"\")"
        n.Map -> (attr.isNotEmpty() && attr.mutable()).ifElse("hashMapOf()", "emptyMap()")
        n.List -> (attr.isNotEmpty() && attr.mutable()).ifElse("arrayListOf()", "arrayListOf()")
        else -> {
            if (baseType is Literal) {
                "${(baseType.findParent(EnumTypeI::class.java) as EnumTypeI).toKotlin(c, derived, attr)}.${baseType.toKotlin()}"
            } else {
                (this.parent() == n).ifElse("\"\"", { "${c.n(this, derived)}.EMPTY" })
            }
        }
    }
}


fun <T : AttributeI> T.toKotlinEmpty(c: GenerationContext, derived: String): String {
    return type().toKotlinEmpty(c, derived, this)
}


fun <T : AttributeI> T.toKotlinTypeSingle(c: GenerationContext, api: String): String {
    return type().toKotlin(c, api, this)
}

fun <T : AttributeI> T.toKotlinTypeDef(c: GenerationContext, api: String): String {
    return """${type().toKotlin(c, api, this)}${nullable().then("?")}"""
}

fun <T : CompilationUnitI> T.toKotlinEmptyObject(c: GenerationContext, derived: String): String {
    return """
    companion object {
        val EMPTY = ${c.n(this, derived)}()
    }"""
}

fun <T : AttributeI> T.toKotlinCompanionObjectName(c: GenerationContext): String {
    return """        val ${name().toUnderscoredUpperCase()} = "_${name()}""""
}

fun <T : CompilationUnitI> T.toKotlinExtends(c: GenerationContext, derived: String, api: String): String {
    if (superUnit().isNotEmpty() && derived != api) {
        return " : ${c.n(superUnit(), derived)}, ${c.n(this, api)}"
    } else if (superUnit().isNotEmpty()) {
        return " : ${c.n(superUnit(), derived)}"
    } else if (derived != api) {
        return " : ${c.n(this, api)}"
    } else {
        return ""
    }
}

fun <T : TypeI> T.toKotlinIfNative(c: GenerationContext, derived: String, attr: AttributeI): String? {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String -> "String"
        n.Boolean -> "Boolean"
        n.Int -> "Int"
        n.Long -> "Long"
        n.Float -> "Float"
        n.Date -> c.n(j.util.Date)
        n.TimeUnit -> c.n(j.util.concurrent.TimeUnit)
        n.Path -> c.n(j.nio.file.Path)
        n.Text -> "String"
        n.Blob -> "ByteArray"
        n.Exception -> "Exception"
        n.Error -> "Throwable"
        n.Void -> "Unit"
        n.Url -> c.n(j.net.URL)
        n.List -> "${c.n((attr.isNotEmpty() && attr.mutable()).ifElse(k.core.MutableList, k.core.List), derived)}${toKotlinGenericTypes(c, derived, attr)}"
        n.Map -> "${c.n((attr.isNotEmpty() && attr.mutable()).ifElse(k.core.MutableMap, k.core.Map), derived)}${toKotlinGenericTypes(c, derived, attr)}"
        else -> {
            if (this is Lambda) operation().toKotlinLamnda(c, derived) else null
        }
    }
}

fun TypeI.toKotlinGenericTypes(c: GenerationContext, derived: String, attr: AttributeI): String {
    return """${generics().joinWrappedToString(", ", "", "<", ">") { "${it.type().toKotlin(c, derived, attr)}" }}"""
}

fun GenericI.toKotlin(c: GenerationContext, derived: String): String {
    return c.n(type(), derived)
}

fun TypeI.toKotlinGenerics(c: GenerationContext, derived: String, attr: AttributeI): String {
    return """${generics().joinWrappedToString(", ", "", "<", ">") { "${it.toKotlin(c, derived, attr)}" }}"""
}

fun TypeI.toKotlinGenericsClassDef(c: GenerationContext, derived: String, attr: AttributeI): String {
    return """${generics().joinWrappedToString(", ", "", "<", ">") { "${it.name()} : ${it.type().toKotlin(c, derived, attr)}" }}"""
}

fun TypeI.toKotlinGenericsMethodDef(c: GenerationContext, derived: String, attr: AttributeI): String {
    return """${generics().joinWrappedToString(", ", "", "<", "> ") { "${it.name()} : ${it.type().toKotlin(c, derived, attr)}" }}"""
}

fun TypeI.toKotlinGenericsStar(context: GenerationContext, derived: String): String {
    return """${generics().joinWrappedToString(", ", "", "<", "> ") { "*" }}"""
}

fun OperationI.toKotlinGenerics(c: GenerationContext, derived: String): String {
    return """${generics().joinWrappedToString(", ", "", "<", "> ") { "${it.name()} : ${it.type().toKotlin(c, derived)}" }}"""
}

fun <T : TypeI> T.toKotlin(c: GenerationContext, derived: String, attr: AttributeI = Attribute.EMPTY): String {
    return toKotlinIfNative(c, derived, attr) ?: "${c.n(this, derived)}${this.toKotlinGenericTypes(c, derived, attr)}"
}

fun <T : AttributeI> T.toKotlinValue(c: GenerationContext, derived: String): String {
    if (value() != null) {
        return when (type()) {
            n.String, n.Text -> "\"${value()}\""
            n.Boolean, n.Int, n.Long, n.Float, n.Date, n.Path, n.Blob, n.Void -> "${value()}"
            else -> {
                if (value() is Literal) {
                    val lit = value() as Literal
                    "${(lit.parent() as EnumTypeI).toKotlin(c, derived, this)}.${lit.toKotlin()}"
                } else {
                    "${value()}"
                }
            }
        }
    } else {
        return toKotlinEmpty(c, derived)
    }
}

fun <T : AttributeI> T.toKotlinInit(c: GenerationContext, derived: String, api: String): String {
    if (value() != null) {
        return " = ${toKotlinValue(c, derived)}"
    } else if (nullable()) {
        return " = null"
    } else if (initByDefaultTypeValue()) {
        return " = ${toKotlinValue(c, derived)}"
    } else {
        return ""
    }
}

fun <T : AttributeI> T.toKotlinSignature(c: GenerationContext, derived: String, api: String, init: Boolean = true): String {
    return "${name()}: ${toKotlinTypeDef(c, api)}${init.then { toKotlinInit(c, derived, api) }}"
}

fun <T : AttributeI> T.toKotlinMember(c: GenerationContext, derived: String, api: String, init: Boolean = true): String {
    return "    ${replaceable().ifElse("var ", "val ")}${toKotlinSignature(c, derived, api, init)}"
}

fun List<AttributeI>.toKotlinSignature(c: GenerationContext, derived: String, api: String): String {
    return "${joinWrappedToString(", ") { it.toKotlinSignature(c, derived, api) }}"
}

fun List<AttributeI>.toKotlinMember(c: GenerationContext, derived: String, api: String): String {
    return "${joinWrappedToString(", ") { it.toKotlinSignature(c, derived, api) }}"
}

fun <T : ConstructorI> T.toKotlinPrimary(c: GenerationContext, derived: String, api: String): String {
    return if (isNotEmpty()) """(${params().
            joinWrappedToString(", ", "      ") { it.toKotlinMember(c, derived, api) }})${
    superUnit().toKotlinCall(c)}""" else ""
}

fun <T : ConstructorI> T.toKotlin(c: GenerationContext, derived: String, api: String): String {
    return if (isNotEmpty()) """
    constructor(${params().joinWrappedToString(", ", "                ") { it.toKotlinSignature(c, derived, api) }
    })${(superUnit() as ConstructorI).toKotlinCall(c, "${(parent() != superUnit().parent()).ifElse("super", "this")}")} ${
    paramsWithOut(superUnit()).joinSurroundIfNotEmptyToString("$nL        ", prefix = "{$nL        ") {
        it.toKotlinAssign(c)
    }}
    }""" else ""
}

fun <T : ConstructorI> T.toKotlinCall(c: GenerationContext, name: String = "this"): String {
    return isNotEmpty().then { " : $name(${params().joinWrappedToString(", ") { it.name() }})" }
}

fun <T : AttributeI> T.toKotlinAssign(c: GenerationContext): String {
    return "this.${name()} = ${name()}"
}

fun <T : LogicUnitI> T.toKotlinCall(c: GenerationContext): String {
    return isNotEmpty().then { "(${params().joinWrappedToString(", ") { it.name() }})" }
}

fun <T : LogicUnitI> T.toKotlinCallValue(c: GenerationContext, derived: String): String {
    return isNotEmpty().then { "(${params().joinWrappedToString(", ") { it.toKotlinValue(c, derived) }})" }
}

fun <T : LiteralI> T.toKotlinCallValue(c: GenerationContext, derived: String): String {
    return params().isNotEmpty().then { "(${params().joinWrappedToString(", ") { it.toKotlinValue(c, derived) }})" }
}

fun <T : AttributeI> T.toKotlinType(c: GenerationContext, derived: String): String = type().toKotlin(c, derived, this)

fun List<AttributeI>.toKotlinTypes(c: GenerationContext, derived: String): String {
    return "${joinWrappedToString(", ") { it.toKotlinType(c, derived) }}"
}

fun <T : OperationI> T.toKotlinLamnda(c: GenerationContext, derived: String): String =
        """(${params().toKotlinTypes(c, derived)}) -> ${ret().toKotlinType(c, derived)}"""

fun <T : OperationI> T.toKotlinImpl(c: GenerationContext, derived: String, api: String): String {
    return """
    ${open().then("open ")}fun ${toKotlinGenerics(c, derived)}${name()}(${
    params().toKotlinSignature(c, derived, api)})${ret().toKotlinTypeDef(c, api)} {
        throw IllegalAccessException("Not implemented yet.")
    }"""
}

fun <T : CompositeI> T.toKotlinIsEmptyExt(c: GenerationContext,
                                          derived: String = DerivedNames.IMPL.name,
                                          api: String = DerivedNames.API.name): String {
    return """
fun ${c.n(this, api)}?.isEmpty(): Boolean = (this == null || this == ${c.n(this, derived)}.EMPTY)
fun ${c.n(this, api)}?.isNotEmpty(): Boolean = !isEmpty()"""
}
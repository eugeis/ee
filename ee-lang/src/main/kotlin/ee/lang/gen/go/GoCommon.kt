package ee.lang.gen.go

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.java.j
import ee.lang.gen.kt.k

fun <T : TypeI> T.toGoEmpty(c: GenerationContext, derived: String, attr: AttributeI): String {
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
        n.Map -> (attr.isNotEMPTY() && attr.mutable()).ifElse("hashMapOf()", "emptyMap()")
        n.List -> (attr.isNotEMPTY() && attr.mutable()).ifElse("arrayListOf()", "arrayListOf()")
        else -> {
            if (baseType is Literal) {
                "${(baseType.findParent(EnumTypeI::class.java) as EnumTypeI).toGo(c, derived, attr)}.${baseType.toGo()}"
            } else {
                (this.parent() == n).ifElse("\"\"", { "${c.n(this, derived)}.EMPTY" })
            }
        }
    }
}


fun <T : AttributeI> T.toGoEmpty(c: GenerationContext, derived: String): String {
    return type().toGoEmpty(c, derived, this)
}


fun <T : AttributeI> T.toGoTypeSingle(c: GenerationContext, api: String): String {
    return type().toGo(c, api, this)
}

fun <T : AttributeI> T.toGoTypeDef(c: GenerationContext, api: String): String {
    return """${type().toGo(c, api, this)}${nullable().then("?")}"""
}

fun <T : CompilationUnitI> T.toGoEmptyObject(c: GenerationContext, derived: String): String {
    return """
    companion object {
        val EMPTY = ${c.n(this, derived)}()
    }"""
}

fun <T : AttributeI> T.toGoCompanionObjectName(c: GenerationContext): String {
    return """        val ${name().toUnderscoredUpperCase()} = "_${name()}""""
}

fun <T : CompilationUnitI> T.toGoExtends(c: GenerationContext, derived: String, api: String): String {
    if (superUnit().isNotEMPTY() && derived != api) {
        return " : ${c.n(superUnit(), derived)}, ${c.n(this, api)}"
    } else if (superUnit().isNotEMPTY()) {
        return " : ${c.n(superUnit(), derived)}"
    } else if (derived != api) {
        return " : ${c.n(this, api)}"
    } else {
        return ""
    }
}

fun <T : TypeI> T.toGoIfNative(c: GenerationContext, derived: String, attr: AttributeI): String? {
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
        n.List -> "${c.n((attr.isNotEMPTY() && attr.mutable()).ifElse(k.core.MutableList, k.core.List), derived)}${toGoGenericTypes(c, derived, attr)}"
        n.Map -> "${c.n((attr.isNotEMPTY() && attr.mutable()).ifElse(k.core.MutableMap, k.core.Map), derived)}${toGoGenericTypes(c, derived, attr)}"
        else -> {
            if (this is Lambda) operation().toGoLamnda(c, derived) else null
        }
    }
}

fun TypeI.toGoGenericTypes(c: GenerationContext, derived: String, attr: AttributeI): String {
    return """${generics().joinWrappedToString(", ", "", "<", ">") { "${it.type().toGo(c, derived, attr)}" }}"""
}

fun GenericI.toGo(c: GenerationContext, derived: String): String {
    return c.n(type(), derived)
}

fun TypeI.toGoGenerics(c: GenerationContext, derived: String, attr: AttributeI): String {
    return """${generics().joinWrappedToString(", ", "", "<", ">") { "${it.toGo(c, derived, attr)}" }}"""
}

fun TypeI.toGoGenericsClassDef(c: GenerationContext, derived: String, attr: AttributeI): String {
    return """${generics().joinWrappedToString(", ", "", "<", ">") { "${it.name()} : ${it.type().toGo(c, derived, attr)}" }}"""
}

fun TypeI.toGoGenericsMethodDef(c: GenerationContext, derived: String, attr: AttributeI): String {
    return """${generics().joinWrappedToString(", ", "", "<", "> ") { "${it.name()} : ${it.type().toGo(c, derived, attr)}" }}"""
}

fun TypeI.toGoGenericsStar(context: GenerationContext, derived: String): String {
    return """${generics().joinWrappedToString(", ", "", "<", "> ") { "*" }}"""
}

fun OperationI.toGoGenerics(c: GenerationContext, derived: String): String {
    return """${generics().joinWrappedToString(", ", "", "<", "> ") { "${it.name()} : ${it.type().toGo(c, derived)}" }}"""
}

fun <T : TypeI> T.toGo(c: GenerationContext, derived: String, attr: AttributeI = Attribute.EMPTY): String {
    return toGoIfNative(c, derived, attr) ?: "${c.n(this, derived)}${this.toGoGenericTypes(c, derived, attr)}"
}

fun <T : AttributeI> T.toGoValue(c: GenerationContext, derived: String): String {
    if (value() != null) {
        return when (type()) {
            n.String, n.Text -> "\"${value()}\""
            n.Boolean, n.Int, n.Long, n.Float, n.Date, n.Path, n.Blob, n.Void -> "${value()}"
            else -> {
                if (value() is Literal) {
                    val lit = value() as Literal
                    "${(lit.parent() as EnumTypeI).toGo(c, derived, this)}.${lit.toGo()}"
                } else {
                    "${value()}"
                }
            }
        }
    } else {
        return toGoEmpty(c, derived)
    }
}

fun <T : AttributeI> T.toGoInit(c: GenerationContext, derived: String, api: String): String {
    if (value() != null) {
        return " = ${toGoValue(c, derived)}"
    } else if (nullable()) {
        return " = null"
    } else if (initByDefaultTypeValue()) {
        return " = ${toGoValue(c, derived)}"
    } else {
        return ""
    }
}

fun <T : AttributeI> T.toGoSignature(c: GenerationContext, derived: String, api: String, init: Boolean = true): String {
    return "${name()}: ${toGoTypeDef(c, api)}${init.then { toGoInit(c, derived, api) }}"
}

fun <T : AttributeI> T.toGoMember(c: GenerationContext, derived: String, api: String, init: Boolean = true): String {
    return "    ${replaceable().ifElse("var ", "val ")}${toGoSignature(c, derived, api, init)}"
}

fun List<AttributeI>.toGoSignature(c: GenerationContext, derived: String, api: String): String {
    return "${joinWrappedToString(", ") { it.toGoSignature(c, derived, api) }}"
}

fun List<AttributeI>.toGoMember(c: GenerationContext, derived: String, api: String): String {
    return "${joinWrappedToString(", ") { it.toGoSignature(c, derived, api) }}"
}

fun <T : ConstructorI> T.toGoPrimary(c: GenerationContext, derived: String, api: String): String {
    return if (isNotEMPTY()) """(${params().
            joinWrappedToString(", ", "      ") { it.toGoMember(c, derived, api) }})${
    superUnit().toGoCall(c)}""" else ""
}

fun <T : ConstructorI> T.toGo(c: GenerationContext, derived: String, api: String): String {
    return if (isNotEMPTY()) """
    constructor(${params().joinWrappedToString(", ", "                ") { it.toGoSignature(c, derived, api) }
    })${(superUnit() as ConstructorI).toGoCall(c, "${(parent() != superUnit().parent()).ifElse("super", "this")}")} ${
    paramsWithOut(superUnit()).joinSurroundIfNotEmptyToString("$nL        ", prefix = "{$nL        ") {
        it.toGoAssign(c)
    }}
    }""" else ""
}

fun <T : ConstructorI> T.toGoCall(c: GenerationContext, name: String = "this"): String {
    return isNotEMPTY().then { " : $name(${params().joinWrappedToString(", ") { it.name() }})" }
}

fun <T : AttributeI> T.toGoAssign(c: GenerationContext): String {
    return "this.${name()} = ${name()}"
}

fun <T : LogicUnitI> T.toGoCall(c: GenerationContext): String {
    return isNotEMPTY().then { "(${params().joinWrappedToString(", ") { it.name() }})" }
}

fun <T : LogicUnitI> T.toGoCallValue(c: GenerationContext, derived: String): String {
    return isNotEMPTY().then { "(${params().joinWrappedToString(", ") { it.toGoValue(c, derived) }})" }
}

fun <T : LiteralI> T.toGoCallValue(c: GenerationContext, derived: String): String {
    return params().isNotEMPTY().then { "(${params().joinWrappedToString(", ") { it.toGoValue(c, derived) }})" }
}

fun <T : AttributeI> T.toGoType(c: GenerationContext, derived: String): String = type().toGo(c, derived, this)

fun List<AttributeI>.toGoTypes(c: GenerationContext, derived: String): String {
    return "${joinWrappedToString(", ") { it.toGoType(c, derived) }}"
}

fun <T : OperationI> T.toGoLamnda(c: GenerationContext, derived: String): String =
        """(${params().toGoTypes(c, derived)}) -> ${ret().toGoType(c, derived)}"""

fun <T : OperationI> T.toGoImpl(c: GenerationContext, derived: String, api: String): String {
    return """
    ${open().then("open ")}fun ${toGoGenerics(c, derived)}${name()}(${
    params().toGoSignature(c, derived, api)})${ret().toGoTypeDef(c, api)} {
        throw IllegalAccessException("Not implemented yet.")
    }"""
}

fun <T : CompositeI> T.toGoIsEmptyExt(c: GenerationContext,
                                          derived: String = DerivedNames.IMPL.name,
                                          api: String = DerivedNames.API.name): String {
    return """
fun ${c.n(this, api)}?.isEMPTY(): Boolean = (this == null || this == ${c.n(this, derived)}.EMPTY)
fun ${c.n(this, api)}?.isNotEMPTY(): Boolean = !isEMPTY()"""
}
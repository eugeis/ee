package ee.lang.gen.kt

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.java.j

fun <T : TypeIB<*>> T.toKotlinDefault(c: GenerationContext, derived: String, attr: AttributeIB<*>): String {
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
        n.Map -> (attr.isNotEMPTY() && attr.mutable().setAndTrue()).ifElse("hashMapOf()", "emptyMap()")
        n.List -> (attr.isNotEMPTY() && attr.mutable().setAndTrue()).ifElse("arrayListOf()", "arrayListOf()")
        else -> {
            if (baseType is Literal) {
                "${(baseType.findParent(EnumTypeIB::class.java) as EnumTypeIB<*>).toKotlin(c, derived, attr)}.${baseType.toKotlin()}"
            } else if (baseType is EnumTypeIB<*>) {
                "${c.n(this, derived)}.${baseType.literals().first().toKotlin()}"
            } else if (baseType is CompilationUnitIB<*>) {
                "${c.n(this, derived)}()"
            } else {
                (this.parent() == n).ifElse("\"\"", { "${c.n(this, derived)}.EMPTY" })
            }
        }
    }
}

fun <T : AttributeIB<*>> T.toKotlinDefault(c: GenerationContext, derived: String): String {
    return type().toKotlinDefault(c, derived, this)
}

fun <T : AttributeIB<*>> T.toKotlinEMPTY(c: GenerationContext, derived: String): String {
    return (type().parent() == n).ifElse({ type().toKotlinDefault(c, derived, this) }, { "${c.n(type(), derived)}.EMPTY" })
}


fun <T : AttributeIB<*>> T.toKotlinTypeSingle(c: GenerationContext, api: String): String {
    return type().toKotlin(c, api, this)
}

fun <T : AttributeIB<*>> T.toKotlinTypeDef(c: GenerationContext, api: String): String {
    return """${type().toKotlin(c, api, this)}${nullable().then("?")}"""
}

fun <T : CompilationUnitIB<*>> T.toKotlinEmptyObject(c: GenerationContext, derived: String): String {
    return """
    companion object {
        val EMPTY = ${c.n(this, derived)}()
    }"""
}

fun <T : AttributeIB<*>> T.toKotlinCompanionObjectName(c: GenerationContext): String {
    return """        val ${name().toUnderscoredUpperCase()} = "_${name()}""""
}

fun <T : CompilationUnitIB<*>> T.toKotlinExtends(c: GenerationContext, derived: String, api: String): String {
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

fun <T : TypeIB<*>> T.toKotlinIfNative(c: GenerationContext, derived: String, attr: AttributeIB<*>): String? {
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
        n.List -> "${c.n((attr.isNotEMPTY() && attr.mutable().setAndTrue()).ifElse(k.core.MutableList, k.core.List),
                derived)}${toKotlinGenericTypes(c, derived, attr)}"
        n.Map -> "${c.n((attr.isNotEMPTY() && attr.mutable().setAndTrue()).ifElse(k.core.MutableMap, k.core.Map),
                derived)}${toKotlinGenericTypes(c, derived, attr)}"
        else -> {
            if (this is Lambda) operation().toKotlinLambda(c, derived) else null
        }
    }
}

fun TypeIB<*>.toKotlinGenericTypes(c: GenerationContext, derived: String, attr: AttributeIB<*>): String =
        generics().joinWrappedToString(", ", "", "<", ">") { it.type().toKotlin(c, derived, attr) }


fun GenericIB<*>.toKotlin(c: GenerationContext, derived: String): String =
        c.n(type(), derived)

fun TypeIB<*>.toKotlinGenerics(c: GenerationContext, derived: String, attr: AttributeIB<*>): String =
        generics().joinWrappedToString(", ", "", "<", ">") { it.toKotlin(c, derived, attr) }

fun TypeIB<*>.toKotlinGenericsClassDef(c: GenerationContext, derived: String, attr: AttributeIB<*>): String =
        generics().joinWrappedToString(", ", "", "<", ">") {
            "${it.name()} : ${it.type().toKotlin(c, derived, attr)}"
        }

fun TypeIB<*>.toKotlinGenericsMethodDef(c: GenerationContext, derived: String, attr: AttributeIB<*>): String =
        generics().joinWrappedToString(", ", "", "<", "> ") {
            "${it.name()} : ${it.type().toKotlin(c, derived, attr)}"
        }


fun TypeIB<*>.toKotlinGenericsStar(context: GenerationContext, derived: String): String =
        generics().joinWrappedToString(", ", "", "<", "> ") { "*" }

fun OperationIB<*>.toKotlinGenerics(c: GenerationContext, derived: String): String =
        generics().joinWrappedToString(", ", "", "<", "> ") {
            "${it.name()} : ${it.type().toKotlin(c, derived)}"
        }

fun <T : TypeIB<*>> T.toKotlin(c: GenerationContext, derived: String, attr: AttributeIB<*> = Attribute.EMPTY): String =
        toKotlinIfNative(c, derived, attr) ?: "${c.n(this, derived)}${this.toKotlinGenericTypes(c, derived, attr)}"


fun <T : AttributeIB<*>> T.toKotlinValue(c: GenerationContext, derived: String): String {
    if (value() != null) {
        return when (type()) {
            n.String, n.Text -> "\"${value()}\""
            n.Boolean, n.Int, n.Long, n.Float, n.Date, n.Path, n.Blob, n.Void -> "${value()}"
            else -> {
                if (value() is Literal) {
                    val lit = value() as Literal
                    "${(lit.parent() as EnumTypeIB<*>).toKotlin(c, derived, this)}.${lit.toKotlin()}"
                } else {
                    "${value()}"
                }
            }
        }
    } else {
        return toKotlinDefault(c, derived)
    }
}

fun <T : AttributeIB<*>> T.toKotlinInit(c: GenerationContext, derived: String): String {
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

fun <T : AttributeIB<*>> T.toKotlinInitMember(c: GenerationContext, derived: String): String = "this.${name()}${toKotlinInit(c, derived)}"

fun <T : AttributeIB<*>> T.toKotlinSignature(c: GenerationContext, derived: String, api: String, init: Boolean = true): String =
        "${name()}: ${toKotlinTypeDef(c, api)}${init.then { toKotlinInit(c, derived) }}"

fun <T : AttributeIB<*>> T.toKotlinConstructorMember(c: GenerationContext, derived: String, api: String, init: Boolean = true): String =
        "${replaceable().setAndTrue().ifElse("var ", "val ")}${toKotlinSignature(c, derived, api, init)}"

fun <T : AttributeIB<*>> T.toKotlinMember(c: GenerationContext, derived: String, api: String, init: Boolean = true): String =
        "    ${replaceable().setAndTrue().ifElse("var ", "val ")}${toKotlinSignature(c, derived, api, init)}"

fun List<AttributeIB<*>>.toKotlinSignature(c: GenerationContext, derived: String, api: String): String =
        joinWrappedToString(", ") { it.toKotlinSignature(c, derived, api) }

fun List<AttributeIB<*>>.toKotlinMember(c: GenerationContext, derived: String, api: String): String =
        joinWrappedToString(", ") { it.toKotlinSignature(c, derived, api) }

fun <T : ConstructorIB<*>> T.toKotlinPrimary(c: GenerationContext, derived: String, api: String): String {
    return if (isNotEMPTY()) """(${params().
            joinWrappedToString(", ", "      ") { it.toKotlinConstructorMember(c, derived, api) }})${
    superUnit().toKotlinCall(c)}""" else ""
}

fun <T : ConstructorIB<*>> T.toKotlin(c: GenerationContext, derived: String, api: String): String {
    return if (isNotEMPTY()) """
    constructor(${params().joinWrappedToString(", ", "                ") { it.toKotlinSignature(c, derived, api) }
    })${superUnit().isNotEMPTY().then { (superUnit() as ConstructorIB<*>).toKotlinCall(c, (parent() != superUnit().parent()).ifElse("super", "this")) }} ${
    paramsWithOut(superUnit()).joinSurroundIfNotEmptyToString("$nL        ", prefix = "{$nL        ") {
        it.toKotlinAssign(c)
    }}${(parent() as CompilationUnitIB<*>).props().filter { it.meta() }.joinSurroundIfNotEmptyToString("$nL        ", prefix = "$nL        ") {
        it.toKotlinInitMember(c, derived)
    }}
    }""" else ""
}

fun <T : ConstructorIB<*>> T.toKotlinCall(c: GenerationContext, name: String = "this"): String =
        isNotEMPTY().then { " : $name(${params().joinWrappedToString(", ") { it.name() }})" }

fun <T : AttributeIB<*>> T.toKotlinAssign(c: GenerationContext): String =
        "this.${name()} = ${name()}"

fun <T : LogicUnitIB<*>> T.toKotlinCall(c: GenerationContext): String =
        isNotEMPTY().then { "(${params().joinWrappedToString(", ") { it.name() }})" }


fun <T : LogicUnitIB<*>> T.toKotlinCallValue(c: GenerationContext, derived: String): String =
        isNotEMPTY().then { "(${params().joinWrappedToString(", ") { it.toKotlinValue(c, derived) }})" }

fun <T : LiteralIB<*>> T.toKotlinCallValue(c: GenerationContext, derived: String): String =
        params().isNotEmpty().then { "(${params().joinWrappedToString(", ") { it.toKotlinValue(c, derived) }})" }

fun <T : AttributeIB<*>> T.toKotlinType(c: GenerationContext, derived: String): String = type().toKotlin(c, derived, this)

fun List<AttributeIB<*>>.toKotlinTypes(c: GenerationContext, derived: String): String =
        joinWrappedToString(", ") { it.toKotlinType(c, derived) }

fun <T : OperationIB<*>> T.toKotlinLambda(c: GenerationContext, derived: String): String =
        """(${params().toKotlinTypes(c, derived)}) -> ${retFirst().toKotlinType(c, derived)}"""

fun <T : OperationIB<*>> T.toKotlinImpl(c: GenerationContext, derived: String, api: String): String {
    return """
    ${open().then("open ")}fun ${toKotlinGenerics(c, derived)}${name()}(${
    params().toKotlinSignature(c, derived, api)})${retFirst().toKotlinTypeDef(c, api)} {
        throw IllegalAccessException("Not implemented yet.")
    }"""
}

fun <T : CompositeIB<*>> T.toKotlinIsEmptyExt(c: GenerationContext,
                                          derived: String = LangDerivedKind.IMPL,
                                          api: String = LangDerivedKind.API): String {
    return """
fun ${c.n(this, api)}?.isEMPTY(): Boolean = (this == null || this == ${c.n(this, derived)}.EMPTY)
fun ${c.n(this, api)}?.isNotEMPTY(): Boolean = !isEMPTY()"""
}
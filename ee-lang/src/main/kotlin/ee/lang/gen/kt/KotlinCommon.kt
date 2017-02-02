package ee.lang.gen.kt

import ee.common.ext.ifElse
import ee.common.ext.joinWrappedToString
import ee.common.ext.then
import ee.lang.*
import ee.lang.gen.java.Java

fun <T : ItemI> T.toKotlinEmpty(c: GenerationContext, derived: String): String {
    return (this.parent() == n).ifElse("\"\"", { "${c.n(this, derived)}.EMPTY" })
}

fun <T : AttributeI> T.toKotlinEmpty(c: GenerationContext, derived: String): String {
    return type().toKotlinEmpty(c, derived)
}


fun <T : AttributeI> T.toKotlinTypeSingle(c: GenerationContext, api: String): String {
    return """${c.n(type(), api)}"""
}

fun <T : AttributeI> T.toKotlinTypeDef(c: GenerationContext, api: String): String {
    return """${type().multi().ifElse(
            { "MutableList<${c.n(type(), api)}>" },
            { c.n(type(), api) })}${nullable().then("?")}"""
}

fun <T : CompilationUnitI> T.toKotlinEmptyObject(c: GenerationContext, derived: String): String {
    return """

    companion object {
        val EMPTY = ${c.n(this, derived)}()
    }"""
}

fun <T : TypeI> T.toKotlinIfNative(c: GenerationContext, derived: String): String? {
    return when (this) {
        n.String -> "String"
        n.Boolean -> "Boolean"
        n.Int -> "Int"
        n.Long -> "Long"
        n.Float -> "Float"
        n.Date -> c.n(Java.util.Date)
        n.TimeUnit -> c.n(Java.util.concurrent.TimeUnit)
        n.Path -> c.n(Java.nioFile.Path)
        n.Text -> "String"
        n.Blob -> "ByteArray"
        n.Exception -> "Exception"
        n.Error -> "Throwable"
        n.Void -> "Unit"
        else -> {
            if (this is Lambda) operation().toKotlinLamnda(c, derived) else null
        }
    }
}

fun TypeI.toKotlinGenericTypes(c: GenerationContext, derived: String): String {
    return """${generics().joinWrappedToString(", ", "", "<", ">") { "${it.type().toKotlin(c, derived)}" }}"""
}

fun GenericI.toKotlin(c: GenerationContext, derived: String): String {
    return c.n(type(), derived)
}

fun TypeI.toKotlinGenerics(c: GenerationContext, derived: String): String {
    return """${generics().joinWrappedToString(", ", "", "<", ">") { "${it.toKotlin(c, derived)}" }}"""
}

fun TypeI.toKotlinGenericsClassDef(c: GenerationContext, derived: String): String {
    return """${generics().joinWrappedToString(", ", "", "<", ">") { "${it.name()} : ${it.type().toKotlin(c, derived)}" }}"""
}

fun TypeI.toKotlinGenericsMethodDef(c: GenerationContext, derived: String): String {
    return """${generics().joinWrappedToString(", ", "", "<", "> ") { "${it.name()} : ${it.type().toKotlin(c, derived)}" }}"""
}

fun TypeI.toKotlinGenericsStar(context: GenerationContext, derived: String): String {
    return """${generics().joinWrappedToString(", ", "", "<", "> ") { "*" }}"""
}

fun OperationI.toKotlinGenerics(c: GenerationContext, derived: String): String {
    return """${generics().joinWrappedToString(", ", "", "<", "> ") { "${it.name()} : ${it.type().toKotlin(c, derived)}" }}"""
}

fun <T : TypeI> T.toKotlin(c: GenerationContext, derived: String, attr: AttributeI? = findParent(AttributeI::class.java)): String {
    return toKotlinIfNative(c, derived) ?: "${c.n(this, derived)}${this.toKotlinGenericTypes(c, derived)}"
}

fun <T : AttributeI> T.toKotlinValue(c: GenerationContext, derived: String): String {
    if (value() != null) {
        return when (type()) {
            n.String, n.Text -> "\"${value()}\""
            n.Boolean, n.Int, n.Long, n.Float, n.Date, n.Path, n.Blob, n.Void -> "${value()}"
            else -> {
                if (value() is Literal) {
                    val lit = value() as Literal
                    "${(lit.parent() as EnumTypeI).toKotlin(c, derived)}.${lit.toKotlin()}"
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

fun <T : AttributeI> T.toKotlinSignature(c: GenerationContext, derived: String, api: String): String {
    return "${name()}: ${toKotlinTypeDef(c, api)}${toKotlinInit(c, derived, api)}"
}

fun <T : AttributeI> T.toKotlinMember(c: GenerationContext, derived: String, api: String): String {
    return "${replaceable().ifElse("var ", "val ")}${toKotlinSignature(c, derived, api)}"
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
    fun ${toKotlinGenerics(c, derived)}${name()}(${
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
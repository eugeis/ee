package ee.lang.gen.kt

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.java.j

fun <T : TypeI<*>> T.toKotlinDefault(c: GenerationContext, derived: String, mutable: Boolean? = null): String {
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
        n.Map -> (mutable.setAndTrue()).ifElse("hashMapOf()", "emptyMap()")
        n.List -> (mutable.setAndTrue()).ifElse("mutableListOf()", "listOf()")
        else -> {
            if (baseType is EnumLiteralI) {
                "${(baseType.findParent(EnumTypeI::class.java) as EnumTypeI<*>).toKotlin(c, derived,
                        mutable)}.${baseType.toKotlin()}"
            } else if (baseType is EnumTypeI<*>) {
                "${c.n(this, derived)}.${baseType.literals().first().toKotlin()}"
            /*} else if (baseType is CompilationUnitI<*>) {
                "${c.n(this, derived)}()"
                */
            } else {
                (this.parent() == n).ifElse("\"\"", { "${c.n(this, derived)}.EMPTY" })
            }
        }
    }
}

fun <T : AttributeI<*>> T.toKotlinDefault(c: GenerationContext, derived: String, mutable: Boolean? = isMutable()): String {
    return type().toKotlinDefault(c, derived, mutable)
}

fun <T : AttributeI<*>> T.toKotlinEMPTY(c: GenerationContext, derived: String): String {
    return (type().parent() == n).ifElse({ type().toKotlinDefault(c, derived, isMutable()) },
            { "${c.n(type(), derived)}.EMPTY" })
}


fun <T : AttributeI<*>> T.toKotlinTypeSingle(c: GenerationContext, api: String): String {
    return type().toKotlin(c, api, isMutable())
}

fun <T : AttributeI<*>> T.toKotlinTypeDef(c: GenerationContext, api: String, mutable: Boolean? = isMutable()): String =
        type().toKotlinTypeDef(c, api, isNullable(), mutable)

fun <T : TypeI<*>> T.toKotlinTypeDef(c: GenerationContext, api: String, nullable: Boolean, mutable: Boolean? = null): String =
        """${toKotlin(c, api, mutable)}${nullable.then("?")}"""

fun <T : CompilationUnitI<*>> T.toKotlinEmptyObject(c: GenerationContext, derived: String): String {
    return """
    companion object {
        val EMPTY = ${c.n(this, derived)}()
    }"""
}

fun <T : AttributeI<*>> T.toKotlinCompanionObjectName(c: GenerationContext): String {
    return """        val ${name().toUnderscoredUpperCase()} = "_${name()}""""
}

fun <T : TypeI<*>> T.toKotlinExtends(c: GenerationContext, derived: String, api: String): String {
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

fun <T : TypeI<*>> T.toKotlinIfNative(c: GenerationContext, derived: String,
                                      mutable: Boolean? = null): String? {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String -> "String"
        n.Boolean -> "Boolean"
        n.Int -> "Int"
        n.Long -> "Long"
        n.Float -> "Float"
        n.Double -> "Double"
        n.Date -> c.n(j.util.Date)
        n.TimeUnit -> c.n(j.util.concurrent.TimeUnit)
        n.Path -> c.n(j.nio.file.Path)
        n.Text -> "String"
        n.Blob -> "ByteArray"
        n.Exception -> "Exception"
        n.Error -> "Throwable"
        n.Void -> "Unit"
        n.Url -> c.n(j.net.URL)
        n.List -> "${c.n(
                (mutable.setAndTrue()).ifElse(k.core.MutableList, k.core.List),
                derived)}${toKotlinGenericTypes(c, derived, mutable)}"
        n.Map -> "${c.n((mutable.setAndTrue()).ifElse(k.core.MutableMap, k.core.Map),
                derived)}${toKotlinGenericTypes(c, derived, mutable)}"
        else -> {
            if (this is LambdaI<*>) operation().toKotlinLambda(c, derived) else null
        }
    }
}

fun TypeI<*>.toKotlinGenericTypes(c: GenerationContext, derived: String,
                                  mutable: Boolean? = null): String = generics().joinWrappedToString(", ", "", "<", ">") {
    it.type().toKotlin(c, derived, mutable)
}


fun GenericI<*>.toKotlin(c: GenerationContext, derived: String): String = c.n(type(), derived)

fun TypeI<*>.toKotlinGenerics(c: GenerationContext, derived: String,
                              mutable: Boolean? = null): String = generics().joinWrappedToString(
        ", ", "", "<", ">") { it.toKotlin(c, derived, mutable) }

fun TypeI<*>.toKotlinGenericsClassDef(c: GenerationContext, derived: String,
                                      mutable: Boolean? = null): String = generics().joinWrappedToString(
        ", ", "", "<", ">") {
    "${it.name()}: ${it.type().toKotlin(c, derived, mutable)}"
}

fun TypeI<*>.toKotlinGenericsMethodDef(c: GenerationContext, derived: String,
                                       mutable: Boolean? = null): String = generics().joinWrappedToString(
        ", ", "", "<", "> ") {
    "${it.name()}: ${it.type().toKotlin(c, derived, mutable)}"
}


fun TypeI<*>.toKotlinGenericsStar(context: GenerationContext, derived: String): String = generics().joinWrappedToString(
        ", ", "", "<", "> ") { "*" }

fun OperationI<*>.toKotlinGenerics(c: GenerationContext, derived: String): String = generics().joinWrappedToString(
        ", ", "", "<", "> ") {
    "${it.name()} : ${it.type().toKotlin(c, derived)}"
}

fun <T : TypeI<*>> T.toKotlin(c: GenerationContext, derived: String,
                              mutable: Boolean? = null): String = toKotlinIfNative(c, derived, mutable)
        ?: "${c.n(this,
                derived)}${this.toKotlinGenericTypes(c, derived, mutable)}"


fun <T : AttributeI<*>> T.toKotlinValue(c: GenerationContext, derived: String, mutable: Boolean? = isMutable()): String {
    if (value() != null) {
        return when (type()) {
            n.String, n.Text -> "\"${value()}\""
            n.Boolean, n.Int, n.Long, n.Float, n.Date, n.Path, n.Blob, n.Void -> "${value()}"
            else -> {
                if (value() is EnumLiteralI<*>) {
                    val lit = value() as EnumLiteralI<*>
                    "${(lit.parent() as EnumTypeI<*>).toKotlin(c, derived, mutable)}.${lit.toKotlin()}"
                } else {
                    "${value()}"
                }
            }
        }
    } else {
        return toKotlinDefault(c, derived, mutable)
    }
}

fun <T : AttributeI<*>> T.toKotlinInit(c: GenerationContext, derived: String,
                                       mutable: Boolean? = isMutable(), nullable: Boolean = isNullable()): String {
    return when {
        value() != null -> " = ${toKotlinValue(c, derived, mutable)}"
        nullable -> " = null"
        isInitByDefaultTypeValue() -> " = ${toKotlinValue(c, derived, mutable)}"
        else -> ""
    }
}

fun <T : AttributeI<*>> T.toKotlinInitMember(c: GenerationContext,
                                             derived: String): String = "this.${name()}${toKotlinInit(c, derived)}"

fun <T : AttributeI<*>> T.toKotlinSignature(c: GenerationContext, derived: String, api: String,
                                            init: Boolean = true): String = "${name()}: ${toKotlinTypeDef(c, api)}${init.then {
    toKotlinInit(c, derived)
}}"

fun <T : AttributeI<*>> T.toKotlinConstructorMember(c: GenerationContext, derived: String, api: String,
                                                    init: Boolean = true): String = "${(isKey() && findParent(EnumType::class.java) != null).then(
        { "@${c.n(k.json.JsonValue)} " })}${externalName().isNullOrEmpty().not().then(
        { "@${c.n(k.json.JsonProperty)}(\"${externalName()}\") " })}${isReplaceable().setAndTrue().ifElse("var ",
        "val ")}${toKotlinSignature(c, derived, api, init)}"

fun <T : AttributeI<*>> T.toKotlinMember(c: GenerationContext, derived: String, api: String,
                                         init: Boolean = true): String = "    ${externalName().isNullOrEmpty().not().then(
        { "@${c.n(k.json.JsonProperty)}(\"${externalName()}\")$nL    " })}${isReplaceable().setAndTrue().ifElse("var ",
        "val ")}${toKotlinSignature(c, derived, api, init)}"

fun List<AttributeI<*>>.toKotlinSignature(c: GenerationContext, derived: String,
                                          api: String): String = joinWrappedToString(", ") { it.toKotlinSignature(c, derived, api) }

fun List<AttributeI<*>>.toKotlinMember(c: GenerationContext, derived: String,
                                       api: String): String = joinWrappedToString(", ") { it.toKotlinSignature(c, derived, api) }

fun <T : ConstructorI<*>> T.toKotlinPrimary(c: GenerationContext, derived: String, api: String,
                                            type: TypeI<*>): String {
    val superUnitParams = superUnit().params()
    return if (isNotEMPTY()) """(${paramsWithOutFixValue().joinWrappedToString(", ", "      ") { param ->
        if (superUnitParams.find { it.name() == param.name() } == null) {
            param.toKotlinConstructorMember(c, derived, api)
        } else {
            param.toKotlinSignature(c, derived, api)
        }
    }})${superUnit().toKotlinCall(c, derived, type.toKotlinExtends(c, derived, api),
            paramsWithFixValue())}""" else type.toKotlinExtends(c, derived, api)
}

fun <T : ConstructorI<*>> T.toKotlin(c: GenerationContext, derived: String, api: String): String {
    return if (isNotEMPTY()) """
    constructor(${paramsWithOutFixValue().joinWrappedToString(", ", "                ") {
        it.toKotlinSignature(c, derived, api)
    }})${superUnit().isNotEMPTY().then {
        superUnit().toKotlinCall(c, derived, (parent() != superUnit().parent()).ifElse(" : super", " : this"),
                paramsWithFixValue())
    }} ${paramsWithOut(superUnit()).joinSurroundIfNotEmptyToString("$nL        ", prefix = "{$nL        ",
            postfix = "$nL    }") {
        it.toKotlinAssign(c)
    }}${(parent() as CompilationUnitI<*>).props().filter { it.isMeta() }.joinSurroundIfNotEmptyToString("$nL        ",
            prefix = "$nL        ") {
        it.toKotlinInitMember(c, derived)
    }}""" else ""
}

fun <T : ConstructorI<*>> T.toKotlinCall(c: GenerationContext, name: String = "this"): String = isNotEMPTY().then {
    " : $name(${toKotlinCallParams(c)})"
}

fun <T : ConstructorI<*>> T.toKotlinCallParams(c: GenerationContext): String = isNotEMPTY().then {
    params().joinWrappedToString(", ") { it.name() }
}

fun <T : AttributeI<*>> T.toKotlinAssign(c: GenerationContext): String = "this.${name()} = ${name()}"

fun <T : LogicUnitI<*>> T.toKotlinCall(c: GenerationContext, derived: String, prefix: String = "",
                                       values: Map<String, AttributeI<*>> = emptyMap()): String = isNotEMPTY().then {
    "$prefix(${params().joinWrappedToString(", ") {
        if (values.containsKey(it.name())) {
            values[it.name()]!!.toKotlinValue(c, derived)
        } else {
            it.name()
        }
    }})"
}


fun <T : LogicUnitI<*>> T.toKotlinCallValue(c: GenerationContext, derived: String): String = isNotEMPTY().then {
    "(${params().joinWrappedToString(", ") {
        it.toKotlinValue(c, derived)
    }})"
}

fun <T : LiteralI<*>> T.toKotlinCallValue(c: GenerationContext, derived: String): String = params().isNotEmpty().then {
    "(${params().joinWrappedToString(", ") {
        it.toKotlinValue(c, derived)
    }})"
}

fun <T : AttributeI<*>> T.toKotlinType(c: GenerationContext, derived: String): String = type().toKotlin(c, derived,
        isMutable())

fun List<AttributeI<*>>.toKotlinTypes(c: GenerationContext, derived: String): String = joinWrappedToString(
        ", ") { it.toKotlinType(c, derived) }

fun <T : OperationI<*>> T.toKotlinLambda(c: GenerationContext, derived: String): String = """(${params().toKotlinTypes(
        c, derived)}) -> ${retFirst().toKotlinType(c, derived)}"""

fun <T : OperationI<*>> T.toKotlinIfc(c: GenerationContext, derived: String, api: String): String {
    return """
    fun ${toKotlinGenerics(c, derived)}${name()}(${params().toKotlinSignature(c, derived,
            api)}) : ${retFirst().toKotlinTypeDef(c, api)}"""
}

fun <T : OperationI<*>> T.toKotlinImpl(c: GenerationContext, derived: String, api: String): String {
    return """
    ${isOpen().then("open ")}fun ${toKotlinGenerics(c, derived)}${name()}(${params().toKotlinSignature(c, derived,
            api)}) : ${retFirst().toKotlinTypeDef(c, api)} {
        throw IllegalAccessException("Not implemented yet.")
    }"""
}

fun <T : CompositeI<*>> T.toKotlinIsEmptyExt(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                             api: String = LangDerivedKind.API): String {
    return """
fun ${c.n(this, api)}?.isEMPTY(): Boolean = (this == null || this == ${c.n(this, derived)}.EMPTY)
fun ${c.n(this, api)}?.isNotEMPTY(): Boolean = !isEMPTY()"""
}
package ee.lang.gen.kt

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.java.j
import ee.lang.gen.java.jackson

private const val wrapInitBySize: Int = 20

fun <T : TypeI<*>> T.toKotlinDefault(c: GenerationContext, derived: String, mutable: Boolean? = null): String {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String, n.Text -> "\"\""
        n.Boolean -> "false"
        n.Short -> "0"
        n.UShort -> "0"
        n.Int -> "0"
        n.UInt -> "0"
        n.Long -> "0L"
        n.ULong -> "0L"
        n.Float -> "0f"
        n.Double -> "0.0"
        n.Date -> "${c.n(j.util.Date)}()"
        n.Path -> "${c.n(j.nio.file.Paths)}.get(\"\")"
        n.Blob -> "ByteArray(0)"
        n.Void -> ""
        n.Error -> "Throwable()"
        n.Exception -> "Exception()"
        n.Url -> "${c.n(j.net.URL)}(\"\")"
        n.Byte -> "0"
        n.UByte -> "0"
        n.Class -> {
            val g = generics().first().type()
            "${if (g.isEMPTY()) "Any" else g.name()}::class.java"
        }
        n.Map -> (mutable.setAndTrue()).ifElse("hashMapOf()", "emptyMap()")
        n.List -> (mutable.setAndTrue()).ifElse("mutableListOf()", "listOf()")
        n.Collection -> (mutable.setAndTrue()).ifElse("mutableListOf()", "listOf()")
        else -> {
            if (baseType is EnumLiteralI) {
                "${(baseType.findParent(EnumTypeI::class.java) as EnumTypeI<*>).toKotlin(c, derived,
                        mutable)}.${baseType.toKotlin()}"
            } else if (baseType is EnumTypeI<*>) {
                "${c.n(this, derived)}.${baseType.literals().first().toKotlin()}"
                /*} else if (baseType is CompilationUnitI<*>) {
                    "${c.n(this, derived)}()"
                    */
            } else if (baseType is LambdaI<*>) {
                baseType.operation().toKotlinLambdaDefault(c, derived)
            } else if (baseType is ExternalTypeI) {
                (parent() == n).ifElse("\"\"") {
                    val macroEmptyInstance = baseType.macroEmptyInstance()
                    if (macroEmptyInstance != null)
                        c.body(macroEmptyInstance, baseType, derived)
                    else
                        baseType.primaryOrFirstConstructor().toKotlinInstance(c, derived, baseType)
                }
            } else if (baseType is TypeI<*> && baseType.isIfc()) {
                (parent() == n).ifElse("\"\"") { "${c.n(this, derived)}EMPTY" }
            } else if (this is GenericI<*>) {
                "throw IllegalAccessException(\"not supported\")"
            } else {
                (parent() == n).ifElse("\"\"") { "${c.n(this, derived)}.EMPTY" }
            }
        }
    }
}

fun <T : AttributeI<*>> T.toKotlinDefault(c: GenerationContext, derived: String,
                                          mutable: Boolean? = isMutable()): String {
    return type().toKotlinDefault(c, derived, mutable)
}

fun <T : AttributeI<*>> T.toKotlinEMPTY(c: GenerationContext, derived: String): String {
    return type().toKotlinDefault(c, derived, isMutable())
}


fun <T : AttributeI<*>> T.toKotlinTypeSingle(c: GenerationContext, api: String): String {
    return type().toKotlin(c, api, isMutable())
}

fun <T : AttributeI<*>> T.toKotlinTypeDef(c: GenerationContext, api: String, mutable: Boolean? = isMutable()): String =
        type().toKotlinTypeDef(c, api, isNullable(), mutable)

fun <T : TypeI<*>> T.toKotlinTypeDef(c: GenerationContext, api: String, nullable: Boolean,
                                     mutable: Boolean? = null): String =
        """${toKotlin(c, api, mutable)}${nullable.then("?")}"""

fun <T : CompilationUnitI<*>> T.toKotlinEmptyObject(c: GenerationContext, derived: String): String {
    return """
    companion object {
        val EMPTY by lazy { ${c.n(this, derived)}() }
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
        n.Short -> "Short"
        n.UShort -> "Short"
        n.Int -> "Int"
        n.UInt -> "Int"
        n.Long -> "Long"
        n.ULong -> "Long"
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
        n.Byte -> "Byte"
        n.UByte -> "Byte"
        n.Collection -> "${c.n(
                (mutable.setAndTrue()).ifElse(k.core.MutableCollection, k.core.Collection),
                derived)}${toKotlinGenericTypes(c, derived)}"
        n.List -> "${c.n(
                (mutable.setAndTrue()).ifElse(k.core.MutableList, k.core.List),
                derived)}${toKotlinGenericTypes(c, derived)}"
        n.Map -> "${c.n((mutable.setAndTrue()).ifElse(k.core.MutableMap, k.core.Map),
                derived)}${toKotlinGenericTypes(c, derived)}"
        else -> {
            if (this is LambdaI<*>) operation().toKotlinLambda(c, derived) else null
        }
    }
}

fun TypeI<*>.toKotlinGenericTypes(c: GenerationContext, derived: String, mutable: Boolean? = null): String =
        generics().joinWrappedToString(", ", "", "<", ">") {
            it.toKotlin(c, derived, mutable)
        }


fun GenericI<*>.toKotlin(c: GenerationContext, derived: String, mutable: Boolean? = null): String =
        type().isNotEMPTY().ifElse({ type().toKotlin(c, derived, mutable) }) { c.n(this, derived) }

fun TypeI<*>.toKotlinGenerics(c: GenerationContext, derived: String,
                              mutable: Boolean? = null): String = generics().joinWrappedToString(
        ", ", "", "<", ">") { it.toKotlin(c, derived, mutable) }

fun TypeI<*>.toKotlinGenericsClassDef(c: GenerationContext, derived: String,
                                      mutable: Boolean? = null): String = generics().joinWrappedToString(
        ", ", "", "<", ">") {
    "${it.name()}${it.type().isNotEMPTY().then { " : ${it.type().toKotlin(c, derived, mutable)}" }}"
}

fun TypeI<*>.toKotlinGenericsMethodDef(c: GenerationContext, derived: String, mutable: Boolean? = null): String =
        generics().joinWrappedToString(", ", "", "<", "> ") {
            "${it.name()}: ${it.type().toKotlin(c, derived, mutable)}"
        }

fun TypeI<*>.toKotlinGenericsStar(context: GenerationContext, derived: String): String =
        generics().joinWrappedToString(", ", "", "<", "> ") { "*" }

fun OperationI<*>.toKotlinGenerics(c: GenerationContext, derived: String): String = generics().joinWrappedToString(
        ", ", "", "<", "> ") {
    "${it.name()}${it.type().isNotEMPTY().then { " : ${it.type().toKotlin(c, derived)}" }}"
}

fun <T : TypeI<*>> T.toKotlin(c: GenerationContext, derived: String, mutable: Boolean? = null): String =
        toKotlinIfNative(c, derived, mutable) ?: "${c.n(this, derived)}${toKotlinGenericTypes(c, derived)}"


fun <T : AttributeI<*>> T.toKotlinValue(c: GenerationContext, derived: String, mutable: Boolean? = isMutable(),
                                        value: Any? = value()): String {
    return if (value != null) {
        when (type()) {
            n.String, n.Text -> "\"$value\""
            n.Boolean, n.Int, n.Long, n.Float, n.Date, n.Path, n.Blob, n.Void -> "$value"
            else -> {
                when (value) {
                    is EnumLiteralI<*> ->
                        "${(value.parent() as EnumTypeI<*>).toKotlin(c, derived, mutable)}.${value.toKotlin()}"
                    is TypeI<*> ->
                        value.toKotlin(c, derived, mutable)
                    else ->
                        "$value"
                }
            }
        }
    } else {
        toKotlinDefault(c, derived, mutable)
    }
}

fun <T : AttributeI<*>> T.toKotlinInit(c: GenerationContext, derived: String,
                                       mutable: Boolean? = isMutable(), nullable: Boolean = isNullable(),
                                       wrapIdent: String = "$nL            "): String {
    return when {
        value() != null -> {
            val value = toKotlinValue(c, derived, mutable)
            if (value.length > wrapInitBySize) " = $wrapIdent$value"
            else " = $value"
        }
        nullable -> " = null"
        isInitByDefaultTypeValue() -> {
            val value = toKotlinValue(c, derived, mutable)
            if (value.length > wrapInitBySize) " = $wrapIdent$value"
            else " = $value"
        }
        else -> ""
    }
}

fun <T : AttributeI<*>> T.toKotlinValueInit(c: GenerationContext, derived: String, mutable: Boolean? = isMutable(),
                                            nullable: Boolean = isNullable(),
                                            wrapIdent: String = "$nL            "): String {
    return when {
        value() != null -> toKotlinValue(c, derived, mutable)
        nullable -> "null"
        isInitByDefaultTypeValue() -> toKotlinValue(c, derived, mutable)
        else -> ""
    }
}

fun <T : AttributeI<*>> T.toKotlinInitMember(c: GenerationContext, derived: String): String =
        "this.${name()}${toKotlinInit(c, derived)}"

fun <T : AttributeI<*>> T.toKotlinSignature(c: GenerationContext, derived: String, api: String,
                                            initValue: Boolean = true, forceInit: Boolean = true): String =
        "${name()}: ${toKotlinTypeDef(c, api)}${(forceInit || (initValue && value() != null)).then {
            toKotlinInit(c, derived)
        }}"

fun <T : AttributeI<*>> T.toKotlinConstructorMember(c: GenerationContext, derived: String, api: String,
                                                    initValues: Boolean = true, forceInit: Boolean = true): String =
        "${(isKey() && findParent(EnumType::class.java) != null).then { "@${c.n(jackson.json.JsonValue)} " }}${
        toJsonXmlSupport(c)}${isReplaceable().setAndTrue().ifElse("var ", "val ")}${
        toKotlinSignature(c, derived, api, initValues, forceInit)}"

fun <T : AttributeI<*>> T.toKotlinMember(c: GenerationContext, derived: String, api: String,
                                         initValues: Boolean = true, forceInit: Boolean = true): String =
        "    ${toJsonXmlSupport(c)}${isReplaceable().setAndTrue().ifElse("var ", "val ")}${
        toKotlinSignature(c, derived, api, initValues, forceInit)}"

fun <T : AttributeI<*>> T.toJsonXmlSupport(c: GenerationContext): String = externalName().isNullOrEmpty().not().then {
    "${
    c.jsonSupport.then { "@${c.n(jackson.json.JsonProperty)}(\"${externalName()}\") " }}${
    c.xmlSupport.then { "@${c.n(jackson.xml.JacksonXmlProperty)}(localName = \\\"${externalName()}\\\") \" }" }}"
}

fun List<AttributeI<*>>.toKotlinSignature(c: GenerationContext, derived: String,
                                          api: String, initValues: Boolean = true, forceInit: Boolean = true,
                                          wrapIdentWidth: Int = 6): String =
        joinWrappedToString(", ", wrapIdentWidth.toWrapIdentBlack()) {
            it.toKotlinSignature(c, derived, api, initValues, forceInit)
        }

fun List<AttributeI<*>>.toKotlinMember(c: GenerationContext, derived: String, api: String): String =
        joinWrappedToString(", ") {
            it.toKotlinSignature(c, derived, api)
        }

fun <T : ConstructorI<*>> T.toKotlinPrimary(c: GenerationContext, derived: String, api: String,
                                            type: TypeI<*>, wrapIdentWidth: Int = 6): String {
    val superUnitParams = superUnit().params()
    return if (isNotEMPTY()) """(${paramsWithOutFixValue().joinWrappedToString(", ",
            wrapIndent = (wrapIdentWidth + 1).toWrapIdentBlack()) { param ->
        if (superUnitParams.find { it.name() == param.name() } == null) {
            param.toKotlinConstructorMember(c, derived, api)
        } else {
            param.toKotlinSignature(c, derived, api)
        }
    }})${superUnit().toKotlinCall(c, derived, "$nL   ${type.toKotlinExtends(c, derived, api)}",
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
    }}${(parent() as CompilationUnitI<*>).props().filter { it.isMeta() }
            .joinSurroundIfNotEmptyToString("$nL        ",
                    prefix = "$nL        ") {
                it.toKotlinInitMember(c, derived)
            }}""" else ""
}

fun <T : ConstructorI<*>> T.toKotlinCall(c: GenerationContext, name: String = "this"): String =
        isNotEMPTY().then {
            " : $name(${toKotlinCallParams(c)})"
        }

fun <T : ConstructorI<*>> T.toKotlinInstance(c: GenerationContext, derived: String, type: TypeI<*>): String =
        "${c.n(type, derived)}${toKotlinCallValue(c, derived)}"

fun <T : ConstructorI<*>> T.toKotlinCallParams(c: GenerationContext): String = isNotEMPTY().then {
    params().joinWrappedToString(", ") { it.name() }
}

fun <T : AttributeI<*>> T.toKotlinAssign(c: GenerationContext): String = "this.${name()} = ${name()}"

fun <T : LogicUnitI<*>> T.toKotlinCall(c: GenerationContext, derived: String, prefix: String = "",
                                       values: Map<String, AttributeI<*>> = emptyMap()): String = isNotEMPTY().then {
    "$prefix(${params().joinWrappedToString(", ", (prefix.length).toWrapIdentBlack()) {
        if (values.containsKey(it.name())) {
            values[it.name()]!!.toKotlinValue(c, derived)
        } else {
            it.name()
        }
    }})"
}


fun <T : LogicUnitI<*>> T.toKotlinCallValue(c: GenerationContext, derived: String,
                                            externalVariables: Map<String, String> = emptyMap()): String =
        "(${params().joinWrappedToString(", ") {
            if (externalVariables.containsKey(it.name())) {
                externalVariables[it.name()]!!
            } else {
                it.toKotlinValue(c, derived)
            }
        }})"

fun <T : LiteralI<*>> T.toKotlinCallValue(c: GenerationContext, derived: String): String = params().isNotEmpty().then {
    "(${params().joinWrappedToString(", ") {
        it.toKotlinValue(c, derived)
    }})"
}

fun <T : AttributeI<*>> T.toKotlinType(c: GenerationContext, derived: String): String = type().toKotlin(c, derived,
        isMutable())

fun List<AttributeI<*>>.toKotlinTypes(c: GenerationContext, derived: String): String = joinWrappedToString(
        ", ") { it.toKotlinType(c, derived) }

fun <T : OperationI<*>> T.toKotlinLambda(c: GenerationContext, derived: String,
                                         nonBlocking: Boolean = isNonBlocking()): String =
        """${nonBlocking.then("suspend ")}(${params().toKotlinTypes(c, derived)}) -> ${
        retFirst().toKotlinType(c, derived)}"""

fun <T : OperationI<*>> T.toKotlinLambdaDefault(c: GenerationContext, derived: String): String = """{${
params().joinWrappedToString(", ", prefix = " ") { "_" }} -> ${retFirst().toKotlinDefault(c, derived)}}"""

fun <T : OperationI<*>> T.toKotlinIfc(c: GenerationContext, derived: String, api: String,
                                      nonBlocking: Boolean = isNonBlocking()): String {
    val opPrefix = """    ${nonBlocking.then("suspend ")}fun ${toKotlinGenerics(c, derived)}${name()}("""
    return """
$opPrefix${
    params().toKotlinSignature(c, derived, api, true, false, opPrefix.length)})${
    retFirst().isNotEMPTY().then { """ : ${retFirst().toKotlinTypeDef(c, api)}""" }}"""
}

fun <T : OperationI<*>> T.toKotlinImpl(c: GenerationContext, derived: String, api: String,
                                       nonBlocking: Boolean = isNonBlocking()): String {
    val opPrefix = """    ${nonBlocking.then("suspend ")}${isOpen().then("open ")}fun ${
    toKotlinGenerics(c, derived)}${name()}("""
    return """
$opPrefix${params().toKotlinSignature(c, derived, api, wrapIdentWidth = opPrefix.length)}): ${
    retFirst().toKotlinTypeDef(c, api)} {
        throw IllegalAccessException("Not implemented yet.")
    }"""
}

fun <T : OperationI<*>> T.toKotlinEMPTY(c: GenerationContext, derived: String, api: String,
                                        nonBlocking: Boolean = isNonBlocking()): String {
    val opPrefix = """    override ${nonBlocking.then("suspend ")}fun ${toKotlinGenerics(c, derived)}${name()}("""
    return """
$opPrefix${
    params().toKotlinSignature(c, derived, api, false, false, opPrefix.length)})${
    retFirst().isNotEMPTY().ifElse({
        """: ${retFirst().toKotlinTypeDef(c, api)}${retFirst().toKotlinInit(c, api)}"""
    }, { " {}" })}"""
}

fun <T : OperationI<*>> T.toKotlinBlockingWrapper(c: GenerationContext, derived: String, api: String): String {
    val opPrefix = "    override fun ${toKotlinGenerics(c, derived)}${name()}("
    return """
$opPrefix${
    params().toKotlinSignature(c, derived, api, false, false, opPrefix.length)}) =
            ${c.n(k.coroutines.runBlocking)} {
                nonBlocking.${name()}${toKotlinCall(c, derived)}
            }"""
}

fun <T : CompositeI<*>> T.toKotlinIsEmptyExt(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                             api: String = LangDerivedKind.API): String {
    return """
fun ${c.n(this, api)}?.isEMPTY(): Boolean = (this == null || this == ${c.n(this, derived)}.EMPTY)
fun ${c.n(this, api)}?.isNotEMPTY(): Boolean = !isEMPTY()"""
}
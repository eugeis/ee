package ee.lang.gen.proto

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.java.j

fun String.toProtoPackage(): String {
    return toUnderscoredUpperCase().replace(".", "_")
}

fun <T : MacroCompositeI<*>> T.toProtoMacrosBefore(c: GenerationContext, derived: String, api: String): String =
        macrosBefore().joinToString("$nL        ") { c.body(it, this, derived, api) }

fun <T : MacroCompositeI<*>> T.toProtoMacrosBeforeBody(c: GenerationContext, derived: String, api: String): String =
        macrosBeforeBody().joinToString("$nL        ") { c.body(it, this, derived, api) }

fun <T : MacroCompositeI<*>> T.toProtoMacrosBody(c: GenerationContext, derived: String, api: String): String =
        macrosBody().joinToString("$nL        ") { c.body(it, this, derived, api) }

fun <T : MacroCompositeI<*>> T.toProtoMacrosAfterBody(c: GenerationContext, derived: String, api: String): String =
        macrosAfterBody().joinToString("$nL        ") { c.body(it, this, derived, api) }


fun <T : MacroCompositeI<*>> T.toProtoMacrosAfter(c: GenerationContext, derived: String, api: String): String =
        macrosAfter().joinToString("$nL        ") { c.body(it, this, derived, api) }

fun TypeI<*>.toProtoCall(c: GenerationContext, api: String): String =
        c.n(this, api).substringAfterLast(".")

fun <T : AttributeI<*>> T.toProtoDefault(c: GenerationContext, derived: String, parentConstrName: String = ""): String =
        isNullable().ifElse({ "nil" }, {
            type().toProtoDefault(c, derived, this, parentConstrName)
        })

fun <T : AttributeI<*>> T.toProtoTypeDef(c: GenerationContext, api: String): String =
        "${type().toProto(c, api)}${toProtoMacrosAfterBody(c, api, api)}"

fun <T : TypeI<*>> T.toProtoDefault(
        c: GenerationContext, derived: String, attr: AttributeI<*>, parentConstrName: String = ""): String {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String, n.Text -> "\"\""
        n.Boolean -> "false"
        n.Int -> "0"
        n.Long -> "0L"
        n.Float -> "0f"
        n.Date -> proto.time.Now.toProtoCall(c, derived, derived)
        n.Path -> "${c.n(j.nio.file.Paths)}.get(\"\")"
        n.Blob -> "ByteArray(0)"
        n.Void -> ""
        n.Error -> "Throwable()"
        n.Exception -> "Exception()"
        n.Url -> "${c.n(j.net.URL)}(\"\")"
        n.Map -> (attr.isNotEMPTY() && attr.isMutable().setAndTrue()).ifElse("hashMapOf()", "emptyMap()")
        n.List -> (attr.isNotEMPTY() && attr.isMutable().setAndTrue()).ifElse("arrayListOf()", "arrayListOf()")
        else -> {
            if (baseType is LiteralI) {
                baseType.toProtoValue(c, derived)
            } else if (baseType is EnumTypeI) {
                "${c.n(this, derived)}.${baseType.literals().first().toProtoValue(c, derived)}"
            } else if (baseType is TypeI<*>) {
                toProtoInstance(c, derived, derived, parentConstrName)
            } else {
                (this.parent() == n).ifElse("\"\"", { "${c.n(this, derived)}.EMPTY" })
            }
        }
    }
}


fun <T : LogicUnitI<*>> T.toProtoCallValueByPropName(
        c: GenerationContext, derived: String, api: String,
        saltIntName: String, parentConstrName: String = ""): String =
        if (isNotEMPTY()) {
            val logicUnitName = c.n(this, derived)
            """$logicUnitName(${params().nonDefaultAndWithoutValueAndNonDerived()
                    .toProtoCallValueByPropName(c, api, saltIntName, parentConstrName)})"""
        } else ""

fun List<AttributeI<*>>.toProtoCallValueByPropName(
        c: GenerationContext, api: String, saltIntName: String, parentConstrName: String = ""): String =

        joinWrappedToString(", ", "        ") {
            it.toProtoValueByPropName(c, api, saltIntName, parentConstrName)
        }

fun <T : AttributeI<*>> T.toProtoValueByPropName(
        c: GenerationContext, derived: String, saltIntName: String, parentConstrName: String = ""): String {
    val baseType = type().findDerivedOrThis()
    val ret = when (baseType) {
        n.String, n.Text, n.Any -> name().quotes()
        n.Boolean -> "false"
        n.Int -> saltIntName
        n.Long -> saltIntName
        n.Float -> "float32($saltIntName)"
        n.Double -> "float64($saltIntName)"
        n.Date -> "${c.n(proto.gee.PtrTime)}(${proto.time.Now.toProtoCall(c, derived, derived)})"
        n.Path -> "\"/\""
        n.Blob -> "[]byte(${name().quotes()})"
        n.Void -> ""
        n.Error -> "nil"
        n.Exception -> "nil"
        n.Url -> "${c.n(j.net.URL)}(\"\")"
        n.UUID -> proto.google.uuid.New.toProtoCall(c, derived, derived)
        n.Map -> "make(map[${type().generics()[0].toProto(c, derived)}]${type().generics()[1].toProto(c, derived)})"
        n.List -> "[]${type().generics().first().type().toProto(c, derived)}{}"
        else -> {
            if (baseType is LiteralI) {
                baseType.toProtoValue(c, derived)
            } else if (baseType is EnumTypeI) {
                baseType.literals().first().toProtoValue(c, derived)
            } else if (baseType is TypeI<*>) {
                type().findByNameOrPrimaryOrFirstConstructorFull(parentConstrName)
                        .toProtoCallValueByPropName(c, derived, derived, saltIntName, parentConstrName)
            } else {
                (this.parent() == n).ifElse("\"\"", { "${c.n(this, derived)}.EMPTY" })
            }
        }
    }
    return ret
}

fun <T : LiteralI<*>> T.toProtoValue(c: GenerationContext, derived: String): String =
        "${(findParentMust(EnumTypeI::class.java).toProtoCall(c, derived))}s().${toProto()}()"

fun <T : TypeI<*>> T.toProtoIfNative(c: GenerationContext, derived: String): String? {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String, n.Path, n.Text -> "string"
        n.Boolean -> "bool"
        n.Int -> "sint32"
        n.Long -> "sfixed64"
        n.UInt -> "uint32"
        n.ULong -> "fixed64"
        n.Float -> "float"
        n.Double -> "double"
        n.Date -> proto.time.Time.toProto(c, derived)
        n.TimeUnit -> proto.time.Time.toProto(c, derived)
        n.Blob -> "bytes"
        n.Exception, n.Error -> "error"
        n.Void -> ""
        n.Any -> "bytes"
        n.Url -> c.n(j.net.URL)
        n.UUID -> c.n(proto.google.uuid.UUID)
        n.List -> "repeated ${generics()[0].toProto(c, derived)}"
        n.Map -> "map<${generics()[0].toProto(c, derived)}, ${generics()[1].toProto(c, derived)}>"
        else -> {
            if (this is LambdaI<*>) operation().toProtoLambda(c, derived) else null
        }
    }
}

fun GenericI<*>.toProto(c: GenerationContext, derived: String): String = type().toProto(c, derived)

fun <T : TypeI<*>> T.toProto(c: GenerationContext, derived: String): String =
        toProtoIfNative(c, derived) ?: c.n(this, derived)

fun List<AttributeI<*>>.toProtoSignature(c: GenerationContext, api: String): String =
        joinWrappedToString(", ") {
            it.toProtoSignature(c, api)
        }

fun <T : AttributeI<*>> T.toProtoSignature(c: GenerationContext, api: String): String =
        isAnonymous().ifElse({ type().props().filter { !it.isMeta() }.toProtoSignature(c, api) }, {
            "${name()} ${toProtoTypeDef(c, api)}"
        })

fun <T : AttributeI<*>> T.toProtoCall(c: GenerationContext, api: String): String =
        isAnonymous().ifElse({ type().props().filter { !it.isMeta() }.toProtoCall(c, api) }, {
            name()
        })

fun <T : AttributeI<*>> T.toProtoMember(c: GenerationContext, api: String): String =
        "    ${toProtoTypeDef(c, api)} ${nameForProtoMember()}"

fun OperationI<*>.toProtoReturns(c: GenerationContext, api: String = LangDerivedKind.API): String =
        if (returns().isNotEmpty()) {
            if (isErr()) {
                returns().joinSurroundIfNotEmptyToString(", ", "(", ", err error)") {
                    it.toProtoSignature(c, api)
                }
            } else {
                returns().joinSurroundIfNotEmptyToString(", ", "(", ")") {
                    it.toProtoSignature(c, api)
                }
            }
        } else {
            if (isErr()) "(err error)" else ""
        }

fun List<AttributeI<*>>.toProtoCall(c: GenerationContext, api: String): String =
        joinWrappedToString(", ") { it.toProtoCall(c, api) }

fun <T : LogicUnitI<*>> T.toProtoCall(c: GenerationContext, derived: String, api: String): String =
        if (isNotEMPTY()) """${c.n(this, derived)}(${params().nonDefaultAndWithoutValueAndNonDerived().toProtoCall(c,
                api)})""" else ""

fun <T : TypeI<*>> T.toProtoInstance(
        c: GenerationContext, derived: String, api: String, parentConstrName: String = ""): String {
    val constructor = findByNameOrPrimaryOrFirstConstructorFull(parentConstrName)
    return if (constructor.isNotEMPTY()) {
        constructor.toProtoCall(c, derived, api)
    } else {
        "&${c.n(this, derived)}{}"
    }
}

fun <T : AttributeI<*>> T.toProtoType(c: GenerationContext, derived: String): String = type().toProto(c, derived)

fun List<AttributeI<*>>.toProtoTypes(c: GenerationContext, derived: String): String =
        joinWrappedToString(", ") { it.toProtoType(c, derived) }

fun <T : OperationI<*>> T.toProtoLambda(c: GenerationContext, derived: String): String =
        """func (${params().toProtoTypes(c, derived)}) ${toProtoReturns(c, derived)}"""

fun <T : LogicUnitI<*>> T.toProtoName(): String = isVisible().ifElse({ name().capitalize() }, { name().decapitalize() })

fun <T : OperationI<*>> T.toProtoImpl(o: String, c: GenerationContext, api: String): String {
    return hasMacros().then {
        """${toProtoMacrosBefore(c, api, api)}
func (o *$o) ${toProtoName()}(${params().toProtoSignature(c, api)}) ${toProtoReturns(c, api)}{${toProtoMacrosBeforeBody(c, api,
                api)}${toProtoMacrosBody(c, api, api)}${toProtoMacrosAfterBody(c, api, api)}${
        (returns().isNotEmpty() || isErr()).then {
            """
    return"""
        }}
}${toProtoMacrosAfter(c, api, api)}"""
    }
}


fun <T : TypeI<*>> T.toProtoDoc(ident: String = ""): String {
    return if (doc().isNotEMPTY()) {
        """$ident/**
$ident${doc().name()}       
$ident */
"""
    } else {
        ""
    }
}
package ee.lang.gen.go

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.java.j
import java.util.*


fun <T : MacroCompositeI<*>> T.toGoMacrosBefore(c: GenerationContext, derived: String, api: String): String =
    macrosBefore().joinToString("$nL        ") { c.body(it, this, derived, api) }

fun <T : MacroCompositeI<*>> T.toGoMacrosBeforeBody(c: GenerationContext, derived: String, api: String): String =
    macrosBeforeBody().joinToString("$nL        ") { c.body(it, this, derived, api) }

fun <T : MacroCompositeI<*>> T.toGoMacrosBody(c: GenerationContext, derived: String, api: String): String =
    macrosBody().joinToString("$nL        ") { c.body(it, this, derived, api) }

fun <T : MacroCompositeI<*>> T.toGoMacrosAfterBody(c: GenerationContext, derived: String, api: String): String =
    macrosAfterBody().joinToString("$nL        ") { c.body(it, this, derived, api) }


fun <T : MacroCompositeI<*>> T.toGoMacrosAfter(c: GenerationContext, derived: String, api: String): String =
    macrosAfter().joinToString("$nL        ") { c.body(it, this, derived, api) }

fun AttributeI<*>.toGoInitVariables(c: GenerationContext, derived: String, parentConstrName: String = ""): String {
    val name = "${name().replaceFirstChar { it.lowercase(Locale.getDefault()) }} := "
    return name + if (isDefault() || value() != null) {
        toGoValue(c, derived, parentConstrName)
    } else {
        name()
    }
}

fun AttributeI<*>.toGoInitVariablesExplodedAnonymous(
    c: GenerationContext,
    derived: String,
    parentConstrName: String = ""
): String {
    val name = "${name().replaceFirstChar { it.lowercase(Locale.getDefault()) }} := "
    return name + if (isDefault() || value() != null) {
        toGoValue(c, derived, parentConstrName)
    } else if (isAnonymous()) {
        type().toGoInstance(c, derived, derived, parentConstrName)
    } else {
        name()
    }
}

fun TypeI<*>.toGoCall(c: GenerationContext, api: String): String = c.n(this, api).substringAfterLast(".")

fun <T : AttributeI<*>> T.toGoDefault(c: GenerationContext, derived: String, parentConstrName: String = ""): String =
    isNullable().ifElse({ "nil" }, {
        type().toGoDefault(c, derived, this, parentConstrName)
    })

fun <T : AttributeI<*>> T.toGoValue(c: GenerationContext, derived: String, parentConstrName: String = ""): String {
    return if (value() != null) {
        when (type()) {
            n.String, n.Text -> "\"${value()}\""
            n.Boolean, n.Int, n.Long, n.Float, n.Date, n.Path, n.Blob, n.Void -> "${value()}"
            else -> {
                if (value() is LiteralI<*>) {
                    val lit = value() as LiteralI<*>
                    lit.toGoValue(c, derived)
                } else {
                    "${value()}"
                }
            }
        }
    } else {
        toGoDefault(c, derived, parentConstrName)
    }
}

fun AttributeI<*>.toGoInitForConstructor(c: GenerationContext, derived: String): String {
    val name = "${isAnonymous().ifElse({ type().toGoCall(c, derived) }, { nameForGoMember() })}: "
    return name + if (isDefault() || value() != null) {
        toGoValue(c, derived)
    } else if (isAnonymous()) {
        type().toGoInstance(c, derived, derived)
    } else {
        name()
    }
}

fun AttributeI<*>.toGoInitForConstructorEnum(c: GenerationContext, derived: String): String {
    val name = "${isAnonymous().ifElse({ type().toGoCall(c, derived) }, { nameForGoMember().replaceFirstChar {
        it.lowercase(
            Locale.getDefault()
        )
    } })}: "
    return name + if (isDefault() || value() != null) {
        toGoValue(c, derived)
    } else if (isAnonymous()) {
        type().toGoInstance(c, derived, derived)
    } else {
        name()
    }
}

fun AttributeI<*>.toGoInitForConstructorFunc(c: GenerationContext, derived: String): String =
    "${isAnonymous().ifElse({ type().toGoCall(c, derived) }, { nameForGoMember() })}: ${name().replaceFirstChar {
        it.lowercase(
            Locale.getDefault()
        )
    }}"

fun <T : AttributeI<*>> T.toGoTypeDef(c: GenerationContext, api: String): String =
    "${type().toGo(c, api)}${toGoMacrosAfterBody(c, api, api)}"

fun <T : TypeI<*>> T.toGoDefault(
    c: GenerationContext, derived: String, attr: AttributeI<*>, parentConstrName: String = ""
): String {
    return when (val baseType = findDerivedOrThis()) {
        n.String, n.Text -> "\"\""
        n.Boolean -> "false"
        n.Int -> "0"
        n.Long -> "0L"
        n.Float -> "0f"
        n.Date -> g.time.Now.toGoCall(c, derived, derived)
        n.Path -> "${c.n(j.nio.file.Paths)}.get(\"\")"
        n.Blob -> "ByteArray(0)"
        n.Void -> ""
        n.Error -> "Throwable()"
        n.Exception -> "Exception()"
        n.Url -> "${c.n(j.net.URL)}(\"\")"
        n.Map -> (attr.isNotEMPTY() && attr.isMutable().setAndTrue()).ifElse("hashMapOf()", "emptyMap()")
        n.List -> (attr.isNotEMPTY() && attr.isMutable().setAndTrue()).ifElse("arrayListOf()", "arrayListOf()")
        else -> {
            when(baseType) {
                is LiteralI -> baseType.toGoValue(c, derived)
                is EnumTypeI -> "${c.n(this, derived)}.${baseType.literals().first().toGoValue(c, derived)}"
                is TypeI<*> -> toGoInstance(c, derived, derived, parentConstrName)
                else -> (this.parent() == n).ifElse("\"\"") { "${c.n(this, derived)}.EMPTY" }
            }
        }
    }
}


fun <T : LogicUnitI<*>> T.toGoCallValueByPropName(
    c: GenerationContext, derived: String, api: String,
    saltIntName: String, parentConstrName: String = ""
): String =
    if (isNotEMPTY()) {
        val logicUnitName = c.n(this, derived)
        """$logicUnitName(${
            params().nonDefaultAndNonDerived()
                .toGoCallValueByPropName(c, api, saltIntName, parentConstrName)
        })"""
    } else ""

fun List<AttributeI<*>>.toGoCallValueByPropName(
    c: GenerationContext, api: String, saltIntName: String, parentConstrName: String = ""
): String =

    joinWrappedToString(", ", "        ") {
        it.toGoValueByPropName(c, api, saltIntName, parentConstrName)
    }

fun <T : AttributeI<*>> T.toGoValueByPropName(
    c: GenerationContext, derived: String, saltIntName: String, parentConstrName: String = ""
): String {
    val baseType = type().findDerivedOrThis()
    val ret = when (baseType) {
        n.String, n.Text, n.Any -> "${c.n(g.fmt.Sprintf)}(\"${name().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }} %v\", $saltIntName)"
        n.Boolean -> "false"
        n.Byte -> saltIntName
        n.Int -> saltIntName
        n.Long -> saltIntName
        n.Short -> saltIntName
        n.UShort -> "ushort($saltIntName)"
        n.UInt -> "uint32($saltIntName)"
        n.ULong -> "uint64($saltIntName)"
        n.Float -> "float32($saltIntName)"
        n.Double -> "float64($saltIntName)"
        n.Date -> "${c.n(g.gee.PtrTime)}(${g.time.Now.toGoCall(c, derived, derived)})"
        n.Path -> "\"/\""
        n.Blob -> "[]byte(${c.n(g.fmt.Sprintf)}(\"${name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} %v\", $saltIntName))"
        n.Void -> ""
        n.Error -> "nil"
        n.Exception -> "nil"
        n.Url -> "${c.n(j.net.URL)}(\"\")"
        n.UUID -> g.google.uuid.New.toGoCall(c, derived, derived)
        n.Map -> "make(map[${type().generics()[0].toGo(c, derived)}]${type().generics()[1].toGo(c, derived)})"
        n.List -> "[]${type().generics().first().type().toGo(c, derived)}{}"
        else -> {
            if (baseType is LiteralI) {
                baseType.toGoValue(c, derived)
            } else if (baseType is EnumTypeI) {
                baseType.literals().first().toGoValue(c, derived)
            } else if (baseType is TypeI<*>) {
                type().findByNameOrPrimaryOrFirstConstructorFull(parentConstrName)
                    .toGoCallValueByPropName(c, derived, derived, saltIntName, parentConstrName)
            } else {
                (this.parent() == n).ifElse("\"\"") { "${c.n(this, derived)}.EMPTY" }
            }
        }
    }
    return ret
}


//"${(baseType.findParent(EnumTypeI::class.java) as EnumTypeI<*>).toGo(c, derived)}s().${baseType.toGo()}()"
fun <T : LiteralI<*>> T.toGoValue(c: GenerationContext, derived: String): String =
    "${(findParentMust(EnumTypeI::class.java).toGoCall(c, derived))}s().${toGo()}()"

fun <T : TypeI<*>> T.toGoIfNative(c: GenerationContext, derived: String): String? {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String, n.Path, n.Text -> "string"
        n.Boolean -> "bool"
        n.Int, n.Long -> "int"
        n.UShort -> "ushort"
        n.UInt -> "uint32"
        n.ULong -> "uint64"
        n.Float -> "float32"
        n.Double -> "float64"
        n.Date -> g.time.Time.toGo(c, derived)
        n.TimeUnit -> g.time.Time.toGo(c, derived)
        n.Blob -> "[]byte"
        n.Exception, n.Error -> "error"
        n.Void -> ""
        n.Any -> "interface{}"
        n.Url -> c.n(j.net.URL)
        n.UUID -> c.n(g.google.uuid.UUID)
        n.List -> "[]${generics()[0].toGo(c, derived)}"
        n.Map -> "map[${generics()[0].toGo(c, derived)}]${generics()[1].toGo(c, derived)}"
        else -> {
            if (this is LambdaI<*>) operation().toGoLambda(c, derived) else null
        }
    }
}

fun <T : TypeI<*>> T.toGoNilOrEmpty(c: GenerationContext): String {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String, n.UUID -> "\"\""
        else -> {
            "nil"
        }
    }
}

fun GenericI<*>.toGo(c: GenerationContext, derived: String): String = type().toGo(c, derived)

fun <T : TypeI<*>> T.toGo(c: GenerationContext, derived: String): String =
    toGoIfNative(c, derived) ?: "${(isIfc()).not().then("*")}${c.n(this, derived)}"

fun OperationI<*>.toGoIfc(c: GenerationContext, api: String = LangDerivedKind.API): String {
    return """
	${name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(${params().toGoSignature(c, api)}) ${toGoReturns(c, api)}"""
}

fun List<AttributeI<*>>.toGoSignature(c: GenerationContext, api: String): String =
    joinWrappedToString(", ") {
        it.toGoSignature(c, api)
    }

fun <T : AttributeI<*>> T.toGoSignatureExplodeAnonymous(c: GenerationContext, api: String): String =
    isAnonymous().ifElse({ type().props().filter { !it.isMeta() }.toGoSignature(c, api) }, {
        "${name()} ${toGoTypeDef(c, api)}"
    })

fun <T : AttributeI<*>> T.toGoSignature(c: GenerationContext, api: String): String =
    "${name()} ${toGoTypeDef(c, api)}"

fun <T : AttributeI<*>> T.toGoCallExplodeAnonymous(c: GenerationContext, api: String): String =
    isAnonymous().ifElse({ type().props().filter { !it.isMeta() }.toGoCall(c, api) }, {
        name()
    })

fun <T : AttributeI<*>> T.toGoCall(c: GenerationContext, api: String): String =
    name()

fun <T : AttributeI<*>> T.toGoMember(c: GenerationContext, api: String): String =
    isAnonymous().ifElse({ "    ${toGoTypeDef(c, api)}" }, { "    ${nameForGoMember()} ${toGoTypeDef(c, api)}" })

fun <T : AttributeI<*>> T.toGoYamlJsonEhTags(): String {
    val replaceable = isReplaceable()
    return (replaceable != null && !replaceable).ifElse({ "" },
        { """ `json:"${externalName() ?: name().replaceFirstChar { it.lowercase(Locale.getDefault()) }},omitempty" yaml:"${externalName()?.toHyphenLowerCase() ?: name().toHyphenLowerCase()},omitempty" eh:"optional"`""" })
}

fun <T : AttributeI<*>> T.toGoEnumMember(c: GenerationContext, api: String): String =
    isAnonymous().ifElse({ "    ${toGoTypeDef(c, api)}" }, { "    ${nameDecapitalize()} ${toGoTypeDef(c, api)}" })

fun OperationI<*>.toGoReturns(c: GenerationContext, api: String = LangDerivedKind.API): String =
    if (returns().isNotEmpty()) {
        if (isErr()) {
            returns().joinSurroundIfNotEmptyToString(", ", "(", ", err error)") {
                it.toGoSignature(c, api)
            }
        } else {
            returns().joinSurroundIfNotEmptyToString(", ", "(", ")") {
                it.toGoSignature(c, api)
            }
        }
    } else {
        if (isErr()) "(err error)" else ""
    }

fun List<AttributeI<*>>.toGoCall(c: GenerationContext, api: String): String =
    joinWrappedToString(", ") { it.toGoCall(c, api) }

fun <T : ConstructorI<*>> T.toGo(c: GenerationContext, derived: String, api: String): String {
    val type = findParentMust(CompilationUnitI::class.java)
    val name = c.n(type, derived)
    return if (isNotEMPTY()) """${toGoMacrosBefore(c, derived, api)}
func ${c.n(this, derived)}(${
        params().nonDefaultAndNonDerived().joinWrappedToString(
            ", ",
            "                "
        ) {
            it.toGoSignature(c, api)
        }
    }) (ret *$name${isErrorHandling().then(", err error")}) {${toGoMacrosBeforeBody(c, derived, api)}${
        macrosBody().isNotEmpty().ifElse({
            """
    ${toGoMacrosBody(c, derived, api)}"""
        }, {
            """${
                params().defaultAndValueOrInit().joinSurroundIfNotEmptyToString("") {
                    """
    ${it.toGoInitVariables(c, derived, name())}"""
                }
            }
    ret = &$name{${
                paramsForType().joinSurroundIfNotEmptyToString(
                    ",$nL        ", "$nL        ", ",$nL    "
                ) {
                    it.toGoInitForConstructorFunc(c, derived)
                }
            }}"""
        })
    }${toGoMacrosAfterBody(c, derived, api)}
    return
}${toGoMacrosAfter(c, derived, api)}""" else ""
}

fun <T : AttributeI<*>> T.toGoAssign(o: String): String =
    "$o.${isMutable().setAndTrue().ifElse({ name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }, { name().replaceFirstChar { it.lowercase(Locale.getDefault()) } })} = ${name()}"

fun <T : LogicUnitI<*>> T.toGoCall(c: GenerationContext, derived: String, api: String): String =
    if (isNotEMPTY()) """${c.n(this, derived)}(${
        params().nonDefaultAndNonDerived().toGoCall(
            c,
            api
        )
    })""" else ""

fun <T : TypeI<*>> T.toGoInstance(
    c: GenerationContext, derived: String, api: String, parentConstrName: String = ""
): String {
    val constructor = findByNameOrPrimaryOrFirstConstructorFull(parentConstrName)
    return if (constructor.isNotEMPTY()) {
        constructor.toGoCall(c, derived, api)
    } else {
        "&${c.n(this, derived)}{}"
    }
}

fun <T : AttributeI<*>> T.toGoType(c: GenerationContext, derived: String): String = type().toGo(c, derived)

fun List<AttributeI<*>>.toGoTypes(c: GenerationContext, derived: String): String =
    joinWrappedToString(", ") { it.toGoType(c, derived) }

fun <T : OperationI<*>> T.toGoLambda(c: GenerationContext, derived: String): String =
    """func (${params().toGoTypes(c, derived)}) ${toGoReturns(c, derived)}"""

fun <T : LogicUnitI<*>> T.toGoName(): String = isVisible().ifElse({ name().replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(
        Locale.getDefault()
    ) else it.toString()
} }, { name().replaceFirstChar {
    it.lowercase(
        Locale.getDefault()
    )
} })

fun <T : OperationI<*>> T.toGoImpl(o: String, c: GenerationContext, api: String): String {
    return hasMacros().then {
        """${toGoMacrosBefore(c, api, api)}
func (o *$o) ${toGoName()}(${params().toGoSignature(c, api)}) ${toGoReturns(c, api)}{${
            toGoMacrosBeforeBody(
                c, api,
                api
            )
        }${toGoMacrosBody(c, api, api)}${toGoMacrosAfterBody(c, api, api)}${
            (returns().isNotEmpty() || isErr()).then {
                """
    return"""
            }
        }
}${toGoMacrosAfter(c, api, api)}"""
    }
}

fun CompilationUnitI<*>.toGoPropAnonymousInit(
    c: GenerationContext, derived: String, api: String, prefix: String ="",
) = propsAnonymous().joinSurroundIfNotEmptyToString(", ", prefix = prefix) { prop ->
    "${prop.type().name()}: ${prop.type().primaryOrFirstConstructorOrFull().toGoCall(c, derived, api)}"
}
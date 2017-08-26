package ee.lang.gen.go

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.java.j


fun <T : MacroCompositeI> T.toGoMacrosBefore(c: GenerationContext, derived: String, api: String): String {
    return macrosBefore().joinToString("$nL        ") { c.body(it, this, derived, api) }
}

fun <T : MacroCompositeI> T.toGoMacrosBeforeBody(c: GenerationContext, derived: String, api: String): String {
    return macrosBeforeBody().joinToString("$nL        ") { c.body(it, this, derived, api) }
}

fun <T : MacroCompositeI> T.toGoMacrosBody(c: GenerationContext, derived: String, api: String): String {
    return macrosBody().joinToString("$nL        ") { c.body(it, this, derived, api) }
}

fun <T : MacroCompositeI> T.toGoMacrosAfterBody(c: GenerationContext, derived: String, api: String): String {
    return macrosAfterBody().joinToString("$nL        ") { c.body(it, this, derived, api) }
}

fun <T : MacroCompositeI> T.toGoMacrosAfter(c: GenerationContext, derived: String, api: String): String {
    return macrosAfter().joinToString("$nL        ") { c.body(it, this, derived, api) }
}

fun AttributeI.toGoInitVariables(c: GenerationContext, derived: String): String {
    val name = "${name()}:= "
    return name + if (default() || value() != null) {
        toGoValue(c, derived)
    } else if (anonymous()) {
        type().toGoInstance(c, derived, derived)
    } else {
        name()
    }
}

fun TypeI.toGoCall(c: GenerationContext, api: String): String {
    return c.n(this, api).substringAfterLast(".")
}

fun <T : AttributeI> T.toGoDefault(c: GenerationContext, derived: String): String {
    return nullable().ifElse({ "nil" }, { type().toGoDefault(c, derived, this) })
}

fun <T : AttributeI> T.toGoValue(c: GenerationContext, derived: String): String {
    if (value() != null) {
        return when (type()) {
            n.String, n.Text -> "\"${value()}\""
            n.Boolean, n.Int, n.Long, n.Float, n.Date, n.Path, n.Blob, n.Void -> "${value()}"
            else -> {
                if (value() is Literal) {
                    val lit = value() as Literal
                    "${(lit.parent() as EnumTypeI).toGo(c, derived)}.${lit.toGo()}"
                } else {
                    "${value()}"
                }
            }
        }
    } else {
        return toGoDefault(c, derived)
    }
}

fun AttributeI.toGoInitForConstructor(c: GenerationContext, derived: String): String {
    val name = "${anonymous().ifElse({ type().toGoCall(c, derived) }, { nameForMember() })}: "
    return name + if (default() || value() != null) {
        toGoValue(c, derived)
    } else if (anonymous()) {
        type().toGoInstance(c, derived, derived)
    } else {
        name()
    }
}

fun AttributeI.toGoInitForConstructorFunc(c: GenerationContext, derived: String): String {
    return "${anonymous().ifElse({ type().toGoCall(c, derived) }, { nameForMember() })}: ${name()}"
}

fun <T : AttributeI> T.toGoTypeDef(c: GenerationContext, api: String): String {
    return "${type().toGo(c, api)}${toGoMacrosAfterBody(c, api, api)}"
}

fun <T : TypeI> T.toGoDefault(c: GenerationContext, derived: String, attr: AttributeI): String {
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
                "${(baseType.findParent(EnumTypeI::class.java) as EnumTypeI).toGo(c, derived)}.${baseType.toGo()}"
            } else if (baseType is EnumTypeI) {
                "${c.n(this, derived)}.${baseType.literals().first().toGo()}"
            } else if (baseType is TypeI) {
                toGoInstance(c, derived, derived)
            } else {
                (this.parent() == n).ifElse("\"\"", { "${c.n(this, derived)}.EMPTY" })
            }
        }
    }
}

fun <T : TypeI> T.toGoIfNative(c: GenerationContext, derived: String): String? {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String, n.Path, n.Text -> "string"
        n.Boolean -> "bool"
        n.Int, n.Long -> "int"
        n.Float -> "float64"
        n.Date -> g.time.Time.toGo(c, derived)
        n.TimeUnit -> g.time.Time.toGo(c, derived)
        n.Blob -> "[]byte"
        n.Exception, n.Error -> "error"
        n.Void -> ""
        n.Any -> "interface{}"
        n.Url -> c.n(j.net.URL)
        n.UUID -> c.n(g.eh.UUID)
        n.List -> "[]${generics()[0].toGo(c, derived)}"
        n.Map -> "map(${generics()[0].toGo(c, derived)})${generics()[1].toGo(c, derived)}"
        else -> {
            if (this is Lambda) operation().toGoLambda(c, derived) else null
        }
    }
}

fun <T : TypeI> T.toGoNilOrEmpty(c: GenerationContext): String? {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String, n.UUID -> "\"\""
        else -> {
            "nil"
        }
    }
}

fun GenericI.toGo(c: GenerationContext, derived: String): String {
    return type().toGo(c, derived)
}

fun <T : TypeI> T.toGo(c: GenerationContext, derived: String): String {
    return toGoIfNative(c, derived) ?: "${ifc().not().then("*")}${c.n(this, derived)}"
}

fun <T : AttributeI> T.toGoSignature(c: GenerationContext, api: String): String {
    return anonymous().ifElse({ type().props().filter { !it.meta() }.toGoSignature(c, api) }, {
        "${name()} ${toGoTypeDef(c, api)}"
    })
}

fun <T : AttributeI> T.toGoCall(c: GenerationContext, api: String): String {
    return anonymous().ifElse({ type().props().filter { !it.meta() }.toGoCall(c, api) }, {
        "${name()}"
    })
}

fun <T : AttributeI> T.toGoMember(c: GenerationContext, api: String): String {
    return anonymous().ifElse({ "    ${toGoTypeDef(c, api)}" }, { "    ${nameForMember()} ${toGoTypeDef(c, api)}" })
}

fun <T : AttributeI> T.toGoEnumMember(c: GenerationContext, api: String): String {
    return anonymous().ifElse({ "    ${toGoTypeDef(c, api)}" }, { "    ${nameForEnum()} ${toGoTypeDef(c, api)}" })
}

fun List<AttributeI>.toGoSignature(c: GenerationContext, api: String): String {
    return joinWrappedToString(", ") { it.toGoSignature(c, api) }
}

fun List<AttributeI>.toGoCall(c: GenerationContext, api: String): String {
    return joinWrappedToString(", ") { it.toGoCall(c, api) }
}

fun <T : ConstructorI> T.toGo(c: GenerationContext, derived: String, api: String): String {
    val type = findParentMust(CompilationUnitI::class.java)
    val name = c.n(type, derived)
    return if (isNotEMPTY()) """${toGoMacrosBefore(c, derived, api)}
func ${c.n(this, derived)}(${params().nonDefaultAndWithoutValueAndNonDerived().joinWrappedToString(", ", "                ") {
        it.toGoSignature(c, api)
    }
    }) (ret *$name) {${toGoMacrosBeforeBody(c, derived, api)}${macrosBody().isNotEmpty().ifElse({
        """
    ${toGoMacrosBody(c, derived, api)}"""
    }, {
        """${params().defaultOrWithValueAndNonDerived().joinSurroundIfNotEmptyToString("") {
            """
    ${it.toGoInitVariables(c, derived)}"""
        }}
    ret = &$name{${paramsForType().joinSurroundIfNotEmptyToString(
                ",$nL        ", "$nL        ", ",$nL    ") {
            it.toGoInitForConstructorFunc(c, derived)
        }}}"""
    })}${toGoMacrosAfterBody(c, derived, api)}
    return
}${toGoMacrosAfter(c, derived, api)}""" else ""
}

fun <T : AttributeI> T.toGoAssign(o: String): String {
    return "$o.${mutable().setAndTrue().ifElse({ name().capitalize() },
            { name().decapitalize() })} = ${name()}"
}

fun <T : LogicUnitI> T.toGoCall(c: GenerationContext, derived: String, api: String): String =
        if (isNotEMPTY()) """${c.n(this, derived)}(${params().filter { !it.default() }.toGoCall(c, api)})""" else ""

fun <T : TypeI> T.toGoInstance(c: GenerationContext, derived: String, api: String): String {
    val constructor = primaryOrFirstConstructor()
    return if (constructor != null) {
        constructor.toGoCall(c, derived, api)
    } else {
        "&${c.n(this, derived)}{}"
    }
}

fun <T : AttributeI> T.toGoType(c: GenerationContext, derived: String): String = type().toGo(c, derived)

fun List<AttributeI>.toGoTypes(c: GenerationContext, derived: String): String {
    return joinWrappedToString(", ") { it.toGoType(c, derived) }
}

fun <T : OperationI> T.toGoLambda(c: GenerationContext, derived: String): String =
        """func (${params().toGoTypes(c, derived)}) ${ret().toGoType(c, derived)}"""

fun <T : LogicUnitI> T.toGoName(): String = visible().ifElse({ name().capitalize() }, { name().decapitalize() })

fun <T : OperationI> T.toGoImpl(o: String, c: GenerationContext, api: String): String {
    return hasMacros().then {
        """${toGoMacrosBefore(c, api, api)}
func (o *$o) ${toGoName()}(${params().toGoSignature(c, api)}) ${
        ret().isNotEMPTY().then { "(ret ${ret().toGoTypeDef(c, api)})" }} {${
        toGoMacrosBeforeBody(c, api, api)}${toGoMacrosBody(c, api, api)}${toGoMacrosAfterBody(c, api, api)}${
        ret().isNotEMPTY().then {
            """
    return"""
        }}
}${toGoMacrosAfter(c, api, api)}"""
    }
}
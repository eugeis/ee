package ee.lang.gen.go

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.java.j
import ee.lang.gen.swagger.*

fun AttributeI.toGoInit(): String {
    return """${this.name()}: ${this.value()}"""
}

fun TypeI.toGoCall(c: GenerationContext, api: String): String {
    return c.n(this, api).substringAfterLast(".")
}

fun AttributeI.toGoInitCall(c: GenerationContext, api: String): String {
    return "${anonymous().ifElse({ type().toGoCall(c, api) }, { nameForMember() })}: ${
    (default() || anonymous()).ifElse({ type().primaryOrFirstConstructor().toGoCall(c, api, api) }, { name() })}"
}

fun <T : AttributeI> T.toGoTypeDef(c: GenerationContext, api: String): String {
    return "${type().toGo(c, api)}${toGoMacros(c, api, api)}"
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

fun <T : AttributeI> T.toGoMember(c: GenerationContext, api: String): String {
    return anonymous().ifElse({ "    ${toGoTypeDef(c, api)}" }, { "    ${nameForMember()} ${toGoTypeDef(c, api)}" })
}

fun <T : AttributeI> T.toGoEnumMember(c: GenerationContext, api: String): String {
    return anonymous().ifElse({ "    ${toGoTypeDef(c, api)}" }, { "    ${nameForEnum()} ${toGoTypeDef(c, api)}" })
}

fun List<AttributeI>.toGoSignature(c: GenerationContext, api: String): String {
    return joinWrappedToString(", ") { it.toGoSignature(c, api) }
}

fun <T : ConstructorI> T.toGo(c: GenerationContext, derived: String, api: String): String {
    val name = c.n(parent(), derived)
    val nonDefaultParams = params().filter { !it.default() && it.derivedAsType().isEmpty() }
    return if (isNotEMPTY()) """
func ${c.n(this, derived)}(${nonDefaultParams.joinWrappedToString(", ", "                ") {
        it.toGoSignature(c, api)
    }
    }) (ret *$name) {
    ret = &$name{${params().joinSurroundIfNotEmptyToString(
            ",$nL        ", "$nL        ", ",$nL    ") { it.toGoInitCall(c, api) }}}${
    toGoMacros(c, api, api)}
    return
}""" else ""
}

fun <T : AttributeI> T.toGoAssign(o: String): String {
    return "$o.${mutable().setAndTrue().ifElse({ name().capitalize() },
            { name().decapitalize() })} = ${name()}"
}

fun <T : LogicUnitI> T.toGoCall(c: GenerationContext, derived: String, api: String): String {
    return if (isNotEMPTY()) """${c.n(this, derived)}(${params().filter { !it.default() }.joinWrappedToString(
            ", ", "                ") { it.name() }})""" else ""
}

fun <T : AttributeI> T.toGoType(c: GenerationContext, derived: String): String = type().toGo(c, derived)

fun List<AttributeI>.toGoTypes(c: GenerationContext, derived: String): String {
    return joinWrappedToString(", ") { it.toGoType(c, derived) }
}

fun <T : OperationI> T.toGoLambda(c: GenerationContext, derived: String): String =
        """func (${params().toGoTypes(c, derived)}) ${ret().toGoType(c, derived)}"""

fun <T : LogicUnitI> T.toGoName(): String = visible().ifElse({ name().capitalize() }, { name().decapitalize() })

fun <T : MacroCompositeI> T.toGoMacros(c: GenerationContext, derived: String, api: String): String {
    return macros().joinToString("$nL        ") { c.body(it, this, derived, api) }
}


fun <T : OperationI> T.toGoImpl(o: String, c: GenerationContext, api: String): String {
    return macros().isNotEmpty().then {
        """
func (o *$o) ${toGoName()}(${params().toGoSignature(c, api)}) (ret ${ret().toGoTypeDef(c, api)}) {
    ${toGoMacros(c, api, api)}
}"""
    }
}
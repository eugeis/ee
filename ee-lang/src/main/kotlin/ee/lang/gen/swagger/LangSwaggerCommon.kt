package ee.lang.gen.swagger

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.go.g
import ee.lang.gen.go.nameForEnum
import ee.lang.gen.go.nameForMember
import ee.lang.gen.java.j

fun AttributeI.toSwaggerInit(): String {
    return """${this.name()}: ${this.value()}"""
}

fun TypeI.toSwaggerCall(c: GenerationContext, api: String): String {
    return c.n(this, api).substringAfterLast(".")
}

fun AttributeI.toSwaggerInitCall(c: GenerationContext, api: String): String {
    return "${anonymous().ifElse({ type().toSwaggerCall(c, api) }, { nameForMember() })}: ${
    (default() || anonymous()).ifElse({ type().primaryOrFirstConstructor().toSwaggerCall(c, api, api) }, { name() })}"
}

fun <T : AttributeI> T.toSwaggerTypeDef(c: GenerationContext, api: String): String {
    return "${type().toSwagger(c, api)}"
}

fun <T : TypeI> T.toSwaggerIfNative(c: GenerationContext, derived: String): String? {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String, n.Path, n.Text -> "string"
        n.Boolean -> "boolean"
        n.Int, n.Long -> "integer"
        n.Float -> "float64"
        n.Date -> g.time.Time.toSwagger(c, derived)
        n.TimeUnit -> g.time.Time.toSwagger(c, derived)
        n.Blob -> "[]byte"
        n.Exception, n.Error -> "error"
        n.Void -> ""
        n.Any -> "interface{}"
        n.Url -> c.n(j.net.URL)
        n.UUID -> c.n(g.eh.UUID)
        n.List -> "[]${generics()[0].toSwagger(c, derived)}"
        n.Map -> "map(${generics()[0].toSwagger(c, derived)})${generics()[1].toSwagger(c, derived)}"
        else -> {
            if (this is Lambda) operation().toSwaggerLamnda(c, derived) else null
        }
    }
}

fun <T : TypeI> T.toSwaggerNilOrEmpty(c: GenerationContext): String? {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String, n.UUID -> "\"\""
        else -> {
            "nil"
        }
    }
}

fun GenericI.toSwagger(c: GenerationContext, derived: String): String {
    return type().toSwagger(c, derived)
}

fun <T : TypeI> T.toSwagger(c: GenerationContext, derived: String): String {
    return toSwaggerIfNative(c, derived) ?: "${ifc().not().then("*")}${c.n(this, derived)}"
}

fun <T : AttributeI> T.toSwaggerSignature(c: GenerationContext, api: String): String {
    return anonymous().ifElse({ type().props().filter { !it.meta() }.toSwaggerSignature(c, api) }, {
        "${name()} ${toSwaggerTypeDef(c, api)}"
    })
}

/*
query:
            type: string
            description: this string will be added to the url of the service
 */
fun <T : AttributeI> T.toSwaggerMember(c: GenerationContext, api: String): String {
    return "    ${nameForMember()}: ${toSwaggerTypeDef(c, api)}"
}

fun <T : AttributeI> T.toSwaggerEnumMember(c: GenerationContext, api: String): String {
    return anonymous().ifElse({ "    ${toSwaggerTypeDef(c, api)}" }, { "    " + nameForEnum() + " " + toSwaggerTypeDef(c, api) })
}

fun List<AttributeI>.toSwaggerSignature(c: GenerationContext, api: String): String {
    return joinWrappedToString(", ") { it.toSwaggerSignature(c, api) }
}

fun <T : ConstructorI> T.toSwagger(c: GenerationContext, derived: String, api: String): String {
    val name = c.n(parent(), derived)
    val nonDefaultParams = params().filter { !it.default() && it.derivedAsType().isEmpty() }
    return if (isNotEMPTY()) """
func ${c.n(this, derived)}(${nonDefaultParams.joinWrappedToString(", ", "                ") {
        it.toSwaggerSignature(c, api)
    }
    }) (ret *$name) {
    ret = &$name{${params().joinSurroundIfNotEmptyToString(
            ",${nL}        ", "${nL}        ", ",${nL}    ") { it.toSwaggerInitCall(c, api) }}}${
    toSwaggerMacros(c, api, api)}
    return
}""" else ""
}

fun <T : AttributeI> T.toSwaggerAssign(o: String): String {
    return "$o.${mutable().setAndTrue().ifElse({ name().capitalize() },
            { name().decapitalize() })} = ${name()}"
}

fun <T : LogicUnitI> T.toSwaggerCall(c: GenerationContext, derived: String, api: String): String {
    return if (isNotEMPTY()) """${c.n(this, derived)}(${params().filter { !it.default() }.joinWrappedToString(
            ", ", "                ") { it.name() }})""" else ""
}

fun <T : AttributeI> T.toSwaggerType(c: GenerationContext, derived: String): String = type().toSwagger(c, derived)
fun List<AttributeI>.toSwaggerTypes(c: GenerationContext, derived: String): String {
    return joinWrappedToString(", ") { it.toSwaggerType(c, derived) }
}

fun <T : OperationI> T.toSwaggerLamnda(c: GenerationContext, derived: String): String =
        """func (${params().toSwaggerTypes(c, derived)}) ${ret().toSwaggerType(c, derived)}"""

fun <T : LogicUnitI> T.toSwaggerName(): String = visible().ifElse({ name().capitalize() }, { name().decapitalize() })
fun <T : MacroCompositeI> T.toSwaggerMacros(c: GenerationContext, derived: String, api: String): String {
    return macros().joinToString("${nL}        ") { c.body(it, this, derived, api) }
}

fun <T : OperationI> T.toSwaggerImpl(o: String, c: GenerationContext, api: String): String {
    return macros().isNotEmpty().then {
        """
func (o *$o) ${toSwaggerName()}(${params().toSwaggerSignature(c, api)}) (ret ${ret().toSwaggerTypeDef(c, api)}) {
    ${toSwaggerMacros(c, api, api)}
}"""
    }
}

fun <T : CompilationUnitI> T.toSwaggerPath(c: GenerationContext,
                                           derived: String = LangDerivedKind.IMPL,
                                           api: String = LangDerivedKind.API): String {
    return """/${c.n(this, derived)}"""
}

fun <T : CompilationUnitI> T.toSwaggerDefinition(c: GenerationContext,
                                                 derived: String = LangDerivedKind.IMPL,
                                                 api: String = LangDerivedKind.API): String {
    val name = c.n(this, derived)
    return """
$name:
    type: object
    properties:${
    props().joinSurroundIfNotEmptyToString(nL, prefix = nL) { it.toSwaggerMember(c, api) }}"""
}
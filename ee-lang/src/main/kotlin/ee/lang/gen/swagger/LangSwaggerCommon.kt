package ee.lang.gen.swagger

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.go.g
import ee.lang.gen.go.nameForEnum
import ee.lang.gen.go.nameForMember
import ee.lang.gen.java.j

fun <T : AttributeI> T.toSwaggerTypeDef(c: GenerationContext, api: String): String {
    return "        type: ${type().toSwagger(c, api)}${type().toSwaggerFormatIfNative(c, api)}"
}

fun <T : TypeI> T.toSwaggerIfNative(c: GenerationContext, derived: String): String? {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String, n.Path, n.Text -> "string"
        n.Boolean -> "boolean"
        n.Int, n.Long -> "integer"
        n.Float -> "number"
        n.Date -> "string"
        n.TimeUnit -> "string"
        n.Blob -> "string"
        n.Exception, n.Error -> "error"
        n.Void -> ""
        n.Any -> "string"
        n.Url -> "string"
        n.UUID -> "string"
        n.List -> "[]${generics()[0].toSwagger(c, derived)}"
        n.Map -> "string"
        else -> {
            if (this is Lambda) operation().toSwaggerLambda(c, derived) else null
        }
    }
}

fun <T : TypeI> T.toSwaggerFormatIfNative(c: GenerationContext, derived: String): String? {
    val baseType = findDerivedOrThis()
    val prefix = "\n        format:"
    return when (baseType) {
        n.Int -> "$prefix int32"
        n.Long -> "$prefix int64"
        n.Float -> "$prefix float"
        n.Date -> "$prefix date-time"
        n.Blob -> "$prefix binary"
        else -> {
            ""
        }
    }
}

fun <T : TypeI> T.toSwagger(c: GenerationContext, derived: String): String {
    return toSwaggerIfNative(c, derived) ?: "\$ref: \"#/definitions/${c.n(this, derived)}\""
}

fun GenericI.toSwagger(c: GenerationContext, derived: String): String {
    return type().toSwagger(c, derived)
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
    return "      ${nameForMember().decapitalize()}:$nL${toSwaggerTypeDef(c, api)}"
}

fun <T : AttributeI> T.toSwaggerEnumMember(c: GenerationContext, api: String): String {
    return anonymous().ifElse({ "    ${toSwaggerTypeDef(c, api)}" }, { "    " + nameForEnum() + " " + toSwaggerTypeDef(c, api) })
}

fun List<AttributeI>.toSwaggerSignature(c: GenerationContext, api: String): String {
    return joinWrappedToString(", ") { it.toSwaggerSignature(c, api) }
}

fun <T : AttributeI> T.toSwaggerType(c: GenerationContext, derived: String): String = type().toSwagger(c, derived)
fun List<AttributeI>.toSwaggerTypes(c: GenerationContext, derived: String): String {
    return joinWrappedToString(", ") { it.toSwaggerType(c, derived) }
}

fun <T : OperationI> T.toSwaggerLambda(c: GenerationContext, derived: String): String =
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
    return """/${c.n(this, derived).toHyphenLowerCase()}"""
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
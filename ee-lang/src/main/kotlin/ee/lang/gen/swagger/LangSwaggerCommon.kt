package ee.lang.gen.swagger

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.go.g
import ee.lang.gen.go.nameForEnum
import ee.lang.gen.go.nameForMember
import ee.lang.gen.java.j

fun <T : AttributeI> T.toSwaggerTypeDef(c: GenerationContext, api: String): String {
    return "        type: ${type().toSwagger(c, api)}"
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
            if (this is Lambda) operation().toSwaggerLambda(c, derived) else null
        }
    }
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
    return "      ${nameForMember()}:$nL${toSwaggerTypeDef(c, api)}"
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
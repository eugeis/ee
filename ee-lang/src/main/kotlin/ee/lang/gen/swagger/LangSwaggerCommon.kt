package ee.lang.gen.swagger

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.go.nameForGoMember

fun <T : AttributeI<*>> T.toSwaggerTypeDef(c: GenerationContext, api: String, indent: String = "        "): String {
    return "$indent${type().toSwagger(c, api, indent)}${type().toSwaggerFormatIfNative(indent)}"
}

fun <T : ItemI<*>> T.toSwaggerDescription(indent: String = "        "): String {
    return doc().isNotEMPTY().then {
        "$nL${indent}description: ${doc().toDsl()}"
    }
}

fun Collection<ItemI<*>>.toSwaggerLiterals(indent: String = "        "): String {
    return joinSurroundIfNotEmptyToString("") { "$nL$indent- ${it.name()}" }
}

fun <T : TypeI<*>> T.toSwaggerIfNative(c: GenerationContext, derived: String, indent: String): String? {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String, n.Path, n.Text -> "string"
        n.Boolean                -> "boolean"
        n.Int, n.Long            -> "integer"
        n.Float                  -> "number"
        n.Date                   -> "string"
        n.TimeUnit               -> "string"
        n.Blob                   -> "string"
        n.Exception, n.Error     -> "error"
        n.Void                   -> ""
        n.Any                    -> "string"
        n.Url                    -> "string"
        n.UUID                   -> "string"
        n.List                   -> "array$nL${indent}items:${generics()[0].toSwagger(c, derived, "$indent  ")}"
        n.Map                    -> "string"
        else                     -> {
            if (this is Lambda) operation().toSwaggerLambda(c, derived, indent) else null
        }
    }
}

fun <T : TypeI<*>> T.toSwaggerFormatIfNative(indent: String = "        "): String {
    val baseType = findDerivedOrThis()
    val prefix = "\n${indent}format:"
    return when (baseType) {
        n.Int   -> "$prefix int32"
        n.Long  -> "$prefix int64"
        n.Float -> "$prefix float"
        n.Date  -> "$prefix date-time"
        n.Blob  -> "$prefix binary"
        else    -> {
            ""
        }
    }
}

fun <T : TypeI<*>> T.toSwagger(c: GenerationContext, derived: String, indent: String): String {
    val nativeType = toSwaggerIfNative(c, derived, indent)
    return """$nL$indent${(nativeType != null).ifElse({ "type: $nativeType" },
        { "\$ref: \"#/components/schemas/${c.n(this, derived)}\"" })}"""
}

fun GenericI<*>.toSwagger(c: GenerationContext, derived: String, indent: String): String {
    return type().toSwagger(c, derived, indent)
}

fun <T : AttributeI<*>> T.toSwaggerSignature(c: GenerationContext, api: String): String {
    return anonymous().ifElse({ type().props().filter { !it.meta() }.toSwaggerSignature(c, api) }, {
        "${name()} ${toSwaggerTypeDef(c, api)}"
    })
}

/*
query:
            type: string
            description: this string will be added to the url of the service
 */
fun <T : AttributeI<*>> T.toSwaggerMember(c: GenerationContext, api: String, indent: String = "        "): String {
    return "$indent${nameForGoMember().decapitalize()}:${toSwaggerTypeDef(c, api, "$indent  ")}"
}

fun <T : AttributeI<*>> T.toSwaggerEnumMember(c: GenerationContext, api: String): String {
    return anonymous().ifElse({ "    ${toSwaggerTypeDef(c, api)}" },
        { "    " + nameDecapitalize() + " " + toSwaggerTypeDef(c, api) })
}

fun List<AttributeI<*>>.toSwaggerSignature(c: GenerationContext, api: String): String {
    return joinWrappedToString(", ") { it.toSwaggerSignature(c, api) }
}

fun <T : AttributeI<*>> T.toSwaggerType(c: GenerationContext, derived: String, indent: String): String =
    type().toSwagger(c, derived, indent)

fun List<AttributeI<*>>.toSwaggerTypes(c: GenerationContext, derived: String, indent: String): String {
    return joinWrappedToString(", ") { it.toSwaggerType(c, derived, indent) }
}

fun <T : OperationI<*>> T.toSwaggerLambda(c: GenerationContext, derived: String, indent: String): String =
    """func (${params().toSwaggerTypes(c, derived, indent)}) ${retFirst().toSwaggerType(c, derived, indent)}"""

fun <T : LogicUnitI<*>> T.toSwaggerName(): String = visible().ifElse({ name().capitalize() }, { name().decapitalize() })

fun <T : CompilationUnitI<*>> T.toSwaggerPath(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
    api: String = LangDerivedKind.API): String {
    return """/${c.n(this, derived).toHyphenLowerCase()}s"""
}

fun <T : CompilationUnitI<*>> T.toSwaggerDefinition(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
    api: String = LangDerivedKind.API): String {
    val name = c.n(this, derived)
    return """
    $name:
      type: object
      properties:${props().joinSurroundIfNotEmptyToString(nL, prefix = nL) { it.toSwaggerMember(c, api) }}"""
}

fun <T : EnumTypeI<*>> T.toSwaggerEnum(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
    api: String = LangDerivedKind.API): String {
    val name = c.n(this, derived)
    return """
    $name:
      type: object
      properties:
        name:
          type: string
          enum:${literals().toSwaggerLiterals("            ")}"""
}
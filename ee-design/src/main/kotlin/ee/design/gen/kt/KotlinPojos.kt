package ee.design.gen.kt

import ee.common.ext.toUnderscoredUpperCase
import ee.design.*

fun LiteralI.toKotlin(): String = "${name().toUnderscoredUpperCase()}"

fun LiteralI.toKotlinIsMethod(): String {
    return """
    fun is${name().capitalize()}() : Boolean = this == ${toKotlin()}"""
}

fun <T : EnumTypeI> T.toKotlinEnum(c: GenerationContext,
                                   derived: String = DerivedNames.API.name,
                                   api: String = DerivedNames.API.name): String {
    val name = c.n(this, derived)
    return """
enum class $name${primaryConstructor().toKotlinPrimary(c, derived, api)} {
    ${literals().joinToString(",$nL    ") { "${it.toKotlin()}${it.toKotlinCallValue(c, derived)}" }};${
    propsExceptPrimaryConstructor().joinToString(nL) { it.toKotlinMember(c, derived, api) }}${
    operations().joinToString(nL) { it.toKotlinImpl(c, derived, api) }}${
    literals().joinToString("", nL) { it.toKotlinIsMethod() }}
}"""
}

fun <T : EnumTypeI> T.toKotlinEnumParserMethod(c: GenerationContext,
                                               derived: String = DerivedNames.API.name): String {
    val name = c.n(this, derived)
    return """
fun String?.to$name(): $name {
    return if (this != null) $name.valueOf(this) else $name.${literals().first().toKotlin()}
}"""
}
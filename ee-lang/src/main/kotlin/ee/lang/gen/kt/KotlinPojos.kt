package ee.lang.gen.kt

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toUnderscoredUpperCase
import ee.lang.*

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

fun <T : EnumTypeI> T.toKotlinEnumParseMethod(c: GenerationContext,
                                               derived: String = DerivedNames.API.name): String {
    val name = c.n(this, derived)
    return """
fun String?.to$name(): $name {
    return if (this != null) $name.valueOf(this) else $name.${literals().first().toKotlin()}
}"""
}

fun <T : CompilationUnitI> T.toKotlinPojo(c: GenerationContext,
                                                derived: String = DerivedNames.IMPL.name,
                                                api: String = DerivedNames.API.name
): String {
    return """
open class ${c.n(this, derived)} : ${c.n(superUnit(), derived)}${(derived != api).then(
            { ", ${c.n(this, DerivedNames.API.name)}" })} {${
    props().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) { it.toKotlinMember(c, derived, api) }}
    constructor(value: ${c.n(this, derived)}.() -> Unit = {}) : super(value as ${c.n(superUnit(), derived)}.() -> Unit)${
    toKotlinEmptyObject(c, derived)}
}"""
}
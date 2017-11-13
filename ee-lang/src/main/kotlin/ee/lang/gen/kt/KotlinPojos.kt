package ee.lang.gen.kt

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toUnderscoredUpperCase
import ee.lang.*

fun LiteralIB<*>.toKotlin(): String = name().toUnderscoredUpperCase()

fun LiteralIB<*>.toKotlinIsMethod(): String {
    return """
    fun is${name().capitalize()}() : Boolean = this == ${toKotlin()}"""
}

fun <T : EnumTypeIB<*>> T.toKotlinEnum(c: GenerationContext,
                                   derived: String = LangDerivedKind.API,
                                   api: String = LangDerivedKind.API): String {
    val name = c.n(this, derived)
    return """
enum class $name${primaryConstructor().toKotlinPrimary(c, derived, api)} {
    ${literals().joinToString(",$nL    ") { "${it.toKotlin()}${it.toKotlinCallValue(c, derived)}" }};${
    propsExceptPrimaryConstructor().joinToString(nL) { it.toKotlinMember(c, derived, api) }}${
    operations().joinToString(nL) { it.toKotlinImpl(c, derived, api) }}${
    literals().joinToString("", nL) { it.toKotlinIsMethod() }}
}"""
}

fun <T : EnumTypeIB<*>> T.toKotlinEnumParseMethod(c: GenerationContext,
                                              derived: String = LangDerivedKind.API): String {
    val name = c.n(this, derived)
    return """
fun String?.to$name(): $name {
    return if (this != null) $name.valueOf(this) else $name.${literals().first().toKotlin()}
}"""
}

fun <T : CompilationUnitIB<*>> T.toKotlinImpl(c: GenerationContext,
                                          derived: String = LangDerivedKind.IMPL,
                                          api: String = LangDerivedKind.API): String {
    return """
${open().then("open ")}class ${c.n(this, derived)}${toKotlinExtends(c, derived, api)}${
    primaryConstructor().toKotlinPrimary(c, derived, api)} {${
    props().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) { it.toKotlinMember(c, derived, api, false) }}${
    otherConstructors().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toKotlin(c, derived, api)
    }}${operations().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toKotlinImpl(c, derived, api)
    }}${
    toKotlinEmptyObject(c, derived)}
}"""
}
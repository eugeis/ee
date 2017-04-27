package ee.lang.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toUnderscoredUpperCase
import ee.lang.*

fun LiteralI.toGo(): String = "${name().toUnderscoredUpperCase()}"

fun LiteralI.toGoIsMethod(): String {
    return """
    fun is${name().capitalize()}() : Boolean = this == ${toGo()}"""
}

fun <T : EnumTypeI> T.toGoEnum(c: GenerationContext,
                                   derived: String = DerivedNames.API.name,
                                   api: String = DerivedNames.API.name): String {
    val name = c.n(this, derived)
    return """
enum class $name${primaryConstructor().toGoPrimary(c, derived, api)} {
    ${literals().joinToString(",$nL    ") { "${it.toGo()}${it.toGoCallValue(c, derived)}" }};${
    propsExceptPrimaryConstructor().joinToString(nL) { it.toGoMember(c, derived, api) }}${
    operations().joinToString(nL) { it.toGoImpl(c, derived, api) }}${
    literals().joinToString("", nL) { it.toGoIsMethod() }}
}"""
}

fun <T : EnumTypeI> T.toGoEnumParseMethod(c: GenerationContext,
                                              derived: String = DerivedNames.API.name): String {
    val name = c.n(this, derived)
    return """
fun String?.to$name(): $name {
    return if (this != null) $name.valueOf(this) else $name.${literals().first().toGo()}
}"""
}

fun <T : CompilationUnitI> T.toGoImpl(c: GenerationContext,
                                          derived: String = DerivedNames.IMPL.name,
                                          api: String = DerivedNames.API.name): String {
    return """
${open().then("open ")}class ${c.n(this, derived)}${toGoExtends(c, derived, api)}${
    primaryConstructor().toGoPrimary(c, derived, api)} {${
    props().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) { it.toGoMember(c, derived, api, false) }}${
    otherConstructors().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toGo(c, derived, api)
    }}${operations().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toGoImpl(c, derived, api)
    }}${
    toGoEmptyObject(c, derived)}
}"""
}
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
type ${name.decapitalize()} struct {
    Name string
    Index int
}${
    primaryConstructor().toGo(c, derived, api)}


${
    literals().joinToString(",$nL    ", "const (", ")") { "${it.toGo()}${it.toGoCallValue(c, derived)}" }}${
    operations().joinToString(nL) { it.toGoImpl(c, derived, api) }}${
    literals().joinToString("", nL) { it.toGoIsMethod() }}


    ${literals().joinToString(",$nL    ") { "${it.toGo()}${it.toGoCallValue(c, derived)}" }};${
    propsExceptPrimaryConstructor().joinToString(nL) { it.toGoMember(c, derived, api) }}${
    operations().joinToString(nL) { it.toGoImpl(c, derived, api) }}${
    literals().joinToString("", nL) { it.toGoIsMethod() }}
func Parse${name}(name string) (ret $name, ok bool) {${
    literals().joinToString("$nL    ", "switch (name) {", "}") { "case \"${it.toGo()}\":$nL    ret = ${it.toGo()}" }}
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
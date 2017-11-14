package ee.lang.gen.ts

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toUnderscoredUpperCase
import ee.lang.*


fun LiteralI<*>.toTypeScript(): String = name().toUnderscoredUpperCase()
fun LiteralI<*>.toTypeScriptIsMethod(): String {
    return """
    is${name().capitalize()}() : boolean {
        return this == ${toTypeScript()};
    }"""
}

fun <T : EnumTypeI<*>> T.toTypeScriptEnum(c: GenerationContext,
                                       derived: String = LangDerivedKind.API,
                                       api: String = LangDerivedKind.API): String {
    val name = c.n(this, derived)
    return """
enum $name {
    ${literals().joinToString(",${nL}    ") { "${it.toTypeScript()}${it.toTypeScriptCallValue(c, derived)}" }}
}"""
/*
;${
    props().joinToString(nL) { it.toTypeScriptMember(c, derived, api) }}${
    constructors().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toTypeScript(c, derived, api)
    }}${
    operations().joinToString(nL) { it.toTypeScriptImpl(c, derived, api) }}${
    literals().joinToString("", nL) { it.toTypeScriptIsMethod() }}
}"""*/
}

fun <T : EnumTypeI<*>> T.toTypeScriptEnumParseMethod(c: GenerationContext,
                                                  derived: String = LangDerivedKind.API): String {
    val name = c.n(this, derived)
    return """
fun String?.to$name(): $name {
    return if (this != null) $name.valueOf(this) else $name.${literals().first().toTypeScript()};
}"""
}

fun <T : CompilationUnitI<*>> T.toTypeScriptImpl(c: GenerationContext,
                                              derived: String = LangDerivedKind.IMPL,
                                              api: String = LangDerivedKind.API): String {
    return """
${open().then("export ")}class ${c.n(this, derived)}${toTypeScriptExtends(c, derived, api)} {${
    props().filter { !it.meta() }.joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toTypeScriptMember(c, derived, api, false)
    }}${
    constructors().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toTypeScript(c, derived, api)
    }}${operations().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toTypeScriptImpl(c, derived, api)
    }}
}"""
}
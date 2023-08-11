package ee.lang.gen.ts

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toUnderscoredUpperCase
import ee.lang.*
import java.util.*


fun LiteralI<*>.toTypeScript(): String = name().toUnderscoredUpperCase()
fun LiteralI<*>.toTypeScriptIsMethod(): String {
    return """is${name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}() : boolean {
        return this == ${toTypeScript()};
}"""
}

fun <T : EnumTypeI<*>> T.toTypeScriptEnum(c: GenerationContext, derived: String = LangDerivedKind.API,
                                          api: String = LangDerivedKind.API): String {
    val name = c.n(this, derived)
    return """${isOpen().then("export ")}enum $name {
    ${literals().joinToString(",${nL}    ") { "${it.toTypeScript()}${it.toTypeScriptCallValue(c, derived)}" }}
}"""
    /*
    ;${
        paramsNotDerived().joinToString(nL) { it.toTypeScriptMember(c, derived, api) }}${
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
    return """fun String?.to$name(): $name {
    return if (this != null) $name.valueOf(this) else $name.${literals().first().toTypeScript()};
}"""
}

fun <T : CompilationUnitI<*>> T.toTypeScriptImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                 api: String = LangDerivedKind.API): String {
    return """${isOpen().then("export ")}class ${c.n(this, LangDerivedKind.WithParentAsName)}${toTypeScriptExtends(c, derived,
        api)} {${props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        it.toTypeScriptMember(c, derived, api, false, tab)
    }}${constructors().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        it.toTypeScript(c, derived, api)
    }}${props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        when(it.type()) {
            is BasicI<*> -> it.type().props().filter { basicProperty -> !basicProperty.isMeta() }.joinSurroundIfNotEmptyToString(nL, prefix = nL) { basicProperty -> basicProperty.toTypeScriptFindBy(c, derived, api, it.name(), this) }
            else -> it.toTypeScriptFindBy(c, derived, api)
        }
    }}
    ${operations().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        it.toTypeScriptImpl(c, derived, api)
    }}
    ${this.toTypeScriptTooltipFunction(c, derived, api)}
}"""
}

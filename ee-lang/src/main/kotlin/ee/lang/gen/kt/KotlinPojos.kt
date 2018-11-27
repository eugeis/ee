package ee.lang.gen.kt

import ee.common.ext.ifElse
import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.joinWrappedToString
import ee.common.ext.then
import ee.lang.*

fun LiteralI<*>.toKotlin(): String = name().capitalize()

fun LiteralI<*>.toKotlinIsMethod(): String {
    return """
    fun is${name().capitalize()}(): Boolean = this == ${toKotlin()}"""
}

fun <T : EnumTypeI<*>> T.toKotlinEnum(c: GenerationContext, derived: String = LangDerivedKind.API,
                                      api: String = LangDerivedKind.API): String {
    val name = c.n(this, derived)
    return """
enum class $name${primaryOrFirstConstructor().toKotlinPrimary(c, derived, api, this)} {
    ${literals().joinToString(",$nL    ") {
        "${it.toKotlin()}${it.toKotlinCallValue(c, derived)}"
    }};${propsExceptPrimaryConstructor().joinToString(nL) {
        it.toKotlinMember(c, derived, api)
    }}${operationsWithoutDataTypeOperations().joinToString(nL) { it.toKotlinImpl(c, derived, api) }}${
    literals().joinToString("",
            nL) { it.toKotlinIsMethod() }}
}"""
}

fun <T : EnumTypeI<*>> T.toKotlinEnumParseMethod(c: GenerationContext, derived: String = LangDerivedKind.API): String {
    val name = c.n(this, derived)
    return """
fun String?.to$name(): $name {
    return $name.values().find { this == null || it.name.equals(this, true) } ?: $name.${literals().first().toKotlin()}
}"""
}

fun <T : CompilationUnitI<*>> T.toKotlinIfc(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                            api: String = LangDerivedKind.API): String {
    return """
interface ${c.n(this, api)}${toKotlinGenericsClassDef(c, derived)}${superUnits().joinWrappedToString(
            ", ", prefix = " : ") { "${c.n(it, api)}" }}${operationsWithoutDataTypeOperations()
            .joinSurroundIfNotEmptyToString(nL, prefix = " {$nL", postfix = "$nL}") {
                it.toKotlinIfc(c, derived, api)
            }}"""
}

fun <T : CompilationUnitI<*>> T.toKotlinIfcEMPTY(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                 api: String = LangDerivedKind.API): String {
    return """
${generics().isEmpty().ifElse({ "object ${c.n(this, derived)}EMPTY" }) {
        "class ${c.n(this, derived)}EMPTY${
        toKotlinGenericsClassDef(c, derived)}"
    }} : ${c.n(this, derived)}${toKotlinGenerics(c, derived)}${operationsWithInherited()
            .joinSurroundIfNotEmptyToString(nL, prefix = " {$nL", postfix = "$nL}") {
                it.toKotlinEMPTY(c, derived, api)
            }}"""
}

fun <T : CompilationUnitI<*>> T.toKotlinImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                             api: String = LangDerivedKind.API,
                                             dataClass: Boolean = this is BasicI<*> &&
                                                     superUnits().isEmpty() && superUnitFor().isEmpty()): String {
    return """
${(isOpen() && !dataClass).then("open ")}${dataClass.then("data ")}class ${
    toKotlinGenericsClassDef(c, derived)}${c.n(this, derived)}${
    primaryConstructor().toKotlinPrimary(c, derived, api,
            this)} {${propsWithoutParamsOfPrimaryConstructor().joinSurroundIfNotEmptyToString(nL, prefix = nL,
            postfix = nL) {
        it.toKotlinMember(c, derived, api, false)
    }}${otherConstructors().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toKotlin(c, derived, api)
    }}${operationsWithoutDataTypeOperations().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toKotlinImpl(c, derived, api)
    }}${toKotlinEmptyObject(c, derived)}
}"""
}
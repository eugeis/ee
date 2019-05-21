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
    val typePrefix = """enum class $name"""
    return """
$typePrefix${primaryOrFirstConstructor().toKotlinPrimary(c, derived, api, this, typePrefix.length)} {
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
                                            api: String = LangDerivedKind.API,
                                            itemName: String = c.n(this, api),
                                            nonBlocking: Boolean = isNonBlocking()): String {
    return """
interface $itemName${toKotlinGenericsClassDef(c, derived)}${superUnits().joinWrappedToString(
            ", ", prefix = " : ") { c.n(it, api) }}${operationsWithoutDataTypeOperations()
            .joinSurroundIfNotEmptyToString(nL, prefix = " {$nL", postfix = "$nL}") {
                it.toKotlinIfc(c, derived, api, nonBlocking || it.isNonBlocking())
            }}"""
}

fun <T : CompilationUnitI<*>> T.toKotlinEMPTY(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                              api: String = LangDerivedKind.API,
                                              itemName: String = c.n(this, derived),
                                              nonBlocking: Boolean = isNonBlocking()): String {
    return """
${generics().isEmpty().ifElse({ "object ${itemName}EMPTY" }) {
        "class ${itemName}EMPTY${
        toKotlinGenericsClassDef(c, derived)}"
    }} : $itemName${toKotlinGenerics(c, derived)}${operationsWithInherited()
            .joinSurroundIfNotEmptyToString(nL, prefix = " {$nL", postfix = "$nL}") {
                it.toKotlinEMPTY(c, derived, api, nonBlocking || it.isNonBlocking())
            }}"""
}

fun <T : CompilationUnitI<*>> T.toKotlinBlockingWrapper(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                        api: String = LangDerivedKind.API): String {
    val itemName = c.n(this, derived)
    return """
${generics().isEmpty().ifElse({ "class ${itemName}BlockingWrapper" }) {
        "class ${itemName}BlockingWrapper${
        toKotlinGenericsClassDef(c, derived)}"
    }}(private val nonBlocking: $itemName) : ${itemName}Blocking${toKotlinGenerics(c, derived)}${operationsWithInherited()
            .joinSurroundIfNotEmptyToString(nL, prefix = " {$nL", postfix = "$nL}") {
                it.toKotlinBlockingWrapper(c, derived, api)
            }}"""
}

fun <T : CompilationUnitI<*>> T.toKotlinImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                             api: String = LangDerivedKind.API,
                                             itemName: String = c.n(this, derived),
                                             dataClass: Boolean = this is BasicI<*> &&
                                                     superUnits().isEmpty() && superUnitFor().isEmpty(),
                                             nonBlocking: Boolean = isNonBlocking()): String {
    val typePrefix = """${(isOpen() && !dataClass).then("open ")}${dataClass.then("data ")}class $itemName${
    toKotlinGenericsClassDef(c, derived)}"""
    return """
$typePrefix${
    primaryConstructor().toKotlinPrimary(c, derived, api, this, typePrefix.length)} {${
    propsWithoutParamsOfPrimaryConstructor().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toKotlinMember(c, derived, api, false)
    }}${otherConstructors().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toKotlin(c, derived, api)
    }}${operationsWithoutDataTypeOperations().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toKotlinImpl(c, derived, api, nonBlocking || it.isNonBlocking())
    }}${toKotlinEmptyObject(c, derived)}
}"""
}
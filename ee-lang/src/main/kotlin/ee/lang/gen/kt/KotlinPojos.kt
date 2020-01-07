package ee.lang.gen.kt

import ee.common.ext.ifElse
import ee.common.ext.joinSurroundIfNotEmptyToString
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
    return """${toKotlinDoc()}
$typePrefix${primaryOrFirstConstructor().toKotlinPrimaryAndExtends(c, derived, api, this)} {
    ${literals().joinToString(",$nL    ") {
        "${it.toKotlin()}${it.toKotlinCallValue(c, derived)}"
    }};${propsExceptPrimaryConstructor().joinToString(nL) {
        it.toKotlinMember(c, derived, api)
    }}${operationsWithoutDataTypeOperations().joinToString(nL) {
        it.toKotlinImpl(c, derived, api, isNonBlock(it))
    }}${
    literals().joinToString("", nL) { it.toKotlinIsMethod() }}
}"""
}

fun <T : EnumTypeI<*>> T.toKotlinEnumParseMethod(c: GenerationContext, derived: String = LangDerivedKind.API): String {
    val name = c.n(this, derived)
    return """
fun String?.to$name(orInstance: $name = $name.${literals().first().toKotlin()}): $name {
    return $name.values().find { 
        this == null || it.name.equals(this, true) 
    } ?: orInstance
}"""
}

fun <T : CompilationUnitI<*>> T.toKotlinIfc(
        c: GenerationContext, derived: String = LangDerivedKind.IMPL,
        api: String = LangDerivedKind.API,
        itemName: String = c.n(this, api), nonBlock: Boolean? = null): String {
    return """${toKotlinDoc()}
interface $itemName${toKotlinGenerics(c, derived)}${toKotlinExtends(c, derived, api)}${
    operationsWithoutDataTypeOperations()
            .joinSurroundIfNotEmptyToString(nL, prefix = " {$nL", postfix = "$nL}") {
                it.toKotlinIfc(c, derived, api, nonBlock.notNullValueElse(isNonBlock(it)))
            }}"""
}

fun <T : CompilationUnitI<*>> T.toKotlinEMPTY(
        c: GenerationContext, derived: String = LangDerivedKind.IMPL,
        api: String = LangDerivedKind.API,
        itemName: String = c.n(this, derived), nonBlock: Boolean? = null): String {
    val generics = toKotlinGenerics(c, derived)
    val extendsEMPTY = if (generics.isNotEmpty()) toKotlinExtendsEMPTY(c, derived, api) else ""

    val allOperations = generics.isNotEmpty().ifElse(operations(), operationsWithInherited())
    return """${generics.isEmpty().ifElse("object", "open class")} ${itemName}EMPTY$generics$extendsEMPTY${
    extendsEMPTY.isEmpty().ifElse(" : ", ", ")}$itemName$generics${
    allOperations.joinSurroundIfNotEmptyToString(nL, prefix = " {$nL", postfix = "$nL}") {
        it.toKotlinEMPTY(c, derived, api, nonBlock.notNullValueElse(isNonBlock(it)))
    }}"""
}

fun <T : CompilationUnitI<*>> T.toKotlinBlockingWrapper(
        c: GenerationContext, derived: String = LangDerivedKind.IMPL, api: String = LangDerivedKind.API): String {

    val itemName = c.n(this, derived)

    return """${toKotlinDoc()}
${generics().isEmpty().ifElse({ "class ${itemName}BlockingWrapper" }) {
        "class ${itemName}BlockingWrapper${toKotlinGenerics(c, derived)}"
    }}(
        val api: $itemName${toKotlinGenerics(c, derived)}, 
        val scope: ${c.n(k.coroutines.CoroutineScope)} = ${c.n(k.coroutines.GlobalScope)}) 
    : ${itemName}Blocking${toKotlinGenerics(c, derived)}, ${c.n(k.coroutines.CoroutineScope)} {
    
    override val coroutineContext: ${c.n(k.coroutines.CoroutineContext)}
        get() = scope.coroutineContext${operationsWithInherited()
            .joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
                it.toKotlinBlockingWrapper(c, derived, api, isNonBlock(it))
            }}
}"""
}

fun <T : CompilationUnitI<*>> T.toKotlinImpl(
        c: GenerationContext, derived: String = LangDerivedKind.IMPL,
        api: String = LangDerivedKind.API,
        itemName: String = c.n(this, derived),
        dataClass: Boolean = this is BasicI<*> && superUnits().isEmpty() && superUnitFor().isEmpty()): String {
    val typePrefix = """${(isOpen() && !dataClass).then("open ")}${dataClass.then("data ")}class $itemName${
    toKotlinGenerics(c, derived)}"""
    return """${toKotlinDoc()}
$typePrefix${
    primaryConstructor().toKotlinPrimaryAndExtends(c, derived, api, this)} {${
    propsWithoutParamsOfPrimaryConstructor().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toKotlinMember(c, derived, api, false)
    }}${otherConstructors().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toKotlin(c, derived, api)
    }}${operationsWithoutDataTypeOperations().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toKotlinImpl(c, derived, api, isNonBlock(it))
    }}${toKotlinEqualsHashcode(c, derived)}${toKotlinToString()}${toKotlinEmptyObject(c, derived)}
}"""
}
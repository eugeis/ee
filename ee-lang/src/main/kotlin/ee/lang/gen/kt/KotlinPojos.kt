package ee.lang.gen.kt

import ee.common.ext.ifElse
import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toUnderscoredUpperCase
import ee.lang.*

fun LiteralI<*>.toKotlin(): String = name().toUnderscoredUpperCase()

fun LiteralI<*>.toKotlinIsMethod(): String {
    return """
    fun is${name().capitalize()}(): Boolean = this == ${toKotlin()}"""
}

fun <T : EnumTypeI<*>> T.toKotlinEnum(
        c: GenerationContext, derived: String = LangDerivedKind.API,
        api: String = LangDerivedKind.API): String {

    val externalNameNeeded = isKotlinExternalNameNeeded()
    val externalNameProp = propExternalName()

    val enumConst = if (externalNameNeeded) {
        Constructor {
            name("Full")
            primary()
            parent(this)
            params(externalNameProp, *propsAllNoMeta().toTypedArray())
            namespace(namespace())
        }.init()
    } else {
        primaryOrFirstConstructorOrFull().init()
    }

    val name = c.n(this, derived)
    val typePrefix = """enum class $name"""

    return """${toKotlinDoc()}
$typePrefix${enumConst.toKotlinEnum(c, derived, api)} {
    ${literals().joinToString(",$nL    ") { lit ->
        "${lit.toKotlin()}${
        if (externalNameNeeded) {
            lit.toKotlinCallValue(c, derived, mutableListOf(p(externalNameProp) {
                value(lit.externalName() ?: lit.name())
            }))
        } else {
            lit.toKotlinCallValue(c, derived)
        }}"
    }};${propsExceptPrimaryConstructor().joinToString(nL) {
        it.toKotlinMember(c, derived, api)
    }}${operationsWithoutDataTypeOperations().joinToString(nL) {
        it.toKotlinImpl(c, derived, api, isNonBlock(it))
    }}${
    literals().joinToString("", nL) { it.toKotlinIsMethod() }}
}"""
}

fun propExternalName() = p("externalName").key().init()

fun <T : EnumTypeI<*>> T.isKotlinExternalNameNeeded() =
        literals().find {
            val literName = it.toKotlin()
            val externalName = it.externalName()
            literName != it.name() || (externalName != null && externalName != literName)
        } != null

fun <T : EnumTypeI<*>> T.toKotlinEnumParseMethod(
        c: GenerationContext, derived: String = LangDerivedKind.API): String {

    val externalNameParse = if (isKotlinExternalNameNeeded()) {
        "$nL$nL${toKotlinEnumParseMethodByProp(c, derived, propExternalName())}"
    } else ""

    return """${toKotlinEnumParseMethodByProp(c, derived, p("name").init())}${
    externalNameParse}${props().joinSurroundIfNotEmptyToString(nL, "$nL$nL") {
        toKotlinEnumParseMethodByProp(c, derived, it)
    }}"""
}

fun <T : EnumTypeI<*>> T.toKotlinEnumParseMethodByProp(
        c: GenerationContext, derived: String = LangDerivedKind.API, prop: AttributeI<*>): String {
    val name = c.n(this, derived)
    return """fun ${prop.type().toKotlin(c, derived)}?.to${name}${prop.toByName()}(orInstance: $name = $name.${literals().first().toKotlin()}): $name {
    return $name.values().find { 
        this == null || it.${prop.name()}.equals(this, true) 
    } ?: orInstance
}"""
}

fun AttributeI<*>.toByName() = """By${name().capitalize()}"""

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
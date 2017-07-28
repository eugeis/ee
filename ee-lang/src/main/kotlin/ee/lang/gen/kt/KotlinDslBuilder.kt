package ee.lang.gen.kt

import ee.common.ext.ifElse
import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toUnderscoredUpperCase
import ee.lang.*

fun <T : AttributeI> T.toKotlinDslTypeDef(c: GenerationContext, api: String): String {
    return """${multi().ifElse({ "${c.n(l.ListMultiHolder, api)}<${c.n(type(), api)}>" }, { c.n(type(), api) })}${nullable().then("?")}"""
}

fun <T : AttributeI> T.toKotlinDslBuilderMethodsI(c: GenerationContext, api: String,
                                                  parent: ItemI = findParent(CompilationUnitI::class.java) ?: parent()): String {
    val value = name().equals("value").ifElse("aValue", "value")
    return """
    fun ${name()}(): ${toKotlinDslTypeDef(c, api)}${multi().ifElse({
        """
    fun ${name()}(vararg $value: ${toKotlinTypeSingle(c, api)}): ${c.n(parent, api)}"""
    }, {
        """
    fun ${name()}($value: ${toKotlinDslTypeDef(c, api)}): ${c.n(parent, api)}"""
    })}${nonFluent().isNotBlank().then {
        """
    fun ${nonFluent()}($value: ${toKotlinTypeSingle(c, api)}): ${toKotlinTypeSingle(c, api)}
    fun ${nonFluent()}($value: ${toKotlinTypeSingle(c, api)}.() -> Unit = {}) : ${toKotlinTypeSingle(c, api)}"""
    }}"""
}

fun <T : AttributeI> T.toKotlinDslBuilderMethods(c: GenerationContext, derived: String, api: String,
                                                 parent: ItemI = findParent(CompilationUnitI::class.java) ?: parent()): String {
    val value = name().equals("value").ifElse("aValue", "value")
    val override = (derived != api).ifElse("override ", "")
    return """${multi().ifElse({
        """
    ${override}fun ${name()}(): ${toKotlinDslTypeDef(c, api)} = itemAsList(${
        name().toUnderscoredUpperCase()}, ${toKotlinTypeSingle(c, api)}::class.java, true, true)
    ${override}fun ${name()}(vararg $value: ${toKotlinTypeSingle(c, api)}): ${c.n(parent, api)} = apply { ${name()}().addItems(value.asList()) }"""
    }, {
        """
    ${override}fun ${name()}(): ${toKotlinDslTypeDef(c, api)} = attr(${name().toUnderscoredUpperCase()}${
        nullable().not().then { ", { ${(value() == null || value().toString().isEmpty()).ifElse(toKotlinEmpty(c, derived), value())} }" }})
    ${override}fun ${name()}($value: ${toKotlinDslTypeDef(c, api)}): ${c.n(parent, api)} = apply { attr(${
        name().toUnderscoredUpperCase()}, $value) }"""
    })}${nonFluent().isNotBlank().then {
        """
    ${override}fun ${nonFluent()}($value: ${toKotlinTypeSingle(c, api)}): ${toKotlinTypeSingle(c, api)} = applyAndReturn { ${multi().ifElse({
            """${name()}().addItem($value); value"""
        }, { """${name()}().value($value)""" })} }
    ${override}fun ${nonFluent()}($value: ${toKotlinTypeSingle(c, api)}.() -> Unit) : ${toKotlinTypeSingle(c, api)} = ${nonFluent()}(${toKotlinTypeSingle(c, derived)}($value))"""
    }}"""
}

fun <T : CompilationUnitI> T.toKotlinDslBuilderI(c: GenerationContext, derived: String = LangDerivedKind.API): String {
    return """
interface ${c.n(this, derived)} : ${c.n(superUnit(), derived)} {${
    props().joinToString(nL) { it.toKotlinDslBuilderMethodsI(c, derived, this) }}
}"""
}

fun <T : CompilationUnitI> T.toKotlinDslBuilder(c: GenerationContext,
                                                derived: String = LangDerivedKind.IMPL,
                                                api: String = LangDerivedKind.API
): String {
    val multiProps = props().filter { it.multi() }
    return """
open class ${c.n(this, derived)} : ${c.n(superUnit(), derived)}${(derived != api).then(
            { ", ${c.n(this, LangDerivedKind.API)}" })} {
    constructor(value: ${c.n(this, derived)}.() -> Unit = {}) : super(value as ${c.n(superUnit(), derived)}.() -> Unit)${
    props().joinSurroundIfNotEmptyToString(nL, prefix = nL) { it.toKotlinDslBuilderMethods(c, derived, api, this) }}${
    multiProps.isNotEmpty().then {
        """

    override fun fillSupportsItems() {${
        multiProps.joinSurroundIfNotEmptyToString(nL, prefix = nL) { "        ${it.name()}()" }}
        super.fillSupportsItems()
    }"""
    }}

    companion object {
        val EMPTY = ${c.n(this, derived)}({ name(${c.n(n.ItemEmpty, api)}.name()) })${
    props().joinSurroundIfNotEmptyToString(nL, prefix = nL) { it.toKotlinCompanionObjectName(c) }}
    }
}"""
}
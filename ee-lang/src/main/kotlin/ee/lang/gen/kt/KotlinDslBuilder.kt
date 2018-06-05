package ee.lang.gen.kt

import ee.common.ext.ifElse
import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toUnderscoredUpperCase
import ee.lang.*

fun <T : AttributeI<*>> T.toKotlinTypeSingleB(c: GenerationContext, api: String): String {
    return type().isNative().ifElse({ c.n(type(), api) }, { "${c.n(type(), api)}<*>" })
}

fun <T : AttributeI<*>> T.toKotlinDslTypeDef(c: GenerationContext, api: String): String {
    return """${isMulti().ifElse({ "${c.n(l.ListMultiHolder, LangDerivedKind.IMPL)}<${toKotlinTypeSingleB(c, api)}>" },
        { toKotlinTypeSingleB(c, api) })}${isNullable().then("?")}"""
}

fun <T : AttributeI<*>> T.toKotlinDslBuilderMethodsI(c: GenerationContext, api: String): String {
    val value = (name() == "value").ifElse("aValue", "value")
    val bool = (type() == n.Boolean)
    return """
    fun ${bool.ifElse({ "is${name().capitalize()}" }, { name() })}(): ${toKotlinDslTypeDef(c, api)}${isMulti().ifElse({
        """
    fun ${name()}(vararg $value: ${toKotlinTypeSingleB(c, api)}): B"""
    }, {
        """
    fun ${name()}($value: ${toKotlinDslTypeDef(c, api)}): B${bool.then {
            """
    fun ${name()}(): B = ${name()}(true)
    fun not${name().capitalize()}(): B = ${name()}(false)"""
        }}"""
    })}${nonFluent().isNotBlank().then {
        """
    fun ${nonFluent()}($value: ${toKotlinTypeSingleB(c, api)}): ${toKotlinTypeSingleB(c, api)}
    fun ${nonFluent()}($value: ${toKotlinTypeSingleB(c, api)}.() -> Unit = {}): ${toKotlinTypeSingleB(c, api)}"""
    }}"""
}

fun <T : AttributeI<*>> T.toKotlinDslBuilderMethods(c: GenerationContext, derived: String, api: String): String {
    val value = (name() == "value").ifElse("aValue", "value")
    val override = (derived != api).ifElse("override ", "")
    return """${isMulti().ifElse({
        """
    ${override}fun ${name()}(): ${toKotlinDslTypeDef(c,
            api)} = itemAsList(${name().toUnderscoredUpperCase()}, ${toKotlinTypeSingle(c, api)}::class.java, true)
    ${override}fun ${name()}(vararg $value: ${toKotlinTypeSingleB(c,
            api)}): B = apply { ${name()}().addItems(value.asList()) }"""
    }, {
        """
    ${override}fun ${(type() == n.Boolean).ifElse({ "is${name().capitalize()}" }, { name() })}(): ${toKotlinDslTypeDef(c,
            api)} = attr(${name().toUnderscoredUpperCase()}${isNullable().not().then {
            ", { ${(value() == null || value().toString().isEmpty()).ifElse(toKotlinEMPTY(c, derived), value())} }"
        }})
    ${override}fun ${name()}($value: ${toKotlinDslTypeDef(c,
            api)}): B = apply { attr(${name().toUnderscoredUpperCase()}, $value) }"""
    })}${nonFluent().isNotBlank().then {
        """
    ${override}fun ${nonFluent()}($value: ${toKotlinTypeSingleB(c, api)}): ${toKotlinTypeSingleB(c,
            api)} = applyAndReturn { ${isMulti().ifElse({
            """${name()}().addItem($value); value"""
        }, { """${name()}().value($value)""" })} }
    ${override}fun ${nonFluent()}($value: ${toKotlinTypeSingleB(c, api)}.() -> Unit): ${toKotlinTypeSingleB(c,
            api)} = ${nonFluent()}(${toKotlinTypeSingle(c, derived)}($value))"""
    }}"""
}

fun <T : CompilationUnitI<*>> T.toKotlinDslBuilderI(c: GenerationContext,
    derived: String = LangDerivedKind.API): String {
    return """
interface ${c.n(this, derived)}<B : ${c.n(this, derived)}<B>> : ${c.n(superUnit(), derived)}<B> {${props().joinToString(
        nL) { it.toKotlinDslBuilderMethodsI(c, derived) }}
}"""
}

fun <T : CompilationUnitI<*>> T.toKotlinDslBuilder(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
    api: String = LangDerivedKind.API): String {
    val multiProps = props().filter { it.isMulti() }
    val B = c.n(this, LangDerivedKind.MANUAL)
    val T = c.n(this, derived)
    return """
open class $T(value: $T.() -> Unit = {}) : $B<$T>(value) {

    companion object {
        val EMPTY = $T { name(${c.n(n.ItemEmpty, api)}.name()) }.apply<$T> { init() }
    }
}

open class $B<B : $B<B>>(value: B.() -> Unit = {}) : ${c.n(superUnit(),
        LangDerivedKind.MANUAL)}<B>(value)${(derived != api).then(
        { ", ${c.n(this, LangDerivedKind.API)}<B>" })} {${props().joinSurroundIfNotEmptyToString(nL,
        prefix = nL) { it.toKotlinDslBuilderMethods(c, derived, api) }}${multiProps.isNotEmpty().then {
        """

    override fun fillSupportsItems() {${multiProps.joinSurroundIfNotEmptyToString(nL,
            prefix = nL) { "        ${it.name()}()" }}
        super.fillSupportsItems()
    }"""
    }}${props().isNotEmpty().then {
        """

    companion object {${props().joinSurroundIfNotEmptyToString(nL, prefix = nL) { it.toKotlinCompanionObjectName(c) }}
    }"""
    }}
}"""
}
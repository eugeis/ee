package ee.lang.gen.kt

import ee.common.ext.ifElse
import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.joinWrappedToString
import ee.common.ext.then
import ee.lang.*

const val derivedBuilder = "BuilderI"
const val derivedBuilderB = "BuilderB"
const val derivedBuilderT = "Builder"

fun <T : AttributeI<*>> T.toKotlinTypeGenerics(c: GenerationContext, api: String): String =
        if (type().isOrDerived(n.Map)) k.core.Pair.GT(*type().generics().toTypedArray())
                .toKotlinTypeDef(c, api, false) else
            type().generics().first().type().toKotlinTypeDef(c, api, false)

fun <T : AttributeI<*>> T.toKotlinSignatureBuilder(c: GenerationContext, derived: String, api: String): String =
        "${name()}: ${type().isMulti().ifElse({
            """${type().toKotlinTypeDef(c, api, false, true)}${
            toKotlinInit(c, derived, true, false)}"""
        }, { """${type().toKotlinTypeDef(c, api, isNullable())}${toKotlinInit(c, derived, true)}""" })}"

fun <T : AttributeI<*>> T.toKotlinSignatureBuilderInit(c: GenerationContext, derived: String): String =
        "        ${name()}${toKotlinInit(c, derived, true, type().isMulti().not() && isNullable())}"

fun <T : AttributeI<*>> T.toKotlinMemberBuilder(c: GenerationContext, derived: String, api: String): String =
        "    protected var ${toKotlinSignatureBuilder(c, derived, api)}"

fun <T : AttributeI<*>> T.toKotlinBuilderMethodsI(c: GenerationContext, api: String): String {
    val value = (name() == "value").ifElse("aValue", "value")
    val bool = (type() == n.Boolean)
    return """
    fun ${bool.ifElse({ "is${name().capitalize()}" }, { name() })}(): ${toKotlinTypeDef(c, api)}${type().isMulti().ifElse({
        """
    fun ${name()}(vararg $value: ${toKotlinTypeGenerics(c, api)}): B"""
    }, {
        """
    fun ${name()}($value: ${toKotlinTypeDef(c, api)}): B${bool.then {
            """
    fun ${name()}(): B = ${name()}(true)
    fun not${name().capitalize()}(): B = ${name()}(false)"""
        }}"""
    })}${nonFluent().isNotBlank().then {
        """
    fun ${nonFluent()}($value: ${toKotlinTypeGenerics(c, api)}): ${toKotlinTypeGenerics(c, api)}
    fun ${nonFluent()}($value: ${toKotlinTypeGenerics(c, api)}.() -> Unit = {}): ${toKotlinTypeGenerics(c, api)}"""
    }}"""
}

fun <T : AttributeI<*>> T.toKotlinBuilderMethods(c: GenerationContext, derived: String, api: String): String {
    val value = (name() == "value").ifElse("aValue", "value")
    val override = (derived != api).ifElse("override ", "")
    return """${type().isMulti().ifElse({
        """
    ${override}fun ${name()}(): ${toKotlinTypeDef(c, api)} = ${isNullable().ifElse(
                { "${name()}.takeUnless { it.isEmpty() }" }, { name() })}
    ${override}fun ${name()}(vararg $value: ${toKotlinTypeGenerics(c, api)
        }): B = applyB { ${name()}.${type().isOrDerived(n.Map).ifElse("putAll", "addAll")}(value) }"""
    }, {
        """
    ${override}fun ${(type() == n.Boolean).ifElse({ "is${name().capitalize()}" },
                { name() })}(): ${toKotlinTypeDef(c, api)} = ${name()}
    ${override}fun ${name()}($value: ${toKotlinTypeDef(c, api)}): B = applyB { ${name()} = $value }"""
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

fun <T : CompilationUnitI<*>> T.toKotlinBuilderI(c: GenerationContext,
                                                 api: String = derivedBuilder): String {
    return """
interface ${c.n(this, api)}<B : ${c.n(this, api)}<B, T>, T : ${c.n(this, LangDerivedKind.API)}>${
    superUnit().isNotEMPTY().then {
        """ : ${c.n(superUnit(), api)}<B, T>"""
    }}${props().isNotEmpty().then {
        """ {${props().joinToString(nL) { it.toKotlinBuilderMethodsI(c, LangDerivedKind.API) }}${
        superUnit().isEMPTY().then {
            """

    fun build(): T
    fun clear(): B"""
        }}
}"""
    }}"""
}

fun <T : ConstructorI<*>> T.toKotlinCallParamsBuilder(
        c: GenerationContext, wrapIdent: String = "                    "): String = isNotEMPTY().then {
    params().joinWrappedToString(", ", wrapIdent) {
        (it.type() == n.Boolean).ifElse(
                { "is${it.name().capitalize()}()" },
                { "${it.name()}()" }
        )
    }
}

fun <T : CompilationUnitI<*>> T.toKotlinBuilder(c: GenerationContext, derived: String = derivedBuilderB,
                                                api: String = derivedBuilder): String {
    val multiProps = props().filter { it.isMulti() }
    val B = c.n(this, derived)
    val BT = c.n(this, derivedBuilderT)
    val T = c.n(this, LangDerivedKind.API)
    val superUnitExists = superUnit().isNotEMPTY()
    return """
open class $BT : $B<$BT, $T>() {
    override fun build(): $T =
            $T(${primaryOrFirstConstructor().toKotlinCallParamsBuilder(c)})
}

abstract class $B<B : $B<B, T>, T : ${c.n(this, LangDerivedKind.API)}> : ${superUnitExists.then {
        """${c.n(superUnit(), derived)}<B, T>(), """
    }}${c.n(this, api)}<B, T>${props().isNotEmpty().then {
        """ {${props().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
            it.toKotlinMemberBuilder(c, LangDerivedKind.IMPL, LangDerivedKind.API)
        }}${props().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
            it.toKotlinBuilderMethods(c, LangDerivedKind.IMPL, LangDerivedKind.API)
        }}

    override fun clear() = applyB {${superUnitExists.then {
            """
        super.clear()"""
        }}${props().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
            it.toKotlinSignatureBuilderInit(c, LangDerivedKind.IMPL)
        }}
    }${superUnitExists.not().then {
            """

    @Suppress("UNCHECKED_CAST")
    protected inline fun applyB(block: $B<B, T>.() -> Unit): B = apply(block) as B"""
        }}
}"""
    }}"""
}
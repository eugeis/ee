package ee.lang.gen.kt

import ee.common.ext.*
import ee.lang.*

const val derivedBuilder = "BuilderI"
const val derivedBuilderB = "BuilderB"
const val derivedBuilderT = "Builder"


fun <T : AttributeI<*>> T.toKotlinTypeGenerics(c: GenerationContext, api: String): String =
        if (type().isOrDerived(n.Map))
            k.core.Pair.GT(*type().generics().toTypedArray()).toKotlinTypeDef(c, api, false)
        else {
            val firstGeneric = type().generics().first()
            if (firstGeneric.type().isNotEMPTY())
                firstGeneric.type().toKotlinTypeDef(c, api, false)
            else
                firstGeneric.toKotlinTypeDef(c, api, false)
        }

fun <T : AttributeI<*>> T.toKotlinSignatureBuilder(c: GenerationContext, derived: String, api: String,
                                                   forceInit: Boolean = true): String =
        "${name()}: ${type().isMulti().ifElse({
            """${type().toKotlinTypeDef(c, api, nullable = false, mutable = true)}${
            toKotlinInit(c, derived, nullable = false, mutable = true)}"""
        }, { """${type().toKotlinTypeDef(c, api, isNullable())}${toKotlinInit(c, derived, mutable = true)}""" })}"

fun <T : AttributeI<*>> T.toKotlinSignatureBuilderInit(c: GenerationContext, derived: String): String =
        "        ${name()}${toKotlinInit(c, derived, type().isMulti().not() && isNullable(), true)}"

fun <T : AttributeI<*>> T.toKotlinFillFrom(): String =
        type().isMulti().ifElse({
            """        ${name()}.${type().isOrDerived(n.Map).ifElse(
                    "putAll(item.${name()}${isNullable().then(" ?: emptyMap()")})",
                    "addAll(item.${name()}${isNullable().then(" ?: emptyList()")})")}"""
        }, {
            """        ${name()} = item.${name()}"""
        })

fun <T : AttributeI<*>> T.toKotlinMemberBuilder(
        c: GenerationContext, derived: String, api: String, visibility: String = "private"): String =
        "    $visibility var ${toKotlinSignatureBuilder(c, derived, api)}"


fun List<AttributeI<*>>.toKotlinSignaturePrimaryBuilder(
        c: GenerationContext, derived: String, api: String,
        superUnitParams: List<AttributeI<*>> = emptyList(), wrapIdentWidth: Int = 6): String {
    return joinWrappedToString(", ",
            wrapIndent = (wrapIdentWidth + 1).toWrapIdentBlack()) { param ->
        if (superUnitParams.containsByName(param)) {
            param.toKotlinSignature(c, derived, api)
        } else {
            param.toKotlinConstructorMemberBuilder(c, derived, api)
        }
    }
}

fun <T : AttributeI<*>> T.toKotlinConstructorMemberBuilder(c: GenerationContext, derived: String, api: String): String =
        "private var ${toKotlinSignatureBuilder(c, derived, api)}"


fun <T : AttributeI<*>> T.toKotlinBuilderMethodsI(c: GenerationContext, api: String, builderType: String = "B"): String {
    val value = (name() == "value").ifElse("aValue", "value")
    val bool = (type() == n.Boolean)
    return """
    fun ${bool.ifElse({ "is${name().capitalize()}" }, { name() })}(): ${toKotlinTypeDef(c, api)}${type().isMulti().ifElse({
        """
    fun ${name()}(vararg $value: ${toKotlinTypeGenerics(c, api)}): $builderType
    fun clear${name().capitalize()}(): $builderType"""
    }, {
        """
    fun ${name()}($value: ${toKotlinTypeDef(c, api)}): $builderType${bool.then {
            """
    fun ${name()}(): $builderType = ${name()}(true)
    fun not${name().capitalize()}(): $builderType = ${name()}(false)"""
        }}"""
    })}${nonFluent().isNotBlank().then {
        """
    fun ${nonFluent()}($value: ${toKotlinTypeGenerics(c, api)}): ${toKotlinTypeGenerics(c, api)}
    fun ${nonFluent()}($value: ${toKotlinTypeGenerics(c, api)}.() -> Unit = {}): ${toKotlinTypeGenerics(c, api)}"""
    }}"""
}


fun <T : AttributeI<*>> T.toKotlinBuilderMethods(
        c: GenerationContext, derived: String, api: String, B: String = "B", applyB: String = "applyB"): String {
    val value = (name() == "value").ifElse("aValue", "value")
    val override = (derived != api).ifElse("override ", "")
    return """${type().isMulti().ifElse({
        """
    ${override}fun ${name()}(): ${toKotlinTypeDef(c, api)} = ${isNullable().ifElse(
                { "${name()}.takeUnless { it.isEmpty() }" }, { name() })}
    ${override}fun ${name()}(vararg $value: ${toKotlinTypeGenerics(c, api)
        }): $B = $applyB {
        ${name()}.${type().isOrDerived(n.Map).ifElse("putAll", "addAll")}(value.asList()) }
    ${override}fun clear${name().capitalize()}(): $B = $applyB { ${name()}.clear() }"""
    }, {
        """
    ${override}fun ${(type() == n.Boolean).ifElse({ "is${name().capitalize()}" },
                { name() })}(): ${toKotlinTypeDef(c, api)} = ${name()}
    ${override}fun ${name()}($value: ${toKotlinTypeDef(c, api)}): $B = $applyB { ${name()} = $value }"""
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
    val superUnitExists = superUnit().isNotEMPTY()
    val subTypeAllowed = isKotlinBuilderSubTypeAllowed()
    val followGenerics = toKotlinGenericsClassDefFollow(c, api)
    val generics = toKotlinGenerics(c, api)

    val builderName = c.n(this, api)
    val B = if(subTypeAllowed || superUnitExists) "B" else "$builderName$generics"
    val typeName = c.n(this, LangDerivedKind.API)
    return subTypeAllowed.ifElse({
        """
interface $builderName<B : $builderName<B, T$followGenerics>, T : $typeName${toKotlinGenerics(c, api)}$followGenerics>${
        superUnitExists.then {
            """ : ${c.n(superUnit(), api)}<B, T>"""
        }}${propsNoMeta().isNotEmpty().then {
            """ {${propsNoMeta().joinToString(nL) { it.toKotlinBuilderMethodsI(c, LangDerivedKind.API) }}${
            superUnitExists.not().then {
                """

    fun fillFrom(item: T): B
    fun build(): T
    fun clear(): B"""
            }}
}"""
        }}"""
    }, {
        """${superUnitExists.ifElse({"""
interface $builderName<B : $builderName<B>$followGenerics> : ${c.n(superUnit(), api)}<B, $typeName$followGenerics>"""}, 
                {"""
interface $B"""})}${props().isNotEmpty().then {
            """ {${props().joinToString(nL) { it.toKotlinBuilderMethodsI(c, LangDerivedKind.API, B) }}${
            superUnitExists.not().then {
                """

    fun fillFrom(item: $typeName$generics): $B
    fun build(): $typeName$generics
    fun clear(): $B"""
            }}
}"""
        }}"""
    })
}

fun <T : ConstructorI<*>> T.toKotlinCallParamsBuilder(
        c: GenerationContext, wrapIdent: String = "                    "): String = isNotEMPTY().then {
    params().toKotlinCallParamsBuilder(c, wrapIdent)
}

fun List<AttributeI<*>>.toKotlinCallParamsBuilder(
        c: GenerationContext, wrapIdent: String = "                    "): String =
        joinWrappedToString(", ", wrapIdent) {
            (it.type() == n.Boolean).ifElse(
                    { "is${it.name().capitalize()}()" },
                    { "${it.name()}()" }
            )
        }

fun <T : CompilationUnitI<*>> T.toKotlinBuilder(c: GenerationContext, derived: String = derivedBuilderB,
                                                api: String = derivedBuilder): String {
    val subTypeAllowed = isKotlinBuilderSubTypeAllowed()
    val T = c.n(this, LangDerivedKind.API)
    val superUnitExists = superUnit().isNotEMPTY()

    val followGenerics = toKotlinGenericsClassDefFollow(c, api)
    val generics = toKotlinGenerics(c, api)

    val BT = c.n(this, derivedBuilderT)

    val propsAllNotNullableGeneric = propsAllNotNullableGeneric()

    var primConstr = ""
    var primConstrAbstr = ""
    var primConstrCall = ""

    if (propsAllNotNullableGeneric.isNotEmpty()) {
        primConstrAbstr = "(${propsAllNotNullableGeneric.toKotlinSignaturePrimaryBuilder(c, LangDerivedKind.API, LangDerivedKind.API)})"
        primConstr = "(${propsAllNotNullableGeneric.toKotlinSignature(c, LangDerivedKind.API, LangDerivedKind.API)})"
        primConstrCall = propsAllNotNullableGeneric.toKotlinCallParams(c)
    }


    return subTypeAllowed.ifElse({
        val B = c.n(this, derived)

        """
open class $BT$generics$primConstr :
        $B<$BT$generics, $T$generics$followGenerics>($primConstrCall) {
    override fun build(): $T$generics =
            $T(${primaryOrFirstConstructorOrFull().toKotlinCallParamsBuilder(c)})
}

abstract class $B<B : $B<B, T$followGenerics>, T : ${c.n(this, LangDerivedKind.API)}$generics$followGenerics>$primConstrAbstr :
        ${superUnitExists.then {
            "${c.n(superUnit(), derived)}$generics<B, T$generics$followGenerics>(), "
        }}${c.n(this, api)}<B, T$followGenerics>${props().isNotEmpty().then {
            """ {${propsWithoutNotNullableGeneric().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
                it.toKotlinMemberBuilder(c, LangDerivedKind.IMPL, LangDerivedKind.API)
            }}${propsNoMeta().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
                it.toKotlinBuilderMethods(c, LangDerivedKind.IMPL, LangDerivedKind.API)
            }}

    override fun fillFrom(item: T) = applyB {${superUnitExists.then {
                """
        super.fillFrom(item)"""
            }}${props().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
                it.toKotlinFillFrom()
            }}
    }

    override fun clear() = applyB {${superUnitExists.then {
                """
        super.clear()"""
            }}${propsWithoutNotNullableGeneric().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
                it.toKotlinSignatureBuilderInit(c, LangDerivedKind.IMPL)
            }}
    }${superUnitExists.not().then {
                """

    @Suppress("UNCHECKED_CAST")
    protected inline fun applyB(block: $B<B, T$followGenerics>.() -> Unit): B = apply(block) as B"""
            }}
}"""
        }}"""
    }, {
        val B = "$BT$generics"

        """
class $B$primConstrAbstr :${superUnitExists.ifElse( { """
        ${c.n(superUnit(), derived)}$generics<$BT, $T$generics$followGenerics>(), 
        ${c.n(this, api)}<$BT>"""
        },{" ${c.n(this, api)}$generics"})}${props().isNotEmpty().then {
            """ {${propsWithoutNotNullableGeneric().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
                it.toKotlinMemberBuilder(c, LangDerivedKind.IMPL, LangDerivedKind.API)
            }}${propsNoMeta().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
                it.toKotlinBuilderMethods(c, LangDerivedKind.IMPL, LangDerivedKind.API, B, "apply")
            }}

    override fun build(): $T$generics =
            $T(${primaryOrFirstConstructorOrFull().toKotlinCallParamsBuilder(c)})

    override fun fillFrom(item: $T$generics) = apply {${superUnitExists.then {
                """
        super.fillFrom(item)"""
            }}${props().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
                it.toKotlinFillFrom()
            }}
    }

    override fun clear() = apply {${superUnitExists.then {
                """
        super.clear()"""
            }}${propsWithoutNotNullableGeneric().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
                it.toKotlinSignatureBuilderInit(c, LangDerivedKind.IMPL)
            }}
    }
}"""
        }}"""
    })
}

private fun <T : CompilationUnitI<*>> T.isKotlinBuilderSubTypeAllowed() = isOpen() && this !is Basic
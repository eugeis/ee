package ee.lang.gen.kt

import ee.common.ext.ifElse
import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toUnderscoredUpperCase
import ee.lang.*
import ee.lang.gen.DerivedNames
import ee.lang.gen.isNative
import java.util.*


fun <T : ItemI<*>> T.toKotlinEMPTY(c: GenerationContext, derived: String): String {
    return (this.parent() == n).ifElse("\"\"", { "${c.n(this, derived)}.EMPTY" })
}

fun <T : AttributeI> T.toKotlinEMPTY(c: GenerationContext, derived: String): String {
    return type().toKotlinEMPTY(c, derived)
}


fun <T : AttributeI> T.toKotlinTypeSingle(c: GenerationContext, api: String): String {
    return type().isNative().ifElse({ c.n(type(), api) }, { "${c.n(type(), api)}<*>" })
}

fun <T : AttributeI> T.toKotlinDslTypeDef(c: GenerationContext, api: String): String {
    return """${isMulti().ifElse({ "ListMultiHolder<${toKotlinTypeSingle(c, api)}>" },
        { toKotlinTypeSingle(c, api) })}${isNullable().then("?")}"""
}

fun <T : AttributeI> T.toKotlinTypeSingleNoGeneric(c: GenerationContext, api: String): String {
    return type().isNative().ifElse({ c.n(type(), api) }, { c.n(type(), api) })
}

fun <T : AttributeI> T.toKotlinTypeSingleClass(c: GenerationContext, api: String): String {
    return type().isNative().ifElse({ c.n(type(), api) }, { c.n(type(), api) })
}

fun <T : AttributeI> T.toKotlinDslTypeDefNoGeneric(c: GenerationContext, api: String): String {
    return """${isMulti().ifElse({ "ListMultiHolder<${toKotlinTypeSingleNoGeneric(c, api)}>" },
        { toKotlinTypeSingleNoGeneric(c, api) })}${isNullable().then("?")}"""
}

fun <T : AttributeI> T.toKotlinDslBuilderMethodsIB(c: GenerationContext, api: String): String {
    val value = (name() == "value").ifElse("aValue", "value")
    val bool = type() == n.Boolean
    return isMulti().ifElse({
        """
    fun ${name()}(vararg $value: ${toKotlinTypeSingle(c, api)}): B"""
    }, {
        """
    fun ${name()}($value: ${toKotlinDslTypeDef(c, api)}): B${bool.then {
            """
    fun ${name()}(): B = ${name()}(true)
    fun not${name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(): B = ${name()}(false)"""
        }}"""
    })
}

fun <T : AttributeI> T.toKotlinDslBuilderMethodsI(c: GenerationContext, api: String): String {
    val value = (name() == "value").ifElse("aValue", "value")
    return """
    fun ${(type() == n.Boolean && !isMulti()).ifElse({ "is${name().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }}" },
        { name() })}(): ${toKotlinDslTypeDef(c, api)}${nonFluent().isNotBlank().then {
        """
    fun ${nonFluent()}($value: ${toKotlinTypeSingle(c, api)}): ${toKotlinTypeSingle(c, api)}
    fun ${nonFluent()}($value: ${toKotlinTypeSingle(c, api)}.() -> Unit = {}): ${toKotlinTypeSingle(c, api)}"""
    }}"""
}

fun <T : AttributeI> T.toKotlinDslBuilderMethods(c: GenerationContext, derived: String, api: String): String {
    val value = (name() == "value").ifElse("aValue", "value")
    val override = (derived != api).ifElse("override ", "")
    return """${isMulti().ifElse({
        """
    ${override}fun ${name()}(): ${toKotlinDslTypeDef(c,
            api)} = itemAsList(${name().toUnderscoredUpperCase()}, ${toKotlinTypeSingleNoGeneric(c,
            api)}::class.java, true)
    ${override}fun ${name()}(vararg $value: ${toKotlinTypeSingle(c,
            api)}): B = apply { ${name()}().addItems(value.asList()) }"""
    }, {
        """
    ${override}fun ${(type() == n.Boolean).ifElse({ "is${name().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }}" }, { name() })}(): ${toKotlinDslTypeDef(
            c,
            api)} = attr(${name().toUnderscoredUpperCase()}${isNullable().not().then {
            ", { ${value().toString().isEmpty().ifElse(toKotlinEMPTY(c, derived), value())} }"
        }})
    ${override}fun ${name()}($value: ${toKotlinDslTypeDef(c,
            api)}): B = apply { attr(${name().toUnderscoredUpperCase()}, $value) }"""
    })}${nonFluent().isNotBlank().then {
        """
    ${override}fun ${nonFluent()}($value: ${toKotlinTypeSingle(c, api)}): ${toKotlinTypeSingle(c,
            api)} = applyAndReturn { ${isMulti().ifElse({
            """${name()}().addItem($value); value"""
        }, { """${name()}().addItem($value)""" })} }
    ${override}fun ${nonFluent()}($value: ${toKotlinTypeSingle(c, api)}.() -> Unit): ${toKotlinTypeSingle(c,
            api)} = ${nonFluent()}(${toKotlinTypeSingleClass(c, derived)}($value))"""
    }}"""
}

val specialEmptyObjects = setOf("CompilationUnit", "LogicUnit")
// isMulti holder for general types (like 'superUnitFor') must not be used as target for dynamic DSL objects,
// like "object commands : Command... {..}"
val generalTypes = setOf("Item", "Composite", "CompilationUnit", "LogicUnit", "Type", "Command", "Controller")

fun <T : AttributeI> T.toKotlinCompanionObjectName(): String {
    return """        val ${name().toUnderscoredUpperCase()} = "${generalTypes.contains(type().name()).then(
        "_")}_${name()}""""
}

fun <T : CompositeI<*>> T.toKotlinDslBuilderI(c: GenerationContext, api: String = DerivedNames.API): String {
    val props = items().filterIsInstance(AttributeI::class.java)
    return """
interface ${c.n(this, api)}<B : ${c.n(this, api)}<B>> : ${c.n(derivedFrom(),
        api)}<B> {${props.joinSurroundIfNotEmptyToString(nL) {
        it.toKotlinDslBuilderMethodsIB(c, api)
    }}${props.joinSurroundIfNotEmptyToString(nL) { it.toKotlinDslBuilderMethodsI(c, api) }}
}"""
}

fun <T : CompositeI<*>> T.toKotlinDslBuilder(c: GenerationContext, derived: String = DerivedNames.IMPL,
    api: String = DerivedNames.API): String {
    val props = items().filterIsInstance(AttributeI::class.java)
    val multiProps = props.filter { it.isMulti() }
    val target = c.n(this, derived)
    return """
open class ${c.n(this, derived)}(adapt: ${c.n(this, derived)}.() -> Unit = {}) : ${
    c.n(this, derived)}B<${c.n(this,
        derived)}>(adapt) {

    companion object {
        val EMPTY = ${specialEmptyObjects.contains(target).ifElse({ "${target}Empty" }, {
        "$target { name(ItemEmpty.name()) }.apply<$target> { init() }"
    })}
    }
}

open class ${c.n(this, derived)}B<B : ${c.n(this, api)}<B>>(adapt: B.() -> Unit = {}) : ${c.n(derivedFrom(),
        derived)}B<B>(adapt)${(derived != api).then { ", ${c.n(this, DerivedNames.API)}<B>" }} {${
    props.joinSurroundIfNotEmptyToString(nL,
        prefix = nL) { it.toKotlinDslBuilderMethods(c, derived, api) }}${multiProps.isNotEmpty().then {
        """

    override fun fillSupportsItems() {${multiProps.joinSurroundIfNotEmptyToString(nL,
            prefix = nL) { "        ${it.name()}()" }}
        super.fillSupportsItems()
    }"""
    }}${props.isNotEmpty().then {
        """

    companion object {${props.joinSurroundIfNotEmptyToString(nL, prefix = nL) { it.toKotlinCompanionObjectName() }}
    }"""
    }}
}
"""
}

fun <T : ItemI<*>> T.toKotlinObjectTreeCompilationUnit(c: GenerationContext,
    derived: String = DerivedNames.DSL_TYPE): String {
    return """    val ${c.n(this, derived)} = ${c.n(l.CompilationUnit, derived)}${
    derivedFrom().isNotEMPTY().then {
        " { derivedFrom(${c.n(derivedFrom(), derived)}) }"
    }}"""
}

fun <T : CompositeI<*>> T.toKotlinDslObjectTree(c: GenerationContext, derived: String = DerivedNames.DSL_TYPE): String {
    return """
object ${c.n(this)} : ${c.n(l.StructureUnit)}({ namespace("${namespace()}") }) {
${items().filter { !(it.name() == "MultiHolder") }.joinSurroundIfNotEmptyToString(nL) {
        it.toKotlinObjectTreeCompilationUnit(c, derived)
    }}

    object MultiHolder : CompilationUnit({ derivedFrom(Item) }) {
        val T = G { type(Item) }
    }
}"""
}
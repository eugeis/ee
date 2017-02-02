package ee.lang.gen.kt

import ee.common.ext.ifElse
import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.lang.*

fun <T : AttributeI> T.toKotlinDslTypeDefMember(c: GenerationContext, api: String): String {
    return """${multi().ifElse({ "${c.n(type(), DerivedNames.COMPOSITE.name)}" }, {
        nullable().ifElse({
            "${c.n(l.NullValueHolder, api)}<${c.n(type(), api)}>"
        }, { "${c.n(l.ValueHolder, api)}<${c.n(type(), api)}>" })
    })}"""
}

fun <T : AttributeI> T.toKotlinDslTypeDef(c: GenerationContext, api: String): String {
    return """${multi().ifElse({ "List<${c.n(type(), api)}>" }, { c.n(type(), api) })}${nullable().then("?")}"""
}

fun <T : AttributeI> T.toKotlinDslBuilderProp(c: GenerationContext, derived: String, api: String): String {
    return """    private var _${name()}: ${toKotlinDslTypeDefMember(c, api)} = ${(value() == null || value().toString().isEmpty()).ifElse({
        if (multi()) {
            "add(${c.n(type(), DerivedNames.COMPOSITE.name)}({ name(\"${name()}\") }))"
        } else if (nullable()) {
            "add(${c.n(l.NullValueHolder, derived)}({ name(\"${name()}\") }))"
        } else {
            "add(${c.n(l.ValueHolder, derived)}(${toKotlinEmpty(c, derived)} as ${c.n(type(), api)}, { name(\"${name()}\") }))"
        }
    }, { "add(${c.n(l.ValueHolder, derived)}(${value()}, { name(\"${name()}\") }))" })}"""
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
    ${override}fun ${name()}(): ${toKotlinDslTypeDef(c, api)} = _${name()}.items()
    ${override}fun ${name()}(vararg $value: ${toKotlinTypeSingle(c, api)}): ${c.n(parent, api)} = apply { _${name()}.addAll(value.toList()) }"""
    }, {
        """
    ${override}fun ${name()}(): ${toKotlinDslTypeDef(c, api)} = _${name()}.value()
    ${override}fun ${name()}($value: ${toKotlinDslTypeDef(c, api)}): ${c.n(parent, api)} = apply { _${name()}.value($value) }"""
    })}${nonFluent().isNotBlank().then {
        """
    ${override}fun ${nonFluent()}($value: ${toKotlinTypeSingle(c, api)}): ${toKotlinTypeSingle(c, api)} = applyAndReturn { ${multi().ifElse({
            """_${name()}.add($value); value"""
        }, { """_${name()}.value($value)""" })} }
    ${override}fun ${nonFluent()}($value: ${toKotlinTypeSingle(c, api)}.() -> Unit) : ${toKotlinTypeSingle(c, api)} = ${nonFluent()}(${toKotlinTypeSingle(c, derived)}($value))"""
    }}"""
}

fun <T : CompilationUnitI> T.toKotlinDslBuilderI(c: GenerationContext, derived: String = DerivedNames.API.name): String {
    return """
interface ${c.n(this, derived)} : ${c.n(superUnit(), derived)} {${
    props().joinToString(nL) { it.toKotlinDslBuilderMethodsI(c, derived) }}
}"""
}

fun <T : CompilationUnitI> T.toKotlinDslBuilder(c: GenerationContext,
                                                derived: String = DerivedNames.IMPL.name,
                                                api: String = DerivedNames.API.name
): String {
    return """
open class ${c.n(this, derived)} : ${c.n(superUnit(), derived)}${(derived != api).then(
            { ", ${c.n(this, DerivedNames.API.name)}" })} {${
    props().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) { it.toKotlinDslBuilderProp(c, derived, api) }}
    constructor(value: ${c.n(this, derived)}.() -> Unit = {}) : super(value as ${c.n(superUnit(), derived)}.() -> Unit)${
    props().joinSurroundIfNotEmptyToString(nL, prefix = nL) { it.toKotlinDslBuilderMethods(c, derived, api) }}${
    toKotlinEmptyObject(c, derived)}
}"""
}

fun <T : CompilationUnitI> T.toKotlinDslComposite(c: GenerationContext,
                                                  derived: String = DerivedNames.COMPOSITE.name,
                                                  api: String = DerivedNames.API.name): String {

    return """
open class ${c.n(this, derived)} : ${c.n(l.TypedComposite, DerivedNames.IMPL.name)}<${c.n(this, api)}> {
    constructor(value: ${c.n(this, derived)}.() -> Unit = {}) : super(${c.n(this, api)}::class.java,
            value as ${c.n(l.TypedComposite, DerivedNames.IMPL.name)}<${c.n(this, api)}>.() -> Unit)
}"""
}
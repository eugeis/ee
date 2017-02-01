package ee.design.gen

import ee.common.ext.ifElse
import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.design.*


fun <T : ItemI> T.toKotlinEmpty(c: GenerationContext, derived: String): String {
    return (this.parent() == n).ifElse("\"\"", { "${c.n(this, derived)}.EMPTY" })
}

fun <T : AttributeI> T.toKotlinEmpty(c: GenerationContext, derived: String): String {
    return type().toKotlinEmpty(c, derived)
}


fun <T : AttributeI> T.toKotlinTypeSingle(c: GenerationContext, api: String): String {
    return """${c.n(type(), api)}"""
}

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
    return """    private var _${name()}: ${toKotlinDslTypeDefMember(c, api)} = ${value().toString().isEmpty().ifElse({
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
                                                  parent: ItemI = parent()): String {
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

fun <T : AttributeI> T.toKotlinDslBuilderMethods(c: GenerationContext, derived: String, api: String, parent: ItemI = parent()): String {
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

val specialEmptyObjects = setOf("CompilationUnit", "LogicUnit")
fun <T : CompositeI> T.toKotlinEmptyObject(c: GenerationContext, derived: String): String {
    val target = c.n(this, derived)
    return """

    companion object {
        val EMPTY = ${specialEmptyObjects.contains(target).ifElse({ "${target}Empty" }, { "${c.n(this, derived)}()" })}
    }"""
}

fun <T : CompositeI> T.toKotlinDslComposite(c: GenerationContext,
                                            derived: String = DerivedNames.COMPOSITE.name,
                                            api: String = DerivedNames.API.name): String {

    return """
open class ${c.n(this, derived)} : ${c.n(l.TypedComposite, DerivedNames.IMPL.name)}<${c.n(this, api)}> {
    constructor(value: ${c.n(this, derived)}.() -> Unit = {}) : super(${c.n(this, api)}::class.java,
            value as ${c.n(l.TypedComposite, DerivedNames.IMPL.name)}<${c.n(this, api)}>.() -> Unit)${
    toKotlinEmptyObject(c, derived)}
}"""
}

fun <T : CompositeI> T.toKotlinDslBuilderI(c: GenerationContext, api: String = DerivedNames.API.name): String {
    val props = items().filterIsInstance(AttributeI::class.java)
    return """
interface ${c.n(this, api)} : ${c.n(derivedFrom(), api)} {${
    props.joinSurroundIfNotEmptyToString(nL) { it.toKotlinDslBuilderMethodsI(c, api) }}
}"""
}

fun <T : CompositeI> T.toKotlinDslBuilder(c: GenerationContext,
                                          derived: String = DerivedNames.IMPL.name,
                                          api: String = DerivedNames.API.name): String {
    val props = items().filterIsInstance(AttributeI::class.java)
    return """
open class ${c.n(this, derived)} : ${c.n(derivedFrom(), derived)}${(derived != api).then(
            { ", ${c.n(this, DerivedNames.API.name)}" })} {
${props.joinSurroundIfNotEmptyToString(nL) { it.toKotlinDslBuilderProp(c, derived, api) }}

    constructor(value: ${c.n(this, derived)}.() -> Unit = {}) : super(value as ${c.n(derivedFrom(), derived)}.() -> Unit)${
    props.joinSurroundIfNotEmptyToString(nL, prefix = nL) { it.toKotlinDslBuilderMethods(c, derived, api) }}${
    toKotlinEmptyObject(c, derived)}
}"""
}

fun <T : CompositeI> T.toKotlinIsEmptyExt(c: GenerationContext,
                                          derived: String = DerivedNames.IMPL.name,
                                          api: String = DerivedNames.API.name): String {
    return """
fun ${c.n(this, api)}?.isEmpty(): Boolean = (this == null || this == ${c.n(this, derived)}.EMPTY)
fun ${c.n(this, api)}?.isNotEmpty(): Boolean = !isEmpty()"""
}

fun <T : ItemI> T.toKotlinObjectTreeCompilationUnit(c: GenerationContext, derived: String = DerivedNames.DSL_TYPE.name): String {
    return """    val ${c.n(this, derived)} = ${c.n(l.CompilationUnit, derived)}(${derivedFrom().isNotEMPTY().then {
        "{ derivedFrom(${c.n(derivedFrom(), derived)}) }"
    }})"""
}

fun <T : CompositeI> T.toKotlinDslTypes(c: GenerationContext, derived: String = DerivedNames.DSL_TYPE.name): String {
    return """
object ${c.n(this)} : ${c.n(l.StructureUnit)}({ namespace("${namespace()}") }) {
${items().filter { !it.name().equals("TypedComposite") }.joinSurroundIfNotEmptyToString(nL) { it.toKotlinObjectTreeCompilationUnit(c, derived) }}

    object TypedComposite : CompilationUnit({ derivedFrom(Item) }) {
        val T = G({ type(Item) })
    }
}"""
}
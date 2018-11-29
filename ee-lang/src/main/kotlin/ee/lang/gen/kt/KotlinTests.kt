package ee.lang.gen.kt

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.lang.*
import ee.lang.gen.java.junit

fun <T : EnumTypeI<*>> T.toKotlinEnumParseAndIsMethodsTests(c: GenerationContext, derived: String = LangDerivedKind.API): String {
    val name = c.n(this, derived).capitalize()
    var prev = literals().last()

    return """
class ${name}EnumParseAndIsMethodsTests {
    @${c.n(junit.Test, derived)}
    fun testStringTo${name}_Normal() {
        $name.values().forEach { item ->
            //check normal
            ${c.n(k.test.assertSame, derived)}(item, item.name.to$name())
        }
    }

    @${c.n(junit.Test, derived)}
    fun testStringTo${name}_CaseNotSensitive() {
        $name.values().forEach { item ->
            //check case not sensitive
            ${c.n(k.test.assertSame, derived)}(item, item.name.toLowerCase().to$name())
            ${c.n(k.test.assertSame, derived)}(item, item.name.toUpperCase().to$name())
        }
        //check null to default value
        ${c.n(k.test.assertSame, derived)}(null.to$name(), $name.${literals().first().toKotlin()})

        //check unknown to default value
        ${c.n(k.test.assertSame, derived)}("".to$name(), $name.${literals().first().toKotlin()})
    }

    @${c.n(junit.Test, derived)}
    fun testStringTo${name}_NullOrWrongToDefault() {
        //check null to default value
        ${c.n(k.test.assertSame, derived)}(null.to$name(), $name.${literals().first().toKotlin()})

        //check empty to default value
        ${c.n(k.test.assertSame, derived)}("".to$name(), $name.${literals().first().toKotlin()})

        //check unknown to default value
        ${c.n(k.test.assertSame, derived)}("$@%".to$name(), $name.${literals().first().toKotlin()})
    }${literals().joinToString(nL) {

        val litCap = it.name().capitalize()
        val prevCap = prev.name().capitalize()
        prev = it

        """
    @${c.n(junit.Test, derived)}
    fun testIs${it.toKotlin()}() {
        //normal
        ${c.n(k.test.assertTrue, derived)}($name.$litCap.is$litCap())

        //wrong
        ${c.n(k.test.assertFalse, derived)}($name.$prevCap.is$litCap())
    }
"""
    }}
}
"""
}

fun <T : CompilationUnitI<*>> T.toKotlinFieldTest(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                  api: String = LangDerivedKind.API,
                                                  dataClass: Boolean = this is BasicI<*> &&
                                                          superUnits().isEmpty() && superUnitFor().isEmpty()): String {
    val name = c.n(this, derived).capitalize()
    val timeProps = primaryConstructor().params().filter {
        it.type() == n.Date
    }.associateBy({ it.name() }) {
        it.name()
    }

    return """
class ${name}FieldTests {
    @${c.n(junit.Test, derived)}
    fun test${name}_Normal() {${timeProps.values.joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        "        val $it = Date()"
    }}
        val item = $name${primaryConstructor().toKotlinCallValue(c, derived, externalVariables = timeProps)}${
    props().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        "        ${c.n(k.test.assertEquals, derived)}(${
        it.toKotlinValue(c, derived, value = timeProps[it.name()] ?: it.value())}, item.${it.name()})"
    }}
    }

    @${c.n(junit.Test, derived)}
    fun test${name}_Default() {
        val item = $name.EMPTY${
    props().joinSurroundIfNotEmptyToString(nL, prefix = nL,
            postfix = nL) {
        if (!it.isNullable() &&  it.type() == n.Date) {
            "        ${c.n(k.test.assertTrue, derived)}(${
            it.toKotlinValueInit(c, derived)}.time - item.${it.name()}.time <= 1000)"
        } else {
            "        ${c.n(k.test.assertEquals, derived)}(${
            it.toKotlinValueInit(c, derived)}, item.${it.name()})"
        }
    }}
    }
}"""
}
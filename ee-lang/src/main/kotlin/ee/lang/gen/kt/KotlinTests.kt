package ee.lang.gen.kt

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toUnderscoredUpperCase
import ee.lang.*
import ee.lang.gen.java.junit

fun <T : EnumTypeI<*>> T.toKotlinEnumParseAndIsMethodsTests(
        c: GenerationContext, derived: String = LangDerivedKind.API): String {
    val name = c.n(this, derived).capitalize()
    val toByName = "to${name}ByName"
    var prev = literals().last()

    return """
class ${name}EnumParseAndIsMethodsTests {
    @${c.n(junit.Test, derived)}
    fun testStringTo${name}_Normal() {
        $name.values().forEach { item ->
            //check normal
            ${c.n(k.test.assertSame, derived)}(item, item.name.$toByName())
        }
    }

    @${c.n(junit.Test, derived)}
    fun testStringTo${name}_CaseNotSensitive() {
        $name.values().forEach { item ->
            //check case not sensitive
            ${c.n(k.test.assertSame, derived)}(item, item.name.toLowerCase().$toByName())
            ${c.n(k.test.assertSame, derived)}(item, item.name.toUpperCase().$toByName())
        }
        //check null to default value
        ${c.n(k.test.assertSame, derived)}(null.$toByName(), $name.${literals().first().toKotlin()})

        //check unknown to default value
        ${c.n(k.test.assertSame, derived)}("".$toByName(), $name.${literals().first().toKotlin()})
    }

    @${c.n(junit.Test, derived)}
    fun testStringTo${name}_NullOrWrongToDefault() {
        //check null to default value
        ${c.n(k.test.assertSame, derived)}(null.$toByName(), $name.${literals().first().toKotlin()})

        //check empty to default value
        ${c.n(k.test.assertSame, derived)}("".$toByName(), $name.${literals().first().toKotlin()})

        //check unknown to default value
        ${c.n(k.test.assertSame, derived)}("$@%".$toByName(), $name.${literals().first().toKotlin()})
    }${literals().joinToString(nL) {

        val litCap = it.name().toUnderscoredUpperCase()
        val prevCap = prev.name().toUnderscoredUpperCase()
        prev = it

        """
    @${c.n(junit.Test, derived)}
    fun testIs${it.toKotlin()}() {
        //normal
        ${c.n(k.test.assertTrue, derived)}($name.$litCap.is${it.name().capitalize()}())

        //wrong
        ${c.n(k.test.assertFalse, derived)}($name.$prevCap.is${it.name().capitalize()}())
    }
"""
    }}
}
"""
}

fun <T : CompilationUnitI<*>> T.toKotlinFieldTest(
        c: GenerationContext, derived: String = LangDerivedKind.IMPL, api: String = LangDerivedKind.API,
        dataClass: Boolean = this is BasicI<*> && superUnits().isEmpty() && superUnitFor().isEmpty()): String {
    if (generics().isNotEmpty())
        return ""

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
        val item = $name${primaryConstructor().toKotlinCallValue(c, derived,
            externalVariables = timeProps, resolveLiteralValue = true)}${
    propsAll().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        "        ${c.n(k.test.assertEquals, derived)}(${
        it.toKotlinValue(c, derived, value = timeProps[it.name()] ?: it.value(),
                resolveLiteralValue = true)}, item.${it.name()})"
    }}
    }

    @${c.n(junit.Test, derived)}
    fun test${name}_Default() {
        val item = $name.EMPTY${
    propsAll().joinSurroundIfNotEmptyToString(nL, prefix = nL,
            postfix = nL) {
        if (!it.isNullable() && it.type() == n.Date) {
            "        ${c.n(k.test.assertTrue, derived)}(${
            it.toKotlinValueInit(c, derived)}.time - item.${it.name()}.time <= 5000)"
        } else {
            "        ${c.n(k.test.assertEquals, derived)}(${
            it.toKotlinValueInit(c, derived, resolveLiteralValue = true)}, item.${it.name()})"
        }
    }}
    }
}"""
}
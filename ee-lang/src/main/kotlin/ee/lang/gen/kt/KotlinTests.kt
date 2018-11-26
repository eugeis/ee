package ee.lang.gen.kt

import ee.lang.EnumTypeI
import ee.lang.GenerationContext
import ee.lang.LangDerivedKind
import ee.lang.gen.java.junit
import ee.lang.nL

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
"""}}
}
"""
}
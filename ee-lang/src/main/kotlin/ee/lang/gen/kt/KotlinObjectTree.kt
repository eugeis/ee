package ee.lang.gen.kt

import ee.lang.*

fun <T : AttributeI<*>> T.toKotlinObjectTreeProp(c: GenerationContext, derived: String): String {
    return """
    val ${name()} = ${c.n(type(), derived)}()"""
}

fun <T : CompilationUnitI<*>> T.toKotlinObjectTree(c: GenerationContext,
    derived: String = LangDerivedKind.IMPL): String {
    return """
object ${c.n(this)} : ${c.n(derivedFrom())}() {${props().joinToString(nL) { it.toKotlinObjectTreeProp(c, derived) }}
}"""
}
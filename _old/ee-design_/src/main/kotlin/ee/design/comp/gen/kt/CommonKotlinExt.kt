package ee.design.comp.gen.kt

import ee.design.comp.Basic
import ee.design.comp.Commands
import ee.design.comp.Controller
import ee.design.comp.Queries
import ee.design.*
import ee.design.gen.kt.KotlinContext

fun Basic.toKotlinImpl(context: KotlinContext, indent: String = "", derived: TypeDerived<CompilationUnitD> = api): String {
    val newIndent = "$indent$tab"
    val classDef = toKotlinClassDef(context, indent, derived)
    val signatureIndent = "".padEnd(classDef.length + 1)
    return """${toKotlinComment(context, indent)}$classDef${primaryConstructor.toKotlinPrimary(context, signatureIndent, superUnit)} {
${toKotlinCompanion(context, newIndent, api)}
${propsExceptPrimaryConstructor.joinToString(nL) { it.toKotlinMember(context, newIndent) }}
${otherConstructors.joinWrappedToString(nL) { it.toKotlin(context, newIndent, primaryConstructor) }}
${operations.joinToString(nL) { it.toKotlinImpl(context, newIndent) }}
$indent}
${toKotlinOrEmptyMethod(context, indent, derived)}"""
}

fun Commands.toKotlinImpl(context: KotlinContext, indent: String = "",
                          derived: TypeDerived<CompilationUnit> = base.ifElse(apiBase, api)): String {
    return (this as Controller).toKotlinImpl(context, indent, derived)
}

fun Queries.toKotlinImpl(context: KotlinContext, indent: String = "",
                         derived: TypeDerived<CompilationUnit> = base.ifElse(apiBase, api)): String {
    return (this as Controller).toKotlinImpl(context, indent, derived)
}
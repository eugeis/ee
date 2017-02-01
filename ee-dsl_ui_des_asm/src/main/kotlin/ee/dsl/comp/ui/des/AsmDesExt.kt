package ee.design.ui.des

import ee.common.ext.toCamelCase
import ee.design.GenerationContext
import ee.design.nL
import ee.design.tab
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode

fun ClassNode.toDesEnum(context: GenerationContext, indent: String = ""): String {
    val newIndent = "$indent$tab"
    val fieldDesc = "L$name;"
    return """${indent}object ${name.substringAfterLast("/")} : EnumType() {
${fields.filter { it.desc.equals(fieldDesc) }.joinToString(nL) { it.toDesLit(context, newIndent) }}
$indent}"""
}

fun ClassNode.toDesWidget(context: GenerationContext, indent: String = ""): String {
    val newIndent = "$indent$tab"
    return """${indent}object ${name.substringAfterLast("/")} : Widget() {
${fields.filter { !it.name.startsWith("$") && !it.name.startsWith("_") }.joinToString(nL) {
        it.toDesProp(context, newIndent)
    }}
$indent}"""
}

fun FieldNode.toDesLit(context: GenerationContext, indent: String = ""): String {
    return """${indent}val ${name.toLowerCase().toCamelCase().capitalize()} = lit()"""
}

fun FieldNode.toDesProp(context: GenerationContext, indent: String = ""): String {
    return """${indent}val ${name} = prop()"""
}

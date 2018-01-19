package ee.design.gen.ts

import ee.common.ext.ifElse
import ee.common.ext.toUnderscoredUpperCase
import ee.design.*

class TypeScriptContext : GenerationContext {

    constructor(namespace: String = "", header: String = "", footer: String = "") : super(namespace, header, footer)

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${toPackage(indent)}${toImports(indent)}$content"
    }

    private fun toPackage(indent: String): String {
        return namespace.isEmpty().ifElse("", { "${indent}package $namespace;${nL}${nL}" })
    }

    private fun toImports(indent: String): String {
        return types.isEmpty().ifElse("", {
            types.filter { it.namespace.isNotEmpty() && it.namespace != namespace }
                .map { "${it.namespace}.${it.name};" }.sorted().joinToString(nL, indent)
        })
    }
}

fun <T : ElementIfc> T.toTypeScript(context: TypeScriptContext, indent: String = ""): String {
    if (this is TextElement) {
        return "$indent$text"
    } else {
        return "$indent$name"
    }
}

fun <T : TextElement> T.toTypeScript(context: TypeScriptContext, indent: String = ""): String {
    return "$indent$text"
}

fun <T : Comment> T.toTypeScript(context: TypeScriptContext, indent: String = ""): String {
    if (children.size == 1 && !children.first().toTypeScript(context).contains("\n")) {
        return "$indent/* ${children.first().toTypeScript(context)} */$nL"
    } else {
        val newIndent = "$indent$tab"
        return "/*${nL}${children.joinToString(nL) { it.toTypeScript(context, newIndent) }}$indent*/${nL}"
    }
}

fun <T : Element> T.toTypeScriptComment(context: TypeScriptContext, indent: String = ""): String {
    return when (doc) {
        null          -> ""
        Comment.EMPTY -> ""
        else          -> "${doc?.toTypeScript(context, indent)}"
    }
}

fun <T : TypeD> T.toTypeScript(context: TypeScriptContext, indent: String = ""): String {
    return when (this) {
        td.String  -> "${indent}string"
        td.Boolean -> "${indent}boolea"
        else       -> "$indent$name"
    }
}

fun <T : TypeD> T.toTypeScriptReturnDefault(context: TypeScriptContext, indent: String = ""): String {
    if (this == td.void) {
        return ""
    } else {
        return "${indent}null"
    }
}

fun <T : Attribute> T.toTypeScriptGetterIfc(context: TypeScriptContext, indent: String = ""): String {
    return "${toTypeScriptComment(context, indent)}$indent$name(): ${type.toTypeScript(context, indent)};"
}

fun <T : Attribute> T.toTypeScriptSetterIfc(context: TypeScriptContext, indent: String = ""): String {
    return "${toTypeScriptComment(context, indent)}$indent$name($name: ${type.toTypeScript(context)});"
}

fun <T : Literal> T.toTypeScriptLiteral(context: TypeScriptContext, indent: String = "",
    mappings: T.(context: TypeScriptContext, indent: String) -> String = { c, ind -> "" }): String {
    return """${toTypeScriptComment(context, indent)}${mappings(context,
        indent)}$indent${name.toUnderscoredUpperCase()}"""
}

fun <T : Attribute> T.toTypeScriptMember(context: TypeScriptContext, indent: String = "",
    mappings: T.(context: TypeScriptContext, indent: String) -> String = { c, ind -> "" },
    modifier: String = ""): String {
    return """${toTypeScriptComment(context, indent)}${mappings(context,
        indent)}$indent$modifier$name: ${type.toTypeScript(context)};"""
}

fun <T : Attribute> T.toTypeScriptGetterImpl(context: TypeScriptContext, indent: String = "",
    mappings: T.(context: TypeScriptContext, indent: String) -> String = { c, ind -> "" },
    modifier: String = ""): String {
    val newIndent = "$indent${tab}"
    return """${toTypeScriptComment(context, indent)}${mappings(context,
        indent)}$indent$modifier$name(): ${type.toTypeScript(context)} {
${newIndent}return $name;
$indent}
"""
}

fun <T : Attribute> T.toTypeScriptSetterImpl(context: TypeScriptContext, indent: String = "",
    mappings: T.(context: TypeScriptContext, indent: String) -> String = { c, ind -> "" },
    modifier: String = ""): String {
    val newIndent = "$indent${tab}"
    return """${toTypeScriptComment(context, indent)}${mappings(context,
        indent)}$indent$modifier$name($type.to name: ${type.toTypeScript(context)}) {
${newIndent}this.$name = $name;
$indent}
"""
}

fun <T : Attribute> T.toTypeScriptSignatureReturn(context: TypeScriptContext, indent: String = ""): String {
    return "${type.toTypeScript(context, indent)}"
}

fun <T : Attribute> T.toTypeScriptReturnDefault(context: TypeScriptContext, indent: String): String {
    return "${type.toTypeScriptReturnDefault(context, indent)}"
}

fun <T : Attribute> T.toTypeScriptSignature(context: TypeScriptContext, indent: String = "",
    mappings: T.(context: TypeScriptContext, indent: String) -> String = { c, ind -> "" }): String {
    return "${toTypeScriptComment(context, indent)}${mappings(context, indent)}$indent$name: ${type.toTypeScript(
        context)}"
}

fun List<Attribute>.toTypeScriptSignature(context: TypeScriptContext, indent: String): String {
    return "${joinToString(", ") { it.toTypeScriptSignature(context, indent) }}"
}

fun <T : Operation> T.toTypeScriptIfc(context: TypeScriptContext, indent: String = "",
    mappings: T.(context: TypeScriptContext, indent: String) -> String = { c, ind -> "" }): String {
    return "${toTypeScriptComment(context, indent)}${mappings(context,
        indent)}$indent$name(${params.toTypeScriptSignature(context, indent)}): ${ret?.toTypeScriptSignatureReturn(
        context)};"
}

fun <T : Operation> T.toTypeScriptImpl(context: TypeScriptContext, indent: String = "",
    mappings: T.(context: TypeScriptContext, indent: String) -> String = { c, ind -> "" },
    modifier: String = ""): String {
    return """${toTypeScriptComment(context, indent)}${mappings(context,
        indent)}$indent$modifier$name(${params.toTypeScriptSignature(context,
        indent)}): ${ret?.toTypeScriptSignatureReturn(context)} {
${ret?.toTypeScriptReturnDefault(context, indent)}
$indent}
"""
}

fun CompilationUnitD.toTypeScriptIfc(context: TypeScriptContext, indent: String = "",
    derived: TypeDerived<CompilationUnitD> = api): String {
    val newIndent = "$indent${tab}"
    return """${toTypeScriptComment(context, indent)}${indent}export interface ${derived.name} {
${props.joinToString(nL) { it.toTypeScriptMember(context, newIndent) }}
${operations.joinToString(nL) { it.toTypeScriptIfc(context, newIndent) }}
$indent}
"""
}

fun CompilationUnitD.toTypeScriptImpl(context: TypeScriptContext, indent: String = "",
    derived: TypeDerived<CompilationUnitD> = api): String {
    val newIndent = "$indent${tab}"
    return """${toTypeScriptComment(context, indent)}${indent}export class ${derived.name} {
${props.joinToString(nL) { it.toTypeScriptMember(context, newIndent) }}
${operations.joinToString(nL) { it.toTypeScriptImpl(context, newIndent) }}
$indent}
"""
}

fun EnumTypeD.toTypeScriptEnum(context: TypeScriptContext, indent: String = "",
    derived: TypeDerived<EnumTypeD> = enum): String {
    val newIndent = "$indent${tab}"
    return """${toTypeScriptComment(context, indent)}${indent}export enum ${derived.name} {
${literals.joinToString(",${nL}") { it.toTypeScriptLiteral(context, newIndent) }}
${props.joinToString(nL) { it.toTypeScriptMember(context, newIndent) }}
${findAllByType(OperationI::class.java).joinToString(nL) { it.toTypeScriptImpl(context, newIndent) }}
$indent}
"""
}

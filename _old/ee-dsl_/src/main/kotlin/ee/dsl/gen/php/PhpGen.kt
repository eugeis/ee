package ee.design.gen.php

import ee.common.ext.ifElse
import ee.common.ext.joinWrappedToString
import ee.common.ext.toUnderscoredUpperCase
import ee.design.*
import ee.design.gen.java.Java

class PhpContext : GenerationContext {

    constructor(namespace: String = "", header: String = "", footer: String = "") : super(namespace, header, footer)

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${toPackage(indent)}${toImports(indent)}$content"
    }

    private fun toPackage(indent: String): String {
        return namespace.isEmpty().ifElse("", { "${indent}package $namespace$nL$nL" })
    }

    private fun toImports(indent: String): String {
        return types.isEmpty().ifElse("", {
            val outsideTypes = types.filter { it.namespace.isNotEmpty() && it.namespace != namespace }
            outsideTypes.isEmpty().ifElse("", {
                "${outsideTypes.map { "${indent}import ${it.namespace}.${it.name}" }.sorted().
                        joinToString(nL)}${nL}${nL}"
            })
        })
    }
}

class Php : StructureUnit("Php") {
    companion object Core : StructureUnit("") {
        val List = type()
    }
}

fun <T : ElementIfc> T.toPhp(context: PhpContext, indent: String = ""): String {
    if (this is TextElement) {
        return "$indent$text"
    } else if (this is Literal) {
        return "$indent${name.toUnderscoredUpperCase()}"
    } else {
        return "$indent$name"
    }
}

fun <T : TextElement> T.toPhp(context: PhpContext, indent: String = ""): String {
    return "$indent$text"
}

fun <T : Comment> T.toPhp(context: PhpContext, indent: String = ""): String {
    if (children.size == 1 && !children.first().toPhp(context).contains("\n")) {
        return "$indent/* ${children.first().toPhp(context)} */${nL}"
    } else {
        val newIndent = "$indent$tab"
        return "/*$nL${children.joinToString(nL) { it.toPhp(context, newIndent) }}$indent*/${nL}"
    }
}

fun <T : Element> T.toPhpComment(context: PhpContext, indent: String = ""): String {
    return when (doc) {
        null -> ""
        Comment.EMPTY -> ""
        else -> "${doc?.toPhp(context, indent)}"
    }
}

fun <T : TypeD> T.toPhp(context: PhpContext, indent: String = ""): String {
    return when (this) {
        td.String -> "String"
        td.Boolean -> "Boolean"
        td.Integer -> "Integer"
        td.Long -> "Long"
        td.Float -> "Float"
        td.Date -> context.n(Java.util.Date)
        else -> "$indent$name"
    }
}

fun <T : TypeD> T.toPhpValue(context: PhpContext): String {
    if (this is NativeType) {
        return when (this) {
            td.String -> "''"
            td.Boolean -> "false"
            td.Integer -> "0"
            td.Long -> "0l"
            td.Float -> "0f"
            td.Date -> "${td.Date.toPhp(context)}()"
            else -> "\"\""
        }
    } else if (this is ExternalType) {
        return "null"
    } else if (this is EnumTypeD) {
        return "${context.n(this)}.${this.literals.first().toPhp(context)}"
    } else if (this is CompilationUnit) {
        return "${context.n(this)}.EMPTY"
    } else {
        return "null"
    }
}

fun <T : Attribute> T.toPhpGetterIfc(context: PhpContext, indent: String = ""): String {
    return "${toPhpComment(context, indent)}$indent$name(): ${type.toPhp(context, indent)}"
}

fun <T : Attribute> T.toPhpSetterIfc(context: PhpContext, indent: String = ""): String {
    return "${toPhpComment(context, indent)}$indent$name($name: ${type.toPhp(context)})"
}

fun <T : Literal> T.toPhpLiteral(context: PhpContext, indent: String = "", mappings: T.(context: PhpContext, indent: String) -> String = { c, ind -> "" }): String {
    return """${toPhpComment(context, indent)}${mappings(context, indent)}${indent}const ${name.toUnderscoredUpperCase()} = '$name';"""
}

fun <T : Attribute> T.toPhpTypeDef(context: PhpContext): String {
    if (type.multi) {
        return ": ${context.n(Php.List)}<${type.toPhp(context)}>"
    } else {
        return ": ${type.toPhp(context)}"
    }
}

fun <T : Attribute> T.toPhpMember(context: PhpContext, indent: String = "", mappings: T.(context: PhpContext, indent: String) -> String = { c, ind -> "" }, modifier: String = "public "): String {
    return "${toPhpComment(context, indent)}${mappings(context, indent)}$indent$modifier$$name${toPhpTypeDef(context)}${toPhpInit(context, true)}"
}

fun <T : Attribute> T.toPhpGetterImpl(context: PhpContext, indent: String = "", mappings: T.(context: PhpContext, indent: String) -> String = { c, ind -> "" }, modifier: String = ""): String {
    val newIndent = "$indent$tab"
    return """${toPhpComment(context, indent)}${mappings(context, indent)}$indent$modifier$name(): ${type.toPhp(context)} {
${newIndent}return $name
$indent}
"""
}

fun <T : Attribute> T.toPhpAssign(context: PhpContext, indent: String = ""): String {
    return "${indent}this.$name = $name"
}

fun <T : Attribute> T.toPhpSetterImpl(context: PhpContext, indent: String = "", mappings: T.(context: PhpContext, indent: String) -> String = { c, ind -> "" }, modifier: String = ""): String {
    val newIndent = "$indent$tab"
    return """${toPhpComment(context, indent)}${mappings(context, indent)}$indent$modifier$name($type.to name: ${type.toPhp(context)}) {
${toPhpAssign(context, newIndent)}
$indent}
"""
}

fun <T : Attribute> T.toPhpValue(context: PhpContext): String {
    //@TODO use value
    return toPhpValueEmpty(context)
}

fun <T : Attribute> T.toPhpReturn(context: PhpContext, indent: String): String {
    if (type == td.void) {
        return ""
    } else {
        return "${indent}return ${toPhpValue(context)}"
    }
}

fun <T : Attribute> T.toPhpInit(context: PhpContext, mustInit: Boolean): String {
    if (value != null) {
        return " = ${toPhpValue(context)}"
    } else if (nullable) {
        return " = null"
    } else if (mustInit) {
        return " = ${toPhpValue(context)}"
    } else {
        return ""
    }
}

fun <T : Attribute> T.toPhpSignature(context: PhpContext, indent: String = "", definition: Boolean = false, modifier: String = "",
                                     mappings: T.(context: PhpContext, indent: String) -> String = { c, ind -> "" }): String {
    return "${toPhpComment(context, indent)}${mappings(context, indent)}$modifier$$name${toPhpTypeDef(context)}${toPhpInit(context, true)}"
}

fun List<Attribute>.toPhpSignature(context: PhpContext, indent: String, definition: Boolean = false, modifier: String = ""): String {
    return "${joinWrappedToString(", ", indent) { it.toPhpSignature(context, indent, definition && !it.inherited, modifier) }}"
}

fun List<Attribute>.toPhpMember(context: PhpContext, indent: String, modifier: String = ""): String {
    return "${joinWrappedToString(", ", indent) { it.toPhpSignature(context, indent, true && !it.inherited, modifier) }}"
}

fun <T : Operation> T.toPhpIfc(context: PhpContext, indent: String = "",
                               mappings: T.(context: PhpContext, indent: String) -> String = { c, ind -> "" }): String {
    return "${toPhpComment(context, indent)}${mappings(context, indent)}$indent$name(${params.toPhpSignature(context, indent)})${ret?.toPhpTypeDef(context)}"
}

fun <T : Operation> T.toPhpImpl(context: PhpContext, indent: String = "",
                                mappings: T.(context: PhpContext, indent: String) -> String = { c, ind -> "" }, modifier: String = ""): String {
    return """${toPhpComment(context, indent)}${mappings(context, indent)}$indent$modifier$name(${params.toPhpSignature(context, indent)})${ret?.toPhpTypeDef(context)} {
${ret?.toPhpReturn(context, indent)}
$indent}
"""
}

fun <T : Attribute> T.toPhpCall(context: PhpContext, indent: String = ""): String {
    return "$name"
}

fun <T : Attribute> T.toPhpValueEmpty(context: PhpContext, indent: String = ""): String {
    if (nullable) {
        return "null"
    } else if (type.multi && mutable) {
        return "arrayListOf()"
    } else if (type.multi) {
        return "emptyList()"
    } else {
        return "${type.toPhpValue(context)}"
    }
}

fun List<Attribute>.toPhpCall(context: PhpContext, indent: String): String {
    return "${joinWrappedToString(", ", indent) { it.toPhpCall(context, indent) }}"
}

fun List<Attribute>.toPhpValueEmpty(context: PhpContext, indent: String): String {
    return "${joinWrappedToString(", ", indent) { it.toPhpValueEmpty(context, indent) }}"
}

fun <T : LogicUnit> T.toPhpCall(context: PhpContext, indent: String = "", name: String = ""): String {
    return "$name(${params.toPhpCall(context, indent)})"
}

fun <T : LogicUnit> T.toPhpValueEmpty(context: PhpContext, indent: String = "", name: String = ""): String {
    return "$name(${params.toPhpValueEmpty(context, indent)})"
}

fun <T : Constructor> T.toPhpCall(context: PhpContext, indent: String = "", name: String = "this"): String {
    if (this != Constructor.EMPTY) {
        return ": $name(${params.toPhpCall(context, "$indent$tab")})"
    } else {
        return ""
    }
}

fun <T : Constructor> T.toPhpPrimary(context: PhpContext, indent: String = "", superUnit: CompilationUnitD? = null,
                                     mappings: T.(context: PhpContext, indent: String) -> String = { c, ind -> "" },
                                     modifier: String = ""): String {
    if (this != Constructor.EMPTY) {
        if (superUnit != null) {
            return """(${params.toPhpMember(context, indent)})${superUnit.primaryConstructor.toPhpCall(context, indent, context.n(superUnit))}"""
        } else {
            return """(${params.toPhpMember(context, indent)})"""
        }
    } else {
        return ""
    }
}

fun <T : Constructor> T.toPhp(context: PhpContext, indent: String = "", superConstructor: Constructor,
                              mappings: T.(context: PhpContext, indent: String) -> String = { c, ind -> "" },
                              modifier: String = ""): String {
    val newIndent = "$indent$tab"
    return """${toPhpComment(context, indent)}${mappings(context, indent)}$indent${modifier}constructor(${params.toPhpSignature(context, newIndent)})${superConstructor.toPhpCall(context, "$newIndent$tab")} {
${substractParamsOf(superConstructor).joinToString(nL) { it.toPhpAssign(context, newIndent) }}
$indent}
"""
}

fun <T : Constructor> T.toPhpFactoryMethod(context: PhpContext, indent: String = "", derived: TypeDerived<CompilationUnitD>): String {
    val methodDef = """${indent}fun new"""
    val signatureIndent = "".padEnd(methodDef.length + 1)
    return """$methodDef(${params.toPhpSignature(context, signatureIndent)}) = ${derived.name}(${params.toPhpCall(context, signatureIndent)})"""
}

fun <T : Constructor> T.substractParamsOf(superConstructor: Constructor)
        = params.filter { param -> superConstructor.params.firstOrNull { it.name == param.name } == null }

val CompilationUnitD.primaryConstructor: Constructor
    get() = storage.getOrPut(this, "primaryConstructor", { constructors.firstOrNull() ?: Constructor.EMPTY })

val CompilationUnitD.otherConstructors: List<Constructor>
    get() = storage.getOrPut(this, "otherConstructors", { if (constructors.size > 1) constructors.subList(1, constructors.size) else emptyList() })

val Constructor.props: List<Attribute>
    get() = storage.getOrPut(this, "props", { params?.filterIsInstance(PropAttributeI::class.java)?.map { it.prop!! } })

val CompilationUnitD.propsExceptPrimaryConstructor: List<Attribute>
    get() = storage.getOrPut(this, "propsExceptPrimaryConstructor", { if (primaryConstructor != Constructor.EMPTY) props.filter { !primaryConstructor.props.contains(it) } else props })

fun CompilationUnitD.toPhpIfc(context: PhpContext, indent: String = "", derived: TypeDerived<CompilationUnitD> = api): String {
    val newIndent = "$indent$tab"
    return """${toPhpComment(context, indent)}${indent}interface ${derived.name} {
${props.joinToString(nL) { it.toPhpMember(context, newIndent) }}
${operations.joinToString(nL) { it.toPhpIfc(context, newIndent) }}
$indent}"""
}

fun CompilationUnitD.toPhpExtends(context: PhpContext, indent: String = ""): String {
    return (superUnit != null).ifElse({ "${superUnit?.primaryConstructor?.toPhp(context, indent)}" }, "")
}

fun CompilationUnitD.toPhpClassDef(context: PhpContext, indent: String, derived: TypeDerived<CompilationUnitD>): String {
    return """$indent${virtual.ifElse("abstract ", "")}class ${derived.name}"""
}

fun CompilationUnitD.toPhpImpl(context: PhpContext, indent: String = "", derived: TypeDerived<CompilationUnitD> = api): String {
    val newIndent = "$indent$tab"
    val classDef = toPhpClassDef(context, indent, derived)
    val signatureIndent = "".padEnd(classDef.length + 1)
    return """${toPhpComment(context, indent)}$classDef${primaryConstructor.toPhpPrimary(context, signatureIndent, superUnit)} {
${propsExceptPrimaryConstructor.joinToString(nL) { it.toPhpMember(context, newIndent) }}
${otherConstructors.joinWrappedToString(nL) { it.toPhp(context, newIndent, primaryConstructor) }}
${operations.joinToString(nL) { it.toPhpImpl(context, newIndent) }}
$indent}"""
}

fun EnumTypeD.toPhpEnum(context: PhpContext, indent: String = "", derived: TypeDerived<EnumTypeD> = enum): String {
    val newIndent = "$indent$tab"
    return """${toPhpComment(context, indent)}${indent}abstract class ${derived.name} {
${literals.joinToString(nL) { it.toPhpLiteral(context, newIndent) }}
${props.joinToString(nL) { it.toPhpMember(context, newIndent) }}
${operations.joinToString(nL) { it.toPhpImpl(context, newIndent) }}
$indent}"""
}
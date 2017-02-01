package ee.design.gen.java

import ee.common.ext.addReturn
import ee.common.ext.emptyOr
import ee.common.ext.ifElse
import ee.common.ext.joinWrappedToString
import ee.design.*

object Java : StructureUnit("Java") {
    object util : StructureUnit("java.util") {
        val Date = type()

        object concurrent : StructureUnit("java.util.concurrent") {
            val TimeUnit = type()
        }
    }

    object nioFile : StructureUnit("java.nio.file") {
        val Path = type()
        val Paths = type()
    }
}

fun override(condition: Boolean, indent: String): String = emptyOr(condition, { "$indent@Override${nL}" })
fun serialVersionUID(condition: Boolean, indent: String): String =
        emptyOr(condition, { "${indent}private static final long serialVersionUID = 1L;${nL}" })

fun override(indent: String): String = "$indent@Override${nL}"

fun <T : TypeD> T.toJava(context: JavaContext, indent: String = ""): String {
    return context.n(this, indent)
}

fun <T : TypeD> T.toJavaReturnDefault(context: JavaContext, indent: String): String {
    if (this == td.void) {
        return ""
    } else {
        return "${indent}null"
    }
}

fun <T : Attribute> T.toJavaGetterIfc(context: JavaContext, indent: String): String {
    return "${type.toJava(context, indent)} get${name.capitalize()}();"
}

fun <T : Attribute> T.toJavaSetterIfc(context: JavaContext, indent: String): String {
    return "${indent}td.void set${name.capitalize()}(${type.toJava(context)} $name);"
}

fun <T : Attribute> T.toJavaMember(context: JavaContext, indent: String,
                                   mappings: String = "",
                                   modifier: String = "protected"): String {
    return """$mappings$indent$modifier ${type.toJava(context)} $name;"""
}

fun <T : Attribute> T.toJavaGetterImpl(context: JavaContext, indent: String,
                                       mappings: String = "",
                                       modifier: String = "public"): String {
    val newIndent = "$indent$tab"
    return """$mappings$indent$modifier ${type.toJava(context)} get${name.capitalize()}() {
${newIndent}return $name;
$indent}
"""
}

fun <T : Attribute> T.toJavaSetterImpl(context: JavaContext, indent: String,
                                       mappings: String = "", modifier: String = "public"): String {
    val newIndent = "$indent$tab"
    return """$mappings$indent$modifier td.void set${name.capitalize()}(${type.toJava(context)} $name) {
${newIndent}this.$name = $name;
$indent}
"""
}

fun <T : Attribute> T.toJavaSignatureReturn(context: JavaContext, indent: String = ""): String {
    return "${type.toJava(context, indent)}"
}

fun <T : Attribute> T.toJavaReturnDefault(context: JavaContext, indent: String): String {
    return "${type.toJavaReturnDefault(context, indent)}"
}

fun <T : Attribute> T.toJavaSignature(context: JavaContext, indent: String,
                                      mappings: String = ""): String {
    return "$mappings$indent${type.toJava(context)} $name"
}

fun <T : Attribute> T.toJavaAnnotationInit(context: JavaContext, indent: String): String {
    return "$name"
}

fun List<Attribute>.toJavaSignature(context: JavaContext, indent: String): String {
    return "$indent${joinWrappedToString(", ", indent) { it.toJavaSignature(context, indent) }}"
}

fun List<Attribute>.toJavaAnnotationInit(context: JavaContext, indent: String): String {
    return "$indent${joinWrappedToString(", ", indent) { it.toJavaSignature(context, indent) }}"
}

fun <T : Operation> T.toJavaIfc(context: JavaContext, indent: String, mappings: String = ""): String {
    return "$mappings$indent${ret?.toJavaSignatureReturn(context)} $name(${params.toJavaSignature(context, indent)});"
}

fun <T : Operation> T.toJavaImpl(context: JavaContext, indent: String, mappings: String = "",
                                 modifier: String = "public"): String {
    return """$mappings$indent$modifier ${ret?.toJavaSignatureReturn(context)} $name(${params.toJavaSignature(context, indent)}) {
${ret?.toJavaReturnDefault(context, indent)}
$indent}
"""
}

class JavaContext : GenerationContext {

    constructor(namespace: String = "", header: String = "", footer: String = "") : super(namespace, header, footer)

    /* Annotation */
    fun ann(item: TypeD, indent: String = "", vararg params: Attribute): String {
        return "$indent@${types.addReturn(item).name}${params.toList().joinWrappedToString(",", indent, "(", ")") {
            "${it.name} = ${it.value}"
        }}"
    }

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${toPackage(indent)}${toImports(indent)}$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return namespace.isEmpty().ifElse("", { "${indent}package $namespace;$nL$nL" })
    }

    private fun toImports(indent: String): String {
        return types.isEmpty().ifElse("", {
            val outsideTypes = types.filter { it.namespace.isNotEmpty() && it.namespace != namespace }
            outsideTypes.isEmpty().ifElse("", {
                "${outsideTypes.map { "${indent}import ${it.namespace}.${it.name};" }.sorted().
                        joinToString(nL)}$nL$nL"
            })
        })
    }
}

//completed
fun CompilationUnitD.toJavaIfc(context: JavaContext, indent: String = "", derived: TypeDerived<CompilationUnitD> = api): String {
    val newIndent = "$indent$tab"
    return context.complete("""${indent}public interface ${derived.name} {
${operations.joinToString(nL) { it.toJavaIfc(context, newIndent) }}
${props.joinToString(nL) { it.toJavaGetterIfc(context, newIndent) }}
${props.joinToString(nL) { it.toJavaSetterIfc(context, newIndent) }}
$indent}
""", indent)
}

fun CompilationUnitD.toJavaImpl(context: JavaContext, indent: String = "", derived: TypeDerived<CompilationUnitD> = impl, serializable: Boolean = true): String {
    val newIndent = "$indent$tab"
    return context.complete("""${indent}public class ${derived.name} implements ${api.name} {
${serialVersionUID(serializable, newIndent)}
${props.joinToString(nL) { it.toJavaMember(context, newIndent) }}
${operations.joinToString(nL) { it.toJavaImpl(context, newIndent) }}
${props.joinToString(nL) { it.toJavaGetterImpl(context, newIndent, override(newIndent)) }}
${props.joinToString(nL) { it.toJavaSetterImpl(context, newIndent, override(newIndent)) }}
$indent}
""", indent)
}


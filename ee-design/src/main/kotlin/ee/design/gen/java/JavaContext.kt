package ee.design.gen.java

import ee.common.ext.addReturn
import ee.common.ext.joinWrappedToString
import ee.common.ext.then
import ee.design.AttributeI
import ee.design.GenerationContext
import ee.design.TypeI
import ee.design.nL

open class JavaContext : GenerationContext {

    constructor(namespace: String = "", header: String = "", footer: String = "") : super(namespace, header, footer)

    /* Annotation */
    fun ann(item: TypeI, vararg params: AttributeI): String {
        return "@${types.addReturn(item).name()}${params.toList().joinWrappedToString(",", prefix = "(", postfix = ")") {
            "${it.name()} = ${it.value()}"
        }}"
    }

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${toPackage(indent)}${toImports(indent)}$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return namespace.isNotEmpty().then { "${indent}package $namespace;$nL$nL" }
    }

    private fun toImports(indent: String): String {
        return types.isNotEmpty().then {
            val outsideTypes = types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace }
            outsideTypes.isNotEmpty().then {
                "${outsideTypes.map { "${indent}import ${it.namespace()}.${it.name()};" }.sorted().
                        joinToString(nL)}$nL$nL"
            }
        }
    }
}
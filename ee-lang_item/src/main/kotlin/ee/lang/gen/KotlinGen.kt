package ee.lang.gen

import ee.common.ext.then
import ee.lang.GenerationContext
import ee.lang.nL

open class KotlinContext : GenerationContext {

    constructor(namespace: String = "", header: String = "", footer: String = "") : super(namespace, header, footer)

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${toPackage(indent)}${toImports(indent)}$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return namespace.isNotEmpty().then { "${indent}package $namespace$nL$nL" }
    }

    private fun toImports(indent: String): String {
        return types.isNotEmpty().then {
            val outsideTypes = types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace }
            outsideTypes.isNotEmpty().then {
                "${outsideTypes.map { "${indent}import ${it.namespace()}.${it.name()}" }.sorted().
                        joinToString(nL)}$nL$nL"
            }
        }
    }
}
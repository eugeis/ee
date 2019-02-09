package ee.lang.gen

import ee.common.ext.then
import ee.lang.*

open class KotlinContextBuilder<M>(name: String, val scope: String, macroController: MacroController,
                                   builder: M.() -> KotlinContext) : ContextBuilder<M>(name, macroController, builder)

open class KotlinContext : GenerationContext {
    constructor(namespace: String = "", moduleFolder: String = "", genFolder: String = "src-gen/main/kotlin",
                genFolderDeletable: Boolean = true, genFolderDeletePattern: Regex? = null,
                derivedController: DerivedController,
                macroController: MacroController) : super(namespace, moduleFolder,
            genFolder, genFolderDeletable, genFolderDeletePattern, derivedController, macroController)

    override fun complete(content: String, indent: String): String {
        val toImports = toImports(indent)
        return "${toHeader(indent)}${toPackage(indent)}$toImports$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return namespace.isNotEmpty().then { "${indent}package $namespace$nL$nL" }
    }

    private fun toImports(indent: String): String {
        return types.isNotEmpty().then {
            val all = types.map { "${it.namespace()}.${it.name()}" }.joinToString(";")
            val outsideTypes = types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace }
            outsideTypes.isNotEmpty().then {
                "${outsideTypes.map { "${indent}import ${it.namespace()}.${it.name()}" }.toSortedSet().joinToString(
                        nL)}$nL$nL"
            }
        }
    }

    companion object {
        const val CONTEXT_KOTLIN = "kotlin"
    }
}

val itemAndTemplateNameAsKotlinFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name().capitalize()}${name.capitalize()}.kt")
}

val templateNameAsKotlinFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("$name.kt")
}

val itemNameAsKotlinFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name()}.kt")
}

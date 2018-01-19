package ee.lang.gen

import ee.common.ext.then
import ee.lang.*

open class KotlinContext : GenerationContext {
    constructor(namespace: String = "", moduleFolder: String = "", genFolder: String = "src-gen/main/kotlin",
        genFolderDeletable: Boolean = true, genFolderDeletePattern: Regex? = null,
        derivedController: DerivedController = DerivedController(DerivedStorage())) : super(namespace, moduleFolder,
        genFolder, genFolderDeletable, genFolderDeletePattern, derivedController)

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
                "${outsideTypes.map { "${indent}import ${it.namespace()}.${it.name()}" }.sorted().joinToString(
                    nL)}$nL$nL"
            }
        }
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

package ee.lang.gen.doc

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.KotlinContext


open class MkContextBuilder<M>(name: String, val scope: String, macroController: MacroController,
                                   builder: M.() -> MkContext) : ContextBuilder<M>(name, macroController, builder)

open class MkContext : GenerationContext {
    val namespaceLastPart: String

    constructor(namespace: String = "", moduleFolder: String = "", genFolder: String = "src/main/doc",
        genFolderDeletable: Boolean = false, genFolderPatternDeletable: Regex? = ".*Base.mk".toRegex(),
        derivedController: DerivedController, macroController: MacroController)
            : super(namespace, moduleFolder, genFolder,
        genFolderDeletable, genFolderPatternDeletable, derivedController, macroController) {
        namespaceLastPart = namespace.substringAfterLast(".")
    }

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${toPackage(indent)}${toImports(indent)}$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return namespaceLastPart.isNotEmpty().then { "${indent}package $namespaceLastPart$nL$nL" }
    }

    private fun toImports(indent: String): String {
        return ""
    }

    override fun n(item: ItemI<*>, derivedKind: String): String {
        val derived = types.addReturn(derivedController.derive(item, derivedKind))
        if (derived.namespace().isEmpty() || derived.namespace().equals(namespace, true)) {
            return derived.name()
        } else {
            return """${derived.namespace().substringAfterLast(".").toLowerCase()}.${derived.name()}"""
        }
    }
}

val itemAndTemplateNameAsMkFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name().capitalize()}${name.capitalize()}.mk")
}

val templateNameAsMarkdownFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("$name.mk")
}

val itemNameAsMarkdownFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name()}.mk")
}
package ee.lang.gen.doc

import ee.common.ext.*
import ee.lang.*

open class MkContext (
    var alwaysImportTypes: Boolean = false,
    namespace: String = "",
    moduleFolder: String = "",
    genFolder: String = "src/main/doc",
    genFolderDeletable: Boolean = false,
    genFolderPatternDeletable: Regex? = ".*Base.mk".toRegex(),
    derivedController: DerivedController,
    macroController: MacroController) : GenerationContext(
    namespace, moduleFolder, genFolder,
    genFolderDeletable, genFolderPatternDeletable, derivedController, macroController
) {

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${toPackage(indent)}${toImports(indent)}$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return namespace.substringAfterLast(".").isNotEmpty().then { "${indent}package ${namespace.substringAfterLast(".")}$nL$nL" }
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

fun <T : StructureUnitI<*>> T.prepareForMarkdownGeneration(): T {
    initsForMarkdownGeneration()
    extendForMarkdownGenerationLang()
    return this
}

fun <T : StructureUnitI<*>> T.initsForMarkdownGeneration(): T {
    initObjectTrees()
    return this
}

fun <T : StructureUnitI<*>> T.extendForMarkdownGenerationLang(): T {
    //declare as 'isBase' all compilation units with non implemented operations.
    declareAsBaseWithNonImplementedOperation()

    prepareAttributesOfEnums()

    defineSuperUnitsAsAnonymousProps()

    defineConstructorNoProps()
    return this
}

val itemAndTemplateNameAsMkFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name().capitalize()}${name.capitalize()}.puml")
}

val templateNameAsMarkdownFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("$name.puml")
}

val itemNameAsMarkdownFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name()}.md")
}

val markdownClassDiagram = MarkdownNames("ClassDiagram")

class MarkdownNames(private val elementType: String) {

    val puml: TemplateI<*>.(CompositeI<*>) -> Names = baseName("puml")

    private fun baseName(extension: String): TemplateI<*>.(CompositeI<*>) -> Names = {
        Names("${it.toMarkdownFileNameBase(elementType)}.$extension")
    }
}

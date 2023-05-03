package ee.lang.gen.puml.classdiagram

import ee.lang.*
import java.util.*

open class CdContext(
    var alwaysImportTypes: Boolean = false,
    namespace: String = "",
    moduleFolder: String = "",
    genFolder: String = "src/app/shared",
    genFolderDeletable: Boolean = false,
    genFolderPatternDeletable: Regex? = ".*ClassDiagram.puml".toRegex(),
    derivedController: DerivedController,
    macroController: MacroController) : GenerationContext(
    namespace, moduleFolder, genFolder,
    genFolderDeletable, genFolderPatternDeletable, derivedController, macroController
) {

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${toPackage(indent)}${toImports(indent)}$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return "" //namespaceLastPart.isNotEmpty().then { "${indent}package $namespaceLastPart${nL}${nL}" }
    }

    private fun toImports(indent: String): String {
        return ""
    }

}

fun <T : StructureUnitI<*>> T.prepareForCdGeneration(): T {
    initsForCdGeneration()
    extendForCdGenerationLang()
    return this
}

fun <T : StructureUnitI<*>> T.initsForCdGeneration(): T {
    initObjectTrees()
    return this
}

fun <T : StructureUnitI<*>> T.extendForCdGenerationLang(): T {
    //declare as 'isBase' all compilation units with non implemented operations.
    declareAsBaseWithNonImplementedOperation()

    prepareAttributesOfEnums()

    defineSuperUnitsAsAnonymousProps()

    defineConstructorNoProps()
    return this
}

val itemAndTemplateNameAsPumlFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}.puml")
}

val templateNameAsPumlFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("$name.puml")
}

val itemNameAsPumlFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name()}.puml")
}

val plantUmlClassDiagram = PumlNames("ClassDiagram")

class PumlNames(private val elementType: String) {

    val puml: TemplateI<*>.(CompositeI<*>) -> Names = baseName("puml")

    private fun baseName(extension: String): TemplateI<*>.(CompositeI<*>) -> Names = {
        Names("${it.toPumlCdFileNameBase(elementType)}.$extension")
    }
}

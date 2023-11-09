package ee.design.gen.doc

import ee.design.CompI
import ee.lang.*
import ee.lang.gen.doc.markdownClassDiagram
import ee.lang.gen.doc.prepareForMarkdownGeneration
import ee.lang.gen.ts.itemNameAsTsFileName
import java.nio.file.Path

open class DesignDocGenerator(val model: StructureUnitI<*>, private val targetAsSingleModule: Boolean = true) {

    fun buildDocContextFactory() = DesignDocContextFactory(targetAsSingleModule)
    fun buildDocTemplates() = DesignDocTemplates(itemNameAsTsFileName)

    fun generate(target: Path) {

        model.prepareForMarkdownGeneration()

        val generatorClassDiagram = docMarkDown("", model).generator

        generatorClassDiagram.delete(target, model)
        generatorClassDiagram.generate(target, model)
    }


    open fun docMarkDown(fileNamePrefix: String = "", model: StructureUnitI<*>): GeneratorContexts<StructureUnitI<*>> {
        val docTemplates = buildDocTemplates()
        val docContextFactory = buildDocContextFactory()

        val components: StructureUnitI<*>.() -> List<CompI<*>> = {
            if (this is CompI<*>) listOf(this) else findDownByType(CompI::class.java)
        }

        val compilationUnit: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            if (this is CompilationUnitI<*>) listOf(this) else findDownByType(CompilationUnitI::class.java)
        }

        val moduleGenerators = mutableListOf<GeneratorI<StructureUnitI<*>>>()
        val generator = GeneratorGroup(
            "doc",
            listOf(GeneratorGroupItems("doc", items = components, generators = moduleGenerators))
        )

        val docContextBuilder = docContextFactory.buildForImplOnly("")
        moduleGenerators.add(
            GeneratorItems("PlantUmlClassDiagram",
            contextBuilder = docContextBuilder, items = components,

            templates = {
                listOf(
                    docTemplates.generatePlantUmlClassDiagramComponent(markdownClassDiagram.puml)
                ) })
        )

        return GeneratorContexts(generator, docContextBuilder)
    }
}

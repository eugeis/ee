package ee.design.gen.doc

import ee.design.gen.DesignGeneratorFactory
import ee.lang.StructureUnitI
import ee.lang.gen.doc.prepareForMarkdownGeneration
import java.nio.file.Path

open class DesignDocGenerator(val model: StructureUnitI<*>) {

    fun generate(target: Path) {

        val generatorFactory = DesignGeneratorFactory()
        model.prepareForMarkdownGeneration()

        val generatorClassDiagram = generatorFactory.docMarkDown("", model).generator

        generatorClassDiagram.delete(target, model)
        generatorClassDiagram.generate(target, model)
    }
}

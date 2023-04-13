package ee.design.gen.puml.classdiagram

import ee.design.gen.DesignGeneratorFactory
import ee.lang.StructureUnitI
import ee.lang.gen.puml.classdiagram.prepareForCdGeneration
import java.nio.file.Path

open class DesignCdGenerator(val model: StructureUnitI<*>) {

    fun generate(target: Path) {

        val generatorFactory = DesignGeneratorFactory()
        model.prepareForCdGeneration()

        val generatorClassDiagram = generatorFactory.pumlClassDiagram("", model).generator

        generatorClassDiagram.delete(target, model)
        generatorClassDiagram.generate(target, model)
    }
}

package ee.design.gen.go

import ee.design.ModuleI
import ee.design.addDefaultCommandsForEntities
import ee.design.declareAsBaseWithNonImplementedOperation
import ee.design.defineNamesForDataTypeControllers
import ee.design.gen.DesignGeneratorFactory
import ee.lang.StructureUnitI
import ee.lang.findDownByType
import ee.lang.gen.go.prepareForGoGeneration
import java.nio.file.Path

open class DesignGoGenerator {
    val model: StructureUnitI

    constructor(model: StructureUnitI) {
        this.model = model
    }

    fun generate(target: Path) {
        model.extendForGoGeneration()

        /*
        val generatorFactory = DesignGeneratorFactory()
        val generator = generatorFactory.pojoGo()
        generator.delete(target, model)
        model.findDownByType(ModuleI::class.java).forEach { module ->
            generator.generate(target, module)
        }
        */
    }

    protected fun StructureUnitI.extendForGoGeneration() {
        prepareForGoGeneration()

        addDefaultCommandsForEntities()

        defineNamesForDataTypeControllers()
    }
}
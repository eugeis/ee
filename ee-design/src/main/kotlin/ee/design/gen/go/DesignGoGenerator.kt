package ee.design.gen.go

import ee.design.*
import ee.design.gen.DesignGeneratorFactory
import ee.lang.StructureUnitI
import ee.lang.findDownByType
import ee.lang.gen.go.extendForGoGenerationLang
import ee.lang.gen.go.initsForGoGeneration
import java.nio.file.Path

open class DesignGoGenerator {
    val model: StructureUnitI

    constructor(model: StructureUnitI) {
        this.model = model
    }

    fun generate(target: Path) {
        model.extendForGoGeneration()

        val generatorFactory = DesignGeneratorFactory()
        val generator = generatorFactory.eventDrivenGo()
        generator.delete(target, model)
        /*
        val modules = model.findDownByType(ModuleI::class.java)
        modules.forEach { module ->
            generator.generate(target, module)
        }
        */
        generator.generate(target, model)
    }

    protected fun StructureUnitI.extendForGoGeneration() {
        initsForGoGeneration()

        addCommandsAndEventsForAggregates()

        defineCommandAndEventPropsAsReplaceable()

        addCommandEnumsForAggregate()

        addEventEnumsForAggregate()

        addEventhorizonArtifactsForAggregate()

        defineNamesForTypeControllers()

        extendForGoGenerationLang()
    }
}
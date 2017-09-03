package ee.design.gen.ts

import ee.design.addIdPropToEntities
import ee.design.gen.DesignGeneratorFactory
import ee.lang.StructureUnitI
import ee.lang.gen.ts.initsForTsGeneration
import java.nio.file.Path

open class DesignTypeScriptGenerator {
    val model: StructureUnitI

    constructor(model: StructureUnitI) {
        this.model = model
    }

    fun generate(target: Path) {
        model.extendForGoGeneration()

        val generatorFactory = DesignGeneratorFactory()
        val generator = generatorFactory.angular()
        generator.delete(target, model)
        generator.generate(target, model)
    }

    protected fun StructureUnitI.extendForGoGeneration() {
        initsForTsGeneration()

        addIdPropToEntities()

        //addCommandsAndEventsForAggregates()

        //addQueriesForAggregates()

        //addDefaultReturnValuesForQueries()

        //addIdPropToEventsAndCommands()

        //addEventhorizonArtifactsForAggregate()

        //defineNamesForTypeControllers()

        //extendForGoGenerationLang()
    }
}
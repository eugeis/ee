package ee.design.gen.ts

import ee.design.addIdPropToEntities
import ee.design.gen.DesignGeneratorFactory
import ee.lang.StructureUnitIB
import ee.lang.gen.ts.initsForTsGeneration
import java.nio.file.Path

open class DesignTypeScriptGenerator {
    val model: StructureUnitIB<*>

    constructor(model: StructureUnitIB<*>) {
        this.model = model
    }

    fun generate(target: Path) {
        model.extendForGoGeneration()

        val generatorFactory = DesignGeneratorFactory()
        val generator = generatorFactory.angular()
        generator.delete(target, model)
        generator.generate(target, model)
    }

    protected fun StructureUnitIB<*>.extendForGoGeneration() {
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
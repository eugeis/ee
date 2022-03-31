package ee.design.gen.ts

import ee.design.addIdPropToEntities
import ee.design.gen.DesignGeneratorFactory
import ee.lang.StructureUnitI
import ee.lang.gen.ts.initsForTsGeneration
import java.nio.file.Path

open class DesignTypeScriptGenerator(val model: StructureUnitI<*>) {

    fun generate(target: Path) {
        model.extendForTsGeneration()

        val generatorFactory = DesignGeneratorFactory()
        val generatorContexts = generatorFactory.angular("", model)
        val generator = generatorContexts.generator
        generator.delete(target, model)
        generator.generate(target, model)
    }

    protected fun StructureUnitI<*>.extendForTsGeneration() {
        initsForTsGeneration()

        addIdPropToEntities()

        //addCommandsAndEventsForAggregates()

        //addQueriesForAggregates()

        //addDefaultReturnValuesForQueries()

        //addIdPropToEventsAndCommands()

        //addEventHorizonArtifacts()

        //defineNamesForTypeControllers()

        //extendForGoGenerationLang()
    }
}

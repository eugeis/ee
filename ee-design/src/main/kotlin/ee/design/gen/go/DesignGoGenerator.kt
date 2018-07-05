package ee.design.gen.go

import ee.design.*
import ee.design.gen.DesignGeneratorFactory
import ee.lang.*
import ee.lang.gen.go.initsForGoGeneration
import java.nio.file.Path

open class DesignGoGenerator {
    val model: StructureUnitI<*>

    constructor(model: StructureUnitI<*>) {
        this.model = model
    }

    fun generate(target: Path) {
        model.extendForGoGeneration()

        val generatorFactory = DesignGeneratorFactory()
        val generator = generatorFactory.eventDrivenGo()
        generator.delete(target, model)
        generator.generate(target, model)
    }

    protected fun StructureUnitI<*>.extendForGoGeneration() {
        initsForGoGeneration()

        addIdPropToEntities()

        addCommandsAndEventsForAggregates()

        addQueriesForAggregates()

        addAggregateHandler()

        addDefaultReturnValuesForQueries()

        addIdPropToEventsAndCommands()

        addEventHorizonArtifactsForAggregate()

        renameControllersAccordingParentType()

        //setOptionalTagToEventsAndCommandsProps()

        //extendForGoGenerationLang()
        declareAsBaseWithNonImplementedOperation()

        prepareAttributesOfEnums()

        defineSuperUnitsAsAnonymousProps()

        defineConstructorNoProps { constructors().isEmpty() && this !is CommandI<*> && this !is EventI<*> }
    }
}
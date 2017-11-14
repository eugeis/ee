package ee.design.gen.go

import ee.design.*
import ee.design.gen.DesignGeneratorFactory
import ee.lang.*
import ee.lang.gen.go.initsForGoGeneration
import java.nio.file.Path

open class DesignGoGenerator {
    val model: StructureUnitIB<*>

    constructor(model: StructureUnitIB<*>) {
        this.model = model
    }

    fun generate(target: Path) {
        model.extendForGoGeneration()

        val generatorFactory = DesignGeneratorFactory()
        val generator = generatorFactory.eventDrivenGo()
        generator.delete(target, model)
        generator.generate(target, model)
    }

    protected fun StructureUnitIB<*>.extendForGoGeneration() {
        initsForGoGeneration()

        addIdPropToEntities()

        addCommandsAndEventsForAggregates()

        addQueriesForAggregates()

        addDefaultReturnValuesForQueries()

        addIdPropToEventsAndCommands()

        addEventhorizonArtifactsForAggregate()

        defineNamesForTypeControllers()

        //setOptionalTagToEventsAndCommandsProps()

        //extendForGoGenerationLang()
        declareAsBaseWithNonImplementedOperation()

        prepareAttributesOfEnums()

        defineSuperUnitsAsAnonymousProps()

        defineConstructorEmpty { constructors().isEmpty() && this !is CommandIB<*> && this !is EventIB<*> }
    }

    private fun addEventhorizonArtifactsForAggregate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
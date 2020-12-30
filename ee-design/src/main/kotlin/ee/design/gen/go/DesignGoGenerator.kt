package ee.design.gen.go

import ee.design.*
import ee.design.gen.DesignGeneratorFactory
import ee.lang.*
import ee.lang.gen.go.initsForGoGeneration
import org.slf4j.LoggerFactory
import java.nio.file.Path

open class DesignGoGenerator(val models: List<StructureUnitI<*>>, targetAsSingleModule: Boolean = true) {
    private val log = LoggerFactory.getLogger(javaClass)
    val generatorFactory = DesignGeneratorFactory(targetAsSingleModule)

    init {
        models.extendForGoGeneration()
    }

    constructor(model: StructureUnitI<*>, targetAsSingleModule: Boolean = true) :
            this(listOf(model), targetAsSingleModule)

    fun generate(
            target: Path,
            generatorContexts: GeneratorContexts<StructureUnitI<*>> = generatorFactory.go(),
            shallSkip: GeneratorI<*>.(model: Any?) -> Boolean = { false }) {

        models.forEach {
            it.generate(target, generatorContexts, shallSkip)
        }
    }

    fun StructureUnitI<*>.generate(
            target: Path, generatorContexts: GeneratorContexts<StructureUnitI<*>> = generatorFactory.go(),
            shallSkip: GeneratorI<*>.(model: Any?) -> Boolean = { false }) {

        val model = this
        val generator = generatorContexts.generator
        log.info("generate ${generator.names()} to $target for ${model.name()}")
        val modules = if (model is ModuleI) listOf(model) else model.findDownByType(ModuleI::class.java)
        modules.forEach { module ->
            generator.delete(target, module, shallSkip)
        }
        modules.forEach { module ->
            generator.generate(target, module, shallSkip)
        }
    }

    protected fun List<StructureUnitI<*>>.extendForGoGeneration() {
        forEach {
            it.extendForGoGeneration()
        }
    }

    protected fun StructureUnitI<*>.extendForGoGeneration() {
        initsForGoGeneration()

        addIdPropToEntities()

        addIdPropToValues()

        addCommandsAndEventsForAggregates()

        addQueriesForAggregates()

        addAggregateHandler()

        addDefaultReturnValuesForQueries()

        addIdPropToCommands()

        addEsArtifacts()

        renameControllersAccordingParentType()

        //setOptionalTagToEventsAndCommandsProps()

        //extendForGoGenerationLang()
        declareAsBaseWithNonImplementedOperation()

        prepareAttributesOfEnums()

        defineSuperUnitsAsAnonymousProps()

        defineConstructorNoProps { constructors().isEmpty() && this !is CommandI<*> && this !is EventI<*> }
    }
}
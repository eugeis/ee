package ee.design.gen.go

import ee.design.*
import ee.design.gen.DesignGeneratorFactory
import ee.lang.*
import ee.lang.gen.go.initsForGoGeneration
import org.slf4j.LoggerFactory
import java.nio.file.Path

open class DesignGoGenerator(val model: StructureUnitI<*>, singleModule: Boolean = false) {
    private val log = LoggerFactory.getLogger(javaClass)
    val generatorFactory = DesignGeneratorFactory(singleModule)

    init {
        model.extendForGoGeneration()
    }

    fun generate(target: Path,
                 generatorContexts: GeneratorContexts<StructureUnitI<*>> = generatorFactory.go(),
                 shallSkip: GeneratorI<*>.(model: Any?) -> Boolean = { false }) {
        //val generatorContexts = generatorFactory.go()
        //generatorContexts.generator.delete(target, model)
        //generatorContexts.generator.generate(target, model)

        val generator = generatorContexts.generator
        log.info("generate ${generator.names()} to $target for ${model.name()}")
        val modules = model.findDownByType(ModuleI::class.java)
        modules.forEach { module ->
            generator.delete(target, module, shallSkip)
        }
        modules.forEach { module ->
            generator.generate(target, module, shallSkip)
        }
    }

    protected fun StructureUnitI<*>.extendForGoGeneration() {
        initsForGoGeneration()

        addIdPropToEntities()

        addCommandsAndEventsForAggregates()

        addQueriesForAggregates()

        addAggregateHandler()

        addDefaultReturnValuesForQueries()

        addIdPropToEventsAndCommands()

        addEventHorizonArtifacts()

        renameControllersAccordingParentType()

        //setOptionalTagToEventsAndCommandsProps()

        //extendForGoGenerationLang()
        declareAsBaseWithNonImplementedOperation()

        prepareAttributesOfEnums()

        defineSuperUnitsAsAnonymousProps()

        defineConstructorNoProps { constructors().isEmpty() && this !is CommandI<*> && this !is EventI<*> }
    }
}
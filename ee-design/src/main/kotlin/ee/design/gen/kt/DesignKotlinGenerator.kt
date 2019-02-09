package ee.design.gen.kt

import ee.design.ModuleI
import ee.design.gen.DesignGeneratorFactory
import ee.design.renameControllersAccordingParentType
import ee.lang.*
import ee.lang.gen.kt.prepareForKotlinGeneration
import org.slf4j.LoggerFactory
import java.nio.file.Path

open class DesignKotlinGenerator(val model: StructureUnitI<*>, singleModule: Boolean = false) {
    private val log = LoggerFactory.getLogger(javaClass)
    val generatorFactory = DesignGeneratorFactory(singleModule)

    init {
        model.extendForKotlinGeneration()
    }

    fun generate(target: Path, generatorContexts: GeneratorContexts<StructureUnitI<*>> = generatorFactory.pojoKt(),
                 shallSkip: GeneratorI<*>.(model: Any?) -> Boolean = { false }) {
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

    protected fun StructureUnitI<*>.extendForKotlinGeneration() {
        prepareForKotlinGeneration()

        //define names for data type controllers
        renameControllersAccordingParentType()
    }
}
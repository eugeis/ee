package ee.design.gen.kt

import ee.design.ModuleI
import ee.design.renameArtifactsAccordingParentType
import ee.design.markReplaceableConfigProps
import ee.lang.*
import ee.lang.gen.kt.LangKotlinGenerator
import ee.lang.gen.kt.prepareForKotlinGeneration
import org.slf4j.LoggerFactory
import java.nio.file.Path

open class DesignKotlinGenerator(model: StructureUnitI<*>, targetAsSingleModule: Boolean = false): LangKotlinGenerator(model, targetAsSingleModule) {
    private val log = LoggerFactory.getLogger(javaClass)

    init {
        model.extendForKotlinGeneration()
    }

    fun generate(target: Path, generatorContexts: GeneratorContexts<StructureUnitI<*>> = pojoKt(),
                 shallSkip: GeneratorI<*>.(model: Any?) -> Boolean = { false }) {
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

    protected fun StructureUnitI<*>.extendForKotlinGeneration() {
        prepareForKotlinGeneration()

        //define names for data type controllers
        renameArtifactsAccordingParentType()

        markReplaceableConfigProps()
    }
}
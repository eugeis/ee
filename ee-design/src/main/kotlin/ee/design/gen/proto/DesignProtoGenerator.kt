package ee.design.gen.proto

import ee.design.CompI
import ee.design.ModuleI
import ee.design.gen.DesignGeneratorFactory
import ee.design.renameControllersAccordingParentType
import ee.design.setReplaceableConfigProps
import ee.lang.*
import ee.lang.gen.KotlinContext
import ee.lang.gen.KotlinContextBuilder
import ee.lang.gen.kt.LangKotlinContextFactory
import ee.lang.gen.kt.prepareForKotlinGeneration
import ee.lang.gen.proto.LangProtoContextFactory
import ee.lang.gen.proto.LangProtoGeneratorFactory
import ee.lang.gen.proto.LangProtoTemplates
import ee.lang.gen.proto.itemNameAsProtoFileName
import org.slf4j.LoggerFactory
import java.nio.file.Path

open class DesignProtoGenerator(val model: StructureUnitI<*>, targetAsSingleModule: Boolean = true) {
    private val log = LoggerFactory.getLogger(javaClass)
    val generatorFactory = DesignProtoGeneratorFactory(targetAsSingleModule)

    init {
        model.initObjectTrees()
    }

    fun generate(target: Path, generatorContexts: GeneratorContexts<StructureUnitI<*>> = generatorFactory.pojoProto(),
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
}

class DesignProtoGeneratorFactory(private val targetAsSingleModule: Boolean = false) :
        LangProtoGeneratorFactory(targetAsSingleModule) {
    override fun buildProtoContextFactory() = DesignProtoContextFactory(targetAsSingleModule)
    override fun buildProtoTemplates() = LangProtoTemplates(itemNameAsProtoFileName)
}

open class DesignProtoContextFactory(targetAsSingleModule: Boolean) : LangProtoContextFactory(targetAsSingleModule) {
    override fun StructureUnitI<*>.computeModuleFolder(): String {
        val compOrStructureUnit = findThisOrParentUnsafe(CompI::class.java) ?: this
        return if (compOrStructureUnit == this) {
            "${artifact()}/proto"
        } else {
            return if (targetAsSingleModule) {
                "${compOrStructureUnit.artifact()}/proto"
            } else {
                "${compOrStructureUnit.artifact()}/${artifact()}/proto"
            }
        }
    }
}
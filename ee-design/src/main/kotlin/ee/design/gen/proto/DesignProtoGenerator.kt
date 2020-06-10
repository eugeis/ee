package ee.design.gen.proto

import ee.design.CompI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.proto.LangProtoContextFactory
import ee.lang.gen.proto.LangProtoGeneratorFactory
import ee.lang.gen.proto.LangProtoTemplates
import ee.lang.gen.proto.itemNameAsProtoFileName
import org.slf4j.LoggerFactory
import java.nio.file.Path

open class DesignProtoGenerator(val models: List<StructureUnitI<*>>, targetAsSingleModule: Boolean = true) {
    private val log = LoggerFactory.getLogger(javaClass)
    val generatorFactory = DesignProtoGeneratorFactory(targetAsSingleModule)

    init {
        models.initObjectTrees()
    }

    constructor(model: StructureUnitI<*>, targetAsSingleModule: Boolean = true) :
            this(listOf(model), targetAsSingleModule)

    fun generate(target: Path, generatorContexts: GeneratorContexts<StructureUnitI<*>> = generatorFactory.pojoProto(),
                 shallSkip: GeneratorI<*>.(model: Any?) -> Boolean = { false }) {
        models.forEach {
            it.generate(target, generatorContexts, shallSkip)
        }
    }

    fun StructureUnitI<*>.generate(
            target: Path, generatorContexts: GeneratorContexts<StructureUnitI<*>> = generatorFactory.pojoProto(),
            shallSkip: GeneratorI<*>.(model: Any?) -> Boolean = { false }) {
        val model = this
        val generator = generatorContexts.generator
        log.info("generate ${generator.names()} to $target for ${model.name()}")
        val modules = if (model is ModuleI) listOf(models) else model.findDownByType(ModuleI::class.java)
        modules.forEach { module ->
            generator.delete(target, model, shallSkip)
        }
        modules.forEach { module ->
            generator.generate(target, model, shallSkip)
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
package ee.design.gen.kt

import ee.design.ModuleI
import ee.design.gen.DesignGeneratorFactory
import ee.design.renameControllersAccordingParentType
import ee.lang.GeneratorI
import ee.lang.StructureUnitI
import ee.lang.findDownByType
import ee.lang.gen.kt.prepareForKotlinGeneration
import java.nio.file.Path

open class DesignKotlinGenerator {
    private val singleModule: Boolean
    val model: StructureUnitI<*>
    val generatorFactory: DesignGeneratorFactory

    constructor(model: StructureUnitI<*>, singleModule: Boolean = true) {
        this.model = model
        this.singleModule = singleModule
        generatorFactory = DesignGeneratorFactory(singleModule)
        model.extendForKotlinGeneration()
    }

    fun generate(target: Path, generator: GeneratorI<StructureUnitI<*>> = generatorFactory.pojoKt()) {
        model.findDownByType(ModuleI::class.java).forEach { module ->
            generator.delete(target, module)
        }
        model.findDownByType(ModuleI::class.java).forEach { module ->
            generator.generate(target, module)
        }
    }

    protected fun StructureUnitI<*>.extendForKotlinGeneration() {
        prepareForKotlinGeneration()

        //define names for data type controllers
        renameControllersAccordingParentType()
    }
}
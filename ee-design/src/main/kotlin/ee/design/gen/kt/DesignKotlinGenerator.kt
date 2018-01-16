package ee.design.gen.kt

import ee.design.ModuleI
import ee.design.gen.DesignGeneratorFactory
import ee.design.renameControllersAccordingParentType
import ee.lang.StructureUnitI
import ee.lang.findDownByType
import ee.lang.gen.kt.prepareForKotlinGeneration
import java.nio.file.Path

open class DesignKotlinGenerator {
    val model: StructureUnitI<*>

    constructor(model: StructureUnitI<*>) {
        this.model = model
    }

    fun generate(target: Path) {
        model.extendForKotlinGeneration()
        val generatorFactory = DesignGeneratorFactory()
        val generator = generatorFactory.pojoKt()
        generator.delete(target, model)
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
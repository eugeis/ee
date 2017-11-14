package ee.design.gen.kt

import ee.design.ModuleIB
import ee.design.defineNamesForTypeControllers
import ee.design.gen.DesignGeneratorFactory
import ee.lang.StructureUnitIB
import ee.lang.findDownByType
import ee.lang.gen.kt.prepareForKotlinGeneration
import java.nio.file.Path

open class DesignKotlinGenerator {
    val model: StructureUnitIB<*>

    constructor(model: StructureUnitIB<*>) {
        this.model = model
    }

    fun generate(target: Path) {
        model.extendForKotlinGeneration()
        val generatorFactory = DesignGeneratorFactory()
        val generator = generatorFactory.pojoKt()
        generator.delete(target, model)
        model.findDownByType(ModuleIB::class.java).forEach { module ->
            generator.generate(target, module)
        }
    }

    protected fun StructureUnitIB<*>.extendForKotlinGeneration() {
        prepareForKotlinGeneration()

        //define names for data type controllers
        defineNamesForTypeControllers()
    }
}
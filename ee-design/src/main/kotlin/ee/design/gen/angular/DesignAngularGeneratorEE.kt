package ee.design.gen.angular

import ee.design.gen.DesignGeneratorFactory
import ee.lang.StructureUnitI
import ee.lang.gen.ts.prepareForTsGeneration
import java.nio.file.Path

open class DesignAngularGeneratorEE(val model: StructureUnitI<*>) {

    fun generate(target: Path) {

        val generatorFactory = DesignGeneratorFactory()
        model.prepareForTsGeneration()

        val generatorApiBase = generatorFactory.typeScriptApiBase("", model).generator
        val generatorAngular = generatorFactory.angular("", model).generator

        generatorApiBase.delete(target, model)
        generatorAngular.delete(target, model)

        generatorApiBase.generate(target, model)
        generatorAngular.generate(target, model)
    }
}

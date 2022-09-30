package ee.design.gen.angular

import ee.design.addIdPropToEntities
import ee.design.gen.DesignGeneratorFactory
import ee.lang.StructureUnitI
import ee.lang.gen.ts.initsForTsGeneration
import java.nio.file.Path

open class DesignAngularGenerator(val model: StructureUnitI<*>) {

    fun generate(target: Path) {

        val generatorFactory = DesignGeneratorFactory()

        val generatorAngularModule = generatorFactory.angularModules("", model)
        val generatorModule = generatorAngularModule.generator
        val generatorAngularHtmlAndScss = generatorFactory.angularHtmlAndScssComponent("", model)
        val generatorHtmlAndScss = generatorAngularHtmlAndScss.generator

        generatorModule.generate(target, model)
        generatorHtmlAndScss.generate(target, model)
    }
}

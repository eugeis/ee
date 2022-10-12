package ee.design.gen.angular

import ee.design.gen.DesignGeneratorFactory
import ee.lang.StructureUnitI
import ee.lang.gen.ts.prepareForTsGeneration
import java.nio.file.Path

open class DesignAngularGenerator(val model: StructureUnitI<*>) {

    fun generate(target: Path) {

        val generatorFactory = DesignGeneratorFactory()
        model.prepareForTsGeneration()

        val generatorContextsApiBase = generatorFactory.typeScriptApiBase("", model)
        val generatorApiBase = generatorContextsApiBase.generator
        val generatorContextsComponent = generatorFactory.angularTypeScriptComponent("", model)
        val generatorComponent = generatorContextsComponent.generator

        val generatorAngularModule = generatorFactory.angularModules("", model)
        val generatorModule = generatorAngularModule.generator
        val generatorAngularHtmlAndScss = generatorFactory.angularHtmlAndScssComponent("", model)
        val generatorHtmlAndScss = generatorAngularHtmlAndScss.generator

        generatorApiBase.delete(target, model)
        generatorComponent.delete(target, model)
        generatorModule.delete(target, model)
        generatorHtmlAndScss.delete(target, model)

        generatorApiBase.generate(target, model)
        generatorComponent.generate(target, model)
        generatorModule.generate(target, model)
        generatorHtmlAndScss.generate(target, model)
    }
}

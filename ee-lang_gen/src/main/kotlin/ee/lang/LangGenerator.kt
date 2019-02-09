package ee.lang

import ee.lang.gen.LangGenGeneratorFactory
import ee.lang.gen.prepareForKotlinGeneration
import ee.lang.integ.eePath
import java.nio.file.Path

fun main() {
    generate(eePath)
}

fun generate(target: Path) {
    var model = l.prepareForKotlinGeneration()
    val generatorFactory = LangGenGeneratorFactory(namespace = model.namespace(), module = "ee-lang")
    val generatorContexts = generatorFactory.dsl("Lang")
    generatorContexts.generator.delete(target, model)
    generatorContexts.generator.generate(target, model)
}
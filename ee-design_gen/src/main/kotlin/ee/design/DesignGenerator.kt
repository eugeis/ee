package ee.design

import ee.lang.gen.LangGeneratorFactory
import ee.lang.gen.prepareForKotlinGeneration
import ee.lang.integ.eePath
import java.nio.file.Path

fun main(args: Array<String>) {
    generate(eePath)
}

fun generate(target: Path) {
    var model = d.prepareForKotlinGeneration()
    val generatorFactory = LangGeneratorFactory()
    val generator = generatorFactory.dsl("Design")
    generator.generate(target, model)
}
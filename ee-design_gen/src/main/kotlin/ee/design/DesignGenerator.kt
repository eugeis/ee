package ee.design

import ee.lang.gen.LangGeneratorFactory
import ee.lang.gen.kt.prepareForKotlinGeneration
import ee.lang.integ.eePath
import java.nio.file.Path

fun main(args: Array<String>) {
    generate(eePath)
}

fun generate(target: Path) {
    var model = d.prepareForKotlinGeneration()
    val generatorFactory = LangGeneratorFactory()
    val generator = generatorFactory.dslKt("Design")
    generator.delete(target, model)
    generator.generate(target, model)
}
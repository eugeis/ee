package ee.design

import ee.lang.gen.kt.LangKotlinGenerator
import ee.lang.gen.kt.prepareForKotlinGeneration
import ee.lang.integ.eePath
import java.nio.file.Path

fun main() {
    generate(eePath)
}

fun generate(target: Path) {
    var model = d.prepareForKotlinGeneration()
    val generatorFactory = LangKotlinGenerator(model)
    val generatorContexts = generatorFactory.dslKt("Design")
    generatorContexts.generator.delete(target, model)
    generatorContexts.generator.generate(target, model)
}
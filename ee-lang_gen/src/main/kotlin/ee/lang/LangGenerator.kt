package ee.lang

import ee.lang.gen.GeneratorFactory
import ee.lang.gen.prepareForKotlinGeneration
import ee.lang.integ.eePath
import java.nio.file.Path

fun main(args: Array<String>) {
    generate(eePath)
}

fun generate(target: Path) {
    var model = l.prepareForKotlinGeneration()
    val generatorFactory = GeneratorFactory(namespace = model.namespace(), module = "ee-lang")
    val generator = generatorFactory.dsl("Lang")
    generator.generate(target, model)
}
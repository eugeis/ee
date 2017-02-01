package ee.task

import ee.design.KotlinGenerator
import ee.design.integ.eePath

fun main(args: Array<String>) {
    val generator = KotlinGenerator(model())
    generator.generate(eePath)
}

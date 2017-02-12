package ee.design.ui

import ee.design.gen.DesingKotlinGenerator
import ee.design.Model
import ee.lang.integ.eePath

fun main(args: Array<String>) {
    val generator = DesingKotlinGenerator(Model())
    generator.generate(eePath)
}
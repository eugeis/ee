package ee.design.ui

import ee.design.KotlinGenerator
import ee.design.Model
import ee.lang.integ.eePath

fun main(args: Array<String>) {
    val generator = KotlinGenerator(Model())
    generator.generate(eePath)
}
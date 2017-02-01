package ee.design.ui

import ee.design.Model
import ee.design.KotlinGenerator
import ee.design.integ.eePath

fun main(args: Array<String>) {
    val generator = KotlinGenerator(Model())
    generator.generate(eePath)
}
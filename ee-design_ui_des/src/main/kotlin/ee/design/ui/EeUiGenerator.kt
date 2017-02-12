package ee.design.ui

import ee.design.gen.DesignKotlinGenerator
import ee.design.Model
import ee.lang.integ.eePath

fun main(args: Array<String>) {
    val generator = DesignKotlinGenerator(Model())
    generator.generate(eePath)
}
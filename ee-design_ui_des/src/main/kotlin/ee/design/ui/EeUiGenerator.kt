package ee.design.ui

import ee.design.Comp
import ee.design.gen.kt.DesignKotlinGenerator
import ee.lang.integ.eePath

fun main(args: Array<String>) {
    val generator = DesignKotlinGenerator(Comp())
    generator.generate(eePath)
}
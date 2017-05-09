package ee.task

import ee.design.gen.kt.DesignKotlinGenerator
import ee.lang.integ.eePath

fun main(args: Array<String>) {
    val generator = DesignKotlinGenerator(Task)
    generator.generate(eePath)
}

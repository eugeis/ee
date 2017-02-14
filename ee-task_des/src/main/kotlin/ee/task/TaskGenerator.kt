package ee.task

import ee.design.gen.DesignKotlinGenerator
import ee.lang.integ.eePath

fun main(args: Array<String>) {
    val generator = DesignKotlinGenerator(Task)
    generator.generate(eePath)
}

package ee.system

import ee.design.gen.kt.DesignKotlinGenerator
import ee.lang.integ.eePath

fun main(args: Array<String>) {
    DesignKotlinGenerator(System).generate(eePath)
}

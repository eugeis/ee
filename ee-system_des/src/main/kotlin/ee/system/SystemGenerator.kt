package ee.system
import ee.design.gen.DesignKotlinGenerator
import ee.lang.integ.eePath

fun main(args: Array<String>) {
    val generator = DesignKotlinGenerator(model())
    generator.generate(eePath)
}

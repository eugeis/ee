package ee.design.gen.java

import ee.design.StructureUnit
import ee.design.Type

object java : StructureUnit({ namespace("java") }) {
    object util : StructureUnit() {
        val Date = Type()

        object concurrent : StructureUnit({ namespace("java.util.concurrent") }) {
            val TimeUnit = Type()
        }
    }

    object nio : StructureUnit() {
        object file : StructureUnit() {
            val Path = Type()
            val Paths = Type()
        }
    }
}
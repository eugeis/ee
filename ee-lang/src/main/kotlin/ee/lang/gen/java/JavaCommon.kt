package ee.lang.gen.java

import ee.lang.StructureUnit
import ee.lang.Type

object j : StructureUnit({ name("Java").namespace("java") }) {
    object util : StructureUnit({ namespace("java.util") }) {
        val Date = Type()

        object concurrent : StructureUnit({ namespace("java.util.concurrent") }) {
            val TimeUnit = Type()
        }
    }

    object nio {
        object file : StructureUnit({ namespace("java.nio.file") }) {
            val Path = Type()
            val Paths = Type()
        }
    }
}
package ee.lang.gen

import ee.lang.StructureUnit
import ee.lang.Type

object j : StructureUnit({ namespace("java").name("Java") }) {
    object util : StructureUnit() {
        val Date = Type()

        object concurrent : StructureUnit() {
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
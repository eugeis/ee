package ee.lang.gen.java

import ee.lang.ExternalType
import ee.lang.StructureUnit
import ee.lang.Type

object j : StructureUnit({ namespace("java").name("Java") }) {
    object util : StructureUnit() {
        val Date = Type()

        object concurrent : StructureUnit() {
            val TimeUnit = Type()
        }
    }

    object net : StructureUnit() {
        val URL = Type()
    }

    object nio : StructureUnit() {
        object file : StructureUnit() {
            val Path = Type()
            val Paths = Type()
        }
    }
}

object junit : StructureUnit({ namespace("org.junit.jupiter.api").name("JUnit5") }) {
    val Test = ExternalType()

    object Assertions : StructureUnit() {
        val assertEquals = ExternalType()
    }
}


object jackson : StructureUnit({ namespace("com.fasterxml.jackson") }) {
    object json : StructureUnit({ namespace("com.fasterxml.jackson.annotation") }) {
        object JsonProperty : ExternalType()
        object JsonValue : ExternalType()
    }

    object xml : StructureUnit({ namespace("com.fasterxml.jackson.dataformat.xml.annotation") }) {
        object JacksonXmlProperty : ExternalType()
        object JacksonXmlElementWrapper : ExternalType()
    }
}
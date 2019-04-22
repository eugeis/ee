package ee.lang.gen.kt

import ee.common.ext.then
import ee.lang.*

fun <T : CompilationUnitI<*>> T.toKotlinFxPojo(c: GenerationContext, derived: String = "Fx",
    api: String = LangDerivedKind.API) : String {
    val itemName = c.n(this, derived)
    val typePrefix = """${isOpen().then("open ")}class ${
    toKotlinGenericsClassDef(c, derived)}$itemName"""
    return """
$typePrefix${""

}"""
}

object javaFx : StructureUnit() {
    object javaFx : StructureUnit() {
        val FXCollections = NativeType()
    }
}

object tornadofx : StructureUnit({ name("tornadofx") }) {
    val ItemViewModel = NativeType()

    object Collection : NativeType({ multi(true) }) {
        val T = n.Collection.G { type(n.String) }
    }

    val getProperty = NativeType()
    val setProperty = NativeType()
    val property = NativeType()
}


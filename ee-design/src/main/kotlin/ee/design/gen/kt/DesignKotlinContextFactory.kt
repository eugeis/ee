package ee.design.gen.kt

import ee.design.CompI
import ee.lang.DerivedController
import ee.lang.StructureUnitI
import ee.lang.findThisOrParentUnsafe
import ee.lang.gen.KotlinContext
import ee.lang.gen.kt.LangKotlinContextFactory

open class DesignKotlinContextFactory : LangKotlinContextFactory() {
    override fun contextBuilder(controller: DerivedController): StructureUnitI<*>.() -> KotlinContext {
        return {
            val structureUnit = this
            val compOrStructureUnit = this.findThisOrParentUnsafe(CompI::class.java) ?: structureUnit
            KotlinContext(moduleFolder = "${compOrStructureUnit.artifact()}/${compOrStructureUnit.artifact()}",
                namespace = structureUnit.namespace().toLowerCase(), derivedController = controller)
        }
    }
}

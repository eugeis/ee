package ee.design.gen.kt

import ee.design.CompIB
import ee.lang.DerivedController
import ee.lang.StructureUnitIB
import ee.lang.findThisOrParentUnsafe
import ee.lang.gen.KotlinContext
import ee.lang.gen.kt.LangKotlinContextFactory

open class DesignKotlinContextFactory : LangKotlinContextFactory() {
    override fun contextBuilder(controller: DerivedController): StructureUnitIB<*>.() -> KotlinContext {
        return {
            val structureUnit = this
            val compOrStructureUnit = this.findThisOrParentUnsafe(CompIB::class.java) ?: structureUnit
            KotlinContext(moduleFolder = "${compOrStructureUnit.artifact()}/${compOrStructureUnit.artifact()}",
                    namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = controller
            )
        }
    }
}

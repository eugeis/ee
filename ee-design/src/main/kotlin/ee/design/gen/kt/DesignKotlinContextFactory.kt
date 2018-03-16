package ee.design.gen.kt

import ee.design.CompI
import ee.lang.DerivedController
import ee.lang.StructureUnitI
import ee.lang.findThisOrParentUnsafe
import ee.lang.gen.KotlinContext
import ee.lang.gen.kt.LangKotlinContextFactory

open class DesignKotlinContextFactory : LangKotlinContextFactory {
    constructor(singleModule: Boolean) : super(singleModule)

    override fun contextBuilder(controller: DerivedController): StructureUnitI<*>.() -> KotlinContext {
        return {
            KotlinContext(moduleFolder = computeModuleFolder(), namespace = namespace().toLowerCase(),
                    derivedController = controller)
        }
    }

    private fun StructureUnitI<*>.computeModuleFolder(): String {
        val compOrStructureUnit = this.findThisOrParentUnsafe(CompI::class.java) ?: this
        return if (singleModule) {
            "${compOrStructureUnit.artifact()}/${compOrStructureUnit.artifact()}"
        } else {
            "${compOrStructureUnit.artifact()}/${artifact()}"
        }
    }
}

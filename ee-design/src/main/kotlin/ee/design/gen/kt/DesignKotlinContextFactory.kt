package ee.design.gen.kt

import ee.design.CompI
import ee.lang.DerivedController
import ee.lang.StructureUnitI
import ee.lang.findThisOrParentUnsafe
import ee.lang.gen.KotlinContext
import ee.lang.gen.KotlinContextBuilder
import ee.lang.gen.kt.LangKotlinContextFactory

open class DesignKotlinContextFactory(targetAsSingleModule: Boolean) : LangKotlinContextFactory(targetAsSingleModule) {

    override fun contextBuilder(controller: DerivedController, scope: String): KotlinContextBuilder<StructureUnitI<*>> {
        return KotlinContextBuilder(KotlinContext.CONTEXT_KOTLIN, scope, macroController){
            KotlinContext(namespace().toLowerCase(), computeModuleFolder(), "src-gen/$scope/kotlin",
                    derivedController = controller, macroController = macroController)
        }
    }

    override fun StructureUnitI<*>.computeModuleFolder(): String {
        val compOrStructureUnit = findThisOrParentUnsafe(CompI::class.java) ?: this
        return if(compOrStructureUnit == this) {
            artifact()
        } else {
            if (targetAsSingleModule) {
                "${compOrStructureUnit.artifact()}/${compOrStructureUnit.artifact()}"
            } else {
                "${compOrStructureUnit.artifact()}/${artifact()}"
            }
        }
    }
}

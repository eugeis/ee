package ee.design.gen.kt

import ee.design.CompI
import ee.lang.DerivedController
import ee.lang.StructureUnitI
import ee.lang.findThisOrParentUnsafe
import ee.lang.gen.KotlinContext
import ee.lang.gen.KotlinContextBuilder
import ee.lang.gen.kt.LangKotlinContextFactory

open class DesignKotlinContextFactory : LangKotlinContextFactory {
    constructor(singleModule: Boolean) : super(singleModule)

    override fun contextBuilder(controller: DerivedController, scope: String): KotlinContextBuilder<StructureUnitI<*>> {
        return KotlinContextBuilder(KotlinContext.CONTEXT_KOTLIN, scope, macroController){
            KotlinContext(namespace().toLowerCase(), computeModuleFolder(), "src-gen/$scope/kotlin",
                    derivedController = controller, macroController = macroController)
        }
    }

    private fun StructureUnitI<*>.computeModuleFolder(): String {
        val compOrStructureUnit = this.findThisOrParentUnsafe(CompI::class.java) ?: this
        return if(compOrStructureUnit == this) {
            artifact()
        } else {
            if (singleModule) {
                "${compOrStructureUnit.artifact()}/${compOrStructureUnit.artifact()}"
            } else {
                "${compOrStructureUnit.artifact()}/${artifact()}"
            }
        }
    }
}

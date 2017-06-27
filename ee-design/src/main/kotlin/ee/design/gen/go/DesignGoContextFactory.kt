package ee.design.gen.go

import ee.design.CompI
import ee.lang.DerivedController
import ee.lang.StructureUnitI
import ee.lang.findThisOrParent
import ee.lang.gen.go.GoContext
import ee.lang.gen.go.LangGoContextFactory

open class DesignGoContextFactory : LangGoContextFactory() {
    override fun contextBuilder(controller: DerivedController): StructureUnitI.() -> GoContext {
        return {
            val structureUnit = this
            val compOrStructureUnit = this.findThisOrParent(CompI::class.java) ?: structureUnit
            GoContext(moduleFolder = "${compOrStructureUnit.artifact()}/${compOrStructureUnit.artifact()}",
                    namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = controller
            )
        }
    }
}

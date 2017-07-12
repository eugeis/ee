package ee.design.gen.go

import ee.design.CommandI
import ee.design.CompI
import ee.design.EventI
import ee.lang.*
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

    override fun registerForImplOnly(derived: DerivedController): DerivedController {
        super.registerForImplOnly(derived)
        return derived
    }

    override fun ItemI.buildName(): String {
        return if (this is CommandI) {
            parentNameAndName().capitalize()
        } else if (this is EventI) {
            nameAndParentName().capitalize()
        } else {
            name()
        }
    }
}

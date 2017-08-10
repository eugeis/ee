package ee.design.gen.go

import ee.design.CommandI
import ee.design.CompI
import ee.design.EventI
import ee.lang.*
import ee.lang.gen.go.GoContext
import ee.lang.gen.go.LangGoContextFactory

open class DesignGoContextFactory : LangGoContextFactory() {
    override fun contextBuilder(derived: DerivedController): StructureUnitI.() -> GoContext {
        return {
            val structureUnit = this
            val compOrStructureUnit = this.findThisOrParent(CompI::class.java) ?: structureUnit
            GoContext(moduleFolder = "${compOrStructureUnit.artifact()}/${compOrStructureUnit.artifact()}",
                    namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = derived,
                    macroController = macroController
            )
        }
    }

    override fun registerForImplOnly(derived: DerivedController) {
        super.registerForImplOnly(derived)
    }

    override fun ItemI.buildName(): String {
        return if (this is CommandI) {
            buildNameForCommand()
        } else if (this is EventI) {
            buildNameForEvent()
        } else if (this is ConstructorI) {
            buildNameForConstructor()
        } else {
            name()
        }
    }

    protected open fun CommandI.buildNameForCommand() = nameAndParentName().capitalize()

    protected open fun EventI.buildNameForEvent() = parentNameAndName().capitalize()
}

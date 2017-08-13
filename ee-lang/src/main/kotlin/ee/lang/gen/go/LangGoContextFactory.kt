package ee.lang.gen.go

import ee.common.ext.ifElse
import ee.lang.ConstructorI
import ee.lang.DerivedController
import ee.lang.ItemI
import ee.lang.StructureUnitI

open class LangGoContextFactory : LangCommonContextFactory() {

    open fun buildForImplOnly(): StructureUnitI.() -> GoContext {
        val derivedController = DerivedController()
        registerForImplOnly(derivedController)
        return contextBuilder(derivedController)
    }

    override fun contextBuilder(derived: DerivedController): StructureUnitI.() -> GoContext {
        return {
            val structureUnit = this
            GoContext(moduleFolder = structureUnit.artifact(),
                    namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = derived,
                    macroController = macroController
            )
        }
    }

    override fun ItemI.buildName(): String {
        return if (this is ConstructorI) {
            buildName()
        } else {
            name()
        }
    }

    override fun ConstructorI.buildNameForConstructor() = name().equals(parent().name()).ifElse(
            { "New${name().capitalize()}" },
            { "New${parent().name().capitalize()}${name().capitalize()}" })
}
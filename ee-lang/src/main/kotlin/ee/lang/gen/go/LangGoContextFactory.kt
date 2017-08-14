package ee.lang.gen.go

import ee.common.ext.ifElse
import ee.lang.ConstructorI
import ee.lang.DerivedController
import ee.lang.ItemI
import ee.lang.StructureUnitI
import ee.lang.gen.common.LangCommonContextFactory

open class LangGoContextFactory : LangCommonContextFactory() {

    open fun buildForImplOnly(): StructureUnitI.() -> GoContext {
        val derivedController = DerivedController()
        registerForImplOnly(derivedController)
        return contextBuilder(derivedController)
    }

    override fun contextBuilder(derived: DerivedController): StructureUnitI.() -> GoContext {
        return {
            val structureUnit = this
            GoContext(moduleFolder = structureUnit.artifact(), namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = derived, macroController = macroController
            )
        }
    }

    override fun buildName(item: ItemI, kind: String): String {
        return if (item is ConstructorI) {
            buildNameForConstructor(item, kind)
        } else {
            super.buildName(item, kind)
        }
    }

    override fun buildNameForConstructor(item: ConstructorI, kind: String) = item.name().equals(item.parent().name()).ifElse(
            { "New${buildNameCommon(item, kind).capitalize()}" },
            { "New${buildNameCommon(item.parent(), kind).capitalize()}${buildNameCommon(item, kind).capitalize()}" })
}
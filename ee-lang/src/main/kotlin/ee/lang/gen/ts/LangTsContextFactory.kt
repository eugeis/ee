package ee.lang.gen.ts

import ee.common.ext.ifElse
import ee.lang.*
import ee.lang.gen.common.LangCommonContextFactory

open class LangTsContextFactory : LangCommonContextFactory() {

    open fun buildForImplOnly(): StructureUnitI.() -> TsContext {
        val derivedController = DerivedController()
        registerForImplOnly(derivedController)
        return contextBuilder(derivedController)
    }

    override fun contextBuilder(derived: DerivedController): StructureUnitI.() -> TsContext {
        return {
            val structureUnit = this
            TsContext(moduleFolder = structureUnit.artifact(), namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = derived, macroController = macroController
            )
        }
    }

    override fun buildName(item: ItemI, kind: String): String {
        return if (item is ConstructorI) {
            buildNameForConstructor(item, kind)
        } else if (item is OperationI) {
            buildNameForOperation(item, kind)
        } else {
            super.buildName(item, kind)
        }
    }

    override fun buildNameForConstructor(item: ConstructorI, kind: String) = item.name().equals(item.parent().name()).ifElse(
            { "New${buildNameCommon(item, kind).capitalize()}" },
            { "New${buildNameCommon(item.parent(), kind).capitalize()}${buildNameCommon(item, kind).capitalize()}" })

    override fun buildNameForOperation(item: OperationI, kind: String): String {
        return buildNameCommon(item, kind).capitalize()
    }
}
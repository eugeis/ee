package ee.lang.gen.go

import ee.common.ext.ifElse
import ee.lang.*
import ee.lang.gen.common.LangCommonContextFactory

open class LangGoContextFactory : LangCommonContextFactory() {

    open fun buildForImplOnly(): StructureUnitIB<*>.() -> GoContext {
        val derivedController = DerivedController()
        registerForImplOnly(derivedController)
        return contextBuilder(derivedController)
    }

    override fun contextBuilder(derived: DerivedController): StructureUnitIB<*>.() -> GoContext {
        return {
            val structureUnit = this
            GoContext(moduleFolder = structureUnit.artifact(), namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = derived, macroController = macroController
            )
        }
    }

    override fun buildName(item: ItemIB<*>, kind: String): String {
        return if (item is ConstructorIB) {
            buildNameForConstructor(item, kind)
        } else if (item is OperationIB) {
            buildNameForOperation(item, kind)
        } else {
            super.buildName(item, kind)
        }
    }

    override fun buildNameForConstructor(item: ConstructorIB<*>, kind: String) = item.name().equals(item.parent().name()).ifElse(
            { "New${buildNameCommon(item, kind).capitalize()}" },
            { "New${buildNameCommon(item.parent(), kind).capitalize()}${buildNameCommon(item, kind).capitalize()}" })

    override fun buildNameForOperation(item: OperationIB<*>, kind: String): String {
        return buildNameCommon(item, kind).capitalize()
    }
}
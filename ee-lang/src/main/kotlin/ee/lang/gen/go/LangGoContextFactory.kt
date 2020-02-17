package ee.lang.gen.go

import ee.common.ext.ifElse
import ee.lang.*
import ee.lang.gen.common.LangCommonContextFactory

open class GoContextBuilder<M>(name: String, macroController: MacroController, builder: M.() -> GoContext)
    : ContextBuilder<M>(name, macroController, builder)

open class LangGoContextFactory(targetAsSingleModule: Boolean) : LangCommonContextFactory(targetAsSingleModule) {

    override fun contextBuilder(derived: DerivedController): GoContextBuilder<StructureUnitI<*>> {
        return GoContextBuilder(CONTEXT_GO, macroController) {
            val structureUnit = this
            GoContext(
                    moduleFolder = structureUnit.artifact(),
                    namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = derived,
                    macroController = macroController)
        }
    }

    open fun buildForImplOnly(): ContextBuilder<StructureUnitI<*>> {
        val derivedController = DerivedController()
        registerForImplOnly(derivedController)
        return contextBuilder(derivedController)
    }

    override fun buildName(item: ItemI<*>, kind: String): String {
        return if (item is ConstructorI) {
            buildNameForConstructor(item, kind)
        } else if (item is OperationI) {
            buildNameForOperation(item, kind)
        } else {
            super.buildName(item, kind)
        }
    }

    override fun buildNameForConstructor(item: ConstructorI<*>, kind: String) =
            (item.name() == item.parent().name()).ifElse({ "New${buildNameCommon(item, kind).capitalize()}" }, {
                "New${buildNameCommon(item.parent(), kind).capitalize()}${buildNameCommon(item, kind).capitalize()}"
            })

    override fun buildNameForOperation(item: OperationI<*>, kind: String): String {
        return buildNameCommon(item, kind).capitalize()
    }

    companion object {
        const val CONTEXT_GO = "go"
    }
}
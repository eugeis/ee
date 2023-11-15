package ee.lang.gen.go

import ee.common.ext.ifElse
import ee.lang.*
import ee.lang.gen.common.LangCommonContextFactory
import java.util.*

open class GoContextBuilder<M>(name: String, macroController: MacroController, builder: M.() -> GoContext)
    : ContextBuilder<M>(name, macroController, builder)

open class LangGoContextFactory(targetAsSingleModule: Boolean) : LangCommonContextFactory(targetAsSingleModule) {

    override fun contextBuilder(
        derived: DerivedController, buildNamespace: StructureUnitI<*>.()->String): GoContextBuilder<StructureUnitI<*>> {
        return GoContextBuilder(CONTEXT_GO, macroController) {
            val structureUnit = this
            GoContext(
                    moduleFolder = structureUnit.artifact(),
                    namespace = structureUnit.namespace().lowercase(Locale.getDefault()),
                    derivedController = derived,
                    macroController = macroController)
        }
    }

    open fun buildForImplOnly(): ContextBuilder<StructureUnitI<*>> {
        val derivedController = DerivedController()
        registerForImplOnly(derivedController)
        return contextBuilder(derivedController)
    }

    open fun buildForIfcAndImpl(): ContextBuilder<StructureUnitI<*>> {
        val derivedController = DerivedController()
        registerForIfcAndImpl(derivedController)
        return contextBuilder(derivedController)
    }

    override fun buildName(item: ItemI<*>, kind: String): String {
        val ret = if (item is ConstructorI) {
            buildNameForConstructor(item, kind)
        } else if (item is OperationI) {
            buildNameForOperation(item, kind)
        } else {
            super.buildName(item, kind)
        }
        return ret
    }

    override fun buildNameForConstructor(item: ConstructorI<*>, kind: String) =
            (item.name() == item.parent().name()).ifElse({ "New${buildNameCommon(item, kind).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }}" }, {
                "New${buildNameCommon(item.parent(), kind).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${buildNameCommon(item, kind).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }}"
            })

    override fun buildNameForOperation(item: OperationI<*>, kind: String): String {
        return buildNameCommon(item, kind).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    companion object {
        const val CONTEXT_GO = "go"
    }
}

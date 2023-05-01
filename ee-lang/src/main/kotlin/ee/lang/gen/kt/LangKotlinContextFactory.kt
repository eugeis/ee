package ee.lang.gen.kt

import ee.lang.*
import ee.lang.ContextBuilder
import ee.lang.gen.KotlinContext
import ee.lang.gen.KotlinContextBuilder
import ee.lang.gen.common.LangCommonContextFactory
import java.util.*

open class LangKotlinContextFactory(targetAsSingleModule: Boolean) : LangCommonContextFactory(targetAsSingleModule) {

    open fun buildForImplOnly(scope: String = "main"): ContextBuilder<StructureUnitI<*>> {
        val controller = DerivedController()

        controller.registerKinds(listOf(LangDerivedKind.API, LangDerivedKind.IMPL), { name() }, isNotPartOfNativeTypes)

        return contextBuilder(controller, scope)
    }

    open fun buildForDslBuilder(scope: String = "main"): ContextBuilder<StructureUnitI<*>> {
        val controller = DerivedController()

        controller.registerKind(LangDerivedKind.API, { "${name()}I" }, isNotPartOfNativeTypes)
        controller.registerKind(LangDerivedKind.IMPL, { name() }, isNotPartOfNativeTypes)
        controller.registerKind(LangDerivedKind.MANUAL, { "${name()}B" }, isNotPartOfNativeTypes)

        return contextBuilder(controller, scope)
    }

    protected open fun contextBuilder(controller: DerivedController, scope: String): ContextBuilder<StructureUnitI<*>> {
        return KotlinContextBuilder(CONTEXT_COMMON, scope, macroController) {
            KotlinContext(
                namespace().lowercase(Locale.getDefault()), artifact(), "src-gen/$scope/kotlin",
                    derivedController = controller, macroController = macroController)
        }
    }
}
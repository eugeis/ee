package ee.lang.gen.common

import ee.lang.*
import ee.lang.gen.go.GoContext
import ee.lang.gen.go.GoContextBuilder
import ee.lang.gen.go.LangGoContextFactory
import ee.lang.gen.java.j
import ee.lang.gen.kt.k

open class LangCommonContextFactory(val targetAsSingleModule: Boolean = true) {
    val isNotPartOfNativeTypes: ItemI<*>.() -> Boolean = { n != parent() && j != parent() && k != parent() }
    val macroController = MacroController()

    open fun build(): ContextBuilder<StructureUnitI<*>> {
        val derivedController = DerivedController()
        registerForImplOnly(derivedController)
        return contextBuilder(derivedController)
    }

    protected open fun registerForImplOnly(derived: DerivedController) {
        derived.registerKinds(listOf(LangDerivedKind.API, LangDerivedKind.IMPL), { buildName(this, it) },
                isNotPartOfNativeTypes)
        //derived.dynamicTransformer = DerivedByTransformer("DYNAMIC", { buildNameDynamic(this, it) }, isNotPartOfNativeTypes)

    }

    protected open fun registerForIfcAndImpl(derived: DerivedController) {
        derived.registerKind(LangDerivedKind.API, { buildName(this, it) }, isNotPartOfNativeTypes)
        derived.registerKind(LangDerivedKind.IMPL, { "${name()}Impl" }, isNotPartOfNativeTypes)
    }

    protected open fun contextBuilder(derived: DerivedController): ContextBuilder<StructureUnitI<*>> {
        return ContextBuilder(CONTEXT_COMMON, macroController) {
            GenerationContext(
                    namespace = namespace().toLowerCase(),
                    moduleFolder = computeModuleFolder(),
                    derivedController = derived, macroController = macroController)
        }
    }

    protected open fun StructureUnitI<*>.computeModuleFolder(): String = artifact()

    protected open fun buildName(item: ItemI<*>, kind: String): String = buildNameCommon(item, kind)

    protected open fun buildNameCommon(item: ItemI<*>, kind: String): String = item.name()
    protected open fun buildNameDynamic(item: ItemI<*>, kind: String): String = "${buildName(item, kind)}$kind"
    protected open fun buildNameForConstructor(item: ConstructorI<*>, kind: String) = buildName(item, kind)
    protected open fun buildNameForOperation(item: OperationI<*>, kind: String) = buildName(item, kind)

    companion object {
        const val CONTEXT_COMMON = "common"
    }
}

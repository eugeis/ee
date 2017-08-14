package ee.lang.gen.common

import ee.lang.*
import ee.lang.gen.java.j
import ee.lang.gen.kt.k

open class LangCommonContextFactory {
    val isNotPartOfNativeTypes: ItemI.() -> Boolean = { n != parent() && j != parent() && k != parent() }
    val macroController = MacroController()

    open fun build(): StructureUnitI.() -> GenerationContext {
        val derivedController = DerivedController()
        registerForImplOnly(derivedController)
        return contextBuilder(derivedController)
    }

    protected open fun registerForImplOnly(derived: DerivedController) {
        derived.registerKinds(listOf(LangDerivedKind.API, LangDerivedKind.IMPL), { buildName(this, it) }, isNotPartOfNativeTypes)
        //derived.dynamicTransformer = DerivedByTransformer("DYNAMIC", { buildNameDynamic(this, it) }, isNotPartOfNativeTypes)

    }

    protected open fun contextBuilder(derived: DerivedController): StructureUnitI.() -> GenerationContext {
        return {
            val su = this
            GenerationContext(moduleFolder = su.artifact(), namespace = su.namespace().toLowerCase(),
                    derivedController = derived, macroController = macroController
            )
        }
    }

    protected open fun buildName(item: ItemI, kind: String): String = buildNameCommon(item, kind)

    protected open fun buildNameCommon(item: ItemI, kind: String): String = item.name()
    protected open fun buildNameDynamic(item: ItemI, kind: String): String = "${buildName(item, kind)}$kind"
    protected open fun buildNameForConstructor(item: ConstructorI, kind: String) = buildName(item, kind)
}
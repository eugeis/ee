package ee.lang.gen.doc

import ee.lang.*
import ee.lang.ContextBuilder
import ee.lang.gen.KotlinContext
import ee.lang.gen.KotlinContextBuilder
import ee.lang.gen.common.LangCommonContextFactory

open class LangMarkdownContextFactory : LangCommonContextFactory {
    protected val singleModule: Boolean

    constructor(singleModule: Boolean = true) {
        this.singleModule = singleModule
    }

    protected open fun contextBuilder(controller: DerivedController, scope: String): ContextBuilder<StructureUnitI<*>> {
        return MkContextBuilder(CONTEXT_COMMON, scope, macroController) {
            MkContext(namespace().toLowerCase(), artifact(), "src-gen/$scope/doc",
                    derivedController = controller, macroController = macroController)
        }
    }
}
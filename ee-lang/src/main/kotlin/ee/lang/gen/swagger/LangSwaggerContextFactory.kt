package ee.lang.gen.swagger

import ee.lang.*
import ee.lang.gen.java.j
import ee.lang.gen.kt.k

open class LangSwaggerContextFactory {
    private val isNotPartOfNativeTypes: ItemI.() -> Boolean = { n != parent() && j != parent() && k != parent() }

    open fun build(): StructureUnitI.() -> GenerationContext {
        val controller = DerivedController()

        controller.registerKinds(listOf(LangDerivedKind.API, LangDerivedKind.IMPL), isNotPartOfNativeTypes, {  buildName()  })

        return contextBuilder(controller)
    }

    protected open fun contextBuilder(controller: DerivedController): StructureUnitI.() -> GenerationContext {
        return {
            val structureUnit = this
            GenerationContext(moduleFolder = structureUnit.artifact(),
                    namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = controller
            )
        }
    }

    protected open fun ItemI.buildName(): String = name()
}
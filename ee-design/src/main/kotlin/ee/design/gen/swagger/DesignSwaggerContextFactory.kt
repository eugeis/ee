package ee.design.gen.swagger

import ee.design.CompI
import ee.lang.DerivedController
import ee.lang.GenerationContext
import ee.lang.StructureUnitI
import ee.lang.findThisOrParent
import ee.lang.gen.go.LangCommonContextFactory

open class DesignSwaggerContextFactory : LangCommonContextFactory() {
    override fun contextBuilder(derived: DerivedController): StructureUnitI.() -> GenerationContext {
        return {
            val structureUnit = this
            val compOrStructureUnit = this.findThisOrParent(CompI::class.java) ?: structureUnit
            GenerationContext(moduleFolder = "${compOrStructureUnit.artifact()}/${compOrStructureUnit.artifact()}",
                    genFolder = "src-gen/main/swagger", genFolderDeletable = true,
                    namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = derived,
                    macroController = macroController
            )
        }
    }
}
package ee.design.gen.swagger

import ee.design.CompI
import ee.lang.*
import ee.lang.gen.common.LangCommonContextFactory
import java.util.*

open class DesignSwaggerContextFactory : LangCommonContextFactory() {
    override fun contextBuilder(
        derived: DerivedController, buildNamespace: StructureUnitI<*>.()->String): ContextBuilder<StructureUnitI<*>> {
        return ContextBuilder(CONTEXT_COMMON, macroController){
            val structureUnit = this
            val compOrStructureUnit = this.findThisOrParentUnsafe(CompI::class.java) ?: structureUnit
            GenerationContext(moduleFolder = "${compOrStructureUnit.artifact()}/${compOrStructureUnit.artifact()}",
                genFolder = "src-gen/main/swagger", genFolderDeletable = true,
                namespace = structureUnit.namespace().lowercase(Locale.getDefault()), derivedController = derived,
                macroController = macroController)
        }
    }
}

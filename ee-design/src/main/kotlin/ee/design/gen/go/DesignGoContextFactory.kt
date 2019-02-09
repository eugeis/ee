package ee.design.gen.go

import ee.design.CommandI
import ee.design.CompI
import ee.design.EventI
import ee.lang.*
import ee.lang.gen.go.GoContext
import ee.lang.gen.go.GoContextBuilder
import ee.lang.gen.go.LangGoContextFactory

open class DesignGoContextFactory : LangGoContextFactory() {
    override fun contextBuilder(derived: DerivedController): GoContextBuilder<StructureUnitI<*>> {
        return GoContextBuilder(CONTEXT_GO, macroController){
            val structureUnit = this
            val compOrStructureUnit = this.findThisOrParentUnsafe(CompI::class.java) ?: structureUnit

            GoContext(moduleFolder = "${compOrStructureUnit.artifact()}/${compOrStructureUnit.artifact()}",
                namespace = structureUnit.namespace().toLowerCase(), derivedController = derived,
                macroController = macroController)
        }
    }

    override fun buildName(item: ItemI<*>, kind: String): String {
        return when (item) {
            is CommandI<*> -> buildNameForCommand(item, kind)
            is EventI<*>   -> buildNameForEvent(item, kind)
            else           -> super.buildName(item, kind)
        }
    }

    protected open fun buildNameForCommand(item: CommandI<*>, kind: String) = item.nameAndParentName().capitalize()
    protected open fun buildNameForEvent(item: EventI<*>, kind: String) = item.parentNameAndName().capitalize()
}

package ee.design.gen.ts

import ee.design.CommandIB
import ee.design.CompIB
import ee.design.EventIB
import ee.lang.*
import ee.lang.gen.ts.LangTsContextFactory
import ee.lang.gen.ts.TsContext

open class DesignTsContextFactory : LangTsContextFactory() {
    override fun contextBuilder(derived: DerivedController): StructureUnitIB<*>.() -> TsContext {
        return {
            val structureUnit = this
            val compOrStructureUnit = this.findThisOrParentUnsafe(CompIB::class.java) ?: structureUnit
            TsContext(moduleFolder = "${compOrStructureUnit.artifact()}/${compOrStructureUnit.artifact()}_ng",
                    namespace = structureUnit.namespace().toLowerCase(),
                    derivedController = derived,
                    macroController = macroController
            )
        }
    }

    override fun registerForImplOnly(derived: DerivedController) {
        super.registerForImplOnly(derived)
    }

    override fun buildName(item: ItemIB<*>, kind: String): String {
        return if (item is CommandIB<*>) {
            buildNameForCommand(item, kind)
        } else if (item is EventIB<*>) {
            buildNameForEvent(item, kind)
        } else {
            super.buildName(item, kind)
        }
    }

    protected open fun buildNameForCommand(item: CommandIB<*>, kind: String) = item.nameAndParentName().capitalize()
    protected open fun buildNameForEvent(item: EventIB<*>, kind: String) = item.parentNameAndName().capitalize()
}
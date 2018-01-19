package ee.design.gen.ts

import ee.lang.CompositeI
import ee.lang.NamesI
import ee.lang.TemplateI
import ee.lang.gen.ts.LangTsTemplates

open class DesignTsTemplates : LangTsTemplates {
    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI<*>) -> NamesI) : super(defaultNameBuilder)
    /*
        open fun command(nameBuilder: TemplateI<CommandI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
                = Template<CommandI>("Command", nameBuilder) { item, c -> item.toGoCommandImpl(c) }

        open fun commandTypes(nameBuilder: TemplateI<EntityI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
                = Template<EntityI>("CommandTypes", nameBuilder) { item, c -> item.toGoCommandTypes(c) }

        open fun eventTypes(nameBuilder: TemplateI<EntityI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
                = Template<EntityI>("EventTypes", nameBuilder) { item, c -> item.toGoEventTypes(c) }
    */
}
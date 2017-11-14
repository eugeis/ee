package ee.design.gen.go

import ee.design.CommandIB
import ee.design.EntityIB
import ee.lang.*
import ee.lang.gen.go.LangGoTemplates

open class DesignGoTemplates : LangGoTemplates {
    constructor(defaultNameBuilder: TemplateI<*>.(CompositeIB<*>) -> NamesI) : super(defaultNameBuilder)

    open fun command(nameBuilder: TemplateI<CommandIB<*>>.(CompilationUnitIB<*>) -> NamesI = defaultNameBuilder)
            = Template("Command", nameBuilder) { item, c -> item.toGoCommandImpl(c) }

    open fun commandTypes(nameBuilder: TemplateI<EntityIB<*>>.(CompilationUnitIB<*>) -> NamesI = defaultNameBuilder)
            = Template("CommandTypes", nameBuilder) { item, c -> item.toGoCommandTypes(c) }

    open fun eventTypes(nameBuilder: TemplateI<EntityIB<*>>.(CompilationUnitIB<*>) -> NamesI = defaultNameBuilder)
            = Template("EventTypes", nameBuilder) { item, c -> item.toGoEventTypes(c) }

    open fun <T : EntityIB<*>> entity(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder)
            = Template("Entity", nameBuilder) { item, c -> item.toGoEntityImpl(c) }

}
package ee.design.gen.go

import ee.design.CommandI
import ee.design.EntityI
import ee.lang.*
import ee.lang.gen.go.LangGoTemplates
import ee.lang.gen.go.toGoEnum
import ee.lang.gen.go.toGoImpl

open class DesignGoTemplates : LangGoTemplates {
    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI) -> NamesI) : super(defaultNameBuilder)

    open fun command(nameBuilder: TemplateI<CommandI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<CommandI>("Command", nameBuilder) { item, c -> item.toGoCommandImpl(c) }

    open fun commandTypes(nameBuilder: TemplateI<EntityI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<EntityI>("CommandTypes", nameBuilder) { item, c -> item.toGoCommandTypes(c) }
}
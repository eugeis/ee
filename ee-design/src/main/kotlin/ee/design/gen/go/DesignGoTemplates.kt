package ee.design.gen.go

import ee.design.CommandI
import ee.lang.CompositeI
import ee.lang.NamesI
import ee.lang.Template
import ee.lang.TemplateI
import ee.lang.gen.go.LangGoTemplates

open class DesignGoTemplates : LangGoTemplates {
    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI) -> NamesI) : super(defaultNameBuilder)

    fun command(nameBuilder: TemplateI<CommandI>.(CommandI) -> NamesI = defaultNameBuilder)
            = Template<CommandI>("Command", nameBuilder) { item, c -> item.toGoCommand(c) }

}
package ee.design.gen.go

import ee.design.EntityI
import ee.lang.CompositeI
import ee.lang.NamesI
import ee.lang.Template
import ee.lang.TemplateI
import ee.lang.gen.go.LangGoTemplates

open class DesignGoTemplates : LangGoTemplates {
    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI) -> NamesI) : super(defaultNameBuilder)

    open fun <T : EntityI> aggregate(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder)
            = Template<T>("aggregate", nameBuilder) { item, c -> item.toGoEventhorizonAggregate(c) }
}
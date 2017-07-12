package ee.design.gen.go

import ee.lang.CompositeI
import ee.lang.NamesI
import ee.lang.TemplateI
import ee.lang.gen.go.LangGoTemplates

open class DesignGoTemplates : LangGoTemplates {
    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI) -> NamesI) : super(defaultNameBuilder)
}
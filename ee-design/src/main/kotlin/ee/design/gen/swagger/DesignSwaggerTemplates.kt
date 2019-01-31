package ee.design.gen.swagger

import ee.design.CompI
import ee.lang.CompositeI
import ee.lang.NamesI
import ee.lang.Template
import ee.lang.TemplateI
import ee.lang.gen.swagger.itemNameAsSwaggerFileName

open class DesignSwaggerTemplates {
    private val defaultNameBuilder: TemplateI<*>.(CompositeI<*>) -> NamesI

    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI<*>) -> NamesI = itemNameAsSwaggerFileName) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    open fun model(nameBuilder: TemplateI<CompI<*>>.(CompositeI<*>) -> NamesI = defaultNameBuilder) =
        Template("Model", nameBuilder) { item, c -> item.toSwagger(c) }

}
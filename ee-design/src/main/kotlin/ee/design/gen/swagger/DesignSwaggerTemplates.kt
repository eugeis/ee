package ee.design.gen.swagger

import ee.design.CompIB
import ee.lang.CompositeIB
import ee.lang.NamesI
import ee.lang.Template
import ee.lang.TemplateI
import ee.lang.gen.swagger.itemNameAsSwaggerFileName

open class DesignSwaggerTemplates {
    val defaultNameBuilder: TemplateI<*>.(CompositeIB<*>) -> NamesI

    constructor(defaultNameBuilder: TemplateI<*>.(CompositeIB<*>) -> NamesI = itemNameAsSwaggerFileName) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    open fun model(nameBuilder: TemplateI<CompIB<*>>.(CompositeIB<*>) -> NamesI = defaultNameBuilder)
            = Template<CompIB<*>>("Model", nameBuilder) { item, c -> item.toSwagger(c) }

}
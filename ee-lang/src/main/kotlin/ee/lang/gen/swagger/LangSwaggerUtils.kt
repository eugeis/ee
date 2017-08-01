package ee.lang.gen.swagger

import ee.lang.CompositeI
import ee.lang.Names
import ee.lang.TemplateI

val itemNameAsSwaggerFileName: TemplateI<*>.(CompositeI) -> Names = {
    Names("${it.name()}.yml")
}
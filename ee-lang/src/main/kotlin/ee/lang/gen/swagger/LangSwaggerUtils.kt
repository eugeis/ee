package ee.lang.gen.swagger

import ee.lang.CompositeIB
import ee.lang.Names
import ee.lang.TemplateI

val itemNameAsSwaggerFileName: TemplateI<*>.(CompositeIB<*>) -> Names = {
    Names("${it.name()}.yml")
}
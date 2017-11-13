package ee.lang.gen

import ee.lang.CompositeIB
import ee.lang.NamesI
import ee.lang.Template
import ee.lang.TemplateI
import ee.lang.gen.kt.toKotlinDslBuilder
import ee.lang.gen.kt.toKotlinDslBuilderI
import ee.lang.gen.kt.toKotlinDslObjectTree

open class LangGenKotlinTemplates {
    val defaultNameBuilder: TemplateI<CompositeIB<*>>.(CompositeIB<*>) -> NamesI

    constructor(defaultNameBuilder: TemplateI<CompositeIB<*>>.(CompositeIB<*>) -> NamesI) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    fun dslBuilderI(nameBuilder: TemplateI<CompositeIB<*>>.(CompositeIB<*>) -> NamesI = defaultNameBuilder)
            = Template("BuilderI", nameBuilder) { item, c -> item.toKotlinDslBuilderI(c) }

    fun dslObjectTree(nameBuilder: TemplateI<CompositeIB<*>>.(CompositeIB<*>) -> NamesI = defaultNameBuilder)
            = Template("ObjectTree", nameBuilder) { item, c -> item.toKotlinDslObjectTree(c) }

    fun dslBuilder(nameBuilder: TemplateI<CompositeIB<*>>.(CompositeIB<*>) -> NamesI = defaultNameBuilder)
            = Template("Builder", nameBuilder) { item, c -> item.toKotlinDslBuilder(c) }
}
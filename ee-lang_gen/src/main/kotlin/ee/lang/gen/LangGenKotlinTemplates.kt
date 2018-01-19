package ee.lang.gen

import ee.lang.CompositeI
import ee.lang.NamesI
import ee.lang.Template
import ee.lang.TemplateI
import ee.lang.gen.kt.toKotlinDslBuilder
import ee.lang.gen.kt.toKotlinDslBuilderI
import ee.lang.gen.kt.toKotlinDslObjectTree

open class LangGenKotlinTemplates {
    val defaultNameBuilder: TemplateI<CompositeI<*>>.(CompositeI<*>) -> NamesI

    constructor(defaultNameBuilder: TemplateI<CompositeI<*>>.(CompositeI<*>) -> NamesI) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    fun dslBuilderI(nameBuilder: TemplateI<CompositeI<*>>.(CompositeI<*>) -> NamesI = defaultNameBuilder) =
        Template("BuilderI", nameBuilder) { item, c -> item.toKotlinDslBuilderI(c) }

    fun dslObjectTree(nameBuilder: TemplateI<CompositeI<*>>.(CompositeI<*>) -> NamesI = defaultNameBuilder) =
        Template("ObjectTree", nameBuilder) { item, c -> item.toKotlinDslObjectTree(c) }

    fun dslBuilder(nameBuilder: TemplateI<CompositeI<*>>.(CompositeI<*>) -> NamesI = defaultNameBuilder) =
        Template("Builder", nameBuilder) { item, c -> item.toKotlinDslBuilder(c) }
}
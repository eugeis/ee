package ee.lang.gen

import ee.lang.CompositeI
import ee.lang.NamesI
import ee.lang.Template
import ee.lang.TemplateI
import ee.lang.gen.kt.*

open class LangGenKotlinTemplates {
    val defaultNameBuilder: TemplateI<CompositeI>.(CompositeI) -> NamesI

    constructor(defaultNameBuilder: TemplateI<CompositeI>.(CompositeI) -> NamesI) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    fun dslBuilderI(nameBuilder: TemplateI<CompositeI>.(CompositeI) -> NamesI = defaultNameBuilder)
            = Template<CompositeI>("BuilderI", nameBuilder) { item, c -> item.toKotlinDslBuilderI(c) }

    fun dslObjectTree(nameBuilder: TemplateI<CompositeI>.(CompositeI) -> NamesI = defaultNameBuilder)
            = Template<CompositeI>("ObjectTree", nameBuilder) { item, c -> item.toKotlinDslObjectTree(c) }

    fun dslBuilder(nameBuilder: TemplateI<CompositeI>.(CompositeI) -> NamesI = defaultNameBuilder)
            = Template<CompositeI>("Builder", nameBuilder) { item, c -> item.toKotlinDslBuilder(c) }

    fun isEmptyExt(nameBuilder: TemplateI<CompositeI>.(CompositeI) -> NamesI = defaultNameBuilder)
            = Template<CompositeI>("IsEmptyExt", nameBuilder) { item, c -> item.toKotlinIsEmptyExt(c) }
}
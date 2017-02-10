package ee.lang.gen

import ee.lang.CompositeI
import ee.lang.NamesI
import ee.lang.Template
import ee.lang.gen.kt.*

open class KotlinTemplates {
    val defaultNameBuilder: Template<CompositeI>.(CompositeI) -> NamesI

    constructor(defaultNameBuilder: Template<CompositeI>.(CompositeI) -> NamesI) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    fun dslBuilderI(nameBuilder: Template<CompositeI>.(CompositeI) -> NamesI = defaultNameBuilder)
            = Template<CompositeI>("BuilderI", nameBuilder) { item, c -> item.toKotlinDslBuilderI(c) }

    fun dslComposite(nameBuilder: Template<CompositeI>.(CompositeI) -> NamesI = defaultNameBuilder)
            = Template<CompositeI>("Composite", nameBuilder) { item, c -> item.toKotlinDslComposite(c) }

    fun dslObjectTree(nameBuilder: Template<CompositeI>.(CompositeI) -> NamesI = defaultNameBuilder)
            = Template<CompositeI>("ObjectTree", nameBuilder) { item, c -> item.toKotlinDslObjectTree(c) }

    fun dslBuilder(nameBuilder: Template<CompositeI>.(CompositeI) -> NamesI = defaultNameBuilder)
            = Template<CompositeI>("Builder", nameBuilder) { item, c -> item.toKotlinDslBuilder(c) }

    fun isEmptyExt(nameBuilder: Template<CompositeI>.(CompositeI) -> NamesI = defaultNameBuilder)
            = Template<CompositeI>("IsEmptyExt", nameBuilder) { item, c -> item.toKotlinIsEmptyExt(c) }
}
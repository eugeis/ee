package ee.lang.gen

import ee.lang.CompilationUnitI
import ee.lang.NamesI
import ee.lang.Template
import ee.lang.gen.kt.*

open class KotlinTemplates {
    val defaultNameBuilder: Template<CompilationUnitI>.(CompilationUnitI) -> NamesI

    constructor(defaultNameBuilder: Template<CompilationUnitI>.(CompilationUnitI) -> NamesI) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    fun dslBuilderI(nameBuilder: Template<CompilationUnitI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitI>("BuilderI", nameBuilder) { item, c -> item.toKotlinDslBuilderI(c) }

    fun dslComposite(nameBuilder: Template<CompilationUnitI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitI>("Composite", nameBuilder) { item, c -> item.toKotlinDslComposite(c) }

    fun dslObjectTree(nameBuilder: Template<CompilationUnitI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitI>("ObjectTree", nameBuilder) { item, c -> item.toKotlinObjectTree(c) }

    fun dslBuilder(nameBuilder: Template<CompilationUnitI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitI>("Builder", nameBuilder) { item, c -> item.toKotlinDslBuilder(c) }

    fun isEmptyExt(nameBuilder: Template<CompilationUnitI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitI>("IsEmptyExt", nameBuilder) { item, c -> item.toKotlinIsEmptyExt(c) }

}

package ee.lang.gen.doc

import ee.lang.*
import ee.lang.gen.itemNameAsKotlinFileName

open class LangMarkdownTemplates {
    private val defaultNameBuilder: TemplateI<*>.(CompilationUnitI<*>) -> NamesI

    constructor(defaultNameBuilder: TemplateI<*>.(CompilationUnitI<*>) -> NamesI = itemNameAsMarkdownFileName) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    open fun pojoImpl(
            nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
            Template("PojoImpl", nameBuilder) { item, c -> item.toMarkdownClassImpl(c) }

    open fun pojoPlainImpl(
            nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
            Template("PojoPlainImpl", nameBuilder) { item, c -> item.toPlainUmlClassImpl(c,
                    generateComments = false) }

    open fun pojoPlainImplWithComments(
            nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
            Template("PojoPlainImplWithComments", nameBuilder) { item, c -> item.toPlainUmlClassImpl(c,
                    generateComments = true) }

}

package ee.lang.gen.doc

import ee.lang.*

open class LangMarkdownTemplates {
    val defaultNameBuilder: TemplateI<*>.(CompositeI<*>) -> NamesI

    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI<*>) -> NamesI = itemNameAsMarkdownFileName) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    open fun pojoImpl(
            nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
            Template("PojoImpl", nameBuilder) { item, c -> item.toMarkdownClassImpl(c) }

    open fun pojoPlainImplClass(
            nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
            Template("pojoPlainImplClass", nameBuilder) { item, c -> item.toPlantUmlClassImpl(c) }

    open fun pojoPlainSuperClass(
            nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
            Template("pojoPlainImplClass", nameBuilder) { item, c -> item.toPlantUmlSuperClass(c) }

    open fun pojoPlainImplWithComments(
            nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
            Template("PojoPlainImplWithComments", nameBuilder) { item, c -> item.toPlantUmlClassDetails(c,
                    generateComments = true) }




}

package ee.lang.gen.go

import ee.lang.*

open class LangGoTemplates {
    val defaultNameBuilder: TemplateI<*>.(CompositeI) -> NamesI

    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI) -> NamesI = itemNameAsGoFileName) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    open fun enum(nameBuilder: TemplateI<EnumTypeI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<EnumTypeI>("Enum", nameBuilder) { item, c -> item.toGoEnum(c) }

    open fun pojo(nameBuilder: TemplateI<CompilationUnitI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitI>("Pojo", nameBuilder) { item, c -> item.toGoImpl(c, DerivedNames.API.name) }

}

package ee.lang.gen.go

import ee.lang.*

open class LangGoTemplates {
    val defaultNameBuilder: TemplateI<*>.(CompilationUnitI) -> NamesI

    constructor(defaultNameBuilder: TemplateI<*>.(CompilationUnitI) -> NamesI = itemNameAsGoFileName) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    fun enum(nameBuilder: TemplateI<EnumTypeI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<EnumTypeI>("Enum", nameBuilder) { item, c -> item.toGoEnum(c) }

    fun pojo(nameBuilder: TemplateI<CompilationUnitI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitI>("Pojo", nameBuilder) { item, c -> item.toGoImpl(c, DerivedNames.API.name) }

}

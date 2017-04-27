package ee.lang.gen.go

import ee.lang.*
import ee.lang.gen.itemNameAsKotlinFileName
import ee.lang.gen.kt.*

open class LangGoTemplates {
    val defaultNameBuilder: TemplateI<*>.(CompilationUnitI) -> NamesI

    constructor(defaultNameBuilder: TemplateI<*>.(CompilationUnitI) -> NamesI = itemNameAsKotlinFileName) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    fun pojo(nameBuilder: TemplateI<CompilationUnitI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitI>("Pojo", nameBuilder) { item, c -> item.toGoImpl(c, DerivedNames.API.name) }

}

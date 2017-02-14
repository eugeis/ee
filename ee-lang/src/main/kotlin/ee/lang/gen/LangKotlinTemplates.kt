package ee.lang.gen

import ee.lang.*
import ee.lang.gen.kt.*

open class LangKotlinTemplates {
    val defaultNameBuilder: TemplateI<*>.(CompilationUnitI) -> NamesI

    constructor(defaultNameBuilder: TemplateI<*>.(CompilationUnitI) -> NamesI = itemNameAsKotlinFileName) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    fun dslBuilderI(nameBuilder: TemplateI<CompilationUnitI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitI>("BuilderI", nameBuilder) { item, c -> item.toKotlinDslBuilderI(c) }

    fun dslComposite(nameBuilder: TemplateI<CompilationUnitI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitI>("Composite", nameBuilder) { item, c -> item.toKotlinDslComposite(c) }

    fun dslObjectTree(nameBuilder: TemplateI<CompilationUnitI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitI>("ObjectTree", nameBuilder) { item, c -> item.toKotlinObjectTree(c) }

    fun dslBuilder(nameBuilder: TemplateI<CompilationUnitI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitI>("Builder", nameBuilder) { item, c -> item.toKotlinDslBuilder(c) }

    fun isEmptyExt(nameBuilder: TemplateI<CompilationUnitI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitI>("IsEmptyExt", nameBuilder) { item, c -> item.toKotlinIsEmptyExt(c) }

    fun enum(nameBuilder: TemplateI<EnumTypeI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<EnumTypeI>("Enum", nameBuilder) { item, c -> item.toKotlinEnum(c) }

    fun enumParseMethod(nameBuilder: TemplateI<EnumTypeI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<EnumTypeI>("EnumParseMethod", nameBuilder) { item, c -> item.toKotlinEnumParseMethod(c) }

    fun pojo(nameBuilder: TemplateI<CompilationUnitI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitI>("Pojo", nameBuilder) { item, c -> item.toKotlinImpl(c, DerivedNames.API.name) }

}

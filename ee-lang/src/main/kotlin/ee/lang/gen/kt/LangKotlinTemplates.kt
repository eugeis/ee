package ee.lang.gen.kt

import ee.lang.*
import ee.lang.gen.itemNameAsKotlinFileName

open class LangKotlinTemplates {
    val defaultNameBuilder: TemplateI<*>.(CompilationUnitI<*>) -> NamesI

    constructor(defaultNameBuilder: TemplateI<*>.(CompilationUnitI<*>) -> NamesI = itemNameAsKotlinFileName) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    open fun dslBuilderI(
        nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template<CompilationUnitI<*>>("BuilderI", nameBuilder) { item, c -> item.toKotlinDslBuilderI(c) }

    open fun dslObjectTree(
        nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template<CompilationUnitI<*>>("ObjectTree", nameBuilder) { item, c -> item.toKotlinObjectTree(c) }

    open fun dslBuilder(
        nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template<CompilationUnitI<*>>("Builder", nameBuilder) { item, c -> item.toKotlinDslBuilder(c) }

    open fun enum(nameBuilder: TemplateI<EnumTypeI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template<EnumTypeI<*>>("Enum", nameBuilder) { item, c -> item.toKotlinEnum(c) }

    open fun enumParseMethod(
        nameBuilder: TemplateI<EnumTypeI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template<EnumTypeI<*>>("EnumParseMethod", nameBuilder) { item, c -> item.toKotlinEnumParseMethod(c) }

    open fun pojo(nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template<CompilationUnitI<*>>("Pojo", nameBuilder) { item, c -> item.toKotlinImpl(c, LangDerivedKind.API) }

}

package ee.lang.gen.kt

import ee.lang.*
import ee.lang.gen.itemNameAsKotlinFileName

open class LangKotlinTemplates {
    val defaultNameBuilder: TemplateI<*>.(CompilationUnitIB<*>) -> NamesI

    constructor(defaultNameBuilder: TemplateI<*>.(CompilationUnitIB<*>) -> NamesI = itemNameAsKotlinFileName) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    open fun dslBuilderI(nameBuilder: TemplateI<CompilationUnitIB<*>>.(CompilationUnitIB<*>) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitIB<*>>("BuilderI", nameBuilder) { item, c -> item.toKotlinDslBuilderI(c) }

    open fun dslObjectTree(nameBuilder: TemplateI<CompilationUnitIB<*>>.(CompilationUnitIB<*>) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitIB<*>>("ObjectTree", nameBuilder) { item, c -> item.toKotlinObjectTree(c) }

    open fun dslBuilder(nameBuilder: TemplateI<CompilationUnitIB<*>>.(CompilationUnitIB<*>) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitIB<*>>("Builder", nameBuilder) { item, c -> item.toKotlinDslBuilder(c) }

    open fun enum(nameBuilder: TemplateI<EnumTypeIB<*>>.(CompilationUnitIB<*>) -> NamesI = defaultNameBuilder)
            = Template<EnumTypeIB<*>>("Enum", nameBuilder) { item, c -> item.toKotlinEnum(c) }

    open fun enumParseMethod(nameBuilder: TemplateI<EnumTypeIB<*>>.(CompilationUnitIB<*>) -> NamesI = defaultNameBuilder)
            = Template<EnumTypeIB<*>>("EnumParseMethod", nameBuilder) { item, c -> item.toKotlinEnumParseMethod(c) }

    open fun pojo(nameBuilder: TemplateI<CompilationUnitIB<*>>.(CompilationUnitIB<*>) -> NamesI = defaultNameBuilder)
            = Template<CompilationUnitIB<*>>("Pojo", nameBuilder) { item, c -> item.toKotlinImpl(c, LangDerivedKind.API) }

}

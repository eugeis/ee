package ee.lang.gen.go

import ee.lang.*

open class LangGoTemplates {
    val defaultNameBuilder: TemplateI<*>.(CompositeIB<*>) -> NamesI

    constructor(defaultNameBuilder: TemplateI<*>.(CompositeIB<*>) -> NamesI = itemNameAsGoFileName) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    open fun enum(nameBuilder: TemplateI<EnumTypeIB<*>>.(CompilationUnitIB<*>) -> NamesI = defaultNameBuilder)
            = Template("Enum", nameBuilder) { item, c -> item.toGoEnum(c) }

    open fun <T : CompilationUnitIB<*>> pojo(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder)
            = Template("Pojo", nameBuilder) { item, c -> item.toGoImpl(c) }

    open fun <T : CompilationUnitIB<*>> pojoExcludePropsWithValue(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder)
            = Template("pojoExcludePropsWithValue", nameBuilder) { item, c -> item.toGoImpl(c = c, excludePropsWithValue = true) }

}

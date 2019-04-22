package ee.lang.gen.go

import ee.lang.*

open class LangGoTemplates {
    val defaultNameBuilder: TemplateI<*>.(CompositeI<*>) -> NamesI

    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI<*>) -> NamesI = itemNameAsGoFileName) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    open fun enum(nameBuilder: TemplateI<EnumTypeI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template("Enum", nameBuilder) { item, c -> item.toGoEnum(c) }

    open fun <T : CompilationUnitI<*>> pojo(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("Pojo", nameBuilder) { item, c -> item.toGoImpl(c) }

    open fun <T : CompilationUnitI<*>> newTestInstance(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("NewTestInstance", nameBuilder) { item, c -> item.toGoNewTestInstance(c) }

    open fun <T : CompilationUnitI<*>> pojoExcludePropsWithValue(
        nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("pojoExcludePropsWithValue", nameBuilder) { item, c ->
            item.toGoImpl(c = c, excludePropsWithValue = true)
        }

}

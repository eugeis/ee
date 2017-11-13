package ee.lang.gen.ts

import ee.lang.*

open class LangTsTemplates {
    val defaultNameBuilder: TemplateI<*>.(CompositeIB<*>) -> NamesI

    constructor(defaultNameBuilder: TemplateI<*>.(CompositeIB<*>) -> NamesI = itemNameAsTsFileName) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    open fun enum(nameBuilder: TemplateI<EnumTypeIB<*>>.(CompilationUnitIB<*>) -> NamesI = defaultNameBuilder)
            = Template("Enum", nameBuilder) { item, c -> item.toTypeScriptEnum(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitIB<*>> pojo(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder)
            = Template("Pojo", nameBuilder) { item, c -> item.toTypeScriptImpl(c, LangDerivedKind.API) }

}
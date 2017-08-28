package ee.lang.gen.ts

import ee.lang.*

open class LangTsTemplates {
    val defaultNameBuilder: TemplateI<*>.(CompositeI) -> NamesI

    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI) -> NamesI = itemNameAsTsFileName) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    open fun enum(nameBuilder: TemplateI<EnumTypeI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
            = Template<EnumTypeI>("Enum", nameBuilder) { item, c -> item.toTypeScriptEnum(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI> pojo(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder)
            = Template<T>("Pojo", nameBuilder) { item, c -> item.toTypeScriptImpl(c, LangDerivedKind.API) }

}
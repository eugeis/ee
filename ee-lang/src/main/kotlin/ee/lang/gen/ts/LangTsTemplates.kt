package ee.lang.gen.ts

import ee.lang.*

open class LangTsTemplates {
    val defaultNameBuilder: TemplateI<*>.(CompositeI<*>) -> NamesI

    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI<*>) -> NamesI = itemNameAsTsFileName) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    open fun enum(nameBuilder: TemplateI<EnumTypeI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template("Enum", nameBuilder) { item, c -> item.toTypeScriptEnum(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> pojo(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("Pojo", nameBuilder) { item, c -> item.toTypeScriptImpl(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> basicComponentTypeScript(items: BasicI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("BasicTypeScriptComponent", nameBuilder) { item, c -> item.toAngularBasicTSComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> basicComponentHTML(items: BasicI<*>, basics: List<BasicI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("BasicHTMLComponent", nameBuilder) { item, c -> item.toAngularBasicHTMLComponent(items, basics, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> basicComponentSCSS(items: BasicI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("BasicSCSSComponent", nameBuilder) { item, c -> item.toAngularBasicSCSSComponent(items, c, LangDerivedKind.API) }

}

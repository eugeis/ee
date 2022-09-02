package ee.lang.gen.ts

import ee.design.EntityI
import ee.design.ModuleI
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

    open fun <T : CompilationUnitI<*>> moduleComponentTypeScript(items: ModuleI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleTypeScriptComponent", nameBuilder) { item, c -> item.toTypeScriptModuleTSComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> moduleComponentHTML(items: ModuleI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleHTMLComponent", nameBuilder) { item, c -> item.toTypeScriptModuleHTMLComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> moduleComponentSCSS(items: ModuleI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleSCSSComponent", nameBuilder) { item, c -> item.toTypeScriptModuleSCSSComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> moduleService(items: ModuleI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleService", nameBuilder) { item, c -> item.toTypeScriptModuleService(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityViewComponentTypeScript(items: EntityI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleTypeScriptComponent", nameBuilder) { item, c -> item.toTypeScriptEntityViewTSComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityViewComponentHTML(items: EntityI<*>, enums: List<EnumTypeI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleHTMLComponent", nameBuilder) { item, c -> item.toTypeScriptEntityViewHTMLComponent(items, enums, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityViewComponentSCSS(items: EntityI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleSCSSComponent", nameBuilder) { item, c -> item.toTypeScriptEntityViewSCSSComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityListComponentTypeScript(items: EntityI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleTypeScriptComponent", nameBuilder) { item, c -> item.toTypeScriptEntityListTSComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityListComponentHTML(items: EntityI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleHTMLComponent", nameBuilder) { item, c -> item.toTypeScriptEntityListHTMLComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityListComponentSCSS(items: EntityI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleSCSSComponent", nameBuilder) { item, c -> item.toTypeScriptEntityListSCSSComponent(items, c, LangDerivedKind.API) }

    /*open fun <T : CompilationUnitI<*>> angularComponentTypeScript(items: BasicI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("TypeScriptComponent", nameBuilder) { item, c -> item.toTypeScriptComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> angularComponentHTML(items: BasicI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("HtmlComponent", nameBuilder) { item, c -> item.toHtmlComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> angularComponentCSS(items: BasicI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ScssComponent", nameBuilder) { item, c -> item.toScssComponent(items, c, LangDerivedKind.API) }*/

}

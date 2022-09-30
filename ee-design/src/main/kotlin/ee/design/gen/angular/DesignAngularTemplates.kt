package ee.design.gen.angular

import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.LangTsTemplates
import toAngularModule
import toAngularRoutingModule
import toAngularModuleHTMLComponent
import toAngularModuleSCSSComponent
import toAngularEntityViewHTMLComponent
import toAngularEntityViewSCSSComponent
import toAngularFormHTMLComponent
import toAngularFormSCSSComponent
import toAngularEntityListHTMLComponent
import toAngularEntityListSCSSComponent
import toAngularBasicHTMLComponent
import toAngularBasicSCSSComponent

open class DesignAngularTemplates : LangTsTemplates {
    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI<*>) -> NamesI) : super(defaultNameBuilder)

    open fun <T : CompilationUnitI<*>> angularModule(items: ModuleI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("AngularModule", nameBuilder) { item, c -> item.toAngularModule(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> angularRoutingModule(items: ModuleI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("AngularRoutingModule", nameBuilder) { item, c -> item.toAngularRoutingModule(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> moduleComponentHTML(items: ModuleI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleHTMLComponent", nameBuilder) { item, c -> item.toAngularModuleHTMLComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> moduleComponentSCSS(items: ModuleI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleSCSSComponent", nameBuilder) { item, c -> item.toAngularModuleSCSSComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityViewComponentHTML(items: EntityI<*>, enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityViewHTMLComponent", nameBuilder) { item, c -> item.toAngularEntityViewHTMLComponent(items, enums, basics, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityViewComponentSCSS(items: EntityI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityViewSCSSComponent", nameBuilder) { item, c -> item.toAngularEntityViewSCSSComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> formComponentHTML(items: EntityI<*>, enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityFormHTMLComponent", nameBuilder) { item, c -> item.toAngularFormHTMLComponent(items, enums, basics, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> formComponentSCSS(items: EntityI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityFormSCSSComponent", nameBuilder) { item, c -> item.toAngularFormSCSSComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityListComponentHTML(items: EntityI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityListHTMLComponent", nameBuilder) { item, c -> item.toAngularEntityListHTMLComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityListComponentSCSS(items: EntityI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityListSCSSComponent", nameBuilder) { item, c -> item.toAngularEntityListSCSSComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> basicComponentHTML(items: BasicI<*>, basics: List<BasicI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("BasicHTMLComponent", nameBuilder) { item, c -> item.toAngularBasicHTMLComponent(items, basics, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> basicComponentSCSS(items: BasicI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("BasicSCSSComponent", nameBuilder) { item, c -> item.toAngularBasicSCSSComponent(items, c, LangDerivedKind.API) }

}

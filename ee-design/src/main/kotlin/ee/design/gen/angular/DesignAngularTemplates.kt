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

    open fun <T : CompilationUnitI<*>> angularModule(module: ModuleI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("AngularModule", nameBuilder) { item, c -> item.toAngularModule(module, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> angularRoutingModule(module: ModuleI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("AngularRoutingModule", nameBuilder) { item, c -> item.toAngularRoutingModule(module, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> moduleComponentHTML(module: ModuleI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleHTMLComponent", nameBuilder) { item, c -> item.toAngularModuleHTMLComponent(module, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> moduleComponentSCSS(module: ModuleI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleSCSSComponent", nameBuilder) { item, c -> item.toAngularModuleSCSSComponent(module, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityViewComponentHTML(enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityViewHTMLComponent", nameBuilder) { item, c -> item.toAngularEntityViewHTMLComponent(enums, basics, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityViewComponentSCSS(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityViewSCSSComponent", nameBuilder) { item, c -> item.toAngularEntityViewSCSSComponent(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> formComponentHTML(enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityFormHTMLComponent", nameBuilder) { item, c -> item.toAngularFormHTMLComponent(enums, basics, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> formComponentSCSS(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityFormSCSSComponent", nameBuilder) { item, c -> item.toAngularFormSCSSComponent(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityListComponentHTML(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityListHTMLComponent", nameBuilder) { item, c -> item.toAngularEntityListHTMLComponent(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityListComponentSCSS(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityListSCSSComponent", nameBuilder) { item, c -> item.toAngularEntityListSCSSComponent(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> basicComponentHTML(basics: List<BasicI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("BasicHTMLComponent", nameBuilder) { item, c -> item.toAngularBasicHTMLComponent(basics, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> basicComponentSCSS(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("BasicSCSSComponent", nameBuilder) { item, c -> item.toAngularBasicSCSSComponent(c, LangDerivedKind.API) }

}

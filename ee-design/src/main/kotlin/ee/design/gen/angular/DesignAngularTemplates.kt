package ee.design.gen.angular

import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.LangTsTemplates
import toAngularModule
import toAngularRoutingModule
import toAngularModuleHTMLComponent
import toAngularModuleSCSS
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

    open fun <T : ModuleI<*>> angularModule(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("AngularModule", nameBuilder) { item, c -> item.toAngularModule(c, LangDerivedKind.API) }

    open fun <T : ModuleI<*>> angularRoutingModule(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("AngularRoutingModule", nameBuilder) { item, c -> item.toAngularRoutingModule(c, LangDerivedKind.API) }

    open fun <T : ModuleI<*>> moduleHTML(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleHTMLComponent", nameBuilder) { item, c -> item.toAngularModuleHTMLComponent(c, LangDerivedKind.API) }

    open fun <T : ModuleI<*>> moduleSCSS(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleSCSSComponent", nameBuilder) { item, c -> item.toAngularModuleSCSS(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityViewHTML(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityViewHTMLComponent", nameBuilder) { item, c -> item.toAngularEntityViewHTMLComponent(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityViewSCSS(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityViewSCSSComponent", nameBuilder) { item, c -> item.toAngularEntityViewSCSSComponent(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityFormHTML(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityFormHTMLComponent", nameBuilder) { item, c -> item.toAngularFormHTMLComponent(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityFormSCSS(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityFormSCSSComponent", nameBuilder) { item, c -> item.toAngularFormSCSSComponent(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityListHTML(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityListHTMLComponent", nameBuilder) { item, c -> item.toAngularEntityListHTMLComponent(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityListSCSS(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityListSCSSComponent", nameBuilder) { item, c -> item.toAngularEntityListSCSSComponent(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> basicHTML(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("BasicHTMLComponent", nameBuilder) { item, c -> item.toAngularBasicHTMLComponent(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> basicSCSS(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("BasicSCSSComponent", nameBuilder) { item, c -> item.toAngularBasicSCSSComponent(c, LangDerivedKind.API) }

}

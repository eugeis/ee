package ee.design.gen.angular

import ee.design.CompI
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.AngularDerivedType
import ee.lang.gen.ts.AngularFileFormat
import ee.lang.gen.ts.LangTsTemplates
import toAngularModule
import toAngularRoutingModule
import toAngularModuleHTMLComponent
import toAngularDefaultSCSS
import toAngularEntityViewHTMLComponent
import toAngularEntityViewSCSSComponent
import toAngularFormHTMLComponent
import toAngularFormSCSSComponent
import toAngularEntityListHTMLComponent
import toAngularEntityListSCSSComponent
import toAngularBasicHTMLComponent
import toAngularBasicSCSSComponent
import toAngularEntityAggregateViewHTMLComponent
import toAngularEnumHTMLComponent
import toAngularEnumSCSSComponent

open class DesignAngularTemplates : LangTsTemplates {
    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI<*>) -> NamesI) : super(defaultNameBuilder)

    open fun <T : ModuleI<*>> angularModule(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder, components: List<CompI<*>>) =
        Template("AngularModule", nameBuilder) { item, c -> item.toAngularModule(c, Module = AngularDerivedType.Module, components) }

    open fun <T : ModuleI<*>> angularRoutingModule(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("AngularRoutingModule", nameBuilder) { item, c -> item.toAngularRoutingModule(c, RoutingModules = AngularDerivedType.RoutingModules) }

    open fun <T : ModuleI<*>> moduleHTML(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleHTMLComponent", nameBuilder) { item, c -> item.toAngularModuleHTMLComponent(c, ViewService = AngularDerivedType.ViewService) }

    open fun <T : ModuleI<*>> moduleSCSS(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleSCSSComponent", nameBuilder) { item, c -> item.toAngularDefaultSCSS(c) }

    open fun <T : CompilationUnitI<*>> entityViewHTML(entities: List<EntityI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityViewHTMLComponent", nameBuilder) { item, c -> item.toAngularEntityViewHTMLComponent(c, entities, DataService = AngularDerivedType.DataService) }

    open fun <T : CompilationUnitI<*>> entityViewSCSS(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityViewSCSSComponent", nameBuilder) { item, c -> item.toAngularEntityViewSCSSComponent(c) }

    open fun <T : CompilationUnitI<*>> entityFormHTML(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityFormHTMLComponent", nameBuilder) { item, c -> item.toAngularFormHTMLComponent(c, DataService = AngularDerivedType.DataService) }

    open fun <T : CompilationUnitI<*>> entityFormSCSS(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityFormSCSSComponent", nameBuilder) { item, c -> item.toAngularFormSCSSComponent(c, derived = AngularFileFormat.EntityForm) }

    open fun <T : CompilationUnitI<*>> entityListHTML(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder, aggregateEntity: List<EntityI<*>>) =
        Template("EntityListHTMLComponent", nameBuilder) { item, c -> item.toAngularEntityListHTMLComponent(c, DataService = AngularDerivedType.DataService, false, (aggregateEntity.isNotEmpty() && aggregateEntity.any {prop -> prop.name().equals(item.name(), true)})) }

    open fun <T : CompilationUnitI<*>> entityAggregateViewHTML(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
            Template("EntityAggregateViewHTMLComponent", nameBuilder) { item, c -> item.toAngularEntityAggregateViewHTMLComponent(c, DataService = AngularDerivedType.DataService, true) }

    open fun <T : CompilationUnitI<*>> entityListSCSS(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
            Template("EntityListSCSSComponent", nameBuilder) { item, c -> item.toAngularEntityListSCSSComponent(c, derived = AngularFileFormat.EntityList) }

    open fun <T : CompilationUnitI<*>> entityAggregateViewSCSS(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityListSCSSComponent", nameBuilder) { item, c -> item.toAngularEntityListSCSSComponent(c, derived = AngularFileFormat.EntityList) }

    open fun <T : CompilationUnitI<*>> basicHTML(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("BasicHTMLComponent", nameBuilder) { item, c -> item.toAngularBasicHTMLComponent(c, derived = AngularFileFormat.BasicForm) }

    open fun <T : CompilationUnitI<*>> basicSCSS(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("BasicSCSSComponent", nameBuilder) { item, c -> item.toAngularBasicSCSSComponent(c, derived = AngularFileFormat.BasicForm) }

    open fun <T : CompilationUnitI<*>> enumHTML(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EnumHTMLComponent", nameBuilder) { item, c -> item.toAngularEnumHTMLComponent(c) }

    open fun <T : CompilationUnitI<*>> enumSCSS(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EnumSCSSComponent", nameBuilder) { item, c -> item.toAngularEnumSCSSComponent(c) }

}

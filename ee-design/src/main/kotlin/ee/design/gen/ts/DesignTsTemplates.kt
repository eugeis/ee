package ee.design.gen.ts

import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.LangTsTemplates
import ee.lang.gen.ts.toAngularBasicTSComponent
import toAngularEntityDataService
import toAngularEntityListHTMLComponent
import toAngularEntityListSCSSComponent
import toAngularEntityListTSComponent
import toAngularEntityViewHTMLComponent
import toAngularEntityViewSCSSComponent
import toAngularEntityViewTSComponent
import toAngularFormHTMLComponent
import toAngularFormSCSSComponent
import toAngularFormTSComponent
import toAngularModule
import toAngularModuleHTMLComponent
import toAngularModuleSCSSComponent
import toAngularModuleService
import toAngularModuleTSComponent
import toAngularRoutingModule

open class DesignTsTemplates : LangTsTemplates {
    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI<*>) -> NamesI) : super(defaultNameBuilder)
    /*
        isOpen fun command(nameBuilder: TemplateI<CommandI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
                = Template<CommandI>("Command", nameBuilder) { item, c -> item.toGoCommandImpl(c) }

        isOpen fun commandTypes(nameBuilder: TemplateI<EntityI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
                = Template<EntityI>("CommandTypes", nameBuilder) { item, c -> item.toGoCommandTypes(c) }

        isOpen fun eventTypes(nameBuilder: TemplateI<EntityI>.(CompilationUnitI) -> NamesI = defaultNameBuilder)
                = Template<EntityI>("EventTypes", nameBuilder) { item, c -> item.toGoEventTypes(c) }
    */


    open fun <T : CompilationUnitI<*>> moduleComponentTypeScript(items: ModuleI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleTypeScriptComponent", nameBuilder) { item, c -> item.toAngularModuleTSComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> moduleService(items: ModuleI<*>, modules: List<ModuleI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleService", nameBuilder) { item, c -> item.toAngularModuleService(items, modules, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityViewComponentTypeScript(items: EntityI<*>, enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityViewTypeScriptComponent", nameBuilder) { item, c -> item.toAngularEntityViewTSComponent(items, enums, basics, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> formComponentTypeScript(items: EntityI<*>, enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>, entities: List<EntityI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityFormTypeScriptComponent", nameBuilder) { item, c -> item.toAngularFormTSComponent(items, enums, basics, entities, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityListComponentTypeScript(items: EntityI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityListTypeScriptComponent", nameBuilder) { item, c -> item.toAngularEntityListTSComponent(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityDataService(items: EntityI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityDataService", nameBuilder) { item, c -> item.toAngularEntityDataService(items, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> basicComponentTypeScript(items: BasicI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("BasicTypeScriptComponent", nameBuilder) { item, c -> item.toAngularBasicTSComponent(items, c, LangDerivedKind.API) }

}

package ee.design.gen.ts

import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.LangTsTemplates
import ee.lang.gen.ts.toAngularBasicTSComponent
import toAngularEntityDataService
import toAngularEntityListTSComponent
import toAngularEntityViewTSComponent
import toAngularFormTSComponent
import toAngularModuleService
import toAngularModuleTSComponent

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


    open fun <T : CompilationUnitI<*>> moduleComponentTypeScript(module: ModuleI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleTypeScriptComponent", nameBuilder) { item, c -> item.toAngularModuleTSComponent(module, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> moduleService(module: ModuleI<*>, modules: List<ModuleI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleService", nameBuilder) { item, c -> item.toAngularModuleService(module, modules, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityViewComponentTypeScript(basics: List<BasicI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityViewTypeScriptComponent", nameBuilder) { item, c -> item.toAngularEntityViewTSComponent(basics, c, LangDerivedKind.API)  }

    open fun <T : CompilationUnitI<*>> formComponentTypeScript(enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>, entities: List<EntityI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityFormTypeScriptComponent", nameBuilder) { item, c -> item.toAngularFormTSComponent(enums, basics, entities, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityListComponentTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityListTypeScriptComponent", nameBuilder) { item, c -> item.toAngularEntityListTSComponent(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityDataService(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityDataService", nameBuilder) { item, c -> item.toAngularEntityDataService(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> basicComponentTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("BasicTypeScriptComponent", nameBuilder) { item, c -> item.toAngularBasicTSComponent(c, LangDerivedKind.API) }

}

package ee.design.gen.ts

import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.go.g
import ee.lang.gen.ts.LangTsTemplates
import ee.lang.gen.ts.toAngularBasicTSComponent
import ee.lang.gen.ts.toAngularEnumTSComponent
import toAngularEntityDataService
import toAngularEntityListTypeScript
import toAngularEntityViewTypeScript
import toAngularEntityFormTypeScript
import toAngularModuleService
import toAngularModuleTypeScript

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


    open fun <T : ModuleI<*>> moduleTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleTypeScriptComponent", nameBuilder) { item, c -> item.toAngularModuleTypeScript(c, LangDerivedKind.API) }

    open fun <T : ModuleI<*>> moduleService(modules: List<ModuleI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleService", nameBuilder) { item, c -> item.toAngularModuleService(modules, c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityViewTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityViewTypeScriptComponent", nameBuilder) { item, c -> item.toAngularEntityViewTypeScript(c, LangDerivedKind.API)  }

    open fun <T : CompilationUnitI<*>> entityFormTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityFormTypeScriptComponent", nameBuilder) { item, c -> item.toAngularEntityFormTypeScript(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityListTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityListTypeScriptComponent", nameBuilder) { item, c -> item.toAngularEntityListTypeScript(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> entityDataService(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityDataService", nameBuilder) { item, c -> item.toAngularEntityDataService(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> basicTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("BasicTypeScriptComponent", nameBuilder) { item, c -> item.toAngularBasicTSComponent(c, LangDerivedKind.API) }

    open fun <T : CompilationUnitI<*>> enumTypeScript(parent: ItemI<*>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EnumTypeScriptComponent", nameBuilder) { item, c -> item.toAngularEnumTSComponent(parent, c, LangDerivedKind.API) }

}

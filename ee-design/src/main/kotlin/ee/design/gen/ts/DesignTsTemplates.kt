package ee.design.gen.ts

import ee.design.CompI
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.AngularDerivedType
import ee.lang.gen.ts.AngularFileFormat
import ee.lang.gen.ts.AngularFileFormatNames
import ee.lang.gen.ts.LangTsTemplates
import toAngularBasicTSComponent
import toAngularEntityAggregateViewTypeScript
import toAngularEntityDataService
import toAngularEntityListTypeScript
import toAngularEntityViewTypeScript
import toAngularEntityFormTypeScript
import toAngularEnumTSComponent
import toAngularModuleService
import toAngularModuleTypeScript
import toAngularTranslateJson

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
    open fun <T : CompI<*>> translateJson(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
            Template("AngularTranslateJson", nameBuilder) { item, c -> item.toAngularTranslateJson() }

    open fun <T : ModuleI<*>> moduleTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleTypeScriptComponent", nameBuilder) { item, c -> item.toAngularModuleTypeScript(c, Model = AngularDerivedType.Module, ViewComponent = AngularDerivedType.ViewComponent) }

    open fun <T : ModuleI<*>> moduleService(modules: List<ModuleI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ModuleService", nameBuilder) { item, c -> item.toAngularModuleService(modules, c, ViewService = AngularDerivedType.ViewService) }

    open fun <T : CompilationUnitI<*>> entityViewTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityViewTypeScriptComponent", nameBuilder) { item, c -> item.toAngularEntityViewTypeScript(c, Model = AngularDerivedType.Entity, ViewComponent = AngularDerivedType.ViewComponent)  }

    open fun <T : CompilationUnitI<*>> valueViewTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityViewTypeScriptComponent", nameBuilder) { item, c -> item.toAngularEntityViewTypeScript(c, Model = AngularDerivedType.Value, ViewComponent = AngularDerivedType.ViewComponent)  }

    open fun <T : CompilationUnitI<*>> entityFormTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityFormTypeScriptComponent", nameBuilder) { item, c -> item.toAngularEntityFormTypeScript(c, Model = AngularDerivedType.Entity, FormComponent = AngularDerivedType.FormComponent) }

    open fun <T : CompilationUnitI<*>> valueFormTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityFormTypeScriptComponent", nameBuilder) { item, c -> item.toAngularEntityFormTypeScript(c, Model = AngularDerivedType.Value, FormComponent = AngularDerivedType.FormComponent) }

    open fun <T : CompilationUnitI<*>> entityListTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityListTypeScriptComponent", nameBuilder) { item, c -> item.toAngularEntityListTypeScript(c, Model = AngularDerivedType.Entity, ListComponent = AngularDerivedType.ListComponent, false) }

    open fun <T : CompilationUnitI<*>> entityAggregateViewTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
            Template("EntityAggregateViewTypeScriptComponent", nameBuilder) { item, c -> item.toAngularEntityAggregateViewTypeScript(c, Model = AngularDerivedType.Entity, ListComponent = AngularDerivedType.AggregateViewComponent, true, AngularFileFormat.EntityAggregateView) }

    open fun <T : CompilationUnitI<*>> valueListTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityListTypeScriptComponent", nameBuilder) { item, c -> item.toAngularEntityListTypeScript(c, Model = AngularDerivedType.Value, ListComponent = AngularDerivedType.ListComponent) }

    open fun <T : CompilationUnitI<*>> entityDataService(entites: List<EntityI<*>>, nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EntityDataService", nameBuilder) { item, c -> item.toAngularEntityDataService(entites, c, DataService = AngularDerivedType.DataService) }

    open fun <T : CompilationUnitI<*>> basicTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("BasicTypeScriptComponent", nameBuilder) { item, c -> item.toAngularBasicTSComponent(c, BasicComponent = AngularDerivedType.BasicComponent) }

    open fun <T : CompilationUnitI<*>> enumTypeScript(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("EnumTypeScriptComponent", nameBuilder) { item, c -> item.toAngularEnumTSComponent(c, EnumComponent = AngularDerivedType.EnumComponent, DataService = AngularDerivedType.DataService) }

}

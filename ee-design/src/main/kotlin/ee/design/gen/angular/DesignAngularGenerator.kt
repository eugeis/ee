package ee.design.gen.angular

import ee.design.*
import ee.design.gen.ts.DesignTsContextFactory
import ee.design.gen.ts.DesignTsTemplates
import ee.design.gen.ts.DesignTypeScriptGenerator
import ee.lang.*
import ee.lang.gen.ts.*
import java.nio.file.Path

open class DesignAngularGenerator(val model: StructureUnitI<*>) {

    companion object {
        fun buildTsContextFactoryAngular() = DesignTsContextFactory(false)
    }

    fun generate(target: Path) {
        model.prepareForTsGeneration()

        val generatorApiBase = DesignTypeScriptGenerator(model).typeScriptApiBase("", model).generator
        val generatorAngular = angular("", model).generator
        val generatorAngularTranslate = angularTranslate("", model).generator

        /*val generatorContextsComponent = generatorFactory.angularTypeScriptComponent("", model)
        val generatorComponent = generatorContextsComponent.generator

        val generatorAngularModule = generatorFactory.angularModules("", model)
        val generatorModule = generatorAngularModule.generator
        val generatorAngularHtmlAndScss = generatorFactory.angularHtmlAndScssComponent("", model)
        val generatorHtmlAndScss = generatorAngularHtmlAndScss.generator*/

        generatorApiBase.delete(target, model)
        generatorAngular.delete(target, model)
        generatorAngularTranslate.delete(target, model)

        generatorApiBase.generate(target, model)
        generatorAngular.generate(target, model)
        generatorAngularTranslate.generate(target, model)

        /*generatorComponent.delete(target, model)
        generatorModule.delete(target, model)
        generatorHtmlAndScss.delete(target, model)*/

        /*generatorComponent.generate(target, model)
        generatorModule.generate(target, model)
        generatorHtmlAndScss.generate(target, model)*/
    }

    open fun angular(fileNamePrefix: String = "", model: StructureUnitI<*>): GeneratorContexts<StructureUnitI<*>> {
        val angularTemplates = DesignAngularTemplates(itemNameAsTsFileName)
        val tsTemplates = DesignTsTemplates(itemNameAsTsFileName)
        val tsContextFactory = buildTsContextFactoryAngular()

        val components: StructureUnitI<*>.() -> List<CompI<*>> = {
            if (this is CompI<*>) listOf(this) else findDownByType(CompI::class.java)
        }
        val modules: StructureUnitI<*>.() -> List<ModuleI<*>> = {
            if (this is ModuleI<*>) listOf(this) else findDownByType(ModuleI::class.java)
        }

        val commands: StructureUnitI<*>.() -> List<CommandI<*>> = { findDownByType(CommandI::class.java) }
        val commandEnums: StructureUnitI<*>.() -> List<EnumTypeI<*>> = {
            findDownByType(EnumTypeI::class.java).filter {
                it.parent() is ControllerI<*> && it.name().endsWith("CommandType")
            }
        }

        val events: StructureUnitI<*>.() -> List<EventI<*>> = { findDownByType(EventI::class.java) }
        val eventEnums: StructureUnitI<*>.() -> List<EnumTypeI<*>> = {
            findDownByType(EnumTypeI::class.java).filter {
                it.parent() is ControllerI<*> && it.name().endsWith("EventType")
            }
        }

        val enums: StructureUnitI<*>.() -> List<EnumTypeI<*>> = {
            findDownByType(EnumTypeI::class.java).filter {
                it.parent() is StructureUnitI<*> && it.derivedAsType().isEmpty()
            }.sortedBy { it.name() }
        }

        val values: StructureUnitI<*>.() -> List<ValuesI<*>> = {
            findDownByType(ValuesI::class.java).filter { it.derivedAsType().isEmpty() }
                .sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val basics: StructureUnitI<*>.() -> List<BasicI<*>> = {
            findDownByType(BasicI::class.java).filter { it.derivedAsType().isEmpty() }
                .sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val entities: StructureUnitI<*>.() -> List<EntityI<*>> = {
            findDownByType(EntityI::class.java).filter { it.derivedAsType().isEmpty() }
                .sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val aggregateEntities: StructureUnitI<*>.() -> List<EntityI<*>> = {
            findDownByType(EntityI::class.java).filter {
                it.belongsToAggregate().derivedAsType().isEmpty() && it.belongsToAggregate().isNotEMPTY() }
                .sortedBy { "${it.belongsToAggregate().javaClass.simpleName} ${name()}" }.map { it.belongsToAggregate() }
        }

        val moduleGenerators = mutableListOf<GeneratorI<StructureUnitI<*>>>()
        val generator = GeneratorGroup(
            "Angular",
            listOf(GeneratorGroupItems("AngularModules", items = modules, generators = moduleGenerators))
        )

        // View Component for Modules
        val moduleViewComponentContextBuilder = tsContextFactory.buildForImplOnly("/components/view")
        moduleGenerators.add(
            GeneratorItems("AngularModuleViewComponent",
            contextBuilder = moduleViewComponentContextBuilder, items = modules,

            templates = {
                listOf(
                    tsTemplates.moduleTypeScript(angularModuleViewComponent.ts),
                    angularTemplates.moduleHTML(angularModuleViewComponent.html),
                    angularTemplates.moduleSCSS(angularModuleViewComponent.scss),
                ) })
        )

        // Module & Routing Component for Modules
        val moduleModulesContextBuilder = tsContextFactory.buildForImplOnly()
        moduleGenerators.add(
            GeneratorItems("AngularModules",
            contextBuilder = moduleModulesContextBuilder, items = modules,

            templates = {
                listOf(
                    angularTemplates.angularModule(angularModule.ts, components.invoke(model)),
                    angularTemplates.angularRoutingModule(angularRoutingModule.ts),
                ) })
        )

        // Module Service
        val moduleServiceContextBuilder = tsContextFactory.buildForImplOnly("/service")
        moduleGenerators.add(
            GeneratorItems("AngularModulesService",
            contextBuilder = moduleServiceContextBuilder, items = modules,

            templates = {
                listOf(
                    tsTemplates.moduleService(modules.invoke(model), angularModuleService.ts)
                ) })
        )

        // View, List, Form, Service Components for Entities
        val entityComponentContextBuilder = tsContextFactory.buildForImplOnly()
        moduleGenerators.add(
            GeneratorItems("AngularEntityComponent",
            contextBuilder = entityComponentContextBuilder, items = entities,

            templates = {
                listOf(
                    tsTemplates.entityViewTypeScript(angularEntityViewComponent.ts),
                    angularTemplates.entityViewHTML(entities.invoke(model), angularEntityViewComponent.html),
                    angularTemplates.entityViewSCSS(angularEntityViewComponent.scss),

                    tsTemplates.entityFormTypeScript(angularEntityFormComponent.ts),
                    angularTemplates.entityFormHTML(angularEntityFormComponent.html),
                    angularTemplates.entityFormSCSS(angularEntityFormComponent.scss),

                    tsTemplates.entityListTypeScript(angularEntityListComponent.ts),
                    angularTemplates.entityListHTML(angularEntityListComponent.html, aggregateEntities.invoke(model), entities.invoke(model)),
                    angularTemplates.entityListSCSS(angularEntityListComponent.scss),

                    tsTemplates.entityDataService(entities.invoke(model), values.invoke(model), angularEntityService.ts)
                ) })
        )

        // View, List, Form Components for Aggregate Entity
        val aggregateEntityComponentContextBuilder = tsContextFactory.buildForImplOnly()
        moduleGenerators.add(
            GeneratorItems("AngularAggregateEntityComponent",
            contextBuilder = aggregateEntityComponentContextBuilder, items = aggregateEntities,

            templates = {
                listOf(
                    tsTemplates.entityAggregateViewTypeScript(angularAggregateEntityComponent.ts),
                    angularTemplates.entityAggregateViewHTML(angularAggregateEntityComponent.html),
                    angularTemplates.entityAggregateViewSCSS(angularAggregateEntityComponent.scss),
                ) })
        )

        // View, List, Form, Service Components for Values
        val valuesComponentContextBuilder = tsContextFactory.buildForImplOnly()
        moduleGenerators.add(
            GeneratorItems("AngularValuesComponent",
            contextBuilder = valuesComponentContextBuilder, items = values,

            templates = {
                listOf(
                    tsTemplates.valueViewTypeScript(angularEntityViewComponent.ts),
                    angularTemplates.entityViewHTML(entities.invoke(model), angularEntityViewComponent.html),
                    angularTemplates.entityViewSCSS(angularEntityViewComponent.scss),

                    tsTemplates.valueFormTypeScript(angularEntityFormComponent.ts),
                    angularTemplates.entityFormHTML(angularEntityFormComponent.html),
                    angularTemplates.entityFormSCSS(angularEntityFormComponent.scss),

                    tsTemplates.valueListTypeScript(angularEntityListComponent.ts),
                    angularTemplates.entityListHTML(angularEntityListComponent.html, aggregateEntities.invoke(model), entities.invoke(model)),
                    angularTemplates.entityListSCSS(angularEntityListComponent.scss),

                    tsTemplates.entityDataService(entities.invoke(model), values.invoke(model), angularEntityService.ts)
                ) })
        )

        // Basics Components
        val basicsContextBuilder = tsContextFactory.buildForImplOnly("/basics")
        moduleGenerators.add(
            GeneratorItems("AngularBasics",
            contextBuilder = basicsContextBuilder, items = basics,

            templates = {
                listOf(
                    tsTemplates.basicTypeScript(angularBasicComponent.ts),
                    angularTemplates.basicHTML(angularBasicComponent.html),
                    angularTemplates.basicSCSS(angularBasicComponent.scss),
                ) })
        )

        // Enums Components
        val enumsContextBuilder = tsContextFactory.buildForImplOnly("/enums")
        moduleGenerators.add(
            GeneratorItems("AngularEnums",
            contextBuilder = enumsContextBuilder, items = enums,

            templates = {
                listOf(
                    tsTemplates.enumTypeScript(angularEnumComponent.ts),
                    angularTemplates.enumHTML(angularEnumComponent.html),
                    angularTemplates.enumSCSS(angularEnumComponent.scss),
                ) })
        )

        return GeneratorContexts(generator, moduleViewComponentContextBuilder, moduleModulesContextBuilder, moduleServiceContextBuilder,
            entityComponentContextBuilder, aggregateEntityComponentContextBuilder, basicsContextBuilder, enumsContextBuilder,)
    }

    open fun angularTranslate(fileNamePrefix: String = "", model: StructureUnitI<*>): GeneratorContexts<StructureUnitI<*>> {
        val tsTemplates = DesignTsTemplates(itemNameAsJsonFileName)
        val tsContextFactory = buildTsContextFactoryAngular()

        val components: StructureUnitI<*>.() -> List<CompI<*>> = {
            if (this is CompI<*>) listOf(this) else findDownByType(CompI::class.java)
        }

        val moduleGenerators = mutableListOf<GeneratorI<StructureUnitI<*>>>()
        val generator = GeneratorGroup(
            "AngularTranslate",
            listOf(GeneratorGroupItems("AngularTranslate", items = components, generators = moduleGenerators))
        )

        val angularTranslateFileContextFactory = tsContextFactory.buildForImplOnly("/assets/i18n")
        moduleGenerators.add(
            GeneratorItems("AngularTranslate",
            contextBuilder = angularTranslateFileContextFactory, items = components,

            templates = {
                listOf(
                    tsTemplates.translateJson(english.json),
                    tsTemplates.translateJson(germany.json),
                    tsTemplates.translateJson(france.json),
                    tsTemplates.translateJson(spain.json),
                ) })
        )

        return GeneratorContexts(generator, angularTranslateFileContextFactory)
    }
}

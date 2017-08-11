package ee.design.gen

import ee.design.*
import ee.design.gen.go.*
import ee.design.gen.kt.DesignKotlinContextFactory
import ee.design.gen.kt.DesignKotlinTemplates
import ee.design.gen.swagger.DesignSwaggerContextFactory
import ee.design.gen.swagger.DesignSwaggerTemplates
import ee.lang.*
import ee.lang.gen.LangGeneratorFactory
import ee.lang.gen.go.itemAndTemplateNameAsGoFileName
import ee.lang.gen.go.itemNameAsGoFileName
import ee.lang.gen.itemNameAsKotlinFileName
import ee.lang.gen.swagger.itemNameAsSwaggerFileName

open class DesignGeneratorFactory : LangGeneratorFactory {
    constructor() : super()

    override fun buildKotlinContextFactory() = DesignKotlinContextFactory()
    override fun buildKotlinTemplates() = DesignKotlinTemplates(itemNameAsKotlinFileName)

    override fun buildGoContextFactory() = DesignGoContextFactory()
    override fun buildGoTemplates() = DesignGoTemplates(itemNameAsGoFileName)

    override fun buildSwaggerContextFactory() = DesignSwaggerContextFactory()
    fun buildSwaggerTemplates() = DesignSwaggerTemplates(itemNameAsSwaggerFileName)

    open fun eventDrivenGo(fileNamePrefix: String = ""): GeneratorI<StructureUnitI> {
        val goTemplates = buildGoTemplates()

        val swaggerTemplates = buildSwaggerTemplates()
        val swaggerContextFactory = buildSwaggerContextFactory()
        val swaggerContextBuilder = swaggerContextFactory.build()

        val contextFactory = buildGoContextFactory()
        val contextBuilder = contextFactory.buildForImplOnly()

        val components: StructureUnitI.() -> List<CompI> = { if (this is CompI) listOf(this) else findDownByType(CompI::class.java) }
        val modules: StructureUnitI.() -> List<ModuleI> = { if (this is ModuleI) listOf(this) else findDownByType(ModuleI::class.java) }

        val commands: StructureUnitI.() -> List<CommandI> = { findDownByType(CommandI::class.java) }
        val commandEnums: StructureUnitI.() -> List<EnumTypeI> = {
            findDownByType(EnumTypeI::class.java).filter { it.parent() is Commands }
        }

        val events: StructureUnitI.() -> List<EventI> = { findDownByType(EventI::class.java) }
        val eventEnums: StructureUnitI.() -> List<EnumTypeI> = {
            findDownByType(EnumTypeI::class.java).filter { it.parent() is Events }
        }

        val ehEnums: StructureUnitI.() -> List<EnumTypeI> = {
            findDownByType(EnumTypeI::class.java).filter {
                it.derivedAsType().equals(DesignDerivedType.Aggregate, true)
            }.sortedBy { it.name() }
        }

        val ehValues: StructureUnitI.() -> List<ValuesI> = {
            findDownByType(ValuesI::class.java).filter { it.derivedAsType().equals(DesignDerivedType.Aggregate, true) }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val ehBasics: StructureUnitI.() -> List<BasicI> = {
            findDownByType(BasicI::class.java).filter { it.derivedAsType().equals(DesignDerivedType.Aggregate, true) }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val ehEntities: StructureUnitI.() -> List<EntityI> = {
            findDownByType(EntityI::class.java).filter { it.derivedAsType().equals(DesignDerivedType.Aggregate, true) }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val ehController: StructureUnitI.() -> List<ControllerI> = {
            findDownByType(ControllerI::class.java).filter { it.derivedAsType().equals(DesignDerivedType.Aggregate, true) }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }


        val httpController: StructureUnitI.() -> List<ControllerI> = {
            findDownByType(ControllerI::class.java).filter { it.derivedAsType().equals(DesignDerivedType.Http, true) }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }


        val enums: StructureUnitI.() -> List<EnumTypeI> = {
            findDownByType(EnumTypeI::class.java).filter {
                it.parent() is StructureUnitI && it.derivedAsType().isEmpty()
            }.sortedBy { it.name() }
        }

        val values: StructureUnitI.() -> List<ValuesI> = {
            findDownByType(ValuesI::class.java).filter { it.derivedAsType().isEmpty() }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val basics: StructureUnitI.() -> List<BasicI> = {
            findDownByType(BasicI::class.java).filter { it.derivedAsType().isEmpty() }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val entities: StructureUnitI.() -> List<EntityI> = {
            findDownByType(EntityI::class.java).filter { it.derivedAsType().isEmpty() }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        contextFactory.macroController.registerMacro(OperationI::toGoAggregateInitializerRegisterCommands.name,
                OperationI::toGoAggregateInitializerRegisterCommands)
        contextFactory.macroController.registerMacro(CompilationUnitI::toGoAggregateInitializerConst.name,
                CompilationUnitI::toGoAggregateInitializerConst)
        contextFactory.macroController.registerMacro(CompilationUnitI::toGoAggregateInitializerRegisterForEvents.name,
                CompilationUnitI::toGoAggregateInitializerRegisterForEvents)
        contextFactory.macroController.registerMacro(ConstructorI::toGoAggregateInitializerBody.name,
                ConstructorI::toGoAggregateInitializerBody)
        contextFactory.macroController.registerMacro(ConstructorI::toGoEventhorizonInitializerBody.name,
                ConstructorI::toGoEventhorizonInitializerBody)
        contextFactory.macroController.registerMacro(OperationI::toGoEventhorizonInitializerSetupBody.name,
                OperationI::toGoEventhorizonInitializerSetupBody)
        contextFactory.macroController.registerMacro(AttributeI::toGoPropOptionalAfterBody.name,
                AttributeI::toGoPropOptionalAfterBody)
        contextFactory.macroController.registerMacro(OperationI::toGoCommandHandlerExecuteCommandBody.name,
                OperationI::toGoCommandHandlerExecuteCommandBody)
        contextFactory.macroController.registerMacro(OperationI::toGoCommandHandlerSetupBody.name,
                OperationI::toGoCommandHandlerSetupBody)
        contextFactory.macroController.registerMacro(OperationI::toGoEventHandlerApplyEvent.name,
                OperationI::toGoEventHandlerApplyEvent)
        contextFactory.macroController.registerMacro(OperationI::toGoEventHandlerSetupBody.name,
                OperationI::toGoEventHandlerSetupBody)
        contextFactory.macroController.registerMacro(OperationI::toGoHttpHandlerBody.name,
                OperationI::toGoHttpHandlerBody)
        contextFactory.macroController.registerMacro(OperationI::toGoSetupHttpRouterBody.name,
                OperationI::toGoSetupHttpRouterBody)
        contextFactory.macroController.registerMacro(ConstructorI::toGoHttpRouterBody.name,
                ConstructorI::toGoHttpRouterBody)
        contextFactory.macroController.registerMacro(ConstructorI::toGoHttpModuleRouterBody.name,
                ConstructorI::toGoHttpModuleRouterBody)
        contextFactory.macroController.registerMacro(OperationI::toGoSetupModuleHttpRouter.name,
                OperationI::toGoSetupModuleHttpRouter)

        return GeneratorGroup<StructureUnitI>(listOf(
                GeneratorGroupItems<StructureUnitI, StructureUnitI>(items = modules, generators = listOf(
                        GeneratorSimple<StructureUnitI>(
                                contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                                name = "${fileNamePrefix}ApiBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                                fragments = {
                                    listOf(
                                            ItemsFragment<StructureUnitI, CompilationUnitI>(items = entities,
                                                    fragments = { listOf(goTemplates.pojo()) }),
                                            ItemsFragment<StructureUnitI, CompilationUnitI>(items = values,
                                                    fragments = { listOf(goTemplates.pojo()) }),
                                            ItemsFragment<StructureUnitI, CompilationUnitI>(items = basics,
                                                    fragments = { listOf(goTemplates.pojo()) }),
                                            ItemsFragment<StructureUnitI, EnumTypeI>(items = enums,
                                                    fragments = { listOf(goTemplates.enum()) })
                                    )
                                })
                        ),
                        GeneratorSimple<StructureUnitI>(
                                contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                                name = "${fileNamePrefix}CommandsBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                                fragments = {
                                    listOf(
                                            ItemsFragment<StructureUnitI, EntityI>(items = entities,
                                                    fragments = { listOf(goTemplates.commandTypes()) }),
                                            ItemsFragment<StructureUnitI, CommandI>(items = commands,
                                                    fragments = { listOf(goTemplates.command()) }),
                                            ItemsFragment<StructureUnitI, EnumTypeI>(items = commandEnums,
                                                    fragments = { listOf(goTemplates.enum()) }))
                                })
                        ),
                        GeneratorSimple<StructureUnitI>(
                                contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                                name = "${fileNamePrefix}EventsBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                                fragments = {
                                    listOf(
                                            ItemsFragment<StructureUnitI, EntityI>(items = entities,
                                                    fragments = { listOf(goTemplates.eventTypes()) }),
                                            ItemsFragment<StructureUnitI, EventI>(items = events,
                                                    fragments = { listOf(goTemplates.pojo()) }),
                                            ItemsFragment<StructureUnitI, EnumTypeI>(items = eventEnums,
                                                    fragments = { listOf(goTemplates.enum()) })
                                    )
                                })
                        ),
                        GeneratorSimple<StructureUnitI>(
                                contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                                name = "${fileNamePrefix}EventhorizonBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                                fragments = {
                                    listOf(
                                            ItemsFragment<StructureUnitI, ControllerI>(items = ehController,
                                                    fragments = { listOf(goTemplates.pojo()) }),
                                            ItemsFragment<StructureUnitI, CompilationUnitI>(items = ehValues,
                                                    fragments = { listOf(goTemplates.pojo()) }),
                                            ItemsFragment<StructureUnitI, CompilationUnitI>(items = ehBasics,
                                                    fragments = { listOf(goTemplates.pojo()) }),
                                            ItemsFragment<StructureUnitI, EnumTypeI>(items = ehEnums,
                                                    fragments = { listOf(goTemplates.enum()) }))
                                })
                        ),
                        GeneratorSimple<StructureUnitI>(
                                contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                                name = "${fileNamePrefix}HttpBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                                fragments = {
                                    listOf(
                                            ItemsFragment<StructureUnitI, ControllerI>(items = httpController,
                                                    fragments = { listOf(goTemplates.pojo()) }))
                                })
                        ),
                        Generator<StructureUnitI, CompI>(
                                contextBuilder = swaggerContextBuilder, items = components,
                                templates = { listOf(swaggerTemplates.model()) }
                        )
                )),
                Generator<StructureUnitI, CompI>(
                        contextBuilder = swaggerContextBuilder, items = components,
                        templates = { listOf(swaggerTemplates.model()) }
                )
        ))
    }
}
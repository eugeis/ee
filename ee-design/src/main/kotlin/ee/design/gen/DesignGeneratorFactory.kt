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
            findDownByType(EnumTypeI::class.java).filter {
                it.parent() is ControllerI &&
                        it.name().endsWith("CommandType")
            }
        }

        val events: StructureUnitI.() -> List<EventI> = { findDownByType(EventI::class.java) }
        val eventEnums: StructureUnitI.() -> List<EnumTypeI> = {
            findDownByType(EnumTypeI::class.java).filter {
                it.parent() is ControllerI &&
                        it.name().endsWith("EventType")
            }
        }

        val derivedTypes = arrayListOf<String>(DesignDerivedType.Aggregate, DesignDerivedType.Query, DesignDerivedType.Http)

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

        registerMacros(contextFactory)

        val moduleGenerators = arrayListOf<GeneratorI<StructureUnitI>>()
        val ret = GeneratorGroup<StructureUnitI>(listOf(
                GeneratorGroupItems<StructureUnitI, StructureUnitI>(items = modules, generators = moduleGenerators),
                Generator<StructureUnitI, CompI>(
                        contextBuilder = swaggerContextBuilder, items = components,
                        templates = { listOf(swaggerTemplates.model()) }
                )
        ))

        moduleGenerators.addAll(listOf(
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
                )
        ))


        moduleGenerators.addAll(derivedTypes.map { derivedType ->
            GeneratorSimple<StructureUnitI>(
                    contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                    name = "${fileNamePrefix}${derivedType}Base", nameBuilder = itemAndTemplateNameAsGoFileName,
                    fragments = {
                        listOf(
                                ItemsFragment<StructureUnitI, ControllerI>(items = {
                                    findDownByType(ControllerI::class.java).filter {
                                        it.derivedAsType().equals(derivedType, true)
                                    }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                                }, fragments = { listOf(goTemplates.pojo()) }),
                                ItemsFragment<StructureUnitI, CompilationUnitI>(items = {
                                    findDownByType(ValuesI::class.java).filter {
                                        it.derivedAsType().equals(derivedType, true)
                                    }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                                }, fragments = { listOf(goTemplates.pojo()) }),
                                ItemsFragment<StructureUnitI, CompilationUnitI>(items = {
                                    findDownByType(BasicI::class.java).filter {
                                        it.derivedAsType().equals(derivedType, true)
                                    }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                                }, fragments = { listOf(goTemplates.pojo()) }),
                                ItemsFragment<StructureUnitI, EnumTypeI>(items = {
                                    findDownByType(EnumTypeI::class.java).filter {
                                        it.derivedAsType().equals(derivedType, true)
                                    }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                                }, fragments = { listOf(goTemplates.enum()) }))
                    })
            )
        })

        return ret
    }

    protected fun registerMacros(contextFactory: DesignGoContextFactory) {
        val macros = contextFactory.macroController
        macros.registerMacro(OperationI::toGoAggregateInitializerRegisterCommands.name,
                OperationI::toGoAggregateInitializerRegisterCommands)
        macros.registerMacro(CompilationUnitI::toGoAggregateInitializerConst.name,
                CompilationUnitI::toGoAggregateInitializerConst)
        macros.registerMacro(CompilationUnitI::toGoAggregateInitializerRegisterForEvents.name,
                CompilationUnitI::toGoAggregateInitializerRegisterForEvents)
        macros.registerMacro(ConstructorI::toGoAggregateInitializerBody.name,
                ConstructorI::toGoAggregateInitializerBody)
        macros.registerMacro(ConstructorI::toGoEventhorizonInitializerBody.name,
                ConstructorI::toGoEventhorizonInitializerBody)
        macros.registerMacro(OperationI::toGoEventhorizonInitializerSetupBody.name,
                OperationI::toGoEventhorizonInitializerSetupBody)
        macros.registerMacro(AttributeI::toGoPropOptionalAfterBody.name,
                AttributeI::toGoPropOptionalAfterBody)
        macros.registerMacro(OperationI::toGoCommandHandlerExecuteCommandBody.name,
                OperationI::toGoCommandHandlerExecuteCommandBody)
        macros.registerMacro(OperationI::toGoCommandHandlerSetupBody.name,
                OperationI::toGoCommandHandlerSetupBody)
        macros.registerMacro(OperationI::toGoEventHandlerApplyEvent.name,
                OperationI::toGoEventHandlerApplyEvent)
        macros.registerMacro(OperationI::toGoEventHandlerSetupBody.name,
                OperationI::toGoEventHandlerSetupBody)
        macros.registerMacro(OperationI::toGoHttpHandlerBody.name,
                OperationI::toGoHttpHandlerBody)
        macros.registerMacro(OperationI::toGoHttpHandlerIdBasedBody.name,
                OperationI::toGoHttpHandlerIdBasedBody)
        macros.registerMacro(OperationI::toGoHttpHandlerCommandBody.name,
                OperationI::toGoHttpHandlerCommandBody)
        macros.registerMacro(OperationI::toGoSetupHttpRouterBody.name,
                OperationI::toGoSetupHttpRouterBody)
        macros.registerMacro(ConstructorI::toGoHttpRouterBeforeBody.name,
                ConstructorI::toGoHttpRouterBeforeBody)
        macros.registerMacro(ConstructorI::toGoHttpModuleRouterBeforeBody.name,
                ConstructorI::toGoHttpModuleRouterBeforeBody)
        macros.registerMacro(OperationI::toGoSetupModuleHttpRouter.name,
                OperationI::toGoSetupModuleHttpRouter)
        macros.registerMacro(OperationI::toGoFindByBody.name,
                OperationI::toGoFindByBody)
        macros.registerMacro(OperationI::toGoCountByBody.name,
                OperationI::toGoCountByBody)
        macros.registerMacro(OperationI::toGoExistByBody.name,
                OperationI::toGoExistByBody)
    }
}
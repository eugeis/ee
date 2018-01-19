package ee.design.gen

import ee.design.*
import ee.design.gen.go.*
import ee.design.gen.kt.DesignKotlinContextFactory
import ee.design.gen.kt.DesignKotlinTemplates
import ee.design.gen.swagger.DesignSwaggerContextFactory
import ee.design.gen.swagger.DesignSwaggerTemplates
import ee.design.gen.ts.DesignTsContextFactory
import ee.design.gen.ts.DesignTsTemplates
import ee.lang.*
import ee.lang.gen.LangGeneratorFactory
import ee.lang.gen.common.LangCommonContextFactory
import ee.lang.gen.go.itemAndTemplateNameAsGoFileName
import ee.lang.gen.go.itemNameAsGoFileName
import ee.lang.gen.itemNameAsKotlinFileName
import ee.lang.gen.swagger.itemNameAsSwaggerFileName
import ee.lang.gen.ts.itemAndTemplateNameAsTsFileName
import ee.lang.gen.ts.itemNameAsTsFileName

open class DesignGeneratorFactory : LangGeneratorFactory {
    constructor() : super()

    override fun buildKotlinContextFactory() = DesignKotlinContextFactory()
    override fun buildKotlinTemplates() = DesignKotlinTemplates(itemNameAsKotlinFileName)

    override fun buildGoContextFactory() = DesignGoContextFactory()
    override fun buildGoTemplates() = DesignGoTemplates(itemNameAsGoFileName)

    override fun buildTsContextFactory() = DesignTsContextFactory()
    override fun buildTsTemplates() = DesignTsTemplates(itemNameAsTsFileName)

    override fun buildSwaggerContextFactory() = DesignSwaggerContextFactory()
    fun buildSwaggerTemplates() = DesignSwaggerTemplates(itemNameAsSwaggerFileName)

    open fun eventDrivenGo(fileNamePrefix: String = ""): GeneratorI<StructureUnitI<*>> {

        val swaggerTemplates = buildSwaggerTemplates()
        val swaggerContextFactory = buildSwaggerContextFactory()
        val swaggerContextBuilder = swaggerContextFactory.build()

        val goTemplates = buildGoTemplates()
        val goContextFactory = buildGoContextFactory()
        val goContextBuilder = goContextFactory.buildForImplOnly()

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

        registerGoMacros(goContextFactory)

        val moduleGenerators = arrayListOf<GeneratorI<StructureUnitI<*>>>()
        val ret = GeneratorGroup(listOf(GeneratorGroupItems(items = modules, generators = moduleGenerators),
            Generator(contextBuilder = swaggerContextBuilder, items = components,
                templates = { listOf(swaggerTemplates.model()) })))

        moduleGenerators.addAll(listOf(GeneratorSimple(contextBuilder = goContextBuilder,
            template = FragmentsTemplate<StructureUnitI<*>>(name = "${fileNamePrefix}ApiBase",
                nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                    listOf(ItemsFragment(items = entities, fragments = { listOf(goTemplates.entity()) }),
                        ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = values,
                            fragments = { listOf(goTemplates.pojo()) }),
                        ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = basics,
                            fragments = { listOf(goTemplates.pojo()) }),
                        ItemsFragment(items = enums, fragments = { listOf(goTemplates.enum()) }))
                })), GeneratorSimple(contextBuilder = goContextBuilder,
            template = FragmentsTemplate<StructureUnitI<*>>(name = "${fileNamePrefix}CommandsBase",
                nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                    listOf(ItemsFragment(items = entities, fragments = { listOf(goTemplates.commandTypes()) }),
                        ItemsFragment(items = commands, fragments = { listOf(goTemplates.command()) }),
                        ItemsFragment(items = commandEnums, fragments = { listOf(goTemplates.enum()) }))
                })), GeneratorSimple(contextBuilder = goContextBuilder,
            template = FragmentsTemplate<StructureUnitI<*>>(name = "${fileNamePrefix}EventsBase",
                nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                    listOf(ItemsFragment(items = entities, fragments = { listOf(goTemplates.eventTypes()) }),
                        ItemsFragment(items = events, fragments = { listOf(goTemplates.pojoExcludePropsWithValue()) }),
                        ItemsFragment(items = eventEnums, fragments = { listOf(goTemplates.enum()) }))
                }))))

        val derivedTypes = arrayListOf(DesignDerivedType.Aggregate, DesignDerivedType.Query, DesignDerivedType.Http)
        moduleGenerators.addAll(derivedTypes.map { derivedType ->
            GeneratorSimple(contextBuilder = goContextBuilder,
                template = FragmentsTemplate<StructureUnitI<*>>(name = "$fileNamePrefix${derivedType}Base",
                    nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                        listOf(ItemsFragment<StructureUnitI<*>, ControllerI<*>>(items = {
                            findDownByType(ControllerI::class.java).filter {
                                it.derivedAsType().equals(derivedType, true)
                            }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                        }, fragments = { listOf(goTemplates.pojo()) }), ItemsFragment(items = {
                            findDownByType(ValuesI::class.java).filter {
                                it.derivedAsType().equals(derivedType, true)
                            }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                        }, fragments = { listOf(goTemplates.pojo()) }), ItemsFragment(items = {
                            findDownByType(BasicI::class.java).filter {
                                it.derivedAsType().equals(derivedType, true)
                            }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                        }, fragments = { listOf(goTemplates.pojo()) }),
                            ItemsFragment<StructureUnitI<*>, EnumTypeI<*>>(items = {
                                findDownByType(EnumTypeI::class.java).filter {
                                    it.derivedAsType().equals(derivedType, true)
                                }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                            }, fragments = { listOf(goTemplates.enum()) }))
                    }))
        })

        return ret
    }

    open fun angular(fileNamePrefix: String = ""): GeneratorI<StructureUnitI<*>> {
        val tsTemplates = buildTsTemplates()
        val tsContextFactory = buildTsContextFactory()
        val tsContextBuilder = tsContextFactory.buildForImplOnly()

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

        val moduleGenerators = arrayListOf<GeneratorI<StructureUnitI<*>>>()
        val ret = GeneratorGroup(listOf(GeneratorGroupItems(items = modules, generators = moduleGenerators)))

        moduleGenerators.addAll(listOf(GeneratorSimple(contextBuilder = tsContextBuilder,
            template = FragmentsTemplate<StructureUnitI<*>>(name = "${fileNamePrefix}ApiBase",
                nameBuilder = itemAndTemplateNameAsTsFileName, fragments = {
                    listOf(ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = entities,
                        fragments = { listOf(tsTemplates.pojo()) }),
                        ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = values,
                            fragments = { listOf(tsTemplates.pojo()) }),
                        ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = basics,
                            fragments = { listOf(tsTemplates.pojo()) }),
                        ItemsFragment(items = enums, fragments = { listOf(tsTemplates.enum()) }))
                }))))
        return ret
    }

    protected fun registerGoMacros(contextFactory: LangCommonContextFactory) {
        val macros = contextFactory.macroController
        macros.registerMacro(OperationI<*>::toGoAggregateInitializerRegisterCommands.name,
            OperationI<*>::toGoAggregateInitializerRegisterCommands)
        macros.registerMacro(CompilationUnitI<*>::toGoAggregateInitializerConst.name,
            CompilationUnitI<*>::toGoAggregateInitializerConst)
        macros.registerMacro(CompilationUnitI<*>::toGoAggregateInitializerRegisterForEvents.name,
            CompilationUnitI<*>::toGoAggregateInitializerRegisterForEvents)
        macros.registerMacro(ConstructorI<*>::toGoAggregateInitializerBody.name,
            ConstructorI<*>::toGoAggregateInitializerBody)
        macros.registerMacro(ConstructorI<*>::toGoEventhorizonInitializerBody.name,
            ConstructorI<*>::toGoEventhorizonInitializerBody)
        macros.registerMacro(OperationI<*>::toGoEventhorizonInitializerSetupBody.name,
            OperationI<*>::toGoEventhorizonInitializerSetupBody)
        macros.registerMacro(AttributeI<*>::toGoPropOptionalAfterBody.name, AttributeI<*>::toGoPropOptionalAfterBody)
        macros.registerMacro(OperationI<*>::toGoCommandHandlerExecuteCommandBody.name,
            OperationI<*>::toGoCommandHandlerExecuteCommandBody)
        macros.registerMacro(OperationI<*>::toGoCommandHandlerSetupBody.name,
            OperationI<*>::toGoCommandHandlerSetupBody)
        macros.registerMacro(OperationI<*>::toGoEventHandlerApplyEvent.name, OperationI<*>::toGoEventHandlerApplyEvent)
        macros.registerMacro(OperationI<*>::toGoEventHandlerSetupBody.name, OperationI<*>::toGoEventHandlerSetupBody)
        macros.registerMacro(OperationI<*>::toGoHttpHandlerBody.name, OperationI<*>::toGoHttpHandlerBody)
        macros.registerMacro(OperationI<*>::toGoHttpHandlerIdBasedBody.name, OperationI<*>::toGoHttpHandlerIdBasedBody)
        macros.registerMacro(OperationI<*>::toGoHttpHandlerCommandBody.name, OperationI<*>::toGoHttpHandlerCommandBody)
        macros.registerMacro(OperationI<*>::toGoSetupHttpRouterBody.name, OperationI<*>::toGoSetupHttpRouterBody)
        macros.registerMacro(ConstructorI<*>::toGoHttpRouterBeforeBody.name, ConstructorI<*>::toGoHttpRouterBeforeBody)
        macros.registerMacro(ConstructorI<*>::toGoHttpModuleRouterBeforeBody.name,
            ConstructorI<*>::toGoHttpModuleRouterBeforeBody)
        macros.registerMacro(OperationI<*>::toGoSetupModuleHttpRouter.name, OperationI<*>::toGoSetupModuleHttpRouter)
        macros.registerMacro(OperationI<*>::toGoFindByBody.name, OperationI<*>::toGoFindByBody)
        macros.registerMacro(OperationI<*>::toGoCountByBody.name, OperationI<*>::toGoCountByBody)
        macros.registerMacro(OperationI<*>::toGoExistByBody.name, OperationI<*>::toGoExistByBody)
        macros.registerMacro(OperationI<*>::toGoCommandHandlerAddPreparerBody.name,
            OperationI<*>::toGoCommandHandlerAddPreparerBody)

    }
}
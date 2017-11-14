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

    open fun eventDrivenGo(fileNamePrefix: String = ""): GeneratorI<StructureUnitIB<*>> {

        val swaggerTemplates = buildSwaggerTemplates()
        val swaggerContextFactory = buildSwaggerContextFactory()
        val swaggerContextBuilder = swaggerContextFactory.build()

        val goTemplates = buildGoTemplates()
        val goContextFactory = buildGoContextFactory()
        val goContextBuilder = goContextFactory.buildForImplOnly()

        val components: StructureUnitIB<*>.() -> List<CompIB<*>> = {
            if (this is CompIB<*>) listOf(this) else findDownByType(CompIB::class.java)
        }
        val modules: StructureUnitIB<*>.() -> List<ModuleIB<*>> = {
            if (this is ModuleIB<*>) listOf(this) else findDownByType(ModuleIB::class.java)
        }

        val commands: StructureUnitIB<*>.() -> List<CommandIB<*>> = { findDownByType(CommandIB::class.java) }
        val commandEnums: StructureUnitIB<*>.() -> List<EnumTypeIB<*>> = {
            findDownByType(EnumTypeIB::class.java).filter {
                it.parent() is ControllerIB<*> &&
                        it.name().endsWith("CommandType")
            }
        }

        val events: StructureUnitIB<*>.() -> List<EventIB<*>> = { findDownByType(EventIB::class.java) }
        val eventEnums: StructureUnitIB<*>.() -> List<EnumTypeIB<*>> = {
            findDownByType(EnumTypeIB::class.java).filter {
                it.parent() is ControllerIB<*> &&
                        it.name().endsWith("EventType")
            }
        }

        val derivedTypes = arrayListOf<String>(DesignDerivedType.Aggregate, DesignDerivedType.Query, DesignDerivedType.Http)

        val enums: StructureUnitIB<*>.() -> List<EnumTypeIB<*>> = {
            findDownByType(EnumTypeIB::class.java).filter {
                it.parent() is StructureUnitIB<*> && it.derivedAsType().isEmpty()
            }.sortedBy { it.name() }
        }

        val values: StructureUnitIB<*>.() -> List<ValuesIB<*>> = {
            findDownByType(ValuesIB::class.java).filter { it.derivedAsType().isEmpty() }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val basics: StructureUnitIB<*>.() -> List<BasicIB<*>> = {
            findDownByType(BasicIB::class.java).filter { it.derivedAsType().isEmpty() }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val entities: StructureUnitIB<*>.() -> List<EntityIB<*>> = {
            findDownByType(EntityIB::class.java).filter { it.derivedAsType().isEmpty() }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        registerGoMacros(goContextFactory)

        val moduleGenerators = arrayListOf<GeneratorI<StructureUnitIB<*>>>()
        val ret = GeneratorGroup<StructureUnitIB<*>>(listOf(
                GeneratorGroupItems<StructureUnitIB<*>, StructureUnitIB<*>>(items = modules, generators = moduleGenerators),
                Generator<StructureUnitIB<*>, CompIB<*>>(
                        contextBuilder = swaggerContextBuilder, items = components,
                        templates = { listOf(swaggerTemplates.model()) }
                )
        ))

        moduleGenerators.addAll(listOf(
                GeneratorSimple<StructureUnitIB<*>>(
                        contextBuilder = goContextBuilder, template = FragmentsTemplate<StructureUnitIB<*>>(
                        name = "${fileNamePrefix}ApiBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                        fragments = {
                            listOf(
                                    ItemsFragment<StructureUnitIB<*>, EntityIB<*>>(items = entities,
                                            fragments = { listOf(goTemplates.entity()) }),
                                    ItemsFragment<StructureUnitIB<*>, CompilationUnitIB<*>>(items = values,
                                            fragments = { listOf(goTemplates.pojo()) }),
                                    ItemsFragment<StructureUnitIB<*>, CompilationUnitIB<*>>(items = basics,
                                            fragments = { listOf(goTemplates.pojo()) }),
                                    ItemsFragment<StructureUnitIB<*>, EnumTypeIB<*>>(items = enums,
                                            fragments = { listOf(goTemplates.enum()) })
                            )
                        })
                ),
                GeneratorSimple<StructureUnitIB<*>>(
                        contextBuilder = goContextBuilder, template = FragmentsTemplate<StructureUnitIB<*>>(
                        name = "${fileNamePrefix}CommandsBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                        fragments = {
                            listOf(
                                    ItemsFragment<StructureUnitIB<*>, EntityIB<*>>(items = entities,
                                            fragments = { listOf(goTemplates.commandTypes()) }),
                                    ItemsFragment<StructureUnitIB<*>, CommandIB<*>>(items = commands,
                                            fragments = { listOf(goTemplates.command()) }),
                                    ItemsFragment<StructureUnitIB<*>, EnumTypeIB<*>>(items = commandEnums,
                                            fragments = { listOf(goTemplates.enum()) }))
                        })
                ),
                GeneratorSimple<StructureUnitIB<*>>(
                        contextBuilder = goContextBuilder, template = FragmentsTemplate<StructureUnitIB<*>>(
                        name = "${fileNamePrefix}EventsBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                        fragments = {
                            listOf(
                                    ItemsFragment<StructureUnitIB<*>, EntityIB<*>>(items = entities,
                                            fragments = { listOf(goTemplates.eventTypes()) }),
                                    ItemsFragment<StructureUnitIB<*>, EventIB<*>>(items = events,
                                            fragments = { listOf(goTemplates.pojoExcludePropsWithValue()) }),
                                    ItemsFragment<StructureUnitIB<*>, EnumTypeIB<*>>(items = eventEnums,
                                            fragments = { listOf(goTemplates.enum()) })
                            )
                        })
                )
        ))


        moduleGenerators.addAll(derivedTypes.map { derivedType ->
            GeneratorSimple<StructureUnitIB<*>>(
                    contextBuilder = goContextBuilder, template = FragmentsTemplate<StructureUnitIB<*>>(
                    name = "$fileNamePrefix${derivedType}Base", nameBuilder = itemAndTemplateNameAsGoFileName,
                    fragments = {
                        listOf(
                                ItemsFragment<StructureUnitIB<*>, ControllerIB<*>>(items = {
                                    findDownByType(ControllerIB::class.java).filter {
                                        it.derivedAsType().equals(derivedType, true)
                                    }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                                }, fragments = { listOf(goTemplates.pojo()) }),
                                ItemsFragment<StructureUnitIB<*>, CompilationUnitIB<*>>(items = {
                                    findDownByType(ValuesIB::class.java).filter {
                                        it.derivedAsType().equals(derivedType, true)
                                    }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                                }, fragments = { listOf(goTemplates.pojo()) }),
                                ItemsFragment<StructureUnitIB<*>, CompilationUnitIB<*>>(items = {
                                    findDownByType(BasicIB::class.java).filter {
                                        it.derivedAsType().equals(derivedType, true)
                                    }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                                }, fragments = { listOf(goTemplates.pojo()) }),
                                ItemsFragment<StructureUnitIB<*>, EnumTypeIB<*>>(items = {
                                    findDownByType(EnumTypeIB::class.java).filter {
                                        it.derivedAsType().equals(derivedType, true)
                                    }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                                }, fragments = { listOf(goTemplates.enum()) }))
                    })
            )
        })
        return ret
    }

    open fun angular(fileNamePrefix: String = ""): GeneratorI<StructureUnitIB<*>> {
        val tsTemplates = buildTsTemplates()
        val tsContextFactory = buildTsContextFactory()
        val tsContextBuilder = tsContextFactory.buildForImplOnly()

        val components: StructureUnitIB<*>.() -> List<CompIB<*>> = {
            if (this is CompIB<*>) listOf(this) else findDownByType(CompIB::class.java)
        }
        val modules: StructureUnitIB<*>.() -> List<ModuleIB<*>> = {
            if (this is ModuleIB<*>) listOf(this) else findDownByType(ModuleIB::class.java)
        }

        val commands: StructureUnitIB<*>.() -> List<CommandIB<*>> = { findDownByType(CommandIB::class.java) }
        val commandEnums: StructureUnitIB<*>.() -> List<EnumTypeIB<*>> = {
            findDownByType(EnumTypeIB::class.java).filter {
                it.parent() is ControllerIB<*> &&
                        it.name().endsWith("CommandType")
            }
        }

        val events: StructureUnitIB<*>.() -> List<EventIB<*>> = { findDownByType(EventIB::class.java) }
        val eventEnums: StructureUnitIB<*>.() -> List<EnumTypeIB<*>> = {
            findDownByType(EnumTypeIB::class.java).filter {
                it.parent() is ControllerIB<*> &&
                        it.name().endsWith("EventType")
            }
        }

        val enums: StructureUnitIB<*>.() -> List<EnumTypeIB<*>> = {
            findDownByType(EnumTypeIB::class.java).filter {
                it.parent() is StructureUnitIB<*> && it.derivedAsType().isEmpty()
            }.sortedBy { it.name() }
        }

        val values: StructureUnitIB<*>.() -> List<ValuesIB<*>> = {
            findDownByType(ValuesIB::class.java).filter { it.derivedAsType().isEmpty() }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val basics: StructureUnitIB<*>.() -> List<BasicIB<*>> = {
            findDownByType(BasicIB::class.java).filter { it.derivedAsType().isEmpty() }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val entities: StructureUnitIB<*>.() -> List<EntityIB<*>> = {
            findDownByType(EntityIB::class.java).filter { it.derivedAsType().isEmpty() }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val moduleGenerators = arrayListOf<GeneratorI<StructureUnitIB<*>>>()
        val ret = GeneratorGroup<StructureUnitIB<*>>(listOf(
                GeneratorGroupItems<StructureUnitIB<*>, StructureUnitIB<*>>(items = modules, generators = moduleGenerators)
        ))

        moduleGenerators.addAll(listOf(
                GeneratorSimple<StructureUnitIB<*>>(
                        contextBuilder = tsContextBuilder, template = FragmentsTemplate<StructureUnitIB<*>>(
                        name = "${fileNamePrefix}ApiBase", nameBuilder = itemAndTemplateNameAsTsFileName,
                        fragments = {
                            listOf(
                                    ItemsFragment<StructureUnitIB<*>, CompilationUnitIB<*>>(items = entities,
                                            fragments = { listOf(tsTemplates.pojo()) }),
                                    ItemsFragment<StructureUnitIB<*>, CompilationUnitIB<*>>(items = values,
                                            fragments = { listOf(tsTemplates.pojo()) }),
                                    ItemsFragment<StructureUnitIB<*>, CompilationUnitIB<*>>(items = basics,
                                            fragments = { listOf(tsTemplates.pojo()) }),
                                    ItemsFragment<StructureUnitIB<*>, EnumTypeIB<*>>(items = enums,
                                            fragments = { listOf(tsTemplates.enum()) })
                            )
                        })
                )
        ))
        return ret
    }

    protected fun registerGoMacros(contextFactory: LangCommonContextFactory) {
        val macros = contextFactory.macroController
        macros.registerMacro(OperationIB<*>::toGoAggregateInitializerRegisterCommands.name,
                OperationIB<*>::toGoAggregateInitializerRegisterCommands)
        macros.registerMacro(CompilationUnitIB<*>::toGoAggregateInitializerConst.name,
                CompilationUnitIB<*>::toGoAggregateInitializerConst)
        macros.registerMacro(CompilationUnitIB<*>::toGoAggregateInitializerRegisterForEvents.name,
                CompilationUnitIB<*>::toGoAggregateInitializerRegisterForEvents)
        macros.registerMacro(ConstructorIB<*>::toGoAggregateInitializerBody.name,
                ConstructorIB<*>::toGoAggregateInitializerBody)
        macros.registerMacro(ConstructorIB<*>::toGoEventhorizonInitializerBody.name,
                ConstructorIB<*>::toGoEventhorizonInitializerBody)
        macros.registerMacro(OperationIB<*>::toGoEventhorizonInitializerSetupBody.name,
                OperationIB<*>::toGoEventhorizonInitializerSetupBody)
        macros.registerMacro(AttributeIB<*>::toGoPropOptionalAfterBody.name,
                AttributeIB<*>::toGoPropOptionalAfterBody)
        macros.registerMacro(OperationIB<*>::toGoCommandHandlerExecuteCommandBody.name,
                OperationIB<*>::toGoCommandHandlerExecuteCommandBody)
        macros.registerMacro(OperationIB<*>::toGoCommandHandlerSetupBody.name,
                OperationIB<*>::toGoCommandHandlerSetupBody)
        macros.registerMacro(OperationIB<*>::toGoEventHandlerApplyEvent.name,
                OperationIB<*>::toGoEventHandlerApplyEvent)
        macros.registerMacro(OperationIB<*>::toGoEventHandlerSetupBody.name,
                OperationIB<*>::toGoEventHandlerSetupBody)
        macros.registerMacro(OperationIB<*>::toGoHttpHandlerBody.name,
                OperationIB<*>::toGoHttpHandlerBody)
        macros.registerMacro(OperationIB<*>::toGoHttpHandlerIdBasedBody.name,
                OperationIB<*>::toGoHttpHandlerIdBasedBody)
        macros.registerMacro(OperationIB<*>::toGoHttpHandlerCommandBody.name,
                OperationIB<*>::toGoHttpHandlerCommandBody)
        macros.registerMacro(OperationIB<*>::toGoSetupHttpRouterBody.name,
                OperationIB<*>::toGoSetupHttpRouterBody)
        macros.registerMacro(ConstructorIB<*>::toGoHttpRouterBeforeBody.name,
                ConstructorIB<*>::toGoHttpRouterBeforeBody)
        macros.registerMacro(ConstructorIB<*>::toGoHttpModuleRouterBeforeBody.name,
                ConstructorIB<*>::toGoHttpModuleRouterBeforeBody)
        macros.registerMacro(OperationIB<*>::toGoSetupModuleHttpRouter.name,
                OperationIB<*>::toGoSetupModuleHttpRouter)
        macros.registerMacro(OperationIB<*>::toGoFindByBody.name,
                OperationIB<*>::toGoFindByBody)
        macros.registerMacro(OperationIB<*>::toGoCountByBody.name,
                OperationIB<*>::toGoCountByBody)
        macros.registerMacro(OperationIB<*>::toGoExistByBody.name,
                OperationIB<*>::toGoExistByBody)
        macros.registerMacro(OperationIB<*>::toGoCommandHandlerAddPreparerBody.name,
                OperationIB<*>::toGoCommandHandlerAddPreparerBody)

    }
}
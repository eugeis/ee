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

open class DesignGeneratorFactory(targetAsSingleModule: Boolean = true) : LangGeneratorFactory(targetAsSingleModule) {

    override fun buildKotlinContextFactory() = DesignKotlinContextFactory(targetAsSingleModule)
    override fun buildKotlinTemplates() = DesignKotlinTemplates(itemNameAsKotlinFileName)

    override fun buildGoContextFactory() = DesignGoContextFactory(targetAsSingleModule)
    override fun buildGoTemplates() = DesignGoTemplates(itemNameAsGoFileName)

    override fun buildTsContextFactory() = DesignTsContextFactory()
    override fun buildTsTemplates() = DesignTsTemplates(itemNameAsTsFileName)

    override fun buildSwaggerContextFactory() = DesignSwaggerContextFactory()
    fun buildSwaggerTemplates() = DesignSwaggerTemplates(itemNameAsSwaggerFileName)


    open fun go(fileNamePrefix: String = ""): GeneratorContexts<StructureUnitI<*>> {

        val goTemplates = buildGoTemplates()
        val contextFactory = buildGoContextFactory()
        val goContextBuilderIfcOrImplOnly = contextFactory.buildForImplOnly()
        val goContextBuilderIfcAndImpl = contextFactory.buildForIfcAndImpl()

        val components: StructureUnitI<*>.() -> List<CompI<*>> = {
            if (this is CompI<*>) listOf(this) else findDownByType(CompI::class.java)
        }

        val modules: StructureUnitI<*>.() -> List<ModuleI<*>> = {
            val ret = if (this is ModuleI<*>) listOf(this) else findDownByType(ModuleI::class.java)
            ret
        }

        val enums: StructureUnitI<*>.() -> List<EnumTypeI<*>> = {
            val ret = findDownByType(EnumTypeI::class.java).filter {
                it.parent() is StructureUnitI<*> && it.derivedAsType().isEmpty()
            }.sortedBy { it.name() }
            ret
        }

        val interfaces: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { it.isIfc() }
        }

        val values: StructureUnitI<*>.() -> List<ValuesI<*>> = {
            val ret = findDownByType(ValuesI::class.java).filter {
                !it.isIfc() && it.derivedAsType().isEmpty()
            }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
            ret
        }

        val basics: StructureUnitI<*>.() -> List<BasicI<*>> = {
            val ret = findDownByType(BasicI::class.java).filter {
                !it.isIfc() && it.derivedAsType().isEmpty()
            }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
            ret
        }

        val entities: StructureUnitI<*>.() -> List<EntityI<*>> = {
            val ret = findDownByType(EntityI::class.java).filter {
                !it.isIfc() && it.derivedAsType().isEmpty()
            }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
            ret
        }

        val states: StructureUnitI<*>.() -> List<StateI<*>> = {
            findDownByType(StateI::class.java).filter {
                !it.isIfc() && it.derivedAsType().isEmpty()
            }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val controllersWithOutOps: StructureUnitI<*>.() -> List<ControllerI<*>> = {
            val ret = findDownByType(ControllerI::class.java).filter {
                !it.isIfc() && it.derivedAsType().isEmpty() && it.operations().isEmpty()
            }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
            ret
        }

        val controllersWithOps: StructureUnitI<*>.() -> List<ControllerI<*>> = {
            val ret = findDownByType(ControllerI::class.java).filter {
                !it.isIfc() && it.derivedAsType().isEmpty() && it.operations().isNotEmpty()
            }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
            ret
        }

        registerGoMacros(contextFactory)

        val moduleGenerators = mutableListOf<GeneratorI<StructureUnitI<*>>>()
        val generator = GeneratorGroup(
            "go", listOf(
                GeneratorGroupItems(
                    "modulesGenerators",
                    items = modules, generators = moduleGenerators
                )
            )
        )

        moduleGenerators.addAll(
            listOf(
                GeneratorSimple(
                    "ifc_base", contextBuilder = goContextBuilderIfcOrImplOnly,
                    template = FragmentsTemplate(name = "${fileNamePrefix}ifc_base",
                        nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                            listOf(ItemsFragment(items = interfaces, fragments = {
                                listOf(goTemplates.ifc())
                            }))
                        })
                ),
                GeneratorSimple(
                    "_api_base", contextBuilder = goContextBuilderIfcOrImplOnly,
                    template = FragmentsTemplate(name = "${fileNamePrefix}api_base",
                        nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                            listOf(
                                ItemsFragment(items = entities, fragments = { listOf(goTemplates.entity()) }),
                                ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = values,
                                    fragments = { listOf(goTemplates.pojo()) }),
                                ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = basics,
                                    fragments = { listOf(goTemplates.pojo()) }),
                                ItemsFragment(items = enums, fragments = { listOf(goTemplates.enum()) }),
                                ItemsFragment(items = controllersWithOutOps, fragments = { listOf(goTemplates.pojo()) })
                            )
                        })
                ),
                GeneratorSimple(
                    "ifc_api_base", contextBuilder = goContextBuilderIfcAndImpl,
                    template = FragmentsTemplate(name = "${fileNamePrefix}ifc_api_base",
                        nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                            listOf(
                                ItemsFragment(items = controllersWithOps, fragments = {
                                    listOf(goTemplates.ifc())
                                }),
                                ItemsFragment(items = controllersWithOps, fragments = {
                                    listOf(goTemplates.pojo())
                                })
                            )
                        })
                ),
                GeneratorSimple(
                    "_test_base", contextBuilder = goContextBuilderIfcOrImplOnly,
                    template = FragmentsTemplate(name = "${fileNamePrefix}test_base",
                        nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                            listOf(
                                ItemsFragment(items = entities, fragments = { listOf(goTemplates.newTestInstance()) }),
                                ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = values,
                                    fragments = { listOf(goTemplates.newTestInstance()) }),
                                ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = basics,
                                    fragments = { listOf(goTemplates.newTestInstance()) })
                            )
                        })
                )
            )
        )

        //val derivedTypes = mutableListOf(DesignDerivedType.Aggregate, DesignDerivedType.Query, DesignDerivedType.Http,
        //        DesignDerivedType.Client, DesignDerivedType.Cli, DesignDerivedType.StateMachine)
        val derivedTypes: List<String> = emptyList()
        val derivedTypesGenerators = derivedTypes.map { derivedType ->
            GeneratorSimple(
                "${derivedType}_base", contextBuilder = goContextBuilderIfcOrImplOnly,
                template = FragmentsTemplate(name = "${fileNamePrefix}${derivedType}_base",
                    nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                        listOf(
                            ItemsFragment<StructureUnitI<*>, ControllerI<*>>(items = {
                                findDownByType(ControllerI::class.java).filter {
                                    it.derivedAsType().equals(derivedType, true)
                                }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                            }, fragments = { listOf(goTemplates.pojo()) }),
                            ItemsFragment(items = {
                                findDownByType(ValuesI::class.java).filter {
                                    it.derivedAsType().equals(derivedType, true)
                                }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                            }, fragments = { listOf(goTemplates.pojo()) }),
                            ItemsFragment(items = {
                                findDownByType(BasicI::class.java).filter {
                                    it.derivedAsType().equals(derivedType, true)
                                }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                            }, fragments = { listOf(goTemplates.pojo()) }),
                            ItemsFragment<StructureUnitI<*>, EnumTypeI<*>>(items = {
                                findDownByType(EnumTypeI::class.java).filter {
                                    it.derivedAsType().equals(derivedType, true)
                                }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                            }, fragments = { listOf(goTemplates.enum()) })
                        )
                    })
            )
        }
        moduleGenerators.addAll(derivedTypesGenerators)

        return GeneratorContexts(generator, goContextBuilderIfcOrImplOnly)
    }

    open fun goEventDriven(fileNamePrefix: String = ""): GeneratorContexts<StructureUnitI<*>> {

        val swaggerTemplates = buildSwaggerTemplates()
        val swaggerContextFactory = buildSwaggerContextFactory()
        val swaggerContextBuilder = swaggerContextFactory.build()

        val goTemplates = buildGoTemplates()
        val contextFactory = buildGoContextFactory()
        val goContextBuilder = contextFactory.buildForImplOnly()

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

        val interfaces: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { it.isIfc() }
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
            val retEntities = findDownByType(EntityI::class.java).filter { it.derivedAsType().isEmpty() }
                .sortedBy { "${it.javaClass.simpleName} ${name()}" }
            retEntities.forEach {
                it.propId()
                it.propDeletedAt()
            }
            retEntities
        }

        val states: StructureUnitI<*>.() -> List<StateI<*>> = {
            findDownByType(StateI::class.java).filter { it.derivedAsType().isEmpty() }
                .sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        registerGoMacros(contextFactory)

        val moduleGenerators = mutableListOf<GeneratorI<StructureUnitI<*>>>()
        val generator = GeneratorGroup(
            "eventDrivenGo", listOf(
                GeneratorGroupItems(
                    "modulesGenerators",
                    items = modules, generators = moduleGenerators
                ),
                Generator("swaggerComponent", contextBuilder = swaggerContextBuilder, items = components,
                    templates = { listOf(swaggerTemplates.model()) })
            )
        )

        moduleGenerators.addAll(
            listOf(
                GeneratorSimple(
                    "IfcBase", contextBuilder = goContextBuilder,
                    template = FragmentsTemplate(name = "${fileNamePrefix}IfcBase",
                        nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                            listOf(ItemsFragment(items = interfaces, fragments = {
                                listOf(goTemplates.ifc())
                            }))
                        })
                ), GeneratorSimple(
                    "ApiBase", contextBuilder = goContextBuilder,
                    template = FragmentsTemplate(name = "${fileNamePrefix}ApiBase",
                        nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                            listOf(
                                ItemsFragment(items = entities, fragments = { listOf(goTemplates.entity()) }),
                                ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = values,
                                    fragments = { listOf(goTemplates.pojo()) }),
                                ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = basics,
                                    fragments = { listOf(goTemplates.pojo()) }),
                                ItemsFragment(items = enums, fragments = { listOf(goTemplates.enum()) })
                            )
                        })
                ),
                GeneratorSimple(
                    "TestBase", contextBuilder = goContextBuilder,
                    template = FragmentsTemplate(name = "${fileNamePrefix}TestBase",
                        nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                            listOf(
                                ItemsFragment(items = entities, fragments = { listOf(goTemplates.newTestInstance()) }),
                                ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = values,
                                    fragments = { listOf(goTemplates.newTestInstance()) }),
                                ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = basics,
                                    fragments = { listOf(goTemplates.newTestInstance()) })
                            )
                        })
                ), GeneratorSimple(
                    "CommandsBase", contextBuilder = goContextBuilder,
                    template = FragmentsTemplate(name = "${fileNamePrefix}CommandsBase",
                        nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                            listOf(
                                ItemsFragment(items = entities, fragments = { listOf(goTemplates.commandTypes()) }),
                                ItemsFragment(items = commands, fragments = { listOf(goTemplates.command()) }),
                                ItemsFragment(items = commandEnums, fragments = { listOf(goTemplates.enum()) })
                            )
                        })
                ), GeneratorSimple(
                    "EventsBase", contextBuilder = goContextBuilder,
                    template = FragmentsTemplate(name = "${fileNamePrefix}EventsBase",
                        nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                            listOf(
                                ItemsFragment(items = entities, fragments = { listOf(goTemplates.eventTypes()) }),
                                ItemsFragment(
                                    items = events,
                                    fragments = { listOf(goTemplates.pojoExcludePropsWithValue()) }),
                                ItemsFragment(items = eventEnums, fragments = { listOf(goTemplates.enum()) })
                            )
                        })
                )
            )
        )

        val derivedTypes = mutableListOf(
            DesignDerivedType.Aggregate, DesignDerivedType.Query, DesignDerivedType.Http,
            DesignDerivedType.Client, DesignDerivedType.Cli, DesignDerivedType.StateMachine
        )
        val derivedTypesGenerators = derivedTypes.map { derivedType ->
            GeneratorSimple(
                "${derivedType}Base", contextBuilder = goContextBuilder,
                template = FragmentsTemplate(name = "$fileNamePrefix${derivedType}Base",
                    nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                        listOf(ItemsFragment(items = {
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
                            ItemsFragment(items = {
                                findDownByType(EnumTypeI::class.java).filter {
                                    it.derivedAsType().equals(derivedType, true)
                                }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                            }, fragments = { listOf(goTemplates.enum()) })
                        )
                    })
            )
        }
        moduleGenerators.addAll(derivedTypesGenerators)

        return GeneratorContexts(generator, swaggerContextBuilder, goContextBuilder)
    }

    open fun angular(fileNamePrefix: String = ""): GeneratorContexts<StructureUnitI<*>> {
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

        val moduleGenerators = mutableListOf<GeneratorI<StructureUnitI<*>>>()
        val generator = GeneratorGroup(
            "angular",
            listOf(GeneratorGroupItems("angularModules", items = modules, generators = moduleGenerators))
        )

        moduleGenerators.addAll(
            listOf(
                GeneratorSimple(
                    "ApiBase", contextBuilder = tsContextBuilder,
                    template = FragmentsTemplate(name = "${fileNamePrefix}ApiBase",
                        nameBuilder = itemAndTemplateNameAsTsFileName, fragments = {
                            listOf(
                                ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = entities,
                                    fragments = { listOf(tsTemplates.pojo()) }),
                                ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = values,
                                    fragments = { listOf(tsTemplates.pojo()) }),
                                ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = basics,
                                    fragments = { listOf(tsTemplates.pojo()) }),
                                ItemsFragment(items = enums, fragments = { listOf(tsTemplates.enum()) })
                            )
                        })
                )
            )
        )
        return GeneratorContexts(generator, tsContextBuilder)
    }

    protected fun registerGoMacros(contextFactory: LangCommonContextFactory) {
        val macros = contextFactory.macroController
        macros.registerMacro(
            OperationI<*>::toGoAggregateInitializerRegisterCommands.name,
            OperationI<*>::toGoAggregateInitializerRegisterCommands
        )
        macros.registerMacro(
            CompilationUnitI<*>::toGoAggregateInitializerConst.name,
            CompilationUnitI<*>::toGoAggregateInitializerConst
        )
        macros.registerMacro(
            CompilationUnitI<*>::toGoAggregateInitializerRegisterForEvents.name,
            CompilationUnitI<*>::toGoAggregateInitializerRegisterForEvents
        )
        macros.registerMacro(
            ConstructorI<*>::toGoAggregateInitializerBody.name,
            ConstructorI<*>::toGoAggregateInitializerBody
        )
        macros.registerMacro(
            ConstructorI<*>::toGoEhInitializerBody.name,
            ConstructorI<*>::toGoEhInitializerBody
        )
        macros.registerMacro(
            OperationI<*>::toGoEhInitializerSetupBody.name,
            OperationI<*>::toGoEhInitializerSetupBody
        )
        macros.registerMacro(AttributeI<*>::toGoPropOptionalAfterBody.name, AttributeI<*>::toGoPropOptionalAfterBody)
        macros.registerMacro(
            OperationI<*>::toGoCommandHandlerExecuteCommandBody.name,
            OperationI<*>::toGoCommandHandlerExecuteCommandBody
        )
        macros.registerMacro(
            OperationI<*>::toGoCommandHandlerSetupBody.name,
            OperationI<*>::toGoCommandHandlerSetupBody
        )
        macros.registerMacro(OperationI<*>::toGoEventHandlerApplyEvent.name, OperationI<*>::toGoEventHandlerApplyEvent)
        macros.registerMacro(OperationI<*>::toGoEventHandlerSetupBody.name, OperationI<*>::toGoEventHandlerSetupBody)
        macros.registerMacro(OperationI<*>::toGoHttpHandlerBody.name, OperationI<*>::toGoHttpHandlerBody)
        macros.registerMacro(OperationI<*>::toGoHttpHandlerIdBasedBody.name, OperationI<*>::toGoHttpHandlerIdBasedBody)
        macros.registerMacro(OperationI<*>::toGoHttpHandlerCommandBody.name, OperationI<*>::toGoHttpHandlerCommandBody)
        macros.registerMacro(OperationI<*>::toGoSetupHttpRouterBody.name, OperationI<*>::toGoSetupHttpRouterBody)
        macros.registerMacro(ConstructorI<*>::toGoHttpRouterBeforeBody.name, ConstructorI<*>::toGoHttpRouterBeforeBody)
        macros.registerMacro(
            ConstructorI<*>::toGoHttpModuleRouterBeforeBody.name,
            ConstructorI<*>::toGoHttpModuleRouterBeforeBody
        )
        macros.registerMacro(OperationI<*>::toGoSetupModuleHttpRouter.name, OperationI<*>::toGoSetupModuleHttpRouter)

        macros.registerMacro(OperationI<*>::toGoHttpClientCreateItemsBody.name, OperationI<*>::toGoHttpClientCreateItemsBody)
        macros.registerMacro(OperationI<*>::toGoHttpClientDeleteItemsBody.name, OperationI<*>::toGoHttpClientDeleteItemsBody)
        macros.registerMacro(OperationI<*>::toGoHttpClientDeleteByIdBody.name, OperationI<*>::toGoHttpClientDeleteByIdBody)
        macros.registerMacro(OperationI<*>::toGoHttpClientFindAllBody.name, OperationI<*>::toGoHttpClientFindAllBody)
        macros.registerMacro(
            OperationI<*>::toGoHttpClientImportJsonBody.name,
            OperationI<*>::toGoHttpClientImportJsonBody
        )
        macros.registerMacro(
            OperationI<*>::toGoHttpClientReadFileJsonBody.name,
            OperationI<*>::toGoHttpClientReadFileJsonBody
        )
        macros.registerMacro(ConstructorI<*>::toGoHttpClientBeforeBody.name, ConstructorI<*>::toGoHttpClientBeforeBody)
        macros.registerMacro(
            ConstructorI<*>::toGoHttpModuleClientBeforeBody.name,
            ConstructorI<*>::toGoHttpModuleClientBeforeBody
        )
        macros.registerMacro(
            ConstructorI<*>::toGoHttpModuleCliBeforeBody.name,
            ConstructorI<*>::toGoHttpModuleCliBeforeBody
        )

        macros.registerMacro(OperationI<*>::toGoFindByBody.name, OperationI<*>::toGoFindByBody)
        macros.registerMacro(OperationI<*>::toGoCountByBody.name, OperationI<*>::toGoCountByBody)
        macros.registerMacro(OperationI<*>::toGoExistByBody.name, OperationI<*>::toGoExistByBody)
        macros.registerMacro(
            OperationI<*>::toGoCommandHandlerAddPreparerBody.name,
            OperationI<*>::toGoCommandHandlerAddPreparerBody
        )

        macros.registerMacro(
            OperationI<*>::toGoStateEventHandlerApplyEvent.name,
            OperationI<*>::toGoStateEventHandlerApplyEvent
        )
        macros.registerMacro(
            OperationI<*>::toGoStateEventHandlerSetupBody.name,
            OperationI<*>::toGoStateEventHandlerSetupBody
        )

    }
}
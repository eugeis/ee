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
import ee.lang.gen.ts.*

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
                            ItemsFragment(items = {
                                findDownByType(ControllerI::class.java).filter {
                                    it.derivedAsType().equals(derivedType, true)
                                }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                            }, fragments = { listOf(goTemplates.ifcOrPojo()) }),
                            ItemsFragment(items = {
                                findDownByType(ValuesI::class.java).filter {
                                    it.derivedAsType().equals(derivedType, true)
                                }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                            }, fragments = { listOf(goTemplates.ifcOrPojo()) }),
                            ItemsFragment(items = {
                                findDownByType(BasicI::class.java).filter {
                                    it.derivedAsType().equals(derivedType, true)
                                }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                            }, fragments = { listOf(goTemplates.ifcOrPojo()) }),
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

            )
        )

        val derivedTypes = mutableListOf(
            "",
            DesignDerivedType.Aggregate,
            DesignDerivedType.AggregateEvents,
            DesignDerivedType.AggregateCommands,
            DesignDerivedType.Query,
            DesignDerivedType.Http,
            DesignDerivedType.Client,
            DesignDerivedType.Cli,
            DesignDerivedType.StateMachine,
            DesignDerivedType.StateMachineEvents,
            DesignDerivedType.StateMachineCommands,
        )

        derivedTypes.forEach { derivedType ->

            val controllers: StructureUnitI<*>.() -> Collection<ControllerI<*>> = {
                findDownByType(type = ControllerI::class.java, stopSteppingDownIfFound = false).filter {
                    !it.isIfc() && it.derivedAsType().equals(derivedType, true)
                }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
            }

            val commands: StructureUnitI<*>.() -> List<CommandI<*>> = {
                findDownByType(CommandI::class.java).filter {
                    it.derivedAsType().equals(derivedType, true)
                }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
            }

            val eventsWithData: StructureUnitI<*>.() -> List<EventI<*>> = {
                findDownByType(EventI::class.java).filter {
                    it.props().isNotEmpty() && it.derivedAsType().equals(derivedType, true)
                }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
            }

            val enums: StructureUnitI<*>.() -> List<EnumTypeI<*>> = {
                findDownByType(EnumTypeI::class.java).filter {
                    it.derivedAsType().equals(derivedType, true)
                }.sortedBy { it.name() }
            }

            val interfaces: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
                findDownByType(CompilationUnitI::class.java, stopSteppingDownIfFound = false).filter {
                    it.isIfc() && it.derivedAsType().equals(derivedType, true)
                }.sortedBy { it.name() }
            }

            val values: StructureUnitI<*>.() -> List<ValuesI<*>> = {
                findDownByType(ValuesI::class.java).filter {
                    it.derivedAsType().equals(derivedType, true)
                }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
            }

            val basics: StructureUnitI<*>.() -> List<BasicI<*>> = {
                findDownByType(BasicI::class.java).filter {
                    it.derivedAsType().equals(derivedType, true)
                }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
            }

            val entities: StructureUnitI<*>.() -> List<EntityI<*>> = {
                val retEntities = findDownByType(EntityI::class.java).filter {
                    it.derivedAsType().equals(derivedType, true)
                }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
                retEntities.forEach {
                    it.propIdOrAdd()
                    it.propDeletedAt()
                }
                retEntities
            }

            moduleGenerators.add(
                GeneratorSimple(
                    "IfcBase", contextBuilder = goContextBuilder,
                    template = FragmentsTemplate(name = "${fileNamePrefix}${derivedType}IfcBase",
                        nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                            listOf(ItemsFragment(items = interfaces, fragments = {
                                listOf(goTemplates.ifc())
                            }))
                        })
                )
            )
            moduleGenerators.add(
                GeneratorSimple(
                    "ApiBase", contextBuilder = goContextBuilder,
                    template = FragmentsTemplate(name = "${fileNamePrefix}${derivedType}ApiBase",
                        nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                            listOf(
                                ItemsFragment(items = entities, fragments = { listOf(goTemplates.entity()) }),
                                ItemsFragment(items = controllers, fragments = { listOf(goTemplates.pojo()) }),
                                ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = values,
                                    fragments = { listOf(goTemplates.pojo()) }),
                                ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = basics,
                                    fragments = { listOf(goTemplates.pojo()) }),
                                ItemsFragment(items = enums, fragments = { listOf(goTemplates.enum()) })
                            )
                        })
                )
            )

            moduleGenerators.add(
                GeneratorSimple(
                    "TestBase", contextBuilder = goContextBuilder,
                    template = FragmentsTemplate(name = "${fileNamePrefix}${derivedType}TestBase",
                        nameBuilder = itemAndTemplateNameAsGoFileName,
                        fragments = {
                            listOf(
                                ItemsFragment(items = entities,
                                    fragments = { listOf(goTemplates.newTestInstance()) }),
                                ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = values,
                                    fragments = { listOf(goTemplates.newTestInstance()) }),
                                ItemsFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = basics,
                                    fragments = { listOf(goTemplates.newTestInstance()) })
                            )
                        })
                )
            )
            moduleGenerators.add(
                GeneratorSimple(
                    "CommandsBase", contextBuilder = goContextBuilder,
                    template = FragmentsTemplate(name = "${fileNamePrefix}${derivedType}EsCommandsBase",
                        nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                            listOf(
                                ItemsFragment(items = entities, fragments = { listOf(goTemplates.commandTypes()) }),
                                ItemsFragment(items = commands, fragments = { listOf(goTemplates.command()) }),
                            )
                        })
                )
            )
            moduleGenerators.add(
                GeneratorSimple(
                    "EventsBase", contextBuilder = goContextBuilder,
                    template = FragmentsTemplate(name = "${fileNamePrefix}${derivedType}EsEventsBase",
                        nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                            listOf(
                                ItemsFragment(items = entities, fragments = { listOf(goTemplates.eventTypes()) }),
                                ItemsFragment(
                                    items = eventsWithData,
                                    fragments = { listOf(goTemplates.pojoExcludePropsWithValue()) }),
                            )
                        })
                )
            )
        }

        return GeneratorContexts(generator, swaggerContextBuilder, goContextBuilder)
    }

    open fun angular(fileNamePrefix: String = "", model: StructureUnitI<*>): GeneratorContexts<StructureUnitI<*>> {
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

        /*
        for (i in basics.invoke(model)){
            moduleGenerators.add(
                GeneratorSimple(
                    "TypeScriptComponent", contextBuilder = tsContextBuilder,
                    template = SingleItemFragmentsTemplate(name = "${fileNamePrefix}${i.name().toLowerCase()}.component",
                        nameBuilder = templateNameAsTsFileName, fragments = {
                            SingleItemFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = basics,
                                fragments = { tsTemplates.angularComponentTypeScript(i) })
                        })
                )
            )
            moduleGenerators.add(
                GeneratorSimple(
                    "HtmlComponent", contextBuilder = tsContextBuilder,
                    template = SingleItemFragmentsTemplate(name = "${fileNamePrefix}${i.name().toLowerCase()}.component",
                        nameBuilder = templateNameAsHTMLFileName, fragments = {
                            SingleItemFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = basics,
                                fragments = { tsTemplates.angularComponentHTML(i) })
                        })
                )
            )
            moduleGenerators.add(
                GeneratorSimple(
                    "ScssComponent", contextBuilder = tsContextBuilder,
                    template = SingleItemFragmentsTemplate(name = "${fileNamePrefix}${i.name().toLowerCase()}.component",
                        nameBuilder = templateNameAsCSSFileName, fragments = {
                            SingleItemFragment<StructureUnitI<*>, CompilationUnitI<*>>(items = basics,
                                fragments = { tsTemplates.angularComponentCSS(i) })
                        })
                )
            )
        }*/

        return GeneratorContexts(generator, tsContextBuilder)
    }

    protected fun registerGoMacros(contextFactory: LangCommonContextFactory) {
        val macros = contextFactory.macroController
        macros.registerMacro(
            OperationI<*>::toGoAggregateEngineRegisterCommands.name,
            OperationI<*>::toGoAggregateEngineRegisterCommands
        )
        macros.registerMacro(
            CompilationUnitI<*>::toGoAggregateEngineConst.name,
            CompilationUnitI<*>::toGoAggregateEngineConst
        )
        macros.registerMacro(
            CompilationUnitI<*>::toGoAggregateEngineRegisterForEvents.name,
            CompilationUnitI<*>::toGoAggregateEngineRegisterForEvents
        )

        macros.registerMacro(
            ConstructorI<*>::toGoAggregateEngineBody.name,
            ConstructorI<*>::toGoAggregateEngineBody
        )

        macros.registerMacro(
            OperationI<*>::toGoAggregateEngineSetupBody.name,
            OperationI<*>::toGoAggregateEngineSetupBody
        )

        macros.registerMacro(
            OperationI<*>::toGoAggregateApplyEventBody.name,
            OperationI<*>::toGoAggregateApplyEventBody
        )

        macros.registerMacro(ConstructorI<*>::toGoEhEngineBody.name, ConstructorI<*>::toGoEhEngineBody)
        macros.registerMacro(OperationI<*>::toGoEhEngineSetupBody.name, OperationI<*>::toGoEhEngineSetupBody)
        macros.registerMacro(AttributeI<*>::toGoPropOptionalAfterBody.name, AttributeI<*>::toGoPropOptionalAfterBody)

        macros.registerMacro(
            OperationI<*>::toGoStatesCommandHandlerSetupBody.name,
            OperationI<*>::toGoStatesCommandHandlerSetupBody
        )
        macros.registerMacro(
            OperationI<*>::toGoStatesCommandHandlerExecute.name,
            OperationI<*>::toGoStatesCommandHandlerExecute
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

        macros.registerMacro(OperationI<*>::toGoHttpClientCreateBody.name, OperationI<*>::toGoHttpClientCreateBody)
        macros.registerMacro(
            OperationI<*>::toGoHttpClientCreateItemsBody.name,
            OperationI<*>::toGoHttpClientCreateItemsBody
        )
        macros.registerMacro(
            OperationI<*>::toGoHttpClientDeleteByIdsBody.name,
            OperationI<*>::toGoHttpClientDeleteByIdsBody
        )
        macros.registerMacro(
            OperationI<*>::toGoHttpClientDeleteByIdBody.name,
            OperationI<*>::toGoHttpClientDeleteByIdBody
        )
        macros.registerMacro(OperationI<*>::toGoHttpClientFindAllBody.name, OperationI<*>::toGoHttpClientFindAllBody)
        macros.registerMacro(
            OperationI<*>::toGoHttpClientImportJsonBody.name,
            OperationI<*>::toGoHttpClientImportJsonBody
        )
        macros.registerMacro(
            OperationI<*>::toGoHttpClientExportJsonBody.name,
            OperationI<*>::toGoHttpClientExportJsonBody
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

        macros.registerMacro(ConstructorI<*>::toGoCliBeforeBody.name, ConstructorI<*>::toGoCliBeforeBody)

        macros.registerMacro(OperationI<*>::toGoCliBuildCommands.name, OperationI<*>::toGoCliBuildCommands)
        macros.registerMacro(OperationI<*>::toGoCliDeleteByIdBody.name, OperationI<*>::toGoCliDeleteByIdBody)
        macros.registerMacro(OperationI<*>::toGoCliDeleteByIdsBody.name, OperationI<*>::toGoCliDeleteByIdsBody)
        macros.registerMacro(OperationI<*>::toGoCliImportJsonBody.name, OperationI<*>::toGoCliImportJsonBody)
        macros.registerMacro(OperationI<*>::toGoCliExportJsonBody.name, OperationI<*>::toGoCliExportJsonBody)

        macros.registerMacro(OperationI<*>::toGoFindByBody.name, OperationI<*>::toGoFindByBody)
        macros.registerMacro(OperationI<*>::toGoCountByBody.name, OperationI<*>::toGoCountByBody)
        macros.registerMacro(OperationI<*>::toGoExistByBody.name, OperationI<*>::toGoExistByBody)

        macros.registerMacro(
            OperationI<*>::toGoStateCommandHandlerSetupBody.name,
            OperationI<*>::toGoStateCommandHandlerSetupBody
        )

        macros.registerMacro(
            OperationI<*>::toGoStateCommandHandlerAddCommandPreparerBody.name,
            OperationI<*>::toGoStateCommandHandlerAddCommandPreparerBody
        )

        macros.registerMacro(
            OperationI<*>::toGoStateAddCommandsPreparerBody.name,
            OperationI<*>::toGoStateAddCommandsPreparerBody
        )

        macros.registerMacro(
            OperationI<*>::toGoStateCommandHandlerExecuteBody.name,
            OperationI<*>::toGoStateCommandHandlerExecuteBody
        )

        macros.registerMacro(
            OperationI<*>::toGoStateEventType.name,
            OperationI<*>::toGoStateEventType
        )

        macros.registerMacro(
            OperationI<*>::toGoStateEventHandlersPreparerBody.name,
            OperationI<*>::toGoStateEventHandlersPreparerBody
        )

        macros.registerMacro(
            OperationI<*>::toGoStateEventHandlerApplyEvent.name,
            OperationI<*>::toGoStateEventHandlerApplyEvent
        )

        macros.registerMacro(
            OperationI<*>::toGoStateEventHandlerSetupBody.name,
            OperationI<*>::toGoStateEventHandlerSetupBody
        )

        macros.registerMacro(
            OperationI<*>::toGoStatesEventHandlerSetupBody.name,
            OperationI<*>::toGoStatesEventHandlerSetupBody
        )

        macros.registerMacro(
            OperationI<*>::toGoStatesEventHandlerApplyEvent.name,
            OperationI<*>::toGoStatesEventHandlerApplyEvent
        )

        macros.registerMacro(
            OperationI<*>::toGoAggregateHandleCommand.name,
            OperationI<*>::toGoAggregateHandleCommand
        )
    }
}

package ee.lang.gen.kt

import ee.lang.*
import ee.lang.gen.common.LangCommonContextFactory
import ee.lang.gen.go.itemNameAsGoFileName
import ee.lang.gen.itemAndTemplateNameAsKotlinFileName
import ee.lang.gen.itemNameAsKotlinFileName
import ee.lang.gen.templateNameAsKotlinFileName
import org.slf4j.LoggerFactory

open class LangKotlinGenerator(val model: StructureUnitI<*>, private val targetAsSingleModule: Boolean = false) {
    private val log = LoggerFactory.getLogger(javaClass)

    protected open fun buildKotlinContextFactory() = LangKotlinContextFactory(targetAsSingleModule)
    protected open fun buildKotlinTemplates() = LangKotlinTemplates(itemNameAsKotlinFileName)

    open fun dslKt(fileNamePrefix: String = ""): GeneratorContexts<StructureUnitI<*>> {
        val ktTemplates = buildKotlinTemplates()
        val ktContextFactory = buildKotlinContextFactory()
        val ktContextBuilder = ktContextFactory.buildForDslBuilder()

        val compilationUnits: StructureUnitI<*>.() -> List<CompilationUnitI<*>> =
            { items().filterIsInstance(CompilationUnitI::class.java) }

        val generator = GeneratorGroup("dslKt", listOf(
            GeneratorSimple("IfcBase", contextBuilder = ktContextBuilder,
                template = ItemsTemplate(name = "${fileNamePrefix}IfcBase", nameBuilder = templateNameAsKotlinFileName,
                    items = compilationUnits, fragments = { listOf(ktTemplates.dslBuilderI()) })),
            GeneratorSimple("ApiBase", contextBuilder = ktContextBuilder,
                template = ItemsTemplate(name = "${fileNamePrefix}ApiBase", nameBuilder = templateNameAsKotlinFileName,
                    items = compilationUnits, fragments = { listOf(ktTemplates.dslBuilder()) }))))
        return GeneratorContexts(generator, ktContextBuilder)
    }

    open fun pojoKt(fileNamePrefix: String = ""): GeneratorContexts<StructureUnitI<*>> {
        val ktTemplates = buildKotlinTemplates()
        val ktContextFactory = buildKotlinContextFactory()
        val ktContextBuilder = ktContextFactory.buildForImplOnly()
        val ktContextBuilderTest = ktContextFactory.buildForImplOnly("test")

        val enums: StructureUnitI<*>.() -> List<EnumTypeI<*>> = { findDownByType(EnumTypeI::class.java) }
        val compilationUnits: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { it !is EnumTypeI<*> && !it.isIfc() }
        }
        val interfaces: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { it.isIfc() }
        }

        val generator = GeneratorGroup("pojoKt", listOf(
            GeneratorSimple("IfcBase", contextBuilder = ktContextBuilder,
                template = FragmentsTemplate(name = "${fileNamePrefix}IfcBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = interfaces, fragments = {
                            listOf(ktTemplates.ifc(itemNameAsKotlinFileName))
                        }))
                    })),
            GeneratorSimple("ApiBase", contextBuilder = ktContextBuilder,
                template = FragmentsTemplate(name = "${fileNamePrefix}ApiBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(
                            ItemsFragment(items = enums,
                            fragments = { listOf(ktTemplates.enum()) }),
                            ItemsFragment(items = compilationUnits, fragments = {
                                listOf(ktTemplates.pojo(itemNameAsKotlinFileName))
                            })
                        )
                    })),
            GeneratorSimple("IfcEmpty", contextBuilder = ktContextBuilder,
                template = FragmentsTemplate(name = "${fileNamePrefix}IfcEmpty",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = interfaces, fragments = {
                            listOf(ktTemplates.ifcEmpty(itemNameAsKotlinFileName))
                        }))
                    })),
            GeneratorSimple("ApiTestEnumsBase", contextBuilder = ktContextBuilderTest,
                template = FragmentsTemplate(name = "${fileNamePrefix}ApiTestEnumsBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(
                            ItemsFragment(items = enums,
                            fragments = {
                                listOf(ktTemplates.enumParseAndIsMethodsTestsParseMethodTests())
                            })
                        )
                    })),
            GeneratorSimple("ApiTestBase", contextBuilder = ktContextBuilderTest,
                template = FragmentsTemplate(name = "${fileNamePrefix}ApiTestBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(
                            ItemsFragment(items = compilationUnits,
                            fragments = {
                                listOf(ktTemplates.pojoTest())
                            })
                        )
                    }))))
        return GeneratorContexts(generator, ktContextBuilder)
    }

    open fun pojoAndBuildersKt(fileNamePrefix: String = ""): GeneratorContexts<StructureUnitI<*>> {
        val ktTemplates = buildKotlinTemplates()
        val ktContextFactory = buildKotlinContextFactory()
        val ktContextBuilder = ktContextFactory.buildForImplOnly()
        val ktContextBuilderTest = ktContextFactory.buildForImplOnly("test")

        val enums: StructureUnitI<*>.() -> List<EnumTypeI<*>> = {
            findDownByType(EnumTypeI::class.java)
        }
        val compilationUnits: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { it !is EnumTypeI<*> && !it.isIfc() }
        }
        val interfaces: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { it.isIfc() }
        }
        val interfacesNonBlock: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { type ->
                type.isIfc() && (type.isNonBlock() || type.operations().find {
                    it.isNonBlock().notNullValueElse(type.isNonBlock())
                } != null)
            }
        }
        val dataTypes: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(DataTypeI::class.java).filter { it !is EnumTypeI<*> && !it.isIfc() }
        }

        registerKtMacros(ktContextFactory)

        val generator = GeneratorGroup("pojoAndBuildersKt", listOf(
            GeneratorSimple("IfcBase", contextBuilder = ktContextBuilder,
                template = FragmentsTemplate(name = "${fileNamePrefix}IfcBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = interfaces, fragments = {
                            listOf(ktTemplates.ifc(itemNameAsKotlinFileName))
                        }))
                    })),
            GeneratorSimple("IfcBlockingBase", contextBuilder = ktContextBuilder,
                template = FragmentsTemplate(name = "${fileNamePrefix}IfcBlockingBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = interfacesNonBlock, fragments = {
                            listOf(ktTemplates.ifcBlocking(itemNameAsKotlinFileName))
                        }))
                    })),
            GeneratorSimple("IfcEmpty", contextBuilder = ktContextBuilder,
                template = FragmentsTemplate(name = "${fileNamePrefix}IfcEmpty",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = interfaces, fragments = {
                            listOf(ktTemplates.ifcEmpty(itemNameAsKotlinFileName))
                        }))
                    })),
            GeneratorSimple("IfcBlockingEmpty", contextBuilder = ktContextBuilder,
                template = FragmentsTemplate(name = "${fileNamePrefix}IfcBlockingEmpty",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = interfacesNonBlock, fragments = {
                            listOf(ktTemplates.emptyBlocking(itemNameAsKotlinFileName))
                        }))
                    })),
            GeneratorSimple("BlockingWrapper", contextBuilder = ktContextBuilder,
                template = FragmentsTemplate(name = "${fileNamePrefix}BlockingWrapper",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = interfacesNonBlock, fragments = {
                            listOf(ktTemplates.blockingWrapper(itemNameAsKotlinFileName))
                        }))
                    })),
            GeneratorSimple("ApiBase", contextBuilder = ktContextBuilder,
                template = FragmentsTemplate(name = "${fileNamePrefix}ApiBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = enums,
                            fragments = { listOf(ktTemplates.enum()) }),
                            ItemsFragment(items = compilationUnits, fragments = {
                                listOf(ktTemplates.pojo(itemNameAsKotlinFileName))
                            }))
                    })),
            GeneratorSimple("BuilderIfcBase", contextBuilder = ktContextBuilder,
                template = ItemsTemplate(name = "${fileNamePrefix}BuilderIfcBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName,
                    items = dataTypes, fragments = { listOf(ktTemplates.builderI(itemNameAsKotlinFileName)) })),
            GeneratorSimple("BuilderApiBase", contextBuilder = ktContextBuilder,
                template = ItemsTemplate(name = "${fileNamePrefix}BuilderApiBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName,
                    items = dataTypes, fragments = { listOf(ktTemplates.builder(itemNameAsKotlinFileName)) })),
            GeneratorSimple("ApiTestEnumsBase", contextBuilder = ktContextBuilderTest,
                template = FragmentsTemplate(name = "${fileNamePrefix}ApiTestEnumsBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = enums,
                            fragments = {
                                listOf(ktTemplates.enumParseAndIsMethodsTestsParseMethodTests())
                            }))
                    })),
            GeneratorSimple("ApiTestBase", contextBuilder = ktContextBuilderTest,
                template = FragmentsTemplate(name = "${fileNamePrefix}ApiTestBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = compilationUnits,
                            fragments = {
                                listOf(ktTemplates.pojoTest())
                            }))
                    }))
        ))
        return GeneratorContexts(generator, ktContextBuilder, ktContextBuilderTest)
    }

    open fun fx(fileNamePrefix: String = ""): GeneratorContexts<StructureUnitI<*>> {
        val ktTemplates = buildKotlinTemplates()
        val ktContextFactory = buildKotlinContextFactory()
        val ktContextBuilder = ktContextFactory.buildForImplOnly()
        val ktContextBuilderTest = ktContextFactory.buildForImplOnly("test")

        val enums: StructureUnitI<*>.() -> List<EnumTypeI<*>> = {
            findDownByType(EnumTypeI::class.java)
        }
        val compilationUnits: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { it !is EnumTypeI<*> && !it.isIfc() }
        }
        val interfaces: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { it.isIfc() }
        }
        val interfacesNonBlock: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { it.isIfc() && it.isNonBlock() }
        }
        val dataTypes: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(DataTypeI::class.java).filter { it !is EnumTypeI<*> && !it.isIfc() }
        }

        val generator = GeneratorGroup("pojoAndBuildersKt", listOf(
            GeneratorSimple("IfcBase", contextBuilder = ktContextBuilder,
                template = FragmentsTemplate(name = "${fileNamePrefix}IfcBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = interfaces, fragments = {
                            listOf(ktTemplates.ifc(itemNameAsGoFileName))
                        }))
                    })),
            GeneratorSimple("IfcBlockingBase", contextBuilder = ktContextBuilder,
                template = FragmentsTemplate(name = "${fileNamePrefix}IfcBlockingBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = interfacesNonBlock, fragments = {
                            listOf(ktTemplates.ifcBlocking(itemNameAsKotlinFileName))
                        }))
                    })),
            GeneratorSimple("IfcEmpty", contextBuilder = ktContextBuilder,
                template = FragmentsTemplate(name = "${fileNamePrefix}IfcEmpty",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = interfaces, fragments = {
                            listOf(ktTemplates.ifcEmpty(itemNameAsKotlinFileName))
                        }))
                    })),
            GeneratorSimple("IfcBlockingEmpty", contextBuilder = ktContextBuilder,
                template = FragmentsTemplate(name = "${fileNamePrefix}IfcBlockingEmpty",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = interfacesNonBlock, fragments = {
                            listOf(ktTemplates.emptyBlocking(itemNameAsKotlinFileName))
                        }))
                    })),
            GeneratorSimple("BlockingWrapper", contextBuilder = ktContextBuilder,
                template = FragmentsTemplate(name = "${fileNamePrefix}BlockingWrapper",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = interfacesNonBlock, fragments = {
                            listOf(ktTemplates.blockingWrapper(itemNameAsKotlinFileName))
                        }))
                    })),
            GeneratorSimple("ApiBase", contextBuilder = ktContextBuilder,
                template = FragmentsTemplate(name = "${fileNamePrefix}ApiBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = enums,
                            fragments = { listOf(ktTemplates.enum()) }),
                            ItemsFragment(items = compilationUnits, fragments = {
                                listOf(ktTemplates.pojo(itemNameAsKotlinFileName))
                            }))
                    })),
            GeneratorSimple("BuilderIfcBase", contextBuilder = ktContextBuilder,
                template = ItemsTemplate(name = "${fileNamePrefix}BuilderIfcBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName,
                    items = dataTypes, fragments = { listOf(ktTemplates.builderI(itemNameAsKotlinFileName)) })),
            GeneratorSimple("BuilderApiBase", contextBuilder = ktContextBuilder,
                template = ItemsTemplate(name = "${fileNamePrefix}BuilderApiBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName,
                    items = dataTypes, fragments = { listOf(ktTemplates.builder(itemNameAsKotlinFileName)) })),
            GeneratorSimple("ApiTestEnumsBase", contextBuilder = ktContextBuilderTest,
                template = FragmentsTemplate(name = "${fileNamePrefix}ApiTestEnumsBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = enums,
                            fragments = {
                                listOf(ktTemplates.enumParseAndIsMethodsTestsParseMethodTests())
                            }))
                    })),
            GeneratorSimple("ApiTestBase", contextBuilder = ktContextBuilderTest,
                template = FragmentsTemplate(name = "${fileNamePrefix}ApiTestBase",
                    nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                        listOf(ItemsFragment(items = compilationUnits,
                            fragments = {
                                listOf(ktTemplates.pojoTest())
                            }))
                    }))
        ))
        return GeneratorContexts(generator, ktContextBuilder, ktContextBuilderTest)
    }

    protected fun registerKtMacros(contextFactory: LangCommonContextFactory) {
        val macros = contextFactory.macroController
        macros.registerMacro(TypeI<*>::toKotlinInstanceEMPTY.name, TypeI<*>::toKotlinInstanceEMPTY)
        macros.registerMacro(TypeI<*>::toKotlinInstanceDotEMPTY.name, TypeI<*>::toKotlinInstanceDotEMPTY)
    }
}
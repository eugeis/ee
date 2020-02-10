package ee.lang.gen

import ee.lang.*
import ee.lang.gen.common.LangCommonContextFactory
import ee.lang.gen.doc.LangMarkdownContextFactory
import ee.lang.gen.doc.LangMarkdownTemplates
import ee.lang.gen.doc.itemNameAsMarkdownFileName
import ee.lang.gen.doc.templateNameAsMarkdownFileName
import ee.lang.gen.go.LangGoContextFactory
import ee.lang.gen.go.LangGoTemplates
import ee.lang.gen.go.itemAndTemplateNameAsGoFileName
import ee.lang.gen.go.itemNameAsGoFileName
import ee.lang.gen.kt.LangKotlinContextFactory
import ee.lang.gen.kt.LangKotlinTemplates
import ee.lang.gen.kt.toKotlinInstanceDotEMPTY
import ee.lang.gen.kt.toKotlinInstanceEMPTY
import ee.lang.gen.ts.LangTsContextFactory
import ee.lang.gen.ts.LangTsTemplates

open class LangGeneratorFactory(protected val singleModule: Boolean = true) {

    open fun devDoc(fileNamePrefix: String = ""): GeneratorContexts<StructureUnitI<*>> {
        val docTemplates = buildDocTemplates()
        val docContextFactory = buildDocContextFactory()
        val docContextBuilder = docContextFactory.build()

        val compilationUnits: StructureUnitI<*>.() -> List<CompilationUnitI<*>> =
                { items().filterIsInstance(CompilationUnitI::class.java) }

        val generator = GeneratorGroup("documentation", listOf(
                GeneratorSimple("DevDocBase", contextBuilder = docContextBuilder,
                        template = ItemsTemplate(name = "${fileNamePrefix}DevDocBase",
                                nameBuilder = itemNameAsMarkdownFileName,
                                items = compilationUnits, fragments = { listOf(docTemplates.pojoImpl()) })),
                GeneratorSimple("DevPlainUmlBase", contextBuilder = docContextBuilder,
                        template = ItemsTemplate(name = "${fileNamePrefix}DevPlainUmlBase",
                                nameBuilder = templateNameAsMarkdownFileName,
                                items = compilationUnits, fragments = { listOf(docTemplates.pojoPlainImplWithComments()) })),

                GeneratorSimple("DevPlainUmlsuper", contextBuilder = docContextBuilder,
                        template = ItemsTemplate(name = "${fileNamePrefix}DevPlainUmlsuper",
                                nameBuilder = templateNameAsMarkdownFileName,
                                items = compilationUnits, fragments = { listOf(docTemplates.pojoPlainSuperClass()) })),


                GeneratorSimple("DevPlainUmlClass", contextBuilder = docContextBuilder,
                        template = ItemsTemplate(name = "${fileNamePrefix}DevPlainUmlClass",
                                nameBuilder = templateNameAsMarkdownFileName,
                                items = compilationUnits, fragments = { listOf(docTemplates.pojoPlainImplClass()) }))))
        return GeneratorContexts(generator, docContextBuilder)
    }

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
                            listOf(ItemsFragment(items = enums,
                                    fragments = { listOf(ktTemplates.enum()) }),
                                    ItemsFragment(items = compilationUnits, fragments = {
                                        listOf(ktTemplates.pojo(itemNameAsKotlinFileName))
                                    }))
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
                        template = FragmentsTemplate<StructureUnitI<*>>(name = "${fileNamePrefix}ApiBase",
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
                        template = FragmentsTemplate<StructureUnitI<*>>(name = "${fileNamePrefix}ApiBase",
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

    open fun pojoGo(fileNamePrefix: String = ""): GeneratorContexts<StructureUnitI<*>> {
        val goTemplates = buildGoTemplates()
        val goContextFactory = buildGoContextFactory()
        val goContextBuilder = goContextFactory.buildForImplOnly()

        val enums: StructureUnitI<*>.() -> List<EnumTypeI<*>> = {
            findDownByType(EnumTypeI::class.java).filter {
                it.parent() is StructureUnitI<*> || it.derivedAsType().isNotEmpty()
            }
        }
        val compilationUnits: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { it !is EnumTypeI<*> }
        }

        val generator = GeneratorGroup("pojoGo", listOf(GeneratorSimple("ApiBase", contextBuilder = goContextBuilder,
                template = FragmentsTemplate<StructureUnitI<*>>(name = "${fileNamePrefix}ApiBase",
                        nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                    listOf(ItemsFragment(items = enums, fragments = { listOf(goTemplates.enum()) }),
                            ItemsFragment(items = compilationUnits, fragments = { listOf(goTemplates.pojo()) }))
                }))))
        return GeneratorContexts(generator, goContextBuilder)
    }

    protected open fun buildKotlinContextFactory() = LangKotlinContextFactory()
    protected open fun buildKotlinTemplates() = LangKotlinTemplates(itemNameAsKotlinFileName)

    protected open fun buildGoContextFactory() = LangGoContextFactory()
    protected open fun buildGoTemplates() = LangGoTemplates(itemNameAsGoFileName)

    protected open fun buildTsContextFactory() = LangTsContextFactory()
    protected open fun buildTsTemplates() = LangTsTemplates()

    protected open fun buildSwaggerContextFactory() = LangCommonContextFactory()

    protected open fun buildDocContextFactory() = LangMarkdownContextFactory()
    protected open fun buildDocTemplates() = LangMarkdownTemplates(itemNameAsMarkdownFileName)


    protected fun registerKtMacros(contextFactory: LangCommonContextFactory) {
        val macros = contextFactory.macroController
        macros.registerMacro(TypeI<*>::toKotlinInstanceEMPTY.name, TypeI<*>::toKotlinInstanceEMPTY)
        macros.registerMacro(TypeI<*>::toKotlinInstanceDotEMPTY.name, TypeI<*>::toKotlinInstanceDotEMPTY)
    }

    companion object {
        const val CONTEXT_KOTLIN = "kotlin"
        const val CONTEXT_GO = "go"
    }
}
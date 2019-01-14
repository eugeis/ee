package ee.lang.gen

import ee.lang.*
import ee.lang.gen.common.LangCommonContextFactory
import ee.lang.gen.go.LangGoContextFactory
import ee.lang.gen.go.LangGoTemplates
import ee.lang.gen.go.itemAndTemplateNameAsGoFileName
import ee.lang.gen.go.itemNameAsGoFileName
import ee.lang.gen.kt.LangKotlinContextFactory
import ee.lang.gen.kt.LangKotlinTemplates
import ee.lang.gen.ts.LangTsContextFactory
import ee.lang.gen.ts.LangTsTemplates

open class LangGeneratorFactory {
    protected val singleModule: Boolean

    constructor(singleModule: Boolean = true) {
        this.singleModule = singleModule
    }

    open fun dslKt(fileNamePrefix: String = ""): GeneratorGroupI<StructureUnitI<*>> {
        val kotlinTemplates = buildKotlinTemplates()
        val contextBuilder = buildKotlinContextFactory().buildForDslBuilder()
        val compilationUnits: StructureUnitI<*>.() -> List<CompilationUnitI<*>> =
                { items().filterIsInstance(CompilationUnitI::class.java) }

        return GeneratorGroup("dslKt", listOf(
                GeneratorSimple("IfcBase", contextBuilder = contextBuilder,
                        template = ItemsTemplate(name = "${fileNamePrefix}IfcBase", nameBuilder = templateNameAsKotlinFileName,
                                items = compilationUnits, fragments = { listOf(kotlinTemplates.dslBuilderI()) })),
                GeneratorSimple("ApiBase", contextBuilder = contextBuilder,
                        template = ItemsTemplate(name = "${fileNamePrefix}ApiBase", nameBuilder = templateNameAsKotlinFileName,
                                items = compilationUnits, fragments = { listOf(kotlinTemplates.dslBuilder()) }))))
    }

    open fun pojoKt(fileNamePrefix: String = ""): GeneratorGroupI<StructureUnitI<*>> {
        val kotlinTemplates = buildKotlinTemplates()
        val contextBuilder = buildKotlinContextFactory().buildForImplOnly()
        val enums: StructureUnitI<*>.() -> List<EnumTypeI<*>> = { findDownByType(EnumTypeI::class.java) }
        val compilationUnits: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { it !is EnumTypeI<*> }
        }

        return GeneratorGroup("pojoKt", listOf(GeneratorSimple("ApiBase", contextBuilder = contextBuilder,
                template = FragmentsTemplate<StructureUnitI<*>>(name = "${fileNamePrefix}ApiBase",
                        nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                    listOf(ItemsFragment(items = enums,
                            fragments = { listOf(kotlinTemplates.enum(), kotlinTemplates.enumParseMethod()) }),
                            ItemsFragment(items = compilationUnits, fragments = { listOf(kotlinTemplates.pojo()) }))
                }))))
    }

    open fun pojoAndBuildersKt(fileNamePrefix: String = ""): GeneratorGroupI<StructureUnitI<*>> {
        val kotlinTemplates = buildKotlinTemplates()
        val contextBuilderTest = buildKotlinContextFactory().buildForImplOnly("test")
        val contextBuilder = buildKotlinContextFactory().buildForImplOnly()
        val enums: StructureUnitI<*>.() -> List<EnumTypeI<*>> = { findDownByType(EnumTypeI::class.java) }
        val compilationUnits: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { it !is EnumTypeI<*> && !it.isIfc() }
        }
        val interfaces: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { it.isIfc() }
        }
        val dataTypes: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(DataTypeI::class.java).filter { it !is EnumTypeI<*> && !it.isIfc() }
        }

        return GeneratorGroup("pojoAndBuildersKt", listOf(
                GeneratorSimple("IfcBase", contextBuilder = contextBuilder,
                        template = FragmentsTemplate(name = "${fileNamePrefix}IfcBase",
                                nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                            listOf(ItemsFragment(items = interfaces, fragments = { listOf(kotlinTemplates.ifc()) }))
                        })),
                GeneratorSimple("IfcEmpty", contextBuilder = contextBuilder,
                        template = FragmentsTemplate(name = "${fileNamePrefix}IfcEmpty",
                                nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                            listOf(ItemsFragment(items = interfaces, fragments = { listOf(kotlinTemplates.ifcEmpty()) }))
                        })),
                GeneratorSimple("ApiBase", contextBuilder = contextBuilder,
                        template = FragmentsTemplate<StructureUnitI<*>>(name = "${fileNamePrefix}ApiBase",
                                nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                            listOf(ItemsFragment(items = enums,
                                    fragments = { listOf(kotlinTemplates.enum(), kotlinTemplates.enumParseMethod()) }),
                                    ItemsFragment(items = compilationUnits, fragments = { listOf(kotlinTemplates.pojo()) }))
                        })),
                GeneratorSimple("BuilderIfcBase", contextBuilder = contextBuilder,
                        template = ItemsTemplate(name = "${fileNamePrefix}BuilderIfcBase",
                                nameBuilder = itemAndTemplateNameAsKotlinFileName,
                                items = dataTypes, fragments = { listOf(kotlinTemplates.builderI(itemNameAsKotlinFileName)) })),
                GeneratorSimple("BuilderApiBase", contextBuilder = contextBuilder,
                        template = ItemsTemplate(name = "${fileNamePrefix}BuilderApiBase",
                                nameBuilder = itemAndTemplateNameAsKotlinFileName,
                                items = dataTypes, fragments = { listOf(kotlinTemplates.builder(itemNameAsKotlinFileName)) })),
                GeneratorSimple("ApiTestEnumsBase", contextBuilder = contextBuilderTest,
                        template = FragmentsTemplate(name = "${fileNamePrefix}ApiTestEnumsBase",
                                nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                            listOf(ItemsFragment(items = enums,
                                    fragments = {
                                        listOf(kotlinTemplates.enumParseAndIsMethodsTestsParseMethodTests())
                                    }))
                        })),
                GeneratorSimple("ApiTestBase", contextBuilder = contextBuilderTest,
                        template = FragmentsTemplate(name = "${fileNamePrefix}ApiTestBase",
                                nameBuilder = itemAndTemplateNameAsKotlinFileName, fragments = {
                            listOf(ItemsFragment(items = compilationUnits,
                                    fragments = {
                                        listOf(kotlinTemplates.pojoTest())
                                    }))
                        }))
        ))
    }

    open fun pojoGo(fileNamePrefix: String = ""): GeneratorGroupI<StructureUnitI<*>> {
        val goTemplates = buildGoTemplates()
        val contextBuilder = buildGoContextFactory().buildForImplOnly()
        val enums: StructureUnitI<*>.() -> List<EnumTypeI<*>> = {
            findDownByType(EnumTypeI::class.java).filter {
                it.parent() is StructureUnitI<*> || it.derivedAsType().isNotEmpty()
            }
        }
        val compilationUnits: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { it !is EnumTypeI<*> }
        }

        return GeneratorGroup("pojoGo", listOf(GeneratorSimple("ApiBase", contextBuilder = contextBuilder,
                template = FragmentsTemplate<StructureUnitI<*>>(name = "${fileNamePrefix}ApiBase",
                        nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                    listOf(ItemsFragment(items = enums, fragments = { listOf(goTemplates.enum()) }),
                            ItemsFragment(items = compilationUnits, fragments = { listOf(goTemplates.pojo()) }))
                }))))
    }

    protected open fun buildKotlinContextFactory() = LangKotlinContextFactory()
    protected open fun buildKotlinTemplates() = LangKotlinTemplates(itemNameAsKotlinFileName)

    protected open fun buildGoContextFactory() = LangGoContextFactory()
    protected open fun buildGoTemplates() = LangGoTemplates(itemNameAsGoFileName)

    protected open fun buildTsContextFactory() = LangTsContextFactory()
    protected open fun buildTsTemplates() = LangTsTemplates()

    protected open fun buildSwaggerContextFactory() = LangCommonContextFactory()
}
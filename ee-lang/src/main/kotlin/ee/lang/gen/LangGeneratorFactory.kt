package ee.lang.gen

import ee.lang.*
import ee.lang.gen.go.LangGoContextFactory
import ee.lang.gen.go.LangGoTemplates
import ee.lang.gen.go.itemAndTemplateNameAsGoFileName
import ee.lang.gen.kt.LangKotlinContextFactory
import ee.lang.gen.kt.LangKotlinTemplates

open class LangGeneratorFactory {
    open fun dslKt(fileNamePrefix: String = ""): GeneratorI<StructureUnitI> {
        val kotlinTemplates = buildKotlinTemplates()
        val contextBuilder = buildKotlinContextFactory().buildForDslBuilder()
        val composites: StructureUnitI.() -> List<CompilationUnitI> = { items().filterIsInstance(CompilationUnitI::class.java) }

        return GeneratorGroup<StructureUnitI>(listOf(

                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = ItemsTemplate<StructureUnitI, CompilationUnitI>(
                        name = "${fileNamePrefix}IfcBase", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, fragments = { listOf(kotlinTemplates.dslBuilderI()) })
                ),
                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = ItemsTemplate<StructureUnitI, CompilationUnitI>(
                        name = "${fileNamePrefix}ApiBase", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, fragments = { listOf(kotlinTemplates.dslBuilder()) })
                )
        ))
    }

    open fun pojoKt(fileNamePrefix: String = ""): GeneratorI<StructureUnitI> {
        val kotlinTemplates = buildKotlinTemplates()
        val contextBuilder = buildKotlinContextFactory().buildForImplOnly()
        val enums: StructureUnitI.() -> List<EnumTypeI> = { findDownByType(EnumTypeI::class.java) }
        val compilationUnits: StructureUnitI.() -> List<CompilationUnitI> = {
            findDownByType(CompilationUnitI::class.java).filter { it !is EnumTypeI }
        }

        return GeneratorGroup<StructureUnitI>(listOf(
                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                        name = "${fileNamePrefix}ApiBase", nameBuilder = itemAndTemplateNameAsKotlinFileName,
                        fragments = {
                            listOf(
                                    ItemsFragment<StructureUnitI, EnumTypeI>(items = enums,
                                            fragments = { listOf(kotlinTemplates.enum(), kotlinTemplates.enumParseMethod()) }),
                                    ItemsFragment<StructureUnitI, CompilationUnitI>(items = compilationUnits,
                                            fragments = { listOf(kotlinTemplates.pojo()) }))
                        })
                )
        ))
    }

    open fun pojoGo(fileNamePrefix: String = ""): GeneratorI<StructureUnitI> {
        val goTemplates = buildGoTemplates()
        val contextBuilder = buildGoContextFactory().buildForImplOnly()
        val enums: StructureUnitI.() -> List<EnumTypeI> = { findDownByType(EnumTypeI::class.java) }
        val compilationUnits: StructureUnitI.() -> List<CompilationUnitI> = {
            findDownByType(CompilationUnitI::class.java).filter { it !is EnumTypeI }
        }

        return GeneratorGroup<StructureUnitI>(listOf(
                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                        name = "${fileNamePrefix}ApiBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                        fragments = {
                            listOf(
                                    ItemsFragment<StructureUnitI, EnumTypeI>(items = enums,
                                            fragments = { listOf(goTemplates.enum()) }),
                                    ItemsFragment<StructureUnitI, CompilationUnitI>(items = compilationUnits,
                                            fragments = { listOf(goTemplates.pojo()) }),
                                    ItemsFragment<StructureUnitI, CompilationUnitI>(items = compilationUnits,
                                            fragments = { listOf(goTemplates.pojo()) }))
                        })
                )
        ))
    }

    protected open fun buildKotlinContextFactory() = LangKotlinContextFactory()

    protected open fun buildKotlinTemplates() = LangKotlinTemplates({ Names("${it.name()}.kt") })

    protected open fun buildGoContextFactory() = LangGoContextFactory()

    protected open fun buildGoTemplates() = LangGoTemplates({ Names("${it.name()}.go") })
}
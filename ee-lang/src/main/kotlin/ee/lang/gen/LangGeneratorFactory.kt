package ee.lang.gen

import ee.lang.*

open class LangGeneratorFactory {
    val kotlinTemplates = buildKotlinTemplates()

    open fun dsl(fileNamePrefix: String = ""): GeneratorI<StructureUnitI> {
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
                        items = composites, fragments = { listOf(kotlinTemplates.dslBuilder(), kotlinTemplates.isEmptyExt()) })
                ),
                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = ItemsTemplate<StructureUnitI, CompilationUnitI>(
                        name = "${fileNamePrefix}Composites", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, fragments = { listOf(kotlinTemplates.dslComposite()) })
                )
        ))
    }

    open fun pojo(fileNamePrefix: String = ""): GeneratorI<StructureUnitI> {
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
                            listOf(ItemsFragment<StructureUnitI, EnumTypeI>(items = enums,
                                    fragments = { listOf(kotlinTemplates.enum(), kotlinTemplates.enumParseMethod()) }),
                                    ItemsFragment<StructureUnitI, CompilationUnitI>(items = compilationUnits,
                                            fragments = { listOf(kotlinTemplates.pojo()) }))
                        })
                )
        ))
    }

    protected open fun buildKotlinContextFactory() = LangKotlinContextFactory()

    protected open fun buildKotlinTemplates() = LangKotlinTemplates({ Names("${it.name()}.kt") })
}
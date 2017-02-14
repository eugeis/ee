package ee.lang.gen

import ee.lang.*

open class LangGeneratorFactory {
    val kotlinTemplates = buildKotlinTemplates()

    open fun dsl(fileNamePrefix: String = ""): GeneratorI<StructureUnitI> {
        val contextBuilder = buildKotlinContextFactory().buildForDslBuilder()
        val composites: StructureUnitI.() -> List<CompilationUnitI> = { items().filterIsInstance(CompilationUnitI::class.java) }

        return GeneratorGroup<StructureUnitI>(listOf(

                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = TemplatesForSameFilename<StructureUnitI, CompilationUnitI>(
                        name = "${fileNamePrefix}IfcBase", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, templates = { listOf(kotlinTemplates.dslBuilderI()) })
                ),
                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = TemplatesForSameFilename<StructureUnitI, CompilationUnitI>(
                        name = "${fileNamePrefix}ApiBase", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, templates = { listOf(kotlinTemplates.dslBuilder(), kotlinTemplates.isEmptyExt()) })
                ),
                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = TemplatesForSameFilename<StructureUnitI, CompilationUnitI>(
                        name = "${fileNamePrefix}Composites", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, templates = { listOf(kotlinTemplates.dslComposite()) })
                )
        ))
    }

    open fun pojo(fileNamePrefix: String = ""): GeneratorI<StructureUnitI> {
        val contextBuilder = buildKotlinContextFactory().buildForImplOnly()
        val composites: StructureUnitI.() -> List<CompilationUnitI> = { items().filterIsInstance(CompilationUnitI::class.java) }
        val enums: StructureUnitI.() -> List<EnumTypeI> = { items().filterIsInstance(EnumTypeI::class.java) }

        return GeneratorGroup<StructureUnitI>(listOf(

                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = TemplatesForSameFilename<StructureUnitI, CompilationUnitI>(
                        name = "${fileNamePrefix}IfcBase", nameBuilder = itemAndTemplateNameAsKotlinFileName,
                        items = composites, templates = { listOf(kotlinTemplates.dslBuilderI()) })
                ),
                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = TemplatesForSameFilename<StructureUnitI, EnumTypeI>(
                        name = "${fileNamePrefix}ApiBase", nameBuilder = itemAndTemplateNameAsKotlinFileName,
                        items = enums, templates = { listOf(kotlinTemplates.enum(), kotlinTemplates.enumParseMethod()) })
                )
        ))
    }

    protected open fun buildKotlinContextFactory() = LangKotlinContextFactory()

    protected open fun buildKotlinTemplates() = LangKotlinTemplates({ Names("${it.name()}.kt") })
}
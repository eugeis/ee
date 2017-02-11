package ee.lang.gen

import ee.lang.*

open class LangGeneratorFactory {
    val namespace: String
    val module: String
    val kotlinTemplates = buildKotlinTemplates()

    //interfaces
    constructor(namespace: String, module: String) {
        this.namespace = namespace
        this.module = module
    }

    open fun dsl(fileNamePrefix: String = ""): GeneratorI<StructureUnitI> {
        val genFolder = "src-gen/main/kotlin"
        var context = KotlinContextFactory.buildForDslBuilder(namespace)
        val composites: StructureUnitI.() -> List<CompilationUnitI> = { items().filterIsInstance(CompilationUnitI::class.java) }

        return GeneratorGroup<StructureUnitI>(listOf(

                GeneratorSimple<StructureUnitI>(
                        moduleFolder = module, genFolder = genFolder, deleteGenFolder = true,
                        context = context, template = TemplatesForSameFilename<StructureUnitI, CompilationUnitI>(
                        name = "${fileNamePrefix}IfcBase", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, templates = { listOf(kotlinTemplates.dslBuilderI()) })
                ),
                GeneratorSimple<StructureUnitI>(
                        moduleFolder = module, genFolder = genFolder,
                        context = context, template = TemplatesForSameFilename<StructureUnitI, CompilationUnitI>(
                        name = "${fileNamePrefix}ApiBase", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, templates = { listOf(kotlinTemplates.dslBuilder(), kotlinTemplates.isEmptyExt()) })
                ),
                GeneratorSimple<StructureUnitI>(
                        moduleFolder = module, genFolder = genFolder,
                        context = context, template = TemplatesForSameFilename<StructureUnitI, CompilationUnitI>(
                        name = "${fileNamePrefix}Composites", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, templates = { listOf(kotlinTemplates.dslComposite()) })
                )
        ))
    }

    open fun pojo(fileNamePrefix: String = ""): GeneratorI<StructureUnitI> {
        val genFolder = "src-gen/main/kotlin"
        var context = KotlinContextFactory.buildForDslBuilder(namespace)
        val composites: StructureUnitI.() -> List<CompilationUnitI> = { items().filterIsInstance(CompilationUnitI::class.java) }
        val enums: StructureUnitI.() -> List<EnumTypeI> = { items().filterIsInstance(EnumTypeI::class.java) }

        return GeneratorGroup<StructureUnitI>(listOf(

                GeneratorSimple<StructureUnitI>(
                        moduleFolder = module, genFolder = genFolder, deleteGenFolder = true,
                        context = context, template = TemplatesForSameFilename<StructureUnitI, CompilationUnitI>(
                        name = "${fileNamePrefix}IfcBase", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, templates = { listOf(kotlinTemplates.dslBuilderI()) })
                ),
                GeneratorSimple<StructureUnitI>(
                        moduleFolder = module, genFolder = genFolder,
                        context = context, template = TemplatesForSameFilename<StructureUnitI, EnumTypeI>(
                        name = "${fileNamePrefix}ApiBase", nameBuilder = templateNameAsKotlinFileName,
                        items = enums, templates = { listOf(kotlinTemplates.enum(), kotlinTemplates.enumParseMethod()) })
                )
        ))
    }

    protected open fun buildKotlinTemplates() = LangKotlinTemplates({ Names("${it.name()}.kt") })

}
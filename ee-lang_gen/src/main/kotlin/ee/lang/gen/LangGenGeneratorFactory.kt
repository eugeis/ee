package ee.lang.gen

import ee.lang.*

open class LangGenGeneratorFactory {
    val namespace: String
    val module: String
    val kotlinTemplates = LangGenKotlinTemplates(itemNameAsKotlinFileName)

    //interfaces
    constructor(namespace: String, module: String) {
        this.namespace = namespace
        this.module = module
    }

    fun dsl(fileNamePrefix: String = ""): GeneratorI<CompositeI> {
        val genFolder = "src-gen/main/kotlin"
        var context = KotlinContextFactory.buildForDslBuilder(namespace)
        val composites: CompositeI.() -> List<CompositeI> = { items().filterIsInstance(CompositeI::class.java) }


        return GeneratorGroup<CompositeI>(listOf(
                GeneratorSimple<CompositeI>(
                        moduleFolder = module, genFolder = genFolder, deleteGenFolder = true,
                        context = context, template = TemplatesForSameFilename<CompositeI, CompositeI>(
                        name = "${fileNamePrefix}IfcBase", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, templates = { listOf(kotlinTemplates.dslBuilderI()) })
                ),
                GeneratorSimple<CompositeI>(
                        moduleFolder = module, genFolder = genFolder,
                        context = context, template = TemplatesForSameFilename<CompositeI, CompositeI>(
                        name = "${fileNamePrefix}ApiBase", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, templates = { listOf(kotlinTemplates.dslBuilder(), kotlinTemplates.isEmptyExt()) })
                ),
                GeneratorSimple<CompositeI>(
                        moduleFolder = module, genFolder = genFolder,
                        context = context, template = TemplatesForSameFilename<CompositeI, CompositeI>(
                        name = "${fileNamePrefix}Composites", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, templates = { listOf(kotlinTemplates.dslComposite()) })
                ),
                GeneratorSimple<CompositeI>(
                        moduleFolder = module, genFolder = genFolder,
                        context = context, template = kotlinTemplates.dslObjectTree()
                )
        ))
    }

}
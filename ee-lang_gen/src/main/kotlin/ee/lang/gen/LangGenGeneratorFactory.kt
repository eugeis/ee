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

    fun dsl(fileNamePrefix: String = ""): GeneratorI<CompositeI<*>> {
        val contextBuilder = KotlinContextFactory().buildForDslBuilder(namespace = namespace, moduleFolder = module)
        val composites: CompositeI<*>.() -> List<CompositeI<*>> = { items().filterIsInstance(CompositeI::class.java) }

        return GeneratorGroup("dsl", listOf(GeneratorSimple("IfcBase", contextBuilder = contextBuilder,
                template = ItemsTemplate(name = "${fileNamePrefix}IfcBase", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, fragments = { listOf(kotlinTemplates.dslBuilderI()) })),
                GeneratorSimple("ApiBase", contextBuilder = contextBuilder,
                        template = ItemsTemplate(name = "${fileNamePrefix}ApiBase", nameBuilder = templateNameAsKotlinFileName,
                                items = composites, fragments = { listOf(kotlinTemplates.dslBuilder()) })),
                GeneratorSimple("ObjectTree", contextBuilder = contextBuilder, template = kotlinTemplates.dslObjectTree())))
    }

}
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

    fun dsl(fileNamePrefix: String = ""): GeneratorContexts<CompositeI<*>> {
        val ktContextBuilder = KotlinContextFactory().buildForDslBuilder(namespace = namespace, moduleFolder = module)
        val composites: CompositeI<*>.() -> List<CompositeI<*>> = { items().filterIsInstance(CompositeI::class.java) }

        val generator = GeneratorGroup("dsl", listOf(GeneratorSimple("IfcBase", contextBuilder = ktContextBuilder,
                template = ItemsTemplate(name = "${fileNamePrefix}IfcBase", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, fragments = { listOf(kotlinTemplates.dslBuilderI()) })),
                GeneratorSimple("ApiBase", contextBuilder = ktContextBuilder,
                        template = ItemsTemplate(name = "${fileNamePrefix}ApiBase", nameBuilder = templateNameAsKotlinFileName,
                                items = composites, fragments = { listOf(kotlinTemplates.dslBuilder()) })),
                GeneratorSimple("ObjectTree", contextBuilder = ktContextBuilder, template = kotlinTemplates.dslObjectTree())))
        return GeneratorContexts(generator, ktContextBuilder)
    }

}
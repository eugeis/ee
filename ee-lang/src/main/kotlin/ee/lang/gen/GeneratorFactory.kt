package ee.lang.gen

import ee.lang.*

open class GeneratorFactory {
    val namespace: String
    val module: String
    val kotlinTemplates = KotlinTemplates({ Names("${it.name()}.kt") })

    //interfaces

    constructor(namespace: String, module: String) {
        this.namespace = namespace
        this.module = module
    }

    fun dsl(fileNamePrefix: String = ""): GeneratorI<StructureUnitI> {
        val genFolder = "src-gen/main/kotlin"
        var context = KotlinContextFactory.buildForDslBuilder(namespace)
        val composites: StructureUnitI.() -> List<CompilationUnitI> = { items().filterIsInstance(CompilationUnitI::class.java) }

        return GeneratorGroup<StructureUnitI>(listOf(
                Generator<StructureUnitI, CompilationUnitI>(
                        moduleFolder = module, genFolder = genFolder, deleteGenFolder = true,
                        context = context,
                        items = composites, templates = { listOf(kotlinTemplates.dslBuilderI()) },
                        fileName = "${fileNamePrefix}IfcBase.kt"
                ),
                Generator<StructureUnitI, CompilationUnitI>(
                        moduleFolder = module, genFolder = genFolder, deleteGenFolder = false,
                        context = context,
                        items = composites, templates = { listOf(kotlinTemplates.dslBuilder(), kotlinTemplates.isEmptyExt()) },
                        fileName = "${fileNamePrefix}ApiBase.kt"
                ),
                Generator<StructureUnitI, CompilationUnitI>(
                        moduleFolder = module, genFolder = genFolder, deleteGenFolder = false,
                        context = context,
                        items = composites, templates = { listOf(kotlinTemplates.dslComposite()) },
                        fileName = "${fileNamePrefix}Composites.kt"
                )
        ))
    }

}
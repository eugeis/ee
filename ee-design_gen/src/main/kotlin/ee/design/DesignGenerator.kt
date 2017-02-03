package ee.design

import ee.lang.*
import ee.lang.integ.eePath
import ee.lang.gen.KotlinContext
import ee.lang.gen.kt.*
import java.nio.file.Path

fun main(args: Array<String>) {
    generate(eePath)
}

private fun generate(target: Path) {
    var model = prepareModel()

    val nameBuilder: Template<CompilationUnitI>.(CompilationUnitI) -> NamesI = { Names("${it.name()}.kt") }

    //interfaces
    var context = KotlinContextFactory.buildForDslBuilder(model.namespace())

    var generator = Generator<StructureUnitI, CompilationUnitI>(
            moduleFolder = model.artifact(), genFolder = "src-gen/main/kotlin", deleteGenFolder = true,
            context = context,
            items = { items().filterIsInstance(CompilationUnitI::class.java) }, templates = { templatesApiBase(nameBuilder) },
            fileName = "${model.name()}IfcBase.kt"
    )
    generator.generate(target, model)

    context.clear()
    generator = Generator<StructureUnitI, CompilationUnitI>(
            moduleFolder = model.artifact(), genFolder = "src-gen/main/kotlin", deleteGenFolder = false,
            context = context,
            items = { items().filterIsInstance(CompilationUnitI::class.java) }, templates = { templatesImplBase(nameBuilder) },
            fileName = "${model.name()}ApiBase.kt"
    )
    generator.generate(target, model)

    context.clear()
    generator = Generator<StructureUnitI, CompilationUnitI>(
            moduleFolder = model.artifact(), genFolder = "src-gen/main/kotlin", deleteGenFolder = false,
            context = context,
            items = { items().filterIsInstance(CompilationUnitI::class.java) }, templates = { templatesComposites(nameBuilder) },
            fileName = "${model.name()}Composites.kt"
    )
    generator.generate(target, model)
}

private fun prepareModel(): StructureUnitI {
    n.initObjectTree()
    l.initObjectTree()
    //println(l.render())
    val ret = d.initObjectTree()
    ret.sortByName()
    //println(ret.render())
    return ret
}

private fun templatesApiBase(nameBuilder: Template<CompilationUnitI>.(CompilationUnitI) -> NamesI) =
        listOf(Template<CompilationUnitI>("DslBuilderI", nameBuilder) { item, c ->
            item.toKotlinDslBuilderI(c)
        })

private fun templatesImplBase(nameBuilder: Template<CompilationUnitI>.(CompilationUnitI) -> NamesI) =
        listOf(Template<CompilationUnitI>("DslBuilder", nameBuilder) { item, c ->
            item.toKotlinDslBuilder(c)
        }, Template<CompilationUnitI>("IsEmptyExt", nameBuilder) { item, c ->
            item.toKotlinIsEmptyExt(c)
        })

private fun templatesComposites(nameBuilder: Template<CompilationUnitI>.(CompilationUnitI) -> NamesI) =
        listOf(Template<CompilationUnitI>("DslComposite", nameBuilder) { item, c ->
            item.toKotlinDslComposite(c)
        })
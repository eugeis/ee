package ee.design

import ee.lang.*
import ee.lang.integ.eePath
import ee.lang.gen.kt.toKotlinDslBuilder
import ee.lang.gen.kt.toKotlinDslBuilderI
import ee.lang.gen.kt.toKotlinDslComposite
import ee.lang.gen.kt.toKotlinIsEmptyExt
import ee.lang.gen.KotlinContext
import java.nio.file.Path

fun main(args: Array<String>) {
    generate(eePath)
}

private fun generate(target: Path) {
    var model = prepareModel()

    val nameBuilder: Template<CompilationUnitI>.(CompilationUnitI) -> NamesI = { Names("${it.name()}.kt") }

    //interfaces
    var context = KotlinContext(namespace = model.namespace())
    prepareDerivedController(context)

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

private fun prepareDerivedController(context: KotlinContext) {
    val derivedController = context.derivedController

    val isNotPartOfDslTypes: ItemI.() -> Boolean = { n != this.parent() }
    val isNotPartOfDslModelAndTypes: ItemI.() -> Boolean = {
        l != this.parent() && n != this.parent()
    }

    derivedController.registerKind(DerivedNames.API.name, isNotPartOfDslTypes, { "${name()}I" })
    derivedController.registerKind(DerivedNames.API_BASE.name, isNotPartOfDslTypes, { "${name()}IfcBase" })
    derivedController.registerKind(DerivedNames.IMPL.name, isNotPartOfDslTypes, { name() })
    derivedController.registerKind(DerivedNames.IMPL_BASE.name, isNotPartOfDslTypes, { "${name()}Base" })
    derivedController.registerKind(DerivedNames.COMPOSITE.name, isNotPartOfDslTypes, { "${name()}s" })
    derivedController.registerKind(DerivedNames.DSL_TYPE.name, isNotPartOfDslModelAndTypes, { "ItemTypes.${name()}" })
}

private fun prepareModel(): StructureUnitI {
    n.initObjectTree()
    l.initObjectTree()
    //println(l.render())
    val ret = c.initObjectTree()
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
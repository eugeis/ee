package ee.lang

import ee.lang.gen.*
import ee.lang.integ.eePath
import java.nio.file.Path

fun main(args: Array<String>) {
    generate(eePath)
}

fun generate(target: Path) {
    var model = prepareModel()

    val nameBuilder: Template<CompositeI>.(CompositeI) -> NamesI = { Names("${it.name()}.kt") }

    //interfaces
    var context = KotlinContext(namespace = model.namespace())
    prepareDerivedController(context)

    var generator = Generator<CompositeI, CompositeI>(
            moduleFolder = "ee-lang", genFolder = "src-gen/main/kotlin", deleteGenFolder = true,
            context = context,
            items = { items().filterIsInstance(CompositeI::class.java) }, templates = { templatesApiBase(nameBuilder) },
            fileName = "LangIfcBase.kt"
    )
    generator.generate(target, model)

    context.clear()
    generator = Generator<CompositeI, CompositeI>(
            moduleFolder = "ee-lang", genFolder = "src-gen/main/kotlin", deleteGenFolder = false,
            context = context,
            items = { items().filterIsInstance(CompositeI::class.java) }, templates = { templatesImplBase(nameBuilder) },
            fileName = "LangApiBase.kt"
    )
    generator.generate(target, model)

    context.clear()
    generator = Generator<CompositeI, CompositeI>(
            moduleFolder = "ee-lang", genFolder = "src-gen/main/kotlin", deleteGenFolder = false,
            context = context,
            items = { items().filterIsInstance(CompositeI::class.java) }, templates = { templatesComposites(nameBuilder) },
            fileName = "LangComposites.kt"
    )
    generator.generate(target, model)
    context.clear()
    generator = Generator<CompositeI, CompositeI>(
            moduleFolder = "ee-lang", genFolder = "src-gen/main/kotlin", deleteGenFolder = false,
            context = context,
            items = { listOf(this) }, templates = { templatesObjectTree(nameBuilder) },
            fileName = "l.kt"
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
    derivedController.registerKind(DerivedNames.EMPTY.name, isNotPartOfDslTypes, { "${name()}Empty" })
    derivedController.registerKind(DerivedNames.EMPTY_CLASS.name, isNotPartOfDslTypes, { "${name()}EmptyClass" })
    derivedController.registerKind(DerivedNames.DSL_TYPE.name, isNotPartOfDslModelAndTypes, { "ItemTypes.${name()}" })
}

fun prepareModel(): CompositeI {
    n.initObjectTree()
    val ret = l.initObjectTree()
    ret.sortByName()
    return ret
}

private fun templatesApiBase(nameBuilder: Template<CompositeI>.(CompositeI) -> NamesI) =
        listOf(Template<CompositeI>("BuilderI", nameBuilder) { item, c ->
            item.toKotlinDslBuilderI(c)
        })

private fun templatesImplBase(nameBuilder: Template<CompositeI>.(CompositeI) -> NamesI) =
        listOf(Template<CompositeI>("Builder", nameBuilder) { item, c ->
            item.toKotlinDslBuilder(c)
        }, Template<CompositeI>("IsEmptyExt", nameBuilder) { item, c ->
            item.toKotlinIsEmptyExt(c)
        })

private fun templatesComposites(nameBuilder: Template<CompositeI>.(CompositeI) -> NamesI) =
        listOf(Template<CompositeI>("Composite", nameBuilder) { item, c ->
            item.toKotlinDslComposite(c)
        })

private fun templatesObjectTree(nameBuilder: Template<CompositeI>.(CompositeI) -> NamesI) =
        listOf(Template<CompositeI>("ObjectTree", nameBuilder) { item, c ->
            item.toKotlinDslTypes(c)
        })
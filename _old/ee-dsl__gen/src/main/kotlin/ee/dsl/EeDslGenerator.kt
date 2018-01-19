package ee.design

import ee.design.*
import ee.design.integ.eeKt
import ee.design.lang.KotlinContext
import java.nio.file.Path

fun main(args: Array<String>) {
    generate(eeKt)
}

fun generate(target: Path) {
    val model = prepareModel()

    val nameBuilder: Template<ItemI>.(ItemI) -> NamesI = { Names("${it.name()}.gen") }
    val templates = templates(nameBuilder)

    val generator = Generator<CompositeI, ItemI>(moduleFolder = "ee-dsl/temp", genFolder = "src-gen/main/kotlin",
        deleteGenFolder = true, context = KotlinContext("ee.design"), items = { items() }, templates = { templates },
        fileName = "${model.name()}ApiBase.gen")
    generator.generate(target, model)
}

fun prepareModel(): CompositeI {
    val ret = DslModel.initObjectTree()
    ret.sortByName()
    return ret
}

private fun templates(nameBuilder: Template<ItemI>.(ItemI) -> NamesI) =
    listOf(Template<ItemI>("ApiBase", nameBuilder) { item, c ->
        """
open class ${c.n(item)} : ${c.n(item.derivedFrom())} {
    constructor(init: ${c.n(item)}.() -> Unit = {}) : super() {
        init()
    }
}"""
    })
package ee.design.gen.doc

import ee.design.CompI
import ee.lang.*
import ee.lang.gen.doc.*

open class DesignDocTemplates : LangMarkdownTemplates {
    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI<*>) -> NamesI) : super(defaultNameBuilder)

    open fun <T : CompI<*>> generatePlantUmlClassDiagramComponent(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ClassDiagramComponent", nameBuilder) { item, c -> item.toPlantUmlClassDiagramComp(c, true, moduleComponentName, componentPart(true)) }

}

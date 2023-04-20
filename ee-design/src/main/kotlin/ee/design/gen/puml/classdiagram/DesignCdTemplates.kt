package ee.design.gen.puml.classdiagram

import ee.design.CompI
import ee.lang.*
import ee.lang.gen.puml.classdiagram.LangCdTemplates

open class DesignCdTemplates : LangCdTemplates {
    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI<*>) -> NamesI) : super(defaultNameBuilder)

    open fun <T : CompI<*>> generateCdComponent(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("ClassDiagramComponent", nameBuilder) { item, c -> item.toPumlCdComp(c) }

}

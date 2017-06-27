package ee.design.gen.go

import ee.lang.CompilationUnitI
import ee.lang.NamesI
import ee.lang.TemplateI
import ee.lang.gen.go.LangGoTemplates

open class DesignGoTemplates : LangGoTemplates {
    constructor(defaultNameBuilder: TemplateI<*>.(CompilationUnitI) -> NamesI) : super(defaultNameBuilder)

}
package ee.design.gen

import ee.lang.CompilationUnitI
import ee.lang.NamesI
import ee.lang.TemplateI
import ee.lang.gen.kt.LangKotlinTemplates

open class DesignKotlinTemplates : LangKotlinTemplates {
    constructor(defaultNameBuilder: TemplateI<*>.(CompilationUnitI) -> NamesI) : super(defaultNameBuilder)

}
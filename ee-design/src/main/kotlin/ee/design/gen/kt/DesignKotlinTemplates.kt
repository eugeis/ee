package ee.design.gen.kt

import ee.lang.CompilationUnitIB
import ee.lang.NamesI
import ee.lang.TemplateI
import ee.lang.gen.kt.LangKotlinTemplates

open class DesignKotlinTemplates : LangKotlinTemplates {
    constructor(defaultNameBuilder: TemplateI<*>.(CompilationUnitIB<*>) -> NamesI) : super(defaultNameBuilder)

}
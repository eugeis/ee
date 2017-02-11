package ee.design.gen

import ee.lang.CompilationUnitI
import ee.lang.NamesI
import ee.lang.Template
import ee.lang.gen.LangKotlinTemplates

open class DesignKotlinTemplates : LangKotlinTemplates {
    constructor(defaultNameBuilder: Template<CompilationUnitI>.(CompilationUnitI) -> NamesI) : super(defaultNameBuilder)

}
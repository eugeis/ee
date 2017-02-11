package ee.design.gen

import ee.lang.Names
import ee.lang.gen.LangGeneratorFactory

open class DesignGeneratorFactory : LangGeneratorFactory {
    constructor(namespace: String, module: String) : super(namespace, module)

    override fun buildKotlinTemplates() = DesignKotlinTemplates({ Names("${it.name()}.kt") })
}
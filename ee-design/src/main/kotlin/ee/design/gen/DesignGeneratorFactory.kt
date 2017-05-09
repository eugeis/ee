package ee.design.gen

import ee.design.gen.kt.DesignKotlinContextFactory
import ee.design.gen.kt.DesignKotlinTemplates
import ee.lang.Names
import ee.lang.gen.LangGeneratorFactory
import ee.lang.gen.go.LangGoContextFactory
import ee.lang.gen.go.LangGoTemplates

open class DesignGeneratorFactory : LangGeneratorFactory {
    constructor() : super()

    override fun buildKotlinContextFactory() = DesignKotlinContextFactory()
    override fun buildKotlinTemplates() = DesignKotlinTemplates({ Names("${it.name()}.kt") })

    override fun buildGoContextFactory() = LangGoContextFactory()
    override fun buildGoTemplates() = LangGoTemplates({ Names("${it.name()}.go") })
}
package ee.design.gen

import ee.design.gen.go.DesignGoContextFactory
import ee.design.gen.go.DesignGoTemplates
import ee.design.gen.kt.DesignKotlinContextFactory
import ee.design.gen.kt.DesignKotlinTemplates
import ee.lang.Names
import ee.lang.gen.LangGeneratorFactory
import ee.lang.gen.go.LangGoTemplates

open class DesignGeneratorFactory : LangGeneratorFactory {
    constructor() : super()

    override fun buildKotlinContextFactory() = DesignKotlinContextFactory()
    override fun buildKotlinTemplates() = DesignKotlinTemplates({ Names("${it.name()}.kt") })

    override fun buildGoContextFactory() = DesignGoContextFactory()
    override fun buildGoTemplates() = DesignGoTemplates({ Names("${it.name()}.go") })
}
package ee.lang.gen

import ee.lang.*
import ee.lang.gen.common.LangCommonContextFactory
import ee.lang.gen.go.LangGoContextFactory
import ee.lang.gen.go.LangGoTemplates
import ee.lang.gen.go.itemAndTemplateNameAsGoFileName
import ee.lang.gen.go.itemNameAsGoFileName
import ee.lang.gen.kt.LangKotlinContextFactory
import ee.lang.gen.kt.LangKotlinTemplates
import ee.lang.gen.ts.LangTsContextFactory
import ee.lang.gen.ts.LangTsTemplates

open class LangGeneratorFactory {
    open fun dslKt(fileNamePrefix: String = ""): GeneratorI<StructureUnitIB<*>> {
        val kotlinTemplates = buildKotlinTemplates()
        val contextBuilder = buildKotlinContextFactory().buildForDslBuilder()
        val composites: StructureUnitIB<*>.() -> List<CompilationUnitIB<*>> = { items().filterIsInstance(CompilationUnitIB::class.java) }

        return GeneratorGroup(listOf(

                GeneratorSimple(
                        contextBuilder = contextBuilder, template = ItemsTemplate<StructureUnitIB<*>, CompilationUnitIB<*>>(
                        name = "${fileNamePrefix}IfcBase", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, fragments = { listOf(kotlinTemplates.dslBuilderI()) })
                ),
                GeneratorSimple(
                        contextBuilder = contextBuilder, template = ItemsTemplate<StructureUnitIB<*>, CompilationUnitIB<*>>(
                        name = "${fileNamePrefix}ApiBase", nameBuilder = templateNameAsKotlinFileName,
                        items = composites, fragments = { listOf(kotlinTemplates.dslBuilder()) })
                )
        ))
    }

    open fun pojoKt(fileNamePrefix: String = ""): GeneratorI<StructureUnitIB<*>> {
        val kotlinTemplates = buildKotlinTemplates()
        val contextBuilder = buildKotlinContextFactory().buildForImplOnly()
        val enums: StructureUnitIB<*>.() -> List<EnumTypeIB<*>> = { findDownByType(EnumTypeIB::class.java) }
        val compilationUnits: StructureUnitIB<*>.() -> List<CompilationUnitIB<*>> = {
            findDownByType(CompilationUnitIB::class.java).filter { it !is EnumTypeIB<*> }
        }

        return GeneratorGroup(listOf(
                GeneratorSimple(
                        contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitIB<*>>(
                        name = "${fileNamePrefix}ApiBase", nameBuilder = itemAndTemplateNameAsKotlinFileName,
                        fragments = {
                            listOf(
                                    ItemsFragment<StructureUnitIB<*>, EnumTypeIB<*>>(items = enums,
                                            fragments = { listOf(kotlinTemplates.enum(), kotlinTemplates.enumParseMethod()) }),
                                    ItemsFragment<StructureUnitIB<*>, CompilationUnitIB<*>>(items = compilationUnits,
                                            fragments = { listOf(kotlinTemplates.pojo()) }))
                        })
                )
        ))
    }

    open fun pojoGo(fileNamePrefix: String = ""): GeneratorI<StructureUnitIB<*>> {
        val goTemplates = buildGoTemplates()
        val contextBuilder = buildGoContextFactory().buildForImplOnly()
        val enums: StructureUnitIB<*>.() -> List<EnumTypeIB<*>> = {
            findDownByType(EnumTypeIB::class.java).filter {
                it.parent() is StructureUnitIB<*> || it.derivedAsType().isNotEmpty()
            }
        }
        val compilationUnits: StructureUnitIB<*>.() -> List<CompilationUnitIB<*>> = {
            findDownByType(CompilationUnitIB::class.java).filter { it !is EnumTypeIB<*> }
        }

        return GeneratorGroup<StructureUnitIB<*>>(listOf(
                GeneratorSimple<StructureUnitIB<*>>(
                        contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitIB<*>>(
                        name = "${fileNamePrefix}ApiBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                        fragments = {
                            listOf(
                                    ItemsFragment<StructureUnitIB<*>, EnumTypeIB<*>>(items = enums,
                                            fragments = { listOf(goTemplates.enum()) }),
                                    ItemsFragment<StructureUnitIB<*>, CompilationUnitIB<*>>(items = compilationUnits,
                                            fragments = { listOf(goTemplates.pojo()) }))
                        })
                )
        ))
    }

    protected open fun buildKotlinContextFactory() = LangKotlinContextFactory()
    protected open fun buildKotlinTemplates() = LangKotlinTemplates(itemNameAsKotlinFileName)

    protected open fun buildGoContextFactory() = LangGoContextFactory()
    protected open fun buildGoTemplates() = LangGoTemplates(itemNameAsGoFileName)

    protected open fun buildTsContextFactory() = LangTsContextFactory()
    protected open fun buildTsTemplates() = LangTsTemplates()

    protected open fun buildSwaggerContextFactory() = LangCommonContextFactory()
}
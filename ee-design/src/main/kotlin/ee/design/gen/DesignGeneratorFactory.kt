package ee.design.gen

import ee.design.*
import ee.design.gen.go.DesignGoContextFactory
import ee.design.gen.go.DesignGoTemplates
import ee.design.gen.kt.DesignKotlinContextFactory
import ee.design.gen.kt.DesignKotlinTemplates
import ee.lang.*
import ee.lang.gen.LangGeneratorFactory
import ee.lang.gen.go.itemAndTemplateNameAsGoFileName

open class DesignGeneratorFactory : LangGeneratorFactory {
    constructor() : super()

    override fun buildKotlinContextFactory() = DesignKotlinContextFactory()
    override fun buildKotlinTemplates() = DesignKotlinTemplates({ Names("${it.name()}.kt") })

    override fun buildGoContextFactory() = DesignGoContextFactory()
    override fun buildGoTemplates() = DesignGoTemplates({ Names("${it.name()}.go") })

    open fun eventDrivenGo(fileNamePrefix: String = ""): GeneratorI<StructureUnitI> {
        val goTemplates = buildGoTemplates()

        val contextBuilder = buildGoContextFactory().buildForImplOnly()
        val commandEnums: StructureUnitI.() -> List<EnumTypeI> = {
            findDownByType(EnumTypeI::class.java).filter { it.parent() is Commands }
        }
        val commands: StructureUnitI.() -> List<CommandI> = { findDownByType(CommandI::class.java) }

        val eventEnums: StructureUnitI.() -> List<EnumTypeI> = {
            findDownByType(EnumTypeI::class.java).filter { it.parent() is Events }
        }
        val events: StructureUnitI.() -> List<EventI> = { findDownByType(EventI::class.java) }


        val ehEnums: StructureUnitI.() -> List<EnumTypeI> = {
            findDownByType(EnumTypeI::class.java).filter {
                it.derivedAsType().equals(DesignDerivedType.AGGREGATE, true)
            }.sortedBy { it.name() }
        }

        val ehCompilationUnits: StructureUnitI.() -> List<CompilationUnitI> = {
            findDownByType(CompilationUnitI::class.java).filter {
                it !is EnumTypeI && it.derivedAsType().equals(DesignDerivedType.AGGREGATE, true)
            }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val enums: StructureUnitI.() -> List<EnumTypeI> = {
            findDownByType(EnumTypeI::class.java).filter {
                it.parent() is StructureUnitI && it.derivedAsType().isEmpty()
            }.sortedBy { it.name() }
        }

        val compilationUnits: StructureUnitI.() -> List<CompilationUnitI> = {
            findDownByType(CompilationUnitI::class.java).filter {
                it !is EnumTypeI && it.derivedAsType().isEmpty()
            }.sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        return GeneratorGroup<StructureUnitI>(listOf(
                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                        name = "${fileNamePrefix}ApiBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                        fragments = {
                            listOf(
                                    ItemsFragment<StructureUnitI, CompilationUnitI>(items = compilationUnits,
                                            fragments = { listOf(goTemplates.pojo()) }),
                                    ItemsFragment<StructureUnitI, EnumTypeI>(items = enums,
                                            fragments = { listOf(goTemplates.enum()) })
                            )
                        })
                ),
                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                        name = "${fileNamePrefix}CommandsBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                        fragments = {
                            listOf(
                                    ItemsFragment<StructureUnitI, CommandI>(items = commands,
                                            fragments = { listOf(goTemplates.pojo()) }),
                                    ItemsFragment<StructureUnitI, EnumTypeI>(items = commandEnums,
                                            fragments = { listOf(goTemplates.enum()) }))
                        })
                ),
                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                        name = "${fileNamePrefix}EventsBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                        fragments = {
                            listOf(
                                    ItemsFragment<StructureUnitI, EventI>(items = events,
                                            fragments = { listOf(goTemplates.pojo()) }),
                                    ItemsFragment<StructureUnitI, EnumTypeI>(items = eventEnums,
                                            fragments = { listOf(goTemplates.enum()) })
                            )
                        })
                ),
                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                        name = "${fileNamePrefix}EventhorizonBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                        fragments = {
                            listOf(
                                    ItemsFragment<StructureUnitI, CompilationUnitI>(items = ehCompilationUnits,
                                            fragments = { listOf(goTemplates.pojo()) }),
                                    ItemsFragment<StructureUnitI, EnumTypeI>(items = ehEnums,
                                            fragments = { listOf(goTemplates.enum()) }))
                        })
                )
        ))
    }
}
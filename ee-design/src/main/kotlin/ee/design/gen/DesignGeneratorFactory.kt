package ee.design.gen

import ee.design.Commands
import ee.design.CommandI
import ee.design.EventI
import ee.design.Events
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

        return GeneratorGroup<StructureUnitI>(listOf(
                pojoGo(fileNamePrefix),
                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                        name = "${fileNamePrefix}CommandsBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                        fragments = {
                            listOf(
                                    ItemsFragment<StructureUnitI, EnumTypeI>(items = commandEnums,
                                            fragments = { listOf(goTemplates.enum()) }),
                                    ItemsFragment<StructureUnitI, CommandI>(items = commands,
                                            fragments = { listOf(goTemplates.pojo()) }))
                        })
                ),
                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                        name = "${fileNamePrefix}EventsBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                        fragments = {
                            listOf(
                                    ItemsFragment<StructureUnitI, EnumTypeI>(items = eventEnums,
                                            fragments = { listOf(goTemplates.enum()) }),
                                    ItemsFragment<StructureUnitI, EventI>(items = events,
                                            fragments = { listOf(goTemplates.pojo()) }))
                        })
                ),
                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                        name = "${fileNamePrefix}EventhorizonBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                        fragments = {
                            listOf()
                        })
                )
        ))
    }
}
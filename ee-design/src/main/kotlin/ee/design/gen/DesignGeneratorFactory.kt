package ee.design.gen

import ee.design.*
import ee.design.gen.go.*
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

        val contextFactory = buildGoContextFactory()
        val contextBuilder = contextFactory.buildForImplOnly()

        val commands: StructureUnitI.() -> List<CommandI> = { findDownByType(CommandI::class.java) }
        val commandEnums: StructureUnitI.() -> List<EnumTypeI> = {
            findDownByType(EnumTypeI::class.java).filter { it.parent() is Commands }
        }

        val events: StructureUnitI.() -> List<EventI> = { findDownByType(EventI::class.java) }
        val eventEnums: StructureUnitI.() -> List<EnumTypeI> = {
            findDownByType(EnumTypeI::class.java).filter { it.parent() is Events }
        }

        val ehEnums: StructureUnitI.() -> List<EnumTypeI> = {
            findDownByType(EnumTypeI::class.java).filter {
                it.derivedAsType().equals(DesignDerivedType.Aggregate, true)
            }.sortedBy { it.name() }
        }

        val ehValues: StructureUnitI.() -> List<ValuesI> = {
            findDownByType(ValuesI::class.java).filter { it.derivedAsType().equals(DesignDerivedType.Aggregate, true) }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val ehBasics: StructureUnitI.() -> List<BasicI> = {
            findDownByType(BasicI::class.java).filter { it.derivedAsType().equals(DesignDerivedType.Aggregate, true) }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val ehEntities: StructureUnitI.() -> List<EntityI> = {
            findDownByType(EntityI::class.java).filter { it.derivedAsType().equals(DesignDerivedType.Aggregate, true) }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val ehController: StructureUnitI.() -> List<ControllerI> = {
            findDownByType(ControllerI::class.java).filter { it.derivedAsType().equals(DesignDerivedType.Aggregate, true) }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }


        val enums: StructureUnitI.() -> List<EnumTypeI> = {
            findDownByType(EnumTypeI::class.java).filter {
                it.parent() is StructureUnitI && it.derivedAsType().isEmpty()
            }.sortedBy { it.name() }
        }

        val values: StructureUnitI.() -> List<ValuesI> = {
            findDownByType(ValuesI::class.java).filter { it.derivedAsType().isEmpty() }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val basics: StructureUnitI.() -> List<BasicI> = {
            findDownByType(BasicI::class.java).filter { it.derivedAsType().isEmpty() }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        val entities: StructureUnitI.() -> List<EntityI> = {
            findDownByType(EntityI::class.java).filter { it.derivedAsType().isEmpty() }.
                    sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }

        contextFactory.macroController.registerMacro(OperationI::toGoAggregateInitializerRegisterCommands.name,
                OperationI::toGoAggregateInitializerRegisterCommands)
        contextFactory.macroController.registerMacro(CompilationUnitI::toGoAggregate.name,
                CompilationUnitI::toGoAggregate)
        contextFactory.macroController.registerMacro(CompilationUnitI::toGoAggregateInitializer.name,
                CompilationUnitI::toGoAggregateInitializer)
        contextFactory.macroController.registerMacro(CompilationUnitI::toGoEventhorizonInitializer.name,
                CompilationUnitI::toGoEventhorizonInitializer)

        return GeneratorGroup<StructureUnitI>(listOf(
                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                        name = "${fileNamePrefix}ApiBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                        fragments = {
                            listOf(
                                    ItemsFragment<StructureUnitI, CompilationUnitI>(items = entities,
                                            fragments = { listOf(goTemplates.pojo()) }),
                                    ItemsFragment<StructureUnitI, CompilationUnitI>(items = values,
                                            fragments = { listOf(goTemplates.pojo()) }),
                                    ItemsFragment<StructureUnitI, CompilationUnitI>(items = basics,
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
                                    ItemsFragment<StructureUnitI, EntityI>(items = entities,
                                            fragments = { listOf(goTemplates.commandTypes()) }),
                                    ItemsFragment<StructureUnitI, CommandI>(items = commands,
                                            fragments = { listOf(goTemplates.command()) }),
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
                                    ItemsFragment<StructureUnitI, ControllerI>(items = ehController,
                                            fragments = { listOf(goTemplates.pojo()) }),
                                    ItemsFragment<StructureUnitI, CompilationUnitI>(items = ehValues,
                                            fragments = { listOf(goTemplates.pojo()) }),
                                    ItemsFragment<StructureUnitI, CompilationUnitI>(items = ehBasics,
                                            fragments = { listOf(goTemplates.pojo()) }),
                                    ItemsFragment<StructureUnitI, EnumTypeI>(items = ehEnums,
                                            fragments = { listOf(goTemplates.enum()) }))
                        })
                )
        ))
    }
}
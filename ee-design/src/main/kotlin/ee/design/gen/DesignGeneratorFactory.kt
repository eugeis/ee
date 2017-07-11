package ee.design.gen

import ee.design.CommandControllerI
import ee.design.CommandI
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
        val enums: StructureUnitI.() -> List<EnumTypeI> = {
            findDownByType(EnumTypeI::class.java).filter { it.parent() is CommandControllerI }
        }
        val commands: StructureUnitI.() -> List<CommandI> = {
            findDownByType(CommandI::class.java)
        }

        return GeneratorGroup<StructureUnitI>(listOf(
                pojoGo(fileNamePrefix),
                GeneratorSimple<StructureUnitI>(
                        contextBuilder = contextBuilder, template = FragmentsTemplate<StructureUnitI>(
                        name = "${fileNamePrefix}CommandsBase", nameBuilder = itemAndTemplateNameAsGoFileName,
                        fragments = {
                            listOf(
                                    ItemsFragment<StructureUnitI, EnumTypeI>(items = enums,
                                            fragments = { listOf(goTemplates.enum()) }),
                                    ItemsFragment<StructureUnitI, CommandI>(items = commands,
                                            fragments = { listOf(goTemplates.command()) }))
                        })
                )
        ))
    }
}
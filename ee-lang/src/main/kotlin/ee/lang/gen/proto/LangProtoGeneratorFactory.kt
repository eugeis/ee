package ee.lang.gen.proto

import ee.lang.*

open class LangProtoGeneratorFactory(private val targetAsSingleModule: Boolean = false) {

    open fun pojoProto(fileNamePrefix: String = ""): GeneratorContexts<StructureUnitI<*>> {
        val templates = buildProtoTemplates()
        val contextFactory = buildProtoContextFactory()
        val contextBuilder = contextFactory.buildForImplOnly()

        val enums: StructureUnitI<*>.() -> List<EnumTypeI<*>> = { findDownByType(EnumTypeI::class.java) }
        val compilationUnits: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { it !is EnumTypeI<*> && !it.isIfc() }
        }

        val generator = GeneratorGroup("pojoProto", listOf(
                GeneratorSimple("base", contextBuilder = contextBuilder,
                        template = FragmentsTemplate(name = "${fileNamePrefix}base",
                                nameBuilder = itemAndTemplateNameAsProtoFileName, fragments = {
                            listOf(
                                    ItemsFragment(items = enums, fragments = {
                                        listOf(templates.enum())
                                    }),
                                    ItemsFragment(items = compilationUnits, fragments = {
                                        listOf(templates.pojo(itemNameAsProtoFileName))
                                    }))
                        }))))
        return GeneratorContexts(generator, contextBuilder)
    }

    protected open fun buildProtoContextFactory() = LangProtoContextFactory(targetAsSingleModule)
    protected open fun buildProtoTemplates() = LangProtoTemplates(itemNameAsProtoFileName)

}
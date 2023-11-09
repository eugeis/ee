package ee.lang.gen.go

import ee.lang.*
import ee.lang.gen.common.LangCommonContextFactory
import ee.lang.gen.go.itemNameAsGoFileName
import ee.lang.gen.itemAndTemplateNameAsKotlinFileName
import ee.lang.gen.itemNameAsKotlinFileName
import ee.lang.gen.templateNameAsKotlinFileName
import org.slf4j.LoggerFactory

open class LangGoGenerator(val model: StructureUnitI<*>, private val targetAsSingleModule: Boolean = false) {
    private val log = LoggerFactory.getLogger(javaClass)

    protected open fun buildGoContextFactory() = LangGoContextFactory(targetAsSingleModule)
    protected open fun buildGoTemplates() = LangGoTemplates(itemNameAsGoFileName)

    open fun pojoGo(fileNamePrefix: String = ""): GeneratorContexts<StructureUnitI<*>> {
        val goTemplates = buildGoTemplates()
        val goContextFactory = buildGoContextFactory()
        val goContextBuilder = goContextFactory.buildForImplOnly()

        val enums: StructureUnitI<*>.() -> List<EnumTypeI<*>> = {
            findDownByType(EnumTypeI::class.java).filter {
                it.parent() is StructureUnitI<*> || it.derivedAsType().isNotEmpty()
            }
        }
        val compilationUnits: StructureUnitI<*>.() -> List<CompilationUnitI<*>> = {
            findDownByType(CompilationUnitI::class.java).filter { it !is EnumTypeI<*> }
        }

        val generator = GeneratorGroup("pojoGo", listOf(GeneratorSimple("ApiBase",
            contextBuilder = goContextBuilder, template = FragmentsTemplate(name = "${fileNamePrefix}ApiBase",
                nameBuilder = itemAndTemplateNameAsGoFileName, fragments = {
                    listOf(ItemsFragment(items = enums, fragments = { listOf(goTemplates.enum()) }),
                        ItemsFragment(items = compilationUnits, fragments = { listOf(goTemplates.pojo()) }))
                }))))
        return GeneratorContexts(generator, goContextBuilder)
    }
}
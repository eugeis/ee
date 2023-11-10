package ee.lang.gen.doc

import ee.lang.*
import ee.lang.gen.common.LangCommonContextFactory
import ee.lang.gen.go.itemNameAsGoFileName
import ee.lang.gen.itemAndTemplateNameAsKotlinFileName
import ee.lang.gen.itemNameAsKotlinFileName
import ee.lang.gen.templateNameAsKotlinFileName
import org.slf4j.LoggerFactory

open class LangDocGenerator(val model: StructureUnitI<*>, private val targetAsSingleModule: Boolean = false) {
    private val log = LoggerFactory.getLogger(javaClass)

    open fun devDoc(fileNamePrefix: String = ""): GeneratorContexts<StructureUnitI<*>> {
        val docTemplates = buildDocTemplates()
        val docContextFactory = buildDocContextFactory()
        val docContextBuilder = docContextFactory.build()

        val compilationUnits: StructureUnitI<*>.() -> List<CompilationUnitI<*>> =
            { items().filterIsInstance(CompilationUnitI::class.java) }

        val generator = GeneratorGroup("documentation", listOf(
            GeneratorSimple("DevDocBase", contextBuilder = docContextBuilder,
                template = ItemsTemplate(name = "${fileNamePrefix}DevDocBase",
                    nameBuilder = itemNameAsMarkdownFileName,
                    items = compilationUnits, fragments = { listOf(docTemplates.pojoImpl()) })),
            GeneratorSimple("DevPlainUmlBase", contextBuilder = docContextBuilder,
                template = ItemsTemplate(name = "${fileNamePrefix}DevPlainUmlBase",
                    nameBuilder = templateNameAsMarkdownFileName,
                    items = compilationUnits, fragments = { listOf(docTemplates.pojoPlainImplWithComments()) })),

            GeneratorSimple("DevPlainUmlsuper", contextBuilder = docContextBuilder,
                template = ItemsTemplate(name = "${fileNamePrefix}DevPlainUmlsuper",
                    nameBuilder = templateNameAsMarkdownFileName,
                    items = compilationUnits, fragments = { listOf(docTemplates.pojoPlainSuperClass()) })),


            GeneratorSimple("DevPlainUmlClass", contextBuilder = docContextBuilder,
                template = ItemsTemplate(name = "${fileNamePrefix}DevPlainUmlClass",
                    nameBuilder = templateNameAsMarkdownFileName,
                    items = compilationUnits, fragments = { listOf(docTemplates.pojoPlainImplClass()) }))))
        return GeneratorContexts(generator, docContextBuilder)
    }

    protected open fun buildDocContextFactory() = LangMarkdownContextFactory(targetAsSingleModule)
    protected open fun buildDocTemplates() = LangMarkdownTemplates(itemNameAsMarkdownFileName)

}
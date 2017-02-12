package ee.lang

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import ee.common.ext.*
import java.nio.file.Path
import java.util.*

val tab = "    "
val nL = "\n"

interface GeneratorI<M> {
    fun generate(target: Path, model: M)
}

open class GeneratorGroup<M> : GeneratorI<M> {
    val generators: Collection<GeneratorI<M>>

    constructor(generators: Collection<GeneratorI<M>>) {
        this.generators = generators
    }

    override fun generate(target: Path, model: M) {
        generators.forEach { it.generate(target, model) }
    }
}

abstract class GeneratorBase<M> : GeneratorI<M> {
    val contextBuilder: M.() -> GenerationContext
    var deleteGenFolder: Boolean

    constructor(deleteGenFolder: Boolean = false, contextBuilder: M.() -> GenerationContext) {
        this.deleteGenFolder = deleteGenFolder
        this.contextBuilder = contextBuilder
    }

    protected open fun prepareNamespace(target: Path, context: GenerationContext): Path {
        val folder = target.resolve(context.genFolder)
        if (deleteGenFolder) folder.deleteRecursively()

        val ret = folder.resolve(context.namespace.toDotsAsPath())

        ret.mkdirs()
        return ret
    }
}

open class GeneratorSimple<M> : GeneratorBase<M> {
    val template: TemplateI<M>

    constructor(deleteGenFolder: Boolean = false, contextBuilder: M.() -> GenerationContext, template: TemplateI<M>) :
            super(deleteGenFolder, contextBuilder) {
        this.template = template
    }

    override fun generate(target: Path, model: M) {
        val c = model.contextBuilder()
        val module = target.resolve(c.moduleFolder)
        val metaData = module.loadMetaData()

        val pkg = prepareNamespace(module, c)
        val path = pkg.resolve(template.name(model).fileName)
        val relative = target.relativize(path).toString()
        if (!path.exists() || !metaData.wasModified(relative, path.lastModified())) {
            path.toFile().writeText(c.complete(template.generate(model, c)))
            metaData.track(relative, path.lastModified())
            c.clear()
            metaData.store(target)
        } else {
            println("File exists $path and was modified after generation, skip generation.")
        }
    }
}

open class Generator<M, I> : GeneratorBase<M> {
    val items: M.() -> Collection<I>
    val templates: I.() -> Collection<Template<I>>

    constructor(deleteGenFolder: Boolean = false, context: M.() -> GenerationContext, items: M.() -> Collection<I>,
                templates: I.() -> Collection<Template<I>>) : super(deleteGenFolder, context) {
        this.items = items
        this.templates = templates
    }

    override fun generate(target: Path, model: M) {
        val c = model.contextBuilder()
        val module = target.resolve(c.moduleFolder)
        val metaData = module.loadMetaData()

        model.items().forEach { item ->
            item.templates().forEach { template ->
                val pkg = prepareNamespace(module, c)
                val path = pkg.resolve(template.name(item).fileName)
                val relative = target.relativize(path).toString()
                if (!path.exists() || !metaData.wasModified(relative, path.lastModified())) {
                    path.toFile().writeText(c.complete(template.generate(template, item, c)))
                    metaData.track(relative, path.lastModified())
                    c.clear()
                } else {
                    println("File exists $path and was modified after generation, skip generation.")
                }
            }
        }
        metaData.store(target)
    }
}

interface NamesI {
    val fileName: String
}

open class Names(override val fileName: String) : NamesI

interface TemplateI<I> {
    val name: String
    fun generate(item: I, context: GenerationContext): String
    fun name(item: I): NamesI
}

open class Template<I> : TemplateI<I> {
    override val name: String
    val generate: TemplateI<I>.(I, GenerationContext) -> String
    val nameBuilder: TemplateI<I>.(I) -> NamesI

    constructor(name: String, nameBuilder: TemplateI<I>.(I) -> NamesI,
                generate: TemplateI<I>.(item: I, context: GenerationContext) -> String) {
        this.name = name
        this.nameBuilder = nameBuilder
        this.generate = generate
    }

    override fun generate(item: I, context: GenerationContext): String = generate(this, item, context)

    override fun name(item: I): NamesI {
        return nameBuilder(item)
    }
}

open class TemplatesForSameFilename<M, I> : TemplateI<M> {
    override val name: String
    val items: M.() -> Collection<I>
    val templates: I.() -> Collection<TemplateI<I>>
    val nameBuilder: TemplateI<M>.(M) -> NamesI

    constructor(name: String, items: M.() -> Collection<I>, templates: I.() -> Collection<Template<I>>,
                nameBuilder: TemplateI<M>.(M) -> NamesI) {
        this.name = name
        this.nameBuilder = nameBuilder
        this.items = items
        this.templates = templates
    }

    override fun generate(model: M, context: GenerationContext): String {
        val buffer = StringBuffer()
        model.items().forEach { item ->
            item.templates().forEach { template ->
                buffer.appendln(template.generate(item, context))
                buffer.appendln()
            }
        }
        return buffer.toString()
    }

    override fun name(item: M): NamesI {
        return nameBuilder(this, item)
    }
}


open class GenerationContext : Cloneable {
    var namespace: String

    var moduleFolder: String
    var genFolder: String

    var header: String = ""
    var footer: String = ""

    var derivedController: DerivedController

    val types: MutableSet<ItemI> = hashSetOf()

    constructor(namespace: String = "",
                moduleFolder: String = "", genFolder: String = "",
                derivedController: DerivedController = DerivedController(DerivedStorage<ItemI>())) {
        this.namespace = namespace
        this.moduleFolder = moduleFolder
        this.genFolder = genFolder
        this.derivedController = derivedController
    }

    open fun toHeader(indent: String = ""): String {
        return header.isEmpty().ifElse("", { "$indent$header$nL" })
    }

    open fun toFooter(indent: String = ""): String {
        return footer.isEmpty().ifElse("", { "$indent$footer$nL" })
    }

    open fun complete(content: String, indent: String = ""): String {
        return "${toHeader(indent)}$content${toFooter(indent)}"
    }

    open fun clear(): GenerationContext {
        types.clear()
        return this
    }

    /* Name */
    open fun n(value: Any?, derivedKind: String = ""): String {
        return if (value is ItemI) n(value, derivedKind) else "$value"
    }

    open fun n(item: ItemI, derivedKind: String = ""): String {
        return "${types.addReturn(derivedController.derive(item, derivedKind)).name()}"
    }
}

data class GeneratedFile(val path: String, val date: Date)
class GenerationMetaData(val files: MutableMap<String, GeneratedFile> = hashMapOf()) {

    fun wasModified(path: String, date: Date): Boolean {
        val generated = files[path]
        val ret = generated == null || generated.date.before(date)

        return ret
    }

    fun track(path: String, date: Date) {
        val file = GeneratedFile(path, date)
        files[file.path] = file
    }
}

val mapper: ObjectMapper by lazy { jsonMapper() }
fun Path.loadMetaData(name: String = "ee.json"): GenerationMetaData {
    mkdirs()
    val eeFile = resolve(name)
    val ret: GenerationMetaData = if (eeFile.exists()) mapper.readValue(eeFile.toFile()) else GenerationMetaData()
    return ret
}

fun GenerationMetaData.store(path: Path, name: String = "ee.json") {
    mapper.writeValue(path.resolve(name).toFile(), this)
}

private fun jsonMapper(): ObjectMapper {
    val mapper = ObjectMapper().registerModule(KotlinModule())
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    return mapper
}
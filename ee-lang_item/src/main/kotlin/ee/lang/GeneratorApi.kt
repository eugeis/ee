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

open class Generator<M, I> {
    val facet: String
    val moduleFolder: String
    val genFolder: String
    val deleteGenFolder: Boolean
    val context: GenerationContext
    val items: M.() -> Collection<I>
    val templates: I.() -> Collection<Template<I>>
    val fileName: String?

    constructor(facet: String, moduleFolder: String, genFolder: String, deleteGenFolder: Boolean = false,
                context: GenerationContext, items: M.() -> Collection<I>,
                templates: I.() -> Collection<Template<I>>, fileName: String? = null) {
        this.facet = facet
        this.moduleFolder = moduleFolder
        this.genFolder = genFolder
        this.deleteGenFolder = deleteGenFolder
        this.context = context
        this.items = items
        this.templates = templates
        this.fileName = fileName
    }

    open fun generate(target: Path, model: M) {
        val module = target.resolve(moduleFolder)
        val metaData = module.loadMetaData()

        if (fileName != null) {
            val pkg = prepareNamespace(module, context)
            val path = pkg.resolve(fileName)
            val relative = target.relativize(path).toString()
            if (!path.exists() || !metaData.wasModified(relative, path.lastModified())) {
                val buffer = StringBuffer()
                model.items().forEach { item ->
                    item.templates().forEach { template ->
                        buffer.appendln(template.generate(template, item, context))
                        buffer.appendln()
                    }
                }
                path.toFile().writeText(context.complete(buffer.toString()))
                metaData.track(relative, path.lastModified())
            } else {
                println("File exists $path and was modified after generation, skip generation.")
            }
        } else {
            model.items().forEach { item ->
                item.templates().forEach { template ->
                    val pkg = prepareNamespace(module, context)
                    val path = pkg.resolve(template.name(item).fileName)
                    val relative = target.relativize(path).toString()
                    if (!path.exists() || !metaData.wasModified(relative, path.lastModified())) {
                        path.toFile().writeText(context.complete(template.generate(template, item, context)))
                        metaData.track(relative, path.lastModified())
                    } else {
                        println("File exists $path and was modified after generation, skip generation.")
                    }
                }
            }
        }
        metaData.store(target)
    }

    protected open fun prepareNamespace(target: Path, context: GenerationContext): Path {
        val folder = target.resolve(genFolder)
        if (deleteGenFolder) folder.deleteRecursively()

        val ret = folder.resolve(context.namespace.toDotsAsPath())

        ret.mkdirs()
        return ret
    }
}

interface NamesI {
    val fileName: String
}

open class Names(override val fileName: String) : NamesI

open class Template<I> {
    val name: String
    val nameBuilder: Template<I>.(I) -> NamesI
    val generate: Template<I>.(I, GenerationContext) -> String

    constructor(name: String,
                nameBuilder: Template<I>.(I) -> NamesI,
                generate: Template<I>.(item: I, context: GenerationContext) -> String) {
        this.name = name
        this.nameBuilder = nameBuilder
        this.generate = generate
    }

    fun name(item: I): NamesI {
        return nameBuilder(item)
    }
}


open class GenerationContext : Cloneable {
    val namespace: String
    val header: String
    val footer: String
    val storage = DerivedStorage<ItemI>()
    val derivedController = DerivedController(storage)

    val types: MutableSet<ItemI> = hashSetOf()

    constructor(namespace: String, header: String = "", footer: String = "") {
        this.namespace = namespace
        this.header = header
        this.footer = footer
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
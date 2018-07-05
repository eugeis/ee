package ee.lang

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import ee.common.ext.*
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.*

val tab = "    "
val nL = "\n"

private val log = LoggerFactory.getLogger("GeneratorApi")

interface GeneratorI<M> {
    fun name(): String
    fun parent(): GeneratorGroupI<M>?
    fun generate(target: Path, model: M, shallSkip: GeneratorI<*>.(model: Any?) -> Boolean = { false })
    fun delete(target: Path, model: M, shallSkip: GeneratorI<*>.(model: Any?) -> Boolean = { false })
}

interface GeneratorGroupI<M> : GeneratorI<M> {
    fun names(prefix: String = ""): List<String>
}

abstract class AbstractGenerator<M>(val name: String, var parent: GeneratorGroupI<M>? = null) : GeneratorI<M> {
    override fun name(): String = name
    override fun parent(): GeneratorGroupI<M>? = parent
    fun parent(parent: GeneratorGroupI<M>?) {
        this.parent = parent
    }
}

open class GeneratorGroup<M>(name: String, val generators: Collection<GeneratorI<M>>)
    : AbstractGenerator<M>(name), GeneratorGroupI<M> {

    override fun delete(target: Path, model: M, shallSkip: GeneratorI<*>.(model: Any?) -> Boolean) {
        if (shallSkip(model)) return
        log.debug("delete in $target for $model")
        generators.forEach { it.delete(target, model, shallSkip) }
    }

    override fun generate(target: Path, model: M, shallSkip: GeneratorI<*>.(model: Any?) -> Boolean) {
        if (shallSkip(model)) return

        log.debug("generate ${names()} in $target for $model")
        generators.forEach { it.generate(target, model, shallSkip) }
    }

    override fun names(prefix: String): List<String> = generators.names("$prefix$name")
}

open class GeneratorGroupItems<M, I>(facet: String, val generators: Collection<GeneratorI<I>>,
    val items: M.() -> Collection<I>) : AbstractGenerator<M>(facet), GeneratorGroupI<M> {
    override fun delete(target: Path, model: M, shallSkip: GeneratorI<*>.(model: Any?) -> Boolean) {
        if (shallSkip(model)) return

        log.debug("delete ${name()} in $target for $model")
        model.items().forEach { item ->
            generators.forEach { it.delete(target, item, shallSkip) }
        }
    }

    override fun generate(target: Path, model: M, shallSkip: GeneratorI<*>.(model: Any?) -> Boolean) {
        if (shallSkip(model)) return

        log.debug("generate ${name()} to $target for $model")
        model.items().forEach { item ->
            generators.forEach { it.generate(target, item, shallSkip) }
        }
    }

    override fun names(prefix: String): List<String> = generators.names("$prefix$name")
}

private fun Collection<GeneratorI<*>>.names(name: String): List<String> =
    mutableListOf(name).apply {
        this@names.forEach {
            if (it is GeneratorGroupI<*>) {
                addAll(it.names("$name."))
            } else {
                add("$name.${it.name()}")
            }
        }
    }

abstract class GeneratorBase<M>(name: String, val contextBuilder: M.() -> GenerationContext) :
    AbstractGenerator<M>(name) {

    protected open fun prepareNamespace(module: Path, context: GenerationContext): Path {
        val folder = module.resolve(context.genFolder)
        val ret = folder.resolve(context.namespace.toDotsAsPath())
        ret.mkdirs()
        return ret
    }

    override fun delete(target: Path, model: M, shallSkip: GeneratorI<*>.(model: Any?) -> Boolean) {
        if (shallSkip(model)) return

        val c = model.contextBuilder()
        if (c.genFolderDeletable) {
            val module = target.resolve(c.moduleFolder)
            val folder = module.resolve(c.genFolder)
            folder.deleteFilesRecursively()
        } else if (c.genFolderDeletePattern != null) {
            val module = target.resolve(c.moduleFolder)
            val folder = module.resolve(c.genFolder)
            folder.deleteFilesRecursively(c.genFolderDeletePattern)
        }
    }
}

open class GeneratorSimple<M>(name: String, contextBuilder: M.() -> GenerationContext,
    val template: TemplateI<M>) : GeneratorBase<M>(name, contextBuilder) {

    override fun generate(target: Path, model: M, shallSkip: GeneratorI<*>.(model: Any?) -> Boolean) {
        if (shallSkip(model)) return

        val c = model.contextBuilder()
        val module = target.resolve(c.moduleFolder)
        val metaData = module.loadMetaData()

        val pkg = prepareNamespace(module, c)
        val path = pkg.resolve(template.name(model).fileName)
        val relative = target.relativize(path).toString()
        if (!path.exists() || !metaData.wasModified(relative, path.lastModified())) {
            log.debug("generate $path for $model")
            path.toFile().writeText(c.complete(template.generate(model, c)))
            metaData.track(relative, path.lastModified())
            c.clear()
            metaData.store(module)
        } else {
            log.debug("File exists $path and was modified after generation, skip generation.")
        }
    }
}

open class Generator<M, I>(name: String, contextBuilder: M.() -> GenerationContext, val items: M.() -> Collection<I>,
    val templates: I.() -> Collection<Template<I>>) : GeneratorBase<M>(name, contextBuilder) {

    override fun generate(target: Path, model: M, shallSkip: GeneratorI<*>.(model: Any?) -> Boolean) {
        if (shallSkip(model)) return

        val c = model.contextBuilder()
        val module = target.resolve(c.moduleFolder)
        val metaData = module.loadMetaData()

        model.items().forEach { item ->
            item.templates().forEach { template ->
                val pkg = prepareNamespace(module, c)
                val path = pkg.resolve(template.name(item).fileName)
                val relative = target.relativize(path).toString()
                if (!path.exists() || !metaData.wasModified(relative, path.lastModified())) {
                    log.debug("generate $path for $model")
                    path.toFile().writeText(c.complete(template.generate(template, item, c)))
                    metaData.track(relative, path.lastModified())
                    c.clear()
                } else {
                    log.debug("File exists $path and was modified after generation, skip generation.")
                }
            }
        }
        metaData.store(module)
    }
}

interface NamesI {
    val fileName: String
}

open class Names(override val fileName: String) : NamesI

interface FragmentI<I> {
    val name: String
    fun generate(item: I, context: GenerationContext): String
}

interface TemplateI<I> : FragmentI<I> {
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

open class FragmentsTemplate<M> : TemplateI<M> {
    override val name: String
    val fragments: M.() -> Collection<FragmentI<M>>
    val nameBuilder: TemplateI<M>.(M) -> NamesI

    constructor(name: String, fragments: M.() -> Collection<FragmentI<M>>, nameBuilder: TemplateI<M>.(M) -> NamesI) {
        this.name = name
        this.nameBuilder = nameBuilder
        this.fragments = fragments
    }

    override fun generate(model: M, context: GenerationContext): String {
        val buffer = StringBuffer()
        model.fragments().forEach { fragment ->
            buffer.appendln(fragment.generate(model, context))
            buffer.appendln()
        }
        return buffer.toString()
    }

    override fun name(item: M): NamesI {
        return nameBuilder(this, item)
    }
}

open class ItemsFragment<M, I> : FragmentI<M> {
    override val name: String
    val items: M.() -> Collection<I>
    val fragments: I.() -> Collection<FragmentI<I>>

    constructor(name: String = "", items: M.() -> Collection<I>, fragments: I.() -> Collection<FragmentI<I>>) {
        this.name = name
        this.items = items
        this.fragments = fragments
    }

    override fun generate(model: M, context: GenerationContext): String {
        val buffer = StringBuffer()
        model.items().forEach { item ->
            item.fragments().forEach { fragment ->
                buffer.appendln(fragment.generate(item, context))
                buffer.appendln()
            }
        }
        return buffer.toString()
    }
}

open class ItemsTemplate<M, I> : ItemsFragment<M, I>, TemplateI<M> {
    val nameBuilder: TemplateI<M>.(M) -> NamesI

    constructor(name: String, items: M.() -> Collection<I>, fragments: I.() -> Collection<FragmentI<I>>,
        nameBuilder: TemplateI<M>.(M) -> NamesI) : super(name, items, fragments) {
        this.nameBuilder = nameBuilder
    }

    override fun name(item: M): NamesI {
        return nameBuilder(this, item)
    }
}


open class GenerationContext : Cloneable {
    val namespace: String

    val moduleFolder: String
    val genFolder: String
    val genFolderDeletable: Boolean
    val genFolderDeletePattern: Regex?

    var header: String = ""
    var footer: String = ""

    val derivedController: DerivedController
    val macroController: MacroController

    val types: MutableSet<ItemI<*>> = hashSetOf()

    constructor(namespace: String = "", moduleFolder: String = "", genFolder: String = "",
        genFolderDeletable: Boolean = false, genFolderDeletePattern: Regex? = null,
        derivedController: DerivedController = DerivedController(DerivedStorage<ItemI<*>>()),
        macroController: MacroController = MacroController()) {
        this.namespace = namespace
        this.moduleFolder = moduleFolder
        this.genFolder = genFolder
        this.genFolderDeletable = genFolderDeletable
        this.genFolderDeletePattern = genFolderDeletePattern
        this.derivedController = derivedController
        this.macroController = macroController
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
        return if (value is ItemI<*>) n(value, derivedKind) else "$value"
    }

    open fun n(item: ItemI<*>, derivedKind: String = ""): String {
        return types.addReturn(derivedController.derive(item, derivedKind)).name()
    }

    open fun <T : ItemI<*>> body(macro: String, item: T, derivedKind: String = "", apiKind: String = ""): String {
        return macroController.find<T>(macro).code.invoke(item, this, derivedKind, apiKind)
    }
}

open class Macro<T : ItemI<*>> {
    val name: String
    val code: T.(c: GenerationContext, derivedKind: String, api: String) -> String

    constructor(name: String, template: T.(c: GenerationContext, derivedKind: String, api: String) -> String) {
        this.name = name
        this.code = template
    }

    companion object {
        val EMPTY = Macro<ItemI<*>>("EMPTY", { c, d, a -> "" })
    }
}

open class MacroController {
    val nameToMacro = hashMapOf<String, Macro<*>>()

    open fun <T : ItemI<*>> registerMacro(name: String,
        template: T.(c: GenerationContext, derivedKind: String, api: String) -> String) {
        nameToMacro.put(name, Macro(name = name, template = template))
    }

    open fun <T : ItemI<*>> find(name: String): Macro<T> {
        var ret = nameToMacro.get(name)
        if (ret == null) {
            log.warn("No macro '$name' found, return EMPTY.")
            ret = Macro.EMPTY
        }
        return ret as Macro<T>
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
    var ret: GenerationMetaData? = null
    if (eeFile.exists()) try {
        ret = mapper.readValue(eeFile.toFile())
    } catch (e: Exception) {
        log.warn("can't read $eeFile, create new one")
    }
    if (ret == null) ret = GenerationMetaData()
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
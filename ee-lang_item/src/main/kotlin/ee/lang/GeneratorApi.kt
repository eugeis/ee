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
        generators.forEach {
            it.delete(target, model, shallSkip)
        }
    }

    override fun generate(target: Path, model: M, shallSkip: GeneratorI<*>.(model: Any?) -> Boolean) {
        if (shallSkip(model)) return

        log.debug("generate ${names()} in $target for $model")
        generators.forEach {
            it.generate(target, model, shallSkip)
        }
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

abstract class GeneratorBase<M>(name: String, val contextBuilder: ContextBuilder<M>) :
        AbstractGenerator<M>(name) {

    protected open fun prepareNamespace(module: Path, context: GenerationContext): Path {
        val folder = module.resolve(context.genFolder)
        val ret = folder.resolve(context.namespace.toDotsAsPath())
        ret.mkdirs()
        return ret
    }

    override fun delete(target: Path, model: M, shallSkip: GeneratorI<*>.(model: Any?) -> Boolean) {
        if (shallSkip(model)) return

        val c = contextBuilder.builder.invoke(model)
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

open class GeneratorSimple<M>(name: String, contextBuilder: ContextBuilder<M>,
                              val template: TemplateI<M>) : GeneratorBase<M>(name, contextBuilder) {

    override fun generate(target: Path, model: M, shallSkip: GeneratorI<*>.(model: Any?) -> Boolean) {
        if (shallSkip(model)) return

        val c = contextBuilder.builder.invoke(model)
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

open class Generator<M, I>(name: String, contextBuilder: ContextBuilder<M>, val items: M.() -> Collection<I>,
                           val templates: I.() -> Collection<Template<I>>) : GeneratorBase<M>(name, contextBuilder) {

    override fun generate(target: Path, model: M, shallSkip: GeneratorI<*>.(model: Any?) -> Boolean) {
        if (shallSkip(model)) return

        val c = contextBuilder.builder.invoke(model)
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

open class Template<I>(
        override val name: String, val nameBuilder: TemplateI<I>.(I) -> NamesI,
        val generate: TemplateI<I>.(item: I, context: GenerationContext) -> String) : TemplateI<I> {

    override fun generate(item: I, context: GenerationContext): String = generate(this, item, context)

    override fun name(item: I): NamesI {
        return nameBuilder(item)
    }
}

open class FragmentsTemplate<M>(
        override val name: String,
        val fragments: M.() -> Collection<FragmentI<M>>, val nameBuilder: TemplateI<M>.(M) -> NamesI) : TemplateI<M> {

    override fun generate(item: M, context: GenerationContext): String {
        val buffer = StringBuffer()
        item.fragments().forEach { fragment ->
            buffer.appendln(fragment.generate(item, context))
            buffer.appendln()
        }
        return buffer.toString()
    }

    override fun name(item: M): NamesI {
        return nameBuilder(this, item)
    }
}

open class ItemsFragment<M, I>(
        override val name: String = "",
        val items: M.() -> Collection<I>, val fragments: I.() -> Collection<FragmentI<I>>) : FragmentI<M> {

    override fun generate(item: M, context: GenerationContext): String {
        val buffer = StringBuffer()
        item.items().forEach { childItem ->
            childItem.fragments().forEach { fragment ->
                buffer.appendln(fragment.generate(childItem, context))
                buffer.appendln()
            }
        }
        return buffer.toString()
    }
}

open class ItemsTemplate<M, I>(
        name: String, items: M.() -> Collection<I>, fragments: I.() -> Collection<FragmentI<I>>,
        val nameBuilder: TemplateI<M>.(M) -> NamesI) : ItemsFragment<M, I>(name, items, fragments), TemplateI<M> {

    override fun name(item: M): NamesI {
        return nameBuilder(this, item)
    }
}


open class GenerationContext(
        val namespace: String = "", val moduleFolder: String = "", val genFolder: String = "",
        val genFolderDeletable: Boolean = false, val genFolderDeletePattern: Regex? = null,
        val derivedController: DerivedController,
        val macroController: MacroController) : Cloneable {


    var header: String = ""
    var footer: String = ""

    var xmlSupport: Boolean = false
    var jsonSupport: Boolean = true

    val types: MutableSet<ItemI<*>> = hashSetOf()

    open fun toHeader(indent: String = ""): String {
        return header.isEmpty().ifElse("") { "$indent$header$nL" }
    }

    open fun toFooter(indent: String = ""): String {
        return footer.isEmpty().ifElse("") { "$indent$footer$nL" }
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

open class ContextBuilder<M>(val name: String, val macroController: MacroController,
                             val builder: M.() -> GenerationContext)

open class GeneratorContexts<M>(val generator: GeneratorGroupI<M>, vararg val contexts: ContextBuilder<M>)

open class Macro<T : ItemI<*>>(
        val name: String, val code: T.(c: GenerationContext, derivedKind: String, api: String) -> String) {

    companion object {
        val EMPTY = Macro<ItemI<*>>("EMPTY") { _, _, _ -> "" }
    }
}

open class MacroController {
    private val nameToMacro = hashMapOf<String, Macro<*>>()

    open fun <T : ItemI<*>> registerMacro(
            name: String, template: T.(c: GenerationContext, derivedKind: String, api: String) -> String) {
        nameToMacro[name] = Macro(name = name, code = template)
    }

    open fun <T : ItemI<*>> find(name: String): Macro<T> {
        var ret = nameToMacro[name]
        if (ret == null) {
            log.warn("No macro '$name' found, return EMPTY.")
            ret = Macro.EMPTY
        }
        @Suppress("UNCHECKED_CAST")
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
    mapper.enable(SerializationFeature.INDENT_OUTPUT)
    return mapper
}
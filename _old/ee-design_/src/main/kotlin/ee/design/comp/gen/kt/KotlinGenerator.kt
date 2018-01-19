package ee.design.comp.gen.kt

import ee.common.ext.*
import ee.design.comp.*
import ee.design.comp.gen.java.Jpa
import ee.design.*
import ee.design.gen.java.Java
import ee.design.gen.kt.*
import java.nio.file.Path

open class KotlinGenerator {
    val model: Model

    constructor(model: Model) {
        this.model = model
    }

    fun generate(target: Path) {
        model.extendForKotlinGeneration()
        model.generateKotlinApi(target)
    }

    protected fun Model.extendForKotlinGeneration() {
        initObjectTrees()

        //declare as 'base' all compilation units with non implemented operations.
        declareAsBaseWithNonImplementedOperation()

        //define constructor with all parameters.
        defineConstructorAllForNonConstructors()


        //define names for data type controllers
        defineNamesForDataTypeControllers()
    }

    protected fun initObjectTrees() {
        Java.initObjectTree()
        Kotlin.initObjectTree()
        Jpa.initObjectTree()
        t.initObjectTree()
    }

    protected fun Model.defineNamesForDataTypeControllers() {
        findDownByType(DataTypeI::class.java).forEach { entity ->
            entity.controllers.forEach { item ->
                storage.put(item, DerivedNames.API_BASE.name,
                    TypeDerived(item, "${entity.name}${item.name.capitalize()}Base", true))
                storage.put(item, DerivedNames.API.name, TypeDerived(item, "${entity.name}${item.name.capitalize()}"))
            }
            entity.queriesControllers.forEach { item ->
                storage.put(item, DerivedNames.API_BASE.name,
                    TypeDerived(item, "${entity.name}${item.name.capitalize()}Base", true))
                storage.put(item, DerivedNames.API.name, TypeDerived(item, "${entity.name}${item.name.capitalize()}"))
            }
            entity.commandsControllers.forEach { item ->
                storage.put(item, DerivedNames.API_BASE.name,
                    TypeDerived(item, "${entity.name}${item.name.capitalize()}Base", true))
                storage.put(item, DerivedNames.API.name, TypeDerived(item, "${entity.name}${item.name.capitalize()}"))
            }
        }
    }

    protected fun Model.defineConstructorAllForNonConstructors() {
        findDownByType(CompilationUnitI::class.java,
            stopSteppingDownIfFound = false).filter { it.constructors.isEmpty() }.extend { constructorAll() }
    }

    protected fun Model.declareAsBaseWithNonImplementedOperation() {
        findDownByType(CompilationUnitI::class.java).filter { it.operations.isNotEmpty() && !it.base }
            .forEach { it.base = true }

        //derive controllers from super units
        findDownByType(ControllerI::class.java).filter { it.parent is CompilationUnit }.forEach {
            val dataItem = it.parent as CompilationUnit
            dataItem.propagateItemToSubtypes(it)

            val T = it.G("T", dataItem)
            if (it !is Queries) {
                it.prop(T, "item")
            }
        }
    }

    protected fun <T : CompilationUnit> T.propagateItemToSubtypes(item: CompilationUnit) {
        superUnitFor.filter { superUnitChild ->
            superUnitChild.children.filterIsInstance<CompilationUnit>().find {
                (it.name == item.name || it.superUnit == superUnitChild)
            } == null
        }.forEach { superUnitChild ->
                val derivedItem = item.deriveSubType<Controller> {
                    namespace = superUnitChild.namespace
                    G("T", superUnitChild)
                }
                superUnitChild.add(derivedItem)
                superUnitChild.propagateItemToSubtypes(derivedItem)
            }
    }

    protected fun Model.generateKotlinApi(target: Path) {
        val metaData = target.loadMetaData()
        deleteSrcGenKotlin(target)
        for (module in findDownByType(CompModuleI::class.java)) {
            module.generateKotlin(target, metaData)
        }
        metaData.store(target)
    }


    protected fun CompModule.generateKotlin(target: Path, metaData: GenerationMetaData) {
        generateSrcGenKotlinApi(target)
        generateSrcKotlinApi(target, metaData)
    }

    protected fun CompModule.generateSrcGenKotlinApi(target: Path) {
        val pkg = target.resolve("src-gen/main/kotlin/${namespace.toDotsAsPath()}")
        pkg.mkdirs()

        pkg.resolve("${name.capitalize()}ApiBase.gen").toFile().printWriter().use { out ->
            val context = buildContext()

            val buffer = StringBuffer()

            generateSrcGenKotlinApi(buffer, context, enums) { context -> toKotlinEnum(context) }
            generateSrcGenKotlinApi(buffer, context, basics) { context -> toKotlinImpl(context) }
            generateSrcGenKotlinApi(buffer, context, values) { context -> toKotlinImpl(context) }
            generateSrcGenKotlinApi(buffer, context, entities) { context -> toKotlinImpl(context) }
            generateSrcGenKotlinApi(buffer, context, controllers) { context -> toKotlinImpl(context) }

            entities.forEach { item ->
                generateSrcGenKotlinApi(buffer, context, item.controllers, { context ->
                    toKotlinImpl(context)
                })
                generateSrcGenKotlinApi(buffer, context, item.queriesControllers, { context ->
                    toKotlinImpl(context)
                })
                generateSrcGenKotlinApi(buffer, context, item.commandsControllers, { context ->
                    toKotlinImpl(context)
                })
            }

            values.forEach { item ->
                generateSrcGenKotlinApi(buffer, context, item.controllers, { context ->
                    toKotlinImpl(context)
                })
                generateSrcGenKotlinApi(buffer, context, item.queriesControllers, { context ->
                    toKotlinImpl(context)
                })
                generateSrcGenKotlinApi(buffer, context, item.commandsControllers, { context ->
                    toKotlinImpl(context)
                })
            }
            out.println(context.complete(buffer.toString()))
        }
    }

    protected fun CompModule.buildContext(namespace: String = this.namespace): KotlinContext {
        val context = KotlinContext(moduleFolder = artifact, genFolder = "src-gen/main/kotlin", deleteGenFolder = true,
            namespace = namespace)
        return context
    }

    protected fun <T : CompilationUnit> generateSrcGenKotlinApi(buffer: StringBuffer, context: KotlinContext,
        items: Collection<T>, generator: T.(KotlinContext) -> String) {
        for (item in items) {
            buffer.appendln(item.generator(context))
            buffer.appendln()
        }
    }


    protected fun CompModule.generateSrcKotlinApi(target: Path, metaData: GenerationMetaData) {
        val pkg = target.resolve("src/main/kotlin/${namespace.toDotsAsPath()}")
        pkg.mkdirs()

        generateSrcKotlinApi(target, pkg, this, enums, metaData) { context -> toKotlinEnum(context) }
        generateSrcKotlinApi(target, pkg, this, basics, metaData) { context -> toKotlinExtends(context) }
        generateSrcKotlinApi(target, pkg, this, values, metaData) { context -> toKotlinExtends(context) }
        generateSrcKotlinApi(target, pkg, this, entities, metaData) { context -> toKotlinExtends(context) }
        generateSrcKotlinApi(target, pkg, this, controllers, metaData) { context -> toKotlinExtends(context) }

        entities.forEach { item ->
            generateSrcKotlinApi(target, pkg, this, item.controllers, metaData, { context -> toKotlinExtends(context) })
            generateSrcKotlinApi(target, pkg, this, item.queriesControllers, metaData,
                { context -> toKotlinExtends(context) })
            generateSrcKotlinApi(target, pkg, this, item.commandsControllers, metaData,
                { context -> toKotlinExtends(context) })
        }
        values.forEach { item ->
            generateSrcKotlinApi(target, pkg, this, item.controllers, metaData, { context -> toKotlinExtends(context) })
            generateSrcKotlinApi(target, pkg, this, item.queriesControllers, metaData,
                { context -> toKotlinExtends(context) })
            generateSrcKotlinApi(target, pkg, this, item.commandsControllers, metaData,
                { context -> toKotlinExtends(context) })
        }
    }

    protected fun <T : CompilationUnit> generateSrcKotlinApi(target: Path, pkg: Path, module: CompModule,
        items: Collection<T>, metaData: GenerationMetaData, generator: T.(KotlinContext) -> String) {
        for (item in items.filter { it.base }) {
            val path = pkg.resolve("${item.api.name.capitalize()}.gen")
            val relative = target.relativize(path).toString()
            if (!path.exists() || !metaData.wasModified(relative, path.lastModified())) {
                val file = path.toFile()
                file.printWriter().use { out ->
                    val context = module.buildContext(item.namespace)
                    val buffer = StringBuffer()
                    buffer.appendln(item.generator(context))
                    out.println(context.complete(buffer.toString()))
                }
                metaData.track(relative, path.lastModified())
            } else {
                println("File exists $path and was modified after generation, skip generation in src.")
            }
        }
    }

    protected fun deleteSrcGenKotlin(target: Path) {
        val pkg = target.resolve("src-gen/main/kotlin")
        pkg.deleteRecursively()
    }
}
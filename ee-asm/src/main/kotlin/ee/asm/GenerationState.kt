package ee.asm

import org.objectweb.asm.tree.ClassNode

interface Generator<R> {
    fun generate(state: GenerationState): Iterable<R>
}

class GenerationState(override val classTree: ClassTree, config: AnkoConfiguration) : ClassTreeUtils,
                                                                                      Configurable(config),
                                                                                      ReflectionUtils {

    val availableClasses: List<ClassNode> =
        classTree.filter { !isExcluded(it) && !classTree.findNode(it)!!.fromPlatformJar }

    val availableMethods: List<MethodNodeWithClass> = findAvailableMethods(availableClasses)

    private val cachedResults: MutableMap<Class<out Generator<*>>, Iterable<*>> = hashMapOf()

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(clazz: Class<out Generator<T>>): Iterable<T> = cachedResults.getOrPut(clazz) {
        initializeClass(clazz).generate(this)
    } as Iterable<T>

    override fun isExcluded(node: ClassNode) =
        node.fqName in config.excludedClasses || "${node.packageName}.*" in config.excludedClasses

    override fun isExcluded(node: MethodNodeWithClass) =
        (node.clazz.fqName + "#" + node.method.name) in config.excludedMethods

}
package ee.lang

import ee.common.ext.buildLabel
import org.slf4j.LoggerFactory
import java.util.*

private val log = LoggerFactory.getLogger("ItemUtils")

val IGNORE = "ignore"

data class InitChain<T>(val initFunctions: List<T.() -> Unit>) : Function1<T, Unit> {
    override fun invoke(p1: T) {
        initFunctions.forEach { it.invoke(p1) }
    }
}

fun <T> inits(vararg initFunctions: T.() -> Unit) = InitChain(initFunctions.toList())

fun ItemI<*>.findDerivedOrThis() = if (derivedFrom().isEMPTY()) this else derivedFrom()
fun ItemI<*>.isOrDerived(item: ItemI<*>) = this == item || derivedFrom() == item

fun <T : ItemI<*>> List<T>.extend(code: T.() -> Unit = {}) {
    forEach { it.extend(code) }
}

fun <T : ItemI<*>> T.extend(code: T.() -> Unit = {}) {
    code()
    init()
    if (this is MultiHolderI<*, *>) {
        fillSupportsItems()
    }
}

fun <B : ItemI<*>> B.doc(comment: String): B = apply { doc(Comment({ name(comment) })) }

fun <B : ItemI<B>> List<B>.derive(adapt: B.() -> Unit = {}): List<B> {
    return map { it.derive(adapt) }
}

fun <T : ItemI<*>> ItemI<*>.findThisOrParentUnsafe(clazz: Class<*>): T? {
    @Suppress("UNCHECKED_CAST")
    return if (clazz.isInstance(this)) this as T else findParentUnsafe(clazz)
}

fun <T : ItemI<*>> ItemI<*>.findThisOrParent(clazz: Class<T>): T? {
    return if (clazz.isInstance(this)) this as T else findParent(clazz)
}

fun <T : ItemI<*>> ItemI<*>.findParent(clazz: Class<T>): T? {
    val parent = parent()
    return when {
        parent.isEMPTY() -> null
        clazz.isInstance(parent) ->
            @Suppress("UNCHECKED_CAST")
            parent as T
        else -> parent.findParent(clazz)
    }
}

fun <T : ItemI<*>> ItemI<*>.findParentUnsafe(clazz: Class<*>): T? {
    val parent = parent()
    return when {
        parent.isEMPTY() -> null
        clazz.isInstance(parent) ->
            @Suppress("UNCHECKED_CAST")
            parent as T
        else -> parent.findParentUnsafe(clazz)
    }
}

fun <T : ItemI<*>> ItemI<*>.findParentMust(clazz: Class<T>): T {
    val parent = parent()
    return when {
        parent.isEMPTY() ->
            throw IllegalStateException("There is no parent for $clazz in ${this.name()}")
        clazz.isInstance(parent) ->
            @Suppress("UNCHECKED_CAST")
            parent as T
        else -> parent.findParentMust(clazz)
    }
}

fun <T : ItemI<*>> ItemI<*>.findParentNonInternal(): T? {
    val parent = parent()
    if (parent.isEMPTY()) {
        return null
    } else if (!parent.isInternal()) {
        @Suppress("UNCHECKED_CAST")
        return parent as T
    } else {
        return parent.findParentNonInternal()
    }
}

fun <T> MultiHolderI<*, *>.findAllByType(type: Class<T>): List<T> {
    return items().filterIsInstance(type)
}

fun <T> ItemI<*>.findUpByType(type: Class<T>, destination: MutableList<T> = ArrayList<T>(),
    alreadyHandled: MutableSet<ItemI<*>> = hashSetOf(), stopSteppingUpIfFound: Boolean = true): List<T> =
    findAcrossByType(type, destination, alreadyHandled, stopSteppingUpIfFound) { listOf(parent()) }

fun <T> ItemI<*>.findAcrossByType(type: Class<T>, destination: MutableList<T> = ArrayList<T>(),
    alreadyHandled: MutableSet<ItemI<*>> = HashSet(), stopSteppingAcrossIfFound: Boolean = true,
    acrossSelector: ItemI<*>.() -> Collection<ItemI<*>>): List<T> = findAcross({
    @Suppress("UNCHECKED_CAST")
    if (type.isInstance(this)) this as T else null
}, destination, alreadyHandled, stopSteppingAcrossIfFound, acrossSelector)

@Suppress("UNCHECKED_CAST")
fun <T> MultiHolderI<*, *>.findDownByType(type: Class<T>, destination: MutableList<T> = ArrayList<T>(),
                                          alreadyHandled: MutableSet<ItemI<*>> = hashSetOf(), stopSteppingDownIfFound: Boolean = true): List<T> =
        findAcrossByType(type, destination, alreadyHandled, stopSteppingDownIfFound, {
            if (this is MultiHolderI<*, *> && this.supportsItemType(
                            ItemI::class.java)) this.items() as Collection<ItemI<*>> else emptyList()
        })

@Suppress("UNCHECKED_CAST")
fun <T> ItemI<*>.findDown(select: ItemI<*>.() -> T?, destination: MutableList<T> = ArrayList<T>(),
                          alreadyHandled: MutableSet<ItemI<*>> = HashSet(), stopSteppingAcrossIfFound: Boolean = true): List<T> =
        findAcross(select, destination, alreadyHandled, stopSteppingAcrossIfFound, {
            if (this is MultiHolderI<*, *> && this.supportsItemType(
                            ItemI::class.java)) this.items() as Collection<ItemI<*>> else emptyList()
        })

fun <T> ItemI<*>.findAcross(select: ItemI<*>.() -> T?, destination: MutableList<T> = ArrayList<T>(),
                            alreadyHandled: MutableSet<ItemI<*>> = HashSet(), stopSteppingAcrossIfFound: Boolean = true,
                            acrossSelector: ItemI<*>.() -> Collection<ItemI<*>>): List<T> {
    acrossSelector().forEach { acrossItem ->
        if (!alreadyHandled.contains(acrossItem)) {
            alreadyHandled.add(acrossItem)
            val selected = acrossItem.select()
            if (selected != null && !destination.contains(selected)) {
                destination.add(selected)
                if (!stopSteppingAcrossIfFound) acrossItem.findAcross(select, destination, alreadyHandled,
                        stopSteppingAcrossIfFound, acrossSelector)
            } else {
                acrossItem.findAcross(select, destination, alreadyHandled, stopSteppingAcrossIfFound, acrossSelector)
            }
        }
    }
    return destination
}


fun <B : MultiHolderI<I, *>, I> B.initObjectTree(deriveNamespace: ItemI<*>.() -> String = {
    parent().namespace()
}): B {
    initIfNotInitialized()
    if (name().isBlank()) {
        name(buildLabel().name)
    }
    for (f in javaClass.declaredFields) {
        try {
            val getter = javaClass.declaredMethods.find { it.name == "get${f.name.capitalize()}" }
            if (getter != null) {
                val child = getter.invoke(this)
                if (child is ItemI<*>) {
                    child.initIfNotInitialized()
                    if (child.name().isBlank() && f.name != IGNORE) child.name(f.name)
                    //set the parent, parent shall be the DSL model parent and not some isInternal object or reference object
                    child.parent(this)
                    if (!containsItem(child as I)) {
                        addItem(child)
                    }
                    if (child.namespace().isBlank()) child.namespace(child.deriveNamespace())
                }
            }
        } catch (e: Exception) {
            log.info("$f $e")
        }

    }
    javaClass.declaredClasses.forEach {
        val child = it.findInstance()
        if (child != null && child is ItemI<*>) {
            if (!child.isInitialized()) child.init()
            if (child.name().isBlank()) child.name(child.buildLabel().name)

            //initObjectTree recursively if the parent is not set
            //set the parent, parent shall be the DSL model parent and not some isInternal object or reference object
            child.parent(this)
            if (!containsItem(child as I)) {
                addItem(child)
                if (child.namespace().isBlank()) child.namespace(child.deriveNamespace())
                if (child is MultiHolderI<*, *>) child.initObjectTree<B, I>(deriveNamespace)
            }
            if (child.namespace().isBlank()) child.namespace(child.deriveNamespace())
        }
    }
    fillSupportsItems()
    return this
}

fun <T> Class<T>.findInstance(): Any? {
    try {
        //val field = declaredFields.find { "INSTANCE" == name }
        //return field?.get(null)
        return getField("INSTANCE").get(null)
    } catch (e: NoSuchFieldException) {
        return null
    } catch (e: NoClassDefFoundError) {
        println(e)
        return null
    }
}


fun MultiHolderI<*, *>.initBlackNames() {
    findDown({ if (this.name().isBlank()) this else null }).forEach { it.initBlackName() }
}

fun ItemI<*>.initBlackName() {
    if (name().isBlank()) {
        if (derivedFrom().isNotEMPTY() && derivedFrom().name().isNotBlank()) {
            name(derivedFrom().name())
        } else if (parent().isNotEMPTY() && parent().name().isNotBlank()) {
            name(parent().name())
        } else {
            log.info("can't resolve name of $this")
        }
    }
}

fun ItemI<*>.initIfNotInitialized() {
    if (!isInitialized()) init()
}
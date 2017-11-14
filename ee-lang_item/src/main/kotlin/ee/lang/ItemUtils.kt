package ee.lang

import ee.common.ext.buildLabel
import org.slf4j.LoggerFactory
import java.util.*

private val log = LoggerFactory.getLogger("ItemUtils")

val IGNORE = "ignore"

data class InitChain<T>(val initFunctions: List<T.() -> Unit>) : Function1<T, Unit> {
    override fun invoke(p1: T): Unit {
        initFunctions.forEach { it.invoke(p1) }
    }
}

fun <T> inits(vararg initFunctions: T.() -> Unit) = InitChain<T>(initFunctions.toList())

fun ItemIB<*>.findDerivedOrThis() = if (derivedFrom().isEMPTY()) this else derivedFrom()
fun ItemIB<*>.isOrDerived(item: ItemIB<*>) = this == item || derivedFrom() == item

fun <T : ItemIB<*>> List<T>.extend(code: T.() -> Unit = {}) {
    forEach { it.extend(code) }
}

fun <T : ItemIB<*>> T.extend(code: T.() -> Unit = {}) {
    code()
    init()
    if (this is MultiHolderIB<*, *>) {
        fillSupportsItems()
    }
}

fun <B : ItemIB<*>> B.doc(comment: String): B = apply { doc(Comment({ name(comment) })) }

fun <B : ItemIB<B>> List<B>.derive(adapt: B.() -> Unit = {}): List<B> {
    return map { it.derive(adapt) }
}

fun <T : ItemIB<*>> ItemIB<*>.findThisOrParentUnsafe(clazz: Class<*>): T? {
    return if (clazz.isInstance(this)) this as T else findParentUnsafe(clazz)
}

fun <T : ItemIB<*>> ItemIB<*>.findThisOrParent(clazz: Class<T>): T? {
    return if (clazz.isInstance(this)) this as T else findParent(clazz)
}

fun <T : ItemIB<*>> ItemIB<*>.findParent(clazz: Class<T>): T? {
    val parent = parent()
    if (parent.isEMPTY()) {
        return null
    } else if (clazz.isInstance(parent)) {
        return parent as T
    } else {
        return parent.findParent(clazz)
    }
}

fun <T : ItemIB<*>> ItemIB<*>.findParentUnsafe(clazz: Class<*>): T? {
    val parent = parent()
    if (parent.isEMPTY()) {
        return null
    } else if (clazz.isInstance(parent)) {
        return parent as T
    } else {
        return parent.findParentUnsafe(clazz)
    }
}

fun <T : ItemIB<*>> ItemIB<*>.findParentMust(clazz: Class<T>): T {
    val parent = parent()
    if (parent.isEMPTY()) {
        throw IllegalStateException("There is no parent for $clazz in $this")
    } else if (clazz.isInstance(parent)) {
        return parent as T
    } else {
        return parent.findParentMust(clazz)
    }
}

fun <T : ItemIB<*>> ItemIB<*>.findParentNonInternal(): T? {
    val parent = parent()
    if (parent.isEMPTY()) {
        return null
    } else if (!parent.internal()) {
        return parent as T
    } else {
        return parent.findParentNonInternal()
    }
}

fun <T> MultiHolderIB<*, *>.findAllByType(type: Class<T>): List<T> {
    return items().filterIsInstance(type)
}

fun <T> ItemIB<*>.findUpByType(type: Class<T>, destination: MutableList<T> = ArrayList<T>(),
                               alreadyHandled: MutableSet<ItemIB<*>> = hashSetOf(),
                               stopSteppingUpIfFound: Boolean = true): List<T> =
        findAcrossByType(type, destination, alreadyHandled, stopSteppingUpIfFound) { listOf(parent()) }

fun <T> ItemIB<*>.findAcrossByType(type: Class<T>, destination: MutableList<T> = ArrayList<T>(),
                                   alreadyHandled: MutableSet<ItemIB<*>> = HashSet(),
                                   stopSteppingAcrossIfFound: Boolean = true,
                                   acrossSelector: ItemIB<*>.() -> Collection<ItemIB<*>>): List<T> =
        findAcross({
            if (type.isInstance(this)) this as T else null
        }, destination, alreadyHandled, stopSteppingAcrossIfFound, acrossSelector)

fun <T> MultiHolderIB<*, *>.findDownByType(type: Class<T>, destination: MutableList<T> = ArrayList<T>(),
                                           alreadyHandled: MutableSet<ItemIB<*>> = hashSetOf(),
                                           stopSteppingDownIfFound: Boolean = true): List<T> =
        findAcrossByType(type, destination, alreadyHandled, stopSteppingDownIfFound, {
            if (this is MultiHolderIB<*, *> && this.supportsItemType(ItemIB::class.java))
                this.items() as Collection<ItemIB<*>> else emptyList()
        })

fun <T> ItemIB<*>.findDown(select: ItemIB<*>.() -> T?, destination: MutableList<T> = ArrayList<T>(),
                           alreadyHandled: MutableSet<ItemIB<*>> = HashSet(),
                           stopSteppingAcrossIfFound: Boolean = true): List<T> =
        findAcross(select, destination, alreadyHandled, stopSteppingAcrossIfFound, {
            if (this is MultiHolderIB<*, *> && this.supportsItemType(ItemIB::class.java))
                this.items() as Collection<ItemIB<*>> else emptyList()
        })

fun <T> ItemIB<*>.findAcross(select: ItemIB<*>.() -> T?, destination: MutableList<T> = ArrayList<T>(),
                             alreadyHandled: MutableSet<ItemIB<*>> = HashSet(),
                             stopSteppingAcrossIfFound: Boolean = true,
                             acrossSelector: ItemIB<*>.() -> Collection<ItemIB<*>>): List<T> {
    acrossSelector().forEach { acrossItem ->
        if (!alreadyHandled.contains(acrossItem)) {
            alreadyHandled.add(acrossItem)
            val selected = acrossItem.select()
            if (selected != null && !destination.contains(selected)) {
                destination.add(selected)
                if (!stopSteppingAcrossIfFound) acrossItem.
                        findAcross(select, destination, alreadyHandled, stopSteppingAcrossIfFound, acrossSelector)
            } else {
                acrossItem.findAcross(select, destination, alreadyHandled, stopSteppingAcrossIfFound, acrossSelector)
            }
        }
    }
    return destination
}


fun <B : MultiHolderIB<I, *>, I> B.initObjectTree(deriveNamespace: ItemIB<*>.() -> String = {
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
                if (child is ItemIB<*>) {
                    child.initIfNotInitialized()
                    if (child.name().isBlank() && !f.name.equals(IGNORE)) child.name(f.name)
                    //set the parent, parent shall be the DSL model parent and not some internal object or reference object
                    child.parent(this)
                    if (!containsItem(child as I)) {
                        addItem(child)
                    }
                    if (child.namespace().isBlank()) child.namespace(child.deriveNamespace())
                }
            }
        } catch (e: Exception) {
            println("$f $e")
        }

    }
    javaClass.declaredClasses.forEach {
        val child = it.findInstance()
        if (child != null && child is ItemIB<*>) {
            if (!child.isInitialized()) child.init()
            if (child.name().isBlank()) child.name(child.buildLabel().name)

            //initObjectTree recursively if the parent is not set
            //set the parent, parent shall be the DSL model parent and not some internal object or reference object
            child.parent(this)
            if (!containsItem(child as I)) {
                addItem(child)
                if (child.namespace().isBlank()) child.namespace(child.deriveNamespace())
                if (child is MultiHolderIB<*, *>) child.initObjectTree<B, I>(deriveNamespace)
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


fun MultiHolderIB<*, *>.initBlackNames() {
    findDown({ if (this.name().isBlank()) this else null }).forEach { it.initBlackName() }
}

fun ItemIB<*>.initBlackName() {
    if (name().isBlank() && this is ItemIB<*>) {
        if (derivedFrom().isNotEMPTY() && derivedFrom().name().isNotBlank()) {
            name(derivedFrom().name())
        } else if (parent().isNotEMPTY() && parent().name().isNotBlank()) {
            name(parent().name())
        } else {
            log.debug("Can't resolve name of $this")
        }
    }
}

fun ItemIB<*>.initIfNotInitialized() {
    if (!isInitialized()) init()
}
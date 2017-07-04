package ee.lang

import com.sun.org.apache.xpath.internal.operations.Bool
import ee.common.ext.buildLabel
import org.slf4j.LoggerFactory
import java.util.*

private val log = LoggerFactory.getLogger("ItemUtils")

data class InitChain<T>(val initFunctions: List<T.() -> Unit>) : Function1<T, Unit> {
    override fun invoke(p1: T): Unit {
        initFunctions.forEach { it.invoke(p1) }
    }
}

fun <T> inits(vararg initFunctions: T.() -> Unit) = InitChain<T>(initFunctions.toList())

fun ItemI.findDerivedOrThis() = if (derivedFrom().isEMPTY()) this else derivedFrom()
fun ItemI.isOrDerived(item: ItemI) = this == item || derivedFrom() == item

fun <T : ItemI> List<T>.extend(code: T.() -> Unit = {}) {
    forEach { it.code() }
}

fun <T : ItemI> T.doc(comment: String): T = apply { doc(Comment({ name(comment) })) }

fun <T : ItemI> List<T>.derive(init: T.() -> Unit = {}): List<T> {
    return map { it.derive(init) }
}

fun <T : ItemI> ItemI.findThisOrParent(clazz: Class<T>): T? {
    return if (clazz.isInstance(this)) this as T else findParent(clazz)
}

fun <T : ItemI> ItemI.findParent(clazz: Class<T>): T? {
    val parent = parent()
    if (parent.isEMPTY()) {
        return null
    } else if (clazz.isInstance(parent)) {
        return parent as T
    } else {
        return parent.findParent(clazz)
    }
}

fun <T : ItemI> ItemI.findParentMust(clazz: Class<T>): T {
    val parent = parent()
    if (parent.isEMPTY()) {
        throw IllegalStateException("There is no parent for $clazz in $this")
    } else if (clazz.isInstance(parent)) {
        return parent as T
    } else {
        return parent.findParentMust(clazz)
    }
}

fun <T> MultiHolderI<*>.findAllByType(type: Class<T>): List<T> {
    return items().filterIsInstance(type)
}

fun <T> ItemI.findUpByType(type: Class<T>, destination: MutableList<T> = ArrayList<T>(),
                           alreadyHandled: MutableSet<ItemI> = HashSet(),
                           stopSteppingUpIfFound: Boolean = true): List<T> =
        findAcrossByType(type, destination, alreadyHandled, stopSteppingUpIfFound) { listOf(parent()) }

fun <T> ItemI.findAcrossByType(type: Class<T>, destination: MutableList<T> = ArrayList<T>(),
                               alreadyHandled: MutableSet<ItemI> = HashSet(),
                               stopSteppingAcrossIfFound: Boolean = true,
                               acrossSelector: ItemI.() -> Collection<ItemI>): List<T> =
        findAcross({
            if (type.isInstance(this)) this as T else null
        }, destination, alreadyHandled, stopSteppingAcrossIfFound, acrossSelector)

fun <T> MultiHolderI<*>.findDownByType(type: Class<T>, destination: MutableList<T> = ArrayList<T>(),
                                       alreadyHandled: MutableSet<ItemI> = HashSet(),
                                       stopSteppingDownIfFound: Boolean = true): List<T> =
        findAcrossByType(type, destination, alreadyHandled, stopSteppingDownIfFound, {
            if (this is MultiHolderI<*> && this.supportsItemType(ItemI::class.java))
                this.items() as Collection<ItemI> else emptyList()
        })

fun <T> ItemI.findDown(select: ItemI.() -> T?, destination: MutableList<T> = ArrayList<T>(),
                       alreadyHandled: MutableSet<ItemI> = HashSet(),
                       stopSteppingAcrossIfFound: Boolean = true): List<T> =
        findAcross(select, destination, alreadyHandled, stopSteppingAcrossIfFound, {
            if (this is MultiHolderI<*> && this.supportsItemType(ItemI::class.java))
                this.items() as Collection<ItemI> else emptyList()
        })

fun <T> ItemI.findAcross(select: ItemI.() -> T?, destination: MutableList<T> = ArrayList<T>(),
                         alreadyHandled: MutableSet<ItemI> = HashSet(),
                         stopSteppingAcrossIfFound: Boolean = true,
                         acrossSelector: ItemI.() -> Collection<ItemI>): List<T> {
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


fun <T : MultiHolderI<*>> T.initObjectTree(searchForTargetComposite: Boolean = false,
                                           deriveNamespace: ItemI.() -> String = { parent().namespace() }): T {
    if (!isInitialized()) init()
    if (name().isBlank()) {
        name(buildLabel().name)
    }
    for (f in javaClass.declaredFields) {
        try {
            val getter = javaClass.declaredMethods.find { it.name == "get${f.name.capitalize()}" }
            if (getter != null) {
                initChild(getter.invoke(this), searchForTargetComposite, deriveNamespace)
            }
        } catch (e: Exception) {
            println("$f $e")
        }

    }
    javaClass.declaredClasses.forEach {
        initChild(it.findInstance(), searchForTargetComposite, deriveNamespace)
    }
    return this
}

fun <T : MultiHolderI<*>> T.initChild(child: Any?, searchForTargetComposite: Boolean = false,
                                      deriveNamespace: ItemI.() -> String = { parent().namespace() }) {
    if (child != null && child is ItemI) {
        if (!child.isInitialized()) child.init()
        if (child.name().isBlank()) child.name(child.buildLabel().name)

        //initObjectTree recursively if the parent is not set
        var parentSet = false
        if (child.parent().isEMPTY()) {
            val targetMultiHolder = findSupportsItem(child, searchForTargetComposite)
            if (!targetMultiHolder.containsItem(child)) {
                parentSet = true
                targetMultiHolder.addItem(child)
            }
        }
        if (child.namespace().isBlank()) child.namespace(child.deriveNamespace())
        if (child is MultiHolderI<*> && (parentSet || child.name().startsWith("_")))
            child.initObjectTree(searchForTargetComposite, deriveNamespace)
    }
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


fun MultiHolderI<*>.initBlackNames() {
    findDown({ if (this.name().isBlank()) this else null }).forEach(ItemI::initBlackName)
}

fun ItemI.initBlackName() {
    if (name().isBlank()) {
        if (derivedFrom().isNotEMPTY() && derivedFrom().name().isNotBlank()) {
            name(derivedFrom().name())
        } else if (parent().isNotEMPTY() && parent().name().isNotBlank()) {
            name(parent().name())
        } else {
            log.debug("Can't resolve name of $this")
        }
    }
}
package ee.design

import ee.common.ext.buildLabel
import java.util.*

fun ItemI?.isEmpty(): Boolean = (this == null || this == Item.EMPTY)
fun ItemI?.isNotEmpty(): Boolean = !isEmpty()

fun <T : ItemI> List<T>.extend(init: T.() -> Unit = {}) {
    forEach { it.init() }
}

fun <T : ItemI> List<T>.derive(init: T.() -> Unit = {}): List<T> {
    return map { it.derive(init) }
}

fun <T : ItemI> ItemI.findParent(clazz: Class<T>): T? {
    if (parent() == Item.EMPTY) {
        return null
    } else if (clazz.isInstance(parent())) {
        return parent() as T
    } else {
        return parent().findParent(clazz)
    }
}

fun <T : ItemI> CompositeI.findAllByType(type: Class<T>): List<T> {
    return items().filterIsInstance(type)
}

fun <T : ItemI> ItemI.findUpByType(type: Class<T>, destination: MutableList<T> = ArrayList<T>(),
                                   alreadyHandled: MutableSet<ItemI> = HashSet(),
                                   stopSteppingUpIfFound: Boolean = true): List<T> =
        findAcrossByType(type, destination, alreadyHandled, stopSteppingUpIfFound) { listOf(parent()) }

fun <T : ItemI> ItemI.findAcrossByType(type: Class<T>, destination: MutableList<T> = ArrayList<T>(),
                                       alreadyHandled: MutableSet<ItemI> = HashSet(),
                                       stopSteppingAcrossIfFound: Boolean = true,
                                       acrossSelector: ItemI.() -> Collection<ItemI>): List<T> =
        findAcross({
            if (type.isInstance(this)) this as T else null
        }, destination, alreadyHandled, stopSteppingAcrossIfFound, acrossSelector)

fun <T : ItemI> CompositeI.findDownByType(type: Class<T>, destination: MutableList<T> = ArrayList<T>(),
                                          alreadyHandled: MutableSet<ItemI> = HashSet(),
                                          stopSteppingDownIfFound: Boolean = true): List<T> =
        findAcrossByType(type, destination, alreadyHandled, stopSteppingDownIfFound, {
            if (this is CompositeI) this.items() else emptyList()
        })

fun <T : ItemI> ItemI.findDown(select: ItemI.() -> T?, destination: MutableList<T> = ArrayList<T>(),
                               alreadyHandled: MutableSet<ItemI> = HashSet(),
                               stopSteppingAcrossIfFound: Boolean = true): List<T> =
        findAcross(select, destination, alreadyHandled, stopSteppingAcrossIfFound, {
            if (this is CompositeI) this.items() else emptyList()
        })

fun <T : ItemI> ItemI.findAcross(select: ItemI.() -> T?, destination: MutableList<T> = ArrayList<T>(),
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


fun <T : CompositeI> T.initObjectTree(): T {
    if (name().isBlank()) {
        name(buildLabel().name)
    }
    for (f in javaClass.declaredFields) {
        try {
            val getter = javaClass.declaredMethods.find { it.name == "get${f.name.capitalize()}" }
            if (getter != null) {
                val prop = getter.invoke(this)
                if (prop is ItemI) {
                    if (prop.name().isBlank()) prop.name(f.name)
                    if (!contains(prop)) add(prop)
                }
            }
        } catch (e: Exception) {
            println("$f $e")
        }

    }
    javaClass.declaredClasses.forEach {
        val child = it.findInstance()
        if (child != null) {
            if (child is ItemI && !contains(child)) {
                if (child.name().isBlank()) child.name(child.buildLabel().name)
                add(child)
                if (child is CompositeI) child.initObjectTree()
            }
        }
    }
    return this
}

fun <T> Class<T>.findInstance(): Any? {
    try {
        return getField("INSTANCE").get(null)
    } catch (e: NoSuchFieldException) {
        return null
    }
}


fun CompositeI.initBlackNames() {
    findDown({ if (this.name().isBlank()) this else null }).forEach(ItemI::initBlackName)
}

fun ItemI.initBlackName() {
    if (name().isBlank()) {
        if (derivedFrom().isNotEmpty() && derivedFrom().name().isNotBlank()) {
            name(derivedFrom().name())
        } else if (parent().isNotEmpty() && parent().name().isNotBlank()) {
            name(parent().name())
        } else {
            println("Can't resolve name of $this")
        }
    }
}
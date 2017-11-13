package ee.lang

import java.util.*

val storage = DerivedStorage<ItemIB<*>>()

class DerivedStorage<I>(val itemToStorage: MutableMap<I, MutableMap<String, Any>> = HashMap<I, MutableMap<String, Any>>()) {
    fun <T, S : I> getOrPut(item: S, key: String, init: S.(String) -> T): T {
        val itemStorage = itemToStorage.getOrPut(item, { HashMap() })
        return itemStorage.getOrPut(key, { item.init(key) as Any }) as T
    }

    fun <T, EE : I> put(item: EE, key: String, derived: T) {
        val itemStorage = itemToStorage.getOrPut(item, { HashMap<String, Any>() })
        itemStorage.put(key, derived as Any)
    }

    fun reset(item: I) {
        itemToStorage.remove(item)
    }
}

open class DerivedKind<T : ItemIB<*>> {
    val name: String
    val init: T.(String) -> T
    var support: T.() -> Boolean

    constructor(name: String, support: T.() -> Boolean = { true }, init: T.(String) -> T) {
        this.name = name
        this.support = support
        this.init = init
    }
}

open class DerivedByTransformer(name: String, transformer: ItemIB<*>.(String) -> String,
                                support: ItemIB<*>.() -> Boolean = { true }) : DerivedKind<ItemIB<*>>(name, support,
        { if (this.support()) this.derive({ name(transformer(it)) }) else this })

open class DerivedController {
    val nameToDerivedKind = HashMap<String, DerivedKind<*>>()
    var dynamicTransformer: DerivedByTransformer = DerivedByTransformer("DYNAMIC", { "${name()}$it" })
    val storage: DerivedStorage<ItemIB<*>>

    constructor(storage: DerivedStorage<ItemIB<*>> = DerivedStorage()) {
        this.storage = storage
    }

    open fun registerKinds(kinds: Collection<String>, transformer: ItemIB<*>.(String) -> String, support: ItemIB<*>.() -> Boolean = { true }) {
        kinds.forEach { register(DerivedByTransformer(it, transformer, support)) }
    }

    open fun registerKind(kind: String, transformer: ItemIB<*>.(String) -> String, support: ItemIB<*>.() -> Boolean = { true }): DerivedKind<*> {
        return register(DerivedByTransformer(kind, transformer, support))
    }

    open fun <T : ItemIB<*>> register(kind: DerivedKind<T>): DerivedKind<T> {
        nameToDerivedKind.put(kind.name, kind)
        return kind
    }

    open fun <T : ItemIB<*>> derive(item: T, kind: DerivedKind<T>) = storage.getOrPut(item, kind.name, kind.init)

    open fun <T : ItemIB<*>> derive(item: T, kindName: String): T {
        if (kindName.isNotBlank()) {
            var kind = nameToDerivedKind[kindName]
            if (kind == null) {
                kind = DerivedKind(kindName, dynamicTransformer.support, dynamicTransformer.init)
                nameToDerivedKind[kindName] = kind
            }
            return derive(item, kind as DerivedKind<T>)
        } else {
            return item
        }
    }
}
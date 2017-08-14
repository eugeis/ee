package ee.lang

import java.util.*

val storage = DerivedStorage<ItemI>()

class DerivedStorage<I>(val itemToStorage: MutableMap<I, MutableMap<String, Any>> = HashMap<I, MutableMap<String, Any>>()) {
    fun <T, S : I> getOrPut(item: S, key: String, init: S.(String) -> T): T {
        val itemStorage = itemToStorage.getOrPut(item, { HashMap<String, Any>() })
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


open class DerivedItem<out T : ItemI>(val delegate: T, val name: String = delegate.name(),
                                      val namespace: String = delegate.namespace()) : ItemI by delegate {
    override fun name(): String = name
    override fun namespace(): String = namespace
}

open class DerivedKind<T : ItemI> {
    val name: String
    val init: T.(String) -> T
    var support: T.() -> Boolean

    constructor(name: String, support: T.() -> Boolean = { true }, init: T.(String) -> T) {
        this.name = name
        this.support = support
        this.init = init
    }
}

open class DerivedByTransformer(name: String, transformer: ItemI.(String) -> String,
                                support: ItemI.() -> Boolean = { true }) : DerivedKind<ItemI>(name, support,
        { if (this.support()) DerivedItem<ItemI>(this, this.transformer(it)) else this })

open class DerivedController {
    val nameToDerivedKind = HashMap<String, DerivedKind<*>>()
    var dynamicTransformer: DerivedByTransformer = DerivedByTransformer("DYNAMIC", { "${name()}$it" })
    val storage: DerivedStorage<ItemI>

    constructor(storage: DerivedStorage<ItemI> = DerivedStorage()) {
        this.storage = storage
    }

    open fun registerKinds(kinds: Collection<String>, transformer: ItemI.(String) -> String, support: ItemI.() -> Boolean = { true }) {
        kinds.forEach { register(DerivedByTransformer(it, transformer, support)) }
    }

    open fun registerKind(kind: String, transformer: ItemI.(String) -> String, support: ItemI.() -> Boolean = { true }): DerivedKind<*> {
        return register(DerivedByTransformer(kind, transformer, support))
    }

    open fun <T : ItemI> register(kind: DerivedKind<T>): DerivedKind<T> {
        nameToDerivedKind.put(kind.name, kind)
        return kind
    }

    open fun <T : ItemI> derive(item: T, kind: DerivedKind<T>) = storage.getOrPut(item, kind.name, kind.init)

    open fun <T : ItemI> derive(item: T, kindName: String): T {
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
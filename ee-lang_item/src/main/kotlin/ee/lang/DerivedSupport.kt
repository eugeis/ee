package ee.lang

import java.util.*

val storage = DerivedStorage<ItemI>()

class DerivedStorage<I>(val itemToStorage: MutableMap<I, MutableMap<String, Any>> = HashMap<I, MutableMap<String, Any>>()) {
    fun <T, S : I> getOrPut(item: S, key: String, init: S.() -> T): T {
        val itemStorage = itemToStorage.getOrPut(item, { HashMap<String, Any>() })
        return itemStorage.getOrPut(key, { item.init() as Any }) as T
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
    val init: T.() -> T
    var support: T.() -> Boolean

    constructor(name: String, support: T.() -> Boolean = { true }, init: T.() -> T) {
        this.name = name
        this.support = support
        this.init = init
    }
}

open class DerivedByTransformer : DerivedKind<ItemI> {
    constructor(name: String, support: ItemI.() -> Boolean = { true },
                transformer: ItemI.() -> String) : super(name, support, {
        if (this.support()) DerivedItem<ItemI>(this, this.transformer()) else this
    })
}

open class DerivedController {
    val nameToDerivedKind = HashMap<String, DerivedKind<*>>()
    val storage: DerivedStorage<ItemI>

    constructor(storage: DerivedStorage<ItemI> = DerivedStorage()) {
        this.storage = storage
    }

    open fun registerKinds(kinds: Collection<String>, support: ItemI.() -> Boolean, transformer: ItemI.() -> String) {
        kinds.forEach { register(DerivedByTransformer(it, support, transformer)) }
    }

    open fun registerKind(kind: String, support: ItemI.() -> Boolean, transformer: ItemI.() -> String): DerivedKind<*> {
        return register(DerivedByTransformer(kind, support, transformer))
    }

    open fun <T : ItemI> register(kind: DerivedKind<T>): DerivedKind<T> {
        nameToDerivedKind.put(kind.name, kind)
        return kind
    }

    open fun <T : ItemI> derive(item: T, kind: DerivedKind<T>) = storage.getOrPut(item, kind.name, kind.init)

    open fun <T : ItemI> derive(item: T, kindName: String): T {
        if (kindName.isNotBlank()) {
            val kind = nameToDerivedKind[kindName]
            return if (kind != null && kind is DerivedKind<*>) derive(item, kind as DerivedKind<T>) else {
                println("There is no $kind registered")
                item
            }
        } else {
            return item
        }
    }
}
package ee.lang

import java.util.*

val storage = DerivedStorage<ItemI<*>>()

class DerivedStorage<I>(val itemToStorage: MutableMap<I, MutableMap<String, Any>> = HashMap()) {
    fun <T, S : I> getOrPut(item: S, key: String, init: S.(String) -> T): T {
        val itemStorage = itemToStorage.getOrPut(item) { HashMap() }
        @Suppress("UNCHECKED_CAST")
        return itemStorage.getOrPut(key) {
            item.init(key) as Any
        } as T
    }

    fun reset(item: I) {
        itemToStorage.remove(item)
    }
}

open class DerivedKind<T : ItemI<*>> {
    val name: String
    val init: T.(String) -> T
    var support: T.() -> Boolean

    constructor(name: String, support: T.() -> Boolean = { true }, init: T.(String) -> T) {
        this.name = name
        this.support = support
        this.init = init
    }
}

open class NameTransformer(name: String, nameTransformer: ItemI<*>.(String) -> String,
                           support: ItemI<*>.() -> Boolean = { true }) : DerivedKind<ItemI<*>>(name, support, {
    if (this.support()) {
        this.deriveWithParent { name(nameTransformer(it)) }
    } else this
})

open class NameAndNamespaceTransformers(name: String, nameTransformer: ItemI<*>.(String) -> String,
                                        namespaceTransformer: ItemI<*>.(String) -> String,
                                        support: ItemI<*>.() -> Boolean = { true }) : DerivedKind<ItemI<*>>(name, support, {
    if (this.support()) {
        this.deriveWithParent {
            namespace(namespaceTransformer(it))
            name(nameTransformer(it))
        }
    } else this
})

open class DerivedController {
    val nameToDerivedKind = HashMap<String, DerivedKind<*>>()
    var dynamicTransformer: NameTransformer = NameTransformer("DYNAMIC", { "${name()}$it" })
    val storage: DerivedStorage<ItemI<*>>

    constructor(storage: DerivedStorage<ItemI<*>> = DerivedStorage()) {
        this.storage = storage
    }

    open fun registerKinds(kinds: Collection<String>, transformer: ItemI<*>.(String) -> String,
        support: ItemI<*>.() -> Boolean = { true }) {
        kinds.forEach { register(NameTransformer(it, transformer, support)) }
    }

    open fun registerKind(kind: String, transformer: ItemI<*>.(String) -> String,
        support: ItemI<*>.() -> Boolean = { true }): DerivedKind<*> {
        return register(NameTransformer(kind, transformer, support))
    }

    open fun <T : ItemI<*>> register(kind: DerivedKind<T>): DerivedKind<T> {
        nameToDerivedKind[kind.name] = kind
        return kind
    }

    open fun <T : ItemI<*>> derive(item: T, kind: DerivedKind<T>) = storage.getOrPut(item, kind.name, kind.init)

    open fun <T : ItemI<*>> derive(item: T, kindName: String): T {
        if (kindName.isNotBlank()) {
            var kind = nameToDerivedKind[kindName]
            if (kind == null) {
                kind = DerivedKind(kindName, dynamicTransformer.support, dynamicTransformer.init)
                nameToDerivedKind[kindName] = kind
            }
            @Suppress("UNCHECKED_CAST")
            return derive(item, kind as DerivedKind<T>)
        } else {
            return item
        }
    }
}

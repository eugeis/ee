package ee.design

import ee.common.ext.deepCopy
import java.io.Serializable

interface ItemI : Serializable {
    fun namespace(): String
    fun namespace(value: String): ItemI

    fun name(): String
    fun name(value: String): ItemI

    fun parent(): ItemI
    fun parent(value: ItemI): ItemI

    fun derivedItems(): List<ItemI>
    fun onDerived(derived: ItemI)

    fun derivedFrom(): ItemI
    fun derivedFrom(value: ItemI): ItemI

    fun <T : ItemI> apply(code: T.() -> Unit): T
    fun <R> applyAndReturn(code: () -> R): R

    fun <T : ItemI> derive(init: T.() -> Unit = {}): T
    fun <T : ItemI> deriveDeep(init: T.() -> Unit): T

    fun <T : ItemI> deriveSubType(init: T.() -> Unit): T
    fun <T : ItemI> createType(): T

}

object ItemEmpty : ItemI {
    override fun namespace(): String = ""
    override fun namespace(value: String): ItemI = this
    override fun name(): String = ""
    override fun name(value: String): ItemI = this
    override fun parent(): ItemI = ItemEmpty
    override fun parent(value: ItemI): ItemI = this
    override fun onDerived(derived: ItemI) {}
    override fun derivedItems(): List<ItemI> = emptyList()
    override fun derivedFrom(): ItemI = ItemEmpty
    override fun derivedFrom(value: ItemI): ItemI = this
    override fun <T : ItemI> derive(init: T.() -> Unit): T = this as T
    override fun <T : ItemI> apply(code: T.() -> Unit): T = this as T
    override fun <R> applyAndReturn(code: () -> R): R = code()
    override fun <T : ItemI> deriveDeep(init: T.() -> Unit): T = this as T
    override fun <T : ItemI> deriveSubType(init: T.() -> Unit): T = this as T
    override fun <T : ItemI> createType(): T = this as T
}

interface TypedCompositeI<I : ItemI> : ItemI, List<I> {
    fun items(): MutableList<I>

    fun <T : ItemI> add(item: T): T

    fun addAll(elements: Collection<I>)

    fun sortByName()

    override val size: Int
        get() = items().size

    override fun contains(item: I): Boolean

    override fun containsAll(elements: Collection<I>): Boolean = items().containsAll(elements)

    override fun get(index: Int): I = items().get(index)

    override fun indexOf(element: I): Int = items().indexOf(element)

    override fun isEmpty(): Boolean = items().isEmpty()

    override fun iterator(): Iterator<I> = items().iterator()

    override fun lastIndexOf(element: I): Int = items().lastIndexOf(element)

    override fun listIterator(): ListIterator<I> = items().listIterator()

    override fun listIterator(index: Int): ListIterator<I> = items().listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<I> = items().subList(fromIndex, toIndex)
}

interface CompositeI : TypedCompositeI<ItemI> {
}

open class Item : ItemI, Cloneable {
    private var name: String = ""
    private var namespace: String = ""
    private var parent: ItemI = EMPTY
    private var derivedFrom: ItemI = EMPTY
    private var derivedItems: MutableList<ItemI> = arrayListOf()

    constructor(init: Item.() -> Unit = {}) {
        init()
    }

    override fun namespace(): String = namespace
    override fun namespace(value: String): ItemI = apply { namespace = value }

    override fun name(): String = name
    override fun name(value: String): ItemI = apply { name = value }

    override fun parent(): ItemI = parent
    override fun parent(value: ItemI): ItemI = apply { parent = value }

    override fun onDerived(derived: ItemI) {
        derivedItems.add(derived)
    }

    override fun derivedItems(): List<ItemI> = derivedItems

    override fun derivedFrom(): ItemI = derivedFrom
    override fun derivedFrom(value: ItemI): ItemI = apply {
        derivedFrom = value
        value.onDerived(this)
    }

    override fun <T : ItemI> derive(init: T.() -> Unit): T {
        val ret = clone() as T
        ret.derivedFrom(this)
        ret.init()
        return ret
    }

    override fun <T : ItemI> deriveDeep(init: T.() -> Unit): T {
        val ret = deepCopy() as T
        ret.derivedFrom(this)
        ret.init()
        return ret
    }

    override fun <T : ItemI> deriveSubType(init: T.() -> Unit): T {
        val ret = createType<T>()
        ret.name(name())
        ret.derivedFrom(this)
        ret.init()
        return ret
    }

    override fun <T : ItemI> createType(): T {
        val ret = javaClass.newInstance() as T
        return ret
    }

    override fun <T : ItemI> apply(code: T.() -> Unit): T {
        (this as T).code()
        return this
    }

    override fun <R> applyAndReturn(code: () -> R): R = code()


    companion object {
        val EMPTY = ItemEmpty
    }
}

open class TypedComposite<I : ItemI> : Item, TypedCompositeI<I> {
    private val items: MutableList<I> = arrayListOf()

    constructor(init: TypedCompositeI<I>.() -> Unit = {}) {
        init()
    }

    override fun <T : ItemI> add(item: T): T {
        if (item.namespace().isBlank()) item.namespace(namespace())
        item.parent(this)
        items.add(item as I)
        return item
    }

    override fun addAll(collection: Collection<I>) {
        collection.forEach { add(it) }
    }

    override fun contains(item: I): Boolean = items.contains(item)

    override fun sortByName() {
        items.sortBy(ItemI::name)
    }

    override fun items(): MutableList<I> = items
}

open class Composite : TypedComposite<ItemI>, CompositeI {
    constructor(init: CompositeI.() -> Unit = {}) {
        init()
    }
}
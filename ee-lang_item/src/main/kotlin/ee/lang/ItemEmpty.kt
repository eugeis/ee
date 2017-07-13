package ee.lang

import java.util.*

val ItemEmpty = ItemEmptyClass()

open class ItemEmptyClass : ItemI {
    override fun namespace(): String = ""
    override fun namespace(value: String): ItemI = this
    override fun name(): String = "@@EMPTY@@"
    override fun name(value: String): ItemI = this
    override fun doc(): CommentI = CommentEmpty
    override fun doc(value: CommentI): ItemI = this
    override fun parent(): ItemI = ItemEmpty
    override fun parent(value: ItemI): ItemI = this
    override fun onDerived(derived: ItemI) {}
    override fun derivedItems(): List<ItemI> = emptyList()
    override fun derivedFrom(): ItemI = ItemEmpty
    override fun derivedFrom(value: ItemI): ItemI = this
    override fun derivedAsType(): String = ""
    override fun derivedAsType(value: String): ItemI = this
    override fun <T : ItemI> derive(init: T.() -> Unit): T = this as T
    override fun <T : ItemI> apply(code: T.() -> Unit): T = this as T
    override fun <R> applyAndReturn(code: () -> R): R = code()
    override fun <T : ItemI> deriveSubType(init: T.() -> Unit): T = this as T
    override fun render(builder: StringBuilder, indent: String) {}
    override fun render(): String = ""
    override fun isInitialized(): Boolean = true
    override fun internal(): Boolean = true
    override fun init() {}
    override fun <T : ItemI> copy(): T = this as T
}

val MultiMapHolderEmpty = MapMultiHolderEmptyClass<Any>()

open class MultiHolderEmptyClass<I> : ItemEmptyClass(), MultiHolderI<I> {
    override fun items(): Collection<I> = emptyList()

    override fun containsItem(item: I): Boolean = false

    override fun <T : I> addItem(item: T): T = item

    override fun <T : I> addItems(items: Collection<T>): MultiHolderI<I> = this

    override fun <T> supportsItem(item: T): Boolean = false

    override fun <T> supportsItemType(itemType: Class<T>): Boolean = false

    override fun <T> fillSupportsItem(item: T): Boolean = false

    override fun fillSupportsItems() {}
}

open class ListMultiHolderEmptyClass<I>(private val items: MutableList<I> = ArrayList()) :
        MultiHolderEmptyClass<I>(), ListMultiHolderI<I>, MutableList<I> by items {
}

open class MapMultiHolderEmptyClass<I> : MultiHolderEmptyClass<I>(), MapMultiHolderI<I> {
    override fun removeItem(childName: String) {}
    override fun <T : I> addItem(childName: String, item: T, attachParent: Boolean): T = item
    override fun itemsMap(): Map<String, I> = emptyMap()
}

val CompositeEmpty = CompositeEmptyClass()

open class CompositeEmptyClass : MapMultiHolderEmptyClass<ItemI>(), CompositeI {
    override fun <T : Any> attr(name: String, attr: T?, attachParent: Boolean) = attr
    override fun attributes(): MapMultiHolderI<*> = MultiMapHolderEmpty
}

val CommentEmpty = CommentEmptyClass()

open class CommentEmptyClass : ListMultiHolderEmptyClass<String>(), CommentI

fun <T : ItemI> T?.isEMPTY(): Boolean = (this == null || this == ItemEmpty ||
        this.javaClass.toString().contains("Empty") || this.name().equals(ItemEmpty.name()))

fun <T : ItemI> T?.isNotEMPTY(): Boolean = !isEMPTY()
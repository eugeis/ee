package ee.lang

import java.util.*

object ItemEmpty : ItemEmptyClass<ItemEmpty>()

open class ItemEmptyClass<B : ItemIB<B>> : ItemIB<B> {
    override fun namespace(): String = ""
    override fun namespace(value: String): B = apply {}
    override fun name(): String = "@@EMPTY@@"
    override fun name(value: String): B = apply {}
    override fun doc(): CommentIB<*> = CommentEmpty
    override fun doc(value: CommentIB<*>): B = apply {}
    override fun parent(): ItemIB<*> = ItemEmpty
    override fun parent(value: ItemIB<*>): B = apply {}
    override fun onDerived(derived: ItemIB<*>) {}
    override fun derivedItems(): List<ItemIB<*>> = emptyList()
    override fun derivedFrom(): ItemIB<*> = ItemEmpty
    override fun derivedFrom(value: ItemIB<*>): B = apply {}
    override fun derivedAsType(): String = ""
    override fun derivedAsType(value: String): B = apply {}
    override fun derive(adapt: B.() -> Unit): B = apply {}
    override fun <T : ItemIB<*>> apply(code: T.() -> Unit): T = this as T
    override fun <R> applyAndReturn(code: () -> R): R = code()
    override fun deriveSubType(adapt: B.() -> Unit): B = apply {}
    override fun render(builder: StringBuilder, indent: String) {}
    override fun render(): String = ""
    override fun isInitialized(): Boolean = true
    override fun internal(): Boolean = true
    override fun init() {}
    override fun copy(): B = apply {}
}

object MultiMapHolderEmpty : MapMultiHolderEmptyClass<Any, MultiMapHolderEmpty>()

open class MultiHolderEmptyClass<I, B : MultiHolderIB<I, B>> : ItemEmptyClass<B>(), MultiHolderIB<I, B> {
    override fun items(): Collection<I> = emptyList()

    override fun containsItem(item: I): Boolean = false

    override fun <T : I> addItem(item: T): T = item

    override fun <T : I> addItems(items: Collection<T>): B = this as B

    override fun <T> supportsItem(item: T): Boolean = false

    override fun <T> supportsItemType(itemType: Class<T>): Boolean = false

    override fun <T> fillSupportsItem(item: T): Boolean = false

    override fun fillSupportsItems() {}
}

open class ListMultiHolderEmptyClass<I, B : ListMultiHolderIB<I, B>>(private val items: MutableList<I> = ArrayList()) :
        MultiHolderEmptyClass<I, B>(), ListMultiHolderIB<I, B>, MutableList<I> by items {
}

open class MapMultiHolderEmptyClass<I, B : MapMultiHolderIB<I, B>> : MultiHolderEmptyClass<I, B>(), MapMultiHolderIB<I, B> {
    override fun removeItem(childName: String) {}
    override fun <T : I> addItem(childName: String, item: T): T = item
    override fun itemsMap(): Map<String, I> = emptyMap()
}

object CompositeEmpty : CompositeEmptyClass<CompositeEmpty>()

open class CompositeEmptyClass<B : CompositeIB<B>> : MapMultiHolderEmptyClass<ItemIB<*>, B>(), CompositeIB<B> {
    override fun <T : Any> attr(name: String, attr: T?) = attr
    override fun attributes(): MapMultiHolderIB<*, *> = MultiMapHolderEmpty
}

object CommentEmpty : CommentEmptyClass<CommentEmpty>()

open class CommentEmptyClass<B : CommentIB<B>> : ListMultiHolderEmptyClass<String, B>(), CommentIB<B>

fun <T : ItemIB<*>> T?.isEMPTY(): Boolean = (this == null || this == ItemEmpty ||
        this.javaClass.toString().contains("Empty") || this.name().equals(ItemEmpty.name()))

fun <T : ItemIB<*>> T?.isNotEMPTY(): Boolean = !isEMPTY()
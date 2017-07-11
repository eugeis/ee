package ee.lang

import java.io.Serializable

interface ItemI {
    fun namespace(): String
    fun namespace(value: String): ItemI

    fun name(): String
    fun name(value: String): ItemI

    fun doc(): CommentI
    fun doc(value: CommentI): ItemI

    fun parent(): ItemI
    fun parent(value: ItemI): ItemI

    /** The instance is internal an not real part of model,
     * it must not be a parent of real model item */
    fun internal(): Boolean

    fun derivedItems(): List<ItemI>
    fun onDerived(derived: ItemI)

    fun derivedFrom(): ItemI
    fun derivedFrom(value: ItemI): ItemI

    fun <T : ItemI> apply(code: T.() -> Unit): T
    fun <R> applyAndReturn(code: () -> R): R

    fun <T : ItemI> derive(adapt: T.() -> Unit = {}): T
    fun <T : ItemI> deriveSubType(adapt: T.() -> Unit): T

    fun <T : ItemI> copy(): T

    fun render(builder: StringBuilder, indent: String)

    fun render(): String

    fun isInitialized(): Boolean
    fun init()
}

interface MultiHolderI<I> : ItemI {
    fun items(): Collection<I>

    fun <T : I> addItem(item: T, attachParent: Boolean = true): T
    fun <T : I> addItems(items: Collection<T>, attachParent: Boolean = true): MultiHolderI<I>
    fun containsItem(item: I): Boolean

    fun <T> supportsItem(item: T): Boolean
    fun <T> supportsItemType(itemType: Class<T>): Boolean
    fun <T> fillSupportsItem(item: T): Boolean
    fun fillSupportsItems()
}

interface ListMultiHolderI<I> : MultiHolderI<I>, MutableList<I> {
}

interface MapMultiHolderI<I> : MultiHolderI<I> {
    fun <T : I> addItem(childName: String, item: T, attachParent: Boolean = false): T
    fun removeItem(childName: String)
    fun itemsMap(): Map<String, I>
}

interface CompositeI : MapMultiHolderI<ItemI> {
    fun <T : Any> attr(name: String, attr: T?, attachParent: Boolean = false): T?
    fun attributes(): MapMultiHolderI<*>
}

interface CommentI : ListMultiHolderI<String> {
}
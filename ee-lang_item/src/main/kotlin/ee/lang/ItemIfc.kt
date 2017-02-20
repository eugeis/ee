package ee.lang

import java.io.Serializable

interface ItemI : Serializable {
    fun namespace(): String
    fun namespace(value: String): ItemI

    fun name(): String
    fun name(value: String): ItemI

    fun doc(): CommentI
    fun doc(value: CommentI): ItemI

    fun parent(): ItemI
    fun parent(value: ItemI): ItemI

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


interface ValueHolderI<T> : ItemI {
    fun value(): T
    fun value(value: T): T
}

interface NullValueHolderI<T> : ItemI {
    fun value(): T?
    fun value(value: T?): T?
}

interface MultiHolderI<I> : ItemI {
    fun items(): Collection<I>

    fun <T : I> addItem(item: T): T
    fun containsItem(item: I): Boolean

    fun <T> supportsItem(item: T): Boolean
    fun <T> supportsItemType(itemType: Class<T>): Boolean

    fun <T> findSupportsItem(item: T, childrenFirst: Boolean = false): MultiHolderI<T>
}

interface MultiListHolderI<I> : MultiHolderI<I>, MutableList<I> {
}

interface MultiMapHolderI<I> : MultiHolderI<I> {
    fun <T : I> addItem(name: String, item: T): T
    fun removeItem(name: String)
    fun itemsMap(): Map<String, I>
}

interface CompositeI : MultiMapHolderI<ItemI> {
    fun <T : Any> attr(name: String, attr: T?): T?
    fun attributes(): MultiMapHolderI<*>
}

interface CommentI : MultiListHolderI<String> {
}
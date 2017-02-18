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

    fun <T : ItemI> derive(init: T.() -> Unit = {}): T
    fun <T : ItemI> deriveDeep(init: T.() -> Unit): T

    fun <T : ItemI> deriveSubType(init: T.() -> Unit): T
    fun <T : ItemI> createType(): T

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

interface TypedCompositeI<I : ItemI> : ItemI, Iterable<I> {
    fun items(): List<I>

    fun <T : ItemI> add(item: T): T

    fun addAll(elements: Collection<I>)

    fun contains(item: I): Boolean

    fun remove(item: I): Boolean

    //return new
    fun <T : ItemI> replace(old: T, new: T): T

    fun first(): I

    fun sortByName()

    fun <T : ItemI> supportsItem(item: T): Boolean

    fun <T : ItemI> supportsItemType(itemType: Class<T>): Boolean

    fun <T : ItemI> findSupportsItem(item: T): TypedCompositeI<T>
}

interface CompositeI : TypedCompositeI<ItemI> {
}

interface CommentI : CompositeI {
}
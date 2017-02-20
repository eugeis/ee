package ee.lang

import java.util.*

val ItemEmpty = ItemEmptyClass()

open class ItemEmptyClass : ItemI {
    override fun namespace(): String = ""
    override fun namespace(value: String): ItemI = this
    override fun name(): String = ""
    override fun name(value: String): ItemI = this
    override fun doc(): CommentI = CommentEmpty
    override fun doc(value: CommentI): ItemI = this
    override fun parent(): ItemI = ItemEmpty
    override fun parent(value: ItemI): ItemI = this
    override fun onDerived(derived: ItemI) {}
    override fun derivedItems(): List<ItemI> = emptyList()
    override fun derivedFrom(): ItemI = ItemEmpty
    override fun derivedFrom(value: ItemI): ItemI = this
    override fun <T : ItemI> derive(init: T.() -> Unit): T = this as T
    override fun <T : ItemI> apply(code: T.() -> Unit): T = this as T
    override fun <R> applyAndReturn(code: () -> R): R = code()
    override fun <T : ItemI> deriveSubType(init: T.() -> Unit): T = this as T
    override fun render(builder: StringBuilder, indent: String) {}
    override fun render(): String = ""
    override fun isInitialized(): Boolean = true
    override fun init() {}
    override fun <T : ItemI> copy(): T = this as T
}

val MultiMapHolderEmpty = MultiMapHolderEmptyClass<Any>()

open class MultiHolderEmptyClass<I> : ItemEmptyClass(), MultiHolderI<I> {
    override fun items(): Collection<I> = emptyList()

    override fun containsItem(item: I): Boolean = false

    override fun <T : I> addItem(item: T): T = item

    override fun <T> supportsItem(item: T): Boolean = false

    override fun <T> supportsItemType(itemType: Class<T>): Boolean = false

    override fun <T> findSupportsItem(item: T, childrenFirst: Boolean): MultiHolderI<T> = this as MultiHolderI<T>
}

open class MultiListHolderEmptyClass<I>(private val items: MutableList<I> = ArrayList()) :
        MultiHolderEmptyClass<I>(), MultiListHolderI<I>, MutableList<I> by items {
}

open class MultiMapHolderEmptyClass<I> : MultiHolderEmptyClass<I>(), MultiMapHolderI<I> {
    override fun <T : I> addItem(name: String, item: T): T = item
    override fun itemsMap(): Map<String, I> = emptyMap()
}

val CompositeEmpty = CompositeEmptyClass()

open class CompositeEmptyClass : MultiMapHolderEmptyClass<ItemI>(), CompositeI {
    override fun <T : Any> attr(name: String, attr: T): T = attr
    override fun attributes(): MultiMapHolderI<*> = MultiMapHolderEmpty
}

val CommentEmpty = CommentEmptyClass()

open class CommentEmptyClass : MultiListHolderEmptyClass<String>(), CommentI

fun ItemI?.isEMPTY(): Boolean = (this == null || this == ItemEmpty)
fun ItemI?.isNotEMPTY(): Boolean = !isEMPTY()

fun CompositeI?.isEMPTY(): Boolean = (this == null || this == CompositeEmpty)
fun CompositeI?.isNotEMPTY(): Boolean = !isEMPTY()

fun CommentI?.isEMPTY(): Boolean = (this == null || this == CommentEmpty)
fun CommentI?.isNotEMPTY(): Boolean = !isEMPTY()
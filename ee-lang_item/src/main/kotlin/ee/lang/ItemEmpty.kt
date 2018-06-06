package ee.lang

import java.util.*

object ItemEmpty : ItemEmptyClass<ItemEmpty>()

open class ItemEmptyClass<B : ItemI<B>> : ItemI<B> {
    override fun extendAdapt(adapt: B.() -> Unit): B = apply {}
    override fun namespace(): String = ""
    override fun namespace(value: String): B = apply {}
    override fun name(): String = "@@EMPTY@@"
    override fun name(value: String): B = apply {}
    override fun doc(): CommentI<*> = CommentEmpty
    override fun doc(value: CommentI<*>): B = apply {}
    override fun parent(): ItemI<*> = ItemEmpty
    override fun parent(value: ItemI<*>): B = apply {}
    override fun onDerived(derived: ItemI<*>) {}
    override fun derivedItems(): List<ItemI<*>> = emptyList()
    override fun derivedFrom(): ItemI<*> = ItemEmpty
    override fun derivedFrom(value: ItemI<*>): B = apply {}
    override fun derivedAsType(): String = ""
    override fun derivedAsType(value: String): B = apply {}
    override fun derive(adapt: B.() -> Unit): B = apply {}
    override fun deriveWithParent(adapt: B.() -> Unit): B = apply {}
    @Suppress("UNCHECKED_CAST")
    override fun <T : ItemI<*>> apply(code: T.() -> Unit): T = this as T

    override fun <R> applyAndReturn(code: () -> R): R = code()
    override fun deriveSubType(adapt: B.() -> Unit): B = apply {}
    override fun toDsl(builder: StringBuilder, indent: String) {}
    override fun toDsl(): String = ""
    override fun isInitialized(): Boolean = true
    override fun isInternal(): Boolean = true
    override fun init(): B = apply {}
    override fun copy(): B = apply {}
    override fun copyWithParent(): B = apply {}
}

object MultiMapHolderEmpty : MapMultiHolderEmptyClass<Any, MultiMapHolderEmpty>()

open class MultiHolderEmptyClass<I, B : MultiHolderI<I, B>> : ItemEmptyClass<B>(), MultiHolderI<I, B> {
    override fun items(): Collection<I> = emptyList()

    override fun containsItem(item: I): Boolean = false

    override fun <T : I> addItem(item: T): T = item
    @Suppress("UNCHECKED_CAST")
    override fun <T : I> addItems(items: Collection<T>): B = this as B

    override fun <T> supportsItem(item: T): Boolean = false

    override fun <T> supportsItemType(itemType: Class<T>): Boolean = false

    override fun <T> fillSupportsItem(item: T): Boolean = false

    override fun fillSupportsItems() {}
}

open class ListMultiHolderEmptyClass<I, B : ListMultiHolderI<I, B>>(private val items: MutableList<I> = ArrayList()) :
    MultiHolderEmptyClass<I, B>(), ListMultiHolderI<I, B>, MutableList<I> by items

object CommentEmpty : CommentEmptyClass<CommentEmpty>()

open class CommentEmptyClass<B : CommentI<B>> : ListMultiHolderEmptyClass<String, B>(), CommentI<B>

fun <T : ItemI<*>> T?.isEMPTY(): Boolean =
    (this == null || this == ItemEmpty || this.javaClass.toString().contains("Empty") || name() ==
            ItemEmpty.name())

fun <T : ItemI<*>> T?.isNotEMPTY(): Boolean = !isEMPTY()


open class MapMultiHolderEmptyClass<I, B : MapMultiHolderI<I, B>> : MultiHolderEmptyClass<I, B>(),
        MapMultiHolderI<I, B> {
    override fun removeItem(childName: String) {}
    override fun <T : I> addItem(childName: String, item: T): T = item
    override fun itemsMap(): Map<String, I> = emptyMap()
}

object CompositeEmpty : CompositeEmptyClass<CompositeEmpty>()

open class CompositeEmptyClass<B : CompositeI<B>> : MapMultiHolderEmptyClass<ItemI<*>, B>(), CompositeI<B> {
    override fun <T : Any> attr(name: String, attr: T?) = attr
    override fun attributes(): MapMultiHolderI<*, *> = MultiMapHolderEmpty
}
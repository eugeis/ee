package ee.lang

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

    fun noInheritance(): Boolean
    /**
     * Derived as new free given type, e.g. Aggregate from Entity,
     * in order to have filter criteria and not catch derived Entity for generation and
     * provide special names
     */
    fun derivedAsType(): String

    fun derivedAsType(value: String): ItemI

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

    fun <T : I> addItem(item: T): T
    fun <T : I> addItems(items: Collection<T>): MultiHolderI<I>
    fun containsItem(item: I): Boolean

    fun <T> supportsItem(item: T): Boolean
    fun <T> supportsItemType(itemType: Class<T>): Boolean
    fun <T> fillSupportsItem(item: T): Boolean
    fun fillSupportsItems()
}

interface ListMultiHolderI<I> : MultiHolderI<I>, MutableList<I> {
}

interface MapMultiHolderI<I> : MultiHolderI<I> {
    fun <T : I> addItem(childName: String, item: T): T
    fun removeItem(childName: String)
    fun itemsMap(): Map<String, I>
}

interface CompositeI : MapMultiHolderI<ItemI> {
    fun <T : Any> attr(name: String, attr: T?): T?
    fun attributes(): MapMultiHolderI<*>
}

interface CommentI : ListMultiHolderI<String> {
}
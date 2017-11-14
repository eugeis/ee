package ee.lang

interface ItemI<B : ItemI<B>> {
    fun namespace(): String
    fun name(): String
    fun doc(): CommentI<*>
    fun parent(): ItemI<*>

    /** The instance is internal an not real part of model,
     * it must not be a parent of real model item */
    fun internal(): Boolean

    fun derivedItems(): List<ItemI<*>>
    fun onDerived(derived: ItemI<*>)
    /**
     * Derived as new free given type, e.g. Aggregate from Entity,
     * in order to have filter criteria and not catch derived Entity for generation and
     * provide special names
     */
    fun derivedAsType(): String

    fun derivedFrom(): ItemI<*>

    fun <R> applyAndReturn(code: () -> R): R

    fun render(builder: StringBuilder, indent: String)
    fun render(): String

    fun isInitialized(): Boolean
    fun init()
    fun namespace(value: String): B
    fun name(value: String): B
    fun doc(value: CommentI<*>): B
    fun parent(value: ItemI<*>): B
    fun derivedAsType(value: String): B
    fun derivedFrom(value: ItemI<*>): B
    fun copy(): B

    fun <T : ItemI<*>> apply(code: T.() -> Unit): T
    fun derive(adapt: B.() -> Unit = {}): B
    fun deriveSubType(adapt: B.() -> Unit): B
}


interface MultiHolderI<I, B : MultiHolderI<I, B>> : ItemI<B> {
    fun items(): Collection<I>

    fun <T : I> addItem(item: T): T
    fun containsItem(item: I): Boolean

    fun <T> supportsItem(item: T): Boolean
    fun <T> supportsItemType(itemType: Class<T>): Boolean
    fun <T> fillSupportsItem(item: T): Boolean
    fun fillSupportsItems()

    fun <T : I> addItems(items: Collection<T>): B
}

interface ListMultiHolderI<I, B : ListMultiHolderI<I, B>> : MultiHolderI<I, B>, MutableList<I> {
}

interface MapMultiHolderI<I, B : MapMultiHolderI<I, B>> : MultiHolderI<I, B> {
    fun <T : I> addItem(childName: String, item: T): T
    fun removeItem(childName: String)
    fun itemsMap(): Map<String, I>
}

interface CompositeI<B : CompositeI<B>> : MapMultiHolderI<ItemI<*>, B> {
    fun <T : Any> attr(name: String, attr: T?): T?
    fun attributes(): MapMultiHolderI<*, *>
}

interface CommentI<B : CommentI<B>> : ListMultiHolderI<String, B> {
}
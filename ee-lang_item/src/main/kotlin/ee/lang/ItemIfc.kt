package ee.lang

interface ItemIB<B : ItemIB<B>> {
    fun namespace(): String
    fun name(): String
    fun doc(): CommentIB<*>
    fun parent(): ItemIB<*>

    /** The instance is internal an not real part of model,
     * it must not be a parent of real model item */
    fun internal(): Boolean

    fun derivedItems(): List<ItemIB<*>>
    fun onDerived(derived: ItemIB<*>)
    /**
     * Derived as new free given type, e.g. Aggregate from Entity,
     * in order to have filter criteria and not catch derived Entity for generation and
     * provide special names
     */
    fun derivedAsType(): String

    fun derivedFrom(): ItemIB<*>

    fun <R> applyAndReturn(code: () -> R): R

    fun render(builder: StringBuilder, indent: String)
    fun render(): String

    fun isInitialized(): Boolean
    fun init()
    fun namespace(value: String): B
    fun name(value: String): B
    fun doc(value: CommentIB<*>): B
    fun parent(value: ItemIB<*>): B
    fun derivedAsType(value: String): B
    fun derivedFrom(value: ItemIB<*>): B
    fun copy(): B

    fun <T : ItemIB<*>> apply(code: T.() -> Unit): T
    fun derive(adapt: B.() -> Unit = {}): B
    fun deriveSubType(adapt: B.() -> Unit): B
}


interface MultiHolderIB<I, B : MultiHolderIB<I, B>> : ItemIB<B> {
    fun items(): Collection<I>

    fun <T : I> addItem(item: T): T
    fun containsItem(item: I): Boolean

    fun <T> supportsItem(item: T): Boolean
    fun <T> supportsItemType(itemType: Class<T>): Boolean
    fun <T> fillSupportsItem(item: T): Boolean
    fun fillSupportsItems()

    fun <T : I> addItems(items: Collection<T>): B
}

interface ListMultiHolderIB<I, B : ListMultiHolderIB<I, B>> : MultiHolderIB<I, B>, MutableList<I> {
}

interface MapMultiHolderIB<I, B : MapMultiHolderIB<I, B>> : MultiHolderIB<I, B> {
    fun <T : I> addItem(childName: String, item: T): T
    fun removeItem(childName: String)
    fun itemsMap(): Map<String, I>
}

interface CompositeIB<B : CompositeIB<B>> : MapMultiHolderIB<ItemIB<*>, B> {
    fun <T : Any> attr(name: String, attr: T?): T?
    fun attributes(): MapMultiHolderIB<*, *>
}

interface CommentIB<B : CommentIB<B>> : ListMultiHolderIB<String, B> {
}
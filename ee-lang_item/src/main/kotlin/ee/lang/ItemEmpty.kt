package ee.lang

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

val CompositeEmpty = CompositeEmptyClass()

open class CompositeEmptyClass : ItemEmptyClass(), CompositeI {
    override fun items(): List<ItemI> = emptyList()
    override fun <T : ItemI> add(item: T): T = item
    override fun addAll(elements: Collection<ItemI>) {}
    override fun iterator(): Iterator<ItemI> = items().iterator()
    override fun first(): ItemI = items().first()
    override fun <T : ItemI> supportsItemType(item: Class<T>): Boolean = false
    override fun <T : ItemI> supportsItem(item: T): Boolean = false
    override fun <T : ItemI> findSupportsItem(item: T): MultiHolder<T> = this as MultiHolder<T>
    override fun sortByName() {}
    override fun contains(item: ItemI): Boolean = false
    override fun remove(item: ItemI): Boolean = false
    override fun <T : ItemI> replace(old: T, new: T): T = new
}

val CommentEmpty = CommentEmptyClass()

open class CommentEmptyClass : CompositeEmptyClass(), CommentI

fun ItemI?.isEMPTY(): Boolean = (this == null || this == ItemEmpty)
fun ItemI?.isNotEMPTY(): Boolean = !isEMPTY()

fun CompositeI?.isEMPTY(): Boolean = (this == null || this == CompositeEmpty)
fun CompositeI?.isNotEMPTY(): Boolean = !isEMPTY()

fun CommentI?.isEMPTY(): Boolean = (this == null || this == CommentEmpty)
fun CommentI?.isNotEMPTY(): Boolean = !isEMPTY()
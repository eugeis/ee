package ee.lang

import ee.common.ext.declaredConstuctorWithOneGenericType
import ee.common.ext.deepCopy
import ee.common.ext.joinSurroundIfNotEmptyTo

open class Item : ItemI, Cloneable {
    private var _name: String = ""
    private var _namespace: String = ""
    private var _doc: CommentI = CommentEmpty
    private var _parent: ItemI = EMPTY
    private var _derivedFrom: ItemI = EMPTY
    private var _derivedItems: MutableList<ItemI> = arrayListOf()
    private var _init: Item.() -> Unit
    private var _initialized: Boolean = false

    constructor(value: Item.() -> Unit = {}) {
        this._init = value
    }

    override fun init() {
        _initialized = true
        _init(this)
    }

    override fun isInitialized(): Boolean = _initialized

    override fun name(): String = _name
    override fun name(value: String): ItemI = apply { _name = value }

    override fun namespace(): String = _namespace
    override fun namespace(value: String): ItemI = apply { _namespace = value }

    override fun doc(): CommentI = _doc
    override fun doc(value: CommentI): ItemI = apply { _doc = value }

    override fun parent(): ItemI = _parent
    override fun parent(value: ItemI): ItemI = apply { _parent = value }

    override fun onDerived(derived: ItemI) {
        _derivedItems.add(derived)
    }

    override fun derivedItems(): List<ItemI> = _derivedItems

    override fun derivedFrom(): ItemI = _derivedFrom
    override fun derivedFrom(value: ItemI): ItemI = apply {
        _derivedFrom = value
        value.onDerived(this)
    }
    override fun <T : ItemI> derive(adapt: T.() -> Unit): T {
        init()
        val ret = clone() as T
        ret.derivedFrom(this)
        ret.adapt()
        return ret
    }

    override fun <T : ItemI> deriveDeep(adapt: T.() -> Unit): T {
        init()
        val ret = deepCopy() as T
        ret.derivedFrom(this)
        ret.adapt()
        return ret
    }

    override fun <T : ItemI> deriveSubType(adapt: T.() -> Unit): T {
        val ret = createType<T>()
        ret.name(name())
        ret.derivedFrom(this)
        ret.adapt()
        return ret
    }

    override fun <T : ItemI> createType(): T {
        val ret = javaClass.newInstance() as T
        return ret
    }

    override fun <T : ItemI> apply(code: T.() -> Unit): T {
        (this as T).code()
        return this
    }

    override fun <R> applyAndReturn(code: () -> R): R = code()


    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent${name()}@${Integer.toHexString(hashCode())}")
    }

    override fun render(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }

    companion object {
        val EMPTY = ItemEmpty
    }
}

open class ValueHolder<T> : Item, ValueHolderI<T> {
    private var _value: T

    constructor(value: T, init: ValueHolder<T>.() -> Unit = {}) : super(init as Item.() -> Unit) {
        this._value = value
    }

    override fun value(): T = _value

    override fun value(value: T): T {
        this._value = value
        return value
    }

    override fun init() {
        super.init()
        val currentValue = _value
        if (currentValue is ItemI && !currentValue.isInitialized()) {
            currentValue.init()
        }
    }

    override fun render(builder: StringBuilder, indent: String) {
        super.render(builder, indent)
        val currentValue = _value
        if (currentValue is ItemI) {
            builder.append(" = ").append(currentValue.name()).append("@${Integer.toHexString(hashCode())}")
        } else {
            builder.append(" = ").append(currentValue.toString())
        }
    }
}

open class NullValueHolder<T> : Item, NullValueHolderI<T> {
    private var _value: T? = null

    constructor(init: NullValueHolder<T>.() -> Unit = {}) : super(init as Item.() -> Unit)

    constructor(value: T?, init: NullValueHolder<T>.() -> Unit = {}) : super(init as Item.() -> Unit) {
        this._value = value
    }

    override fun value(): T? = _value

    override fun value(value: T?): T? {
        this._value = value
        return value
    }

    override fun init() {
        super.init()
        val currentValue = _value
        if (currentValue != null && currentValue is ItemI && !currentValue.isInitialized()) {
            currentValue.init()
        }
    }

    override fun render(builder: StringBuilder, indent: String) {
        super.render(builder, indent)
        val currentValue = _value
        if (currentValue != null && currentValue is ItemI) {
            builder.append(" = ").append(currentValue.name()).append("@${Integer.toHexString(hashCode())}")
        } else {
            builder.append(" = ").append(currentValue.toString())
        }
    }
}

open class TypedComposite<I : ItemI> : Item, TypedCompositeI<I> {
    private val _type: Class<I>
    private val _items: MutableList<I> = arrayListOf()

    constructor(type: Class<I>, value: TypedComposite<I>.() -> Unit = {}) : super(value as Item.() -> Unit) {
        _type = type
    }

    override fun init() {
        super.init()
        items().forEach { if (!it.isInitialized()) it.init() }
    }

    override fun <T : ItemI> add(item: T): T {
        item.parent(this)
        _items.add(item as I)
        return item
    }

    override fun addAll(collection: Collection<I>) {
        collection.forEach { add(it) }
    }

    override fun contains(item: I): Boolean = _items.contains(item)

    override fun first(): I = _items.first()

    override fun iterator(): Iterator<I> = _items.iterator()

    override fun sortByName() {
        _items.sortBy(ItemI::name)
    }

    override fun <T : ItemI> supportsItemType(itemType: Class<T>): Boolean = itemType.isAssignableFrom(_type)

    override fun <T : ItemI> supportsItem(item: T): Boolean = _type.isInstance(item)

    override fun <T : ItemI> findSupportsItem(item: T): TypedComposite<T> =
            (_items.filterIsInstance(TypedComposite::class.java).find { it.supportsItem(item) } ?: this) as TypedComposite<T>

    override fun items(): List<I> = _items

    //renderer
    override fun render(builder: StringBuilder, indent: String) {
        super.render(builder, indent)
        renderChildren(builder, indent)
    }

    open fun renderChildren(builder: StringBuilder, indent: String) {
        items().joinSurroundIfNotEmptyTo(builder, "", " {\n", "$indent}") {
            it.render(builder, indent + "  ")
            "\n"
        }
    }
}

open class Composite : TypedComposite<ItemI>, CompositeI {
    constructor(value: Composite.() -> Unit = {}) : super(ItemI::class.java, value as TypedComposite<ItemI>.() -> Unit)
}

open class Comment : Composite, CommentI {
    constructor(value: Comment.() -> Unit = {}) : super(value as Composite.() -> Unit)
}
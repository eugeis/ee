package ee.lang

import ee.common.ext.joinSurroundIfNotEmptyTo
import java.util.*

open class Item : ItemI {
    private var _name: String = ""
    private var _namespace: String = ""
    private var _doc: CommentI = CommentEmpty
    private var _parent: ItemI = EMPTY
    private var _derivedFrom: ItemI = EMPTY
    private var _derivedItems: MutableList<ItemI> = arrayListOf()
    private var _initialized: Boolean = false
    protected var _init: Item.() -> Unit

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
        val ret = copy<T>()
        ret.derivedFrom(this)
        ret.adapt()
        return ret
    }

    override fun <T : ItemI> deriveSubType(adapt: T.() -> Unit): T {
        init()
        val ret = createType<T>()
        ret.name(name())
        ret.derivedFrom(this)
        ret.adapt()
        return ret
    }

    override fun <T : ItemI> copy(): T = fill(createType()) as T

    open protected fun <T : ItemI> createType(): T {
        val ret = javaClass.constructors.first().newInstance(_init) as T
        return ret
    }

    open protected fun fill(item: ItemI) {
        item.name(_name)
        item.namespace(_namespace)
        item.doc(_doc.copy())
        item.parent(_parent)
        item.derivedFrom(_derivedFrom)
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

open class ValueHolder<I> : Item, ValueHolderI<I> {
    private var _value: I

    constructor(value: I, init: ValueHolder<I>.() -> Unit = {}) : super(init as Item.() -> Unit) {
        this._value = value
    }

    override fun value(): I = _value

    override fun value(value: I): I {
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

    override fun fill(item: ItemI) {
        super.fill(item)
        if (item is ValueHolder<*>) {
            item.value(_value as Nothing)
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

open class NullValueHolder<I> : Item, NullValueHolderI<I> {
    private var _value: I? = null

    constructor(init: NullValueHolder<I>.() -> Unit = {}) : super(init as Item.() -> Unit)

    constructor(value: I?, init: NullValueHolder<I>.() -> Unit = {}) : super(init as Item.() -> Unit) {
        this._value = value
    }

    override fun value(): I? = _value

    override fun value(value: I?): I? {
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

    override fun fill(item: ItemI) {
        super.fill(item)
        if (item is NullValueHolder<*>) {
            item.value(_value as Nothing)
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

abstract class MultiHolder<I>(private val _type: Class<I>, value: MultiHolder<I>.() -> Unit = {}) :
        Item(value as Item.() -> Unit), MultiHolderI<I> {

    override fun init() {
        super.init()
        items().filterIsInstance<ItemI>().forEach { if (!it.isInitialized()) it.init() }
    }

    override fun <T : ItemI> createType(): T {
        val ret = javaClass.constructors.first().newInstance(_type, _init) as T
        return ret
    }


    override fun fill(item: ItemI) {
        super.fill(item)
        if (item is MultiListHolderI<*>) {
            val itemToFill = item as MultiHolderI<I>
            items().forEach {
                if (it is ItemI) {
                    itemToFill.addItem(it.copy<ItemI>() as I)
                } else {
                    itemToFill.addItem(it)
                }
            }
        }
    }

    override fun containsItem(item: I): Boolean = items().contains(item)

    override fun <T> supportsItemType(itemType: Class<T>): Boolean = itemType.isAssignableFrom(_type)

    override fun <T> supportsItem(item: T): Boolean = _type.isInstance(item)

    override fun <T> findSupportsItem(item: T, childrenFirst: Boolean): MultiHolderI<T> =
            (if (childrenFirst) items().filterIsInstance(MultiHolderI::class.java).find {
                it.supportsItem(item)
            } ?: this else this) as MultiHolderI<T>

    //renderer
    override fun render(builder: StringBuilder, indent: String) {
        super.render(builder, indent)
        renderChildren(builder, indent)
    }

    open fun renderChildren(builder: StringBuilder, indent: String) {
        items().joinSurroundIfNotEmptyTo(builder, "", " {\n", "$indent}") {
            if (it is ItemI) {
                it.render(builder, indent + "  ")
                "\n"
            } else {
                "$it"
            }
        }
    }
}

open class MultiListHolder<I>(_type: Class<I>, value: MultiListHolder<I>.() -> Unit = {},
                              private val _items: MutableList<I> = arrayListOf()) :
        MultiHolder<I>(_type, value as MultiHolder<*>.() -> Unit), MultiListHolderI<I>, MutableList<I> by _items {

    override fun <T : I> addItem(item: T): T {
        if (item is ItemI) item.parent(this)
        _items.add(item)
        return item
    }

    override fun items(): Collection<I> = _items
}

open class MultiMapHolder<I>(_type: Class<I>, adapt: MultiMapHolder<I>.() -> Unit = {},
                             private val _items: MutableMap<String, I> = TreeMap()) :
        MultiHolder<I>(_type, adapt as MultiHolder<*>.() -> Unit), MultiMapHolderI<I> {

    override fun <T : I> addItem(item: T): T {
        if (item is ItemI) {
            item.parent(this)
            _items.put(item.name(), item)
        } else {
            _items.put(item.toString(), item)
        }
        return item
    }

    override fun removeItem(name: String) {
        _items.remove(name)
    }

    override fun <T : I> addItem(name: String, item: T): T {
        _items.put(name, item)
        return item
    }

    override fun items(): Collection<I> = _items.values
    override fun itemsMap(): Map<String, I> = _items

    open fun <T : I> item(name: String): T? {
        var ret = _items[name]
        return if (ret != null) ret as T else null
    }

    open fun <T : I> item(name: String, factory: () -> T): T {
        var ret = _items[name]
        return if (ret == null) addItem(name, factory()) else ret as T
    }
}

open class Composite : MultiMapHolder<ItemI>, CompositeI {
    constructor(adapt: Composite.() -> Unit) : super(ItemI::class.java, adapt as MultiMapHolder<ItemI>.() -> Unit)

    open fun <R> itemAsMap(name: String, type: Class<R>): MultiMapHolder<R> =
            item(name, { MultiMapHolder<R>(type, { name(name) }) })

    open fun <R> itemAsList(name: String, type: Class<R>): MultiListHolder<R> =
            item(name, { MultiListHolder<R>(type, { name(name) }) })

    open fun <T : Any> attr(name: String): T? = attributes().item(name)
    open fun <T : Any> attr(name: String, factory: () -> T): T = attributes().item(name, factory)

    override fun <T : Any> attr(name: String, attr: T?): T? {
        if (attr != null) {
            attributes().addItem(name, attr)
        } else {
            attributes().addItem(name, attr)
        }
        return attr
    }

    override fun attributes(): MultiMapHolder<Any> = itemAsMap("attributes", Any::class.java)

    companion object {
    }
}

open class Comment : MultiListHolder<String>, CommentI {
    constructor(value: Comment.() -> Unit = {}) : super(String::class.java, value as MultiListHolder<*>.() -> Unit)
}
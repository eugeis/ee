package ee.lang

import ee.common.ext.joinSurroundIfNotEmptyTo
import org.slf4j.LoggerFactory
import java.util.*

private val log = LoggerFactory.getLogger("ItemApi")

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
        ret.init()
        ret.adapt()
        return ret
    }

    override fun <T : ItemI> deriveSubType(adapt: T.() -> Unit): T {
        init()
        val ret = createType<T>()
        if (ret != null) {
            ret.name(name())
            ret.derivedFrom(this)
            ret.adapt()
            return ret
        }
        return this as T
    }

    override fun <T : ItemI> copy(): T {
        val ret = createType<T>()
        if (ret != null) {
            fill(ret)
            return ret
        } else {
            log.debug("Can't create a new instance of $ret.")
            return this as T
        }
    }

    open protected fun <T : ItemI> createType(): T? {
        return createType(javaClass) ?: createType(javaClass.superclass)
    }

    open protected fun <T : ItemI> createType(type: Class<*>): T? {
        var constructor = type.constructors.find {
            it.toString().contains("(kotlin.jvm.functions.Function1)")
        }
        if (constructor != null) return constructor.newInstance(_init) as T
        return null
    }

    open protected fun fill(item: ItemI) {
        item.name(_name)
        item.namespace(_namespace)
        item.doc(_doc.copy())
        item.parent(Item.EMPTY)
        item.derivedFrom(this)
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

abstract class MultiHolder<I>(private val _type: Class<I>, value: MultiHolder<I>.() -> Unit = {}) :
        Item(value as Item.() -> Unit), MultiHolderI<I> {

    override fun init() {
        super.init()
        items().filterIsInstance<ItemI>().forEach { if (!it.isInitialized()) it.init() }
    }

    override fun <T : ItemI> createType(type: Class<*>): T? {
        var constructor = type.constructors.find {
            it.toString().contains("(java.lang.Class,kotlin.jvm.functions.Function1)")
        }
        if (constructor != null) return constructor.newInstance(_type, _init) as T

        constructor = type.constructors.find {
            it.toString().contains("(kotlin.jvm.functions.Function1)")
        }
        if (constructor != null) return constructor.newInstance(_init) as T
        return null
    }

    override fun <T : I> addItems(items: Collection<T>, attachParent: Boolean): MultiHolderI<I> = apply { items.forEach { addItem(it, attachParent) } }

    override fun containsItem(item: I): Boolean = items().contains(item)

    override fun <T> supportsItemType(itemType: Class<T>): Boolean = itemType.isAssignableFrom(_type)

    override fun <T> supportsItem(item: T): Boolean = _type.isInstance(item)

    fun itemType(): Class<I> = _type

    protected fun fillParent(item: I) {
        if (item is ItemI) {
            if (item.parent().isEMPTY()) {
                item.parent(this)
            } else {
                log.debug("Can't set ${this}(${this.name()}) as parent to $item(${
                item.name()}), because current parent is ${item.parent()}(${item.parent().name()})")
            }
        }
    }

    override fun <T> fillSupportsItem(item: T): Boolean {
        if (supportsItem(item) && !containsItem(item as I)) {
            addItem(item)
            return true
        }
        return false
    }

    override fun fillSupportsItems() {
        val items = items().filterIsInstance<ItemI>()
        val plainItems = items.filter { !it.name().startsWith("_") }
        val containers = items.filterIsInstance<MultiHolderI<*>>().filter { it.name().startsWith("_") && !it.name().startsWith("__") }
        plainItems.forEach { plainItem ->
            containers.forEach { container -> container.fillSupportsItem(plainItem) }
        }
    }

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

open class ListMultiHolder<I>(_type: Class<I>, value: ListMultiHolder<I>.() -> Unit = {},
                              private val _items: MutableList<I> = arrayListOf()) :
        MultiHolder<I>(_type, value as MultiHolder<*>.() -> Unit), ListMultiHolderI<I>, MutableList<I> by _items {

    override fun <T : I> addItem(item: T, attachParent: Boolean): T {
        if (attachParent) fillParent(item)
        _items.add(item)
        return item
    }

    override fun items(): Collection<I> = _items

    override fun <T : ItemI> createType(type: Class<*>): T? {
        var constructor = type.constructors.find {
            it.toString().contains("(java.lang.Class,kotlin.jvm.functions.Function1,java.util.List)")
        }
        if (constructor != null) return constructor.newInstance(itemType(), _init, ArrayList<I>()) as T

        constructor = type.constructors.find {
            it.toString().contains("(kotlin.jvm.functions.Function1)")
        }
        if (constructor != null) return constructor.newInstance(_init) as T
        return null
    }

    override fun fill(item: ItemI) {
        super.fill(item)
        if (item is ListMultiHolderI<*>) {
            val itemToFill = item as ListMultiHolderI<I>
            _items.forEach {
                if (it is ItemI && it.parent() == this) {
                    itemToFill.addItem(it.copy<ItemI>() as I)
                } else {
                    itemToFill.addItem(it, false)
                }
            }
        }
    }

    companion object {
        fun <I> empty(): ListMultiHolder<I> = ListMultiHolder<Any>(Any::class.java) as ListMultiHolder<I>
    }
}

open class MapMultiHolder<I>(_type: Class<I>, adapt: MapMultiHolder<I>.() -> Unit = {},
                             private val _items: MutableMap<String, I> = TreeMap()) :
        MultiHolder<I>(_type, adapt as MultiHolder<*>.() -> Unit), MapMultiHolderI<I> {

    override fun <T : I> addItem(item: T, attachParent: Boolean): T {
        if (attachParent) fillParent(item)
        if (item is ItemI) {
            addItem(item.name(), item)
        } else {
            _items.put(item.toString(), item)
        }
        return item
    }

    override fun removeItem(childName: String) {
        _items.remove(childName)
    }

    override fun <T : I> addItem(name: String, item: T, attachParent: Boolean): T {
        if (attachParent) fillParent(item)
        _items.put(name, item)
        return item
    }

    override fun items(): Collection<I> = _items.values
    override fun itemsMap(): Map<String, I> = _items

    open fun <T : I> item(name: String): T? {
        var ret = _items[name]
        return if (ret != null) ret as T else null
    }

    open fun <T : I> item(name: String, attachParent: Boolean, factory: () -> T): T {
        var ret = _items[name]
        return if (ret == null) addItem(name, factory(), attachParent) else ret as T
    }

    override fun <T : ItemI> createType(type: Class<*>): T? {
        var constructor = type.constructors.find {
            it.toString().contains("(java.lang.Class,kotlin.jvm.functions.Function1,java.util.Map)")
        }
        if (constructor != null) return constructor.newInstance(itemType(), _init, TreeMap<String, I>()) as T

        constructor = type.constructors.find {
            it.toString().contains("(kotlin.jvm.functions.Function1)")
        }
        if (constructor != null) return constructor.newInstance(_init) as T
        return null
    }

    override fun fill(item: ItemI) {
        super.fill(item)
        if (item is MapMultiHolderI<*>) {
            val itemToFill = item as MapMultiHolderI<I>
            _items.forEach {
                val value = it.value
                if (value is ItemI && value.parent() == this) {
                    itemToFill.addItem(it.key, value.copy<ItemI>() as I)
                } else {
                    itemToFill.addItem(it.key, value, false)
                }
            }
        }
    }
}

open class Composite : MapMultiHolder<ItemI>, CompositeI {
    constructor(adapt: Composite.() -> Unit = {}) : super(ItemI::class.java, adapt as MapMultiHolder<ItemI>.() -> Unit)

    open fun <R> itemAsMap(name: String, type: Class<R>, attachParent: Boolean = false): MapMultiHolder<R> =
            item(name, attachParent, { MapMultiHolder<R>(type, { name(name) }) })

    open fun <R> itemAsList(name: String, type: Class<R>, attachParent: Boolean = true): ListMultiHolder<R> =
            item(name, attachParent, { ListMultiHolder<R>(type, { name(name) }) })

    open fun <T : Any> attr(name: String): T? = attributes().item(name)
    open fun <T : Any> attr(name: String, factory: () -> T, attachParent: Boolean = false): T =
            attributes().item(name, attachParent, factory)

    override fun <T : Any> attr(name: String, attr: T?, attachParent: Boolean): T? {
        if (attr != null) {
            attributes().addItem(name, attr, attachParent)
        } else {
            attributes().removeItem(name)
        }
        return attr
    }

    override fun attributes(): MapMultiHolder<Any> = itemAsMap("__attributes", Any::class.java, true)
}

open class Comment : ListMultiHolder<String>, CommentI {
    constructor(value: Comment.() -> Unit = {}) : super(String::class.java, value as ListMultiHolder<*>.() -> Unit)
}
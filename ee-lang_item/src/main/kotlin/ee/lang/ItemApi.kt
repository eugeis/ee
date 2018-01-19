package ee.lang

import ee.common.ext.joinSurroundIfNotEmptyTo
import org.slf4j.LoggerFactory
import java.util.*

private val log = LoggerFactory.getLogger("ItemApi")

open class Item(adapt: Item.() -> Unit = {}) : ItemB<Item>(adapt)

open class ItemB<B : ItemI<B>> : ItemI<B> {
    private var _name: String = ""
    private var _namespace: String = ""
    private var _doc: CommentI<*> = CommentEmpty
    private var _parent: ItemI<*> = EMPTY
    private var _internal: Boolean = false
    private var _derivedFrom: ItemI<*> = EMPTY
    private var _derivedItems: MutableList<ItemI<*>> = arrayListOf()
    private var _derivedAsType: String = ""
    private var _initialized: Boolean = false
    protected var _adapt: B.() -> Unit
    protected var _adaptDerive: B.() -> Unit

    constructor(adapt: B.() -> Unit = {}) {
        this._adapt = adapt
        this._adaptDerive = {}
    }

    override fun init() {
        if (!isInitialized()) {
            _initialized = true
            _adapt(this as B)
        }
    }

    override fun extendAdapt(adapt: B.() -> Unit): B {
        val oldAdapt = _adapt
        _adapt = {
            oldAdapt()
            adapt()
        }

        //if already initialized, we need to apply for current instance
        if (isInitialized()) (this as B).adapt()
        return this as B
    }

    override fun isInitialized(): Boolean = _initialized

    override fun name(): String = _name
    override fun name(value: String): B = apply { _name = value }

    override fun namespace(): String = _namespace
    override fun namespace(value: String): B = apply { _namespace = value }

    override fun doc(): CommentI<*> = _doc
    override fun doc(value: CommentI<*>): B = apply { _doc = value }

    override fun parent(): ItemI<*> = _parent
    override fun parent(value: ItemI<*>): B = apply { _parent = value }

    override fun onDerived(derived: ItemI<*>) {
        _derivedItems.add(derived)
    }

    override fun derivedItems(): List<ItemI<*>> = _derivedItems

    override fun derivedFrom(): ItemI<*> = _derivedFrom
    override fun derivedFrom(value: ItemI<*>): B = apply {
        _derivedFrom = value
        value.onDerived(this)
    }

    override fun derivedAsType(): String = _derivedAsType
    override fun derivedAsType(value: String): B = apply { _derivedAsType = value }

    override fun derive(adapt: B.() -> Unit): B {
        init()
        val ret = copy()
        ret.derivedFrom(this)
        ret.adapt()
        //ret.extendAdapt(adapt)
        return ret
    }

    override fun deriveWithParent(adapt: B.() -> Unit): B {
        init()
        val ret = copyWithParent()
        ret.derivedFrom(this)
        ret.adapt()
        //ret.extendAdapt(adapt)
        return ret
    }


    override fun deriveSubType(adapt: B.() -> Unit): B {
        init()
        val ret = createType()
        if (ret != null) {
            ret.name(name())
            ret.derivedFrom(this)
            ret.adapt()
            //ret.extendAdapt(adapt)
            ret.init()
            if (ret is MultiHolderI<*, *>) {
                ret.fillSupportsItems()
            }
            return ret
        }
        return this as B
    }

    override fun copy(): B {
        val ret = createType()
        if (ret != null) {
            ret.parent(ItemB.EMPTY)
            fill(ret)
            if (ret is MultiHolderI<*, *>) {
                ret.fillSupportsItems()
            }
            return ret
        } else {
            log.debug("can't create a new instance of $ret.")
            return this as B
        }
    }

    override fun copyWithParent(): B {
        val ret = createType()
        if (ret != null) {
            ret.parent(parent())
            fill(ret)
            if (ret is MultiHolderI<*, *>) {
                ret.fillSupportsItems()
            }
            return ret
        } else {
            log.debug("Can't create a new instance of $ret.")
            return this as B
        }
    }

    open protected fun createType(): B? {
        var ret = createType(javaClass)
        if (ret == null) {
            log.trace("can't create instance of '{}', try to use the superclass {}", javaClass, javaClass.superclass)
            ret = createType(javaClass.superclass)
        }
        return ret
    }


    open protected fun createType(type: Class<*>): B? {
        var constructor = type.constructors.find {
            it.toString().contains("(kotlin.jvm.functions.Function1)")
        }
        if (constructor != null) return constructor.newInstance(_adaptDerive) as B
        return null
    }

    open protected fun fill(item: B) {
        item.init()
        if (item.name().isEmpty()) item.name(_name)
        if (item.namespace().isEmpty()) item.namespace(_namespace)
        if (item.doc().isEMPTY()) {
            val doc = _doc
            item.doc(doc.copy())
        }
    }

    override fun <T : ItemI<*>> apply(code: T.() -> Unit): T {
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

    override fun internal(): Boolean = _internal
    open fun internal(value: Boolean): ItemI<*> = apply { _internal = value }

    companion object {
        val EMPTY = ItemEmpty
    }
}

abstract class MultiHolder<I, B : MultiHolderI<I, B>>(private val _type: Class<I>, value: B.() -> Unit = {}) :
        ItemB<B>(value), MultiHolderI<I, B> {

    override fun init() {
        if (!isInitialized()) super.init()
        items().filterIsInstance<ItemI<*>>().forEach {
            if (!it.isInitialized() || (it is MultiHolderI<*, *> && it.parent() == this)) {
                it.init()
            }
        }
    }

    override fun createType(type: Class<*>): B? {
        var constructor = type.constructors.find {
            it.toString().contains("(java.lang.Class,kotlin.jvm.functions.Function1)")
        }
        if (constructor != null) return constructor.newInstance(_type, _adaptDerive) as B

        constructor = type.constructors.find {
            it.toString().contains("(kotlin.jvm.functions.Function1)")
        }
        if (constructor != null) return constructor.newInstance(_adaptDerive) as B
        return null
    }

    override fun <T : I> addItems(items: Collection<T>): B = apply { items.forEach { addItem(it) } }

    override fun containsItem(item: I): Boolean = items().contains(item)

    override fun <T> supportsItemType(itemType: Class<T>): Boolean = itemType.isAssignableFrom(_type)

    override fun <T> supportsItem(item: T): Boolean = _type.isInstance(item)

    fun itemType(): Class<I> = _type

    protected fun fillThisOrNonInternalAsParentAndInit(item: I) {
        if (item is ItemI<*>) {
            if (item.parent().isEMPTY()) {
                if (item.internal() || !this.internal()) {
                    item.parent(this)
                } else {
                    item.parent(findParentNonInternal() ?: this)
                }
                if (isInitialized() && !item.isInitialized()) item.init()
            } else {
                log.trace(
                    "can't set as parent '${this}(${this.name()})' to '$item(${item.name()})', because current parent is ${item.parent()}(${item.parent().name()})")
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
        val items = items().filterIsInstance<ItemI<*>>()
        val plainItems = items.filter { !it.name().startsWith("_") }
        val containers = items.filterIsInstance<MultiHolderI<*, *>>().filter {
            it.name().startsWith("_") && !it.name().startsWith("__")
        }
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
            if (it is ItemI<*>) {
                it.render(builder, indent + "  ")
                "\n"
            } else {
                "$it"
            }
        }
    }
}


open class ListMultiHolder<I>(_type: Class<I>, adapt: ListMultiHolder<I>.() -> Unit = {}) :
        ListMultiHolderB<I, ListMultiHolder<I>>(_type, adapt) {
    companion object {
        fun <I> empty(): ListMultiHolder<I> = ListMultiHolder(Any::class.java) as ListMultiHolder<I>
    }
}

open class ListMultiHolderB<I, B : ListMultiHolderI<I, B>>(_type: Class<I>, value: B.() -> Unit = {},
    private val _items: MutableList<I> = arrayListOf()) : MultiHolder<I, B>(_type, value), ListMultiHolderI<I, B>,
                                                          MutableList<I> by _items {

    override fun <T : I> addItem(item: T): T {
        fillThisOrNonInternalAsParentAndInit(item)
        _items.add(item)
        return item
    }

    override fun items(): Collection<I> = _items

    override fun createType(type: Class<*>): B? {
        var constructor = type.constructors.find {
            it.toString().contains("(java.lang.Class,kotlin.jvm.functions.Function1,java.util.List)")
        }
        if (constructor != null) return constructor.newInstance(itemType(), _adaptDerive, ArrayList<I>()) as B

        constructor = type.constructors.find {
            it.toString().contains("(java.lang.Class,kotlin.jvm.functions.Function1)")
        }
        if (constructor != null) return constructor.newInstance(itemType(), _adaptDerive) as B


        constructor = type.constructors.find {
            it.toString().contains("(kotlin.jvm.functions.Function1)")
        }
        if (constructor != null) return constructor.newInstance(_adaptDerive) as B
        return null
    }

    override fun fill(item: B) {
        super.fill(item)
        val itemToFill = item as ListMultiHolderI<I, B>
        _items.forEach {
            if (it is ItemI<*> && it.parent() == this) {
                itemToFill.addItem(it.copy() as I)
            } else if (it is ItemI<*> && it.parent() == this.parent()) {
                //don't need to copy, because it will be grouped by fillSupported
            } else {
                itemToFill.addItem(it)
            }
        }
    }

    companion object {
        fun <I> empty(): ListMultiHolder<I> = ListMultiHolder(Any::class.java) as ListMultiHolder<I>
    }
}

open class MapMultiHolder<I>(_type: Class<I>, adapt: MapMultiHolder<I>.() -> Unit = {}) :
        MapMultiHolderB<I, MapMultiHolder<I>>(_type, adapt)

open class MapMultiHolderB<I, B : MapMultiHolderI<I, B>>(_type: Class<I>, adapt: B.() -> Unit = {},
    private val _items: MutableMap<String, I> = TreeMap()) : MultiHolder<I, B>(_type, adapt), MapMultiHolderI<I, B> {

    override fun <T : I> addItem(item: T): T {
        if (item is ItemI<*>) {
            addItem(item.name(), item)
        } else {
            fillThisOrNonInternalAsParentAndInit(item)
            _items.put(item.toString(), item)
        }
        return item
    }

    override fun removeItem(childName: String) {
        _items.remove(childName)
    }

    override fun <T : I> addItem(name: String, item: T): T {
        fillThisOrNonInternalAsParentAndInit(item)
        _items.put(name, item)
        return item
    }

    override fun items(): Collection<I> = _items.values
    override fun itemsMap(): Map<String, I> = _items

    open fun <T : I> item(name: String): T? {
        var ret = _items[name]
        return if (ret != null) ret as T else null
    }

    open fun <T : I> item(name: String, internal: Boolean, factory: () -> T): T {
        var ret = _items[name]
        return if (ret == null) {
            val item = factory()
            if (item is ItemB<*>) {
                item.internal(internal)
            }
            addItem(name, item)
        } else ret as T
    }

    override fun createType(type: Class<*>): B? {
        var constructor = type.constructors.find {
            it.toString().contains("(java.lang.Class,kotlin.jvm.functions.Function1,java.util.Map)")
        }
        if (constructor != null) return constructor.newInstance(itemType(), _adaptDerive, TreeMap<String, I>()) as B

        constructor = type.constructors.find {
            it.toString().contains("(java.lang.Class,kotlin.jvm.functions.Function1)")
        }
        if (constructor != null) return constructor.newInstance(itemType(), _adaptDerive) as B

        constructor = type.constructors.find {
            it.toString().contains("(kotlin.jvm.functions.Function1)")
        }
        if (constructor != null) return constructor.newInstance(_adaptDerive) as B
        return null
    }

    override fun fill(item: B) {
        super.fill(item)
        val itemToFill = item as MapMultiHolderI<I, B>
        _items.forEach {
            val value = it.value
            if (value is ItemI<*> && value.parent() == this) {
                itemToFill.addItem(it.key, value.copy() as I)
            } else {
                itemToFill.addItem(it.key, value)
            }
        }
    }
}

open class Composite(adapt: Composite.() -> Unit = {}) : CompositeB<Composite>(adapt)
open class CompositeB<B : CompositeI<B>> : MapMultiHolderB<ItemI<*>, B>, CompositeI<B> {
    constructor(adapt: B.() -> Unit = {}) : super(ItemI::class.java, adapt)

    open fun <R> itemAsMap(name: String, type: Class<R>, attachParent: Boolean = false,
        internal: Boolean = false): MapMultiHolderB<R, *> {
        return item(name, internal, { MapMultiHolder(type, { name(name) }) })
    }

    open fun <R> itemAsList(name: String, type: Class<R>, internal: Boolean = false): ListMultiHolder<R> {
        return item(name, internal, { ListMultiHolder(type, { name(name) }) })
    }

    open fun <T : Any> attr(name: String): T? = attributes().item(name)
    open fun <T : Any> attr(name: String, factory: () -> T, attachParent: Boolean = false): T =
        attributes().item(name, false, factory)

    override fun <T : Any> attr(name: String, attr: T?): T? {
        if (attr != null) {
            attributes().addItem(name, attr)
        } else {
            attributes().removeItem(name)
        }
        return attr
    }

    override fun attributes(): MapMultiHolderB<Any, *> = itemAsMap("__attributes", Any::class.java, true, true)
}

open class Comment : ListMultiHolderB<String, Comment>, CommentI<Comment> {
    constructor(adapt: Comment.() -> Unit = {}) : super(String::class.java, adapt)
}
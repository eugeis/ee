package ee.lang

object l : Composite({ namespace("ee.lang") }) {

    //from lang_item
    val Item = ItemDsl()
    val ValueHolder = ItemDsl({ derivedFrom(Item) })
    val NullValueHolder = ItemDsl({ derivedFrom(Item) })
    val TypedComposite = ItemDsl({ derivedFrom(Item) })
    val Composite = ItemDsl({ derivedFrom(TypedComposite) })
    val Comment = ItemDsl({ derivedFrom(Composite) })

    //lang types
    object TextComposite : Composite({ derivedFrom(Composite) })

    object Type : Composite({ derivedFrom(Composite) }) {
        val generics = AttributeI({ type(Generic).multi(true).nonFluent("G") })
        val multi = AttributeI({ type(n.Boolean).value(false) })
        val defaultValue = AttributeI({ type(n.Any).nullable(true) })
    }

    object Generic : Composite({ derivedFrom(Type) }) {
        val type = AttributeI({ type(Type) })
    }

    object Lambda : Composite({ derivedFrom(Type) }) {
        val operation = AttributeI({ type(Operation) })
    }

    object NativeType : Composite({ derivedFrom(Type) })

    object ExternalType : Composite({ derivedFrom(Type) })

    object Attribute : Composite({ derivedFrom(Composite) }) {
        val type = AttributeI({ type(Type) })
        val key = AttributeI({ type(n.Boolean).value(false) })
        val unique = AttributeI({ type(n.Boolean).value(false) })
        val value = AttributeI({ type(n.Any).nullable(true) })
        val initByDefaultTypeValue = AttributeI({ type(n.Boolean).value(true) })
        val nullable = AttributeI({ type(n.Boolean).value(false) })
        val accessible = AttributeI({ type(n.Boolean).value(true) })
        val replaceable = AttributeI({ type(n.Boolean).value(true) })
        val meta = AttributeI({ type(n.Boolean).value(false) })
        val multi = AttributeI({ type(n.Boolean).value(false) })
        val hidden = AttributeI({ type(n.Boolean).value(false) })
        val mutable = AttributeI({ type(n.Boolean).value(true) })
        val length = AttributeI({ type(n.Int).nullable(true) })
        val inherited = AttributeI({ type(n.Boolean).value(false) })
        val open = AttributeI({ type(n.Boolean).value(false) })
        val nonFluent = AttributeI({ type(n.String) })
    }

    object LogicUnit : Composite({ derivedFrom(TextComposite) }) {
        val virtual = AttributeI({ type(n.Boolean).value(false) })
        val superUnit = AttributeI({ type(LogicUnit) })
        val params = AttributeI({ type(Attribute).multi(true) })
    }

    object Operation : Composite({ derivedFrom(LogicUnit) }) {
        val generics = AttributeI({ type(Generic).multi(true).nonFluent("G") })
        val ret = AttributeI({ type(Attribute).nonFluent("r") })
    }

    object CompilationUnit : Composite({ derivedFrom(Type) }) {
        val base = AttributeI({ type(n.Boolean).value(false) })
        val open = AttributeI({ type(n.Boolean).value(true) })
        val virtual = AttributeI({ type(n.Boolean).value(false) })
        val superUnitFor = AttributeI({ type(CompilationUnit).multi(true) })
        val superUnit = AttributeI({ type(CompilationUnit) })
        val props = AttributeI({ type(Attribute).multi(true).nonFluent("prop") })
        val operations = AttributeI({ type(Operation).multi(true).nonFluent("op") })
        val constructors = AttributeI({ type(Constructor).multi(true).nonFluent("constr") })
    }

    object StructureUnit : Composite({ derivedFrom(Composite) }) {
        val key = AttributeI({ type(n.String).value("") })
        val fullName = AttributeI({ type(n.String).value("") })
        val artifact = AttributeI({ type(n.String).value("") })
    }

    object Constructor : Composite({ derivedFrom(LogicUnit) }) {
        val primary = AttributeI({ type(n.Boolean).value(false) })
    }

    object EnumType : Composite({ derivedFrom(CompilationUnit) }) {
        val literals = AttributeI({ type(Literal).multi(true).nonFluent("lit") })
    }

    object Literal : Composite({ derivedFrom(LogicUnit) })
}


//help model types
open class AttributeI : Item {
    private var type: ItemI = Item.EMPTY
    private var value: Any = ""
    private var nullable: Boolean = false
    private var multi: Boolean = false
    private var nameNonFluent: String = ""
    private var init: AttributeI.() -> Unit

    constructor(value: AttributeI.() -> Unit = {}) {
        this.init = value
    }

    override fun init() {
        init(this)
    }

    fun type(): ItemI = type
    fun type(value: ItemI): AttributeI = apply { type = value }

    fun value(): Any = value
    fun value(aValue: Any): AttributeI = apply { value = aValue }

    fun nullable(): Boolean = nullable
    fun nullable(value: Boolean): AttributeI = apply { nullable = value }

    fun multi(): Boolean = multi
    fun multi(value: Boolean): AttributeI = apply { multi = value }

    fun nonFluent(): String = nameNonFluent
    fun nonFluent(value: String): AttributeI = apply { nameNonFluent = value }
}


open class ItemDsl : Item {
    constructor(init: Item.() -> Unit = {}) : super(init)
}

object n : Composite({ namespace("ee.lang").name("native") }) {
    val Void = Item()
    val Any = Item()
    val Path = Item()
    val Text = Item()
    val Blob = Item()
    val String = Item()
    val Boolean = Item()
    val Int = Item()
    val Long = Item()
    val Float = Item()
    val Date = Item()
    val Exception = Item()
    val Error = Item()
}
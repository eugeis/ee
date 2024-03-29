package ee.lang

object l : Composite({ namespace("ee.lang") }) {

    //from lang_item
    val Item = ItemDsl()
    val MultiHolder = ItemDsl { derivedFrom(Item) }
    val ListMultiHolder = ItemDsl { derivedFrom(MultiHolder) }
    val MapMultiHolder = ItemDsl { derivedFrom(MultiHolder) }
    val Composite = ItemDsl { derivedFrom(MapMultiHolder) }
    val Comment = ItemDsl { derivedFrom(Composite) }

    //lang types
    object MacroComposite : Composite({ derivedFrom(Composite) }) {
        val macrosBefore = AttributeI { type(n.String).multi(true) }
        val macrosBeforeBody = AttributeI { type(n.String).multi(true) }
        val macrosBody = AttributeI { type(n.String).multi(true) }
        val macrosAfterBody = AttributeI { type(n.String).multi(true) }
        val macrosAfter = AttributeI { type(n.String).multi(true) }
        val tags = AttributeI { type(n.String).multi(true) }
    }

    object PropsRef : Composite({ derivedFrom(MacroComposite) }) {
        val all = AttributeI { type(n.Boolean).value(false) }
        val props = AttributeI { type(Attribute).multi(true).nonFluent("prop") }
    }

    object Type : Composite({ derivedFrom(MacroComposite) }) {
        val ifc = AttributeI { type(n.Boolean).value(false) }
        val generics = AttributeI { type(Generic).multi(true).nonFluent("G") }
        val multi = AttributeI { type(n.Boolean).value(false) }
        val defaultValue = AttributeI { type(n.Any).nullable() }
        val open = AttributeI { type(n.Boolean).value(true) }
        val virtual = AttributeI { type(n.Boolean).value(false) }
        val superUnitFor = AttributeI { type(Type).multi(true) }
        val superUnits = AttributeI { type(Type).multi(true) }
        val props = AttributeI { type(Attribute).multi(true).nonFluent("prop") }
        val operations = AttributeI { type(Operation).multi(true).nonFluent("op") }
        val constructors = AttributeI { type(Constructor).multi(true).nonFluent("constr") }
        val macroEmptyInstance = AttributeI { type(n.String).nullable() }

        val nonBlock = AttributeI { type(n.Boolean).value(false) }
        val toStrAll = AttributeI { type(n.Boolean).value(false) }
        val toEqualsAll = AttributeI { type(n.Boolean).value(false) }
    }

    object Generic : Composite({ derivedFrom(Type) }) {
        val type = AttributeI { type(Type) }
    }

    object Lambda : Composite({ derivedFrom(Type) }) {
        val operation = AttributeI { type(Operation) }
    }

    object NativeType : Composite({ derivedFrom(Type) })

    object ExternalType : Composite({ derivedFrom(Type) })

    object Expression : Composite({ derivedFrom(MacroComposite) })

    object Literal : Composite({ derivedFrom(Expression) }) {
        val type = AttributeI { type(Type).value("n.Void") }
        val value = AttributeI { type(n.Any).nullable() }
        val params = AttributeI { type(Attribute).multi(true) }
    }

    object EnumLiteral : Composite({ derivedFrom(Literal) }) {
        val externalName = AttributeI { type(n.String) }.nullable()
    }

    object Attribute : Composite({ derivedFrom(Literal) }) {
        val key = AttributeI { type(n.Boolean).value(false) }
        val unique = AttributeI { type(n.Boolean).value(false) }
        val initByDefaultTypeValue = AttributeI { type(n.Boolean).value(true) }
        val nullable = AttributeI { type(n.Boolean).value(false) }
        val anonymous = AttributeI { type(n.Boolean).value(false) }
        val accessible = AttributeI { type(n.Boolean).nullable() }
        val replaceable = AttributeI { type(n.Boolean).nullable() }
        val meta = AttributeI { type(n.Boolean).value(false) }
        val default = AttributeI { type(n.Boolean).value(false) }
        val fixValue = AttributeI { type(n.Boolean).value(false) }
        val multi = AttributeI { type(n.Boolean).value(false) }
        val hidden = AttributeI { type(n.Boolean).value(false) }
        val mutable = AttributeI { type(n.Boolean).nullable() }
        val length = AttributeI { type(n.Int).nullable() }
        val inherited = AttributeI { type(n.Boolean).value(false) }
        val imploded = AttributeI { type(n.Boolean).value(false) }
        val open = AttributeI { type(n.Boolean).value(false) }
        val concurrent = AttributeI { type(n.Boolean).value(false) }
        val nonFluent = AttributeI { type(n.String) }
        val externalName = AttributeI { type(n.String) }.nullable()

        val toStr = AttributeI { type(n.Boolean).nullable() }
        val toEquals = AttributeI { type(n.Boolean).nullable() }
    }

    object LogicUnit : Composite({ derivedFrom(Expression) }) {
        val virtual = AttributeI { type(n.Boolean).value(false) }
        val superUnit = AttributeI { type(LogicUnit) }
        val params = AttributeI { type(Attribute).multi(true) }
        val visible = AttributeI { type(n.Boolean).value(true) }
        val errorHandling = AttributeI { type(n.Boolean).value(false) }
    }

    object Operation : Composite({ derivedFrom(LogicUnit) }) {
        val generics = AttributeI { type(Generic).multi(true).nonFluent("G") }
        val returns = AttributeI { type(Attribute).multi(true).nonFluent("ret") }
        val err = AttributeI { type(n.Boolean).value(true).doc("error possible") }
        val open = AttributeI { type(n.Boolean).value(true) }
        val nonBlock = AttributeI { type(n.Boolean).nullable() }
    }

    object CompilationUnit : Composite({ derivedFrom(Type) }) {
        val base = AttributeI { type(n.Boolean).value(false) }
    }

    object StructureUnit : Composite({ derivedFrom(MacroComposite) }) {
        val key = AttributeI { type(n.String).value("") }
        val fullName = AttributeI { type(n.String).value("") }
        val artifact = AttributeI { type(n.String).value("") }
    }

    object Constructor : Composite({ derivedFrom(LogicUnit) }) {
        val primary = AttributeI { type(n.Boolean).value(false) }
    }

    object DataType : Composite({ derivedFrom(CompilationUnit) })
    object DataTypeOperation : Composite({ derivedFrom(Operation) })

    object EnumType : Composite({ derivedFrom(DataType) }) {
        val literals = AttributeI { type(EnumLiteral).multi(true).nonFluent("lit") }
    }

    object Basic : Composite({ derivedFrom(DataType) })
    object Values : Composite({ derivedFrom(DataType) })

    //logic
    object Predicate : Composite({ derivedFrom(Expression) })

    object NotPredicate : Composite({ derivedFrom(Predicate) }) {
        val value = AttributeI { type(Predicate) }
    }

    object LeftRightPredicatesPredicate : Composite({ derivedFrom(Predicate) }) {
        val left = AttributeI { type(Predicate) }
        val right = AttributeI { type(Predicate) }
    }

    object LeftRightPredicate : Composite({ derivedFrom(Predicate) }) {
        val left = AttributeI { type(Literal) }
        val right = AttributeI { type(Literal) }
    }

    object LeftRightLiteral : Composite({ derivedFrom(Literal) }) {
        val left = AttributeI { type(Literal) }
        val right = AttributeI { type(Literal) }
    }

    object AndPredicate : Composite({ derivedFrom(LeftRightPredicatesPredicate) })
    object OrPredicate : Composite({ derivedFrom(LeftRightPredicatesPredicate) })
    object EqPredicate : Composite({ derivedFrom(LeftRightPredicate) })
    object NePredicate : Composite({ derivedFrom(LeftRightPredicate) })
    object LtPredicate : Composite({ derivedFrom(LeftRightPredicate) })
    object LtePredicate : Composite({ derivedFrom(LeftRightPredicate) })
    object GtPredicate : Composite({ derivedFrom(LeftRightPredicate) })
    object GtePredicate : Composite({ derivedFrom(LeftRightPredicate) })

    object PlusExpression : Composite({ derivedFrom(LeftRightLiteral) })
    object MinusExpression : Composite({ derivedFrom(LeftRightLiteral) })
    object DivideExpression : Composite({ derivedFrom(LeftRightLiteral) })
    object TimesExpression : Composite({ derivedFrom(LeftRightLiteral) })
    object RemainderExpression : Composite({ derivedFrom(LeftRightLiteral) })
    object IncrementExpression : Composite({ derivedFrom(Literal) })
    object DecrementExpression : Composite({ derivedFrom(Literal) })

    object Action : Composite({ derivedFrom(LogicUnit) })

    object ApplyAction : Composite({ derivedFrom(Action) }) {
        val target = AttributeI { type(Attribute) }
        val value = AttributeI { type(Literal) }
    }

    object AssignAction : Composite({ derivedFrom(ApplyAction) })
    object PlusAssignAction : Composite({ derivedFrom(ApplyAction) })
    object MinusAssignAction : Composite({ derivedFrom(ApplyAction) })
    object TimesAssignAction : Composite({ derivedFrom(ApplyAction) })
    object DivideAssignAction : Composite({ derivedFrom(ApplyAction) })
    object RemainderAssignAction : Composite({ derivedFrom(ApplyAction) })
}


//help model types
open class AttributeI : Item {
    private var type: ItemI<*> = EMPTY
    private var value: Any = ""
    private var nullable: Boolean = false
    private var multi: Boolean = false
    private var nameNonFluent: String = ""
    private var init: AttributeI.() -> Unit

    constructor(value: AttributeI.() -> Unit = {}) {
        this.init = value
    }

    override fun init(): Item {
        init(this)
        return this
    }

    fun type(): ItemI<*> = type
    fun type(value: ItemI<*>): AttributeI = apply { type = value }

    fun value(): Any = value
    fun value(aValue: Any): AttributeI = apply { value = aValue }

    fun isNullable(): Boolean = nullable
    fun nullable(value: Boolean): AttributeI = apply { nullable = value }
    fun nullable(): AttributeI = nullable(true)
    fun notNullable(): AttributeI = nullable(false)

    fun isMulti(): Boolean = multi
    fun multi(value: Boolean): AttributeI = apply { multi = value }
    fun multi(): AttributeI = multi(true)
    fun notMulti(): AttributeI = multi(false)

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
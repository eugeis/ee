package ee.design

import ee.common.EeAny
import ee.common.ext.addReturn
import ee.common.ext.buildLabel
import ee.common.ext.declaredConstuctorWithOneGenericType
import ee.common.ext.ifElse
import java.util.*

interface ElementIfc {
    var name: String
    var parent: ElementIfc
    var derivedFrom: ElementIfc
    fun onNameChanged(old: String, new: String)

    fun <T> findParent(clazz: Class<T>): T? {
        if (parent == ElementIfcEmpty) {
            return null
        } else if (clazz.isInstance(parent)) {
            return parent as T
        } else {
            return parent.findParent(clazz)
        }
    }

    fun isOrDerived(item: ElementIfc) = this == item || derivedFrom == item
}

object ElementIfcEmpty : ElementIfc {
    override var name: String = ""
    override var parent: ElementIfc = ElementIfcEmpty
    override var derivedFrom: ElementIfc = ElementIfcEmpty
    override fun onNameChanged(old: String, new: String) {
    }
}

open class Element : EeAny, ElementIfc, Cloneable {
    var label = buildLabel()
    var fullName: String = ""
    override var derivedFrom: ElementIfc = ElementIfcEmpty
    override var name = ""
        set(value) {
            val old = field
            field = value
            onNameChanged(old, value)
        }
    override var parent: ElementIfc = ElementIfcEmpty
    var doc: Comment? = null

    companion object {
        val EMPTY = ElementIfcEmpty
    }

    constructor() {
    }

    constructor(init: Element.() -> Unit = {}) {
        init()
    }

    constructor(name: String, init: Element.() -> Unit = {}) {
        this.name = name
        init()
    }

    open fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$name")
    }

    open fun render(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }

    open fun <T : Element> derive(init: T.() -> Unit = {}): T {
        val ret = this.clone() as T
        ret.derivedFrom = this
        ret.init()
        return ret
    }

    open fun <T : Element> deriveSubType(init: T.() -> Unit = {}): T {
        val ret = createType<T>()
        ret.name = name
        ret.derivedFrom = this
        ret.init()
        return ret
    }

    open protected fun <T : Element> createType(): T {
        val ret = this.javaClass.newInstance() as T
        return ret
    }

    override fun onNameChanged(old: String, new: String) {
    }

    open fun <T : Element> findUpByType(type: Class<T>, destination: MutableList<T> = ArrayList<T>(),
        alreadyHandled: MutableSet<ElementIfc> = HashSet(), stopSteppingUpIfFound: Boolean = true): List<T> =
        findAcrossByType(type, destination, alreadyHandled, stopSteppingUpIfFound) { parent }


    open fun <T : Element> findAcrossByType(type: Class<T>, destination: MutableList<T> = ArrayList<T>(),
        alreadyHandled: MutableSet<ElementIfc> = HashSet(), stopSteppingUpIfFound: Boolean = true,
        acrossSelector: ElementIfc.() -> ElementIfc): List<T> {
        val baseElement = acrossSelector()
        if (!alreadyHandled.contains(baseElement)) {
            alreadyHandled.add(baseElement)
            if (type.isInstance(baseElement) && !destination.contains(baseElement)) {
                destination.add(baseElement as T)
                if (!stopSteppingUpIfFound) findAcrossByType(type, destination, alreadyHandled, stopSteppingUpIfFound,
                    acrossSelector)
            } else if (baseElement is CompositeD) {
                baseElement.children.filterIsInstanceTo(destination, type)
            }
            if (baseElement is Element) baseElement.findAcrossByType(type, destination, alreadyHandled,
                stopSteppingUpIfFound, acrossSelector)
        }
        return destination
    }

    override fun fillToStringType(b: StringBuffer) {
        if (name.isNotEmpty()) {
            b.append(name).append(TYPE_NAME_SEPARATOR)
        }
        super.fillToStringType(b)
    }
}


open class Composite : CompositeT<CompositeD> {
    constructor(init: CompositeD.() -> Unit = {}) : super(CompositeD(init))
}

open class CompositeT<T : CompositeD> : DslType<T> {
    constructor(data: T) : super(data)

    open fun <T : Element> findAllByType(type: Class<T>): List<T> = _d.findAllByType(type)

    open fun <T : ElementIfc> findDownByType(type: Class<T>, destination: MutableList<T> = ArrayList<T>(),
        alreadyHandled: MutableSet<ElementIfc> = HashSet(), stopSteppingDownIfFound: Boolean = true): List<T> =
        _d.findDownByType(type, destination, alreadyHandled, stopSteppingDownIfFound)
}

open class CompositeD : Element {
    val children: MutableList<ElementIfc> = arrayListOf<ElementIfc>()
    val attributes: Map<String, ElementIfc> = hashMapOf<String, ElementIfc>()

    constructor() : super()

    constructor(init: CompositeD.() -> Unit = {}) : super() {
        init()
    }

    constructor(name: String, init: CompositeD.() -> Unit = {}) : super(name) {
        init()
    }

    open fun <T : ElementIfc> add(child: T, init: T.() -> Unit = {}): T {
        child.parent = this
        child.init()
        children.add(child)
        return child
    }

    open fun <T : ElementIfc> addText(child: T, init: T.() -> String): T {
        child.parent = this
        children.add(initTextElement(child, init))
        return child
    }

    open fun <T : Element> findAllByType(type: Class<T>): List<T> {
        return children.filterIsInstance(type)
    }

    open fun <T : ElementIfc> findDownByType(type: Class<T>, destination: MutableList<T> = ArrayList<T>(),
        alreadyHandled: MutableSet<ElementIfc> = HashSet(), stopSteppingDownIfFound: Boolean = true): List<T> {
        for (item in children) {
            if (!alreadyHandled.contains(item)) {
                alreadyHandled.add(item)
                if (type.isInstance(item)) {
                    if (!destination.contains(item)) {
                        destination.add(item as T)
                    }
                    if (!stopSteppingDownIfFound && item is CompositeD) item.findDownByType(type, destination,
                        alreadyHandled, stopSteppingDownIfFound)
                } else if (item is CompositeD) item.findDownByType(type, destination, alreadyHandled,
                    stopSteppingDownIfFound)
            }
        }
        return destination
    }

    open fun sortChildrenByName() {
        children.sortBy { it.name }
    }

    override fun fillToStringType(b: StringBuffer) {
        if (children.isNotEmpty()) {
            b.append("children.size=").append(children.size).append(TYPE_NAME_SEPARATOR)
        }
        if (attributes.isNotEmpty()) {
            b.append("attributes.size=").append(attributes.size).append(TYPE_NAME_SEPARATOR)
        }
        super.fillToStringType(b)
    }

    override fun render(builder: StringBuilder, indent: String) {
        super.render(builder, indent)
        renderAttributes(builder)
        renderChildren(builder, indent)
    }

    protected open fun renderChildren(builder: StringBuilder, indent: String) {
        builder.append(" {\n")
        children.filterIsInstance(Element::class.java).forEach {
            it.render(builder, indent + "  ")
        }
        builder.append("$indent}\n")
    }

    protected open fun renderAttributes(builder: StringBuilder) {
        for (a in attributes.keys) {
            builder.append(" $a=\"${attributes[a]}\"")
        }
    }

    override fun render(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }
}

class TextElement(val text: String, val newLine: Boolean = false) : Element("") {
    override fun render(builder: StringBuilder, indent: String) {
        if (newLine) builder.append("$indent$text\n") else builder.append("$indent$text")
    }
}

fun doc(text: String) = Comment(text)

abstract class TextComposite : CompositeD {
    constructor() : super()

    open operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }
}

open class Comment : TextComposite {
    companion object {
        val EMPTY = Comment()
    }

    constructor() : super()

    constructor(text: String) : super() {
        children.add(TextElement(text))
    }
}


fun <T : ElementIfc> initTextElement(child: T, init: T.() -> String): T {
    val ret = child.init()
    if (ret.isNotEmpty()) {
        if (child is CompositeD) {
            child.children.add(TextElement(ret))
        }
    }
    return child
}

fun <T : CompositeT<*>> T.initObjectTree(): T {
    val data = _d
    if (data.name.isBlank()) {
        data.name = buildLabel().name
    }
    for (f in javaClass.declaredFields) {
        try {
            val getter = javaClass.declaredMethods.find { it.name == "get${f.name.capitalize()}" }
            if (getter != null) {
                val prop = getter.invoke(this)
                if (prop is ElementIfc) {
                    if (prop.name.isBlank()) {
                        prop.name = f.name
                    }
                    if (!data.children.contains(prop)) {
                        data.add(prop)
                    }
                }
            }
        } catch (e: Exception) {
            println("$f $e")
        }

    }
    javaClass.declaredClasses.forEach {
        val child = it.findInstance()
        if (child != null) {
            if (child is ElementIfc && !data.children.contains(child)) {
                data.add(child)
            } else if (child is CompositeT<*>) {
                data.add(child._d)
                child.initObjectTree()
            }
        }
    }
    return this
}

fun <T : CompositeD> T.initObjectTree(): T {
    if (name.isBlank()) {
        name = label.name
    }
    for (f in javaClass.declaredFields) {
        try {
            val getter = javaClass.declaredMethods.find { it.name == "get${f.name.capitalize()}" }
            if (getter != null) {
                val prop = getter.invoke(this)
                if (prop is ElementIfc) {
                    if (prop.name.isBlank()) {
                        prop.name = f.name
                    }
                    if (!children.contains(prop)) {
                        add(prop)
                    }
                }
            }
        } catch (e: Exception) {
            println("$f $e")
        }

    }
    javaClass.declaredClasses.forEach {
        val child = it.findInstance()
        if (child != null) {
            if (child is ElementIfc && !children.contains(child)) {
                add(child)
                if (child is CompositeD) {
                    child.initObjectTree()
                }
            }
        }
    }
    return this
}

fun <T> Class<T>.findInstance(): Any? {
    try {
        return getField("INSTANCE").get(null)
    } catch (e: NoSuchFieldException) {
        return null
    }
}


fun ifElse(condition: Boolean, t: () -> String, f: () -> String): String {
    if (condition) return f() else return f()
}

interface IsBase {
    val base: Boolean
}


interface TypeIfc : ElementIfc {
    val namespace: String
    var generics: MutableList<GenericD>

    fun findGeneric(genName: String): GenericD? {
        return generics.find { it.name == genName } ?: findParent(LogicUnit::class.java)?.findGeneric(genName)
    }
}


fun <T : Element> List<T>.extend(init: T.() -> Unit = {}) {
    forEach { it.init() }
}

fun <T : Element> List<T>.derive(init: T.() -> Unit = {}): List<T> {
    return map { it.derive(init) }
}

class TypeDerived<out T : TypeIfc>(val delegate: T, val aName: String, override val base: Boolean = false) :
        TypeIfc by delegate, IsBase {
    override var name: String = ""
        get() = aName
}

open class Generic : TypeT<GenericD> {
    constructor(name: String, type: TypeIfc, init: GenericD.() -> Unit = {}) : super(GenericD(name, type, init))
}

open class GenericT<T : GenericD> : TypeT<T> {
    constructor(data: T) : super(data)
}

open class GenericD : TypeD {
    var type: TypeIfc = t.Any._d

    constructor() : super()
    constructor(name: String, type: TypeIfc, init: GenericD.() -> Unit = {}) : super(name) {
        this.type = type
        init()
    }
}

open class Type : TypeT<TypeD> {
    constructor(init: TypeD.() -> Unit = {}) : super(TypeD(init))
}

open class TypeT<T : TypeD> : CompositeT<T> {
    constructor(data: T) : super(data)

    fun G(type: TypeT<*> = t.Any, name: String = "", init: GenericD.() -> Unit = {}): GenericD =
        _d.G(type._d, name, init)

    fun G(name: String, type: TypeT<*> = t.Any, init: GenericD.() -> Unit = {}): GenericD = _d.G(name, type._d, init)

    fun <R : TypeT<*>> T(vararg types: TypeT<*>): R = derive(_d.T(types.map { it._d }.toList()))
}

open class TypeD : CompositeD, TypeIfc {
    override var namespace: String = ""
    override var generics: MutableList<GenericD> = arrayListOf()
    var _defaultValue: Any? = null
    var multi: Boolean = false

    constructor() : super()
    constructor(name: String, namespace: String) : super(name) {
        this.namespace = namespace
    }

    constructor(init: TypeD.() -> Unit = {}) : super() {
        init()
    }

    constructor(name: String, init: TypeD.() -> Unit = {}) : super(name) {
        init()
    }

    fun G(type: TypeIfc = t.Any._d, name: String = "", init: GenericD.() -> Unit = {}): GenericD =
        generics.addReturn(add(GenericD(name, type, init)))

    fun G(name: String, type: TypeIfc = t.Any._d, init: GenericD.() -> Unit = {}): GenericD =
        generics.addReturn(add(GenericD(name, type, init)))


    fun <T : TypeD> T(types: List<TypeIfc>): T {
        if (generics.size >= types.size) {
            var i = 0
            val ret = derive<T> {
                for (type in types) {
                    if (type is GenericD) {
                        generics[i++] = type
                    } else {
                        generics[i++].type = type
                    }
                }
            }
            return ret
        } else {
            throw IllegalArgumentException("To many generic types")
        }
    }

    override fun clone(): Any {
        val ret = super.clone() as TypeD
        ret.generics = generics.map { it.clone() as GenericD }.toMutableList()
        return ret
    }

    override fun renderChildren(builder: StringBuilder, indent: String) {
    }
}

open class Lambda : LambdaT<LambdaD> {
    constructor(op: Operation, init: LambdaD.() -> Unit = {}) : super(LambdaD(op, init))
}

open class LambdaT<T : TypeD> : TypeT<T> {
    constructor(data: T) : super(data)
}

open class LambdaD : TypeD {
    val op: Operation

    constructor(op: Operation, init: LambdaD.() -> Unit = {}) : super(op.name) {
        this.op = op
        init()
    }
}

open class NativeType : NativeTypeT<NativeTypeD> {
    constructor(init: NativeTypeD.() -> Unit = {}) : super(NativeTypeD(init))
    constructor(name: String, init: NativeTypeD.() -> Unit = {}) : super(NativeTypeD(name, init))
}

open class NativeTypeT<T : TypeD> : TypeT<T> {
    constructor(data: T) : super(data)
}

open class NativeTypeD : TypeD {
    constructor(init: NativeTypeD.() -> Unit = {}) : super() {
        init()
    }

    constructor(name: String, init: NativeTypeD.() -> Unit = {}) : super(name) {
        init()
    }
}

open class ExternalType : ExternalTypeT<ExternalTypeD> {
    constructor(init: ExternalTypeD.() -> Unit = {}) : super(ExternalTypeD(init))
}

open class ExternalTypeT<T : ExternalTypeD> : TypeT<T> {
    constructor(data: T) : super(data)
}

open class ExternalTypeD : TypeD {
    constructor(init: ExternalTypeD.() -> Unit = {}) : super() {
        init()
    }
}

class Ref<T : Element>(ref: String, target: T? = null) : Element(ref) {
    val setters: MutableList<(Ref<T>) -> Unit> = arrayListOf()
    var target: T? = target
        set(value) {
            field = value
            onUpdate()
        }

    fun onUpdate() {
        for (setter in setters) {
            setter(this)
        }
    }

    fun resolve(setter: (Ref<T>) -> Unit) {
        addListener(setter)
        if (target != null) {
            setter(this)
        }
    }

    fun addListener(setter: (Ref<T>) -> Unit) {
        setters.add(setter)
    }

    fun withTarget(target: T): Ref<T> {
        this.target = target
        return this
    }
}

open class Attribute : Element {
    //key or part of a key, e.g. primary key
    var type: TypeD = t.Void._d
    var key: Boolean = false
    var unique: Boolean = false
    var value: Any? = null
    var initByDefaultTypeValue: Boolean = true
    var nullable: Boolean = false
    var accessible: Boolean = true
    var replaceable: Boolean = true
    var meta: Boolean = false
    var hidden: Boolean = false
    var mutable: Boolean = true
    var length: Integer? = null
    var inherited: Boolean = false
    var open: Boolean = false

    constructor(name: String, type: TypeD = t.Void._d, init: (Attribute.() -> Unit)? = null) : super(name) {
        this.type = type
        if (init != null) {
            init()
        }
    }

    open fun calculate() {
    }
}

abstract class LogicUnit : TextComposite {
    var virtual = false
    var superUnit: LogicUnit? = null
    var params: List<Attribute> = arrayListOf()

    constructor() : super()
    constructor(params: List<Attribute> = emptyList(), init: LogicUnit.() -> Unit = {}) : super() {
        this.params = params
        init()
    }

    fun param(name: String = "", type: TypeD = t.String._d, init: Attribute.() -> Unit = {}): Attribute {
        if (params !is MutableList<Attribute>) {
            params = arrayListOf()
        }
        return (params as MutableList<Attribute>).addReturn(add(Attribute(name, type, init)))
    }

    fun param(type: TypeD = t.String._d, name: String = "", init: Attribute.() -> Unit = {}): Attribute {
        if (params !is MutableList<Attribute>) {
            params = arrayListOf()
        }
        return (params as MutableList<Attribute>).addReturn(add(Attribute(name, type, init)))
    }

    protected open fun deriveName(prefix: String) {
        if (name.isEmpty()) {
            name = params.joinToString("", prefix) { it.name }
        }
    }

    open fun findGeneric(genName: String): GenericD? {
        return findParent(TypeIfc::class.java)?.findGeneric(genName)
    }

    override fun <T : Element> deriveSubType(init: T.() -> Unit): T {
        val ret = derive(init)
        if (ret is LogicUnit) {
            ret.superUnit = this
        }
        return ret
    }
}

open class Operation : LogicUnit {
    companion object {
        val EMPTY = Operation()
    }

    var generics: MutableList<GenericD> = arrayListOf()
    var ret: Attribute = t.void

    constructor() : super()
    constructor(params: List<Attribute> = emptyList(), ret: Attribute = t.void,
        init: Operation.() -> Unit = {}) : super(params) {
        this.ret = ret
        init()
    }

    override fun findGeneric(genName: String): GenericD? {
        return generics.find { it.name == genName } ?: super.findGeneric(genName)
    }

    fun G(type: TypeIfc = t.Any._d, name: String = "", init: GenericD.() -> Unit = {}): GenericD =
        generics.addReturn(add(GenericD(name, type, init)))

    fun G(name: String, type: TypeIfc = t.Any._d, init: GenericD.() -> Unit = {}): GenericD =
        generics.addReturn(add(GenericD(name, type, init)))

    operator fun invoke(vararg initParams: Attribute): DelegateOperation {
        return DelegateOperation(this, { params = initParams.toList() })
    }

    operator fun invoke(op: Operation): DelegateOperation {
        val ret = DelegateOperation(op)
        ret.name = this.name
        ret.ret = this.ret
        return ret
    }
}

open class DelegateOperation : Operation {
    var operation: Operation = Operation.EMPTY
    val openParams: List<Attribute> by lazy { operation.params.filter { opParam -> params.find { opParam.name == it.name || opParam.type.name == it.type.name } == null } }

    constructor() : super()
    constructor(operation: Operation, init: DelegateOperation.() -> Unit = {}) : super() {
        this.operation = operation
        init()
    }


}

//helper design functions
fun lambda(vararg params: Attribute, ret: Attribute = t.void, init: Operation.() -> Unit = {}): Lambda =
    Lambda(Operation(params.toList(), ret, init))

fun param(name: String, type: TypeT<*> = t.String, init: Attribute.() -> Unit = {}): Attribute {
    return Attribute(name, type._d, init)
}

fun param(name: Attribute, init: PropAttribute.() -> Unit = {}): PropAttribute {
    return PropAttribute(name, init)
}

fun ret(type: TypeT<*> = t.String, init: Attribute.() -> Unit = {}): Attribute = param("ret", type, init)

val refs = arrayListOf<Ref<*>>()
fun <T : Element> ref(name: String, target: T? = null): Ref<T> {
    val ret = refs.addReturn(Ref<T>(name, target)) as Ref<T>
    /*
    ret.addListener {
        println("Resolved($it)")
    }*/
    return ret
}


open class PropAttribute(var prop: Attribute) : Attribute(prop.name, prop.type) {
    constructor(prop: Attribute, init: (PropAttribute.() -> Unit)? = null) : this(prop) {
        key = prop.key
        unique = prop.unique
        value = prop.value
        initByDefaultTypeValue = prop.initByDefaultTypeValue
        nullable = prop.nullable
        accessible = prop.accessible
        replaceable = prop.replaceable
        meta = prop.meta
        hidden = prop.hidden
        mutable = prop.mutable
        length = prop.length
        inherited = prop.inherited

        if (init != null) {
            init()
            calculate()
        }
    }
}

open class DslType<T : ElementIfc> : Cloneable {
    val _d: T

    constructor(data: T) {
        this._d = data
    }

    open protected fun <R : DslType<*>> derive(data: T): R {
        val ctor = javaClass.declaredConstuctorWithOneGenericType()
                ?: javaClass.superclass.declaredConstuctorWithOneGenericType()
        if (ctor != null) {
            return ctor!!.newInstance(data) as R
        } else {
            throw IllegalArgumentException("There is no consturctors for $data")
        }

    }
}

open class CompilationUnit : CompilationUnitT<CompilationUnitD> {
    constructor(data: CompilationUnitD) : super(data)
    constructor(init: CompilationUnitD.() -> Unit = {}) : super(CompilationUnitD(init))
}

open class CompilationUnitT<T : CompilationUnitD> : TypeT<T> {
    constructor(data: T) : super(data)

    fun prop(type: TypeT<*> = t.String, name: String = "", init: Attribute.() -> Unit = {}): Attribute =
        _d.prop(type._d, name, init)

    fun constructorAll() = _d.constructorAll()

    fun constructor(vararg params: Attribute, init: Constructor.() -> Unit = {}) = _d.constructor(params.toList(), init)

    fun op(vararg params: Attribute, ret: Attribute = t.void, init: Operation.() -> Unit = {}): Operation =
        _d.op(params.toList(), ret, init)

    fun op(operation: DelegateOperation): Operation = _d.op(operation)
}

open class CompilationUnitD : TypeD, IsBase {
    override var base: Boolean = false
    var open = true
    var virtual = false
    var superUnitFor: MutableList<CompilationUnitD> = arrayListOf()
    var superUnit: CompilationUnitD? = null
        set(value) {
            if (field != null) {
                field!!.superUnitFor.remove(this)
            }
            field = value
            value?.superUnitFor?.add(this)
        }

    val props: MutableList<Attribute> = arrayListOf()
    val operations: MutableList<Operation> = arrayListOf()
    val delegates: MutableList<DelegateOperation> = arrayListOf()
    val constructors: MutableList<Constructor> = arrayListOf()

    val propsAll: MutableList<Attribute> by lazy {
        if (superUnit != null) {
            val ret = mutableListOf<Attribute>()
            val myType = this
            superUnit?.propsAll?.mapTo(ret, {
                it.derive {
                    inherited = true

                    //use my generic specialization, if available
                    if (type is Generic) {
                        val myGenericType = myType.findGeneric(type.name)
                        if (myGenericType != null) {
                            type = myGenericType
                        }
                    }
                }
            })
            ret.addAll(props)
            ret
        } else {
            props
        }
    }

    constructor() : super()
    constructor(init: CompilationUnitD.() -> Unit = {}) : super() {
        init()
    }

    constructor(name: String, init: CompilationUnitD.() -> Unit = {}) : super(name) {
        init()
    }

    fun superUnit(superUnitT: CompilationUnitT<*>?) {
        superUnit = superUnitT?._d
    }

    fun prop(type: TypeD = t.String._d, name: String = "", init: Attribute.() -> Unit = {}): Attribute =
        props.addReturn(add(Attribute(name, type, init)))

    fun constructorAll() = constructors.addReturn(add(Constructor(propsAll.filter { !it.meta }.map { param(it) })))

    fun constructor(params: List<Attribute>, init: Constructor.() -> Unit = {}) =
        constructors.addReturn(add(Constructor(params.toList(), init)))

    fun op(params: List<Attribute>, ret: Attribute = t.void, init: Operation.() -> Unit = {}): Operation =
        operations.addReturn(add(Operation(params.toList(), ret, init)))

    fun op(operation: DelegateOperation): Operation = delegates.addReturn(add(operation))

    override fun <T : Element> deriveSubType(init: T.() -> Unit): T {
        val ret = super.deriveSubType(init)
        if (ret is CompilationUnitD) {
            ret.superUnit = this
            this.constructors.forEach {
                val derivedConstructor = it.deriveSubType<Constructor>()
                ret.constructors.add(ret.add(derivedConstructor))
            }
        }
        return ret
    }

    override fun onNameChanged(old: String, new: String) {
        if ((namespace == null || namespace.isBlank()) && parent != null) {
            if (parent is StructureUnit) {
                namespace = (parent as StructureUnitD).namespace
            } else if (parent is Type) {
                namespace = (parent as TypeD).namespace
            }
        }
    }
}

open class CompilationUnitForDsl : CompilationUnit {
    constructor() : super()
}

open class StructureUnit : StructureUnitT<StructureUnitD> {
    constructor(init: StructureUnitD.() -> Unit = {}) : super(StructureUnitD(init))
    constructor(namespace: String, init: StructureUnitD.() -> Unit = {}) : super(StructureUnitD(namespace, init))
}

open class StructureUnitT<T : StructureUnitD> : CompositeT<T> {
    constructor(data: T) : super(data)

    fun type(init: ExternalTypeD.() -> Unit = {}): ExternalTypeD = _d.type(init)
}

open class StructureUnitD : CompositeD {
    var namespace: String = ""
    var artifact: String = ""
    override var name = ""
        set(value) {
            field = value
            if (fullName.isBlank()) {
                fullName = value
            }
            if (namespace.isBlank()) {
                if (parent == Element.EMPTY) {
                    namespace = name.toLowerCase()
                } else if (parent is StructureUnit) {
                    namespace = (parent as StructureUnitD).deriveNamespace(name.toLowerCase())
                }
            }
            if (artifact.isBlank()) {
                if (parent == Element.EMPTY) {
                    artifact = name.toLowerCase()
                } else if (parent is StructureUnit) {
                    artifact = (parent as StructureUnitD).deriveArtifact(name.toLowerCase())
                }
            }
        }

    protected fun deriveNamespace(name: String) =
        (this.namespace.endsWith(name) || "shared".equals(name, true)).ifElse(this.namespace,
            { "${this.namespace}.$name" })

    protected fun deriveArtifact(name: String) =
        (this.artifact.endsWith(name)).ifElse(this.artifact, { "${this.artifact}-$name" })

    constructor() : super()
    constructor(init: StructureUnitD.() -> Unit = {}) : super() {
        init()
    }

    constructor(namespace: String, init: StructureUnitD.() -> Unit = {}) : super() {
        this.namespace = namespace
        init()
    }

    fun type(init: ExternalTypeD.() -> Unit = {}): ExternalTypeD {
        return add(ExternalTypeD(init), {
            if (namespace.isEmpty()) {
                namespace = this@StructureUnitD.namespace
            }
        })
    }
}

open class Constructor : LogicUnit {
    companion object {
        val EMPTY = Constructor()
    }

    constructor() : super()
    constructor(params: List<Attribute> = emptyList(), init: Constructor.() -> Unit = {}) : super(params) {
        init()
    }
}

open class EnumType : EnumTypeT<EnumTypeD> {
    constructor(init: EnumTypeD.() -> Unit = {}) : super(EnumTypeD(init))
}

open class EnumTypeT<T : EnumTypeD> : CompilationUnitT<T> {
    constructor(data: T) : super(data)

    fun lit(init: Literal.() -> Unit = {}) = _d.lit(init)
    fun lit(vararg params: Attribute, init: Literal.() -> Unit = {}) = _d.lit(params.toList(), init)
}

open class EnumTypeD : CompilationUnitD {
    val literals: MutableList<Literal> = arrayListOf()

    constructor() : super()
    constructor(init: EnumTypeD.() -> Unit = {}) : super() {
        init()
    }

    fun lit(init: Literal.() -> Unit = {}) = literals.addReturn(add(Literal(init)))

    fun lit(params: List<Attribute>, init: Literal.() -> Unit = {}) = literals.addReturn(add(Literal(params, init)))

    override fun <T : Element> createType(): T {
        return EnumType() as T
    }
}

open class Literal : LogicUnit {
    constructor(init: Literal.() -> Unit = {}) : super()
    constructor(params: List<Attribute> = emptyList(), init: Literal.() -> Unit = {}) : super(params) {
        init()
    }
}

//for other DSLs
object t : StructureUnit("ee") {
    //types
    val Void = NativeType()
    val Any = NativeType("Any")
    val Path = NativeType("Path")
    val Text = NativeType("Text")
    val Blob = NativeType("Blob")
    val String = NativeType("String")
    val Boolean = NativeType("Boolean")
    val Integer = NativeType("Integer")
    val Long = NativeType("Long")
    val Float = NativeType("Float")
    val Date = NativeType("Date")
    val Exception = NativeType("Exception")
    val Error = NativeType("Throwable")

    object TimeUnit : EnumType() {
        val Milliseconds = lit()
        val Nanoseconds = lit()
        val Microseconds = lit()
        val Seconds = lit()
        val Minutes = lit()
        val Hours = lit()
        val Days = lit()
    }

    object Class : NativeType("Class") {
        val T = G()
    }

    object List : NativeType("List", { multi = true }) {
        val T = G(String)
    }

    object Map : NativeType("Map", { multi = true }) {
        val K = G(String)
        val V = G(String)
    }

    object Comment : CompilationUnitForDsl() {}

    object ElementIfc : CompilationUnitForDsl() {
        val name = prop()
        val secondConstructor = constructor(name)
    }

    object TypeIfc : CompilationUnitForDsl() {
        val name = prop()
        val secondConstructor = constructor(name)
    }

    object Element : CompilationUnitForDsl() {
        val name = prop()
        val secondConstructor = constructor(name)
    }

    object Composite : CompilationUnitForDsl() {
        val name = prop()
        val secondConstructor = constructor(name)
    }

    object CompilationUnit : CompilationUnitForDsl() {
        val name = prop()
        val secondConstructor = constructor(name)
    }

    object Operation : CompilationUnitForDsl() {}

    object StructureUnit : CompilationUnitForDsl() {
        val name = prop()
        val secondConstructor = constructor(name)
    }

    //attributes
    val void = Attribute("ret", Void._d)
}


object td : StructureUnitD("ee") {
    //types
    val Void = t.Void._d
    val Any = t.Any._d
    val Path = t.Path._d
    val Text = t.Text._d
    val Blob = t.Blob._d
    val String = t.String._d
    val Boolean = t.Boolean._d
    val Integer = t.Integer._d
    val Long = t.Long._d
    val Float = t.Float._d
    val Date = t.Date._d
    val Exception = t.Exception._d
    val Error = t.Error._d

    val TimeUnit = t.TimeUnit._d
    val Class = t.Class._d
    val List = t.List._d
    val Map = t.Map._d
    val Comment = t.Comment._d
    val ElementIfc = t.ElementIfc._d
    val TypeIfc = t.TypeIfc._d
    val Element = t.Element._d
    val Composite = t.Composite._d
    val CompilationUnit = t.CompilationUnit._d
    val Operation = t.Operation
    val StructureUnit = t.StructureUnit

    //attributes
    val void = t.void
}
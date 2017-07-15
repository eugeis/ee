package ee.lang

import ee.common.ext.ifElse

open class LangDerivedKindNames {
    val API = "Api"
    val IMPL = "Impl"
}

object LangDerivedKind : LangDerivedKindNames()

fun ItemI.parentNameAndName(): String = storage.getOrPut(this, "parentNameAndName", {
    val parent = findParent(DataType::class.java)
    if (parent != null) {
        val regexp = "(\\B[A-Z])".toRegex()
        if (regexp.containsMatchIn(name())) name().replaceFirst(regexp, "${parent.name().capitalize()}$1") else
            "${parent.name()}${name().capitalize()}"
    } else {
        name()
    }
})

fun ItemI.nameAndParentName(): String = storage.getOrPut(this, "nameAndParentName", {
    val parent = findParent(DataType::class.java)
    if (parent != null) {
        val regexp = "(\\B[A-Z])".toRegex()
        if (regexp.containsMatchIn(name())) name().replaceFirst(regexp, "$1${parent.name().capitalize()}") else
            "${name()}${parent.name().capitalize()}"
    } else {
        name()
    }
})

fun <T : LogicUnitI> T.findGeneric(name: String): GenericI? = findParent(TypeI::class.java)?.findGeneric(name)

fun <T : LogicUnitI> T.paramsWithOut(superUnit: LogicUnitI)
        = params().filter { param -> superUnit.params().firstOrNull { it.name() == param.name() } == null }

fun <T : TypeI> T.findGeneric(name: String): GenericI? =
        generics().find { it.name() == name } ?: findParent(LogicUnitI::class.java)?.findGeneric(name)

fun CompilationUnitI.primaryConstructor(): ConstructorI = storage.getOrPut(this, "primaryConstructor", {
    constructors().filter { it.primary() }.firstOrNull() ?: Constructor.EMPTY
})

fun CompilationUnitI.primaryOrFirstConstructor(): ConstructorI = storage.getOrPut(this, "primaryOrFirstConstructor", {
    constructors().filter { it.primary() }.firstOrNull() ?: constructors().firstOrNull() ?: Constructor.EMPTY
})

fun CompilationUnitI.otherConstructors(): List<ConstructorI> = storage.getOrPut(this, "otherConstructors", {
    constructors().filterNot { it.primary() }
})

fun ConstructorI.props(): List<AttributeI> = storage.getOrPut(this, "props", {
    params().filter { it.derivedFrom().isNotEMPTY() }
})

fun CompilationUnitI.propsExceptPrimaryConstructor(): List<AttributeI> = storage.getOrPut(this,
        "propsExceptPrimaryConstructor", {
    if (primaryConstructor().isNotEMPTY()) props().filter { prop ->
        primaryConstructor().props().find { it.name() == prop.name() } == null
    } else props()
})

fun CompilationUnitI.propsSuperUnit(): List<AttributeI> = storage.getOrPut(this, "propsSuperUnit", {
    propsAll().filter { !it.inherited() }
})

fun CompilationUnitI.propsAll(): List<AttributeI> = storage.getOrPut(this, "propsAll", {
    if (superUnit().isNotEMPTY()) {
        val ret = mutableListOf<AttributeI>()
        val myType = this
        superUnit().propsAll().mapTo(ret, {
            it.derive {
                inherited(true)

                //use my generic specialization, if available
                if (type() is GenericI) {
                    val myGenericType = myType.findGeneric(type().name())
                    if (myGenericType != null) {
                        type(myGenericType)
                    }
                }
            }
        })
        ret.addAll(props())
        ret
    } else {
        props()
    }
})

//helper design functions
/*
fun lambda(init: LambdaI.() -> Unit): LambdaI = Lambda(init)

fun p(init: AttributeI.() -> Unit = {}): AttributeI = Attribute(init)

fun p(name: String, type: TypeI = n.String, body: AttributeI.() -> Unit = {}): AttributeI = Attribute({
    type(type).name(name)
    body()
})


fun ret(type: TypeI = n.String, body: AttributeI.() -> Unit = {}): AttributeI = p {
    type(type).name("ret")
    body()
}
*/

fun lambda(vararg params: AttributeI, body: OperationI.() -> Unit = {}): LambdaI = Lambda({
    operation(Operation({
        params(*params)
        body()
    }))
})

interface TypedAttributeI<T : TypeI> : AttributeI {
    fun sub(subType: T.() -> AttributeI): AttributeI {
        //TODO create new structure with parent and sub type
        return (type() as T).subType()
    }

    override fun type(): T
    fun typeT(value: T): TypedAttributeI<T>
}

open class TypedAttribute<T : TypeI> : Attribute, TypedAttributeI<T> {
    constructor(value: TypedAttribute<T>.() -> Unit = {}) : super(value as Composite.() -> Unit)

    override fun type(): T {
        return super.type() as T
    }

    override fun typeT(value: T): TypedAttributeI<T> = apply { type(value) }
}

fun p(init: AttributeI.() -> Unit = {}): AttributeI = Attribute(init)

fun p(name: String, type: TypeI = n.String, body: AttributeI.() -> Unit = {}): AttributeI = Attribute({
    type(type).name(name)
    body()
})

fun p(name: AttributeI, init: AttributeI.() -> Unit = {}): AttributeI = name.derive(init)

fun CompilationUnit.propS(value: AttributeI.() -> Unit = {}): AttributeI = prop(Attribute({
    type(n.String)
    value
}))

fun CompilationUnit.propB(value: AttributeI.() -> Unit = {}): AttributeI = prop(Attribute({
    type(n.Boolean)
    value
}))

fun CompilationUnit.propI(value: AttributeI.() -> Unit = {}): AttributeI = prop(Attribute({
    type(n.Int)
    value
}))


fun CompilationUnit.propL(value: AttributeI.() -> Unit = {}): AttributeI = prop(Attribute({
    type(n.Long)
    value
}))

fun CompilationUnit.propF(value: AttributeI.() -> Unit = {}): AttributeI = prop(Attribute({
    type(n.Float)
    value
}))


fun CompilationUnit.propDT(value: AttributeI.() -> Unit = {}): AttributeI = prop(Attribute({
    type(n.Date)
    value
}))

fun <T : TypeI> CompilationUnitI.prop(type: T): TypedAttributeI<T> {
    val ret = TypedAttribute<T>({ type(type) })
    props(ret)
    return ret
}

/*
fun <T : TypeI> LogicUnitI.param(init: TypedAttributeI<T>.() -> Unit = {}): TypedAttributeI<T> {
    val ret = TypedAttribute<T>(init)
    params(ret)
    return ret
}

fun <T : TypeI> LogicUnitI.paramT(type: T): TypedAttributeI<T> {
    val ret = TypedAttribute<T>({ type(type) })
    params(ret)
    return ret
}
*/

fun AttributeI.accessibleAndMutable(): Boolean = storage.getOrPut(this, "accessibleAndMutable", {
    accessible() && mutable()
})

fun <T : CompositeI> T.defineConstructorAllPropsForNonConstructors() {
    findDownByType(CompilationUnitI::class.java, stopSteppingDownIfFound = false).filter { it.constructors().isEmpty() }
            .extend { constructorAllProps() }
}

fun <T : CompositeI> T.defineConstructorOwnPropsOnlyForNonConstructors() {
    findDownByType(CompilationUnitI::class.java, stopSteppingDownIfFound = false).filter { it.constructors().isEmpty() }
            .extend { constructorOwnPropsOnly() }
}

fun <T : CompositeI> T.defineSuperUnitsAsAnonymousProps() {
    findDownByType(CompilationUnitI::class.java, stopSteppingDownIfFound = false).filter { it.superUnit().isNotEMPTY() }
            .extend {
                val item = this
                prop({ type(item.superUnit()).anonymous(true).name(item.superUnit().name()) })
            }
}

fun <T : CompositeI> T.declareAsBaseWithNonImplementedOperation() {
    findDownByType(CompilationUnitI::class.java).filter { it.operations().isNotEMPTY() && !it.base() }.forEach { it.base(true) }
}

fun <T : CompositeI> T.prepareAttributesOfEnums() {
    findDownByType(EnumTypeI::class.java).forEach { it.props().forEach { it.replaceable(false).initByDefaultTypeValue(false) } }
}

fun <T : CompilationUnitI> T.constructorAllProps(): ConstructorI {
    val constrProps = propsAll().filter { !it.meta() }.map { p(it) }
    val primary = this is EnumTypeI
    return if (constrProps.isNotEmpty()) constr {
        parent(this@constructorAllProps)
        primary(primary).params(*constrProps.toTypedArray()).name("constructorAllProps")
        superUnit(this@constructorAllProps.superUnit().primaryOrFirstConstructor())
    } else Constructor.EMPTY
}

fun <T : CompilationUnitI> T.constructorOwnPropsOnly(): ConstructorI {
    val constrProps = props().filter { !it.meta() }.map { p(it) }
    val primary = this is EnumTypeI
    return if (constrProps.isNotEmpty()) constr {
        parent(this@constructorOwnPropsOnly)
        primary(primary).params(*constrProps.toTypedArray()).name("constructorOwnPropsOnly")
        superUnit(this@constructorOwnPropsOnly.superUnit().primaryOrFirstConstructor())
    } else Constructor.EMPTY
}

fun <T : CompilationUnitI> T.propagateItemToSubtypes(item: CompilationUnitI) {
    superUnitFor().filter { superUnitChild ->
        superUnitChild.items().filterIsInstance<CompilationUnitI>().find {
            (it.name() == item.name() || it.superUnit() == superUnitChild)
        } == null
    }.forEach { superUnitChild ->
        val derivedItem = item.deriveSubType<T> {
            namespace(superUnitChild.namespace())
            G({ type(superUnitChild).name("T") })
        }
        superUnitChild.addItem(derivedItem)
        superUnitChild.propagateItemToSubtypes(derivedItem)
    }
}

fun <T : TypeI> T.GT(vararg types: TypeI): T {
    if (generics().size >= types.size) {
        var i = 0
        val ret = derive<T> {
            val generics = generics()
            for (type in types) {
                if (type is GenericI) {
                    generics[i++] = type
                } else {
                    generics[i++].type(type)
                }
            }
        }
        return ret
    } else {
        throw IllegalArgumentException("To many generic types")
    }
}

fun TypeI.G(type: TypeI): GenericI = G { type(type) }

fun OperationI.ret(type: TypeI): OperationI = ret(Attribute({ type(type) }))
fun LogicUnitI.p(name: String, type: TypeI = n.String, body: AttributeI.() -> Unit = {}): LogicUnitI = params(
        Attribute({
            type(type).name(name)
            body()
        }))

fun ItemI.deriveNamespace(name: String) = (namespace().endsWith(name) || "shared".equals(name, true)).
        ifElse(namespace(), { "${namespace()}.$name" })

fun StructureUnitI.deriveArtifact(name: String) = (artifact().endsWith(name)).
        ifElse(artifact(), { "${artifact()}-$name" })

fun <T : StructureUnitI> T.extendModel(): T {
    val ret = initObjectTrees()
    return ret
}

fun <T : StructureUnitI> T.initObjectTrees(): T {
    n.initObjectTree()
    l.initObjectTree()
    return initObjectTree()
}

fun <T : StructureUnitI> T.initObjectTree(): T {
    (this as MultiHolder<ItemI>).initObjectTree {
        if (this is StructureUnitI) {
            val parent = findParent(StructureUnitI::class.java) ?: parent()
            if (parent.namespace().isBlank() || this !is StructureUnitI) parent.namespace()
            else parent.deriveNamespace(name().toLowerCase())
        } else {
            parent().namespace()
        }
    }
    initBlackNames()
    initFullNameArtifacts()
    return this
}

fun <T : StructureUnitI> T.initFullNameArtifacts() {
    if (fullName().isBlank()) {
        fullName(name())
    }

    val name = name().toLowerCase()
    val parent = findParent(StructureUnitI::class.java)

    if (artifact().isBlank()) {
        if (parent == null || parent == Item.EMPTY) {
            artifact(name)
        } else if (parent is StructureUnitI) {
            artifact(parent.deriveArtifact(name))
        }
    }

    items().forEach {
        if (it is StructureUnitI) {
            it.initFullNameArtifacts()
        } else if (it is MultiHolderI<*> && it.supportsItemType(StructureUnitI::class.java)) {
            it.items().filterIsInstance(StructureUnitI::class.java).forEach { it.initFullNameArtifacts() }
        }
    }
}
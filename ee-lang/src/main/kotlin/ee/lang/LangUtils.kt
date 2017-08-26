package ee.lang

import ee.common.ext.ifElse
import ee.common.ext.setAndTrue

open class LangDerivedKindNames {
    val API = "Api"
    val IMPL = "Impl"
    val MANUAL = "MANUAL"
}

object LangDerivedKind : LangDerivedKindNames()

fun ItemI.parentNameAndName(): String = storage.getOrPut(this, "parentNameAndName", {
    val parent = findParent(DataType::class.java)
    if (parent != null) {
        val regexp = "(\\B[A-Z][a-z]*)".toRegex()
        if (regexp.containsMatchIn(name())) name().replaceFirst(regexp, "$1${parent.name().capitalize()}") else
            "${parent.name()}${name().capitalize()}"
    } else {
        name()
    }
})

fun ItemI.nameAndParentName(): String = storage.getOrPut(this, "nameAndParentName", {
    val parent = findParent(DataType::class.java)
    if (parent != null) {
        val regexp = "(\\B[A-Z][a-z]*)".toRegex()
        if (regexp.containsMatchIn(name())) name().replaceFirst(regexp, "${parent.name().capitalize()}\$1") else
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


fun ListMultiHolderI<AttributeI>.nonDefaultAndWithoutValueAndNonDerived(): List<AttributeI> =
        storage.getOrPut(this, "nonDefaultAndWithoutValueAndNonDerived", {
            filter { !it.default() && it.derivedAsType().isEmpty() }
        })

fun ListMultiHolderI<AttributeI>.defaultOrWithValueAndNonDerived(): List<AttributeI> =
        storage.getOrPut(this, "defaultOrWithValueAndNonDerived", {
            filter { (it.default() || it.anonymous()) && it.derivedAsType().isEmpty() }
        })

fun TypeI.primaryConstructor(): ConstructorI = storage.getOrPut(this, "primaryConstructor", {
    constructors().filter { it.primary() }.firstOrNull() ?: Constructor.EMPTY
})

fun TypeI.primaryOrFirstConstructor(): ConstructorI = storage.getOrPut(this, "primaryOrFirstConstructor", {
    constructors().filter { it.primary() }.firstOrNull() ?: constructors().firstOrNull() ?: Constructor.EMPTY
})

fun TypeI.otherConstructors(): List<ConstructorI> = storage.getOrPut(this, "otherConstructors", {
    constructors().filterNot { it.primary() }
})

fun ConstructorI.props(): List<AttributeI> = storage.getOrPut(this, "props", {
    params().filter { it.derivedFrom().isNotEMPTY() }
})

fun ConstructorI.paramsForType(): List<AttributeI> = storage.getOrPut(this, "paramsForType", {
    val type = findParentMust(TypeI::class.java)
    params().filter { param -> type.props().find { it.name() == param.name() && it.type() == param.type() } != null }
})

fun TypeI.propsExceptPrimaryConstructor(): List<AttributeI> = storage.getOrPut(this,
        "propsExceptPrimaryConstructor", {
    if (primaryConstructor().isNotEMPTY()) props().filter { prop ->
        primaryConstructor().props().find { it.name() == prop.name() } == null
    } else props()
})

fun TypeI.propsSuperUnit(): List<AttributeI> = storage.getOrPut(this, "propsSuperUnit", {
    propsAll().filter { !it.inherited() }
})

fun TypeI.propsAll(): List<AttributeI> = storage.getOrPut(this, "propsAll", {
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

*/

fun ret(type: TypeI = n.String, body: AttributeI.() -> Unit = {}): AttributeI = p {
    type(type).name("ret")
    body()
}

fun lambda(vararg params: AttributeI, adapt: OperationI.() -> Unit = {}): LambdaI = Lambda({
    operation(Operation({
        params(*params)
        adapt()
    }))
})

interface TypedAttributeI<T : TypeI> : AttributeI {
    fun sub(subType: T.() -> AttributeI): AttributeI {
        initIfNotInitialized()
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

fun CompilationUnitI.propS(adapt: AttributeI.() -> Unit = {}): AttributeI = prop(Attribute({
    type(n.String)
    adapt()
}))

fun CompilationUnitI.propB(adapt: AttributeI.() -> Unit = {}): AttributeI = prop(Attribute({
    type(n.Boolean)
    adapt()
}))

fun CompilationUnitI.propI(adapt: AttributeI.() -> Unit = {}): AttributeI = prop(Attribute({
    type(n.Int)
    adapt()
}))


fun CompilationUnitI.propL(adapt: AttributeI.() -> Unit = {}): AttributeI = prop(Attribute({
    type(n.Long)
    adapt()
}))

fun CompilationUnitI.propF(adapt: AttributeI.() -> Unit = {}): AttributeI = prop(Attribute({
    type(n.Float)
    adapt()
}))


fun CompilationUnitI.propDT(adapt: AttributeI.() -> Unit = {}): AttributeI = prop(Attribute({
    type(n.Date)
    adapt()
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
    accessible().setAndTrue() && mutable().setAndTrue()
})

fun <T : CompositeI> T.defineConstructorAllPropsForNonConstructors() {
    findDownByType(CompilationUnitI::class.java, stopSteppingDownIfFound = false).filter { it.constructors().isEmpty() }
            .extend { constructorAllProps() }
}

fun <T : CompositeI> T.defineConstructorOwnPropsOnlyForNonConstructors() {
    findDownByType(CompilationUnitI::class.java, stopSteppingDownIfFound = false).filter { it.constructors().isEmpty() }
            .extend { constructorOwnPropsOnly() }
}

fun <T : CompositeI> T.defineConstructorEmpty() {
    findDownByType(CompilationUnitI::class.java, stopSteppingDownIfFound = false).filter { it.constructors().isEmpty() }
            .extend { constructorEmpty() }
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

fun <T : TypeI> T.constructorAllProps(adapt: ConstructorI.() -> Unit = {}): ConstructorI {
    val constrProps = propsAll().filter { !it.meta() }
    val primary = this is EnumTypeI
    return if (constrProps.isNotEmpty()) {
        storage.reset(this)
        val parent = this
        constr {
            parent(parent)
            primary(primary).params(*constrProps.toTypedArray())
            namespace(parent.namespace())
            superUnit(parent.superUnit().primaryOrFirstConstructor())
            adapt()
        }
    } else Constructor.EMPTY
}

fun <T : TypeI> T.constructorOwnPropsOnly(adapt: ConstructorI.() -> Unit = {}): ConstructorI {
    val constrProps = props().filter { !it.meta() }
    val primary = this is EnumTypeI
    return if (constrProps.isNotEmpty()) {
        storage.reset(this)
        val parent = this
        constr {
            parent(parent)
            primary(primary).params(*constrProps.toTypedArray())
            namespace(this@constructorOwnPropsOnly.namespace())
            superUnit(parent.superUnit().primaryOrFirstConstructor())
            adapt()
        }
    } else Constructor.EMPTY
}

fun <T : TypeI> T.constructorEmpty(adapt: ConstructorI.() -> Unit = {}): ConstructorI {
    val constrProps = props().filter { it.anonymous() }.map { p(it).default(true).anonymous(it.anonymous()) }
    return if (this !is EnumTypeI) {
        storage.reset(this)
        val parent = this
        constr {
            parent(parent)
            primary(true).params(*constrProps.toTypedArray())
            namespace(parent.namespace())
            superUnit(parent.superUnit().primaryOrFirstConstructor())
            adapt()
        }
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
fun LogicUnitI.p(name: String, type: TypeI = n.String, adapt: AttributeI.() -> Unit = {}): LogicUnitI = params(
        Attribute({
            type(type).name(name)
            adapt()
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

fun <T : MacroCompositeI> T.hasMacros() = macrosBefore().isNotEmpty() ||
        macrosBody().isNotEmpty() || macrosAfter().isNotEmpty()
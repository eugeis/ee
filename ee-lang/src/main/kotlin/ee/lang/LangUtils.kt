package ee.lang

import ee.common.ext.ifElse
import ee.common.ext.setAndTrue

open class LangDerivedKindNames {
    val API = "Api"
    val IMPL = "Impl"
    val MANUAL = "MANUAL"
}

object LangDerivedKind : LangDerivedKindNames()

fun ItemI<*>.parentNameAndName(): String = storage.getOrPut(this, "parentNameAndName", {
    val parent = findParent(DataTypeI::class.java)
    if (parent != null) {
        val regexp = "(\\B[A-Z][a-z]*)".toRegex()
        if (regexp.containsMatchIn(name())) name().replaceFirst(regexp,
            "$1${parent.name().capitalize()}") else "${parent.name()}${name().capitalize()}"
    } else {
        name()
    }
})

fun ItemI<*>.nameAndParentName(): String = storage.getOrPut(this, "nameAndParentName", {
    val parent = findParent(DataTypeI::class.java)
    if (parent != null) {
        val regexp = "(\\B[A-Z][a-z]*)".toRegex()
        if (regexp.containsMatchIn(name())) name().replaceFirst(regexp,
            "${parent.name().capitalize()}\$1") else "${name()}${parent.name().capitalize()}"
    } else {
        name()
    }
})

fun <T : LogicUnitI<*>> T.findGeneric(name: String): GenericI<*>? = findParent(TypeI::class.java)?.findGeneric(name)

fun <T : LogicUnitI<*>> T.paramsWithOut(superUnit: LogicUnitI<*>) =
    params().filter { param -> superUnit.params().firstOrNull { it.name() == param.name() } == null }

fun <T : TypeI<*>> T.findGeneric(name: String): GenericI<*>? =
    generics().find { it.name() == name } ?: findParent(LogicUnitI::class.java)?.findGeneric(name)


fun ListMultiHolderI<AttributeI<*>, *>.nonDefaultAndWithoutValueAndNonDerived(): List<AttributeI<*>> =
    storage.getOrPut(this, "nonDefaultAndWithoutValueAndNonDerived", {
        filter { (!(it.default() || (it.anonymous() && it.type().props().isEmpty()))) && it.derivedAsType().isEmpty() }
    })

fun ListMultiHolderI<AttributeI<*>, *>.defaultOrWithValueAndNonDerived(): List<AttributeI<*>> =
    storage.getOrPut(this, "defaultOrWithValueAndNonDerived", {
        filter { (it.default() || it.anonymous()) && it.derivedAsType().isEmpty() }
    })

fun TypeI<*>.primaryConstructor(): ConstructorI<*> = storage.getOrPut(this, "primaryConstructor", {
    constructors().filter { it.primary() }.firstOrNull() ?: Constructor.EMPTY
})

fun TypeI<*>.primaryOrFirstConstructor(): ConstructorI<*> = storage.getOrPut(this, "primaryOrFirstConstructor", {
    constructors().filter { it.primary() }.firstOrNull() ?: constructors().firstOrNull() ?: Constructor.EMPTY
})

fun TypeI<*>.otherConstructors(): List<ConstructorI<*>> = storage.getOrPut(this, "otherConstructors", {
    constructors().filterNot { it.primary() }
})

fun ConstructorI<*>.props(): List<AttributeI<*>> = storage.getOrPut(this, "props", {
    params().filter { it.derivedFrom().isNotEMPTY() }
})

fun ConstructorI<*>.paramsForType(): List<AttributeI<*>> = storage.getOrPut(this, "paramsForType", {
    val type = findParentMust(TypeI::class.java)
    params().filter { param -> type.props().find { it.name() == param.name() && it.type() == param.type() } != null }
})

fun TypeI<*>.propsExceptPrimaryConstructor(): List<AttributeI<*>> =
    storage.getOrPut(this, "propsExceptPrimaryConstructor", {
        if (primaryConstructor().isNotEMPTY()) props().filter { prop ->
            primaryConstructor().props().find { it.name() == prop.name() } == null
        } else props()
    })

fun TypeI<*>.propsSuperUnit(): List<AttributeI<*>> = storage.getOrPut(this, "propsSuperUnit", {
    propsAll().filter { !it.inherited() }
})

fun TypeI<*>.propsAll(): List<AttributeI<*>> = storage.getOrPut(this, "propsAll", {
    if (superUnit().isNotEMPTY()) {
        val ret = mutableListOf<AttributeI<*>>()
        val myType = this
        superUnit().propsAll().mapTo(ret, {
            it.derive {
                inherited(true)

                //use my generic specialization, if available
                if (type() is GenericI<*>) {
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

fun TypeI<*>.propsAllWithoutMetaAndAnonymousWithoutProps(): List<AttributeI<*>> =
    storage.getOrPut(this, "propsAllWithoutMetaAndAnonymousWithoutProps", {
        propsAll().filter { !it.meta() && !(it.anonymous() && !props().isEmpty()) }
    })

fun TypeI<*>.propsWithoutMetaAndAnonymousWithoutProps(): List<AttributeI<*>> =
    storage.getOrPut(this, "propsWithoutMetaAndAnonymousWithoutProps", {
        props().filter { !it.meta() && !(it.anonymous() && !props().isEmpty()) }
    })

fun TypeI<*>.propsAllNoMeta(): List<AttributeI<*>> = storage.getOrPut(this, "propsAllNoMeta", {
    propsAll().filter { !it.meta() }
})

fun TypeI<*>.propsNoMeta(): List<AttributeI<*>> = storage.getOrPut(this, "propsNoMeta", {
    props().filter { !it.meta() }
})

fun TypeI<*>.propsNoMetaNoKey(): List<AttributeI<*>> = storage.getOrPut(this, "propsNoMetaNoKey", {
    props().filter { !it.meta() && !it.key() }
})

fun TypeI<*>.propsNoMetaNoValue(): List<AttributeI<*>> = storage.getOrPut(this, "propsNoMetaNoValue", {
    props().filter { !it.meta() && it.value() == null }
})

fun TypeI<*>.operationsWithoutDataType(): List<OperationI<*>> = storage.getOrPut(this, "operationsWithoutDataType", {
    operations().filter { it !is DataTypeOperationI }
})

//props().filter { it.anonymous() }.map { p(it).default(true).anonymous(it.anonymous()) }

//helper design functions
/*
fun lambda(init: LambdaI<*>.() -> Unit): LambdaI<*> = Lambda(init)

fun p(init: AttributeI<*>.() -> Unit = {}): AttributeI<*> = duration(init)

fun p(name: String, type: TypeI<*> = n.String, body: AttributeI<*>.() -> Unit = {}): AttributeI<*> = duration({
    type(type).name(name)
    body()
})

*/

fun ret(type: TypeI<*> = n.String, body: AttributeI<*>.() -> Unit = {}): AttributeI<*> = p {
    type(type).name("ret")
    body()
}

fun lambda(vararg params: AttributeI<*>, adapt: OperationI<*>.() -> Unit = {}): LambdaI<*> = Lambda({
    operation(Operation({
        params(*params)
        adapt()
    }))
})

interface TypedAttributeI<T : TypeI<*>, B : TypedAttributeI<T, B>> : AttributeI<B> {
    override fun type(): T
    fun sub(subType: T.() -> AttributeI<*>): AttributeI<*> {
        initIfNotInitialized()
        //TODO create new structure with parent and sub type
        return (type() as T).subType()
    }

    fun typeT(value: T): B
}

open class TypedAttribute<T : TypeI<*>> : AttributeB<TypedAttribute<T>>, TypedAttributeI<T, TypedAttribute<T>> {
    constructor(value: TypedAttribute<*>.() -> Unit = {}) : super(value)

    override fun type(): T {
        return super.type() as T
    }

    override fun typeT(value: T): TypedAttribute<T> = apply { type(value) }
}

fun p(init: AttributeI<*>.() -> Unit = {}): AttributeI<*> = Attribute(init)

fun p(name: String, type: TypeI<*> = n.String, body: AttributeI<*>.() -> Unit = {}): AttributeI<*> = Attribute({
    type(type).name(name)
    body()
})

fun p(name: AttributeI<*>, init: AttributeI<*>.() -> Unit = {}): AttributeI<*> = name.derive(init)

fun TypeI<*>.propE(adapt: AttributeI<*>.() -> Unit = {}): AttributeI<*> = prop(Attribute({
    type(l.EnumType)
    adapt()
}))

fun TypeI<*>.propS(adapt: AttributeI<*>.() -> Unit = {}): AttributeI<*> = prop(Attribute({
    type(n.String)
    adapt()
}))

fun TypeI<*>.propListT(type: TypeI<*>, adapt: AttributeI<*>.() -> Unit = {}): AttributeI<*> = prop(Attribute({
    type(n.List.GT(type))
    adapt()
}))

fun TypeI<*>.propB(adapt: AttributeI<*>.() -> Unit = {}): AttributeI<*> = prop(Attribute({
    type(n.Boolean)
    adapt()
}))

fun TypeI<*>.propI(adapt: AttributeI<*>.() -> Unit = {}): AttributeI<*> = prop(Attribute({
    type(n.Int)
    adapt()
}))


fun TypeI<*>.propL(adapt: AttributeI<*>.() -> Unit = {}): AttributeI<*> = prop(Attribute({
    type(n.Long)
    adapt()
}))

fun TypeI<*>.propF(adapt: AttributeI<*>.() -> Unit = {}): AttributeI<*> = prop(Attribute({
    type(n.Float)
    adapt()
}))


fun TypeI<*>.propDT(adapt: AttributeI<*>.() -> Unit = {}): AttributeI<*> = prop(Attribute({
    type(n.Date)
    adapt()
}))

fun <T : TypeI<*>> TypeI<*>.prop(type: T): TypedAttributeI<T, *> {
    val ret = TypedAttribute<T>({ type(type) })
    props(ret)
    return ret
}

/*
fun <T : TypeI<*>> LogicUnitI<*>.param(init: TypedAttributeI<T>.() -> Unit = {}): TypedAttributeI<T> {
    val ret = TypedAttribute<T>(init)
    params(ret)
    return ret
}

fun <T : TypeI<*>> LogicUnitI<*>.paramT(type: T): TypedAttributeI<T> {
    val ret = TypedAttribute<T>({ type(type) })
    params(ret)
    return ret
}
*/

fun AttributeI<*>.accessibleAndMutable(): Boolean = storage.getOrPut(this, "accessibleAndMutable", {
    accessible().setAndTrue() && mutable().setAndTrue()
})

fun AttributeI<*>.nameDecapitalize(): String = storage.getOrPut(this, "nameDecapitalize", {
    name().decapitalize()
})

fun <T : CompositeI<*>> T.defineConstructorAllPropsForNonConstructors() {
    findDownByType(TypeI::class.java, stopSteppingDownIfFound = false).filter { it.constructors().isEmpty() }
        .extend { constructorAllProps() }
}

fun <T : CompositeI<*>> T.defineConstructorOwnPropsOnlyForNonConstructors() {
    findDownByType(TypeI::class.java, stopSteppingDownIfFound = false).filter { it.constructors().isEmpty() }
        .extend { constructorOwnPropsOnly() }
}

fun <T : CompositeI<*>> T.defineConstructorEmpty(filter: TypeI<*>.() -> Boolean = { constructors().isEmpty() }) {
    findDownByType(TypeI::class.java, stopSteppingDownIfFound = false).filter { it.filter() }
        .extend { constructorEmpty() }
}

fun <T : CompositeI<*>> T.defineSuperUnitsAsAnonymousProps() {
    findDownByType(CompilationUnitI::class.java, stopSteppingDownIfFound = false).filter { it.superUnit().isNotEMPTY() }
        .extend {
            val item = this
            prop({ type(item.superUnit()).anonymous(true).name(item.superUnit().name()) })
        }
}

fun <T : CompositeI<*>> T.declareAsBaseWithNonImplementedOperation() {
    findDownByType(CompilationUnitI::class.java).filter { it.operations().isNotEMPTY() && !it.base() }
        .forEach { it.base(true) }
}

fun <T : CompositeI<*>> T.prepareAttributesOfEnums() {
    findDownByType(EnumTypeI::class.java).forEach {
        it.props().forEach { it.replaceable(false).initByDefaultTypeValue(false) }
    }
}

fun TypeI<*>.superUnit(): TypeI<*> = superUnits().firstOrNull() ?: Type.EMPTY
fun <B : TypeI<B>> B.superUnit(value: TypeI<*>): B = superUnits(value)

fun <T : TypeI<*>> T.constructorAllProps(adapt: ConstructorI<*>.() -> Unit = {}): ConstructorI<*> {
    var ret = primaryOrFirstConstructor()
    if (ret.isEMPTY()) {
        val primary = this is EnumTypeI<*>
        storage.reset(this)
        val parent = this
        val superUnitConstructor =
            if (parent.superUnit().isNotEMPTY() && parent.superUnit().primaryOrFirstConstructor().isEMPTY()) {
                parent.superUnit().constructorAllProps(adapt)
            } else {
                parent.superUnit().primaryOrFirstConstructor()
            }

        ret = constr {
            parent(parent)
            primary(primary).params(*propsAllNoMeta().toTypedArray())
            namespace(parent.namespace())
            superUnit(superUnitConstructor)
            adapt()
        }
    }
    return ret
}

fun <T : TypeI<*>> T.constructorOwnPropsOnly(adapt: ConstructorI<*>.() -> Unit = {}): ConstructorI<*> {
    val primary = this is EnumTypeI<*>
    storage.reset(this)
    val parent = this
    return constr {
        parent(parent)
        primary(primary).params(*propsNoMeta().toTypedArray())
        namespace(this@constructorOwnPropsOnly.namespace())
        superUnit(parent.superUnit().primaryOrFirstConstructor())
        adapt()
    }
}

fun <T : TypeI<*>> T.constructorEmpty(adapt: ConstructorI<*>.() -> Unit = {}): ConstructorI<*> {
    val constrProps = props().filter { it.anonymous() }.map { p(it).default(true).anonymous(it.anonymous()) }
    return if (this !is EnumTypeI<*>) {
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

fun <T : TypeI<*>> T.propagateItemToSubtypes(item: TypeI<*>) {
    superUnitFor().filter { superUnitChild ->
        superUnitChild.items().filterIsInstance<TypeI<*>>().find {
            (it.name() == item.name() || it.superUnit() == superUnitChild)
        } == null
    }.forEach { superUnitChild ->
            val derivedItem = item.deriveSubType {
                namespace(superUnitChild.namespace())
                G({ type(superUnitChild).name("T") })
            }
            superUnitChild.addItem(derivedItem)
            superUnitChild.propagateItemToSubtypes(derivedItem)
        }
}

fun <T : TypeI<*>> T.GT(vararg types: TypeI<*>): T {
    val originalGenerics = generics()
    if (originalGenerics.size >= types.size) {
        var i = 0
        val ret = derive {
            val derivedGenerics = generics()
            for (type in types) {
                if (type is GenericI<*>) {
                    derivedGenerics[i++] = type
                } else {
                    derivedGenerics[i++].type(type)
                }
            }
        }
        return ret as T
    } else {
        throw IllegalArgumentException("To many generic types")
    }
}

fun TypeI<*>.G(type: TypeI<*>): GenericI<*> = G { type(type) }
fun TypeI<*>.isNative(): Boolean = parent() == n

fun OperationI<*>.retFirst(): AttributeI<*> = returns().firstOrNull() ?: Attribute.EMPTY
fun OperationI<*>.ret(type: TypeI<*>): OperationI<*> = returns(Attribute { type(type).name("ret") })
fun LogicUnitI<*>.p(name: String, type: TypeI<*> = n.String, adapt: AttributeI<*>.() -> Unit = {}): LogicUnitI<*> =
    params(Attribute({
        type(type).name(name)
        adapt()
    }))

fun ItemI<*>.deriveNamespaceShared(name: String) =
    (namespace().endsWith(name) || "shared".equals(name, true)).ifElse(namespace(), { "${namespace()}.$name" })

fun ItemI<*>.deriveNamespace(name: String) =
    (namespace().endsWith(name)).ifElse(namespace(), { "${namespace()}.$name" })

fun StructureUnitI<*>.deriveArtifact(name: String) =
    (artifact().endsWith(name)).ifElse(artifact(), { "${artifact()}-$name" })

fun <T : StructureUnitI<*>> T.extendModel(): T {
    val ret = initObjectTrees()
    return ret
}

fun <T : StructureUnitI<*>> T.initObjectTrees(): T {
    n.initObjectTree()
    l.initObjectTree()
    return initObjectTree()
}

fun <T : StructureUnitI<*>> T.initObjectTree(): T {
    (this as MultiHolderI<ItemI<*>, *>).initObjectTree {
        if (this is StructureUnitI) {
            val parent = (findParent(StructureUnitI::class.java) ?: parent()) as ItemI
            if (parent.namespace().isBlank()) parent.namespace()
            else parent.deriveNamespace(name().toLowerCase())
        } else {
            parent().namespace()
        }
    }
    initBlackNames()
    initFullNameArtifacts()
    return this
}

fun <T : StructureUnitI<*>> T.initFullNameArtifacts() {
    if (fullName().isBlank()) {
        fullName(name())
    }

    val name = name().toLowerCase()
    val parent = findParent(StructureUnitI::class.java)

    if (artifact().isBlank()) {
        if (parent == null || parent.isEMPTY()) {
            artifact(name)
        } else {
            artifact(parent.deriveArtifact(name))
        }
    }

    items().forEach {
        if (it is StructureUnitI<*>) {
            it.initFullNameArtifacts()
        } else if (it is MultiHolderI<*, *> && it.supportsItemType(StructureUnitI::class.java)) {
            it.items().filterIsInstance(StructureUnitI::class.java).forEach { it.initFullNameArtifacts() }
        }
    }
}

fun <T : MacroCompositeI<*>> T.hasMacros() =
    macrosBefore().isNotEmpty() || macrosBody().isNotEmpty() || macrosAfter().isNotEmpty()

fun TypeI<*>.findProp(propToSearch: AttributeI<*>): AttributeI<*> = props().find {
    it == propToSearch || it.derivedFrom() == propToSearch || it.name() == propToSearch.name()
} ?: Attribute.EMPTY

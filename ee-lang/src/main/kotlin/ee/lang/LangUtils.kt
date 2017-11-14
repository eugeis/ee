package ee.lang

import ee.common.ext.ifElse
import ee.common.ext.setAndTrue

open class LangDerivedKindNames {
    val API = "Api"
    val IMPL = "Impl"
    val MANUAL = "MANUAL"
}

object LangDerivedKind : LangDerivedKindNames()

fun ItemIB<*>.parentNameAndName(): String = storage.getOrPut(this, "parentNameAndName", {
    val parent = findParent(DataType::class.java)
    if (parent != null) {
        val regexp = "(\\B[A-Z][a-z]*)".toRegex()
        if (regexp.containsMatchIn(name())) name().replaceFirst(regexp, "$1${parent.name().capitalize()}") else
            "${parent.name()}${name().capitalize()}"
    } else {
        name()
    }
})

fun ItemIB<*>.nameAndParentName(): String = storage.getOrPut(this, "nameAndParentName", {
    val parent = findParent(DataType::class.java)
    if (parent != null) {
        val regexp = "(\\B[A-Z][a-z]*)".toRegex()
        if (regexp.containsMatchIn(name())) name().replaceFirst(regexp, "${parent.name().capitalize()}\$1") else
            "${name()}${parent.name().capitalize()}"
    } else {
        name()
    }
})

fun <T : LogicUnitIB<*>> T.findGeneric(name: String): GenericIB<*>? = findParent(TypeIB::class.java)?.findGeneric(name)

fun <T : LogicUnitIB<*>> T.paramsWithOut(superUnit: LogicUnitIB<*>)
        = params().filter { param -> superUnit.params().firstOrNull { it.name() == param.name() } == null }

fun <T : TypeIB<*>> T.findGeneric(name: String): GenericIB<*>? =
        generics().find { it.name() == name } ?: findParent(LogicUnitIB::class.java)?.findGeneric(name)


fun ListMultiHolderIB<AttributeIB<*>, *>.nonDefaultAndWithoutValueAndNonDerived(): List<AttributeIB<*>> =
        storage.getOrPut(this, "nonDefaultAndWithoutValueAndNonDerived", {
            filter { (!(it.default() || (it.anonymous() && it.type().props().isEmpty()))) && it.derivedAsType().isEmpty() }
        })

fun ListMultiHolderIB<AttributeIB<*>, *>.defaultOrWithValueAndNonDerived(): List<AttributeIB<*>> =
        storage.getOrPut(this, "defaultOrWithValueAndNonDerived", {
            filter { (it.default() || it.anonymous()) && it.derivedAsType().isEmpty() }
        })

fun TypeIB<*>.primaryConstructor(): ConstructorIB<*> = storage.getOrPut(this, "primaryConstructor", {
    constructors().filter { it.primary() }.firstOrNull() ?: Constructor.EMPTY
})

fun TypeIB<*>.primaryOrFirstConstructor(): ConstructorIB<*> = storage.getOrPut(this, "primaryOrFirstConstructor", {
    constructors().filter { it.primary() }.firstOrNull() ?: constructors().firstOrNull() ?: Constructor.EMPTY
})

fun TypeIB<*>.otherConstructors(): List<ConstructorIB<*>> = storage.getOrPut(this, "otherConstructors", {
    constructors().filterNot { it.primary() }
})

fun ConstructorIB<*>.props(): List<AttributeIB<*>> = storage.getOrPut(this, "props", {
    params().filter { it.derivedFrom().isNotEMPTY() }
})

fun ConstructorIB<*>.paramsForType(): List<AttributeIB<*>> = storage.getOrPut(this, "paramsForType", {
    val type = findParentMust(TypeIB::class.java)
    params().filter { param -> type.props().find { it.name() == param.name() && it.type() == param.type() } != null }
})

fun TypeIB<*>.propsExceptPrimaryConstructor(): List<AttributeIB<*>> = storage.getOrPut(this,
        "propsExceptPrimaryConstructor", {
    if (primaryConstructor().isNotEMPTY()) props().filter { prop ->
        primaryConstructor().props().find { it.name() == prop.name() } == null
    } else props()
})

fun TypeIB<*>.propsSuperUnit(): List<AttributeIB<*>> = storage.getOrPut(this, "propsSuperUnit", {
    propsAll().filter { !it.inherited() }
})

fun TypeIB<*>.propsAll(): List<AttributeIB<*>> = storage.getOrPut(this, "propsAll", {
    if (superUnit().isNotEMPTY()) {
        val ret = mutableListOf<AttributeIB<*>>()
        val myType = this
        superUnit().propsAll().mapTo(ret, {
            it.derive {
                inherited(true)

                //use my generic specialization, if available
                if (type() is GenericIB<*>) {
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

fun TypeIB<*>.propsAllWithoutMetaAndAnonymousWithoutProps(): List<AttributeIB<*>> =
        storage.getOrPut(this, "propsAllWithoutMetaAndAnonymousWithoutProps", {
            propsAll().filter { !it.meta() && !(it.anonymous() && !props().isEmpty()) }
        })

fun TypeIB<*>.propsWithoutMetaAndAnonymousWithoutProps(): List<AttributeIB<*>> =
        storage.getOrPut(this, "propsWithoutMetaAndAnonymousWithoutProps", {
            props().filter { !it.meta() && !(it.anonymous() && !props().isEmpty()) }
        })

fun TypeIB<*>.propsAllNoMeta(): List<AttributeIB<*>> =
        storage.getOrPut(this, "propsAllNoMeta", {
            propsAll().filter { !it.meta() }
        })

fun TypeIB<*>.propsNoMeta(): List<AttributeIB<*>> =
        storage.getOrPut(this, "propsNoMeta", {
            props().filter { !it.meta() }
        })

fun TypeIB<*>.propsNoMetaNoKey(): List<AttributeIB<*>> =
        storage.getOrPut(this, "propsNoMetaNoKey", {
            props().filter { !it.meta() && !it.key() }
        })

fun TypeIB<*>.propsNoMetaNoValue(): List<AttributeIB<*>> =
        storage.getOrPut(this, "propsNoMetaNoValue", {
            props().filter { it.value() == null && !it.meta() }
        })

//props().filter { it.anonymous() }.map { p(it).default(true).anonymous(it.anonymous()) }

//helper design functions
/*
fun lambda(init: LambdaIB<*>.() -> Unit): LambdaIB<*> = Lambda(init)

fun p(init: AttributeIB<*>.() -> Unit = {}): AttributeIB<*> = duration(init)

fun p(name: String, type: TypeIB<*> = n.String, body: AttributeIB<*>.() -> Unit = {}): AttributeIB<*> = duration({
    type(type).name(name)
    body()
})

*/

fun ret(type: TypeIB<*> = n.String, body: AttributeIB<*>.() -> Unit = {}): AttributeIB<*> = p {
    type(type).name("ret")
    body()
}

fun lambda(vararg params: AttributeIB<*>, adapt: OperationIB<*>.() -> Unit = {}): LambdaIB<*> = Lambda({
    operation(Operation({
        params(*params)
        adapt()
    }))
})

interface TypedAttributeI<T : TypeIB<*>> : AttributeIB<TypedAttributeI<T>> {
    override fun type(): T
}

interface TypedAttributeIB<T : TypeIB<*>, B : TypedAttributeIB<T, B>> : AttributeIB<B> {
    fun sub(subType: T.() -> AttributeIB<*>): AttributeIB<*> {
        initIfNotInitialized()
        //TODO create new structure with parent and sub type
        return (type() as T).subType()
    }

    fun typeT(value: T): B
}

open class TypedAttribute<T : TypeIB<*>> : AttributeB<TypedAttribute<T>>, TypedAttributeIB<T, TypedAttribute<T>> {
    constructor(value: TypedAttribute<*>.() -> Unit = {}) : super(value)

    override fun type(): T {
        return super.type() as T
    }

    override fun typeT(value: T): TypedAttribute<T> = apply { type(value) }
}

fun p(init: AttributeIB<*>.() -> Unit = {}): AttributeIB<*> = Attribute(init)

fun p(name: String, type: TypeIB<*> = n.String, body: AttributeIB<*>.() -> Unit = {}): AttributeIB<*> = Attribute({
    type(type).name(name)
    body()
})

fun p(name: AttributeIB<*>, init: AttributeIB<*>.() -> Unit = {}): AttributeIB<*> = name.derive(init)

fun CompilationUnitIB<*>.propE(adapt: AttributeIB<*>.() -> Unit = {}): AttributeIB<*> = prop(Attribute({
    type(l.EnumType)
    adapt()
}))

fun CompilationUnitIB<*>.propS(adapt: AttributeIB<*>.() -> Unit = {}): AttributeIB<*> = prop(Attribute({
    type(n.String)
    adapt()
}))

fun CompilationUnitIB<*>.propListT(type: TypeIB<*>, adapt: AttributeIB<*>.() -> Unit = {}): AttributeIB<*> = prop(Attribute({
    type(n.List.GT(type))
    adapt()
}))

fun CompilationUnitIB<*>.propB(adapt: AttributeIB<*>.() -> Unit = {}): AttributeIB<*> = prop(Attribute({
    type(n.Boolean)
    adapt()
}))

fun CompilationUnitIB<*>.propI(adapt: AttributeIB<*>.() -> Unit = {}): AttributeIB<*> = prop(Attribute({
    type(n.Int)
    adapt()
}))


fun CompilationUnitIB<*>.propL(adapt: AttributeIB<*>.() -> Unit = {}): AttributeIB<*> = prop(Attribute({
    type(n.Long)
    adapt()
}))

fun CompilationUnitIB<*>.propF(adapt: AttributeIB<*>.() -> Unit = {}): AttributeIB<*> = prop(Attribute({
    type(n.Float)
    adapt()
}))


fun CompilationUnitIB<*>.propDT(adapt: AttributeIB<*>.() -> Unit = {}): AttributeIB<*> = prop(Attribute({
    type(n.Date)
    adapt()
}))

fun <T : TypeIB<*>> CompilationUnitIB<*>.prop(type: T): TypedAttributeIB<T, *> {
    val ret = TypedAttribute<T>({ type(type) })
    props(ret)
    return ret
}

/*
fun <T : TypeIB<*>> LogicUnitIB<*>.param(init: TypedAttributeI<T>.() -> Unit = {}): TypedAttributeI<T> {
    val ret = TypedAttribute<T>(init)
    params(ret)
    return ret
}

fun <T : TypeIB<*>> LogicUnitIB<*>.paramT(type: T): TypedAttributeI<T> {
    val ret = TypedAttribute<T>({ type(type) })
    params(ret)
    return ret
}
*/

fun AttributeIB<*>.accessibleAndMutable(): Boolean = storage.getOrPut(this, "accessibleAndMutable", {
    accessible().setAndTrue() && mutable().setAndTrue()
})

fun AttributeIB<*>.nameDecapitalize(): String = storage.getOrPut(this, "nameDecapitalize", {
    name().decapitalize()
})

fun <T : CompositeIB<*>> T.defineConstructorAllPropsForNonConstructors() {
    findDownByType(CompilationUnitIB::class.java, stopSteppingDownIfFound = false).filter { it.constructors().isEmpty() }
            .extend { constructorAllProps() }
}

fun <T : CompositeIB<*>> T.defineConstructorOwnPropsOnlyForNonConstructors() {
    findDownByType(CompilationUnitIB::class.java, stopSteppingDownIfFound = false).filter { it.constructors().isEmpty() }
            .extend { constructorOwnPropsOnly() }
}

fun <T : CompositeIB<*>> T.defineConstructorEmpty(filter: CompilationUnitIB<*>.() -> Boolean = { constructors().isEmpty() }) {
    findDownByType(CompilationUnitIB::class.java, stopSteppingDownIfFound = false).filter { it.filter() }
            .extend { constructorEmpty() }
}

fun <T : CompositeIB<*>> T.defineSuperUnitsAsAnonymousProps() {
    findDownByType(CompilationUnitIB::class.java, stopSteppingDownIfFound = false).filter { it.superUnit().isNotEMPTY() }
            .extend {
                val item = this
                prop({ type(item.superUnit()).anonymous(true).name(item.superUnit().name()) })
            }
}

fun <T : CompositeIB<*>> T.declareAsBaseWithNonImplementedOperation() {
    findDownByType(CompilationUnitIB::class.java).filter { it.operations().isNotEMPTY() && !it.base() }.forEach { it.base(true) }
}

fun <T : CompositeIB<*>> T.prepareAttributesOfEnums() {
    findDownByType(EnumTypeIB::class.java).forEach { it.props().forEach { it.replaceable(false).initByDefaultTypeValue(false) } }
}

fun <T : TypeIB<*>> T.constructorAllProps(adapt: ConstructorIB<*>.() -> Unit = {}): ConstructorIB<*> {
    val primary = this is EnumTypeIB<*>
    storage.reset(this)
    val parent = this
    return constr {
        parent(parent)
        primary(primary).params(*propsAllNoMeta().toTypedArray())
        namespace(parent.namespace())
        superUnit(parent.superUnit().primaryOrFirstConstructor())
        adapt()
    }
}

fun <T : TypeIB<*>> T.constructorOwnPropsOnly(adapt: ConstructorIB<*>.() -> Unit = {}): ConstructorIB<*> {
    val primary = this is EnumTypeIB<*>
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

fun <T : TypeIB<*>> T.constructorEmpty(adapt: ConstructorIB<*>.() -> Unit = {}): ConstructorIB<*> {
    val constrProps = props().filter { it.anonymous() }.map { p(it).default(true).anonymous(it.anonymous()) }
    return if (this !is EnumTypeIB<*>) {
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

fun <T : CompilationUnitIB<*>> T.propagateItemToSubtypes(item: CompilationUnitIB<*>) {
    superUnitFor().filter { superUnitChild ->
        superUnitChild.items().filterIsInstance<CompilationUnitIB<*>>().find {
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

fun <T : TypeIB<*>> T.GT(vararg types: TypeIB<*>): T {
    if (generics().size >= types.size) {
        var i = 0
        val ret = derive {
            val generics = generics()
            for (type in types) {
                if (type is GenericIB<*>) {
                    generics[i++] = type
                } else {
                    generics[i++].type(type)
                }
            }
        }
        return ret as T
    } else {
        throw IllegalArgumentException("To many generic types")
    }
}

fun TypeIB<*>.G(type: TypeIB<*>): GenericIB<*> = G { type(type) }
fun TypeIB<*>.isNative(): Boolean = parent() == n

fun OperationIB<*>.retFirst(): AttributeIB<*> = returns().firstOrNull() ?: Attribute.EMPTY
fun OperationIB<*>.ret(type: TypeIB<*>): OperationIB<*> = returns(Attribute { type(type).name("ret") })
fun LogicUnitIB<*>.p(name: String, type: TypeIB<*> = n.String, adapt: AttributeIB<*>.() -> Unit = {}): LogicUnitIB<*> = params(
        Attribute({
            type(type).name(name)
            adapt()
        }))

fun ItemIB<*>.deriveNamespaceShared(name: String) = (namespace().endsWith(name) || "shared".equals(name, true)).
        ifElse(namespace(), { "${namespace()}.$name" })

fun ItemIB<*>.deriveNamespace(name: String) = (namespace().endsWith(name)).
        ifElse(namespace(), { "${namespace()}.$name" })

fun StructureUnitIB<*>.deriveArtifact(name: String) = (artifact().endsWith(name)).
        ifElse(artifact(), { "${artifact()}-$name" })

fun <T : StructureUnitIB<*>> T.extendModel(): T {
    val ret = initObjectTrees()
    return ret
}

fun <T : StructureUnitIB<*>> T.initObjectTrees(): T {
    n.initObjectTree()
    l.initObjectTree()
    return initObjectTree()
}

fun <T : StructureUnitIB<*>> T.initObjectTree(): T {
    (this as MultiHolderIB<ItemIB<*>, *>).initObjectTree {
        if (this is StructureUnitIB) {
            val parent = (findParent(StructureUnitIB::class.java) ?: parent()) as ItemIB
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

fun <T : StructureUnitIB<*>> T.initFullNameArtifacts() {
    if (fullName().isBlank()) {
        fullName(name())
    }

    val name = name().toLowerCase()
    val parent = findParent(StructureUnitIB::class.java)

    if (artifact().isBlank()) {
        if (parent == null || parent.isEMPTY()) {
            artifact(name)
        } else {
            artifact(parent.deriveArtifact(name))
        }
    }

    items().forEach {
        if (it is StructureUnitIB<*>) {
            it.initFullNameArtifacts()
        } else if (it is MultiHolderIB<*, *> && it.supportsItemType(StructureUnitIB::class.java)) {
            it.items().filterIsInstance(StructureUnitIB::class.java).forEach { it.initFullNameArtifacts() }
        }
    }
}

fun <T : MacroCompositeIB<*>> T.hasMacros() = macrosBefore().isNotEmpty() ||
        macrosBody().isNotEmpty() || macrosAfter().isNotEmpty()
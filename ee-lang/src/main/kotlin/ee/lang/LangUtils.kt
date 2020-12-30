package ee.lang

import ee.common.ext.ifElse
import ee.common.ext.setAndTrue
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("LangUtils")

open class LangDerivedKindNames {
    val API = "Api"
    val IMPL = "Impl"
    val MANUAL = "MANUAL"
}

object LangDerivedKind : LangDerivedKindNames()

fun ItemI<*>.parentNameAndName(): String = storage.getOrPut(this, "parentNameAndName") {
    val parent = findParent(DataTypeI::class.java)
    if (parent != null) {
        /*
         val regexp = "(\\B[A-Z][a-z]*)".toRegex()
         if (regexp.containsMatchIn(name())) name().replaceFirst(regexp,
                 "$1${parent.name().capitalize()}") else "${parent.name()}${name().capitalize()}"
         */
        "${parent.name()}${name().capitalize()}"
    } else {
        name()
    }
}

fun ItemI<*>.nameAndParentName(): String = storage.getOrPut(this, "nameAndParentName") {
    val parent = findParent(DataTypeI::class.java)
    if (parent != null) {
        /*
        val regexp = "(\\B[A-Z][a-z]*)".toRegex()
        if (regexp.containsMatchIn(name())) name().replaceFirst(regexp,
                "${parent.name().capitalize()}\$1") else "${name()}${parent.name().capitalize()}"
         */
        "${name()}${parent.name().capitalize()}"
    } else {
        name()
    }
}

fun <T : LogicUnitI<*>> T.findGeneric(name: String): GenericI<*>? = findParent(TypeI::class.java)?.findGeneric(name)

fun <T : LogicUnitI<*>> T.paramsWithOut(
    superUnit: LogicUnitI<*>
) =
    params().filter { param -> superUnit.params().firstOrNull { it.name() == param.name() } == null }

fun <T : TypeI<*>> T.findGeneric(name: String): GenericI<*>? = generics().find { it.name() == name } ?: findParent(
    LogicUnitI::class.java
)?.findGeneric(name)


fun ListMultiHolderI<AttributeI<*>, *>.nonDefaultAndNonDerived(): List<AttributeI<*>> = storage.getOrPut(
    this, "nonDefaultAndNonDerived"
) {
    filter { (!(it.isDefault())) && it.derivedAsType().isEmpty() }
}

fun ListMultiHolderI<AttributeI<*>, *>.defaultAndValueOrInit(): List<AttributeI<*>> = storage.getOrPut(
    this,
    "defaultAndValue"
) {
    filter { (it.isDefault() && (it.value() != null || it.isInitByDefaultTypeValue())) }
}

fun TypeI<*>.primaryConstructor(): ConstructorI<*> = storage.getOrPut(this, "primaryConstructor") {
    constructors().find { it.isPrimary() } ?: Constructor.EMPTY
}

fun TypeI<*>.findByNameOrPrimaryOrFirstConstructorFull(constrName: String = ""): ConstructorI<*> {
    return constructors().find {
        it.name() == constrName
    } ?: primaryOrFirstConstructorOrFull()
}

fun TypeI<*>.primaryOrFirstConstructorOrFull(): ConstructorI<*> = storage.getOrPut(
    this,
    "primaryOrFirstConstructorOrFull"
) {
    constructors().find { it.isPrimary() } ?: constructors().firstOrNull() ?: constructorFull()
}

fun TypeI<*>.otherConstructors(): List<ConstructorI<*>> = storage.getOrPut(this, "otherConstructors") {
    constructors().filterNot { it.isPrimary() }
}

fun ConstructorI<*>.paramsNotDerived(): List<AttributeI<*>> = storage.getOrPut(this, "paramsNotDerived") {
    params().filter { it.derivedFrom().isNotEMPTY() }
}

fun ConstructorI<*>.paramsWithOutValue(): List<AttributeI<*>> = storage.getOrPut(
    this,
    "paramsWithOutValue"
) {
    params().filter { param -> param.value() == null }
}

fun ConstructorI<*>.paramsWithOutFixValue(): List<AttributeI<*>> = storage.getOrPut(
    this,
    "paramsWithOutFixValue"
) {
    params().filter { param -> !param.isFixValue() }
}

fun ConstructorI<*>.paramsWithFixValue(): Map<String, AttributeI<*>> = storage.getOrPut(
    this,
    "paramsWithFixValue"
) {
    params().filter { param -> param.isFixValue() }.associateBy { it.name() }
}

fun ConstructorI<*>.paramsForType(): List<AttributeI<*>> = storage.getOrPut(this, "paramsForType") {
    val type = findParentMust(TypeI::class.java)
    params().filter { param ->
        type.props().find {
            it.name() == param.name()
                    && it.type() == param.type()
        } != null
    }
}

fun TypeI<*>.propsExceptPrimaryConstructor(): List<AttributeI<*>> = storage.getOrPut(
    this,
    "propsExceptPrimaryConstructor"
) {
    if (primaryConstructor().isNotEMPTY()) props().filter { prop ->
        primaryConstructor().params().find { it.name() == prop.name() } == null
    } else props()
}

fun TypeI<*>.propsSuperUnit(): List<AttributeI<*>> = storage.getOrPut(this, "propsSuperUnit") {
    propsAll().filter { !it.isInherited() }
}

fun TypeI<*>.propsAll(): List<AttributeI<*>> = storage.getOrPut(this, "propsAll") {
    if (superUnit().isNotEMPTY()) {
        val ret = mutableListOf<AttributeI<*>>()
        val myType = this
        superUnit().propsAll().mapTo(ret) {
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
        }
        ret.addAll(props())
        ret
    } else {
        props()
    }
}

fun TypeI<*>.propsAllWithoutMetaAndAnonymousWithoutProps(): List<AttributeI<*>> = storage.getOrPut(
    this,
    "propsAllWithoutMetaAndAnonymousWithoutProps"
) {
    propsAll().filter { !it.isMeta() && !(it.isAnonymous() && !props().isEmpty()) }
}

fun TypeI<*>.propsWithoutMetaAndAnonymousWithoutProps(): List<AttributeI<*>> = storage.getOrPut(
    this,
    "propsWithoutMetaAndAnonymousWithoutProps"
) {
    props().filter { !it.isMeta() && !(it.isAnonymous() && !props().isEmpty()) }
}

fun TypeI<*>.propsWithoutParamsOfPrimaryConstructor(): List<AttributeI<*>> = storage.getOrPut(
    this,
    "propsWithoutPrimaryConstructor"
) {
    val constrParams = primaryConstructor().params()
    if (constrParams.isEmpty()) {
        props()
    } else {
        props().filter { param -> constrParams.find { param.name() == it.name() } == null }
    }
}

fun TypeI<*>.propsAllNotNullableGeneric(): List<AttributeI<*>> =
    storage.getOrPut(this, "propsAllNotNullableGeneric") {
        propsAll().filter {
            it.isNotNullableNoNativeGeneric()
        }
    }

private fun AttributeI<*>.isNotNullableNoNativeGeneric() = !isNullable() && type() is GenericI<*>

fun TypeI<*>.propsWithoutNotNullableGeneric(): List<AttributeI<*>> =
    storage.getOrPut(this, "propsWithoutNotNullableGeneric") {
        props().filter {
            !it.isNotNullableNoNativeGeneric()
        }
    }

fun TypeI<*>.propsAllNoMeta(): List<AttributeI<*>> = storage.getOrPut(this, "propsAllNoMeta") {
    propsAll().filter { !it.isMeta() }
}

fun TypeI<*>.propsToString(): List<AttributeI<*>> = storage.getOrPut(this, "propsToString") {
    if (isToStrAll()) propsAllNoMeta() else propsAllNoMeta().filter { it.isToStr().notNullValueOrFalse() }
}

fun TypeI<*>.propsEquals(): List<AttributeI<*>> = storage.getOrPut(this, "propsEquals") {
    if (isToEqualsAll()) propsAllNoMeta() else propsAllNoMeta().filter { it.isToEquals().notNullValueOrFalse() }
}

fun TypeI<*>.propsNoNative(): List<AttributeI<*>> = storage.getOrPut(this, "propsNoNative") {
    propsAll().filter { !it.type().isNative() }
}

fun TypeI<*>.propsCollectionValueTypes(): List<ValuesI<*>> = storage.getOrPut(this, "propsCollectionValueTypes") {
    val ret = mutableListOf<ValuesI<*>>()
    props().forEach {
        if (it.type().derivedFrom() == n.List || it.type().derivedFrom() == n.Collection) {
            val generic = it.type().generics().first()
            val genericType = generic.type()
            if (genericType is ValuesI<*>) {
                ret.add(genericType)
            }
        }
    }
    ret
}

fun TypeI<*>.propsCollectionValues(): List<AttributeI<*>> = storage.getOrPut(this, "propsCollectionValues") {
    props().filter {
        if (it.type().derivedFrom() == n.List || it.type().derivedFrom() == n.Collection) {
            val generic = it.type().generics().first()
            generic.type() is ValuesI<*>
        } else {
            false
        }
    }
}

fun TypeI<*>.propsMapValues(): List<AttributeI<*>> = storage.getOrPut(this, "propsMapValues") {
    props().filter {
        if (it.type().derivedFrom() == n.Map) {
            val generic = it.type().generics()[1]
            generic.type() is ValuesI<*>
        } else {
            false
        }
    }
}

fun TypeI<*>.propsAnonymous(): List<AttributeI<*>> = storage.getOrPut(this, "propsAnonymous") {
    props().filter { it.isAnonymous() }
}

fun TypeI<*>.propsNoMeta(): List<AttributeI<*>> = storage.getOrPut(this, "propsNoMeta") {
    props().filter { !it.isMeta() }
}

fun TypeI<*>.propsNoMetaNoKey(): List<AttributeI<*>> = storage.getOrPut(this, "propsNoMetaNoKey") {
    props().filter { !it.isMeta() && !it.isKey() }
}

fun TypeI<*>.propsNoMetaNoValue(): List<AttributeI<*>> = storage.getOrPut(this, "propsNoMetaNoValue") {
    props().filter { !it.isMeta() && it.value() == null }
}

fun TypeI<*>.propsNoMetaNoValueNoId(): List<AttributeI<*>> = storage.getOrPut(this, "propsNoMetaNoValueNoId") {
    props().filter { !it.isMeta() && it.value() == null && !it.isKey() }
}


fun TypeI<*>.genericsAll(): List<GenericI<*>> = storage.getOrPut(this, "genericsAll") {
    if (superUnit().isNotEMPTY()) {
        val ret = mutableListOf<GenericI<*>>()
        ret.addAll(generics())
        superUnit().genericsAll().forEach { suGeneric ->
            if (ret.find { it.name() != suGeneric.name() } == null) {
                ret.add(suGeneric)
            }
        }
        ret
    } else {
        generics()
    }
}

fun TypeI<*>.operationsWithoutDataTypeOperations(): List<OperationI<*>> =
    storage.getOrPut(this, "operationsWithoutDataTypeOperations") {
        operations().filter { it !is DataTypeOperationI }
    }

fun TypeI<*>.operationsWithInherited(): List<OperationI<*>> = storage.getOrPut(
    this,
    "operationsWithInherited"
) {
    operations().toMutableSet().apply {
        superUnits().forEach { addAll(it.operationsWithInherited()) }
    }.toList().sortedBy { it.name() }
}


fun TypeI<*>.addPropId(): AttributeI<*> {
    return prop {
        key(true).type(n.UUID).name("id")
    }
}

fun TypeI<*>.propId(): AttributeI<*>? = props().find { it.isKey() }

fun TypeI<*>.propIdOrAdd(): AttributeI<*> = storage.getOrPut(this, "propId") {
    initIfNotInitialized()
    var ret = propId()
    if (ret == null && superUnit().isNotEMPTY()) {
        ret = superUnit().propId()
    }

    if (ret == null) {
        log.debug("prop 'id' can't be found for '$this', build default one")
        ret = addPropId()
    }
    ret
}

//paramsNotDerived().filterSkipped { it.isAnonymous() }.map { p(it).isDefault(true).isAnonymous(it.isAnonymous()) }

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

fun lambda(vararg params: AttributeI<*>, adapt: OperationI<*>.() -> Unit = {}): LambdaI<*> = Lambda {
    operation(Operation {
        params(*params)
        adapt()
    })
}

interface TypedAttributeI<T : TypeI<*>, B : TypedAttributeI<T, B>> : AttributeI<B> {
    override fun type(): T
    fun sub(subType: T.() -> AttributeI<*>): AttributeI<*> {
        initIfNotInitialized()
        //TODO create new structure with parent and sub type
        return (type() as T).subType()
    }

    fun typeT(value: T): B
}

open class TypedAttribute<T : TypeI<*>>(value: TypedAttribute<*>.() -> Unit = {}) :
    AttributeB<TypedAttribute<T>>(value), TypedAttributeI<T, TypedAttribute<T>> {

    override fun type(): T {
        return super.type() as T
    }

    override fun typeT(value: T): TypedAttribute<T> = apply { type(value) }
}

fun p(init: AttributeI<*>.() -> Unit = {}): AttributeI<*> = Attribute(init)

fun p(name: String, type: TypeI<*> = n.String, body: AttributeI<*>.() -> Unit = {}): AttributeI<*> = Attribute {
    type(type).name(name)
    body()
}

fun p(name: AttributeI<*>, init: AttributeI<*>.() -> Unit = {}): AttributeI<*> = name.derive(init)

fun TypeI<*>.propE(adapt: AttributeI<*>.() -> Unit = {}): AttributeI<*> = prop(Attribute {
    type(l.EnumType)
    adapt()
})

fun TypeI<*>.propS(adapt: AttributeI<*>.() -> Unit = {}): AttributeI<*> = prop(Attribute {
    type(n.String)
    adapt()
})

fun TypeI<*>.propListT(type: TypeI<*>, adapt: AttributeI<*>.() -> Unit = {}): AttributeI<*> = prop(Attribute {
    type(n.List.GT(type))
    adapt()
})

fun TypeI<*>.propB(adapt: AttributeI<*>.() -> Unit = {}): AttributeI<*> = prop(Attribute {
    type(n.Boolean)
    adapt()
})

fun TypeI<*>.propI(adapt: AttributeI<*>.() -> Unit = {}): AttributeI<*> = prop(Attribute {
    type(n.Int)
    adapt()
})


fun TypeI<*>.propL(adapt: AttributeI<*>.() -> Unit = {}): AttributeI<*> = prop(Attribute {
    type(n.Long)
    adapt()
})

fun TypeI<*>.propF(adapt: AttributeI<*>.() -> Unit = {}): AttributeI<*> = prop(Attribute {
    type(n.Float)
    adapt()
})


fun TypeI<*>.propDT(adapt: AttributeI<*>.() -> Unit = {}): AttributeI<*> = prop(Attribute {
    type(n.Date)
    adapt()
})

fun <T : TypeI<*>> TypeI<*>.prop(type: T): TypedAttributeI<T, *> {
    val ret = TypedAttribute<T> {
        type(type)
    }
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

fun AttributeI<*>.accessibleAndMutable(): Boolean = storage.getOrPut(this, "accessibleAndMutable") {
    isAccessible().setAndTrue() && isMutable().setAndTrue()
}

fun AttributeI<*>.nameDecapitalize(): String = storage.getOrPut(this, "nameDecapitalize") {
    name().decapitalize()
}

fun AttributeI<*>.asParam(paramValue: Any): AttributeI<*> = derive { value(paramValue) }
fun EnumTypeI<*>.lit(prop: AttributeI<*>, paramValue: Any): EnumLiteralI<*> = lit {
    params(prop.asParam(paramValue))
}

fun <T : CompositeI<*>> T.defineConstructorAllPropsForNonConstructors() {
    findDownByType(TypeI::class.java, stopSteppingDownIfFound = false).forEach {
        if (it.constructors().isEmpty() && (it !is EnumTypeI || it.props().isNotEmpty())) {
            it.extend { constructorFull() }
        }
    }
}

fun <T : CompositeI<*>> T.defineConstructorOwnPropsOnlyForNonConstructors() {
    findDownByType(
        TypeI::class.java,
        stopSteppingDownIfFound = false
    ).filter { it.constructors().isEmpty() }.extend { constructorOwnPropsOnly() }
}

fun <T : CompositeI<*>> T.defineConstructorNoProps(filter: TypeI<*>.() -> Boolean = { constructors().isEmpty() }) {
    findDownByType(
        TypeI::class.java,
        stopSteppingDownIfFound = false
    ).filter { it.filter() }.extend {
        constructorNoProps()
    }
}

fun <T : CompositeI<*>> T.defineSuperUnitsAsAnonymousProps() {
    findDownByType(CompilationUnitI::class.java, stopSteppingDownIfFound = false).filter {
        !it.superUnit().isIfc() && it.superUnit().isNotEMPTY()
    }.defineSuperUnitsAsAnonymousProps()
}

fun <T : CompilationUnitI<*>> List<T>.defineSuperUnitsAsAnonymousProps() = extend {
    defineSuperUnitsAsAnonymousProps()
}

fun <T : CompilationUnitI<*>> T.defineSuperUnitsAsAnonymousProps() {
    val item = this
    prop {
        type(item.superUnit()).anonymous(true).name(item.superUnit().name())
    }
}

fun <T : CompositeI<*>> T.declareAsBaseWithNonImplementedOperation() {
    findDownByType(CompilationUnitI::class.java).filter { it.operations().isNotEMPTY() && !it.isBase() }.forEach {
        it.base(true)
    }
}

fun <T : CompositeI<*>> T.prepareAttributesOfEnums() {
    findDownByType(EnumTypeI::class.java).forEach {
        it.props().forEach { it.replaceable(false).initByDefaultTypeValue(false) }
    }
}

fun TypeI<*>.superUnit(): TypeI<*> = superUnits().firstOrNull() ?: Type.EMPTY
fun <B : TypeI<B>> B.superUnit(value: TypeI<*>): B = superUnits(value)

fun <T : TypeI<*>> T.constructorOwnPropsOnly(adapt: ConstructorI<*>.() -> Unit = {}): ConstructorI<*> {
    val primary = this is EnumTypeI<*>
    storage.reset(this)
    val parent = this
    return constr {
        parent(parent)
        primary(primary).params(*propsNoMeta().toTypedArray())
        namespace(this@constructorOwnPropsOnly.namespace())
        superUnit(parent.superUnit().primaryOrFirstConstructorOrFull())
        adapt()
    }
}

fun <T : TypeI<*>> T.constructorFull(adapt: ConstructorI<*>.() -> Unit = {}): ConstructorI<*> {
    return if (isNotEMPTY()) {
        val primary = constructors().isEmpty()
        storage.reset(this)
        constr {
            name("Full")
            parent(this@constructorFull)
            primary(primary).params(*propsAllNoMeta().toTypedArray())
            namespace(namespace())
            superUnit(this@constructorFull.superUnit().primaryOrFirstConstructorOrFull())
            adapt()
        }
    } else Constructor.EMPTY
}

fun <T : TypeI<*>> T.constructorNoProps(adapt: ConstructorI<*>.() -> Unit = {}): ConstructorI<*> {
    return if (isNotEMPTY() && this !is EnumTypeI<*>) {
        val constrProps = props().filter { it.isAnonymous() }.map {
            p(it).default(true).anonymous(it.isAnonymous())
        }
        storage.reset(this)
        val parent = this
        constr {
            name("Default")
            parent(parent)
            primary(true).params(*constrProps.toTypedArray())
            namespace(parent.namespace())
            superUnit(parent.superUnit().primaryOrFirstConstructorOrFull())
            adapt()
        }
    } else Constructor.EMPTY
}


fun <T : LogicUnitI<*>> T.deriveReplaceParams(vararg newParams: AttributeI<*>): T {
    return derive {
        params().replaceAll { org ->
            val found = newParams.find { it.name().equals(org.name(), true) }
            found ?: org
        }
        superUnit(this@deriveReplaceParams)
    } as T
}

fun <T : TypeI<*>> T.constrSuper(vararg newParams: AttributeI<*>) {
    constr(superUnit().primaryOrFirstConstructorOrFull().deriveReplaceParams(*newParams))
}


fun <T : TypeI<*>> T.propagateItemToSubtypes(item: TypeI<*>) {
    superUnitFor().filter { superUnitChild ->
        superUnitChild.items().filterIsInstance<TypeI<*>>().find {
            (it.name() == item.name() || it.superUnit() == superUnitChild)
        } == null
    }.forEach { superUnitChild ->
        val derivedItem = item.deriveSubType {
            namespace(superUnitChild.namespace())
            G { type(superUnitChild).name("T") }
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

fun <T : TypeI<*>> T.GTStar(): T {
    val ret = derive {
        val derivedGenerics = generics()
        derivedGenerics.forEach {
            it.name("*")
        }
    }
    return ret as T
}

fun TypeI<*>.G(type: TypeI<*>): GenericI<*> = G { type(type) }
fun TypeI<*>.isNative(): Boolean = parent() == n

fun OperationI<*>.retFirst(): AttributeI<*> = returns().firstOrNull() ?: Attribute.EMPTY
fun OperationI<*>.retA(type: TypeI<*>, name: String = "ret"): AttributeI<*> {
    val ret = Attribute { type(type).name(name) }
    returns(ret)
    return ret
}

fun OperationI<*>.ret(type: TypeI<*>, name: String = "ret"): OperationI<*> =
    returns(Attribute { type(type).name(name) })

fun LogicUnitI<*>.p(
    name: String, type: TypeI<*> = n.String,
    adapt: AttributeI<*>.() -> Unit = {}
): LogicUnitI<*> = params(Attribute {
    type(type).name(name)
    adapt()
})

fun StructureUnitI<*>.deriveArtifact(name: String) = (artifact().endsWith(name)).ifElse(artifact())
{ "${artifact()}-$name" }

fun <T : StructureUnitI<*>> T.extendModel(): T {
    val ret = initObjectTrees()
    return ret
}

fun <T : StructureUnitI<*>> List<T>.initObjectTrees(): List<T> {
    n.initObjectTree()
    l.initObjectTree()
    forEach {
        it.initObjectTree()
    }
    return this
}

fun <T : StructureUnitI<*>> T.initObjectTrees(): T {
    n.initObjectTree()
    l.initObjectTree()
    return initObjectTree()
}

fun <T : StructureUnitI<*>> T.initObjectTree(): T {
    (this as MultiHolderI<ItemI<*>, *>).initObjectTree {
        val ret = if (this is StructureUnitI) {
            val parent = (findParent(StructureUnitI::class.java) ?: parent()) as ItemI<out StructureUnitI<*>>
            if (parent.namespace().isBlank())
                ""
            else
                parent.deriveNamespace(name().toLowerCase())
        } else {
            parent().namespace()
        }
        ret
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

fun EnumType.litFirstParam(firstParam: Any, value: LiteralI<*>.() -> Unit) {
    lit(value)
}

fun TypeI<*>.isNonBlock(op: OperationI<*>) =
    op.isNonBlock().notNullValueElse {
        val parent = op.parent()
        if (parent is TypeI<*>) parent.isNonBlock() else isNonBlock()
    }

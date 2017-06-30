package ee.lang

import ee.common.ext.ifElse

fun <T : LogicUnitI> T.findGeneric(name: String): GenericI? = findParent(TypeI::class.java)?.findGeneric(name)


fun <T : LogicUnitI> T.paramsWithOut(superUnit: LogicUnitI)
        = params().filter { param -> superUnit.params().firstOrNull { it.name() == param.name() } == null }

fun <T : TypeI> T.findGeneric(name: String): GenericI? =
        generics().find { it.name() == name } ?: findParent(LogicUnitI::class.java)?.findGeneric(name)

fun CompilationUnitI.primaryConstructor(): ConstructorI = storage.getOrPut(this, "primaryConstructor", {
    constructors().filter { it.primary() }.firstOrNull() ?: Constructor.EMPTY
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
}

open class TypedAttribute<T : TypeI> : Attribute, TypedAttributeI<T> {
    constructor(value: Attribute.() -> Unit = {}) : super(value as Composite.() -> Unit)
}

fun p(init: AttributeI.() -> Unit = {}): AttributeI = Attribute(init)

fun p(name: String, type: TypeI = n.String, body: AttributeI.() -> Unit = {}): AttributeI = Attribute({
    type(type).name(name)
    body()
})

fun p(name: AttributeI, init: AttributeI.() -> Unit = {}): AttributeI = name.derive(init)

fun AttributeI.accessibleAndMutable(): Boolean = storage.getOrPut(this, "accessibleAndMutable", {
    accessible() && mutable()
})

fun <T : CompositeI> T.defineConstructorAllForNonConstructors() {
    findDownByType(CompilationUnitI::class.java, stopSteppingDownIfFound = false).filter { it.constructors().isEmpty() }
            .extend { constructorAll().init() }
}

fun <T : CompositeI> T.declareAsBaseWithNonImplementedOperation() {
    findDownByType(CompilationUnitI::class.java).filter { it.operations().isNotEMPTY() && !it.base() }.forEach { it.base(true) }
}

fun <T : CompositeI> T.prepareAttributesOfEnums() {
    findDownByType(EnumTypeI::class.java).forEach { it.props().forEach { it.replaceable(false).initByDefaultTypeValue(false) } }
}

fun <T : CompilationUnitI> T.constructorAll(): ConstructorI {
    val constrProps = propsAll().filter { !it.meta() }.map { p(it) }
    val primary = this is EnumTypeI
    return if (constrProps.isNotEmpty()) constr {
        parent(this@constructorAll)
        primary(primary).params(*constrProps.toTypedArray()).name("constructorAll")
        superUnit(this@constructorAll.superUnit().primaryConstructor())
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
fun <T : TypeI> CompilationUnitI.prop(type: T): TypedAttributeI<T> {
    val ret = TypedAttribute<T>({ type(type) })
    props(ret)
    return ret
}

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

fun <T : StructureUnitI> T.initObjectTrees(searchForTargetComposite: Boolean = false): T {
    n.initObjectTree()
    l.initObjectTree()
    return initObjectTree(searchForTargetComposite)
}

fun <T : StructureUnitI> T.initObjectTree(searchForTargetComposite: Boolean = false): T {
    (this as MultiHolder<*>).initObjectTree(searchForTargetComposite, {
        if (this is StructureUnitI) {
            val parent = findParent(StructureUnitI::class.java) ?: parent()
            if (parent.namespace().isBlank() || this !is StructureUnitI) parent.namespace()
            else parent.deriveNamespace(name())
        } else {
            parent().namespace()
        }
    })
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
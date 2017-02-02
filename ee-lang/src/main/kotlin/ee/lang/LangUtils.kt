package ee.lang

fun <T : LogicUnitI> T.findGeneric(name: String): GenericI? = findParent(TypeI::class.java)?.findGeneric(name)

fun <T : TypeI> T.findGeneric(name: String): GenericI? =
        generics().find { it.name() == name } ?: findParent(LogicUnitI::class.java)?.findGeneric(name)

fun CompilationUnitI.primaryConstructor(): ConstructorI = storage.getOrPut(this, "primaryConstructor", {
    constructors().filter { it.primary() }.firstOrNull() ?: Constructor.EMPTY
})

fun CompilationUnitI.otherConstructors(): List<ConstructorI> = storage.getOrPut(this, "otherConstructors", {
    if (constructors().size > 1) constructors().subList(1, constructors().size) else emptyList()
})

fun ConstructorI.props(): List<AttributeI> = storage.getOrPut(this, "props", {
    params().filter { it.derivedFrom().isNotEMPTY() }
})

fun CompilationUnitI.propsExceptPrimaryConstructor(): List<AttributeI> = storage.getOrPut(this,
        "propsExceptPrimaryConstructor", {
    if (primaryConstructor().isNotEmpty()) props().filter { prop ->
        primaryConstructor().props().find { it.name() == prop.name() } == null
    } else props()
})


fun CompilationUnitI.propsAll(): List<AttributeI> = storage.getOrPut(this, "propsAll", {
    if (superUnit().isNotEmpty()) {
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

fun p(init: AttributeI.() -> Unit = {}): AttributeI = Attribute(init)

fun p(name: String, type: TypeI = n.String, body: AttributeI.() -> Unit = {}): AttributeI = Attribute({
    type(type).name(name)
    body()
})

fun p(name: AttributeI, init: AttributeI.() -> Unit = {}): AttributeI = name.derive(init)

fun <T : CompositeI> T.prepareModel(): T {
    n.initObjectTree()
    val ret = initObjectTree()
    ret.sortByName()
    return ret
}

fun <T : CompositeI> T.extendModel(): T {
    n.initObjectTree()
    val ret = initObjectTree()
    ret.sortByName()
    return ret
}

fun <T : CompositeI> T.defineConstructorAllForNonConstructors() {
    findDownByType(CompilationUnit::class.java, stopSteppingDownIfFound = false).filter { it.constructors().isEmpty() }
            .extend { constructorAll() }
}

fun <T : CompositeI> T.declareAsBaseWithNonImplementedOperation() {
    findDownByType(CompilationUnit::class.java).filter { it.operations().isNotEmpty() && !it.base() }.forEach { it.base(true) }
}

fun <T : CompositeI> T.prepareAttributesOfEnums() {
    findDownByType(EnumTypeI::class.java).forEach { it.props().forEach { it.replaceable(false).initByDefaultTypeValue(false) } }
}

fun <T : CompilationUnitI> T.constructorAll(): ConstructorI {
    val constrProps = propsAll().filter { !it.meta() }.map { p(it) }
    val primary = this is EnumTypeI
    return if (constrProps.isNotEmpty()) constr {
        params(*constrProps.toTypedArray())
        primary(primary)
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
        superUnitChild.add(derivedItem)
        superUnitChild.propagateItemToSubtypes(derivedItem)
    }
}

fun <T : TypeI> T.GT(vararg types: TypeI): T {
    if (generics().size >= types.size) {
        var i = 0
        val ret = derive<T> {
            val generics = generics()
            for (type in types) {
                if (type is GenericI && generics is MutableList) {
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
fun CompilationUnitI.prop(type: TypeI): AttributeI = prop { type(type) }
fun OperationI.ret(type: TypeI): OperationI = ret(Attribute({ type(type) }))
fun LogicUnitI.p(name: String, type: TypeI = n.String, body: AttributeI.() -> Unit = {}): LogicUnitI = params(
        Attribute({
            type(type).name(name)
            body()
        }))
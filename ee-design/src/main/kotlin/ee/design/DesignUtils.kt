package ee.design

import ee.lang.*

fun QueryController.findBy(vararg params: AttributeI) = findBy { params(*params) }
fun QueryController.existBy(vararg params: AttributeI) = existBy { params(*params) }

fun CompilationUnitI.op(vararg params: AttributeI, body: OperationI.() -> Unit = {}) = op {
    params(*params)
    body()
}

fun CommandController.command(vararg params: AttributeI) = command { params(*params) }
fun CommandController.createBy(vararg params: AttributeI) = createBy { params(*params) }
fun CommandController.updateBy(vararg params: AttributeI) = updateBy { params(*params) }
fun CommandController.deleteBy(vararg params: AttributeI) = deleteBy { params(*params) }
fun CommandController.composite(vararg commands: CommandI) = composite { operations(*commands) }


fun StructureUnitI.defineNamesForDataTypeControllers() {
}

fun StructureUnitI.declareAsBaseWithNonImplementedOperation() {
    findDownByType(CompilationUnitI::class.java).filter { it.operations().isNotEmpty() && !it.base() }.forEach { it.base(true) }

    //derive controllers from super units
    findDownByType(ControllerI::class.java).filter { it.parent() is CompilationUnitI }.forEach {
        val dataItem = it.parent() as CompilationUnitI
        dataItem.propagateItemToSubtypes(it)

        val T = it.G { type(dataItem).name("T") }
        if (it !is QueryControllerI) {
            it.prop { type(T).name("addItem") }
        }
    }
}

fun <T : CompilationUnitI> T.propagateItemToSubtypes(item: CompilationUnitI) {
    superUnitFor().filter { superUnitChild ->
        superUnitChild.items().filterIsInstance<CompilationUnitI>().find {
            (it.name() == item.name() || it.superUnit() == superUnitChild)
        } == null
    }.forEach { superUnitChild ->
        val derivedItem = item.deriveSubType<ControllerI> {
            namespace(superUnitChild.namespace())
            G { type(superUnitChild).name("T") }
        }
        superUnitChild.addItem(derivedItem)
        superUnitChild.propagateItemToSubtypes(derivedItem)
    }
}
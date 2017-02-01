package ee.design

import ee.design.AttributeI
import ee.design.CompilationUnitI
import ee.design.OperationI

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
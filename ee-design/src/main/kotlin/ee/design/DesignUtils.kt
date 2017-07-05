package ee.design

import ee.lang.*
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("DesignUtils")

fun DataTypeOperationI.nameExternal(): String = storage.getOrPut(this, "nameExternal", {
    val parent = findParent(EntityI::class.java)
    if (parent != null) {
        val regexp = "(\\B[A-Z])".toRegex()
        if (regexp.containsMatchIn(name())) name().replaceFirst(regexp, "${parent.name().capitalize()}$1") else
            "${name()}${parent.name().capitalize()}"
    } else {
        name()
    }
})

fun QueryController.findBy(vararg params: AttributeI) = findBy { params(*params) }
fun QueryController.existBy(vararg params: AttributeI) = existBy { params(*params) }

fun CompilationUnitI.op(vararg params: AttributeI, body: OperationI.() -> Unit = {}) = op {
    params(*params)
    body()
}

fun CommandControllerI.command(vararg params: AttributeI) = command { params(*params) }
fun CommandControllerI.createBy(vararg params: AttributeI) = createBy { params(*params) }
fun CommandControllerI.updateBy(vararg params: AttributeI) = updateBy { params(*params) }
fun CommandControllerI.deleteBy(vararg params: AttributeI) = deleteBy { params(*params) }
fun CommandControllerI.composite(vararg commands: CommandI) = composite { operations(*commands) }


fun StructureUnitI.defineNamesForControllers() {
    findDownByType(ControllerI::class.java).forEach {
        val parent = it.findParentMust(CompilationUnitI::class.java)
        it.name("${parent.name().capitalize()}${it.name().capitalize()}")
    }
}

fun <T : CommandControllerI> T.createByCommand(): CommandI {
    val parent = findParentMust(CompilationUnitI::class.java)
    val commandProps = parent.propsAll().filter { !it.meta() }
    return createBy {
        name("register")
        params(*commandProps.toTypedArray())
        parent(parent)
    }
}

fun <T : CommandControllerI> T.updateByCommand(): CommandI {
    val parent = findParentMust(CompilationUnitI::class.java)
    val commandProps = parent.propsAll().filter { !it.meta() }
    return updateBy {
        name("change")
        params(*commandProps.toTypedArray())
        parent(parent)
    }
}

fun <T : CommandControllerI> T.deleteByCommand(): CommandI {
    val parent = findParentMust(EntityI::class.java)
    return deleteBy {
        name("delete")
        params(parent.id())
        parent(parent)
    }
}

fun StructureUnitI.addDefaultCommandsForEntities() {
    findDownByType(EntityI::class.java).filter { it.commands().isEmpty() }.extend {
        log.debug("Add default commands to ${name()}")
        commands(CommandController {
            name("commands")
            createByCommand()
            updateByCommand()
            deleteByCommand()
        })
    }
}

fun StructureUnitI.declareAsBaseWithNonImplementedOperation() {
    findDownByType(CompilationUnitI::class.java).filter { it.operations().isNotEMPTY() && !it.base() }.forEach { it.base(true) }

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
package ee.design.fx.view

import ee.design.ItemI
import ee.design.TypedCompositeI
import javafx.scene.control.SelectionMode
import javafx.scene.control.TreeItem
import tornadofx.*

class CompositeView() : View("Explorer") {
    val model: ExplorerModel by di()
    val tasksController: TasksController by inject()

    init {
        title = model.name
    }

    override val root = accordion(
            *model.elementGroups.map { group ->
                titledpane(group.first,
                        treeview<ItemNode> {
                            selectionModel.selectionMode = SelectionMode.MULTIPLE
                            root = TreeItem(group.second.toNode(model.nodeFilter))
                            root.isExpanded = true
                            showRootProperty().set(false)
                            cellFormat { text = it.el.name() }
                            onUserSelect {
                                tasksController.onStructureUnitsSelected(
                                        selectionModel.selectedItems.map { it.value.el })
                            }
                            populate {
                                it.value.factory()
                            }
                        }
                )
            }.toTypedArray()
    )
}

open class ExplorerModel(val name: String, val elementGroups: List<Pair<String, TypedCompositeI<*>>>, val nodeFilter: (ItemI) -> Boolean)

open class ItemNode(val el: ItemI, val factory: () -> List<ItemNode> = { emptyList<ItemNode>() })

fun ItemI.toNode(filter: (ItemI) -> Boolean): ItemNode =
        if (this is TypedCompositeI<*>) toNode(filter) else ItemNode(this)

fun TypedCompositeI<*>.toNode(filter: (ItemI) -> Boolean): ItemNode {
    return ItemNode(this) {
        val ret = arrayListOf<ItemNode>()
        items().filter(filter).forEach {
            if (it is TypedCompositeI<*>) {
                ret.add(it.toNode(filter))
            } else {
                ret.add(it.toNode(filter))
            }
        }
        ret
    }
}
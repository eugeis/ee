package ee.lang.fx.view

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

open class ExplorerModel(val name: String, val elementGroups: List<Pair<String, MultiHolderI<*>>>, val nodeFilter: (ItemI) -> Boolean)

open class ItemNode(val el: ItemI, val factory: () -> List<ItemNode> = { emptyList<ItemNode>() })

fun ItemI.toNode(filter: (ItemI) -> Boolean): ItemNode =
        if (this is MultiHolderI<*>) toNode(filter) else ItemNode(this)

fun MultiHolderI<Any>.toNode(filter: (Any) -> Boolean): ItemNode {
    return ItemNode(this) {
        val ret = arrayListOf<ItemNode>()
        items().filterIsInstance(ItemI::class.java).filter(filter).forEach {
            if (it is MultiHolderI<*>) {
                ret.add(it.toNode(filter))
            } else if (it is ItemI) {
                ret.add(it.toNode(filter))
            }
        }
        ret
    }
}
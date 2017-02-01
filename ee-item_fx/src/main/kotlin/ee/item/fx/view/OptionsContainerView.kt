package ee.design.fx.view

import ee.task.Task
import javafx.application.Platform
import tornadofx.View
import tornadofx.tab
import tornadofx.tabpane

class OptionsContainerView : View("OptionsContainerView") {
    val tasksController: TasksController by inject()

    override val root = tabpane {
    }

    fun consoleTab(task: Task): ConsoleView {
        val ret = ConsoleView(task.name)
        Platform.runLater {
            with(root) {
                tab(task.name, ret.root)
            }
        }
        return ret
    }
}

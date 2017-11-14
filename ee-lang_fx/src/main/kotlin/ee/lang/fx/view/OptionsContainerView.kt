package ee.lang.fx.view

import ee.task.Task
import javafx.application.Platform
import tornadofx.*

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

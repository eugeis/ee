package ee.lang.fx.view

import ee.task.TaskFactory
import tornadofx.*

class TasksView : View("TasksView") {
    val tasksController: TasksController by inject()

    override val root = vbox {
        buildTasks(emptyList())
    }

    private fun buildTasks(taskFactories: List<TaskFactory<*>>) {
        taskFactories.groupBy(TaskFactory<*>::group).forEach { group ->
            fieldset(group.key) {
                tilepane {
                    prefColumns = 10
                    for (taskFactory in group.value) {
                        button("${taskFactory.name}") {
                            setOnAction {
                                tasksController.execute(taskFactory)
                            }
                        }
                    }
                }
            }
        }
    }

    fun refresh(taskFactories: List<TaskFactory<*>>) {
        root.children.clear()
        buildTasks(taskFactories)
    }
}

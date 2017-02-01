package ee.design.fx.view

import ee.design.ItemI
import ee.task.Result
import ee.task.TaskFactory
import ee.task.TaskRepository
import ee.task.print
import tornadofx.Controller

class TasksController : Controller() {
    val tasksView: TasksView by inject()
    val outputContainerView: OutputContainerView by inject()

    val repo: TaskRepository by di()
    var selectedElements: List<ItemI> = emptyList()

    fun onStructureUnitsSelected(elements: List<ItemI>) {
        selectedElements = elements
        runAsync {
            repo.find(elements)
        } ui {
            tasksView.refresh(it)
        }
    }

    fun execute(taskFactory: TaskFactory<*>) {
        runAsync {
            val console = outputContainerView.takeConsole(this, taskFactory.name)
            val results = arrayListOf<Result>()
            val ret = Result(action = taskFactory.name, results = results)
            taskFactory.create(selectedElements).forEach { task ->
                console.title = task.name
                val result = task.execute({ console.println(it) })
                console.title = result.toString()
                results.add(result)
            }
            ret.print({ console.println(it) })
            outputContainerView.release(console)
        }
    }
}

package ee.design.fx

import ee.design.fx.view.ModelMainView
import ee.design.fx.view.ExplorerModel
import ee.task.TaskRepository
import tornadofx.App
import tornadofx.DIContainer
import tornadofx.FX
import kotlin.reflect.KClass

open class ModelApp(val model: ExplorerModel, val repo: TaskRepository) : App(ModelMainView::class, Styles::class) {
    init {
        FX.dicontainer = object : DIContainer {
            override fun <T : Any> getInstance(type: KClass<T>): T {
                if (ExplorerModel::class == type) {
                    return model as T
                } else if (TaskRepository::class == type) {
                    return repo as T
                } else {
                    throw IllegalArgumentException("Don't have di instance for $type")
                }
            }
        }
    }
}
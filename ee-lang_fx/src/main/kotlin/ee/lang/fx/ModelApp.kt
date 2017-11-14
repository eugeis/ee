package ee.lang.fx

import ee.lang.fx.view.ExplorerModel
import ee.lang.fx.view.ModelMainView
import ee.task.TaskRepository
import tornadofx.*
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
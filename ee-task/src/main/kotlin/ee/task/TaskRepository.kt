package ee.task

import ee.lang.ItemI

open class TaskRepository : TaskRepositoryBase {
    companion object {
        val EMPTY = TaskRepositoryBase.EMPTY
    }

    constructor(typeFactories: MutableList<TaskFactory<*>> = arrayListOf()) : super(typeFactories) {

    }

    override fun <V : TaskFactory<*>> register(factory: V) {
        typeFactories.add(factory)
    }

    override fun <T : ItemI> find(items: List<T>): List<TaskFactory<*>> {
        return typeFactories.filter { it.supports(items) }
    }
}


package ee.task

import ee.lang.ItemI

open class TaskFactory<T : Task> : TaskFactoryBase<Task> {
    companion object {
        val EMPTY = TaskFactoryBase.EMPTY
    }
    constructor(name: String = "", group: String = ""): super(name, group) {

    }

    override fun supports(items: List<ItemI<*>>): Boolean {
        throw IllegalAccessException("Not implemented yet.")
    }

    override fun create(items: List<ItemI<*>>): List<T> {
        throw IllegalAccessException("Not implemented yet.")
    }

}


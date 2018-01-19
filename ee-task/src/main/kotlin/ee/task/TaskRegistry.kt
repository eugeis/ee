package ee.task

open class TaskRegistry : TaskRegistryBase {
    companion object {
        val EMPTY = TaskRegistryBase.EMPTY
    }

    constructor() : super() {

    }

    override fun register(repo: TaskRepository) {
        throw IllegalAccessException("Not implemented yet.")
    }

}


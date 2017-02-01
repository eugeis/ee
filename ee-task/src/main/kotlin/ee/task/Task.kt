package ee.task

open class Task : TaskBase {
    companion object {
        val EMPTY = TaskBase.EMPTY
    }

    constructor(name: String = "", group: String = "") : super(name, group) {

    }

    override fun execute(output: (String) -> Unit): Result {
        throw IllegalAccessException("Not implemented yet.")
    }

}


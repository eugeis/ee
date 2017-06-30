package ee.task

import ee.lang.Item




open class ExecConfig {
    val home: 
    val cmd: List<String>
    val env: Map<String, String>
    val filterPattern: String
    val failOnError: Boolean
    val filter: Boolean
    val noConsole: Boolean
    val wait: Boolean
    val timeout: Long
    val timeoutUnit: 


    constructor(home:  = .get(""), cmd: List<String> = arrayListOf(), env: Map<String, String> = emptyMap(), filterPattern: String = "", 
                failOnError: Boolean = false, filter: Boolean = false, noConsole: Boolean = false, wait: Boolean = true, 
                timeout: Long = 30, timeoutUnit:  = .SECONDS) {
        this.home = home
        this.cmd = cmd
        this.env = env
        this.filterPattern = filterPattern
        this.failOnError = failOnError
        this.filter = filter
        this.noConsole = noConsole
        this.wait = wait
        this.timeout = timeout
        this.timeoutUnit = timeoutUnit
    }

    companion object {
        val EMPTY = ExecConfig()
    }
}


open class PathResolver {
    val home: 
    val itemToHome: MutableMap<String, String>


    constructor(home:  = .get(""), itemToHome: MutableMap<String, String> = hashMapOf()) {
        this.home = home
        this.itemToHome = itemToHome
    }


    open fun <T : Item> resolve() {
        throw IllegalAccessException("Not implemented yet.")
    }

    companion object {
        val EMPTY = PathResolver()
    }
}


open class Result {
    val action: String
    val ok: Boolean
    val failure: String
    val info: String
    val error: Throwable?
    val results: List<Result>


    constructor(action: String = "", ok: Boolean = true, failure: String = "", info: String = "", error: Throwable? = null, 
                results: List<Result> = arrayListOf()) {
        this.action = action
        this.ok = ok
        this.failure = failure
        this.info = info
        this.error = error
        this.results = results
    }

    companion object {
        val EMPTY = Result()
    }
}


open class Task {
    val name: String
    val group: String


    constructor(name: String = "", group: String = "") {
        this.name = name
        this.group = group
    }


    open fun execute()Result {
        throw IllegalAccessException("Not implemented yet.")
    }

    companion object {
        val EMPTY = Task()
    }
}


open class TaskFactory {
    val name: String
    val group: String


    constructor(name: String = "", group: String = "") {
        this.name = name
        this.group = group
    }


    open fun supports()Boolean {
        throw IllegalAccessException("Not implemented yet.")
    }

    open fun create()MutableList<Task> {
        throw IllegalAccessException("Not implemented yet.")
    }

    companion object {
        val EMPTY = TaskFactory()
    }
}


open class TaskGroup {
    val taskFactories: MutableList<TaskFactory<Task>>
    val tasks: MutableList<Task>


    constructor(taskFactories: MutableList<TaskFactory<Task>> = arrayListOf(), tasks: MutableList<Task> = arrayListOf()) {
        this.taskFactories = taskFactories
        this.tasks = tasks
    }

    companion object {
        val EMPTY = TaskGroup()
    }
}


open class TaskRegistry {
    val pathResolver: PathResolver


    constructor(pathResolver: PathResolver = PathResolver.EMPTY) {
        this.pathResolver = pathResolver
    }


    open fun register()String {
        throw IllegalAccessException("Not implemented yet.")
    }

    companion object {
        val EMPTY = TaskRegistry()
    }
}


open class TaskRepository {
    val typeFactories: MutableList<TaskFactory<Task>>


    constructor(typeFactories: MutableList<TaskFactory<Task>> = arrayListOf()) {
        this.typeFactories = typeFactories
    }


    open fun <V : TaskFactory<Task>> register()String {
        throw IllegalAccessException("Not implemented yet.")
    }

    open fun <T : Item> find()MutableList<TaskFactory<Task>> {
        throw IllegalAccessException("Not implemented yet.")
    }

    companion object {
        val EMPTY = TaskRepository()
    }
}


open class TaskResult {
    val task: Task
    val result: Result


    constructor(task: Task = Task.EMPTY, result: Result = Result.EMPTY) {
        this.task = task
        this.result = result
    }

    companion object {
        val EMPTY = TaskResult()
    }
}




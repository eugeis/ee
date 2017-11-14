package ee.task.shared

import ee.lang.CompilationUnit
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit




open class ExecConfig {
    val home: Path
    val cmd: List<String>
    val env: Map<String, String>
    val filterPattern: Unit
    val failOnError: Boolean
    val filter: Boolean
    val noConsole: Boolean
    val wait: Boolean
    val timeout: Long
    val timeoutUnit: TimeUnit


    constructor(home: Path = Paths.get(""), cmd: List<String> = arrayListOf(), env: Map<String, String> = emptyMap(), 
                filterPattern: Unit = , failOnError: Boolean = false, filter: Boolean = false, 
                noConsole: Boolean = false, wait: Boolean = true, timeout: Long = ee.lang.Attribute@6ebc05a6, 
                timeoutUnit: TimeUnit = TimeUnit.SECONDS) {
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
    val home: Path
    val itemToHome: Map<String, String>


    constructor(home: Path = Paths.get(""), itemToHome: Map<String, String> = emptyMap()) {
        this.home = home
        this.itemToHome = itemToHome
    }


    open fun <T : CompilationUnit> resolve()Path {
        throw IllegalAccessException("Not implemented yet.")
    }

    companion object {
        val EMPTY = PathResolver()
    }
}


open class Result {
    val action: Unit
    val ok: Boolean
    val failure: Unit
    val info: Unit
    val error: Throwable?
    val results: List<Result>


    constructor(action: Unit = , ok: Boolean = true, failure: Unit = , info: Unit = , error: Throwable? = null, 
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
    val name: Unit
    val group: Unit


    constructor(name: Unit = , group: Unit = ) {
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
    val name: Unit
    val group: Unit


    constructor(name: Unit = , group: Unit = ) {
        this.name = name
        this.group = group
    }


    open fun supports()Boolean {
        throw IllegalAccessException("Not implemented yet.")
    }

    open fun create()List<Task> {
        throw IllegalAccessException("Not implemented yet.")
    }

    companion object {
        val EMPTY = TaskFactory()
    }
}


open class TaskGroup {
    val taskFactories: List<TaskFactory<Task>>
    val tasks: List<Task>


    constructor(taskFactories: List<TaskFactory<Task>> = arrayListOf(), tasks: List<Task> = arrayListOf()) {
        this.taskFactories = taskFactories
        this.tasks = tasks
    }

    companion object {
        val EMPTY = TaskGroup()
    }
}


open class TaskRegistry {
    val pathResolver: PathResolver


    constructor(pathResolver: PathResolver = PathResolver()) {
        this.pathResolver = pathResolver
    }


    open fun register()Unit {
        throw IllegalAccessException("Not implemented yet.")
    }

    companion object {
        val EMPTY = TaskRegistry()
    }
}


open class TaskRepository {
    val typeFactories: List<TaskFactory<Task>>


    constructor(typeFactories: List<TaskFactory<Task>> = arrayListOf()) {
        this.typeFactories = typeFactories
    }


    open fun <V : TaskFactory<Task>> register()Unit {
        throw IllegalAccessException("Not implemented yet.")
    }

    open fun <T : CompilationUnit> find()List<TaskFactory<Task>> {
        throw IllegalAccessException("Not implemented yet.")
    }

    companion object {
        val EMPTY = TaskRepository()
    }
}


open class TaskResult {
    val task: Task
    val result: Result


    constructor(task: Task = Task(), result: Result = Result()) {
        this.task = task
        this.result = result
    }

    companion object {
        val EMPTY = TaskResult()
    }
}




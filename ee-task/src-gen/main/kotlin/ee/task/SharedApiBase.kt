package ee.task

import ee.lang.ItemI
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

abstract class ResultBase {
    companion object {
        val EMPTY = Result()
    }
    var action: String = ""
    open var ok: Boolean = true
    var failure: String = ""
    var info: String = ""
    var error: Throwable? = null
    var results: List<Result> = emptyList()

    constructor(action: String = "", ok: Boolean = true, failure: String = "", info: String = "", error: Throwable? = null, 
        results: List<Result> = emptyList()) {
        this.action = action
        this.ok = ok
        this.failure = failure
        this.info = info
        this.error = error
        this.results = results
    }



}

fun ResultBase?.orEmpty(): Result {
    return if (this != null) this as Result else ResultBase.EMPTY
}

open class TaskResult {
    companion object {
        val EMPTY = TaskResult()
    }
    var task: Task = Task.EMPTY
    var result: Result = Result.EMPTY

    constructor(task: Task = Task.EMPTY, result: Result = Result.EMPTY) {
        this.task = task
        this.result = result
    }



}

fun TaskResult?.orEmpty(): TaskResult {
    return if (this != null) this else TaskResult.EMPTY
}

open class ExecConfig {
    companion object {
        val EMPTY = ExecConfig()
    }
    var home: Path = Paths.get("")
    var cmd: List<String> = emptyList()
    var env: Map<String, String> = emptyMap()
    var filterPattern: String = ""
    var failOnError: Boolean = false
    var filter: Boolean = false
    var noConsole: Boolean = false
    var wait: Boolean = true
    var timeout: Long = 30
    var timeoutUnit: TimeUnit = TimeUnit.SECONDS

    constructor(home: Path = Paths.get(""), cmd: List<String> = emptyList(), env: Map<String, String> = emptyMap(), 
        filterPattern: String = "", failOnError: Boolean = false, filter: Boolean = false, noConsole: Boolean = false, 
        wait: Boolean = true, timeout: Long = 30, timeoutUnit: TimeUnit = TimeUnit.SECONDS) {
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



}

fun ExecConfig?.orEmpty(): ExecConfig {
    return if (this != null) this else ExecConfig.EMPTY
}

open class TaskGroup {
    companion object {
        val EMPTY = TaskGroup()
    }
    var taskFactories: MutableList<TaskFactory<*> > = arrayListOf()
    var tasks: MutableList<Task> = arrayListOf()

    constructor(taskFactories: MutableList<TaskFactory<*> > = arrayListOf(), tasks: MutableList<Task> = arrayListOf()) {
        this.taskFactories = taskFactories
        this.tasks = tasks
    }



}

fun TaskGroup?.orEmpty(): TaskGroup {
    return if (this != null) this else TaskGroup.EMPTY
}

abstract class TaskBase {
    companion object {
        val EMPTY = Task()
    }
    var name: String = ""
    var group: String = ""
    constructor(name: String = "", group: String = "") {
        this.name = name
        this.group = group
    }


    abstract fun execute(output: (String) -> Unit = { line -> }): Result

}

fun TaskBase?.orEmpty(): Task {
    return if (this != null) this as Task else TaskBase.EMPTY
}

abstract class TaskFactoryBase<T : Task> {
    companion object {
        val EMPTY = TaskFactory<Task>()
    }
    var name: String = ""
    var group: String = ""
    constructor(name: String = "", group: String = "") {
        this.name = name
        this.group = group
    }


    abstract fun supports(items: List<ItemI<*>> = arrayListOf()): Boolean

    abstract fun create(items: List<ItemI<*>> = arrayListOf()): List<*>

}

fun TaskFactoryBase<*> ?.orEmpty(): TaskFactory<*>  {
    return if (this != null) this as TaskFactory<*>  else TaskFactoryBase.EMPTY
}

abstract class TaskRepositoryBase {
    companion object {
        val EMPTY = TaskRepository()
    }
    var typeFactories: MutableList<TaskFactory<*> > = arrayListOf()

    constructor(typeFactories: MutableList<TaskFactory<*> > = arrayListOf()) {
        this.typeFactories = typeFactories
    }


    abstract fun <V : TaskFactory<*> > register(factory: V)

    abstract fun <T : ItemI<*>> find(items: List<T>): List<*>

}

fun TaskRepositoryBase?.orEmpty(): TaskRepository {
    return if (this != null) this as TaskRepository else TaskRepositoryBase.EMPTY
}

abstract class PathResolverBase {
    companion object {
        val EMPTY = PathResolver()
    }
    var home: Path = Paths.get("")
    var itemToHome: MutableMap<String, String> = hashMapOf()

    constructor(home: Path = Paths.get(""), itemToHome: MutableMap<String, String> = hashMapOf()) {
        this.home = home
        this.itemToHome = itemToHome
    }


    abstract fun <T : ItemI<*>> resolve(item: T): Path

}

fun PathResolverBase?.orEmpty(): PathResolver {
    return if (this != null) this as PathResolver else PathResolverBase.EMPTY
}

abstract class TaskRegistryBase {
    companion object {
        val EMPTY = TaskRegistry()
    }
    var pathResolver: PathResolver = PathResolver.EMPTY
    constructor(pathResolver: PathResolver = PathResolver.EMPTY) {
        this.pathResolver = pathResolver
    }


    abstract fun register(repo: TaskRepository = TaskRepository.EMPTY)

}

fun TaskRegistryBase?.orEmpty(): TaskRegistry {
    return if (this != null) this as TaskRegistry else TaskRegistryBase.EMPTY
}



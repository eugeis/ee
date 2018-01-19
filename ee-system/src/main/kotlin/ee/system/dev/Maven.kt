package ee.system.dev

import ee.common.ext.exists
import ee.task.ExecConfig
import ee.task.Result
import ee.task.exec
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit

open class Maven : MavenBase {
    companion object {
        val EMPTY = MavenBase.EMPTY
    }

    val mvn: Path
    val defaultParams: Map<String, String>
    val defaultFlags: List<String>
    val defaultProfiles: List<String>
    val defaultCommandSuffix: String

    constructor(home: Path = Paths.get(""), plugins: MutableList<String> = arrayListOf(),
        defaultParams: MutableMap<String, String> = hashMapOf(), defaultFlags: List<String> = arrayListOf(),
        defaultProfiles: List<String> = arrayListOf(), defaultCommandSuffix: String = "") : super("maven", home,
        plugins) {
        mvn = home.resolve("bin/mvn")
        this.defaultParams = defaultParams
        this.defaultFlags = defaultFlags
        this.defaultProfiles = defaultProfiles
        this.defaultCommandSuffix = defaultCommandSuffix
    }

    override fun build(buildHome: Path, request: BuildRequest, output: (String) -> Unit): Result {
        val command = command(BuildRequest(tasks = ArrayList(request.tasks), params = HashMap(request.params),
            flags = ArrayList(request.flags), profiles = ArrayList(request.profiles)))
        val ret = buildHome.exec(
            ExecConfig(home = buildHome, cmd = command, wait = true, timeout = 10, timeoutUnit = TimeUnit.MINUTES),
            output = output)
        return Result(ok = (ret == 0))
    }

    fun command(request: BuildRequest): List<String> {
        val ret = arrayListOf<String>()
        ret.add(mvn.toString())
        prepareTasks(request)
        ret.addAll(request.tasks)

        defaultParams.fillParams(ret)
        request.params.fillParams(ret)

        defaultFlags.fillFlags(ret)
        request.flags.fillFlags(ret)

        defaultProfiles.fillProfiles(ret)
        request.profiles.fillProfiles(ret)

        if (defaultCommandSuffix.isNotBlank()) {
            ret.add(defaultCommandSuffix)
        }

        return ret
    }

    private fun prepareTasks(request: BuildRequest) {
        convertPluginTasks(request)
        val tasks = request.tasks
        if (tasks.isEmpty()) {
            request.task("install")
        } else {
            if (tasks.remove("build")) {
                request.task("install")
            }
            if (!tasks.contains("test")) {
                request.flag("skipTests")
            } else if (tasks.size > 1) {
                tasks.remove("test")
            }
        }
    }

    private fun convertPluginTasks(request: BuildRequest) {
        val tasks = ArrayList(request.tasks)
        request.tasks.clear()
        tasks.forEach { task ->
            val plugin = plugins.find { task.endsWith(it, true) }
            if (plugin != null) {
                request.tasks.add(if (task == plugin) "$plugin:$task" else "$plugin:${task.substring(0,
                    task.length - plugin.length)}")
            } else {
                request.tasks.add(task)
            }
        }
    }

    fun Map<String, String>.fillParams(to: MutableList<String>) {
        to.addAll(this.map { "-D${it.key}=${it.value}" })
    }

    fun List<String>.fillFlags(to: MutableList<String>) {
        to.addAll(map { "-D$it" })
    }

    fun List<String>.fillProfiles(to: MutableList<String>) {
        to.addAll(map { "-P$it" })
    }

    override fun supports(buildHome: Path): Boolean = buildHome.resolve("pom.xml").exists()
}


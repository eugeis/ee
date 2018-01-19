package ee.system.dev

import ee.common.ext.exists
import ee.task.ExecConfig
import ee.task.Result
import ee.task.exec
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit

open class Gradle : GradleBase {
    companion object {
        val EMPTY = GradleBase.EMPTY
    }

    val mvn: Path
    val defaultParams: Map<String, String>
    val defaultFlags: List<String>
    val defaultProfiles: List<String>
    val defaultCommandSuffix: String

    constructor(home: Path = Paths.get(""), defaultParams: MutableMap<String, String> = hashMapOf(),
        defaultFlags: List<String> = arrayListOf(), defaultProfiles: List<String> = arrayListOf(),
        defaultCommandSuffix: String = "") : super("maven", home) {
        mvn = home.resolve("bin/gradle")
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

        if (request.tasks.isEmpty()) {
            request.task("install")
        } else {
            if (request.tasks.remove("build")) {
                request.task("install")
            }
            if (request.tasks.size > 1 && !request.tasks.remove("test")) {
                request.flag("skipTests")
            }
        }

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

    fun Map<String, String>.fillParams(to: MutableList<String>) {
        to.addAll(this.map { "-D${it.key}=${it.value}" })
    }

    fun List<String>.fillFlags(to: MutableList<String>) {
        to.addAll(map { "-D$it" })
    }

    fun List<String>.fillProfiles(to: MutableList<String>) {
        to.addAll(map { "-P$it" })
    }

    override fun supports(buildHome: Path): Boolean = buildHome.resolve("build.gradle").exists()
}


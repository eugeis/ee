package ee.system.dev

import ee.system.Tool
import ee.task.Result
import java.nio.file.Path
import java.nio.file.Paths

abstract class BuildRequestBase {
    companion object {
        val EMPTY = BuildRequest()
    }

    var tasks: MutableList<String> = arrayListOf()
    var params: MutableMap<String, String> = hashMapOf()
    var flags: MutableList<String> = arrayListOf()
    var profiles: MutableList<String> = arrayListOf()

    constructor(tasks: MutableList<String> = arrayListOf(), params: MutableMap<String, String> = hashMapOf(),
        flags: MutableList<String> = arrayListOf(), profiles: MutableList<String> = arrayListOf()) {
        this.tasks = tasks
        this.params = params
        this.flags = flags
        this.profiles = profiles
    }


    abstract fun build(): BuildRequest

    abstract fun clean(): BuildRequest

    abstract fun test(): BuildRequest

    abstract fun integTest(): BuildRequest

    abstract fun acceptanceTest(): BuildRequest

    abstract fun install(): BuildRequest

    abstract fun publish(): BuildRequest

    abstract fun flag(flag: String = ""): BuildRequest

    abstract fun task(task: String = ""): BuildRequest

    abstract fun profile(profile: String = ""): BuildRequest

    abstract fun param(name: String = "", value: String = ""): BuildRequest

}

fun BuildRequestBase?.orEmpty(): BuildRequest {
    return if (this != null) this as BuildRequest else BuildRequestBase.EMPTY
}

abstract class BuildToolBase : Tool {
    companion object {
        val EMPTY = BuildTool()
    }

    constructor(elName: String = "", home: Path = Paths.get("")) : super(elName, home) {

    }


    abstract fun buildRequest(): BuildRequest

    abstract fun supports(buildHome: Path = Paths.get("")): Boolean

    abstract fun build(buildHome: Path = Paths.get(""), request: BuildRequest = BuildRequest.EMPTY,
        output: (String) -> Unit = { }): Result

}

fun BuildToolBase?.orEmpty(): BuildTool {
    return if (this != null) this as BuildTool else BuildToolBase.EMPTY
}

abstract class MavenBase : BuildTool {
    companion object {
        val EMPTY = Maven()
    }

    var plugins: MutableList<String> = arrayListOf()

    constructor(elName: String = "", home: Path = Paths.get(""), plugins: MutableList<String> = arrayListOf()) : super(
        elName, home) {
        this.plugins = plugins
    }


}

fun MavenBase?.orEmpty(): Maven {
    return if (this != null) this as Maven else MavenBase.EMPTY
}

abstract class GradleBase : BuildTool {
    companion object {
        val EMPTY = Gradle()
    }

    constructor(elName: String = "", home: Path = Paths.get("")) : super(elName, home) {

    }


}

fun GradleBase?.orEmpty(): Gradle {
    return if (this != null) this as Gradle else GradleBase.EMPTY
}

abstract class BuildToolFactoryBase {
    companion object {
        val EMPTY = BuildToolFactory()
    }

    var maven: Maven = Maven.EMPTY
    var gradle: Gradle = Gradle.EMPTY

    constructor(maven: Maven = Maven.EMPTY, gradle: Gradle = Gradle.EMPTY) {
        this.maven = maven
        this.gradle = gradle
    }


    abstract fun buildTool(buildHome: Path = Paths.get("")): BuildTool

}

fun BuildToolFactoryBase?.orEmpty(): BuildToolFactory {
    return if (this != null) this as BuildToolFactory else BuildToolFactoryBase.EMPTY
}



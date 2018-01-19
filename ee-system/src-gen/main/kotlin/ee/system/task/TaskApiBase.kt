package ee.system.task

import ee.system.dev.BuildRequest
import ee.system.dev.BuildTool
import ee.system.dev.BuildToolFactory
import ee.task.PathResolver
import ee.task.Task
import ee.task.TaskFactory
import ee.task.TaskRegistry
import java.nio.file.Path
import java.nio.file.Paths

abstract class BuildTaskBase : Task {
    companion object {
        val EMPTY = BuildTask()
    }

    var buildTool: BuildTool = BuildTool.EMPTY
    var buildHome: Path = Paths.get("")
    var buildRequest: BuildRequest = BuildRequest.EMPTY

    constructor(name: String = "", group: String = "", buildTool: BuildTool = BuildTool.EMPTY,
        buildHome: Path = Paths.get(""), buildRequest: BuildRequest = BuildRequest.EMPTY) {
        this.name = name
        this.group = group
        this.buildTool = buildTool
        this.buildHome = buildHome
        this.buildRequest = buildRequest
    }


}

fun BuildTaskBase?.orEmpty(): BuildTask {
    return if (this != null) this as BuildTask else BuildTaskBase.EMPTY
}

abstract class BuildTaskFactoryBase : TaskFactory<BuildTask> {
    companion object {
        val EMPTY = BuildTaskFactory()
    }

    var pathResolver: PathResolver = PathResolver.EMPTY
    var buildToolFactory: BuildToolFactory = BuildToolFactory.EMPTY
    var buildRequest: BuildRequest = BuildRequest.EMPTY

    constructor(name: String = "", group: String = "", pathResolver: PathResolver = PathResolver.EMPTY,
        buildToolFactory: BuildToolFactory = BuildToolFactory.EMPTY, buildRequest: BuildRequest = BuildRequest.EMPTY) {
        this.name = name
        this.group = group
        this.pathResolver = pathResolver
        this.buildToolFactory = buildToolFactory
        this.buildRequest = buildRequest
    }


}

fun BuildTaskFactoryBase?.orEmpty(): BuildTaskFactory {
    return if (this != null) this as BuildTaskFactory else BuildTaskFactoryBase.EMPTY
}

abstract class SystemTaskRegistryBase : TaskRegistry {
    companion object {
        val EMPTY = SystemTaskRegistry()
    }

    var buildToolFactory: BuildToolFactory = BuildToolFactory.EMPTY

    constructor(pathResolver: PathResolver = PathResolver.EMPTY,
        buildToolFactory: BuildToolFactory = BuildToolFactory.EMPTY) {
        this.pathResolver = pathResolver
        this.buildToolFactory = buildToolFactory
    }


}

fun SystemTaskRegistryBase?.orEmpty(): SystemTaskRegistry {
    return if (this != null) this as SystemTaskRegistry else SystemTaskRegistryBase.EMPTY
}



package ee.system.task

import ee.system.dev.BuildRequest
import ee.system.dev.BuildToolFactory
import ee.task.PathResolver

open class BuildTaskFactory : BuildTaskFactoryBase {
    companion object {
        val EMPTY = BuildTaskFactoryBase.EMPTY
    }

    constructor() {
    }

    constructor(name: String, group: String = "Build", pathResolver: PathResolver, buildToolFactory: BuildToolFactory,
        buildRequest: BuildRequest) : super(name, group, pathResolver, buildToolFactory, buildRequest) {
    }
}


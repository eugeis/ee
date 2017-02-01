package ee.system.task

import ee.system.dev.BuildRequest
import ee.system.dev.BuildToolFactory
import ee.task.PathResolver
import ee.task.TaskRepository

open class SystemTaskRegistry : SystemTaskRegistryBase {
    companion object {
        val EMPTY = SystemTaskRegistryBase.EMPTY
    }

    constructor() : super() {
    }

    constructor(pathResolver: PathResolver, buildToolFactory: BuildToolFactory) : super(pathResolver, buildToolFactory) {
    }

    override fun register(repo: TaskRepository) {
        registerBuildTaskFactories(repo)
    }

    private fun registerBuildTaskFactories(repo: TaskRepository) {
        repo.register(BuildTaskFactory("build", "Build", pathResolver, buildToolFactory, BuildRequest().build()))
        repo.register(BuildTaskFactory("clean", "Build", pathResolver, buildToolFactory, BuildRequest().clean()))
        repo.register(BuildTaskFactory("cleanBuild", "Build", pathResolver, buildToolFactory, BuildRequest().clean().build()))
        repo.register(BuildTaskFactory("test", "Build", pathResolver, buildToolFactory, BuildRequest().test()))
        repo.register(BuildTaskFactory("buildTest", "Build", pathResolver, buildToolFactory, BuildRequest().build().test()))
        repo.register(BuildTaskFactory("cleanBuildTest", "Build", pathResolver, buildToolFactory, BuildRequest().clean().build().test()))

        repo.register(BuildTaskFactory("eclipse", "Eclipse", pathResolver, buildToolFactory, BuildRequest().task("eclipse").profile("eclipse")))
        repo.register(BuildTaskFactory("cleanEclipse", "Eclipse", pathResolver, buildToolFactory, BuildRequest().task("cleanEclipse").profile("eclipse")))
    }

    private fun registerServiceTaskFactories(repo: TaskRepository) {
        repo.register(BuildTaskFactory("start", "Service", pathResolver, buildToolFactory, BuildRequest().build()))
    }
}


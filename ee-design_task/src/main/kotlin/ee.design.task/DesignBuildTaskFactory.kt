package ee.task

import ee.common.ext.exists
import ee.common.ext.logger
import ee.design.BundleI
import ee.design.CompI
import ee.design.ModelI
import ee.design.ModuleI
import ee.lang.ItemI
import ee.lang.StructureUnit
import ee.lang.StructureUnitI
import ee.lang.findDownByType
import ee.system.dev.BuildRequest
import ee.system.dev.BuildToolFactory
import ee.system.task.BuildTask
import ee.system.task.BuildTaskFactory
import java.nio.file.Path
import java.nio.file.Paths

open class DesignBuildTaskFactory : BuildTaskFactory {
    val log = logger()

    constructor() {
    }

    constructor(name: String, group: String = "Build", pathResolver: PathResolver, buildToolFactory: BuildToolFactory,
        buildRequest: BuildRequest) : super(name, group, pathResolver, buildToolFactory, buildRequest) {
    }

    override fun create(items: List<ItemI<*>>): List<BuildTask> =
        items.filterIsInstance(StructureUnitI::class.java).fillBuildTasks()

    fun ModelI<*>.fillBuildTasks(to: MutableList<BuildTask>) =
        findDownByType(CompI::class.java).forEach { it.fillBuildTasks(to) }

    fun List<StructureUnitI<*>>.fillBuildTasks(to: MutableList<BuildTask> = arrayListOf()): List<BuildTask> {
        forEach {
            if (it is ModelI) {
                it.fillBuildTasks(to)
            } else if (it is CompI) {
                it.fillBuildTasks(to)
            } else if (it is ModuleI) {
                it.fillBuildTasks(to)
            } else if (it is BundleI) {
                it.fillBuildTasks(to)
            } else {
                log.warn("Build not possible for this type of structure unit $it")
            }
        }
        return to
    }

    fun BundleI<*>.fillBuildTasks(to: MutableList<BuildTask>) = units().fillBuildTasks(to)

    fun CompI<*>.fillBuildTasks(to: MutableList<BuildTask>) {

        val path: Path = pathResolver.resolve(this)
        val releaseUnits = Paths.get("$path-root/release-units")
        moduleGroups().forEach { mg ->
            val releaseUnitPath = releaseUnits.resolve(mg.name())
            if (releaseUnitPath.exists()) {
                to.add(
                    BuildTask("$name: ${artifact()}(${mg.name()})", group, buildToolFactory.buildTool(releaseUnitPath),
                        releaseUnitPath, buildRequest))
            } else {
                log.warn("Release unit does not exists $mg")
            }
        }
    }

    fun ModuleI<*>.fillBuildTasks(to: MutableList<BuildTask>) {
        val path: Path = pathResolver.resolve(this)
        if (path.exists()) {
            to.add(BuildTask("$name: ${artifact()}", group, buildToolFactory.buildTool(path), path, buildRequest))
        } else {
            log.warn("Module does not exists $this")
        }
    }

    override fun supports(items: List<ItemI<*>>): Boolean {
        return items.find { it is StructureUnit } != null
    }
}


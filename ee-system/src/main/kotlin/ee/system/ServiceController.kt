package ee.system

import ee.common.ext.copyRecursively
import ee.common.ext.deleteFilesRecursively
import java.nio.file.Path

open class ServiceController<T : Service> : ServiceControllerBase<T> {
    companion object {
        val EMPTY = ServiceControllerBase.EMPTY
    }

    constructor(item: T) : super(item) {

    }

    override fun start() {
        throw IllegalAccessException("Not implemented yet.")
    }

    override fun stop() {
        throw IllegalAccessException("Not implemented yet.")
    }

    override fun ping(): Boolean {
        throw IllegalAccessException("Not implemented yet.")
    }

    override fun copyLogs(target: Path) {
        val targetForService = target.resolve(item.name())
        collectLogs().forEach { it.copyRecursively(targetForService) }
    }

    override fun copyConfigs(target: Path) {
        val targetForService = target.resolve(item.name())
        collectConfigs().forEach { it.copyRecursively(targetForService) }
    }

    override fun deleteLogs() {
        collectLogs().forEach(Path::deleteFilesRecursively)
    }

    override fun deleteOpsData() {
        collectOpsData().forEach(Path::deleteFilesRecursively)
    }

    protected fun collectLogs(): List<Path> {
        return emptyList()
    }

    protected fun collectOpsData(): List<Path> {
        return emptyList()
    }

    protected fun collectConfigs(): List<Path> {
        return emptyList()
    }
}


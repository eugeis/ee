package ee.system

import ee.lang.Composite
import java.nio.file.Path
import java.nio.file.Paths

enum class PackageState(var order: Int = 0) {
    UNKNOWN(0),
    RESOLVED(1),
    PREPARED(2),
    INSTALLED(3),
    UNINSTALLED(-1);


    fun isUnknown(): Boolean = this == UNKNOWN
    fun isResolved(): Boolean = this == RESOLVED
    fun isPrepared(): Boolean = this == PREPARED
    fun isInstalled(): Boolean = this == INSTALLED
    fun isUninstalled(): Boolean = this == UNINSTALLED
}

fun String?.toPackageState(): PackageState {
    return if (this != null) {
        PackageState.valueOf(this)
    } else {
        PackageState.UNKNOWN
    }
}

data class PackageCoordinate(var classifier: String = "", var version: String = "", var group: String = "", var artifact: String = "",
                             var type: String = "", var packaging: String = "") {
    companion object {
        val EMPTY = PackageCoordinate()
    }


}

fun PackageCoordinate?.orEmpty(): PackageCoordinate {
    return if (this != null) this else PackageCoordinate.EMPTY
}

data class PackagePaths(var resolved: Path = Paths.get(""), var prepared: Path = Paths.get(""), var installed: Path = Paths.get("")) {
    companion object {
        val EMPTY = PackagePaths()
    }


}

fun PackagePaths?.orEmpty(): PackagePaths {
    return if (this != null) this else PackagePaths.EMPTY
}

abstract class SystemBase : Composite {


    constructor(elName: String = "") : super({ name(elName) }) {

    }


}


open class System : SystemBase {
    companion object {
        val EMPTY = System()
    }

    var machines: MutableList<Machine> = arrayListOf()

    constructor(elName: String = "", machines: MutableList<Machine> = arrayListOf()) : super(elName) {
        this.machines = machines
    }


}

fun System?.orEmpty(): System {
    return if (this != null) this else System.EMPTY
}

open class Machine : SystemBase {
    companion object {
        val EMPTY = Machine()
    }

    var workspaces: MutableList<Workspace> = arrayListOf()

    constructor(elName: String = "", workspaces: MutableList<Workspace> = arrayListOf()) : super(elName) {
        this.workspaces = workspaces
    }


}

fun Machine?.orEmpty(): Machine {
    return if (this != null) this else Machine.EMPTY
}

open class Workspace : SystemBase {
    companion object {
        val EMPTY = Workspace()
    }

    var home: Path = Paths.get("")
    var meta: Path = Paths.get("")
    var prepared: Path = Paths.get("")
    var services: MutableList<Service> = arrayListOf()
    var tools: MutableList<Tool> = arrayListOf()
    var packages: MutableList<Package> = arrayListOf()

    constructor(elName: String = "", home: Path = Paths.get(""), meta: Path = Paths.get(""), prepared: Path = Paths.get(""),
                services: MutableList<Service> = arrayListOf(), tools: MutableList<Tool> = arrayListOf(),
                packages: MutableList<Package> = arrayListOf()) : super(elName) {
        this.home = home
        this.meta = meta
        this.prepared = prepared
        this.services = services
        this.tools = tools
        this.packages = packages
    }


}

fun Workspace?.orEmpty(): Workspace {
    return if (this != null) this else Workspace.EMPTY
}

open class Service : SystemBase {
    companion object {
        val EMPTY = Service()
    }

    var category: String = ""
    var dependsOn: MutableList<Service> = arrayListOf()
    var dependsOnMe: MutableList<Service> = arrayListOf()

    constructor(elName: String = "", category: String = "", dependsOn: MutableList<Service> = arrayListOf(),
                dependsOnMe: MutableList<Service> = arrayListOf()) : super(elName) {
        this.category = category
        this.dependsOn = dependsOn
        this.dependsOnMe = dependsOnMe
    }


}

fun Service?.orEmpty(): Service {
    return if (this != null) this else Service.EMPTY
}

abstract class SocketServiceBase : Service {
    companion object {
        val EMPTY = SocketService()
    }

    var host: String = ""
    var port: Int = 0

    constructor(elName: String = "", category: String = "", dependsOn: MutableList<Service> = arrayListOf(),
                dependsOnMe: MutableList<Service> = arrayListOf(), host: String = "", port: Int = 0) : super(elName, category, dependsOn, dependsOnMe) {
        this.host = host
        this.port = port
    }


}

fun SocketServiceBase?.orEmpty(): SocketService {
    return if (this != null) this as SocketService else SocketServiceBase.EMPTY
}

open class JmxService : SocketService {
    companion object {
        val EMPTY = JmxService()
    }


    constructor(elName: String = "", category: String = "", dependsOn: MutableList<Service> = arrayListOf(),
                dependsOnMe: MutableList<Service> = arrayListOf(), host: String = "", port: Int = 0) : super(elName, category, dependsOn, dependsOnMe, host, port) {

    }


}

fun JmxService?.orEmpty(): JmxService {
    return if (this != null) this else JmxService.EMPTY
}

open class JavaService : SocketService {
    companion object {
        val EMPTY = JavaService()
    }

    var home: Path = Paths.get("")
    var logs: Path = Paths.get("")
    var configs: Path = Paths.get("")
    var command: String = ""

    constructor(elName: String = "", category: String = "", dependsOn: MutableList<Service> = arrayListOf(),
                dependsOnMe: MutableList<Service> = arrayListOf(), host: String = "", port: Int = 0, home: Path = Paths.get(""),
                logs: Path = Paths.get(""), configs: Path = Paths.get(""), command: String = "") : super(elName, category, dependsOn, dependsOnMe, host, port) {
        this.home = home
        this.logs = logs
        this.configs = configs
        this.command = command
    }


}

fun JavaService?.orEmpty(): JavaService {
    return if (this != null) this else JavaService.EMPTY
}

open class Package : SystemBase {
    companion object {
        val EMPTY = Package()
    }

    var category: String = ""
    var coordinate: PackageCoordinate = PackageCoordinate.EMPTY
    var state: PackageState = PackageState.UNKNOWN
    var dependsOn: MutableList<Package> = arrayListOf()
    var dependsOnMe: MutableList<Package> = arrayListOf()
    var paths: PackagePaths = PackagePaths.EMPTY

    constructor(elName: String = "", category: String = "", coordinate: PackageCoordinate = PackageCoordinate.EMPTY,
                state: PackageState = PackageState.UNKNOWN, dependsOn: MutableList<Package> = arrayListOf(),
                dependsOnMe: MutableList<Package> = arrayListOf(), paths: PackagePaths = PackagePaths.EMPTY) : super(elName) {
        this.category = category
        this.coordinate = coordinate
        this.state = state
        this.dependsOn = dependsOn
        this.dependsOnMe = dependsOnMe
        this.paths = paths
    }


}

fun Package?.orEmpty(): Package {
    return if (this != null) this else Package.EMPTY
}

open class ContentPackage : Package {
    companion object {
        val EMPTY = ContentPackage()
    }

    var targetCoordinate: PackageCoordinate = PackageCoordinate.EMPTY

    constructor(elName: String = "", category: String = "", coordinate: PackageCoordinate = PackageCoordinate.EMPTY,
                state: PackageState = PackageState.UNKNOWN, dependsOn: MutableList<Package> = arrayListOf(),
                dependsOnMe: MutableList<Package> = arrayListOf(), paths: PackagePaths = PackagePaths.EMPTY,
                targetCoordinate: PackageCoordinate = PackageCoordinate.EMPTY) : super(elName, category, coordinate, state, dependsOn, dependsOnMe, paths) {
        this.targetCoordinate = targetCoordinate
    }


}

fun ContentPackage?.orEmpty(): ContentPackage {
    return if (this != null) this else ContentPackage.EMPTY
}

open class MetaPackage : Package {
    companion object {
        val EMPTY = MetaPackage()
    }

    var targetCoordinate: PackageCoordinate = PackageCoordinate.EMPTY

    constructor(elName: String = "", category: String = "", coordinate: PackageCoordinate = PackageCoordinate.EMPTY,
                state: PackageState = PackageState.UNKNOWN, dependsOn: MutableList<Package> = arrayListOf(),
                dependsOnMe: MutableList<Package> = arrayListOf(), paths: PackagePaths = PackagePaths.EMPTY,
                targetCoordinate: PackageCoordinate = PackageCoordinate.EMPTY) : super(elName, category, coordinate, state, dependsOn, dependsOnMe, paths) {
        this.targetCoordinate = targetCoordinate
    }


}

fun MetaPackage?.orEmpty(): MetaPackage {
    return if (this != null) this else MetaPackage.EMPTY
}

open class AddonPackage : MetaPackage {
    companion object {
        val EMPTY = AddonPackage()
    }

    var extract: Boolean = false
    var includes: String = ""
    var excludes: String = ""
    var target: Path = Paths.get("")

    constructor(elName: String = "", category: String = "", coordinate: PackageCoordinate = PackageCoordinate.EMPTY,
                state: PackageState = PackageState.UNKNOWN, dependsOn: MutableList<Package> = arrayListOf(),
                dependsOnMe: MutableList<Package> = arrayListOf(), paths: PackagePaths = PackagePaths.EMPTY,
                targetCoordinate: PackageCoordinate = PackageCoordinate.EMPTY, extract: Boolean = false, includes: String = "",
                excludes: String = "", target: Path = Paths.get("")) : super(elName, category, coordinate, state, dependsOn, dependsOnMe, paths, targetCoordinate) {
        this.extract = extract
        this.includes = includes
        this.excludes = excludes
        this.target = target
    }


}

fun AddonPackage?.orEmpty(): AddonPackage {
    return if (this != null) this else AddonPackage.EMPTY
}

abstract class ToolBase : SystemBase {
    companion object {
        val EMPTY = Tool()
    }

    var home: Path = Paths.get("")

    constructor(elName: String = "", home: Path = Paths.get("")) : super(elName) {
        this.home = home
    }


}

fun ToolBase?.orEmpty(): Tool {
    return if (this != null) this as Tool else ToolBase.EMPTY
}

abstract class ServiceControllerBase<T : Service> {
    companion object {
        val EMPTY = ServiceController<Service>(Service.EMPTY)
    }

    var item: T

    constructor(item: T) {
        this.item = item
    }


    abstract fun start()

    abstract fun stop()

    abstract fun ping(): Boolean

    abstract fun copyLogs(target: Path = Paths.get(""))

    abstract fun copyConfigs(target: Path = Paths.get(""))

    abstract fun deleteLogs()

    abstract fun deleteOpsData()

}

fun ServiceControllerBase<*>?.orEmpty(): ServiceController<*> {
    return if (this != null) this as ServiceController<*> else ServiceControllerBase.EMPTY
}

abstract class ServiceQueriesBase<T : Service> {
    companion object {
        val EMPTY = ServiceQueries<Service>()
    }

    constructor() {

    }


}

fun ServiceQueriesBase<*>?.orEmpty(): ServiceQueries<*> {
    return if (this != null) this as ServiceQueries<*> else ServiceQueriesBase.EMPTY
}

abstract class ServiceCommandsBase<T : Service> {
    companion object {
        val EMPTY = ServiceCommands<Service>(Service.EMPTY)
    }

    var item: T

    constructor(item: T) {
        this.item = item
    }


}

fun ServiceCommandsBase<*>?.orEmpty(): ServiceCommands<*> {
    return if (this != null) this as ServiceCommands<*> else ServiceCommandsBase.EMPTY
}

abstract class SocketServiceControllerBase<T : SocketService> : ServiceController<T> {
    companion object {
        val EMPTY = SocketServiceController<SocketService>(SocketService.EMPTY)
    }

    constructor(item: T) : super(item) {

    }


}

fun SocketServiceControllerBase<*>?.orEmpty(): SocketServiceController<*> {
    return if (this != null) this as SocketServiceController<*> else SocketServiceControllerBase.EMPTY
}

abstract class SocketServiceQueriesBase<T : SocketService> : ServiceQueries<T> {
    companion object {
        val EMPTY = SocketServiceQueries<SocketService>()
    }

    constructor() : super() {

    }


}

fun SocketServiceQueriesBase<*>?.orEmpty(): SocketServiceQueries<*> {
    return if (this != null) this as SocketServiceQueries<*> else SocketServiceQueriesBase.EMPTY
}

abstract class SocketServiceCommandsBase<T : SocketService> : ServiceCommands<T> {
    companion object {
        val EMPTY = SocketServiceCommands<SocketService>(SocketService.EMPTY)
    }

    constructor(item: T) : super(item) {

    }


}

fun SocketServiceCommandsBase<*>?.orEmpty(): SocketServiceCommands<*> {
    return if (this != null) this as SocketServiceCommands<*> else SocketServiceCommandsBase.EMPTY
}

abstract class JmxServiceControllerBase<T : JmxService> : SocketServiceController<T> {
    companion object {
        val EMPTY = JmxServiceController<JmxService>(JmxService.EMPTY)
    }

    constructor(item: T) : super(item) {

    }


}

fun JmxServiceControllerBase<*>?.orEmpty(): JmxServiceController<*> {
    return if (this != null) this as JmxServiceController<*> else JmxServiceControllerBase.EMPTY
}

abstract class JmxServiceQueriesBase<T : JmxService> : SocketServiceQueries<T> {
    companion object {
        val EMPTY = JmxServiceQueries<JmxService>()
    }

    constructor() : super() {

    }


}

fun JmxServiceQueriesBase<*>?.orEmpty(): JmxServiceQueries<*> {
    return if (this != null) this as JmxServiceQueries<*> else JmxServiceQueriesBase.EMPTY
}

abstract class JmxServiceCommandsBase<T : JmxService> : SocketServiceCommands<T> {
    companion object {
        val EMPTY = JmxServiceCommands<JmxService>(JmxService.EMPTY)
    }

    constructor(item: T) : super(item) {

    }


}

fun JmxServiceCommandsBase<*>?.orEmpty(): JmxServiceCommands<*> {
    return if (this != null) this as JmxServiceCommands<*> else JmxServiceCommandsBase.EMPTY
}

abstract class JavaServiceControllerBase<T : JavaService> : SocketServiceController<T> {
    companion object {
        val EMPTY = JavaServiceController<JavaService>(JavaService.EMPTY)
    }

    constructor(item: T) : super(item) {

    }


}

fun JavaServiceControllerBase<*>?.orEmpty(): JavaServiceController<*> {
    return if (this != null) this as JavaServiceController<*> else JavaServiceControllerBase.EMPTY
}

abstract class JavaServiceQueriesBase<T : JavaService> : SocketServiceQueries<T> {
    companion object {
        val EMPTY = JavaServiceQueries<JavaService>()
    }

    constructor() : super() {

    }


}

fun JavaServiceQueriesBase<*>?.orEmpty(): JavaServiceQueries<*> {
    return if (this != null) this as JavaServiceQueries<*> else JavaServiceQueriesBase.EMPTY
}

abstract class JavaServiceCommandsBase<T : JavaService> : SocketServiceCommands<T> {
    companion object {
        val EMPTY = JavaServiceCommands<JavaService>(JavaService.EMPTY)
    }

    constructor(item: T) : super(item) {

    }


}

fun JavaServiceCommandsBase<*>?.orEmpty(): JavaServiceCommands<*> {
    return if (this != null) this as JavaServiceCommands<*> else JavaServiceCommandsBase.EMPTY
}

abstract class PackageControllerBase<T : Package> {
    companion object {
        val EMPTY = PackageController<Package>(Package.EMPTY)
    }

    var item: T

    constructor(item: T) {
        this.item = item
    }


    abstract fun prepare(params: Map<String, String> = hashMapOf())

    abstract fun configure(params: Map<String, String> = hashMapOf())

    abstract fun install(params: Map<String, String> = hashMapOf())

    abstract fun uninstall()

}

fun PackageControllerBase<*>?.orEmpty(): PackageController<*> {
    return if (this != null) this as PackageController<*> else PackageControllerBase.EMPTY
}

abstract class PackageCommandsBase<T : Package> {
    companion object {
        val EMPTY = PackageCommands<Package>(Package.EMPTY)
    }

    var item: T

    constructor(item: T) {
        this.item = item
    }


}

fun PackageCommandsBase<*>?.orEmpty(): PackageCommands<*> {
    return if (this != null) this as PackageCommands<*> else PackageCommandsBase.EMPTY
}

abstract class ContentPackageControllerBase<T : ContentPackage> : PackageController<T> {
    companion object {
        val EMPTY = ContentPackageController<ContentPackage>(ContentPackage.EMPTY)
    }

    constructor(item: T) : super(item) {

    }


}

fun ContentPackageControllerBase<*>?.orEmpty(): ContentPackageController<*> {
    return if (this != null) this as ContentPackageController<*> else ContentPackageControllerBase.EMPTY
}

abstract class ContentPackageCommandsBase<T : ContentPackage> : PackageCommands<T> {
    companion object {
        val EMPTY = ContentPackageCommands<ContentPackage>(ContentPackage.EMPTY)
    }

    constructor(item: T) : super(item) {

    }


}

fun ContentPackageCommandsBase<*>?.orEmpty(): ContentPackageCommands<*> {
    return if (this != null) this as ContentPackageCommands<*> else ContentPackageCommandsBase.EMPTY
}

abstract class MetaPackageControllerBase<T : MetaPackage> : PackageController<T> {
    companion object {
        val EMPTY = MetaPackageController<MetaPackage>(MetaPackage.EMPTY)
    }

    constructor(item: T) : super(item) {

    }


}

fun MetaPackageControllerBase<*>?.orEmpty(): MetaPackageController<*> {
    return if (this != null) this as MetaPackageController<*> else MetaPackageControllerBase.EMPTY
}

abstract class MetaPackageCommandsBase<T : MetaPackage> : PackageCommands<T> {
    companion object {
        val EMPTY = MetaPackageCommands<MetaPackage>(MetaPackage.EMPTY)
    }

    constructor(item: T) : super(item) {

    }


}

fun MetaPackageCommandsBase<*>?.orEmpty(): MetaPackageCommands<*> {
    return if (this != null) this as MetaPackageCommands<*> else MetaPackageCommandsBase.EMPTY
}

abstract class AddonPackageControllerBase<T : AddonPackage> : MetaPackageController<T> {
    companion object {
        val EMPTY = AddonPackageController<AddonPackage>(AddonPackage.EMPTY)
    }

    constructor(item: T) : super(item) {

    }


}

fun AddonPackageControllerBase<*>?.orEmpty(): AddonPackageController<*> {
    return if (this != null) this as AddonPackageController<*> else AddonPackageControllerBase.EMPTY
}

abstract class AddonPackageCommandsBase<T : AddonPackage> : MetaPackageCommands<T> {
    companion object {
        val EMPTY = AddonPackageCommands<AddonPackage>(AddonPackage.EMPTY)
    }

    constructor(item: T) : super(item) {

    }


}

fun AddonPackageCommandsBase<*>?.orEmpty(): AddonPackageCommands<*> {
    return if (this != null) this as AddonPackageCommands<*> else AddonPackageCommandsBase.EMPTY
}



package ee.system

import ee.design.*
import ee.lang.*
import ee.task.Task.shared.PathResolver
import ee.task.Task.shared.Result
import ee.task.Task.shared.Task
import ee.task.Task.shared.TaskFactory
import ee.task.Task.shared.TaskRegistry

object System : Comp({ artifact("ee-system").namespace("ee.system") }) {
    object shared : Module() {
        object SystemBase : Values({
            superUnit(l.Composite)
            virtual(true)
        })

        object System : Entity({ superUnit(SystemBase) }) {
            val machines = prop(n.List.GT(Machine))
        }

        object Machine : Entity({ superUnit(SystemBase) }) {
            val workspaces = prop(n.List.GT(Workspace))
        }

        object Workspace : Entity({ superUnit(SystemBase) }) {
            val home = prop(n.Path)
            val meta = prop(n.Path)
            val prepared = prop(n.Path)

            val services = prop(n.List.GT(Service))
            val tools = prop(n.List.GT(Tool))
            val packages = prop(n.List.GT(Package))
        }

        object Service : Entity({ superUnit(SystemBase) }) {
            val category = prop()
            val dependsOn = prop(n.List.GT(Service))
            val dependsOnMe = prop(n.List.GT(Service))

            object queries : QueryController() {
                val findByCategory = findBy(category)
            }

            object commands : CommandController() {
                val start = command()
                val stop = command()
                val ping = command()

                val copyLogs = command { p("target", n.Path) }

                val copyConfigs = command { p("target", n.Path) }

                val deleteLogs = command()
                val deleteOpsData = command()
            }

            object controller : Controller() {
                val start = op()
                val stop = op()
                val ping = op { ret(n.Boolean) }

                val copyLogs = op { p("target", n.Path) }
                val copyConfigs = op { p("target", n.Path) }

                val deleteLogs = op()
                val deleteOpsData = op()
            }
        }

        object SocketService : Entity({
            superUnit(Service)
            base(true)
        }) {
            val host = prop()
            val port = prop(n.Int)
        }

        object JmxService : Entity({ superUnit(SocketService) }) {
        }

        object JavaService : Entity({ superUnit(SocketService) }) {
            val home = prop(n.Path)
            val logs = prop(n.Path)
            val configs = prop(n.Path)
            val command = prop()
        }

        object Tool : Controller({ superUnit(SystemBase) }) {
            val home = prop(n.Path)
        }

        object PackageCoordinate : Basic() {
            val classifier = prop()
            val version = prop()
            val group = prop()
            val artifact = prop()
            val type = prop()
            val packaging = prop()
        }

        object PackageState : EnumType() {
            val order = prop(n.Int)

            val Unknown = lit { p(order).value(0) }
            val Resolved = lit { p(order).value(1) }
            val Prepared = lit { p(order).value(2) }
            val Installed = lit { p(order).value(3) }
            val Uninstalled = lit { p(order).value(-1) }
        }

        object PackagePaths : Basic() {
            val resolved = prop(n.Path)
            val prepared = prop(n.Path)
            val installed = prop(n.Path)
        }

        object Package : Entity({ superUnit(SystemBase) }) {
            val category = prop()
            val coordinate = prop(PackageCoordinate)
            val state = prop(PackageState)

            val dependsOn = prop(n.List.GT(Package))
            val dependsOnMe = prop(n.List.GT(Package))

            val paths = prop(PackagePaths)

            object commands : CommandController() {
                val prepare = command(p("params", n.Map))
                val configure = command(p("params", n.Map))
                val install = command(p("params", n.Map))
                val uninstall = command()
            }

            object controller : Controller() {
                val prepare = op(p("params", n.Map))
                val configure = op(p("params", n.Map))
                val install = op(p("params", n.Map))
                val uninstall = op()
            }
        }

        object ContentPackage : Entity({ superUnit(Package) }) {
            val targetCoordinate = prop(PackageCoordinate)
        }

        object MetaPackage : Entity({ superUnit(Package) }) {
            val targetCoordinate = prop(PackageCoordinate)
        }

        object AddonPackage : Entity({ superUnit(MetaPackage) }) {
            val extract = prop(n.Boolean)
            val includes = prop()
            val excludes = prop()
            val target = prop(n.Path)
        }
    }

    object srv : Module() {
        object MySqlTool : Controller({
            superUnit(shared.Tool)
            base(true)
        }) {
            val bin = prop(n.Path)

            val start = op(p("defaultsFile", n.Path), p("params", n.Map))
            val stop = op(p("user"), p("password") { hidden(true) })
            val sql = op(p("sqlCommand"), p("user"), p("password"), p("schema") { value("") })
            val truncateDb = op(p("schema"), p("sqlCommand"), p("user"), p("password"))
        }

        object WildFlyTool : Controller({
            superUnit(shared.Tool)
            base(true)
        }) {
            val bin = prop(n.Path)

            val start = op(p("config", n.Path), p("host"), p("port", n.Int),
                    p("params", n.Map))
            val stop = op(p("host"),
                    p("managementPort", n.Int), p("managementUser"), p("managementPassword"))

        }

        object WildFlyService : Entity({
            superUnit(shared.SocketService)
            base(true)
        }) {
            val home = prop(n.Path)
            val config = prop(n.Path)
            val managementPort = prop { type(n.Int).value(9990) }
            val managementUser = prop { value("admin") }
            val managementPassword = prop { value("admin") }
            val defaultParams = prop(n.Map)

            val tool = prop(WildFlyTool)
        }

        object MySqlService : Entity({
            superUnit(shared.SocketService)
            base(true)
        }) {
            val home = prop(n.Path)
            val config = prop(n.Path)
            val defaultUser = prop()
            val defaultPassword = prop()

            val tool = prop(MySqlTool)
        }
    }

    object dev : Module() {

        object Artifact : Entity() {
            val id = prop()

            object commands : CommandController() {
                val artifactCommand = command(id)

                //build
                val build = command { superUnit(artifactCommand) }
                val clean = command { superUnit(artifactCommand) }
                val cleanBuild = composite(clean, build)
                val test = command { superUnit(artifactCommand) }
                val buildTest = composite(build, test)

                //eclipse
                val eclipse = command { superUnit(artifactCommand) }
                val cleanEclipse = command { superUnit(artifactCommand) }
            }
        }


        object BuildRequest : Values() {
            val tasks = prop(n.List.GT(n.String))
            val params = prop(n.Map.GT(n.String, n.String))
            val flags = prop(n.List.GT(n.String))
            val profiles = prop(n.List.GT(n.String))

            val build = op { ret(BuildRequest) }
            val clean = op { ret(BuildRequest) }
            val test = op { ret(BuildRequest) }
            val integTest = op { ret(BuildRequest) }
            val acceptanceTest = op { ret(BuildRequest) }
            val install = op { ret(BuildRequest) }
            val publish = op { ret(BuildRequest) }
            val flag = op {
                p("flag")
                ret(BuildRequest)
            }
            val task = op(p("task")) { ret(BuildRequest) }
            val profile = op(p("profile")) { ret(BuildRequest) }
            val param = op(p("name"), p("value")) { ret(BuildRequest) }
        }

        object BuildTool : Controller({
            superUnit(shared.Tool)
            base(true)
        }) {
            val buildRequest = op { ret(BuildRequest) }
            val supports = op(p("buildHome", n.Path)) { ret(n.Boolean) }
            val build = op(p("buildHome", n.Path), p("request", BuildRequest),
                    p("output", lambda(p("line")))) { ret(Result) }
        }

        object Maven : Controller({
            superUnit(BuildTool)
            base(true)
        }) {
            val plugins = prop(n.List.GT(n.String))
        }

        object Gradle : Controller({
            superUnit(BuildTool)
            base(true)
        })

        object BuildToolFactory : Controller({ base(true) }) {
            val maven = prop(dev.Maven)
            val gradle = prop(dev.Gradle)
            val buildTool = op(p("buildHome", n.Path)) { ret(BuildTool) }
        }
    }

    object task : Module() {
        object BuildTask : Controller({
            superUnit(Task)
            base(true)
        }) {
            val buildTool = prop(dev.BuildTool)
            val buildHome = prop(n.Path)
            val buildRequest = prop(dev.BuildRequest)
        }

        object BuildTaskFactory : Controller({
            superUnit(TaskFactory.GT(BuildTask))
            base(true)
        }) {
            val pathResolver = prop(PathResolver)
            val buildToolFactory = prop(dev.BuildToolFactory)
            val buildRequest = prop(dev.BuildRequest)
        }

        object SystemTaskRegistry : Controller({
            superUnit(TaskRegistry)
            base(true)
        }) {
            val buildToolFactory = prop(dev.BuildToolFactory)
        }
    }
}

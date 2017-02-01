package ee.task

import ee.design.*
import ee.design.*

object Task : Comp({ namespace("ee.task") }) {
    object shared : Module() {

        object TaskGroup : Entity() {
            val taskFactories = prop(n.List.GT(TaskFactory))
            val tasks = prop(n.List.GT(Task))
        }

        object Result : Values({ base(true) }) {
            val action = prop()
            val ok = prop { type(n.Boolean).value(true).open(true) }
            val failure = prop()
            val info = prop()
            val error = prop { type(n.Error).nullable(true) }
            val results = prop { type(n.List.GT(Result)).mutable(false) }
        }

        object Task : Controller() {
            val name = prop()
            val group = prop()
            val execute = op {
                p { type(lambda { p { name("line") } }).name("output") }
                ret(Result)
            }
        }

        object TaskResult : Values() {
            val task = prop(Task)
            val result = prop(Result)
        }

        object TaskFactory : Controller() {
            val T = G(Task)
            val name = prop()
            val group = prop()

            val supports = op {
                p { type(n.List.GT(l.Item)).nullable(false).name("items") }
                ret(n.Boolean)
            }

            val create = op {
                p { type(n.List.GT(l.Item)).nullable(false).name("items") }
                ret(n.List.GT(T))
            }
        }

        object TaskRepository : Controller() {
            val typeFactories = prop(n.List.GT(TaskFactory))

            val register = op {
                val V = G { type(TaskFactory).name("V") }
                p { type(V).nullable(false).initByDefaultTypeValue(false).name("factory") }
            }

            val find = op {
                val T = G { type(l.Item).name("T") }
                p { type(n.List.GT(T)).initByDefaultTypeValue(false).name("items") }
                ret(n.List.GT(TaskFactory))
            }
        }

        object PathResolver : Controller() {
            val home = prop(n.Path)
            val itemToHome = prop(n.Map.GT(n.String, n.String))
            val resolve = op {
                val T = G { type(l.Item).name("T") }
                p { type(T).nullable(false).initByDefaultTypeValue(false).name("item") }
                ret(n.Path)
            }
        }

        object TaskRegistry : Controller() {
            val pathResolver = prop(PathResolver)
            val register = op {
                p { type(TaskRepository).name("repo") }
            }
        }

        object ExecConfig : Values() {
            val home = prop(n.Path)
            val cmd = prop { type(n.List.GT(n.String)).mutable(false) }
            val env = prop { type(n.Map.GT(n.String, n.String)).mutable(false) }
            val filterPattern = prop { defaultValue(".*(\\.{4}|exception|error|fatal|success).*") }
            val failOnError = prop { type(n.Boolean).value(false) }
            val filter = prop { type(n.Boolean).value(false) }
            val noConsole = prop { type(n.Boolean).value(false) }
            val wait = prop { type(n.Boolean).value(true) }
            val timeout = prop { type(n.Long).value(30) }
            val timeoutUnit = prop { type(n.TimeUnit).value(n.TimeUnit.Seconds) }
        }
    }
}

fun model(): StructureUnitI {
    n.initObjectTree()
    return Task.initObjectTree()
}

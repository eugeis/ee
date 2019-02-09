package ee.lang.gen.go

import ee.common.ext.*
import ee.lang.*

object g : StructureUnit({ namespace("").name("Go") }) {
    val error = ExternalType { ifc(true) }

    object fmt : StructureUnit({ namespace("fmt") }) {
        val Sprintf = Operation()
        val Fprintf = Operation()
        val Errorf = Operation()
    }

    object html : StructureUnit({ namespace("html") }) {
        val EscapeString = Operation()
    }

    object errors : StructureUnit({ namespace("errors") }) {
        val New = Operation { ret(error) }
    }

    object time : StructureUnit({ namespace("time") }) {
        val Time = ExternalType()
        val Now = Operation()
    }

    object context : StructureUnit({ namespace("context") }) {
        val Context = ExternalType { ifc(true) }
    }

    object net : StructureUnit({ namespace("net") }) {
        object http : StructureUnit() {
            val ResponseWriter = ExternalType { ifc(true) }
            val Request = ExternalType()

            val MethodGet = Operation()
            val MethodHead = Operation()
            val MethodPost = Operation()
            val MethodPut = Operation()
            val MethodPatch = Operation()
            val MethodDelete = Operation()
            val MethodConnect = Operation()
            val MethodOptions = Operation()
            val MethodTrace = Operation()
        }
    }

    object encoding : StructureUnit({ namespace("encoding") }) {
        object json : StructureUnit() {
            val NewDecoder = Operation()
            val Decoder = ExternalType()
            val Marshal = Operation()
            val Unmarshal = Operation()
        }
    }

    object mux : StructureUnit({ namespace("github.com.gorilla.mux") }) {
        object Router : ExternalType() {}

        object Vars : ExternalType() {}
    }

    object mgo2 : StructureUnit({ namespace("gopkg.in.mgo.v2.bson") }) {
        object bson : StructureUnit() {
            object Raw : ExternalType()
        }
    }

    //common libs
    object gee : StructureUnit({ namespace("github.com.go-ee.utils") }) {
        object enum : StructureUnit() {
            val Literal = ExternalType()
            val EnumBaseJson = ExternalType()
        }

        object net : StructureUnit() {
            val Command = ExternalType()

            val QueryType = ExternalType()
            val QueryTypeCount = ExternalType()
            val QueryTypeExist = ExternalType()
            val QueryTypeFind = ExternalType()
        }

        object eh : StructureUnit() {
            object AggregateInitializer : ExternalType() {
                val RegisterForAllEvents = Operation()
                val RegisterForEvent = Operation()
                val NewAggregateInitializer = Operation()
            }

            object DelegateCommandHandler : ExternalType({ ifc(true) })

            object DelegateEventHandler : ExternalType({ ifc(true) })

            object AggregateStoreEvent : ExternalType({ ifc(true) })

            object AggregateBase : ExternalType()

            object NewAggregateBase : Operation()

            object EventHandlerNotImplemented : Operation()

            object CommandHandlerNotImplemented : Operation()

            object QueryNotImplemented : Operation()

            object EntityAlreadyExists : Operation()

            object EntityNotExists : Operation()

            object IdsDismatch : Operation()

            object ValidateIdsMatch : Operation()

            object ValidateNewId : Operation()

            object HttpCommandHandler : ExternalType() {
                val context = prop { type(g.context.Context) }
                val commandBus = prop { type(g.eh.CommandHandler) }

                val ignore = constructorFull()
            }

            object HttpQueryHandler : ExternalType() {
                val ignore = constructorFull()
            }

            object Projector : ExternalType() {}

            object NewProjector : Operation() {}

        }
    }

    object eh : StructureUnit({ namespace("github.com.looplab.eventhorizon").name("eh") }) {

        object Aggregate : ExternalType() {}

        object AggregateBase : ExternalType() {}

        object NewAggregateBase : Operation() {}

        object RegisterEventData : Operation() {}

        object EventData : ExternalType({ ifc(true) }) {}

        object AggregateType : ExternalType() {}

        object Command : ExternalType({ ifc(true) }) {}

        object CommandHandler : ExternalType({ ifc(true) })

        object CommandBus : ExternalType({
            name("CommandHandler")
            namespace("github.com.looplab.eventhorizon.commandhandler.bus")
        }) {}

        object CommandType : ExternalType() {}

        object AggregateCommandHandler : ExternalType({ ifc(true) }) {
            object SetAggregate : Operation() {
                val aggregateType = p()
                val cmdType = p()
                val ret = retError()
            }
        }

        object Entity : ExternalType({ ifc(true) }) {}

        object EventStore : ExternalType({ ifc(true) }) {
            object Save : Operation() {
                val ctx = p()
            }
        }

        object EventBus : ExternalType({ ifc(true) }) {}

        object EventPublisher : ExternalType({ ifc(true) }) {}

        object EventHandler : ExternalType({ ifc(true) }) {}

        object Event : ExternalType({ ifc(true) }) {}

        object EventType : ExternalType() {}

        object UUID : ExternalType() {}

        object ReadRepo : ExternalType({ ifc(true) }) {

        }

        object WriteRepo : ExternalType({ ifc(true) }) {

        }

        object ReadWriteRepo : ExternalType({ ifc(true) }) {

        }

        object Projector : ExternalType({ namespace("github.com.looplab.eventhorizon.eventhandler.projector") }) {}
    }


    object mapset : StructureUnit({ namespace("github.com.deckarep.golang-set.mapset").name("mapset") }) {
        val NewSet = Operation()

        object Set : Type() {
            val Add = Operation()
        }
    }

}

open class GoContext : GenerationContext {
    val namespaceLastPart: String

    constructor(namespace: String = "", moduleFolder: String = "", genFolder: String = "src/main/go/src",
        genFolderDeletable: Boolean = false, genFolderPatternDeletable: Regex? = ".*Base.go".toRegex(),
        derivedController: DerivedController, macroController: MacroController)
            : super(namespace, moduleFolder, genFolder,
        genFolderDeletable, genFolderPatternDeletable, derivedController, macroController) {
        namespaceLastPart = namespace.substringAfterLast(".")
    }

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${toPackage(indent)}${toImports(indent)}$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return namespaceLastPart.isNotEmpty().then { "${indent}package $namespaceLastPart$nL$nL" }
    }

    private fun toImports(indent: String): String {
        return types.isNotEmpty().then {
            val outsideTypes = types.filter { it.namespace().isNotEmpty() && !it.namespace().equals(namespace, true) }
            outsideTypes.isNotEmpty().then {
                outsideTypes.sortedBy { it.namespace() }.map { "$indent${it.namespace()}" }.toSortedSet()
                    .joinSurroundIfNotEmptyToString(nL, "${indent}import ($nL", "$nL)") {
                        if (it.startsWith("ee.")) {
                            """    "${it.toLowerCase().toDotsAsPath()
                                .replace("ee/", "github.com/go-ee/")
                                .replace("github/com", "github.com")
                                .replace("gopkg/in/mgo/v2", "gopkg.in/mgo.v2")}""""
                        } else {
                            """    "${it.toLowerCase().toDotsAsPath()
                                .replace("github/com", "github.com")
                                .replace("gopkg/in/mgo/v2", "gopkg.in/mgo.v2")}""""
                        }
                    }
            }
        }
    }

    override fun n(item: ItemI<*>, derivedKind: String): String {
        val derived = types.addReturn(derivedController.derive(item, derivedKind))
        if (derived.namespace().isEmpty() || derived.namespace().equals(namespace, true)) {
            return derived.name()
        } else {
            return """${derived.namespace().substringAfterLast(".").toLowerCase()}.${derived.name()}"""
        }
    }
}

fun <T : StructureUnitI<*>> T.prepareForGoGeneration(): T {
    initsForGoGeneration()
    extendForGoGenerationLang()
    return this
}

fun <T : StructureUnitI<*>> T.initsForGoGeneration(): T {
    g.initObjectTree()
    initObjectTrees()
    return this
}

fun <T : StructureUnitI<*>> T.extendForGoGenerationLang(): T {
    //declare as 'isBase' all compilation units with non implemented operations.
    declareAsBaseWithNonImplementedOperation()

    prepareAttributesOfEnums()

    defineSuperUnitsAsAnonymousProps()

    defineConstructorNoProps()
    return this
}

fun OperationI<*>.retTypeAndError(retType: TypeI<*>): OperationI<*> =
    returns(Attribute { type(retType).name("ret") }, Attribute { type(g.error).name("err") })

fun OperationI<*>.retError(): OperationI<*> = returns(Attribute { type(g.error).name("err") })

fun AttributeI<*>.nameForGoMember(): String = storage.getOrPut(this, "nameForGoMember", {
    isReplaceable().notSetOrTrue().ifElse({ name().capitalize() }, { name().decapitalize() })
})

val itemAndTemplateNameAsGoFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name().capitalize()}${name.capitalize()}.go")
}

val templateNameAsGoFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("$name.go")
}

val itemNameAsGoFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name()}.go")
}
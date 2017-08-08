package ee.lang.gen.go

import ee.common.ext.*
import ee.lang.*

object g : StructureUnit({ namespace("").name("Go") }) {
    val error = ExternalType({ ifc(true) })

    object fmt : StructureUnit({ namespace("fmt") }) {
        val Sprintf = Operation()
    }

    object errors : StructureUnit({ namespace("errors") }) {
        val New = Operation({ ret(error) })
    }

    object time : StructureUnit({ namespace("time") }) {
        val Time = ExternalType()
        val Now = Operation()
    }

    object context : StructureUnit({ namespace("context") }) {
        val Context = ExternalType({ ifc(true) })
    }

    object net : StructureUnit({ namespace("net") }) {
        object http : StructureUnit() {
            val ResponseWriter = ExternalType({ ifc(true) })
            val Request = ExternalType()
        }
    }

    object mux : StructureUnit({ namespace("github.com.gorilla.mux") }) {
        object Router : ExternalType() {
        }
    }

    //common libs
    object gee : StructureUnit({ namespace("github.com.eugeis.gee") }) {
        object enum : StructureUnit() {
            val Literal = ExternalType()
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

            object EntityAlreadyExists : Operation()

            object EntityNotExists : Operation()

            object IdsDismatch : Operation()
        }
    }

    object eh : StructureUnit({ namespace("github.com.looplab.eventhorizon").name("eh") }) {

        object Aggregate : ExternalType() {
        }

        object AggregateBase : ExternalType() {
        }

        object NewAggregateBase : Operation() {
        }


        object AggregateType : ExternalType() {
        }

        object Command : ExternalType({ ifc(true) }) {
        }

        object CommandHandler : ExternalType({ ifc(true) }) {
        }

        object CommandType : ExternalType() {
        }

        object AggregateCommandHandler : ExternalType({ ifc(true) }) {
            object SetAggregate : Operation() {
                val aggregateType = p()
                val cmdType = p()
                val ret = ret()
            }
        }

        object EventStore : ExternalType({ ifc(true) }) {
            object Save : Operation() {
                val ctx = p()
            }
        }

        object EventBus : ExternalType({ ifc(true) }) {
        }

        object EventPublisher : ExternalType({ ifc(true) }) {
        }

        object EventHandler : ExternalType({ ifc(true) }) {
        }

        object CommandBus : ExternalType({ ifc(true) }) {
        }

        object Event : ExternalType({ ifc(true) }) {
        }

        object EventType : ExternalType() {
        }

        object UUID : ExternalType() {
        }
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

    constructor(namespace: String = "", moduleFolder: String = "",
                genFolder: String = "src/main/go",
                genFolderDeletable: Boolean = false, genFolderPatternDeletable: Regex? = ".*Base.go".toRegex(),
                derivedController: DerivedController = DerivedController(DerivedStorage<ItemI>()),
                macroController: MacroController = MacroController())
            : super(namespace, moduleFolder, genFolder, genFolderDeletable, genFolderPatternDeletable,
            derivedController, macroController) {
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
                outsideTypes.map { "$indent${it.namespace()}" }.toSortedSet().
                        joinSurroundIfNotEmptyToString(nL, "${indent}import ($nL", "$nL)") {
                            """    "${it.toLowerCase().toDotsAsPath().replace("github/com", "github.com")}""""
                        }
            }
        }
    }

    override fun n(item: ItemI, derivedKind: String): String {
        val derived = types.addReturn(derivedController.derive(item, derivedKind))
        if (derived.namespace().isEmpty() || derived.namespace().equals(namespace, true)) {
            return derived.name()
        } else {
            return """${derived.namespace().substringAfterLast(".").toLowerCase()}.${derived.name()}"""
        }
    }
}

fun <T : StructureUnitI> T.prepareForGoGeneration(): T {
    initsForGoGeneration()
    extendForGoGenerationLang()
    return this
}

fun <T : StructureUnitI> T.initsForGoGeneration(): T {
    g.initObjectTree()
    initObjectTrees()
    return this
}

fun <T : StructureUnitI> T.extendForGoGenerationLang(): T {
    //declare as 'base' all compilation units with non implemented operations.
    declareAsBaseWithNonImplementedOperation()

    prepareAttributesOfEnums()

    defineSuperUnitsAsAnonymousProps()

    defineConstructorEmpty()
    return this
}

fun AttributeI.nameForMember(): String = storage.getOrPut(this, "nameForMember", {
    replaceable().notSetOrTrue().ifElse({ "${name().capitalize()} " }, { name().decapitalize() })
})

fun AttributeI.nameForEnum(): String = storage.getOrPut(this, "nameForEnum", {
    name().decapitalize()
})

val itemAndTemplateNameAsGoFileName: TemplateI<*>.(CompositeI) -> Names = {
    Names("${it.name().capitalize()}${name.capitalize()}.go")
}

val templateNameAsGoFileName: TemplateI<*>.(CompositeI) -> Names = {
    Names("$name.go")
}

val itemNameAsGoFileName: TemplateI<*>.(CompositeI) -> Names = {
    Names("${it.name()}.go")
}
package ee.lang.gen.proto

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.common.LangCommonContextFactory
import java.nio.file.Path

open class ProtoContextBuilder<M>(name: String, macroController: MacroController, builder: M.() -> ProtoContext)
    : ContextBuilder<M>(name, macroController, builder)

open class LangProtoContextFactory(targetAsSingleModule: Boolean = true) : LangCommonContextFactory(targetAsSingleModule) {

    open fun buildForImplOnly(scope: String = "main"): ContextBuilder<StructureUnitI<*>> {
        val derivedController = DerivedController()
        registerForImplOnly(derivedController)
        return contextBuilder(derivedController, scope)
    }

    protected open fun contextBuilder(derived: DerivedController, scope: String): ProtoContextBuilder<StructureUnitI<*>> {
        return ProtoContextBuilder(CONTEXT_PROTO, macroController) {
            val structureUnit = this
            ProtoContext(namespace = structureUnit.namespace().toLowerCase(), moduleFolder = computeModuleFolder(),
                    derivedController = derived, macroController = macroController)
        }
    }

    override fun buildName(item: ItemI<*>, kind: String): String {
        return if (item is ConstructorI) {
            buildNameForConstructor(item, kind)
        } else if (item is OperationI) {
            buildNameForOperation(item, kind)
        } else {
            super.buildName(item, kind)
        }
    }

    override fun buildNameForOperation(item: OperationI<*>, kind: String): String {
        return buildNameCommon(item, kind).capitalize()
    }

    companion object {
        const val CONTEXT_PROTO = "proto"
    }
}

open class ProtoContext(
        namespace: String = "", moduleFolder: String = "", genFolder: String = "",
        genFolderDeletable: Boolean = false, genFolderPatternDeletable: Regex? = ".*_base.proto".toRegex(),
        derivedController: DerivedController, macroController: MacroController)
    : GenerationContext(namespace, moduleFolder, genFolder, genFolderDeletable,
        genFolderPatternDeletable, derivedController, macroController) {

    override fun resolveFolder(base: Path): Path {
        return base.resolve(genFolder)
    }

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${indent}syntax = \"proto3\";${nL}${toImports(indent)}${
        toPackage(indent)}$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return namespace.isNotEmpty().then {
            """${indent}package ${namespace.toUnderscoredUpperCase()};$nL${
            indent}option java_package = ${namespace.quotes()};$nL$nL"""
        }
    }

    private fun toImports(indent: String): String {
        return types.isNotEmpty().then {
            val outsideTypes = types.filter {
                it.namespace().isNotEmpty() && !it.namespace().equals(namespace, true)
            }
            outsideTypes.isNotEmpty().then {
                outsideTypes.sortedBy {
                    it.namespace()
                }.map { "$indent${it.namespace().substringAfterLast(".")}" }.toSortedSet()
                        .joinSurroundIfNotEmptyToString(nL) {
                            """import "${it.toLowerCase()}_base.proto";"""
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

fun AttributeI<*>.nameForProtoMember(): String = storage.getOrPut(this, "nameForProtoMember", {
    name().decapitalize()
})

val itemAndTemplateNameAsProtoFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name().toUnderscoredLowerCase()}_${name.toUnderscoredLowerCase()}.proto")
}

val templateNameAsProtoFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${name.toUnderscoredLowerCase()}.proto")
}

val itemNameAsProtoFileName: TemplateI<*>.(CompositeI<*>) -> Names = {
    Names("${it.name().toUnderscoredLowerCase()}.proto")
}

object proto : StructureUnit({ namespace("").name("Proto") }) {
    val error = ExternalType { ifc(true) }

    object fmt : StructureUnit({ namespace("fmt") }) {
    }

    object errors : StructureUnit({ namespace("errors") }) {
        val New = Operation { ret(error) }
    }

    object io : StructureUnit({ namespace("io") }) {
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
            val Client = ExternalType {}

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
        val PtrTime = Operation()

        object enum : StructureUnit() {
            val Literal = ExternalType()
        }

        object net : StructureUnit() {
            val Command = ExternalType()

            val QueryType = ExternalType()
            val QueryTypeCount = ExternalType()
            val QueryTypeExist = ExternalType()
            val QueryTypeFind = ExternalType()

            val PostById = Operation()
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
                val context = ee.lang.gen.go.g.gee.eh.HttpCommandHandler.prop { type(proto.context.Context) }
                val commandBus = ee.lang.gen.go.g.gee.eh.HttpCommandHandler.prop { type(proto.eh.CommandHandler) }

                val ignore = constructorFull()
            }

            object HttpQueryHandler : ExternalType() {
                val ignore = constructorFull()
            }

            object Projector : ExternalType() {}

            object NewProjector : Operation() {}
        }
    }

    object google : StructureUnit({ namespace("github.com.google").name("google") }) {
        object uuid : StructureUnit() {
            object UUID : ExternalType()
            object New : Operation()
            object NewUUID : Operation()
            object Parse : Operation()
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

    object cli : StructureUnit({ namespace("github.com.urfave.cli").name("cli") }) {
        val Command = ExternalType { ifc(true) }
        val Context = ExternalType()
        val NewApp = Operation()
    }

}
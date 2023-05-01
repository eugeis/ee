package ee.lang.gen.proto

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.common.LangCommonContextFactory
import java.nio.file.Path
import java.util.*

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
            ProtoContext(namespace = structureUnit.namespace().lowercase(Locale.getDefault()), moduleFolder = computeModuleFolder(),
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
        return buildNameCommon(item, kind).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
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

    private val namespaceLastPart: String = namespace.substringAfterLast(".")

    override fun resolveNamespaceFolder(base: Path): Path {
        return base.resolve(genFolder)
    }

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${indent}syntax = \"proto3\";${nL}${toImports(indent)}${
        toPackage(indent)}$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return namespace.isNotEmpty().then {
            """${indent}package ${namespace.toProtoPackage()};$nL${
            indent}option java_package = "$namespace.proto";$nL${
            indent}option go_package = "$namespaceLastPart";$nL$nL"""
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
                        .joinSurroundIfNotEmptyToString("") {
                            """import "${it.lowercase(Locale.getDefault())}_base.proto";$nL"""
                        }
            }
        }
    }

    override fun n(item: ItemI<*>, derivedKind: String): String {
        val derived = types.addReturn(derivedController.derive(item, derivedKind))
        return if (derived.namespace().isEmpty() || derived.namespace().equals(namespace, true)) {
            derived.name()
        } else {
            "${derived.namespace().toProtoPackage()}.${derived.name()}"
        }
    }
}

fun AttributeI<*>.nameForProtoMember(): String = storage.getOrPut(this, "nameForProtoMember", {
    name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
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

    object errors : StructureUnit({ namespace("errors") }) {
        val New = Operation { ret(error) }
    }

    object io : StructureUnit({ namespace("io") })

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
        }
    }

    object encoding : StructureUnit({ namespace("encoding") }) {
        object json : StructureUnit()

        val NewDecoder = Operation()
            val Decoder = ExternalType()
            val Marshal = Operation()
            val Unmarshal = Operation()
    }

    //common libs
    object gee : StructureUnit({ namespace("github.com.go-ee.utils") }) {
        val PtrTime = Operation()

        object enum : StructureUnit() {
            val Literal = ExternalType()
        }

        object net : StructureUnit() {
            val Command = ExternalType()
        }
    }

    object google : StructureUnit({ namespace("github.com.google").name("google") }) {
        object uuid : StructureUnit() {
            object UUID : ExternalType()
            object New : Operation()
        }
    }

    object eh : StructureUnit({ namespace("github.com.looplab.eventhorizon").name("eh") }) {

        object Command : ExternalType({ ifc(true) })

        object Entity : ExternalType({ ifc(true) })
    }
}

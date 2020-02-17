package ee.lang.gen.proto

import ee.common.ext.*
import ee.lang.*

open class ProtoContext(
        namespace: String = "", moduleFolder: String = "", genFolder: String = "src",
        genFolderDeletable: Boolean = false, genFolderPatternDeletable: Regex? = ".*_base.proto".toRegex(),
        derivedController: DerivedController, macroController: MacroController)
    : GenerationContext(namespace, moduleFolder, genFolder, genFolderDeletable,
        genFolderPatternDeletable, derivedController, macroController) {

    val namespaceLastPart: String = namespace.substringAfterLast(".")

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${toPackage(indent)}${toImports(indent)}$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return namespaceLastPart.isNotEmpty().then { "${indent}package $namespaceLastPart$nL$nL" }
    }

    private fun toImports(indent: String): String {
        return types.isNotEmpty().then {
            val outsideTypes = types.filter {
                it.namespace().isNotEmpty() && !it.namespace().equals(namespace, true)
            }
            outsideTypes.isNotEmpty().then {
                outsideTypes.sortedBy {
                    it.namespace()
                }.map { "$indent${it.namespace()}" }.toSortedSet()
                        .joinSurroundIfNotEmptyToString(nL, "${indent}import ($nL", "$nL)") {
                                """    "${it.toLowerCase().toDotsAsPath()}""""
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
    isReplaceable().notSetOrTrue().ifElse({ name().capitalize() }, { name().decapitalize() })
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
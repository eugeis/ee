package ee.lang.gen.go

import ee.common.ext.then
import ee.lang.*

open class GoContext : GenerationContext {

    constructor(namespace: String = "", moduleFolder: String = "",
                genFolder: String = "src-gen/main/go", genFolderDeletable: Boolean = true,
                derivedController: DerivedController = DerivedController(DerivedStorage<ItemI>()))
            : super(namespace, moduleFolder, genFolder, genFolderDeletable, derivedController)

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${toPackage(indent)}${toImports(indent)}$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return namespace.isNotEmpty().then { "${indent}package $namespace$nL$nL" }
    }

    private fun toImports(indent: String): String {
        return types.isNotEmpty().then {
            val outsideTypes = types.filter { it.namespace().isNotEmpty() && it.namespace() != namespace }
            outsideTypes.isNotEmpty().then {
                "${outsideTypes.map { "${indent}import ${it.namespace()}.${it.name()}" }.sorted().
                        joinToString(nL)}$nL$nL"
            }
        }
    }
}

fun <T : StructureUnitI> T.prepareForGoGeneration(searchForTargetComposite: Boolean = true): T {
    initObjectTrees(searchForTargetComposite)

    //declare as 'base' all compilation units with non implemented operations.
    declareAsBaseWithNonImplementedOperation()

    prepareAttributesOfEnums()

    //define constructor with all parameters.
    defineConstructorAllForNonConstructors()
    return this
}
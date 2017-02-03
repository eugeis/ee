package ee.design

import ee.lang.*
import java.nio.file.Path

open class KotlinGenerator {
    val model: StructureUnitI

    constructor(model: StructureUnitI) {
        this.model = model
    }

    fun generate(target: Path) {
        model.extendForKotlinGeneration()
    }

    protected fun StructureUnitI.extendForKotlinGeneration() {
        initObjectTrees()

        //declare as 'base' all compilation units with non implemented operations.
        declareAsBaseWithNonImplementedOperation()

        //define constructor with all parameters.
        defineConstructorAllForNonConstructors()


        //define names for data type controllers
        defineNamesForDataTypeControllers()
    }

    protected fun initObjectTrees() {
        /*
        j.initObjectTree()
        k.initObjectTree()
        Jpa.initObjectTree()
        */
        n.initObjectTree()
    }

    protected fun StructureUnitI.defineNamesForDataTypeControllers() {

    }

    protected fun StructureUnitI.defineConstructorAllForNonConstructors() {
        findDownByType(CompilationUnitI::class.java, stopSteppingDownIfFound = false).filter { it.constructors().isEmpty() }
                .extend { constructorAll() }
    }

    protected fun StructureUnitI.declareAsBaseWithNonImplementedOperation() {
        findDownByType(CompilationUnitI::class.java).filter { it.operations().isNotEmpty() && !it.base() }.forEach { it.base(true) }

        //derive controllers from super units
        findDownByType(ControllerI::class.java).filter { it.parent() is CompilationUnit }.forEach {
            val dataItem = it.parent() as CompilationUnit
            dataItem.propagateItemToSubtypes(it)

            val T = it.G { type(dataItem).name("T") }
            if (it !is QueryControllerI) {
                it.prop { type(T).name("item") }
            }
        }
    }

    protected fun <T : CompilationUnit> T.propagateItemToSubtypes(item: CompilationUnit) {
        superUnitFor().filter { superUnitChild ->
            superUnitChild.items().filterIsInstance<CompilationUnit>().find {
                (it.name() == item.name() || it.superUnit() == superUnitChild)
            } == null
        }.forEach { superUnitChild ->
            val derivedItem = item.deriveSubType<ControllerI> {
                namespace(superUnitChild.namespace())
                G { type(superUnitChild).name("T") }
            }
            superUnitChild.add(derivedItem)
            superUnitChild.propagateItemToSubtypes(derivedItem)
        }
    }
}
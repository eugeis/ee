package ee.design

import ee.design.*
import ee.design.gen.jpa
import ee.design.gen.java.java
import ee.design.gen.kt.kt
import ee.design.gen.kt.initObjectTrees
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

        deriveControllersFromSuperUnits()

        //define constructor with all parameters.
        defineConstructorAllForNonConstructors()


        //define names for data type controllers
        defineNamesForDataTypeControllers()
    }

    protected fun initObjectTrees() {
        n.initObjectTree()
        l.initObjectTrees()
        java.initObjectTree()
        kt.initObjectTree()
        jpa.initObjectTree()
    }

    protected fun StructureUnitI.defineNamesForDataTypeControllers() {

    }

    protected fun StructureUnitI.defineConstructorAllForNonConstructors() {
        findDownByType(CompilationUnitI::class.java, stopSteppingDownIfFound = false).filter { it.constructors().isEmpty() }
                .extend { constructorAll() }
    }

    protected fun StructureUnitI.declareAsBaseWithNonImplementedOperation() {
        findDownByType(CompilationUnitI::class.java).filter { it.operations().isNotEmpty() && !it.base() }.forEach { it.base(true) }
    }

    private fun StructureUnitI.deriveControllersFromSuperUnits() {
        findDownByType(ControllerI::class.java).filter { it.parent() is CompilationUnitI }.forEach {
            val dataItem = it.parent() as CompilationUnitI
            dataItem.propagateItemToSubtypes(it)

            val T = it.G { type(dataItem).name("T") }
            if (it !is QueryController) {
                it.prop { type(T).name("item") }
            }
        }
    }

    protected fun <T : CompilationUnitI> T.propagateItemToSubtypes(item: CompilationUnitI) {
        superUnitFor().filter { superUnitChild ->
            superUnitChild.items().filterIsInstance<CompilationUnitI>().find {
                (it.name() == item.name() || it.superUnit() == superUnitChild)
            } == null
        }.forEach { superUnitChild ->
            val derivedItem = item.deriveSubType<Controller> {
                namespace(superUnitChild.namespace())
                G { type(superUnitChild).name("T") }
            }
            superUnitChild.add(derivedItem)
            superUnitChild.propagateItemToSubtypes(derivedItem)
        }
    }
}
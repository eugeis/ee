package ee.design.gen

import ee.design.ControllerI
import ee.design.ModuleI
import ee.design.QueryControllerI
import ee.lang.CompilationUnitI
import ee.lang.StructureUnitI
import ee.lang.findDownByType
import ee.lang.gen.kt.prepareForKotlinGeneration
import java.nio.file.Path

open class DesignKotlinGenerator {
    val model: StructureUnitI

    constructor(model: StructureUnitI) {
        this.model = model
    }

    fun generate(target: Path) {
        model.extendForKotlinGeneration()
        val generatorFactory = DesignGeneratorFactory()
        val generator = generatorFactory.pojo()
        generator.delete(target, model)
        model.findDownByType(ModuleI::class.java).forEach { module ->
            generator.generate(target, module)
        }
    }

    protected fun StructureUnitI.extendForKotlinGeneration() {
        prepareForKotlinGeneration()

        //define names for data type controllers
        defineNamesForDataTypeControllers()

        declareAsBaseWithNonImplementedOperation()
    }

    protected fun StructureUnitI.defineNamesForDataTypeControllers() {

    }

    protected fun StructureUnitI.declareAsBaseWithNonImplementedOperation() {
        findDownByType(CompilationUnitI::class.java).filter { it.operations().isNotEmpty() && !it.base() }.forEach { it.base(true) }

        //derive controllers from super units
        findDownByType(ControllerI::class.java).filter { it.parent() is CompilationUnitI }.forEach {
            val dataItem = it.parent() as CompilationUnitI
            dataItem.propagateItemToSubtypes(it)

            val T = it.G { type(dataItem).name("T") }
            if (it !is QueryControllerI) {
                it.prop { type(T).name("addItem") }
            }
        }
    }

    protected fun <T : CompilationUnitI> T.propagateItemToSubtypes(item: CompilationUnitI) {
        superUnitFor().filter { superUnitChild ->
            superUnitChild.items().filterIsInstance<CompilationUnitI>().find {
                (it.name() == item.name() || it.superUnit() == superUnitChild)
            } == null
        }.forEach { superUnitChild ->
            val derivedItem = item.deriveSubType<ControllerI> {
                namespace(superUnitChild.namespace())
                G { type(superUnitChild).name("T") }
            }
            superUnitChild.addItem(derivedItem)
            superUnitChild.propagateItemToSubtypes(derivedItem)
        }
    }
}
package ee.design

import ee.lang.*

object d : StructureUnit({ artifact("ee-design").namespace("ee.design").name("Design") }) {

    object Model : CompilationUnit({ superUnit(l.StructureUnit) }) {
        val models = prop(Model).multi(true)
        val comps = prop(Comp).multi(true)
    }

    object Bundle : CompilationUnit({ superUnit(l.StructureUnit) }) {
        val units = prop(l.StructureUnit).multi(true)
    }

    object Module : CompilationUnit({ superUnit(l.StructureUnit) }) {
        val parentNamespace = prop(n.Boolean).value(false)
        val dependencies = prop(Module).multi(true)
        val events = prop(Event).multi(true)
        val commands = prop(Command).multi(true)
        val entities = prop(Entity).multi(true)
        val enums = prop(l.EnumType).multi(true)
        val values = prop(Values).multi(true)
        val basics = prop(Basic).multi(true)
        val controllers = prop(Controller).multi(true)

    }

    object ModuleGroup : CompilationUnit({ superUnit(l.StructureUnit) }) {
        val modules = prop(Module).multi(true)
    }

    object Comp : CompilationUnit({ superUnit(ModuleGroup) }) {
        val moduleGroups = prop(ModuleGroup).multi(true)
    }

    object Event : CompilationUnit({ superUnit(l.CompilationUnit) })

    object Facet : CompilationUnit({ superUnit(ModuleGroup) })

    object ExternalModule : CompilationUnit({ superUnit(Module) }) {
        val externalTypes = prop(l.ExternalType).multi(true)
    }

    object Controller : CompilationUnit({ superUnit(l.CompilationUnit) })

    object QueryController : CompilationUnit({ superUnit(Controller) }) {
        val findBys = prop(FindBy).multi(true).nonFluent("findBy")
        val countBys = prop(CountBy).multi(true).nonFluent("countBy")
        val existBys = prop(ExistBy).multi(true).nonFluent("existBy")
    }

    object Command : CompilationUnit({ superUnit(DataTypeOperation) })

    object DataTypeOperation : CompilationUnit({ superUnit(l.Operation) })

    object CompositeCommand : CompilationUnit({ superUnit(DataTypeOperation) }) {
        val operations = prop(l.Operation).multi(true)
    }

    object CommandController : CompilationUnit({ superUnit(Controller) }) {
        val commands = prop(Command).multi(true).nonFluent("command")
        val composites = prop(CompositeCommand).multi(true).nonFluent("composite")
        val createBys = prop(CreateBy).multi(true).nonFluent("createBy")
        val updateBys = prop(UpdateBy).multi(true).nonFluent("updateBy")
        val deleteBys = prop(DeleteBy).multi(true).nonFluent("deleteBy")
    }

    object FindBy : CompilationUnit({ superUnit(DataTypeOperation) })

    object CountBy : CompilationUnit({ superUnit(DataTypeOperation) })

    object ExistBy : CompilationUnit({ superUnit(DataTypeOperation) })

    object CreateBy : CompilationUnit({ superUnit(Command) })

    object DeleteBy : CompilationUnit({ superUnit(Command) })

    object UpdateBy : CompilationUnit({ superUnit(Command) })

    object Entity : CompilationUnit({ superUnit(l.CompilationUnit) }) {
        val id = prop(l.Attribute)
        val controllers = prop(Controller).multi(true)
        val commands = prop(CommandController).multi(true)
        val queries = prop(QueryController).multi(true)
    }

    object Basic : CompilationUnit({ superUnit(l.CompilationUnit) })

    object Values : CompilationUnit({ superUnit(l.CompilationUnit) })

    object Widget : CompilationUnit({ superUnit(l.CompilationUnit) })
}
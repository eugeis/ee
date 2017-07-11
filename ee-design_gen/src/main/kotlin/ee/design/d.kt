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
    object BussinesEvent : CompilationUnit({ superUnit(Event) })

    object Facet : CompilationUnit({ superUnit(ModuleGroup) })

    object ExternalModule : CompilationUnit({ superUnit(Module) }) {
        val externalTypes = prop(l.ExternalType).multi(true)
    }

    object Controller : CompilationUnit({ superUnit(l.CompilationUnit) }) {
        val enums = prop(l.EnumType).multi(true).doc("Enums used special for controller needs, like CommandTypeEnums")
    }

    object Queries : CompilationUnit({ superUnit(Controller) }) {
        val findBys = prop(FindBy).multi(true).nonFluent("findBy")
        val countBys = prop(CountBy).multi(true).nonFluent("countBy")
        val existBys = prop(ExistBy).multi(true).nonFluent("existBy")
    }

    object Events : CompilationUnit({ superUnit(Controller) }) {
        val events = prop(BussinesEvent).multi(true).nonFluent("event")
        val created = prop(Created).multi(true).nonFluent("created")
        val updated = prop(Updated).multi(true).nonFluent("updated")
        val deleted = prop(Deleted).multi(true).nonFluent("deleted")
    }

    object Command : CompilationUnit({ superUnit(l.DataTypeOperation) })
    object BussinesCommand : CompilationUnit({ superUnit(Command) })

    object CompositeCommand : CompilationUnit({ superUnit(l.DataTypeOperation) }) {
        val operations = prop(l.Operation).multi(true)
    }

    object Commands : CompilationUnit({ superUnit(Controller) }) {
        val commands = prop(BussinesCommand).multi(true).nonFluent("command")
        val composites = prop(CompositeCommand).multi(true).nonFluent("composite")
        val createBys = prop(CreateBy).multi(true).nonFluent("createBy")
        val updateBys = prop(UpdateBy).multi(true).nonFluent("updateBy")
        val deleteBys = prop(DeleteBy).multi(true).nonFluent("deleteBy")
    }

    object FindBy : CompilationUnit({ superUnit(l.DataTypeOperation) })
    object CountBy : CompilationUnit({ superUnit(l.DataTypeOperation) })
    object ExistBy : CompilationUnit({ superUnit(l.DataTypeOperation) })

    object CreateBy : CompilationUnit({ superUnit(Command) })
    object DeleteBy : CompilationUnit({ superUnit(Command) })
    object UpdateBy : CompilationUnit({ superUnit(Command) })

    object Created : CompilationUnit({ superUnit(Event) })
    object Deleted : CompilationUnit({ superUnit(Event) })
    object Updated : CompilationUnit({ superUnit(Event) })

    object Entity : CompilationUnit({ superUnit(l.DataType) }) {
        val belongsToAggregate = prop(Entity)
        val aggregateFor = prop(Entity).multi(true)
        val controllers = prop(Controller).multi(true)
        val commands = prop(Commands).multi(true)
        val queries = prop(Queries).multi(true)
        val events = prop(Events).multi(true)
    }

    object Basic : CompilationUnit({ superUnit(l.DataType) })

    object Values : CompilationUnit({ superUnit(l.DataType) })

    object Widget : CompilationUnit({ superUnit(l.CompilationUnit) })
}
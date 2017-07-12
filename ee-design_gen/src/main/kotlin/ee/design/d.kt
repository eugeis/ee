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
        val entities = prop(Entity).multi(true).nonFluent("entity")
        val enums = prop(l.EnumType).multi(true).nonFluent("enumType")
        val values = prop(Values).multi(true).nonFluent("valueType")
        val basics = prop(Basic).multi(true).nonFluent("basic")
        val controllers = prop(Controller).multi(true).nonFluent("controller")

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
        val enums = prop(l.EnumType).multi(true).nonFluent("enumType").doc(
                "Enums used special for controller needs, like CommandTypeEnums")
        val values = prop(Values).multi(true).nonFluent("valueType").doc("Values used special for controller needs")
        val basics = prop(Basic).multi(true).nonFluent("basic").doc("Baics used special for controller needs")
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

    object Command : CompilationUnit({ superUnit(l.CompilationUnit) })
    object BussinesCommand : CompilationUnit({ superUnit(Command) })

    object CompositeCommand : CompilationUnit({ superUnit(l.CompilationUnit) }) {
        val commands = prop(Command).multi(true)
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
        val controllers = prop(Controller).multi(true).nonFluent("controller")
        val commands = prop(Commands).multi(true).nonFluent("command")
        val queries = prop(Queries).multi(true).nonFluent("query")
        val events = prop(Events).multi(true).nonFluent("event")
    }

    object Basic : CompilationUnit({ superUnit(l.DataType) })

    object Values : CompilationUnit({ superUnit(l.DataType) })

    object Widget : CompilationUnit({ superUnit(l.CompilationUnit) })
}
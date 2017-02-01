package ee.design

import ee.design.CompilationUnit
import ee.design.StructureUnit
import ee.design.l
import ee.design.prop

object c : StructureUnit({ artifact("ee-comp").namespace("ee.design").name("Comp") }) {

    object DesignModule : CompilationUnit({ superUnit(l.Module) }) {
        val events = prop(Event).multi(true)
        val commands = prop(Command).multi(true)
        val entities = prop(Entity).multi(true)
        val enums = prop(l.EnumType).multi(true)
        val values = prop(Values).multi(true)
        val basics = prop(Basic).multi(true)
        val controllers = prop(Controller).multi(true)
    }

    object Controller : CompilationUnit({ superUnit(l.CompilationUnit) })

    object Event : CompilationUnit({ superUnit(l.CompilationUnit) })

    object QueryController : CompilationUnit({ superUnit(Controller) }) {
        val findBys = prop(FindBy).multi(true).nonFluent("findBy")
        val countBys = prop(CountBy).multi(true).nonFluent("countBy")
        val existBys = prop(ExistBy).multi(true).nonFluent("existBy")
    }

    object Command : CompilationUnit({ superUnit(l.Operation) })

    object Query : CompilationUnit({ superUnit(l.Operation) })

    object CompositeCommand : CompilationUnit({ superUnit(Command) }) {
        val operations = prop(l.Operation).multi(true)
    }

    object CommandController : CompilationUnit({ superUnit(Controller) }) {
        val commands = prop(Command).multi(true).nonFluent("command")
        val composites = prop(CompositeCommand).multi(true).nonFluent("composite")
        val createBys = prop(CreateBy).multi(true).nonFluent("createBy")
        val updateBys = prop(UpdateBy).multi(true).nonFluent("updateBy")
        val deleteBys = prop(DeleteBy).multi(true).nonFluent("deleteBy")
    }

    object FindBy : CompilationUnit({ superUnit(Query) })

    object CountBy : CompilationUnit({ superUnit(Query) })

    object ExistBy : CompilationUnit({ superUnit(Query) })

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
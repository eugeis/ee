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
        val processManagers = prop(ProcessManager).multi(true).nonFluent("processManager")
        val projectors = prop(Projector).multi(true).nonFluent("projector")
    }

    object ModuleGroup : CompilationUnit({ superUnit(l.StructureUnit) }) {
        val modules = prop(Module).multi(true)
    }

    object Comp : CompilationUnit({ superUnit(ModuleGroup) }) {
        val moduleGroups = prop(ModuleGroup).multi(true)
    }

    object Event : CompilationUnit({ superUnit(l.CompilationUnit) }) {
    }

    object BusinessEvent : CompilationUnit({ superUnit(Event) })

    object Facet : CompilationUnit({ superUnit(ModuleGroup) })

    object ExternalModule : CompilationUnit({ superUnit(Module) }) {
        val externalTypes = prop(l.ExternalType).multi(true)
    }

    object Controller : CompilationUnit({ superUnit(l.CompilationUnit) }) {
        val enums = prop(l.EnumType).multi(true).nonFluent("enumType").doc(
                "Enums used special for controller needs, like CommandTypeEnums")
        val values = prop(Values).multi(true).nonFluent("valueType").doc(
                "Values used special for controller needs")
        val basics = prop(Basic).multi(true).nonFluent("basic").doc(
                "Baics used special for controller needs")
    }

    object Command : CompilationUnit({ superUnit(Event) }) {
        val affectMulti = prop(n.Boolean).value(false)
        val event = prop(Event).doc("Default target/to be produced event")
    }

    object StateMachine : CompilationUnit({ superUnit(Controller) }) {
        val stateProp = prop(l.Attribute)
        val timeoutProp = prop(l.Attribute)

        val timeout = propL()
        val states = prop(State).multi(true).nonFluent("state")
        val checks = prop(Check).multi(true).nonFluent("check")
    }

    object ProcessManager : CompilationUnit({ superUnit(StateMachine) }) {
    }

    object Projector : CompilationUnit({ superUnit(StateMachine) }) {
    }

    object AggregateHandler : CompilationUnit({ superUnit(StateMachine) }) {
    }

    object State : CompilationUnit({ superUnit(Controller) }) {
        val timeout = propL()
        val entryActions = prop(l.Action).multi(true).nonFluent("entry")
        val exitActions = prop(l.Action).multi(true).nonFluent("exit")
        val executors = prop(Executor).multi(true).nonFluent("execute")
        val handlers = prop(Handler).multi(true).nonFluent("handle")
    }

    object DynamicState : CompilationUnit({ superUnit(State) }) {
        val checks = prop(Check).multi(true).nonFluent("yes")
        val notChecks = prop(Check).multi(true).nonFluent("no")
    }

    object Executor : CompilationUnit({ superUnit(l.LogicUnit) }) {
        val on = prop(Command)
        val checks = prop(Check).multi(true).nonFluent("yes")
        val notChecks = prop(Check).multi(true).nonFluent("no")
        val actions = prop(l.Action).multi(true).nonFluent("action")
        val output = prop(Event).multi(true).nonFluent("produce")
    }

    object Handler : CompilationUnit({ superUnit(l.LogicUnit) }) {
        val on = prop(Event)
        val checks = prop(Check).multi(true).nonFluent("yes")
        val notChecks = prop(Check).multi(true).nonFluent("no")
        val to = prop(State)
        val actions = prop(l.Action).multi(true).nonFluent("action")
        val output = prop(Command).multi(true).nonFluent("produce")
    }

    object Check : CompilationUnit({ superUnit(l.LogicUnit) }) {
        val cachedInContext = propB()
    }

    object BusinessCommand : CompilationUnit({ superUnit(Command) })

    object CompositeCommand : CompilationUnit({ superUnit(l.CompilationUnit) }) {
        val commands = prop(Command).multi(true)
    }

    object FindBy : CompilationUnit({ superUnit(l.DataTypeOperation) }) {
        val multiResult = prop(n.Boolean).value(true)
    }

    object CountBy : CompilationUnit({ superUnit(l.DataTypeOperation) })
    object ExistBy : CompilationUnit({ superUnit(l.DataTypeOperation) })

    object CreateBy : CompilationUnit({ superUnit(Command) })
    object DeleteBy : CompilationUnit({ superUnit(Command) })
    object UpdateBy : CompilationUnit({ superUnit(Command) })

    object Created : CompilationUnit({ superUnit(Event) })
    object Deleted : CompilationUnit({ superUnit(Event) })
    object Updated : CompilationUnit({ superUnit(Event) })

    object Entity : CompilationUnit({ superUnit(l.DataType) }) {
        val defaultEvents = propB().value(true)
        val defaultQueries = propB().value(true)
        val defaultCommands = propB().value(true)

        val belongsToAggregate = prop(Entity)
        val aggregateFor = prop(Entity).multi(true)
        val controllers = prop(Controller).multi(true).nonFluent("controller")

        val findBys = prop(FindBy).multi(true).nonFluent("findBy")
        val countBys = prop(CountBy).multi(true).nonFluent("countBy")
        val existBys = prop(ExistBy).multi(true).nonFluent("existBy")

        val commands = prop(BusinessCommand).multi(true).nonFluent("command")
        val composites = prop(CompositeCommand).multi(true).nonFluent("composite")
        val createBys = prop(CreateBy).multi(true).nonFluent("createBy")
        val updateBys = prop(UpdateBy).multi(true).nonFluent("updateBy")
        val deleteBys = prop(DeleteBy).multi(true).nonFluent("deleteBy")

        val events = prop(BusinessEvent).multi(true).nonFluent("event")
        val created = prop(Created).multi(true).nonFluent("created")
        val updated = prop(Updated).multi(true).nonFluent("updated")
        val deleted = prop(Deleted).multi(true).nonFluent("deleted")

        val handlers = prop(AggregateHandler).multi(true).nonFluent("handler")
        val projectors = prop(Projector).multi(true).nonFluent("projector")
        val processManager = prop(ProcessManager).multi(true).nonFluent("processManager")
    }

    object Basic : CompilationUnit({ superUnit(l.DataType) })
    object Values : CompilationUnit({ superUnit(l.DataType) })
    object Widget : CompilationUnit({ superUnit(l.CompilationUnit) })
}
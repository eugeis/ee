package ee.design.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.design.*
import ee.lang.*
import ee.lang.gen.go.*


fun <T : OperationI<*>> T.toGoStateCommandHandlerSetupBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    val state = findParentMust(ControllerI::class.java).derivedFrom() as StateI<*>
    val commands = state.uniqueCommands()

    val commandsPreparer = """
    o.CommandsPreparer = func(cmd ${c.n(g.eh.Command)}, entity ${entity.toGo(c, api)}) (err error) {
        if entity.DeletedAt != nil {
            err = ${c.n(g.gee.eh.CommandError)}{Err: ${c.n(g.gee.eh.ErrAggregateDeleted)}, Cmd: cmd, Entity: entity}
        }
        return
    }
    """

    return commandsPreparer + commands.joinSurroundIfNotEmptyToString("") { item ->
        val handler = c.n(item, DesignDerivedType.Handler).capitalize()
        """
    o.$handler = func(command ${item.toGo(c, api)}, entity ${
            entity.toGo(
                c,
                api
            )
        }, store ${g.gee.eh.AggregateStoreEvent.toGo(c, api)}) (err error) {${
            if (item is CreateByI<*> && item.event().isNotEMPTY()) {
                """
        ${item.toGoStoreEvent(c, derived, api)}"""
            } else if ((item is UpdateByI<*> || item is DeleteByI<*> || !item.isAffectMulti()) && item.event()
                    .isNotEMPTY()
            ) {
                """
        ${item.toGoStoreEvent(c, derived, api)}"""
            } else {
                """
        err = ${c.n(g.gee.eh.CommandHandlerNotImplemented, api)}(${c.n(item, api)}${DesignDerivedType.Command})"""
            }
        }
        return
    }"""
    }
}

fun <T : OperationI<*>> T.toGoStateCommandHandlerAddCommandPreparerBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    val command = derivedFrom()
    val handlerName = c.n(command, DesignDerivedType.Handler).capitalize()

    return """
    prevHandler := o.$handlerName
	o.$handlerName = func(command *${c.n(command, api)}, entity *${c.n(entity, api)}, store ${
        c.n(
            g.gee.eh.AggregateStoreEvent, api
        )
    }) (err error) {
		if err = preparer(command, entity); err == nil {
			err = prevHandler(command, entity, store)
		}
		return
	}"""
}


fun <T : OperationI<*>> T.toGoStateCommandHandlerAddCommandsPreparerBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API): String {

    val entity = findParentMust(EntityI::class.java)
    val handlerName = "CommandsPreparer"

    return """
    prevHandler := o.$handlerName
	o.$handlerName = func(cmd ${c.n(g.eh.Command, api)}, entity *${c.n(entity, api)}) (err error) {
		if err = preparer(cmd, entity); err == nil {
            if prevHandler != nil {
			    err = prevHandler(cmd, entity)
            }
		}
		return
	}"""
}

fun <T : OperationI<*>> T.toGoStateCommandHandlerExecuteCommandBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API): String {

    val entity = findParentMust(EntityI::class.java)
    val state = findParentMust(ControllerI::class.java).derivedFrom() as StateI<*>
    val commands = state.uniqueCommands()
    val entityParamName = entity.name().decapitalize()

    val notSupported = """err = ${c.n(g.errors.New, api)}(${
        c.n(g.fmt.Sprintf, api)}("Not supported command type '%v' for entity '%v", cmd.CommandType(), entity))"""

    return """
    $entityParamName := entity.(${entity.toGo(c, api)})
    if err = o.CommandsPreparer(cmd, $entityParamName); err != nil {
        return
    }
    ${commands.joinSurroundIfNotEmptyToString(separator = "", prefix = "switch cmd.CommandType() {", postfix =
    """
    default:
        $notSupported
    }""", emptyString = notSupported
    ) { executor ->
        """
    case ${executor.nameAndParentName().capitalize()}Command:
        err = o.${executor.name().capitalize()}${DesignDerivedType.Handler}(cmd.(${executor.toGo(c, api)}), $entityParamName, store)"""
    }
    }"""
}

fun <T : StateI<*>> T.toGoStateHandler(c: GenerationContext, derived: String = "Handler",
    api: String = "Handler"): String {
    return """
        ${toGoImpl(c, derived, api, true)}
"""
}


fun <T : OperationI<*>> T.toGoStateEventHandlerApplyEvent(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API): String {

    val entity = findParentMust(EntityI::class.java)
    val state = findParentMust(ControllerI::class.java).derivedFrom() as StateI<*>
    val events = state.uniqueEvents()

    return """
    ${events.joinSurroundIfNotEmptyToString("", { "switch event.EventType() {" }, {
        """
    default:
		err = ${c.n(g.errors.New, api)}(${c.n(g.fmt.Sprintf,
            api)}("Not supported event type '%v' for entity '%v", event.EventType(), entity))
	}"""
    }) { event->
        """
    case ${event.parentNameAndName().capitalize()}Event:
        err = o.${event.name().capitalize()}${DesignDerivedType.Handler}(event, ${
            event.hasData().then("event.Data().(${event.toGo(c, api)}), ")}entity.(${entity.toGo(c, api)}))"""
    }}"""
}


fun <T : OperationI<*>> T.toGoStateEventHandlerSetupBody(c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val state = findParentMust(ControllerI::class.java).derivedFrom() as StateI<*>

    val events = state.uniqueEvents()

    val id = entity.propId().name().capitalize()
    return events.joinSurroundIfNotEmptyToString("") { event ->
        val handler = c.n(event, DesignDerivedType.Handler).capitalize()
        """
    ${event.toGoRegisterEventData(c, api, derived)}
    //default handler implementation
    o.$handler = func(event ${c.n(g.eh.Event, api)}, ${event.hasData().then(
            "eventData ${event.toGo(c, api)}, ")}entity ${entity.toGo(c, api)}) (err error) {${
            if (event is CreatedI<*>) {
            """
		entity.$id = event.AggregateID()${
                event.toGoApplyEvent(c, derived)}"""
        } else if (event is UpdatedI<*>) {
            event.toGoApplyEvent(c, derived)
        } else if (event is DeletedI<*>) {
            """
        *entity = *${entity.toGoInstance(c, derived, api)}${
                event.toGoApplyEvent(c, derived)}"""
        } else {
            """
        //err = ${c.n(g.gee.eh.EventHandlerNotImplemented, api)}(${c.n(event, api)}${DesignDerivedType.Event})"""
        }}
        return
    }"""
    }
}

fun StateI<*>.uniqueEvents() = handlers().map { it.on() }.toSet().toList().sortedBy { it.name() }
fun StateI<*>.uniqueCommands() = executors().map { it.on() }.toSet().toList().sortedBy { it.name() }

fun EventI<*>.toGoRegisterEventData(c: GenerationContext, api: String, derived: String) =
    if (hasData()) {
        """
    //register event object factory
    ${c.n(g.eh.RegisterEventData, api)}(${c.n(this, api)}Event, func() ${c.n(g.eh.EventData, api)} {
        return &${c.n(this, derived)}{}
    })
    """
    } else {
        ""
    }
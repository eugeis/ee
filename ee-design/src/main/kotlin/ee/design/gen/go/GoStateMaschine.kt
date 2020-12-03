package ee.design.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.design.*
import ee.lang.*
import ee.lang.gen.go.*

fun <T : StateI<*>> T.toGoStateHandler(c: GenerationContext, derived: String = "Handler",
    api: String = "Handler"): String {
    return """
        ${toGoImpl(c, derived, api, true)}
"""
}


fun <T : OperationI<*>> T.toGoStateEventHandlerApplyEvent(c: GenerationContext,
    derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API): String {
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
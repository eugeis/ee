package ee.design.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.design.*
import ee.lang.*
import ee.lang.gen.go.*

fun <T : StateI<*>> T.toGoStateHandler(c: GenerationContext, derived: String = "Handler",
    api: String = "Handler"): String {
    val entity = findParentMust(EntityI::class.java)
    val name = c.n(this, derived)
    return """
        ${toGoImpl(c, derived, api, true)}
"""
}


fun <T : OperationI<*>> T.toGoStateEventHandlerApplyEvent(c: GenerationContext,
    derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val state = findParentMust(ControllerI::class.java).derivedFrom() as StateI<*>
    val events = state.handlers().map { it.on() }
    return """
    ${events.joinSurroundIfNotEmptyToString("", { "switch event.EventType() {" }, {
        """
    default:
		err = ${c.n(g.errors.New, api)}(${c.n(g.fmt.Sprintf,
            api)}("Not supported event type '%v' for entity '%v", event.EventType(), entity))
	}"""
    }) {
        """
    case ${it.parentNameAndName().capitalize()}Event:
        err = o.${it.name().capitalize()}${DesignDerivedType.Handler}(event.Data().(${it.toGo(c,
            api)}), entity.(${entity.toGo(c, api)}))"""
    }}"""
}


fun <T : OperationI<*>> T.toGoStateEventHandlerSetupBody(c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val state = findParentMust(ControllerI::class.java).derivedFrom() as StateI<*>
    val events = state.handlers().map { it.on() }
    val id = entity.id().name().capitalize()
    return events.joinSurroundIfNotEmptyToString("") { item ->
        val handler = c.n(item, DesignDerivedType.Handler).capitalize()
        val aggregateType = c.n(entity, DesignDerivedType.AggregateType).capitalize()
        """

    //register event object factory
    ${c.n(g.eh.RegisterEventData, api)}(${c.n(item, api)}Event, func() ${c.n(g.eh.EventData, api)} {
		return &${c.n(item, derived)}{}
	})

    //default handler implementation
    o.$handler = func(event ${item.toGo(c, api)}, entity ${entity.toGo(c,
            api)}) (err error) {${if (item is CreatedI<*>) {
            """
        if err = ${c.n(g.gee.eh.ValidateNewId,
                api)}(entity.$id, event.$id, $aggregateType); err == nil {${item.toGoApplyEvent(c, derived)}
        }"""
        } else if (item is UpdatedI<*>) {
            """
        if err = ${c.n(g.gee.eh.ValidateIdsMatch,
                api)}(entity.$id, event.$id, $aggregateType); err == nil {${item.toGoApplyEventNoKeys(c, derived)}
        }"""
        } else if (item is DeletedI<*>) {
            """
        if err = ${c.n(g.gee.eh.ValidateIdsMatch, api)}(entity.$id, event.$id, $aggregateType); err == nil {
            *entity = *${entity.toGoInstance(c, derived, api)}
        }"""
        } else {
            """
        //err = ${c.n(g.gee.eh.EventHandlerNotImplemented, api)}(${c.n(item, api)}${DesignDerivedType.Event})"""
        }}
        return
    }"""
    }
}
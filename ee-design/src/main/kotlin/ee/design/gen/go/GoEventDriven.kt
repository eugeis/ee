package ee.design.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toPlural
import ee.design.*
import ee.lang.*
import ee.lang.gen.go.*

fun <T : EntityI> T.toGoCommandTypes(c: GenerationContext): String {
    val commands = findDownByType(CommandI::class.java)
    return commands.joinSurroundIfNotEmptyToString(nL, "${nL}const ($nL", "$nL)") {
        """     ${it.nameAndParentName().capitalize()}${DesignDerivedType.Command} ${c.n(g.eh.CommandType)} = "${it.nameAndParentName().capitalize()}""""
    }
}

fun <T : EntityI> T.toGoEventTypes(c: GenerationContext): String {
    val commands = findDownByType(EventI::class.java)
    return commands.joinSurroundIfNotEmptyToString(nL, "${nL}const ($nL", "$nL)") {
        """     ${it.parentNameAndName().capitalize()}${DesignDerivedType.Event} ${c.n(g.eh.EventType)} = "${it.parentNameAndName().capitalize()}""""
    }
}

fun <T : OperationI> T.toGoCommandHandlerExecuteCommandBody(c: GenerationContext,
                                                            derived: String = DesignDerivedKind.IMPL,
                                                            api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val commands = entity.findDownByType(CommandI::class.java)
    return """
    ${commands.joinSurroundIfNotEmptyToString("", "switch cmd.CommandType() {") {
        """
    case ${it.nameAndParentName().capitalize()}Command:
        ret = o.${it.name().capitalize()}${DesignDerivedType.Handler}(cmd.(${it.toGo(c, api)}), entity.(${entity.toGo(c, api)}), store)"""
    }}
    default:
		ret = ${c.n(g.errors.New, api)}(${c.n(g.fmt.Sprintf, api)}("Not supported command type '%v' for entity '%v", cmd.CommandType(), entity))
	}"""
}

fun <T : OperationI> T.toGoHttpHandlerBody(c: GenerationContext,
                                           derived: String = DesignDerivedKind.IMPL,
                                           api: String = DesignDerivedKind.API): String {
    return """
    ${c.n(g.fmt.Fprintf, api)}(w, "Hello, %q from ${parentNameAndName()}", ${c.n(g.html.EscapeString, api)}(r.URL.Path))"""
}

fun <T : OperationI> T.toGoHttpHandlerIdBasedBody(c: GenerationContext,
                                                  derived: String = DesignDerivedKind.IMPL,
                                                  api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    return """
    vars := ${c.n(g.mux.Vars, api)}(r)
    id := vars["${entity.id().name().decapitalize()}"]
    ${(derivedFrom() is CommandI).then {
        val command = derivedFrom() as CommandI
        """
    decoder := ${c.n(g.encoding.json.NewDecoder, derived)}(r.Body)
    command := &${command.nameAndParentName().capitalize()}{}
    if err := decoder.Decode(command); err != nil {
        w.WriteHeader(http.StatusBadRequest)
        fmt.Fprintf(w, "Can't decode body to command %v because of %v", command, err)
    }
    defer r.Body.Close()

    if err := o.commandBus.HandleCommand(o.context, command); err != nil {
		w.WriteHeader(http.StatusExpectationFailed)
		fmt.Fprintf(w, "Can't execute command %v because of %v", command, err)
		return
	}"""
    }}
    ${c.n(g.fmt.Fprintf, api)}(w, "id=%v, %q from ${parentNameAndName()}", id, ${c.n(g.html.EscapeString, api)}(r.URL.Path))"""
}

fun <T : CommandI> T.toGoStoreEvent(c: GenerationContext,
                                    derived: String = DesignDerivedKind.IMPL,
                                    api: String = DesignDerivedKind.API): String {
    return """store.StoreEvent(${event().parentNameAndName()}${DesignDerivedType.Event}, &${c.n(event(), api)}{${
    props().joinSurroundIfNotEmptyToString("") { prop ->
        """
                    ${prop.name().capitalize()}: command.${prop.name().capitalize()},"""
    }}}, ${g.time.Now.toGoCall(c, derived, api)})"""
}

fun <T : EventI> T.toGoApplyEvent(c: GenerationContext): String {
    return props().joinSurroundIfNotEmptyToString("") { prop ->
        """
                entity.${prop.name().capitalize()} = event.${prop.name().capitalize()}"""
    }
}

fun <T : OperationI> T.toGoCommandHandlerSetupBody(c: GenerationContext,
                                                   derived: String = DesignDerivedKind.IMPL,
                                                   api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val commands = entity.findDownByType(CommandI::class.java)
    return commands.joinSurroundIfNotEmptyToString("") { item ->
        val handler = "${item.name().capitalize()}${DesignDerivedType.Handler}"
        """
    if o.$handler == nil {
        o.$handler = func(command ${item.toGo(c, api)}, entity ${entity.toGo(c, api)}, store ${g.gee.eh.AggregateStoreEvent.toGo(c, api)}) (ret error) {${
        if (item is CreateByI && item.event().isNotEMPTY()) {
            """
            if len(entity.${entity.id().name().capitalize()}) > 0 {
                ret = ${c.n(g.gee.eh.EntityAlreadyExists, api)}(entity.${entity.id().name().capitalize()}, ${c.n(entity, api)}${DesignDerivedType.AggregateType})
            } else {
                ${item.toGoStoreEvent(c, derived, api)}
            }"""
        } else if ((item is UpdateByI || item is DeleteByI) && item.event().isNotEMPTY()) {
            """
            if len(entity.${entity.id().name().capitalize()}) == 0 {
                ret = ${c.n(g.gee.eh.EntityNotExists, api)}(entity.${entity.id().name().capitalize()}, ${c.n(entity, api)}${DesignDerivedType.AggregateType})
            } else if entity.${entity.id().name().capitalize()} != command.${entity.id().name().capitalize()} {
                ret = ${c.n(g.gee.eh.IdsDismatch, api)}(entity.${entity.id().name().capitalize()}, command.${entity.id().name().capitalize()}, ${
            c.n(entity, api)}${DesignDerivedType.AggregateType})
            } else {
                ${item.toGoStoreEvent(c, derived, api)}
            }"""
        } else {
            "ret = ${c.n(g.gee.eh.CommandHandlerNotImplemented, api)}(${c.n(item, api)}${DesignDerivedType.Command})"
        }}
            return
        }
    }
    """
    }
}


fun <T : OperationI> T.toGoEventHandlerApplyEvent(c: GenerationContext,
                                                  derived: String = DesignDerivedKind.IMPL,
                                                  api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val events = entity.findDownByType(EventI::class.java)
    return """
    ${events.joinSurroundIfNotEmptyToString("", "switch event.EventType() {", """
    default:
		ret = ${c.n(g.errors.New, api)}(${c.n(g.fmt.Sprintf, api)}("Not supported event type '%v' for entity '%v", event.EventType(), entity))
	}""") {
        """
    case ${it.parentNameAndName().capitalize()}Event:
        ret = o.${it.name().capitalize()}${DesignDerivedType.Handler}(event.Data().(${it.toGo(c, api)}), entity.(${entity.toGo(c, api)}))"""
    }}"""
}

fun <T : OperationI> T.toGoEventHandlerSetupBody(c: GenerationContext,
                                                 derived: String = DesignDerivedKind.IMPL,
                                                 api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val events = entity.findDownByType(EventI::class.java)
    return events.joinSurroundIfNotEmptyToString("") { item ->
        val handler = "${item.name().capitalize()}${DesignDerivedType.Handler}"
        """
    if o.$handler == nil {
        o.$handler = func(event ${item.toGo(c, api)}, entity ${entity.toGo(c, api)}) (ret error) {${
        if (item is CreatedI) {
            """
            if len(entity.${entity.id().name().capitalize()}) > 0 {
                ret = ${c.n(g.gee.eh.EntityAlreadyExists, api)}(entity.${entity.id().name().capitalize()}, ${
            c.n(entity, api)}${DesignDerivedType.AggregateType})
            } else {${item.toGoApplyEvent(c)}
            }"""
        } else if (item is UpdatedI) {
            """
            if len(entity.${entity.id().name().capitalize()}) > 0 {
                ret = ${c.n(g.gee.eh.EntityNotExists, api)}(entity.${entity.id().name().capitalize()}, ${
            c.n(entity, api)}${DesignDerivedType.AggregateType})
            } else if entity.${entity.id().name().capitalize()} != event.${entity.id().name().capitalize()} {
                ret = ${c.n(g.gee.eh.IdsDismatch, api)}(entity.${entity.id().name().capitalize()}, event.${entity.id().name().capitalize()}, ${
            c.n(entity, api)}${DesignDerivedType.AggregateType})
            } else {${item.toGoApplyEvent(c)}
            }"""
        } else if (item is DeletedI) {
            """
            if len(entity.${entity.id().name().capitalize()}) > 0 {
                ret = ${c.n(g.gee.eh.EntityNotExists, api)}(entity.${entity.id().name().capitalize()}, ${
            c.n(entity, api)}${DesignDerivedType.AggregateType})
            } else if entity.${entity.id().name().capitalize()} != event.${entity.id().name().capitalize()} {
                ret = ${c.n(g.gee.eh.IdsDismatch, api)}(entity.${entity.id().name().capitalize()}, event.${entity.id().name().capitalize()}, ${
            c.n(entity, api)}${DesignDerivedType.AggregateType})
            } else {${item.props().joinSurroundIfNotEmptyToString("") { prop ->
                """
                entity.${prop.name().capitalize()} = ${prop.type().toGoNilOrEmpty(c)}"""
            }}
            }"""
        } else {
            "    ret = ${c.n(g.gee.eh.EventHandlerNotImplemented, api)}(${c.n(item, api)}${DesignDerivedType.Event})"
        }}
            return
        }
    }
    """
    }
}

fun <T : CompilationUnitI> T.toGoAggregateInitializerConst(c: GenerationContext,
                                                           derived: String = DesignDerivedKind.IMPL,
                                                           api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val name = c.n(entity, api)
    return """
const ${entity.name()}${DesignDerivedType.AggregateType} ${c.n(g.eh.AggregateType)} = "$name"
"""
}

fun <T : ConstructorI> T.toGoAggregateInitializerBody(c: GenerationContext,
                                                      derived: String = DesignDerivedKind.IMPL,
                                                      api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val name = c.n(findParentMust(CompilationUnitI::class.java), api)
    return """
    commandHandler := &${entity.name()}${DesignDerivedType.CommandHandler}{}
    eventHandler := &${entity.name()}${DesignDerivedType.EventHandler}{}
	ret = &$name{AggregateInitializer: ${c.n(g.gee.eh.AggregateInitializer.NewAggregateInitializer, api)}(${entity.name()}${DesignDerivedType.AggregateType},
        func(id ${c.n(g.eh.UUID)}) ${c.n(g.eh.Aggregate)} {
            return ${c.n(g.gee.eh.NewAggregateBase)}(${entity.name()}${DesignDerivedType.AggregateType}, id, commandHandler, eventHandler, ${
    entity.primaryOrFirstConstructor().toGoCall(c, derived, api)})
        },
        ${entity.name()}CommandTypes().Literals(), ${entity.name()}EventTypes().Literals(),
        []func() error{commandHandler.SetupCommandHandler, eventHandler.SetupEventHandler},
        eventStore, eventBus, eventPublisher, commandBus),
        ${entity.name()}${DesignDerivedType.CommandHandler}: commandHandler,
        ${entity.name()}${DesignDerivedType.EventHandler}: eventHandler,
    }
"""
}

fun <T : CompilationUnitI> T.toGoAggregateInitializerRegisterForEvents(c: GenerationContext,
                                                                       derived: String = DesignDerivedKind.IMPL,
                                                                       api: String = DesignDerivedKind.API): String {
    val name = c.n(this, api)
    val entity = findParentMust(EntityI::class.java)
    val events = entity.events().findDownByType(EventI::class.java)
    return """
${events.joinSurroundIfNotEmptyToString(nL, nL) {
        """
func (o *$name) RegisterFor${it.name().capitalize()}(handler ${c.n(g.eh.EventHandler)}){
    o.RegisterForEvent(handler, ${entity.name()}EventTypes().${it.parentNameAndName().capitalize()}())
}"""
    }}
"""
}

fun <T : ConstructorI> T.toGoEventhorizonInitializerBody(c: GenerationContext,
                                                         derived: String = DesignDerivedKind.IMPL,
                                                         api: String = DesignDerivedKind.API): String {
    val name = c.n(findParent(CompilationUnitI::class.java), derived)
    val module = findParentMust(ModuleI::class.java)
    val entities = module.entities().filter { it.belongsToAggregate().isEMPTY() }
    return """
	ret = &$name{eventStore: eventStore, eventBus: eventBus, eventPublisher: eventPublisher,
            commandBus: commandBus, ${entities.joinSurroundIfNotEmptyToString(",$nL    ", "$nL    ") {
        """${it.name().capitalize()}${DesignDerivedType.AggregateInitializer}: New${
        it.name().capitalize()}${DesignDerivedType.AggregateInitializer}(eventStore, eventBus, eventPublisher, commandBus)"""
    }}}
"""
}


fun <T : OperationI> T.toGoEventhorizonInitializerSetupBody(c: GenerationContext,
                                                            derived: String = DesignDerivedKind.IMPL,
                                                            api: String = DesignDerivedKind.API): String {
    val module = findParentMust(ModuleI::class.java)
    val entities = module.entities().filter { it.belongsToAggregate().isEMPTY() }
    return """${entities.joinSurroundIfNotEmptyToString("$nL    ", "$nL    ") {
        """
    if ret = o.${it.name().capitalize()}${DesignDerivedType.AggregateInitializer}.Setup(); ret != nil {
        return
    }"""
    }}
"""
}


fun <T : CommandI> T.toGoCommandImpl(c: GenerationContext,
                                     derived: String = DesignDerivedKind.IMPL,
                                     api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val name = c.n(this, derived)
    return """
        ${toGoImpl(c, derived, api)}
func (o *$name) AggregateID() ${c.n(g.eh.UUID)}            { return o.${entity.id().nameForMember()} }
func (o *$name) AggregateType() ${c.n(g.eh.AggregateType)}  { return ${entity.name()}${DesignDerivedType.AggregateType} }
func (o *$name) CommandType() ${c.n(g.eh.CommandType)}      { return ${nameAndParentName().capitalize()}${DesignDerivedType.Command} }
"""
}

fun <T : OperationI> T.toGoAggregateInitializerRegisterCommands(c: GenerationContext,
                                                                derived: String = DesignDerivedKind.IMPL,
                                                                api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    //TODO find a way to get correct name for xxxAggregateType
    return """${c.n(g.gee.eh.AggregateInitializer.RegisterForAllEvents)}(handler, $ { c.n(entity, api) } AggregateType, ${entity.name()} CommandTypes ().Literals())"""
}

fun <T : AttributeI> T.toGoPropOptionalAfterBody(c: GenerationContext,
                                                 derived: String = DesignDerivedKind.IMPL,
                                                 api: String = DesignDerivedKind.API): String {
    return """`eh:"optional"`"""
}

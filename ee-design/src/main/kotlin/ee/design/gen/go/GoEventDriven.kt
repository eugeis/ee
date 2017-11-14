package ee.design.gen.go

import ee.common.ext.ifElse
import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.design.*
import ee.lang.*
import ee.lang.gen.go.*

fun <T : EntityIB<*>> T.toGoCommandTypes(c: GenerationContext): String {
    val commands = findDownByType(CommandIB::class.java)
    return commands.joinSurroundIfNotEmptyToString(nL, "${nL}const ($nL", "$nL)") {
        """     ${it.nameAndParentName().capitalize()}${DesignDerivedType.Command} ${c.n(g.eh.CommandType)} = "${it.nameAndParentName().capitalize()}""""
    }
}

fun <T : EntityIB<*>> T.toGoEventTypes(c: GenerationContext): String {
    val commands = findDownByType(EventIB::class.java)
    return commands.joinSurroundIfNotEmptyToString(nL, "${nL}const ($nL", "$nL)") {
        """     ${it.parentNameAndName().capitalize()}${DesignDerivedType.Event} ${c.n(g.eh.EventType)} = "${it.parentNameAndName().capitalize()}""""
    }
}

fun <T : OperationIB<*>> T.toGoCommandHandlerExecuteCommandBody(c: GenerationContext,
                                                            derived: String = DesignDerivedKind.IMPL,
                                                            api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityIB::class.java)
    val commands = entity.findDownByType(CommandIB::class.java)
    return """
    ${commands.joinSurroundIfNotEmptyToString("", "switch cmd.CommandType() {") {
        """
    case ${it.nameAndParentName().capitalize()}Command:
        err = o.${it.name().capitalize()}${DesignDerivedType.Handler}(cmd.(${it.toGo(c, api)}), entity.(${entity.toGo(c, api)}), store)"""
    }}
    default:
		err = ${c.n(g.errors.New, api)}(${c.n(g.fmt.Sprintf, api)}("Not supported command type '%v' for entity '%v", cmd.CommandType(), entity))
	}"""
}

fun <T : OperationIB<*>> T.toGoCommandHandlerAddPreparerBody(c: GenerationContext,
                                                         derived: String = DesignDerivedKind.IMPL,
                                                         api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityIB::class.java)
    val command = derivedFrom()
    val handlerName = c.n(command, DesignDerivedType.Handler).capitalize()
    return """
    prevHandler := o.$handlerName
	o.$handlerName = func(command *${c.n(command, api)}, entity *${c.n(entity, api)}, store ${c.n(g.gee.eh.AggregateStoreEvent, api)}) (err error) {
		if err = preparer(command, entity); err == nil {
			err = prevHandler(command, entity, store)
		}
		return
	}"""
}

fun <T : OperationIB<*>> T.toGoFindByBody(c: GenerationContext,
                                      derived: String = DesignDerivedKind.IMPL,
                                      api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityIB::class.java)
    return params().isEmpty().ifElse({
        """
    var result []${c.n(g.eh.Entity)}
	if result, err = o.repo.FindAll(o.context); err == nil {
        ret = make(${retFirst().type().toGo(c, derived)}, len(result))
		for i, e := range result {
            ret[i] = e.(${entity.toGo(c, derived)})
		}
    }"""
    }, {
        (params().size == 1 && params().first().key()).ifElse({
            """
    var result ${c.n(g.eh.Entity)}
	if result, err = o.repo.Find(o.context, ${params().first().name()}); err == nil {
        ret = result.(${retFirst().type().toGo(c, derived)})
    }"""
        }, {
            """
    err = ${c.n(g.gee.eh.QueryNotImplemented, api)}("${nameAndParentName()}")"""
        })
    })
}

fun <T : OperationIB<*>> T.toGoExistByBody(c: GenerationContext,
                                       derived: String = DesignDerivedKind.IMPL,
                                       api: String = DesignDerivedKind.API): String {
    return params().isEmpty().ifElse({
        """
    var result int
	if result, err = o.CountAll(); err == nil {
        ret = result > 0
    }"""
    }, {
        (params().size == 1 && params().first().key()).ifElse({
            """
    var result int
	if result, err = o.CountById(${params().first().name()}); err == nil {
        ret = result > 0
    }"""
        }, {
            """
    err = ${c.n(g.gee.eh.QueryNotImplemented, api)}("${nameAndParentName()}")"""
        })
    })
}

fun <T : OperationIB<*>> T.toGoCountByBody(c: GenerationContext,
                                       derived: String = DesignDerivedKind.IMPL,
                                       api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityIB::class.java)
    return params().isEmpty().ifElse({
        """
    var result []${entity.toGo(c, derived)}
	if result, err = o.FindAll(); err == nil {
        ret = len(result)
    }"""
    }, {
        (params().size == 1 && params().first().key()).ifElse({
            """
    var result ${entity.toGo(c, derived)}
	if result, err = o.FindById(${params().first().name()}); err == nil && result != nil {
        ret = 1
    }"""
        }, {
            """
    err = ${c.n(g.gee.eh.QueryNotImplemented, api)}("${nameAndParentName()}")"""
        })
    })
}

fun <T : OperationIB<*>> T.toGoHttpHandlerBody(c: GenerationContext,
                                           derived: String = DesignDerivedKind.IMPL,
                                           api: String = DesignDerivedKind.API): String {
    val queryHandler = derivedFrom() as OperationIB<*>
    return """${queryHandler.params().joinSurroundIfNotEmptyToString("", """
    vars := ${c.n(g.mux.Vars, api)}(r)""") {
        """
    ${it.nameDecapitalize()} := ${it.key().ifElse({ """${c.n(g.eh.UUID, api)}(vars["${it.nameDecapitalize()}"])""" },
                { """vars["${it.nameDecapitalize()}"]""" })}"""
    }}
    ret, err := o.QueryRepository.${queryHandler.toGoCall(c, api, api)}
    o.HandleResult(ret, err, "${parentNameAndName()}", w, r)"""
}

fun <T : OperationIB<*>> T.toGoHttpHandlerCommandBody(c: GenerationContext,
                                                  derived: String = DesignDerivedKind.IMPL,
                                                  api: String = DesignDerivedKind.API): String {
    return (derivedFrom() is CommandIB<*>).then {
        val entity = findParentMust(EntityIB::class.java)
        val command = derivedFrom() as CommandIB<*>
        """
    vars := ${c.n(g.mux.Vars, api)}(r)
    ${entity.id().name().decapitalize()} := ${c.n(g.eh.UUID, api)}(vars["${entity.id().name().decapitalize()}"])
    o.HandleCommand(&${command.nameAndParentName().capitalize()}{${entity.id().name().capitalize()}: ${entity.id().name().decapitalize()}}, w, r)"""
    }
}

fun <T : OperationIB<*>> T.toGoHttpHandlerIdBasedBody(c: GenerationContext,
                                                  derived: String = DesignDerivedKind.IMPL,
                                                  api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityIB::class.java)
    return """
    vars := ${c.n(g.mux.Vars, api)}(r)
    id := vars["${entity.id().name().decapitalize()}"]
    ${c.n(g.fmt.Fprintf, api)}(w, "id=%v, %q from ${parentNameAndName()}", id, ${c.n(g.html.EscapeString, api)}(r.URL.Path))"""
}

fun <T : CommandIB<*>> T.toGoStoreEvent(c: GenerationContext,
                                    derived: String = DesignDerivedKind.IMPL,
                                    api: String = DesignDerivedKind.API): String {
    return """store.StoreEvent(${event().parentNameAndName()}${DesignDerivedType.Event}, &${c.n(event(), api)}{${
    propsNoMetaNoValue().joinSurroundIfNotEmptyToString("") { prop ->
        """
                ${prop.name().capitalize()}: command.${prop.name().capitalize()},"""
    }}}, ${g.time.Now.toGoCall(c, derived, api)})"""
}

fun <T : EventIB<*>> T.toGoApplyEvent(c: GenerationContext, derived: String): String =
        props().joinSurroundIfNotEmptyToString("") { it.toGoApplyEventProp(c, derived) }

fun <T : EventIB<*>> T.toGoApplyEventNoKeys(c: GenerationContext, derived: String): String =
        propsNoMetaNoKey().joinSurroundIfNotEmptyToString("") { it.toGoApplyEventProp(c, derived) }

fun <T : AttributeIB<*>> T.toGoApplyEventProp(c: GenerationContext, derived: String): String =
        """
            entity.${name().capitalize()} = ${
        (value() != null).ifElse({ toGoValue(c, derived) }, { "event.${name().capitalize()}" })}"""


fun <T : OperationIB<*>> T.toGoCommandHandlerSetupBody(c: GenerationContext,
                                                   derived: String = DesignDerivedKind.IMPL,
                                                   api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityIB::class.java)
    val commands = entity.findDownByType(CommandIB::class.java)
    val id = entity.id().name().capitalize()
    return commands.joinSurroundIfNotEmptyToString("") { item ->
        val handler = c.n(item, DesignDerivedType.Handler).capitalize()
        val aggregateType = c.n(entity, DesignDerivedType.AggregateType).capitalize()
        """
    o.$handler = func(command ${item.toGo(c, api)}, entity ${entity.toGo(c, api)}, store ${g.gee.eh.AggregateStoreEvent.toGo(c, api)}) (err error) {${
        if (item is CreateByIB<*> && item.event().isNotEMPTY()) {
            """
        if err = ${c.n(g.gee.eh.ValidateNewId, api)}(entity.$id, command.$id, $aggregateType); err == nil {
            ${item.toGoStoreEvent(c, derived, api)}
        }"""
        } else if ((item is UpdateByIB<*> || item is DeleteByIB<*> || !item.affectMulti()) && item.event().isNotEMPTY()) {
            """
        if err = ${c.n(g.gee.eh.ValidateIdsMatch, api)}(entity.$id, command.$id, $aggregateType); err == nil {
            ${item.toGoStoreEvent(c, derived, api)}
        }"""
        } else {
            """
        err = ${c.n(g.gee.eh.CommandHandlerNotImplemented, api)}(${c.n(item, api)}${DesignDerivedType.Command})"""
        }}
        return
    }"""
    }
}


fun <T : OperationIB<*>> T.toGoEventHandlerApplyEvent(c: GenerationContext,
                                                  derived: String = DesignDerivedKind.IMPL,
                                                  api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityIB::class.java)
    val events = entity.findDownByType(EventIB::class.java)
    return """
    ${events.joinSurroundIfNotEmptyToString("", "switch event.EventType() {", """
    default:
		err = ${c.n(g.errors.New, api)}(${c.n(g.fmt.Sprintf, api)}("Not supported event type '%v' for entity '%v", event.EventType(), entity))
	}""") {
        """
    case ${it.parentNameAndName().capitalize()}Event:
        err = o.${it.name().capitalize()}${DesignDerivedType.Handler}(event.Data().(${it.toGo(c, api)}), entity.(${entity.toGo(c, api)}))"""
    }}"""
}

fun <T : OperationIB<*>> T.toGoEventHandlerSetupBody(c: GenerationContext,
                                                 derived: String = DesignDerivedKind.IMPL,
                                                 api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityIB::class.java)
    val events = entity.findDownByType(EventIB::class.java)
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
    o.$handler = func(event ${item.toGo(c, api)}, entity ${entity.toGo(c, api)}) (err error) {${
        if (item is CreatedIB<*>) {
            """
        if err = ${c.n(g.gee.eh.ValidateNewId, api)}(entity.$id, event.$id, $aggregateType); err == nil {${
            item.toGoApplyEvent(c, derived)}
        }"""
        } else if (item is UpdatedIB<*>) {
            """
        if err = ${c.n(g.gee.eh.ValidateIdsMatch, api)}(entity.$id, event.$id, $aggregateType); err == nil {${
            item.toGoApplyEventNoKeys(c, derived)}
        }"""
        } else if (item is DeletedIB<*>) {
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

fun <T : CompilationUnitIB<*>> T.toGoAggregateInitializerConst(c: GenerationContext,
                                                           derived: String = DesignDerivedKind.IMPL,
                                                           api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityIB::class.java)
    val name = c.n(entity, api)
    return """
const ${entity.name()}${DesignDerivedType.AggregateType} ${c.n(g.eh.AggregateType)} = "$name"
"""
}

fun <T : ConstructorIB<*>> T.toGoAggregateInitializerBody(c: GenerationContext,
                                                      derived: String = DesignDerivedKind.IMPL,
                                                      api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityIB::class.java)
    val name = c.n(findParentMust(CompilationUnitIB::class.java), api)
    return """
    commandHandler := &${entity.name()}${DesignDerivedType.CommandHandler}{}
    eventHandler := &${entity.name()}${DesignDerivedType.EventHandler}{}
    entityFactory := func() ${c.n(g.eh.Entity)} { return ${entity.toGoInstance(c, derived, api)} }
    ret = &$name{AggregateInitializer: ${c.n(g.gee.eh.AggregateInitializer.NewAggregateInitializer, api)}(${entity.name()}${DesignDerivedType.AggregateType},
        func(id ${c.n(g.eh.UUID)}) ${c.n(g.eh.Aggregate)} {
            return ${c.n(g.gee.eh.NewAggregateBase)}(${entity.name()}${DesignDerivedType.AggregateType}, id, commandHandler, eventHandler, entityFactory())
        }, entityFactory,
        ${entity.name()}CommandTypes().Literals(), ${entity.name()}EventTypes().Literals(), eventHandler,
        []func() error{commandHandler.SetupCommandHandler, eventHandler.SetupEventHandler},
        eventStore, eventBus, eventPublisher, commandBus, readRepos), ${
    entity.name()}${DesignDerivedType.CommandHandler}: commandHandler, ${
    entity.name()}${DesignDerivedType.EventHandler}: eventHandler, ProjectorHandler: eventHandler,
    }
"""
}

fun <T : CompilationUnitIB<*>> T.toGoAggregateInitializerRegisterForEvents(c: GenerationContext,
                                                                       derived: String = DesignDerivedKind.IMPL,
                                                                       api: String = DesignDerivedKind.API): String {
    val name = c.n(this, api)
    val entity = findParentMust(EntityIB::class.java)
    val events = entity.events().findDownByType(EventIB::class.java)
    return """
${events.joinSurroundIfNotEmptyToString(nL, nL) {
        """
func (o *$name) RegisterFor${it.name().capitalize()}(handler ${c.n(g.eh.EventHandler)}){
    o.RegisterForEvent(handler, ${entity.name()}EventTypes().${it.parentNameAndName().capitalize()}())
}"""
    }}
"""
}

fun <T : ConstructorIB<*>> T.toGoEventhorizonInitializerBody(c: GenerationContext,
                                                         derived: String = DesignDerivedKind.IMPL,
                                                         api: String = DesignDerivedKind.API): String {
    val name = c.n(findParent(CompilationUnitIB::class.java), derived)
    val module = findParentMust(ModuleIB::class.java)
    val entities = module.entities().filter { it.belongsToAggregate().isEMPTY() }
    return """
	ret = &$name{eventStore: eventStore, eventBus: eventBus, eventPublisher: eventPublisher,
            commandBus: commandBus, ${entities.joinSurroundIfNotEmptyToString(",$nL    ", "$nL    ") {
        """${it.name().capitalize()}${DesignDerivedType.AggregateInitializer}: New${
        it.name().capitalize()}${DesignDerivedType.AggregateInitializer}(eventStore, eventBus, eventPublisher, commandBus)"""
    }}}
"""
}


fun <T : OperationIB<*>> T.toGoEventhorizonInitializerSetupBody(c: GenerationContext,
                                                            derived: String = DesignDerivedKind.IMPL,
                                                            api: String = DesignDerivedKind.API): String {
    val module = findParentMust(ModuleIB::class.java)
    val entities = module.entities().filter { it.belongsToAggregate().isEMPTY() }
    return """${entities.joinSurroundIfNotEmptyToString("$nL    ", "$nL    ") {
        """
    if err = o.${it.name().capitalize()}${DesignDerivedType.AggregateInitializer}.Setup(); err != nil {
        return
    }"""
    }}
"""
}


fun <T : CommandIB<*>> T.toGoCommandImpl(c: GenerationContext,
                                     derived: String = DesignDerivedKind.IMPL,
                                     api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityIB::class.java)
    val name = c.n(this, derived)
    return """
        ${toGoImpl(c, derived, api, true)}
func (o *$name) AggregateID() ${c.n(g.eh.UUID)}            { return o.${entity.id().nameForGoMember()} }
func (o *$name) AggregateType() ${c.n(g.eh.AggregateType)}  { return ${entity.name()}${DesignDerivedType.AggregateType} }
func (o *$name) CommandType() ${c.n(g.eh.CommandType)}      { return ${nameAndParentName().capitalize()}${DesignDerivedType.Command} }
"""
}

fun <T : OperationIB<*>> T.toGoAggregateInitializerRegisterCommands(c: GenerationContext,
                                                                derived: String = DesignDerivedKind.IMPL,
                                                                api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityIB::class.java)
    //TODO find a way to get correct name for xxxAggregateType
    return """${c.n(g.gee.eh.AggregateInitializer.RegisterForAllEvents)}(handler, $ { c.n(entity, api) } AggregateType, ${entity.name()} CommandTypes ().Literals())"""
}

fun <T : AttributeIB<*>> T.toGoPropOptionalAfterBody(c: GenerationContext,
                                                 derived: String = DesignDerivedKind.IMPL,
                                                 api: String = DesignDerivedKind.API): String =
        """`eh:"optional"`"""


fun <T : EntityIB<*>> T.toGoEntityImpl(c: GenerationContext,
                                     derived: String = DesignDerivedKind.IMPL,
                                     api: String = DesignDerivedKind.API): String {
    val name = c.n(this, derived)
    return """
        ${toGoImpl(c, derived, api, true)}
func (o *$name) EntityID() ${c.n(g.eh.UUID)} { return o.${id().nameForGoMember()} }
"""
}
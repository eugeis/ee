package ee.design.gen.go

import ee.common.ext.ifElse
import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.design.*
import ee.lang.*
import ee.lang.gen.go.*

fun <T : EntityI<*>> T.toGoCommandTypes(c: GenerationContext): String {
    val items = findDownByType(CommandI::class.java)
    return items.joinSurroundIfNotEmptyToString(nL, "${nL}const ($nL", "$nL)") {
        """     ${it.nameAndParentName().capitalize()}${DesignDerivedType.Command} ${
            c.n(
                g.eh.CommandType
            )
        } = "${it.nameAndParentName().capitalize()}""""
    }
}

fun <T : EntityI<*>> T.toGoEventTypes(c: GenerationContext): String {
    val items = findDownByType(EventI::class.java)
    return items.joinSurroundIfNotEmptyToString(nL, "${nL}const ($nL", "$nL)") {
        """     ${it.parentNameAndName().capitalize()}${DesignDerivedType.Event} ${
            c.n(
                g.eh.EventType
            )
        } = "${it.parentNameAndName().capitalize()}""""
    }
}

fun <T : OperationI<*>> T.toGoCommandHandlerExecuteCommandBody(
    c: GenerationContext,
    derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    val items = entity.findDownByType(CommandI::class.java)
    return """
    ${
        items.joinSurroundIfNotEmptyToString("", "switch cmd.CommandType() {") {
            """
    case ${it.nameAndParentName().capitalize()}Command:
        err = o.${it.name().capitalize()}${DesignDerivedType.Handler}(cmd.(${it.toGo(c, api)}), entity.(${
                entity.toGo(
                    c,
                    api
                )
            }), store)"""
        }
    }
    default:
		err = ${c.n(g.errors.New, api)}(${
        c.n(
            g.fmt.Sprintf,
            api
        )
    }("Not supported command type '%v' for entity '%v", cmd.CommandType(), entity))
	}"""
}

fun <T : OperationI<*>> T.toGoCommandHandlerAddPreparerBody(
    c: GenerationContext,
    derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
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

fun <T : OperationI<*>> T.toGoFindByBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    return params().isEmpty().ifElse({
        """
    var result []${c.n(g.eh.Entity)}
	if result, err = o.repo.FindAll(o.ctx); err == nil {
        ret = make(${retFirst().type().toGo(c, derived)}, len(result))
		for i, e := range result {
            ret[i] = e.(${entity.toGo(c, derived)})
		}
    }"""
    }, {
        (params().size == 1 && params().first().isKey()).ifElse({
            """
    var result ${c.n(g.eh.Entity)}
	if result, err = o.repo.Find(o.ctx, ${params().first().name()}); err == nil {
        ret = result.(${retFirst().type().toGo(c, derived)})
    }"""
        }, {
            """
    err = ${c.n(g.gee.eh.QueryNotImplemented, api)}("${nameAndParentName()}")"""
        })
    })
}

fun <T : OperationI<*>> T.toGoExistByBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    return params().isEmpty().ifElse({
        """
    var result int
	if result, err = o.CountAll(); err == nil {
        ret = result > 0
    }"""
    }, {
        (params().size == 1 && params().first().isKey()).ifElse({
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

fun <T : OperationI<*>> T.toGoCountByBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    return params().isEmpty().ifElse({
        """
    var result []${entity.toGo(c, derived)}
	if result, err = o.FindAll(); err == nil {
        ret = len(result)
    }"""
    }, {
        (params().size == 1 && params().first().isKey()).ifElse({
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

fun <T : OperationI<*>> T.toGoHttpHandlerBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val queryHandler = derivedFrom() as OperationI<*>
    return """${
        queryHandler.params().joinSurroundIfNotEmptyToString(
            "", """
    vars := ${c.n(g.mux.Vars, api)}(r)"""
        ) {
            """
    ${it.nameDecapitalize()}, _ := ${
                it.isKey().ifElse({
                    """${
                        c.n(
                            g.google.uuid.Parse, api
                        )
                    }(vars["${it.nameDecapitalize()}"])"""
                },
                    { """vars["${it.nameDecapitalize()}"]""" })
            }"""
        }
    }
    ret, err := o.QueryRepository.${queryHandler.toGoCall(c, api, api)}
    o.HandleResult(ret, err, "${parentNameAndName()}", w, r)"""
}

fun <T : OperationI<*>> T.toGoHttpHandlerCommandBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    return (derivedFrom() is CommandI<*>).then {
        val entity = findParentMust(EntityI::class.java)
        val command = derivedFrom() as CommandI<*>
        """
    vars := ${c.n(g.mux.Vars, api)}(r)
    ${entity.propId().name().decapitalize()}, _ := ${c.n(g.google.uuid.Parse, api)}(vars["${
            entity.propId().name().decapitalize()
        }"])
    o.HandleCommand(&${command.nameAndParentName().capitalize()}{${
            entity.propId().name().capitalize()
        }: ${entity.propId().name().decapitalize()}}, w, r)"""
    }
}

fun <T : OperationI<*>> T.toGoHttpHandlerIdBasedBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    return """
    vars := ${c.n(g.mux.Vars, api)}(r)
    id := vars["${entity.propId().name().decapitalize()}"]
    ${c.n(g.fmt.Fprintf, api)}(w, "id=%v, %q from ${parentNameAndName()}", id, ${
        c.n(
            g.html.EscapeString,
            api
        )
    }(r.URL.Path))"""
}

fun <T : CommandI<*>> T.toGoStoreEvent(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {

    return """store.AppendEvent(${event().parentNameAndName()}${DesignDerivedType.Event}, ${
        event().hasData().ifElse(
            """&${c.n(event(), api)}{${
                propsNoMetaNoValueNoId().joinSurroundIfNotEmptyToString(", ") { prop ->
                    """
                ${prop.name().capitalize()}: command.${prop.name().capitalize()}"""
                }
            }}""", "nil"
        )
    }, ${g.time.Now.toGoCall(c, derived, api)})"""
}

fun <T : EventI<*>> T.toGoApplyEvent(c: GenerationContext, derived: String): String =
    props().joinSurroundIfNotEmptyToString("") { it.toGoApplyEventProp(c, derived) }

fun <T : AttributeI<*>> T.toGoApplyEventProp(c: GenerationContext, derived: String): String = """
		entity.${name().capitalize()} = ${
    (value() != null).ifElse({ toGoValue(c, derived) },
        { "eventData.${name().capitalize()}" })
}"""


fun <T : OperationI<*>> T.toGoCommandHandlerSetupBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    val commands = entity.findDownByType(CommandI::class.java)
    val id = entity.propId().name().capitalize()
    return commands.joinSurroundIfNotEmptyToString("") { item ->
        val handler = c.n(item, DesignDerivedType.Handler).capitalize()
        val aggregateType = c.n(entity, DesignDerivedType.AggregateType).capitalize()
        """
    o.$handler = func(command ${item.toGo(c, api)}, entity ${
            entity.toGo(
                c,
                api
            )
        }, store ${
            g.gee.eh.AggregateStoreEvent.toGo(
                c,
                api
            )
        }) (err error) {${
            if (item is CreateByI<*> && item.event().isNotEMPTY()) {
                """
        if err = ${c.n(g.gee.eh.ValidateNewId, api)}(entity.$id, command.$id, $aggregateType); err == nil {
            ${item.toGoStoreEvent(c, derived, api)}
        }"""
            } else if ((item is UpdateByI<*> || item is DeleteByI<*> || !item.isAffectMulti()) && item.event()
                    .isNotEMPTY()
            ) {
                """
        if err = ${c.n(g.gee.eh.ValidateIdsMatch, api)}(entity.$id, command.$id, $aggregateType); err == nil {
            ${item.toGoStoreEvent(c, derived, api)}
        }"""
            } else {
                """
        err = ${c.n(g.gee.eh.CommandHandlerNotImplemented, api)}(${c.n(item, api)}${DesignDerivedType.Command})"""
            }
        }
        return
    }"""
    }
}

fun <T : OperationI<*>> T.toGoEventHandlerApplyEvent(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    val events = entity.findDownByType(EventI::class.java)
    return """
    ${
        events.joinSurroundIfNotEmptyToString(
            "", "switch event.EventType() {", """
    default:
		err = ${c.n(g.errors.New, api)}(${
                c.n(g.fmt.Sprintf, api)
            }("Not supported event type '%v' for entity '%v", event.EventType(), entity))
	}"""
        ) { event ->
            """
    case ${event.parentNameAndName().capitalize()}Event:
        err = o.${event.name().capitalize()}${DesignDerivedType.Handler}(event, ${
                event.hasData().then("event.Data().(${event.toGo(c, api)}), ")
            }entity.(${entity.toGo(c, api)}))"""
        }
    }"""
}

fun <T : OperationI<*>> T.toGoEventHandlerSetupBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    val events = entity.findDownByType(EventI::class.java)
    val id = entity.propId().name().capitalize()
    return events.joinSurroundIfNotEmptyToString("") { event ->
        val handler = c.n(event, DesignDerivedType.Handler).capitalize()
        """${event.toGoRegisterEventData(c, api, derived)}
    //default handler implementation
    o.$handler = func(event ${c.n(g.eh.Event, api)}, ${
            event.hasData().then(
                "eventData ${event.toGo(c, api)}, "
            )
        }entity ${entity.toGo(c, api)}) (err error) {${
            if (event is CreatedI<*>) {
                """
		entity.$id = event.AggregateID()${
                    event.toGoApplyEvent(c, derived)
                }"""
            } else if (event is UpdatedI<*>) {
                event.toGoApplyEvent(c, derived)
            } else if (event is DeletedI<*>) {
                """
		entity.DeletedAt = ${c.n(g.gee.PtrTime, api)}(${g.time.Now.toGoCall(c, derived, api)})${
                    event.toGoApplyEvent(c, derived)
                }"""
            } else {
                """
        err = ${c.n(g.gee.eh.EventHandlerNotImplemented, api)}(${c.n(event, api)}${DesignDerivedType.Event})"""
            }
        }
        return
    }"""
    }
}

fun <T : CompilationUnitI<*>> T.toGoAggregateInitializerConst(
    c: GenerationContext,
    derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    val name = c.n(entity, api)
    return """
const ${entity.name()}${DesignDerivedType.AggregateType} ${c.n(g.eh.AggregateType)} = "$name"
"""
}

fun <T : ConstructorI<*>> T.toGoAggregateInitializerBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    val name = c.n(findParentMust(CompilationUnitI::class.java), api)
    return """
    commandHandler := &${entity.name()}${DesignDerivedType.CommandHandler}{}
    eventHandler := &${entity.name()}${DesignDerivedType.EventHandler}{}
    entityFactory := func() ${c.n(g.eh.Entity)} { return ${entity.toGoInstance(c, derived, api)} }
    ret = &$name{AggregateInitializer: ${
        c.n(
            g.gee.eh.AggregateInitializer.NewAggregateInitializer,
            api
        )
    }(${entity.name()}${DesignDerivedType.AggregateType},
        func(id ${c.n(g.google.uuid.UUID)}) ${c.n(g.eh.Aggregate)} {
            return ${
        c.n(
            g.gee.eh.NewAggregateBase
        )
    }(${entity.name()}${
        DesignDerivedType.AggregateType
    }, id, commandHandler, eventHandler, entityFactory())
        }, entityFactory,
        ${entity.name()}CommandTypes().Literals(), ${entity.name()}EventTypes().Literals(), eventHandler,
        []func() error{commandHandler.SetupCommandHandler, eventHandler.SetupEventHandler},
        eventStore, eventBus, commandBus, readRepos), ${entity.name()}${
        DesignDerivedType.CommandHandler
    }: commandHandler, ${entity.name()}${
        DesignDerivedType.EventHandler
    }: eventHandler, ProjectorHandler: eventHandler,
    }
"""
}

fun <T : CompilationUnitI<*>> T.toGoAggregateInitializerRegisterForEvents(
    c: GenerationContext,
    derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
    val name = c.n(this, api)
    val entity = findParentMust(EntityI::class.java)
    val events = entity.events().findDownByType(EventI::class.java)
    return """
${
        events.joinSurroundIfNotEmptyToString(nL, nL) {
            """
func (o *$name) RegisterFor${it.name().capitalize()}(handler ${c.n(g.eh.EventHandler)}) error {
    return o.RegisterForEvent(handler, ${entity.name()}EventTypes().${it.parentNameAndName().capitalize()}())
}"""
        }
    }
"""
}

fun <T : ConstructorI<*>> T.toGoEhInitializerBody(
    c: GenerationContext,
    derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
    val name = c.n(findParent(CompilationUnitI::class.java), derived)
    val module = findParentMust(ModuleI::class.java)
    val entities = module.entities().filter { it.belongsToAggregate().isEMPTY() }
    return """
	ret = &$name{eventStore: eventStore, eventBus: eventBus,
            commandBus: commandBus, ${
        entities.joinSurroundIfNotEmptyToString(",$nL    ", "$nL    ") {
            """${it.name().capitalize()}${DesignDerivedType.AggregateInitializer}: New${
                it.name().capitalize()
            }${DesignDerivedType.AggregateInitializer}(eventStore, eventBus, commandBus)"""
        }
    }}
"""
}


fun <T : OperationI<*>> T.toGoEhInitializerSetupBody(
    c: GenerationContext,
    derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
    val module = findParentMust(ModuleI::class.java)
    val entities = module.entities().filter { it.belongsToAggregate().isEMPTY() }
    return """${
        entities.joinSurroundIfNotEmptyToString("$nL    ", "$nL    ") {
            """
    if err = o.${it.name().capitalize()}${DesignDerivedType.AggregateInitializer}.Setup(); err != nil {
        return
    }"""
        }
    }
"""
}


fun <T : CommandI<*>> T.toGoCommandImpl(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    val name = c.n(this, derived)
    return """
        ${toGoImpl(c, derived, api, true)}
func (o *$name) AggregateID() ${c.n(g.google.uuid.UUID)}            { return o.${entity.propId().nameForGoMember()} }
func (o *$name) AggregateType() ${
        c.n(
            g.eh.AggregateType
        )
    }  { return ${entity.name()}${DesignDerivedType.AggregateType} }
func (o *$name) CommandType() ${
        c.n(
            g.eh.CommandType
        )
    }      { return ${nameAndParentName().capitalize()}${DesignDerivedType.Command} }
"""
}

fun <T : OperationI<*>> T.toGoAggregateInitializerRegisterCommands(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    //TODO find a way to get correct name for xxxAggregateType
    return """${
        c.n(
            g.gee.eh.AggregateInitializer.RegisterForAllEvents
        )
    }(handler, $ { c.n(entity, api) } AggregateType, ${
        entity.name()
    } CommandTypes ().Literals())"""
}

fun <T : AttributeI<*>> T.toGoPropOptionalAfterBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String = """`eh:"optional"`"""


fun <T : EntityI<*>> T.toGoEntityImpl(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val name = c.n(this, derived)
    return """
        ${toGoImpl(c, derived, api, true)}
func (o *$name) EntityID() ${c.n(g.google.uuid.UUID)} { return o.${propId().nameForGoMember()} }
func (o *$name) Deleted() *${c.n(g.time.Time)} { return o.${propDeletedAt().nameForGoMember()} }
"""
}
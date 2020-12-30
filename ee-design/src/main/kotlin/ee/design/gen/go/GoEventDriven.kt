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
            c.n(g.eh.CommandType)
        } = "${it.nameAndParentName().capitalize()}""""
    }
}

fun <T : EntityI<*>> T.toGoEventTypes(c: GenerationContext): String {
    val items = findDownByType(EventI::class.java)
    return items.joinSurroundIfNotEmptyToString(nL, "${nL}const ($nL", "$nL)") {
        """     ${it.parentNameAndName().capitalize()}${DesignDerivedType.Event} ${
            c.n(g.eh.EventType)
        } = "${it.parentNameAndName().capitalize()}""""
    }
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
                        c.n(g.google.uuid.Parse, api)
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
        val command = derivedFrom() as CommandI<*>
        val varProps = command.propVars()
        """
    vars := ${c.n(g.mux.Vars, api)}(r)${
            varProps.joinSurroundIfNotEmptyToString("") { propKey ->
                val propName = propKey.decapitalize()
                """
    $propName, _ := ${c.n(g.google.uuid.Parse, api)}(vars["$propName"])"""
            }
        }
    command := &${command.nameAndParentName().capitalize()}{${
            varProps.joinSurroundIfNotEmptyToString(", ") { propKey ->
                """${propKey.capitalize()}: ${propKey.decapitalize()}"""
            }
        }}
    o.HandleCommand(command, w, r)"""
    }
}

private fun CommandI<*>.propVars(): List<String> {
    val ret = mutableListOf(propIdName())
    if (this is UpdateChildByI<*>) {
        ret.add(type().propIdNameParent())
    } else if (this is RemoveChildByI<*>) {
        ret.add(type().propIdNameParent())
    }
    return ret
}

fun <T : OperationI<*>> T.toGoHttpHandlerIdBasedBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    return """
    vars := ${c.n(g.mux.Vars, api)}(r)
    id := vars["${entity.propIdOrAdd().name().decapitalize()}"]
    ${c.n(g.fmt.Fprintf, api)}(w, "id=%v, %q from ${parentNameAndName()}", id, ${
        c.n(
            g.html.EscapeString,
            api
        )
    }(r.URL.Path))"""
}

fun <T : CommandI<*>> T.toGoStoreEvent(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    return """store.AppendEvent(${event().parentNameAndName()}${DesignDerivedType.Event}, ${
        event().hasData().ifElse(
            """&${c.n(event(), api)}{${
                propsNoMetaNoValueNoId().joinSurroundIfNotEmptyToString(", ") { prop ->
                    """
                ${
                        prop.isAnonymous().ifElse({
                            "${prop.type().name()}: command.${prop.type().name()}"
                        },
                            {
                                "${prop.name().capitalize()}: command.${prop.name().capitalize()}"
                            })
                    }"""
                }
            }}""", "nil"
        )
    }, ${g.time.Now.toGoCall(c, derived, api)})"""
}

fun <T : EventI<*>> T.toGoApplyEvent(
    c: GenerationContext, derived: String, excludeProps: Set<String> = emptySet(), variableName: String = "entity"
): String =
    props().filter { !excludeProps.contains(it.name()) }
        .joinSurroundIfNotEmptyToString("") { it.toGoApplyEventProp(c, derived, variableName) }

fun <T : AttributeI<*>> T.toGoApplyEventProp(c: GenerationContext, derived: String, variableName: String): String = """
		$variableName.${name().capitalize()} = ${
    (value() != null).ifElse({ toGoValue(c, derived) },
        { "eventData.${name().capitalize()}" })
}"""

fun <T : OperationI<*>> T.toGoEventHandlerApplyEvent(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
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
    val id = entity.propIdOrAdd().name().capitalize()
    return events.joinSurroundIfNotEmptyToString("") { event ->
        val handler = c.n(event, DesignDerivedType.Handler).capitalize()
        """${event.toGoRegisterEventData(c, api, derived)}
    //default command handler implementation
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

fun <T : CompilationUnitI<*>> T.toGoAggregateEngineConst(
    c: GenerationContext,
    derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    val name = c.n(entity, api)
    return """
const ${entity.name()}${DesignDerivedType.AggregateType} ${c.n(g.eh.AggregateType)} = "$name"
"""
}

fun <T : ConstructorI<*>> T.toGoAggregateEngineBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    val entityNameDec = entity.name().decapitalize()
    val entityNameDecAggregate = "${entityNameDec}Aggregate"
    val entityNameAggregate = "${entity.name()}Aggregate"
    val aggregateType = "${entity.name()}${DesignDerivedType.AggregateType}"

    return """
    $entityNameDecAggregate${DesignDerivedType.Executors} := New${entity.name()}Aggregate${DesignDerivedType.Executors}Full()
    ${entityNameDec}Aggregate${DesignDerivedType.Handlers} := New${entity.name()}Aggregate${DesignDerivedType.Handlers}Full()
    
    entityFactory := func() ${c.n(g.eh.Entity)} { return ${
        entity.primaryOrFirstConstructorOrFull().toGoCall(c, derived, api)
    } }
    aggregateEngine := ${c.n(g.gee.eh.AggregateEngine.NewAggregateEngine)}(middleware, $aggregateType,
        func(id ${c.n(g.google.uuid.UUID)}) ${c.n(g.eh.Aggregate)} {
            return &$entityNameAggregate{
                AggregateBase:             ${c.n(g.eh.NewAggregateBase)}($aggregateType, id),
                ${entity.name()}:                   ${
        entity.primaryOrFirstConstructorOrFull().toGoCall(c, derived, api)
    },
                Aggregate${DesignDerivedType.Executors}: ${entityNameDecAggregate}${DesignDerivedType.Executors},
                Aggregate${DesignDerivedType.Handlers}:  ${entityNameDecAggregate}${DesignDerivedType.Handlers},
            }
        }, entityFactory,
        ${entity.name()}CommandTypes().Literals(), ${entity.name()}EventTypes().Literals())

    ret = &${entity.name()}AggregateEngine{
        AggregateEngine: aggregateEngine,
        Aggregate${DesignDerivedType.Executors}: ${entityNameDecAggregate}${DesignDerivedType.Executors},
        Aggregate${DesignDerivedType.Handlers}: ${entityNameDecAggregate}${DesignDerivedType.Handlers},
    }"""
}

fun <T : OperationI<*>> T.toGoAggregateEngineSetupBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    val stateMachines = entity.findDownByType(StateMachineI::class.java)
    return """
    if err = o.AggregateEngine.Setup(); err != nil {
        return
    }${
        stateMachines.joinSurroundIfNotEmptyToString(nL, nL) { stateMachine ->
            val stateMachinePrefix = stateMachine.sourceArtifactsPrefix()
            """
    if err = o.$stateMachinePrefix${DesignDerivedType.Executors}.SetupCommandHandler(); err != nil {
        return
    }
    
    if err = o.$stateMachinePrefix${DesignDerivedType.Handlers}.SetupEventHandler(); err != nil {
        return
    }"""
        }
    }"""
}

fun <T : CompilationUnitI<*>> T.toGoAggregateEngineRegisterForEvents(
    c: GenerationContext,
    derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
    val name = c.n(this, api)
    val entity = findParentMust(EntityI::class.java)
    val events = entity.findDownByType(EventI::class.java).sortedBy { it.name() }
    val aggregateHandler = "${entity.name()}AggregateHandler"
    val projectEventHandler = "${entity.name()}Projector"

    return """${
        events.joinSurroundIfNotEmptyToString(nL, nL) {
            """
func (o *$name) RegisterFor${it.name().capitalize()}(handler ${c.n(g.eh.EventHandler)}) error {
    return o.RegisterForEvent(handler, ${entity.name()}EventTypes().${it.parentNameAndName().capitalize()}())
}"""
        }
    }
    
func (o *$name) Register${projectEventHandler}(
    projType string, listener $aggregateHandler, events []${c.n(g.eh.EventType)}) (ret *$projectEventHandler, err error) {
	
    var repo ${c.n(g.eh.ReadWriteRepo)}
    if repo, err = o.Repos(projType, o.EntityFactory); err != nil {
        return
    }
    
	ret = New${projectEventHandler}(projType, listener, repo)
    proj := projector.NewEventHandler(ret, repo)
	proj.SetEntityFactory(o.EntityFactory)
	err = o.RegisterForEvents(proj, events)
	return
}

type $projectEventHandler struct {
	$aggregateHandler
    projType ${c.n(g.eh.Type)}
    Repo ${c.n(g.eh.ReadRepo)}
}

func New${projectEventHandler}(projType string, eventHandler $aggregateHandler, repo ${c.n(g.eh.ReadRepo)}) (ret *${projectEventHandler}) {
	ret = &${projectEventHandler}{
        $aggregateHandler: eventHandler,
		projType:     ${c.n(g.eh.Type)}(projType),
        Repo:              repo,
	}
	return
}

func (o *${projectEventHandler}) ProjectorType() ${c.n(g.eh.Type)} {
	return o.projType
}

func (o *${projectEventHandler}) Project(
	ctx ${c.n(g.context.Context)}, event ${c.n(g.eh.Event)}, entity ${c.n(g.eh.Entity)}) (ret ${c.n(g.eh.Entity)}, err error) {

	if err = o.Apply(event, entity.(*${entity.name()})); err == nil {
        if event.EventType() != ${entity.name()}DeletedEvent {
            ret = entity
        }
    }
	return
}

"""
}

fun <T : ConstructorI<*>> T.toGoEhEngineBody(
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
            """${it.name().capitalize()}${DesignDerivedType.AggregateEngine}: New${
                it.name().capitalize()
            }${DesignDerivedType.AggregateEngine}(eventStore, eventBus, commandBus)"""
        }
    }}
"""
}


fun <T : OperationI<*>> T.toGoEhEngineSetupBody(
    c: GenerationContext,
    derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
    val module = findParentMust(ModuleI::class.java)
    val entities = module.entities().filter { it.belongsToAggregate().isEMPTY() }
    return """${
        entities.joinSurroundIfNotEmptyToString("$nL    ", "$nL    ") { entity ->
            """
    if err = o.${entity.name().capitalize()}.Setup(); err != nil {
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
func (o *$name) AggregateID() ${c.n(g.google.uuid.UUID)} { return o.${entity.propIdOrAdd().nameForGoMember()} }
func (o *$name) AggregateType() ${c.n(g.eh.AggregateType)} { return ${entity.name()}${DesignDerivedType.AggregateType} }
func (o *$name) CommandType() ${c.n(g.eh.CommandType)} { return ${nameAndParentName().capitalize()}${DesignDerivedType.Command} }
"""
}

fun <T : OperationI<*>> T.toGoAggregateEngineRegisterCommands(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    //TODO find a way to get correct name for xxxAggregateType
    return """${c.n(g.gee.eh.AggregateEngine.RegisterForAllEvents)}(handler, ${c.n(entity, api)} AggregateType, ${
        entity.name()
    } CommandTypes().Literals())"""
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
func (o *$name) EntityID() ${c.n(g.google.uuid.UUID)} { return o.${propIdOrAdd().nameForGoMember()} }
func (o *$name) Deleted() *${c.n(g.time.Time)} { return o.${propDeletedAt().nameForGoMember()} }
"""
}
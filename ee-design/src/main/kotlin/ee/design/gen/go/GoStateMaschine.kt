package ee.design.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.joinWithIndexSurroundIfNotEmptyToString
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

    return commands.joinSurroundIfNotEmptyToString("") { command ->
        val handler = c.n(command, DesignDerivedType.Handler).capitalize()
        """
    o.$handler = func(command ${command.toGo(c, api)}, entity ${
            entity.toGo(c, api)
        }, store ${g.gee.ehu.AggregateStoreEvent.toGo(c, api)}) (err error) {${
            if (command is CreateByI<*> && command.event().isNotEMPTY()) {
                """${command.toGoCheckInitValuesId(c)}
        ${command.toGoStoreEvent(c, derived, api)}"""
            } else if (command is AddChildByI<*> && command.event().isNotEMPTY()) {
                """
        if command.${command.type().propIdNameParentCap()} == uuid.Nil {
            command.${command.type().propIdNameParentCap()} = ${c.n(g.google.uuid.New)}()
        }
        ${command.toGoStoreEvent(c, derived, api)}"""
            } else if (command is UpdateChildByI<*> && command.event().isNotEMPTY()) {
                """
        if command.${command.type().propIdNameParentCap()} == uuid.Nil {
            err = ${
                    c.n(
                        g.gee.ehu.EntityChildIdNotDefined,
                        api
                    )
                }(command.AggregateID(), command.AggregateType(), "${command.child().name()}")
        }
        ${command.toGoStoreEvent(c, derived, api)}"""
            } else if ((command is UpdateByI<*> || command is DeleteByI<*> ||
                        !command.isAffectMulti()) && command.event().isNotEMPTY()
            ) {
                """${command.toGoCheckInitValuesId(c)}
        ${command.toGoStoreEvent(c, derived, api)}"""
            } else {
                """
        err = ${c.n(g.gee.ehu.CommandHandlerNotImplemented, api)}(${c.n(command, api)}${DesignDerivedType.Command})"""
            }
        }
        return
    }"""
    }
}

private fun CommandI<*>.toGoCheckInitValuesId(c: GenerationContext) =
    propsCollectionValues().joinSurroundIfNotEmptyToString("") {
        val idName = it.type().propIdOrAdd().name().capitalize()
        """
        
        for _, item := range command.${it.name().capitalize()} {
            if item.$idName == uuid.Nil {
                item.$idName = ${c.n(g.google.uuid.New)}()
            }
        }"""
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
            g.gee.ehu.AggregateStoreEvent, api
        )
    }) (err error) {
		if err = preparer(command, entity); err == nil {
			err = prevHandler(command, entity, store)
		}
		return
	}"""
}


fun <T : OperationI<*>> T.toGoStateAddCommandsPreparerBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

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

fun <T : OperationI<*>> T.toGoStateCommandHandlerExecuteBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    val state = findParentMust(ControllerI::class.java).derivedFrom() as StateI<*>
    val commands = state.uniqueCommands()
    val entityParamName = entity.name().decapitalize()

    val notSupported = """err = ${c.n(g.errors.New, api)}(${
        c.n(g.fmt.Sprintf, api)
    }("Not supported command type '%v' in state '${state.name()}' for entity '%v", cmd.CommandType(), $entityParamName))"""

    return """
    if o.CommandsPreparer != nil {
        if err = o.CommandsPreparer(cmd, $entityParamName); err != nil {
            return
        }
    }
    ${
        commands.joinSurroundIfNotEmptyToString(
            separator = "", prefix = """
    switch cmd.CommandType() {""", postfix =
            """
    default:
        $notSupported
    }""", emptyString = notSupported
        ) { executor ->
            """
    case ${executor.dataTypeNameAndParentName().capitalize()}Command:
        err = o.${executor.name().capitalize()}${DesignDerivedType.Handler}(cmd.(${
                executor.toGo(c, api)
            }), $entityParamName, store)"""
        }
    }"""
}

fun <T : OperationI<*>> T.toGoStatesCommandHandlerSetupBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val stateMachine = findParentMust(StateMachineI::class.java)

    return stateMachine.states().joinSurroundIfNotEmptyToString("") { state ->
        """
    if err = o.${state.name()}.SetupCommandHandler(); err != nil {
        return
    }"""
    }
}

fun <T : OperationI<*>> T.toGoStatesCommandHandlerExecute(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    val stateMachine = findParentMust(StateMachineI::class.java)

    val entityParamName = entity.name().decapitalize()
    val stateMachinePrefix = stateMachine.sourceArtifactsPrefix()
    val entityStateMachinePrefix = "${entity.name()}$stateMachinePrefix"

    val stateParamName = "${stateMachine.sourceArtifactsPrefix()}State"

    return """
    ${
        stateMachine.states().joinSurroundIfNotEmptyToString(
            separator = "",
            prefix = {
                """
    stateTypes := ${entityStateMachinePrefix}StateTypes()                    
    currentAggregateState := ${entityParamName}.$stateParamName
    if currentAggregateState == "" {
        currentAggregateState = stateTypes.${stateMachine.defaultState().name()}().Name()
    }
    
    switch currentAggregateState {"""
            },
            postfix = {
                """
    default:
		err = ${c.n(g.errors.New, api)}(${
                    c.n(g.fmt.Sprintf, api)
                }("Not supported state '%v' for entity '%v", ${entityParamName}.${
                    stateMachine.sourceArtifactsPrefix()
                }State, $entityParamName))
	}"""
            }) { state ->
            """
    case stateTypes.${state.name()}().Name():
        err = o.${state.name()}.Execute(cmd, $entityParamName, store)"""
        }
    }"""
}

fun <T : StateI<*>> T.toGoStateHandler(
    c: GenerationContext, derived: String = "Handler",
    api: String = "Handler"
): String {
    return """
        ${toGoImpl(c, derived, api, true)}
"""
}


fun <T : OperationI<*>> T.toGoStateEventHandlersPreparerBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    val handlerName = "EventsPreparer"

    return """
    prevHandler := o.$handlerName
	o.$handlerName = func(event ${c.n(g.eh.Event, api)}, entity *${c.n(entity, api)}) (err error) {
		if err = preparer(event, entity); err == nil {
            if prevHandler != nil {
			    err = prevHandler(event, entity)
            }
		}
		return
	}"""
}

fun <T : OperationI<*>> T.toGoStatesEventHandlerApplyEvent(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    val stateMachine = findParentMust(StateMachineI::class.java)

    val entityParamName = entity.name().decapitalize()
    val stateMachinePrefix = stateMachine.sourceArtifactsPrefix()
    val entityStateMachinePrefix = "${entity.name()}$stateMachinePrefix"

    val stateParamName = "${stateMachine.sourceArtifactsPrefix()}State"
    val newStateParamName = "new$stateParamName"

    return """
    ${
        stateMachine.states().joinSurroundIfNotEmptyToString(
            separator = "",
            prefix = {
                """
    currentAggregateState := ${entityParamName}.$stateParamName
    if currentAggregateState == "" {
        currentAggregateState = ${entityStateMachinePrefix}StateTypes().${stateMachine.defaultState().name()}().Name()
    }
    
    var $newStateParamName *${entityStateMachinePrefix}StateType     
    switch currentAggregateState {"""
            },
            postfix = {
                """
    default:
		err = ${c.n(g.errors.New, api)}(${
                    c.n(g.fmt.Sprintf, api)
                }("Not supported ${stateMachine.sourceArtifactsPrefix()}State '%v' for entity '%v", ${entityParamName}.${
                    stateMachine.sourceArtifactsPrefix()
                }State, $entityParamName))
	}

    if err == nil && $newStateParamName != nil && $newStateParamName.Name() != ${entityParamName}.$stateParamName {
        ${entityParamName}.$stateParamName = $newStateParamName.Name()
    }"""
            }) { state ->
            """
    case ${entityStateMachinePrefix}StateTypes().${state.name()}().Name():
        new${stateMachine.sourceArtifactsPrefix()}State, err = o.${state.name()}.Apply(event, $entityParamName)"""
        }
    }"""
}

fun <T : OperationI<*>> T.toGoStateEventType(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    val stateMachine = findParentMust(StateMachineI::class.java)

    val stateMachinePrefix = stateMachine.sourceArtifactsPrefix()
    val entityStateMachinePrefix = "${entity.name()}$stateMachinePrefix"

    val state = findParentMust(ControllerI::class.java).derivedFrom() as StateI<*>

    return """
    ret = ${entityStateMachinePrefix}StateTypes().${state.name()}()"""
}

fun <T : OperationI<*>> T.toGoStateEventHandlerApplyEvent(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    val state = findParentMust(ControllerI::class.java).derivedFrom() as StateI<*>
    val eventHandlersList = state.eventHandlers()
    val entityParamName = entity.name().decapitalize()
    val stateMachine = findParentMust(StateMachineI::class.java)

    val stateMachinePrefix = stateMachine.sourceArtifactsPrefix()
    val entityStateMachinePrefix = "${entity.name()}$stateMachinePrefix"

    return """
    ${
        eventHandlersList.joinSurroundIfNotEmptyToString(separator = "", prefix = {
            """
    switch event.EventType() {"""
        }, postfix = {
            """
    default:
		err = ${c.n(g.errors.New, api)}(${
                c.n(g.fmt.Sprintf, api)
            }("Not supported event type '%v' for entity '%v", event.EventType(), $entityParamName))
	}"""
        }) { eventHandlers ->
            val event = eventHandlers.event
            """
    case ${event.dataTypeParentNameAndName().capitalize()}Event:
        err = o.${event.name().capitalize()}${DesignDerivedType.Handler}(event, ${
                event.hasData().then("event.Data().(${event.toGo(c, api)}), ")
            }$entityParamName)${
                eventHandlers.handlers.filter {
                    it.to().isNotEMPTY()
                }.joinWithIndexSurroundIfNotEmptyToString(separator = "") { i, handler ->
                    val toGoPredicates = handler.toGoPredicates(c, derived, api)
                    val newState = """${entityStateMachinePrefix}StateTypes().${handler.to().name()}()"""
                    if (toGoPredicates.isEmpty()) {
                        if (i == 0) {
                            """    
        ret = $newState"""
                        } else {
                            """ else {
            ret = $newState
        }"""
                        }
                    } else {
                        if (i == 0) {
                            """
        if ($toGoPredicates) {
            ret = $newState
        }"""
                        } else {
                            """ else if ($toGoPredicates) {
            ret = $newState
        }"""
                        }
                    }
                }
            }"""
        }
    }"""
}

fun <T : OperationI<*>> T.toGoStatesEventHandlerSetupBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val stateMachine = findParentMust(StateMachineI::class.java)

    return stateMachine.states().joinSurroundIfNotEmptyToString("") { state ->
        """
    if err = o.${state.name()}.SetupEventHandler(); err != nil {
        return
    }"""
    }
}


fun <T : OperationI<*>> T.toGoStateEventHandlerSetupBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    val state = findParentMust(ControllerI::class.java).derivedFrom() as StateI<*>

    val events = state.uniqueEvents()

    val id = entity.propIdOrAdd().name().capitalize()
    return events.joinSurroundIfNotEmptyToString("") { event ->
        val handler = c.n(event, DesignDerivedType.Handler).capitalize()
        """
        ${event.toGoRegisterEventData(c, api, derived)}
	//default event handler implementation
	o.$handler = func(event ${c.n(g.eh.Event, api)}, ${
            event.hasData().then(
                "eventData ${event.toGo(c, api)}, "
            )
        } entity ${entity.toGo(c, api)}) (err error) {
        ${
            if (event is CreatedI<*>) {
                """
		entity.$id = event.AggregateID()${
                    event.toGoApplyEvent(c, derived)
                }"""
            } else if (event is UpdatedI<*>) {
                event.toGoApplyEvent(c, derived)
            } else if (event is DeletedI<*>) {
                """
        *entity = *${entity.toGoInstance(c, derived, api)}${
                    event.toGoApplyEvent(c, derived)
                }"""
            } else if (event is ChildAddedI<*>) {
                """
        child := ${event.type().primaryOrFirstConstructorOrFull().toGoCall(c, derived, api)}
        child.$id = eventData.${event.type().propIdNameParentCap()}${
                    event.toGoApplyEvent(c, derived, setOf(event.type().propIdNameParentCap()), "child")
                }
        entity.${event.child().toGoAddMethodName()}(child)"""
            } else if (event is ChildUpdatedI<*>) {
                """
        if _, child := entity.${event.child().toGoFindMethodName()}(eventData.${
                    event.type().propIdNameParentCap()
                }); child == nil {
            err = ${c.n(g.gee.ehu.EntityChildNotExists, api)}(event.AggregateID(), event.AggregateType(), 
                eventData.${event.type().propIdNameParentCap()}, "${event.child().name()}")           
        } else {${event.toGoApplyEvent(c, derived, setOf(event.type().propIdNameParentCap()), "    child")}
        }"""
            } else if (event is ChildRemovedI<*>) {
                """
        if oldItem := entity.${event.child().toGoRemoveMethodName()}(eventData.${
                    event.type().propIdNameParentCap()
                }); oldItem == nil {
            err = ${c.n(g.gee.ehu.EntityChildNotExists, api)}(event.AggregateID(), event.AggregateType(), 
                eventData.${event.type().propIdNameParentCap()}, "${event.child().name()}")           
        }"""
            } else {
                """
        err = ${c.n(g.gee.ehu.EventHandlerNotImplemented, api)}(${c.n(event, api)}${DesignDerivedType.Event})"""
            }
        }
        return
    }"""
    }
}

data class EventHandlers(val event: EventI<*>, val handlers: MutableList<HandlerI<*>>)
data class CommandExecutors(val command: CommandI<*>, val executors: MutableList<ExecutorI<*>>)

fun StateI<*>.uniqueEvents() = handlers().map { it.on() }.toSet().toList().sortedBy { it.name() }
fun StateI<*>.uniqueCommands() = executors().map { it.on() }.toSet().toList().sortedBy { it.name() }

fun StateI<*>.eventHandlers(): List<EventHandlers> {
    val eventToEventHandlers = mutableMapOf<String, EventHandlers>()
    handlers().forEach {
        val eventHandlers = eventToEventHandlers.getOrPut(it.on().name(), {
            EventHandlers(it.on(), mutableListOf())
        })
        eventHandlers.handlers.add(it)
    }
    return eventToEventHandlers.values.sortedBy { it.event.name() }
}

fun StateI<*>.commandHandlers(): List<CommandExecutors> {
    val commandToCommandExecutors = mutableMapOf<String, CommandExecutors>()
    executors().forEach {
        val eventHandlers = commandToCommandExecutors.getOrPut(it.on().name(), {
            CommandExecutors(it.on(), mutableListOf())
        })
        eventHandlers.executors.add(it)
    }
    return commandToCommandExecutors.values.sortedBy { it.command.name() }
}

fun EventI<*>.toGoRegisterEventData(c: GenerationContext, api: String, derived: String) =
    if (hasData()) {
        """
    //register event object factory
    ${c.n(g.eh.RegisterEventData, api)}(${c.n(this, api)}Event, func() ${c.n(g.eh.EventData, api)} {
        return &${c.n(this, derived)}{${toGoPropAnonymousInit(c, derived, api)}}
    })
        """
    } else {
        ""
    }

fun <T : OperationI<*>> T.toGoAggregateApplyEventBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    val stateMachine = findParentMust(StateMachineI::class.java)

    val stateMachinePrefix = stateMachine.sourceArtifactsPrefix()

    return """
    err = o.${stateMachinePrefix}Handlers.Apply(event, o.${entity.name()})"""
}

fun <T : OperationI<*>> T.toGoAggregateHandleCommand(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    val stateMachine = findParentMust(StateMachineI::class.java)

    val stateMachinePrefix = stateMachine.sourceArtifactsPrefix()

    return """
    err = o.${stateMachinePrefix}Executors.Execute(cmd, o.${entity.name()}, o.AggregateBase)"""
}

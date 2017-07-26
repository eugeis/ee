package ee.design.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.design.*
import ee.lang.*
import ee.lang.gen.go.g
import ee.lang.gen.go.nameForMember
import ee.lang.gen.go.toGo
import ee.lang.gen.go.toGoImpl

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

fun <T : OperationI> T.toGoCommandHandlerExecuteCommand(c: GenerationContext,
                                                        derived: String = DesignDerivedKind.IMPL,
                                                        api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val commands = entity.findDownByType(CommandI::class.java)
    return """
    var ret error
    ${commands.joinSurroundIfNotEmptyToString("", "switch cmd.CommandType() {") {
        """
    case ${it.nameAndParentName().capitalize()}Command:
        ret = o.${it.name().capitalize()}${DesignDerivedType.Handler}(cmd.(${it.toGo(c, api)}), entity.(${entity.toGo(c, api)}), store)"""
    }}
    default:
		ret = ${c.n(g.errors.New, api)}(${c.n(g.fmt.Sprintf, api)}("Not supported command type '%v' for entity '%v", cmd.CommandType(), entity))
	}
    return ret
    """
}

fun <T : OperationI> T.toGoCommandHandlerSetup(c: GenerationContext,
                                               derived: String = DesignDerivedKind.IMPL,
                                               api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val commands = entity.findDownByType(CommandI::class.java)
    return """
    var ret error
    return ret
    """
}


fun <T : OperationI> T.toGoEventHandlerApplyEvent(c: GenerationContext,
                                                  derived: String = DesignDerivedKind.IMPL,
                                                  api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val events = entity.findDownByType(EventI::class.java)
    return """
    var ret error
    ${events.joinSurroundIfNotEmptyToString("", "switch event.EventType() {") {
        """
    case ${it.parentNameAndName().capitalize()}Event:
        ret = o.${it.name().capitalize()}${DesignDerivedType.Handler}(event.Data().(${it.toGo(c, api)}), entity.(${entity.toGo(c, api)}))"""
    }}
    default:
		ret = ${c.n(g.errors.New, api)}(${c.n(g.fmt.Sprintf, api)}("Not supported event type '%v' for entity '%v", event.EventType(), entity))
	}
    return ret
    """
}

fun <T : OperationI> T.toGoEventHandlerSetup(c: GenerationContext,
                                             derived: String = DesignDerivedKind.IMPL,
                                             api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val events = entity.findDownByType(EventI::class.java)
    return """
    var ret error
    return ret
    """
}

fun <T : CompilationUnitI> T.toGoAggregateInitializer(c: GenerationContext,
                                                      derived: String = DesignDerivedKind.IMPL,
                                                      api: String = DesignDerivedKind.API): String {
    val name = c.n(this, api)
    val entity = findParentMust(EntityI::class.java)
    val events = entity.events().first().findDownByType(EventI::class.java)
    return """
const ${entity.name()}${DesignDerivedType.AggregateType} ${c.n(g.eh.AggregateType)} = "$name"

func New${name}(
	eventStore ${c.n(g.eh.EventStore)}, eventBus ${c.n(g.eh.EventBus, api)}, eventPublisher ${c.n(g.eh.EventPublisher)},
	commandBus ${c.n(g.eh.CommandBus)}) (ret *${name}) {
    commandHandler := &${entity.name()}${DesignDerivedType.CommandHandler}{}
    eventHandler := &${entity.name()}${DesignDerivedType.EventHandler}{}
	ret = &$name{AggregateInitializer: ${c.n(g.gee.eh.AggregateInitializer.NewAggregateInitializer, api)}(${entity.name()}${DesignDerivedType.AggregateType},
        func(id ${c.n(g.eh.UUID)}) ${c.n(g.eh.Aggregate)} {
            return ${c.n(g.gee.eh.NewAggregateBase)}(${entity.name()}${DesignDerivedType.AggregateType}, id, commandHandler, eventHandler, &${c.n(entity, derived)}{})
        },
        ${entity.name()}CommandTypes().Literals(), ${entity.name()}EventTypes().Literals(),
        []func() error{commandHandler.SetupCommandHandler, eventHandler.SetupEventHandler},
        eventStore, eventBus, eventPublisher, commandBus),
        ${entity.name()}${DesignDerivedType.CommandHandler}: commandHandler,
        ${entity.name()}${DesignDerivedType.EventHandler}: eventHandler,
    }
	return
}
${events.joinSurroundIfNotEmptyToString(nL, nL) {
        """
func (o *${name}) RegisterFor${it.name().capitalize()}(handler ${c.n(g.eh.EventHandler)}){
    o.RegisterForEvent(handler, ${entity.name()}EventTypes().${it.parentNameAndName().capitalize()}())
}"""
    }}
"""
}

fun <T : CompilationUnitI> T.toGoEventhorizonInitializer(c: GenerationContext,
                                                         derived: String = DesignDerivedKind.IMPL,
                                                         api: String = DesignDerivedKind.API): String {
    val name = c.n(this, api)
    val module = findParentMust(ModuleI::class.java)
    val entities = module.entities().filter { it.belongsToAggregate().isNotEMPTY() }
    return """
func New${name}(
	eventStore ${c.n(g.eh.EventStore)}, eventBus ${c.n(g.eh.EventBus, api)}, eventPublisher ${c.n(g.eh.EventPublisher)},
	commandBus ${c.n(g.eh.CommandBus)}) (ret *${name}) {
	ret = &$name{eventStore: eventStore, eventBus: eventBus, eventPublisher: eventPublisher,
            commandBus: commandBus, ${entities.joinSurroundIfNotEmptyToString(",$nL    ", "$nL    ") {
        """${it.name().capitalize()}${DesignDerivedType.AggregateInitializer}: New${
        it.name().capitalize()}${DesignDerivedType.AggregateInitializer}(eventStore, eventBus, eventPublisher, commandBus)"""
    }}}
	return
}

func (o *$name) Setup() (err error) {${entities.joinSurroundIfNotEmptyToString("$nL    ", "$nL    ") {
        """
    if err = o.${it.name().capitalize()}${DesignDerivedType.AggregateInitializer}.Setup(); err != nil {
        return
    }"""
    }}
    return
}
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

fun <T : AttributeI> T.toGoPropOptionalTag(c: GenerationContext,
                                           derived: String = DesignDerivedKind.IMPL,
                                           api: String = DesignDerivedKind.API): String {
    return """`eh:"optional"`"""
}

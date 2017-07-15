package ee.design.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.design.*
import ee.lang.*
import ee.lang.gen.go.g
import ee.lang.gen.go.nameForMember
import ee.lang.gen.go.toGoImpl

fun <T : EntityI> T.toGoCommandTypes(c: GenerationContext,
                                     derived: String = DesignDerivedKind.IMPL,
                                     api: String = DesignDerivedKind.API): String {
    val commands = findDownByType(CommandI::class.java)
    return commands.joinSurroundIfNotEmptyToString(nL, "${nL}const ($nL", "$nL)") {
        """     ${it.nameAndParentName().capitalize()}${DesignDerivedType.Command} ${c.n(g.eh.CommandType)} = "${it.nameAndParentName().capitalize()}""""
    }
}

fun <T : CompilationUnitI> T.toGoAggregate(c: GenerationContext,
                                           derived: String = DesignDerivedKind.IMPL,
                                           api: String = DesignDerivedKind.API): String {
    val name = c.n(this, api)
    return """
const ${name}Type ${c.n(g.eh.AggregateType)} = "$name"

func New$name(id ${c.n(g.eh.UUID, api)}) *$name {
	return &$name{
		AggregateBase: ${c.n(g.eh.NewAggregateBase)}(${name}Type, id),
	}
}

func (o *$name) HandleCommand(ctx ${c.n(g.context.Context)}, cmd ${c.n(g.eh.Command)}) error {
    println("HandleCommand %v - %v", ctx, cmd)
    return nil
}

func (o *$name) ApplyEvent(ctx context.Context, event ${c.n(g.eh.Event)}) error {
    println("ApplyEvent %v - %v", ctx, event)
    return nil
}
"""
}

fun <T : CompilationUnitI> T.toGoAggregateInitializer(c: GenerationContext,
                                                      derived: String = DesignDerivedKind.IMPL,
                                                      api: String = DesignDerivedKind.API): String {
    val name = c.n(this, api)
    val entity = findParentMust(EntityI::class.java)
    val events = entity.events().first().findDownByType(EventI::class.java)
    return """
func New${name}(
	eventStore ${c.n(g.eh.EventStore)}, eventBus ${c.n(g.eh.EventBus, api)}, eventPublisher ${c.n(g.eh.EventPublisher)},
	commandBus ${c.n(g.eh.CommandBus)}) (ret *${name}) {
	ret = &$name{AggregateInitializer: ${c.n(g.gee.eh.AggregateInitializer.NewAggregateInitializer, api)}(${entity.name()}AggregateType,
        func(id ${c.n(g.eh.UUID)}) ${c.n(g.eh.Aggregate)} { return New${entity.name()}Aggregate(id) },
        ${entity.name()}CommandTypes().Literals(), ${entity.name()}EventTypes().Literals(), eventStore, eventBus, eventPublisher, commandBus),
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
        """${it.name().decapitalize()}${DesignDerivedType.AggregateInitializer}: New${
        it.name().capitalize()}${DesignDerivedType.AggregateInitializer}(eventStore, eventBus, eventPublisher, commandBus)"""
    }}}
	return
}

func (o *$name) Setup() (err error) {${entities.joinSurroundIfNotEmptyToString("$nL    ", "$nL    ") {
        """
    if err = o.${it.name().decapitalize()}${DesignDerivedType.AggregateInitializer}.Setup(); err != nil {
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
func (o *${name}) AggregateID() ${c.n(g.eh.UUID)}            { return o.${entity.id().nameForMember()} }
func (o *${name}) AggregateType() ${c.n(g.eh.AggregateType)}  { return ${entity.name()}${DesignDerivedType.AggregateType} }
func (o *${name}) CommandType() ${c.n(g.eh.CommandType)}      { return ${nameAndParentName().capitalize()}${DesignDerivedType.Command} }
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

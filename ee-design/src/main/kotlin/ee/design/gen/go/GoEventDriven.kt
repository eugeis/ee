package ee.design.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.design.*
import ee.lang.*
import ee.lang.gen.go.g


fun <T : CompilationUnitI> T.toGoAggregate(c: GenerationContext,
                                           derived: String = DesignDerivedKind.IMPL,
                                           api: String = DesignDerivedKind.API): String {
    val name = c.n(this, api)
    return """
const ${name}Type ${c.n(eh.AggregateType, LangDerivedKind.API)} = "$name"
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
	eventStore *${c.n(eh.EventStore)}, eventBus *${c.n(eh.EventBus, api)}, eventPublisher *${c.n(eh.EventPublisher)},
	commandBus *${c.n(eh.CommandBus)}) (ret *${name}) {
	ret = &$name{AggregateInitializer: ${c.n(g.gee.eh.AggregateInitializer.NewAggregateInitializer, api)}(${entity.name()}AggregateType,
        ${entity.name()}CommandTypes().Literals(), ${entity.name()}EventTypes().Literals(), eventStore, eventBus, eventPublisher, commandBus),
    }
	return
}
${events.joinSurroundIfNotEmptyToString(nL, nL) {
        """
func (o *${name}) RegisterFor${it.name().capitalize()}(handler ${c.n(eh.EventHandler)}){
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
	eventStore *${c.n(eh.EventStore)}, eventBus *${c.n(eh.EventBus, api)}, eventPublisher *${c.n(eh.EventPublisher)},
	commandBus *${c.n(eh.CommandBus)}) (ret *${name}) {
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

fun <T : OperationI> T.toGoAggregateInitializerRegisterCommands(c: GenerationContext,
                                                                derived: String = DesignDerivedKind.IMPL,
                                                                api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    //TODO find a way to get correct name for xxxAggregateType
    return """${c.n(g.gee.eh.AggregateInitializer.RegisterForAllEvents)}(handler, $ { c.n(entity, api) } AggregateType, ${entity.name()} CommandTypes ().Literals())"""
}
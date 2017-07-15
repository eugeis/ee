package ee.design.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.design.DesignDerivedKind
import ee.design.EntityI
import ee.design.EventI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.go.g
import ee.lang.gen.go.toGoImpl


fun <T : CompilationUnitI> T.toGoAggregateInitializer(c: GenerationContext,
                                                      derived: String = DesignDerivedKind.IMPL,
                                                      api: String = DesignDerivedKind.API): String {
    val name = c.n(this, api)
    val events = findParentMust(EntityI::class.java).events().first().findDownByType(EventI::class.java)

    return """
const ${name}Type ${c.n(eh.AggregateType, LangDerivedKind.API)} = "$name"

type ${name}Initializer struct {
    *${c.n(g.gee.eh.AggregateInitializer, api)}
}${events.joinSurroundIfNotEmptyToString(nL, nL) {
        """
func (o *${name}Initializer) RegisterFor${it.name().capitalize()}(handler ${c.n(eh.EventHandler)}){
    o.RegisterForEvent(handler, ${name()}EventTypes().${it.parentNameAndName().capitalize()}())
}"""
    }}

func New${name}Initializer(
	store *${c.n(eh.EventStore)}, eventBus *${c.n(eh.EventBus, api)}, publisher *${c.n(eh.EventPublisher)},
	commandBus *${c.n(eh.CommandBus)}) (ret *${name}Initializer) {
	ret = &${name}Initializer{
        AggregateInitializer: ${c.n(g.gee.eh.AggregateInitializer.NewAggregateInitializer, api)}(${name}Type, ${name}CommandTypes().Literals(),
		ChurchAggregateEventTypes().Literals(), store, eventBus, publisher, commandBus),
    }
	return
}
"""
}

fun <T : OperationI> T.toGoAggregateInitializerRegisterCommands(c: GenerationContext,
                                                                derived: String = DesignDerivedKind.IMPL,
                                                                api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    //TODO find a way to get correct name for xxxAggregateType
    return """${c.n(g.gee.eh.AggregateInitializer.RegisterForAllEvents)}(handler, ${c.n(entity, api)}AggregateType, ${entity.name()}CommandTypes().Literals())"""
}
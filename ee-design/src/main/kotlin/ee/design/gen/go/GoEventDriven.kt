package ee.design.gen.go

import ee.design.DesignDerivedKind
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.go.toGoImpl


fun <T : EntityI> T.toGoEventhorizonAggregate(c: GenerationContext,
                                              derived: String = DesignDerivedKind.IMPL,
                                              api: String = DesignDerivedKind.API): String {
    val name = c.n(this, api)
    return """
const ${name}Type ${c.n(eh.AggregateType, LangDerivedKind.API)} = "$name"
${toGoImpl(c, derived, api)}"""
}

fun <T : OperationI> T.toGoAggregateInitializerRegisterCommands(c: GenerationContext,
                                                                derived: String = DesignDerivedKind.IMPL,
                                                                api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val module = entity.findParentMust(ModuleI::class.java)
    return """
    aggregateType := ${c.n(eh.AggregateType)}(${module.name()}AggregateTypes().${entity.name()})
    for _, command := range ${entity.name()}CommandTypes().Values() {
        handler.SetAggregate(aggregateType, ${c.n(eh.CommandType)}(command.Name()))
    }"""
}
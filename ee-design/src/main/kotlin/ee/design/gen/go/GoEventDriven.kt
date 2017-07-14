package ee.design.gen.go

import ee.design.DesignDerivedKind
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.go.g
import ee.lang.gen.go.toGoImpl


fun <T : CompilationUnitI> T.toGoAggregateType(c: GenerationContext,
                                               derived: String = DesignDerivedKind.IMPL,
                                               api: String = DesignDerivedKind.API): String {
    val name = c.n(this, api)
    return """
const ${name}Type ${c.n(eh.AggregateType, LangDerivedKind.API)} = "$name""""
}

fun <T : OperationI> T.toGoAggregateInitializerRegisterCommands(c: GenerationContext,
                                                                derived: String = DesignDerivedKind.IMPL,
                                                                api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    return """${c.n(g.gee.eh.RegisterCommands)}(handler, ${c.n(entity, api)}AggregateType, ${entity.name()}CommandTypes().Literals())"""
}
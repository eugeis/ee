package ee.design.gen.go

import ee.design.DesignDerivedKind
import ee.design.EntityI
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
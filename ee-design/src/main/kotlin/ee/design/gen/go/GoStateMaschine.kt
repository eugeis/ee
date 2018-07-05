package ee.design.gen.go

import ee.design.*
import ee.lang.GenerationContext
import ee.lang.findParentMust
import ee.lang.gen.go.g
import ee.lang.gen.go.nameForGoMember
import ee.lang.gen.go.toGoImpl
import ee.lang.nameAndParentName

fun <T : StateI<*>> T.toGoStateHandler(c: GenerationContext, derived: String = "Handler",
    api: String = "Handler"): String {
    val entity = findParentMust(EntityI::class.java)
    val name = c.n(this, derived)
    return """
        ${toGoImpl(c, derived, api, true)}
"""
}
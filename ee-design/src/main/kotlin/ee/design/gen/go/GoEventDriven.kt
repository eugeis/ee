package ee.design.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.design.CommandI
import ee.lang.*
import ee.lang.gen.go.toGoAddMethod
import ee.lang.gen.go.toGoGetMethod
import ee.lang.gen.go.toGoMember


fun <T : CommandI> T.toGoCommand(c: GenerationContext,
                                 derived: String = DerivedNames.IMPL.name,
                                 api: String = DerivedNames.API.name): String {
    val name = c.n(this, derived)
    return """
type $name struct {${
    params().joinSurroundIfNotEmptyToString(nL, prefix = nL) { it.toGoMember(c, derived, api, false) }}
}${
    params().filter { it.accessible() && !it.mutable() }.joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        it.toGoGetMethod(name, c, derived)
    }}${
    params().filter { it.type().isOrDerived(n.List) }.joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        it.toGoAddMethod(name, c, derived)
    }}"""
}
package ee.lang.gen.doc

import ee.common.ext.ifElse
import ee.common.ext.joinWrappedToString
import ee.common.ext.then
import ee.lang.*
import ee.lang.gen.kt.toKotlinCallParams

fun <T : CompilationUnitI<*>> T.toMarkdownClassImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                             api: String = LangDerivedKind.API,
                                             itemName: String = c.n(this, derived),
                                             dataClass: Boolean = this is BasicI<*> &&
                                                     superUnits().isEmpty() && superUnitFor().isEmpty(),
                                             nonBlocking: Boolean = isNonBlocking()): String {
    return """@startuml
        ${name()} -> ${props().size}: count of properties is
        @enduml """
}

fun <T : CommentI<*>> T.toPlainUml(c: GenerationContext, generateComments: Boolean): String =
        (generateComments && isNotEMPTY()).then {
            "note right:${name()}"
        }

fun <T : CompilationUnitI<*>> T.toPlainUmlClassImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                    generateComments: Boolean=false): String {
    return """

        @startuml
        title ${parent().name()}
        ${isIfc().ifElse("interface ", "class")} ${name()} {
        {field} properties ${props().size} operation
        }
        ${doc().toPlainUml(c, generateComments)}
        @enduml"""
}
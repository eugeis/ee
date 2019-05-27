package ee.lang.gen.doc

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.kt.*

fun <T : CompilationUnitI<*>> T.toMarkdownClassImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                             api: String = LangDerivedKind.API,
                                             itemName: String = c.n(this, derived),
                                             dataClass: Boolean = this is BasicI<*> &&
                                                     superUnits().isEmpty() && superUnitFor().isEmpty(),
                                             nonBlocking: Boolean = isNonBlocking()): String {
    return """
        ${name()} -> ${props().size}: count of properties is
        """
}

fun <T : CommentI<*>> T.toPlainUml(c: GenerationContext, generateComments: Boolean): String =
        (generateComments && isNotEMPTY()).then {
            "note right:${name()}"
        }

fun <T : AttributeI<*>> T.toPlainUmlMember(c: GenerationContext, genProps: Boolean = true): String =
        (genProps && isNotEMPTY()).then {
            "${c.n(this)} : ${c.n(type())}"
        }
fun OperationI<*>.toPlainOperUml(c: GenerationContext, genOperations:Boolean = true): String = (genOperations && isNotEMPTY()).then {
   // "${it.name()}: ${it.type()}
    "${c.n(this)}"
}

fun <T : CompilationUnitI<*>> T.toPlainUmlClassImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                    generateComments: Boolean=false, startStopUmpl: Boolean = true): String {
    return """
           ${startUml(startStopUmpl)}
          ${toPlainUmlTemplateClassImpl(c, startStopUml = false)}
        ${stopUml(startStopUmpl)}"""

}

fun <T : CompilationUnitI<*>> T.toPlainUmlTemplateClassImpl(c: GenerationContext,generateComments: Boolean=false,
                                                               startStopUml: Boolean = true,
                                                               genOperations: Boolean = true,
                                                               genProps: Boolean = true) :String {
          return """
            ${startUml(startStopUml)}
            package ${namespace()} <<Folder>> {
                title ${parent().name()}
                ${isIfc().ifElse("interface ", "class")} ${name()} {
                  {field} ${props().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
                  it.toPlainUmlMember(c, genProps) }}
                  {method} ${operations().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL)
                  {it.toPlainOperUml(c,genOperations)}}
                }
                 ${doc().toPlainUml(c, generateComments)}
            }
            ${stopUml(startStopUml)}
        """
}

private fun stopUml(startStopUml: Boolean) = startStopUml.then("@enduml")

private fun startUml(startStopUml: Boolean) = startStopUml.then("@startuml")


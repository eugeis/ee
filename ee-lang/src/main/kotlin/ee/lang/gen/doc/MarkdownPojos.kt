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
# ${parent().name()}

       ${parent().name()}is a module and artifact mcr-opcua

## ${namespace()}

          ${namespace()} is a package
## Properties

          ${props().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {it.toPlainUmlMember(c, genProps = true) }}
## Operations

          ${operations().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL){it.toPlainOperUml(c,genOperations = true)}}

## PlantUml diagram
    ```plantuml

        ${toPlainUmlClassImpl(c, startStopUmpl = true)}
    ```
        """
}

fun <T : CommentI<*>> T.toPlainUml(c: GenerationContext, generateComments: Boolean,startNoteUml:Boolean = true): String =
        (generateComments && isNotEMPTY()).then { "${startNoteUml(startNoteUml)} ${name()}"
        }

fun <T : AttributeI<*>> T.toPlainUmlMember(c: GenerationContext, genProps: Boolean = true): String =
        (genProps && isNotEMPTY()).then { "${c.n(this)} : ${c.n(type())}"
        }
fun OperationI<*>.toPlainOperUml(c: GenerationContext, genOperations:Boolean = true): String = (genOperations && isNotEMPTY()).then {
   // "${it.name()}: ${it.type()}
    "${c.n(this)}"
}

fun <T : CompilationUnitI<*>> T.toPlainUmlOpenclassImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                        api: String = LangDerivedKind.API,
                                                        itemName: String = c.n(this, derived),
                                                        dataClass: Boolean = this is BasicI<*> &&
                                                                superUnits().isEmpty() && superUnitFor().isEmpty(),
                                                        nonBlocking: Boolean = isNonBlocking()): String {
    return """
        ${(isOpen() && !dataClass).then("open ")}${dataClass.then("data ")}class $nL ..
    """
}

fun <T : CompilationUnitI<*>> T.toPlainUmlClassImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                    generateComments: Boolean=false, startStopUmpl: Boolean = true): String {
    return """
           ${startUml(startStopUmpl)}
           package ${namespace()} <<Folder>> {
          ${toPlainUmlTemplateClassImpl(c, startStopUml = false)}
          ${doc().toPlainUml(c, generateComments)}
          }

        ${stopUml(startStopUmpl)}"""

}

fun <T : CompilationUnitI<*>> T.toPlainUmlTemplateClassImpl(c: GenerationContext,generateComments: Boolean=false,
                                                               startStopUml: Boolean = true,
                                                               genOperations: Boolean = true,
                                                               genProps: Boolean = true) :String {
          return """
            ${startUml(startStopUml)}
                title ${parent().name()}
                ${isIfc().ifElse("interface ", "class")} ${name()} {
                  {field}${toPlainUmlOpenclassImpl(c)}
                  {field} ${props().joinSurroundIfNotEmptyToString(nL, prefix = "", postfix = "") {it.toPlainUmlMember(c, genProps) }}
                  {method} ${operations().joinSurroundIfNotEmptyToString(nL, prefix = "", postfix = "")
                  {it.toPlainOperUml(c,genOperations)}}
                }


            ${stopUml(startStopUml)}
        """
}


private fun stopUml(startStopUml: Boolean) = startStopUml.then("@enduml")

private fun startUml(startStopUml: Boolean) = startStopUml.then("@startuml")
private fun startNoteUml(startNoteUml: Boolean) = startNoteUml.then("note right:")


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

          ${props().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) { it.toPlainUmlMember(c, genProps = true) }}
## Operations

          ${operations().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) { it.toPlainOperUml(c, genOperations = true) }}

## PlantUml diagram
    ```plantuml

        ${toPlainUmlClassImpl(c, startStopUml = true)}
    ```
        """
}

fun <T : CommentI<*>> T.toPlainUml(c: GenerationContext, generateComments: Boolean, startNoteUml: Boolean = true): String =
        (generateComments && isNotEMPTY()).then {
            "${startNoteUml(startNoteUml)} ${name()}"
        }

fun <T : AttributeI<*>> T.toPlainUmlMember(c: GenerationContext, genProps: Boolean = true): String =
        (genProps && isNotEMPTY()).then {
            "${c.n(this)} : ${c.n(type())}"
        }

fun OperationI<*>.toPlainOperUml(c: GenerationContext, genOperations: Boolean = true): String = (genOperations && isNotEMPTY()).then {
    // "${it.name()}: ${it.type()}
    "${c.n(this)}"
}

fun <T : ConstructorI<*>> T.toPlainUmlConstructor(c: GenerationContext, genConstr: Boolean = true): String = (genConstr && isNotEMPTY()).then {
    "${c.n(this)}$nL --"
}

fun <T : CompilationUnitI<*>> T.toPlainUmlOpenclassImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                        api: String = LangDerivedKind.API,
                                                        itemName: String = c.n(this, derived),
                                                        dataClass: Boolean = this is BasicI<*> &&
                                                                superUnits().isEmpty() && superUnitFor().isEmpty(),
                                                        nonBlocking: Boolean = isNonBlocking(), genOpenClass: Boolean = true): String = (genOpenClass && isNotEMPTY()).then {
    "${(isOpen() && !dataClass).then("open ")}${dataClass.then("data ")}class $nL .."
}


fun <T : CompilationUnitI<*>> T.toPlainUmlClassImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                    generateComments: Boolean = false, startStopUml: Boolean = true): String {
    return """
           ${startUml(startStopUml)}
           package ${namespace()} <<Folder>> {
          ${toPlainUmlTemplateClassImpl(c, startStopUml = false)}
          ${doc().toPlainUml(c, generateComments)}
          }
        ${stopUml(startStopUml)}"""

}

fun <T : TypeI<*>> T.toPlainUmlExtends(c: GenerationContext, genExtend: Boolean = false): String = (genExtend && isNotEMPTY()).then {
    "  <|--  ${name()}"
}

fun <T : AttributeI<*>> T.toPlainUmlNotNative(c: GenerationContext, derived: String, genNative: Boolean = true, mutable: Boolean? = null): String = (genNative && isNotEMPTY()).then {
    "${c.n(name())}"
}

fun <T : CompilationUnitI<*>> T.toPlainUmlTemplateClassImpl(c: GenerationContext, generateComments: Boolean = false,
                                                            startStopUml: Boolean = false,
                                                            genOperations: Boolean = true,
                                                            genProps: Boolean = true,
                                                            genConstr: Boolean = false): String {
    return """
            ${startUml(startStopUml)}

                  title ${parent().name()}
                 ${isIfc().ifElse("interface ", "class")}  ${name()} {
                  {field}${toPlainUmlOpenclassImpl(c, genOpenClass = false)}
                  {field}${constructors().joinSurroundIfNotEmptyToString(nL, prefix = "", postfix = "") { it.toPlainUmlConstructor(c, genConstr) }}
                  {field} ** Properties **${nL} ${props().joinSurroundIfNotEmptyToString(nL, prefix = "", postfix = "") { it.toPlainUmlMember(c, genProps) }}
                  {method} ${operations().joinSurroundIfNotEmptyToString(nL, prefix = "", postfix = "")
    { it.toPlainOperUml(c, genOperations) }}
                   }
                   ${superUnit().isNotEMPTY().then { """${name()}  ${superUnit().toPlainUmlExtends(c, genExtend = true)}""" }}

            ${stopUml(startStopUml)}
        """
}


fun <T : CompilationUnitI<*>> T.toPlainUmlClassNative(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                      generateComments: Boolean = false, startStopUml: Boolean = true, genProps: Boolean = true): String {
    return """
        ${propsNoNativeType().isNotEmpty().then {
        """
        ${startUml(startStopUml)}

        class ${name()}
       ${propsNoNativeType().joinSurroundIfNotEmptyToString(nL, prefix = " ", postfix = "") {
            "${name()} --> ${it.toPlainUmlNotNative(c, derived)} : ${it.toPlainUmlNotNative(c, derived)}"
        }}

        ${stopUml(startStopUml)}"""
    }}
           """

}


private fun stopUml(startStopUml: Boolean) = startStopUml.then("@enduml")
private fun startUml(startStopUml: Boolean) = startStopUml.then("@startuml")
private fun startNoteUml(startNoteUml: Boolean) = startNoteUml.then("note top:")


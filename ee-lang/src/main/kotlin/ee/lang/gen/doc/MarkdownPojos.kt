package ee.lang.gen.doc

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.kt.k
import java.util.stream.Collectors

fun <T : CompilationUnitI<*>> T.toMarkdownClassImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL): String {
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

fun <T : AttributeI<*>> T.toPlainUmlSubClassMember(c: GenerationContext, genProps: Boolean = true): String =
        (genProps && isNotEMPTY()).then {
            "${c.n(this)} : ${c.n(type())}"
        }
fun OperationI<*>.toPlainOperUml(c: GenerationContext, genOperations: Boolean = true): String = (genOperations && isNotEMPTY()).then {
    // "${it.name()}: ${it.type()}
    "${c.n(this)}"
}

fun <T : AttributeI<*>> T.toPlainUmlNotNative(c: GenerationContext, genNative: Boolean = true): String = (genNative && isNotEMPTY()).then {
    "${c.n(name())}"
}
fun <T : AttributeI<*>> T.toPlainUmlNotNativeType(c: GenerationContext, genNative: Boolean = true): String = (genNative && isNotEMPTY()).then {
    if ("${c.n(type())}" == "Map") {
        "${c.n(type().isMulti().ifElse("${c.n(type().generics().first().name())}ey", "${c.n(type().generics().last().name())}alue"))} "
    }
    if ("${c.n(type())}" == "List") {"T_List"}
    if ("${c.n(type())}" == "Collection") {"T_Collection"}

        "${c.n(type())}"


}
fun <T : ItemI<*>> T.toKotlinPacketOperation(c: GenerationContext, genOperations: Boolean = true): String = (genOperations && isNotEMPTY()).then {
    "${c.n(name())}"
}


fun <T : CompilationUnitI<*>> T.toPlainUmlClassImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                    startStopUml: Boolean = true, genProps: Boolean = true, generateComments:Boolean=true, genOperations: Boolean =true, genNoNative:Boolean=true): String {
    return """
        ${startUml(startStopUml)}
        package ${namespace()}{
        ${isIfc().ifElse("interface ", "class")}  ${name()} {
        {method} ${nL} ${operations().joinSurroundIfNotEmptyToString(nL, prefix = " ", postfix = ""){" ${it.toKotlinPacketOperation(c,genOperations = true)}()"
        }}

        {field}  ${nL} ${props().joinSurroundIfNotEmptyToString(nL, prefix = "", postfix = "") { it.toPlainUmlMember(c, genProps) }}

        }
         ${doc().toPlainUml(c, generateComments)}
       ${propsNoNativeType(genNoNative = true).joinSurroundIfNotEmptyToString(nL, prefix = " ", postfix = "") {
            "${nL}${name()} -- ${it.toPlainUmlNotNativeType(c).isEmpty().then { """${it.toPlainUmlNotNative(c)}""" }} ${it.toPlainUmlNotNativeType(c,genOperations)} : ${it.toPlainUmlNotNative(c,genOperations)} ${nL}"

        }
    }
        ${stopUml(startStopUml)}
        """

}


fun <T : CompilationUnitI<*>> T.toPlainUmlClassDetails(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                       startStopUml: Boolean = true, genProps: Boolean = true,genproperty:Boolean=true, generateComments:Boolean=true, genOperations: Boolean =true, genNoNative:Boolean=true): String {

    return """
        ${startUml(startStopUml)}
        package ${namespace()}{
        ${isIfc().ifElse("interface ", "class")}  ${name()} {
        {method} ${nL} ${operations().joinSurroundIfNotEmptyToString(nL, prefix = " ", postfix = ""){" ${it.toKotlinPacketOperation(c,genOperations = true)}()"
    }}

        {field}  ${nL} ${props().joinSurroundIfNotEmptyToString(nL, prefix = "", postfix = "") { it.toPlainUmlMember(c, genProps) }}

        }
         ${doc().toPlainUml(c, generateComments)}
       ${propsNoNativeType(genproperty = true).joinSurroundIfNotEmptyToString(nL, prefix = " ", postfix = "") {
        "class  ${it.toPlainUmlNotNativeType(c).isEmpty().then { """${it.toPlainUmlNotNative(c)}""" }} ${it.toPlainUmlNotNativeType(c,genOperations)} { ${nL}{field}" +
                "  ${props().joinSurroundIfNotEmptyToString(nL, prefix = "", postfix = "") { it.toPlainUmlSubClassMember(c, genProps) }} ${nL}}"
    }
    }
       ${propsNoNativeType(genNoNative = true).joinSurroundIfNotEmptyToString(nL, prefix = " ", postfix = "") {
        "${nL}${name()} -- ${it.toPlainUmlNotNativeType(c).isEmpty().then { """${it.toPlainUmlNotNative(c)}""" }} ${it.toPlainUmlNotNativeType(c,genOperations)} : ${it.toPlainUmlNotNative(c,genOperations)} ${nL}"

    }
    }
        ${stopUml(startStopUml)}
        """

}




private fun stopUml(startStopUml: Boolean) = startStopUml.then("@enduml")
private fun startUml(startStopUml: Boolean) = startStopUml.then("@startuml")
private fun startNoteUml(startNoteUml: Boolean) = startNoteUml.then("note top:")




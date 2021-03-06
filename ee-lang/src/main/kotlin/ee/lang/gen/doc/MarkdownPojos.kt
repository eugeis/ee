package ee.lang.gen.doc

import ee.common.ext.*
import ee.lang.*

fun <T : CompilationUnitI<*>> T.toMarkdownClassImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL): String {
    return """
# ${parent().name()}

       ${parent().name()}is a module and artifact mcr-opcua

## ${namespace()}

          ${namespace()} is a package

##${isIfc().ifElse("interface ", "class")}  ${name()}
  ${doc().toPlainUml(c,generateComments= true)}


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
            "${startNoteUml(startNoteUml)} ${name()}  "
        }

fun <T : AttributeI<*>> T.toPlainUmlMember(c: GenerationContext, genProps: Boolean = true): String =
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
        "${c.n(type().isMulti().ifElse("${c.n(type().generics().first().name())}", "${c.n(type().generics().last().name())}alue"))} "
    }
    else if ("${c.n(type())}" == "List") {
        "${c.n(type())}"
    } else "${c.n(type())}"

}
fun <T : ItemI<*>> T.toKotlinPacketOperation(c: GenerationContext, genOperations: Boolean = true): String = (genOperations && isNotEMPTY()).then {
    "${c.n(name())}"
}

fun <T : CompilationUnitI<*>> T.toPlainUmlClassImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                    startStopUml: Boolean = true, genProps: Boolean = true, generateComments:Boolean=true, genOperations: Boolean =true, genNoNative:Boolean=true): String {
    return """
        ${startUml(startStopUml)}
${name().isNotEmpty().then { """
        ${isIfc().ifElse("interface ", "class")}  ${name()} {
        {method} ${nL} ${operations().joinSurroundIfNotEmptyToString(nL, prefix = " ", postfix = ""){" ${it.toKotlinPacketOperation(c,genOperations = true)}()"

    }}
        {field}  ${nL} ${props().joinSurroundIfNotEmptyToString(nL, prefix = "", postfix = "") { it.toPlainUmlMember(c, genProps) }}


        }
        """}}
         ${doc().toPlainUml(c, generateComments)}


          ${stopUml(startStopUml)}
        """

}


fun <T : CompilationUnitI<*>> T.toPlainUmlClassDetails(
        c: GenerationContext, derived: String = LangDerivedKind.IMPL,
        startStopUml: Boolean = true, genProps: Boolean = true, genproperty:Boolean=true, generateComments:Boolean=true,
        genOperations: Boolean =true, genNoNative:Boolean=true): String {

    val propTypes = propsNoNative().map { it.type() }.filterIsInstance(CompilationUnitI::class.java).toSet()

    return """
        ${startUml(startStopUml)}
        package ${namespace()}{
      ${name().isNotEmpty().then { """  ${isIfc().ifElse("interface ", "class")}  ${name()} {
        {field}  ${nL} ${props().joinSurroundIfNotEmptyToString(nL, prefix = "", postfix = "") { it.toPlainUmlMember(c, genProps) }}
        ${operations().isNotEmpty().then { """--
        {method} ${nL} ${operations().joinSurroundIfNotEmptyToString(nL, prefix = " ", postfix = ""){" ${it.toKotlinPacketOperation(c,genOperations = true)}()"}}
        """}}
        """}}
        }

         ${doc().toPlainUml(c, generateComments)}
       ${propTypes.joinSurroundIfNotEmptyToString(nL, prefix = " ", postfix = " ") {
        it.toPlainUmlClassImpl(c, derived, false)
    }}

       ${propsNoNative().joinSurroundIfNotEmptyToString(nL, prefix = " ", postfix = "") {

        "${nL} ${it.toPlainUmlNotNativeType(c).isNotEmpty().then { " ${name()} -- ${it.toPlainUmlNotNativeType(c,genOperations)} : ${it.toPlainUmlNotNative(c,genOperations)} ${nL}" }} "
    }
    }
        ${stopUml(startStopUml)}
        """

}
fun <T : CompilationUnitI<*>> T.toPlainUmlSuperClass(
        c: GenerationContext, derived: String = LangDerivedKind.IMPL,
        startStopUml: Boolean = true, genProps: Boolean = true, genproperty:Boolean=true, generateComments:Boolean=true,
        genOperations: Boolean =true, genNoNative:Boolean=true): String {
    return """
      ${ if (superUnit().name() == "@@EMPTY@@") {""}else "${startUml(startStopUml)}"}

              ${propsNoNative().joinSurroundIfNotEmptyToString(nL, prefix = " ", postfix = "") {
                if (superUnit().name()== "@@EMPTY@@") {""}else "${it.toPlainUmlNotNativeType(c).isNotEmpty().then { "${superUnit().name()} <|-- ${it.toPlainUmlNotNativeType(c, genOperations)} : ${it.toPlainUmlNotNative(c, genOperations)}"
        }}"

    }
    }
 ${ if (superUnit().name() == "@@EMPTY@@") {""}else "${stopUml(startStopUml)}"}
    """
}



private fun stopUml(startStopUml: Boolean) = startStopUml.then("@enduml")
private fun startUml(startStopUml: Boolean) = startStopUml.then("@startuml")
private fun startNoteUml(startNoteUml: Boolean) = startNoteUml.then("note top:")




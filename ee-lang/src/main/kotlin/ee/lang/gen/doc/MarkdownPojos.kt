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
  ${doc().toPlantUml(c,generateComments= true)}


## Properties

          ${props().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) { it.toPlantUmlMember(c, genProps = true) }}
## Operations

          ${operations().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) { it.toPlantOperUml(c, genOperations = true) }}

## PlantUml diagram
    ```plantuml

        ${toPlantUmlClassImpl(c, startStopUml = true)}
    ```
        """
}

fun <T : CommentI<*>> T.toPlantUml(c: GenerationContext, generateComments: Boolean, startNoteUml: Boolean = true): String =
        (generateComments && isNotEMPTY()).then {
            "${startNoteUmlTop(startNoteUml)} ${name()}  "
        }

fun <T : AttributeI<*>> T.toPlantUmlMember(c: GenerationContext, genProps: Boolean = true): String =
        (genProps && isNotEMPTY()).then {
            "${c.n(this)} : ${c.n(type())}"
        }


fun OperationI<*>.toPlantOperUml(c: GenerationContext, genOperations: Boolean = true): String = (genOperations && isNotEMPTY()).then {
    // "${it.name()}: ${it.type()}
    "${c.n(this)}"
}

fun <T : AttributeI<*>> T.toPlantUmlNotNative(c: GenerationContext, genNative: Boolean = true): String = (genNative && isNotEMPTY()).then {
    "${c.n(name())}"
}
fun <T : AttributeI<*>> T.toPlantUmlNotNativeType(c: GenerationContext, genNative: Boolean = true): String = (genNative && isNotEMPTY()).then {
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

fun <T : CompilationUnitI<*>> T.toPlantUmlClassImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                    startStopUml: Boolean = true, genProps: Boolean = true, generateComments:Boolean=true, genOperations: Boolean =true, genNoNative:Boolean=true): String {
    return """
        ${startUml(startStopUml)}
${name().isNotEmpty().then { """
        ${isIfc().ifElse("interface ", "class")}  ${name()} {
        {method} ${nL} ${operations().joinSurroundIfNotEmptyToString(nL, prefix = " ", postfix = ""){" ${it.toKotlinPacketOperation(c,genOperations = true)}()"

    }}
        {field}  ${nL} ${props().joinSurroundIfNotEmptyToString(nL, prefix = "", postfix = "") { it.toPlantUmlMember(c, genProps) }}


        }
        """}}
         ${doc().toPlantUml(c, generateComments)}


          ${stopUml(startStopUml)}
        """

}


fun <T : CompilationUnitI<*>> T.toPlantUmlClassDetails(
        c: GenerationContext, derived: String = LangDerivedKind.IMPL,
        startStopUml: Boolean = true, genProps: Boolean = true, genproperty:Boolean=true, generateComments:Boolean=true,
        genOperations: Boolean =true, genNoNative:Boolean=true): String {

    val propTypes = propsNoNative().map { it.type() }.filterIsInstance(CompilationUnitI::class.java).toSet()

    return """
        ${startUml(startStopUml)}
        package ${namespace()}{
      ${name().isNotEmpty().then { """  ${isIfc().ifElse("interface ", "class")}  ${name()} {
        {field}  ${nL} ${props().joinSurroundIfNotEmptyToString(nL, prefix = "", postfix = "") { it.toPlantUmlMember(c, genProps) }}
        ${operations().isNotEmpty().then { """--
        {method} ${nL} ${operations().joinSurroundIfNotEmptyToString(nL, prefix = " ", postfix = ""){" ${it.toKotlinPacketOperation(c,genOperations = true)}()"}}
        """}}
        """}}
        }

         ${doc().toPlantUml(c, generateComments)}
       ${propTypes.joinSurroundIfNotEmptyToString(nL, prefix = " ", postfix = " ") {
        it.toPlantUmlClassImpl(c, derived, false)
    }}

       ${propsNoNative().joinSurroundIfNotEmptyToString(nL, prefix = " ", postfix = "") {

        "${nL} ${it.toPlantUmlNotNativeType(c).isNotEmpty().then { " ${name()} -- ${it.toPlantUmlNotNativeType(c,genOperations)} : ${it.toPlantUmlNotNative(c,genOperations)} ${nL}" }} "
    }
    }
        ${stopUml(startStopUml)}
        """

}
fun <T : CompilationUnitI<*>> T.toPlantUmlSuperClass(
        c: GenerationContext, derived: String = LangDerivedKind.IMPL,
        startStopUml: Boolean = true, genProps: Boolean = true, genproperty:Boolean=true, generateComments:Boolean=true,
        genOperations: Boolean =true, genNoNative:Boolean=true): String {
    return """
      ${ if (superUnit().name() == "@@EMPTY@@") {""}else "${startUml(startStopUml)}"}

              ${propsNoNative().joinSurroundIfNotEmptyToString(nL, prefix = " ", postfix = "") {
                if (superUnit().name()== "@@EMPTY@@") {""}else "${it.toPlantUmlNotNativeType(c).isNotEmpty().then { "${superUnit().name()} <|-- ${it.toPlantUmlNotNativeType(c, genOperations)} : ${it.toPlantUmlNotNative(c, genOperations)}"
        }}"

    }
    }
 ${ if (superUnit().name() == "@@EMPTY@@") {""}else "${stopUml(startStopUml)}"}
    """
}



fun stopUml(startStopUml: Boolean) = startStopUml.then("@enduml")
fun startUml(startStopUml: Boolean) = startStopUml.then("@startuml")
fun startNoteUmlTop(startNoteUml: Boolean) = startNoteUml.then("note top:")
fun startNoteUmlRight(startNoteUml: Boolean) = startNoteUml.then("note right of")
fun endNote(stopNoteUml: Boolean) = stopNoteUml.then("end note")
fun componentPart(generatePart: Boolean) = generatePart.then("<<Component>>")
fun modulePart(generatePart: Boolean) = generatePart.then("<<Module>>")
fun basicPart(generatePart: Boolean) = generatePart.then("<<Basic>>")
var moduleComponentName = "package"
var entityComponentName = "entity"
var basicComponentName = "class"
var enumComponentName = "enum"




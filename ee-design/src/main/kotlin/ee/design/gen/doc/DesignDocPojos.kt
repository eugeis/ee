package ee.design.gen.doc

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.design.*
import ee.lang.*
import ee.lang.gen.doc.*
import ee.lang.gen.ts.toTypeScriptGenericTypes
import java.util.*

val nL3Tab = nL + tab + tab + tab
val nL2Tab = nL + tab + tab

fun <T : CompI<*>> T.toPlantUmlClassDiagramComp(c: GenerationContext, startStopUml: Boolean = true, componentName: String,
                                                componentPart: String, generateComponentPart: Boolean = true ): String {
    return """
${startUml(startStopUml)}
$componentName ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}$componentPart {
    ${this.modules().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toPlantUmlClassDiagramModule(c, moduleComponentName,modulePart(generateComponentPart), generateComponentPart) }}
}
${this.modules().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toPlantUmlClassDiagramRelation(c) }}
${stopUml(startStopUml)}"""
}

// TODO: REMOVE DUPLICATE IF GENERIC TYPE HAVE ALREADY APPEARED
fun <T : ModuleI<*>> T.toPlantUmlClassDiagramRelation(c: GenerationContext): String {
    return """${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toPlantUmlClassDiagramEntityRelation(c) }}${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toPlantUmlClassDiagramBasicRelation(c) }}"""
}

fun <T : EntityI<*>> T.toPlantUmlClassDiagramEntityRelation(c: GenerationContext): String {
    return """
' Relation From ${this.name()}
${this.props().checkEntityToEntityRelation(c)}${this.props().checkEntityToOtherRelation(c)}"""
}

fun <T : ListMultiHolder<AttributeI<*>>> T.checkEntityToEntityRelation(c: GenerationContext): String {
    return this.filter { it.type() is EntityI<*> || (it.type().generics().filter { genericType -> genericType.type() is EntityI<*> }).isNotEmpty() }.distinct().joinSurroundIfNotEmptyToString("") { nL + it.toPlantUmlClassDiagramGenerateRelationToEntity(c) }
}

fun <T : ListMultiHolder<AttributeI<*>>> T.checkEntityToOtherRelation(c: GenerationContext): String {
    return this.filter { it.type() is BasicI<*> || it.type() is EnumTypeI<*>
            || (it.type().name().contains("list", true)
            && (it.type().generics().filter { genericType -> genericType.type() is BasicI<*> || genericType.type() is EnumTypeI<*> }).isNotEmpty()) }.distinct().joinSurroundIfNotEmptyToString("") { nL + it.toPlantUmlClassDiagramGenerateRelation(c) }
}

fun <T : BasicI<*>> T.toPlantUmlClassDiagramBasicRelation(c: GenerationContext): String {
    return this.props().filter { it.type() is BasicI<*> || it.type() is EnumTypeI<*> || it.type() is EntityI<*>
            || (it.type().name().contains("list", true)
            && (it.type().generics().filter { genericType -> genericType.type() is BasicI<*> || genericType.type() is EnumTypeI<*> || genericType.type() is EntityI<*> }).isNotEmpty()) }.distinct().joinSurroundIfNotEmptyToString(nL) { it.toPlantUmlClassDiagramGenerateRelation(c) }
}

fun <T : ModuleI<*>> T.toPlantUmlClassDiagramModule(c: GenerationContext, componentName: String, componentPart: String, generateComponentPart: Boolean): String {
    return """
    $componentName ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}$componentPart {
        ${this.entities().generateEntitiesComponents(c)}${this.basics().generateBasicsComponents(c, generateComponentPart)}${this.enums().generateEnumsComponents(c)}
    }"""
}

fun <T : ListMultiHolder<EntityI<*>>> T.generateEntitiesComponents(c: GenerationContext): String {
    return this.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") {  it.toPlantUmlClassDiagramComponent(c, entityComponentName, "") }
}

fun <T : ListMultiHolder<BasicI<*>>> T.generateBasicsComponents(c: GenerationContext, generateComponentPart: Boolean): String {
    return this.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") {  it.toPlantUmlClassDiagramComponent(c, basicComponentName, basicPart(generateComponentPart)) }
}

fun <T : ListMultiHolder<EnumTypeI<*>>> T.generateEnumsComponents(c: GenerationContext): String {
    return this.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") {  it.toPlantUmlClassDiagramEnum(c, enumComponentName) }
}

fun <T : CompilationUnitI<*>> T.toPlantUmlClassDiagramComponent(c: GenerationContext, componentName: String, componentPart: String): String {
    return """
        $componentName ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}$componentPart {
            ${this.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL3Tab) { it.toPlantUmlClassDiagramGeneratePropAndType(c) }}
            ${operations().isNotEmpty().then { """
            {method} $nL ${operations().joinSurroundIfNotEmptyToString(""){ nL3Tab + "${it.toKotlinPacketOperation(c,genOperations = true)}()"}}
            """}}
        }
        ${this.props().filter { !it.doc().isEMPTY() }.joinSurroundIfNotEmptyToString(nL2Tab) { it.toPlantUmlClassDiagramGeneratePropDoc(c) }}"""
}

fun <T : EnumTypeI<*>> T.toPlantUmlClassDiagramEnum(c: GenerationContext, componentName: String): String {
    return """
        $componentName ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} {
            ${this.literals().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL3Tab) { it.toPlantUmlClassDiagramGenerateEnumProp(c) }}
        }
        ${this.literals().filter { !it.doc().isEMPTY() }.joinSurroundIfNotEmptyToString(nL2Tab) { it.toPlantUmlClassDiagramGenerateEnumDoc(c) }}"""
}


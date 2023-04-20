package ee.design.gen.doc

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.design.*
import ee.lang.*
import ee.lang.gen.doc.*
import ee.lang.gen.ts.toTypeScriptGenericTypes

val nL3Tab = nL + tab + tab + tab
val nL2Tab = nL + tab + tab

fun <T : CompI<*>> T.toPlantUmlClassDiagramComp(c: GenerationContext, startStopUml: Boolean = true, componentName: String,
                                                componentPart: String, generateComponentPart: Boolean = true ): String {
    return """
${startUml(startStopUml)}
$componentName ${this.name().capitalize()}$componentPart {
    ${this.modules().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toPlantUmlClassDiagramModule(c, moduleComponentName,modulePart(generateComponentPart), generateComponentPart) }}
}
${this.modules().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toPlantUmlClassDiagramRelation(c) }}
${stopUml(startStopUml)}"""
}

fun <T : ModuleI<*>> T.toPlantUmlClassDiagramRelation(c: GenerationContext): String {
    return """${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toPlantUmlClassDiagramEntityRelation(c) }}${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toPlantUmlClassDiagramBasicRelation(c) }}"""
}

fun <T : EntityI<*>> T.toPlantUmlClassDiagramEntityRelation(c: GenerationContext): String {
    return """
' Relation From ${this.name()}
${this.props().checkEntityToEntityRelation(c)}${this.props().checkEntityToOtherRelation(c)}"""
}

fun <T : ListMultiHolder<AttributeI<*>>> T.checkEntityToEntityRelation(c: GenerationContext): String {
    return this.filter { it.type() is EntityI<*> }.joinSurroundIfNotEmptyToString("") { nL + it.toPlantUmlClassDiagramGenerateRelationToEntity(c) }
}

fun <T : ListMultiHolder<AttributeI<*>>> T.checkEntityToOtherRelation(c: GenerationContext): String {
    return this.filter { it.type() is BasicI<*> || it.type() is EnumTypeI<*> || (it.type().name().contains("list", true) && !it.type().toTypeScriptGenericTypes(c, "", it).contains("date", true)) }.joinSurroundIfNotEmptyToString("") { nL + it.toPlantUmlClassDiagramGenerateRelation(c) }
}

fun <T : BasicI<*>> T.toPlantUmlClassDiagramBasicRelation(c: GenerationContext): String {
    return this.props().filter { it.type() is BasicI<*> || it.type() is EnumTypeI<*> || it.type() is EntityI<*> || it.type().name().contains("list", true) }.joinSurroundIfNotEmptyToString(nL) { it.toPlantUmlClassDiagramGenerateRelation(c) }
}

fun <T : ModuleI<*>> T.toPlantUmlClassDiagramModule(c: GenerationContext, componentName: String, componentPart: String, generateComponentPart: Boolean): String {
    return """
    $componentName ${this.name().capitalize()}$componentPart {
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
        $componentName ${this.name().capitalize()}$componentPart {
            ${this.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL3Tab) { it.toPlantUmlClassDiagramGeneratePropAndType(c) }}
            ${operations().isNotEmpty().then { """
            {method} $nL ${operations().joinSurroundIfNotEmptyToString(""){ nL3Tab + "${it.toKotlinPacketOperation(c,genOperations = true)}()"}}
            """}}
        }
        ${this.props().filter { !it.doc().isEMPTY() }.joinSurroundIfNotEmptyToString(nL2Tab) { it.toPlantUmlClassDiagramGeneratePropDoc(c) }}"""
}

fun <T : EnumTypeI<*>> T.toPlantUmlClassDiagramEnum(c: GenerationContext, componentName: String): String {
    return """
        $componentName ${this.name().capitalize()} {
            ${this.literals().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL3Tab) { it.toPlantUmlClassDiagramGenerateEnumProp(c) }}
        }
        ${this.literals().filter { !it.doc().isEMPTY() }.joinSurroundIfNotEmptyToString(nL2Tab) { it.toPlantUmlClassDiagramGenerateEnumDoc(c) }}"""
}


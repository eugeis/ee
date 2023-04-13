package ee.design.gen.puml.classdiagram

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.design.CompI
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*

val nL3Tab = nL + tab + tab + tab
val nL2Tab = nL + tab + tab

fun <T : CompI<*>> T.toCdComp(c: GenerationContext): String {
    return """
@startuml
package ${this.name().capitalize()}<<Component>> {
    ${this.modules().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toCdModule(c) }}
}
${this.modules().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toCdRelation(c) }}
@enduml"""
}

fun <T : ModuleI<*>> T.toCdRelation(c: GenerationContext): String {
    return """
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toCdEntityRelation(c) }}
${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toCdBasicRelation(c) }}"""
}

fun <T : EntityI<*>> T.toCdEntityRelation(c: GenerationContext): String {
    return """
${this.props().filter { it.type() is EntityI<*> }.joinSurroundIfNotEmptyToString(nL) { it.toCdGenerateRelationToEntity(c) }}
${this.props().filter { it.type() is BasicI<*> || it.type() is EnumTypeI<*> || it.type().name().contains("list", true) }.joinSurroundIfNotEmptyToString(nL) { it.toCdGenerateRelation(c) }}"""
}

fun <T : BasicI<*>> T.toCdBasicRelation(c: GenerationContext): String {
    return """
${this.props().filter { it.type() is BasicI<*> || it.type() is EnumTypeI<*> || it.type() is EntityI<*> || it.type().name().contains("list", true) }.joinSurroundIfNotEmptyToString(nL) { it.toCdGenerateRelation(c) }}"""
}

fun <T : ModuleI<*>> T.toCdModule(c: GenerationContext): String {
    return """
    package ${this.name().capitalize()}<<Module>> {
        ${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toCdEntity(c) }}
        ${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toCdBasic(c) }}
        ${this.enums().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toCdEnum(c) }}
    }"""
}

fun <T : EntityI<*>> T.toCdEntity(c: GenerationContext): String {
    return """
        entity ${this.name().capitalize()} {
            ${this.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL3Tab) { it.toCdGeneratePropAndType(c) }}
            ${this.findBys().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL3Tab) { it.toCdGenerateFindByMethods(c) }}
            ${this.createBys().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL3Tab) { it.toCdGenerateCreateByMethods(c) }}
            ${this.updateBys().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL3Tab) { it.toCdGenerateUpdateByMethods(c) }}
        }
        ${this.props().filter { !it.doc().isEMPTY() }.joinSurroundIfNotEmptyToString(nL2Tab) { it.toCdGeneratePropDoc(c) }}"""
}

fun <T : BasicI<*>> T.toCdBasic(c: GenerationContext): String {
    return """
        class ${this.name().capitalize()}<<Basic>> {
            ${this.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL3Tab) { it.toCdGeneratePropAndType(c) }}
        }
        ${this.props().filter { !it.doc().isEMPTY() }.joinSurroundIfNotEmptyToString(nL2Tab) { it.toCdGeneratePropDoc(c) }}"""
}

fun <T : EnumTypeI<*>> T.toCdEnum(c: GenerationContext): String {
    return """
        enum ${this.name().capitalize()} {
            ${this.literals().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL3Tab) { it.toCdGenerateEnumProp(c) }}
        }
        ${this.literals().filter { !it.doc().isEMPTY() }.joinSurroundIfNotEmptyToString(nL2Tab) { it.toCdGenerateEnumDoc(c) }}"""
}

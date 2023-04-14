package ee.design.gen.puml.classdiagram

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.design.*
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
    return """${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toCdEntityRelation(c) }}${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toCdBasicRelation(c) }}"""
}

fun <T : EntityI<*>> T.toCdEntityRelation(c: GenerationContext): String {
    return """
' Relation From ${this.name()}
${this.props().checkEntityToEntityRelation(c)}${this.props().checkEntityToOtherRelation(c)}"""
}

fun <T : ListMultiHolder<AttributeI<*>>> T.checkEntityToEntityRelation(c: GenerationContext): String {
    return this.filter { it.type() is EntityI<*> }.joinSurroundIfNotEmptyToString("") { nL + it.toCdGenerateRelationToEntity(c) }
}

fun <T : ListMultiHolder<AttributeI<*>>> T.checkEntityToOtherRelation(c: GenerationContext): String {
    return this.filter { it.type() is BasicI<*> || it.type() is EnumTypeI<*> || it.type().name().contains("list", true) }.joinSurroundIfNotEmptyToString("") { nL + it.toCdGenerateRelation(c) }
}

fun <T : BasicI<*>> T.toCdBasicRelation(c: GenerationContext): String {
    return this.props().filter { it.type() is BasicI<*> || it.type() is EnumTypeI<*> || it.type() is EntityI<*> || it.type().name().contains("list", true) }.joinSurroundIfNotEmptyToString(nL) { it.toCdGenerateRelation(c) }
}

fun <T : ModuleI<*>> T.toCdModule(c: GenerationContext): String {
    return """
    package ${this.name().capitalize()}<<Module>> {
        ${this.entities().checkEntitiesComponent(c)}${this.basics().checkBasicsComponent(c)}${this.enums().checkEnumsComponent(c)}
    }"""
}

fun <T : ListMultiHolder<EntityI<*>>> T.checkEntitiesComponent(c: GenerationContext): String {
    return this.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") {  it.toCdEntity(c) }
}

fun <T : ListMultiHolder<BasicI<*>>> T.checkBasicsComponent(c: GenerationContext): String {
    return this.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") {  it.toCdBasic(c) }
}

fun <T : ListMultiHolder<EnumTypeI<*>>> T.checkEnumsComponent(c: GenerationContext): String {
    return this.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") {  it.toCdEnum(c) }
}

fun <T : EntityI<*>> T.toCdEntity(c: GenerationContext): String {
    return """
        entity ${this.name().capitalize()} {
            ${this.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL3Tab) { it.toCdGeneratePropAndType(c) }}${this.findBys().checkFindByMethod(c)}${this.createBys().checkCreateByMethod(c)}${this.updateBys().checkUpdateByMethod(c)}
        }
        ${this.props().filter { !it.doc().isEMPTY() }.joinSurroundIfNotEmptyToString(nL2Tab) { it.toCdGeneratePropDoc(c) }}"""
}

fun<T : ListMultiHolder<FindByI<*>>> T.checkFindByMethod(c: GenerationContext): String {
    return this.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") { nL3Tab + it.toCdGenerateFindByMethods(c) }
}

fun<T : ListMultiHolder<CreateByI<*>>> T.checkCreateByMethod(c: GenerationContext): String {
    return this.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") { nL3Tab + it.toCdGenerateCreateByMethods(c) }
}

fun<T : ListMultiHolder<UpdateByI<*>>> T.checkUpdateByMethod(c: GenerationContext): String {
    return this.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") { nL3Tab + it.toCdGenerateUpdateByMethods(c) }
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

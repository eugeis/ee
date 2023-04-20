package ee.design.gen.puml.classdiagram

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.design.*
import ee.lang.*
import ee.lang.gen.ts.toTypeScriptGenericTypes

val nL3Tab = nL + tab + tab + tab
val nL2Tab = nL + tab + tab

fun <T : CompI<*>> T.toPumlCdComp(c: GenerationContext): String {
    return """
@startuml
package ${this.name().capitalize()}<<Component>> {
    ${this.modules().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toPumlCdModule(c) }}
}
${this.modules().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toPumlCdRelation(c) }}
@enduml"""
}

fun <T : ModuleI<*>> T.toPumlCdRelation(c: GenerationContext): String {
    return """${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toPumlCdEntityRelation(c) }}${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { it.toPumlCdBasicRelation(c) }}"""
}

fun <T : EntityI<*>> T.toPumlCdEntityRelation(c: GenerationContext): String {
    return """
' Relation From ${this.name()}
${this.props().checkEntityToEntityRelation(c)}${this.props().checkEntityToOtherRelation(c)}"""
}

fun <T : ListMultiHolder<AttributeI<*>>> T.checkEntityToEntityRelation(c: GenerationContext): String {
    return this.filter { it.type() is EntityI<*> }.joinSurroundIfNotEmptyToString("") { nL + it.toPumlCdGenerateRelationToEntity(c) }
}

fun <T : ListMultiHolder<AttributeI<*>>> T.checkEntityToOtherRelation(c: GenerationContext): String {
    return this.filter { it.type() is BasicI<*> || it.type() is EnumTypeI<*> || (it.type().name().contains("list", true) && !it.type().toTypeScriptGenericTypes(c, "", it).contains("date", true)) }.joinSurroundIfNotEmptyToString("") { nL + it.toPumlCdGenerateRelation(c) }
}

fun <T : BasicI<*>> T.toPumlCdBasicRelation(c: GenerationContext): String {
    return this.props().filter { it.type() is BasicI<*> || it.type() is EnumTypeI<*> || it.type() is EntityI<*> || it.type().name().contains("list", true) }.joinSurroundIfNotEmptyToString(nL) { it.toPumlCdGenerateRelation(c) }
}

fun <T : ModuleI<*>> T.toPumlCdModule(c: GenerationContext): String {
    return """
    package ${this.name().capitalize()}<<Module>> {
        ${this.entities().checkEntitiesComponent(c)}${this.basics().checkBasicsComponent(c)}${this.enums().checkEnumsComponent(c)}
    }"""
}

fun <T : ListMultiHolder<EntityI<*>>> T.checkEntitiesComponent(c: GenerationContext): String {
    return this.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") {  it.toPumlCdEntity(c) }
}

fun <T : ListMultiHolder<BasicI<*>>> T.checkBasicsComponent(c: GenerationContext): String {
    return this.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") {  it.toPumlCdBasic(c) }
}

fun <T : ListMultiHolder<EnumTypeI<*>>> T.checkEnumsComponent(c: GenerationContext): String {
    return this.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") {  it.toPumlCdEnum(c) }
}

fun <T : EntityI<*>> T.toPumlCdEntity(c: GenerationContext): String {
    return """
        entity ${this.name().capitalize()} {
            ${this.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL3Tab) { it.toPumlCdGeneratePropAndType(c) }}${this.findBys().checkFindByMethod(c)}${this.createBys().checkCreateByMethod(c)}${this.updateBys().checkUpdateByMethod(c)}
        }
        ${this.props().filter { !it.doc().isEMPTY() }.joinSurroundIfNotEmptyToString(nL2Tab) { it.toPumlCdGeneratePropDoc(c) }}"""
}

fun<T : ListMultiHolder<FindByI<*>>> T.checkFindByMethod(c: GenerationContext): String {
    return this.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") { nL3Tab + it.toPumlCdGenerateFindByMethods(c) }
}

fun<T : ListMultiHolder<CreateByI<*>>> T.checkCreateByMethod(c: GenerationContext): String {
    return this.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") { nL3Tab + it.toPumlCdGenerateCreateByMethods(c) }
}

fun<T : ListMultiHolder<UpdateByI<*>>> T.checkUpdateByMethod(c: GenerationContext): String {
    return this.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") { nL3Tab + it.toPumlCdGenerateUpdateByMethods(c) }
}

fun <T : BasicI<*>> T.toPumlCdBasic(c: GenerationContext): String {
    return """
        class ${this.name().capitalize()}<<Basic>> {
            ${this.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL3Tab) { it.toPumlCdGeneratePropAndType(c) }}
        }
        ${this.props().filter { !it.doc().isEMPTY() }.joinSurroundIfNotEmptyToString(nL2Tab) { it.toPumlCdGeneratePropDoc(c) }}"""
}

fun <T : EnumTypeI<*>> T.toPumlCdEnum(c: GenerationContext): String {
    return """
        enum ${this.name().capitalize()} {
            ${this.literals().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL3Tab) { it.toPumlCdGenerateEnumProp(c) }}
        }
        ${this.literals().filter { !it.doc().isEMPTY() }.joinSurroundIfNotEmptyToString(nL2Tab) { it.toPumlCdGenerateEnumDoc(c) }}"""
}

package ee.design.gen.puml.classdiagram

import ee.design.CreateByI
import ee.design.FindByI
import ee.design.UpdateByI
import ee.lang.*
import ee.lang.gen.ts.toTypeScriptGenericTypes

fun <T : AttributeI<*>> T.toCdGeneratePropAndType(c: GenerationContext): String =
    """${this.name()} : ${if (!this.type().name().equals("list", true)) {this.type().name()} else {"listOf" + this.type().toTypeScriptGenericTypes(c, "", this)}}"""

fun <T : AttributeI<*>> T.toCdGenerateRelation(c: GenerationContext): String =
    """${if (!this.type().name().equals("list", true)) {this.type().name()} else {this.type().toTypeScriptGenericTypes(c, "", this).replace("<", "").replace(">", "")}} "1..*" --o "1" ${this.parent().name()} : contains""".trimIndent()

fun <T : AttributeI<*>> T.toCdGenerateRelationToEntity(c: GenerationContext): String =
    """${this.type().name()} <.. ${this.parent().name()} : depends on""".trimMargin()

fun <T : AttributeI<*>> T.toCdGeneratePropDoc(c: GenerationContext): String =
    """note right of ${this.parent().name()}::${this.name()}
            ${this.doc().name()}
        end note
    """

fun <T : LiteralI<*>> T.toCdGenerateEnumProp(c: GenerationContext): String =
    this.name()

fun <T : LiteralI<*>> T.toCdGenerateEnumDoc(c: GenerationContext): String =
    """note right of ${this.parent().name()}::${this.name()}
            ${this.doc().name()}
        end note
    """

fun <T : FindByI<*>> T.toCdGenerateFindByMethods(c: GenerationContext): String =
    """{method} ${this.name()} : findBy()"""

fun <T : CreateByI<*>> T.toCdGenerateCreateByMethods(c: GenerationContext): String =
    """{method} ${this.name()} : createBy()"""

fun <T : UpdateByI<*>> T.toCdGenerateUpdateByMethods(c: GenerationContext): String =
    """{method} ${this.name()} : updateBy()"""

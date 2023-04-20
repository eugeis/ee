package ee.design.gen.puml.classdiagram

import ee.design.CreateByI
import ee.design.FindByI
import ee.design.UpdateByI
import ee.lang.*
import ee.lang.gen.ts.toTypeScriptGenericTypes

fun <T : AttributeI<*>> T.toPumlCdGeneratePropAndType(c: GenerationContext): String =
    """${this.name()} : ${if (!this.type().name().equals("list", true)) {this.type().name()} else {"listOf" + this.type().toTypeScriptGenericTypes(c, "", this)}}"""

fun <T : AttributeI<*>> T.toPumlCdGenerateRelation(c: GenerationContext): String =
    """${if (!this.type().name().equals("list", true)) {this.type().name()} else {this.type().toTypeScriptGenericTypes(c, "", this).replace("<", "").replace(">", "")}} "1..*" --o "1" ${this.parent().name()} : contains""".trimIndent()

fun <T : AttributeI<*>> T.toPumlCdGenerateRelationToEntity(c: GenerationContext): String =
    """${this.type().name()} <.. ${this.parent().name()} : depends on""".trimMargin()

fun <T : AttributeI<*>> T.toPumlCdGeneratePropDoc(c: GenerationContext): String =
    """note right of ${this.parent().name()}::${this.name()}
            ${this.doc().name()}
        end note
    """

fun <T : LiteralI<*>> T.toPumlCdGenerateEnumProp(c: GenerationContext): String =
    this.name()

fun <T : LiteralI<*>> T.toPumlCdGenerateEnumDoc(c: GenerationContext): String =
    """note right of ${this.parent().name()}::${this.name()}
            ${this.doc().name()}
        end note
    """

fun <T : FindByI<*>> T.toPumlCdGenerateFindByMethods(c: GenerationContext): String =
    """{method} ${this.name()} : findBy()"""

fun <T : CreateByI<*>> T.toPumlCdGenerateCreateByMethods(c: GenerationContext): String =
    """{method} ${this.name()} : createBy()"""

fun <T : UpdateByI<*>> T.toPumlCdGenerateUpdateByMethods(c: GenerationContext): String =
    """{method} ${this.name()} : updateBy()"""

package ee.design.gen.doc

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.lang.*
import ee.lang.gen.doc.endNote
import ee.lang.gen.doc.startNoteUmlRight
import ee.lang.gen.ts.toTypeScriptGenericTypes

fun <T : AttributeI<*>> T.toPlantUmlClassDiagramGeneratePropAndType(c: GenerationContext): String =
    """${this.name()} : ${if (!this.type().name().equals("list", true)) {this.type().name()} else {"listOf" + this.type().toTypeScriptGenericTypes(c, "", this) }}"""

fun <T : AttributeI<*>> T.toPlantUmlClassDiagramGenerateRelation(c: GenerationContext): String =
    """${if (!this.type().name().equals("list", true)) {this.type().name()} else {this.type().toTypeScriptGenericTypes(c, "", this).replace("<", "").replace(">", "")}} "1..*" --o "1" ${this.parent().name()} : contains"""

fun <T : AttributeI<*>> T.toPlantUmlClassDiagramGenerateRelationToEntity(c: GenerationContext): String =
    """${if (!this.type().name().equals("list", true)) {this.type().name()} else {this.type().generics().filter { !it.isEMPTY() }.first().type().name()}} <.. ${this.parent().name()} : depends on"""

fun <T : AttributeI<*>> T.toPlantUmlClassDiagramGeneratePropDoc(c: GenerationContext, generateNote: Boolean = true): String =
    """${startNoteUmlRight(generateNote)} ${this.parent().name()}::${this.name()}
            ${this.doc().name()}
        ${endNote(generateNote)}
    """

fun <T : LiteralI<*>> T.toPlantUmlClassDiagramGenerateEnumProp(c: GenerationContext): String =
    this.name()

fun <T : LiteralI<*>> T.toPlantUmlClassDiagramGenerateEnumDoc(c: GenerationContext, generateNote: Boolean = true): String =
    """${startNoteUmlRight(generateNote)} ${this.parent().name()}::${this.name()}
            ${this.doc().name()}
        ${endNote(generateNote)}
    """

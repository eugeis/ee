package ee.design.comp.gen.sql

import ee.common.ext.ifElse
import ee.common.ext.toSql
import ee.design.comp.Basic
import ee.design.comp.Entity
import ee.design.*
import ee.design.gen.sql.*

fun <T : Attribute> T.toSqlFkColumnName(context: SqlContext, relationUniqueType: Boolean = true): String {
    val complexType = type
    if (complexType is Entity) {
        return relationUniqueType.ifElse({ complexType.toSqlFkColumnName(context) }, { "${toSql(context)}_${complexType._id?.toSql(context)}}" })
    } else {
        return toSql(context)
    }
}

fun <T : Entity> T.toSqlColumnNamePrefix(context: SqlContext): String {
    return "${toSql(context).decapitalize()}_"
}

fun <T : Entity> T.toSqlFkColumnName(context: SqlContext): String {
    return "${toSqlColumnNamePrefix(context)}${_id?.toSql(context)}"
}

fun <T : Entity> T.toSqlFkColumnDef(context: SqlContext, indent: String = "", namePrefix: String = "", commentPrefix: String = ""): String {
    return "${_id?.toSqlColumnDef(context, indent, namePrefix, commentPrefix)}"
}

fun <T : Attribute> T.toSqlColumnDef(context: SqlContext, indent: String = "", namePrefix: String = "", commentPrefix: String = "", relationUniqueType: Boolean = true): String {
    val complexType = type
    if (complexType is Basic) {
        return "${toSqlComment(context, indent, commentPrefix)}${complexType.toSqlColumnDef(context, indent, toSqlColumnNamePrefix(context), "$commentPrefix$name.")}"
    } else if (complexType is Entity) {
        return "${toSqlComment(context, indent, commentPrefix)}${complexType.toSqlFkColumnDef(context, indent, relationUniqueType.ifElse({ complexType.toSqlColumnNamePrefix(context) }, { toSqlColumnNamePrefix(context) }), "$commentPrefix$name.")}"
    } else {
        return "$indent$namePrefix${toSql(context)}${toSqlTypeDef(context)}${toSqlComment(context, indent, commentPrefix)}"
    }
}

fun List<Attribute>.toSqlColumnDef(context: SqlContext, indent: String, namePrefix: String = "", commentPrefix: String = ""): String {
    return "${joinToString(",$nL") { it.toSqlColumnDef(context, indent, namePrefix, commentPrefix) }}"
}

fun <T : Attribute> T.toSqlRelationTableName(context: SqlContext, parent: Entity, relationUniqueType: Boolean = true): String {
    return "${parent.toSql(context)}_${(type is CompilationUnit).ifElse({ type.toSql(context) }, { name.toSql() })}"
}

fun <T : Attribute> T.toSqlCreateRelationTable(context: SqlContext, indent: String = "", parent: Entity): String {
    val newIndent = "$indent$tab"
    return return """$nL${toSqlComment(context, indent, "", parent)}${indent}CREATE TABLE IF NOT EXISTS ${toSqlRelationTableName(context, parent)} (
${parent.toSqlFkColumnDef(context, newIndent, parent?.toSqlColumnNamePrefix(context), "${parent.name}.")},
${toSqlColumnDef(context, newIndent, "")},
${newIndent}PRIMARY KEY (${parent.toSqlFkColumnName(context)}, ${toSqlFkColumnName(context)}));
"""
}

fun <T : Attribute> T.toSqlDropRelationTable(context: SqlContext, indent: String = "", parent: Entity): String {
    return """$nL${toSqlComment(context, indent, "", parent)}${indent}DROP TABLE IF EXISTS ${toSqlRelationTableName(context, parent)};
"""
}

fun Entity.toSqlCreateRelationTables(context: SqlContext, indent: String = ""): String {
    if (virtual) return ""
    return """${propsAll.filter { it.type.multi() }.joinToString("${nL}") { it.toSqlCreateRelationTable(context, indent, this) }}"""
}

fun <T : Attribute> T.toSqlCreateForeignKey(context: SqlContext, indent: String = "", parent: Entity): String {
    val newIndent = "$indent${tab}"
    return """$nL${toSqlComment(context, indent, "", parent)}${indent}ALTER TABLE ${parent.toSql(context)}
${newIndent}ADD FOREIGN KEY (${toSqlFkColumnName(context)})
${newIndent}REFERENCES ${type.toSql(context)} (${(type as Entity)._id?.toSql(context)});
"""
}

fun Entity.toSqlCreateForeignKeys(context: SqlContext, indent: String = ""): String {
    if (virtual) return ""
    return """${propsAll.filter { !it.type.multi() && it.type is Entity }.joinToString("${nL}") { it.toSqlCreateForeignKey(context, indent, this) }}"""
}

fun <T : Attribute> T.toSqlCreateIndex(context: SqlContext, indent: String = "", parent: Entity): String {
    val newIndent = "$indent${tab}"
    return """$nL${toSqlComment(context, indent, "", parent)}${indent}ALTER TABLE ${parent.toSql(context)}
${newIndent}ADD INDEX (${toSqlFkColumnName(context)});
"""
}

fun CompilationUnit.toSqlColumnDef(context: SqlContext, indent: String = "", namePrefix: String = "", commentPrefix: String = ""): String {
    return "${propsAll.filter { !it.type.multi() }.toSqlColumnDef(context, indent, namePrefix, commentPrefix)}"
}

fun Entity.toSqlCreateIndexes(context: SqlContext, indent: String = ""): String {
    if (virtual) return ""
    return """${propsAll.filter { !it.type.multi() && it.type is EnumType }.joinToString("${nL}") { it.toSqlCreateIndex(context, indent, this) }}"""
}

fun Entity.toSqlCreateTable(context: SqlContext, indent: String = ""): String {
    if (virtual) return ""
    val newIndent = "$indent${tab}"
    return """${toSqlComment(context, indent)}CREATE TABLE IF NOT EXISTS ${toSql(context)} (
${toSqlColumnDef(context, newIndent)},
${newIndent}PRIMARY KEY (${_id?.toSql(context)}));
${toSqlCreateIndexes(context, indent)}
${toSqlCreateRelationTables(context, indent)}"""
}

fun Entity.toSqlDropRelationTables(context: SqlContext, indent: String = ""): String {
    if (virtual) return ""
    return """${propsAll.filter { it.type.multi() }.joinToString("${nL}") { it.toSqlDropRelationTable(context, indent, this) }}"""
}

fun Entity.toSqlDropTable(context: SqlContext, indent: String = ""): String {
    if (virtual) return ""
    return """${toSqlComment(context, indent)}DROP TABLE IF EXISTS ${toSql(context)};
${toSqlDropRelationTables(context, indent)}"""
}


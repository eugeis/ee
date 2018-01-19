package ee.design.gen.sql

import ee.common.ext.*
import ee.design.*

class SqlContext : GenerationContext {

    constructor(namespace: String = "", header: String = "", footer: String = "") : super(namespace, header, footer)

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${namespace.isNotEmpty().ifElse("use ${namespace}e;", "")}$nL$content"
    }

    override fun n(item: TypeIfc, indent: String): String {
        return "$indent${types.addReturn(item).name.toSql()}"
    }
}

class Sql : StructureUnit("Sql") {
    companion object Core : StructureUnit("") {
        val List = type()
    }
}

fun <T : ElementIfc> T.toSql(context: SqlContext, indent: String = ""): String {
    if (this is TextElement) {
        return "$indent$text"
    } else if (this is Literal) {
        return "$indent${name.toUnderscoredUpperCase()}"
    } else {
        return "$indent$name"
    }
}

fun <T : TextElement> T.toSql(context: SqlContext, indent: String = ""): String {
    return "$indent$text"
}

fun <T : Comment> T.toSql(context: SqlContext, indent: String = ""): String {
    if (children.size == 1 && !children.first().toSql(context).contains("\n")) {
        return "$indent/* ${children.first().toSql(context)} */${nL}"
    } else {
        val newIndent = "$indent${tab}"
        return "/*$nL${children.joinToString(nL) { it.toSql(context, newIndent) }}$indent*/${nL}"
    }
}

fun <T : TypeD> T.toSqlComment(context: SqlContext, indent: String = ""): String {
    return when (doc) {
        null          -> "$indent#$name${nL}"
        Comment.EMPTY -> "$indent#$name${nL}"
        else          -> "${doc?.toSql(context, indent)}"
    }
}

fun <T : TypeD> T.toSql(context: SqlContext): String {
    return when (this) {
        td.String  -> "VARCHAR(255)"
        td.Text    -> "LONGTEXT"
        td.Boolean -> "BOOLEAN"
        td.Integer -> "INTEGER"
        td.Long    -> "BIGINT"
        td.Float   -> "FLOAT(7,4)"
        td.Date    -> "DATETIME"
        else       -> {
            if (this is EnumType) "VARCHAR(255)" else context.n(this)
        }
    }
}

fun <T : TypeD> T.toSqlValue(context: SqlContext): String {
    if (this is NativeType) {
        return when (this) {
            td.String  -> "\"\""
            td.Text    -> "\"\""
            td.Boolean -> "false"
            td.Integer -> "0"
            td.Long    -> "0l"
            td.Float   -> "0f"
            td.Date    -> "${td.Date.toSql(context)}()"
            else       -> "\"\""
        }
    } else if (this is ExternalType) {
        return "null"
    } else if (this is EnumTypeD) {
        return "${context.n(this)}.${this.literals.first().toSql(context)}"
    } else if (this is CompilationUnit) {
        return "${context.n(this)}.EMPTY"
    } else {
        return "null"
    }
}

fun <T : Attribute> T.toSqlTypeDef(context: SqlContext): String {
    return " ${type.toSql(context)}"
}

fun <T : Attribute> T.toSql(context: SqlContext): String {
    return "${name.toSql()}"
}


fun <T : Attribute> T.toSqlComment(context: SqlContext, indent: String = "", commentPrefix: String = "",
    parent: CompilationUnitD? = null): String {
    if (doc == null && doc != Comment.EMPTY) {
        if (parent == null) {
            if (type == null || type is NativeType) {
                return " COMMENT '$commentPrefix$name'"
            } else if (type is EnumType) {
                return " COMMENT '$commentPrefix$name(${type.name})'"
            } else {
                return "$indent#$commentPrefix$name(${type.name})${nL}"
            }
        } else {
            return "$indent#$commentPrefix${parent.name}.$name(${type.name})${nL}"
        }
    } else {
        return "${doc?.toSql(context, indent)}"
    }
}

fun <T : Attribute> T.toSqlColumnNamePrefix(context: SqlContext): String {
    return "${toSql(context)}_"
}

fun <T : Attribute> T.toSqlAssign(context: SqlContext, indent: String = ""): String {
    return "${indent}this.$name = $name"
}

fun <T : Attribute> T.toSqlCall(context: SqlContext, indent: String = ""): String {
    return "$name"
}

fun <T : Attribute> T.toSqlValueEmpty(context: SqlContext, indent: String = ""): String {
    if (nullable) {
        return "null"
    } else if (type.multi && mutable) {
        return "arrayListOf()"
    } else if (type.multi) {
        return "emptyList()"
    } else {
        return "${type.toSqlValue(context)}"
    }
}

fun List<Attribute>.toSqlCall(context: SqlContext, indent: String): String {
    return "${joinWrappedToString(", ", indent) { it.toSqlCall(context, indent) }}"
}

fun List<Attribute>.toSqlValueEmpty(context: SqlContext, indent: String): String {
    return "${joinWrappedToString(", ", indent) { it.toSqlValueEmpty(context, indent) }}"
}

fun <T : LogicUnit> T.toSqlCall(context: SqlContext, indent: String = "", name: String = ""): String {
    return "$name(${params.toSqlCall(context, indent)})"
}

fun <T : LogicUnit> T.toSqlValueEmpty(context: SqlContext, indent: String = "", name: String = ""): String {
    return "$name(${params.toSqlValueEmpty(context, indent)})"
}

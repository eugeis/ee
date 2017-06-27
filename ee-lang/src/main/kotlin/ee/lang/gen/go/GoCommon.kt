package ee.lang.gen.go

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.java.j
import ee.lang.gen.kt.k

fun <T : TypeI> T.toGoEmpty(c: GenerationContext, derived: String, attr: AttributeI): String {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String, n.Text -> """"""""
        n.Boolean -> "false"
        n.Int -> "0"
        n.Long -> "0"
        n.Float -> "0"
        n.Date -> "${c.n(g.time.Now)}()"
        n.Path -> "${c.n(j.nio.file.Paths)}.get(\"\")"
        n.Blob -> "make([]byte,0)"
        n.Void -> ""
        n.Error -> """error.New("")"""
        n.Exception -> """error.New("")"""
        n.Url -> "${c.n(j.net.URL)}(\"\")"
        n.List -> "make([]${generics()[0].toGo(c, derived)}, 0)"
        n.Map -> "make(map(${generics()[0].toGo(c, derived)})${generics()[1].toGo(c, derived)})"
        else -> {
            if (baseType is Literal) {
                "${(baseType.findParent(EnumTypeI::class.java) as EnumTypeI).toGo(c, derived, attr)}.${baseType.toGo()}"
            } else {
                (this.parent() == n).ifElse("\"\"", { "${c.n(this, derived)}.EMPTY" })
            }
        }
    }
}

fun AttributeI.toGoInit(): String {
    return """${this.name()}: ${this.value()}"""
}

fun AttributeI.toGoInitCall(): String {
    return "${nameForMember()}: ${name()}"
}

fun <T : AttributeI> T.toGoEmpty(c: GenerationContext, derived: String): String {
    return type().toGoEmpty(c, derived, this)
}

fun <T : AttributeI> T.toGoTypeDef(c: GenerationContext, api: String): String {
    return """${type().toGo(c, api, this)}"""
}

fun <T : CompilationUnitI> T.toGoExtends(c: GenerationContext, derived: String, api: String): String {
    if (superUnit().isNotEMPTY() && derived != api) {
        return " : ${c.n(superUnit(), derived)}, ${c.n(this, api)}"
    } else if (superUnit().isNotEMPTY()) {
        return " : ${c.n(superUnit(), derived)}"
    } else if (derived != api) {
        return " : ${c.n(this, api)}"
    } else {
        return ""
    }
}

fun <T : TypeI> T.toGoIfNative(c: GenerationContext, derived: String, pointer: Boolean = true): String? {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String -> "string"
        n.Boolean -> "bool"
        n.Int -> "int"
        n.Long -> "int"
        n.Float -> "float64"
        n.Date -> g.time.Time.toGo(c, derived, pointer = pointer)
        n.TimeUnit -> g.time.Time.toGo(c, derived, pointer = pointer)
        n.Path -> "string"
        n.Text -> "string"
        n.Blob -> "[]byte"
        n.Exception -> "error"
        n.Error -> "error"
        n.Void -> ""
        n.Url -> c.n(j.net.URL)
        n.List -> "[]${generics()[0].toGo(c, derived)}"
        n.Map -> "map(${generics()[0].toGo(c, derived)})${generics()[1].toGo(c, derived)}"
        else -> {
            if (this is Lambda) operation().toGoLamnda(c, derived) else null
        }
    }
}

fun TypeI.toGoGenericTypes(c: GenerationContext, derived: String, attr: AttributeI): String {
    return """${generics().joinWrappedToString(", ", "", "<", ">") { "${it.type().toGo(c, derived, attr)}" }}"""
}

fun GenericI.toGo(c: GenerationContext, derived: String): String {
    return type().toGo(c, derived)
}

fun <T : TypeI> T.toGo(c: GenerationContext, derived: String, attr: AttributeI = Attribute.EMPTY, pointer: Boolean = true): String {
    return toGoIfNative(c, derived, pointer) ?: "${pointer.then("*")}${c.n(this, derived)}"
}

fun <T : AttributeI> T.toGoValue(c: GenerationContext, derived: String): String {
    if (value() != null) {
        return when (type()) {
            n.String, n.Text -> "\"${value()}\""
            n.Boolean, n.Int, n.Long, n.Float, n.Date, n.Path, n.Blob, n.Void -> "${value()}"
            else -> {
                if (value() is Literal) {
                    val lit = value() as Literal
                    "${(lit.parent() as EnumTypeI).toGo(c, derived, this)}.${lit.toGo()}"
                } else {
                    "${value()}"
                }
            }
        }
    } else {
        return toGoEmpty(c, derived)
    }
}

fun <T : AttributeI> T.toGoInit(c: GenerationContext, derived: String, api: String): String {
    if (value() != null) {
        return " = ${toGoValue(c, derived)}"
    } else if (nullable()) {
        return " = null"
    } else if (initByDefaultTypeValue()) {
        return " = ${toGoValue(c, derived)}"
    } else {
        return ""
    }
}

fun <T : AttributeI> T.toGoSignature(c: GenerationContext, derived: String, api: String, init: Boolean = true): String {
    return "${name()} ${toGoTypeDef(c, api)}"
}

fun <T : AttributeI> T.toGoMember(c: GenerationContext, derived: String, api: String, init: Boolean = true): String {
    return "    ${nameForMember()} ${toGoTypeDef(c, api)}"
}

fun List<AttributeI>.toGoSignature(c: GenerationContext, derived: String, api: String): String {
    return "${joinWrappedToString(", ") { it.toGoSignature(c, derived, api) }}"
}

fun List<AttributeI>.toGoMember(c: GenerationContext, derived: String, api: String): String {
    return "${joinWrappedToString(", ") { it.toGoSignature(c, derived, api) }}"
}

fun <T : ConstructorI> T.toGo(c: GenerationContext, derived: String, api: String): String {
    val name = c.n(parent(), derived)
    return if (isNotEMPTY()) """
func New$name(${params().joinWrappedToString(", ", "                ") { it.toGoSignature(c, derived, api) }
    }) (ret $name, err error) {
    ${superUnit().isNotEMPTY().ifElse({
        """ret = ${superUnit().toGoCall(c)}
        ${paramsWithOut(superUnit()).joinSurroundIfNotEmptyToString("$nL    ", prefix = "$nL    ") {
            it.toGoAssign(c, "ret")
        }}"""
    }, {
        "ret = $name${params().joinSurroundIfNotEmptyToString(",$nL        ", "{$nL        ", ",$nL    }") {
            it.toGoInitCall()
        }}"
    })}
    return
}""" else ""
}

fun <T : ConstructorI> T.toGoCall(c: GenerationContext, name: String = "this"): String {
    return isNotEMPTY().then { " : $name(${params().joinWrappedToString(", ") { it.name() }})" }
}

fun <T : AttributeI> T.toGoAssign(c: GenerationContext, o: String): String {
    return "$o.${mutable().ifElse({ name().capitalize() },
            { name().decapitalize() })} = ${name()}"
}

fun <T : LogicUnitI> T.toGoCall(c: GenerationContext): String {
    return isNotEMPTY().then { "(${params().joinWrappedToString(", ") { it.name() }})" }
}

fun <T : LogicUnitI> T.toGoCallValue(c: GenerationContext, derived: String): String {
    return isNotEMPTY().then { "(${params().joinWrappedToString(", ") { it.toGoValue(c, derived) }})" }
}

fun <T : LiteralI> T.toGoCallValue(c: GenerationContext, derived: String): String {
    return params().isNotEMPTY().then { "(${params().joinWrappedToString(", ") { it.toGoValue(c, derived) }})" }
}

fun <T : AttributeI> T.toGoType(c: GenerationContext, derived: String): String = type().toGo(c, derived, this)

fun List<AttributeI>.toGoTypes(c: GenerationContext, derived: String): String {
    return "${joinWrappedToString(", ") { it.toGoType(c, derived) }}"
}

fun <T : OperationI> T.toGoLamnda(c: GenerationContext, derived: String): String =
        """(${params().toGoTypes(c, derived)}) -> ${ret().toGoType(c, derived)}"""

fun <T : OperationI> T.toGoImpl(o: String, c: GenerationContext, derived: String, api: String): String {
    return """
func (o *$o) ${name()}(${
    params().toGoSignature(c, derived, api)})${ret().toGoTypeDef(c, api)} {
        throw IllegalAccessException("Not implemented yet.")
    }"""
}
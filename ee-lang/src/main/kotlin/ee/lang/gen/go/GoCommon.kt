package ee.lang.gen.go

import ee.common.ext.ifElse
import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.joinWrappedToString
import ee.common.ext.then
import ee.lang.*
import ee.lang.gen.java.j

fun AttributeI.toGoInit(): String {
    return """${this.name()}: ${this.value()}"""
}

fun TypeI.toGoCall(c: GenerationContext, api: String): String {
    return c.n(this, api).substringAfterLast(".")
}

fun AttributeI.toGoInitCall(c: GenerationContext, api: String): String {
    return "${anonymous().ifElse({ type().toGoCall(c, api) }, { nameForMember() })}: ${name()}"
}

fun <T : AttributeI> T.toGoTypeDef(c: GenerationContext, api: String): String {
    return type().toGo(c, api)
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

fun GenericI.toGo(c: GenerationContext, derived: String): String {
    return type().toGo(c, derived)
}

fun <T : TypeI> T.toGo(c: GenerationContext, derived: String, pointer: Boolean = true): String {
    return toGoIfNative(c, derived, pointer) ?: "${pointer.then("*")}${c.n(this, derived)}"
}

fun <T : AttributeI> T.toGoSignature(c: GenerationContext, api: String): String {
    return "${name()} ${toGoTypeDef(c, api)}"
}

fun <T : AttributeI> T.toGoMember(c: GenerationContext, api: String): String {
    return anonymous().ifElse({ "    ${toGoTypeDef(c, api)}" }, { "    ${nameForMember()} ${toGoTypeDef(c, api)}" })
}

fun <T : AttributeI> T.toGoEnumMember(c: GenerationContext, api: String): String {
    return anonymous().ifElse({ "    ${toGoTypeDef(c, api)}" }, { "    ${nameForEnum()} ${toGoTypeDef(c, api)}" })
}

fun List<AttributeI>.toGoSignature(c: GenerationContext, api: String): String {
    return joinWrappedToString(", ") { it.toGoSignature(c, api) }
}

fun <T : ConstructorI> T.toGo(c: GenerationContext, derived: String, api: String): String {
    val name = c.n(parent(), derived)
    return if (isNotEMPTY()) """
func New$name(${params().joinWrappedToString(", ", "                ") { it.toGoSignature(c, api) }
    }) (ret *$name, err error) {
    ret = &$name${params().joinSurroundIfNotEmptyToString(",$nL        ", "{$nL        ", ",$nL    }") {
        it.toGoInitCall(c, api)
    }}
    return
}""" else ""
}

fun <T : AttributeI> T.toGoAssign(o: String): String {
    return "$o.${mutable().ifElse({ name().capitalize() },
            { name().decapitalize() })} = ${name()}"
}

fun <T : LogicUnitI> T.toGoCall(): String {
    return isNotEMPTY().then { "(${params().joinWrappedToString(", ") { it.name() }})" }
}

fun <T : ConstructorI> T.toGoCall(c: GenerationContext, derived: String, api: String): String {
    val name = c.n(parent(), derived)
    return if (isNotEMPTY()) """New$name(${params().joinWrappedToString(", ", "                ") {
        it.name()
    }})""" else ""
}

fun <T : AttributeI> T.toGoType(c: GenerationContext, derived: String): String = type().toGo(c, derived)

fun List<AttributeI>.toGoTypes(c: GenerationContext, derived: String): String {
    return joinWrappedToString(", ") { it.toGoType(c, derived) }
}

fun <T : OperationI> T.toGoLamnda(c: GenerationContext, derived: String): String =
        """(${params().toGoTypes(c, derived)}) -> ${ret().toGoType(c, derived)}"""

fun <T : LogicUnitI> T.toGoName(): String = visible().ifElse({ name().capitalize() }, { name().decapitalize() })

fun <T : OperationI> T.toGoImpl(o: String, c: GenerationContext, api: String): String {
    return macro().isNotEmpty().then {
        """
func (o *$o) ${toGoName()}(${params().toGoSignature(c, api)}) ${ret().toGoTypeDef(c, api)} {
    ${c.body(macro(), this, api, api)}
}"""
    }
}
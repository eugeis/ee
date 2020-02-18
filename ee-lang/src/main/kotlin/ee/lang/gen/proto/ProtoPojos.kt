package ee.lang.gen.proto

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.kt.*

fun LiteralI<*>.toProto(): String = name().capitalize()

fun AttributeI<*>.toProtoGetMethod(o: String, c: GenerationContext, api: String = LangDerivedKind.API): String {
    return """
func (o *$o) ${name().capitalize()}() ${toProtoType(c, api)} {
    return o.${name()}
}"""
}

fun AttributeI<*>.toProtoAddMethod(o: String, c: GenerationContext, api: String = LangDerivedKind.API): String {
    val type = type().generics()[0].toProto(c, api)
    return """
func (o *$o) AddTo${name().capitalize()}(item $type) $type {
    o.${nameForProtoMember()} = append(o.${nameForProtoMember()}, item)
    return item
}"""
}


fun <T : CompilationUnitI<*>> T.toProtoImpl(
        c: GenerationContext, derived: String = LangDerivedKind.IMPL,
        api: String = LangDerivedKind.API, excludePropsWithValue: Boolean = false): String {

    val name = c.n(this, derived)
    val currentProps = excludePropsWithValue.ifElse({ props().filter { it.value() == null } }, props())
    return """${toProtoMacrosBefore(c, derived, api)}
message $name {${toProtoMacrosBeforeBody(c, derived, api)}${currentProps.joinWithIndexToString(nL,
            prefix = nL) { idx, prop ->
        "${prop.toProtoMember(c, api)} = ${idx + 1};"
    }}${toProtoMacrosAfterBody(c, derived, api)}
};${toProtoMacrosAfter(c, derived, api)}"""
}


fun <T : EnumTypeI<*>> T.toProtoEnum(
        c: GenerationContext, derived: String = LangDerivedKind.API,
        api: String = LangDerivedKind.API): String {

    val name = c.n(this, derived)
    val typePrefix = """enum $name"""

    return """${toProtoDoc()}
$typePrefix {
    ${literals().joinWithIndexToString("$nL    ") { idx, lit ->
        "${lit.toProto()} = $idx;"
    }}
};
"""
}
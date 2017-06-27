package ee.lang.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.joinWithIndexToString
import ee.common.ext.then
import ee.common.ext.toUnderscoredUpperCase
import ee.lang.*

fun LiteralI.toGo(): String = "${name().toUnderscoredUpperCase()}"

fun LiteralI.toGoIsMethod(index: Int, parent: ItemI = findParent(CompilationUnitI::class.java) ?: parent()): String {
    return """
func (o *${parent.name().capitalize()}) Is${name().capitalize()}() bool {
    return o == _${parent.name().decapitalize()}s.${toGo()}
}

func (o *${parent.name().capitalize()}Literals) ${name().capitalize()}() ${parent.name().capitalize()} {
    return _${parent.name().decapitalize()}s.${toGo()}
}
"""
}

fun AttributeI.toGoLiteralArray(c: GenerationContext, derived: String,
                                parent: EnumTypeI = findParentMust(EnumTypeI::class.java)): String {
    return """var ${parent.name().decapitalize()}${name().capitalize()} = []${
    this.type().toGo(c, derived, this)}${parent.literals().joinToString(", ", "{", "}")
    { "${it.params().find { it.derivedFrom() == this }?.value()}" }}"""
}

fun <T : EnumTypeI> T.toGoEnum(c: GenerationContext,
                               derived: String = DerivedNames.API.name,
                               api: String = DerivedNames.API.name): String {
    val name = c.n(this, derived)
    val enum = name.capitalize()
    val enums = "${name.decapitalize()}s"
    val enumLiterals = "${name.decapitalize()}Literals"
    return """
type $enum struct {
	name  string
	order int${
    props().joinToString("", nL) { it.toGoMember(c, derived, api) }}
}

type $enumLiterals struct {
	values []*$enum
}

var _$enums = &$enumLiterals{values: []*$enum{
	{name: "", order: 0},
}}

func $enums() *$enumLiterals {
	return _$enums
}

func (o *$enumLiterals) LitName1() *$enum {
	return o.values[0]
}

func (o *$enumLiterals) Values() []*$enum {
	return o.values
}

${literals().joinWithIndexToString("$nL    ", "const ($nL    ", "$nL)") { i, item -> if (i == 0) "${item.toGo()} $enum = iota" else "${item.toGo()}" }}

${literals().joinToString(", ", "var $enums = []string{", "}") { "\"${it.toGo()}\"" }}${
    props().joinToString("", nL) { it.toGoLiteralArray(c, derived, this) }}${
    propsExceptPrimaryConstructor().joinToString(nL) { it.toGoMember(c, derived, api) }}${
    operations().joinToString(nL) { it.toGoImpl(c, derived, api) }}${
    literals().joinWithIndexToString { i, item -> item.toGoIsMethod(i, this) }}
func Parse${name}(name string) (ret $name, ok bool) {${
    literals().joinToString("$nL    ", "$nL    switch (name) {$nL    ", "$nL    }") { "case \"${it.toGo()}\":$nL        ret = ${it.toGo()}" }}
    return
}"""
}

fun <T : CompilationUnitI> T.toGoImpl(c: GenerationContext,
                                      derived: String = DerivedNames.IMPL.name,
                                      api: String = DerivedNames.API.name): String {
    return """
${open().then("open ")}class ${c.n(this, derived)}${toGoExtends(c, derived, api)}${
    primaryConstructor().toGoPrimary(c, derived, api)} {${
    props().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) { it.toGoMember(c, derived, api, false) }}${
    otherConstructors().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toGo(c, derived, api)
    }}${operations().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
        it.toGoImpl(c, derived, api)
    }}${
    toGoEmptyObject(c, derived)}
}"""
}
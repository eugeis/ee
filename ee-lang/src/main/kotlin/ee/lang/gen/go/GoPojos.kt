package ee.lang.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.joinWithIndexToString
import ee.lang.*

fun LiteralI.toGo(): String = "${name().capitalize()}"
fun EnumTypeI.toGoAccess(): String = "${name().capitalize()}s"
fun EnumTypeI.toGoLiterals(): String = toGoAccess().decapitalize()

fun LiteralI.toGoIsMethod(o: String, literals: String): String {
    return """
func (o *$o) Is${name().capitalize()}() bool {
    return o == _$literals.${toGo()}()
}"""
}

fun AttributeI.toGoGetMethod(o: String, c: GenerationContext,
                             derived: String = DerivedNames.API.name): String {
    return """
func (o *$o) ${name().capitalize()}() ${toGoType(c, derived)} {
    return o.${name()}
}"""
}

fun LiteralI.toGoLitMethod(index: Int, enum: String, literals: String): String {
    return """
func (o *$literals) ${name().capitalize()}() *$enum {
    return _$literals.values[$index]
}"""
}

fun LiteralI.toGoCase(): String {
    return """  case "${this.toGo()}":
        ret = o.${this.toGo()}()"""
}

fun LiteralI.toGoInit(index: Int): String {
    return """{name: "${this.name()}", ordinal: $index${this.params().joinSurroundIfNotEmptyToString(", ", ", ") { it.toGoInit() }}}"""
}

fun AttributeI.toGoInit(): String {
    return """${this.name()}: ${this.value()}"""
}

fun <T : EnumTypeI> T.toGoEnum(c: GenerationContext,
                               derived: String = DerivedNames.API.name,
                               api: String = DerivedNames.API.name): String {
    val name = c.n(this, derived)
    val enum = name.capitalize()
    val enums = toGoAccess()
    val enumLiterals = toGoLiterals()
    return """
type $enum struct {
	name  string
	ordinal int${
    props().joinSurroundIfNotEmptyToString("", nL) { it.toGoMember(c, derived, api) }}
}

func (o *$enum) Name() string {
    return o.name
}

func (o *$enum) Ordinal() int {
    return o.ordinal
}${
    props().joinSurroundIfNotEmptyToString("", nL) { it.toGoGetMethod(enum, c, derived) }}
${literals().joinSurroundIfNotEmptyToString(nL) { item -> item.toGoIsMethod(enum, enumLiterals) }}${
    operations().joinToString(nL) { it.toGoImpl(c, derived, api) }}

type $enumLiterals struct {
	values []*$enum
}

var _$enumLiterals = &$enumLiterals{values: []*$enum${literals().joinWithIndexToString(",$nL    ", "{$nL    ", "},") { i, item ->
        item.toGoInit(i)
    }}
}

func $enums() *$enumLiterals {
	return _$enumLiterals
}

func (o *$enumLiterals) Values() []*$enum {
	return o.values
}
${literals().joinWithIndexToString(nL) { i, item -> item.toGoLitMethod(i, enum, enumLiterals) }}

func (o *$enumLiterals) Parse$name(name string) (ret *$name, ok bool) {${
    literals().joinToString("$nL    ", "$nL    switch name {$nL    ", "$nL    }") { it.toGoCase() }}
    return
}"""
}

fun <T : CompilationUnitI> T.toGoImpl(c: GenerationContext,
                                      derived: String = DerivedNames.IMPL.name,
                                      api: String = DerivedNames.API.name): String {
    return """
type ${c.n(this, derived)}${toGoExtends(c, derived, api)} struct ${
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
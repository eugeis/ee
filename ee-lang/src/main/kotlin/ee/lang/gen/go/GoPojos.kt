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

fun <T : EnumTypeI> T.toGoEnum(c: GenerationContext,
                               derived: String = DerivedNames.API.name,
                               api: String = DerivedNames.API.name): String {
    val name = c.n(this, derived)
    val enums = toGoAccess()
    val literals = toGoLiterals()
    return """
type $name struct {
	name  string
	ordinal int${
    props().joinSurroundIfNotEmptyToString("", nL) { it.toGoMember(c, derived, api) }}
}

func (o *$name) Name() string {
    return o.name
}

func (o *$name) Ordinal() int {
    return o.ordinal
}${
    props().joinSurroundIfNotEmptyToString("", nL) { it.toGoGetMethod(name, c, derived) }}
${literals().joinSurroundIfNotEmptyToString(nL) { item -> item.toGoIsMethod(name, literals) }}${
    operations().joinToString(nL) { it.toGoImpl(c, derived, api) }}

type $literals struct {
	values []*$name
}

var _$literals = &$literals{values: []*$name${literals().joinWithIndexToString(",$nL    ", "{$nL    ", "},") { i, item ->
        item.toGoInit(i)
    }}
}

func $enums() *$literals {
	return _$literals
}

func (o *$literals) Values() []*$name {
	return o.values
}
${literals().joinWithIndexToString(nL) { i, item -> item.toGoLitMethod(i, name, literals) }}

func (o *$literals) Parse$name(name string) (ret *$name, ok bool) {${
    literals().joinToString("$nL    ", "$nL    switch name {$nL    ", "$nL    }") { it.toGoCase() }}
    return
}"""
}

fun <T : CompilationUnitI> T.toGoImpl(c: GenerationContext,
                                      derived: String = DerivedNames.IMPL.name,
                                      api: String = DerivedNames.API.name): String {
    return """
type ${c.n(this, derived)} struct {${
    props().joinSurroundIfNotEmptyToString(nL, prefix = nL) { it.toGoMember(c, derived, api, false) }}
}${
    constructors().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        it.toGo(c, derived, api)
    }}${operations().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        it.toGoImpl(c, derived, api)
    }}"""
}
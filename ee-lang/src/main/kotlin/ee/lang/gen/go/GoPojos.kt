package ee.lang.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.joinWithIndexToString
import ee.common.ext.setAndTrue
import ee.lang.*

fun LiteralI.toGo(): String = name().capitalize()
fun EnumTypeI.toGoAccess(): String = "${name().capitalize()}s"
fun EnumTypeI.toGoLiterals(): String = toGoAccess().decapitalize()

fun LiteralI.toGoIsMethod(o: String, literals: String): String {
    return """
func (o *$o) Is${name().capitalize()}() bool {
    return o == _$literals.${toGo()}()
}"""
}

fun AttributeI.toGoGetMethod(o: String, c: GenerationContext,
                             api: String = LangDerivedKind.API): String {
    return """
func (o *$o) ${name().capitalize()}() ${toGoType(c, api)} {
    return o.${name()}
}"""
}

fun AttributeI.toGoAddMethod(o: String, c: GenerationContext,
                             api: String = LangDerivedKind.API): String {
    val type = type().generics()[0].toGo(c, api)
    return """
func (o *$o) AddTo${name().capitalize()}(item $type) $type {
    o.${nameForMember()} = append(o.${nameForMember()}, item)
    return item
}"""
}

fun LiteralI.toGoLitMethod(index: Int, enum: String, literals: String): String {
    return """
func (o *$literals) ${name().capitalize()}() *$enum {
    return _$literals.values[$index]
}"""
}

fun LiteralI.toGoCase(): String {
    return """  case o.${this.toGo()}().Name():
        ret = o.${this.toGo()}()"""
}

fun LiteralI.toGoInit(index: Int): String {
    return """{name: "${this.name()}", ordinal: $index${
    this.params().joinSurroundIfNotEmptyToString(", ", ", ") { it.toGoInit() }}}"""
}

fun <T : EnumTypeI> T.toGoEnum(c: GenerationContext, api: String = LangDerivedKind.API): String {
    val name = c.n(this, api)
    val enums = toGoAccess()
    val literals = toGoLiterals()
    return """
type $name struct {
	name  string
	ordinal int${
    props().joinSurroundIfNotEmptyToString(nL, nL) { it.toGoEnumMember(c, api) }}
}

func (o *$name) Name() string {
    return o.name
}

func (o *$name) Ordinal() int {
    return o.ordinal
}${
    props().joinSurroundIfNotEmptyToString("", nL) { it.toGoGetMethod(name, c, api) }}
${literals().joinSurroundIfNotEmptyToString(nL) { item -> item.toGoIsMethod(name, literals) }}${
    operations().joinToString(nL) { it.toGoImpl(name, c, api) }}

type $literals struct {
	values []*$name
    literals []${c.n(g.gee.enum.Literal)}
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

func (o *$literals) Literals() []${c.n(g.gee.enum.Literal)} {
	if o.literals == nil {
		o.literals = make([]${c.n(g.gee.enum.Literal)}, len(o.values))
		for i, item := range o.values {
			o.literals[i] = item
		}
	}
	return o.literals
}
${literals().joinWithIndexToString(nL) { i, item -> item.toGoLitMethod(i, name, literals) }}

func (o *$literals) Parse$name(name string) (ret *$name, ok bool) {
	if item, ok := enum.Parse(name, o.literals); ok {
		return item.(*$name), ok
	}
	return
}"""
}

fun <T : CompilationUnitI> T.toGoImpl(c: GenerationContext,
                                      derived: String = LangDerivedKind.IMPL,
                                      api: String = LangDerivedKind.API): String {
    val name = c.n(this, derived)
    return """
${toGoMacros(c, api, api)}
type $name struct {${
    props().joinSurroundIfNotEmptyToString(nL, prefix = nL) { it.toGoMember(c, api) }}
}${
    constructors().filter { it.derivedAsType().isEmpty() }.joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        it.toGo(c, derived, api)
    }}${
    props().filter { it.accessible().setAndTrue() && !it.mutable().setAndTrue() }.joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        it.toGoGetMethod(name, c, derived)
    }}${
    props().filter { it.type().isOrDerived(n.List) }.joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        it.toGoAddMethod(name, c, derived)
    }}${operations().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        it.toGoImpl(name, c, api)
    }}"""
}
package ee.lang.gen.go

import ee.common.ext.ifElse
import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.joinWithIndexToString
import ee.common.ext.setAndTrue
import ee.lang.*

fun LiteralI<*>.toGo(): String = name().capitalize()
fun EnumTypeI<*>.toGoAccess(): String = "${name().capitalize()}s"
fun EnumTypeI<*>.toGoLiterals(): String = toGoAccess().decapitalize()

fun LiteralI<*>.toGoIsMethod(o: String, literals: String): String {
    return """
func (o *$o) Is${name().capitalize()}() bool {
    return o == _$literals.${toGo()}()
}"""
}

fun AttributeI<*>.toGoGetMethod(o: String, c: GenerationContext, api: String = LangDerivedKind.API): String {
    return """
func (o *$o) ${name().capitalize()}() ${toGoType(c, api)} {
    return o.${name()}
}"""
}

fun AttributeI<*>.toGoAddMethod(o: String, c: GenerationContext, api: String = LangDerivedKind.API): String {
    val type = type().generics()[0].toGo(c, api)
    return """
func (o *$o) AddTo${name().capitalize()}(item $type) $type {
    o.${nameForGoMember()} = append(o.${nameForGoMember()}, item)
    return item
}"""
}

fun LiteralI<*>.toGoLitMethod(index: Int, enum: String, literals: String): String {
    return """
func (o *$literals) ${name().capitalize()}() *$enum {
    return _$literals.values[$index]
}"""
}

fun LiteralI<*>.toGoCase(): String {
    return """  case o.${this.toGo()}().Name():
        ret = o.${this.toGo()}()"""
}

fun LiteralI<*>.toGoInitVariables(index: Int, c: GenerationContext, derived: String): String {
    return """{name: "${this.name()}", ordinal: $index${this.params().joinSurroundIfNotEmptyToString(", ", ", ") {
        it.toGoInitForConstructor(c, derived)
    }}}"""
}

fun <T : EnumTypeI<*>> T.toGoEnum(c: GenerationContext, api: String = LangDerivedKind.API): String {
    val name = c.n(this, api)
    val enums = toGoAccess()
    val literals = toGoLiterals()
    return """
type $name struct {
	name  string
	ordinal int${props().joinSurroundIfNotEmptyToString(nL, nL) { it.toGoEnumMember(c, api) }}
}

func (o *$name) Name() string {
    return o.name
}

func (o *$name) Ordinal() int {
    return o.ordinal
}

func (o $name) MarshalJSON() (ret []byte, err error) {
	return ${c.n(g.encoding.json.Marshal, api)}(&${c.n(g.gee.enum.EnumBaseJson, api)}{Name: o.name})
}

func (o *$name) UnmarshalJSON(data []byte) (err error) {
	lit := ${c.n(g.gee.enum.EnumBaseJson, api)}{}
	if err = ${c.n(g.encoding.json.Unmarshal, api)}(data, &lit); err == nil {
		if v, ok := $enums().Parse$name(lit.Name); ok {
            *o = *v
        } else {
            err = ${c.n(g.fmt.Errorf, api)}("invalid $name %q", lit.Name)
        }
	}
	return
}

func (o $name) GetBSON() (ret interface{}, err error) {
	return o.name, nil
}

func (o *$name) SetBSON(raw ${c.n(g.mgo2.bson.Raw, api)}) (err error) {
	var lit string
    if err = raw.Unmarshal(&lit); err == nil {
		if v, ok := $enums().Parse$name(lit); ok {
            *o = *v
        } else {
            err = ${c.n(g.fmt.Errorf, api)}("invalid $name %q", lit)
        }
    }
    return
}${props().joinSurroundIfNotEmptyToString("", nL) { it.toGoGetMethod(name, c, api) }}
${literals().joinSurroundIfNotEmptyToString(nL) { item ->
        item.toGoIsMethod(name, literals)
    }}${operations().joinToString(nL) { it.toGoImpl(name, c, api) }}

type $literals struct {
	values []*$name
    literals []${c.n(g.gee.enum.Literal)}
}

var _$literals = &$literals{values: []*$name${literals().joinWithIndexToString(",$nL    ", "{$nL    ",
        "},") { i, item ->
        item.toGoInitVariables(i, c, api)
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
	if item, ok := enum.Parse(name, o.Literals()); ok {
		return item.(*$name), ok
	}
	return
}"""
}

fun <T : CompilationUnitI<*>> T.toGoImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
    api: String = LangDerivedKind.API, excludePropsWithValue: Boolean = false): String {
    val name = c.n(this, derived)
    val currentProps = excludePropsWithValue.ifElse({ props().filter { it.value() == null } }, props())
    return """${toGoMacrosBefore(c, derived, api)}
type $name struct {${toGoMacrosBeforeBody(c, derived, api)}${currentProps.joinSurroundIfNotEmptyToString(nL,
        prefix = nL) { "${it.toGoMember(c, api)}${it.toGoJsonTags()}" }}${toGoMacrosAfterBody(c, derived, api)}
}${toGoMacrosAfter(c, derived, api)}${constructors().filter {
        it.derivedAsType().isEmpty()
    }.joinSurroundIfNotEmptyToString(nL, prefix = nL) {
            it.toGo(c, derived, api)
        }}${currentProps.filter { it.isAccessible().setAndTrue() && !it.isMutable().setAndTrue() }.joinSurroundIfNotEmptyToString(
        nL, prefix = nL) {
        it.toGoGetMethod(name, c, derived)
    }}${currentProps.filter { it.type().isOrDerived(n.List) }.joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        it.toGoAddMethod(name, c, derived)
    }}${operations().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        it.toGoImpl(name, c, api)
    }}"""
}
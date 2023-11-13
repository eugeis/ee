package ee.lang.gen.go

import ee.common.ext.*
import ee.lang.*
import java.util.*

fun String.toGo(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
fun LiteralI<*>.toGo(): String = name().toGo()
fun EnumTypeI<*>.toGoAccess(): String = "${name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}s"
fun EnumTypeI<*>.toGoLiterals(): String = toGoAccess().replaceFirstChar { it.lowercase(Locale.getDefault()) }

fun LiteralI<*>.toGoIsMethod(o: String, literals: String): String {
    return """
func (o *$o) Is${name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}() bool {
    return o.name == _$literals.${toGo()}().name
}"""
}

fun AttributeI<*>.toGoGetMethod(o: String, c: GenerationContext, api: String = LangDerivedKind.API): String {
    return """
func (o *$o) ${name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}() ${toGoType(c, api)} {
    return o.${name()}
}"""
}

fun AttributeI<*>.toGoFindMethodName(): String {
    return "${name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}Find"
}

fun AttributeI<*>.toGoFindMethod(o: String, c: GenerationContext, api: String = LangDerivedKind.API): String {
    val genericType = type().generics().first()
    val propId = genericType.type().propId() ?: return ""
    return """
func (o *$o) ${toGoFindMethodName()}(${propId.name()} ${propId.type().toGo(c, api)}) (int, ${genericType.toGo(c, api)}) {
    for i, item := range o.${nameForGoMember()} {
        if ${propId.name()} == item.${propId.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} {
            return i, item
        }
    }
    return -1, nil
}"""
}

fun AttributeI<*>.toGoRemoveMethodName(): String {
    return "${name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}Remove"
}

fun AttributeI<*>.toGoRemoveMethod(o: String, c: GenerationContext, api: String = LangDerivedKind.API): String {
    val genericType = type().generics().first()
    val propId = genericType.type().propId() ?: return ""
    return """
func (o *$o) ${toGoRemoveMethodName()}(${propId.name()} ${propId.type().toGo(c, api)}) (ret ${genericType.toGo(c, api)}) {
    var index int
    if index, ret = o.${toGoFindMethodName()}(${propId.name()}); index >= 0 {
        o.${nameForGoMember()} = append(o.${nameForGoMember()}[:index], o.${nameForGoMember()}[index+1:]...)
    }
    return
}"""
}

fun AttributeI<*>.toGoAddMethodName(): String {
    return "${name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}Add"
}

fun AttributeI<*>.toGoAddMethod(o: String, c: GenerationContext, api: String = LangDerivedKind.API): String {
    val genericType = type().generics().first()
    val type = genericType.toGo(c, api)
    return """
func (o *$o) ${toGoAddMethodName()}(item $type) $type {
    o.${nameForGoMember()} = append(o.${nameForGoMember()}, item)
    return item
}"""
}

fun AttributeI<*>.toGoReplaceMethodName(): String {
    return "${name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}Replace"
}

fun AttributeI<*>.toGoReplaceMethod(o: String, c: GenerationContext, api: String = LangDerivedKind.API): String {
    val genericType = type().generics().first()
    val propId = genericType.type().propId() ?: return ""
    val type = genericType.toGo(c, api)
    return """
func (o *$o) ${toGoReplaceMethodName()}(item $type) (ret $type) {
    var index int
    if index, ret = o.${toGoFindMethodName()}(item.${propId.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}); index >= 0 {
        o.${nameForGoMember()}[index] = item
    }
    return
}"""
}

fun EnumLiteralI<*>.toGoLitMethod(index: Int, enum: String, literals: String): String {
    return """
func (o *$literals) ${name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}() *$enum {
    return o.values[$index]
}"""
}

fun EnumLiteralI<*>.toGoCase(): String {
    return """  case o.${this.toGo()}().Name():
        ret = o.${this.toGo()}()"""
}

fun EnumLiteralI<*>.toGoInitVariables(index: Int, c: GenerationContext, derived: String): String {
    return """{name: "${externalName() ?: name()}", ordinal: $index${
        this.params()
            .joinSurroundIfNotEmptyToString(", ", ", ") {
                it.toGoInitForConstructorEnum(c, derived)
            }
    }}"""
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
}${props().joinSurroundIfNotEmptyToString("", nL) { it.toGoGetMethod(name, c, api) }}
${
        literals().joinSurroundIfNotEmptyToString(nL) { item ->
            item.toGoIsMethod(name, literals)
        }
    }${operations().joinToString(nL) { it.toGoImpl(name, c, api) }}

func (o *$name) MarshalJSON() (ret []byte, err error) {
    ret = []byte(${c.n(g.fmt.Sprintf, api)}("\"%v\"", o.name))
	return
}

func (o *$name) UnmarshalJSON(data []byte) (err error) {
	name := string(data)
    //remove quotes
    name = name[1 : len(name)-1]
    if v, ok := $enums().Parse$name(name); ok {
        *o = *v
    } else {
        err = ${c.n(g.fmt.Errorf, api)}("invalid $name %q", name)
    }
	return
}

func (o *$name) GetBSON() (ret interface{}, err error) {
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
}

type $literals struct {
	values []*$name
    valuesAsLiterals []${c.n(g.gee.enum.Literal)}
}

var _$literals = &$literals{values: []*$name${
        literals().joinWithIndexToString(
            ",$nL    ", "{$nL    ",
            "},"
        ) { i, item ->
            item.toGoInitVariables(i, c, api)
        }
    }
}

func $enums() *$literals {
	return _$literals
}

func (o *$literals) Values() []*$name {
	return o.values
}
${literals().joinWithIndexToString(nL) { i, item -> item.toGoLitMethod(i, name, literals) }}

func (o *$literals) Parse$name(name string) (ret *$name, ok bool) {
	for _, lit := range o.Values() {
		if ${c.n(g.strings.EqualFold)}(lit.Name(), name) {
			return lit, true
		}
	}
	return nil, false
}

// we have to convert the instances to Literal interface, because it is not a other way in Go
func (o *$literals) Literals() []${c.n(g.gee.enum.Literal)} {
	if o.valuesAsLiterals == nil {
		o.valuesAsLiterals = make([]${c.n(g.gee.enum.Literal)}, len(o.values))
		for i, item := range o.values {
			o.valuesAsLiterals[i] = item
		}
	}
	return o.valuesAsLiterals
}"""
}

fun List<OperationI<*>>.toGoIfc(c: GenerationContext, api: String): String =
    joinSurroundIfNotEmptyToString(nL) {
        it.toGoIfc(c, api)
    }

fun <T : CompilationUnitI<*>> T.toGoIfc(
    c: GenerationContext, derived: String = LangDerivedKind.API, api: String = LangDerivedKind.API
): String {

    val name = c.n(this, derived)
    return """${toGoMacrosBefore(c, derived, api)}
type $name interface {${toGoMacrosBeforeBody(c, derived, api)}${
        superUnits().joinSurroundIfNotEmptyToString(nL, nL) {
            "    ${c.n(it, derived)}"
        }
    }${
        operations().toGoIfc(c, api)
    }${toGoMacrosAfterBody(c, derived, api)}
}${toGoMacrosAfter(c, derived, api)}"""
}

fun <T : CompilationUnitI<*>> T.toGoImpl(
    c: GenerationContext, derived: String = LangDerivedKind.IMPL,
    api: String = LangDerivedKind.API, excludePropsWithValue: Boolean = false
): String {

    val name = c.n(this, derived)
    val currentProps = excludePropsWithValue.ifElse({ props().filter { it.value() == null } }, props())
    return """${toGoMacrosBefore(c, derived, api)}
type $name struct {${toGoMacrosBeforeBody(c, derived, api)}${
        currentProps.joinSurroundIfNotEmptyToString(
            nL,
            prefix = nL
        ) { "${it.toGoMember(c, api)}${(this is DataTypeI<*>).then { it.toGoYamlJsonEhTags() }}" }
    }${
        toGoMacrosAfterBody(c, derived, api)
    }
}${toGoMacrosAfter(c, derived, api)}${
        constructors().filter {
            it.derivedAsType().isEmpty()
        }.joinSurroundIfNotEmptyToString(nL, prefix = nL) {
            it.toGo(c, derived, api)
        }
    }${
        currentProps.filter { it.isAccessible().setAndTrue() && !it.isMutable().setAndTrue() }
            .joinSurroundIfNotEmptyToString(nL, prefix = nL) {
                it.toGoGetMethod(name, c, derived)
            }
    }${
        currentProps.filter { it.type().isOrDerived(n.List) }
            .joinSurroundIfNotEmptyToString(nL, prefix = nL) {
                """${it.toGoAddMethod(name, c, derived)}
                    ${it.toGoRemoveMethod(name, c, derived)}
                    ${it.toGoReplaceMethod(name, c, derived)}                  
                    ${it.toGoFindMethod(name, c, derived)}"""
            }
    }${
        operations().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
            it.toGoImpl(name, c, api)
        }
    }"""
}

fun <T : CompilationUnitI<*>> T.toGoNewTestInstance(
    c: GenerationContext, derived: String = LangDerivedKind.IMPL, api: String = LangDerivedKind.API
): String {

    return constructors().joinToString(nL) {
        toGoNewTestInstance(c, derived, api, it)
    }
}

private fun <T : CompilationUnitI<*>> T.toGoNewTestInstance(
    c: GenerationContext, derived: String, api: String, constr: ConstructorI<*>
): String {

    val name = c.n(this, derived)
    val constrName = "${c.n(constr, derived)}ByPropNames"
    val constrNames = "${c.n(constr, derived).toPlural()}ByPropNames"

    return """
func $constrNames(salt int, count int) []*$name {
	items := make([]*$name, count)
	for i := 0; i < count; i++ {
		items[i] = $constrName(i + salt, count)
	}
	return items
}

func $constrName(salt int, childrenPropCount int) (ret *$name)  {
    ret = ${
        findByNameOrPrimaryOrFirstConstructorFull(constr.name())
            .toGoCallValueByPropName(c, derived, api, "salt", constr.name())
    }
    ${
        propsNoMetaNoValue().joinSurroundIfNotEmptyToString("\n    ") { prop ->
            if (!prop.isAnonymous()) {
                "ret.${prop.name()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} = ${
                    prop.toGoTestInstance(c, derived, "salt", constr.name())
                }"
            } else {
                "ret.${prop.type().name()} = ${
                    prop.toGoTestInstance(c, derived, "salt", constr.name())
                }"
            }
        }
    }
    return
}"""
}
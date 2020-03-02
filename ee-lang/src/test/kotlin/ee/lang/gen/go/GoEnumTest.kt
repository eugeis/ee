package ee.lang.gen.go

import ee.common.ext.logger
import ee.lang.gen.kt.TestModel
import ee.lang.gen.kt.infoBeforeAfter
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GoEnumTest {
    val log = logger()

    @BeforeEach
    fun beforeGoPojosTest() {
        TestModel.prepareForGoGeneration()
    }

    @Test
    fun simpleEnumTest() {
        val out = TestModel.SimpleEnum.toGoEnum(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
type SimpleEnum struct {
	name  string
	ordinal int
}

func (o *SimpleEnum) Name() string {
    return o.name
}

func (o *SimpleEnum) Ordinal() int {
    return o.ordinal
}

func (o *SimpleEnum) IsLitName1() bool {
    return o == _simpleEnums.LitName1()
}

func (o *SimpleEnum) IsLitName2() bool {
    return o == _simpleEnums.LitName2()
}

func (o SimpleEnum) MarshalJSON() (ret []byte, err error) {
    ret = []byte(fmt.Sprintf("\"%v\"", o.name))
	return
}

func (o *SimpleEnum) UnmarshalJSON(data []byte) (err error) {
	name := string(data)
    //remove quotes
    name = name[1 : len(name)-1]
    if v, ok := SimpleEnums().ParseSimpleEnum(name); ok {
        *o = *v
    } else {
        err = fmt.Errorf("invalid SimpleEnum %q", name)
    }
	return
}

func (o SimpleEnum) GetBSON() (ret interface{}, err error) {
	return o.name, nil
}

func (o *SimpleEnum) SetBSON(raw bson.Raw) (err error) {
	var lit string
    if err = raw.Unmarshal(&lit); err == nil {
		if v, ok := SimpleEnums().ParseSimpleEnum(lit); ok {
            *o = *v
        } else {
            err = fmt.Errorf("invalid SimpleEnum %q", lit)
        }
    }
    return
}

type simpleEnums struct {
	values []*SimpleEnum
    literals []enum.Literal
}

var _simpleEnums = &simpleEnums{values: []*SimpleEnum{
    {name: "LitName1", ordinal: 0},
    {name: "LitName2", ordinal: 1}},
}

func SimpleEnums() *simpleEnums {
	return _simpleEnums
}

func (o *simpleEnums) Values() []*SimpleEnum {
	return o.values
}

func (o *simpleEnums) Literals() []enum.Literal {
	if o.literals == nil {
		o.literals = make([]enum.Literal, len(o.values))
		for i, item := range o.values {
			o.literals[i] = item
		}
	}
	return o.literals
}

func (o *simpleEnums) LitName1() *SimpleEnum {
    return _simpleEnums.values[0]
}

func (o *simpleEnums) LitName2() *SimpleEnum {
    return _simpleEnums.values[1]
}

func (o *simpleEnums) ParseSimpleEnum(name string) (ret *SimpleEnum, ok bool) {
	if item, ok := enum.Parse(name, o.Literals()); ok {
		return item.(*SimpleEnum), ok
	}
	return
}"""))
    }

    @Test
    fun complexEnumTest() {
        val out = TestModel.ComplexEnum.toGoEnum(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
type ComplexEnum struct {
	name  string
	ordinal int
    code int
}

func (o *ComplexEnum) Name() string {
    return o.name
}

func (o *ComplexEnum) Ordinal() int {
    return o.ordinal
}

func (o *ComplexEnum) Code() int {
    return o.code
}

func (o *ComplexEnum) IsLitName1() bool {
    return o == _complexEnums.LitName1()
}

func (o *ComplexEnum) IsLitName2() bool {
    return o == _complexEnums.LitName2()
}

func (o ComplexEnum) MarshalJSON() (ret []byte, err error) {
    ret = []byte(fmt.Sprintf("\"%v\"", o.name))
	return
}

func (o *ComplexEnum) UnmarshalJSON(data []byte) (err error) {
	name := string(data)
    //remove quotes
    name = name[1 : len(name)-1]
    if v, ok := ComplexEnums().ParseComplexEnum(name); ok {
        *o = *v
    } else {
        err = fmt.Errorf("invalid ComplexEnum %q", name)
    }
	return
}

func (o ComplexEnum) GetBSON() (ret interface{}, err error) {
	return o.name, nil
}

func (o *ComplexEnum) SetBSON(raw bson.Raw) (err error) {
	var lit string
    if err = raw.Unmarshal(&lit); err == nil {
		if v, ok := ComplexEnums().ParseComplexEnum(lit); ok {
            *o = *v
        } else {
            err = fmt.Errorf("invalid ComplexEnum %q", lit)
        }
    }
    return
}

type complexEnums struct {
	values []*ComplexEnum
    literals []enum.Literal
}

var _complexEnums = &complexEnums{values: []*ComplexEnum{
    {name: "LitName1", ordinal: 0, code: 1},
    {name: "LitName2", ordinal: 1, code: 2}},
}

func ComplexEnums() *complexEnums {
	return _complexEnums
}

func (o *complexEnums) Values() []*ComplexEnum {
	return o.values
}

func (o *complexEnums) Literals() []enum.Literal {
	if o.literals == nil {
		o.literals = make([]enum.Literal, len(o.values))
		for i, item := range o.values {
			o.literals[i] = item
		}
	}
	return o.literals
}

func (o *complexEnums) LitName1() *ComplexEnum {
    return _complexEnums.values[0]
}

func (o *complexEnums) LitName2() *ComplexEnum {
    return _complexEnums.values[1]
}

func (o *complexEnums) ParseComplexEnum(name string) (ret *ComplexEnum, ok bool) {
	if item, ok := enum.Parse(name, o.Literals()); ok {
		return item.(*ComplexEnum), ok
	}
	return
}"""))
    }

    private fun context() = LangGoContextFactory(true).buildForImplOnly().builder.invoke(TestModel)
}
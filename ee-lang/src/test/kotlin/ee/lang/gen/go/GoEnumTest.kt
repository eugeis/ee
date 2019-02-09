package ee.lang.gen.go

import ee.common.ext.logger
import ee.lang.gen.kt.TestModel
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
        log.info(out)
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

type simpleEnums struct {
	values []*SimpleEnum
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

func (o *simpleEnums) LitName1() *SimpleEnum {
    return _simpleEnums.values[0]
}

func (o *simpleEnums) LitName2() *SimpleEnum {
    return _simpleEnums.values[1]
}

func (o *simpleEnums) ParseSimpleEnum(name string) (ret *SimpleEnum, ok bool) {
    switch name {
      case "LitName1":
        ret = o.LitName1()
      case "LitName2":
        ret = o.LitName2()
    }
    return
}"""))
    }

    @Test
    fun complexEnumTest() {
        val out = TestModel.ComplexEnum.toGoEnum(context())
        log.info(out)
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

type complexEnums struct {
	values []*ComplexEnum
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

func (o *complexEnums) LitName1() *ComplexEnum {
    return _complexEnums.values[0]
}

func (o *complexEnums) LitName2() *ComplexEnum {
    return _complexEnums.values[1]
}

func (o *complexEnums) ParseComplexEnum(name string) (ret *ComplexEnum, ok bool) {
    switch name {
      case "LitName1":
        ret = o.LitName1()
      case "LitName2":
        ret = o.LitName2()
    }
    return
}"""))
    }

    private fun context() = LangGoContextFactory().buildForImplOnly().builder.invoke(TestModel)
}
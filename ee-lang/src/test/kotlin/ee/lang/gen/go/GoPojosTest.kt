package ee.lang.gen.go

import ee.common.ext.logger
import ee.lang.gen.kt.TestModel
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GoPojosTest {
    val log = logger()

    @BeforeEach
    fun beforeGoPojosTest() {
        TestModel.prepareForGoGeneration()
    }

    @Test
    fun simplePojoTest() {
        val out = TestModel.Trace.toGoImpl(context())
        log.info(out)
        assertThat(out, `is`("""
type Trace struct {
    createdAt time.Time
    updatedAt time.Time
    modifiedBy string
}

func NewTrace(createdAt time.Time, updatedAt time.Time, modifiedBy string) (ret Trace, err error) {
    ret = Trace{
        createdAt: createdAt,
        updatedAt: updatedAt,
        modifiedBy: modifiedBy,
    }
    return
}"""))
    }

    @Test
    fun complexPojoTest() {
        val out = TestModel.Login.toGoImpl(context())
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
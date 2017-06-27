package ee.lang.gen.kt

import ee.common.ext.logger
import ee.lang.gen.go.prepareForGoGeneration
import ee.lang.gen.go.toGoEnum
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GoPojosTest {
    val log = logger()

    @Before
    fun beforeGoPojosTest() {
        TestModel.prepareForGoGeneration()
    }

    @Test
    fun simpleEnumTest() {
        val out = TestModel.SimpleEnum.toGoEnum(context())
        log.info(out)
        Assert.assertThat(out, `is`("""
type SimpleEnum uint

func (o SimpleEnum) String() string {
	return simpleEnumNames[o]
}
func (o SimpleEnum) Values() *[]string {
	return &simpleEnumNames
}

const (
    LIT_NAME1 SimpleEnum = iota
    LIT_NAME2
)

var simpleEnumNames = []string{"LIT_NAME1", "LIT_NAME2"}

func (o SimpleEnum) isLitName1() bool {
    return o == LIT_NAME1
}
func (o SimpleEnum) isLitName2() bool {
    return o == LIT_NAME2
}
func ParseSimpleEnum(name string) (ret SimpleEnum, ok bool) {
    switch (name) {
    case "LIT_NAME1":
        ret = LIT_NAME1
    case "LIT_NAME2":
        ret = LIT_NAME2
    }
    return
}"""))
    }

    @Test
    fun complexEnumTest() {
        val out = TestModel.ComplexEnum.toGoEnum(context())
        log.info(out)
        Assert.assertThat(out, `is`("""
type ComplexEnum uint

func (o ComplexEnum) String() string {
	return complexEnumNames[o]
}
func (o ComplexEnum) Values() *[]string {
	return &complexEnumNames
}

const (
    LIT_NAME1 ComplexEnum = iota
    LIT_NAME2
)

var complexEnumNames = []string{"LIT_NAME1", "LIT_NAME2"}

func (o ComplexEnum) isLitName1() bool {
    return o == LIT_NAME1
}
func (o ComplexEnum) isLitName2() bool {
    return o == LIT_NAME2
}
func ParseComplexEnum(name string) (ret ComplexEnum, ok bool) {
    switch (name) {
    case "LIT_NAME1":
        ret = LIT_NAME1
    case "LIT_NAME2":
        ret = LIT_NAME2
    }
    return
}"""))
    }

    private fun context() = LangKotlinContextFactory().buildForImplOnly().invoke(TestModel)
}
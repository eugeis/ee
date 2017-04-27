package ee.lang.gen.kt

import ee.common.ext.logger
import ee.lang.*
import ee.lang.gen.kt.LangKotlinContextFactory
import ee.lang.gen.kt.prepareForKotlinGeneration
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Test

private object TestModel : StructureUnit() {
    object SimpleEnum : EnumType({ namespace("ee.lang.test") }) {
        val LitName1 = lit()
        val LitName2 = lit()
    }

    object ComplexEnum : EnumType({}) {
        val order = prop(n.Int)

        val LitName1 = lit({ params(p(order) { value(0) }) })
        val LitName2 = lit({ params(p(order) { value(1) }) })
    }
}

class KotlinPojosTest {
    val log = logger()

    @Before
    fun beforeKotlinPojosTest() {
        TestModel.prepareForKotlinGeneration()
    }

    @Test
    fun simpleEnumTest() {
        val out = TestModel.SimpleEnum.toKotlinEnum(context())
        //log.info(out)
        Assert.assertThat(out, `is`("""
enum class SimpleEnum {
    LIT_NAME1,
    LIT_NAME2;

    fun isLitName1() : Boolean = this == LIT_NAME1
    fun isLitName2() : Boolean = this == LIT_NAME2
}"""))
    }

    @Test
    fun complexEnumTest() {
        val out = TestModel.ComplexEnum.toKotlinEnum(context())
        //log.info(out)
        Assert.assertThat(out, `is`("""
enum class ComplexEnum(val order: Int) {
    LIT_NAME1(0),
    LIT_NAME2(1);

    fun isLitName1() : Boolean = this == LIT_NAME1
    fun isLitName2() : Boolean = this == LIT_NAME2
}"""))
    }

    private fun context() = LangKotlinContextFactory().buildForImplOnly().invoke(TestModel)
}
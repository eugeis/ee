package ee.lang.gen.kt

import ee.common.ext.logger
import ee.lang.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

object TestModel : StructureUnit() {
    object SimpleEnum : EnumType({ namespace("ee.lang.test") }) {
        val LitName1 = lit()
        val LitName2 = lit()
    }

    object ComplexEnum : EnumType({}) {
        val code = prop(n.Int)

        val LitName1 = lit { params(p(code) { value(1) }) }
        val LitName2 = lit { params(p(code) { value(2) }) }
    }

    object Trace : CompilationUnit() {
        val createdAt = prop(n.Date)
        val updatedAt = prop(n.Date)
        val modifiedBy = prop()
    }

    object Login : CompilationUnit() {
        val principal = prop()
        val password = prop()
        val disabled = prop { type(n.Boolean) }
        val lastLoginAt = prop { type(n.Date) }
        val trace = prop { type(Trace).meta(true) }
    }
}

class KotlinPojosTest {
    val log = logger()

    @BeforeEach
    fun beforeKotlinPojosTest() {
        TestModel.prepareForKotlinGeneration()
    }

    @Test
    fun simpleEnumTest() {
        val out = TestModel.SimpleEnum.toKotlinEnum(context())
        //log.info(out)
        assertThat(out, `is`("""
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
        assertThat(out, `is`("""
enum class ComplexEnum(val code: Int) {
    LIT_NAME1(1),
    LIT_NAME2(2);

    fun isLitName1() : Boolean = this == LIT_NAME1
    fun isLitName2() : Boolean = this == LIT_NAME2
}"""))
    }

    private fun context() = LangKotlinContextFactory().buildForImplOnly().invoke(TestModel)
}
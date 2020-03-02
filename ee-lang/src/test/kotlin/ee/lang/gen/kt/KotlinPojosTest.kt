package ee.lang.gen.kt

import ee.common.ext.logger
import ee.lang.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger

object TestModel : StructureUnit({ namespace("ee.lang.test") }) {
    object SimpleEnum : EnumType() {
        val LitName1 = lit()
        val LitName2 = lit()
    }

    object ComplexEnum : EnumType() {
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
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
enum class SimpleEnum(
        @JsonValue
        val externalName: String) {
    LIT_NAME1("LitName1"),
    LIT_NAME2("LitName2");

    fun isLitName1(): Boolean = this == LIT_NAME1
    fun isLitName2(): Boolean = this == LIT_NAME2

    companion object {
        fun findByByName(name: String?, orInstance: SimpleEnum = LIT_NAME1): SimpleEnum {
            return name.toSimpleEnumByName(orInstance)
        }

        fun findByByExternalName(externalName: String?, orInstance: SimpleEnum = LIT_NAME1): SimpleEnum {
            return externalName.toSimpleEnumByExternalName(orInstance)
        }
    }    
}

fun String?.toSimpleEnumByName(orInstance: SimpleEnum = SimpleEnum.LIT_NAME1): SimpleEnum {
    val found = SimpleEnum.values().find { 
        this != null && it.name.equals(this, true) 
    }
    return found ?: orInstance
}

fun String?.toSimpleEnumByExternalName(orInstance: SimpleEnum = SimpleEnum.LIT_NAME1): SimpleEnum {
    val found = SimpleEnum.values().find { 
        this != null && it.externalName.equals(this, true) 
    }
    return found ?: orInstance
}
"""))
    }

    @Test
    fun complexEnumTest() {
        val out = TestModel.ComplexEnum.toKotlinEnum(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
enum class ComplexEnum(
        @JsonValue
        val externalName: String,
        val code: Int) {
    LIT_NAME1("LitName1", 1),
    LIT_NAME2("LitName2", 2);

    fun isLitName1(): Boolean = this == LIT_NAME1
    fun isLitName2(): Boolean = this == LIT_NAME2

    companion object {
        fun findByByName(name: String?, orInstance: ComplexEnum = LIT_NAME1): ComplexEnum {
            return name.toComplexEnumByName(orInstance)
        }

        fun findByByExternalName(externalName: String?, orInstance: ComplexEnum = LIT_NAME1): ComplexEnum {
            return externalName.toComplexEnumByExternalName(orInstance)
        }

        fun findByByCode(code: Int?, orInstance: ComplexEnum = LIT_NAME1): ComplexEnum {
            return code.toComplexEnumByCode(orInstance)
        }
    }    
}

fun String?.toComplexEnumByName(orInstance: ComplexEnum = ComplexEnum.LIT_NAME1): ComplexEnum {
    val found = ComplexEnum.values().find { 
        this != null && it.name.equals(this, true) 
    }
    return found ?: orInstance
}

fun String?.toComplexEnumByExternalName(orInstance: ComplexEnum = ComplexEnum.LIT_NAME1): ComplexEnum {
    val found = ComplexEnum.values().find { 
        this != null && it.externalName.equals(this, true) 
    }
    return found ?: orInstance
}

fun Int?.toComplexEnumByCode(orInstance: ComplexEnum = ComplexEnum.LIT_NAME1): ComplexEnum {
    val found = ComplexEnum.values().find { 
        this != null && it.code == this 
    }
    return found ?: orInstance
}
"""))
    }
    private fun context() = LangKotlinContextFactory(true).buildForImplOnly().builder.invoke(TestModel)
}

fun Logger.infoBeforeAfter(out: String) {
    info("before")
    info(out)
    info("after")
}
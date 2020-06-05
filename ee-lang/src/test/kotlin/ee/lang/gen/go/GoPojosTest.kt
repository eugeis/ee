package ee.lang.gen.go

import ee.common.ext.logger
import ee.lang.gen.kt.TestModel
import ee.lang.gen.kt.infoBeforeAfter
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
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
type Trace struct {
    CreatedAt *time.Time
    UpdatedAt *time.Time
    ModifiedBy 
}

func NewTraceDefault() (ret *Trace) {
    ret = &Trace{}
    return
}"""))
    }

    @Test
    fun complexPojoTest() {
        val out = TestModel.Login.toGoImpl(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
type Login struct {
    Principal 
    Password 
    Disabled bool
    LastLoginAt *time.Time
    Trace *Trace
}

func NewLoginDefault() (ret *Login) {
    ret = &Login{}
    return
}"""))
    }

    private fun context() = LangGoContextFactory(true).buildForImplOnly().builder.invoke(TestModel)
}
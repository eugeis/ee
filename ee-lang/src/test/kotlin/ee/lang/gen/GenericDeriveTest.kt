package ee.lang.gen

import ee.lang.CompilationUnit
import ee.lang.GT
import ee.lang.StructureUnit
import ee.lang.TypeI
import ee.lang.gen.kt.prepareForKotlinGeneration
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test

private object TestModel2 : StructureUnit() {
    object Type1 : CompilationUnit() {
    }

    object Type2 : CompilationUnit() {
    }

    object Pojo : CompilationUnit() {
        val prop1 = prop { type(GT(Type1)) }
        val prop2 = prop { type(GT(Type2)) }
    }
}

class GenericDeriveTest {

    @Test
    fun deriveTest() {
        TestModel2.prepareForKotlinGeneration()

        val type = TestModel2.Pojo.prop1.type()
        Assert.assertThat(type.generics()[0].type(), CoreMatchers.sameInstance(TestModel2.Type1 as TypeI<*>))

        val type2 = TestModel2.Pojo.prop2.type()
        Assert.assertThat(type2.generics()[0].type(), CoreMatchers.sameInstance(TestModel2.Type2 as TypeI<*>))
    }
}
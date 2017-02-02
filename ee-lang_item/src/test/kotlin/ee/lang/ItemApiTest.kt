package ee.lang

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.assertThat
import org.junit.Test

class ItemApiTest {

    @Test
    fun deriveTest_equals() {
        val item: Item = Item({ name("ItemTest").namespace("ee.dsl.test") })
        val derived: Item = item.derive()

        assertThat(derived.name(), equalTo("ItemTest"))
        assertThat(derived.namespace(), equalTo("ee.dsl.test"))
    }

    @Test
    fun deriveTest_different() {
        val item = Item({ name("ItemTest").namespace("ee.dsl.test") })
        val derived: Item = item.derive { name("${item.name()}Derived").namespace("${item.namespace()}Derived") }

        assertThat(derived.name(), equalTo("ItemTestDerived"))
        assertThat(derived.namespace(), equalTo("ee.dsl.testDerived"))
    }
}
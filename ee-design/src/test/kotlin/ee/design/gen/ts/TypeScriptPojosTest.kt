package ee.design.gen.ts

import ee.common.ext.logger
import ee.lang.*
import ee.design.*
import ee.lang.gen.ts.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import org.slf4j.Logger


object SimpleComp: Comp({ artifact("ee-lang-test").namespace("ee.lang.test") }) {
    object SimpleModule: Module() {
        // Simple object with basic elements
        object SimpleBasic : Basic() {
            val firstSimpleProperty = propS()
            val anotherSimpleProperty = propS()
            val lastSimpleProperty = propS()
        }

        object SimpleEntity : Entity() {
            val simpleBasicProperties = prop(SimpleBasic)
            val name = propS()
            val birthday = propDT()
            val bool = propB()
            val count = propI()
            val float = propF()
        }

        object SimpleEnum : EnumType() {
            val variable1 = lit()
            val variable2 = lit()
            val variable3 = lit()
        }

        // Object for Testing with Generic
        object GenericEntity : Entity() {
            val elementWithGenericType = prop(n.List.GT(SimpleEntity))
            val elementWithoutGenericType = propS()
        }

        // Object for Testing with Operation
        object EntityWithOperations : Entity() {
            val name = propS()
            val birthday = propDT()
            val contact = prop(OperationComponent)

            val findByFirstName = findBy(contact.sub { firstname })
            val findByLastName = findBy(contact.sub { lastname })
        }

        object OperationComponent : Basic() {
            val firstname = propS()
            val lastname = propS()
        }

        // Object for Testing with Nullables
        object EntityWithNullables : Entity() {
            val nameNotNullable = propS { nullable(false) }
            val nameNullable = propS { nullable(true) }
        }

        // Object for Testing with Empty Constructor
        object EntityWithEmptyConstructor : Entity() {
            val superunit = defineSuperUnitsAsAnonymousProps()
            val constructor = constructorOwnPropsOnly()
        }
    }
}

// Object for Testing with Constructor
object CompWithConstructor: Comp({ artifact("ee-lang-test").namespace("ee.lang.test") }) {
    object ModuleWithConstructor: Module() {
        object EntityWithConstructor : Entity() {
            val superunit = prop {
                type(superUnit().name("superUnitTest"))
            }
            val voidItem = prop {
                type()
            }
        }
    }
}

object ComponentWithoutInterface: Comp({ artifact("ee-lang-test").namespace("ee.lang.test") }) {
    object ModuleWithoutInherit: Module() {
        object BasicWithoutInherit : Basic() {
            val firstSimpleProperty = propS()
            val anotherSimpleProperty = propS()
            val lastSimpleProperty = propS()
        }
    }
}

object ComponentWithInterface: Comp({ artifact("ee-lang-test").namespace("ee.lang.test") }) {
    object ModuleWithInherit: Module() {
        object BasicWithInherit : Basic() {
            val firstSimpleProperty = prop(BasicWithoutInherit)
            val anotherSimpleProperty = propS()
            val lastSimpleProperty = propS()
        }

        object BasicWithoutInherit : Basic() {
            val firstSimpleProperty = propS()
            val anotherSimpleProperty = propS()
            val lastSimpleProperty = propS()
        }
    }
}

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TypeScriptPojosTest {
    val log = logger()

    @BeforeEach
    fun beforeTypeScriptPojosTest() {
        SimpleComp.prepareForTsGeneration()
    }

    @Test
    @Order(1)
    @DisplayName("Empty Test without Derived")
    fun emptyTypeScriptTestWithoutDerived() {
        val out = SimpleComp.toTypeScriptEMPTY(context(), "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("SimpleComp.EMPTY"))
    }

    @Test
    @Order(2)
    @DisplayName("Empty Test with Derived")
    fun emptyTypeScriptTestWithDerived() {
        val out = SimpleComp.toTypeScriptEMPTY(context(), "WithDerived")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("SimpleCompWithDerived.EMPTY"))
    }

    @Test
    @Order(3)
    @DisplayName("Typescript Test Basic Elements")
    fun simpleBasicTest() {
        val out = SimpleComp.SimpleModule.SimpleBasic.toTypeScriptImpl(context(), "", "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            export class SimpleBasic {
                firstSimpleProperty: string
                anotherSimpleProperty: string
                lastSimpleProperty: string
            
                constructor() { 
                }
            }
        """.trimIndent()))
    }

    @Test
    @Order(4)
    @DisplayName("Typescript Test Entity Elements")
    fun simpleEntityTest() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toTypeScriptImpl(context(), "", "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            export class SimpleEntity {
                simpleBasicProperties: SimpleBasic
                name: string
                birthday: Date
                bool: boolean
                count: number
                float: number
            
                constructor() { 
                }
            }
        """.trimIndent()))
    }

    @Test
    @Order(5)
    @DisplayName("Typescript Test Entity Elements with Extend")
    fun simpleEntityTestWithExtends() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toTypeScriptImpl(context(), "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            export class SimpleEntity extends SimpleEntity {
                simpleBasicProperties: SimpleBasic
                name: string
                birthday: Date
                bool: boolean
                count: number
                float: number
            
                constructor() { 
                }
            }
        """.trimIndent()))
    }

    @Test
    @Order(6)
    @DisplayName("Typescript Test Entity Elements with Generics")
    fun simpleEntityTestWithGeneric() {
        val out = SimpleComp.SimpleModule.GenericEntity.toTypeScriptImpl(context(), "", "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            export class GenericEntity {
                elementWithGenericType: Array<SimpleEntity>
                elementWithoutGenericType: string
            
                constructor() { 
                }
            }
        """.trimIndent()))
    }

    @Test
    @Order(7)
    @DisplayName("Typescript Test Entity Elements with Operations")
    fun simpleEntityTestWithOperations() {
        val out = SimpleComp.SimpleModule.EntityWithOperations.toTypeScriptImpl(context(), "", "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            export class EntityWithOperations {
                name: string
                birthday: Date
                contact: OperationComponent
            
                constructor() { 
                }
            
                findByFirstName(firstname: string = ''): EntityWithOperations {
                    throw new ReferenceError('Not implemented yet.');
                }
            
                findByLastName(lastname: string = ''): EntityWithOperations {
                    throw new ReferenceError('Not implemented yet.');
                }
            }
        """.trimIndent()))
    }

    @Test
    @Order(8)
    @DisplayName("Typescript Test Entity Elements with Nullables")
    fun simpleEntityTestWithNullables() {
        val out = SimpleComp.SimpleModule.EntityWithNullables.toTypeScriptImpl(context(), "", "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            export class EntityWithNullables {
                nameNotNullable: string
                nameNullable: string?
            
                constructor() { 
                }
            }
        """.trimIndent()))
    }

    @Test
    @Order(9)
    @DisplayName("Typescript Test Enum Elements")
    fun simpleEnumTest() {
        val out = SimpleComp.SimpleModule.SimpleEnum.toTypeScriptEnum(context(), "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            export enum SimpleEnum {
                VARIABLE1,
                VARIABLE2,
                VARIABLE3
            }
        """.trimIndent()))
    }

    @Test
    @Order(10)
    @DisplayName("Typescript Test Enum Parse Method")
    fun simpleEnumParseMethodTest() {
        val out = SimpleComp.SimpleModule.SimpleEnum.toTypeScriptEnumParseMethod(context(), "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            fun String?.toSimpleEnum(): SimpleEnum {
                return if (this != null) SimpleEnum.valueOf(this) else SimpleEnum.VARIABLE1;
            }
        """.trimIndent()))
    }

    @Test
    @Order(11)
    @DisplayName("Typescript Test Entity Elements Default")
    fun toTypeScriptDefaultTest() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toTypeScriptDefault(context(), "", Attribute.EMPTY)
        log.infoBeforeAfter(out)
        assertThat(out, `is`("new SimpleEntity()".trimIndent()))
    }

    @Test
    @Order(12)
    @DisplayName("Typescript Test with Empty Constructor")
    fun simpleEmptyConstructor() {
        val out = SimpleComp.SimpleModule.EntityWithEmptyConstructor.toTypeScriptImpl(context(), "", "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            export class EntityWithEmptyConstructor {
                @@EMPTY@@: @@EMPTY@@
            
                constructor(@@EMPTY@@: @@EMPTY@@ = @@EMPTY@@.EMPTY) { {
                    this.@@EMPTY@@ = @@EMPTY@@
                }
            }
        """.trimIndent()))
    }

    /*@Test
    @Order(13)
    @DisplayName("Typescript Test with Constructor")
    fun entityWithConstructor() {
        CompWithConstructor.prepareForTsGeneration()
        val out = CompWithConstructor.ModuleWithConstructor.EntityWithConstructor.toTypeScriptImpl(contextConst(), "", "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            export class EntityWithConstructor extends superUnitTest {
                superunit: superUnitTest
                voidItem: void
                superUnitTest: superUnitTest

                constructor(superUnitTest: superUnitTest = superUnitTest.EMPTY) {
                    this.superUnitTest = superUnitTest
                }
            }
        """.trimIndent()))
    }*/
}

private fun context() = LangTsContextFactory().buildForImplOnly().builder.invoke(SimpleComp)
private fun contextConst() = LangTsContextFactory().buildForImplOnly().builder.invoke(CompWithConstructor)

private fun contextCompWithInterface() = LangTsContextFactory().buildForImplOnly().builder.invoke(ComponentWithInterface)

private fun contextCompWithoutInterface() = LangTsContextFactory().buildForImplOnly().builder.invoke(ComponentWithoutInterface)

fun Logger.infoBeforeAfter(out: String) {
    info("before")
    info(out)
    info("after")
}

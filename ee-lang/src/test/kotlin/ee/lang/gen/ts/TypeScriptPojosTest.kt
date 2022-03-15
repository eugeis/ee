package ee.lang.gen.ts

import ee.common.ext.logger
import ee.lang.*
import ee.design.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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

        object EntityWithNullables : Entity() {
            val nameNotNullable = propS { nullable(false) }
            val nameNullable = propS { nullable(true) }
        }
    }
}

class TypeScriptPojosTest {
    val log = logger()

    @BeforeEach
    fun beforeTypeScriptPojosTest() {
        SimpleComp.prepareForTsGeneration()
    }

    @Test
    fun emptyTypeScriptTestWithoutDerived() {
        val out = SimpleComp.toTypeScriptEMPTY(context(), "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("SimpleComp.EMPTY"))
    }

    @Test
    fun emptyTypeScriptTestWithDerived() {
        val out = SimpleComp.toTypeScriptEMPTY(context(), "WithDerived")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("SimpleCompWithDerived.EMPTY"))
    }

    @Test
    fun simpleBasicTest() {
        val out = SimpleComp.SimpleModule.SimpleBasic.toTypeScriptImpl(context(), "", "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            export class SimpleBasic {
                firstSimpleProperty: string
                anotherSimpleProperty: string
                lastSimpleProperty: string
            
                constructor() 
                }
            }
        """.trimIndent()))
    }

    @Test
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

                constructor() 
                }
            }
        """.trimIndent()))
    }

    @Test
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

                constructor() 
                }
            }
        """.trimIndent()))
    }

    @Test
    fun simpleEntityTestWithGeneric() {
        val out = SimpleComp.SimpleModule.GenericEntity.toTypeScriptImpl(context(), "", "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            export class GenericEntity {
                elementWithGenericType: Array<SimpleEntity>
                elementWithoutGenericType: string
            
                constructor() 
                }
            }
        """.trimIndent()))
    }

    @Test
    fun simpleEntityTestWithOperations() {
        val out = SimpleComp.SimpleModule.EntityWithOperations.toTypeScriptImpl(context(), "", "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            export class EntityWithOperations {
                name: string
                birthday: Date
                contact: OperationComponent
            
                constructor() 
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
    fun simpleEntityTestWithNullables() {
        val out = SimpleComp.SimpleModule.EntityWithNullables.toTypeScriptImpl(context(), "", "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            export class EntityWithNullables {
                nameNotNullable: string
                nameNullable: string?
            
                constructor() 
                }
            }
        """.trimIndent()))
    }

    @Test
    fun simpleEnumTest() {
        val out = SimpleComp.SimpleModule.SimpleEnum.toTypeScriptEnum(context(), "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            enum SimpleEnum {
                VARIABLE1,
                VARIABLE2,
                VARIABLE3
            }
        """.trimIndent()))
    }

    @Test
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
    fun toTypeScriptDefaultTest() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toTypeScriptDefault(context(), "", Attribute.EMPTY)
        log.infoBeforeAfter(out)
        assertThat(out, `is`("new SimpleEntity()".trimIndent()))
    }
}

private fun context() = LangTsContextFactory().buildForImplOnly().builder.invoke(SimpleComp)

fun Logger.infoBeforeAfter(out: String) {
    info("before")
    info(out)
    info("after")
}

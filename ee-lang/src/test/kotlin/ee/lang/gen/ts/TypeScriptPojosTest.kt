package ee.lang.gen.ts

import ee.common.ext.logger
import ee.lang.*
import ee.design.*
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
            
                constructor() 
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

                constructor() 
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

                constructor() 
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
            
                constructor() 
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
    @Order(8)
    @DisplayName("Typescript Test Entity Elements with Nullables")
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
    @Order(9)
    @DisplayName("Typescript Test Enum Elements")
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
    @DisplayName("Typescript Test for Generating SCSS Component Without Interface")
    fun toTypeScriptScssComponentTestWithoutInterface() {
        val basics: StructureUnitI<*>.() -> List<BasicI<*>> = {
            findDownByType(BasicI::class.java).filter { it.derivedAsType().isEmpty() }
                .sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }
        ComponentWithoutInterface.prepareForTsGeneration()
        val out = ComponentWithoutInterface.ModuleWithoutInherit.BasicWithoutInherit.
        toScssComponent(basics.invoke(ComponentWithoutInterface).first(),
            contextCompWithoutInterface(), "", "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
        
        :host {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
        }
        
        button {
            display: inline-block;
        }
            
        """.trimIndent()))
    }

    @Test
    @Order(13)
    @DisplayName("Typescript Test for Generating HTML Component Without Interface")
    fun toTypeScriptHtmlComponentTestWithoutInterface() {
        val basics: StructureUnitI<*>.() -> List<BasicI<*>> = {
            findDownByType(BasicI::class.java).filter { it.derivedAsType().isEmpty() }
                .sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }
        ComponentWithoutInterface.prepareForTsGeneration()
        val out = ComponentWithoutInterface.ModuleWithoutInherit.BasicWithoutInherit.
        toHtmlComponent(basics.invoke(ComponentWithoutInterface).first(),
            contextCompWithoutInterface(), "", "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
        <mat-form-field appearance="fill">
            <mat-label>FirstSimpleProperty</mat-label>
            <input matInput id="firstSimpleProperty">
        </mat-form-field>
        
        <mat-form-field appearance="fill">
            <mat-label>AnotherSimpleProperty</mat-label>
            <input matInput id="anotherSimpleProperty">
        </mat-form-field>
        
        <mat-form-field appearance="fill">
            <mat-label>LastSimpleProperty</mat-label>
            <input matInput id="lastSimpleProperty">
        </mat-form-field>
        
        <div>
            <button mat-raised-button (click)="inputElement()">Input</button>
            <mat-form-field appearance="fill">
                <mat-label>Select</mat-label>
                <mat-select (valueChange)="changeIndex(${"$"}event)">
                    <div *ngFor="let item of dataElement; let i = index">
                        <mat-option [value]="i">{{i}}</mat-option>
                    </div>
                </mat-select>
            </mat-form-field>
            <button mat-raised-button (click)="loadElement(index)">Load Value</button>
            <button mat-raised-button (click)="printElement(index)">Check Value</button>
            <button mat-raised-button (click)="editElement(index)">Edit Value</button>
            <button mat-raised-button (click)="deleteElement(index)">Delete</button>
        </div>
            
        """.trimIndent()))
    }

    @Test
    @Order(14)
    @DisplayName("Typescript Test for Generating Typescript Component Without Interface")
    fun toTypeScriptComponentTestWithoutInterface() {
        val basics: StructureUnitI<*>.() -> List<BasicI<*>> = {
            findDownByType(BasicI::class.java).filter { it.derivedAsType().isEmpty() }
                .sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }
        ComponentWithoutInterface.prepareForTsGeneration()
        val out = ComponentWithoutInterface.ModuleWithoutInherit.BasicWithoutInherit.
        toTypeScriptComponent(basics.invoke(ComponentWithoutInterface).first(),
            contextCompWithoutInterface(), "", "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            import { Component, OnInit } from '@angular/core';

            @Component({
              selector: 'app-basicwithoutinherit',
              templateUrl: './basicwithoutinherit.component.html',
              styleUrls: ['./basicwithoutinherit.component.scss']
            })
            
            export class BasicWithoutInheritComponent implements OnInit {
            
              firstSimpleProperty: String
              anotherSimpleProperty: String
              lastSimpleProperty: String
              index = 0;
              dataElement: any[][] = [];
              tempArray: any[][] = [];
            
              constructor() { }
            
              ngOnInit(): void {
              }
              inputElement() {
                this.firstSimpleProperty = this.simplifiedHtmlInputElement('firstSimpleProperty');
                this.anotherSimpleProperty = this.simplifiedHtmlInputElement('anotherSimpleProperty');
                this.lastSimpleProperty = this.simplifiedHtmlInputElement('lastSimpleProperty');
                this.dataElement.push([this.firstSimpleProperty, this.anotherSimpleProperty, this.lastSimpleProperty]);
            
              }
              deleteElement(index: number) {
                this.dataElement[index][0] = '';
                this.dataElement[index][1] = '';
                this.dataElement[index][2] = '';
                this.tempArray = this.dataElement.filter(function(element) {return element[0] !== '' })
                this.dataElement = this.tempArray;
              }
              printElement(index: number) {
                console.log('FirstSimpleProperty Value: ' + this.dataElement[index][0])
                console.log('AnotherSimpleProperty Value: ' + this.dataElement[index][1])
                console.log('LastSimpleProperty Value: ' + this.dataElement[index][2])
              }
              changeIndex(input: number) {
                    this.index = input;
              }
              loadElement(index: number) {
                (<HTMLInputElement>document.getElementById('firstSimpleProperty')).value = this.dataElement[index][0];
                (<HTMLInputElement>document.getElementById('anotherSimpleProperty')).value = this.dataElement[index][1];
                (<HTMLInputElement>document.getElementById('lastSimpleProperty')).value = this.dataElement[index][2];
              }
              editElement(index: number) {
                this.dataElement[index][0] = this.simplifiedHtmlInputElement('firstSimpleProperty');
                this.dataElement[index][1] = this.simplifiedHtmlInputElement('anotherSimpleProperty');
                this.dataElement[index][2] = this.simplifiedHtmlInputElement('lastSimpleProperty');
              }
              simplifiedHtmlInputElement(element: string) {
                    return (<HTMLInputElement>document.getElementById(element)).value;
              }
            
            }
        """.trimIndent()))
    }

    @Test
    @Order(15)
    @DisplayName("Typescript Test for Generating Typescript Component With Interface")
    fun toTypeScriptComponentTestWithInterface() {
        val basics: StructureUnitI<*>.() -> List<BasicI<*>> = {
            findDownByType(BasicI::class.java).filter { it.derivedAsType().isEmpty() }
                .sortedBy { "${it.javaClass.simpleName} ${name()}" }
        }
        ComponentWithInterface.prepareForTsGeneration()
        val out = ComponentWithInterface.ModuleWithInherit.BasicWithInherit.
        toTypeScriptComponent(basics.invoke(ComponentWithInterface).first(),
            contextCompWithoutInterface(), "", "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            import { Component, OnInit } from '@angular/core';
            import { BasicWithoutInherit } from '../../schkola/modulewithinherit/ModulewithinheritApiBase';
            
            @Component({
              selector: 'app-basicwithinherit',
              templateUrl: './basicwithinherit.component.html',
              styleUrls: ['./basicwithinherit.component.scss']
            })
            
            export class BasicWithInheritComponent implements OnInit {
            
              firstSimpleProperty: BasicWithoutInherit = new BasicWithoutInherit()
              anotherSimpleProperty: String
              lastSimpleProperty: String
              index = 0;
              dataElement: any[][] = [];
              tempArray: any[][] = [];
            
              constructor() { }
            
              ngOnInit(): void {
              }
              inputElement() {
                this.firstSimpleProperty.firstSimpleProperty = this.simplifiedHtmlInputElement('firstSimplePropertyFirstSimpleProperty');
                this.firstSimpleProperty.anotherSimpleProperty = this.simplifiedHtmlInputElement('firstSimplePropertyAnotherSimpleProperty');
                this.firstSimpleProperty.lastSimpleProperty = this.simplifiedHtmlInputElement('firstSimplePropertyLastSimpleProperty');
                this.anotherSimpleProperty = this.simplifiedHtmlInputElement('anotherSimpleProperty');
                this.lastSimpleProperty = this.simplifiedHtmlInputElement('lastSimpleProperty');
                this.dataElement.push([this.firstSimpleProperty.firstSimpleProperty, this.firstSimpleProperty.anotherSimpleProperty, this.firstSimpleProperty.lastSimpleProperty, this.anotherSimpleProperty, this.lastSimpleProperty]);
            
              }
              deleteElement(index: number) {
                this.dataElement[index][0] = '';
                this.dataElement[index][1] = '';
                this.dataElement[index][2] = '';
                this.dataElement[index][3] = '';
                this.dataElement[index][4] = '';
                this.tempArray = this.dataElement.filter(function(element) {return element[0] !== '' })
                this.dataElement = this.tempArray;
              }
              printElement(index: number) {
                console.log('FirstSimplePropertyFirstSimpleProperty Value: ' + this.dataElement[index][0])
                console.log('FirstSimplePropertyAnotherSimpleProperty Value: ' + this.dataElement[index][1])
                console.log('FirstSimplePropertyLastSimpleProperty Value: ' + this.dataElement[index][2])
                console.log('AnotherSimpleProperty Value: ' + this.dataElement[index][3])
                console.log('LastSimpleProperty Value: ' + this.dataElement[index][4])
              }
              changeIndex(input: number) {
                    this.index = input;
              }
              loadElement(index: number) {
                (<HTMLInputElement>document.getElementById('firstSimplePropertyFirstSimpleProperty')).value = this.dataElement[index][0];
                (<HTMLInputElement>document.getElementById('firstSimplePropertyAnotherSimpleProperty')).value = this.dataElement[index][1];
                (<HTMLInputElement>document.getElementById('firstSimplePropertyLastSimpleProperty')).value = this.dataElement[index][2];
                (<HTMLInputElement>document.getElementById('anotherSimpleProperty')).value = this.dataElement[index][3];
                (<HTMLInputElement>document.getElementById('lastSimpleProperty')).value = this.dataElement[index][4];
              }
              editElement(index: number) {
                this.dataElement[index][0] = this.simplifiedHtmlInputElement('firstSimplePropertyFirstSimpleProperty');
                this.dataElement[index][1] = this.simplifiedHtmlInputElement('firstSimplePropertyAnotherSimpleProperty');
                this.dataElement[index][2] = this.simplifiedHtmlInputElement('firstSimplePropertyLastSimpleProperty');
                this.dataElement[index][3] = this.simplifiedHtmlInputElement('anotherSimpleProperty');
                this.dataElement[index][4] = this.simplifiedHtmlInputElement('lastSimpleProperty');
              }
              simplifiedHtmlInputElement(element: string) {
                    return (<HTMLInputElement>document.getElementById(element)).value;
              }
            
            }
        """.trimIndent()))
    }

    @Test
    @Order(16)
    @DisplayName("Typescript Test with Empty Constructor")
    fun simpleEmptyConstructor() {
        val out = SimpleComp.SimpleModule.EntityWithEmptyConstructor.toTypeScriptImpl(context(), "", "")
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
            export class EntityWithEmptyConstructor {
                @@EMPTY@@: @@EMPTY@@
            
                constructor(@@EMPTY@@: @@EMPTY@@ = @@EMPTY@@.EMPTY) {
                    this.@@EMPTY@@ = @@EMPTY@@
                }
            }
        """.trimIndent()))
    }

    @Test
    @Order(17)
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
    }
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

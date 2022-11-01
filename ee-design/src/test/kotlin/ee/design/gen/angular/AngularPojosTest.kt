package ee.design.gen.angular

import ee.common.ext.logger
import ee.lang.*
import ee.design.*
import ee.lang.gen.ts.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import org.slf4j.Logger
import toAngularEntityDataService
import toAngularEntityFormTypeScript
import toAngularEntityListHTMLComponent
import toAngularEntityListSCSSComponent
import toAngularEntityListTypeScript
import toAngularEntityViewHTMLComponent
import toAngularEntityViewSCSSComponent
import toAngularEntityViewTypeScript
import toAngularFormHTMLComponent
import toAngularFormSCSSComponent
import toAngularModule
import toAngularModuleHTMLComponent
import toAngularModuleSCSS
import toAngularModuleService
import toAngularModuleTypeScript
import toAngularRoutingModule


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

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AngularPojosTest {
    val log = logger()

    @BeforeEach
    fun beforeAngularPojosTest() {
        SimpleComp.prepareForTsGeneration()
    }

    @Test
    @Order(1)
    @DisplayName("Angular Test for Generating Module TypeScript Component")
    fun toAngularModuleTypeScriptComponentTest() {
        val out = SimpleComp.SimpleModule.toAngularModuleTypeScript(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""import {Component, Input} from '@angular/core';
import {SimpleModuleViewService} from '@simpleComp/SimpleModule/service/simplemodule-module-view.service';

@Component({
  selector: 'app-simplemodule',
  templateUrl: './simplemodule-module-view.component.html',
  styleUrls: ['./simplemodule-module-view.component.scss'],
  providers: [SimpleModuleViewService]
})

export class SimpleModuleViewComponent {

    @Input() pageName = 'SimpleModuleComponent';
       
    constructor(public simplemoduleViewService: SimpleModuleViewService) {}

}"""))
    }

    @Test
    @Order(2)
    @DisplayName("Angular Test for Generating Module TypeScript Service Component")
    fun toAngularModuleTypeScriptServiceComponentTest() {
        val out = SimpleComp.SimpleModule.toAngularModuleService(SimpleComp.modules(), context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""export class SimpleModuleViewService {

    pageElement = ['SimpleModule'];

    tabElement = ['EntityWithNullables', 'EntityWithOperations', 'GenericEntity', 'SimpleEntity'];

    pageName = 'SimpleModuleComponent';
}
"""))
    }

    @Test
    @Order(3)
    @DisplayName("Angular Test for Generating Module HTML Component")
    fun toAngularModuleHTMLComponentTest() {
        val out = SimpleComp.SimpleModule.toAngularModuleHTMLComponent(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""<mat-sidenav-container>
    <mat-sidenav opened="true" disableClose="true" position="end" #drawer
                 [mode]="'side'" [fixedInViewport]="true">
        <mat-nav-list>
            <a *ngFor="let pageName of simplemoduleViewService.pageElement"
               mat-list-item
               routerLinkActive="active-link"
               [routerLink]="'/' + pageName.toLowerCase()"
            >{{pageName.toUpperCase()}}</a>
        </mat-nav-list>
    </mat-sidenav>

    <mat-sidenav-content>
        <mat-toolbar>
            <span>{{pageName}}</span>

            <span class="toolbar-space"></span>

            <button mat-icon-button (click)="drawer.toggle()">
                <mat-icon>menu</mat-icon>
            </button>
        </mat-toolbar>

        <nav mat-tab-nav-bar>
            <div *ngFor="let pageTabsName of simplemoduleViewService.tabElement">
                <a mat-tab-link
                   [routerLink]="'/simplemodule' + '/' + pageTabsName.toLowerCase()"
                   routerLinkActive="active-link"
                >{{pageTabsName.toUpperCase()}}
                </a>
            </div>
        </nav>
    </mat-sidenav-content>
</mat-sidenav-container>"""))
    }

    @Test
    @Order(4)
    @DisplayName("Angular Test for Generating Module SCSS Component")
    fun toAngularModuleSCSSComponentTest() {
        val out = SimpleComp.SimpleModule.toAngularModuleSCSS(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`(""".toolbar-space {
    flex: 1 1 auto;
}

mat-sidenav-container {
    position: relative;
    width: 100%;
    z-index: 2;
}
"""))
    }

    @Test
    @Order(5)
    @DisplayName("Angular Test for Generating Entity List TypeScript Component")
    fun toAngularEntityListTypeScriptComponentTest() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toAngularEntityListTypeScript(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""import {Component, OnInit} from '@angular/core';
import {TableDataService} from '@template/services/data.service';
import {SimpleEntityDataService} from '@simplecomp/simplemodule/simpleentity/service/simpleentity-data.service';

@Component({
  selector: 'app-simpleentity-list',
  templateUrl: './simpleentity-entity-list.component.html',
  styleUrls: ['./simpleentity-entity-list.component.scss'],
  providers: [{provide: TableDataService, useClass: SimpleEntityDataService}]
})

export class SimpleEntityListComponent implements OnInit {

    simpleentity: SimpleEntity = new SimpleEntity();

    tableHeader: Array<String> = [];

    constructor(public simpleentityDataService: SimpleEntityDataService) {}

    ngOnInit(): void {
        this.tableHeader = this.generateTableHeader();
    }

    generateTableHeader() {
        return ['Box', 'Actions', 'firstSimpleProperty', 'anotherSimpleProperty', 'lastSimpleProperty', 'name', 'birthday', 'bool', 'count', 'float'];
    }
}
"""))
    }

    @Test
    @Order(6)
    @DisplayName("Angular Test for Generating Entity List HTML Component")
    fun toAngularEntityListHTMLComponentTest() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toAngularEntityListHTMLComponent(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""<app-simplemodule [pageName]="simpleentityDataService.pageName"></app-simplemodule>
<a class="newButton" [routerLink]="'./new'"
        routerLinkActive="active-link">
    <mat-icon>add_circle_outline</mat-icon> Add New Item
</a>

<ng-container *ngIf="simpleentityDataService.isHidden; else showed">
    <a class="showButton" (click)="simpleentityDataService.toggleHidden()">
        <mat-icon>delete_outline</mat-icon> Delete Multiple Items
    </a>
</ng-container>

<ng-template #showed>
    <a class="deleteButton" (click)="simpleentityDataService.clearMultipleItems(simpleentityDataService.selection.selected); simpleentityDataService.toggleHidden()">
        <mat-icon>delete_outline</mat-icon> Delete Items
    </a>
</ng-template>

<app-table [selection]="simpleentityDataService.selection" [isHidden]="simpleentityDataService.isHidden" [displayedColumns]="tableHeader"></app-table>
"""))
    }

    @Test
    @Order(7)
    @DisplayName("Angular Test for Generating Entity List SCSS Component")
    fun toAngularEntityListSCSSComponentTest() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toAngularEntityListSCSSComponent(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""app-table {
    position: absolute;
    width: 80% !important;
    z-index: 1;
    top: 30%;
    left: 10%;
}

a {
    text-decoration: none;
    border: 0;
    background: white;
    color: black;
    cursor: pointer;
}

.newButton {
    position: absolute;
    top: 20%;
    left: 30%;
}

.deleteButton, .showButton {
    position: absolute;
    top: 20%;
    left: 50%;
}

@media screen and (max-width: 1000px) {
    app-table {
        max-width: 70%;
    }
}

@media screen and (max-width: 585px) {
    app-table {
        max-width: 60%;
    }

    .newButton {
        position: absolute;
        top: 20%;
        left: 10%;
    }

    .deleteButton, .showButton {
        position: absolute;
        top: 20%;
        left: 40%;
    }
}
"""))
    }

    @Test
    @Order(8)
    @DisplayName("Angular Test for Generating Entity View TypeScript Component")
    fun toAngularEntityViewTypeScriptComponentTest() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toAngularEntityViewTypeScript(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""import {Component, OnInit} from '@angular/core';
import {TableDataService} from '@template/services/data.service';
import {SimpleEntityDataService} from '@simplecomp/simplemodule/simpleentity/service/simpleentity-data.service';

@Component({
  selector: 'app-simpleentity-view',
  templateUrl: './simpleentity-entity-view.component.html',
  styleUrls: ['./simpleentity-entity-view.component.scss'],
  providers: [{provide: TableDataService, useClass: SimpleEntityDataService}]
})

export class SimpleEntityViewComponent implements OnInit {

    simpleentity: SimpleEntity;

    constructor(public simpleentityDataService: SimpleEntityDataService) {}

    ngOnInit(): void {
        this.simpleentity = this.simpleentityDataService.getFirst();
        this.simpleentityDataService.checkRoute(this.simpleentity);
    }
}
"""))
    }

    @Test
    @Order(9)
    @DisplayName("Angular Test for Generating Entity View HTML Component")
    fun toAngularEntityViewHTMLComponentTest() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toAngularEntityViewHTMLComponent(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""<app-simplemodule [pageName]="simpleentityDataService.pageName"></app-simplemodule>

<app-simpleentity-form [simpleentity]="simpleentity"></app-simpleentity-form>

<app-button [element]="simpleentity" [isEdit]="simpleentityDataService.isEdit" [itemIndex]="simpleentityDataService.itemIndex"></app-button>
"""))
    }

    @Test
    @Order(10)
    @DisplayName("Angular Test for Generating Entity View SCSS Component")
    fun toAngularEntityViewSCSSComponentTest() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toAngularEntityViewSCSSComponent(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""app-button {
    position: relative;
    left: 10%;
}

"""))
    }

    @Test
    @Order(10)
    @DisplayName("Angular Test for Generating Entity Form TypeScript Component")
    fun toAngularEntityFormTypeScriptComponentTest() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toAngularEntityFormTypeScript(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""import {Component, OnInit, Input} from '@angular/core';
import {TableDataService} from '@template/services/data.service';
import {SimpleEntityDataService} from '@simplecomp/simplemodule/simpleentity/service/simpleentity-data.service';

@Component({
  selector: 'app-simpleentity-form',
  templateUrl: './simpleentity-entity-form.component.html',
  styleUrls: ['./simpleentity-entity-form.component.scss'],
  
})

export class SimpleEntityFormComponent implements OnInit {


    @Input() simpleentity: SimpleEntity;

    constructor(public simpleentityDataService: SimpleEntityDataService) {}

    ngOnInit(): void {
        if (this.simpleentity.simpleBasicProperties === undefined) {
            this.simpleentity.simpleBasicProperties = new SimpleBasic();
        }
    }
}
"""))
    }

    @Test
    @Order(11)
    @DisplayName("Angular Test for Generating Entity Form HTML Component")
    fun toAngularEntityFormHTMLComponentTest() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toAngularFormHTMLComponent(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
<div>
    <form class="simpleentity-form">
        <fieldset>
            <legend>SimpleEntity</legend>
            
            <mat-form-field appearance="outline">
                <mat-label>name</mat-label>
                <input matInput name="name" [(ngModel)]="simpleentity.name">
            </mat-form-field>

            <mat-form-field appearance="outline">
                <mat-label>birthday</mat-label>
                <input matInput [matDatepicker]="picker" [(ngModel)]="simpleentity.birthday">
                <mat-hint>MM/DD/YYYY</mat-hint>
                <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
                <mat-datepicker #picker></mat-datepicker>
            </mat-form-field>

            <mat-form-field appearance="outline">
                <mat-label>bool</mat-label>
                <mat-select [(value)]="simpleentity.bool">
                    <mat-option *ngFor="let item of ['true', 'false']" [value]="item">{{item}}</mat-option>
                </mat-select>
            </mat-form-field>


    
            
        </fieldset>
        
        <app-simplebasic [parentName]="'SimpleEntity'" [simplebasic]="simpleentity.simpleBasicProperties"></app-simplebasic>





    </form>
</div>
"""))
    }

    @Test
    @Order(12)
    @DisplayName("Angular Test for Generating Entity Form SCSS Component")
    fun toAngularEntityFormSCSSComponentTest() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toAngularFormSCSSComponent(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""form {
    position: relative;
    max-width: 80%;
    z-index: 1;
    left: 10%;
}

fieldset {
    width: 80%;
    padding: 20px;
    border: round(30) 1px;

    .mat-form-field {
        padding: 10px 0;
    }
}

@media screen and (max-width: 650px) {
    form {
        left: 5%;
    }
}

@media screen and (max-width: 480px) {
    form {
        left: 5%;
        max-width: 50%;
    }
}
"""))
    }

    @Test
    @Order(13)
    @DisplayName("Angular Test for Generating Entity Data Service Component")
    fun toAngularEntityDataServiceComponentTest() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toAngularEntityDataService(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""import {Injectable} from '@angular/core';
import {TableDataService} from '@template/services/data.service';
import {SelectionModel} from '@angular/cdk/collections';

@Injectable()
export class SimpleEntityDataService extends TableDataService {
    itemName = 'simpleentity';

    pageName = 'SimpleEntityComponent';
    
    isHidden = true;
    
    

    selection = new SelectionModel<any>(true, []);

    getFirst() {
        return new SimpleEntity();
    }
    
    toggleHidden() {
        this.isHidden = !this.isHidden;
    }
    
    
}
"""))
    }

    @Test
    @Order(14)
    @DisplayName("Angular Test for Generating Module Component")
    fun toAngularModuleComponentTest() {
        val out = SimpleComp.SimpleModule.toAngularModule(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""import { NgModule } from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {SimpleModuleRoutingModules} from './simplemodule-routing.module';
import {CommonModule} from '@angular/common';
import {TemplateModule} from '@template/template.module';
import {MaterialModule} from '@template/material.module';

import {SimpleModuleViewComponent} from './components/view/simplemodule-module-view.component';
import {EntityWithNullablesViewComponent} from '@simpleComp/simplemodule/entitywithnullables/components/view/entitywithnullables-entity-view.component';
import {EntityWithNullablesListComponent} from '@simpleComp/simplemodule/entitywithnullables/components/list/entitywithnullables-entity-list.component';
import {EntityWithNullablesFormComponent} from '@simpleComp/simplemodule/entitywithnullables/components/form/entitywithnullables-entity-form.component';
import {EntityWithOperationsViewComponent} from '@simpleComp/simplemodule/entitywithoperations/components/view/entitywithoperations-entity-view.component';
import {EntityWithOperationsListComponent} from '@simpleComp/simplemodule/entitywithoperations/components/list/entitywithoperations-entity-list.component';
import {EntityWithOperationsFormComponent} from '@simpleComp/simplemodule/entitywithoperations/components/form/entitywithoperations-entity-form.component';
import {GenericEntityViewComponent} from '@simpleComp/simplemodule/genericentity/components/view/genericentity-entity-view.component';
import {GenericEntityListComponent} from '@simpleComp/simplemodule/genericentity/components/list/genericentity-entity-list.component';
import {GenericEntityFormComponent} from '@simpleComp/simplemodule/genericentity/components/form/genericentity-entity-form.component';
import {SimpleEntityViewComponent} from '@simpleComp/simplemodule/simpleentity/components/view/simpleentity-entity-view.component';
import {SimpleEntityListComponent} from '@simpleComp/simplemodule/simpleentity/components/list/simpleentity-entity-list.component';
import {SimpleEntityFormComponent} from '@simpleComp/simplemodule/simpleentity/components/form/simpleentity-entity-form.component';
import {OperationComponentComponent} from '@simpleComp/simplemodule/basics/operationcomponent/operationcomponent-basic.component';
import {SimpleBasicComponent} from '@simpleComp/simplemodule/basics/simplebasic/simplebasic-basic.component';

@NgModule({
    declarations: [
        SimpleModuleViewComponent,
        EntityWithNullablesViewComponent,
        EntityWithNullablesListComponent,
        EntityWithNullablesFormComponent,
        EntityWithOperationsViewComponent,
        EntityWithOperationsListComponent,
        EntityWithOperationsFormComponent,
        GenericEntityViewComponent,
        GenericEntityListComponent,
        GenericEntityFormComponent,
        SimpleEntityViewComponent,
        SimpleEntityListComponent,
        SimpleEntityFormComponent,
        OperationComponentComponent,
        SimpleBasicComponent
    ],
    imports: [
        SimpleModuleRoutingModules,
        TemplateModule,
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        MaterialModule,
    ],
    providers: [],
    exports: [
        EntityWithNullablesFormComponent,
        EntityWithOperationsFormComponent,
        GenericEntityFormComponent,
        SimpleEntityFormComponent,
        OperationComponentComponent,
        SimpleBasicComponent
    ]
})
export class SimpleModuleModule {}"""))
    }

    @Test
    @Order(15)
    @DisplayName("Angular Test for Generating Routing Module Component")
    fun toAngularRoutingModuleComponentTest() {
        val out = SimpleComp.SimpleModule.toAngularRoutingModule(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import {SimpleModuleViewComponent} from './components/view/simplemodule-module-view.component';
import {EntityWithNullablesViewComponent} from '@simpleComp/simplemodule/entitywithnullables/components/view/entitywithnullables-entity-view.component';
import {EntityWithNullablesListComponent} from '@simpleComp/simplemodule/entitywithnullables/components/list/entitywithnullables-entity-list.component';
import {EntityWithOperationsViewComponent} from '@simpleComp/simplemodule/entitywithoperations/components/view/entitywithoperations-entity-view.component';
import {EntityWithOperationsListComponent} from '@simpleComp/simplemodule/entitywithoperations/components/list/entitywithoperations-entity-list.component';
import {GenericEntityViewComponent} from '@simpleComp/simplemodule/genericentity/components/view/genericentity-entity-view.component';
import {GenericEntityListComponent} from '@simpleComp/simplemodule/genericentity/components/list/genericentity-entity-list.component';
import {SimpleEntityViewComponent} from '@simpleComp/simplemodule/simpleentity/components/view/simpleentity-entity-view.component';
import {SimpleEntityListComponent} from '@simpleComp/simplemodule/simpleentity/components/list/simpleentity-entity-list.component';

const routes: Routes = [
    { path: '', component: SimpleModuleViewComponent },
    { path: 'entitywithnullables', component: EntityWithNullablesListComponent },
    { path: 'entitywithnullables/new', component: EntityWithNullablesViewComponent },
    { path: 'entitywithnullables/edit/:id', component: EntityWithNullablesViewComponent },
    { path: 'entitywithoperations', component: EntityWithOperationsListComponent },
    { path: 'entitywithoperations/new', component: EntityWithOperationsViewComponent },
    { path: 'entitywithoperations/edit/:id', component: EntityWithOperationsViewComponent },
    { path: 'genericentity', component: GenericEntityListComponent },
    { path: 'genericentity/new', component: GenericEntityViewComponent },
    { path: 'genericentity/edit/:id', component: GenericEntityViewComponent },
    { path: 'simpleentity', component: SimpleEntityListComponent },
    { path: 'simpleentity/new', component: SimpleEntityViewComponent },
    { path: 'simpleentity/edit/:id', component: SimpleEntityViewComponent }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class SimpleModuleRoutingModules {}

"""))
    }
}

private fun context() = LangTsContextFactory().buildForImplOnly().builder.invoke(SimpleComp)

fun Logger.infoBeforeAfter(out: String) {
    info("before")
    info(out)
    info("after")
}

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
import toAngularDefaultSCSS
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
            val otherModuleEntity = prop(OtherSimpleModule.OtherSimpleEntity)
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
    object OtherSimpleModule: Module() {
        // Simple object with basic elements
        object OtherSimpleBasic : Basic() {
            val firstSimpleProperty = propS()
            val anotherSimpleProperty = propS()
            val lastSimpleProperty = propS()
        }

        object OtherSimpleEntity : Entity() {
            val simpleBasicProperties = prop(OtherSimpleBasic)
            val name = propS()
            val birthday = propDT()
            val bool = propB()
            val count = propI()
            val float = propF()
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

    pageElement = ['OtherSimpleModule', 'SimpleModule'];

    tabElement = ['EntityWithNullables', 'EntityWithOperations', 'GenericEntity', 'SimpleEntity'];

    pageName = 'SimpleModule';
}
"""))
    }

    @Test
    @Order(3)
    @DisplayName("Angular Test for Generating Module HTML Component")
    fun toAngularModuleHTMLComponentTest() {
        val out = SimpleComp.SimpleModule.toAngularModuleHTMLComponent(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""<app-page [pageName]="simplemoduleViewService.pageName" [pageElement]="simplemoduleViewService.pageElement" [tabElement]="simplemoduleViewService.tabElement"></app-page>
"""))
    }

    @Test
    @Order(4)
    @DisplayName("Angular Test for Generating Default/ Module SCSS Component")
    fun toAngularModuleSCSSComponentTest() {
        val out = SimpleComp.SimpleModule.toAngularDefaultSCSS(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""host{}"""))
    }

    @Test
    @Order(5)
    @DisplayName("Angular Test for Generating Entity List TypeScript Component")
    fun toAngularEntityListTypeScriptComponentTest() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toAngularEntityListTypeScript(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {TableDataService} from '@template/services/data.service';
import {SimpleEntityDataService} from '@simplecomp/simplemodule/simpleentity/service/simpleentity-data.service';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';

@Component({
  selector: 'app-simpleentity-list',
  templateUrl: './simpleentity-entity-list.component.html',
  styleUrls: ['./simpleentity-entity-list.component.scss'],
  providers: [{provide: TableDataService, useClass: SimpleEntityDataService}]
})

export class SimpleEntityListComponent implements OnInit, AfterViewInit {

    simpleentity: SimpleEntity = new SimpleEntity();

    tableHeader: Array<String> = [];
    
    elementValue: MatTableDataSource<any>;
    
    @ViewChild(MatSort) sort: MatSort;

    constructor(public simpleentityDataService: SimpleEntityDataService) {}

    ngAfterViewInit() {
        this.simpleentityDataService.dataSources.sort = this.sort;
    }
    ngOnInit(): void {
        this.tableHeader = this.generateTableHeader();
        this.simpleentityDataService.checkSearchRoute();
        if (this.simpleentityDataService.isSearch) {
            this.simpleentityDataService.loadSearchData()
        } else {
            this.simpleentityDataService.dataSources =
                new MatTableDataSource(this.simpleentityDataService.changeMapToArray(
                    this.simpleentityDataService.retrieveItemsForTableList()));
        }
        this.elementValue = new MatTableDataSource(this.simpleentityDataService.changeMapToArray(
            this.simpleentityDataService.retrieveItemsFromCache()));
    }

    generateTableHeader() {
        return ['Box', 'Actions', 'simpleBasicProperties-firstSimpleProperty', 'simpleBasicProperties-anotherSimpleProperty', 'simpleBasicProperties-lastSimpleProperty', 'name', 'birthday', 'bool', 'count', 'float', 'othermoduleentity-entity'];
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
        assertThat(out, `is`("""<app-simplemodule></app-simplemodule>
<div class="simpleentity-list-button">
    <a class="newButton" [routerLink]="'./new'"
            routerLinkActive="active-link">
        <mat-icon>add_circle_outline</mat-icon> {{"add" | translate}} {{"new" | translate}} {{"item" | translate}}
    </a>
    
    <ng-container *ngIf="simpleentityDataService.isHidden; else showed">
        <a class="showButton" (click)="simpleentityDataService.toggleHidden()">
            <mat-icon>delete_outline</mat-icon> {{"delete" | translate}}...
        </a>
    </ng-container>
    
    <ng-template #showed>
        <a class="deleteButton" (click)="simpleentityDataService.clearMultipleItems(simpleentityDataService.selection.selected); simpleentityDataService.toggleHidden()">
            <mat-icon>delete_outline</mat-icon> {{"delete" | translate}} {{"item" | translate}}
        </a>
    </ng-template>
    
    <mat-form-field class="filter">
        <mat-label>{{"filter" | translate}}</mat-label>
        <input matInput (keyup)="simpleentityDataService.applyFilter(${'$'}event)" placeholder="Input Filter..." [ngModel]="simpleentityDataService.filterValue">
    </mat-form-field>
</div>

<div class="mat-elevation-z8 simpleentity-list" style="overflow-x: scroll">
    <table mat-table matSort [dataSource]="simpleentityDataService.dataSources">
        <ng-container matColumnDef="Box">
            <th mat-header-cell *matHeaderCellDef>
                <section [style.visibility]="simpleentityDataService.isHidden? 'hidden': 'visible'">
                    <mat-checkbox color="warn"
                                  (change)="${'$'}event ? simpleentityDataService.masterToggle() : null"
                                  [checked]="simpleentityDataService.selection.hasValue() && simpleentityDataService.allRowsSelected()"
                                  [indeterminate]="simpleentityDataService.selection.hasValue() && !simpleentityDataService.allRowsSelected()"></mat-checkbox>
                </section>
            </th>
            <td mat-cell *matCellDef="let element; let i = index" [attr.data-label]="'box'">
                <section [style.visibility]="simpleentityDataService.isHidden? 'hidden': 'visible'">
                    <mat-checkbox color="warn"
                                  (click)="${'$'}event.stopPropagation()"
                                  (change)="${'$'}event ? simpleentityDataService.selection.toggle(element) : null"
                                  [checked]="simpleentityDataService.selection.isSelected(element)"></mat-checkbox>
                </section>
            </td>
        </ng-container>

        <ng-container matColumnDef="Actions">
            <th mat-header-cell *matHeaderCellDef> {{"table.action" | translate}} </th>
            <td mat-cell *matCellDef="let element; let i = index" [attr.data-label]="'actions'">
                <mat-menu #appMenu="matMenu">
                    <ng-template matMenuContent>
                        <button mat-menu-item (click)="simpleentityDataService.editItems(i, element)"><mat-icon>edit</mat-icon>
                            <span>{{"edit" | translate}}</span></button>
                        <button mat-menu-item (click)="simpleentityDataService.removeItem(element)"><mat-icon>delete</mat-icon>
                            <span>{{"delete" | translate}}</span></button>
                    </ng-template>
                </mat-menu>

                <button mat-icon-button [matMenuTriggerFor]="appMenu">
                    <mat-icon>more_vert</mat-icon>
                </button>
            </td>
        </ng-container>
        
        
        <ng-container matColumnDef="simpleBasicProperties-firstSimpleProperty">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.firstsimpleproperty" | translate}} </th>
            <td mat-cell *matCellDef="let element"> {{element['simpleBasicProperties-firstSimpleProperty']}} </td>
        </ng-container>

        <ng-container matColumnDef="simpleBasicProperties-anotherSimpleProperty">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.anothersimpleproperty" | translate}} </th>
            <td mat-cell *matCellDef="let element"> {{element['simpleBasicProperties-anotherSimpleProperty']}} </td>
        </ng-container>

        <ng-container matColumnDef="simpleBasicProperties-lastSimpleProperty">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.lastsimpleproperty" | translate}} </th>
            <td mat-cell *matCellDef="let element"> {{element['simpleBasicProperties-lastSimpleProperty']}} </td>
        </ng-container>

        <ng-container matColumnDef="name">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.name" | translate}} </th>
            <td mat-cell *matCellDef="let element"> {{element['name']}} </td>
        </ng-container>

        <ng-container matColumnDef="birthday">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.birthday" | translate}} </th>
            <td mat-cell *matCellDef="let element"> {{element['birthday'] | DateTimeTranslationPipe}} </td>
        </ng-container>

        <ng-container matColumnDef="bool">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.bool" | translate}} </th>
            <td mat-cell *matCellDef="let element"> {{element['bool']}} </td>
        </ng-container>

        <ng-container matColumnDef="count">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.count" | translate}} </th>
            <td mat-cell *matCellDef="let element"> {{element['count']}} </td>
        </ng-container>

        <ng-container matColumnDef="float">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.float" | translate}} </th>
            <td mat-cell *matCellDef="let element"> {{element['float']}} </td>
        </ng-container>

        <ng-container matColumnDef="othermoduleentity-entity">
            <th mat-header-cell mat-sort-header *matHeaderCellDef> {{"table.othermoduleentity" | translate}}</th>
            <td mat-cell *matCellDef="let element; let i = index"> <a (click)="simpleentityDataService.searchItems(i, element['othermoduleentity'], 'othersimplemodule/othersimpleentity', 'simpleentity')">{{elementValue.data[i]['othermoduleentity-name']}}</a> </td>
        </ng-container>


        <tr mat-header-row *matHeaderRowDef="tableHeader"></tr>
        <tr mat-row *matRowDef="let row; columns: tableHeader;"></tr>
    </table>
</div>
"""))
    }

    @Test
    @Order(7)
    @DisplayName("Angular Test for Generating Entity List SCSS Component")
    fun toAngularEntityListSCSSComponentTest() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toAngularEntityListSCSSComponent(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""@import "src/styles";

.simpleentity-list {
    @extend .entity-list;
    position: absolute;
    width: 80% !important;
    z-index: 1;
    top: 40%;
    left: 10%;
}

.simpleentity-list-button {
    @extend .entity-list-button
}

a {
    @extend .entity-link
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
        assertThat(out, `is`("""<app-simplemodule></app-simplemodule>

<app-simpleentity-form [simpleentity]="simpleentity"></app-simpleentity-form>

<app-button [element]="simpleentity" [entityElements]="simpleentityDataService.entityElements" [isEdit]="simpleentityDataService.isEdit"></app-button>
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
import {SimpleEntityDataService} from '@simplecomp/simplemodule/simpleentity/service/simpleentity-data.service';
import {OtherSimpleEntityDataService} from '@simplecomp/othersimplemodule/othersimpleentity/service/othersimpleentity-data.service';

    
@Component({
  selector: 'app-simpleentity-form',
  templateUrl: './simpleentity-entity-form.component.html',
  styleUrls: ['./simpleentity-entity-form.component.scss'],
  
})

export class SimpleEntityFormComponent implements OnInit {

    @Input() simpleentity: SimpleEntity;

    constructor(public simpleentityDataService: SimpleEntityDataService, 
        public othersimpleentityDataService: OtherSimpleEntityDataService, 
) {}
    ngOnInit(): void {
        if (this.simpleentity.simpleBasicProperties === undefined) {
            this.simpleentity.simpleBasicProperties = new SimpleBasic();
        }
        if (this.simpleentity.otherModuleEntity === undefined) {
            this.simpleentity.otherModuleEntity = new OtherSimpleEntity();
        }
    
        this.simpleentityDataService.optionOtherSimpleEntity = this.simpleentityDataService.changeMapToArray(this.othersimpleentityDataService.retrieveItemsFromCache()); 

    
    this.simpleentityDataService.initObservable();
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
<div class="simpleentity-form">
    <form>
        <fieldset>
            <legend>{{"table.simpleentity" | translate}}</legend>
            
            <mat-form-field appearance="outline">
                <mat-label>{{"table.name" | translate}}</mat-label>
                <input matInput name="name" [(ngModel)]="simpleentity.name">
            </mat-form-field>

            <mat-form-field appearance="outline">
                <mat-label>{{"table.birthday" | translate}}</mat-label>
                <input matInput [matDatepicker]="picker" [(ngModel)]="simpleentity.birthday" [ngModel]="simpleentity.birthday | date: 'yyyy-MM-dd'">
                <mat-hint>MM/DD/YYYY</mat-hint>
                <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
                <mat-datepicker #picker></mat-datepicker>
            </mat-form-field>

            <mat-form-field appearance="outline">
                <mat-label>{{"table.bool" | translate}}</mat-label>
                <mat-select [(value)]="simpleentity.bool">
                    <mat-option *ngFor="let item of ['true', 'false']" [value]="item">{{item}}</mat-option>
                </mat-select>
            </mat-form-field>

            <mat-form-field appearance="outline">
                <mat-label>{{"table.count" | translate}}</mat-label>
                <input matInput name="count" type="number" [(ngModel)]="simpleentity.count">
            </mat-form-field>

            <mat-form-field appearance="outline">
                <mat-label>{{"table.float" | translate}}</mat-label>
                <input matInput name="float" type="number" [(ngModel)]="simpleentity.float">
            </mat-form-field>
    
            
        </fieldset>
        
        <app-simplebasic [parentName]="'simpleentity'" [simplebasic]="simpleentity.simpleBasicProperties"></app-simplebasic>






        <fieldset>
            <legend>{{"table.othersimpleentity" | translate}}</legend>
            <mat-form-field appearance="fill">
                <mat-label>{{"select" | translate}} {{"table.othersimpleentity" | translate}}</mat-label>
                <input type="text" matInput [formControl]="simpleentityDataService.controlOtherSimpleEntity" [matAutocomplete]="autoOtherSimpleEntity" [(ngModel)]="simpleentity.otherModuleEntity">
                <mat-autocomplete #autoOtherSimpleEntity="matAutocomplete" [displayWith]="simpleentityDataService.displayOtherSimpleEntity">
                    <mat-option *ngFor="let option of simpleentityDataService.filteredOptionsOtherSimpleEntity | async" [value]="option">
                        {{option.name}}
                    </mat-option>
                </mat-autocomplete>
            </mat-form-field>
        </fieldset>
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
        assertThat(out, `is`("""@import "src/styles";

.simpleentity-form {
    @extend .entity-form
}

app-simplebasic-form {
    position: relative;
    left: -10%;
}






app-othersimpleentity-form {
    position: relative;
    left: -10%;
}

"""))
    }

    @Test
    @Order(13)
    @DisplayName("Angular Test for Generating Entity Data Service Component")
    fun toAngularEntityDataServiceComponentTest() {
        val out = SimpleComp.SimpleModule.SimpleEntity.toAngularEntityDataService(context())
        log.infoBeforeAfter(out)
        assertThat(out, `is`("""
import {OtherSimpleEntity} from '@simplecomp/othersimplemodule/OtherSimpleModuleApiBase';

import {Injectable} from '@angular/core';
import {TableDataService} from '@template/services/data.service';
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class SimpleEntityDataService extends TableDataService {
    itemName = 'simpleentity';

    pageName = 'SimpleEntityComponent';
    
    isHidden = true;
    
    entityElements = ['otherModuleEntity',];   
    
    
    controlOtherSimpleEntity = new FormControl<OtherSimpleEntity>(new OtherSimpleEntity());
    optionOtherSimpleEntity: Array<OtherSimpleEntity>;
    filteredOptionsOtherSimpleEntity: Observable<OtherSimpleEntity[]>;

    
    

    getFirst() {
        return new SimpleEntity();
    }
    
    toggleHidden() {
        this.isHidden = !this.isHidden;
    }
    
    
    displayOtherSimpleEntity(otherSimpleEntity: OtherSimpleEntity): string {
        return otherSimpleEntity ? otherSimpleEntity.name : '';
    }
    
    filterOtherSimpleEntity(name: string, array: Array<OtherSimpleEntity>): OtherSimpleEntity[] {
        return array.filter(option => option.name.toLowerCase().includes(name.toLowerCase()));
    }

    
    initObservable() {
    
        this.filteredOptionsOtherSimpleEntity = this.controlOtherSimpleEntity.valueChanges.pipe(
            startWith(''),
            map((value: OtherSimpleEntity) => {
                const name = typeof value === 'string' ? value : value.name;
                return name ?
                    this.filterOtherSimpleEntity(name as string, this.optionOtherSimpleEntity)
                    : this.optionOtherSimpleEntity.slice();
            }),
        );

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

import {HttpClient} from '@angular/common/http';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {TemplateTranslateService} from '@template/services/translate.service';
import {TranslateLoader, TranslateModule, TranslateService} from '@ngx-translate/core';

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
import {SimpleEnumEnumComponent} from '@simpleComp/simplemodule/enums/simpleenum/simpleenum-enum.component';
import {OtherSimpleModuleModule} from '@simplecomp/othersimplemodule/othersimplemodule-model.module';
    

export function HttpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http);
}

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
        SimpleBasicComponent,
        SimpleEnumEnumComponent,
    ],
    imports: [
        SimpleModuleRoutingModules,
        TemplateModule,
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        MaterialModule,
        TranslateModule.forChild({
            loader: {provide: TranslateLoader, useFactory: HttpLoaderFactory, deps: [HttpClient]},
        }),
        OtherSimpleModuleModule,
        
    ],
    providers: [
        { provide: TranslateService, useExisting: TemplateTranslateService }
    ],
    exports: [
        EntityWithNullablesFormComponent,
        EntityWithOperationsFormComponent,
        GenericEntityFormComponent,
        SimpleEntityFormComponent,
        OperationComponentComponent,
        SimpleBasicComponent,
        SimpleEnumEnumComponent,
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
    { path: 'entitywithnullables/search', component: EntityWithNullablesListComponent },
    { path: 'entitywithoperations', component: EntityWithOperationsListComponent },
    { path: 'entitywithoperations/new', component: EntityWithOperationsViewComponent },
    { path: 'entitywithoperations/edit/:id', component: EntityWithOperationsViewComponent },
    { path: 'entitywithoperations/search', component: EntityWithOperationsListComponent },
    { path: 'genericentity', component: GenericEntityListComponent },
    { path: 'genericentity/new', component: GenericEntityViewComponent },
    { path: 'genericentity/edit/:id', component: GenericEntityViewComponent },
    { path: 'genericentity/search', component: GenericEntityListComponent },
    { path: 'simpleentity', component: SimpleEntityListComponent },
    { path: 'simpleentity/new', component: SimpleEntityViewComponent },
    { path: 'simpleentity/edit/:id', component: SimpleEntityViewComponent },
    { path: 'simpleentity/search', component: SimpleEntityListComponent }
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

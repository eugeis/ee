import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.*

fun <T : CompilationUnitI<*>> T.toAngularModuleTSComponent(items: ModuleI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                           api: String = LangDerivedKind.API): String {
    return """import {Component, Input} from '@angular/core';
${items.toTypeScriptModuleImportServices(items)}
${items.toTypeScriptModuleGenerateComponentPart(items)}
${isOpen().then("export ")}class ${items.name()}ViewComponent {${"\n"}
${items.toTypeScriptModuleInputElement("pageName", tab , items)}       
${items.toTypeScriptModuleConstructor(tab, items)}
}"""
}

fun <T : CompilationUnitI<*>> T.toAngularModuleHTMLComponent(items: ModuleI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                             api: String = LangDerivedKind.API): String {
    return items.toAngularModuleHTML(items)
}

fun <T : CompilationUnitI<*>> T.toAngularModuleSCSSComponent(items: ModuleI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                             api: String = LangDerivedKind.API): String {
    return items.toAngularDefaultSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularModuleService(items: ModuleI<*>, modules: List<ModuleI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                       api: String = LangDerivedKind.API): String {
    return """${isOpen().then("export ")}class ${items.name()}ViewService {

    pageElement = [${modules.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(", ") { """'${it.name()}'""" }}];

    tabElement = [${items.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString() {
        it.toAngularModuleArrayElement(it)
    }}];

    pageName = '${items.name()}Component';
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewTSComponent(items: EntityI<*>, enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                               api: String = LangDerivedKind.API): String {
    return """import {Component, OnInit} from '@angular/core';
import {TableDataService} from '../../../../../template/services/data.service';
import {${c.n(items)}DataService} from '../../service/${items.name().toLowerCase()}-data.service';

${items.toTypeScriptEntityGenerateViewComponentPart(c, items, "view")}
${isOpen().then("export ")}class ${c.n(items)}ViewComponent implements OnInit {

${items.toTypeScriptEntityProp(c, tab, items)}
${items.toAngularConstructorDataService(tab, items)}
${items.toAngularViewOnInit(c, tab, items, basics)}
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewHTMLComponent(items: EntityI<*>, enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return items.toAngularEntityViewHTML(c, items, enums, basics)
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewSCSSComponent(items: EntityI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return items.toAngularEntityViewSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularFormTSComponent(items: EntityI<*>, enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>, entities: List<EntityI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                         api: String = LangDerivedKind.API): String {
    return """import {Component, OnInit, Input} from '@angular/core';
import {${c.n(items)}DataService} from '../../service/${items.name().toLowerCase()}-data.service';

${items.toTypeScriptEntityGenerateFormComponentPart(c, items, "view")}
${isOpen().then("export ")}class ${c.n(items)}FormComponent implements OnInit {

${items.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") {
        it.toAngularGenerateEnumElement(c, tab, items, it.name(), it.type().name(), enums)
    }}
${items.toTypeScriptFormProp(c, tab, items)}
${items.toAngularConstructorDataService(tab, items)}
${items.toAngularFormOnInit(c, tab, items, basics, entities)}
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularFormHTMLComponent(items: EntityI<*>, enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                           api: String = LangDerivedKind.API): String {
    return items.toAngularFormHTML(c, items, enums, basics)
}

fun <T : CompilationUnitI<*>> T.toAngularFormSCSSComponent(items: EntityI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                           api: String = LangDerivedKind.API): String {
    return items.toAngularFormSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListTSComponent(items: EntityI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                               api: String = LangDerivedKind.API): String {
    return """import {Component, OnInit} from '@angular/core';
import {TableDataService} from '../../../../../template/services/data.service';
import {${items.name()}DataService} from '../../service/${items.name().toLowerCase()}-data.service';

${items.toTypeScriptEntityGenerateViewComponentPart(c, items, "list")}
${isOpen().then("export ")}class ${c.n(items)}ListComponent implements OnInit {

${items.toTypeScriptViewEntityPropInit(c, tab, items)}
    tableHeader: Array<String> = [];

${items.toAngularConstructorDataService(tab, items)}
${items.toAngularListOnInit(tab)}

    generateTableHeader() {
        return ['Actions', ${items.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(", ") {
        it.toAngularGenerateTableHeader(c, it)
    }}];
    }
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListHTMLComponent(items: EntityI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return items.toAngularEntityListHTML(items)
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListSCSSComponent(items: EntityI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return items.toAngularEntityListSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityDataService(items: EntityI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                           api: String = LangDerivedKind.API): String {
    return """import {Injectable} from '@angular/core';
import {TableDataService} from '../../../../template/services/data.service';

@Injectable()
${isOpen().then("export ")}class ${c.n(items)}DataService extends TableDataService {
    itemName = '${c.n(items).toLowerCase()}';

    pageName = '${c.n(items)}Component';

    getFirst() {
        return new ${c.n(items)}();
    }
}
"""
}


fun <T : CompilationUnitI<*>> T.toAngularModule(items: ModuleI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                api: String = LangDerivedKind.API): String {
    return """import { NgModule } from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {${items.name().capitalize()}RoutingModules} from './${items.name().toLowerCase()}-routing.module';
import {CommonModule} from '@angular/common';
import {TemplateModule} from '../../template/template.module';
import {MaterialModule} from '../../template/material.module';

import {${items.name().capitalize()}ViewComponent} from './components/view/${items.name().toLowerCase()}-module-view.component';
${items.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleImportEntities(it)
    }}
${items.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleImportBasics(it)
    }}

@NgModule({
    declarations: [
        ${items.name().capitalize()}ViewComponent,
${items.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModuleDeclarationEntities(tab + tab, it)
    }},
${items.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModuleDeclarationBasics(tab + tab, it)
    }}
    ],
    imports: [
        ${items.name().capitalize()}RoutingModules,
        TemplateModule,
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        MaterialModule,
    ],
    providers: [],
    exports: [
${items.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModuleExportViews(tab + tab, it)
    }},
${items.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModuleDeclarationBasics(tab + tab, it)
    }}
    ]
})
export class ${items.name().capitalize()}Module {}"""
}

fun <T : CompilationUnitI<*>> T.toAngularRoutingModule(items: ModuleI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                       api: String = LangDerivedKind.API): String {
    return """import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import {${items.name().capitalize()}ViewComponent} from './components/view/${items.name().toLowerCase()}-module-view.component';
${items.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleImportEntities(it)
    }}

const routes: Routes = [
    { path: '', component: ${items.name().capitalize()}ViewComponent },
${items.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModulePath(tab, it)
    }}
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class ${items.name().capitalize()}RoutingModules {}

"""
}

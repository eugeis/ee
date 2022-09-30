import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*

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

fun <T : CompilationUnitI<*>> T.toAngularModuleHTMLComponent(items: ModuleI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                             api: String = LangDerivedKind.API): String {
    return items.toAngularModuleHTML(items)
}

fun <T : CompilationUnitI<*>> T.toAngularModuleSCSSComponent(items: ModuleI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                             api: String = LangDerivedKind.API): String {
    return items.toAngularDefaultSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewHTMLComponent(items: EntityI<*>, enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return items.toAngularEntityViewHTML(c, items, enums, basics)
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewSCSSComponent(items: EntityI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return items.toAngularEntityViewSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularFormHTMLComponent(items: EntityI<*>, enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                           api: String = LangDerivedKind.API): String {
    return items.toAngularFormHTML(c, items, enums, basics)
}

fun <T : CompilationUnitI<*>> T.toAngularFormSCSSComponent(items: EntityI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                           api: String = LangDerivedKind.API): String {
    return items.toAngularFormSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListHTMLComponent(items: EntityI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return items.toAngularEntityListHTML(items)
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListSCSSComponent(items: EntityI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return items.toAngularEntityListSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularBasicHTMLComponent(items: BasicI<*>, basics: List<BasicI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                            api: String = LangDerivedKind.API): String {
    return """<div>
    <form>
        ${items.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularBasicHTML(c, it, basics)
    }}
    </form>
</div>
"""
}

fun <T : CompilationUnitI<*>> T.toAngularBasicSCSSComponent(items: BasicI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                            api: String = LangDerivedKind.API): String {
    return items.toAngularFormSCSS()
}

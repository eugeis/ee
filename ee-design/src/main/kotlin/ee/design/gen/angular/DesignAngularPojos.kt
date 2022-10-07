import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.design.ModuleI
import ee.lang.*

fun <T : ModuleI<*>> T.toAngularModule(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                api: String = LangDerivedKind.API): String {
    return """import { NgModule } from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {${this.name().capitalize()}RoutingModules} from './${this.name().toLowerCase()}-routing.module';
import {CommonModule} from '@angular/common';
import {TemplateModule} from '@template/template.module';
import {MaterialModule} from '@template/material.module';

import {${this.name().capitalize()}ViewComponent} from './components/view/${this.name().toLowerCase()}-module-view.component';
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleImportEntities(it)
    }}
${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleImportBasics(it)
    }}

@${c.n("NgModule")}({
    declarations: [
        ${this.name().capitalize()}ViewComponent,
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModuleDeclarationEntities(tab + tab, it)
    }},
${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModuleDeclarationBasics(tab + tab, it)
    }}
    ],
    imports: [
        ${this.name().capitalize()}RoutingModules,
        TemplateModule,
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        MaterialModule,
    ],
    providers: [],
    exports: [
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModuleExportViews(tab + tab, it)
    }},
${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModuleDeclarationBasics(tab + tab, it)
    }}
    ]
})
export class ${this.name().capitalize()}Module {}"""
}

fun <T : ModuleI<*>> T.toAngularRoutingModule(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                       api: String = LangDerivedKind.API): String {
    return """import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import {${this.name().capitalize()}ViewComponent} from './components/view/${this.name().toLowerCase()}-module-view.component';
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleImportEntitiesRouting(it)
    }}

const routes: Routes = [
    { path: '', component: ${this.name().capitalize()}ViewComponent },
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModulePath(tab, it)
    }}
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class ${this.name().capitalize()}RoutingModules {}

"""
}

fun <T : ModuleI<*>> T.toAngularModuleHTMLComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                             api: String = LangDerivedKind.API): String {
    return this.toAngularModuleHTML()
}

fun <T : ModuleI<*>> T.toAngularModuleSCSSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                             api: String = LangDerivedKind.API): String {
    return this.toAngularDefaultSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewHTMLComponent(enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return this.toAngularEntityViewHTML(c, enums, basics)
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewSCSSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return this.toAngularEntityViewSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularFormHTMLComponent(enums: List<EnumTypeI<*>>, basics: List<BasicI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                           api: String = LangDerivedKind.API): String {
    return this.toAngularFormHTML(c, enums, basics)
}

fun <T : CompilationUnitI<*>> T.toAngularFormSCSSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                           api: String = LangDerivedKind.API): String {
    return this.toAngularFormSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListHTMLComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return this.toAngularEntityListHTML()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListSCSSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return this.toAngularEntityListSCSS()
}

//TODO: Fix Basic HTML
fun <T : CompilationUnitI<*>> T.toAngularBasicHTMLComponent(basics: List<BasicI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                            api: String = LangDerivedKind.API): String {
    return """<div>
    <form>
        ${props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        this.toAngularBasicHTML(c, it, basics)
    }}
    </form>
</div>
"""
}

fun <T : CompilationUnitI<*>> T.toAngularBasicSCSSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                            api: String = LangDerivedKind.API): String {
    return this.toAngularFormSCSS()
}

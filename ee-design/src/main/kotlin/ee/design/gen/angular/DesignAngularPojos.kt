import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.design.ModuleI
import ee.lang.*

fun <T : CompilationUnitI<*>> T.toAngularModule(module: ModuleI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                api: String = LangDerivedKind.API): String {
    return """import { NgModule } from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {${module.name().capitalize()}RoutingModules} from './${module.name().toLowerCase()}-routing.module';
import {CommonModule} from '@angular/common';
import {TemplateModule} from '../../template/template.module';
import {MaterialModule} from '../../template/material.module';

import {${module.name().capitalize()}ViewComponent} from './components/view/${module.name().toLowerCase()}-module-view.component';
${module.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleImportEntities(it)
    }}
${module.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleImportBasics(it)
    }}

@NgModule({
    declarations: [
        ${module.name().capitalize()}ViewComponent,
${module.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModuleDeclarationEntities(tab + tab, it)
    }},
${module.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModuleDeclarationBasics(tab + tab, it)
    }}
    ],
    imports: [
        ${module.name().capitalize()}RoutingModules,
        TemplateModule,
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        MaterialModule,
    ],
    providers: [],
    exports: [
${module.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModuleExportViews(tab + tab, it)
    }},
${module.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModuleDeclarationBasics(tab + tab, it)
    }}
    ]
})
export class ${module.name().capitalize()}Module {}"""
}

fun <T : CompilationUnitI<*>> T.toAngularRoutingModule(module: ModuleI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                       api: String = LangDerivedKind.API): String {
    return """import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import {${module.name().capitalize()}ViewComponent} from './components/view/${module.name().toLowerCase()}-module-view.component';
${module.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleImportEntities(it)
    }}

const routes: Routes = [
    { path: '', component: ${module.name().capitalize()}ViewComponent },
${module.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModulePath(tab, it)
    }}
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class ${module.name().capitalize()}RoutingModules {}

"""
}

fun <T : CompilationUnitI<*>> T.toAngularModuleHTMLComponent(module: ModuleI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                             api: String = LangDerivedKind.API): String {
    return module.toAngularModuleHTML(module)
}

fun <T : CompilationUnitI<*>> T.toAngularModuleSCSSComponent(module: ModuleI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                             api: String = LangDerivedKind.API): String {
    return module.toAngularDefaultSCSS()
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

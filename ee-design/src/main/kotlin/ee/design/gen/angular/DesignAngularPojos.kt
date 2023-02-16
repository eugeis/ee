import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.design.EntityI
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
        it.toAngularModuleImportEntities()
    }}
${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleImportBasics()
    }}

@${c.n("NgModule")}({
    declarations: [
        ${this.name().capitalize()}ViewComponent,
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModuleDeclarationEntities(tab + tab)
    }},
${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModuleDeclarationBasics(tab + tab)
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
        it.toAngularModuleExportViews(tab + tab)
    }},
${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModuleDeclarationBasics(tab + tab)
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
        it.toAngularModuleImportEntitiesRouting()
    }}

const routes: Routes = [
    { path: '', component: ${this.name().capitalize()}ViewComponent },
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModulePath(tab)
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

fun <T : ModuleI<*>> T.toAngularModuleSCSS(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                           api: String = LangDerivedKind.API): String {
    return this.toAngularModuleSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewHTMLComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return this.toAngularEntityViewHTML()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewSCSSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return this.toAngularEntityViewSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularFormHTMLComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                           api: String = LangDerivedKind.API): String {
    return this.toAngularEntityFormHTML()
}

fun <T : CompilationUnitI<*>> T.toAngularFormSCSSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                           api: String = LangDerivedKind.API): String {
    return this.toAngularEntityFormSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListHTMLComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return this.toAngularEntityListHTML()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListSCSSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return this.toAngularEntityListSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularBasicHTMLComponent(
    c: GenerationContext, derived: String = LangDerivedKind.IMPL,
    api: String = LangDerivedKind.API
): String {
    return """
<fieldset>
    <legend>{{parentName}} ${this.name().capitalize()}</legend>
        ${this.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
            when (it.type()) {
                is EnumTypeI<*> -> it.toHTMLEnumForm("", it.type().name())
                is BasicI<*> -> it.toHTMLObjectForm(it.type().name())
                is EntityI<*> -> it.toHTMLObjectFormEntity(it.type().name())
                else -> when (it.type().name().toLowerCase()) {
                    "boolean" -> it.toHTMLBooleanForm("")
                    "date", "list" -> it.toHTMLDateForm("")
                    else -> {
                        it.toHTMLStringForm("")
                    }
                }
            }
        }
    }
</fieldset>
"""
}

fun <T : CompilationUnitI<*>> T.toAngularBasicSCSSComponent(
    c: GenerationContext, derived: String = LangDerivedKind.IMPL,
    api: String = LangDerivedKind.API
): String {
    return """fieldset {
    width: 80%;
    padding: 20px;
    border: round(30) 1px;

    .mat-form-field {
        padding: 10px 0;
    }
}
"""
}

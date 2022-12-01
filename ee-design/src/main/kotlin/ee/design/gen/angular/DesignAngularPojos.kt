import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
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

import {HttpClient} from '@angular/common/http';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {TemplateTranslateService} from '@template/services/translate.service';
import {TranslateLoader, TranslateModule, TranslateService} from '@ngx-translate/core';

import {${this.name().capitalize()}ViewComponent} from './components/view/${this.name().toLowerCase()}-module-view.component';
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleImportEntities()
    }}
${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleImportBasics()
    }}
${this.enums().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleImportEnums()
    }}
${this.entities().any { entity ->
        entity.props().any {
            it.type().parent().name() != this.name() && it.type().parent().name().first().isUpperCase()
        }
    }.then {this.toAngularImportOtherModules()}}    

export function HttpLoaderFactory(http: ${c.n("HttpClient")}) {
    return new TranslateHttpLoader(http);
}

@${c.n("NgModule")}({
    declarations: [
        ${this.name().capitalize()}ViewComponent,
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationEntities(tab + tab)
    }}
${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationBasics(tab + tab)
    }}
${this.enums().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationEnums(tab + tab) 
    }}
    ],
    imports: [
        ${this.name().capitalize()}RoutingModules,
        TemplateModule,
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        MaterialModule,
        ${c.n("TranslateModule")}.forChild({
            loader: {provide: ${c.n("TranslateLoader")}, useFactory: HttpLoaderFactory, deps: [${c.n("HttpClient")}]},
        }),
        ${this.entities().any { entity ->
        entity.props().any {
            it.type().parent().name() != this.name() && it.type().parent().name().first().isUpperCase()
        }
    }.then {this.toAngularImportOtherModulesOnImportPart()}}
    ],
    providers: [
        { provide: ${c.n("TranslateService")}, useExisting: ${c.n("TemplateTranslateService")} }
    ],
    exports: [
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleExportViews(tab + tab)
    }}
${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationBasics(tab + tab)
    }}
${this.enums().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationEnums(tab + tab)
    }}
    ]
})
export class ${this.name().capitalize()}Module {}"""
}

fun <T : ModuleI<*>> T.toAngularImportOtherModules(): String {
    val sb = StringBuilder()
    val importedOtherModules: MutableList<String> = ArrayList()
    this.entities().forEach { entity ->
        entity.props().filter { it.type().parent().name() != this.name() && it.type().parent().name().first().isUpperCase() }.forEach {
            importedOtherModules.add("import {${it.type().parent().name()}Module} from '@${it.type().parent().parent().name().toLowerCase()}/${it.type().parent().name().toLowerCase()}/${it.type().parent().name().toLowerCase()}-model.module';")
        }
    }
    importedOtherModules.distinct().forEach {
        sb.append(it + "\n")
    }
    return sb.toString()
}

fun <T : ModuleI<*>> T.toAngularImportOtherModulesOnImportPart(): String {
    val sb = StringBuilder()
    val importedOtherModules: MutableList<String> = ArrayList()
    this.entities().forEach { entity ->
        entity.props().filter { it.type().parent().name() != this.name() && it.type().parent().name().first().isUpperCase() }.forEach {
            importedOtherModules.add("${it.type().parent().name()}Module,")
        }
    }
    importedOtherModules.distinct().forEach {
        sb.append(it)
        sb.append(("\n${tab + tab}"))
    }
    return sb.toString()
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

fun <T : ModuleI<*>> T.toAngularDefaultSCSS(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                            api: String = LangDerivedKind.API): String {
    return this.toAngularDefaultSCSS()
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

fun <T : CompilationUnitI<*>> T.toAngularBasicHTMLComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                            api: String = LangDerivedKind.API): String {
    return this.toAngularBasicHTML()
}

fun <T : CompilationUnitI<*>> T.toAngularBasicSCSSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                            api: String = LangDerivedKind.API): String {
    return this.toAngularBasicSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularEnumHTMLComponent(parent: ItemI<*>, elementName: String, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                            api: String = LangDerivedKind.API): String {
    return this.toAngularEnumHTML(parent, elementName)
}

fun <T : CompilationUnitI<*>> T.toAngularEnumSCSSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                            api: String = LangDerivedKind.API): String {
    return this.toAngularDefaultSCSS()
}

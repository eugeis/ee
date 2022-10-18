import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.*

fun <T : ModuleI<*>> T.toAngularModuleTypeScript(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                 api: String = LangDerivedKind.API): String {
    return """import {Component, Input} from '@angular/core';
${this.toAngularModuleImportServices()}
${this.toAngularGenerateComponentPart(c, "module", "view", hasProviders = true, hasClass = false)}
export class ${this.name()}ViewComponent {${"\n"}
${this.toAngularModuleInputElement(c, "pageName", tab)}       
${this.toAngularModuleConstructor(tab)}
}"""
}

fun <T : ModuleI<*>> T.toAngularModuleService(modules: List<ModuleI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                       api: String = LangDerivedKind.API): String {
    return """export class ${this.name()}ViewService {

    pageElement = [${modules.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(", ") { """'${it.name()}'""" }}];

    tabElement = [${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString() {
        it.toAngularModuleTabElement()
    }}];

    pageName = '${this.name()}Component';
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewTypeScript(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                              api: String = LangDerivedKind.API): String {
    return """import {Component, OnInit} from '@angular/core';
import {TableDataService} from '@template/services/data.service';
import {${c.n(this)}DataService} from '@${this.parent().parent().name().toLowerCase()}/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/service/${this.name().toLowerCase()}-data.service';

${this.toAngularGenerateComponentPart(c, "entity", "view", hasProviders = true, hasClass = true)}
${isOpen().then("export ")}class ${c.n(this)}ViewComponent implements ${c.n("OnInit")} {

${this.toTypeScriptEntityProp(c, tab)}
${this.toAngularConstructorDataService(tab)}
${this.toAngularViewOnInit(c, tab)}
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityFormTypeScript(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                              api: String = LangDerivedKind.API): String {
    return """import {Component, OnInit, Input} from '@angular/core';
import {${c.n(this)}DataService} from '@${this.parent().parent().name().toLowerCase()}/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/service/${this.name().toLowerCase()}-data.service';

${this.toAngularGenerateComponentPart(c, "entity", "form", hasProviders = false, hasClass = false)}
${isOpen().then("export ")}class ${c.n(this)}FormComponent implements ${c.n("OnInit")} {

${props().filter { it.type() is EnumTypeI<*> }.joinSurroundIfNotEmptyToString("") {
        it.type().toAngularGenerateEnumElement(c, tab, this)
    }}
${this.toTypeScriptFormProp(c, tab)}
${this.toAngularConstructorDataService(tab)}
${this.toAngularFormOnInit(c, tab)}
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListTypeScript(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                              api: String = LangDerivedKind.API): String {
    return """import {Component, OnInit} from '@angular/core';
import {TableDataService} from '@template/services/data.service';
import {${this.name()}DataService} from '@${this.parent().parent().name().toLowerCase()}/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/service/${this.name().toLowerCase()}-data.service';

${this.toAngularGenerateComponentPart(c, "entity", "list", hasProviders = true, hasClass = true)}
${isOpen().then("export ")}class ${c.n(this)}ListComponent implements ${c.n("OnInit")} {

${this.toTypeScriptEntityPropInit(c, tab)}
    tableHeader: Array<String> = [];

${this.toAngularConstructorDataService(tab)}
${this.toAngularListOnInit(tab)}

    generateTableHeader() {
        return ['Box', 'Actions', ${props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(", ") {
        it.toAngularGenerateTableHeader(c)
    }}];
    }
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityDataService(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                           api: String = LangDerivedKind.API): String {
    return """import {Injectable} from '@angular/core';
import {TableDataService} from '@template/services/data.service';
import {SelectionModel} from '@angular/cdk/collections';

@${c.n("Injectable")}()
${isOpen().then("export ")}class ${c.n(this)}DataService extends TableDataService {
    itemName = '${c.n(this).toLowerCase()}';

    pageName = '${c.n(this)}Component';
    
    isHidden = true;

    selection = new ${c.n("SelectionModel")}<any>(true, []);

    getFirst() {
        return new ${c.n(this)}();
    }
    
    toggleHidden() {
        this.isHidden = !this.isHidden;
    }
}
"""
}

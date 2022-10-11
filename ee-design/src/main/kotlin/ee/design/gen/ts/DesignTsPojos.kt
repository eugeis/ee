import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.*

fun <T : ModuleI<*>> T.toAngularModuleTSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                           api: String = LangDerivedKind.API): String {
    return """import {Component, Input} from '@angular/core';
${this.toTypeScriptModuleImportServices()}
${this.toTypeScriptModuleGenerateComponentPart(c)}
export class ${this.name()}ViewComponent {${"\n"}
${this.toTypeScriptModuleInputElement(c, "pageName", tab)}       
${this.toTypeScriptModuleConstructor(tab)}
}"""
}

fun <T : ModuleI<*>> T.toAngularModuleService(modules: List<ModuleI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                       api: String = LangDerivedKind.API): String {
    return """export class ${this.name()}ViewService {

    pageElement = [${modules.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(", ") { """'${it.name()}'""" }}];

    tabElement = [${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString() {
        it.toAngularModuleArrayElement(it)
    }}];

    pageName = '${this.name()}Component';
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewTSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                               api: String = LangDerivedKind.API): String {
    return """import {Component, OnInit} from '@angular/core';
import {TableDataService} from '@template/services/data.service';
import {${c.n(this)}DataService} from '@${this.parent().parent().name().toLowerCase()}/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/service/${this.name().toLowerCase()}-data.service';

${this.toTypeScriptEntityGenerateViewComponentPart(c, "view")}
${isOpen().then("export ")}class ${c.n(this)}ViewComponent implements ${c.n("OnInit")} {

${this.toTypeScriptEntityProp(c, tab)}
${this.toAngularConstructorDataService(tab)}
${this.toAngularViewOnInit(c, tab)}
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularFormTSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                         api: String = LangDerivedKind.API): String {
    return """import {Component, OnInit, Input} from '@angular/core';
import {${c.n(this)}DataService} from '@${this.parent().parent().name().toLowerCase()}/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/service/${this.name().toLowerCase()}-data.service';

${this.toTypeScriptEntityGenerateFormComponentPart(c)}
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

fun <T : CompilationUnitI<*>> T.toAngularEntityListTSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                               api: String = LangDerivedKind.API): String {
    return """import {Component, OnInit} from '@angular/core';
import {TableDataService} from '@template/services/data.service';
import {${this.name()}DataService} from '@${this.parent().parent().name().toLowerCase()}/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/service/${this.name().toLowerCase()}-data.service';

${this.toTypeScriptEntityGenerateViewComponentPart(c, "list")}
${isOpen().then("export ")}class ${c.n(this)}ListComponent implements ${c.n("OnInit")} {

${this.toTypeScriptViewEntityPropInit(c, tab)}
    tableHeader: Array<String> = [];

${this.toAngularConstructorDataService(tab)}
${this.toAngularListOnInit(tab)}

    generateTableHeader() {
        return ['Actions', ${props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(", ") {
        it.toAngularGenerateTableHeader(c, it)
    }}];
    }
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityDataService(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                           api: String = LangDerivedKind.API): String {
    return """import {Injectable} from '@angular/core';
import {TableDataService} from '@template/services/data.service';

@${c.n("Injectable")}()
${isOpen().then("export ")}class ${c.n(this)}DataService extends TableDataService {
    itemName = '${c.n(this).toLowerCase()}';

    pageName = '${c.n(this)}Component';

    getFirst() {
        return new ${c.n(this)}();
    }
}
"""
}

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.*

fun <T : ModuleI<*>> T.toAngularModuleTypeScript(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                 api: String = LangDerivedKind.API): String {
    return """import {Component, Input} from '@angular/core';
${this.toAngularModuleImportServices()}
${this.toAngularGenerateComponentPart(c, "module", "view", hasProviders = true, hasClass = false)}
export class ${this.name()}ViewComponent {${"\n"}  
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

    pageName = '${this.name()}';
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
${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
    when(it.type()) {
        is EntityI<*>, is ValuesI<*> -> it.type().toAngularImportEntityComponent(it.type().findParentNonInternal())
        else -> ""
    }
}}
    
${this.toAngularGenerateComponentPart(c, "entity", "form", hasProviders = false, hasClass = false)}
${isOpen().then("export ")}class ${c.n(this)}FormComponent implements ${c.n("OnInit")} {

${this.toTypeScriptFormProp(c, tab)}
    constructor(public ${this.name().toLowerCase()}DataService: ${this.name()}DataService, 
${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
    when(it.type()) {
        is EntityI<*>, is ValuesI<*> -> it.type().toAngularPropOnConstructor()
        else -> ""
    }
}}) {}
${this.toAngularFormOnInit(c, tab)}
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListTypeScript(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                              api: String = LangDerivedKind.API): String {
    return """import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {TableDataService} from '@template/services/data.service';
import {${this.name()}DataService} from '@${this.parent().parent().name().toLowerCase()}/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/service/${this.name().toLowerCase()}-data.service';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';

${this.toAngularGenerateComponentPart(c, "entity", "list", hasProviders = true, hasClass = true)}
${isOpen().then("export ")}class ${c.n(this)}ListComponent implements ${c.n("OnInit")}, ${c.n("AfterViewInit")} {

${this.toTypeScriptEntityPropInit(c, tab)}
    tableHeader: Array<String> = [];
    
    @ViewChild(MatSort) sort: MatSort;

${this.toAngularConstructorDataService(tab)}
    ngAfterViewInit() {
        this.${this.name().toLowerCase()}DataService.dataSources.sort = this.sort;
    }
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
    return """
${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
    when(it.type()) {
        is EntityI<*>, is ValuesI<*> -> it.type().toAngularControlServiceImport(it.type().findParentNonInternal())
        else -> ""
    }
}}
import {Injectable} from '@angular/core';
import {TableDataService} from '@template/services/data.service';
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';

@${c.n("Injectable")}({ providedIn: 'root' })
${isOpen().then("export ")}class ${c.n(this)}DataService extends TableDataService {
    itemName = '${c.n(this).toLowerCase()}';

    pageName = '${c.n(this)}Component';
    
    isHidden = true;
    
    entityElements = [${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> """'${it.name().toCamelCase()}',"""
            else -> {
                it.type().props().filter {childElement -> !childElement.isEMPTY() }.joinSurroundIfNotEmptyToString("") {childElement -> 
                    when(childElement.type()) {
                        is EntityI<*>, is ValuesI<*> -> """'${childElement.name().toCamelCase()}',"""
                        else -> ""
                    }
                }
            }
        }
    }}];   
    
    ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.type().toAngularControlService()
            else -> ""
        }
    }}
    
    ${this.props().filter { it.type().name().toLowerCase() == "blob" }.joinSurroundIfNotEmptyToString { 
        """
    selectedFiles?: FileList;
    
    previews: string[] = [];
    """
    }}

    getFirst() {
        return new ${c.n(this)}();
    }
    
    toggleHidden() {
        this.isHidden = !this.isHidden;
    }
    
    ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.type().toAngularControlServiceFunctions(it.type().props().first { element -> element.type().name() == "String" })
            else -> ""
        }
    }}
    
    initObservable() {
    ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.type().toAngularInitObservable(it.type().props().first { element -> element.type().name() == "String" })
            else -> ""
        }
    }}
    }
    
    ${this.props().filter { it.type().name().toLowerCase() == "blob" }.joinSurroundIfNotEmptyToString {
        """
    selectFiles(event: any): void {
        this.selectedFiles = event.target.files;

        this.previews = [];
        if (this.selectedFiles) {
            const fileReader = new FileReader();

            fileReader.onload = (e: any) => {
                this.previews.push(e.target.result);
            };

            fileReader.readAsDataURL(this.selectedFiles[0]);
        }
    }
    """ 
    }}
}
"""
}

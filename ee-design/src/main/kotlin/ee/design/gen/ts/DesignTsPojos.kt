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

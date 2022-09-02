package ee.lang.gen.ts

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toUnderscoredUpperCase
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*

fun LiteralI<*>.toTypeScript(): String = name().toUnderscoredUpperCase()
fun LiteralI<*>.toTypeScriptIsMethod(): String {
    return """is${name().capitalize()}() : boolean {
        return this == ${toTypeScript()};
}"""
}

fun <T : EnumTypeI<*>> T.toTypeScriptEnum(c: GenerationContext, derived: String = LangDerivedKind.API,
                                          api: String = LangDerivedKind.API): String {
    val name = c.n(this, derived)
    return """${isOpen().then("export ")}enum $name {
    ${literals().joinToString(",${nL}    ") { "${it.toTypeScript()}${it.toTypeScriptCallValue(c, derived)}" }}
}"""
    /*
    ;${
        paramsNotDerived().joinToString(nL) { it.toTypeScriptMember(c, derived, api) }}${
        constructors().joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
            it.toTypeScript(c, derived, api)
        }}${
        operations().joinToString(nL) { it.toTypeScriptImpl(c, derived, api) }}${
        literals().joinToString("", nL) { it.toTypeScriptIsMethod() }}
    }"""*/
}

fun <T : EnumTypeI<*>> T.toTypeScriptEnumParseMethod(c: GenerationContext,
                                                     derived: String = LangDerivedKind.API): String {
    val name = c.n(this, derived)
    return """fun String?.to$name(): $name {
    return if (this != null) $name.valueOf(this) else $name.${literals().first().toTypeScript()};
}"""
}

fun <T : CompilationUnitI<*>> T.toTypeScriptImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                 api: String = LangDerivedKind.API): String {
    return """${isOpen().then("export ")}class ${c.n(this, derived)}${toTypeScriptExtends(c, derived,
        api)} {${props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        it.toTypeScriptMember(c, derived, api, false, tab)
    }}${constructors().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        it.toTypeScript(c, derived, api)
    }}${operations().joinSurroundIfNotEmptyToString(nL, prefix = nL) {
        it.toTypeScriptImpl(c, derived, api)
    }}
}"""
}

fun <T : CompilationUnitI<*>> T.toTypeScriptModuleTSComponent(items: ModuleI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
api: String = LangDerivedKind.API): String {
    return """import {Component, Input} from '@angular/core';
${items.toTypeScriptModuleImportServices(items)}
${items.toTypeScriptGenerateComponentPartWithProviders(items)}
${isOpen().then("export ")}class ${items.name()}ViewComponent {${"\n"}
${items.toTypeScriptModuleInputElement("pageName", tab , items)}       
${items.toTypeScriptModuleConstructor(tab, items)}
}"""
}

fun <T : CompilationUnitI<*>> T.toTypeScriptModuleHTMLComponent(items: ModuleI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                api: String = LangDerivedKind.API): String {
    return items.toTypeScriptModuleHTML(items)
}

fun <T : CompilationUnitI<*>> T.toTypeScriptModuleSCSSComponent(items: ModuleI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                api: String = LangDerivedKind.API): String {
    return items.toTypeScriptModuleSCSS()
}

fun <T : CompilationUnitI<*>> T.toTypeScriptModuleService(items: ModuleI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                          api: String = LangDerivedKind.API): String {
    return """${isOpen().then("export ")}class ${items.name()}ViewService {

    pageElement = ['${items.name()}'];

    tabElement = [${items.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString() {
        it.toTypeScriptModuleArrayElement(it)
    }}];

    pageName = '${items.name()}Component';
}
"""
}

fun <T : CompilationUnitI<*>> T.toTypeScriptEntityViewTSComponent(items: EntityI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                  api: String = LangDerivedKind.API): String {
    return """ """
}

fun <T : CompilationUnitI<*>> T.toTypeScriptEntityViewHTMLComponent(items: EntityI<*>, enums: List<EnumTypeI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                    api: String = LangDerivedKind.API): String {
    return items.toTypeScriptEntityViewHTML(c, items, enums)
}

fun <T : CompilationUnitI<*>> T.toTypeScriptEntityViewSCSSComponent(items: EntityI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                    api: String = LangDerivedKind.API): String {
    return items.toTypeScriptEntityViewSCSS()
}

fun <T : CompilationUnitI<*>> T.toTypeScriptEntityListTSComponent(items: EntityI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                  api: String = LangDerivedKind.API): String {
    return """ """
}

fun <T : CompilationUnitI<*>> T.toTypeScriptEntityListHTMLComponent(items: EntityI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                    api: String = LangDerivedKind.API): String {
    return items.toTypeScriptEntityListHTML(items)
}

fun <T : CompilationUnitI<*>> T.toTypeScriptEntityListSCSSComponent(items: EntityI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                    api: String = LangDerivedKind.API): String {
    return items.toTypeScriptEntityListSCSS()
}



/*fun <T : CompilationUnitI<*>> T.toTypeScriptComponent(items: BasicI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                 api: String = LangDerivedKind.API): String {
    val commonTypeAttribute = arrayOf("string", "int", "bool", "double")
    return """import { Component, OnInit } from '@angular/core';
${items.props().filter { it.toString().contains("TypedAttribute") && it.type().name().toLowerCase() !in commonTypeAttribute }.
    joinSurroundIfNotEmptyToString(nL, postfix = nL) { it.toTypeScriptImportElements(it) } }
${items.toTypeScriptGenerateComponentPart(items)}
${isOpen().then("export ")}class ${items.name()}Component implements OnInit {
${items.props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL, prefix = nL) {
    it.toTypeScriptGenerateProperties(c, halfTab, it)
}}
${items.toTypeScriptGenerateArrayPart(halfTab)}
  constructor() { }

  ngOnInit(): void {
  }
  inputElement() {
${items.props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
        it.toTypeScriptInputFunction(c, tab, it)
}}
${items.toTypeScriptInputPushElementToArrayPart(tab, items)}
  }
  deleteElement(index: number) {
${items.props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
        var indexOfElement = calculateIndex(items, it)
        it.toTypeScriptDeleteFunction(c, tab, it, indexOfElement)
}}
${items.toTypeScriptDeleteElementFromArrayPart(tab)}
  }
  printElement(index: number) {
${items.props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
        var indexOfElement = calculateIndex(items, it)
        it.toTypeScriptPrintFunction(tab, it, indexOfElement)
}}
  }
  changeIndex(input: number) {
        this.index = input;
  }
  loadElement(index: number) {
${items.props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
        var indexOfElement = calculateIndex(items, it)
        it.toTypeScriptLoadFunction(c, tab, it, indexOfElement)
}}
  }
  editElement(index: number) {
${items.props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
        var indexOfElement = calculateIndex(items, it)
        it.toTypeScriptEditFunction(c, tab, it, indexOfElement)
    }}
  }
  simplifiedHtmlInputElement(element: string) {
        return (<HTMLInputElement>document.getElementById(element)).value;
  }

}"""
}

fun <T : CompilationUnitI<*>> T.toHtmlComponent(items: BasicI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                      api: String = LangDerivedKind.API): String {
    return """${items.props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
        it.toHtmlForm(it)
    }}
<div>
    <button mat-raised-button (click)="inputElement()">Input</button>
    <mat-form-field appearance="fill">
        <mat-label>Select</mat-label>
        <mat-select (valueChange)="changeIndex(${"$"}event)">
            <div *ngFor="let item of dataElement; let i = index">
                <mat-option [value]="i">{{i}}</mat-option>
            </div>
        </mat-select>
    </mat-form-field>
    <button mat-raised-button (click)="loadElement(index)">Load Value</button>
    <button mat-raised-button (click)="printElement(index)">Check Value</button>
    <button mat-raised-button (click)="editElement(index)">Edit Value</button>
    <button mat-raised-button (click)="deleteElement(index)">Delete</button>
</div>
    """
}

fun <T : CompilationUnitI<*>> T.toScssComponent(items: BasicI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                api: String = LangDerivedKind.API): String {
    return """
:host {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
}

button {
    display: inline-block;
}
    """
}

fun calculateIndex(items: BasicI<*>, it: AttributeI<*>): Int {
    // start index of element
    var indexOfElement = 0
    while (items.props()[indexOfElement] != it) {
        ++indexOfElement
    }
    // searching interface in between indexes
    var indexForSearchingElement = 0
    var indexOfElementWithInterface = 0
    while (items.props()[indexForSearchingElement] != it) {
        if (items.props()[indexForSearchingElement].type().props().size != 0) {
            indexOfElementWithInterface = indexForSearchingElement
            indexOfElement += items.props()[indexOfElementWithInterface].type().props().size - 1
        }
        ++indexForSearchingElement
    }
    return indexOfElement;
}*/

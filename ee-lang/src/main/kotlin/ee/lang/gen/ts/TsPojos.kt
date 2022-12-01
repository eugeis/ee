package ee.lang.gen.ts

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toUnderscoredUpperCase
import ee.lang.*
import toAngularGenerateComponentPart

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


fun <T : ItemI<*>> T.toAngularGenerateEnumElementBasic(c: GenerationContext, indent: String): String {
    return """${indent}${c.n(this).toLowerCase()}Enum = this.loadEnumElement(${c.n(this).capitalize()});"""
}

fun <T : CompilationUnitI<*>> T.toAngularBasicTSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return """import {Component, Input, OnInit} from '@angular/core';

${this.toAngularGenerateComponentPart(c, "basic", "", hasProviders = false, hasClass = false)}
${isOpen().then("export ")}class ${c.n(this)}Component implements ${c.n("OnInit")} {

    @${c.n("Input")}() ${c.n(this).toLowerCase()}: ${c.n(this)};
    @${c.n("Input")}() parentName: String;
${props().filter { it.type() is EnumTypeI<*> }.joinSurroundIfNotEmptyToString("") {
    it.type().toAngularGenerateEnumElementBasic(c, tab)
}}

${if (props().any { it.type() is EnumTypeI<*> }) {
    """
    loadEnumElement(enumElement: any) {
        let tempArray = [];
        Object.keys(enumElement).map((element, index) => {
            tempArray.push(enumElement[index]);
        })
        tempArray = tempArray.filter((item) => item);
        return tempArray;
    }"""
} else {""}}
    ngOnInit() {
        if (this.${c.n(this).toLowerCase()} === undefined) {
            this.${c.n(this).toLowerCase()} = new ${this.name().capitalize()}();
        }
        ${props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { 
            it.toTypeScriptInitEmptyProps(c)
    }.trim()}
    }
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEnumTSComponent(parent: ItemI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                          api: String = LangDerivedKind.API): String {
    return """import {Component, Input, OnInit} from '@angular/core';
import {TableDataService} from '@template/services/data.service';

${this.toAngularGenerateComponentPart(c, "enum", "", hasProviders = false, hasClass = false)}

export class ${c.n(this)}EnumComponent implements ${c.n("OnInit")} {

    @${c.n("Input")}() ${c.n(parent).toLowerCase()}: ${c.n(parent)};
    
    enumElements: Array<string>;
    
    constructor(private tableDataService: ${c.n("TableDataService")}) { }
    
    ngOnInit(): void {
        this.enumElements = this.${c.n("tableDataService")}.loadEnumElement(${c.n(this)}, '${c.n(parent).toLowerCase()}', '${c.n(this).toLowerCase()}-enum');
    }

}
"""
}

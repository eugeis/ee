package ee.lang.gen.ts

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toUnderscoredUpperCase
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
    return """enum $name {
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

fun <T : CompilationUnitI<*>> T.toTypeScriptComponent(items: BasicI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                 api: String = LangDerivedKind.API): String {
    val commonTypeAttribute = arrayOf("string", "int", "bool", "double")
    return """import { Component, OnInit } from '@angular/core';
${items.props().filter { it.toString().contains("TypedAttribute") && it.type().name().toLowerCase() !in commonTypeAttribute }.
    joinSurroundIfNotEmptyToString(nL, postfix = nL) { it.toTypeScriptImportElements(it) } }
${items.toTypeScriptGenerateComponentPart(items)}
${isOpen().then("export ")}class ${items.name()}Component implements OnInit {
${items.props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL, prefix = nL, postfix = nL) {
    it.toTypeScriptProperties(c, halfTab, it)
}}
  constructor() { }

  ngOnInit(): void {
  }
  inputElement() {
${items.props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
        it.toTypeScriptHtmlInputFunction(c, tab, it)
}}
  }
  deleteElement() {
${items.props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
        it.toTypeScriptHtmlDeleteFunction(c, tab, it)
}}
  }
  printElement() {
${items.props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(nL) {
        it.toTypeScriptHtmlPrintFunction(tab, it)
}}
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
    <button mat-raised-button (click)="deleteElement()">Delete</button>
    <button mat-raised-button (click)="printElement()">Check Value</button>
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

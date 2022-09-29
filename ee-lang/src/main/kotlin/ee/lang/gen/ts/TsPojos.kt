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

fun <T : CompilationUnitI<*>> T.toAngularBasicTSComponent(items: BasicI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return """import {Component, Input, OnInit} from '@angular/core';

${items.toTypeScriptBasicGenerateComponentPart(items)}
${isOpen().then("export ")}class ${c.n(items)}Component implements OnInit {

    @Input() ${c.n(items).toLowerCase()}: ${c.n(items)};
    
    ngOnInit() {
        if (this.${c.n(items).toLowerCase()} === undefined) {
            this.${c.n(items).toLowerCase()} = new ${items.name().capitalize()}();
        }
        ${items.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { 
            it.toTypeScriptInitEmptyProps(c, it)
    }.trim()}
    }
    
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularBasicHTMLComponent(items: BasicI<*>, basics: List<BasicI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return """<div>
    <form>
        ${items.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) { 
            it.toAngularBasicHTML(c, it, basics)
        }}
    </form>
</div>
"""
}

fun <T : CompilationUnitI<*>> T.toAngularBasicSCSSComponent(items: BasicI<*>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return items.toAngularFormSCSS()
}
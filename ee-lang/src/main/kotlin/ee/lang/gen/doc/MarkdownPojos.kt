package ee.lang.gen.doc

import ee.common.ext.ifElse
import ee.common.ext.then
import ee.lang.BasicI
import ee.lang.CompilationUnitI
import ee.lang.GenerationContext
import ee.lang.LangDerivedKind

fun <T : CompilationUnitI<*>> T.toMarkdownClassImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                             api: String = LangDerivedKind.API,
                                             itemName: String = c.n(this, derived),
                                             dataClass: Boolean = this is BasicI<*> &&
                                                     superUnits().isEmpty() && superUnitFor().isEmpty(),
                                             nonBlocking: Boolean = isNonBlocking()): String {
    return """# ${name()} count of properties is ${props().size}"""
}

fun <T : CompilationUnitI<*>> T.toPlainUmlClassImpl(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                    api: String = LangDerivedKind.API,
                                                    itemName: String = c.n(this, derived),
                                                    dataClass: Boolean = this is BasicI<*> &&
                                                            superUnits().isEmpty() && superUnitFor().isEmpty(),
                                                    nonBlocking: Boolean = isNonBlocking()): String {
    return """@startuml
        ${isIfc().ifElse("interface ", "class")} ${name()} {"""
}
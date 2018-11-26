package ee.lang.gen.kt

import ee.lang.*
import ee.lang.gen.itemNameAsKotlinFileName

open class LangKotlinTemplates {
    private val defaultNameBuilder: TemplateI<*>.(CompilationUnitI<*>) -> NamesI

    constructor(defaultNameBuilder: TemplateI<*>.(CompilationUnitI<*>) -> NamesI = itemNameAsKotlinFileName) {
        this.defaultNameBuilder = defaultNameBuilder
    }

    open fun dslBuilderI(
        nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template("BuilderI", nameBuilder) { item, c -> item.toKotlinDslBuilderI(c) }

    open fun dslObjectTree(
        nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template("ObjectTree", nameBuilder) { item, c -> item.toKotlinObjectTree(c) }

    open fun dslBuilder(
        nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template("DslBuilder", nameBuilder) { item, c -> item.toKotlinDslBuilder(c) }

    open fun builderI(
            nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
            Template("Builder", nameBuilder) { item, c -> item.toKotlinBuilderI(c) }

    open fun builder(
            nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
            Template("BuilderI", nameBuilder) { item, c -> item.toKotlinBuilder(c) }

    open fun enum(nameBuilder: TemplateI<EnumTypeI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template("Enum", nameBuilder) { item, c -> item.toKotlinEnum(c) }

    open fun enumParseMethod(
        nameBuilder: TemplateI<EnumTypeI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template("EnumParseMethod", nameBuilder) { item, c -> item.toKotlinEnumParseMethod(c) }

    open fun enumParseAndIsMethodsTestsParseMethodTests(
            nameBuilder: TemplateI<EnumTypeI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
            Template("EnumParseMethodTests", nameBuilder) { item, c -> item.toKotlinEnumParseAndIsMethodsTests(c) }

    open fun ifc(nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
            Template("Ifc", nameBuilder) { item, c -> item.toKotlinIfc(c, LangDerivedKind.API) }

    open fun ifcEmpty(nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
            Template("IfcEmpty", nameBuilder) { item, c -> item.toKotlinIfcEMPTY(c, LangDerivedKind.API) }

    open fun pojo(nameBuilder: TemplateI<CompilationUnitI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template("Pojo", nameBuilder) { item, c -> item.toKotlinImpl(c, LangDerivedKind.API) }
}

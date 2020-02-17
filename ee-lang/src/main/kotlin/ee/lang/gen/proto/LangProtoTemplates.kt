package ee.lang.gen.proto

import ee.lang.*

open class LangProtoTemplates(val defaultNameBuilder: TemplateI<*>.(CompositeI<*>) -> NamesI = itemNameAsProtoFileName) {

    open fun enum(nameBuilder: TemplateI<EnumTypeI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
            Template("Enum", nameBuilder) { item, c -> item.toProtoEnum(c) }

    open fun <T : CompilationUnitI<*>> pojo(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
            Template("Pojo", nameBuilder) { item, c -> item.toProtoImpl(c) }

}

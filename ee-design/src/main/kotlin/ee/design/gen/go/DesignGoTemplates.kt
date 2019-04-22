package ee.design.gen.go

import ee.design.CommandI
import ee.design.EntityI
import ee.design.StateI
import ee.lang.*
import ee.lang.gen.go.LangGoTemplates

open class DesignGoTemplates : LangGoTemplates {
    constructor(defaultNameBuilder: TemplateI<*>.(CompositeI<*>) -> NamesI) : super(defaultNameBuilder)

    open fun command(nameBuilder: TemplateI<CommandI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template("Command", nameBuilder) { item, c -> item.toGoCommandImpl(c) }

    open fun commandTypes(nameBuilder: TemplateI<EntityI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template("CommandTypes", nameBuilder) { item, c -> item.toGoCommandTypes(c) }

    open fun eventTypes(nameBuilder: TemplateI<EntityI<*>>.(CompilationUnitI<*>) -> NamesI = defaultNameBuilder) =
        Template("EventTypes", nameBuilder) { item, c -> item.toGoEventTypes(c) }

    open fun <T : EntityI<*>> entity(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("Entity", nameBuilder) { item, c -> item.toGoEntityImpl(c) }

    open fun stateHandler(nameBuilder: TemplateI<StateI<*>>.(StateI<*>) -> NamesI = defaultNameBuilder) =
        Template("StateHandler", nameBuilder) { item, c -> item.toGoStateHandler(c) }

    open fun <T : EntityI<*>> createTestData(nameBuilder: TemplateI<T>.(T) -> NamesI = defaultNameBuilder) =
        Template("CreateTestData", nameBuilder) { item, c -> item.toGoCreateTestData(c) }


}
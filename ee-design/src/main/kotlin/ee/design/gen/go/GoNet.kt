package ee.design.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.toPlural
import ee.design.*
import ee.lang.*
import ee.lang.gen.go.g
import ee.lang.gen.go.nameForMember
import ee.lang.gen.go.toGoImpl

fun <T : CommandI> T.toGoHandler(c: GenerationContext,
                                 derived: String = DesignDerivedKind.IMPL,
                                 api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val name = c.n(this, derived)
    return """
        ${toGoImpl(c, derived, api)}
func (o *$name) AggregateID() ${c.n(g.eh.UUID)}            { return o.${entity.id().nameForMember()} }
func (o *$name) AggregateType() ${c.n(g.eh.AggregateType)}  { return ${entity.name()}${DesignDerivedType.AggregateType} }
func (o *$name) CommandType() ${c.n(g.eh.CommandType)}      { return ${nameAndParentName().capitalize()}${DesignDerivedType.Command} }
"""
}

fun <T : OperationI> T.toGoSetupHttpRouterBody(c: GenerationContext,
                                               derived: String = DesignDerivedKind.IMPL,
                                               api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val finders = entity.findDownByType(FindBy::class.java)
    val counters = entity.findDownByType(CountByI::class.java)
    val exists = entity.findDownByType(ExistByI::class.java)
    val creaters = entity.findDownByType(CreateByI::class.java)
    val updaters = entity.findDownByType(UpdateByI::class.java)
    val deleters = entity.findDownByType(DeleteByI::class.java)

    return """${finders.joinSurroundIfNotEmptyToString("") {
        """
    router.Methods(${c.n(g.gee.net.GET, api)}).PathPrefix(o.PathPrefix).Name("${
        it.nameAndParentName().capitalize()}").HandlerFunc(o.QueryHandler.${it.name().capitalize()})"""
    }}${counters.joinSurroundIfNotEmptyToString("") {
        """
    router.Methods(${c.n(g.gee.net.GET, api)}).PathPrefix(o.PathPrefix).Name("${
        it.nameAndParentName().capitalize()}").HandlerFunc(o.QueryHandler.${it.name().capitalize()})"""
    }}${exists.joinSurroundIfNotEmptyToString("") {
        """
    router.Methods(${c.n(g.gee.net.GET, api)}).PathPrefix(o.PathPrefix).Name("${
        it.nameAndParentName().capitalize()}").HandlerFunc(o.QueryHandler.${it.name().capitalize()})"""
    }}${creaters.joinSurroundIfNotEmptyToString("") {
        """
    router.Methods(${c.n(g.gee.net.POST, api)}).PathPrefix(o.PathPrefix).Name("${
        it.nameAndParentName().capitalize()}").HandlerFunc(o.CommandHandler.${it.name().capitalize()})"""
    }}${updaters.joinSurroundIfNotEmptyToString("") {
        """
    router.Methods(${c.n(g.gee.net.PUT, api)}).PathPrefix(o.PathPrefix).Name("${
        it.nameAndParentName().capitalize()}").HandlerFunc(o.CommandHandler.${it.name().capitalize()})"""
    }}${deleters.joinSurroundIfNotEmptyToString("") {
        """
    router.Methods(${c.n(g.gee.net.DELETE, api)}).PathPrefix(o.PathPrefix).Name("${
        it.nameAndParentName().capitalize()}").HandlerFunc(o.CommandHandler.${it.name().capitalize()})"""
    }}"""
}

fun <T : OperationI> T.toGoSetupModuleHttpRouter(c: GenerationContext,
                                                 derived: String = DesignDerivedKind.IMPL,
                                                 api: String = DesignDerivedKind.API): String {
    val httpRouters = findParentMust(CompilationUnitI::class.java).props().filter {
        it.type() is ControllerI && it.name().endsWith(DesignDerivedType.HttpRouter)
    }
    return httpRouters.joinSurroundIfNotEmptyToString("") {
        """
    if ret = o.${it.name()}.Setup(router); ret != nil {
        return
    }"""
    }
}

fun <T : ConstructorI> T.toGoHttpRouterBody(c: GenerationContext,
                                            derived: String = DesignDerivedKind.IMPL,
                                            api: String = DesignDerivedKind.API): String {
    val item = findParentMust(EntityI::class.java)
    return """
    ret.PathPrefix = ret.PathPrefix + "/" + "${item.name().toPlural().decapitalize()}""""
}

fun <T : ConstructorI> T.toGoHttpModuleRouterBody(c: GenerationContext,
                                                  derived: String = DesignDerivedKind.IMPL,
                                                  api: String = DesignDerivedKind.API): String {
    val item = findParentMust(StructureUnitI::class.java)
    return """
    ret.PathPrefix = ret.PathPrefix + "/" + "${item.name().decapitalize()}""""
}
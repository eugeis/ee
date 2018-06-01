package ee.design.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toPlural
import ee.design.*
import ee.lang.*
import ee.lang.gen.go.g
import ee.lang.gen.go.nameForGoMember
import ee.lang.gen.go.toGoImpl
import ee.lang.gen.go.toGoInstance

fun <T : CommandI<*>> T.toGoHandler(c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)
    val name = c.n(this, derived)
    return """
        ${toGoImpl(c, derived, api)}
func (o *$name) AggregateID() ${c.n(g.eh.UUID)}            { return o.${entity.id().nameForGoMember()} }
func (o *$name) AggregateType() ${c.n(
        g.eh.AggregateType)}  { return ${entity.name()}${DesignDerivedType.AggregateType} }
func (o *$name) CommandType() ${c.n(
        g.eh.CommandType)}      { return ${nameAndParentName().capitalize()}${DesignDerivedType.Command} }
"""
}

fun <T : OperationI<*>> T.toGoSetupHttpRouterBody(c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API): String {
    val entity = findParentMust(EntityI::class.java)

    val finders = entity.findBys().sortedByDescending { it.params().size }
    val counters = entity.countBys().sortedByDescending { it.params().size }
    val exists = entity.existBys().sortedByDescending { it.params().size }

    val commands = entity.commands()
    val creaters = entity.createBys().sortedBy { it.primary() }
    val updaters = entity.updateBys().sortedBy { it.primary() }
    val deleters = entity.deleteBys().sortedBy { it.primary() }

    return """${counters.joinSurroundIfNotEmptyToString("") {
        val idParam = it.params().find { it.isKey() }
        val paramsNoId = it.params().filter { !it.isKey() }
        """
    router.Methods(${c.n(g.net.http.MethodGet, api)}).PathPrefix(o.PathPrefix)${(idParam != null).then(
            { """.Path("/{${idParam!!.name().decapitalize()}}")""" })}.
        Name("${it.nameAndParentName().capitalize()}").HandlerFunc(o.QueryHandler.${it.name().capitalize()}).
        Queries(${c.n(g.gee.net.QueryType, api)}, ${c.n(g.gee.net.QueryTypeCount,
            api)}${paramsNoId.joinSurroundIfNotEmptyToString(", ", ", ") {
            """"${it.name().decapitalize()}", "{${it.name().decapitalize()}}""""
        }})"""
    }}${exists.joinSurroundIfNotEmptyToString("") {
        val idParam = it.params().find { it.isKey() }
        val paramsNoId = it.params().filter { !it.isKey() }
        """
    router.Methods(${c.n(g.net.http.MethodGet, api)}).PathPrefix(o.PathPrefix)${(idParam != null).then(
            { """.Path("/{${idParam!!.name().decapitalize()}}")""" })}.
        Name("${it.nameAndParentName().capitalize()}").HandlerFunc(o.QueryHandler.${it.name().capitalize()}).
        Queries(${c.n(g.gee.net.QueryType, api)}, ${c.n(g.gee.net.QueryTypeExist,
            api)}${paramsNoId.joinSurroundIfNotEmptyToString(", ", ", ") {
            """"${it.name().decapitalize()}", "{${it.name().decapitalize()}}""""
        }})"""
    }}${finders.joinSurroundIfNotEmptyToString("") {
        val idParam = it.params().find { it.isKey() }
        val paramsNoId = it.params().filter { !it.isKey() }
        """
    router.Methods(${c.n(g.net.http.MethodGet, api)}).PathPrefix(o.PathPrefix)${(idParam != null).then(
            { """.Path("/{${idParam!!.name().decapitalize()}}")""" })}.
        Name("${it.nameAndParentName().capitalize()}").HandlerFunc(o.QueryHandler.${it.name().capitalize()})${paramsNoId.joinSurroundIfNotEmptyToString(
            ", ", ".$nL    Queries(", ")") {
            """"${it.name().decapitalize()}", "{${it.name().decapitalize()}}""""
        }}"""
    }}${commands.joinSurroundIfNotEmptyToString("") {
        val idParam = it.props().find { it.isKey() }
        """
    router.Methods(${c.n(g.net.http.MethodPost, api)}).PathPrefix(o.PathPrefix)${(idParam != null).then(
            { """.Path("/{${idParam!!.name().decapitalize()}}")""" })}.
        Queries(${c.n(g.gee.net.Command, api)}, "${it.name()}").
        Name("${it.nameAndParentName().capitalize()}").HandlerFunc(o.CommandHandler.${it.name().capitalize()})"""
    }}${creaters.joinSurroundIfNotEmptyToString("") {
        val idParam = it.props().find { it.isKey() }
        """
    router.Methods(${c.n(g.net.http.MethodPost, api)}).PathPrefix(o.PathPrefix)${(idParam != null).then(
            { """.Path("/{${idParam!!.name().decapitalize()}}")""" })}.${it.primary().not().then {
            """
        Queries(${c.n(g.gee.net.Command, api)}, "${it.name()}")."""
        }}
        Name("${it.nameAndParentName().capitalize()}").HandlerFunc(o.CommandHandler.${it.name().capitalize()})"""
    }}${updaters.joinSurroundIfNotEmptyToString("") {
        val idParam = it.props().find { it.isKey() }
        """
    router.Methods(${c.n(g.net.http.MethodPut, api)}).PathPrefix(o.PathPrefix)${(idParam != null).then(
            { """.Path("/{${idParam!!.name().decapitalize()}}")""" })}.${it.primary().not().then {
            """
        Queries(${c.n(g.gee.net.Command, api)}, "${it.name()}")."""
        }}
        Name("${it.nameAndParentName().capitalize()}").HandlerFunc(o.CommandHandler.${it.name().capitalize()})"""
    }}${deleters.joinSurroundIfNotEmptyToString("") {
        val idParam = it.props().find { it.isKey() }
        """
    router.Methods(${c.n(g.net.http.MethodDelete, api)}).PathPrefix(o.PathPrefix)${(idParam != null).then(
            { """.Path("/{${idParam!!.name().decapitalize()}}")""" })}.${it.primary().not().then {
            """
        Queries(${c.n(g.gee.net.Command, api)}, "${it.name()}")."""
        }}
        Name("${it.nameAndParentName().capitalize()}").HandlerFunc(o.CommandHandler.${it.name().capitalize()})"""
    }}"""
}

fun <T : OperationI<*>> T.toGoSetupModuleHttpRouter(c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API): String {
    val httpRouters = findParentMust(CompilationUnitI::class.java).props().filter {
        it.type() is ControllerI<*> && it.name().endsWith(DesignDerivedType.HttpRouter)
    }
    return httpRouters.joinSurroundIfNotEmptyToString("") {
        """
    if err = o.${it.name()}.Setup(router); err != nil {
        return
    }"""
    }
}

fun <T : ConstructorI<*>> T.toGoHttpRouterBeforeBody(c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API): String {
    val item = findParentMust(EntityI::class.java)
    return """
    pathPrefix = pathPrefix + "/" + "${item.name().toPlural().decapitalize()}"
    entityFactory := func() ${c.n(g.eh.Entity)} { return ${item.toGoInstance(c, derived, api)} }
    repo := readRepos(string(${item.name()}${DesignDerivedType.AggregateType}), entityFactory)"""
}

fun <T : ConstructorI<*>> T.toGoHttpModuleRouterBeforeBody(c: GenerationContext,
    derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API): String {
    val item = findParentMust(StructureUnitI::class.java)
    return """
    pathPrefix = pathPrefix + "/" + "${item.name().decapitalize()}""""
}
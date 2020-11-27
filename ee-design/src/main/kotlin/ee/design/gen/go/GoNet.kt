package ee.design.gen.go

import ee.common.ext.*
import ee.design.*
import ee.lang.*
import ee.lang.gen.go.*

fun <T : CommandI<*>> T.toGoHandler(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    val name = c.n(this, derived)
    return """
        ${toGoImpl(c, derived, api)}
func (o *$name) AggregateID() ${c.n(g.google.uuid.UUID)}            { return o.${entity.propId().nameForGoMember()} }
func (o *$name) AggregateType() ${
        c.n(
            g.eh.AggregateType
        )
    }  { return ${entity.name()}${DesignDerivedType.AggregateType} }
func (o *$name) CommandType() ${
        c.n(
            g.eh.CommandType
        )
    }      { return ${nameAndParentName().capitalize()}${DesignDerivedType.Command} }
"""
}

fun <T : OperationI<*>> T.toGoSetupHttpRouterBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)

    val commandsWithKey = entity.commands().filterCommandsWithKey().sortedByDescending { it.props().size }
    val commandsWithoutKey = entity.commands().filterCommandsWithoutKey().sortedByDescending { it.props().size }
    val creatersWithKey = entity.createBys().filterCommandsWithKey().sortedByDescending { it.props().size }
    val creatersWithoutKey = entity.createBys().filterCommandsWithoutKey().sortedByDescending { it.props().size }
    val updatersWithKey = entity.updateBys().filterCommandsWithKey().sortedByDescending { it.props().size }
    val updatersWithoutKey = entity.updateBys().filterCommandsWithoutKey().sortedByDescending { it.props().size }
    val deletersWithKey = entity.deleteBys().filterCommandsWithKey().sortedByDescending { it.props().size }
    val deletersWithoutKey = entity.deleteBys().filterCommandsWithoutKey().sortedByDescending { it.props().size }

    val queries = entity.findDownByType(DataTypeOperationI::class.java)
    val queriesWithKey = queries.filterOpsWithKey().sortedByDescending { it.params().size }
    val queriesWithoutKey = queries.filterOpsWithoutKey().sortedByDescending { it.params().size }

    val postCommandsWithKey = mutableListOf<CommandI<*>>()
    postCommandsWithKey.addAll(creatersWithKey)
    postCommandsWithKey.addAll(commandsWithKey)

    val postCommandsWithoutKey = mutableListOf<CommandI<*>>()
    postCommandsWithoutKey.addAll(creatersWithoutKey)
    postCommandsWithoutKey.addAll(commandsWithoutKey)

    return """${
        queriesWithKey.routerQueriesWithKeyGet(c, api)
    }${
        postCommandsWithKey.routerCommandsWithKeyPost(c, api)
    }${
        updatersWithKey.routerCommandsWithKeyPut(c, api)
    }${
        deletersWithKey.routerCommandsWithKeyDelete(c, api)
    }${
        queriesWithoutKey.routerQueriesGet(c, api)
    }${
        postCommandsWithoutKey.routerCommandsPost(c, api)
    }${
        updatersWithoutKey.routerCommandsPut(c, api)
    }${
        deletersWithoutKey.routerCommandsDelete(c, api)
    }"""
}

fun Collection<CommandI<*>>.routerCommandsWithKeyPut(c: GenerationContext, api: String) =
    routerCommandsWithKey(c.n(g.net.http.MethodPut, api))

fun Collection<CommandI<*>>.routerCommandsWithKeyPost(c: GenerationContext, api: String) =
    routerCommandsWithKey(c.n(g.net.http.MethodPost, api))

fun Collection<CommandI<*>>.routerCommandsWithKeyDelete(c: GenerationContext, api: String) =
    routerCommandsWithKey(c.n(g.net.http.MethodDelete, api))

fun Collection<CommandI<*>>.routerCommandsWithKey(httpMethod: String) =
    joinWithIndexSurroundIfNotEmptyToString("") { index, item ->
        """
    router.Methods($httpMethod).PathPrefix(o.PathPrefixIdBased).Path("${
            item.buildHttpPathKey(index, item.findPropKey()!!)
        }").
        Name("${item.nameAndParentName().capitalize()}").
        HandlerFunc(o.CommandHandler.${item.name().capitalize()})"""
    }

fun Collection<DataTypeOperationI<*>>.routerQueriesWithKeyGet(c: GenerationContext, api: String) =
    routerQueriesWithKey(c.n(g.net.http.MethodGet, api))

fun Collection<DataTypeOperationI<*>>.routerQueriesWithKey(httpMethod: String) =
    joinWithIndexSurroundIfNotEmptyToString("") { index, item ->
        """
    router.Methods($httpMethod).PathPrefix(o.PathPrefixIdBased).Path("${
            item.buildHttpPathKey(index, item.findParamKey()!!)
        }").
        Name("${item.parentNameAndName().capitalize()}").
        HandlerFunc(o.QueryHandler.${item.name().capitalize()})"""
    }


fun Collection<CommandI<*>>.routerCommandsPut(c: GenerationContext, api: String) =
    routerCommands(c.n(g.net.http.MethodPut, api))

fun Collection<CommandI<*>>.routerCommandsPost(c: GenerationContext, api: String) =
    routerCommands(c.n(g.net.http.MethodPost, api))

fun Collection<CommandI<*>>.routerCommandsDelete(c: GenerationContext, api: String) =
    routerCommands(c.n(g.net.http.MethodDelete, api))

fun Collection<CommandI<*>>.routerCommands(httpMethod: String) =
    joinWithIndexSurroundIfNotEmptyToString("") { index, item ->
        """
    router.Methods($httpMethod).PathPrefix(o.PathPrefix).Path("${
            item.buildHttpPath(index)
        }").
        Name("${item.nameAndParentName().capitalize()}").
        HandlerFunc(o.CommandHandler.${item.name().capitalize()})"""
    }

fun Collection<DataTypeOperationI<*>>.routerQueriesGet(c: GenerationContext, api: String) =
    routerQueries(c.n(g.net.http.MethodGet, api))

fun Collection<DataTypeOperationI<*>>.routerQueries(httpMethod: String) =
    joinWithIndexSurroundIfNotEmptyToString("") { index, item ->
        """
    router.Methods($httpMethod).PathPrefix(o.PathPrefix).Path("${item.buildHttpPath(index)}").
        Name("${item.parentNameAndName().capitalize()}").
        HandlerFunc(o.QueryHandler.${item.name().capitalize()})"""
    }

private fun ItemI<*>.buildHttpPathKey(index: Int, keyParam: AttributeI<*>) =
    if (index == 0) "/{${keyParam.name().decapitalize()}}" else "/{${keyParam.name().decapitalize()}}/${
        name().removeSuffixById().toHyphenLowerCase()
    }"

private fun ItemI<*>.buildHttpPath(index: Int) =
    if (index == 0) "" else "/${name().removeSuffixAll().toHyphenLowerCase()}"

fun String.removeSuffixById(): String = removeSuffix("ById")
fun String.removeSuffixAll(): String = removeSuffix("All")

fun <T : OperationI<*>> T.toGoSetupModuleHttpRouter(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
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

fun <T : ConstructorI<*>> T.toGoHttpRouterBeforeBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val item = findParentMust(EntityI::class.java)
    return """
    pathPrefixIdBased := pathPrefix + "/" + "${item.name().decapitalize()}"
    pathPrefix = pathPrefix + "/" + "${item.name().toPlural().decapitalize()}"   
    entityFactory := func() ${c.n(g.eh.Entity)} { return ${item.toGoInstance(c, derived, api)} }
    repo := readRepos(string(${item.name()}${DesignDerivedType.AggregateType}), entityFactory)
    httpQueryHandler := eh.NewHttpQueryHandlerFull()
    httpCommandHandler := eh.NewHttpCommandHandlerFull(context, commandBus)
    """
}

fun <T : ConstructorI<*>> T.toGoHttpModuleRouterBeforeBody(
    c: GenerationContext,
    derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
    val item = findParentMust(StructureUnitI::class.java)
    return """
    pathPrefix = pathPrefix + "/" + "${item.name().decapitalize()}""""
}
package ee.design.gen.go

import ee.common.ext.*
import ee.design.*
import ee.lang.*
import ee.lang.gen.go.*
import java.util.*

fun <T : CommandI<*>> T.toGoHandler(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)
    val name = c.n(this, derived)
    return """
        ${toGoImpl(c, derived, api)}
func (o *$name) AggregateID() ${c.n(g.google.uuid.UUID)}            { return o.${
        entity.propIdOrAdd().nameForGoMember()
    } }
func (o *$name) AggregateType() ${
        c.n(
            g.eh.AggregateType
        )
    }  { return ${entity.name()}${DesignDerivedType.AggregateType} }
func (o *$name) CommandType() ${
        c.n(
            g.eh.CommandType
        )
    }      { return ${dataTypeNameAndParentName().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${DesignDerivedType.Command} }
"""
}

fun <T : OperationI<*>> T.toGoSetupHttpRouterBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API
): String {
    val entity = findParentMust(EntityI::class.java)

    val postCommandsWithKey = mutableListOf<CommandI<*>>()
    val putCommandsWithKey = mutableListOf<CommandI<*>>()
    val deleteCommandsWithKey = mutableListOf<CommandI<*>>()

    val postCommandsWithoutKey = mutableListOf<CommandI<*>>()
    val putCommandsWithoutKey = mutableListOf<CommandI<*>>()
    val deleteCommandsWithoutKey = mutableListOf<CommandI<*>>()


    postCommandsWithKey.addAll(entity.createBys().filterCommandsWithKey().sortedByDescending { it.props().size })
    postCommandsWithoutKey.addAll(entity.createBys().filterCommandsWithoutKey().sortedByDescending { it.props().size })

    putCommandsWithKey.addAll(entity.updateBys().filterCommandsWithKey().sortedByDescending { it.props().size })
    putCommandsWithoutKey.addAll(entity.updateBys().filterCommandsWithoutKey().sortedByDescending { it.props().size })

    deleteCommandsWithKey.addAll(entity.deleteBys().filterCommandsWithKey().sortedByDescending { it.props().size })
    deleteCommandsWithoutKey.addAll(
        entity.deleteBys().filterCommandsWithoutKey().sortedByDescending { it.props().size })

    postCommandsWithKey.addAll(entity.addChildBys().filterCommandsWithKey().sortedByDescending { it.props().size })
    postCommandsWithoutKey.addAll(
        entity.addChildBys().filterCommandsWithoutKey().sortedByDescending { it.props().size })

    putCommandsWithKey.addAll(entity.updateChildBys().filterCommandsWithKey().sortedByDescending { it.props().size })
    putCommandsWithoutKey.addAll(
        entity.updateChildBys().filterCommandsWithoutKey().sortedByDescending { it.props().size })

    deleteCommandsWithKey.addAll(entity.removeChildBys().filterCommandsWithKey().sortedByDescending { it.props().size })
    deleteCommandsWithoutKey.addAll(
        entity.removeChildBys().filterCommandsWithoutKey().sortedByDescending { it.props().size })

    postCommandsWithKey.addAll(entity.commands().filterCommandsWithKey().sortedByDescending { it.props().size })
    postCommandsWithoutKey.addAll(entity.commands().filterCommandsWithoutKey().sortedByDescending { it.props().size })

    val queries = entity.findDownByType(DataTypeOperationI::class.java)
    val queriesWithKey = queries.filterOpsWithKey().sortedByDescending { it.params().size }
    val queriesWithoutKey = queries.filterOpsWithoutKey().sortedByDescending { it.params().size }

    return """${
        queriesWithKey.routerQueriesWithKeyGet(c, api)
    }${
        postCommandsWithKey.routerCommandsWithKeyPost(c, api)
    }${
        putCommandsWithKey.routerCommandsWithKeyPut(c, api)
    }${
        deleteCommandsWithKey.routerCommandsWithKeyDelete(c, api)
    }${
        queriesWithoutKey.routerQueriesGet(c, api)
    }${
        postCommandsWithoutKey.routerCommandsPost(c, api)
    }${
        putCommandsWithoutKey.routerCommandsPut(c, api)
    }${
        deleteCommandsWithoutKey.routerCommandsDelete(c, api)
    }"""
}

fun Collection<CommandI<*>>.routerCommandsWithKeyPut(c: GenerationContext, api: String) =
    routerCommandsWithKey(c.n(g.net.http.MethodPut, api))

fun Collection<CommandI<*>>.routerCommandsWithKeyPost(c: GenerationContext, api: String) =
    routerCommandsWithKey(c.n(g.net.http.MethodPost, api))

fun Collection<CommandI<*>>.routerCommandsWithKeyDelete(c: GenerationContext, api: String) =
    routerCommandsWithKey(c.n(g.net.http.MethodDelete, api))

fun Collection<CommandI<*>>.routerCommandsWithKey(httpMethod: String) =
    joinWithIndexSurroundIfNotEmptyToString("") { index, command ->
        """
    router.Methods($httpMethod).PathPrefix(o.PathPrefixIdBased).Path("${
            command.buildHttpPathKey(index, command.findPropKey()!!)
        }").
        Name("${command.dataTypeNameAndParentName()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}").
        HandlerFunc(o.CommandHandler.${command.name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }})"""
    }

fun Collection<DataTypeOperationI<*>>.routerQueriesWithKeyGet(c: GenerationContext, api: String) =
    routerQueriesWithKey(c.n(g.net.http.MethodGet, api))

fun Collection<DataTypeOperationI<*>>.routerQueriesWithKey(httpMethod: String) =
    joinWithIndexSurroundIfNotEmptyToString("") { index, item ->
        """
    router.Methods($httpMethod).PathPrefix(o.PathPrefixIdBased).Path("${
            item.buildHttpPathKey(index, item.findParamKey()!!)
        }").
        Name("${item.dataTypeParentNameAndName()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}").
        HandlerFunc(o.QueryHandler.${item.name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }})"""
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
        Name("${item.dataTypeNameAndParentName()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}").
        HandlerFunc(o.CommandHandler.${item.name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }})"""
    }

fun Collection<DataTypeOperationI<*>>.routerQueriesGet(c: GenerationContext, api: String) =
    routerQueries(c.n(g.net.http.MethodGet, api))

fun Collection<DataTypeOperationI<*>>.routerQueries(httpMethod: String) =
    joinWithIndexSurroundIfNotEmptyToString("") { index, item ->
        """
    router.Methods($httpMethod).PathPrefix(o.PathPrefix).Path("${item.buildHttpPath(index)}").
        Name("${item.dataTypeParentNameAndName()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}").
        HandlerFunc(o.QueryHandler.${item.name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }})"""
    }

private fun ItemI<*>.buildHttpChildPathKey(): String {
    return when (val command = this) {
        is UpdateChildByI -> "/{${command.type().propIdNameParent()}}"
        is RemoveChildByI -> "/{${command.type().propIdNameParent()}}"
        else -> ""
    }
}

private fun ItemI<*>.buildHttpPathKey(index: Int, keyParam: AttributeI<*>): String {
    return if (index == 0) {
        "/{${keyParam.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}}"
    } else {
        "/{${keyParam.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}}/${name().removeSuffixForRoute().toHyphenLowerCase()}${
            buildHttpChildPathKey()}"
    }
}

private fun ItemI<*>.buildHttpPath(index: Int) =
    if (index == 0) "" else "/${name().removeSuffixForRoute().toHyphenLowerCase()}"

fun String.removeSuffixForRoute(): String = removeOneOfSuffixes("ById", "Add", "Update", "Remove", "All")

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
    val entity = findParentMust(EntityI::class.java)
    return """
    pathPrefixIdBased := pathPrefix + "/" + "${entity.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}"
    pathPrefix = pathPrefix + "/" + "${entity.name().toPlural().replaceFirstChar { it.lowercase(Locale.getDefault()) }}"   
    ctx := newContext("${entity.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}")
    httpQueryHandler := ${c.n(g.gee.ehu.NewHttpQueryHandlerFull)}()
    httpCommandHandler := ${c.n(g.gee.ehu.NewHttpCommandHandlerFull)}(ctx, commandBus)
    """
}

fun <T : ConstructorI<*>> T.toGoHttpModuleRouterBeforeBody(
    c: GenerationContext,
    derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
    val structureUnit = findParentMust(StructureUnitI::class.java)
    val aggregates = structureUnit.findAggregates()
    return """
    pathPrefix = pathPrefix + "/" + "${structureUnit.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}"
    ${
        aggregates.joinSurroundIfNotEmptyToString("") { entity ->
            val aggregateHandler = "${entity.name()}AggregateHandler"
            val projectEventHandler = "${entity.name()}Projector"

            """
    var ${projectEventHandler.replaceFirstChar { it.lowercase(Locale.getDefault()) }} *$projectEventHandler
    if  ${projectEventHandler.replaceFirstChar { it.lowercase(Locale.getDefault()) }}, err = esEngine.${entity.name()}.Register${
        projectEventHandler}(string(${entity.name()}${DesignDerivedType.AggregateType}), 
             esEngine.${entity.name()}.AggregateHandlers, esEngine.${entity.name()}.Events); err != nil {
        return
    }
     
    ${entity.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}Router := New${entity.name()}Router(pathPrefix, newContext, esEngine.CommandBus,  ${
                projectEventHandler.replaceFirstChar { it.lowercase(Locale.getDefault()) }
            }.Repo)       
       """
        }
    }"""
}

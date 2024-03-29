package ee.design.gen.go

import ee.common.ext.toPlural
import ee.design.*
import ee.lang.*
import ee.lang.gen.go.g
import java.util.*

fun StructureUnitI<*>.addEsArtifacts() {

    findAggregates().groupBy {
        it.findParentMust(ModuleI::class.java)
    }.forEach { (module, entities) ->
        module.extend {

            val aggregateInitializers = mutableMapOf<String, ControllerI<*>>()
            val httpRouters = mutableMapOf<String, ControllerI<*>>()
            val httpClients = mutableMapOf<String, ControllerI<*>>()

            entities.forEach {
                it.extend {
                    addEsArtifacts(aggregateInitializers, httpRouters, httpClients)
                }
            }

            val esEngine = controller {
                name(DesignDerivedType.EsEngine).derivedAsType(DesignDerivedType.Aggregate)
                val middleware = prop { name("middleware").type(g.gee.ehu.Middleware).anonymous() }

                val aggregateInitializerProps = aggregateInitializers.map { (entityName, item) ->
                    prop {
                        type(item).name(entityName)
                    }
                }
                constr {
                    params(
                        middleware,
                        *aggregateInitializerProps.map { p(it) { default(true) } }.toTypedArray()
                    )
                }
                op {
                    name("Setup")
                    macrosBody(OperationI<*>::toGoEhEngineSetupBody.name)
                }
            }

            val projectors = mutableListOf<AttributeI<*>>()

            controller {
                name(DesignDerivedType.HttpRouter).derivedAsType(DesignDerivedType.Http)
                entities.forEach { entity ->
                    val entityProj = "${entity.name()}${DesignDerivedType.Projector}"
                    val p = prop {
                        name(entityProj.replaceFirstChar { it.lowercase(Locale.getDefault()) }).type(Type().name(entityProj)).default().notInitByDefaultTypeValue()
                    }
                    projectors.add(p)
                }
                val pathPrefix = propS { name("pathPrefix") }
                val httpRouterParams = httpRouters.map { (_, item) ->
                    prop {
                        type(item).name("${item.parent().name()}${item.name()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}")
                    }
                }
                op {
                    name("Setup")
                    p("router", g.mux.Router)
                    macrosBody(OperationI<*>::toGoSetupModuleHttpRouter.name)
                }
                constr {
                    params(
                        pathPrefix, p {
                            type(lambda {
                                p("namespace")
                                notErr()
                                ret(g.context.Context)
                            }).name("newContext")
                        },
                        p { name("esEngine").type(esEngine) },
                        *projectors.toTypedArray(),
                        *httpRouterParams.map { p(it) { default().notInitByDefaultTypeValue() } }.toTypedArray()
                    )
                    errorHandling()
                    macrosBeforeBody(ConstructorI<*>::toGoHttpModuleRouterBeforeBody.name)
                }
            }

            val client = controller {
                name(DesignDerivedType.HttpClient).derivedAsType(DesignDerivedType.Client)
                val url = propS { name("url") }
                val client = prop { name("client").type(g.net.http.Client) }

                val httpClientParams = httpClients.mapValues { (_, item) ->
                    prop {
                        type(item).name("${item.parent().name()}${item.name()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}")
                    }
                }

                constr {
                    params(url, client, *httpClientParams.map { (_, it) ->
                        p(it) { default() }
                    }.toTypedArray())
                    macrosBeforeBody(ConstructorI<*>::toGoHttpModuleClientBeforeBody.name)
                }
            }


            val entityClis = mutableMapOf<String, ControllerI<*>>()
            entities.forEach { entity ->
                entity.extend {
                    entityClis[entity.name()] = entity.addCli(httpClients[entity.name()]!!)
                }
            }

            controller {
                name(DesignDerivedType.Cli).derivedAsType(DesignDerivedType.Cli)
                prop { name("client").type(client) }

                val cliParams = entityClis.mapValues { (_, item) ->
                    prop {
                        type(item).name("${item.parent().name()}${item.name()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}")
                    }
                }

                constr {
                    params(p { name("url").type(n.String) }, p { name("httpClient").type(g.net.http.Client) },
                        p { name("client").type(client).default().notInitByDefaultTypeValue() },

                        *cliParams.map { (entityName, it) ->
                            val entityClient = httpClients[entityName]!!
                            p(it) { default().value("New${it.name()}(client.${entityClient.dataTypeParentNameAndName()})") }
                        }.toTypedArray()
                    )
                    macrosBeforeBody(ConstructorI<*>::toGoCobraBeforeBody.name)
                }
            }
        }
    }
}

private fun EntityI<*>.addEsArtifacts(
    fillAggregateInitializer: MutableMap<String, ControllerI<*>>,
    fillHttpRouters: MutableMap<String, ControllerI<*>>,
    fillHttpClients: MutableMap<String, ControllerI<*>>
) {

    val entity = this

    val propId = entity.propIdOrAdd()

    val queryRepository = addQueryRepository()

    val events = findDownByType(EventI::class.java).sortedBy { it.name() }
    val commands = findDownByType(CommandI::class.java)

    val httpQueryHandler = addHttpQueryHandler(queryRepository)

    val httpCommandHandler = addHttpCommandHandler()

    val httpRouter = controller {
        name(DesignDerivedType.HttpRouter).derivedAsType(DesignDerivedType.Http)
        val pathPrefix = propS { name("pathPrefix") }
        val pathPrefixIdBased = propS { name("pathPrefixIdBased") }
        val queryHandler = prop { type(httpQueryHandler).name("queryHandler") }
        val commandH = prop { type(httpCommandHandler).name("commandHandler") }

        op {
            name("Setup")
            p("router", g.mux.Router)
            macrosBody(OperationI<*>::toGoSetupHttpRouterBody.name)
        }
        constr {
            params(
                pathPrefix,
                p(pathPrefixIdBased) { default().notInitByDefaultTypeValue() },
                p {
                    type(lambda {
                        p("namespace")
                        notErr()
                        ret(g.context.Context)
                    }).name("newContext")
                },
                p { type(g.eh.CommandBus).name("commandBus") },
                p { type(g.eh.ReadRepo).name("repo") },
                p { type(queryRepository).default().name("queryRepository") },
                p(queryHandler) { default().initByDefaultTypeValue() },
                p(commandH) { default().initByDefaultTypeValue() })
            macrosBeforeBody(ConstructorI<*>::toGoHttpRouterBeforeBody.name)
        }
    }

    val httpClient = controller {
        name(DesignDerivedType.HttpClient).derivedAsType(DesignDerivedType.Client)

        val urlIdBased = propS { name("urlIdBased") }
        val url = propS { name("url") }
        val client = prop { type(g.net.http.Client).name("client") }

        constr {
            params(p(urlIdBased).default().notInitByDefaultTypeValue(), url, client)
            macrosBeforeBody(ConstructorI<*>::toGoHttpClientBeforeBody.name)
        }

        op {
            name("importJSON")
            params(p { name("fileJSON").type(n.String) })

            macrosBody(OperationI<*>::toGoHttpClientImportJsonBody.name)
        }

        op {
            name("exportJSON")
            params(p { name("targetFileJSON").type(n.String) })

            macrosBody(OperationI<*>::toGoHttpClientExportJsonBody.name)
        }

        op {
            name("Create")
            params(p { name("item").type(entity) })

            macrosBody(OperationI<*>::toGoHttpClientCreateBody.name)
        }

        op {
            name("CreateItems")
            params(p { name("items").type(n.List.GT(entity)) })

            macrosBody(OperationI<*>::toGoHttpClientCreateItemsBody.name)
        }

        op {
            name("DeleteBy${propId.name().toPlural()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}")
            params(p { name("itemIds").type(n.List.GT(entity.propIdOrAdd().type())) })

            macrosBody(OperationI<*>::toGoHttpClientDeleteByIdsBody.name)
        }

        op {
            name("DeleteBy${propId.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}")
            params(p { name("itemId").type(g.google.uuid.UUID) })

            macrosBody(OperationI<*>::toGoHttpClientDeleteByIdBody.name)
        }

        op {
            name("FindAll")
            ret(n.List.GT(entity))
            macrosBody(OperationI<*>::toGoHttpClientFindAllBody.name)
        }

        op {
            name("ReadFileJSON")
            params(p { name("fileJSON").type(n.String) })
            ret(n.List.GT(entity))
            macrosBody(OperationI<*>::toGoHttpClientReadFileJsonBody.name)
        }
    }

    val fillStateMachineHandlers = mutableMapOf<String, ControllerI<*>>()
    val fillStateMachineExecutors = mutableMapOf<String, ControllerI<*>>()

    addStateMachineArtifacts(fillStateMachineHandlers, fillStateMachineExecutors)

    val aggrInitializer = controller {
        name(DesignDerivedType.AggregateEngine).derivedAsType(DesignDerivedType.Aggregate)
        prop {
            type(g.gee.ehu.AggregateEngine)
                .anonymous(true).name("aggregateInitializer")
        }

        fillStateMachineExecutors.forEach { (_, stateMachineExecutor) ->
            val executorName =
                "${stateMachineExecutor.derivedFrom().sourceArtifactsPrefix()}${DesignDerivedType.Executors}"
            prop { name(executorName).type(stateMachineExecutor).default() }
        }

        fillStateMachineHandlers.forEach { (_, stateMachineHandler) ->
            val handlerName =
                "${stateMachineHandler.derivedFrom().sourceArtifactsPrefix()}${DesignDerivedType.Handlers}"
            prop { name(handlerName).type(stateMachineHandler).default() }
        }

        macrosBefore(CompilationUnitI<*>::toGoAggregateEngineConst.name)
        macrosAfter(CompilationUnitI<*>::toGoAggregateEngineRegisterForEvents.name)

        constr {
            params(
                p { type(g.gee.ehu.Middleware).name("middleware") }
            )
            macrosBody(ConstructorI<*>::toGoAggregateEngineBody.name)
        }

        op {
            name("Setup")
            macrosBody(OperationI<*>::toGoAggregateEngineSetupBody.name)
        }

        //add event type enum
        enumType {
            name("${entity.name()}EventType")
            derivedAsType(DesignDerivedType.AggregateEvents)
            events.forEach {
                lit { name(it.dataTypeParentNameAndName()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }) }
            }
        }

        //add command type enum
        enumType {
            name("${entity.name()}CommandType")
            derivedAsType(DesignDerivedType.AggregateCommands)
            commands.forEach {
                lit { name(it.dataTypeNameAndParentName()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }) }
            }
        }
    }

    fillHttpRouters[entity.name()] = httpRouter
    fillHttpClients[entity.name()] = httpClient
    fillAggregateInitializer[entity.name()] = aggrInitializer

}

private fun EntityI<*>.addStateMachineArtifacts(
    fillStateMachineHandlers: MutableMap<String, ControllerI<*>>,
    fillStateMachineExecutors: MutableMap<String, ControllerI<*>>
) {

    val stateMachines = findDownByType(StateMachineI::class.java)
    stateMachines.forEach {
        it.addStateMachineArtifacts(this, fillStateMachineHandlers, fillStateMachineExecutors)
    }
}

private fun StateMachineI<*>.addStateMachineArtifacts(
    entity: EntityI<*>,
    fillStateMachineHandlers: MutableMap<String, ControllerI<*>>,
    fillStateMachineExecutors: MutableMap<String, ControllerI<*>>
) {

    val stateMachine = this

    val entityParamName = entity.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }

    val stateMachinePrefix = sourceArtifactsPrefix()
    val entityStateMachinePrefix = "${entity.name()}$stateMachinePrefix"

    val handlers = mutableListOf<ControllerI<*>>()
    val executors = mutableListOf<ControllerI<*>>()

    //add state type enum
    val stateType = enumType {
        name("${entityStateMachinePrefix}StateType")
        derivedAsType(DesignDerivedType.StateMachine)
        states().forEach {
            lit { name(it.name()) }
        }
    }

    // add state prop
    entity.prop {
        name("${stateMachinePrefix}State").type(n.String)
    }

    controller {
        ifc().name("${entityStateMachinePrefix}${DesignDerivedType.Executor}")
        derivedAsType(DesignDerivedType.StateMachine).derivedFrom(stateMachine)

        op {
            name("Execute")
            p("cmd", g.eh.Command)
            p(entityParamName, entity)
            p("store", g.gee.ehu.AggregateStoreEvent)
        }
    }

    states().forEach { state ->
        val statePrefix = state.sourceArtifactsPrefix(entityStateMachinePrefix)
        val events = state.uniqueEvents()
        val commands = state.uniqueCommands()

        //add event handler
        handlers.add(
            controller {
                name("$statePrefix${DesignDerivedType.Handler}")
                    .derivedAsType(DesignDerivedType.StateMachineEvents).derivedFrom(state)
                events.forEach { event ->
                    prop {
                        type(lambda {
                            p(event.name(), g.eh.Event)
                            if (event.hasData()) {
                                p("${event.name()}Data", event)
                            }
                            p(entityParamName, entity)

                        }).name("${event.name()}${DesignDerivedType.Handler}")
                    }
                }

                op {
                    name("StateType")
                    macrosBody(OperationI<*>::toGoStateEventType.name)
                    ret(stateType).notErr()
                }

                op {
                    name("Apply")
                    p("event", g.eh.Event)
                    p(entityParamName, entity)
                    ret(stateType)
                    macrosBody(OperationI<*>::toGoStateEventHandlerApplyEvent.name)
                }

                op {
                    name("SetupEventHandler")

                    macrosBody(OperationI<*>::toGoStateEventHandlerSetupBody.name)
                }
            })

        //add executor
        executors.add(
            controller {
                name("$statePrefix${DesignDerivedType.Executor}")
                    .derivedAsType(DesignDerivedType.StateMachineCommands).derivedFrom(state)

                prop {
                    name("commandsPreparer").type(lambda {
                        p("cmd", g.eh.Command)
                        p(entityParamName, entity)
                    })
                }

                op {
                    name("AddCommandsPreparer").notErr()
                    p("preparer", lambda {
                        p("cmd", g.eh.Command)
                        p(entityParamName, entity)
                    })
                    macrosBody(OperationI<*>::toGoStateAddCommandsPreparerBody.name)
                }

                commands.forEach { command ->
                    prop {
                        type(lambda {
                            p(command.name(), command)
                            p(entityParamName, entity)
                            p("store", g.gee.ehu.AggregateStoreEvent)

                        }).name("${command.name()}${DesignDerivedType.Handler}")
                    }

                    op {
                        name("Add${command.name()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}Preparer").notErr()
                        p("preparer", lambda {
                            p("cmd", command)
                            p(entityParamName, entity)

                        })
                        derivedFrom(command)
                        macrosBody(OperationI<*>::toGoStateCommandHandlerAddCommandPreparerBody.name)
                    }
                }

                op {
                    name("StateType")
                    macrosBody(OperationI<*>::toGoStateEventType.name)
                    ret(stateType).notErr()
                }

                op {
                    name("Execute")
                    p("cmd", g.eh.Command)
                    p(entityParamName, entity)
                    p("store", g.gee.ehu.AggregateStoreEvent)

                    macrosBody(OperationI<*>::toGoStateCommandHandlerExecuteBody.name)
                }

                op {
                    name("SetupCommandHandler")

                    macrosBody(OperationI<*>::toGoStateCommandHandlerSetupBody.name)
                }
            })
    }

    //add state machine handlers
    controller {
        ifc().name("${entityStateMachinePrefix}${DesignDerivedType.Handler}")
            .derivedAsType(DesignDerivedType.StateMachine).derivedFrom(stateMachine)

        op {
            name("Apply")
            p("event", g.eh.Event)
            p(entityParamName, entity)
        }
    }

    val eventsHandler = controller {
        name("$entityStateMachinePrefix${DesignDerivedType.Handlers}")
            .derivedAsType(DesignDerivedType.StateMachine).derivedFrom(stateMachine)

        handlers.forEach {
            prop { type(it).name(it.derivedFrom().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }).default() }
        }

        prop {
            name("eventsPreparer").type(lambda {
                p("event", g.eh.Event)
                p(entityParamName, entity)
            })
        }.meta()
        constructorFull { }

        op {
            name("AddEventsPreparer").notErr()
            p("preparer", lambda {
                p("event", g.eh.Event)
                p(entityParamName, entity)
            })
            macrosBody(OperationI<*>::toGoStateEventHandlersPreparerBody.name)
        }

        op {
            name("Apply")
            p("event", g.eh.Event)
            p(entityParamName, entity)
            macrosBody(OperationI<*>::toGoStatesEventHandlerApplyEvent.name)
        }

        op {
            name("SetupEventHandler")
            macrosBody(OperationI<*>::toGoStatesEventHandlerSetupBody.name)
        }
    }

    //add state machine executors
    val commandsHandler = controller {
        name("$entityStateMachinePrefix${DesignDerivedType.Executors}")
            .derivedAsType(DesignDerivedType.StateMachine).derivedFrom(stateMachine)

        executors.forEach {
            prop { type(it).name(it.derivedFrom().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }).default() }
        }

        prop {
            name("commandsPreparer").type(lambda {
                p("cmd", g.eh.Command)
                p(entityParamName, entity)
            })
        }.meta()
        constructorFull { }

        op {
            name("AddCommandsPreparer").notErr()
            p("preparer", lambda {
                p("cmd", g.eh.Command)
                p(entityParamName, entity)
            })
            macrosBody(OperationI<*>::toGoStateAddCommandsPreparerBody.name)
        }

        op {
            name("Execute")
            p("cmd", g.eh.Command)
            p(entityParamName, entity)
            p("store", g.gee.ehu.AggregateStoreEvent)

            macrosBody(OperationI<*>::toGoStatesCommandHandlerExecute.name)
        }

        op {
            name("SetupCommandHandler")

            macrosBody(OperationI<*>::toGoStatesCommandHandlerSetupBody.name)
        }
    }


    controller {
        name(entityStateMachinePrefix).derivedAsType(DesignDerivedType.StateMachine)
        prop { name("aggregateBase").type(g.eh.AggregateBase).anonymous() }
        prop { name(entityParamName).type(entity) }
        prop { name("$stateMachinePrefix${DesignDerivedType.Executors}".replaceFirstChar { it.lowercase(Locale.getDefault()) }).type(commandsHandler) }
        prop { name("$stateMachinePrefix${DesignDerivedType.Handlers}".replaceFirstChar { it.lowercase(Locale.getDefault()) }).type(eventsHandler) }

        op {
            name("ApplyEvent")
            p("ctx", g.context.Context)
            p("event", g.eh.Event)

            macrosBody(OperationI<*>::toGoAggregateApplyEventBody.name)
        }

        op {
            name("HandleCommand")
            p("ctx", g.context.Context)
            p("cmd", g.eh.Command)

            macrosBody(OperationI<*>::toGoAggregateHandleCommand.name)
        }

        constructorFull()
    }

    fillStateMachineExecutors[commandsHandler.name()] = commandsHandler
    fillStateMachineHandlers[eventsHandler.name()] = eventsHandler
}


fun ItemI<*>.sourceArtifactsPrefix(): String {
    return if (this is AggregateHandler) {
        "Aggregate"
    } else {
        this.name()
    }
}

fun StateI<*>.sourceArtifactsPrefix(stateMachinePrefix: String): String {
    return "$stateMachinePrefix${name()}"
}

private fun EntityI<*>.addCli(client: ControllerI<*>): BusinessControllerI<*> {

    val propId = propIdOrAdd()

    return controller {
        name(DesignDerivedType.Cli).derivedAsType(DesignDerivedType.Cli)
        val entityClient = prop { type(client).name("client") }
        val optsPropId = propS { name(propId.name()) }
        val optsPropIds = propS { name(propId.name().toPlural()) }

        constr {
            params(entityClient)
        }

        op {
            name("BuildCommands")
            ret(n.List.GT(g.cobra.Command)).notErr()

            macrosBody(OperationI<*>::toGoCobraBuildCommands.name)
        }

        op {
            name("BuildCommandImportJSON")
            ret(g.cobra.Command).notErr()
            macrosBody(OperationI<*>::toGoCobraImportJsonBody.name)
        }

        op {
            name("BuildCommandExportJSON")
            ret(g.cobra.Command).notErr()
            macrosBody(OperationI<*>::toGoCobraExportJsonBody.name)
        }

        op {
            name("BuildCommandDeleteBy${propId.name().toPlural()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}")
            ret(g.cobra.Command).notErr()
            macrosBody(OperationI<*>::toGoCobraDeleteByIdsBody.name)
        }

        op {
            name("BuildCommandDeleteBy${propId.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}")
            ret(g.cobra.Command).notErr()
            macrosBody(OperationI<*>::toGoCobraDeleteByIdBody.name)
        }
    }
}

private fun EntityI<*>.addHttpQueryHandler(queryRepository: BusinessControllerI<*>): BusinessControllerI<*> {
    val dataTypeOperations = findDownByType(DataTypeOperationI::class.java)

    return controller {
        name(DesignDerivedType.HttpQueryHandler).derivedAsType(DesignDerivedType.Http)
        prop {
            type(g.gee.ehu.HttpQueryHandler)
                .anonymous(true).name(DesignDerivedType.HttpQueryHandler.replaceFirstChar { it.lowercase(Locale.getDefault()) })
        }
        prop { type(queryRepository).name("queryRepository") }

        dataTypeOperations.forEach {
            op {
                name(it.name()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }).notErr()
                p("w", g.net.http.ResponseWriter)
                p("r", g.net.http.Request)
                derivedFrom(it)
                macrosBody(OperationI<*>::toGoHttpHandlerBody.name)
            }
        }

        constructorFull { }
    }
}

private fun EntityI<*>.addQueryRepository(): BusinessControllerI<*> {

    return controller {
        name(DesignDerivedType.QueryRepository).derivedAsType(DesignDerivedType.Query)
        prop(g.eh.ReadRepo).replaceable(false).name("repo")
        prop(g.context.Context).replaceable(false).name("ctx")

        //queries
        findBys().forEach {
            op(it).macrosBody(OperationI<*>::toGoFindByBody.name)
        }

        countBys().forEach {
            op(it).macrosBody(OperationI<*>::toGoCountByBody.name)
        }

        existBys().forEach {
            op(it).macrosBody(OperationI<*>::toGoExistByBody.name)
        }

        constructorFull()
    }
}

private fun EntityI<*>.addHttpCommandHandler(): BusinessControllerI<*> {

    val commands = findDownByType(CommandI::class.java).sortedBy { it.name() }

    return controller {
        name(DesignDerivedType.HttpCommandHandler).derivedAsType(DesignDerivedType.Http)
        prop {
            type(g.gee.ehu.HttpCommandHandler)
                .anonymous(true).name(DesignDerivedType.HttpCommandHandler.replaceFirstChar { it.lowercase(Locale.getDefault()) })
        }

        //commands
        commands.forEach {
            op {
                name(it.name()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }).notErr()
                p("w", g.net.http.ResponseWriter)
                p("r", g.net.http.Request)
                derivedFrom(it)
                macrosBody(OperationI<*>::toGoHttpHandlerCommandBody.name)
            }
        }

        constructorFull {}
    }
}

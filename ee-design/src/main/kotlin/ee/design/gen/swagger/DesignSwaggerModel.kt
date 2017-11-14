package ee.design.gen.swagger

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toHyphenLowerCase
import ee.design.*
import ee.lang.*
import ee.lang.gen.swagger.*


fun <T : EntityIB<*>> T.toSwaggerGet(c: GenerationContext,
                                 derived: String = LangDerivedKind.IMPL,
                                 api: String = LangDerivedKind.API): String {
    val finders = findDownByType(FindBy::class.java)
    val counters = findDownByType(CountByIB::class.java)
    val exists = findDownByType(ExistByIB::class.java)

    val paramsMap = mutableMapOf<String, AttributeIB<*>>()
    finders.forEach { it.params().forEach { paramsMap[it.name()] = it } }
    counters.forEach { it.params().forEach { paramsMap[it.name()] = it } }
    exists.forEach { it.params().forEach { paramsMap[it.name()] = it } }

    val params = paramsMap.values.sortedBy { it.name() }

    return if (finders.isNotEmpty() || counters.isNotEmpty() || exists.isNotEmpty()) {
        """
    get:${toSwaggerDescription("      ")}
      parameters:
        - name: operationId
          in: query
          description: id of the operation, e.g. findByName
          required: false
          schema:
            type: string
            enum: ${finders.toSwaggerLiterals("              ")}${
        counters.toSwaggerLiterals("            ")}${
        exists.toSwaggerLiterals("            ")}
        - name: operationType
          in: query
          required: false
          schema:
            type: string
            enum: ${finders.isNotEmpty().then {
            """
              - find"""
        }}${counters.isNotEmpty().then {
            """
              - count"""
        }}${exists.isNotEmpty().then {
            """
              - exists"""
        }}${params.joinSurroundIfNotEmptyToString("") { param ->
            """
        - name: ${param.name()}
          in: query${param.toSwaggerDescription()}
          required: false
          schema:${param.toSwaggerTypeDef(c, api, "            ")}"""
        }}
      responses:
        '200':
          description: fsdfsdf
          content:
            application/json:
              schema:${
        toSwagger(c, api, "                ")}"""
    } else ""
}

fun <T : EntityIB<*>> T.toSwaggerPost(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                  api: String = LangDerivedKind.API): String {
    return """"""
}

fun <T : EntityIB<*>> T.toSwaggerPut(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                 api: String = LangDerivedKind.API): String {
    return """"""
}

fun <T : EntityIB<*>> T.toSwaggerDelete(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                    api: String = LangDerivedKind.API): String {
    return """"""
}

fun <T : CompIB<*>> T.toSwagger(c: GenerationContext,
                            derived: String = LangDerivedKind.IMPL,
                            api: String = LangDerivedKind.API): String {
    val moduleAggregates = findDownByType(EntityIB::class.java).filter { !it.virtual() && it.belongsToAggregate().isEMPTY() && it.derivedAsType().isEmpty() }.groupBy {
        it.findParentMust(ModuleIB::class.java)
    }
    val moduleItems = findDownByType(DataTypeIB::class.java).filter { it.derivedAsType().isEmpty() }.groupBy {
        it.findParentMust(ModuleIB::class.java)
    }
    return """openapi: "3.0.0"
info:
  title: ${fullName()}${doc().isNotEMPTY().then {
        """
  description: ${doc().render()}"""
    }}
  version: "1.0.0"
paths:${moduleAggregates.joinSurroundIfNotEmptyToString("") { module, item ->
        if (item.findDownByType(FindByIB::class.java).isNotEmpty()) {
            """
  /${c.n(module, derived).toHyphenLowerCase()}${item.toSwaggerPath(c, derived)}:${
            item.toSwaggerGet(c, derived, api)}${
            item.toSwaggerPost(c, derived, api)}${
            item.toSwaggerPut(c, derived, api)}${
            item.toSwaggerDelete(c, derived, api)}"""
        } else ""
    }}
components:
  schemas:${moduleItems.joinSurroundIfNotEmptyToString("") { module, item ->
        if (item is EnumTypeIB<*>) item.toSwaggerEnum(c, derived) else item.toSwaggerDefinition(c, derived)
    }}"""
}

/*
responses:
  parameters:
  examples:
  requestBodies:
  headers:
  links:
  callbacks:


  securitySchemes:
 */
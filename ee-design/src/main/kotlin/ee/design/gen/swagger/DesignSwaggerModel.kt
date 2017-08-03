package ee.design.gen.swagger

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toHyphenLowerCase
import ee.design.CompI
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.swagger.toSwaggerDefinition
import ee.lang.gen.swagger.toSwaggerPath


fun <T : CompI> T.toSwagger(c: GenerationContext,
                            derived: String = LangDerivedKind.IMPL,
                            api: String = LangDerivedKind.API): String {
    val moduleAggregates = findDownByType(EntityI::class.java).filter { !it.virtual() && it.belongsToAggregate().isEMPTY() && it.derivedAsType().isEmpty() }.groupBy {
        it.findParentMust(ModuleI::class.java)
    }
    val moduleItems = findDownByType(EntityI::class.java).filter { it.derivedAsType().isEmpty() }.groupBy {
        it.findParentMust(ModuleI::class.java)
    }
    return """swagger: '2.0'
info:
  title: ${fullName()}${doc().isNotEMPTY().then{"""
  description: ${doc().render()}"""}}
  version: "1.0.0"
schemes:
  - http
  - https
produces:
  - application/json
paths:${moduleAggregates.joinSurroundIfNotEmptyToString("") { module, item ->
        """
  /${c.n(module, derived).toHyphenLowerCase()}${item.toSwaggerPath(c, derived)}:"""
    }}
parameters:
responses:
definitions:${moduleItems.joinSurroundIfNotEmptyToString("") { module, item ->
        item.toSwaggerDefinition(c, derived)
    }}"""
}
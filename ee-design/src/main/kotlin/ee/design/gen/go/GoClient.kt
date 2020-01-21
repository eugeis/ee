package ee.design.gen.go

import ee.common.ext.toPlural
import ee.design.DesignDerivedKind
import ee.design.EntityI
import ee.lang.*
import ee.lang.gen.go.g


fun <T : OperationI<*>> T.toGoHttpClientReadFileJsonBody(c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API): String {
    return """
    jsonBytes, _ := ${c.n(g.io.ioutil.ReadFile)}(fileJSON)

	err = ${c.n(g.encoding.json.Unmarshal)}(jsonBytes, &ret)"""
}


fun <T : OperationI<*>> T.toGoHttpClientImportJsonBody(c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API): String {

    val entity = findParentMust(EntityI::class.java)
    return """
    var items []*${c.n(entity, derived)}
	if items, err = o.ReadFileJSON(fileJSON); err != nil {
		return
	}

	err = o.Create(items)"""
}

fun <T : OperationI<*>> T.toGoHttpClientCreateBody(c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API): String {
    return """
    for _, item := range items {
		if err = ${c.n(g.gee.net.PostById)}(item, item.Id, o.Url, o.Client); err != nil {
            return
        }
	}"""
}

fun <T : ConstructorI<*>> T.toGoHttpModuleClientBeforeBody(c: GenerationContext,
    derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API): String {
    val item = findParentMust(StructureUnitI::class.java)
    return """
    url = url + "/" + "${item.name().decapitalize()}""""
}

fun <T : ConstructorI<*>> T.toGoHttpClientBeforeBody(c: GenerationContext, derived: String = DesignDerivedKind.IMPL,
    api: String = DesignDerivedKind.API): String {
    val item = findParentMust(EntityI::class.java)
    return """
    url = url + "/" + "${item.name().toPlural().decapitalize()}""""
}

fun <T : ConstructorI<*>> T.toGoHttpModuleCliBeforeBody(c: GenerationContext,
    derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API): String {
    val item = findParentMust(StructureUnitI::class.java)
    return """
        """
}
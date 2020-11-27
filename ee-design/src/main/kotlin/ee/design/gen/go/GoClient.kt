package ee.design.gen.go

import ee.common.ext.toPlural
import ee.design.DesignDerivedKind
import ee.design.EntityI
import ee.lang.*
import ee.lang.gen.go.g


fun <T : OperationI<*>> T.toGoHttpClientReadFileJsonBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API): String {

    return """
    jsonBytes, _ := ${c.n(g.io.ioutil.ReadFile)}(fileJSON)

	err = ${c.n(g.encoding.json.Unmarshal)}(jsonBytes, &ret)"""
}


fun <T : OperationI<*>> T.toGoHttpClientImportJsonBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API): String {

    val entity = findParentMust(EntityI::class.java)
    return """
    var items []*${c.n(entity, derived)}
	if items, err = o.ReadFileJSON(fileJSON); err != nil {
		return
	}

	err = o.CreateItems(items)"""
}

fun <T : OperationI<*>> T.toGoHttpClientCreateItemsBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API): String {

    return """
    for _, item := range items {
		if err = ${c.n(g.gee.net.PostById)}(item, item.Id, o.UrlIdBased, o.Client); err != nil {
            return
        }
	}"""
}

fun <T : OperationI<*>> T.toGoHttpClientDeleteItemsBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API): String {

    return """
    for _, item := range items {
		if err = ${c.n(g.gee.net.DeleteById)}(item.Id, o.UrlIdBased, o.Client); err != nil {
            return
        }
	}"""
}

fun <T : OperationI<*>> T.toGoHttpClientFindAllBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API): String {

    return """
    err = ${c.n(g.gee.net.GetItems)}(&ret, o.Url, o.Client)"""
}

fun <T : OperationI<*>> T.toGoHttpClientDeleteByIdBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API): String {

    return """
    err = ${c.n(g.gee.net.DeleteById)}(itemId, o.UrlIdBased, o.Client)"""
}

fun <T : ConstructorI<*>> T.toGoHttpModuleClientBeforeBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API): String {

    val item = findParentMust(StructureUnitI::class.java)
    return """
    url = url + "/" + "${item.name().decapitalize()}""""
}

fun <T : ConstructorI<*>> T.toGoHttpClientBeforeBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API): String {

    val item = findParentMust(EntityI::class.java)
    return """
    urlIdBased := url + "/" + "${item.name().decapitalize()}"
    url = url + "/" + "${item.name().toPlural().decapitalize()}""""
}

fun <T : ConstructorI<*>> T.toGoHttpModuleCliBeforeBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API): String {

    val item = findParentMust(StructureUnitI::class.java)
    return """
    client := NewClient(url, httpClient)"""
}
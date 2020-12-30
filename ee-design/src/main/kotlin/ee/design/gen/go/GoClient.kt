package ee.design.gen.go

import ee.common.ext.toPlural
import ee.design.DesignDerivedKind
import ee.design.EntityI
import ee.lang.*
import ee.lang.gen.go.g


fun <T : OperationI<*>> T.toGoHttpClientReadFileJsonBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    return """
    jsonBytes, _ := ${c.n(g.io.ioutil.ReadFile)}(fileJSON)

	err = ${c.n(g.encoding.json.Unmarshal)}(jsonBytes, &ret)"""
}


fun <T : OperationI<*>> T.toGoHttpClientImportJsonBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    return """
    var items []*${c.n(entity, derived)}
	if items, err = o.ReadFileJSON(fileJSON); err != nil {
		return
	}

	err = o.CreateItems(items)"""
}

fun <T : OperationI<*>> T.toGoHttpClientExportJsonBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    return """
    /*
    var items []*${c.n(entity, derived)}
	if items, err = o.FindAll(); err == nil {
    }
    */"""
}

fun <T : OperationI<*>> T.toGoHttpClientCreateBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    return """
    err = ${c.n(g.gee.net.PostById)}(item, item.Id, o.UrlIdBased, o.Client)"""
}

fun <T : OperationI<*>> T.toGoHttpClientCreateItemsBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    return """
    for _, item := range items {
		if err = o.Create(item); err != nil {
            return
        }
	}"""
}

fun <T : OperationI<*>> T.toGoHttpClientDeleteByIdsBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    return """
    for _, itemId := range itemIds {
		if err = ${c.n(g.gee.net.DeleteById)}(itemId, o.UrlIdBased, o.Client); err != nil {
            return
        }
	}"""
}

fun <T : OperationI<*>> T.toGoHttpClientFindAllBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    return """
    err = ${c.n(g.gee.net.GetItems)}(&ret, o.Url, o.Client)"""
}

fun <T : OperationI<*>> T.toGoHttpClientDeleteByIdBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    return """
    err = ${c.n(g.gee.net.DeleteById)}(itemId, o.UrlIdBased, o.Client)"""
}

fun <T : ConstructorI<*>> T.toGoHttpModuleClientBeforeBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val item = findParentMust(StructureUnitI::class.java)
    return """
    url = url + "/" + "${item.name().decapitalize()}""""
}

fun <T : ConstructorI<*>> T.toGoHttpClientBeforeBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val item = findParentMust(EntityI::class.java)
    return """
    urlIdBased := url + "/" + "${item.name().decapitalize()}"
    url = url + "/" + "${item.name().toPlural().decapitalize()}""""
}

fun <T : ConstructorI<*>> T.toGoCliBeforeBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    return """
    client := NewClient(url, httpClient)"""
}

fun <T : OperationI<*>> T.toGoCliBuildCommands(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    return """
    ret = []cli.Command{
    	o.BuildCommandImportJSON(),o.BuildCommandExportJSON(),o.BuildCommandDeleteById(),o.BuildCommandDeleteByIds(),
	}        
    """
}

fun <T : OperationI<*>> T.toGoCliImportJsonBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    return """
    """
}

fun <T : OperationI<*>> T.toGoCliExportJsonBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    return """
    """
}

fun <T : OperationI<*>> T.toGoCliDeleteByIdsBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    val propId = entity.propIdOrAdd()

    return """
	ret = cli.Command{
		Name:  "deleteBy${propId.name().toPlural().capitalize()}",
		Usage: "delete ${entity.name()} by ${propId.name().toPlural()}",
		Flags: []cli.Flag{&cli.StringFlag{
			Name:     "${propId.name().toPlural()}",
			Usage:    "${propId.name().toPlural()} of the ${
        entity.name().toPlural()
    } to delete, separated by semicolon",
			Required: true,
		}},
		Action: func(c *cli.Context) (err error) {
            var ${propId.name()} ${c.n(g.google.uuid.UUID, api)}			
            var ${propId.name().toPlural()} []${c.n(g.google.uuid.UUID, api)}
			for _, idString := range ${c.n(g.strings.Split, api)}(c.String("${propId.name().toPlural()}"),",") {
				if id, err = uuid.Parse(idString); err != nil {
					return
				}
				${propId.name().toPlural()} = append(${propId.name().toPlural()}, ${propId.name()})
			}
            err = o.Client.DeleteBy${propId.name().toPlural().capitalize()}(${propId.name().toPlural()})
            return
		},
	}"""
}

fun <T : OperationI<*>> T.toGoCliDeleteByIdBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    val propId = entity.propIdOrAdd()

    return """
	ret = cli.Command{
		Name:  "deleteBy${propId.name().capitalize()}",
		Usage: "delete ${entity.name()} by ${propId.name()}",
		Flags: []cli.Flag{&cli.StringFlag{
			Name:     "${propId.name()}",
			Usage:    "${propId.name()} of the ${entity.name()} to delete",
			Required: true,
		}},
		Action: func(c *cli.Context) (err error) {
			var ${propId.name()} ${c.n(g.google.uuid.UUID, api)}
			if ${propId.name()}, err = uuid.Parse(c.String("${propId.name()}")); err == nil {
				err = o.Client.DeleteBy${propId.name().capitalize()}(&${propId.name()})	
			}
			return
		},
	}"""
}
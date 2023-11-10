package ee.design.gen.go

import ee.common.ext.toPlural
import ee.design.DesignDerivedKind
import ee.design.EntityI
import ee.lang.*
import ee.lang.gen.go.g
import ee.lang.gen.go.toGo
import java.util.*


fun <T : OperationI<*>> T.toGoHttpClientReadFileJsonBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    return """
    jsonBytes, _ := ${c.n(g.os.ReadFile)}(fileJSON)

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
    url = url + "/" + "${item.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}""""
}

fun <T : ConstructorI<*>> T.toGoHttpClientBeforeBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val item = findParentMust(EntityI::class.java)
    return """
    urlIdBased := url + "/" + "${item.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}"
    url = url + "/" + "${item.name().toPlural().replaceFirstChar { it.lowercase(Locale.getDefault()) }}""""
}

fun <T : ConstructorI<*>> T.toGoCobraBeforeBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    return """
    client := NewClient(url, httpClient)"""
}

fun <T : OperationI<*>> T.toGoCobraBuildCommands(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    return """
    ret = []*cobra.Command{
    	o.BuildCommandImportJSON(), o.BuildCommandExportJSON(), o.BuildCommandDeleteById(), o.BuildCommandDeleteByIds(),
	}        
    """
}

fun <T : OperationI<*>> T.toGoCobraImportJsonBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    return """
    """
}

fun <T : OperationI<*>> T.toGoCobraExportJsonBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    return """
    """
}

fun <T : OperationI<*>> T.toGoCobraDeleteByIdsBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    val propId = entity.propIdOrAdd()

    return """
	ret = &cobra.Command{
		Short:  "deleteBy${propId.name().toPlural()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}",
		Use: "delete ${entity.name()} by ${propId.name().toPlural()}",
		RunE: func(cmd *cobra.Command, args []string) (err error) {
            var ${propId.name()} ${c.n(g.google.uuid.UUID, api)}			
            var ${propId.name().toPlural()} []${c.n(g.google.uuid.UUID, api)}
			for _, idString := range ${c.n(g.strings.Split, api)}(o.${propId.name().toPlural().toGo()},",") {
				if id, err = uuid.Parse(idString); err != nil {
					return
				}
				${propId.name().toPlural()} = append(${propId.name().toPlural()}, ${propId.name()})
			}
            err = o.Client.DeleteBy${propId.name().toPlural()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(${propId.name().toPlural()})
            return
		},
	}
    ret.PersistentFlags().StringVar(&o.${propId.name().toPlural().toGo()}, "${propId.name().toPlural()}", "", "${propId.name().toPlural()} of the ${entity.name().toPlural()} to delete, separated by semicolon")"""
}

fun <T : OperationI<*>> T.toGoCobraDeleteByIdBody(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {

    val entity = findParentMust(EntityI::class.java)
    val propId = entity.propIdOrAdd()

    return """
	ret = &cobra.Command{
		Short:  "deleteBy${propId.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}",
		Use: "delete ${entity.name()} by ${propId.name()}",
		RunE: func(cmd *cobra.Command, args []string) (err error) {
			var ${propId.name()} ${c.n(g.google.uuid.UUID, api)}
			if ${propId.name()}, err = uuid.Parse(o.${propId.toGo()}); err == nil {
				err = o.Client.DeleteBy${propId.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(&${propId.name()})	
			}
			return
		},
	}
    ret.PersistentFlags().StringVar(&o.${propId.toGo()}, "${propId.name()}", "", "${propId.name().toPlural()} of the ${entity.name().toPlural()} to delete")"""
}
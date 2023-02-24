import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.*

fun <T : ModuleI<*>> T.toAngularModuleTypeScript(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                 api: String = LangDerivedKind.API): String {
    return """
${this.toAngularGenerateComponentPart(c, "module", "view", hasProviders = true, hasClass = false)}
export class ${this.name()}ViewComponent {${"\n"}  
${this.toAngularModuleConstructor(c, tab)}
}"""
}

fun <T : ModuleI<*>> T.toAngularModuleService(modules: List<ModuleI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                       api: String = LangDerivedKind.API): String {
    return """export class ${this.name()}ViewService {

    pageElement = [${modules.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(", ") { """'${it.name()}'""" }}];

    tabElement = [${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString() {
        it.toAngularModuleTabElement()
    }}];

    pageName = '${this.name()}';
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewTypeScript(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                              api: String = LangDerivedKind.API): String {
    return """
${this.toAngularGenerateComponentPart(c, "entity", "view", hasProviders = true, hasClass = true)}
${isOpen().then("export ")}class ${c.n(this)}ViewComponent implements ${c.n(angular.core.OnInit)} {

${this.toTypeScriptEntityProp(c, tab)}
${this.toAngularConstructorDataService(c, tab)}
${this.toAngularViewOnInit(c, tab)}
}
"""
}

/*
${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
    when(it.type()) {
        is EntityI<*>, is ValuesI<*> -> it.type().toAngularImportEntityComponent(it.type().findParentNonInternal())
        else -> ""
    }
}}
 */
fun <T : CompilationUnitI<*>> T.toAngularEntityFormTypeScript(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                              api: String = LangDerivedKind.API): String {
    return """
${this.toAngularGenerateComponentPart(c, "entity", "form", hasProviders = false, hasClass = false)}
${isOpen().then("export ")}class ${c.n(this)}FormComponent implements ${c.n(angular.core.OnInit)} {

${this.toTypeScriptFormProp(c, tab)}
    constructor(public ${this.name().toLowerCase()}DataService: ${this.name()}${c.n(service.own.DataService, "-${this.name()}").substringBeforeLast('-')}, 
${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
    when(it.type()) {
        is EntityI<*>, is ValuesI<*> -> it.type().toAngularPropOnConstructor(c)
        else -> ""
    }
}}) {}
${this.toAngularFormOnInit(c, tab)}
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListTypeScript(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                              api: String = LangDerivedKind.API): String {
    return """
${this.toAngularGenerateComponentPart(c, "entity", "list", hasProviders = true, hasClass = true)}
${isOpen().then("export ")}class ${c.n(this)}ListComponent implements ${c.n(angular.core.OnInit)}, ${c.n(angular.core.AfterViewInit)} {

${this.toTypeScriptEntityPropInit(c, tab)}
    tableHeader: Array<String> = [];
    
    @${c.n(angular.core.ViewChild)}(${c.n(angular.material.sort.MatSort)}) sort: ${c.n(angular.material.sort.MatSort)};

${this.toAngularConstructorDataService(c, tab)}
    ng${c.n(angular.core.AfterViewInit)}() {
        this.${this.name().toLowerCase()}DataService.dataSources.sort = this.sort;
    }
${this.toAngularListOnInit(c, tab)}

    generateTableHeader() {
        return ['Box', 'Actions', ${props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(", ") {
        it.toAngularGenerateTableHeader(c)
    }}];
    }
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityDataService(
    entities: List<EntityI<*>>, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
    api: String = LangDerivedKind.API): String {
    return """

@${c.n(angular.core.Injectable)}({ providedIn: 'root' })
${isOpen().then("export ")}class ${c.n(this)}DataService extends ${c.n(service.template.DataService)}<${c.n(this)}> {
    itemName = '${c.n(this).toLowerCase()}';

    pageName = '${c.n(this)}Component';
    
    isHidden = true;
    
    entityElements = [${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> """'${it.name().toCamelCase()}',"""
            else -> {
                it.type().props().filter {childElement -> !childElement.isEMPTY() }.joinSurroundIfNotEmptyToString("") {childElement -> 
                    when(childElement.type()) {
                        is EntityI<*>, is ValuesI<*> -> """'${childElement.name().toCamelCase()}',"""
                        else -> ""
                    }
                }
            }
        }
    }}];   
    
    ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.type().toAngularControlService(c)
            else -> ""
        }
    }}
    
    ${this.props().filter { it.type().name().toLowerCase() == "blob" }.joinSurroundIfNotEmptyToString { 
        """
    selectedFiles?: FileList;
    
    previews: string[] = [];
    """
    }}

    getFirst() {
        return new ${c.n(this)}();
    }
    
    toggleHidden() {
        this.isHidden = !this.isHidden;
    }
    
    ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.type().toAngularControlServiceFunctions(c, it.type().props().first { element -> element.type().name() == "String" })
            else -> ""
        }
    }}
    
    initObservable() {
    ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.type().toAngularInitObservable(c, it.type().props().first { element -> element.type().name() == "String" })
            else -> ""
        }
    }}
    }
    
    ${this.props().filter { it.type().name().toLowerCase() == "blob" }.joinSurroundIfNotEmptyToString {
        """
    selectFiles(event: any): void {
        this.selectedFiles = event.target.files;

        this.previews = [];
        if (this.selectedFiles) {
            const fileReader = new FileReader();

            fileReader.onload = (e: any) => {
                this.previews.push(e.target.result);
            };

            fileReader.readAsDataURL(this.selectedFiles[0]);
        }
    }
    """ 
    }}

    editElement(element: ${c.n(this)}) {
        this.items = this.retrieveItemsFromCache();
        const editItem = JSON.parse(localStorage.getItem('edit'));
        const editItemEntity = localStorage.getItem('edit-entity');
        const oldId = this.itemName + JSON.stringify(editItem);
        const newId = this.itemName + JSON.stringify(element);

        this.addItemToTableArray(element, newId);
        this.items.delete(oldId);
        this.saveItemToCache(this.items);
        this.editInheritedEntity(editItemEntity, element)
    }
    
    editInheritedEntity(itemName: string, newElement: ${c.n(this)}) {
        const editItem = JSON.parse(localStorage.getItem('edit'));
        if (JSON.stringify(newElement) !== JSON.stringify(editItem)) {
        ${entities.filter { entity -> entity.props().any {property ->
            (property.type() is BasicI<*> || property.type() is EntityI<*>) && ( entity.props().any {
                childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) } ||
                property.type().props().any {
                        childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) }
                )
            } 
        }.joinSurroundIfNotEmptyToString("") {
        
        """
            const inheritedElement${it.name().capitalize()}: Map<string, ${c.n(it)}> = new Map(JSON.parse(localStorage.getItem(itemName)));
            inheritedElement${it.name().capitalize()}.forEach((value, key) => {
                if (key.includes(JSON.stringify(editItem))) {
                    value${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)
                ) }.joinSurroundIfNotEmptyToString("") { elementName -> """.${elementName.name()}""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*>) && ( 
                property.type().props().any { childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) }
                ) }.joinSurroundIfNotEmptyToString("") { elementName -> """.${elementName.name()}.${this.name().toCamelCase()}""" }} = newElement;
                    const newId = itemName + JSON.stringify(value);
                    inheritedElement${it.name().capitalize()}.set(newId, value);
                    inheritedElement${it.name().capitalize()}.delete(key);
                }
            });
            localStorage.${it.name().toLowerCase()} = JSON.stringify(Array.from(inheritedElement${it.name().capitalize()}.entries()));
            localStorage.setItem('${it.name().toLowerCase()}', localStorage.${it.name().toLowerCase()});
        """
    }}
        }
    }
}

declare global {
    interface Window {
        ${c.n(this).toLowerCase()}DataService: ${c.n(this)}DataService;
    }
}
window.${c.n(this).toLowerCase()}DataService = new ${c.n(this)}DataService();
"""
}

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.*

val tabs5 = tab + tab + tab + tab + tab

fun <T : ModuleI<*>> T.toAngularModuleTypeScript(c: GenerationContext, ViewComponent: String = AngularDerivedType.ViewComponent): String {
    return """
${this.toAngularGenerateComponentPart(c, "module", "module", "view", hasProviders = true, hasClass = false)}
export class ${this.name()}${ViewComponent} {${"\n"}  
    constructor(public ${c.n(this, AngularDerivedType.ViewService).decapitalize()}: ${c.n(this, AngularDerivedType.ViewService)}) {}$nL
}"""
}

fun <T : ModuleI<*>> T.toAngularModuleService(modules: List<ModuleI<*>>, c: GenerationContext, ViewService: String = AngularDerivedType.ViewService): String {
    return """export class ${this.name()}${ViewService} {

    pageElement = [${modules.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(", ") { """'${it.name()}'""" }}];

    tabElement = [${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString() {
        it.toAngularModuleTabElement()
    }}];

    pageName = '${this.name()}';
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewTypeScript(c: GenerationContext, ViewComponent: String = AngularDerivedType.ViewComponent): String {
    return """
${this.toAngularGenerateComponentPart(c, "entity-${this.parent().name().toLowerCase()}", "entity", "view", hasProviders = true, hasClass = true)}
${isOpen().then("export ")}class ${this.name().capitalize()}${ViewComponent} implements ${c.n(angular.core.OnInit)} {

${this.toTypeScriptEntityProp(c, tab)}
${this.toAngularConstructorDataService(c, tab)}
${this.toAngularViewOnInit(c, tab)}
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityFormTypeScript(c: GenerationContext, FormComponent: String = AngularDerivedType.FormComponent): String {
    return """
${this.toAngularGenerateComponentPart(c, "entity-${this.parent().name().toLowerCase()}", "entity", "form", hasProviders = false, hasClass = false)}
${isOpen().then("export ")}class ${this.name().capitalize()}${FormComponent} implements ${c.n(angular.core.OnInit)} {

${this.toTypeScriptFormProp(c, tab)}
    constructor(public ${c.n(this, AngularDerivedType.DataService).decapitalize()}: ${c.n(this, AngularDerivedType.DataService)}, 
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

fun <T : CompilationUnitI<*>> T.toAngularEntityListTypeScript(c: GenerationContext, ListComponent: String = AngularDerivedType.ListComponent): String {
    return """
${this.toAngularGenerateComponentPart(c, "entity-${this.parent().name().toLowerCase()}","entity", "list", hasProviders = true, hasClass = true)}
${isOpen().then("export ")}class ${this.name().capitalize()}${ListComponent} implements ${c.n(angular.core.OnInit)}, ${c.n(angular.core.AfterViewInit)} {

${this.toTypeScriptEntityPropInit(c, tab)}
    tableHeader: Array<String> = [];
    
    @${c.n(angular.core.ViewChild)}(${c.n(angular.material.sort.MatSort)}) sort: ${c.n(angular.material.sort.MatSort)};

${this.toAngularConstructorDataService(c, tab)}
    ng${c.n(angular.core.AfterViewInit)}() {
        this.${c.n(this, AngularDerivedType.DataService).decapitalize()}.dataSources.sort = this.sort;
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

fun <T : AttributeI<*>> T.toAngularGenerateTableHeader(c: GenerationContext, parentName: String = ""): String {
    return when (this.type()) {
        is EntityI<*>, is ValuesI<*> -> """'${this.name().toLowerCase()}-entity'"""
        is BasicI<*> -> this.type().props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(", ") {
            it.toAngularGenerateTableHeader(c, this.name())
        }
        is EnumTypeI<*> -> """'${if(parentName.isEmpty()) "" else "$parentName-"}${this.name().toCamelCase()}'"""
        else -> """'${if(parentName.isEmpty()) "" else "$parentName-"}${this.name().toCamelCase()}'"""
    }
}

fun <T : CompilationUnitI<*>> T.toAngularEntityDataService(
    entities: List<EntityI<*>>, c: GenerationContext, DataService: String = AngularDerivedType.DataService): String {
    return """

@${c.n(angular.core.Injectable)}({ providedIn: 'root' })
${isOpen().then("export ")}class ${this.name().capitalize()}${DataService} extends ${c.n(service.template.DataService)}<${c.n(this, AngularDerivedType.ApiBase)}> {
    itemName = '${c.n(this, AngularDerivedType.ApiBase).toLowerCase()}';

    pageName = '${c.n(this, AngularDerivedType.Component)}';
    
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
        return new ${c.n(this, AngularDerivedType.ApiBase)}();
    }
    
    toggleHidden() {
        this.isHidden = !this.isHidden;
    }
    
    ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.type().toAngularControlServiceFunctions(c, it.type().props())
            else -> ""
        }
    }}
    
    initObservable() {
    ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.type().toAngularInitObservable(c, it.type().props())
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

    editElement(element: ${c.n(this, AngularDerivedType.ApiBase)}) {
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
    
    editInheritedEntity(itemName: string, newElement: ${c.n(this, AngularDerivedType.ApiBase)}) {
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
            const inheritedElement${it.name().capitalize()}: Map<string, ${c.n(it, AngularDerivedType.ApiBase)}> = new Map(JSON.parse(localStorage.getItem(itemName)));
            inheritedElement${it.name().capitalize()}.forEach((value, key) => {
                if (key.includes(JSON.stringify(editItem))) {
                    ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)
                ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """value.${elementName.name()} = newElement;""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*>) && ( 
                property.type().props().any { childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) }
                ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """value.${elementName.name()}.${this.name().toCamelCase()} = newElement;""" }}
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
        ${this.name().decapitalize()}${DataService}: ${this.name()}${DataService};
    }
}
window.${this.name().decapitalize()}${DataService} = new ${this.name()}${DataService}();
"""
}

fun <T : ItemI<*>> T.toAngularGenerateEnumElementBasic(c: GenerationContext, indent: String): String {
    return """${indent}${c.n(this, AngularDerivedType.Enum).decapitalize()} = this.loadEnumElement(${c.n(this, AngularDerivedType.ApiBase).capitalize()});"""
}

fun <T : CompilationUnitI<*>> T.toAngularBasicTSComponent(c: GenerationContext, BasicComponent: String = AngularDerivedType.BasicComponent): String {
    return """
${this.toAngularGenerateComponentPart(c, "basic-${this.parent().name().toLowerCase()}","basic", "", hasProviders = false, hasClass = false)}
${isOpen().then("export ")}class ${this.name().capitalize()}${BasicComponent} implements ${c.n(angular.core.OnInit)} {

    @${c.n(angular.core.Input)}() ${c.n(this, AngularDerivedType.ApiBase).toLowerCase()}: ${c.n(this, AngularDerivedType.ApiBase)};
    @${c.n(angular.core.Input)}() parentName: String;
${props().filter { it.type() is EnumTypeI<*> }.joinSurroundIfNotEmptyToString("") {
        it.type().toAngularGenerateEnumElementBasic(c, tab)
    }}

${if (props().any { it.type() is EntityI<*> || it.type() is ValuesI<*> }) {
        """${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
            when(it.type()) {
                is EntityI<*>, is ValuesI<*> -> it.type().toAngularControlService(c)
                else -> ""
            }
        }}        
    constructor(${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
            when(it.type()) {
                is EntityI<*>, is ValuesI<*> -> it.type().toAngularPropOnConstructor(c)
                else -> "" }
        }.trim()
        }) {}
"""
    }else{""}}

${if (props().any { it.type() is EnumTypeI<*> }) {
        """
    loadEnumElement(enumElement: any) {
        let tempArray = [];
        Object.keys(enumElement).${c.n(rxjs.operators.map)}((element, index) => {
            tempArray.push(enumElement[index]);
        })
        tempArray = tempArray.filter((item) => item);
        return tempArray;
    }"""
    } else {""}}
    ngOnInit() {
        if (this.${c.n(this, AngularDerivedType.ApiBase).toLowerCase()} === undefined) {
            this.${c.n(this, AngularDerivedType.ApiBase).toLowerCase()} = new ${this.name().capitalize()}();
        }
        ${props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toTypeScriptInitEmptyProps(c)
    }.trim()}
    
    ${if (props().any { it.type() is EntityI<*> || it.type() is ValuesI<*> }) {
        """
        ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
            when(it.type()) {
                is EntityI<*>, is ValuesI<*> -> it.toAngularInitOptionBasic(c, it.type().name())
                else -> ""
            }
        }}
    
        this.initObservable();"""
    } else {""}}
    }
    
${if (props().any { it.type() is EntityI<*> || it.type() is ValuesI<*> }) {
        """    
        ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
            when(it.type()) {
                is EntityI<*>, is ValuesI<*> -> it.type().toAngularControlServiceFunctions(c, it.type().props())
                else -> ""
            }
        }}
    
    initObservable() {
        ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
            when(it.type()) {
                is EntityI<*>, is ValuesI<*> -> it.type().toAngularInitObservable(c, it.type().props())
                else -> ""
            }
        }}
    }"""
    } else {""}}
}
"""
}

fun <T : ItemI<*>> T.toAngularInitOptionBasic(c: GenerationContext, elementType: String): String {
    return """this.option${elementType.capitalize()} = this.${elementType.toLowerCase()}DataService.changeMapToArray(this.${elementType.toLowerCase()}DataService.retrieveItemsFromCache()); $nL"""
}

fun <T : CompilationUnitI<*>> T.toAngularEnumTSComponent(c: GenerationContext, EnumComponent: String = AngularDerivedType.EnumComponent,
                                                         DataService: String = AngularDerivedType.DataService): String {
    return """
${this.toAngularGenerateComponentPart(c, "enum-${this.parent().name().toLowerCase()}","enum", "", hasProviders = false, hasClass = false)}

export class ${this.name().capitalize()}${EnumComponent} implements ${c.n(angular.core.OnInit)} {

    @${c.n(angular.core.Input)}() ${c.n(this, AngularDerivedType.ApiBase).toLowerCase()}: ${c.n(this, AngularDerivedType.ApiBase).capitalize()};
    @${c.n(angular.core.Output)}() ${c.n(this, AngularDerivedType.ApiBase).toLowerCase()}Change = new ${c.n(angular.core.EventEmitter)}<${c.n(this, AngularDerivedType.ApiBase).capitalize()}>();
    
    enumElements: Array<string>;
    
    constructor(private ${DataService.decapitalize()}: ${c.n(service.template.DataService)}<any>) { }
    
    ngOnInit(): void {
        this.enumElements = this.${DataService.decapitalize()}.loadEnumElement(${c.n(this, AngularDerivedType.ApiBase)});
    }
    
    changeValue(event: ${c.n(angular.material.select.MatSelectChange)}) {
        this.${c.n(this, AngularDerivedType.ApiBase).toLowerCase()} = event.value;
        this.${c.n(this, AngularDerivedType.ApiBase).toLowerCase()}Change.emit(event.value);
    }

}
"""
}

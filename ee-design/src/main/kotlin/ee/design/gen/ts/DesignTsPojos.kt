import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.*
import java.util.*

val tabs5 = tab + tab + tab + tab + tab

fun <T : ModuleI<*>> T.toAngularModuleTypeScript(c: GenerationContext, Model: String = AngularDerivedType.Module,ViewComponent: String = AngularDerivedType.ViewComponent): String {
    return """
${this.toAngularGenerateComponentPart(c, "module", "module", "view", hasProviders = true, hasClass = false)}
export class ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${Model}${ViewComponent} {${"\n"}  
    constructor(public ${c.n(this, AngularDerivedType.ViewService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}: ${c.n(this, AngularDerivedType.ViewService)}) {}$nL
}"""
}

fun <T : ModuleI<*>> T.toAngularModuleService(modules: List<ModuleI<*>>, c: GenerationContext, ViewService: String = AngularDerivedType.ViewService): String {
    return """export class ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${ViewService} {

    pageElement = [${modules.filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(", ") { """'${it.name()}'""" }}];

    tabElement = [${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString {
        it.toAngularModuleTabElement()
    }}, ${this.values().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString {
        it.toAngularModuleTabElement()
    }}];

    pageName = '${this.name()}';
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewTypeScript(c: GenerationContext, Model: String = AngularDerivedType.Entity, ViewComponent: String = AngularDerivedType.ViewComponent): String {
    return """
${this.toAngularGenerateComponentPart(c, "entity-${this.parent().name().lowercase(Locale.getDefault())}", "entity", "view", hasProviders = true, hasClass = true)}
${isOpen().then("export ")}class ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${Model}${ViewComponent} implements ${c.n(angular.core.OnInit)} {

${this.toTypeScriptEntityProp(c, tab)}
${this.toAngularConstructorDataService(c, tab)}
${this.toAngularViewOnInit(c, tab)}
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityFormTypeScript(c: GenerationContext, Model: String = AngularDerivedType.Entity, FormComponent: String = AngularDerivedType.FormComponent): String {
    return """
${this.toAngularGenerateComponentPart(c, "entity-${this.parent().name().lowercase(Locale.getDefault())}", "entity", "form", hasProviders = false, hasClass = false)}
${isOpen().then("export ")}class ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${Model}${FormComponent} implements ${c.n(angular.core.OnInit)} {

${this.toTypeScriptFormProp(c, tab)}
    constructor(public ${c.n(this, AngularDerivedType.DataService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}: ${c.n(this, AngularDerivedType.DataService)}, 
${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.distinctBy { it.type().name() }.joinSurroundIfNotEmptyToString("") {
    when(it.type()) {
        is EntityI<*>, is ValuesI<*> -> it.type().toAngularPropOnConstructor(c)
        else -> ""
    }
}}) {}
${this.toAngularFormOnInit(c, tab)}
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListTypeScript(c: GenerationContext, Model: String = AngularDerivedType.Entity, ListComponent: String = AngularDerivedType.ListComponent): String {
    return """
${this.toAngularGenerateComponentPart(c, "entity-${this.parent().name().lowercase(Locale.getDefault())}", "entity", "list", hasProviders = true, hasClass = true)}
${isOpen().then("export ")}class ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${Model}${ListComponent} implements ${c.n(angular.core.OnInit)}, ${c.n(angular.core.AfterViewInit)} {

${this.toTypeScriptEntityPropInit(c, tab)}
    tableHeader: Array<String> = [];
    
    @${c.n(angular.core.ViewChild)}(${c.n(angular.material.sort.MatSort)}) sort: ${c.n(angular.material.sort.MatSort)};

${this.toAngularConstructorDataService(c, tab)}
    ng${c.n(angular.core.AfterViewInit)}() {
        this.${c.n(this, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.dataSources.sort = this.sort;
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
        is EntityI<*>, is ValuesI<*> -> """'${this.name().lowercase(Locale.getDefault())}-entity'"""
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
${isOpen().then("export ")}class ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${DataService} extends ${c.n(service.template.DataService)}<${c.n(this, AngularDerivedType.ApiBase)}> {
    itemName = '${c.n(this, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}';

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
    
    ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.distinctBy { it.type().name() }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.type().toAngularControlService(c)
            else -> ""
        }
    }}
    
    ${this.props().any { it.type().name().lowercase(Locale.getDefault()) == "blob" }.then { 
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
    
    ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.distinctBy { it.type().name() }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.type().toAngularControlServiceFunctions(c, it.type().props())
            else -> ""
        }
    }}
    
    initObservable() {
    ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.distinctBy { it.type().name() }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.type().toAngularInitObservable(c, it.type().props())
            else -> ""
        }
    }}
    }
    
    ${this.props().filter { it.type().name().lowercase(Locale.getDefault()) == "blob" }.joinSurroundIfNotEmptyToString {
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
            (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( entity.props().any {
                childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && !childProperty.type().name().equals("list", true) && childProperty.type().namespace().equals(this.namespace(), true) } ||
                property.type().props().any {
                        childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && !childProperty.type().name().equals("list", true) && childProperty.type().namespace().equals(this.namespace(), true) }
                )
            } 
        }.joinSurroundIfNotEmptyToString("") {
        
        """
            const inheritedElement${it.name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}: Map<string, ${c.n(it, AngularDerivedType.ApiBase)}> = new Map(JSON.parse(localStorage.getItem(itemName)));
            inheritedElement${it.name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}.forEach((value, key) => {
                if (key.includes(JSON.stringify(editItem))) {               
                    ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)
                ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """value.${elementName.name().toCamelCase()} = newElement;""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) }
                ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """value.${elementName.name().toCamelCase()}.${this.name().toCamelCase()} = newElement;""" }}
                    const newId = itemName + JSON.stringify(value);
                    inheritedElement${it.name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}.set(newId, value);
                    inheritedElement${it.name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}.delete(key);
                }
            });
            localStorage.${it.name().lowercase(Locale.getDefault())} = JSON.stringify(Array.from(inheritedElement${it.name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}.entries()));
            localStorage.setItem('${it.name().lowercase(Locale.getDefault())}', localStorage.${it.name().lowercase(Locale.getDefault())});
        """
    }}
        }
    }
}

declare global {
    interface Window {
        ${this.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${DataService}: ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${DataService};
    }
}
window.${this.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${DataService} = new ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${DataService}();
"""
}

fun <T : ItemI<*>> T.toAngularGenerateEnumElementBasic(c: GenerationContext, indent: String): String {
    return """${indent}${c.n(this, AngularDerivedType.Enum).replaceFirstChar { it.lowercase(Locale.getDefault()) }} = this.loadEnumElement(${c.n(this, AngularDerivedType.ApiBase)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }});"""
}

fun <T : CompilationUnitI<*>> T.toAngularBasicTSComponent(c: GenerationContext, BasicComponent: String = AngularDerivedType.BasicComponent): String {
    return """
${this.toAngularGenerateComponentPart(c, "basic-${this.parent().name().lowercase(Locale.getDefault())}", "basic", "", hasProviders = false, hasClass = false)}
${isOpen().then("export ")}class ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${BasicComponent} implements ${c.n(angular.core.OnInit)} {

    @${c.n(angular.core.Input)}() ${this.name().lowercase(Locale.getDefault())}: ${c.n(this, AngularDerivedType.ApiBase)};
    @${c.n(angular.core.Input)}() parentName: String;
${props().filter { it.type() is EnumTypeI<*> }.joinSurroundIfNotEmptyToString("") {
        it.type().toAngularGenerateEnumElementBasic(c, tab)
    }}

${if (props().any { it.type() is EntityI<*> || it.type() is ValuesI<*> }) {
        """${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.distinctBy { it.type().name() }.joinSurroundIfNotEmptyToString("") {
            when(it.type()) {
                is EntityI<*>, is ValuesI<*> -> it.type().toAngularControlService(c)
                else -> ""
            }
        }}        
    constructor(${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.distinctBy { it.type().name() }.joinSurroundIfNotEmptyToString("") {
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
        if (this.${this.name().lowercase(Locale.getDefault())} === undefined) {
            this.${this.name().lowercase(Locale.getDefault())} = new ${c.n(this, AngularDerivedType.ApiBase)}();
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
        ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.distinctBy { it.type().name() }.joinSurroundIfNotEmptyToString("") {
            when(it.type()) {
                is EntityI<*>, is ValuesI<*> -> it.type().toAngularControlServiceFunctions(c, it.type().props())
                else -> ""
            }
        }}
    
    initObservable() {
        ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.distinctBy { it.type().name() }.joinSurroundIfNotEmptyToString("") {
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
    return """this.option${elementType.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} = this.${elementType.lowercase(Locale.getDefault())}DataService.changeMapToArray(this.${elementType.lowercase(
        Locale.getDefault()
    )}DataService.retrieveItemsFromCache()); $nL"""
}

fun <T : CompilationUnitI<*>> T.toAngularEnumTSComponent(c: GenerationContext, EnumComponent: String = AngularDerivedType.EnumComponent,
                                                         DataService: String = AngularDerivedType.DataService): String {
    return """
${this.toAngularGenerateComponentPart(c, "enum-${this.parent().name().lowercase(Locale.getDefault())}", "enum", "", hasProviders = false, hasClass = false)}

export class ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${EnumComponent} implements ${c.n(angular.core.OnInit)} {

    @${c.n(angular.core.Input)}() ${this.name().lowercase(Locale.getDefault())}: ${c.n(this, AngularDerivedType.ApiBase)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }};
    @${c.n(angular.core.Output)}() ${this.name().lowercase(Locale.getDefault())}Change = new ${c.n(angular.core.EventEmitter)}<${c.n(this, AngularDerivedType.ApiBase)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}>();
    
    enumElements: Array<string>;
    
    constructor(private ${DataService.replaceFirstChar { it.lowercase(Locale.getDefault()) }}: ${c.n(service.template.DataService)}<any>) { }
    
    ngOnInit(): void {
        this.enumElements = this.${DataService.replaceFirstChar { it.lowercase(Locale.getDefault()) }}.loadEnumElement(${c.n(this, AngularDerivedType.ApiBase)});
    }
    
    changeValue(event: ${c.n(angular.material.select.MatSelectChange)}) {
        this.${this.name().lowercase(Locale.getDefault())} = event.value;
        this.${this.name().lowercase(Locale.getDefault())}Change.emit(event.value);
    }

}
"""
}

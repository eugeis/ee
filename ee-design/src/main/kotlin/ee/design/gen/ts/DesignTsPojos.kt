import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toCamelCase
import ee.design.CompI
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.*
import java.util.*

val tabs5 = tab + tab + tab + tab + tab
fun <T : CompI<*>> T.toAngularTranslateJson(): String {
    return """
{
    ${this.toAngularGenerateDefaultTranslate()}  
    ${this.modules().filter { !it.enums().isEmpty() }.flatMap { it.enums() }.flatMap { it.literals() }.distinctBy { it.name() }.joinSurroundIfNotEmptyToString("$nL$tab") {
            it.toAngularGenerateModuleEnumsTranslate()
    }}
    ${this.modules().filter { !it.isEMPTY() }.distinctBy { it.name() }.joinSurroundIfNotEmptyToString(nL) {
            it.toAngularGenerateModuleElementsTranslate()
    }}
    "table": {
        "action": "Action"
    }
}"""
}

fun <T : ModuleI<*>> T.toAngularModuleTypeScript(c: GenerationContext, Model: String = AngularDerivedType.Module,ViewComponent: String = AngularDerivedType.ViewComponent): String {
    return """
${this.toAngularGenerateComponentPart(c, "module", "module", "view", hasProviders = true, hasClass = false)}
export class ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${Model}${ViewComponent} implements ${c.n(angular.core.OnInit)} {${"\n"}  
    
    @${c.n(angular.core.Input)}() tabElement: Array<string>;
    
    @${c.n(angular.core.Input)}() componentName: string;
    
    constructor(public ${c.n(this, AngularDerivedType.ViewService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}: ${c.n(this, AngularDerivedType.ViewService)}) {}$nL
        
    ngOnInit(): void {
        if(this.tabElement !== undefined && this.tabElement.length > 0) {
            this.${this.name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}ViewService.tabElement = this.tabElement;
        }
        
        if(this.componentName !== undefined && this.componentName.length > 0) {
            this.${this.name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}ViewService.componentName = this.componentName
        }
    }    
}"""
}

fun <T : ModuleI<*>> T.toAngularModuleService(modules: List<ModuleI<*>>, c: GenerationContext, ViewService: String = AngularDerivedType.ViewService): String {
    return """export class ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${ViewService} {

    pageElement = [${modules.filter { !it.isEMPTY() && it.entities()
        .any { entity -> entity.isBase() && entity.belongsToAggregate().isNotEMPTY() }
    }.joinSurroundIfNotEmptyToString(", ") { """'${it.name()}'""" }}];

    tabElement = [${this.entities().filter { !it.isEMPTY() && it.belongsToAggregate().isEMPTY() }.joinSurroundIfNotEmptyToString(", ") {
        it.toAngularModuleTabElementEntity()
    }}];

    pageName = '${this.name()}';
    
    componentName = '';
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewTypeScript(c: GenerationContext, Model: String = AngularDerivedType.Entity, ViewComponent: String = AngularDerivedType.ViewComponent): String {
    return """
${this.toAngularGenerateComponentPart(c, "entity-${this.parent().name().lowercase(Locale.getDefault())}", "entity", "view", hasProviders = true, hasClass = true)}
${isOpen().then("export ")}class ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${Model}${ViewComponent} implements ${c.n(angular.core.OnInit)} {

${this.toTypeScriptEntityProp(c, tab)}
    tabElement: Array<string>;
    isSpecificView = false;
    decodedParams = {}
    
    protected readonly location = location;
    
${this.toAngularConstructorDataService(c, tab, true)}
${this.toAngularViewOnInit(c, tab)}
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityFormTypeScript(c: GenerationContext, Model: String = AngularDerivedType.Entity, FormComponent: String = AngularDerivedType.FormComponent, DataService: String = AngularDerivedType.DataService): String {
    return """
${this.toAngularGenerateComponentPart(c, "entity-${this.parent().name().lowercase(Locale.getDefault())}", "entity", "form", hasProviders = false, hasClass = false)}
${isOpen().then("export ")}class ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${Model}${FormComponent} implements ${c.n(angular.core.OnInit)} {

    @${c.n(angular.core.Input)}() isDisabled = false;
${this.toTypeScriptFormProp(c, tab)}
    protected readonly location = location;
    
${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "string") }.joinSurroundIfNotEmptyToString(tab) {
    when(it.type()) {
        is EntityI<*>, is ValuesI<*> -> it.type().toAngularSelectedIndices(c)
        else -> when(it.type().name().lowercase(Locale.getDefault())) {
            "list" -> when(it.type().generics().first().type()) {
                is EntityI<*>, is ValuesI<*> -> it.type().generics().first().type().toAngularSelectedMultipleIndices(c)
                else -> ""
            }
            else -> ""
        }
    }
}}
    form: ${c.n(angular.forms.FormGroup)};
    
    constructor(public ${c.n(this, AngularDerivedType.DataService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}: ${c.n(this, AngularDerivedType.DataService)}, 
${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "string") }.joinSurroundIfNotEmptyToString("") {
    when(it.type()) {
        is EntityI<*>, is ValuesI<*> -> it.type().toAngularPropOnConstructor(c)
        else -> when(it.type().name().lowercase(Locale.getDefault())) {
            "list" -> when(it.type().generics().first().type()) {
                is EntityI<*>, is ValuesI<*> -> it.type().generics().first().type().toAngularPropOnConstructor(c)
                else -> ""
            }
            else -> ""
        }
    }
}}) {}
${this.toAngularFormOnInit(c, tab)}

    loadIxOption() {
        ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "string") }.joinSurroundIfNotEmptyToString(tab + tab) {
            when(it.type()) {
                is EntityI<*>, is ValuesI<*>-> it.toAngularInitOption(c, it.type())
                else -> when(it.type().name().lowercase(Locale.getDefault())) {
                    "list" -> when(it.type().generics().first().type()) {
                        is EntityI<*>, is ValuesI<*> -> it.toAngularInitOptionMultiple(c, it.type().generics().first().type())
                        is EnumTypeI<*> -> "this.${c.n(this, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.option${c.n(it.type().generics().first().type(), AngularDerivedType.ApiBase).toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} = this.${c.n(this, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.loadEnumElement(${c.n(it.type().generics().first().type(), AngularDerivedType.ApiBase)
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }});"
                        else -> ""
                    }
                    else -> ""
                }
            }
        }}
    }
    
    ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "string") }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.type().toAngularFunctionRemove(c, it.name(), this)
            else -> when(it.type().name().lowercase(Locale.getDefault())) {
                "list" -> when(it.type().generics().first().type()) {
                    is EntityI<*>, is ValuesI<*> -> it.type().generics().first().type().toAngularFunctionRemoveMultiple(c, it.name(), this)
                    else -> ""
                }
                else -> ""
            }
        }
    }}
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListTypeScript(c: GenerationContext, Model: String = AngularDerivedType.Entity, ListComponent: String = AngularDerivedType.ListComponent, isAggregateView: Boolean = false, componentType: String = "list"): String {
    return """
${this.toAngularGenerateComponentPart(c, "entity-${this.parent().name().lowercase(Locale.getDefault())}", "entity", componentType, hasProviders = true, hasClass = true)}
${isOpen().then("export ")}class ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${Model}${ListComponent} implements ${c.n(angular.core.OnInit)} {

${this.toTypeScriptEntityPropInit(c, tab)}
    
    tabElement: Array<string>;
    isSpecificView = false;
    data: ${c.n(this, AngularDerivedType.ApiBase)}[] = [];
    decodedParams = {}
    
    categories = {
        ${props().filter { it.isNotEMPTY() }.joinSurroundIfNotEmptyToString("") {
            """
        ${it.name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}: {
            label: '${it.name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}',
            options: [],
        },"""
        }}
    };
    
${this.toAngularConstructorDataService(c, tab, true)}

${this.toAngularListOnInit(c, tab, isAggregateView)}

    filteredData(event: any) {
        this.${c.n(this, AngularDerivedType.ApiBase).toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}DataService.filterChange(event).then((result: ${c.n(this, AngularDerivedType.ApiBase)}[]) => {
            this.data = result;
        })
    }

    ${isAggregateView.then {"""generateTabElement() { 
        return [${props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") {
        it.toAngularGenerateTabElement(c)
    }}];
    }"""}}
}
"""
}

fun <T : CompilationUnitI<*>> T.toAngularEntityAggregateViewTypeScript(c: GenerationContext, Model: String = AngularDerivedType.Entity, ListComponent: String = AngularDerivedType.ListComponent, isAggregateView: Boolean = false, componentType: String = "list"): String {
    return """
${this.toAngularGenerateComponentPart(c, "entity-${this.parent().name().lowercase(Locale.getDefault())}", "entity", componentType, hasProviders = true, hasClass = true)}
${isOpen().then("export ")}class ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${Model}${ListComponent} implements ${c.n(angular.core.OnInit)} {

${this.toTypeScriptEntityPropInit(c, tab)}
    
    tabElement: Array<string>;
    data: ${c.n(this, AngularDerivedType.ApiBase)}[];
    decodedParams = {}

${this.toAngularConstructorDataService(c, tab, true)}

${this.toAngularListOnInit(c, tab, isAggregateView)}

    ${isAggregateView.then {"""generateTabElement() { 
        return [${props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") {
        it.toAngularGenerateTabElement(c)
    }}];
    }"""}}
}
"""
}

fun <T : AttributeI<*>> T.toAngularGenerateTabElement(c: GenerationContext, parentName: String = ""): String {
    return when (this.type()) {
        is EntityI<*>, is ValuesI<*> -> """'${this.parent().name().lowercase(Locale.getDefault())}${this.type().name().lowercase(Locale.getDefault())}', """
        else -> when(this.type().name().lowercase(Locale.getDefault())) {
            "list" -> when(this.type().generics().first().type()) {
                is EntityI<*>, is ValuesI<*> -> """'${this.parent().name().lowercase(Locale.getDefault())}${this.type().generics().first().type().name().lowercase(Locale.getDefault())}', """
                else -> ""
            }
            else -> ""
        }
    }
}

fun <T : AttributeI<*>> T.toAngularGenerateTableHeader(c: GenerationContext, parentName: String = ""): String {
    return when (this.type()) {
        is EntityI<*>, is ValuesI<*> -> """'${this.name().lowercase(Locale.getDefault())}-entity', """
        is BasicI<*> -> this.type().props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") {
            it.toAngularGenerateTableHeader(c, this.name())
        }
        is EnumTypeI<*> -> """'${if(parentName.isEmpty()) "" else "$parentName-"}${this.name().toCamelCase()}', """
        else -> when(this.type().name().lowercase(Locale.getDefault())) {
            "list" -> when(this.type().generics().first().type()) {
                is EntityI<*>, is ValuesI<*> -> """'${this.name().lowercase(Locale.getDefault())}-entity', """
                is EnumTypeI<*> -> """'${if(parentName.isEmpty()) "" else "$parentName-"}${this.name().toCamelCase()}', """
                else -> """'${if(parentName.isEmpty()) "" else "$parentName-"}${this.name().toCamelCase()}', """
            }
            else -> """'${if(parentName.isEmpty()) "" else "$parentName-"}${this.name().toCamelCase()}', """
        }
    }
}

fun <T : CompilationUnitI<*>> T.toAngularEntityDataService(
        entities: List<EntityI<*>>, values: List<ValuesI<*>>, c: GenerationContext, DataService: String = AngularDerivedType.DataService): String {
    return """

@${c.n(angular.core.Injectable)}({ providedIn: 'root' })
${isOpen().then("export ")}class ${if(this.name().equals(this.parent().name(), true)) {this.parent().name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} else {this.parent().name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + this.name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}}${DataService} extends ${c.n(service.template.DataService)}<${c.n(this, AngularDerivedType.ApiBase)}> {
    itemName = '${c.n(this, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}';
    moduleName = '${this.parent().name().lowercase(Locale.getDefault())}';
    componentChild = [${this.props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> """'${it.name().lowercase(Locale.getDefault()) + it.type().parent().name().lowercase(Locale.getDefault()) + it.type().name().lowercase(Locale.getDefault())}', """
            else -> {
                when(it.type().name()) {
                    "List" -> when(it.type().generics().first().type()) {
                        is EntityI<*>, is ValuesI<*> -> """'${it.name().lowercase(Locale.getDefault()) + it.type().generics().first().type().parent().name().lowercase(Locale.getDefault()) + it.type().generics().first().type().name().lowercase(Locale.getDefault())}', """
                        else -> ""
                    }
                    else -> ""
                }
            }
        }
    }}]
    isHidden = true;  
    
    ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "string") }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.type().toAngularControlService(c, false)
            else -> when(it.type().name().lowercase(Locale.getDefault())) {
                "list" -> when(it.type().generics().first().type()) {
                    is EntityI<*>, is ValuesI<*> -> it.type().generics().first().type().toAngularControlService(c, false)
                    is EnumTypeI<*> -> it.type().generics().first().type().toAngularControlService(c, true)
                    else -> ""
                }
                    else -> ""
            }
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
    
    ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "string") }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.type().toAngularControlServiceFunctions(c, it.type().props())
            else -> when(it.type().name().lowercase(Locale.getDefault())) {
                "list" -> when(it.type().generics().first().type()) {
                    is EntityI<*>, is ValuesI<*>, is EnumTypeI<*> -> it.type().generics().first().type().toAngularControlServiceFunctions(c, it.type().generics().first().type().props())
                    else -> ""
                }
                else -> ""
            }
        }
    }}
    
    initObservable() {
    ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "string") }.joinSurroundIfNotEmptyToString("") {
        when(it.type()) {
            is EntityI<*>, is ValuesI<*> -> it.type().toAngularInitObservable(c, it.type().props())
            else -> when(it.type().name().lowercase(Locale.getDefault())) {
                "list" -> when(it.type().generics().first().type()) {
                    is EntityI<*>, is ValuesI<*>, is EnumTypeI<*> -> it.type().generics().first().type().toAngularInitObservable(c, it.type().generics().first().type().props())
                    else -> ""
                }
                else -> ""
            }
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

    editElement(element: ${c.n(this, AngularDerivedType.ApiBase)}, elementName: string, isArray: boolean) {
        const newId = this.itemName + JSON.stringify(element);

        this.getData(this.itemName).subscribe((data) => {
            this.getEditData(this.itemName).subscribe((editData) => {
                this.loadMultipleElementFromListItem(elementName).subscribe((currentData) => {
                    if(!JSON.stringify(data).includes(JSON.stringify(element)) 
                        || (JSON.stringify(data) === '{}' || JSON.stringify(data) === '[]')) {
                        if (JSON.stringify(editData) === '{}' || JSON.stringify(editData) === '[]') {
                            this.saveData(this.itemName, element);
                        } else {
                            this.editItemFromTableArray(element, newId, this.itemName);
                            this.editInheritedEntity(element);
                        }

                        if (isArray) {
                            const updatedItems = currentData.map(function(item) {
                                if (JSON.stringify(item) === JSON.stringify(editData)) {
                                    item = element;
                                }
                                return item;
                            });
                            this.saveMultipleListItemData(elementName, updatedItems);
                        } else {
                            this.saveListItemData(elementName, element);
                        }
                    }

                    if (JSON.stringify(editData).includes(JSON.stringify(JSON.parse(localStorage.getItem('specificData'))))) {
                        this.saveSpecificData(element, ${this.props().any { !it.isEMPTY() && it.isToStr() == true }.then {  "element." + this.props().first { prop -> prop.isToStr() == true && !prop.isEMPTY() }.name()  }}${this.props().all { !it.isEMPTY() && (it.isToStr() == false || (it.type() is EntityI<*> || it.type() is ValuesI<*> || it.type() is BasicI<*>)) }.then { """''""" }} );
                    } 
                })
            })
        })
    }
    
    editInheritedEntity(newElement: ${c.n(this, AngularDerivedType.ApiBase)}) {
        this.getEditData(this.itemName).subscribe((editItem) => {
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
                
                this.getAnyEditData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}').subscribe((${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) => {
                    if ((JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '{}' || JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '[]')) {
                        ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)
                    ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.${elementName.name().toCamelCase()} = newElement;""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                    property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) }
                    ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.${elementName.name().toCamelCase()}.${elementName.type().props().filter { elementNameProp -> elementNameProp.isNotEMPTY() && elementNameProp.type().name().equals(this.name(), ignoreCase = true) && elementNameProp.type().namespace().equals(this.namespace(), ignoreCase = true) }.joinSurroundIfNotEmptyToString {elementNameProp -> elementNameProp.name().toCamelCase()}} = newElement;""" }}
                        this.saveEditData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}', ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}, false)
                    }
                })
                
                this.getAnySpecificData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}').subscribe((${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) => {
                    if ((JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '{}' || JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '[]')) {

                        if (Array.isArray(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())})) {
                            ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.forEach((element) => {
                                if (JSON.stringify(element).includes(JSON.stringify(editItem))) {
                                   ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()} = newElement;""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                        property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) }) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}.${elementName.type().props().filter { elementNameProp -> elementNameProp.isNotEMPTY() && elementNameProp.type().name().equals(this.name(), ignoreCase = true) && elementNameProp.type().namespace().equals(this.namespace(), ignoreCase = true) }.joinSurroundIfNotEmptyToString {elementNameProp -> elementNameProp.name().toCamelCase()}} = newElement;""" }}
                                }
                            })
                        } else {
                            ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)
                    ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.${elementName.name().toCamelCase()} = newElement;""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                    property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) }
                    ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.${elementName.name().toCamelCase()}.${elementName.type().props().filter { elementNameProp -> elementNameProp.isNotEMPTY() && elementNameProp.type().name().equals(this.name(), ignoreCase = true) && elementNameProp.type().namespace().equals(this.namespace(), ignoreCase = true) }.joinSurroundIfNotEmptyToString {elementNameProp -> elementNameProp.name().toCamelCase()}} = newElement;""" }}
                        }
                        this.getComponentName().subscribe((componentName) => {
                            this.saveSpecificData(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}, componentName, '${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}')
                            this.saveMultipleListItemData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}', ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())})
                        })
                    }
                })
                
                this.loadAnyElementFromListItem('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}').subscribe((${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) => {
                    if ((JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '{}' || JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '[]')
                        && JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}).includes(JSON.stringify(editItem))) {
                        if (Array.isArray(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())})) {
                            ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.forEach((element) => {
                                if (JSON.stringify(element).includes(JSON.stringify(editItem))) {
                                   ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()} = newElement;""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                    property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) }) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}.${elementName.type().props().filter { elementNameProp -> elementNameProp.isNotEMPTY() && elementNameProp.type().name().equals(this.name(), ignoreCase = true) && elementNameProp.type().namespace().equals(this.namespace(), ignoreCase = true) }.joinSurroundIfNotEmptyToString {elementNameProp -> elementNameProp.name().toCamelCase()}} = newElement;""" }}
                                    this.saveMultipleListItemData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}', ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())})
                                }
                            })
                        } else {
                            ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)
                    ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.${elementName.name().toCamelCase()} = newElement;""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                    property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) }
                    ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.${elementName.name().toCamelCase()}.${elementName.type().props().filter { elementNameProp -> elementNameProp.isNotEMPTY() && elementNameProp.type().name().equals(this.name(), ignoreCase = true) && elementNameProp.type().namespace().equals(this.namespace(), ignoreCase = true) }.joinSurroundIfNotEmptyToString {elementNameProp -> elementNameProp.name().toCamelCase()}} = newElement;""" }}
                            this.saveListItemData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}', ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())})
                        }
                    }
                })
                
                this.getAnyData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}').subscribe((${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) => {
                    if ((JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '{}' || JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '[]')
                        && JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}).includes(JSON.stringify(editItem))) {
                        ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.forEach((element) => {
                            if (Array.isArray(${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                    property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) }) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}""" }})) {
                                ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                        property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) }) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}""" }}.forEach((parentData, index) => {
                                    if (JSON.stringify(editItem).includes(JSON.stringify(parentData))) {
                                        ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}[index] = newElement;""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                        property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) }) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}[index] = newElement;""" }}
                                    }
                                })
                            } else {
                                ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)
                    ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()} = newElement;""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                    property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) }
                    ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}.${elementName.type().props().filter { elementNameProp -> elementNameProp.isNotEMPTY() && elementNameProp.type().name().equals(this.name(), ignoreCase = true) && elementNameProp.type().namespace().equals(this.namespace(), ignoreCase = true) }.joinSurroundIfNotEmptyToString {elementNameProp -> elementNameProp.name().toCamelCase()}} = newElement;""" }}   
                            }
                        })
                        this.replaceData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}', ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())})
                    }
                })
            """
        }}
        
        ${values.filter { basic -> basic.props().any { property ->
        (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( basic.props().any {
            childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && !childProperty.type().name().equals("list", true) && childProperty.type().namespace().equals(this.namespace(), true) } ||
                property.type().props().any {
                    childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && !childProperty.type().name().equals("list", true) && childProperty.type().namespace().equals(this.namespace(), true) }
                )
    }
    }.joinSurroundIfNotEmptyToString("") {
        """
                
                this.getAnyEditData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}').subscribe((${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) => {
                    if ((JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '{}' || JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '[]')) {
                        ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true) && (property.name().equals(this.parent().name(), ignoreCase = true))
                ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.${elementName.name().toCamelCase()} = newElement;""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && (childProperty.name().equals(this.parent().name(), ignoreCase = true)) }
                ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.${elementName.name().toCamelCase()}.${elementName.type().props().filter { elementNameProp -> elementNameProp.isNotEMPTY() && elementNameProp.type().name().equals(this.name(), ignoreCase = true) && elementNameProp.type().namespace().equals(this.namespace(), ignoreCase = true) }.joinSurroundIfNotEmptyToString {elementNameProp -> elementNameProp.name().toCamelCase()}} = newElement;""" }}
                        this.saveEditData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}', ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}, false)
                    }
                })
                
                this.getAnySpecificData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}').subscribe((${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) => {
                    if ((JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '{}' || JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '[]')) {

                        if (Array.isArray(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())})) {
                            ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.forEach((element) => {
                                if (JSON.stringify(element).includes(JSON.stringify(editItem))) {
                                   ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)) && (property.name().equals(this.parent().name(), ignoreCase = true)) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()} = newElement;""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && (childProperty.name().equals(this.parent().name(), ignoreCase = true)) }) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}.${elementName.type().props().filter { elementNameProp -> elementNameProp.isNotEMPTY() && elementNameProp.type().name().equals(this.name(), ignoreCase = true) && elementNameProp.type().namespace().equals(this.namespace(), ignoreCase = true) }.joinSurroundIfNotEmptyToString {elementNameProp -> elementNameProp.name().toCamelCase()}} = newElement;""" }}
                                }
                            })
                        } else {
                            ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true) && (property.name().equals(this.parent().name(), ignoreCase = true))
                ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.${elementName.name().toCamelCase()} = newElement;""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && (childProperty.name().equals(this.parent().name(), ignoreCase = true)) }
                ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.${elementName.name().toCamelCase()}.${elementName.type().props().filter { elementNameProp -> elementNameProp.isNotEMPTY() && elementNameProp.type().name().equals(this.name(), ignoreCase = true) && elementNameProp.type().namespace().equals(this.namespace(), ignoreCase = true) }.joinSurroundIfNotEmptyToString {elementNameProp -> elementNameProp.name().toCamelCase()}} = newElement;""" }}
                        }
                        this.getComponentName().subscribe((componentName) => {
                            this.saveSpecificData(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}, componentName, '${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}')
                            this.saveMultipleListItemData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}', ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())})
                        })
                    }
                })
                
                this.loadAnyElementFromListItem('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}').subscribe((${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) => {
                    if ((JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '{}' || JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '[]')
                        && JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}).includes(JSON.stringify(editItem))) {
                        if (Array.isArray(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())})) {
                            ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.forEach((element) => {
                                if (JSON.stringify(element).includes(JSON.stringify(editItem))) {
                                   ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)) && (property.name().equals(this.parent().name(), ignoreCase = true)) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()} = newElement;""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && (childProperty.name().equals(this.parent().name(), ignoreCase = true))}) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}.${elementName.type().props().filter { elementNameProp -> elementNameProp.isNotEMPTY() && elementNameProp.type().name().equals(this.name(), ignoreCase = true) && elementNameProp.type().namespace().equals(this.namespace(), ignoreCase = true) }.joinSurroundIfNotEmptyToString {elementNameProp -> elementNameProp.name().toCamelCase()}} = newElement;""" }}
                                    this.saveMultipleListItemData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}', ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())})
                                }
                            })
                        } else {
                            ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true) && (property.name().equals(this.parent().name(), ignoreCase = true))
                ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.${elementName.name().toCamelCase()} = newElement;""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && (childProperty.name().equals(this.parent().name(), ignoreCase = true)) }
                ) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.${elementName.name().toCamelCase()}.${elementName.type().props().filter { elementNameProp -> elementNameProp.isNotEMPTY() && elementNameProp.type().name().equals(this.name(), ignoreCase = true) && elementNameProp.type().namespace().equals(this.namespace(), ignoreCase = true) }.joinSurroundIfNotEmptyToString {elementNameProp -> elementNameProp.name().toCamelCase()}} = newElement;""" }}
                            this.saveListItemData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}', ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())})
                        }
                    }
                })
                
                this.getAnyData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}').subscribe((${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) => {
                    if ((JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '{}' || JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '[]')
                        && JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}).includes(JSON.stringify(editItem))) {
                            ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.forEach((element) => {
                            if (Array.isArray(${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)) && (property.name().equals(this.parent().name(), ignoreCase = true)) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && (childProperty.name().equals(this.parent().name(), ignoreCase = true)) }) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}""" }})) {
                                ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)) && (property.name().equals(this.parent().name(), ignoreCase = true)) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && (childProperty.name().equals(this.parent().name(), ignoreCase = true)) }) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}""" }}.forEach((parentData, index) => {
                                    if (JSON.stringify(editItem).includes(JSON.stringify(parentData))) {
                                        ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)) && (property.name().equals(this.parent().name(), ignoreCase = true)) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}[index] = newElement;""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && (childProperty.name().equals(this.parent().name(), ignoreCase = true)) }) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}[index] = newElement;""" }}
                                    }
                                })
                            } else {
                                if (JSON.stringify(editItem).includes(JSON.stringify(${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)) && (property.name().equals(this.parent().name(), ignoreCase = true)) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && (childProperty.name().equals(this.parent().name(), ignoreCase = true)) }) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()}""" }}))) {
                                    ${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( property.type().name().equals(this.name(), ignoreCase = true)) && (property.name().equals(this.parent().name(), ignoreCase = true)) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()} = newElement;""" }}${it.props().filter { property -> (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && (
                property.type().props().any {childProperty -> childProperty.type().name().equals(this.name(), ignoreCase = true) && (childProperty.name().equals(this.parent().name(), ignoreCase = true)) }) }.joinSurroundIfNotEmptyToString(nL + tabs5) { elementName -> """element.${elementName.name().toCamelCase()} = newElement;""" }}
                                }
                            }
                        })
                        this.replaceData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}', ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())})
                    }
                })
            """
    }}
        
        ${entities.filter { entity -> entity.props().any {property ->
            (property.type() is BasicI<*> || property.type() is EntityI<*> || property.type() is ValuesI<*>) && ( entity.props().any {
                childProperty -> childProperty.type().name().equals("list", true) && childProperty.type().generics().first().type().name().equals(this.name(), ignoreCase = true) && childProperty.type().generics().first().type().namespace().equals(this.namespace(), true) } ||
                    property.type().props().any {
                        childProperty -> childProperty.type().name().equals("list", true) && childProperty.type().generics().first().type().name().equals(this.name(), ignoreCase = true) && childProperty.type().generics().first().type().namespace().equals(this.namespace(), true) }
                    )
        }
        }.joinSurroundIfNotEmptyToString("") {
            val listElementName = it.props().filter { property -> property.type().name().equals("list", true) && property.type().generics().first().type().name().equals(this.name(), ignoreCase = true) && property.type().generics().first().type().namespace().equals(this.namespace(), true) }.joinSurroundIfNotEmptyToString { it.name() }
            val listPropName = it.props().filter { property -> property.type().name().equals("list", true) && property.type().generics().first().type().name().equals(this.name(), ignoreCase = true) && property.type().generics().first().type().namespace().equals(this.namespace(), true) }.joinSurroundIfNotEmptyToString { it.type().generics().first().type().name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) } };
            """
                this.getAnyEditData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}').subscribe((${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) => {
                    if ((JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '{}' || JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '[]')
                        && JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}).includes(JSON.stringify(editItem))) {
                        ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.forEach((element) => {
                            element.${listElementName}.forEach((${listPropName}, index) => {
                                if (JSON.stringify(editItem).includes(JSON.stringify(${listPropName}))) {
                                    element.${listElementName}[index] = newElement
                                }
                            })
                        })
                        this.saveEditData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}', ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}, false)
                    }
                })

                this.getAnySpecificData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}').subscribe((${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) => {
                    if ((JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '{}' || JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '[]')) {
                        ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.forEach((element) => {
                            element.${listElementName}.forEach((${listPropName}, index) => {
                                if (JSON.stringify(editItem).includes(JSON.stringify(${listPropName}))) {
                                    element.${listElementName}[index] = newElement
                                }
                            })
                        })
                        this.getComponentName().subscribe((componentName) => {
                            this.saveSpecificData(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}, componentName, '${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}')
                            this.saveMultipleListItemData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}', ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())})
                        })
                    }
                })
                
                this.getAnyData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}').subscribe((${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) => {
                    if ((JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '{}' || JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}) !== '[]')
                        && JSON.stringify(${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}).includes(JSON.stringify(editItem))) {
                        ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}.forEach((element) => {
                            element.${listElementName}.forEach((${listPropName}, index) => {
                                if (JSON.stringify(editItem).includes(JSON.stringify(${listPropName}))) {
                                    element.${listElementName}[index] = newElement
                                }
                            })
                        })
                        this.replaceData('${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}', ${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())})
                    }
                })"""}}
            }
        })
    }
    
    ${entities.filter {!it.isEMPTY() && !it.props().isEMPTY() && !it.belongsToAggregate().isEMPTY() && it.belongsToAggregate().name().equals(this.name(), true)}.joinSurroundIfNotEmptyToString {
        val propName = this.props().filter {prop -> !prop.isEMPTY() && prop.name().contains(it.name(), true)}.first()
        """
    removeAggregateItem(element: Array<${c.n(it, AngularDerivedType.ApiBase).toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}>, elementName: string = '${c.n(it, AngularDerivedType.ApiBase).lowercase(Locale.getDefault())}') {
        element.forEach((content) => {
            this.getAnyData(elementName).subscribe((item) => {
                if (JSON.stringify(item).includes(JSON.stringify(content))) {
                    const index = item.findIndex((${it.name().lowercase(Locale.getDefault())}) => JSON.stringify(${it.name().lowercase(Locale.getDefault())}) === JSON.stringify(content));
                    item.splice(index, 1)
                    this.replaceAnyData(elementName, item)
                }
            })
        })
        
        setTimeout(() => {
            this.getData().subscribe((${this.name().lowercase(Locale.getDefault())}) => {
               ${this.name().lowercase(Locale.getDefault())}.forEach((item) => {
                    element.forEach((content) => {
                        if (JSON.stringify(item).includes(JSON.stringify(content))) {
                            const index = item.${propName.name().lowercase(Locale.getDefault())}.findIndex((${it.name().lowercase(Locale.getDefault())}) => JSON.stringify(${it.name().lowercase(Locale.getDefault())}) === JSON.stringify(content));
                            item.${propName.name().lowercase(Locale.getDefault())}.splice(index, 1)
                        }
                    })
                });
                this.replaceData(this.itemName, ${this.name().lowercase(Locale.getDefault())})
            })
        }, 100)
    }"""
    }}
}

declare global {
    interface Window {
        ${if(this.name().equals(this.parent().name(), true)) {
        this.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
    } else {
        this.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) } + this.name()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}}${DataService}: ${if(this.name().equals(this.parent().name(), true)) {this.parent().name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} else {this.parent().name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + this.name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}}${DataService};
    }
}
window.${if(this.name().equals(this.parent().name(), true)) {
        this.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }
    } else {
        this.parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) } + this.name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}}${DataService} = new ${if(this.name().equals(this.parent().name(), true)) {this.parent().name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} else {this.parent().name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + this.name()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}}${DataService}();
"""
}

fun <T : ItemI<*>> T.toAngularGenerateEnumElementBasic(c: GenerationContext, indent: String): String {
    return """${indent}${c.n(this, AngularDerivedType.Enum).replaceFirstChar { it.lowercase(Locale.getDefault()) }} = this.loadEnumElement(${c.n(this, AngularDerivedType.ApiBase)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }});"""
}

fun <T : CompilationUnitI<*>> T.toAngularBasicTSComponent(c: GenerationContext, BasicComponent: String = AngularDerivedType.BasicComponent, DataService: String = AngularDerivedType.DataService): String {
    return """
${this.toAngularGenerateComponentPart(c, "basic-${this.parent().name().lowercase(Locale.getDefault())}", "basic", "", hasProviders = false, hasClass = false)}
${isOpen().then("export ")}class ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${BasicComponent} implements ${c.n(angular.core.OnInit)} {

    @${c.n(angular.core.Input)}() ${this.name().lowercase(Locale.getDefault())}: ${c.n(this, AngularDerivedType.ApiBase)};
    @${c.n(angular.core.Input)}() parentName: String;
    @${c.n(angular.core.Input)}() isDisabled = false;
    
    selectedIndices: string;
    multipleSelectedIndices: Array<string>;

${if (props().any { it.type() is EntityI<*> || it.type() is ValuesI<*> || it.type().generics().any {genericType -> genericType.type() is EntityI<*> || genericType.type() is ValuesI<*> ||genericType.type() is BasicI<*> || genericType.type() is EnumTypeI<*>}}) {
        """${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "string") }.distinctBy { if(it.type().name().equals("list", true)) {it.type().generics().first().type().name()} else {it.type().name()} }.joinSurroundIfNotEmptyToString("") {
            when(it.type()) {
                is EntityI<*>, is ValuesI<*> -> it.type().toAngularControlService(c, false)
                else -> when(it.type().name().lowercase(Locale.getDefault())) {
                    "list" -> when(it.type().generics().first().type()) {
                        is EntityI<*>, is ValuesI<*> -> it.type().generics().first().type().toAngularControlService(c, false)
                        is EnumTypeI<*> -> it.type().generics().first().type().toAngularControlService(c, true)
                        else -> ""
                    }
                    else -> ""
                }
            }
        }}        
    constructor(${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "string") }.distinctBy { if(it.type().name().equals("list", true)) {it.type().generics().first().type().name()} else {it.type().name()} }.joinSurroundIfNotEmptyToString("") {
            when(it.type()) {
                is EntityI<*>, is ValuesI<*> -> it.type().toAngularPropOnConstructor(c)
                else -> when(it.type().name().lowercase(Locale.getDefault())) {
                    "list" -> when(it.type().generics().first().type()) {
                        is EntityI<*>, is ValuesI<*> -> it.type().generics().first().type().toAngularPropOnConstructor(c)
                        is EnumTypeI<*> -> "public ${DataService.replaceFirstChar { it.lowercase(Locale.getDefault()) }}: ${c.n(service.template.DataService)}<any>"
                        else -> ""
                    }
                    else -> ""
                } }
        }.trim()
        }) {}
"""
    }else{""}}

    ngOnInit() {
        if (this.${this.name().lowercase(Locale.getDefault())} === undefined) {
            this.${this.name().lowercase(Locale.getDefault())} = new ${c.n(this, AngularDerivedType.ApiBase)}();
        }
        ${props().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toTypeScriptInitEmptyProps(c)
    }.trim()}
    
    ${if (props().any { it.type() is EntityI<*> || it.type() is ValuesI<*> || it.type().generics().any {genericType -> genericType.type() is EntityI<*> || genericType.type() is ValuesI<*> ||genericType.type() is BasicI<*> || genericType.type() is EnumTypeI<*>} }) {
        """
        ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "string") }.joinSurroundIfNotEmptyToString("") {
            when(it.type()) {
                is EntityI<*>, is ValuesI<*> -> it.toAngularInitOptionBasic(c, it.type().name())
                else -> when(it.type().name().lowercase(Locale.getDefault())) {
                    "list" -> when(it.type().generics().first().type()) {
                        is EntityI<*>, is ValuesI<*> -> it.toAngularInitOptionBasic(c, it.type().generics().first().type().name())
                        is EnumTypeI<*> -> "this.option${it.type().generics().first().type().name()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} = this.dataService.loadEnumElement(${c.n(it.type().generics().first().type(), AngularDerivedType.ApiBase)
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }});"
                        else -> ""
                    }
                    else -> ""
                }
            }
        }}
    
        this.initObservable();"""
    } else {""}}
    }
    
${if (props().any { it.type() is EntityI<*> || it.type() is ValuesI<*> || it.type().generics().any {genericType -> genericType.type() is EntityI<*> || genericType.type() is ValuesI<*> ||genericType.type() is BasicI<*> || genericType.type() is EnumTypeI<*>} }) {
        """    
        ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "string") }.distinctBy { if(it.type().name().equals("list", true)) {it.type().generics().first().type().name()} else {it.type().name()} }.joinSurroundIfNotEmptyToString("") {
            when(it.type()) {
                is EntityI<*>, is ValuesI<*> -> it.type().toAngularControlServiceFunctions(c, it.type().props())
                else -> when(it.type().name().lowercase(Locale.getDefault())) {
                    "list" -> when(it.type().generics().first().type()) {
                        is EntityI<*>, is ValuesI<*>, is EnumTypeI<*> -> it.type().generics().first().type().toAngularControlServiceFunctions(c, it.type().generics().first().type().props())
                        else -> ""
                    }
                    else -> ""
                }
            }
        }}
        
${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "string") }.joinSurroundIfNotEmptyToString("") {
    when(it.type()) {
        is EntityI<*>, is ValuesI<*> -> it.type().toAngularFunctionRemove(c, it.name(), this)
        else -> when(it.type().name().lowercase(Locale.getDefault())) {
            "list" -> when(it.type().generics().first().type()) {
                is EntityI<*>, is ValuesI<*> -> it.type().generics().first().type().toAngularFunctionRemoveMultiple(c, it.name(), this)
                else -> ""
            }
            else -> ""
        }
    }
}}
    
    initObservable() {
        ${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "string") }.distinctBy { if(it.type().name().equals("list", true)) {it.type().generics().first().type().name()} else {it.type().name()} }.joinSurroundIfNotEmptyToString("") {
            when(it.type()) {
                is EntityI<*>, is ValuesI<*> -> it.type().toAngularInitObservable(c, it.type().props())
                else -> when(it.type().name().lowercase(Locale.getDefault())) {
                    "list" -> when(it.type().generics().first().type()) {
                        is EntityI<*>, is ValuesI<*>, is EnumTypeI<*> -> it.type().generics().first().type().toAngularInitObservable(c, it.type().generics().first().type().props())
                        else -> ""
                    }
                    else -> ""
                }
            }
        }}
    }"""
    } else {""}}
}
"""
}

fun <T : ItemI<*>> T.toAngularInitOptionBasic(c: GenerationContext, elementType: String): String {
    return """this.option${this.parent().parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${elementType.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} = this.${this.parent().parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${elementType.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}DataService.changeMapToArray(this.${this.parent().parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${elementType.toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}DataService.retrieveItemsFromCache()); $nL"""
}

fun <T : CompilationUnitI<*>> T.toAngularEnumTSComponent(c: GenerationContext, EnumComponent: String = AngularDerivedType.EnumComponent,
                                                         DataService: String = AngularDerivedType.DataService): String {
    return """
${this.toAngularGenerateComponentPart(c, "enum-${this.parent().name().lowercase(Locale.getDefault())}", "enum", "", hasProviders = false, hasClass = false)}

export class ${this.name()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${EnumComponent} implements ${c.n(angular.core.OnInit)} {

    @${c.n(angular.core.Input)}() ${this.name().lowercase(Locale.getDefault())}: ${c.n(this, AngularDerivedType.ApiBase)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} | Array<${c.n(this, AngularDerivedType.ApiBase)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}>;
    @${c.n(angular.core.Input)}() isDisabled = false;
    @${c.n(angular.core.Input)}() mode: string;
    @${c.n(angular.core.Input)}() componentName: string;
    @${c.n(angular.core.Output)}() ${this.name().lowercase(Locale.getDefault())}Change = new ${c.n(angular.core.EventEmitter)}<typeof this.${this.name().lowercase(Locale.getDefault())}>();
    
    enumElements: Array<string>;
    multipleSelectedIndices: Array<string> = [];
    
    constructor(public ${DataService.replaceFirstChar { it.lowercase(Locale.getDefault()) }}: ${c.n(service.template.DataService)}<any>) { }
    
    ngOnInit(): void {
        this.enumElements = this.${DataService.replaceFirstChar { it.lowercase(Locale.getDefault()) }}.loadEnumElement(${c.n(this, AngularDerivedType.ApiBase)});

        setTimeout(() => {
            if (this.${this.name().lowercase(Locale.getDefault())} !== undefined) {
                const temp = [];
                this.enumElements.forEach((data, index) => {
                    if (this.${this.name().lowercase(Locale.getDefault())}.toString().toLowerCase().includes(data.toLowerCase())) {
                        temp.push(index.toString())
                    }
                });
                this.multipleSelectedIndices = temp;
            }
        }, 100)
    }
    
    changeValue({ detail: [id] }: CustomEvent<string[]>) {
        this.${this.name().lowercase(Locale.getDefault())}Change.emit(this.enumElements[id]);
    }
    
    changeValueMultiple(index: CustomEvent<string[]>) {
        const temp = [];
        index.detail.forEach(id => {
            temp.push(this.enumElements[id]);
        });
        this.${this.name().lowercase(Locale.getDefault())}Change.emit(temp);
    }

}
"""
}

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.*
import java.util.*


fun <T : ItemI<*>> T.toAngularGenerateDefaultTranslate(): String =
        """
    "add": "Add",
    "new": "New",
    "edit": "Edit",
    "item": "Item",
    "filter": "Filter",
    "select": "Select",
    "delete": "Delete",
    "items": "Items",
    "hover": "Hover",
    "on": "On",
    "save": "Save",
    "load": "Load",
    "cancel": "Cancel",
    "save changes": "Save Changes",
    "cancel edit": "Cancel Edit",
    "date": "Date",
    "format": "Format",
    "language": "Language",
    "configuration": "Configuration",
    
    "shortDate": "Short Date",
    "mediumDate": "Medium Date",
    "longDate": "Long Date",
    "fullDate": "Full Date",
    "home": {
        "navTitle": "Home"
    },
    """

fun <T : LiteralI<*>> T.toAngularGenerateModuleEnumsTranslate(): String =
    """"${this.name().uppercase(Locale.getDefault())}": "${this.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}","""

fun <T : ModuleI<*>> T.toAngularGenerateModuleElementsTranslate(): String =
        """
    "${this.name().lowercase(Locale.getDefault())}": {
        "navTitle": "${this.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}",
        "table": {
            ${this.basics().filter { !it.isEMPTY() && !(it.props().none { prop -> prop.type() !is BasicI<*> }) }.joinSurroundIfNotEmptyToString("$nL$tab$tab$tab") {
                """"${it.name().lowercase(Locale.getDefault())}": "${it.parent().name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} ${it.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}","""
            }}
            ${this.entities().filter { !it.isEMPTY() && !(it.props().none { prop -> prop.type() !is BasicI<*> }) && (it.name().equals(it.parent().name(), true)) }.joinSurroundIfNotEmptyToString(",") {
                it.toAngularGeneratePropsForTableTranslate()
            }}
            ${this.values().filter { !it.isEMPTY() && !(it.props().none { prop -> prop.type() !is BasicI<*> }) && (it.name().equals(it.parent().name(), true)) }.joinSurroundIfNotEmptyToString(",") {
                it.toAngularGeneratePropsForTableTranslate()
            }}
            ${this.basics().filter { !it.isEMPTY() && !(it.props().none { prop -> prop.type() !is BasicI<*> }) }.joinSurroundIfNotEmptyToString(",") {
                it.toAngularGeneratePropsForTableTranslate()
            }}
        }
    },
    ${this.entities().filter { !it.isEMPTY() && !(it.props().all { prop -> prop.type() is BasicI<*> }) && !(it.name().equals(it.parent().name(), true)) }.joinSurroundIfNotEmptyToString(nL) {
            it.toAngularGeneratePropsFromEntityAndValuesTranslate()
    }}
    ${this.values().filter { !it.isEMPTY() && !(it.props().all { prop -> prop.type() is BasicI<*> }) && !(it.name().equals(it.parent().name(), true)) }.joinSurroundIfNotEmptyToString(nL) {
            it.toAngularGeneratePropsFromEntityAndValuesTranslate()
    }}
    ${this.entities().filter { !it.isEMPTY() && (it.props().all { prop -> prop.type() is BasicI<*> }) && !(it.name().equals(it.parent().name(), true)) }.joinSurroundIfNotEmptyToString(nL) {
            it.toAngularGeneratePropsFromEntityAndValuesOnlyBasicsTranslate()
    }}
    ${this.values().filter { !it.isEMPTY() && (it.props().all { prop -> prop.type() is BasicI<*> }) && !(it.name().equals(it.parent().name(), true)) }.joinSurroundIfNotEmptyToString(nL) {
            it.toAngularGeneratePropsFromEntityAndValuesOnlyBasicsTranslate()
    }}"""

fun <T : TypeI<*>> T.toAngularGeneratePropsFromEntityAndValuesTranslate(): String =
    """
    "${this.parent().name().lowercase(Locale.getDefault())}${this.name().lowercase(Locale.getDefault())}": {
        "navTitle": "${this.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}",
        "table": {
            ${this.props().filter { !it.isEMPTY() && it.type() !is BasicI<*>}.joinSurroundIfNotEmptyToString(",$nL$tab$tab$tab") {
                it.toAngularGenerateEntityPropsTranslate()
            }}
        }
    },"""

fun <T : TypeI<*>> T.toAngularGeneratePropsFromEntityAndValuesOnlyBasicsTranslate(): String =
        """
    "${this.parent().name().lowercase(Locale.getDefault())}${this.name().lowercase(Locale.getDefault())}": {
        "navTitle": "${this.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
    },"""

fun <T : TypeI<*>> T.toAngularGeneratePropsForTableTranslate(): String =
        """
            ${this.props().filter { !it.isEMPTY() && it.type() !is BasicI<*>}.joinSurroundIfNotEmptyToString(",$nL$tab$tab$tab") {
                it.toAngularGeneratePropsForTableTranslate()
            }}
            """

fun <T : TypeI<*>> T.toAngularGeneratePropsTranslate(): String =
    """"${this.name().lowercase(Locale.getDefault())}": "${this.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}""""

fun <T : AttributeI<*>> T.toAngularGenerateEntityPropsTranslate(): String =
        """"${this.name().lowercase(Locale.getDefault())}": "${this.parent().name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} ${this.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}""""

fun <T : AttributeI<*>> T.toAngularGeneratePropsForTableTranslate(): String =
        """"${this.name().lowercase(Locale.getDefault())}": "${this.parent().parent().name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} ${this.name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}""""

fun <T : ItemI<*>> T.toAngularSelectedIndices(c: GenerationContext): String {
    return """selectedIndices${if(this.parent().name().equals(this.name())) {
        this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    } else {"""${this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${this.name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""}} = ''; $nL"""
}

fun <T : ItemI<*>> T.toAngularSelectedMultipleIndices(c: GenerationContext): String {
    return """multipleSelectedIndices${if(this.parent().name().equals(this.name())) {
        this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    } else {"""${this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${this.name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"""}}: Array<string> = []; $nL"""
}

fun <T : ItemI<*>> T.toAngularConstructorDataService(c: GenerationContext, indent: String, needLocation: Boolean): String {
    return """${indent}constructor(public ${c.n(this, AngularDerivedType.DataService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}: ${c.n(this, AngularDerivedType.DataService)} ${needLocation.then { """, private _location: ${c.n(angular.common.Location)}""" }}, private _route: ${c.n(angular.router.ActivatedRoute)}) {}$nL"""
}

fun <T : ItemI<*>> T.toAngularPropOnConstructor(c: GenerationContext): String {
    return """${tab + tab}public ${c.n(this, AngularDerivedType.DataService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}: ${c.n(this, AngularDerivedType.DataService)}, $nL"""
}

fun <T : ItemI<*>> T.toAngularFunctionRemove(c: GenerationContext, propName: String, parent: CompilationUnitI<*>): String {
    return """
    removeChip${c.n(this, AngularDerivedType.ApiBase).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(i: number) {
        this.${parent.name().lowercase(Locale.getDefault())}.${propName.lowercase(Locale.getDefault())} = new ${c.n(this, AngularDerivedType.ApiBase).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}();
        this.selectedIndices${c.n(this, AngularDerivedType.ApiBase).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} = '';
    }$nL"""
}

fun <T : ItemI<*>> T.toAngularFunctionRemoveMultiple(c: GenerationContext, propName: String, parent: CompilationUnitI<*>): String {
    return """
    removeChip${c.n(this, AngularDerivedType.ApiBase).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(i: number) {
        this.${parent.name().lowercase(Locale.getDefault())}.${propName.lowercase(Locale.getDefault())}.splice(this.multipleSelectedIndices${c.n(this, AngularDerivedType.ApiBase).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}.indexOf(i.toString()), 1)
        this.multipleSelectedIndices${c.n(this, AngularDerivedType.ApiBase).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}.splice(this.multipleSelectedIndices${c.n(this, AngularDerivedType.ApiBase).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}.indexOf(i.toString()), 1)
    }$nL"""
}

fun <T : TypeI<*>> T.toAngularControlService(c: GenerationContext, isEnum: Boolean): String {
    val name = if(this.parent().name().equals(this.name())) {this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}
    else {this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + this.name().toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}
    return """
    control${name} = new ${c.n(angular.forms.FormControl)}<${c.n(this, AngularDerivedType.ApiBase).toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}>(${if (isEnum) {"0"} else {"new ${c.n(this, AngularDerivedType.ApiBase).toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}()"}});
    option${name}: Array<${c.n(this, AngularDerivedType.ApiBase).toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}>;
    filteredOptions${name}: ${c.n(rxjs.empty.Observable)}<${c.n(this, AngularDerivedType.ApiBase).toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}[]>;$nL"""
}

fun <T : TypeI<*>> T.toAngularControlServiceFunctions(c: GenerationContext, key: ListMultiHolder<AttributeI<*>>): String {
    val name = if(this.parent().name().equals(this.name())) {this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}
    else {this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + this.name().toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}

    return """
    display${name}(${c.n(this, AngularDerivedType.ApiBase).toCamelCase()}: ${c.n(this, AngularDerivedType.ApiBase).toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}): string {
        return ${c.n(this, AngularDerivedType.ApiBase).toCamelCase()} ? ${if(key.any { it.type().name() == "String" }) {c.n(this, AngularDerivedType.ApiBase).toCamelCase() + "." + key.first { it.type().name() == "String" }.name()} else {"JSON.stringify(${c.n(this, AngularDerivedType.ApiBase).toCamelCase()})"}} : '';
    }
    
    filter${name}(name: string, array: Array<${c.n(this, AngularDerivedType.ApiBase).toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}>): ${c.n(this, AngularDerivedType.ApiBase).toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}[] {
        return array.filter(option => ${if(key.any { it.type().name() == "String" }) {"option." + key.first { it.type().name() == "String" }.name()} else {"JSON.stringify(option)"}}.toLowerCase().includes(name.toLowerCase()));
    }$nL"""
}

fun <T : TypeI<*>> T.toAngularInitObservable(c: GenerationContext, key: ListMultiHolder<AttributeI<*>>): String {
    val name = if(this.parent().name().equals(this.name())) {this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}
    else {this.parent().name().toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + this.name().toCamelCase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}
    return """
        this.filteredOptions${name} = this.control${name}.valueChanges.pipe(
            ${c.n(rxjs.operators.startWith)}(''),
            ${c.n(rxjs.operators.map)}((value: ${c.n(this, AngularDerivedType.ApiBase).toCamelCase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}) => {
                const name = typeof value === 'string' ? value : ${if(key.any { it.type().name() == "String" }) {"value." + key.first { it.type().name() == "String" }.name()} else {"JSON.stringify(value)"}};
                return name ?
                    this.filter${name}(name as string, this.option${name})
                    : this.option${name}.slice();
            }),
        );$nL"""
}

fun <T : CompilationUnitI<*>> T.toAngularViewOnInit(c: GenerationContext, indent: String): String {
    return """${indent}ng${c.n(angular.core.OnInit)}(): void {
        this.${this.name().lowercase(Locale.getDefault())} = this.${c.n(this, AngularDerivedType.DataService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getFirst();
        this.${c.n(this, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.checkRoute(this.${this.name().lowercase(Locale.getDefault())});
    
        if (this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.isSpecificNew === undefined) {
            this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getIsSpecificNew().subscribe((status) => {
                this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.isSpecificNew = status;
            });
        }
        
        this._route.queryParams.subscribe(param => {
            this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.componentName = param['name'];
            this.tabElement = param['tabElement'];
        })

        if(this.${c.n(this, AngularDerivedType.DataService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.componentName !== undefined && this.${c.n(this, AngularDerivedType.DataService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.componentName.length > 0) {
            this.isSpecificView = true;
        }
    }"""
}

fun <T : CompilationUnitI<*>> T.toAngularFormOnInit(c: GenerationContext, indent: String): String {

    return """${indent}ng${c.n(angular.core.OnInit)}(): void {
        
    ${this.props().filter { it.type() is BasicI<*> || it.type() is EntityI<*> }.joinSurroundIfNotEmptyToString(nL + tab) {
        it.toAngularEmptyProps(c, indent, it.type())
    }.trim()}
    
    ${this.props().filter { it.type() is ValuesI<*> }.joinSurroundIfNotEmptyToString(nL + tab) {
        it.toAngularEmptyPropsValues(c, indent, it.type())
    }.trim()}
        
    ${this.props().filter { !it.isEMPTY() && it.type().name().equals("list", true) && it.type().generics().first().type() is EntityI<*> }.joinSurroundIfNotEmptyToString(",$nL$tab$tab") {
        """
        if (this.${this.name()
            .lowercase(Locale.getDefault())}.${it.name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} == null) {
            this.${c.n(it.type().generics().first().type(), AngularDerivedType.DataService).toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}.loadMultipleElementFromListItem('${c.n(it.type().generics().first().type(), AngularDerivedType.DataService).replace(AngularDerivedType.DataService, "").lowercase(Locale.getDefault())}').subscribe((elements) => {
                if (elements !== null && elements.length > 0) {
                    this.${this.name()
            .lowercase(Locale.getDefault())}.${it.name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} = [];
                    elements.forEach((element) => {
                        this.${this.name()
            .lowercase(Locale.getDefault())}.${it.name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.push(element)
                    })
                } else {
                    this.${this.name()
            .lowercase(Locale.getDefault())}.${it.name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} = [];
                }
            })
        }"""
    }}
            
    ${this.props().filter { !it.isEMPTY() && it.type().name().equals("list", true) && it.type().generics().first().type() !is EntityI<*> }.joinSurroundIfNotEmptyToString(",$nL$tab$tab") {
        """
        if (this.${this.name()
            .lowercase(Locale.getDefault())}.${it.name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} == null) {
            this.${this.name()
            .lowercase(Locale.getDefault())}.${it.name()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }} = [];
        }"""
    }}
    
        this.${c.n(this, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.checkRoute(this.${this.name().lowercase(Locale.getDefault())})
    
        this.form = new ${c.n(angular.forms.FormGroup)}({ 
            ${this.props().filter { !it.isEMPTY() && it.type().name() !in arrayOf("boolean", "date", "string") }.joinSurroundIfNotEmptyToString(",$nL$tab$tab$tab") {
                """${this.name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${it.name().toCamelCase()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} : new ${c.n(angular.forms.FormControl)}<${if(it.type().name().equals("list", true)) {c.n(it.type().generics().first().type(), AngularDerivedType.ApiBase) + "[]"} else {c.n(it.type(), AngularDerivedType.ApiBase)}}>({value: this.${this.name()
                    .lowercase(Locale.getDefault())}.${it.name()
                    .replaceFirstChar { it.lowercase(Locale.getDefault()) }}, disabled: this.isDisabled})"""
            }}
        })  
        
        if (this.${c.n(this, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.isEdit) {
            this.${c.n(this, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getEditData().subscribe((data) => {
                Object.assign(this.${this.name().lowercase(Locale.getDefault())}, data);
                setTimeout(() => {
                    this.loadIxOption();
                }, 100)
            })
        } else if (this.${c.n(this, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.isView) {
            this.${c.n(this, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getSpecificData().subscribe((data) => {
                this.${c.n(this, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getSpecificViewData(this.${this.name().lowercase(Locale.getDefault())})
                setTimeout(() => {
                    this.loadIxOption();
                }, 100)
            })
        } else {  
            setTimeout(() => {
                this.loadIxOption();
            }, 100)
        }
    
        this.${c.n(this, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.initObservable();
    }"""
}

fun <T : ItemI<*>> T.toAngularInitOption(c: GenerationContext, elementType: TypeI<*>): String {
    return """
        this.${c.n(elementType, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getData().subscribe((data) => {
            this.${c.n(this.parent(), AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.option${c.n(elementType, AngularDerivedType.DataService).replace(AngularDerivedType.DataService, "")}  = data;

            this.${c.n(elementType, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.loadElementFromListItem().subscribe((listItem) => {
                if (JSON.stringify(this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().lowercase(Locale.getDefault())} ) !== '{}' && JSON.stringify(this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().lowercase(Locale.getDefault())} ) !== '[]'
                && (JSON.stringify(listItem) === '{}' || JSON.stringify(listItem) === '[]')) {
                    this.${c.n(elementType, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.saveListItemData(this.${c.n(elementType, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.itemName,
                        this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().lowercase(Locale.getDefault())} )
                }
            })
  
            setTimeout(() => {
                this.${c.n(this.parent(), AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.option${c.n(elementType, AngularDerivedType.DataService).replace(AngularDerivedType.DataService, "")}.forEach((option, index) => {
                    this.${c.n(elementType, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.loadElementFromListItem().subscribe((listItem) => {
                        if (JSON.stringify(listItem).toLowerCase().includes(JSON.stringify(option).toLowerCase())
                            && (JSON.stringify(listItem) !== '{}' || JSON.stringify(listItem) !== '[]')) {
                            this.selectedIndices${c.n(elementType, AngularDerivedType.DataService).replace(AngularDerivedType.DataService, "")} = index.toString();
                            this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().lowercase(Locale.getDefault())} = option;
                        } else if ((JSON.stringify(listItem) === '{}' || JSON.stringify(listItem) === '[]')) {
                            this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().lowercase(Locale.getDefault())} = listItem;
                        }
                    })
                });
            }, 100)
        })$nL"""
}

fun <T : ItemI<*>> T.toAngularInitOptionMultiple(c: GenerationContext, elementType: TypeI<*>): String {
    return """
        this.${c.n(elementType, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getData().subscribe((data) => {
            this.${c.n(this.parent(), AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.option${c.n(elementType, AngularDerivedType.DataService).replace(AngularDerivedType.DataService, "")}  = data;
            this.${c.n(elementType, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.loadMultipleElementFromListItem().subscribe((listItem) => {
                if (JSON.stringify(this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().lowercase(Locale.getDefault())}) !== '{}' && JSON.stringify(this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().lowercase(Locale.getDefault())}) !== '[]'
                && listItem.length < this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().lowercase(Locale.getDefault())}.length) {
                    this.${c.n(elementType, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.saveMultipleListItemData(this.${c.n(elementType, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.itemName, this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().lowercase(Locale.getDefault())});
                }
            })
            setTimeout(() => {
                this.${c.n(this.parent(), AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.option${c.n(elementType, AngularDerivedType.DataService).replace(AngularDerivedType.DataService, "")}.forEach((option, index) => {
                    this.multipleSelectedIndices${c.n(elementType, AngularDerivedType.DataService).replace(AngularDerivedType.DataService, "")} = [];
                    this.${c.n(elementType, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.loadMultipleElementFromListItem().subscribe((listItem) => {
                        if ((JSON.stringify(listItem).toLowerCase().includes(JSON.stringify(option).toLowerCase())
                            && (JSON.stringify(listItem) !== '{}' || JSON.stringify(listItem) !== '[]'))) {
                            this.multipleSelectedIndices${c.n(elementType, AngularDerivedType.DataService).replace(AngularDerivedType.DataService, "")}.push(index.toString());
                            this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().lowercase(Locale.getDefault())} = listItem.filter((value) => {
                                return JSON.stringify(data).includes(JSON.stringify(value));
                            });
                        } else if ((JSON.stringify(listItem) === '{}' || JSON.stringify(listItem) === '[]')) {
                            this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().lowercase(Locale.getDefault())} = listItem;
                        }
                    })
                });
            }, 100)

        })"""
}

fun <T : ItemI<*>> T.toAngularEmptyProps(c: GenerationContext, indent: String, elementType: TypeI<*>): String {
    return """${indent}if (this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()} === undefined) {
            this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()} = new ${c.n(elementType, AngularDerivedType.ApiBase)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}();
        }"""
}

fun <T : ItemI<*>> T.toAngularEmptyPropsValues(c: GenerationContext, indent: String, elementType: TypeI<*>): String {
    return """${indent}if (this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()} === undefined) {
            this.${c.n(elementType, AngularDerivedType.DataService).toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}.loadElementFromListItem('${c.n(elementType, AngularDerivedType.DataService).replace(AngularDerivedType.DataService, "").lowercase(Locale.getDefault())}').subscribe((${this.name().toCamelCase()}) => {
                if (${this.name().toCamelCase()} !== null) {
                    this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()} = ${this.name().toCamelCase()} || new ${c.n(elementType, AngularDerivedType.ApiBase)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}();
                } else {
                    this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()} = new ${c.n(elementType, AngularDerivedType.ApiBase)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}();
                }
            })
        }"""
}

fun <T : TypeI<*>> T.toAngularModuleTabElementEntity(): String =
    "'${if(this.parent().name().equals(this.name())) {this.name()} else {this.parent().name() + this.name()}}'"

fun <T : TypeI<*>> T.toAngularModuleTabElementValue(): String =
    ", '${this.name()}'"

fun <T : ItemI<*>> T.toAngularGenerateComponentPart(c: GenerationContext, selectorName: String, element: String, type: String, hasProviders: Boolean, hasClass: Boolean): String =
    """@${c.n(angular.core.Component)}({
  selector: '${selectorName}-${this.name().lowercase(Locale.getDefault())}${(element == "entity").then("-${type}")}',
  templateUrl: './${this.name().lowercase(Locale.getDefault())}-${element}${type.isNotEmpty().then("-${type}")}.component.html',
  styleUrls: ['./${this.name().lowercase(Locale.getDefault())}-${element}${type.isNotEmpty().then("-${type}")}.component.scss'],
  ${this.checkProvider(c, hasProviders, hasClass).trim()}
})
"""

fun <T : ItemI<*>> T.checkProvider(c: GenerationContext, hasProviders: Boolean, hasClass: Boolean): String {
    val text: String = if (hasProviders && !hasClass) {
        "providers: [${c.n(this, AngularDerivedType.ViewService)}]"
    } else if (hasProviders && hasClass) {
        "providers: [{provide: ${c.n(service.template.DataService)}, useClass: ${c.n(this, AngularDerivedType.DataService)}}]"
    } else {
        ""
    }
    return text
}

fun <T : ItemI<*>> T.toTypeScriptEntityProp(c: GenerationContext, indent: String): String {
    return """${indent}${this.name().lowercase(Locale.getDefault())}: ${c.n(this, AngularDerivedType.ApiBase)};$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptFormProp(c: GenerationContext, indent: String): String {
    return """${indent}@${c.n(angular.core.Input)}() ${this.name().lowercase(Locale.getDefault())}: ${c.n(this, AngularDerivedType.ApiBase)};$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptEntityPropInit(c: GenerationContext, indent: String): String {
    return """${indent}${this.name().lowercase(Locale.getDefault())}: ${c.n(this, AngularDerivedType.ApiBase)} = new ${c.n(this, AngularDerivedType.ApiBase)}();$nL"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationEntities(c: GenerationContext, indent: String): String {
    return """$indent${c.n(this, AngularDerivedType.EntityViewComponent)},
$indent${c.n(this, AngularDerivedType.EntityListComponent)},
$indent${c.n(this, AngularDerivedType.EntityFormComponent)},"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationAggregateEntities(c: GenerationContext, indent: String): String {
    return """$indent${c.n(this, AngularDerivedType.EntityAggregateViewComponent)},"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationValuesImport(c: GenerationContext, indent: String): String {
    return """$indent${c.n(this, AngularDerivedType.ValueViewComponent)},
$indent${c.n(this, AngularDerivedType.ValueListComponent)},
$indent${c.n(this, AngularDerivedType.ValueFormComponent)},"""
}

fun <T : ItemI<*>> T.toAngularModuleExportViews(c: GenerationContext, indent: String): String {
    return """$indent${c.n(this, AngularDerivedType.EntityFormComponent)},"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationBasics(c: GenerationContext, indent: String): String {
    return """$indent${c.n(this, AngularDerivedType.BasicComponent)},"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationEnums(c: GenerationContext, indent: String): String {
    return """$indent${c.n(this, AngularDerivedType.EnumComponent)},"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationValues(c: GenerationContext, indent: String): String {
    return """$indent${c.n(this, AngularDerivedType.ValueFormComponent)},"""
}

fun <T : EntityI<*>> T.toAngularEntityModulePath(c: GenerationContext, indent: String): String {
    return """$indent{ path: '${this.name().lowercase(Locale.getDefault())}', component: ${c.n(this, AngularDerivedType.EntityListComponent)} },
$indent{ path: '${this.name().lowercase(Locale.getDefault())}/new', component: ${c.n(this, AngularDerivedType.EntityViewComponent)} },
$indent{ path: '${this.name().lowercase(Locale.getDefault())}/edit/:id', component: ${c.n(this, AngularDerivedType.EntityViewComponent)} },
$indent{ path: '${this.name().lowercase(Locale.getDefault())}/search', component: ${c.n(this, AngularDerivedType.EntityListComponent)} },"""
}

fun <T : EntityI<*>> T.toAngularEntityViewModulePath(c: GenerationContext, indent: String): String {
    return """$indent{ path: '${this.name().lowercase(Locale.getDefault())}/view/:id', component: ${c.n(this, AngularDerivedType.EntityViewComponent)} },  """
}

fun <T : ItemI<*>> T.toAngularAggregateEntityModulePath(c: GenerationContext, indent: String): String {
    return """$indent{ path: '${this.name().lowercase(Locale.getDefault())}/view', component: ${c.n(this, AngularDerivedType.EntityAggregateViewComponent)} },
    { path: '${this.name().lowercase(Locale.getDefault())}/view/edit/:id', component: ${c.n(this, AngularDerivedType.EntityViewComponent)} },
    """
}

fun <T : EntityI<*>> T.toAngularAggregateEntityPropsModulePath(c: GenerationContext, indent: String): String {
    return """$indent${this.props().filter { !it.isEMPTY() && (it.type() is EntityI<*> || (it.type().name().equals("list", true) && it.type().generics().first().type() is EntityI<*>)) }.joinSurroundIfNotEmptyToString(nL + indent) { """{ path: '${this.name().lowercase(Locale.getDefault())}/view/${this.name().lowercase(Locale.getDefault())}${if(it.type().generics().isEmpty()) {
        it.type().name().lowercase(Locale.getDefault())
    } else {it.type().generics().first().type().name().lowercase(Locale.getDefault())}}', component: ${if(it.type().generics().isEmpty()) {c.n(it.type(), AngularDerivedType.EntityViewComponent)} else {c.n(it.type().generics().first().type(), AngularDerivedType.EntityListComponent)}} },""" }}

$indent${this.props().filter { !it.isEMPTY() && (it.type() is EntityI<*> || (it.type().name().equals("list", true) && it.type().generics().first().type() is EntityI<*>)) }.joinSurroundIfNotEmptyToString(nL + indent) { """{ path: '${this.name().lowercase(Locale.getDefault())}/view/${this.name().lowercase(Locale.getDefault())}${if(it.type().generics().isEmpty()) {
        it.type().name().lowercase(Locale.getDefault())
    } else {it.type().generics().first().type().name().lowercase(Locale.getDefault())}}/edit/:id', component: ${if(it.type().generics().isEmpty()) {c.n(it.type(), AngularDerivedType.EntityViewComponent)} else {c.n(it.type().generics().first().type(), AngularDerivedType.EntityViewComponent)}} },""" }}
        
$indent${this.props().filter { !it.isEMPTY() && (it.type() is ValuesI<*> || (it.type().name().equals("list", true) && it.type().generics().first().type() is ValuesI<*>)) }.joinSurroundIfNotEmptyToString(nL + indent) { """{ path: '${this.name().lowercase(Locale.getDefault())}/view/${this.name().lowercase(Locale.getDefault())}${if(it.type().generics().isEmpty()) {
        it.type().name().lowercase(Locale.getDefault())
    } else {it.type().generics().first().type().name().lowercase(Locale.getDefault())}}', component: ${if(it.type().generics().isEmpty()) {c.n(it.type(), AngularDerivedType.ValueViewComponent)} else {c.n(it.type().generics().first().type(), AngularDerivedType.ValueListComponent)}} },""" }}

$indent${this.props().filter { !it.isEMPTY() && (it.type() is ValuesI<*> || (it.type().name().equals("list", true) && it.type().generics().first().type() is ValuesI<*>)) }.joinSurroundIfNotEmptyToString(nL + indent) { """{ path: '${this.name().lowercase(Locale.getDefault())}/view/${this.name().lowercase(Locale.getDefault())}${if(it.type().generics().isEmpty()) {
        it.type().name().lowercase(Locale.getDefault())
    } else {it.type().generics().first().type().name().lowercase(Locale.getDefault())}}/edit/:id', component: ${if(it.type().generics().isEmpty()) {c.n(it.type(), AngularDerivedType.ValueViewComponent)} else {c.n(it.type().generics().first().type(), AngularDerivedType.ValueViewComponent)}} },""" }}"""
}

fun <T : ValuesI<*>> T.toAngularValueModulePath(c: GenerationContext, indent: String): String {
    return """$indent{ path: '${this.name().lowercase(Locale.getDefault())}', component: ${c.n(this, AngularDerivedType.ValueListComponent)} },
$indent{ path: '${this.name().lowercase(Locale.getDefault())}/new', component: ${c.n(this, AngularDerivedType.ValueViewComponent)} },
$indent{ path: '${this.name().lowercase(Locale.getDefault())}/edit/:id', component: ${c.n(this, AngularDerivedType.ValueViewComponent)} },
$indent{ path: '${this.name().lowercase(Locale.getDefault())}/view/:id', component: ${c.n(this, AngularDerivedType.ValueViewComponent)} },
$indent{ path: '${this.name().lowercase(Locale.getDefault())}/search', component: ${c.n(this, AngularDerivedType.ValueListComponent)} },"""
}

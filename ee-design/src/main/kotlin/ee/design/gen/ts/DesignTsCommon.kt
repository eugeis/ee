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

fun <T : ItemI<*>> T.toAngularConstructorDataService(c: GenerationContext, indent: String, needLocation: Boolean): String {
    return """${indent}constructor(public ${c.n(this, AngularDerivedType.DataService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}: ${c.n(this, AngularDerivedType.DataService)} ${needLocation.then { """, private _location: ${c.n(angular.common.Location)}""" }}, private _route: ${c.n(angular.router.ActivatedRoute)}) {}$nL"""
}

fun <T : ItemI<*>> T.toAngularPropOnConstructor(c: GenerationContext): String {
    return """${tab + tab}public ${c.n(this, AngularDerivedType.DataService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}: ${c.n(this, AngularDerivedType.DataService)}, $nL"""
}

fun <T : ItemI<*>> T.toAngularFunctionBindTo(c: GenerationContext, propName: String, parent: CompilationUnitI<*>): String {
    return """${tab}bindTo${c.n(this, AngularDerivedType.ApiBase).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}({ detail: [id] }: CustomEvent<string[]>) {
        this.${parent.name().lowercase(Locale.getDefault())}.${propName.lowercase(Locale.getDefault())} = this.${c.n(parent, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.option${c.n(this, AngularDerivedType.ApiBase).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}[id];
    }$nL"""
}

fun <T : ItemI<*>> T.toAngularFunctionBindToBasic(c: GenerationContext, propName: String, parent: CompilationUnitI<*>): String {
    return """${tab}bindTo${c.n(this, AngularDerivedType.ApiBase).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}({ detail: [id] }: CustomEvent<string[]>) {
        this.${parent.name().lowercase(Locale.getDefault())}.${propName.lowercase(Locale.getDefault())} = this.option${c.n(this, AngularDerivedType.ApiBase).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}[id];
    }$nL"""
}

fun <T : ItemI<*>> T.toAngularFunctionBindToMultiple(c: GenerationContext, propName: String, parent: CompilationUnitI<*>): String {
    return """${tab}bindTo${c.n(this, AngularDerivedType.ApiBase).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(index: CustomEvent<string[]>) {
        const temp = [];
        index.detail.forEach(id => {
            temp.push(this.${c.n(parent, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.option${c.n(this, AngularDerivedType.ApiBase).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}[id]);
        });
        this.${parent.name().lowercase(Locale.getDefault())}.${propName.lowercase(Locale.getDefault())} = temp;
    }$nL"""
}

fun <T : ItemI<*>> T.toAngularFunctionBindToMultipleBasic(c: GenerationContext, propName: String, parent: CompilationUnitI<*>): String {
    return """${tab}bindTo${c.n(this, AngularDerivedType.ApiBase).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(index: CustomEvent<string[]>) {
        const temp = [];
        index.detail.forEach(id => {
            temp.push(this.option${c.n(this, AngularDerivedType.ApiBase).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}[id]);
        });
        this.${parent.name().lowercase(Locale.getDefault())}.${propName.lowercase(Locale.getDefault())} = temp;
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

fun <T : ItemI<*>> T.toAngularViewOnInit(c: GenerationContext, indent: String): String {
    return """${indent}ng${c.n(angular.core.OnInit)}(): void {
        this.${this.name().lowercase(Locale.getDefault())} = this.${c.n(this, AngularDerivedType.DataService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getFirst();
        this.${c.n(this, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.checkRoute(this.${this.name().lowercase(Locale.getDefault())});
    
        if (this._location.path().includes('?') && this.isSpecificView) {
            this._location.path().split('?')[1].split('&').forEach(param => {
                const [key, value]= param.split('=');
                const decodedKey = decodeURIComponent(key.trim());
                this.decodedParams[decodedKey] = decodeURIComponent(value.trim());
            });
            
            this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.componentName = this.decodedParams['name '];
            this.tabElement = this.decodedParams['tabElement '].split(',').map(value => value.trim());
        }

        if(this.${c.n(this, AngularDerivedType.DataService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.componentName !== undefined && this.${c.n(this, AngularDerivedType.DataService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.componentName.length > 0) {
            this.isSpecificView = true;
            this.${c.n(this, AngularDerivedType.DataService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.changeMapToArray(
                this.${c.n(this, AngularDerivedType.DataService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.retrieveItemsFromCache()).forEach((data) => {
                if (JSON.stringify(this.${c.n(this, AngularDerivedType.DataService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getSpecificData()).includes(JSON.stringify(data))) {
                    Object.assign(this.${this.name().lowercase(Locale.getDefault())}, data)
                }
            })
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
    
        ${this.props().filter { !it.isEMPTY() && it.type().name().equals("list", true) }.joinSurroundIfNotEmptyToString(",$nL$tab$tab") {
        """
        if (this.${this.name()
                .lowercase(Locale.getDefault())}.${it.name()
                .replaceFirstChar { it.lowercase(Locale.getDefault()) }} == null) {
            this.${this.name()
                .lowercase(Locale.getDefault())}.${it.name()
                .replaceFirstChar { it.lowercase(Locale.getDefault()) }} = JSON.parse(localStorage.getItem('list-item-${c.n(it.type().generics().first().type(), AngularDerivedType.ApiBase)
            .lowercase(Locale.getDefault())}')) || [];
        }"""
        }}
    
        this.form = new ${c.n(angular.forms.FormGroup)}({ 
            ${this.props().filter { !it.isEMPTY() && it.type().name() !in arrayOf("boolean", "date", "string") }.joinSurroundIfNotEmptyToString(",$nL$tab$tab$tab") {
                """${this.name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}${it.name().toCamelCase()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} : new ${c.n(angular.forms.FormControl)}<${if(it.type().name().equals("list", true)) {c.n(it.type().generics().first().type(), AngularDerivedType.ApiBase) + "[]"} else {c.n(it.type(), AngularDerivedType.ApiBase)}}>({value: this.${this.name()
                    .lowercase(Locale.getDefault())}.${it.name()
                    .replaceFirstChar { it.lowercase(Locale.getDefault()) }}, disabled: this.isDisabled})"""
            }}
        })
    
${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "string") }.joinSurroundIfNotEmptyToString(tab + tab) {
    when(it.type()) {
        is EntityI<*>, is ValuesI<*> -> it.toAngularInitOption(c, it.type())
        else -> when(it.type().name().lowercase(Locale.getDefault())) {
            "list" -> when(it.type().generics().first().type()) {
                is EntityI<*>, is ValuesI<*> -> it.toAngularInitOption(c, it.type().generics().first().type())
                is EnumTypeI<*> -> "this.${c.n(this, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.option${c.n(it.type().generics().first().type(), AngularDerivedType.ApiBase).toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} = this.${c.n(this, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.loadEnumElement(${c.n(it.type().generics().first().type(), AngularDerivedType.ApiBase)
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }});"
                else -> ""
            }
            else -> ""
        }
    }
}}
    
        this.${c.n(this, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.initObservable();
    }"""
}

fun <T : ItemI<*>> T.toAngularInitOption(c: GenerationContext, elementType: TypeI<*>): String {
    return """this.${c.n(this.parent(), AngularDerivedType.DataService)
        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.option${c.n(elementType, AngularDerivedType.DataService).replace(AngularDerivedType.DataService, "")} 
            = this.${c.n(elementType, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.changeMapToArray(this.${c.n(elementType, AngularDerivedType.DataService).replaceFirstChar { it.lowercase(Locale.getDefault()) }}.retrieveItemsFromCache()); $nL"""
}

fun <T : ItemI<*>> T.toAngularEmptyProps(c: GenerationContext, indent: String, elementType: TypeI<*>): String {
    return """${indent}if (this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()} === undefined) {
            this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()} = new ${c.n(elementType, AngularDerivedType.ApiBase)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}();
        }"""
}

fun <T : ItemI<*>> T.toAngularEmptyPropsValues(c: GenerationContext, indent: String, elementType: TypeI<*>): String {
    return """${indent}if (this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()} === undefined) {
            this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()} = this.${c.n(elementType, AngularDerivedType.DataService).toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}.loadElementFrom${c.n(elementType, AngularDerivedType.ApiBase).toCamelCase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}() || new ${c.n(elementType, AngularDerivedType.ApiBase)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}();
        }"""
}

fun <T : TypeI<*>> T.toAngularModuleTabElementEntity(): String =
    "'${this.name()}'"

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

fun <T : ItemI<*>> T.toAngularEntityModulePath(c: GenerationContext, indent: String): String {
    return """$indent{ path: '${this.name().lowercase(Locale.getDefault())}', component: ${c.n(this, AngularDerivedType.EntityListComponent)} },
$indent{ path: '${this.name().lowercase(Locale.getDefault())}/new', component: ${c.n(this, AngularDerivedType.EntityViewComponent)} },
$indent{ path: '${this.name().lowercase(Locale.getDefault())}/edit/:id', component: ${c.n(this, AngularDerivedType.EntityViewComponent)} },
$indent{ path: '${this.name().lowercase(Locale.getDefault())}/search', component: ${c.n(this, AngularDerivedType.EntityListComponent)} },"""
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

fun <T : ItemI<*>> T.toAngularValueModulePath(c: GenerationContext, indent: String): String {
    return """$indent{ path: '${this.name().lowercase(Locale.getDefault())}', component: ${c.n(this, AngularDerivedType.ValueListComponent)} },
$indent{ path: '${this.name().lowercase(Locale.getDefault())}/new', component: ${c.n(this, AngularDerivedType.ValueViewComponent)} },
$indent{ path: '${this.name().lowercase(Locale.getDefault())}/edit/:id', component: ${c.n(this, AngularDerivedType.ValueViewComponent)} },
$indent{ path: '${this.name().lowercase(Locale.getDefault())}/search', component: ${c.n(this, AngularDerivedType.ValueListComponent)} },"""
}

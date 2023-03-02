import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.angular
import ee.lang.gen.ts.rxjs
import ee.lang.gen.ts.service

fun <T : ItemI<*>> T.toAngularConstructorDataService(c: GenerationContext, indent: String): String {
    return """${indent}constructor(public ${c.n(this, AngularDerivedType.DataService).decapitalize()}: ${c.n(this, AngularDerivedType.DataService)}) {}$nL"""
}

fun <T : ItemI<*>> T.toAngularPropOnConstructor(c: GenerationContext): String {
    return """${tab + tab}public ${c.n(this, AngularDerivedType.DataService).decapitalize()}: ${c.n(this, AngularDerivedType.DataService)}, $nL"""
}

fun <T : TypeI<*>> T.toAngularControlService(c: GenerationContext): String {
    return """
    control${c.n(this).toCamelCase().capitalize()} = new ${c.n(angular.forms.FormControl)}<${c.n(this).toCamelCase().capitalize()}>(new ${c.n(this).toCamelCase().capitalize()}());
    option${c.n(this).toCamelCase().capitalize()}: Array<${c.n(this).toCamelCase().capitalize()}>;
    filteredOptions${c.n(this).toCamelCase().capitalize()}: ${c.n(rxjs.empty.Observable)}<${c.n(this).toCamelCase().capitalize()}[]>;$nL"""
}

fun <T : TypeI<*>> T.toAngularControlServiceFunctions(c: GenerationContext, key: AttributeI<*>): String {
    return """
    display${c.n(this).toCamelCase().capitalize()}(${c.n(this).toCamelCase()}: ${c.n(this).toCamelCase().capitalize()}): string {
        return ${c.n(this).toCamelCase()} ? ${c.n(this).toCamelCase()}.${key.name()} : '';
    }
    
    filter${c.n(this).toCamelCase().capitalize()}(name: string, array: Array<${c.n(this).toCamelCase().capitalize()}>): ${c.n(this).toCamelCase().capitalize()}[] {
        return array.filter(option => option.${key.name()}.toLowerCase().includes(name.toLowerCase()));
    }$nL"""
}

fun <T : TypeI<*>> T.toAngularInitObservable(c: GenerationContext, key: AttributeI<*>): String {
    return """
        this.filteredOptions${c.n(this).toCamelCase().capitalize()} = this.control${c.n(this).toCamelCase().capitalize()}.valueChanges.pipe(
            ${c.n(rxjs.operators.startWith)}(''),
            ${c.n(rxjs.operators.map)}((value: ${c.n(this).toCamelCase().capitalize()}) => {
                const name = typeof value === 'string' ? value : value.${key.name()};
                return name ?
                    this.filter${c.n(this).toCamelCase().capitalize()}(name as string, this.option${c.n(this).toCamelCase().capitalize()})
                    : this.option${c.n(this).toCamelCase().capitalize()}.slice();
            }),
        );$nL"""
}

fun <T : ItemI<*>> T.toAngularViewOnInit(c: GenerationContext, indent: String): String {
    return """${indent}ng${c.n(angular.core.OnInit)}(): void {
        this.${c.n(this).toLowerCase()} = this.${c.n(this, AngularDerivedType.DataService).decapitalize()}.getFirst();
        this.${c.n(this, AngularDerivedType.DataService).decapitalize()}.checkRoute(this.${c.n(this).toLowerCase()});
    }"""
}

fun <T : CompilationUnitI<*>> T.toAngularFormOnInit(c: GenerationContext, indent: String): String {

    return """${indent}ng${c.n(angular.core.OnInit)}(): void {
        ${this.props().filter { it.type() is BasicI<*> || it.type() is EntityI<*> || it.type() is ValuesI<*> }.joinSurroundIfNotEmptyToString(nL + tab) {
        it.toAngularEmptyProps(c, indent, it.type())
    }.trim()}
    
${this.props().filter { it.type() !is EnumTypeI<*> && it.type().name() !in arrayOf("boolean", "date", "list", "string") }.joinSurroundIfNotEmptyToString("") {
when(it.type()) {
    is EntityI<*>, is ValuesI<*> -> it.toAngularInitOption(c, it.type().name())
    else -> ""
}
    }}
    
        this.${c.n(this, AngularDerivedType.DataService).decapitalize()}.initObservable();
    }"""
}

fun <T : ItemI<*>> T.toAngularInitOption(c: GenerationContext, elementType: String): String {
    return """${tab + tab}this.${c.n(this.parent(), AngularDerivedType.DataService).decapitalize()}.option${elementType.capitalize()} = this.${elementType.decapitalize()}DataService.changeMapToArray(this.${elementType.decapitalize()}DataService.retrieveItemsFromCache()); $nL"""
}

fun <T : ItemI<*>> T.toAngularEmptyProps(c: GenerationContext, indent: String, elementType: TypeI<*>): String {
    return """${indent}if (this.${this.parent().name().toLowerCase()}.${this.name().toCamelCase()} === undefined) {
            this.${this.parent().name().toLowerCase()}.${this.name().toCamelCase()} = new ${c.n(elementType).capitalize()}();
        }"""
}

fun <T : TypeI<*>> T.toAngularModuleTabElement(): String =
    "'${this.name()}'"

fun <T : ItemI<*>> T.toAngularGenerateComponentPart(c: GenerationContext, element: String, type: String, hasProviders: Boolean, hasClass: Boolean): String =
    """@${c.n(angular.core.Component)}({
  selector: 'app-${this.name().toLowerCase()}${(element == "entity").then("-${type}")}',
  templateUrl: './${this.name().toLowerCase()}-${element}${type.isNotEmpty().then("-${type}")}.component.html',
  styleUrls: ['./${this.name().toLowerCase()}-${element}${type.isNotEmpty().then("-${type}")}.component.scss'],
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
    return """${indent}${this.name().toLowerCase()}: ${c.n(this)};$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptFormProp(c: GenerationContext, indent: String): String {
    return """${indent}@${c.n(angular.core.Input)}() ${this.name().toLowerCase()}: ${c.n(this)};$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptEntityPropInit(c: GenerationContext, indent: String): String {
    return """${indent}${this.name().toLowerCase()}: ${c.n(this)} = new ${c.n(this)}();$nL"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationEntities(c: GenerationContext, indent: String): String {
    return """$indent${c.n(this, AngularDerivedType.ViewComponent)},
$indent${c.n(this, AngularDerivedType.ListComponent)},
$indent${c.n(this, AngularDerivedType.FormComponent)},"""
}

fun <T : ItemI<*>> T.toAngularModuleExportViews(c: GenerationContext, indent: String): String {
    return """$indent${c.n(this, AngularDerivedType.FormComponent)},"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationBasics(c: GenerationContext, indent: String): String {
    return """$indent${c.n(this, AngularDerivedType.BasicComponent)},"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationEnums(c: GenerationContext, indent: String): String {
    return """$indent${c.n(this, AngularDerivedType.EnumComponent)},"""
}

fun <T : ItemI<*>> T.toAngularModulePath(c: GenerationContext, indent: String): String {
    return """$indent{ path: '${this.name().toLowerCase()}', component: ${c.n(this, AngularDerivedType.ListComponent)} },
$indent{ path: '${this.name().toLowerCase()}/new', component: ${c.n(this, AngularDerivedType.ViewComponent)} },
$indent{ path: '${this.name().toLowerCase()}/edit/:id', component: ${c.n(this, AngularDerivedType.ViewComponent)} },
$indent{ path: '${this.name().toLowerCase()}/search', component: ${c.n(this, AngularDerivedType.ListComponent)} }"""
}

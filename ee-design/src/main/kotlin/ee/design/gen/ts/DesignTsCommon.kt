import ee.common.ext.ifElse
import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.lang.*

fun <T : ItemI<*>> T.toAngularConstructorDataService(indent: String): String {
    return """${indent}constructor(public ${this.name().toLowerCase()}DataService: ${this.name()}DataService) {}$nL"""
}

fun <T : ItemI<*>> T.toAngularViewOnInit(c: GenerationContext, indent: String): String {
    return """${indent}ngOnInit(): void {
        this.${this.name().toLowerCase()} = this.${this.name().toLowerCase()}DataService.getFirst();
        this.${this.name().toLowerCase()}DataService.checkRoute(this.${this.name().toLowerCase()});
    }"""
}

fun <T : CompilationUnitI<*>> T.toAngularFormOnInit(c: GenerationContext, indent: String): String {

    return """${indent}ngOnInit(): void {
        ${this.props().filter { it.type() is BasicI<*> || it.type() is EntityI<*> }.joinSurroundIfNotEmptyToString(nL + tab) {
        it.toAngularEmptyProps(c, indent, it.type())
    }.trim()}
    }"""
}

fun <T : ItemI<*>> T.toAngularEmptyProps(c: GenerationContext, indent: String, elementType: TypeI<*>): String {
    return """${indent}if (this.${this.parent().name().toLowerCase()}.${this.name().toCamelCase()} === undefined) {
            this.${this.parent().name().toLowerCase()}.${this.name().toCamelCase()} = new ${c.n(elementType).capitalize()}();
        }"""
}

fun <T : TypeI<*>> T.toAngularModuleTabElement(): String =
    "'${this.name()}'"

fun <T : ItemI<*>> T.toAngularGenerateComponentPart(c: GenerationContext, element: String, type: String, hasProviders: Boolean, hasClass: Boolean): String =
    """@${c.n("Component")}({
  selector: 'app-${this.name().toLowerCase()}${(element == "entity").then("-${type}")}',
  templateUrl: './${this.name().toLowerCase()}-${element}${type.isNotEmpty().then("-${type}")}.component.html',
  styleUrls: ['./${this.name().toLowerCase()}-${element}${type.isNotEmpty().then("-${type}")}.component.scss'],
  ${this.checkProvider(hasProviders, hasClass).trim()}
})
"""

fun <T : ItemI<*>> T.checkProvider(hasProviders: Boolean, hasClass: Boolean): String {
    val text: String = if (hasProviders && !hasClass) {
        "providers: [${this.name().capitalize()}ViewService]"
    } else if (hasProviders && hasClass) {
        "providers: [{provide: TableDataService, useClass: ${this.name().capitalize()}DataService}]"
    } else {
        ""
    }
    return text
}

fun <T : ItemI<*>> T.toAngularGenerateEnumElement(c: GenerationContext, indent: String, element: T): String {
    return """${indent}${c.n(this).toLowerCase()}Enum = this.${element.name().toLowerCase()}DataService.loadEnumElement(${c.n(this).capitalize()});"""
}

fun <T : ItemI<*>> T.toTypeScriptEntityProp(c: GenerationContext, indent: String): String {
    return """${indent}${this.name().toLowerCase()}: ${c.n(this)};$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptFormProp(c: GenerationContext, indent: String): String {
    return """${indent}@${c.n("Input")}() ${this.name().toLowerCase()}: ${c.n(this)};$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptEntityPropInit(c: GenerationContext, indent: String): String {
    return """${indent}${this.name().toLowerCase()}: ${c.n(this)} = new ${c.n(this)}();$nL"""
}

fun <T : ItemI<*>> T.toAngularModuleImportEntities(): String {
    return """import {${this.name().capitalize()}ViewComponent} from '@${this.parent().parent().name().decapitalize()}/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/components/view/${this.name().toLowerCase()}-entity-view.component';
import {${this.name().capitalize()}ListComponent} from '@${this.parent().parent().name().decapitalize()}/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/components/list/${this.name().toLowerCase()}-entity-list.component';
import {${this.name().capitalize()}FormComponent} from '@${this.parent().parent().name().decapitalize()}/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/components/form/${this.name().toLowerCase()}-entity-form.component';"""
}

fun <T : ItemI<*>> T.toAngularModuleImportEntitiesRouting(): String {
    return """import {${this.name().capitalize()}ViewComponent} from '@${this.parent().parent().name().decapitalize()}/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/components/view/${this.name().toLowerCase()}-entity-view.component';
import {${this.name().capitalize()}ListComponent} from '@${this.parent().parent().name().decapitalize()}/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/components/list/${this.name().toLowerCase()}-entity-list.component';"""
}

fun <T : ItemI<*>> T.toAngularModuleImportBasics(): String {
    return """import {${this.name().capitalize()}Component} from '@${this.parent().parent().name().decapitalize()}/${this.parent().name().toLowerCase()}/basics/${this.name().toLowerCase()}/${this.name().toLowerCase()}-basic.component';"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationEntities(indent: String): String {
    return """$indent${this.name().capitalize()}ViewComponent,
$indent${this.name().capitalize()}ListComponent,
$indent${this.name().capitalize()}FormComponent"""
}

fun <T : ItemI<*>> T.toAngularModuleExportViews(indent: String): String {
    return """$indent${this.name().capitalize()}FormComponent"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationBasics(indent: String): String {
    return """$indent${this.name().capitalize()}Component"""
}

fun <T : ItemI<*>> T.toAngularModulePath(indent: String): String {
    return """$indent{ path: '${this.name().toLowerCase()}', component: ${this.name().capitalize()}ListComponent },
$indent{ path: '${this.name().toLowerCase()}/new', component: ${this.name().capitalize()}ViewComponent },
$indent{ path: '${this.name().toLowerCase()}/edit/:id', component: ${this.name().capitalize()}ViewComponent }"""
}


fun <T : ItemI<*>> T.toAngularModuleImportServices(): String {
    return """import {${this.name()}ViewService} from '@${this.parent().name().decapitalize()}/${this.name()}/service/${this.name().toLowerCase()}-module-view.service';$nL"""
}

fun <T : ItemI<*>> T.toAngularModuleInputElement(c: GenerationContext, name: String, indent: String): String {
    return """${indent}@${c.n("Input")}() $name = '${this.name()}Component';$nL"""
}

fun <T : ItemI<*>> T.toAngularModuleConstructor(indent: String): String {
    return """${indent}constructor(public ${this.name().toLowerCase()}ViewService: ${this.name()}ViewService) {}$nL"""
}

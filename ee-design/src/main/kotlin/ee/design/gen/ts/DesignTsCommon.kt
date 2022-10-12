import ee.common.ext.joinSurroundIfNotEmptyToString
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

fun <T : ItemI<*>> T.toAngularModuleGenerateComponentPart(c: GenerationContext): String =
    """@${c.n("Component")}({
  selector: 'app-${this.name().toLowerCase()}',
  templateUrl: './${this.name().toLowerCase()}-module-view.component.html',
  styleUrls: ['./${this.name().toLowerCase()}-module-view.component.scss'],
  providers: [${this.name().capitalize()}ViewService]
})
"""

fun <T : ItemI<*>> T.toAngularEntityGenerateComponentPart(c: GenerationContext, type: String): String =
    """@${c.n("Component")}({
  selector: 'app-${c.n(this).toLowerCase()}-${type}',
  templateUrl: './${c.n(this).toLowerCase()}-entity-${type}.component.html',
  styleUrls: ['./${c.n(this).toLowerCase()}-entity-${type}.component.scss'],
  providers: [{provide: TableDataService, useClass: ${c.n(this).capitalize()}DataService}]
})
"""

fun <T : ItemI<*>> T.toAngularEntityGenerateFormComponentPart(c: GenerationContext): String =
    """@${c.n("Component")}({
  selector: 'app-${c.n(this).toLowerCase()}-form',
  templateUrl: './${c.n(this).toLowerCase()}-form.component.html',
  styleUrls: ['./${c.n(this).toLowerCase()}-form.component.scss']
})
"""

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
    return """import {${this.name().capitalize()}ViewComponent} from '@schkola/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/components/view/${this.name().toLowerCase()}-entity-view.component';
import {${this.name().capitalize()}ListComponent} from '@schkola/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/components/list/${this.name().toLowerCase()}-entity-list.component';
import {${this.name().capitalize()}FormComponent} from '@schkola/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/components/form/${this.name().toLowerCase()}-form.component';"""
}

fun <T : ItemI<*>> T.toAngularModuleImportEntitiesRouting(): String {
    return """import {${this.name().capitalize()}ViewComponent} from '@schkola/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/components/view/${this.name().toLowerCase()}-entity-view.component';
import {${this.name().capitalize()}ListComponent} from '@schkola/${this.parent().name().toLowerCase()}/${this.name().toLowerCase()}/components/list/${this.name().toLowerCase()}-entity-list.component';"""
}

fun <T : ItemI<*>> T.toAngularModuleImportBasics(): String {
    return """import {${this.name().capitalize()}Component} from '@schkola/${this.parent().name().toLowerCase()}/basics/${this.name().toLowerCase()}/${this.name().toLowerCase()}-basic.component';"""
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
    return """import {${this.name()}ViewService} from '../../service/${this.name().toLowerCase()}-module-view.service';$nL"""
}

fun <T : ItemI<*>> T.toAngularModuleInputElement(c: GenerationContext, name: String, indent: String): String {
    return """${indent}@${c.n("Input")}() $name = '${this.name()}Component';$nL"""
}

fun <T : ItemI<*>> T.toAngularModuleConstructor(indent: String): String {
    return """${indent}constructor(public ${this.name().toLowerCase()}ViewService: ${this.name()}ViewService) {}$nL"""
}

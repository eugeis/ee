import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*

fun <T : ItemI<*>> T.toAngularConstructorDataService(indent: String): String {
    return """${indent}constructor(public ${this.name().toLowerCase()}DataService: ${this.name()}DataService) {}$nL"""
}

fun <T : ItemI<*>> T.toAngularViewOnInit(c: GenerationContext, indent: String, basics: List<BasicI<*>>): String {
    val basicElements: MutableList<String> = ArrayList()
    basics.forEach { basicElements.add(it.name().capitalize()) }

    return """${indent}ngOnInit(): void {
        this.${this.name().toLowerCase()} = this.${this.name().toLowerCase()}DataService.getFirst();
        this.${this.name().toLowerCase()}DataService.checkRoute(this.${this.name().toLowerCase()});
    }"""
}

fun <T : CompilationUnitI<*>> T.toAngularFormOnInit(c: GenerationContext, indent: String, basics: List<BasicI<*>>, entities: List<EntityI<*>>): String {
    val includedElements: MutableList<String> = ArrayList()
    basics.forEach { includedElements.add(it.name().capitalize()) }
    entities.forEach { includedElements.add(it.name().capitalize()) }

    return """${indent}ngOnInit(): void {
        ${this.props().filter { it.type().name() in includedElements }.joinSurroundIfNotEmptyToString(nL + tab) {
        it.toAngularEmptyProps(c, indent, it.type())
    }.trim()}
    }"""
}

fun <T : ItemI<*>> T.toAngularEmptyProps(c: GenerationContext, indent: String, elementType: TypeI<*>): String {
    return """${indent}if (this.${this.parent().name().toLowerCase()}.${this.name().toCamelCase()} === undefined) {
            this.${this.parent().name().toLowerCase()}.${this.name().toCamelCase()} = new ${c.n(elementType).capitalize()}();
        }"""
}

fun <T : TypeI<*>> T.toAngularModuleArrayElement(attr: EntityI<*>): String =
    "'${attr.name()}'"

fun <T : ItemI<*>> T.toTypeScriptModuleGenerateComponentPart(c: GenerationContext): String =
    """@${c.n("Component")}({
  selector: 'app-${this.name().toLowerCase()}',
  templateUrl: './${this.name().toLowerCase()}-module-view.component.html',
  styleUrls: ['./${this.name().toLowerCase()}-module-view.component.scss'],
  providers: [${this.name().capitalize()}ViewService]
})
"""

fun <T : ItemI<*>> T.toTypeScriptEntityGenerateViewComponentPart(c: GenerationContext, type: String): String =
    """@${c.n("Component")}({
  selector: 'app-${c.n(this).toLowerCase()}-${type}',
  templateUrl: './${c.n(this).toLowerCase()}-entity-${type}.component.html',
  styleUrls: ['./${c.n(this).toLowerCase()}-entity-${type}.component.scss'],
  providers: [{provide: TableDataService, useClass: ${c.n(this).capitalize()}DataService}]
})
"""

fun <T : ItemI<*>> T.toTypeScriptEntityGenerateFormComponentPart(c: GenerationContext): String =
    """@${c.n("Component")}({
  selector: 'app-${c.n(this).toLowerCase()}-form',
  templateUrl: './${c.n(this).toLowerCase()}-form.component.html',
  styleUrls: ['./${c.n(this).toLowerCase()}-form.component.scss']
})
"""

fun <T : ItemI<*>> T.toAngularGenerateEnumElement(c: GenerationContext, indent: String, elementType: String, enums: List<EnumTypeI<*>>): String {
    var text = ""
    enums.forEach {
        if(it.name() == elementType) {
            text = """${indent}${elementType.toLowerCase()}Enum = this.${this.parent().name().toLowerCase()}DataService.loadEnumElement(${c.n(it)});"""
        }
    }
    return text
}

fun <T : ItemI<*>> T.toTypeScriptEntityProp(c: GenerationContext, indent: String): String {
    return """${indent}${this.name().toLowerCase()}: ${c.n(this)};$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptFormProp(c: GenerationContext, indent: String): String {
    return """${indent}@${c.n("Input")}() ${this.name().toLowerCase()}: ${c.n(this)};$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptViewEntityPropInit(c: GenerationContext, indent: String): String {
    return """${indent}${this.name().toLowerCase()}: ${c.n(this)} = new ${c.n(this)}();$nL"""
}

fun <T : ItemI<*>> T.toAngularModuleImportEntities(element: EntityI<*>): String {
    return """import {${element.name().capitalize()}ViewComponent} from '@schkola/${element.parent().name().toLowerCase()}/${element.name().toLowerCase()}/components/view/${element.name().toLowerCase()}-entity-view.component';
import {${element.name().capitalize()}ListComponent} from '@schkola/${element.parent().name().toLowerCase()}/${element.name().toLowerCase()}/components/list/${element.name().toLowerCase()}-entity-list.component';
import {${element.name().capitalize()}FormComponent} from '@schkola/${element.parent().name().toLowerCase()}/${element.name().toLowerCase()}/components/form/${element.name().toLowerCase()}-form.component';"""
}

fun <T : ItemI<*>> T.toAngularModuleImportEntitiesRouting(element: EntityI<*>): String {
    return """import {${element.name().capitalize()}ViewComponent} from '@schkola/${element.parent().name().toLowerCase()}/${element.name().toLowerCase()}/components/view/${element.name().toLowerCase()}-entity-view.component';
import {${element.name().capitalize()}ListComponent} from '@schkola/${element.parent().name().toLowerCase()}/${element.name().toLowerCase()}/components/list/${element.name().toLowerCase()}-entity-list.component';"""
}

fun <T : ItemI<*>> T.toAngularModuleImportBasics(element: BasicI<*>): String {
    return """import {${element.name().capitalize()}Component} from '@schkola/${element.parent().name().toLowerCase()}/basics/${element.name().toLowerCase()}/${element.name().toLowerCase()}-basic.component';"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationEntities(indent: String, element: EntityI<*>): String {
    return """$indent${element.name().capitalize()}ViewComponent,
$indent${element.name().capitalize()}ListComponent,
$indent${element.name().capitalize()}FormComponent"""
}

fun <T : ItemI<*>> T.toAngularModuleExportViews(indent: String, element: EntityI<*>): String {
    return """$indent${element.name().capitalize()}FormComponent"""
}

fun <T : ItemI<*>> T.toAngularModuleDeclarationBasics(indent: String, element: BasicI<*>): String {
    return """$indent${element.name().capitalize()}Component"""
}

fun <T : ItemI<*>> T.toAngularModulePath(indent: String, element: EntityI<*>): String {
    return """$indent{ path: '${element.name().toLowerCase()}', component: ${element.name().capitalize()}ListComponent },
$indent{ path: '${element.name().toLowerCase()}/new', component: ${element.name().capitalize()}ViewComponent },
$indent{ path: '${element.name().toLowerCase()}/edit/:id', component: ${element.name().capitalize()}ViewComponent }"""
}


fun <T : ItemI<*>> T.toTypeScriptModuleImportServices(): String {
    return """import {${this.name()}ViewService} from '../../service/${this.name().toLowerCase()}-module-view.service';$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptModuleInputElement(c: GenerationContext, name: String, indent: String): String {
    return """${indent}@${c.n("Input")}() $name = '${this.name()}Component';$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptModuleConstructor(indent: String): String {
    return """${indent}constructor(public ${this.name().toLowerCase()}ViewService: ${this.name()}ViewService) {}$nL"""
}

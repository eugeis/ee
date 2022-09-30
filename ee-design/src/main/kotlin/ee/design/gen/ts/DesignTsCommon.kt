import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.toCamelCase
import ee.design.EntityI
import ee.design.ModuleI
import ee.lang.*

fun <T : ItemI<*>> T.toAngularConstructorDataService(indent: String, element: EntityI<*>): String {
    return """${indent}constructor(public ${element.name().toLowerCase()}DataService: ${element.name()}DataService) {}$nL"""
}

fun <T : ItemI<*>> T.toAngularViewOnInit(c: GenerationContext, indent: String, element: EntityI<*>, basics: List<BasicI<*>>): String {
    val basicElements: MutableList<String> = ArrayList()
    basics.forEach { basicElements.add(it.name().capitalize()) }

    return """${indent}ngOnInit(): void {
        this.${element.name().toLowerCase()} = this.${element.name().toLowerCase()}DataService.getFirst();
        this.${element.name().toLowerCase()}DataService.checkRoute(this.${element.name().toLowerCase()});
    }"""
}

fun <T : ItemI<*>> T.toAngularFormOnInit(c: GenerationContext, indent: String, element: EntityI<*>, basics: List<BasicI<*>>, entities: List<EntityI<*>>): String {
    val includedElements: MutableList<String> = ArrayList()
    basics.forEach { includedElements.add(it.name().capitalize()) }
    entities.forEach { includedElements.add(it.name().capitalize()) }

    return """${indent}ngOnInit(): void {
        ${element.props().filter { it.type().name() in includedElements }.joinSurroundIfNotEmptyToString(nL + tab) {
        it.toAngularEmptyProps(c, indent, it, element, it.type())
    }.trim()}
    }"""
}

fun <T : ItemI<*>> T.toAngularEmptyProps(c: GenerationContext, indent: String, element: AttributeI<*>, entity: EntityI<*>, elementType: TypeI<*>): String {
    return """${indent}if (this.${entity.name().toLowerCase()}.${element.name().toCamelCase()} === undefined) {
            this.${entity.name().toLowerCase()}.${element.name().toCamelCase()} = new ${c.n(elementType).capitalize()}();
        }"""
}

fun <T : TypeI<*>> T.toAngularModuleArrayElement(attr: EntityI<*>): String =
    "'${attr.name()}'"

fun <T : ItemI<*>> T.toTypeScriptModuleGenerateComponentPart(element: ModuleI<*>): String =
    """@Component({
  selector: 'app-${element.name().toLowerCase()}',
  templateUrl: './${element.name().toLowerCase()}-module-view.component.html',
  styleUrls: ['./${element.name().toLowerCase()}-module-view.component.scss'],
  providers: [${element.name()}ViewService]
})
"""

fun <T : ItemI<*>> T.toTypeScriptEntityGenerateViewComponentPart(c: GenerationContext, element: EntityI<*>, type: String): String =
    """@Component({
  selector: 'app-${c.n(element).toLowerCase()}-${type}',
  templateUrl: './${c.n(element).toLowerCase()}-entity-${type}.component.html',
  styleUrls: ['./${c.n(element).toLowerCase()}-entity-${type}.component.scss'],
  providers: [{provide: TableDataService, useClass: ${c.n(element).capitalize()}DataService}]
})
"""

fun <T : ItemI<*>> T.toTypeScriptEntityGenerateFormComponentPart(c: GenerationContext, element: EntityI<*>, type: String): String =
    """@Component({
  selector: 'app-${c.n(element).toLowerCase()}-form',
  templateUrl: './${c.n(element).toLowerCase()}-form.component.html',
  styleUrls: ['./${c.n(element).toLowerCase()}-form.component.scss']
})
"""

fun <T : ItemI<*>> T.toAngularGenerateEnumElement(c: GenerationContext, indent: String, element: EntityI<*>, elementName: String, elementType: String, enums: List<EnumTypeI<*>>): String {
    var text = ""
    enums.forEach {
        if(it.name() == elementType) {
            text = """${indent}${elementType.toLowerCase()}Enum = this.${element.name().toLowerCase()}DataService.loadEnumElement(${c.n(it)});"""
        }
    }
    return text
}

fun <T : ItemI<*>> T.toTypeScriptEntityProp(c: GenerationContext, indent: String, element: EntityI<*>): String {
    return """${indent}${element.name().toLowerCase()}: ${c.n(element)};$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptFormProp(c: GenerationContext, indent: String, element: EntityI<*>): String {
    return """${indent}@Input() ${element.name().toLowerCase()}: ${c.n(element)};$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptViewEntityPropInit(c: GenerationContext, indent: String, element: EntityI<*>): String {
    return """${indent}${element.name().toLowerCase()}: ${c.n(element)} = new ${c.n(element)}();$nL"""
}

fun <T : ItemI<*>> T.toAngularModuleImportEntities(element: EntityI<*>): String {
    return """import {${element.name().capitalize()}ViewComponent} from '@schkola/${element.parent().name().toLowerCase()}/${element.name().toLowerCase()}/components/view/${element.name().toLowerCase()}-entity-view.component';
import {${element.name().capitalize()}ListComponent} from '@schkola/${element.parent().name().toLowerCase()}/${element.name().toLowerCase()}/components/list/${element.name().toLowerCase()}-entity-list.component';
import {${element.name().capitalize()}FormComponent} from '@schkola/${element.parent().name().toLowerCase()}/${element.name().toLowerCase()}/components/form/${element.name().toLowerCase()}-form.component';"""
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


fun <T : ItemI<*>> T.toTypeScriptModuleImportServices(element: ModuleI<*>): String {
    return """import {${element.name()}ViewService} from '../../service/${element.name().toLowerCase()}-module-view.service';$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptModuleInputElement(name: String, indent: String, element: ModuleI<*>): String {
    return """${indent}@Input() $name = '${element.name()}Component';$nL"""
}

fun <T : ItemI<*>> T.toTypeScriptModuleConstructor(indent: String, element: ModuleI<*>): String {
    return """${indent}constructor(public ${element.name().toLowerCase()}ViewService: ${element.name()}ViewService) {}$nL"""
}

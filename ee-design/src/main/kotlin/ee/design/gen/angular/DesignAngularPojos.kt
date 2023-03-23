import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.common.ext.then
import ee.design.ModuleI
import ee.lang.*
import ee.lang.gen.ts.AngularDerivedType
import ee.lang.gen.ts.angular
import ee.lang.gen.ts.module
import ee.lang.gen.ts.ngxtranslate

fun <T : ModuleI<*>> T.toAngularModule(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                api: String = LangDerivedKind.API): String {
    return """ 
export function HttpLoaderFactory(http: ${c.n(angular.commonhttp.HttpClient)}) {
    return new ${c.n(ngxtranslate.httploader.TranslateHttpLoader)}(http);
}

@${c.n(angular.core.NgModule)}({
    declarations: [
        ${c.n(this, AngularDerivedType.ViewComponent)},
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationEntities(c, tab + tab)
    }}
${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationBasics(c, tab + tab)
    }}
${this.enums().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationEnums(c, tab + tab) 
    }}
    ],
    imports: [
        ${c.n(this, AngularDerivedType.RoutingModules)},
        ${c.n(module.template.TemplateModule)},
        ${c.n(angular.common.CommonModule)},
        ${c.n(angular.forms.FormsModule)},
        ${c.n(angular.forms.ReactiveFormsModule)},
        ${c.n(module.material.MaterialModule)},
        ${c.n(ngxtranslate.core.TranslateModule)}.forChild({
            loader: {provide: ${c.n(ngxtranslate.core.TranslateLoader)}, useFactory: HttpLoaderFactory, deps: [${c.n(angular.commonhttp.HttpClient)}]},
        }),
        ${this.entities().any { entity ->
        entity.props().any {
            it.type().parent().name() != this.name() && it.type().parent().name().first().isUpperCase()
        }
    }.then {this.toAngularImportOtherModulesOnImportPart(c)}}
    ],
    providers: [
        { provide: ${c.n(ngxtranslate.core.TranslateService)}, useExisting: ${c.n(module.services.TemplateTranslateService)} }
    ],
    exports: [
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleExportViews(c, tab + tab)
    }}
${this.basics().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationBasics(c, tab + tab)
    }}
${this.enums().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
        it.toAngularModuleDeclarationEnums(c, tab + tab)
    }}
    ]
})
export class ${c.n(this, AngularDerivedType.OwnModule)} {}"""
}

fun <T : ModuleI<*>> T.toAngularImportOtherModulesOnImportPart(c: GenerationContext): String {
    val sb = StringBuilder()
    val importedOtherModules: MutableList<String> = ArrayList()
    this.entities().forEach { entity ->
        entity.props().filter { it.type().parent().name() != this.name() && it.type().parent().name().first().isUpperCase() }.forEach {
            importedOtherModules.add("${c.n(it.type().parent(), AngularDerivedType.Module)},")
        }
    }
    importedOtherModules.distinct().forEach {
        sb.append(it)
        sb.append(("\n${tab + tab}"))
    }
    return sb.toString()
}

fun <T : ModuleI<*>> T.toAngularRoutingModule(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                       api: String = LangDerivedKind.API): String {
    return """
const routes: ${c.n(angular.router.Routes)} = [
    { path: '', component: ${c.n(this, AngularDerivedType.ViewComponent)} },
${this.entities().filter { !it.isEMPTY() }.joinSurroundIfNotEmptyToString(",$nL") {
        it.toAngularModulePath(c, tab)
    }}
];

@${c.n(angular.core.NgModule)}({
    imports: [${c.n(angular.router.RouterModule)}.forChild(routes)],
    exports: [${c.n(angular.router.RouterModule)}],
})
export class ${c.n(this, AngularDerivedType.OwnRoutingModules)} {}

"""
}

fun <T : ModuleI<*>> T.toAngularModuleHTMLComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                    api: String = LangDerivedKind.API): String {
    return this.toAngularModuleHTML()
}

fun <T : ModuleI<*>> T.toAngularDefaultSCSS(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                            api: String = LangDerivedKind.API): String {
    return this.toAngularDefaultSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewHTMLComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return this.toAngularEntityViewHTML()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityViewSCSSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return this.toAngularEntityViewSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularFormHTMLComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                           api: String = LangDerivedKind.API): String {
    return this.toAngularEntityFormHTML()
}

fun <T : CompilationUnitI<*>> T.toAngularFormSCSSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                           api: String = LangDerivedKind.API): String {
    return this.toAngularEntityFormSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListHTMLComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return this.toAngularEntityListHTML()
}

fun <T : CompilationUnitI<*>> T.toAngularEntityListSCSSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                                 api: String = LangDerivedKind.API): String {
    return this.toAngularEntityListSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularBasicHTMLComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                            api: String = LangDerivedKind.API): String {
    return this.toAngularBasicHTML()
}

fun <T : CompilationUnitI<*>> T.toAngularBasicSCSSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                            api: String = LangDerivedKind.API): String {
    return this.toAngularBasicSCSS()
}

fun <T : CompilationUnitI<*>> T.toAngularEnumHTMLComponent(parent: ItemI<*>, elementName: String, c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                            api: String = LangDerivedKind.API): String {
    return this.toAngularEnumHTML(parent, elementName)
}

fun <T : CompilationUnitI<*>> T.toAngularEnumSCSSComponent(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                            api: String = LangDerivedKind.API): String {
    return this.toAngularDefaultSCSS()
}

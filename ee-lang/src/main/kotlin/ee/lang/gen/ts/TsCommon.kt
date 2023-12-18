package ee.lang.gen.ts

import ee.common.ext.*
import ee.lang.*
import ee.lang.gen.java.j
import java.util.*

fun <T : TypeI<*>> T.toTypeScriptDefault(c: GenerationContext, derived: String, attr: AttributeI<*>): String {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.String, n.Text -> "''"
        n.Boolean        -> "false"
        n.Int            -> "0"
        n.Long           -> "0L"
        n.Float          -> "0f"
        n.Date           -> "${c.n(j.util.Date)}()"
        n.Path           -> "${c.n(j.nio.file.Paths)}.get('')"
        n.Blob           -> "new ByteArray(0)"
        n.Void           -> ""
        n.Error          -> "new Throwable()"
        n.Exception      -> "new Exception()"
        n.Url            -> "${c.n(j.net.URL)}('')"
        n.Map            -> (attr.isNotEMPTY() && attr.isMutable().setAndTrue()).ifElse("new Map()", "new Map()")
        n.List           -> (attr.isNotEMPTY() && attr.isMutable().setAndTrue()).ifElse("new Array()", "new Array()")
        else             -> {
            if (baseType is LiteralI<*>) {
                "${(baseType.findParent(EnumTypeI::class.java) as EnumTypeI<*>).toTypeScript(c, derived,
                    attr)}.${baseType.toTypeScript()}"
            } else if (baseType is EnumTypeI<*>) {
                "${c.n(this, derived)}.${baseType.literals().first().toTypeScript()}"
            } else if (baseType is CompilationUnitI<*>) {
                "new ${c.n(this, derived)}()"
            } else {
                (this.parent() == n).ifElse("''", { "${c.n(this, derived)}.EMPTY" })
            }
        }
    }
}

fun <T : AttributeI<*>> T.toTypeScriptDefault(c: GenerationContext, derived: String): String =
    type().toTypeScriptDefault(c, derived, this)


fun <T : ItemI<*>> T.toTypeScriptEMPTY(c: GenerationContext, derived: String): String =
    (this.parent() == n).ifElse("''", { "${c.n(this, derived)}.EMPTY" })


fun <T : AttributeI<*>> T.toTypeScriptEMPTY(c: GenerationContext, derived: String): String =
    type().toTypeScriptEMPTY(c, derived)

fun <T : AttributeI<*>> T.toTypeScriptTypeSingle(c: GenerationContext, api: String): String =
    type().toTypeScript(c, api, this)

fun <T : AttributeI<*>> T.toTypeScriptTypeDef(c: GenerationContext, api: String): String =
    """${type().toTypeScript(c, api, this)}${isNullable().then("?")}"""


fun <T : AttributeI<*>> T.toTypeScriptCompanionObjectName(c: GenerationContext): String =
    """        val ${name().toUnderscoredUpperCase()} = "_${name()}""""

fun <T : CompilationUnitI<*>> T.toTypeScriptExtends(c: GenerationContext, derived: String, api: String): String {
    if (superUnit().isNotEMPTY() && derived != api) {
        return " extends ${c.n(superUnit(), derived)}, ${c.n(this, api)}"
    } else if (superUnit().isNotEMPTY()) {
        return " extends ${c.n(superUnit(), derived)}"
    } else if (derived != api) {
        return " extends ${c.n(this, api)}"
    } else {
        return ""
    }
}

fun <T : TypeI<*>> T.toTypeScriptIfNative(c: GenerationContext, derived: String, attr: AttributeI<*>): String? {
    val baseType = findDerivedOrThis()
    return when (baseType) {
        n.Any       -> "any"
        n.String    -> "string"
        n.Boolean   -> "boolean"
        n.Int       -> "number"
        n.Long      -> "number"
        n.Float     -> "number"
        n.Date      -> "Date"
        n.TimeUnit  -> "string"
        n.Path      -> "string"
        n.Text      -> "string"
        n.Blob      -> "Blob"
        n.Exception -> "Error"
        n.Error     -> "Error"
        n.Void      -> "void"
        n.Url       -> "string"
        n.UUID      -> "string"
        n.List      -> "Array${toTypeScriptGenericTypes(c, derived, attr)}"
        n.Map       -> "Map${toTypeScriptGenericTypes(c, derived, attr)}"
        else        -> {
            if (this is LambdaI<*>) operation().toTypeScriptLambda(c, derived) else null
        }
    }
}

fun TypeI<*>.toTypeScriptGenericTypes(c: GenerationContext, derived: String, attr: AttributeI<*>): String =
    generics().joinWrappedToString(", ", "", "<", ">") { it.type().toTypeScript(c, derived, attr) }

fun GenericI<*>.toTypeScript(c: GenerationContext, derived: String): String = c.n(type(), derived)

fun TypeI<*>.toTypeScriptGenerics(c: GenerationContext, derived: String, attr: AttributeI<*>): String =
    generics().joinWrappedToString(", ", "", "<", ">") { it.toTypeScript(c, derived, attr) }

fun TypeI<*>.toTypeScriptGenericsClassDef(c: GenerationContext, derived: String, attr: AttributeI<*>): String =
    generics().joinWrappedToString(", ", "", "<", ">") {
        "${it.name()} : ${it.type().toTypeScript(c, derived, attr)}"
    }

fun TypeI<*>.toTypeScriptGenericsMethodDef(c: GenerationContext, derived: String, attr: AttributeI<*>): String =
    generics().joinWrappedToString(", ", "", "<", "> ") {
        "${it.name()} : ${it.type().toTypeScript(c, derived, attr)}"
    }

fun TypeI<*>.toTypeScriptGenericsStar(context: GenerationContext, derived: String): String =
    generics().joinWrappedToString(", ", "", "<", "> ") { "*" }

fun OperationI<*>.toTypeScriptGenerics(c: GenerationContext, derived: String): String =
    generics().joinWrappedToString(", ", "", "<", "> ") {
        "${it.name()} : ${it.type().toTypeScript(c, derived)}"
    }

fun <T : TypeI<*>> T.toTypeScript(c: GenerationContext, derived: String,
                                  attr: AttributeI<*> = Attribute.EMPTY): String =
    toTypeScriptIfNative(c, derived, attr) ?: "${c.n(this, LangDerivedKind.WithParentAsName)}${this.toTypeScriptGenericTypes(c, derived, attr)}"

fun <T : AttributeI<*>> T.toTypeScriptValue(c: GenerationContext, derived: String): String {
    if (value() != null) {
        return when (type()) {
            n.String, n.Text                                                  -> "\"${value()}\""
            n.Boolean, n.Int, n.Long, n.Float, n.Date, n.Path, n.Blob, n.Void -> "${value()}"
            else                                                              -> {
                if (value() is LiteralI<*>) {
                    val lit = value() as LiteralI<*>
                    "${(lit.parent() as EnumTypeI<*>).toTypeScript(c, derived, this)}.${lit.toTypeScript()}"
                } else {
                    "${value()}"
                }
            }
        }
    } else {
        return toTypeScriptDefault(c, derived)
    }
}

fun <T : AttributeI<*>> T.toTypeScriptInit(c: GenerationContext, derived: String): String {
    if (value() != null) {
        return " = ${toTypeScriptValue(c, derived)}"
    } else if (isNullable()) {
        return " = null"
    } else if (isInitByDefaultTypeValue()) {
        return " = ${toTypeScriptValue(c, derived)}"
    } else {
        return ""
    }
}

fun <T : AttributeI<*>> T.toTypeScriptInitMember(c: GenerationContext, derived: String): String =
    "this.${name()}${toTypeScriptInit(c, derived)}"

fun <T : AttributeI<*>> T.toTypeScriptSignature(c: GenerationContext, derived: String, api: String,
                                                init: Boolean = true): String =
    "${name()}: ${toTypeScriptTypeDef(c, api)}${init.then { toTypeScriptInit(c, derived) }}"

fun <T : AttributeI<*>> T.toTypeScriptConstructorMember(c: GenerationContext, derived: String, api: String,
                                                        init: Boolean = true): String =
    //"${isReplaceable().setAndTrue().ifElse("", "readonly ")}${toTypeScriptSignature(c, derived, api, init)}"
    "${toTypeScriptSignature(c, derived, api, init)}"

fun <T : AttributeI<*>> T.toTypeScriptMember(c: GenerationContext, derived: String, api: String,
                                             init: Boolean = true, indent: String): String =
    //"    ${isReplaceable().setAndTrue().ifElse("", "readonly ")}${toTypeScriptSignature(c, derived, api, init)}"
    "${indent}${toTypeScriptSignature(c, derived, api, init)}"

fun List<AttributeI<*>>.toTypeScriptSignature(c: GenerationContext, derived: String, api: String): String =
    joinWrappedToString(", ") { it.toTypeScriptSignature(c, derived, api) }

fun List<AttributeI<*>>.toTypeScriptMember(c: GenerationContext, derived: String, api: String): String =
    joinWrappedToString(", ") { it.toTypeScriptSignature(c, derived, api) }

fun <T : ConstructorI<*>> T.toTypeScriptPrimary(c: GenerationContext, derived: String, api: String): String {
    return if (isNotEMPTY()) """(${params().joinWrappedToString(", ", "      ") {
        it.toTypeScriptConstructorMember(c, derived, api)
    }})${superUnit().toTypeScriptCall(c)}""" else ""
}

fun <T : ConstructorI<*>> T.toTypeScript(c: GenerationContext, derived: String, api: String): String {
    return if (isNotEMPTY()) """
    constructor(${params().joinWrappedToString(", ", "                ") {
        it.toTypeScriptSignature(c, derived, api)
    }}) {${superUnit().isNotEMPTY().then {
        (superUnit() as ConstructorI<*>).toTypeScriptCall(c, (parent() != superUnit().parent()).ifElse("super", "this"))
    }} ${paramsWithOut(superUnit()).joinSurroundIfNotEmptyToString("${nL}        ", prefix = "{${nL}        ") {
        it.toTypeScriptAssign(c)
    }}${(parent() as CompilationUnitI<*>).props().filter { it.isMeta() }.joinSurroundIfNotEmptyToString("${nL}        ",
        prefix = "${nL}        ") {
        it.toTypeScriptInitMember(c, derived)
    }}
    }""" else ""
}

fun <T : ConstructorI<*>> T.toTypeScriptCall(c: GenerationContext, name: String = "this"): String =
    isNotEMPTY().then { " : $name(${params().joinWrappedToString(", ") { it.name() }})" }

fun <T : AttributeI<*>> T.toTypeScriptAssign(c: GenerationContext): String = "this.${name()} = ${name()}"

fun <T : LogicUnitI<*>> T.toTypeScriptCall(c: GenerationContext): String =
    isNotEMPTY().then { "(${params().joinWrappedToString(", ") { it.name() }})" }

fun <T : LogicUnitI<*>> T.toTypeScriptCallValue(c: GenerationContext, derived: String): String =
    isNotEMPTY().then { "(${params().joinWrappedToString(", ") { it.toTypeScriptValue(c, derived) }})" }

fun <T : LiteralI<*>> T.toTypeScriptCallValue(c: GenerationContext, derived: String): String =
    params().isNotEmpty().then { "(${params().joinWrappedToString(", ") { it.toTypeScriptValue(c, derived) }})" }

fun <T : AttributeI<*>> T.toTypeScriptType(c: GenerationContext, derived: String): String =
    type().toTypeScript(c, derived, this)

fun List<AttributeI<*>>.toTypeScriptTypes(c: GenerationContext, derived: String): String =
    joinWrappedToString(", ") { it.toTypeScriptType(c, derived) }

fun <T : OperationI<*>> T.toTypeScriptLambda(c: GenerationContext, derived: String): String =
    """(${params().toTypeScriptTypes(c, derived)}) -> ${retFirst().toTypeScriptType(c, derived)}"""

fun <T : OperationI<*>> T.toTypeScriptImpl(c: GenerationContext, derived: String, api: String): String {
    return """
    ${toTypeScriptGenerics(c, derived)}${name()}(${params().toTypeScriptSignature(c, derived,
        api)}): ${retFirst().toTypeScriptTypeDef(c, api)} {
        throw new ReferenceError('Not implemented yet.');
    }"""
}

fun <T : AttributeI<*>> T.toTypeScriptFindBy(c: GenerationContext, derived: String, api: String, parentName: String = "", basicParent: CompilationUnitI<*> = CompilationUnitEmpty): String =
        """
    findBy${parentName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}${name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(element: ${if (basicParent == CompilationUnitEmpty) {c.n(this.parent(), LangDerivedKind.WithParentAsName)} else {c.n(basicParent, LangDerivedKind.WithParentAsName)}}, ${name()}: string = ''): boolean {
        const parts = ${name()}.split(' ');
        return parts.every(p => JSON.stringify(element${parentName.isNotEmpty().then { "." + parentName.replaceFirstChar { it.lowercase(Locale.getDefault()) } }}.${name()}).toLowerCase().includes(p.toLowerCase()));
    }"""

fun <T : CompilationUnitI<*>> T.toTypeScriptTooltipFunction(c: GenerationContext, derived: String = LangDerivedKind.IMPL,
                                                 api: String = LangDerivedKind.API): String {
    return """
    tooltip(object: Object) {
        return JSON.stringify(object)
    }
    """
}

/*fun <T : ItemI<*>> T.toAngularComponentAnnotation(c: GenerationContext): String {
    val selector = toAngularComponentSelector()
    val componentFileName = toAngularComponentFileNameBase()
    return """@${c.n(angular.core.Component)}({
  selector: '$selector',
  templateUrl: '$componentFileName.html',
  styleUrls: ['$componentFileName.scss'],
})"""
}*/

// name().toLowerCase() in file name and path
fun <T : ItemI<*>> T.toAngularComponentFileNameBase(elementType: String, isChild: Boolean, componentName: String): String {
    return "${isChild.then { "${name().lowercase(Locale.getDefault())}/" }}${name().lowercase(Locale.getDefault())}-${elementType}.${componentName.lowercase(
        Locale.getDefault()
    )}"
}
fun <T : ItemI<*>> T.toAngularEntityFileNameBase(elementType: String, componentType: String, format: String): String {
    return "${name().lowercase(Locale.getDefault())}/${componentType}/${name().lowercase(Locale.getDefault())}-${elementType}.${format}"
}

fun <T : ItemI<*>> T.toAngularEntityAggregateViewFileNameBase(elementType: String, componentType: String, format: String): String {
    return "${name().lowercase(Locale.getDefault())}/${componentType}/${name().lowercase(Locale.getDefault())}-${elementType}.${format}"
}

fun <T : ItemI<*>> T.toAngularComponentSelector(): String {
    return fullParentNameAndName().toHyphenLowerCase()
}

fun <T : CompilationUnitI<*>> T.toAngularListOnInit(c: GenerationContext, indent: String, isAggregateView: Boolean = false): String {
    return """${indent}ngOnInit(): void {
        this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.saveChildComponent(this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.componentChild)
        this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.saveIsSpecificNew(JSON.stringify(false));
        this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.saveIsSpecificNewNavigation('');
    
        this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.clearStorage();
        
        ${isAggregateView.not().then { """this._route.queryParams.subscribe(param => {
            this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.componentName = param['name'];
            this.tabElement = param['tabElement'];
        });
        
        this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.checkIfDataChangedFromSpecificView();""" }}
        
        ${isAggregateView.then { """this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getComponentName().subscribe((componentName) => {
            this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.componentName = componentName;
        })""" }}
        
        this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getData().subscribe((data) => {
            this.data = data;
            
            ${isAggregateView.not().then {"""
            this.data.forEach((item) => {
            ${props().filter { it.isNotEMPTY() }.joinSurroundIfNotEmptyToString(nL) {
            """
                if (!this.categories.${it.name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}.options.includes(JSON.stringify(item.${it.name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}))) {
                    this.categories.${it.name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}.options.push(JSON.stringify(item.${it.name().toCamelCase().replaceFirstChar { it.lowercase(Locale.getDefault()) }}))
                }""" }}
            })"""}} 
            
            if(this.${c.n(this, AngularDerivedType.DataService)
                .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.componentName !== undefined && this.${c.n(this, AngularDerivedType.DataService)
                .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.componentName.length > 0) {
                ${isAggregateView.not().then { """
                this.isSpecificView = true;
                this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getSpecificData().subscribe((specificData) => {
                    this.data = data.filter(element => JSON.stringify(specificData).includes(JSON.stringify(element)))
                })
            """ }}
                ${isAggregateView.then { """this.${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }} = this.${c.n(this, AngularDerivedType.DataService)
                .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getFirst();
                this.${c.n(this, AngularDerivedType.DataService)
                .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.checkRoute(this.${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }});""" }}
                
                ${isAggregateView.then { """
                ${c.n(rxjs.empty.forkJoin)}([this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getSpecificData(), ${props().filter { it.type().name().lowercase(Locale.getDefault()) !in arrayOf("string", "boolean", "date", "int", "double", "list") }.joinSurroundIfNotEmptyToString("") { 
                """this.${c.n(this, AngularDerivedType.DataService)
                        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getAnySpecificData('${this.parent().name().lowercase(Locale.getDefault())}${it.type().name().lowercase(Locale.getDefault())}'), """
            }} ${props().filter { it.type().name().lowercase(Locale.getDefault()).equals("list", true) }.joinSurroundIfNotEmptyToString("") {
                """this.${c.n(this, AngularDerivedType.DataService)
                        .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getAnySpecificData('${this.parent().name().lowercase(Locale.getDefault())}${it.type().generics().first().type().name().lowercase(Locale.getDefault())}'), """
            }}]).subscribe(
                    ([${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}, ${props().filter { it.type().name().lowercase(Locale.getDefault()) !in arrayOf("string", "boolean", "date", "int", "double", "list") }.joinSurroundIfNotEmptyToString("") {
                        """${this.parent().name().lowercase(Locale.getDefault())}${it.type().name().lowercase(Locale.getDefault())}, """
                    }} ${props().filter { it.type().name().lowercase(Locale.getDefault()).equals("list", true) }.joinSurroundIfNotEmptyToString("") {
                        """${this.parent().name().lowercase(Locale.getDefault())}${it.type().generics().first().type().name().lowercase(Locale.getDefault())}, """
                    }}]) => {
                        ${props().filter { it.type().name().lowercase(Locale.getDefault()) !in arrayOf("string", "boolean", "date", "int", "double", "list") }.joinSurroundIfNotEmptyToString("") {
                        """${this.name().lowercase(Locale.getDefault())}.${it.name().lowercase(Locale.getDefault())} = ${this.parent().name().lowercase(Locale.getDefault())}${it.type().name().lowercase(Locale.getDefault())}"""
                    }}
                        ${props().filter { it.type().name().lowercase(Locale.getDefault()).equals("list", true) }.joinSurroundIfNotEmptyToString("") {
                        """${this.name().lowercase(Locale.getDefault())}.${it.name().lowercase(Locale.getDefault())} = ${this.parent().name().lowercase(Locale.getDefault())}${it.type().generics().first().type().name().lowercase(Locale.getDefault())}"""
                    }}
                        this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.getComponentName().subscribe((componentName) => {
                            this.${c.n(this, AngularDerivedType.DataService)
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }}.saveSpecificData(${this.name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}, componentName)
                        })
                    }
                )
                """}}
            }
        })

        ${isAggregateView.then {"""this.tabElement = this.generateTabElement();"""}} 
    }"""
}

/*when (this.type().props().size) {
    0 -> """'${this.name().toCamelCase()}'"""
    else -> {
        this.type().props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(", ") {
            it.toTypeScriptTypeProperty(c, this)
        }
    }
}*/

/*fun <T : AttributeI<*>> T.toTypeScriptTypeProperty(c: GenerationContext, elementParent: AttributeI<*>): String {
    return when (this.type()) {
        is EntityI<*>, is ValuesI<*> -> """'${this.name().toLowerCase()}-entity'"""
        is BasicI<*> -> this.type().props().filter { !it.isMeta() }.joinSurroundIfNotEmptyToString(", ") {
                            it.toTypeScriptTypeProperty(c, elementParent)
                        }
        is EnumTypeI<*> -> """'${this.name().toCamelCase()}'"""
        else -> {
            when (this.type().props().size) {
                0 -> """'${elementParent.name().toLowerCase()}-${this.name().toCamelCase()}'"""
                else -> {

                }
            }
        }
    }
}*/

fun <T : AttributeI<*>> T.toTypeScriptInitEmptyProps(c: GenerationContext): String {
    return when (this.type().toTypeScriptIfNative(c, "", this)) {
        "boolean", "string", "number", "Date" -> ""
        else -> {
            when (this.type().props().size) {
                0 -> ""
                else -> {
                    """
        if (this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()} === undefined) {
            this.${this.parent().name().lowercase(Locale.getDefault())}.${this.name().toCamelCase()} = new ${c.n(this.type(), AngularDerivedType.ApiBase)}();
        }"""
                }
            }
        }
    }
}

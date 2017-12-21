package ee.design.gen.kt

import ee.common.ext.*
import ee.design.*
import ee.design.gen.java.Java
import ee.design.gen.kt.TypePosition.*


enum class TypePosition {
    SIGNATURE, MEMBER, OPERATION
}

data class NamesKt(override val name: String,
                   override val derived: String = name,
                   override val fileName: String = "$derived.gen") : Names

object Kotlin : StructureUnit("Kotlin") {
    object Core : StructureUnit("kotlin.collections") {
        object List : ExternalType() {
            val T = G()
        }

        object MutableList : ExternalType() {
            val T = G()
        }

        object MutableMap : ExternalType() {
            val K = G()
            val V = G()
        }
    }
}

object KotlinD : StructureUnit("Kotlin") {
    object Core : StructureUnit("kotlin.collections") {
        val List = Kotlin.Core.List._d
        val MutableList = Kotlin.Core.MutableList._d
        val MutableMap = Kotlin.Core.MutableMap._d
    }
}


fun <T : ElementIfc> T.toKotlin(context: KotlinContext, indent: String = ""): String {
    if (this is TextElement) {
        return "$indent$text"
    } else if (this is Literal) {
        return "$indent${name.toUnderscoredUpperCase()}"
    } else {
        return "$indent$name"
    }
}

fun <T : TextElement> T.toKotlin(context: KotlinContext, indent: String = ""): String {
    return "$indent$text"
}

fun <T : Comment> T.toKotlin(context: KotlinContext, indent: String = ""): String {
    if (children.size == 1 && !children.first().toKotlin(context).contains("\n")) {
        return "$indent/* ${children.first().toKotlin(context)} */$nL"
    } else {
        val newIndent = "$indent$tab"
        return "/*$nL${children.joinToString(nL) { it.toKotlin(context, newIndent) }}$indent*/$nL"
    }
}

fun <T : Element> T.toKotlinComment(context: KotlinContext, indent: String = ""): String {
    return when (doc) {
        null -> ""
        Comment.EMPTY -> ""
        else -> "${doc?.toKotlin(context, indent)}"
    }
}

fun <T : TypeIfc> T.toKotlin(context: KotlinContext, attr: Attribute? = findParent(AttributeI::class.java),
                             indent: String = "", position: TypePosition = SIGNATURE): String {
    return when (this) {
        td.String -> "String"
        td.Boolean -> "Boolean"
        td.Integer -> "Int"
        td.Long -> "Long"
        td.Float -> "Float"
        td.Date -> context.n(Java.util.Date)
        td.TimeUnit -> context.n(Java.util.concurrent.TimeUnit)
        td.Path -> context.n(Java.nioFile.Path)
        td.Text -> "String"
        td.Blob -> "ByteArray"
        td.Exception -> "Exception"
        td.Error -> "Throwable"
        td.Void -> "Unit"
        else -> {
            var currentType: TypeIfc = this
            if (attr != null && attr.mutable && position == MEMBER) {
                if (isOrDerived(td.List)) {
                    currentType = KotlinD.Core.MutableList
                } else if (isOrDerived(td.Map)) {
                    currentType = KotlinD.Core.MutableMap
                }
            }
            if (this is LambdaD) {
                this.op.toKotlinLambda(context)
            } else {
                when (position) {
                    SIGNATURE -> "${context.n(currentType)}${this.toKotlinGenericsStar(context, indent)}"
                    MEMBER -> "${context.n(currentType)}${this.toKotlinGenericTypes(context, indent)}"
                    OPERATION -> "${context.n(currentType)}${this.toKotlinGenerics(context, indent)}"
                    else -> "${context.n(currentType)}${this.toKotlinGenericTypes(context, indent)}"
                }
            }
        }
    }
}

fun <T : TypeIfc> T.toKotlinValue(context: KotlinContext, attr: Attribute? = findParent(AttributeI::class.java)): String {
    if (this is NativeType) {
        return when (this) {
            td.String -> "\"\""
            td.Boolean -> "false"
            td.Integer -> "0"
            td.Long -> "0L"
            td.Float -> "0f"
            td.Date -> "${td.Date.toKotlin(context)}()"
            td.Path -> "${context.n(Java.nioFile.Paths)}.get(\"\")"
            td.Text -> "\"\""
            td.Blob -> "ByteArray(0)"
            td.Void -> ""
            td.Exception -> "Exception()"
            td.Error -> "Throwable()"
            else -> {
                if (isOrDerived(td.Map)) {
                    if (attr != null) {
                        return attr.mutable.ifElse("hashMapOf()", "emptyMap()")
                    } else {
                        return "emptyMap()"
                    }
                } else if (isOrDerived(td.List)) {
                    if (attr != null) {
                        return attr.mutable.ifElse("arrayListOf()", "emptyList()")
                    } else {
                        return "emptyList()"
                    }
                } else "\"\""
            }
        }
    } else if (this is ExternalType) {
        return "null"
    } else if (this is EnumTypeD) {
        return "${context.n(this)}.${literals.first().toKotlin(context)}"
    } else if (this is CompilationUnit) {
        return "${context.n(this)}.EMPTY"
    } else if (this is GenericD) {
        return type.toKotlinValue(context, attr)
    } else if (this is LambdaD) {
        return op.params.joinToString(", ", "{ ", " -> }") { it.name }
    } else {
        return "null"
    }
}

fun <T : Attribute> T.toKotlinGetterIfc(context: KotlinContext, indent: String = ""): String {
    return "${toKotlinComment(context, indent)}$indent$name(): ${type.toKotlin(context, indent)}"
}

fun <T : Attribute> T.toKotlinSetterIfc(context: KotlinContext, indent: String = ""): String {
    return "${toKotlinComment(context, indent)}$indent$name($name: ${type.toKotlin(context)})"
}

fun <T : Literal> T.toKotlinLiteral(context: KotlinContext, indent: String = "", mappings: T.(context: KotlinContext, indent: String) -> String = { c, ind -> "" }): String {
    return """${toKotlinComment(context, indent)}${mappings(context, indent)}$indent${toKotlin(context)}${toKotlinCallValue(context)}"""
}

fun <T : Literal> T.toKotlinIs(context: KotlinContext, indent: String = "", mappings: T.(context: KotlinContext, indent: String) -> String = { c, ind -> "" }): String {
    return """${indent}fun is${name.capitalize()}() : Boolean = this == ${toKotlin(context)}"""
}

fun <T : Attribute> T.toKotlinTypeDef(context: KotlinContext, compUnit: CompilationUnit? = findParent(CompilationUnitI::class.java), position: TypePosition = SIGNATURE): String {
    if (type == td.Void) {
        return ""
    } else {
        return ": ${type.toKotlin(context, this, position = position)}${nullable.then("?")}"
    }
}

fun <T : Attribute> T.toKotlinType(context: KotlinContext): String {
    return type.toKotlin(context, this)
}

fun <T : Attribute> T.toKotlinMember(context: KotlinContext, indent: String = "",
                                     mappings: T.(context: KotlinContext, indent: String) -> String = { c, ind -> "" },
                                     modifier: String = open.then("open "), compUnit: CompilationUnit? = findParent(CompilationUnitI::class.java)): String {
    return "${toKotlinComment(context, indent)}${mappings(context, indent)}$indent$modifier${replaceable.ifElse("var", "val")} $name${toKotlinTypeDef(context, position = MEMBER)}${toKotlinInit(context)}"
}

fun <T : Attribute> T.toKotlinGetterImpl(context: KotlinContext, indent: String = "", mappings: T.(context: KotlinContext, indent: String) -> String = { c, ind -> "" }, modifier: String = ""): String {
    val newIndent = "$indent$tab"
    return """${toKotlinComment(context, indent)}${mappings(context, indent)}$indent$modifier$name(): ${type.toKotlin(context)} {
${newIndent}return $name
$indent}
"""
}

fun <T : Attribute> T.toKotlinAssign(context: KotlinContext, indent: String = ""): String {
    return "${indent}this.$name = $name"
}

fun <T : Attribute> T.toKotlinSetterImpl(context: KotlinContext, indent: String = "", mappings: T.(context: KotlinContext, indent: String) -> String = { c, ind -> "" }, modifier: String = ""): String {
    val newIndent = "$indent$tab"
    return """${toKotlinComment(context, indent)}${mappings(context, indent)}$indent$modifier$name($type.to name: ${type.toKotlin(context)}) {
${toKotlinAssign(context, newIndent)}
$indent}
"""
}

fun <T : Attribute> T.toKotlinValue(context: KotlinContext): String {
    if (value != null) {
        return when (this.type) {
            td.String -> "\"$value\""
            td.Boolean -> "$value"
            td.Integer -> "$value"
            td.Long -> "$value"
            td.Float -> "$value"
            td.Date -> "$value"
            td.Path -> "$value"
            td.Text -> "\"$value\""
            td.Blob -> "$value"
            td.Void -> "$value"
            else -> {
                if (value is Literal) {
                    val lit = value as Literal
                    "${(lit.parent as EnumTypeD).toKotlin(context)}.${lit.toKotlin(context)}"
                } else {
                    "$value"
                }
            }
        }
    } else {
        return toKotlinValueEmpty(context)
    }
}

fun <T : Attribute> T.toKotlinReturn(context: KotlinContext, indent: String): String {
    if (type == td.void) {
        return ""
    } else {
        return "${indent}return ${toKotlinValue(context)}"
    }
}

fun <T : Attribute> T.toKotlinInit(context: KotlinContext, override: Boolean = false): String {
    if (override || type is Generic) {
        return ""
    } else {
        if (value != null) {
            return " = ${toKotlinValue(context)}"
        } else if (nullable) {
            return " = null"
        } else if (initByDefaultTypeValue) {
            return " = ${toKotlinValue(context)}"
        } else {
            return ""
        }
    }
}

fun <T : Attribute> T.toKotlinSignature(context: KotlinContext, indent: String = "", definition: Boolean = false,
                                        modifier: String = "", override: Boolean = false, position: TypePosition = SIGNATURE,
                                        mappings: T.(context: KotlinContext, indent: String) -> String = { c, ind -> "" }): String {
    return "${toKotlinComment(context, indent)}${mappings(context, indent)}$modifier${definition.then { replaceable.ifElse("var ", "val ") }}$name${toKotlinTypeDef(context, position = position)}${toKotlinInit(context, override)}"
}

fun List<Attribute>.toKotlinTypes(context: KotlinContext, indent: String): String {
    return "${joinWrappedToString(", ", indent) { it.toKotlinType(context) }}"
}

fun List<Attribute>.toKotlinSignature(context: KotlinContext, indent: String, definition: Boolean = false, modifier: String = "", override: Boolean = false, position: TypePosition = SIGNATURE): String {
    return "${joinWrappedToString(", ", indent) { it.toKotlinSignature(context, indent, definition && !it.inherited, modifier, override, position = position) }}"
}

fun List<Attribute>.toKotlinMember(context: KotlinContext, indent: String, modifier: String = ""): String {
    return "${joinWrappedToString(", ", indent) { it.toKotlinSignature(context, indent, true && !it.inherited, modifier) }}"
}

fun <T : Operation> T.toKotlinIfc(context: KotlinContext, indent: String = "",
                                  mappings: T.(context: KotlinContext, indent: String) -> String = { c, ind -> "" }): String {
    return "${toKotlinComment(context, indent)}${mappings(context, indent)}$indent$name(${params.toKotlinSignature(context, indent)})${ret.toKotlinTypeDef(context)}"
}

fun <T : Operation> T.toKotlinAbstract(context: KotlinContext, indent: String = "",
                                       mappings: T.(context: KotlinContext, indent: String) -> String = { c, ind -> "" }, modifier: String = "abstract "): String {
    val newIndent = "$indent$tab"
    return """${toKotlinComment(context, indent)}${mappings(context, indent)}$indent${modifier}fun ${toKotlinGenerics(context, indent)}$name(${params.toKotlinSignature(context, indent, position = OPERATION)})${ret.toKotlinTypeDef(context)}
"""
}

fun <T : Operation> T.toKotlinLambda(context: KotlinContext, indent: String = ""): String {
    return """(${params.toKotlinTypes(context, indent)}) -> ${ret.toKotlinType(context)}"""
}

fun <T : Operation> T.toKotlinImpl(context: KotlinContext, indent: String = "", override: Boolean = false,
                                   mappings: T.(context: KotlinContext, indent: String) -> String = { c, ind -> "" }, modifier: String = "override "): String {
    val newIndent = "$indent$tab"
    return """${toKotlinComment(context, indent)}${mappings(context, indent)}$indent${modifier}fun ${toKotlinGenerics(context, indent)}$name(${params.toKotlinSignature(context, indent, override = override, position = OPERATION)})${ret.toKotlinTypeDef(context)} {
${newIndent}throw IllegalAccessException("Not implemented yet.")
$indent}
"""
}

fun <T : DelegateOperation> T.toKotlinImpl(context: KotlinContext, indent: String = "", override: Boolean = false,
                                           mappings: T.(context: KotlinContext, indent: String) -> String = { c, ind -> "" }, modifier: String = "override "): String {
    val newIndent = "$indent$tab"
    val opParent = operation.findParent(CompilationUnitD::class.java)
    return """${toKotlinComment(context, indent)}${mappings(context, indent)}$indent${modifier}fun ${toKotlinGenerics(context, indent)}$name(${openParams.toKotlinSignature(context, indent, override = override, position = OPERATION)})${operation.ret.toKotlinTypeDef(context)} {
$newIndent${(operation.ret != td.void).then("return ")}${opParent?.name?.decapitalize()}.${operation.toKotlinCall(context, indent, operation.name)}
$indent}
"""
}

fun <T : Attribute> T.toKotlinCall(context: KotlinContext, indent: String = ""): String {
    return "$name"
}

fun <T : Attribute> T.toKotlinCallValue(context: KotlinContext, indent: String = ""): String {
    return "$value"
}

fun <T : Attribute> T.toKotlinValueEmpty(context: KotlinContext, indent: String = ""): String {
    if (nullable) {
        return "null"
    } else {
        return "${type.toKotlinValue(context, this)}"
    }
}

fun List<Attribute>.toKotlinCall(context: KotlinContext, indent: String): String {
    return "${joinWrappedToString(", ", indent) { it.toKotlinCall(context, indent) }}"
}

fun List<Attribute>.toKotlinValue(context: KotlinContext, indent: String): String {
    return "${joinWrappedToString(", ", indent) { it.toKotlinValue(context) }}"
}

fun List<Attribute>.toKotlinValueEmpty(context: KotlinContext, indent: String): String {
    return "${joinWrappedToString(", ", indent) { it.toKotlinValueEmpty(context, indent) }}"
}

fun <T : LogicUnit> T.toKotlinCall(context: KotlinContext, indent: String = "", name: String = ""): String {
    return "$name(${params.toKotlinCall(context, indent)})"
}

fun <T : LogicUnit> T.toKotlinCallValue(context: KotlinContext, indent: String = "", name: String = ""): String {
    return "$name(${params.toKotlinValue(context, indent)})"
}

fun <T : LogicUnit> T.toKotlinCallDefaultInit(context: KotlinContext, indent: String = "", name: String = ""): String {
    return "$name(${params.filter { it.type is Generic }.toKotlinValue(context, indent)})"
}

fun <T : LogicUnit> T.toKotlinValueEmpty(context: KotlinContext, indent: String = "", name: String = ""): String {
    return "$name(${params.toKotlinValueEmpty(context, indent)})"
}

fun <T : Constructor> T.toKotlinCall(context: KotlinContext, indent: String = "", name: String = "this"): String {
    if (this != Constructor.EMPTY) {
        return ": $name(${params.toKotlinCall(context, "$indent$tab")})"
    } else {
        return ""
    }
}

fun <T : Constructor> T.toKotlinPrimary(context: KotlinContext, indent: String = "", superUnit: CompilationUnitD? = null,
                                        mappings: T.(context: KotlinContext, indent: String) -> String = { c, ind -> "" },
                                        modifier: String = ""): String {
    if (this != Constructor.EMPTY) {
        if (superUnit != null) {
            return """(${params.toKotlinMember(context, indent)})${superUnit.primaryConstructor.toKotlinCall(context, indent, context.n(superUnit))}"""
        } else {
            return """(${params.toKotlinMember(context, indent)})"""
        }
    } else {
        return ""
    }
}

fun <T : Constructor> T.toKotlin(context: KotlinContext, indent: String = "", superConstructor: Constructor,
                                 mappings: T.(context: KotlinContext, indent: String) -> String = { c, ind -> "" },
                                 modifier: String = ""): String {
    val newIndent = "$indent$tab"
    return """${toKotlinComment(context, indent)}${mappings(context, indent)}$indent${modifier}constructor(${params.toKotlinSignature(context, newIndent, position = MEMBER)})${superConstructor.toKotlinCall(context, "$newIndent$tab", "${(this == superConstructor || this.parent != superConstructor.parent).ifElse("super", "this")}")} {
${substractParamsOf(superConstructor).joinToString(nL) { it.toKotlinAssign(context, newIndent) }}
$indent}
"""
}

fun <T : Constructor> T.substractParamsOf(superConstructor: Constructor)
        = params.filter { param -> superConstructor.params.firstOrNull { it.name == param.name } == null }

class KotlinContext : GenerationContext {

    constructor(namespace: String = "", header: String = "", footer: String = "") : super(namespace, header, footer)

    override fun complete(content: String, indent: String): String {
        return "${toHeader(indent)}${toPackage(indent)}${toImports(indent)}$content${toFooter(indent)}"
    }

    private fun toPackage(indent: String): String {
        return namespace.isNotEmpty().then { "${indent}package $namespace$nL$nL" }
    }

    private fun toImports(indent: String): String {
        return types.isNotEmpty().then {
            val outsideTypes = types.filter { it.namespace.isNotEmpty() && it.namespace != namespace }
            outsideTypes.isEmpty().ifElse("", {
                "${outsideTypes.map { "${indent}import ${it.namespace}.${it.name}" }.sorted().
                        joinToString(nL)}$nL$nL"
            })
        }
    }
}

val CompilationUnitD.primaryConstructor: Constructor
    get() = storage.getOrPut(this, "primaryConstructor", { constructors.firstOrNull() ?: Constructor.EMPTY })

val CompilationUnitD.otherConstructors: List<Constructor>
    get() = storage.getOrPut(this, "otherConstructors", { if (constructors.size > 1) constructors.subList(1, constructors.size) else emptyList() })

val Constructor.props: List<Attribute>
    get() = storage.getOrPut(this, "props",
            { params.filterIsInstance(PropAttributeI::class.java).map { it.prop } })

val CompilationUnitD.propsExceptPrimaryConstructor: List<Attribute>
    get() = storage.getOrPut(this, "propsExceptPrimaryConstructor",
            { if (primaryConstructor != Constructor.EMPTY) props.filter { !primaryConstructor.props.contains(it) } else props })

fun CompilationUnitD.toKotlinIfc(context: KotlinContext, indent: String = "", derived: TypeDerived<CompilationUnitD> = api): String {
    val newIndent = "$indent$tab"
    return """${toKotlinComment(context, indent)}${indent}interface ${derived.name} {${
    props.joinToString(nL) { it.toKotlinMember(context, newIndent) }}${
    operations.joinToString(nL) { it.toKotlinIfc(context, newIndent) }}
$indent}"""
}

fun CompilationUnitD.toKotlinCompanionEmptySupport(context: KotlinContext, indent: String = "", derived: TypeDerived<CompilationUnitD>): String {
    if (virtual) return ""
    val newIndent = "$indent$tab"
    return """
${indent}companion object {
${newIndent}val EMPTY = ${derived.name}${toKotlinGenericTypes(context, indent)}${primaryConstructor.toKotlinCallDefaultInit(context)}
$indent}"""
}

fun CompilationUnitD.toKotlinCompanionExtendsEmptySupport(context: KotlinContext, indent: String = "", base: TypeDerived<CompilationUnitD>): String {
    if (virtual) return ""
    val newIndent = "$indent$tab"
    return """
${indent}companion object {
${newIndent}val EMPTY = ${base.name}.EMPTY
$indent}"""
}

fun CompilationUnitD.toKotlinOrEmptyExtension(context: KotlinContext, indent: String = "", derived: TypeDerived<CompilationUnitD>): String {
    if (virtual) return ""
    val newIndent = "$indent$tab"
    val type = "${derived.name}${toKotlinGenericsStar(context, indent)}"
    return """
${indent}fun ${derived.name}${toKotlinGenericsStar(context, indent)}?.orEmpty(): ${api.name}${toKotlinGenericsStar(context, indent)} {
${newIndent}return if (this != null) this${derived.base.then { " as ${api.name}${toKotlinGenericsStar(context, indent)}" }} else ${derived.name}.EMPTY
$indent}"""
}

fun TypeIfc.toKotlinGenericTypes(context: KotlinContext, indent: String): String {
    return """${generics.joinWrappedToString(", ", indent, "<", ">") { "${it.type.toKotlin(context)}" }}"""
}

fun GenericD.toKotlin(context: KotlinContext, base: TypeIfc? = null): String {
    val parentGeneric = base?.findGeneric(name)
    return if (parentGeneric != null) name else context.n(type)
}

fun TypeIfc.toKotlinGenerics(context: KotlinContext, indent: String, base: TypeIfc? = null): String {
    return """${generics.joinWrappedToString(", ", indent, "<", ">") { "${it.toKotlin(context, base)}" }}"""
}

fun TypeIfc.toKotlinGenericsClassDef(context: KotlinContext, indent: String): String {
    return """${generics.joinWrappedToString(", ", indent, "<", ">") { "${it.name} : ${it.type.toKotlin(context)}" }}"""
}

fun TypeIfc.toKotlinGenericsMethodDef(context: KotlinContext, indent: String): String {
    return """${generics.joinWrappedToString(", ", indent, "<", "> ") { "${it.name} : ${it.type.toKotlin(context)}" }}"""
}

fun TypeIfc.toKotlinGenericsStar(context: KotlinContext, indent: String): String {
    return """${generics.joinWrappedToString(", ", indent, "<", "> ") { "*" }}"""
}

fun Operation.toKotlinGenerics(context: KotlinContext, indent: String): String {
    return """${generics.joinWrappedToString(", ", indent, "<", "> ") { "${it.name} : ${it.type.toKotlin(context)}" }}"""
}

fun CompilationUnitD.toKotlinClassDef(context: KotlinContext, indent: String, derived: TypeDerived<CompilationUnitD>, dataClass: Boolean = false): String {
    return """$indent${dataClass.ifElse("data class", { (virtual || derived.base).ifElse("abstract class", { open.ifElse("open class", "class") }) })} ${derived.name}${toKotlinGenericsClassDef(context, indent)}"""
}

fun CompilationUnitD.toKotlinImpl(context: KotlinContext, indent: String = "",
                                  derived: TypeDerived<CompilationUnitD> = base.ifElse(apiBase, api),
                                  emptySupport: Boolean = true): String {
    val newIndent = "$indent$tab"
    val classDef = toKotlinClassDef(context, indent, derived)
    val signatureIndent = "".padEnd(classDef.length + 1)
    return """${toKotlinComment(context, indent)}$classDef${(superUnit != null).then { " : ${context.n(superUnit!!.api)}${superUnit!!.toKotlinGenerics(context, indent, this)}" }} {${
    emptySupport.then { toKotlinCompanionEmptySupport(context, newIndent, api) }}${
    props.joinToString(nL) { it.toKotlinMember(context, newIndent) }}${
    constructors.joinWrappedToString(nL) { it.toKotlin(context, newIndent, superUnit?.primaryConstructor ?: Constructor.EMPTY) }}${
    delegates.joinToString(nL) { it.toKotlinImpl(context, newIndent) }}${
    derived.base.ifElse({ operations.joinToString(nL) { it.toKotlinAbstract(context, newIndent) } }, { operations.joinToString(nL) { it.toKotlinImpl(context, newIndent) } })}
$indent}${emptySupport.then {toKotlinOrEmptyExtension(context, indent, derived)}}"""
}

fun CompilationUnitD.toKotlinExtends(context: KotlinContext, indent: String = "",
                                     derived: TypeDerived<CompilationUnitD> = api,
                                     extends: TypeDerived<CompilationUnitD> = apiBase,
                                     emptySupport: Boolean = true): String {
    val newIndent = "$indent$tab"
    val classDef = toKotlinClassDef(context, indent, derived)
    return """${toKotlinComment(context, indent)}$classDef${" : ${context.n(extends)}${extends.toKotlinGenerics(context, indent, this)}"} {${
    emptySupport.then { toKotlinCompanionExtendsEmptySupport(context, newIndent, extends) }}${
    constructors.joinWrappedToString(nL) { it.toKotlin(context, newIndent, it) }}${
    operations.joinToString(nL) { it.toKotlinImpl(context, newIndent, override = true) }}
$indent}"""
}

fun EnumTypeD.toKotlinEnum(context: KotlinContext, indent: String = "", derived: TypeDerived<EnumTypeD> = enum): String {
    val newIndent = "$indent$tab"
    return """${toKotlinComment(context, indent)}${indent}enum class ${derived.name}${primaryConstructor.toKotlinPrimary(context, "", superUnit)} {
${literals.joinToString(",$nL") { it.toKotlinLiteral(context, newIndent) }};${
    propsExceptPrimaryConstructor.joinToString(nL) { it.toKotlinMember(context, newIndent) }}${
    operations.joinToString(nL) { it.toKotlinImpl(context, newIndent) }}
${literals.joinToString(nL) { it.toKotlinIs(context, newIndent) }}
$indent}

${indent}fun String?.to${derived.name}(): ${derived.name} {
${newIndent}return if (this != null) {
$newIndent$tab${derived.name}.valueOf(this)
$newIndent} else {
$newIndent$tab${derived.name}.${literals.first().toKotlin(context, "")}
$newIndent}
$indent}"""
}
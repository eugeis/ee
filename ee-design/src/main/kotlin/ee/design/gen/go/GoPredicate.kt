package ee.design.gen.go

import ee.common.ext.joinSurroundIfNotEmptyToString
import ee.design.DesignDerivedKind
import ee.design.HandlerI
import ee.lang.*
import java.util.*


fun <T : HandlerI<*>> T.toGoPredicates(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
    val trueExp = ifTrue().joinSurroundIfNotEmptyToString(" && ") { pred ->
        pred.toGo(c, derived, api)
    }

    val falseExp = ifFalse().joinSurroundIfNotEmptyToString(" && ") { pred ->
        "!(${pred.toGo(c, derived, api)})"
    }

    return if (trueExp.isNotEmpty() && falseExp.isNotEmpty()) {
        "$trueExp && $falseExp"
    } else if (trueExp.isNotEmpty()) {
        trueExp
    } else if (falseExp.isNotEmpty()) {
        falseExp
    } else {
        ""
    }
}

fun <T : PredicateI<*>> T.toGo(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
    return when (this) {
        is EqPredicateI<*> ->
            toGo(c, derived, api)
        is GtPredicateI<*> ->
            toGo(c, derived, api)
        is GtePredicateI<*> ->
            toGo(c, derived, api)
        is LtPredicateI<*> ->
            toGo(c, derived, api)
        is LtePredicateI<*> ->
            toGo(c, derived, api)
        is NePredicateI<*> ->
            toGo(c, derived, api)
        else -> "not supported the predicate yet $this"
    }
}

fun <T : EqPredicateI<*>> T.toGo(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String =
    if (right().name().isEmpty()) {
        left().toGoAccess(c, derived, api)
    } else {
        "${left().toGoAccess(c, derived, api)} == ${right().toGoAccess(c, derived, api)}"
    }

fun <T : GtPredicateI<*>> T.toGo(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String =
    "${left().toGoAccess(c, derived, api)} > ${right().toGoAccess(c, derived, api)}"

fun <T : GtePredicateI<*>> T.toGo(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String =
    "${left().toGoAccess(c, derived, api)} >= ${right().toGoAccess(c, derived, api)}"

fun <T : LtPredicateI<*>> T.toGo(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String =
    "${left().toGoAccess(c, derived, api)} < ${right().toGoAccess(c, derived, api)}"

fun <T : LtePredicateI<*>> T.toGo(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String =
    "${left().toGoAccess(c, derived, api)} <= ${right().toGoAccess(c, derived, api)}"

fun <T : NePredicateI<*>> T.toGo(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String =
    if (right().name().isEmpty()) {
        "!${left().toGoAccess(c, derived, api)}"
    } else {
        "${left().toGoAccess(c, derived, api)} != ${right().toGoAccess(c, derived, api)}"
    }

fun <T : LiteralI<*>> T.toGoAccess(
    c: GenerationContext, derived: String = DesignDerivedKind.IMPL, api: String = DesignDerivedKind.API
): String {
    return "${parent().name().replaceFirstChar { it.lowercase(Locale.getDefault()) }}.${name().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
}
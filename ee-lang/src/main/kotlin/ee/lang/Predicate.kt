package ee.lang

open class Predicate : Item()

/**
 * Not expression
 */
class NotExpression(val param: Any?) : Predicate()

fun not(predicate: Predicate): NotExpression {
    return NotExpression(predicate)
}

/**
 * And expression
 */
class AndExpression(val left: Any?, val right: Any?) : Predicate()

infix fun Predicate.and(predicate: Predicate): AndExpression {
    return AndExpression(this, predicate)
}

/**
 * Or expression
 */
class OrExpression(val left: Any?, val right: Any?) : Predicate()

infix fun Predicate.or(predicate: Predicate): OrExpression {
    return OrExpression(this, predicate)
}

/**
 * Equals expression
 */
class EqExpression(val left: Any?, val right: Any?) : Predicate()

infix fun LiteralI<*>.eq(column: LiteralI<*>): EqExpression {
    return EqExpression(this, column)
}

infix fun LiteralI<*>.eq(str: String?): EqExpression {
    return EqExpression(this, str)
}

infix fun LiteralI<*>.eq(num: Number): EqExpression {
    return EqExpression(this, num)
}

infix fun LiteralI<*>.eq(flag: Boolean): EqExpression {
    return EqExpression(this, flag)
}

/**
 * Not equals expression
 */
class NeExpression(val left: Any?, val right: Any?) : Predicate()

infix fun LiteralI<*>.ne(column: LiteralI<*>): NeExpression {
    return NeExpression(this, column)
}

infix fun LiteralI<*>.ne(str: String?): NeExpression {
    return NeExpression(this, str)
}

infix fun LiteralI<*>.ne(num: Number): NeExpression {
    return NeExpression(this, num)
}

infix fun LiteralI<*>.ne(flag: Boolean): NeExpression {
    return NeExpression(this, flag)
}

/**
 * Less than expression
 */
class LtExpression(val left: Any?, val right: Any?) : Predicate()

infix fun LiteralI<*>.lt(column: LiteralI<*>): LtExpression {
    return LtExpression(this, column)
}

infix fun LiteralI<*>.lt(str: String?): LtExpression {
    return LtExpression(this, str)
}

infix fun LiteralI<*>.lt(num: Number): LtExpression {
    return LtExpression(this, num)
}

/**
 * Less than or equal expression
 */
class LteExpression(val left: Any?, val right: Any?) : Predicate()

infix fun LiteralI<*>.lte(column: LiteralI<*>): LteExpression {
    return LteExpression(this, column)
}

infix fun LiteralI<*>.lte(str: String?): LteExpression {
    return LteExpression(this, str)
}

infix fun LiteralI<*>.lte(num: Number): LteExpression {
    return LteExpression(this, num)
}

/**
 * Greater than expression
 */
class GtExpression(val left: Any?, val right: Any?) : Predicate()

infix fun LiteralI<*>.gt(column: LiteralI<*>): GtExpression {
    return GtExpression(this, column)
}

infix fun LiteralI<*>.gt(str: String?): GtExpression {
    return GtExpression(this, str)
}

infix fun LiteralI<*>.gt(num: Number): GtExpression {
    return GtExpression(this, num)
}

/**
 * Greater than or equal expression
 */
class GteExpression(val left: Any?, val right: Any?) : Predicate()

infix fun LiteralI<*>.gte(column: LiteralI<*>): GteExpression {
    return GteExpression(this, column)
}

infix fun LiteralI<*>.gte(str: String?): GteExpression {
    return GteExpression(this, str)
}

infix fun LiteralI<*>.gte(num: Number): GteExpression {
    return GteExpression(this, num)
}
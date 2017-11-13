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

infix fun LiteralIB<*>.eq(column: LiteralIB<*>): EqExpression {
    return EqExpression(this, column)
}

infix fun LiteralIB<*>.eq(str: String?): EqExpression {
    return EqExpression(this, str)
}

infix fun LiteralIB<*>.eq(num: Number): EqExpression {
    return EqExpression(this, num)
}

infix fun LiteralIB<*>.eq(flag: Boolean): EqExpression {
    return EqExpression(this, flag)
}

/**
 * Not equals expression
 */
class NeExpression(val left: Any?, val right: Any?) : Predicate()

infix fun LiteralIB<*>.ne(column: LiteralIB<*>): NeExpression {
    return NeExpression(this, column)
}

infix fun LiteralIB<*>.ne(str: String?): NeExpression {
    return NeExpression(this, str)
}

infix fun LiteralIB<*>.ne(num: Number): NeExpression {
    return NeExpression(this, num)
}

infix fun LiteralIB<*>.ne(flag: Boolean): NeExpression {
    return NeExpression(this, flag)
}

/**
 * Less than expression
 */
class LtExpression(val left: Any?, val right: Any?) : Predicate()

infix fun LiteralIB<*>.lt(column: LiteralIB<*>): LtExpression {
    return LtExpression(this, column)
}

infix fun LiteralIB<*>.lt(str: String?): LtExpression {
    return LtExpression(this, str)
}

infix fun LiteralIB<*>.lt(num: Number): LtExpression {
    return LtExpression(this, num)
}

/**
 * Less than or equal expression
 */
class LteExpression(val left: Any?, val right: Any?) : Predicate()

infix fun LiteralIB<*>.lte(column: LiteralIB<*>): LteExpression {
    return LteExpression(this, column)
}

infix fun LiteralIB<*>.lte(str: String?): LteExpression {
    return LteExpression(this, str)
}

infix fun LiteralIB<*>.lte(num: Number): LteExpression {
    return LteExpression(this, num)
}

/**
 * Greater than expression
 */
class GtExpression(val left: Any?, val right: Any?) : Predicate()

infix fun LiteralIB<*>.gt(column: LiteralIB<*>): GtExpression {
    return GtExpression(this, column)
}

infix fun LiteralIB<*>.gt(str: String?): GtExpression {
    return GtExpression(this, str)
}

infix fun LiteralIB<*>.gt(num: Number): GtExpression {
    return GtExpression(this, num)
}

/**
 * Greater than or equal expression
 */
class GteExpression(val left: Any?, val right: Any?) : Predicate()

infix fun LiteralIB<*>.gte(column: LiteralIB<*>): GteExpression {
    return GteExpression(this, column)
}

infix fun LiteralIB<*>.gte(str: String?): GteExpression {
    return GteExpression(this, str)
}

infix fun LiteralIB<*>.gte(num: Number): GteExpression {
    return GteExpression(this, num)
}
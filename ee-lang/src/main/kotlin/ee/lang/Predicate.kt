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

infix fun AttributeI.eq(column: AttributeI): EqExpression {
    return EqExpression(this, column)
}

infix fun AttributeI.eq(str: String?): EqExpression {
    return EqExpression(this, str)
}

infix fun AttributeI.eq(num: Number): EqExpression {
    return EqExpression(this, num)
}

infix fun AttributeI.eq(flag: Boolean): EqExpression {
    return EqExpression(this, flag)
}

/**
 * Not equals expression
 */
class NeExpression(val left: Any?, val right: Any?) : Predicate()

infix fun AttributeI.ne(column: AttributeI): NeExpression {
    return NeExpression(this, column)
}

infix fun AttributeI.ne(str: String?): NeExpression {
    return NeExpression(this, str)
}

infix fun AttributeI.ne(num: Number): NeExpression {
    return NeExpression(this, num)
}

infix fun AttributeI.ne(flag: Boolean): NeExpression {
    return NeExpression(this, flag)
}

/**
 * Less than expression
 */
class LtExpression(val left: Any?, val right: Any?) : Predicate()

infix fun AttributeI.lt(column: AttributeI): LtExpression {
    return LtExpression(this, column)
}

infix fun AttributeI.lt(str: String?): LtExpression {
    return LtExpression(this, str)
}

infix fun AttributeI.lt(num: Number): LtExpression {
    return LtExpression(this, num)
}

/**
 * Less than or equal expression
 */
class LteExpression(val left: Any?, val right: Any?) : Predicate()

infix fun AttributeI.lte(column: AttributeI): LteExpression {
    return LteExpression(this, column)
}

infix fun AttributeI.lte(str: String?): LteExpression {
    return LteExpression(this, str)
}

infix fun AttributeI.lte(num: Number): LteExpression {
    return LteExpression(this, num)
}

/**
 * Greater than expression
 */
class GtExpression(val left: Any?, val right: Any?) : Predicate()

infix fun AttributeI.gt(column: AttributeI): GtExpression {
    return GtExpression(this, column)
}

infix fun AttributeI.gt(str: String?): GtExpression {
    return GtExpression(this, str)
}

infix fun AttributeI.gt(num: Number): GtExpression {
    return GtExpression(this, num)
}

/**
 * Greater than or equal expression
 */
class GteExpression(val left: Any?, val right: Any?) : Predicate()

infix fun AttributeI.gte(column: AttributeI): GteExpression {
    return GteExpression(this, column)
}

infix fun AttributeI.gte(str: String?): GteExpression {
    return GteExpression(this, str)
}

infix fun AttributeI.gte(num: Number): GteExpression {
    return GteExpression(this, num)
}
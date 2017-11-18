package ee.lang

fun not(predicate: Predicate): NotExpression = NotExpression { value(predicate) }
infix fun Predicate.and(predicate: Predicate): AndExpression = AndExpression { left(this).right(predicate) }
infix fun Predicate.or(predicate: Predicate): OrExpression = OrExpression { left(this).right(predicate) }

infix fun LiteralI<*>.eq(value: LiteralI<*>): EqExpression = EqExpression { left(this@eq).right(value) }
infix fun LiteralI<*>.eq(value: String?): EqExpression = eq(Literal { value(value).type(n.String) })
infix fun LiteralI<*>.eq(value: Long): EqExpression = eq(Literal { value(value).type(n.Long) })
infix fun LiteralI<*>.eq(value: Boolean): EqExpression = eq(Literal { value(value).type(n.Boolean) })

infix fun LiteralI<*>.ne(value: LiteralI<*>): NeExpression = NeExpression { left(this@ne).right(value) }
infix fun LiteralI<*>.ne(value: String?): NeExpression = ne(Literal { value(value).type(n.String) })
infix fun LiteralI<*>.ne(value: Long): NeExpression = ne(Literal { value(value).type(n.Long) })
infix fun LiteralI<*>.ne(value: Boolean): NeExpression = ne(Literal { value(value).type(n.Boolean) })

infix fun LiteralI<*>.lt(value: LiteralI<*>): LtExpression = LtExpression { left(this@lt).right(value) }
infix fun LiteralI<*>.lt(value: String?): LtExpression = lt(Literal { value(value).type(n.String) })
infix fun LiteralI<*>.lt(value: Long): LtExpression = lt(Literal { value(value).type(n.Long) })

infix fun LiteralI<*>.lte(value: LiteralI<*>): LteExpression = LteExpression { left(this@lte).right(value) }
infix fun LiteralI<*>.lte(value: String?): LteExpression = lte(Literal { value(value).type(n.String) })
infix fun LiteralI<*>.lte(value: Long): LteExpression = lte(Literal { value(value).type(n.Long) })

infix fun LiteralI<*>.gt(value: LiteralI<*>): GtExpression = GtExpression { left(this@gt).right(value) }
infix fun LiteralI<*>.gt(value: String?): GtExpression = gt(Literal { value(value).type(n.String) })
infix fun LiteralI<*>.gt(value: Long): GtExpression = gt(Literal { value(value).type(n.Long) })

infix fun LiteralI<*>.gte(value: LiteralI<*>): GteExpression = GteExpression { left(this@gte).right(value) }
infix fun LiteralI<*>.gte(value: String?): GteExpression = gte(Literal { value(value).type(n.String) })
infix fun LiteralI<*>.gte(value: Long): GteExpression = gte(Literal { value(value).type(n.Long) })
package ee.lang

fun not(predicate: Predicate): NotPredicate = NotPredicate { value(predicate) }
infix fun Predicate.and(predicate: Predicate): AndPredicate = AndPredicate { left(this).right(predicate) }
infix fun Predicate.or(predicate: Predicate): OrPredicate = OrPredicate { left(this).right(predicate) }

infix fun LiteralI<*>.eq(value: LiteralI<*>): EqPredicate = EqPredicate { left(this@eq).right(value) }
infix fun LiteralI<*>.eq(value: String?): EqPredicate = eq(Literal { value(value).type(n.String) })
infix fun LiteralI<*>.eq(value: Long): EqPredicate = eq(Literal { value(value).type(n.Long) })
infix fun LiteralI<*>.eq(value: Boolean): EqPredicate = eq(Literal { value(value).type(n.Boolean) })
fun LiteralI<*>.yes(): EqPredicate = eq(true)

infix fun LiteralI<*>.ne(value: LiteralI<*>): NePredicate = NePredicate { left(this@ne).right(value) }
infix fun LiteralI<*>.ne(value: String?): NePredicate = ne(Literal { value(value).type(n.String) })
infix fun LiteralI<*>.ne(value: Long): NePredicate = ne(Literal { value(value).type(n.Long) })
infix fun LiteralI<*>.ne(value: Boolean): NePredicate = ne(Literal { value(value).type(n.Boolean) })
fun LiteralI<*>.no(): EqPredicate = eq(false)

infix fun LiteralI<*>.lt(value: LiteralI<*>): LtPredicate = LtPredicate { left(this@lt).right(value) }
infix fun LiteralI<*>.lt(value: String?): LtPredicate = lt(Literal { value(value).type(n.String) })
infix fun LiteralI<*>.lt(value: Long): LtPredicate = lt(Literal { value(value).type(n.Long) })

infix fun LiteralI<*>.lte(value: LiteralI<*>): LtePredicate = LtePredicate { left(this@lte).right(value) }
infix fun LiteralI<*>.lte(value: String?): LtePredicate = lte(Literal { value(value).type(n.String) })
infix fun LiteralI<*>.lte(value: Long): LtePredicate = lte(Literal { value(value).type(n.Long) })

infix fun LiteralI<*>.gt(value: LiteralI<*>): GtPredicate = GtPredicate { left(this@gt).right(value) }
infix fun LiteralI<*>.gt(value: String?): GtPredicate = gt(Literal { value(value).type(n.String) })
infix fun LiteralI<*>.gt(value: Long): GtPredicate = gt(Literal { value(value).type(n.Long) })

infix fun LiteralI<*>.gte(value: LiteralI<*>): GtePredicate = GtePredicate { left(this@gte).right(value) }
infix fun LiteralI<*>.gte(value: String?): GtePredicate = gte(Literal { value(value).type(n.String) })
infix fun LiteralI<*>.gte(value: Long): GtePredicate = gte(Literal { value(value).type(n.Long) })

infix fun LiteralI<*>.compareTo(value: LiteralI<*>): GtePredicate = GtePredicate { left(this@compareTo).right(value) }

operator fun LiteralI<*>.plus(value: LiteralI<*>): LiteralI<*> = PlusExpression { left(this@plus).right(value) }
operator fun LiteralI<*>.plus(value: Long): LiteralI<*> = plus(Literal { value(value).type(n.Long) })
operator fun LiteralI<*>.plus(value: Int): LiteralI<*> = plus(Literal { value(value).type(n.Int) })
operator fun LiteralI<*>.plus(value: Float): LiteralI<*> = plus(Literal { value(value).type(n.Float) })

operator fun LiteralI<*>.minus(value: LiteralI<*>): LiteralI<*> = MinusExpression { left(this@minus).right(value) }
operator fun LiteralI<*>.minus(value: Long): LiteralI<*> = minus(Literal { value(value).type(n.Long) })
operator fun LiteralI<*>.minus(value: Int): LiteralI<*> = minus(Literal { value(value).type(n.Int) })
operator fun LiteralI<*>.minus(value: Float): LiteralI<*> = minus(Literal { value(value).type(n.Float) })

operator fun LiteralI<*>.inc(): LiteralI<*> = IncrementExpression { value(this@inc) }
operator fun LiteralI<*>.dec(): LiteralI<*> = DecrementExpression { value(this@dec) }

operator fun LiteralI<*>.times(value: LiteralI<*>): LiteralI<*> = TimesExpression { left(this@times).right(value) }
operator fun LiteralI<*>.times(value: Long): LiteralI<*> = times(Literal { value(value).type(n.Long) })
operator fun LiteralI<*>.times(value: Int): LiteralI<*> = times(Literal { value(value).type(n.Int) })
operator fun LiteralI<*>.times(value: Float): LiteralI<*> = times(Literal { value(value).type(n.Float) })

operator fun LiteralI<*>.div(value: LiteralI<*>): LiteralI<*> = DivideExpression { left(this@div).right(value) }
operator fun LiteralI<*>.div(value: Long): LiteralI<*> = div(Literal { value(value).type(n.Long) })
operator fun LiteralI<*>.div(value: Int): LiteralI<*> = div(Literal { value(value).type(n.Int) })
operator fun LiteralI<*>.div(value: Float): LiteralI<*> = div(Literal { value(value).type(n.Float) })

fun AttributeI<*>.assign(value: LiteralI<*>): ActionI<*> = AssignAction { target(this@assign).value(value) }

package ee.lang


object l : StructureUnit({ namespace("ee.lang") }) {
    val Action = CompilationUnit { derivedFrom(LogicUnit) }
    val AndPredicate = CompilationUnit { derivedFrom(LeftRightPredicatesPredicate) }
    val ApplyAction = CompilationUnit { derivedFrom(Action) }
    val AssignAction = CompilationUnit { derivedFrom(ApplyAction) }
    val Attribute = CompilationUnit { derivedFrom(Literal) }
    val Comment = CompilationUnit { derivedFrom(Composite) }
    val CompilationUnit = CompilationUnit { derivedFrom(Type) }
    val Composite = CompilationUnit { derivedFrom(MapMultiHolder) }
    val Constructor = CompilationUnit { derivedFrom(LogicUnit) }
    val DataType = CompilationUnit { derivedFrom(CompilationUnit) }
    val DataTypeOperation = CompilationUnit { derivedFrom(Operation) }
    val DecrementExpression = CompilationUnit { derivedFrom(Literal) }
    val DivideAssignAction = CompilationUnit { derivedFrom(ApplyAction) }
    val DivideExpression = CompilationUnit { derivedFrom(LeftRightLiteral) }
    val EnumType = CompilationUnit { derivedFrom(DataType) }
    val EqPredicate = CompilationUnit { derivedFrom(LeftRightPredicate) }
    val Expression = CompilationUnit { derivedFrom(MacroComposite) }
    val ExternalType = CompilationUnit { derivedFrom(Type) }
    val Generic = CompilationUnit { derivedFrom(Type) }
    val GtPredicate = CompilationUnit { derivedFrom(LeftRightPredicate) }
    val GtePredicate = CompilationUnit { derivedFrom(LeftRightPredicate) }
    val IncrementExpression = CompilationUnit { derivedFrom(Literal) }
    val Item = CompilationUnit
    val Lambda = CompilationUnit { derivedFrom(Type) }
    val LeftRightLiteral = CompilationUnit { derivedFrom(Literal) }
    val LeftRightPredicate = CompilationUnit { derivedFrom(Predicate) }
    val LeftRightPredicatesPredicate = CompilationUnit { derivedFrom(Predicate) }
    val ListMultiHolder = CompilationUnit { derivedFrom(MultiHolder) }
    val Literal = CompilationUnit { derivedFrom(Expression) }
    val LogicUnit = CompilationUnit { derivedFrom(Expression) }
    val LtPredicate = CompilationUnit { derivedFrom(LeftRightPredicate) }
    val LtePredicate = CompilationUnit { derivedFrom(LeftRightPredicate) }
    val MacroComposite = CompilationUnit { derivedFrom(Composite) }
    val MapMultiHolder = CompilationUnit { derivedFrom(MultiHolder) }
    val MinusAssignAction = CompilationUnit { derivedFrom(ApplyAction) }
    val MinusExpression = CompilationUnit { derivedFrom(LeftRightLiteral) }
    val NativeType = CompilationUnit { derivedFrom(Type) }
    val NePredicate = CompilationUnit { derivedFrom(LeftRightPredicate) }
    val NotPredicate = CompilationUnit { derivedFrom(Predicate) }
    val Operation = CompilationUnit { derivedFrom(LogicUnit) }
    val OrPredicate = CompilationUnit { derivedFrom(LeftRightPredicatesPredicate) }
    val PlusAssignAction = CompilationUnit { derivedFrom(ApplyAction) }
    val PlusExpression = CompilationUnit { derivedFrom(LeftRightLiteral) }
    val Predicate = CompilationUnit { derivedFrom(Expression) }
    val RemainderAssignAction = CompilationUnit { derivedFrom(ApplyAction) }
    val RemainderExpression = CompilationUnit { derivedFrom(LeftRightLiteral) }
    val StructureUnit = CompilationUnit { derivedFrom(MacroComposite) }
    val TimesAssignAction = CompilationUnit { derivedFrom(ApplyAction) }
    val TimesExpression = CompilationUnit { derivedFrom(LeftRightLiteral) }
    val Type = CompilationUnit { derivedFrom(MacroComposite) }

    object MultiHolder : CompilationUnit({ derivedFrom(Item) }) {
        val T = G { type(Item) }
    }
}
package ee.lang


object l : StructureUnit({ namespace("ee.lang") }) {
    val AndExpression = CompilationUnit { derivedFrom(LeftRightPredicateExpression) }
    val Attribute = CompilationUnit { derivedFrom(Literal) }
    val Comment = CompilationUnit { derivedFrom(Composite) }
    val CompilationUnit = CompilationUnit { derivedFrom(Type) }
    val Composite = CompilationUnit { derivedFrom(MapMultiHolder) }
    val Constructor = CompilationUnit { derivedFrom(LogicUnit) }
    val DataType = CompilationUnit { derivedFrom(CompilationUnit) }
    val DataTypeOperation = CompilationUnit { derivedFrom(Operation) }
    val DecrementExpression = CompilationUnit { derivedFrom(SingleLiteralExpression) }
    val DivideExpression = CompilationUnit { derivedFrom(LeftRightLiteralExpression) }
    val EnumType = CompilationUnit { derivedFrom(DataType) }
    val EqExpression = CompilationUnit { derivedFrom(LeftRightExpression) }
    val Expression = CompilationUnit { derivedFrom(MacroComposite) }
    val ExternalType = CompilationUnit { derivedFrom(Type) }
    val Generic = CompilationUnit { derivedFrom(Type) }
    val GtExpression = CompilationUnit { derivedFrom(LeftRightExpression) }
    val GteExpression = CompilationUnit { derivedFrom(LeftRightExpression) }
    val IncrementExpression = CompilationUnit { derivedFrom(SingleLiteralExpression) }
    val Item = CompilationUnit
    val Lambda = CompilationUnit { derivedFrom(Type) }
    val LeftRightExpression = CompilationUnit { derivedFrom(Predicate) }
    val LeftRightLiteralExpression = CompilationUnit { derivedFrom(Literal) }
    val LeftRightPredicateExpression = CompilationUnit { derivedFrom(Predicate) }
    val ListMultiHolder = CompilationUnit { derivedFrom(MultiHolder) }
    val Literal = CompilationUnit { derivedFrom(Expression) }
    val LogicUnit = CompilationUnit { derivedFrom(Expression) }
    val LtExpression = CompilationUnit { derivedFrom(LeftRightExpression) }
    val LteExpression = CompilationUnit { derivedFrom(LeftRightExpression) }
    val MacroComposite = CompilationUnit { derivedFrom(Composite) }
    val MapMultiHolder = CompilationUnit { derivedFrom(MultiHolder) }
    val MinusExpression = CompilationUnit { derivedFrom(LeftRightLiteralExpression) }
    val ModuloExpression = CompilationUnit { derivedFrom(LeftRightLiteralExpression) }
    val NativeType = CompilationUnit { derivedFrom(Type) }
    val NeExpression = CompilationUnit { derivedFrom(LeftRightExpression) }
    val NotExpression = CompilationUnit { derivedFrom(Predicate) }
    val Operation = CompilationUnit { derivedFrom(LogicUnit) }
    val OrExpression = CompilationUnit { derivedFrom(LeftRightPredicateExpression) }
    val PlusExpression = CompilationUnit { derivedFrom(LeftRightLiteralExpression) }
    val Predicate = CompilationUnit { derivedFrom(Expression) }
    val SingleLiteralExpression = CompilationUnit { derivedFrom(Literal) }
    val StructureUnit = CompilationUnit { derivedFrom(MacroComposite) }
    val TimesExpression = CompilationUnit { derivedFrom(LeftRightLiteralExpression) }
    val Type = CompilationUnit { derivedFrom(MacroComposite) }

    object MultiHolder : CompilationUnit({ derivedFrom(Item) }) {
        val T = G { type(Item) }
    }
}
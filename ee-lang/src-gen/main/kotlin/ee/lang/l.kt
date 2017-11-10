package ee.lang


object l : StructureUnit({ namespace("ee.lang") }) {
    val Attribute = CompilationUnit { derivedFrom(Literal) }
    val Comment = CompilationUnit { derivedFrom(Composite) }
    val CompilationUnit = CompilationUnit { derivedFrom(Type) }
    val Composite = CompilationUnit { derivedFrom(MapMultiHolder) }
    val Constructor = CompilationUnit { derivedFrom(LogicUnit) }
    val DataType = CompilationUnit { derivedFrom(CompilationUnit) }
    val DataTypeOperation = CompilationUnit { derivedFrom(Operation) }
    val EnumType = CompilationUnit { derivedFrom(DataType) }
    val Expression = CompilationUnit { derivedFrom(MacroComposite) }
    val ExternalType = CompilationUnit { derivedFrom(Type) }
    val Generic = CompilationUnit { derivedFrom(Type) }
    val Item = CompilationUnit
    val Lambda = CompilationUnit { derivedFrom(Type) }
    val ListMultiHolder = CompilationUnit { derivedFrom(MultiHolder) }
    val Literal = CompilationUnit { derivedFrom(LogicUnit) }
    val LogicUnit = CompilationUnit { derivedFrom(Expression) }
    val MacroComposite = CompilationUnit { derivedFrom(Composite) }
    val MapMultiHolder = CompilationUnit { derivedFrom(MultiHolder) }
    val NativeType = CompilationUnit { derivedFrom(Type) }
    val Operation = CompilationUnit { derivedFrom(LogicUnit) }
    val StructureUnit = CompilationUnit { derivedFrom(MacroComposite) }
    val Type = CompilationUnit { derivedFrom(MacroComposite) }

    object MultiHolder : CompilationUnit({ derivedFrom(Item) }) {
        val T = G { type(Item) }
    }
}
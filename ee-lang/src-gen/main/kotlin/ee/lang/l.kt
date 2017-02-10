package ee.lang


object l : StructureUnit({ namespace("ee.lang") }) {
    val Attribute = CompilationUnit({ derivedFrom(Composite) })
    val Comment = CompilationUnit({ derivedFrom(Composite) })
    val CompilationUnit = CompilationUnit({ derivedFrom(Type) })
    val Composite = CompilationUnit({ derivedFrom(TypedComposite) })
    val Constructor = CompilationUnit({ derivedFrom(LogicUnit) })
    val EnumType = CompilationUnit({ derivedFrom(CompilationUnit) })
    val ExternalType = CompilationUnit({ derivedFrom(Type) })
    val Generic = CompilationUnit({ derivedFrom(Type) })
    val Item = CompilationUnit()
    val Lambda = CompilationUnit({ derivedFrom(Type) })
    val Literal = CompilationUnit({ derivedFrom(LogicUnit) })
    val LogicUnit = CompilationUnit({ derivedFrom(TextComposite) })
    val NativeType = CompilationUnit({ derivedFrom(Type) })
    val NullValueHolder = CompilationUnit({ derivedFrom(Item) })
    val Operation = CompilationUnit({ derivedFrom(LogicUnit) })
    val StructureUnit = CompilationUnit({ derivedFrom(Composite) })
    val TextComposite = CompilationUnit({ derivedFrom(Composite) })
    val Type = CompilationUnit({ derivedFrom(Composite) })
    val ValueHolder = CompilationUnit({ derivedFrom(Item) })

    object TypedComposite : CompilationUnit({ derivedFrom(Item) }) {
        val T = G({ type(Item) })
    }
}
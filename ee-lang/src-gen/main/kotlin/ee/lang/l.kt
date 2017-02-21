package ee.lang


object l : StructureUnit({ namespace("ee.lang") }) {
    val Attribute = CompilationUnit({ derivedFrom(Composite) })
    val Comment = CompilationUnit({ derivedFrom(Composite) })
    val CompilationUnit = CompilationUnit({ derivedFrom(Type) })
    val Composite = CompilationUnit({ derivedFrom(MapMultiHolder) })
    val Constructor = CompilationUnit({ derivedFrom(LogicUnit) })
    val EnumType = CompilationUnit({ derivedFrom(CompilationUnit) })
    val ExternalType = CompilationUnit({ derivedFrom(Type) })
    val Generic = CompilationUnit({ derivedFrom(Type) })
    val Item = CompilationUnit()
    val Lambda = CompilationUnit({ derivedFrom(Type) })
    val ListMultiHolder = CompilationUnit({ derivedFrom(MultiHolder) })
    val Literal = CompilationUnit({ derivedFrom(LogicUnit) })
    val LogicUnit = CompilationUnit({ derivedFrom(TextComposite) })
    val MapMultiHolder = CompilationUnit({ derivedFrom(MultiHolder) })
    val NativeType = CompilationUnit({ derivedFrom(Type) })
    val Operation = CompilationUnit({ derivedFrom(LogicUnit) })
    val StructureUnit = CompilationUnit({ derivedFrom(Composite) })
    val TextComposite = CompilationUnit({ derivedFrom(Composite) })
    val Type = CompilationUnit({ derivedFrom(Composite) })

    object MultiHolder : CompilationUnit({ derivedFrom(Item) }) {
        val T = G({ type(Item) })
    }
}
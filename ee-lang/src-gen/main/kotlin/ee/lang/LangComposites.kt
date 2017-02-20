package ee.lang


open class Attributes : MultiListHolder<AttributeI> {
    constructor(value: Attributes.() -> Unit = {}) : super(AttributeI::class.java,
            value as MultiListHolder<AttributeI>.() -> Unit)

    companion object {
        val EMPTY = Attributes()
    }
}


open class CompilationUnits : MultiListHolder<CompilationUnitI> {
    constructor(value: CompilationUnits.() -> Unit = {}) : super(CompilationUnitI::class.java,
            value as MultiListHolder<CompilationUnitI>.() -> Unit)

    companion object {
        val EMPTY = CompilationUnits()
    }
}


open class Constructors : MultiListHolder<ConstructorI> {
    constructor(value: Constructors.() -> Unit = {}) : super(ConstructorI::class.java,
            value as MultiListHolder<ConstructorI>.() -> Unit)

    companion object {
        val EMPTY = Constructors()
    }
}


open class EnumTypes : MultiListHolder<EnumTypeI> {
    constructor(value: EnumTypes.() -> Unit = {}) : super(EnumTypeI::class.java,
            value as MultiListHolder<EnumTypeI>.() -> Unit)

    companion object {
        val EMPTY = EnumTypes()
    }
}


open class ExternalTypes : MultiListHolder<ExternalTypeI> {
    constructor(value: ExternalTypes.() -> Unit = {}) : super(ExternalTypeI::class.java,
            value as MultiListHolder<ExternalTypeI>.() -> Unit)

    companion object {
        val EMPTY = ExternalTypes()
    }
}


open class Generics : MultiListHolder<GenericI> {
    constructor(value: Generics.() -> Unit = {}) : super(GenericI::class.java,
            value as MultiListHolder<GenericI>.() -> Unit)

    companion object {
        val EMPTY = Generics()
    }
}


open class Lambdas : MultiListHolder<LambdaI> {
    constructor(value: Lambdas.() -> Unit = {}) : super(LambdaI::class.java,
            value as MultiListHolder<LambdaI>.() -> Unit)

    companion object {
        val EMPTY = Lambdas()
    }
}


open class Literals : MultiListHolder<LiteralI> {
    constructor(value: Literals.() -> Unit = {}) : super(LiteralI::class.java,
            value as MultiListHolder<LiteralI>.() -> Unit)

    companion object {
        val EMPTY = Literals()
    }
}


open class LogicUnits : MultiListHolder<LogicUnitI> {
    constructor(value: LogicUnits.() -> Unit = {}) : super(LogicUnitI::class.java,
            value as MultiListHolder<LogicUnitI>.() -> Unit)

    companion object {
        val EMPTY = LogicUnits()
    }
}


open class NativeTypes : MultiListHolder<NativeTypeI> {
    constructor(value: NativeTypes.() -> Unit = {}) : super(NativeTypeI::class.java,
            value as MultiListHolder<NativeTypeI>.() -> Unit)

    companion object {
        val EMPTY = NativeTypes()
    }
}


open class Operations : MultiListHolder<OperationI> {
    constructor(value: Operations.() -> Unit = {}) : super(OperationI::class.java,
            value as MultiListHolder<OperationI>.() -> Unit)

    companion object {
        val EMPTY = Operations()
    }
}


open class StructureUnits : MultiListHolder<StructureUnitI> {
    constructor(value: StructureUnits.() -> Unit = {}) : super(StructureUnitI::class.java,
            value as MultiListHolder<StructureUnitI>.() -> Unit)

    companion object {
        val EMPTY = StructureUnits()
    }
}


open class TextComposites : MultiListHolder<TextCompositeI> {
    constructor(value: TextComposites.() -> Unit = {}) : super(TextCompositeI::class.java,
            value as MultiListHolder<TextCompositeI>.() -> Unit)

    companion object {
        val EMPTY = TextComposites()
    }
}


open class Types : MultiListHolder<TypeI> {
    constructor(value: Types.() -> Unit = {}) : super(TypeI::class.java,
            value as MultiListHolder<TypeI>.() -> Unit)

    companion object {
        val EMPTY = Types()
    }
}


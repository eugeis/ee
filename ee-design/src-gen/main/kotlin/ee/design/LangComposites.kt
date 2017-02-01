package ee.design


open class Attributes : TypedComposite<AttributeI> {
    constructor(value: Attributes.() -> Unit = {}) : super(AttributeI::class.java,
            value as TypedComposite<AttributeI>.() -> Unit)

    companion object {
        val EMPTY = Attributes()
    }
}


open class Bundles : TypedComposite<BundleI> {
    constructor(value: Bundles.() -> Unit = {}) : super(BundleI::class.java,
            value as TypedComposite<BundleI>.() -> Unit)

    companion object {
        val EMPTY = Bundles()
    }
}


open class Comps : TypedComposite<CompI> {
    constructor(value: Comps.() -> Unit = {}) : super(CompI::class.java,
            value as TypedComposite<CompI>.() -> Unit)

    companion object {
        val EMPTY = Comps()
    }
}


open class CompilationUnits : TypedComposite<CompilationUnitI> {
    constructor(value: CompilationUnits.() -> Unit = {}) : super(CompilationUnitI::class.java,
            value as TypedComposite<CompilationUnitI>.() -> Unit)

    companion object {
        val EMPTY = CompilationUnits()
    }
}


open class Constructors : TypedComposite<ConstructorI> {
    constructor(value: Constructors.() -> Unit = {}) : super(ConstructorI::class.java,
            value as TypedComposite<ConstructorI>.() -> Unit)

    companion object {
        val EMPTY = Constructors()
    }
}


open class EnumTypes : TypedComposite<EnumTypeI> {
    constructor(value: EnumTypes.() -> Unit = {}) : super(EnumTypeI::class.java,
            value as TypedComposite<EnumTypeI>.() -> Unit)

    companion object {
        val EMPTY = EnumTypes()
    }
}


open class ExternalModules : TypedComposite<ExternalModuleI> {
    constructor(value: ExternalModules.() -> Unit = {}) : super(ExternalModuleI::class.java,
            value as TypedComposite<ExternalModuleI>.() -> Unit)

    companion object {
        val EMPTY = ExternalModules()
    }
}


open class ExternalTypes : TypedComposite<ExternalTypeI> {
    constructor(value: ExternalTypes.() -> Unit = {}) : super(ExternalTypeI::class.java,
            value as TypedComposite<ExternalTypeI>.() -> Unit)

    companion object {
        val EMPTY = ExternalTypes()
    }
}


open class Facets : TypedComposite<FacetI> {
    constructor(value: Facets.() -> Unit = {}) : super(FacetI::class.java,
            value as TypedComposite<FacetI>.() -> Unit)

    companion object {
        val EMPTY = Facets()
    }
}


open class Generics : TypedComposite<GenericI> {
    constructor(value: Generics.() -> Unit = {}) : super(GenericI::class.java,
            value as TypedComposite<GenericI>.() -> Unit)

    companion object {
        val EMPTY = Generics()
    }
}


open class Lambdas : TypedComposite<LambdaI> {
    constructor(value: Lambdas.() -> Unit = {}) : super(LambdaI::class.java,
            value as TypedComposite<LambdaI>.() -> Unit)

    companion object {
        val EMPTY = Lambdas()
    }
}


open class Literals : TypedComposite<LiteralI> {
    constructor(value: Literals.() -> Unit = {}) : super(LiteralI::class.java,
            value as TypedComposite<LiteralI>.() -> Unit)

    companion object {
        val EMPTY = Literals()
    }
}


open class LogicUnits : TypedComposite<LogicUnitI> {
    constructor(value: LogicUnits.() -> Unit = {}) : super(LogicUnitI::class.java,
            value as TypedComposite<LogicUnitI>.() -> Unit)

    companion object {
        val EMPTY = LogicUnits()
    }
}


open class Models : TypedComposite<ModelI> {
    constructor(value: Models.() -> Unit = {}) : super(ModelI::class.java,
            value as TypedComposite<ModelI>.() -> Unit)

    companion object {
        val EMPTY = Models()
    }
}


open class Modules : TypedComposite<ModuleI> {
    constructor(value: Modules.() -> Unit = {}) : super(ModuleI::class.java,
            value as TypedComposite<ModuleI>.() -> Unit)

    companion object {
        val EMPTY = Modules()
    }
}


open class ModuleGroups : TypedComposite<ModuleGroupI> {
    constructor(value: ModuleGroups.() -> Unit = {}) : super(ModuleGroupI::class.java,
            value as TypedComposite<ModuleGroupI>.() -> Unit)

    companion object {
        val EMPTY = ModuleGroups()
    }
}


open class NativeTypes : TypedComposite<NativeTypeI> {
    constructor(value: NativeTypes.() -> Unit = {}) : super(NativeTypeI::class.java,
            value as TypedComposite<NativeTypeI>.() -> Unit)

    companion object {
        val EMPTY = NativeTypes()
    }
}


open class Operations : TypedComposite<OperationI> {
    constructor(value: Operations.() -> Unit = {}) : super(OperationI::class.java,
            value as TypedComposite<OperationI>.() -> Unit)

    companion object {
        val EMPTY = Operations()
    }
}


open class StructureUnits : TypedComposite<StructureUnitI> {
    constructor(value: StructureUnits.() -> Unit = {}) : super(StructureUnitI::class.java,
            value as TypedComposite<StructureUnitI>.() -> Unit)

    companion object {
        val EMPTY = StructureUnits()
    }
}


open class TextComposites : TypedComposite<TextCompositeI> {
    constructor(value: TextComposites.() -> Unit = {}) : super(TextCompositeI::class.java,
            value as TypedComposite<TextCompositeI>.() -> Unit)

    companion object {
        val EMPTY = TextComposites()
    }
}


open class Types : TypedComposite<TypeI> {
    constructor(value: Types.() -> Unit = {}) : super(TypeI::class.java,
            value as TypedComposite<TypeI>.() -> Unit)

    companion object {
        val EMPTY = Types()
    }
}


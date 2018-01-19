package ee.design

import ee.design.Composite
import ee.design.Item
import ee.design.ItemModel

//Item types
open class ItemDsl : Item {
    constructor(init: Item.() -> Unit = {}) : super(init)
}

object ItemTypes : Composite({ namespace("ee.design") }) {
    val Item = ItemDsl()
    val TypedComposite = ItemDsl({ derivedFrom(Item) })
    val Composite = ItemDsl({ derivedFrom(TypedComposite) })
}

object DslModel : Composite({ namespace("ee.design") }) {

    object TextComposite : Item({ derivedFrom(ItemModel.Composite) })

    object Comment : Item({ derivedFrom(TextComposite) })

    object Generic : Item({ derivedFrom(Type) })

    object Type : Item({ derivedFrom(ItemModel.Composite) })

    object Lambda : Item({ derivedFrom(Type) })

    object NativeType : Item({ derivedFrom(Type) })

    object ExternalType : Item({ derivedFrom(Type) })

    object Attribute : Item({ derivedFrom(ItemModel.Item) })

    object LogicUnit : Item({ derivedFrom(TextComposite) })

    object Operation : Item({ derivedFrom(LogicUnit) })

    object DelegateOperation : Item({ derivedFrom(Operation) })

    object CompilationUnit : Item({ derivedFrom(Type) })

    object StructureUnit : Item({ derivedFrom(ItemModel.Composite) })

    object Constructor : Item({ derivedFrom(LogicUnit) })

    object EnumType : Item({ derivedFrom(CompilationUnit) })

    object Literal : Item({ derivedFrom(LogicUnit) })
}
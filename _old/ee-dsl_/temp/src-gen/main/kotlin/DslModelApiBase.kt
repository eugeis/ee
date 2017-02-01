package ee.design

import ee.design.Composite
import ee.design.Item


open class Attribute : Item {
    constructor(init: Attribute.() -> Unit = {}) : super() {
        init()
    }
}


open class Comment : TextComposite {
    constructor(init: Comment.() -> Unit = {}) : super() {
        init()
    }
}


open class CompilationUnit : Type {
    constructor(init: CompilationUnit.() -> Unit = {}) : super() {
        init()
    }
}


open class Constructor : LogicUnit {
    constructor(init: Constructor.() -> Unit = {}) : super() {
        init()
    }
}


open class DelegateOperation : Operation {
    constructor(init: DelegateOperation.() -> Unit = {}) : super() {
        init()
    }
}


open class EnumType : CompilationUnit {
    constructor(init: EnumType.() -> Unit = {}) : super() {
        init()
    }
}


open class ExternalType : Type {
    constructor(init: ExternalType.() -> Unit = {}) : super() {
        init()
    }
}


open class Generic : Type {
    constructor(init: Generic.() -> Unit = {}) : super() {
        init()
    }
}


open class Lambda : Type {
    constructor(init: Lambda.() -> Unit = {}) : super() {
        init()
    }
}


open class Literal : LogicUnit {
    constructor(init: Literal.() -> Unit = {}) : super() {
        init()
    }
}


open class LogicUnit : TextComposite {
    constructor(init: LogicUnit.() -> Unit = {}) : super() {
        init()
    }
}


open class NativeType : Type {
    constructor(init: NativeType.() -> Unit = {}) : super() {
        init()
    }
}


open class Operation : LogicUnit {
    constructor(init: Operation.() -> Unit = {}) : super() {
        init()
    }
}


open class StructureUnit : Composite {
    constructor(init: StructureUnit.() -> Unit = {}) : super() {
        init()
    }
}


open class TextComposite : Composite {
    constructor(init: TextComposite.() -> Unit = {}) : super() {
        init()
    }
}


open class Type : Composite {
    constructor(init: Type.() -> Unit = {}) : super() {
        init()
    }
}


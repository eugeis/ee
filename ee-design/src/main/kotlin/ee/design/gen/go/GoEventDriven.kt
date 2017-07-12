package ee.design.gen.go

import ee.design.EntityI
import ee.lang.*


object eh : StructureUnit({ namespace("github.com.looplab.eventhorizon").name("eh") }) {

    object AggregateType : ExternalType() {
    }

    object EventStore : ExternalType() {
        object Save : Operation() {
            val ctx = p()
        }
    }

    object EventBus : ExternalType() {
    }

    object EventPublisher : ExternalType() {
    }

    object CommandBus : ExternalType() {
    }

    object Event : ExternalType() {
    }

    object UUID : ExternalType() {
    }
}

fun <T : EntityI> T.toGoEventhorizonAggregate(c: GenerationContext,
                                              derived: String = LangDerivedKind.IMPL,
                                              api: String = LangDerivedKind.API): String {
    val name = c.n(this, derived)
    return """

const ${name}Type ${c.n(eh.AggregateType, derived)} = "$name""""
}
import ee.design.Basic
import ee.design.Module
import ee.lang.TypeI
import ee.lang.doc
import ee.lang.n
import io.swagger.parser.SwaggerParser
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("SwaggerToDesign")

object SwaggerToDesign {
    @JvmStatic
    fun main(args: Array<String>) {
        val swagger = SwaggerParser().read(
                """D:\TC_CACHE\MindConnectRail\mcr-gateway\mcr-mindsphere_swagger\assetmanagement-v3.yaml""")

        val module = Module {
            name("Shared")

            swagger.definitions?.forEach { defName, def ->
                basic(def.toBasic(defName))
            }
        }.init()
        module.toString()
        println(module)
    }
}

fun io.swagger.models.Model.toBasic(name: String): Basic {
    val model = this
    return Basic {
        name(name).doc(model.description ?: "")
        model.properties?.forEach { propName, p ->
            prop { name(propName).type(p.type.toType()).doc(p.description ?: "") }
        }
    }.init()
}

fun String?.toType(): TypeI<*> {
    return if (this == null) {
        n.String
    } else {
        log.info("type: {}", this)
        n.String
    }
}
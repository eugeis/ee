package ee.design.swagger

import java.nio.file.Paths

object SwaggerToDesignMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val swagger = SwaggerToDesign(mutableMapOf("rel.self" to "Link", "Href" to "Link"), mutableSetOf())
        swagger.toDslTypes(Paths.get(
                """D:\TC_CACHE\MindConnectRail\mcr-gateway\mcr-mindsphere_swagger\assetmanagement-v3.yaml""")).types.forEach {
            println(it.value)
        }
    }
}
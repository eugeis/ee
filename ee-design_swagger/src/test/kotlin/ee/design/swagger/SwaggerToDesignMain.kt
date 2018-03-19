package ee.design.swagger

import io.swagger.parser.SwaggerParser

object SwaggerToDesignMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val swagger = SwaggerParser().read(
                """D:\TC_CACHE\MindConnectRail\mcr-gateway\mcr-mindsphere_swagger\assetmanagement-v3.yaml""")

        swagger.toDslTypes().types.forEach {
            println(it.value)
        }
    }
}
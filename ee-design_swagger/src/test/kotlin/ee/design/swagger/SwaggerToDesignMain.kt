package ee.design.swagger

import ee.design.appendTypes
import org.slf4j.LoggerFactory
import java.nio.file.Paths

object SwaggerToDesignMain {
    private val log = LoggerFactory.getLogger(javaClass)

    @JvmStatic
    fun main(args: Array<String>) {
        val swagger = SwaggerToDesign(mutableMapOf(),
                mutableMapOf("rel.self" to "Link", "Href" to "Link", "AspectId" to "n.String", "ETag" to "n.String",
                        "Errors" to "n.String", "TenantId" to "n.String", "TypeId" to "n.String"),
                mutableSetOf("AspectId", "ETag", "Errors", "TenantId", "TypeId"))

        val base = Paths.get("ee/ee-design_swagger/src/test/resources").toRealPath()

        val alreadyGeneratedTypes = mutableMapOf<String, String>()
        val buffer = StringBuffer()
        listOf("assetmanagement-v3.yaml").map {
            swagger.toDslTypes(base.resolve(it))
        }.forEach { dslTypes ->
            buffer.appendTypes(log, dslTypes, alreadyGeneratedTypes)
        }

        log.info(buffer.toString())
    }
}
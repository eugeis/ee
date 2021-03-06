package ee.design.json

import ee.design.appendTypes
import org.slf4j.LoggerFactory
import java.nio.file.Paths

object JsonToDesignMain {
    private val log = LoggerFactory.getLogger(javaClass)

    @JvmStatic
    fun main(args: Array<String>) {
        val json = JsonToDesign(mutableMapOf(),
                mutableMapOf("rel.self" to "Link", "Href" to "Link", "AspectId" to "n.String", "ETag" to "n.String",
                        "Errors" to "n.String", "TenantId" to "n.String", "TypeId" to "n.String"),
                mutableSetOf("AspectId", "ETag", "Errors", "TenantId", "TypeId"))

        val alreadyGeneratedTypes = mutableMapOf<String, String>()
        val buffer = StringBuffer()

        val dslTypes = json.toDslTypes(Paths.get("/home/z000ru5y/dev/s2r/cdm/cdm_schema.json"))
        buffer.appendTypes(log, dslTypes, alreadyGeneratedTypes)

        log.info(buffer.toString())
    }

}
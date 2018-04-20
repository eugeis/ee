package ee.design.swagger

import org.slf4j.LoggerFactory
import java.nio.file.Paths

object SwaggerToDesignMain {
    private val log = LoggerFactory.getLogger(javaClass)

    @JvmStatic
    fun main(args: Array<String>) {
        val swagger = SwaggerToDesign(
            mutableMapOf("rel.self" to "Link", "Href" to "Link", "AspectId" to "n.String", "ETag" to "n.String",
                "Errors" to "n.String", "TenantId" to "n.String", "TypeId" to "n.String"),
            mutableSetOf("AspectId", "ETag", "Errors", "TenantId", "TypeId"))

        val base = Paths.get("ee/ee-design_swagger/src/test/resources").toRealPath()

        val alreadyGeneratedTypes = mutableMapOf<String, String>()
        val buffer = StringBuffer()
        listOf("assetmanagement-v3.yaml").map {
            swagger.toDslTypes(base.resolve(it))
        }.forEach { buffer.appendTypes(it, alreadyGeneratedTypes) }

        log.info(buffer.toString())
    }

    private fun StringBuffer.appendTypes(types: DslTypes, keyToTypes: MutableMap<String, String>): StringBuffer =
        apply {
            appendln("//${types.name}")
            types.types.forEach { k, v ->
                if (keyToTypes.containsKey(k)) {
                    if (keyToTypes[k] != v) {
                        log.warn("different type definitions in {}, with same name {} first:{} second {}", types.name,
                            k, keyToTypes[k], v)
                    }
                } else {
                    keyToTypes[k] = v
                    appendln(v)
                }
            }
        }
}
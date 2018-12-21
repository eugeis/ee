package ee.design.xsd

import org.slf4j.LoggerFactory
import java.nio.file.Paths

object XsdToDesignMain {
    private val log = LoggerFactory.getLogger(javaClass)

    @JvmStatic
    fun main(args: Array<String>) {
        val xsd = XsdToDesign(
                //onlyItems = mutableSetOf("UANode")
                )

        val base = Paths.get("ee/ee-design_xsd/src/test/resources").toRealPath()

        val alreadyGeneratedTypes = mutableMapOf<String, String>()
        val buffer = StringBuffer()
        listOf("UANodeSet.xsd").map {
            xsd.toDslTypes(base.resolve(it))
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
                types.elements.forEach { k, v ->
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
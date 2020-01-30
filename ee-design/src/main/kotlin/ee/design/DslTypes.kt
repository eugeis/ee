package ee.design

import org.slf4j.Logger

data class DslTypes(val name: String, val desc: String, val types: Map<String, String>)

fun StringBuffer.appendTypes(log: Logger, types: DslTypes, keyToTypes: MutableMap<String, String>): StringBuffer =
        apply {
            appendln("//${types.name}")
            types.types.forEach { (k, v) ->
                if (keyToTypes.containsKey(k)) {
                    if (keyToTypes[k] != v) {
                        log.warn("different type definitions in {}, with same name {} first:{} second {}",
                                types.name, k, keyToTypes[k], v)
                    }
                } else {
                    keyToTypes[k] = v
                    appendln(v)
                }
            }
        }
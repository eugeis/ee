package ee.design.json

import ee.common.ext.*
import ee.lang.nL
import org.everit.json.schema.*
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.nio.file.Path
import java.util.*

private val log = LoggerFactory.getLogger("JsonToDesign")

data class DslTypes(val name: String, val desc: String, val types: Map<String, String>)

class JsonToDesign(private val pathsToEntityNames: MutableMap<String, String> = mutableMapOf(),
                   private val namesToTypeName: MutableMap<String, String> = mutableMapOf(),
                   private val ignoreTypes: MutableSet<String> = mutableSetOf()) {

    fun toDslTypes(schemaFile: Path, rootTypeName: String =
            schemaFile.fileName.toString().fileName().toCamelCase().capitalize()): DslTypes =

            JsonToDesignExecutor(schemaFile, rootTypeName, namesToTypeName, ignoreTypes).toDslTypes()

}

private class JsonToDesignExecutor(swaggerFile: Path, val rootTypeName: String,
                                   private val namesToTypeName: MutableMap<String, String> = mutableMapOf(),
                                   private val ignoreTypes: MutableSet<String> = mutableSetOf()) {
    private val primitiveTypes = mapOf("integer" to "n.Int", "string" to "n.String")
    private val typeToPrimitive = mutableMapOf<String, String>()

    private val schema = loadSchema(swaggerFile)
    private val typesToFill = TreeMap<String, String>()

    fun toDslTypes(): DslTypes {
        schema.fillDslType(rootTypeName)
        return DslTypes(name = schema.title ?: "", desc = schema.description ?: "", types = typesToFill)
    }

    private fun loadSchema(swaggerFile: Path): Schema {
        try {
            FileInputStream(swaggerFile.toFile()).use { inputStream ->
                val rawSchema = JSONObject(JSONTokener(inputStream))
                return SchemaLoader.load(rawSchema)
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun Schema.fillDslType(name: String = "") {
        when (this) {
            is CombinedSchema -> {
                val typeName = toDslTypeName(name)
                if (!typesToFill.containsKey(typeName)) {
                    typesToFill[typeName] = toDslType(typeName)
                } else {
                    log.debug("{}, ignore schema, already defined as custom type def", typeName)
                }
            }
            is ReferenceSchema -> {
                referredSchema?.fillDslType()
            }
            is EnumSchema -> {
                val typeName = toDslTypeName(name)
                if (!typesToFill.containsKey(typeName)) {
                    typesToFill[typeName] = toDslType(typeName)
                } else {
                    log.debug("{}, ignore schema, already defined as custom type def", typeName)
                }
            }
            is ArraySchema -> {
                allItemSchema?.fillDslType()
                itemSchemas?.forEach {
                    it.fillDslType()
                }
            }
            is ObjectSchema -> {
                val typeName = toDslTypeName(name)
                if (!typesToFill.containsKey(typeName)) {
                    typesToFill[typeName] = toDslType(typeName)

                    propertySchemas.forEach { (_, prop) ->
                        prop.fillDslType()
                    }
                } else {
                    log.debug("{}, ignore schema, already defined as custom type def", typeName)
                }
            }
            else -> {
                log.debug("{}, ignore schema for custom type def", this)
            }
        }
    }

    private fun EnumSchema.toDslType(name: String): String {
        return """
object $name : EnumType(${description.toDslDoc("{", "}")}) {${toDslLiterals()}
}"""
    }

    private fun EnumSchema.toDslLiterals(): String {
        return possibleValuesAsList.joinToString(nL, nL) {
            "    val ${it.toString().toCamelCase()} = lit()"
        }
    }

    private fun ObjectSchema.toDslType(name: String): String {
        return """
object $name : Values(${description.toDslDoc("{", "}")}) {${toDslProperties()}
}"""
    }

    private fun ObjectSchema.toDslProperties(): String {
        return propertySchemas.toDslProperties {
            requiredProperties.contains(it)
        }
    }

    private fun CombinedSchema.toDslType(name: String): String {
        val objectSchemas = subschemas.filterIsInstance<ObjectSchema>()
        return if (objectSchemas.isNotEmpty()) {
            """
object $name : Values(${description.toDslDoc("{", "}")}) {${
            objectSchemas.joinToString(nL) { it.toDslProperties() }}
}"""
        } else {
            val enumSchemas = subschemas.filterIsInstance<EnumSchema>()
            enumSchemas.firstOrNull()?.toDslType(name) ?: ""
        }
    }

    private fun Map<String, Schema>.toDslProperties(required: (propName: String) -> Boolean): String {
        val props = entries.map { it.value.toDslProp(it.key, required(it.key)) }//.sorted()
        return props.joinToString(nL, nL) { it }
    }

    private fun Schema.toDslProp(name: String, required: Boolean = false): String {
        val nameCamelCase = name.toCamelCase()
        return "    val $nameCamelCase = prop { ${(name != nameCamelCase)
                .then { "externalName(\"$name\")." }}${toDslInit(required)} }"
    }

    private fun Schema.toDslInit(required: Boolean): String {
        val typeName = toDslTypeName()
        return "type($typeName)${required.not().then { ".nullable()" }}${toDslDefaultValue()}${
        description.toDslDoc(".")}"
    }

    private fun Schema.toDslTypeName(name: String): String {
        return if (name.isNotBlank()) {
            name
        } else {
            toDslTypeName()
        }
    }

    private fun Schema.toDslTypeName(): String {
        return when (this) {
            is BooleanSchema -> {
                "n.Boolean"
            }
            is ConstSchema -> {
                "n.String"
            }
            is NullSchema -> {
                "n.Any"
            }
            is NumberSchema -> {
                "n.Int"
            }
            is EmptySchema -> {
                "n.String"
            }
            is CombinedSchema -> toDslTypeName()
            is FalseSchema -> {
                "n.String"
            }
            is ArraySchema -> {
                "n.List.GT(${allItemSchema.toDslTypeName()})"
            }
            is ReferenceSchema -> {
                referredSchema?.toDslTypeName() ?: "n.UNKNOWN_REFERENCE"
            }
            is EnumSchema -> {
                "EnumSchema"
            }
            is NotSchema -> {
                "n.String"
            }
            is ObjectSchema -> toDslTypeName()
            is StringSchema -> {
                "n.String"
            }
            is ConditionalSchema -> {
                "n.String"
            }
            else -> {
                log.info("{}unknown schema", this)
                "UnknownName"
            }
        }
    }

    private fun Schema.toDslDefaultValue(): String {
        return if (defaultValue != null) {
            when (this) {
                is BooleanSchema -> "value($defaultValue)"
                is NumberSchema -> "value($defaultValue)"
                else -> "value(\"$defaultValue\")"
            }
        } else ""
    }

    private fun CombinedSchema.toDslTypeName(): String {
        val typeName = when {
            schemaLocation != null && schemaLocation.startsWith("#/definitions/") -> {
                val combinedSchemaName = schemaLocation.substringAfterLast("#/definitions/").capitalize()
                combinedSchemaName.split("/").filter { it != "properties" }.joinToString("") {
                    it.capitalize()
                }
            }
            else -> {
                subschemas.filterIsInstance<ObjectSchema>().map {
                    it.toDslTypeName()
                }.joinToString("") { it.capitalize() }
            }
        }

        fillDslType(typeName)

        log.debug("name '{}' for '{}'", typeName, this)
        return typeName
    }

    private fun ObjectSchema.toDslTypeName(): String {
        val typeName = when {
            schemaLocation.startsWith("#/definitions/") -> {
                schemaLocation.substringAfterLast("#/definitions/").capitalize()
            }
            else -> {
                propertySchemas.keys.joinToString("") { it.capitalize() }
            }
        }
        log.debug("name '{}' for '{}'", typeName, this)
        return typeName
    }

    private fun String.toDslTypeName(): String {
        return typeToPrimitive[this] ?: namesToTypeName.getOrPut(this) { toCamelCase().capitalize() }
    }

    private fun String?.toDslDoc(suffix: String = "", prefix: String = ""): String {
        return (this != null).ifElse({
            "${suffix}doc(${(this!!.contains("\"") || contains("\n") || contains("\\")).ifElse(
                    "\"\"\"$this\"\"\"", "\"$this\"")})$prefix"
        }, { "" })
    }
}
